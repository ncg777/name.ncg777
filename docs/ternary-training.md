# Training a small ternary Boltzmann network

The `name.ncg777.maths.neural` package fits the existing `TernarySpinModel` to
complete example patterns. It is a minimal **fully visible Boltzmann machine**:
each observed position is a stochastic ternary neuron, and training learns all
symmetric connections, orientation biases and neutrality penalties. It has no
hidden layers, backpropagation engine, missing-data training or arbitrary graph
topology. Every pair of nodes can acquire a connection, including pairs whose
initial coupling is zero. Zero in the training data means neutral, not missing.

No new dependencies are required. It uses Java 17, the project's Commons Math
generator, Jackson for inference checkpoints, picocli for the demo, and JUnit 4.

## Train on your own examples

```java
import name.ncg777.maths.neural.TernaryBoltzmannTrainer;
import name.ncg777.maths.neural.TernaryTrainingOptions;
import name.ncg777.maths.neural.TrainedTernaryNetwork;
import name.ncg777.maths.physics.TernarySpinModel;

int[][] examples = {
    { 1,  1, -1},
    { 1,  1, -1},
    {-1, -1,  1},
    { 0,  0,  0}
};
var initial = new TernarySpinModel(new double[3][3], new double[3], new double[3]);
var options = TernaryTrainingOptions.builder()
    .epochs(300).learningRate(0.05).beta(1)
    .l2(0.001).maxAbsParameter(5).build();
var result = TernaryBoltzmannTrainer.train(initial, examples, options);
var trained = result.model();
```

Repeated rows have their repeated empirical weight. Training is full-batch: it
computes the data's orientation, participation and pairwise agreement once, then
compares these with the current model's statistics at each update. All parameters
start at the supplied model's values; zero initialization is valid for this
fully visible architecture. Inputs and model parameters are defensively copied.

With average log-likelihood L, an undirected edge counted once, and L2 coefficient
lambda, the ascent updates before projection are:

```
h_i     += rate * ( beta * (data[s_i] - model[s_i])       - lambda * h_i)
delta_i += rate * ( beta * (model[s_i^2] - data[s_i^2])   - lambda * delta_i)
J_ij    += rate * ( beta * (data[s_i s_j] - model[s_i s_j]) - lambda * J_ij)
```

The penalty sign is reversed because positive delta discourages participation.
Both copies of J_ij are updated together and the diagonal stays zero. Projection
keeps each parameter in `[-maxAbsParameter, +maxAbsParameter]`. L2 applies to all
unique parameters, optimizing `L - lambda/2 * sum(parameter^2)` before projection.
The reported scores omit regularization. A large rate can make training worse;
finite parameters do not guarantee good mixing or convergence. Nonfinite
arithmetic fails explicitly. Beta is fixed and must be positive during training.

## Choose exact or sampled training explicitly

The default `Mode.EXACT` enumerates the model's states at each epoch. Its model
moments and **mean joint log-likelihood** scores are exact up to floating-point
roundoff. Use it for small networks, usually two to six nodes to start.

For larger networks:

```java
var options = TernaryTrainingOptions.builder()
    .mode(TernaryTrainingOptions.Mode.PERSISTENT_GIBBS)
    .epochs(150).particles(128).sweeps(2).burnIn(50).seed(777)
    .build();
```

This mode initializes particles from training rows, runs initial burn-in, then
keeps their states across epochs. Each update advances every particle by the
specified Gibbs sweeps and uses their empirical moments for the model phase.
It never computes a partition function. Its score is **mean per-site log
pseudolikelihood**, `average_(row,i) log P(row_i | other positions)`, evaluated
directly using the local conditionals. This is a monitoring metric, not the
objective whose gradient is being estimated, and not an exact joint likelihood.
Do not compare its numerical value with exact-mode scores.

The sampled gradient is biased when chains have not mixed. Larger particle
counts or more sweeps can help, at a higher cost. A fixed seed reproduces a run;
it does not establish convergence. Sampling and training here are unconstrained.
For small fitted models, the existing constrained `exactDistribution` can be
used afterward to generate only states satisfying a `Trit` predicate. That
conditioning changes the distribution; training itself does not optimize a
constraint-restricted likelihood.

## Resource limits

Checks occur before allocating particle arrays or enumerating states:

| Limit | Default / maximum |
|---|---|
| Nodes | 1 to 64; dense O(n^2) parameters |
| Examples | 1 to 100,000 in each supplied dataset |
| Epochs | 200 / 10,000 |
| Exact candidate states | 6,561 (8 nodes) / 19,683 (9 nodes) |
| Gibbs particles | 128 / 4,096 |
| Gibbs sweeps per epoch | 2 / 10,000 |
| Initial burn-in sweeps | 50 / 10,000 |
| Estimated work budget | 100,000,000 interaction-work units |
| Absolute parameter bound | 5, configurable |

Training has a stricter exact-state limit than one-off inference because it
repeats the calculation. The work estimate additionally includes all requested
epochs, data/validation scoring, burn-in and sampling, scaled by n^2. It is a
conservative accounting formula, not a time or RAM guarantee. It can reject a
run below the individual node/state limits; early stopping is not assumed in the
estimate. Reduce the relevant counts or explicitly increase `maxWork` after
assessing the cost. There is no silent fallback from exact to sampled learning.
Thread interruption cancels training, with checks between rows, epochs and Gibbs
sweeps. An exact enumeration already in progress finishes before cancellation
is observed; its size is bounded by the exact-state limit.

Exact training costs O(epochs * n^2 * (3^n + rows)) time and O(n 3^n + rows*n)
space. Sampled training costs O(n^2 * (particles*burnIn + epochs*(particles*sweeps
+ rows))) time and O(n^2 + particles*n + rows*n) space, plus O(epochs) history.

## Validation and early stopping

```java
var result = TernaryBoltzmannTrainer.train(initial, trainingRows, validationRows,
    TernaryTrainingOptions.builder().patience(20).minImprovement(1e-6).build());
```

Split data before calling the trainer; it does not create a split for you.
Validation rows never affect gradients or initialize particles. Scores are
recorded at epoch zero and after each update, and higher is better. With
validation, training stops after `patience` epochs without an improvement larger
than `minImprovement` over the patience reference. The returned model is always
the checkpoint with the highest validation score, including the initial model
if all updates hurt. `selectedEpoch` identifies its entry in `history`; later
history entries can describe discarded models. Small improvements still update
the best checkpoint even if they do not reset patience. Without validation,
training runs the configured epochs and returns the final model.

## Predict, generate, save and reload

```java
// The value at site 1 is ignored; the other two sites are observed.
double[] prediction = trained.conditionalDistribution(
    new int[] {1, 0, -1}, 1, result.beta()).probabilities(); // order -1, 0, +1
int[] generated = trained.gibbsSample(new int[3], result.beta(), 100,
    new org.apache.commons.math3.random.MersenneTwister(777));

var file = java.nio.file.Path.of("network.json");
new TrainedTernaryNetwork(trained, result.beta()).save(file);
var loaded = TrainedTernaryNetwork.load(file);
```

The versioned JSON includes all parameters and beta, so inference needs no
retraining. Loading validates dimensions, symmetry, finiteness, version and a
1 MiB file-size limit. Saving replaces the chosen file. A checkpoint contains
inference parameters, not the optimizer's particle state or history. You can
start another training run from its model and beta, but it will not be a bitwise
continuation of a persistent-chain run. Single-site conditional prediction is
exact; finite Gibbs generation remains approximate.

## Runnable demonstration

```sh
mvn clean package assembly:single
java -cp target/name.ncg777-20260502T1743Z-jar-with-dependencies.jar name.ncg777.maths.neural.apps.TernaryTrainingDemo --output network.json
java -cp target/name.ncg777-20260502T1743Z-jar-with-dependencies.jar name.ncg777.maths.neural.apps.TernaryTrainingDemo --mode PERSISTENT_GIBBS --seed 777
```

The demo generates separate training and validation observations from a small
known signed model, fits a model starting at zero parameters, prints before/after
scores and learned parameters, predicts a node from the other nodes and generates
a new pattern. It uses synthetic data only; use the Java API above for your data.
The tests check finite-difference gradients for every parameter type, analytic
one-node fitting, learned agreement/opposition, seeded sampled fitting, validation
isolation, early stopping, work limits, cancellation and checkpoint round trips.
