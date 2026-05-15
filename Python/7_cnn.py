import copy
import time
import torch
import torch.nn as nn
import torch.optim as optim
from torchvision import datasets, transforms
from torch.utils.data import DataLoader, random_split
import numpy as np
import matplotlib.pyplot as plt
from sklearn.metrics import confusion_matrix, ConfusionMatrixDisplay

# =========================================================
# 1. DÁTA
# =========================================================
transform = transforms.ToTensor()

train_full = datasets.MNIST(root="./data", train=True, download=True, transform=transform)
test_set   = datasets.MNIST(root="./data", train=False, download=True, transform=transform)

val_size   = 10000
train_size = len(train_full) - val_size

split_generator = torch.Generator().manual_seed(42)
train_set, val_set = random_split(train_full, [train_size, val_size], generator=split_generator)

BATCH = 64

train_loader = DataLoader(train_set, batch_size=BATCH, shuffle=True)
val_loader   = DataLoader(val_set,   batch_size=BATCH, shuffle=False)
test_loader  = DataLoader(test_set,  batch_size=BATCH, shuffle=False)

# =========================================================
# 2. CNN MODEL
# =========================================================
class CNN(nn.Module):
    def __init__(self, conv_channels, fc_size=128, dropout=0.0):
        super().__init__()

        conv_layers = []
        in_channels = 1
        
        for out_channels in conv_channels:
            conv_layers.append(nn.Conv2d(in_channels, out_channels, kernel_size=3, padding=1))
            conv_layers.append(nn.ReLU())
            conv_layers.append(nn.MaxPool2d(2))
            in_channels = out_channels

        self.features = nn.Sequential(*conv_layers)

        with torch.no_grad():
            dummy = torch.zeros(1, 1, 28, 28)
            dummy_out = self.features(dummy)
            flatten_size = dummy_out.view(1, -1).size(1)

        self.classifier = nn.Sequential(
            nn.Flatten(),
            nn.Linear(flatten_size, fc_size),
            nn.ReLU(),
            nn.Dropout(dropout),
            nn.Linear(fc_size, 10)
        )

    def forward(self, x):
        x = self.features(x)
        x = self.classifier(x)
        return x

# =========================================================
# 3. POMOCNÉ FUNKCIE
# =========================================================
def evaluate_loss(model, loader, criterion, device):
    model.eval()
    total_loss = 0.0

    with torch.no_grad():
        for X_batch, y_batch in loader:
            X_batch, y_batch = X_batch.to(device), y_batch.to(device)
            output = model(X_batch)
            loss = criterion(output, y_batch)
            total_loss += loss.item()

    return total_loss / len(loader)


def get_accuracy(model, loader, device):
    model.eval()
    correct = 0
    total = 0

    with torch.no_grad():
        for X_batch, y_batch in loader:
            X_batch, y_batch = X_batch.to(device), y_batch.to(device)
            preds = model(X_batch).argmax(dim=1)
            correct += (preds == y_batch).sum().item()
            total += y_batch.size(0)

    return correct / total * 100


def train_model(model, train_loader, val_loader, epochs, lr, device):
    model = model.to(device)
    optimizer = optim.Adam(model.parameters(), lr=lr)
    criterion = nn.CrossEntropyLoss()

    train_losses = []
    val_losses = []

    for epoch in range(epochs):
        model.train()
        running_loss = 0.0

        for X_batch, y_batch in train_loader:
            X_batch, y_batch = X_batch.to(device), y_batch.to(device)

            optimizer.zero_grad()
            output = model(X_batch)
            loss = criterion(output, y_batch)
            loss.backward()
            optimizer.step()

            running_loss += loss.item()

        train_epoch_loss = running_loss / len(train_loader)
        val_epoch_loss = evaluate_loss(model, val_loader, criterion, device)

        train_losses.append(train_epoch_loss)
        val_losses.append(val_epoch_loss)

        print(f"    Epocha {epoch+1:2d}/{epochs} | train loss={train_epoch_loss:.4f} | val loss={val_epoch_loss:.4f}")

    return model, train_losses, val_losses


def print_run_table(model_name, run_results):
    print(f"\nVýsledky behov pre {model_name}")
    print(f"{'Beh':<5} {'Train loss':>12} {'Test loss':>12} {'Train acc %':>12} {'Test acc %':>12}")
    print("-" * 58)

    for r in run_results:
        print(
            f"{r['run']:<5} "
            f"{r['train_loss']:>12.4f} "
            f"{r['test_loss']:>12.4f} "
            f"{r['train_acc']:>12.2f} "
            f"{r['test_acc']:>12.2f}"
        )


def print_summary_table(results):
    print(f"\n{'Model':<8} {'Min train%':>11} {'Max train%':>11} {'Avg train%':>11} {'Min test%':>11} {'Max test%':>11} {'Avg test%':>11} {'Avg test loss':>14}")
    print("-" * 95)

    for name, d in results.items():
        train_accs  = [r['train_acc'] for r in d['runs']]
        test_accs   = [r['test_acc'] for r in d['runs']]
        test_losses = [r['test_loss'] for r in d['runs']]

        print(
            f"{name:<8}"
            f"{min(train_accs):>11.2f}"
            f"{max(train_accs):>11.2f}"
            f"{np.mean(train_accs):>11.2f}"
            f"{min(test_accs):>11.2f}"
            f"{max(test_accs):>11.2f}"
            f"{np.mean(test_accs):>11.2f}"
            f"{np.mean(test_losses):>14.4f}"
        )


def plot_loss_curves(best_losses):
    for name, (tr_l, val_l) in best_losses.items():
        plt.figure(figsize=(7, 5))
        plt.plot(tr_l, label='Trénovacia loss')
        plt.plot(val_l, label='Validačná loss')
        plt.title(f"Loss vs Epoch – {name}")
        plt.xlabel("Epocha")
        plt.ylabel("Loss")
        plt.legend()
        plt.grid(True)
        plt.show()


def plot_confusion_matrix(model, loader, device, title="Confusion matrix"):
    all_preds = []
    all_labels = []

    model.eval()
    with torch.no_grad():
        for X_batch, y_batch in loader:
            X_batch = X_batch.to(device)
            preds = model(X_batch).argmax(dim=1).cpu()

            all_preds.extend(preds.numpy())
            all_labels.extend(y_batch.numpy())

    cm = confusion_matrix(all_labels, all_preds)
    disp = ConfusionMatrixDisplay(cm, display_labels=list(range(10)))
    fig, ax = plt.subplots(figsize=(8, 7))
    disp.plot(cmap='Blues', ax=ax, colorbar=False)
    plt.title(title)
    plt.show()


def show_sample_predictions(model, loader, device, n=10):
    model.eval()

    shown = 0
    plt.figure(figsize=(14, 6))

    with torch.no_grad():
        for X_batch, y_batch in loader:
            X_batch = X_batch.to(device)
            outputs = model(X_batch)
            probs = torch.softmax(outputs, dim=1)
            preds = outputs.argmax(dim=1)

            for i in range(X_batch.size(0)):
                if shown >= n:
                    plt.tight_layout()
                    plt.show()
                    return

                img = X_batch[i].cpu().squeeze().numpy()
                true_label = y_batch[i].item()
                pred_label = preds[i].item()
                pred_prob = probs[i][pred_label].item()

                plt.subplot(2, 5, shown + 1)
                plt.imshow(img, cmap="gray")
                plt.title(f"T:{true_label} P:{pred_label}\n{pred_prob:.3f}")
                plt.axis("off")

                shown += 1


def average_overfitting_start(train_losses, val_losses):
    # prvá epocha, kde val loss začne byť vyššia než train loss
    # len jednoduché orientačné pravidlo na tabuľku dropout
    for i in range(len(train_losses)):
        if val_losses[i] > train_losses[i]:
            return i + 1
    return len(train_losses)

# =========================================================
# 4. NASTAVENIA
# =========================================================
EPOCHS = 10
LR = 0.001
N_RUNS = 1

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
print(f"Používam: {device}")

cnn_architectures = {
    "CNN1": {"conv": [32, 64],       "fc": 128, "dropout": 0.0},
    "CNN2": {"conv": [32, 64, 128],  "fc": 128, "dropout": 0.0},
    "CNN3": {"conv": [32, 64, 128],  "fc": 256, "dropout": 0.0},
}

# =========================================================
# 5. POROVNANIE 3 CNN ARCHITEKTÚR
# =========================================================
results_cnn = {}
best_losses_cnn = {}

global_best_test_acc = -1
global_best_model = None
global_best_model_name = None

for name, cfg in cnn_architectures.items():
    print(f"\n{'='*60}")
    print(f"Model {name} | conv={cfg['conv']} | fc={cfg['fc']} | dropout={cfg['dropout']}")
    print(f"{'='*60}")

    run_results = []
    best_arch_test_acc = -1

    for run in range(N_RUNS):
        print(f"\n  Beh {run+1}/{N_RUNS}")
        torch.manual_seed(run)

        model = CNN(conv_channels=cfg["conv"], fc_size=cfg["fc"], dropout=cfg["dropout"])

        start_time = time.time()
        model, tr_losses, val_losses = train_model(
            model, train_loader, val_loader, EPOCHS, LR, device
        )
        train_time = time.time() - start_time

        criterion = nn.CrossEntropyLoss()

        train_loss = evaluate_loss(model, train_loader, criterion, device)
        test_loss  = evaluate_loss(model, test_loader, criterion, device)
        train_acc  = get_accuracy(model, train_loader, device)
        test_acc   = get_accuracy(model, test_loader, device)

        run_info = {
            "run": run + 1,
            "train_loss": train_loss,
            "test_loss": test_loss,
            "train_acc": train_acc,
            "test_acc": test_acc,
            "train_time_sec": train_time,
        }
        run_results.append(run_info)

        print(
            f"  Hotovo | train loss={train_loss:.4f} | test loss={test_loss:.4f} | "
            f"train acc={train_acc:.2f}% | test acc={test_acc:.2f}% | čas={train_time:.1f}s"
        )

        if test_acc > best_arch_test_acc:
            best_arch_test_acc = test_acc
            best_losses_cnn[name] = (tr_losses, val_losses)

        if test_acc > global_best_test_acc:
            global_best_test_acc = test_acc
            global_best_model = copy.deepcopy(model).to(device)
            global_best_model_name = name

    results_cnn[name] = {
        "config": cfg,
        "runs": run_results,
    }

# =========================================================
# 6. TABUĽKY A GRAFY PRE CNN ARCHITEKTÚRY
# =========================================================
for name in cnn_architectures:
    print_run_table(name, results_cnn[name]["runs"])

print_summary_table(results_cnn)
plot_loss_curves(best_losses_cnn)

print(f"\nGlobálne najlepší CNN: {global_best_model_name} | test acc = {global_best_test_acc:.2f}%")
plot_confusion_matrix(
    global_best_model,
    test_loader,
    device,
    title=f"Confusion matrix – najlepší CNN ({global_best_model_name})"
)

show_sample_predictions(global_best_model, test_loader, device, n=10)

# =========================================================
# 7. DROPOUT POROVNANIE
# =========================================================
print(f"\n{'#'*60}")
print("DROPOUT POROVNANIE")
print(f"{'#'*60}")

dropout_values = [0.0, 0.3, 0.5]

dropout_results = {}
dropout_best_losses = {}

# použijeme rovnakú architektúru a meníme len dropout
dropout_base_conv = [32, 64, 128]
dropout_base_fc = 128

for d in dropout_values:
    model_name = f"DROPOUT_{d}"
    print(f"\n{'='*60}")
    print(f"Model {model_name} | conv={dropout_base_conv} | fc={dropout_base_fc} | dropout={d}")
    print(f"{'='*60}")

    run_results = []
    overfit_epochs = []
    best_test_acc = -1

    for run in range(N_RUNS):
        print(f"\n  Beh {run+1}/{N_RUNS}")
        torch.manual_seed(run)

        model = CNN(conv_channels=dropout_base_conv, fc_size=dropout_base_fc, dropout=d)

        model, tr_losses, val_losses = train_model(
            model, train_loader, val_loader, EPOCHS, LR, device
        )

        criterion = nn.CrossEntropyLoss()

        train_loss = evaluate_loss(model, train_loader, criterion, device)
        test_loss  = evaluate_loss(model, test_loader, criterion, device)
        train_acc  = get_accuracy(model, train_loader, device)
        test_acc   = get_accuracy(model, test_loader, device)
        overfit_ep = average_overfitting_start(tr_losses, val_losses)

        run_info = {
            "run": run + 1,
            "train_loss": train_loss,
            "test_loss": test_loss,
            "train_acc": train_acc,
            "test_acc": test_acc,
            "overfit_epoch": overfit_ep,
        }
        run_results.append(run_info)
        overfit_epochs.append(overfit_ep)

        print(
            f"  Hotovo | train loss={train_loss:.4f} | test loss={test_loss:.4f} | "
            f"train acc={train_acc:.2f}% | test acc={test_acc:.2f}% | overfit epoch={overfit_ep}"
        )

        if test_acc > best_test_acc:
            best_test_acc = test_acc
            dropout_best_losses[model_name] = (tr_losses, val_losses)

    dropout_results[model_name] = {
        "dropout": d,
        "runs": run_results,
        "avg_overfit_epoch": np.mean(overfit_epochs),
    }

# =========================================================
# 8. TABUĽKY PRE DROPOUT
# =========================================================
print("\nPorovnanie dropout-u")
print(f"{'Model':<15} {'Dropout':>10} {'Počet behov':>12} {'Priemer ep. pretrén.':>22} {'Priemer val/test loss':>22}")
print("-" * 85)

for name, d in dropout_results.items():
    test_losses = [r["test_loss"] for r in d["runs"]]
    print(
        f"{name:<15}"
        f"{d['dropout']:>10.1f}"
        f"{len(d['runs']):>12}"
        f"{d['avg_overfit_epoch']:>22.2f}"
        f"{np.mean(test_losses):>22.4f}"
    )

print("\nSúhrn dropout výsledkov")
print(f"{'Dropout':<10} {'Avg train acc %':>16} {'Avg test acc %':>15} {'Avg test loss':>15}")
print("-" * 60)

for name, d in dropout_results.items():
    train_accs = [r["train_acc"] for r in d["runs"]]
    test_accs  = [r["test_acc"] for r in d["runs"]]
    test_losses = [r["test_loss"] for r in d["runs"]]

    print(
        f"{d['dropout']:<10.1f}"
        f"{np.mean(train_accs):>16.2f}"
        f"{np.mean(test_accs):>15.2f}"
        f"{np.mean(test_losses):>15.4f}"
    )

plot_loss_curves(dropout_best_losses)