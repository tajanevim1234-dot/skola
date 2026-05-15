import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import os
from sklearn.model_selection import train_test_split
from sklearn.neural_network import MLPClassifier
from sklearn.metrics import confusion_matrix, ConfusionMatrixDisplay, accuracy_score
from sklearn.preprocessing import StandardScaler

script_dir = os.path.dirname(os.path.abspath(__file__))
file_path = os.path.join(script_dir, "databody.csv")
data = pd.read_csv(file_path, header=None)

X = data.iloc[:, 0:3].values
y = np.array([1]*50 + [2]*50 + [3]*50 + [4]*50 + [5]*50)

fig = plt.figure(figsize=(9, 7))
ax = fig.add_subplot(111, projection='3d')

markers = {
    1: ('b', '+'),
    2: ('c', 'o'),
    3: ('g', '^'),
    4: ('r', 'd'),
    5: ('m', 'x')
}

for cls in sorted(np.unique(y)):
    idx = y == cls
    color, marker = markers[cls]
    ax.scatter(X[idx, 0], X[idx, 1], X[idx, 2], c=color, marker=marker, label=f"Trieda {cls}", s=50)

ax.set_title("Body z dát")
ax.set_xlabel("x")
ax.set_ylabel("y")
ax.set_zlabel("z")
ax.legend()
ax.grid(True)

X_train, X_test, y_train, y_test = train_test_split(
    X, y, train_size=0.8, random_state=42, stratify=y
)

scaler = StandardScaler()
X_train_s = scaler.fit_transform(X_train)
X_test_s = scaler.transform(X_test)
X_all_s = scaler.transform(X)

net = MLPClassifier(
    hidden_layer_sizes=(20, 10),
    activation='relu',
    solver='adam',
    max_iter=2000,
    random_state=42
)

net.fit(X_train_s, y_train)

plt.figure(figsize=(8, 5))
plt.plot(net.loss_curve_)
plt.title("Loss vs. epoch")
plt.xlabel("Epoch")
plt.ylabel("Loss")
plt.grid(True)
plt.show()

y_pred = net.predict(X_test_s)
print("=== TESTOVACIE DÁTA ===")
print("Presnosť test:", accuracy_score(y_test, y_pred))
print("Počet zlých klasifikácií test:", np.sum(y_test != y_pred))

cm = confusion_matrix(y_test, y_pred, labels=[1, 2, 3, 4, 5])
disp = ConfusionMatrixDisplay(confusion_matrix=cm, display_labels=[1, 2, 3, 4, 5])
disp.plot(cmap="Blues", values_format="d")
plt.title("Confusion matrix - test")
plt.show()

y_all_pred = net.predict(X_all_s)
print("=== VŠETKY DÁTA ===")
print("Presnosť na všetkých dátach:", accuracy_score(y, y_all_pred))
print("Zle klasifikované body zo všetkých 250:", np.sum(y_all_pred != y))

cm_all = confusion_matrix(y, y_all_pred, labels=[1, 2, 3, 4, 5])
disp_all = ConfusionMatrixDisplay(confusion_matrix=cm_all, display_labels=[1, 2, 3, 4, 5])
disp_all.plot(cmap="Blues", values_format="d")
plt.title("Confusion matrix - všetky dáta")
plt.show()

bodynew = np.array([
    [0.55, 0.25, 0.20],
    [0.30, 0.40, 0.70],
    [0.20, 0.70, 0.50],
    [0.70, 0.55, 0.35],
    [0.90, 0.85, 0.40]
])

bodynew_s = scaler.transform(bodynew)
triedy = net.predict(bodynew_s)
pravdepodobnosti = net.predict_proba(bodynew_s)

print("\n=== NOVÉ BODY ===")
for i in range(len(bodynew)):
    print(f"Bod {i+1}: {bodynew[i]}")
    print(f"  Pravdepodobnosti: {pravdepodobnosti[i]}")
    print(f"  Priradená trieda: {triedy[i]}")

farby = {1: 'b', 2: 'c', 3: 'g', 4: 'r', 5: 'm'}

for i in range(len(bodynew)):
    ax.scatter(
        bodynew[i, 0],
        bodynew[i, 1],
        bodynew[i, 2],
        c=farby[triedy[i]],
        marker='s',
        s=120,
        linewidths=2
    )

plt.show()