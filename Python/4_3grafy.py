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
        penalty += (x1 + x2 - 2500000)
    if x4 < x5:
        penalty += (x5 - x4)
    if x3 + x4 > 0.5*total:
        penalty += (x3 + x4 - 0.5*total)

    return -vynos + penalty


def fitness_mrtva(jedinec):
    x1, x2, x3, x4, x5 = jedinec
    vynos = 0.04*x1 + 0.07*x2 + 0.11*x3 + 0.06*x4 + 0.05*x5
    total = x1 + x2 + x3 + x4 + x5

    if total > 10000000:
        return 1e12
    if x1 + x2 > 2500000:
        return 1e12
    if x4 < x5:
        return 1e12
    if x3 + x4 > 0.5 * total:
        return 1e12

    return -vynos


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

    penalty = pocet * 10000000
    return -vynos + penalty

def je_platne(jedinec):
    x1, x2, x3, x4, x5 = jedinec
    total = x1 + x2 + x3 + x4 + x5

    if total > 10000000:
        return False
    if x1 + x2 > 2500000:
        return False
    if x4 < x5:
        return False
    if x3 + x4 > 0.5 * total:
        return False

    return True

def run_ga(fitness_func):

    def fitness_pop(pop):
        fit = np.zeros(pop.shape[0])
        for i in range(pop.shape[0]):
            fit[i] = fitness_func(pop[i])
        return fit

    space = ga.uniform_space(amount_of_genes=5, lower_limit=0, upper_limit=10000000)

    POP_SIZE = 100
    NUM_GEN = 300
    PTS = 1
    MODE = 0
    N_RUNS = 5

    ELITE_N = 4
    MUT_RATE = 0.12

    runs = []
    best_fit = np.inf
    best_x = None

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

        runs.append(evolution)

        fitness = fitness_pop(pop)
        run_best_fit = fitness.min()
        run_best_x = pop[np.argmin(fitness)]

        if run_best_fit < best_fit:
            best_fit = run_best_fit
            best_x = run_best_x.copy()

    return runs, best_x, best_fit


runs_u, sol_u, fit_u = run_ga(fitness_umerna)
runs_m, sol_m, fit_m = run_ga(fitness_mrtva)
runs_s, sol_s, fit_s = run_ga(fitness_stupnovita)


fig, ax = plt.subplots(1,3, figsize=(18,6))


def plot_graph(ax, runs, solution, fit, title):

    for i, run in enumerate(runs):
        ax.plot(run, label=f"Run {i+1}  min={min(run):.2f}")

    ax.set_title(title)
    ax.set_xlabel("Generácia")
    ax.set_ylabel("Best fitness")
    ax.legend()
    ax.grid()

    platne = je_platne(solution)
    text = f"x = {np.round(solution,0)}\nmin fitness = {fit:.2f}\nplatne = {platne}"
    ax.text(0.5, -0.25, text,
            transform=ax.transAxes,
            ha="center",
            fontsize=10)


plot_graph(ax[0], runs_u, sol_u, fit_u, "Umerná pokuta")
plot_graph(ax[1], runs_m, sol_m, fit_m, "Mŕtva pokuta")
plot_graph(ax[2], runs_s, sol_s, fit_s, "Stupňovitá pokuta")

plt.tight_layout()
plt.show()