import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

# 1D Schwefel
def f1(x):
    return -x * np.sin(np.sqrt(abs(x)))

# 3D funkcia
def F3(x, y, z):
    return f1(x) + f1(y) + f1(z)

def hill_climb_3d(d=2.0, max_steps=2000, bounds=(-1000, 1000), seed=42):
    rng = np.random.default_rng(seed)

    x = rng.uniform(*bounds)
    y = rng.uniform(*bounds)
    z = rng.uniform(*bounds)
    val = F3(x, y, z)

    path = [(x, y, z, val)]

    for _ in range(max_steps):
        neighbors = [
            (x + d, y, z),
            (x - d, y, z),
            (x, y + d, z),
            (x, y - d, z),
            (x, y, z + d),
            (x, y, z - d),
        ]

        best_neighbor = None
        best_val = val

        for nx, ny, nz in neighbors:
            if bounds[0] <= nx <= bounds[1] and \
               bounds[0] <= ny <= bounds[1] and \
               bounds[0] <= nz <= bounds[1]:

                new_val = F3(nx, ny, nz)
                if new_val < best_val:
                    best_val = new_val
                    best_neighbor = (nx, ny, nz)

        if best_neighbor is None:
            break
        else:
            x, y, z = best_neighbor
            val = best_val
            path.append((x, y, z, val))

    return np.array(path)

path = hill_climb_3d()

fig = plt.figure(figsize=(10, 8))
ax = fig.add_subplot(111, projection='3d')

z0 = path[0,2]

x_vals = np.linspace(-1000, 1000, 200)
y_vals = np.linspace(-1000, 1000, 200)
X, Y = np.meshgrid(x_vals, y_vals)
Z = f1(X) + f1(Y) + f1(z0)

ax.plot_surface(X, Y, Z, cmap='viridis', alpha=0.6)

ax.plot(path[:,0], path[:,1], path[:,3], color='blue', marker='o')
ax.scatter(path[-1,0], path[-1,1], path[-1,3], color='red', s=100)

ax.set_xlabel("x")
ax.set_ylabel("y")
ax.set_zlabel("F(x,y,z)")
ax.set_title("3D Deterministic Hill Climbing")

plt.show()