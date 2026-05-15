import numpy as np
import matplotlib.pyplot as plt

def f(x):
    return (-x * np.sin(np.sqrt(np.abs(x))))+100

d = 1.0
max_steps = 1000
restarts = 30

x_vals = np.linspace(-1000, 1000, 10000)
y_vals = f(x_vals)

plt.figure(figsize=(10, 6))
plt.plot(x_vals, y_vals, 'black', linewidth=1)

best_x = None
best_y = np.inf
best_steps_x = None
best_steps_y = None

for r in range(restarts):
    x0 = np.random.uniform(-1000, 1000)
    y0 = f(x0)

    steps_x = [x0]
    steps_y = [y0]
    d = 10.0               
    d_min = 1e-3          
    shrink = 0.5

    for i in range(max_steps):
        left = x0 - d
        right = x0 + d

        if left < -1000 or right > 1000:
            break

        y_left = f(left)
        y_right = f(right)

        if y_left < y0:
            x0, y0 = left, y_left
        elif y_right < y0:
            x0, y0 = right, y_right
        else:
            d *= shrink
            if d < d_min:
                break

        steps_x.append(x0)
        steps_y.append(y0)

    end_x = steps_x[-1]
    end_y = steps_y[-1]

    plt.plot(steps_x, steps_y, marker='o', markersize=3)
    plt.scatter(end_x, end_y, s=100)    

    if y0 < best_y:
        best_y = y0
        best_x = x0
        best_steps_x = steps_x
        best_steps_y = steps_y

print("BEST Minimum:", best_y)
print("BEST x:", best_x+30)

plt.title("Hill Climbing – All Runs")
plt.show()