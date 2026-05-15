import genetic_toolbox as ga
import matplotlib.pyplot as plt
import numpy as np
import random


def dlzka(p1, p2):
    return np.sqrt((p2[0] - p1[0])**2 + (p2[1] - p1[1])**2)


def fitness_jedinec(chrom, B):
    total = 0.0
    total += dlzka(B[0], B[chrom[0]])

    for i in range(len(chrom) - 1):
        total += dlzka(B[chrom[i]], B[chrom[i + 1]])

    total += dlzka(B[chrom[-1]], B[-1])
    return total


def fitness_pop(pop, B):
    fit = np.zeros(pop.shape[0])
    for i in range(pop.shape[0]):
        fit[i] = fitness_jedinec(pop[i], B)
    return fit


def ox_one(p1, p2):
    n = len(p1)
    c1, c2 = sorted(random.sample(range(n), 2))

    child = np.full(n, -1, dtype=int)
    child[c1:c2 + 1] = p1[c1:c2 + 1]

    p2_cycle = list(p2[c2 + 1:]) + list(p2[:c2 + 1])
    missing = [x for x in p2_cycle if x not in child]

    fill_positions = list(range(c2 + 1, n)) + list(range(0, c1))

    for pos, val in zip(fill_positions, missing):
        child[pos] = val

    return child


def crosord_python(pop):
    shape = pop.shape
    pair_list = list(range(shape[0]))
    random.shuffle(pair_list)

    new_pop = []

    for cyk in range(shape[0] // 2):
        i, j = pair_list[2 * cyk], pair_list[2 * cyk + 1]

        child1 = ox_one(pop[i], pop[j])
        child2 = ox_one(pop[j], pop[i])

        new_pop.append(child1)
        new_pop.append(child2)

    if shape[0] % 2 == 1:
        new_pop.append(pop[pair_list[-1]].copy())

    return np.array(new_pop, dtype=int)


def one_inversion(chrom):
    chrom = chrom.copy()
    i, j = sorted(np.random.choice(len(chrom), 2, replace=False))
    chrom[i:j+1] = chrom[i:j+1][::-1]
    return chrom


def invord_python(pop, pm):
    pop = pop.copy()
    rows = pop.shape[0]

    for r in range(rows):
        if random.random() < pm:
            pop[r] = one_inversion(pop[r])

    return pop


B = np.array([
    [0, 0],
    [17, 100],
    [51, 15],
    [70, 62],
    [42, 25],
    [32, 17],
    [51, 64],
    [39, 45],
    [68, 89],
    [20, 19],
    [12, 87],
    [80, 37],
    [35, 82],
    [2, 15],
    [38, 95],
    [33, 50],
    [85, 52],
    [97, 27],
    [99, 10],
    [37, 67],
    [20, 82],
    [49, 0],
    [62, 14],
    [7, 60],
    [0, 0]
])

# --- parametre ---
opakovania = 10
max_pop = 30
max_gen = 1000
pm = 0.15
elite_n = 2

final_values = []


best_overall_fit = np.inf
best_overall_chrom = None
best_overall_evolution = None

plt.figure(figsize=(10, 6))
plt.title("TSP GA - fitness vs generacia")
plt.grid(True)

for run in range(opakovania):
    pop = ga.genrpop_perm(max_pop, 1, 23).astype(int)
    evolution = []

    for gen in range(max_gen):
        fit = fitness_pop(pop, B)

        best_idx = np.argmin(fit)
        best_fit = fit[best_idx]
        best_chrom = pop[best_idx].copy()

        evolution.append(best_fit)

        if best_fit < best_overall_fit:
            best_overall_fit = best_fit
            best_overall_chrom = best_chrom.copy()
            best_overall_evolution = evolution.copy()

        elite, _ = ga.selbest(pop, fit, n_list=[elite_n])
        parents, _ = ga.seltourn(pop, fit, n=max_pop - elite_n)
        children = crosord_python(parents)
        children = invord_python(children, pm)

        pop = np.vstack((elite, children[:max_pop - elite_n]))

    final_values.append(evolution[-1])
    plt.plot(evolution, alpha=0.7, label=f"Run {run+1}: {evolution[-1]:.2f}")

print("Final values:", [round(x, 2) for x in final_values])
print("Pocet behov <= 480:", np.sum(np.array(final_values) <= 480))
print("Najlepsia fitness:", round(best_overall_fit, 4))
print("Najlepsi genom:", best_overall_chrom)

plt.xlabel("Generácia")
plt.ylabel("Best fitness")
plt.legend()
plt.show()

# priebeh fitness najlepsieho behu
plt.figure(figsize=(10, 6))
plt.plot(best_overall_evolution, linewidth=2)
plt.title("Najlepsi beh - fitness vs generacia")
plt.xlabel("Generácia")
plt.ylabel("Best fitness")
plt.grid(True)
plt.show()

# vizualizacia finalnej trasy v rovine
route = np.concatenate(([0], best_overall_chrom, [24]))
route_points = B[route]

plt.figure(figsize=(8, 8))
plt.plot(route_points[:, 0], route_points[:, 1], marker='o')

for idx in route:
    plt.text(B[idx, 0] + 1, B[idx, 1] + 1, str(idx), fontsize=9)

plt.title(f"Najlepsia najdena trasa, fitness = {best_overall_fit:.4f}")
plt.xlabel("x")
plt.ylabel("y")
plt.grid(True)
plt.axis("equal")
plt.show()