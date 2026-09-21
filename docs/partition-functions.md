# Partition functions, ternary networks and subset lattices

These utilities connect the project's ternary logic and enumerators with finite
statistical mechanics. They reuse `Trit`, `WordEnumeration`, Commons Math's seeded
random generators, picocli and JUnit 4. No additional dependencies are needed;
the source remains compatible with Java 17.

## Run the examples

Build using the existing assembly configuration:

```sh
mvn test package assembly:single
java -cp target/name.ncg777-20260502T1743Z-jar-with-dependencies.jar name.ncg777.maths.physics.apps.PartitionFunctionDemo --beta 1 --seed 777
```

The CLI prints exact statistics of a three-node network, a conditional thermal
activation, an approximate Gibbs sample, a generated pattern satisfying a ternary
proposition, active-subset probabilities, a feasible control trajectory, and a
coupling learned from a target correlation. `--beta 0` ignores finite energy
differences; increasing beta favors lower energies. `--help` lists the options.

## Finite distributions and path weighting

`name.ncg777.statistics.BoltzmannDistribution` takes an array of energies and a
finite, nonnegative inverse temperature beta. With default unit reference masses:

```
Z = sum_i exp(-beta E_i)
p_i = exp(-beta E_i) / Z
F = -log(Z) / beta
H = -sum_i p_i log(p_i)
```

Use `logPartitionFunction()` when Z is too large to represent. The implementation
shifts energies and normalizes log weights; probabilities stay usable even when
Z overflows. Very small probabilities may underflow to zero, while
`logProbability(i)` retains a log value where representable. Even log Z can exceed
double range for extreme inputs; `freeEnergy()` avoids the product beta * energy.
Free energy requires positive beta. Entropy is in nats.

The three-argument constructor accepts nonnegative reference masses:

```java
var paths = new BoltzmannDistribution(
    new double[] {2.0, 1.0, Double.POSITIVE_INFINITY},
    1.5, new double[] {0.6, 0.3, 0.1});
int chosenPath = paths.sample(new MersenneTwister(777));
```

Here Z is `sum_i prior_i exp(-beta cost_i)`. A normalized prior gives a path
expectation; arbitrary masses can represent multiplicity. Zero mass or positive
infinite energy excludes an outcome, including at beta=0. Empty support, NaN,
negative infinite energies, negative masses and nonfinite beta are rejected.
Adding a constant to all finite energies changes log Z but not probabilities.

The demo explicitly enumerates four controls in {-1,0,+1} for a one-dimensional
integrator. It rejects trajectories leaving [0,3], penalizes distance from target
2 and control effort, then samples a feasible trajectory. Its first action can
be applied before replanning from the next observed state. This is a finite
planning example; it does not establish an optimal controller for arbitrary
stochastic dynamics. Averaging feasible trajectories need not preserve feasibility,
so the example samples a complete feasible path.

## Ternary attraction, neutrality and repulsion

`name.ncg777.maths.physics.TernarySpinModel` uses:

```
s_i in {-1, 0, +1}
E(s) = -sum_(i<j) J_ij s_i s_j + sum_i delta_i s_i^2 - sum_i h_i s_i
```

Positive J favors agreement, negative J opposition, positive delta favors
neutrality, and h favors an orientation. Neutral nodes contribute no pairwise
interaction. J must be square, exactly symmetric and zero on the diagonal.
Finite parameters are copied; a computed nonfinite energy is rejected.
This is a spin-one equilibrium model, not a general asymmetric recurrent network.
The [minimal training framework](ternary-training.md) learns its parameters from
complete ternary examples using exact or sampled model statistics.

```java
var model = new TernarySpinModel(
    new double[][] {{0, -0.8}, {-0.8, 0}},
    new double[] {0.4, 0.4}, new double[] {0.2, 0});
var exact = model.exactDistribution(1.0);
double orientation = exact.meanOrientation(0); // E[s_0]
double participation = exact.participation(0); // E[s_0^2]
double agreement = exact.correlation(0, 1);    // E[s_0 s_1], not Pearson correlation
double response = exact.covariance(0, 1);      // Cov(s_0, s_1)
```

Zero orientation does not imply neutrality: opposite signs can cancel while
participation stays high. A single node has
`Z = 1 + 2 exp(-beta delta) cosh(beta h)`; this is tested analytically.

`conditionalDistribution(state, site, beta)` returns the three probabilities in
the order -1, 0, +1. Its expectation against `{-1,0,1}` is a smooth thermal
activation. Its local field is `h_i + sum_j J_ij s_j`. The corresponding local
free energy is the effective potential after summing out that hidden spin.

`gibbsSample(initial, beta, sweeps, random)` performs sequential single-site
updates and returns a new array. Supply a seeded Commons Math `RandomGenerator`
for reproducibility. Finite sweeps do not guarantee convergence, especially for
strong interactions. This sampler targets the **unconstrained** model and does
not compute Z. At extreme beta, floating-point underflow can prevent transitions.

Exact enumeration takes O(n^2 3^n) time and O(n 3^n) storage. The default limit of
1,000,000 candidate states permits at most 12 sites. The constrained overload
accepts a caller-selected limit, checked against all 3^n candidates before any
enumeration, even when few states pass the constraint. Raising it may be costly.
The empty model has one empty state, Z=1 and entropy zero.

## Logic and procedural generation

Use a predicate to restrict allowed valuations, preserving the chosen logic:

```java
var constrained = model.exactDistribution(
    1.0, s -> Trit.OR(s[0], s[1]) == 1, 9);
int[] pattern = constrained.sample(new MersenneTwister(777));
double eventProbability = exact.probability(s -> Trit.AND(s[0], s[1]) == 0);
```

This uses the project's existing `Trit.OR` and `Trit.AND` truth tables. The
probabilities describe uncertainty over valuations; zero remains a distinct
ternary value. An impossible constraint throws rather than silently producing an
invalid distribution. Callbacks and returned states receive copies, so they
cannot mutate stored configurations.

## Learning from observables

For fixed beta and fixed support, derivatives of log Z give:

```
d log Z / d h_i     = beta E[s_i]
d log Z / d delta_i = -beta E[s_i^2]
d log Z / d J_ij    = beta E[s_i s_j]             (one undirected edge i<j)
d E[s_i] / d h_j    = beta Cov(s_i,s_j)
d log Z / d beta   = -E[E(s)]
```

Consequently, the average data log-likelihood gradient for one coupling is
`beta * (dataAgreement - modelAgreement)`. The demo iteratively fits a two-node
coupling to target agreement 0.5 at beta=1. Analytic and finite-difference tests
check the identities. For fitting all parameters from data, see the
[training framework](ternary-training.md), including resource limits, sampled
expectations and held-out validation. The general energy-based
learning relation is also described in [Hinton's practical guide, section 2](https://www.cs.toronto.edu/~hinton/absps/guideTR.pdf).

## Active subsets and lattice transforms

A ternary configuration has an active subset `A = {i : s_i != 0}`. Sum exact
configuration probabilities into a `double[1 << n]`, with each subset encoded as
a bit mask. Then:

```java
double[] contained = BooleanLattice.zetaTransform(activeMass);
double[] recovered = BooleanLattice.mobiusTransform(contained);
```

`contained[S] = sum_(A subset of S) activeMass[A]` is the probability that all
active nodes lie in S. The Möbius inverse recovers the individual subset masses.
For unnormalized Boltzmann weights, the same grouping sums partition contributions
from active subnetworks; summing their masses gives Z. These transforms can also
aggregate features over subobjects and extract interactions by inclusion-exclusion.
They operate in O(n 2^n) time, return fresh arrays, require a nonzero power-of-two
length, and reject nonfinite values or arithmetic overflow. Entry zero is the
empty subset. This makes the Boolean-lattice connection computational rather
than only a similarity between graph drawings.
