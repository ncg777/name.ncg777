# Rhythm contour embeddings

Open **Ternary image laboratory → Rhythm contours**, then **Train rhythm
contours**. This is the four-bar view: four full-width rhythm contours stacked
vertically, with sixteen sampled heights per bar. The complete 4×16 array is
encoded jointly into one 8-, 16-, 32-, or 64-trit vector.

Each source bar is a four-digit hexadecimal pattern, hence a 16-step binary
rhythm. This view is not a 4×16 grid of separate rhythms or miniature images.
It shows continuous reference, input and decoded curves, plus a nearest-training
baseline. Orange curves in **Learned distortion** are overlaid on the gray
reference. The intentionally imperfect reconstruction is left as produced by the
decoder; it is not snapped to a valid rhythm.

## Existing project definitions

The source predicate is `ShadowContourIsomorphic`, unchanged. The contour and
shadow contour are obtained from `BinaryNatural.getContour()` and
`getShadowContour()`. The plotted contour sequence uses exactly the expression
already used by `numbers.fixed.apps.Contours`:

```java
rhythm.getContour().circularHoldNonZero()
    .cyclicalAntidifference(0).asOrdinalsUnipolar().addToEach(-1)
```

This is the app's held/integrated ordinal contour sequence, not a direct plot of
the raw −1/0/+1 contour signs. In particular, zero handling follows the existing
`circularHoldNonZero` implementation. The sequence is linearly resampled to
sixteen positions across the bar and scaled vertically to −1..1. Constant
sequences become zero-height curves. Horizontal positions represent normalized
contour-sequence position; they do not recover the original onset timestamps.

The four bars are chosen using the **Matrix Generator's difference rule**:
every source bar must be SCI, and `PredicatedDifferences(SCI)` must hold against
every earlier bar in the stack. This checks both directed set differences.
Repetition is permitted, as it is in Matrix Generator. With one bar per row,
there is no horizontal juxtaposition between separate bars to constrain.
Candidate selection is uniform among compatible bars, corresponding to the
first-column selection principle. This does not modify the original Matrix
Generator or reproduce its unrestricted search loop.

## Dataset and bounds

The catalogue examines all 65,535 nonempty four-digit hexadecimal patterns. The
empty pattern is excluded because it has no contour, although the predicate
accepts it. The current definitions yield **1,211 accepted rhythms** and **90
distinct 8×8 contour drawings**. Identical drawings are grouped and represented
by the first hexadecimal pattern encountered. Groups are shuffled with seed 777
and split into 67 training and 23 test representatives. This emphasizes distinct
drawings rather than weighting them by the number of rhythms that share them.

The curve experiment builds 192 compatible training stacks from the training
rhythm pool and 64 test stacks from the separate test pool, using seeds 777/778.
No representative rhythm crosses pools, and duplicate complete sampled stacks
are rejected across both sets. Both pools still come from the same musical
predicate and may contain related/rotated contour families; this is not a test on
unseen musical styles. Stack construction has a 20,000-attempt cap per split.

## Learning and editing

The continuous-height autoencoder has a dense tanh encoder, hard ternary
quantization at ±1/3, and a dense tanh decoder. The decoder receives only the
discrete code, with no original-height or continuous-latent bypass. A tanh
straight-through surrogate supplies encoder gradients, as in the image demo.
Training minimizes squared reconstruction error. Inputs alternate clean curves
and Gaussian-corrupted curves; targets are always clean. Training uses 80 epochs
and seed 777 at each code width.

Input noise is Gaussian with standard deviation equal to the selected percentage
of the full vertical range (2), clipped to −1..1. The display starts with 0% noise
so the model's own distortion is visible. Fixed noisy tests use 5% and seed
`10000 + exampleIndex`. Unlike the original binary image demo, noise here is not
a bit flip.

Select the code size and test stack, edit the `T/0/1` code, and click **Decode
edited code** to explore other curves. **Re-encode** restores the encoding of the
displayed input. These decoded curves need not correspond to hexadecimal rhythms,
be SCI, or satisfy the stack's difference relations. Only the source examples
have those guarantees. A trit still has no assigned musical meaning.

Training is capped at 1,024 examples, 200 epochs, and work estimate
`examples × epochs × codeWidth × 64 <= 100 million`. Allowed widths are 8, 16, 32,
and 64. Training, enumeration and generation check cancellation and run in a
background Swing worker. Closing the laboratory cancels active jobs. Models are
session-local.

## Results

The benchmark compares decoded curves with copying the observed input, the mean
training stack, and nearest training stack by squared height distance. RMSE is
reported as a percentage of the full vertical range, aggregated over all 64 test
stacks and all heights. Display controls and code edits do not change the fixed
benchmark.

| Method | Clean RMSE | 5% noise RMSE |
| --- | ---: | ---: |
| Copy input | 0.00% | 4.74% |
| Mean training contours | 29.74% | 29.74% |
| Nearest training stack | 22.52% | 22.57% |
| 8 trits | 19.39% | 19.44% |
| 16 trits | 15.14% | 15.55% |
| 32 trits | 12.33% | 12.56% |
| 64 trits | 10.21% | 10.52% |

The learned models improve on the mean and nearest-stack baselines in this run,
but do not improve on keeping the slightly noisy input. These results support
exploring a learned distortion, not claiming lossless recovery or denoising.

## Rhythm examples in the original image demo

The existing **Ternary embeddings** tab also has an **Examples** selector with
**SCI hexadecimal rhythm contours**. It plots each contour sequence as an 8×8
binary drawing using normalized event index/ordinal height and integer line
rasterization. No wraparound closing segment is appended. Quantization is lossy:
different rhythms and sequences can produce the same image. The source hex,
contour, shadow contour, contour sequence, matching-drawing count, and nearest
source hex labels remain visible. The source uses the 67/23 disjoint drawing
split rather than the synthetic-image default 192/64 split. Switching sources
clears fitted models and scores; train again for the new source.

The **Rhythm contours** tab is the recommended view for four continuous curves
on top of each other. The original image tab remains available for comparing
the rasterized version with the earlier pixel experiments.

Tests cover predicate reuse, contour-sequence agreement, raster orientation,
catalogue grouping, source changes, all-pairs difference compatibility, disjoint
rhythm pools, 4×16 sample ordering, hard code decoding, resource bounds,
cancellation, and Swing training/code-edit/reset workflows. The four-bar view is
also rendered offscreen and visually inspected.
