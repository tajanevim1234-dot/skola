import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import os
from sklearn.model_selection import train_test_split
from sklearn.neural_network import MLPClassifier
from sklearn.metrics import (confusion_matrix, ConfusionMatrixDisplay,
                             accuracy_score)
from sklearn.preprocessing import StandardScaler

# ─────────────────────────────────────────────
# 1. NAČÍTANIE DÁT
# ─────────────────────────────────────────────
script_dir = os.path.dirname(os.path.abspath(__file__))
file_path = os.path.join(script_dir, "CTG.csv")
df = pd.read_csv(file_path)

df = df.dropna(subset=['NSP'])

features = ['LB','AC','FM','UC','ASTV','MSTV','ALTV','MLTV',
            'DL','DS','DP','DR','Width','Min','Max','Nmax',
            'Nzeros','Mode','Mean','Median','Variance','Tendency']

X = df[features].values
y = df['NSP'].astype(int).values

print(f"Dataset: {X.shape[0]} záznamov, {X.shape[1]} príznakov")
print(f"Triedy: {np.bincount(y)[1:]}")   # počty tried 1, 2, 3

# ─────────────────────────────────────────────
# 2. ROZDELENIE DÁT  (60 % tréning, 40 % test)
# ─────────────────────────────────────────────
X_train_full, X_test, y_train_full, y_test = train_test_split(
    X, y, test_size=0.40, random_state=0, stratify=y
)

print(f"Tréning: {len(X_train_full)}  |  Test: {len(X_test)}")

scaler = StandardScaler()
X_train_full_s = scaler.fit_transform(X_train_full)
X_test_s       = scaler.transform(X_test)

# ─────────────────────────────────────────────
# 3. DEFINÍCIA 3 ARCHITEKTÚR
# ─────────────────────────────────────────────
architectures = {
    'M1': (32,),       
    'M2': (32, 16),     
    'M3': (32, 16, 8),  
}

EPOCHS    = 300
LR        = 0.001
BATCH     = 32
N_RUNS    = 5

# ─────────────────────────────────────────────
# 4. TRÉNING – 5 SPUSTENÍ PRE KAŽDÚ ARCHITEKTÚRU
# ─────────────────────────────────────────────
results = {}

for model_name, hidden in architectures.items():
    print(f"\n{'='*50}")
    print(f"Model {model_name}  skryté vrstvy: {hidden}")
    print(f"{'='*50}")

    train_accs = []
    test_accs  = []

    for run in range(N_RUNS):

        net = MLPClassifier(
            hidden_layer_sizes=hidden,
            activation='relu',
            solver='adam',
            learning_rate_init=LR,
            batch_size=BATCH,
            max_iter=EPOCHS,
            random_state=run, 
            early_stopping=True, 
            validation_fraction=0.15,
            n_iter_no_change=20,
        )

        net.fit(X_train_full_s, y_train_full)

        tr_acc  = accuracy_score(y_train_full, net.predict(X_train_full_s))
        te_acc  = accuracy_score(y_test,       net.predict(X_test_s))

        train_accs.append(tr_acc)
        test_accs.append(te_acc)

        print(f"  Beh {run+1}: train acc = {tr_acc*100:.1f}%  |  "
              f"test acc = {te_acc*100:.1f}%")

    results[model_name] = {
        'hidden': hidden,
        'train_accs': train_accs,
        'test_accs':  test_accs,
    }

# ─────────────────────────────────────────────
# 5. TABUĽKA – SÚHRNNÉ POROVNANIE MODELOV
# ─────────────────────────────────────────────

print("\n\n" + "="*70)
print("SÚHRNNÉ POROVNANIE MODELOV")
print("="*70)
print(f"{'Model':<6} {'Vrstvy':<16} {'Min test%':>9} {'Max test%':>9} "
      f"{'Avg test%':>9} {'Min train%':>10} {'Max train%':>10} {'Avg train%':>10}")
print("-"*70)

for m, d in results.items():
    ta = d['test_accs']
    tr = d['train_accs']
    print(f"{m:<6} {str(d['hidden']):<16} "
          f"{min(ta)*100:>9.1f} {max(ta)*100:>9.1f} {np.mean(ta)*100:>9.1f} "
          f"{min(tr)*100:>10.1f} {max(tr)*100:>10.1f} {np.mean(tr)*100:>10.1f}")

# ─────────────────────────────────────────────
# 6. NAJLEPŠÍ MODEL – znovu natrénuj pre grafy
# ─────────────────────────────────────────────
best_model_name = max(results, key=lambda m: np.mean(results[m]['test_accs']))
best_hidden     = results[best_model_name]['hidden']
print(f"\nNajlepší model: {best_model_name}  {best_hidden}")

best_run_idx = int(np.argmax(results[best_model_name]['test_accs']))

best_net = MLPClassifier(
    hidden_layer_sizes=best_hidden,
    activation='relu',
    solver='adam',
    learning_rate_init=LR,
    batch_size=BATCH,
    max_iter=EPOCHS,
    random_state=best_run_idx,
    early_stopping=True,
    validation_fraction=0.15,
    n_iter_no_change=20,
)
best_net.fit(X_train_full_s, y_train_full)

y_pred_test  = best_net.predict(X_test_s)
y_pred_train = best_net.predict(X_train_full_s)
y_pred_all   = best_net.predict(scaler.transform(X))

print(f"Najlepší beh (run {best_run_idx+1}):")
print(f"  Train accuracy: {accuracy_score(y_train_full, y_pred_train)*100:.2f}%")
print(f"  Test  accuracy: {accuracy_score(y_test, y_pred_test)*100:.2f}%")
print(f"  Celková accuracy: {accuracy_score(y, y_pred_all)*100:.2f}%")

# ─────────────────────────────────────────────
# 7. GRAF – LOSS VS EPOCH  (najlepší model)
# ─────────────────────────────────────────────

plt.figure(figsize=(8, 5))
plt.plot(best_net.loss_curve_,             label='Trénovacia loss', color='blue')
plt.plot(best_net.validation_scores_,      label='Validačná accuracy', color='orange',
         linestyle='--')
plt.title(f"Loss vs. Epoch – {best_model_name} {best_hidden}")
plt.xlabel("Epocha")
plt.ylabel("Loss / Accuracy")
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.savefig(os.path.join(script_dir, "loss_curve.png"), dpi=150)
plt.show()

# ─────────────────────────────────────────────
# 8. CONFUSION MATRIX – testovacie dáta
# ─────────────────────────────────────────────

cm = confusion_matrix(y_test, y_pred_test, labels=[1, 2, 3])
disp = ConfusionMatrixDisplay(confusion_matrix=cm,
                               display_labels=['Normálny', 'Podozrivý', 'Patologický'])
fig, ax = plt.subplots(figsize=(6, 5))
disp.plot(cmap='Blues', values_format='d', ax=ax)
ax.set_title(f"Confusion matrix – test  ({best_model_name})")
plt.tight_layout()
plt.savefig(os.path.join(script_dir, "confusion_matrix.png"), dpi=150)
plt.show()

# ─────────────────────────────────────────────
# 9. TESTOVANIE VZORIEK – jedna z každej triedy
# ─────────────────────────────────────────────

print("\n" + "="*50)
print("TESTOVANIE VZORIEK Z DATASETU")
print("="*50)

class_labels = {1: 'Normálny', 2: 'Podozrivý', 3: 'Patologický'}
sample_rows = []

for cls in [1, 2, 3]:
    idx = np.where(y == cls)[0][0]
    sample_rows.append(idx)

for idx in sample_rows:
    sample_X = scaler.transform(X[idx].reshape(1, -1))
    pred      = best_net.predict(sample_X)[0]
    proba     = best_net.predict_proba(sample_X)[0]
    skutocna  = y[idx]
    print(f"\nVzorka index {idx}  |  Skutočná trieda: {class_labels[skutocna]}")
    print(f"  Predikcia:     {class_labels[pred]}")
    print(f"  Pravdepodobnosti: Normálny={proba[0]*100:.1f}%  "
          f"Podozrivý={proba[1]*100:.1f}%  Patologický={proba[2]*100:.1f}%")
    print(f"  {'✓ SPRÁVNE' if pred == skutocna else '✗ ZLE'}")

# ─────────────────────────────────────────────
# 10. SENZITIVITA A ŠPECIFICITA (pre každú triedu)
# ─────────────────────────────────────────────

print("\n" + "="*50)
print("SENZITIVITA A ŠPECIFICITA")
print("="*50)
print(f"{'Trieda':<15} {'TP':>5} {'FN':>5} {'FP':>5} {'TN':>5} "
      f"{'Senzitivita':>12} {'Špecificita':>12}")
print("-"*70)

for cls in [1, 2, 3]:
    y_bin_true = (y_test == cls).astype(int)
    y_bin_pred = (y_pred_test == cls).astype(int)

    TP = int(np.sum((y_bin_true == 1) & (y_bin_pred == 1)))
    TN = int(np.sum((y_bin_true == 0) & (y_bin_pred == 0)))
    FP = int(np.sum((y_bin_true == 0) & (y_bin_pred == 1)))
    FN = int(np.sum((y_bin_true == 1) & (y_bin_pred == 0)))

    senzitivita = TP / (TP + FN) if (TP + FN) > 0 else 0
    specificita = TN / (TN + FP) if (TN + FP) > 0 else 0

    print(f"{class_labels[cls]:<15} {TP:>5} {FN:>5} {FP:>5} {TN:>5} "
          f"{senzitivita*100:>11.1f}% {specificita*100:>11.1f}%")

print("\nHotovo!")