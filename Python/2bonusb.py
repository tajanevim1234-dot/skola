import genetic_toolbox as ga
import matplotlib.pyplot as plt
import numpy as np


def testfn3c(Pop):
    x0 = 30
    y0 = 100
    return np.sum(-(Pop - x0) * np.sin(np.sqrt(np.abs(Pop - x0))) + y0, axis=1)


space = ga.uniform_space(amount_of_genes=10, lower_limit=-1000, upper_limit=1000)

POP_SIZE = 50
NUM_GEN = 1000
PTS = 1
MODE = 0
N_RUNS = 5

ELITE_N = 4
MUT_RATE = 0.12

plt.figure()

best_overall_fit = np.inf
best_overall_x = None

for r in range(N_RUNS):
    pop = ga.genrpop(pop_size=POP_SIZE, space=space)
    evolution = []

    for i in range(NUM_GEN):
        fitness = ga.eggholder(pop)
        evolution.append(fitness.min())

        elite, _ = ga.selbest(pop, fitness, n_list=[ELITE_N])
        parents, _ = ga.seltourn(pop, fitness, n=POP_SIZE - ELITE_N)

        ga.crossov(parents, PTS, MODE)
        ga.mutx(parents, rate=MUT_RATE, space=space)

        pop = np.vstack([elite, parents])

    fitness = ga.eggholder(pop)
    run_best_fit = fitness.min()
    run_best_x = pop[np.argmin(fitness)]

    print(f"Run {r+1} best fitness: {run_best_fit:.4f}")

    if run_best_fit < best_overall_fit:
        best_overall_fit = run_best_fit
        best_overall_x = run_best_x.copy()

    plt.plot(evolution, label=f"Run {r+1}")

print("\nBest solution overall:\n", best_overall_x)
print("Best fitness overall:\n", best_overall_fit)

plt.xlabel("Generácia")
plt.ylabel("Best fitness")
plt.legend()
plt.grid()
plt.show()