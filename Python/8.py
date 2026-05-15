import copy
import time
import torch
import torch.nn as nn
import torch.optim as optim
import torchvision.models as models
from torchvision import datasets, transforms
from torch.utils.data import DataLoader, Subset, random_split
import numpy as np
import matplotlib.pyplot as plt
from sklearn.metrics import confusion_matrix, ConfusionMatrixDisplay

# =========================================================
# 1. DÁTA – Food101, 10 vybraných tried
# =========================================================

SELECTED_CLASSES = [
    "apple_pie", "caesar_salad", "clam_chowder", "edamame",
    "french_fries", "hamburger", "hot_dog", "ice_cream", "sushi", "waffles"
]

IMG_SIZE   = 224
BATCH      = 32
VAL_FRAC   = 0.15   # 15 % z train -> validácia
N_RUNS     = 3
EPOCHS     = 10
LR         = 0.0001
device     = torch.device("cuda" if torch.cuda.is_available() else "cpu")
print(f"Používam: {device}")

# --- základný transform (bez augmentácie)
base_transform = transforms.Compose([
    transforms.Resize((IMG_SIZE, IMG_SIZE)),
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406],
                         std=[0.229, 0.224, 0.225]),
])

# --- transform s augmentáciou
aug_transform = transforms.Compose([
    transforms.Resize((IMG_SIZE + 20, IMG_SIZE + 20)),
    transforms.RandomCrop(IMG_SIZE),
    transforms.RandomHorizontalFlip(),
    transforms.ColorJitter(brightness=0.2, contrast=0.2, saturation=0.2),
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406],
                         std=[0.229, 0.224, 0.225]),
])


def filter_by_classes(dataset, selected_classes):
    # Zoradíme vybrané triedy abecedne – rovnaký poriadok pri všetkých modeloch
    sorted_selected = sorted(selected_classes)
    class_to_new_idx = {c: i for i, c in enumerate(sorted_selected)}

    # dataset.classes je zoznam všetkých 101 názvov tried
    # dataset._labels  je zoznam int (index do dataset.classes) pre každý obrázok
    orig_class_to_new = {}
    for orig_idx, orig_name in enumerate(dataset.classes):
        if orig_name in class_to_new_idx:
            orig_class_to_new[orig_idx] = class_to_new_idx[orig_name]

    # _labels je flat list intov
    indices = [i for i, label in enumerate(dataset._labels)
               if label in orig_class_to_new]

    return FilteredSubset(dataset, indices, orig_class_to_new)


class FilteredSubset(torch.utils.data.Dataset):
    """Wrapper – filtruje triedy a remapuje labely."""
    def __init__(self, base_dataset, indices, label_map):
        self.base = base_dataset
        self.indices = indices
        self.label_map = label_map

    def __len__(self):
        return len(self.indices)

    def __getitem__(self, idx):
        img, label = self.base[self.indices[idx]]
        return img, self.label_map[label]

    def set_transform(self, transform):
        self.base.transform = transform


def build_loaders(use_augmentation=False):
    """Načíta Food101, prefiltruje triedy, rozdelí train/val/test."""
    tr = aug_transform if use_augmentation else base_transform

    train_full = datasets.Food101(root="./data", split="train",
                                  download=True, transform=tr)
    test_full  = datasets.Food101(root="./data", split="test",
                                  download=True, transform=base_transform)

    train_filtered = filter_by_classes(train_full, SELECTED_CLASSES)
    test_filtered  = filter_by_classes(test_full,  SELECTED_CLASSES)

    val_size   = int(len(train_filtered) * VAL_FRAC)
    train_size = len(train_filtered) - val_size
    gen = torch.Generator().manual_seed(42)
    train_set, val_set = random_split(train_filtered, [train_size, val_size],
                                      generator=gen)

    train_loader = DataLoader(train_set, batch_size=BATCH, shuffle=True,
                              num_workers=0)
    val_loader   = DataLoader(val_set,   batch_size=BATCH, shuffle=False,
                              num_workers=0)
    test_loader  = DataLoader(test_filtered, batch_size=BATCH, shuffle=False,
                              num_workers=0)

    print(f"  Train: {len(train_set)} | Val: {len(val_set)} | Test: {len(test_filtered)}")
    return train_loader, val_loader, test_loader

# =========================================================
# 2. MODELY
# =========================================================
NUM_CLASSES = len(SELECTED_CLASSES)   # 10


def build_model(name, transfer_learning=True):
    """Vytvorí model – predtrénovaný (TL) alebo od nuly (scratch)."""
    w = "IMAGENET1K_V1" if transfer_learning else None

    if name == "M1_VGG16":
        model = models.vgg16(weights=w)
        if transfer_learning:
            for param in model.features.parameters():
                param.requires_grad = False
        model.classifier[6] = nn.Linear(4096, NUM_CLASSES)

    elif name == "M2_ResNet18":
        model = models.resnet18(weights=w)
        if transfer_learning:
            for name_, param in model.named_parameters():
                if "layer4" not in name_ and "fc" not in name_:
                    param.requires_grad = False
        model.fc = nn.Linear(model.fc.in_features, NUM_CLASSES)

    elif name == "M3_MobileNetV2":
        model = models.mobilenet_v2(weights=w)
        if transfer_learning:
            for param in model.features.parameters():
                param.requires_grad = False
        model.classifier[1] = nn.Linear(model.last_channel, NUM_CLASSES)

    else:
        raise ValueError(f"Neznámy model: {name}")

    return model

# =========================================================
# 3. POMOCNÉ FUNKCIE (rovnaký štýl ako zadanie 7)
# =========================================================

def evaluate_loss(model, loader, criterion, device):
    model.eval()
    total_loss = 0.0
    with torch.no_grad():
        for X, y in loader:
            X, y = X.to(device), y.to(device)
            total_loss += criterion(model(X), y).item()
    return total_loss / len(loader)


def get_accuracy(model, loader, device):
    model.eval()
    correct, total = 0, 0
    with torch.no_grad():
        for X, y in loader:
            X, y = X.to(device), y.to(device)
            preds = model(X).argmax(dim=1)
            correct += (preds == y).sum().item()
            total   += y.size(0)
    return correct / total * 100


def train_model(model, train_loader, val_loader, epochs, lr, device):
    model = model.to(device)
    optimizer = optim.Adam(filter(lambda p: p.requires_grad, model.parameters()), lr=lr)
    criterion = nn.CrossEntropyLoss()
    scheduler = optim.lr_scheduler.StepLR(optimizer, step_size=5, gamma=0.5)

    train_losses, val_losses = [], []

    for epoch in range(epochs):
        model.train()
        running_loss = 0.0
        for X, y in train_loader:
            X, y = X.to(device), y.to(device)
            optimizer.zero_grad()
            loss = criterion(model(X), y)
            loss.backward()
            optimizer.step()
            running_loss += loss.item()

        tr_loss  = running_loss / len(train_loader)
        val_loss = evaluate_loss(model, val_loader, criterion, device)
        train_losses.append(tr_loss)
        val_losses.append(val_loss)
        scheduler.step()

        print(f"    Epocha {epoch+1:2d}/{epochs} | train loss={tr_loss:.4f} | val loss={val_loss:.4f}")

    return model, train_losses, val_losses


def average_overfitting_start(train_losses, val_losses):
    for i in range(len(train_losses)):
        if val_losses[i] > train_losses[i]:
            return i + 1
    return len(train_losses)


def plot_loss_curves(best_losses, title_suffix=""):
    for name, (tr_l, val_l) in best_losses.items():
        plt.figure(figsize=(7, 5))
        plt.plot(tr_l, label="Trénovacia loss")
        plt.plot(val_l, label="Validačná loss")
        plt.title(f"Loss vs Epoch – {name}{title_suffix}")
        plt.xlabel("Epocha")
        plt.ylabel("Loss")
        plt.legend()
        plt.grid(True)
        plt.tight_layout()
        plt.savefig(f"loss_{name.replace(' ', '_')}.png", dpi=120)
        plt.show()


def plot_confusion_matrix(model, loader, device, title="Confusion matrix"):
    all_preds, all_labels = [], []
    model.eval()
    with torch.no_grad():
        for X, y in loader:
            preds = model(X.to(device)).argmax(dim=1).cpu()
            all_preds.extend(preds.numpy())
            all_labels.extend(y.numpy())

    cm = confusion_matrix(all_labels, all_preds)
    short_names = ["apple", "caesar", "chowder", "edamame", "fries",
                   "burger", "hotdog", "icecream", "sushi", "waffles"]
    disp = ConfusionMatrixDisplay(cm, display_labels=short_names)
    fig, ax = plt.subplots(figsize=(9, 8))
    disp.plot(cmap="Blues", ax=ax, colorbar=False)
    plt.xticks(rotation=45, ha="right", fontsize=8)
    plt.title(title)
    plt.tight_layout()
    plt.savefig(f"cm_{title.replace(' ', '_')[:40]}.png", dpi=120)
    plt.show()


def show_sample_predictions(model, loader, device, n=10):
    model.eval()
    short_names = ["apple_pie", "caesar_salad", "clam_chowder", "edamame",
                   "french_fries", "hamburger", "hot_dog", "ice_cream",
                   "sushi", "waffles"]
    shown = 0
    plt.figure(figsize=(14, 6))
    inv_norm = transforms.Normalize(
        mean=[-0.485/0.229, -0.456/0.224, -0.406/0.225],
        std=[1/0.229, 1/0.224, 1/0.225])

    with torch.no_grad():
        for X, y in loader:
            out   = model(X.to(device))
            probs = torch.softmax(out, dim=1)
            preds = out.argmax(dim=1)

            for i in range(X.size(0)):
                if shown >= n:
                    plt.tight_layout()
                    plt.savefig("sample_predictions.png", dpi=120)
                    plt.show()
                    return
                img = inv_norm(X[i]).clamp(0, 1).permute(1, 2, 0).numpy()
                plt.subplot(2, 5, shown + 1)
                plt.imshow(img)
                t = short_names[y[i].item()]
                p = short_names[preds[i].item()]
                prob = probs[i][preds[i]].item()
                color = "green" if t == p else "red"
                plt.title(f"T:{t[:6]}\nP:{p[:6]} {prob:.2f}", fontsize=7, color=color)
                plt.axis("off")
                shown += 1


def print_run_table(model_name, mode, run_results):
    print(f"\nVýsledky behov – {model_name} ({mode})")
    print(f"{'Beh':<5} {'Train loss':>12} {'Train acc%':>12} {'Val loss':>10} {'Val acc%':>10} {'Test loss':>10} {'Test acc%':>10}")
    print("-" * 75)
    for r in run_results:
        print(f"{r['run']:<5} {r['train_loss']:>12.4f} {r['train_acc']:>12.2f} "
              f"{r['val_loss']:>10.4f} {r['val_acc']:>10.2f} "
              f"{r['test_loss']:>10.4f} {r['test_acc']:>10.2f}")


def print_summary_table(results):
    """Súhrnná tabuľka – priemery za scratch aj TL pre každý model."""
    print(f"\n{'Model':<18} {'Režim':<10} {'Avg train loss':>15} {'Avg train%':>11} "
          f"{'Avg val loss':>13} {'Avg val%':>9} {'Avg test loss':>14} {'Avg test%':>10}")
    print("-" * 103)
    for key, d in results.items():
        runs = d["runs"]
        print(
            f"{key:<18} {d['mode']:<10}"
            f"{np.mean([r['train_loss'] for r in runs]):>15.4f}"
            f"{np.mean([r['train_acc']  for r in runs]):>11.2f}"
            f"{np.mean([r['val_loss']   for r in runs]):>13.4f}"
            f"{np.mean([r['val_acc']    for r in runs]):>9.2f}"
            f"{np.mean([r['test_loss']  for r in runs]):>14.4f}"
            f"{np.mean([r['test_acc']   for r in runs]):>10.2f}"
        )

# =========================================================
# 4. NAČÍTANIE DÁT
# =========================================================
print("\n=== Načítanie datasetu Food101 (10 tried, bez augmentácie) ===")
train_loader, val_loader, test_loader = build_loaders(use_augmentation=False)

# =========================================================
# 5. HLAVNÁ SLUČKA – 3 modely × 2 režimy × 3 behy
# =========================================================
model_configs = {
    "M1_VGG16":      "M1_VGG16",
    "M2_ResNet18":   "M2_ResNet18",
    "M3_MobileNetV2":"M3_MobileNetV2",
}

modes = ["scratch", "TL"]

all_results   = {}
best_losses   = {}   # na kreslenie loss kriviek

global_best_test_acc  = -1
global_best_model     = None
global_best_key       = None

for arch_name in model_configs:
    for mode in modes:
        is_tl    = (mode == "TL")
        key      = f"{arch_name}_{mode}"

        print(f"\n{'='*65}")
        print(f"Model {arch_name} | Režim: {mode}")
        print(f"{'='*65}")

        run_results   = []
        best_run_acc  = -1

        for run in range(N_RUNS):
            print(f"\n  Beh {run+1}/{N_RUNS}")
            torch.manual_seed(run)

            model = build_model(arch_name, transfer_learning=is_tl)

            t0 = time.time()
            model, tr_losses, val_losses = train_model(
                model, train_loader, val_loader, EPOCHS, LR, device
            )
            elapsed = time.time() - t0

            criterion  = nn.CrossEntropyLoss()
            train_loss = evaluate_loss(model, train_loader, criterion, device)
            val_loss   = evaluate_loss(model, val_loader,   criterion, device)
            test_loss  = evaluate_loss(model, test_loader,  criterion, device)
            train_acc  = get_accuracy(model, train_loader, device)
            val_acc    = get_accuracy(model, val_loader,   device)
            test_acc   = get_accuracy(model, test_loader,  device)
            overfit_ep = average_overfitting_start(tr_losses, val_losses)

            r = dict(run=run+1, train_loss=train_loss, val_loss=val_loss,
                     test_loss=test_loss, train_acc=train_acc, val_acc=val_acc,
                     test_acc=test_acc, overfit_epoch=overfit_ep,
                     time_sec=elapsed)
            run_results.append(r)

            print(f"  Hotovo | train={train_acc:.2f}% val={val_acc:.2f}% "
                  f"test={test_acc:.2f}% | čas={elapsed:.0f}s")

            if test_acc > best_run_acc:
                best_run_acc = test_acc
                best_losses[key] = (tr_losses, val_losses)

            if test_acc > global_best_test_acc:
                global_best_test_acc = test_acc
                global_best_model    = copy.deepcopy(model).to(device)
                global_best_key      = key

        all_results[key] = {"arch": arch_name, "mode": mode, "runs": run_results}

# =========================================================
# 6. TABUĽKY A GRAFY – porovnanie modelov
# =========================================================
for arch_name in model_configs:
    for mode in modes:
        key = f"{arch_name}_{mode}"
        print_run_table(arch_name, mode, all_results[key]["runs"])

print("\n\n=== SÚHRNNÁ TABUĽKA VŠETKÝCH MODELOV ===")
print_summary_table(all_results)

print(f"\nGlobálne najlepší model: {global_best_key} | test acc = {global_best_test_acc:.2f}%")

plot_loss_curves(best_losses)

plot_confusion_matrix(
    global_best_model, test_loader, device,
    title=f"Confusion matrix – {global_best_key}"
)
show_sample_predictions(global_best_model, test_loader, device, n=10)

# =========================================================
# 7. AUGMENTÁCIA – pre najlepší model (3 nové behy)
# =========================================================
print(f"\n{'#'*65}")
print(f"AUGMENTÁCIA – najlepší model: {global_best_key}")
print(f"{'#'*65}")

best_arch = all_results[global_best_key]["arch"]
best_mode = all_results[global_best_key]["mode"]
is_tl_best = (best_mode == "TL")

print("\n=== Načítanie datasetu s augmentáciou ===")
train_loader_aug, val_loader_aug, test_loader_aug = build_loaders(use_augmentation=True)

aug_run_results  = []
aug_best_losses  = {}
best_aug_acc     = -1
best_aug_model   = None

for run in range(N_RUNS):
    print(f"\n  Beh {run+1}/{N_RUNS} (s augmentáciou)")
    torch.manual_seed(run)

    model = build_model(best_arch, transfer_learning=is_tl_best)
    model, tr_losses, val_losses = train_model(
        model, train_loader_aug, val_loader_aug, EPOCHS, LR, device
    )

    criterion  = nn.CrossEntropyLoss()
    train_loss = evaluate_loss(model, train_loader_aug, criterion, device)
    val_loss   = evaluate_loss(model, val_loader_aug,   criterion, device)
    test_loss  = evaluate_loss(model, test_loader_aug,  criterion, device)
    train_acc  = get_accuracy(model, train_loader_aug, device)
    val_acc    = get_accuracy(model, val_loader_aug,   device)
    test_acc   = get_accuracy(model, test_loader_aug,  device)
    overfit_ep = average_overfitting_start(tr_losses, val_losses)

    r = dict(run=run+1, train_loss=train_loss, val_loss=val_loss,
             test_loss=test_loss, train_acc=train_acc, val_acc=val_acc,
             test_acc=test_acc, overfit_epoch=overfit_ep)
    aug_run_results.append(r)

    if test_acc > best_aug_acc:
        best_aug_acc   = test_acc
        aug_best_losses[f"{global_best_key}_aug"] = (tr_losses, val_losses)
        best_aug_model = copy.deepcopy(model).to(device)

    print(f"  Hotovo | train={train_acc:.2f}% val={val_acc:.2f}% test={test_acc:.2f}%")

# =========================================================
# 8. TABUĽKY AUGMENTÁCIA
# =========================================================
print_run_table(global_best_key, "aug", aug_run_results)

# Porovnávacia tabuľka bez/s augmentáciou
no_aug_runs = all_results[global_best_key]["runs"]
print("\n=== POROVNANIE bez augmentácie vs. s augmentáciou ===")
print(f"{'Varianta':<20} {'Avg val loss':>13} {'Avg val%':>9} {'Avg test loss':>14} {'Avg test%':>10} {'Avg overfit ep.':>16}")
print("-" * 85)

def avg(lst, key): return np.mean([r[key] for r in lst])

for label, runs in [("bez augmentácie", no_aug_runs), ("s augmentáciou", aug_run_results)]:
    print(f"{label:<20} {avg(runs,'val_loss'):>13.4f} {avg(runs,'val_acc'):>9.2f} "
          f"{avg(runs,'test_loss'):>14.4f} {avg(runs,'test_acc'):>10.2f} "
          f"{avg(runs,'overfit_epoch'):>16.2f}")

# Loss krivky pre augmentáciu
plot_loss_curves(aug_best_losses, title_suffix=" (s augmentáciou)")

# Confusion matrix pre aug model
plot_confusion_matrix(best_aug_model, test_loader_aug, device,
                      title=f"Confusion matrix – {global_best_key} (aug)")
show_sample_predictions(best_aug_model, test_loader_aug, device, n=10)

print("\n=== Hotovo ===")