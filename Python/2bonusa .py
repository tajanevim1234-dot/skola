import genetic_toolbox as ga
import matplotlib.pyplot as plt
import numpy as np


def testfn3c(Pop):
    x0 = 30
    y0 = 100
    return np.sum(-(Pop - x0) * np.sin(np.sqrt(np.abs(Pop - x0))) + y0, axis=1)


# MATLAB: S = [-1000ones(1,100); 1000ones(1,100)];
N_GENES = 100
space = ga.uniform_space(amount_of_genes=N_GENES, lower_limit=-1000, upper_limit=1000)

# MATLAB: Population = 500; behy=5; generacie = 2000;
POP_SIZE = 500
N_RUNS = 5
NUM_GEN = 2000

# MATLAB: kriz=crossov(rodic,4,0); mutacie=mutx(kriz,0.2,S);
PTS = 4
MODE = 0
MUT_RATE = 0.2

# MATLAB: naj_jed=selsort(pop,pop_fit,10);
ELITE_N = 10

plt.figure()

for k in range(N_RUNS):
    pop = ga.genrpop(pop_size=POP_SIZE, space=space)
    naj_fit = []

    for gen in range(NUM_GEN):
        fitness = testfn3c(pop)
        naj_fit.append(fitness.min())

        # elite = best 10
        elite, _ = ga.selbest(pop, fitness, n_list=[ELITE_N])

        # parents = SUS selection of size POP_SIZE (ako v MATLABe)
        parents, _ = ga.selsus(pop, fitness, n=POP_SIZE)

        # crossover + mutation (in-place)
        ga.crossov(parents, PTS, MODE)
        ga.mutx(parents, rate=MUT_RATE, space=space)

        # MATLAB: new_pop = [naj_jed; mutacie(1:Population-10, :)];
        pop = np.vstack([elite, parents[:POP_SIZE - ELITE_N]])

    plt.plot(np.arange(1, NUM_GEN + 1), naj_fit, 'r')
    plt.grid(True)
    plt.hold = True  # ak to máš v niektorých prostrediach, inak ignoruj

    fitness = testfn3c(pop)
    best_val = fitness.min()
    best_idx = np.argmin(fitness)

    print(f"Beh {k + 1}: fitness = {best_val:.4f}")
    # print(pop[best_idx])  # ak chceš vypísať aj vektor riešenia

plt.xlabel("Generácia")
plt.ylabel("Best fitness")
plt.grid(True)
plt.show()