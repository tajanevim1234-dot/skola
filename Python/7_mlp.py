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
train_size = len(train_full) - val_size  # 50000

split_generator = torch.Generator().manual_seed(42)
train_set, val_set = random_split(train_full, [train_size, val_size], generator=split_generator)

BATCH = 64

train_loader = DataLoader(train_set, batch_size=BATCH, shuffle=True)
val_loader   = DataLoader(val_set,   batch_size=BATCH, shuffle=False)
test_loader  = DataLoader(test_set,  batch_size=BATCH, shuffle=False)

# =========================================================
# 2. MODEL
# =========================================================
class MLP(nn.Module):
    def __init__(self, hidden_sizes):
        super().__init__()

        layers = [] 
        in_size = 784

        for h in hidden_sizes:
            layers.append(nn.Linear(in_size, h))
            layers.append(nn.ReLU())
            in_size = h

        layers.append(nn.Linear(in_size, 10))
        self.net = nn.Sequential(*layers)

    def forward(self, x):
        x = x.view(x.size(0), -1)  # 28x28 -> 784
        return self.net(x)

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


def print_summary_table(results_mlp):
    print(f"\n{'Model':<8} {'Min train%':>11} {'Max train%':>11} {'Avg train%':>11} {'Min test%':>11} {'Max test%':>11} {'Avg test%':>11} {'Avg test loss':>14}")
    print("-" * 95)

    for name, d in results_mlp.items():
        train_accs = [r['train_acc'] for r in d['runs']]
        test_accs  = [r['test_acc'] for r in d['runs']]
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

# =========================================================
# 4. NASTAVENIA
# =========================================================
EPOCHS = 10
LR = 0.001
N_RUNS = 1

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
print(f"Používam: {device}")

architectures_mlp = {
    "MLP1": [128],
    "MLP2": [256, 128],
}

# =========================================================
# 5. TRÉNOVANIE A POROVNANIE MLP
# =========================================================
results_mlp = {}
best_losses = {}

global_best_test_acc = -1
global_best_model = None
global_best_model_name = None

for name, hidden in architectures_mlp.items():
    print(f"\n{'='*60}")
    print(f"Model {name} | skryté vrstvy: {hidden}")
    print(f"{'='*60}")

    run_results = []
    best_arch_test_acc = -1

    for run in range(N_RUNS):
        print(f"\n  Beh {run+1}/{N_RUNS}")
        torch.manual_seed(run)

        model = MLP(hidden)

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
            best_losses[name] = (tr_losses, val_losses)

        if test_acc > global_best_test_acc:
            global_best_test_acc = test_acc
            global_best_model = copy.deepcopy(model).to(device)
            global_best_model_name = name

    results_mlp[name] = {
        "hidden": hidden,
        "runs": run_results,
    }

# =========================================================
# 6. TABUĽKY VÝSLEDKOV
# =========================================================
for name in architectures_mlp:
    print_run_table(name, results_mlp[name]["runs"])

print_summary_table(results_mlp)

# =========================================================
# 7. GRAFY LOSS PRE NAJLEPŠÍ BEH KAŽDEJ ARCHITEKTÚRY
# =========================================================
plot_loss_curves(best_losses)

# =========================================================
# 8. CONFUSION MATRIX PRE GLOBÁLNE NAJLEPŠÍ MLP
# =========================================================
print(f"\nGlobálne najlepší MLP: {global_best_model_name} | test acc = {global_best_test_acc:.2f}%")
plot_confusion_matrix(
    global_best_model,
    test_loader,
    device,
    title=f"Confusion matrix – najlepší MLP ({global_best_model_name})"
)

# =========================================================
# 9. UKÁŽKY PREDIKCIÍ
# =========================================================
show_sample_predictions(global_best_model, test_loader, device, n=10)