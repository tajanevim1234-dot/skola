import genetic_toolbox as ga
import matplotlib.pyplot as plt
import numpy as np




def fitness_umerna(jedinec):

    x1, x2, x3, x4, x5 = jedinec

    vynos = 0.04*x1 + 0.07*x2 + 0.11*x3 + 0.06*x4 + 0.05*x5

    penalty = 0

    total = x1 + x2 + x3 + x4 + x5

    if total > 10000000:
        penalty += (total - 10000000) * 5

    if x1 + x2 > 2500000:
        penalty += (x1 + x2 - 2500000)*1

    if x4 < x5:
        penalty += (x5 - x4)*1

    if x3 + x4 > 0.5*total:
        penalty += (x3 + x4 - 0.5*total)*1

    fitness = -vynos + penalty

    return fitness

def fitness_mrtva(jedinec):
    x1, x2, x3, x4, x5 = jedinec
    vynos = 0.04*x1 + 0.07*x2 + 0.11*x3 + 0.06*x4 + 0.05*x5
    total = x1 + x2 + x3 + x4 + x5

    porusenie = False

    if total > 10000000:
        porusenie = True
    if x1 + x2 > 2500000:
        porusenie = True
    if x4 < x5:
        porusenie = True
    if x3 + x4 > 0.5*total:
        porusenie = True

    penalty = 10000000 if porusenie else 0
    return -vynos + penalty

def fitness_stupnovita(jedinec):
    x1, x2, x3, x4, x5 = jedinec
    vynos = 0.04*x1 + 0.07*x2 + 0.11*x3 + 0.06*x4 + 0.05*x5
    total = x1 + x2 + x3 + x4 + x5

    pocet = 0

    if total > 10000000:
        pocet += 1
    if x1 + x2 > 2500000:
        pocet += 1
    if x4 < x5:
        pocet += 1
    if x3 + x4 > 0.5*total:
        pocet += 1

    penalty = pocet * 500000
    return -vynos + penalty


def fitness_pop(pop):
    fit = np.zeros(pop.shape[0])
    for i in range(pop.shape[0]):
        fit[i] = fitness_mrtva(pop[i]) #tu sa prepisuju fitness typi
    return fit
    


space = ga.uniform_space(amount_of_genes=5, lower_limit=0, upper_limit=10000000)

POP_SIZE = 50
NUM_GEN = 100
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
        fitness = fitness_pop(pop)
        evolution.append(fitness.min())

        elite, _ = ga.selbest(pop, fitness, n_list=[ELITE_N])
        parents, _ = ga.seltourn(pop, fitness, n=POP_SIZE - ELITE_N)

        ga.crossov(parents, PTS, MODE)
        ga.mutx(parents, rate=MUT_RATE, space=space)

        pop = np.vstack([elite, parents])

    fitness = fitness_pop(pop)
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