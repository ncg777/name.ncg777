# Ternary associative memory

Launch **Ternary associative memory** from the main menu, or run
`name.ncg777.maths.neural.apps.TernaryMemoryApp`.

1. Enter complete patterns, one per line, using T (−1), 0 and 1. Spaces are optional.
2. Choose **Train memories**.
3. Enter a partial cue, such as `1?T0`, and choose **Recall cue**.
4. Compare the spin model's completions with the nearest stored patterns.

The question mark means unknown. Zero is always an observed neutral value.
Editing stored patterns invalidates the fitted memory and requires retraining.
Repeated examples count repeatedly in training and in nearest-match shares.

The spin model assigns a probability to every possible pattern. Recall applies
evidence from the cue and normalizes these probabilities exactly. At a visible
error rate of zero, observed positions are fixed. At rate p > 0, each observed
position has likelihood 1−p when correct and p/2 for each alternative trit.
Unknown positions contribute no evidence. This simple independent corruption
assumption allows the model to correct visible values as well as fill blanks.
The spin model can return combinations that were never stored.

The nearest-pattern baseline measures Hamming distance on observed positions only.
It returns stored patterns at the minimum distance. Among these, repeated example
counts determine the displayed shares and ranking. Those shares are not calibrated
probabilities. If no stored pattern agrees with every observation, nearest recall
can change observed values even at zero error rate; the Changed column makes this
visible. The tables show the best ten candidates, so displayed weights need not
sum to 100%. Exactly equal scores retain enumeration order T,0,1.

## Recall experiment

**Test 200 damaged cues** samples the stored rows with fixed seed 777. For each trial
it hides the chosen number of distinct positions. Each remaining position is then
independently corrupted with the selected error probability, changing uniformly
to one of the other two trits. Both methods receive the same cue; the spin method
uses that same corruption rate as its observation model.

The score is the fraction of trials whose highest-ranked whole pattern equals the
original. The app also reports how often the top two weights are within 1e-10,
indicating ambiguity. It scores one choice, not all tied candidates. Recall from an
ambiguous cue can fail even when the correct memory is among the candidates.

This tests retrieval of **training memories under fresh damage**, not generalization
to unseen patterns. The simple two-pattern test `11`/`TT`, with one position hidden,
is recovered by both methods. It does not demonstrate an advantage for learning.
The point is to inspect when the learned pairwise model helps, matches, or loses
information relative to direct storage.

## Bounds and API

This app intentionally uses 1..6 positions, 1..1,000 complete examples and 1..500
training epochs. Six is a workload limit for exact enumeration, not an intrinsic
limit of ternary memory. It reuses TernaryBoltzmannTrainer, TernarySpinModel and
BoltzmannDistribution, with no new dependencies. Training uses exact mode, beta 1,
learning rate 0.1, and the trainer's existing regularization and work budget.
Training and recall tests run off the Swing event thread; closing the window
cancels the current worker.

```java
var memory = TernaryAssociativeMemory.train(
    TernaryAssociativeMemory.parsePatterns("11\nTT"), 200);
var result = memory.recall(TernaryAssociativeMemory.parseCue("1?"), 0);
var scores = memory.benchmark(200, 1, 0.15, 777);
```

`fromModel` permits recall experiments with an already fitted model. `Cue` stores
values and a separate boolean known-value mask, and defensively copies both.
The API benchmark supports up to 500 trials. The experiment does not change the
spin model or introduce a richer categorical or hidden-feature model.
