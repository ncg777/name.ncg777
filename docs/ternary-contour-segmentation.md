# Sliding 8×8 patches, contours, and regions

Open **Ternary image laboratory → Contours & regions**. This experiment adds the
local-to-global step: restore an outline with overlapping learned patches, then
classify the regions it encloses. It does not use the 64-node associative memory
as a larger image model and does not require new dependencies.

## Try it

1. Start with **Shape with hole**, size 64, no gaps or noise. The reference image
   and the closing/enclosure baseline appear immediately.
2. Click **Train 8×8 model**. Compare the restored contour and region map with
   the reference. Hover over a map to see an 8×8 input window (cyan) and its central
   4×4 output (magenta), synchronized across the six views.
3. Set a gap length of 2 or 3 and add noise, then click **New scene**. Missing
   strokes and isolated noise are actual observed empty/occupied pixels, not
   unknown-value masks.
4. Change the contour-score threshold or the closing radius and click
   **Reanalyse**. This keeps the observed image unchanged, allowing a fair visual
   comparison. Shape, size, gap and noise controls require **New scene**.
5. Try **Island inside hole** and **Cut by image border**. The latter deliberately
   tests an assumption the enclosure method cannot safely make.
6. Click **Evaluate 18 scenes** to compare three methods on the same held-out
   images. Drag the divider between maps and metrics to change their sizes.

The output trits in this tab have a different meaning from the image-memory tab:

| Trit | Meaning | Display |
| --- | --- | --- |
| −1 | Outside | Dark |
| 0 | Boundary | Orange |
| +1 | Inside | Blue |

Zero here means boundary, not transparency or uncertainty. In the input-outline
display, white is an observed stroke and black is empty space. The Java predictor
treats either nonzero input trit as a stroke, ignoring black versus white colour.
This experiment processes synthetic outlines, not photographs. The earlier
**Local boundaries** tab remains available for detecting boundaries in filled
black/white/transparent shapes.

## Local contour restoration

`TernaryPatchNetwork` learns sixteen 8×8 filters with ReLU activations. Sixteen
sigmoid outputs predict the central 4×4 contour patch. The same weights are reused
across the whole image. There are 1,312 trainable real-valued parameters plus a
fixed identity skip: each output receives four times its corresponding observed
input pixel. The learned part supplies corrections to that initial observation.
This is a small convolutional patch restorer, not an end-to-end region network.

At inference, the window moves by two pixels. Input starts at (−4, −4), with empty
padding, and extends past the bottom/right as needed. Every real pixel, including
border pixels and odd-sized images, receives exactly four predictions. Their
arithmetic mean is thresholded. Their population standard deviation is retained
as a measure of disagreement between overlapping patches. Hover tooltips expose
the mean, disagreement and vote count. **Show contour scores** displays the means
before thresholding and closing; the binary contour view is after both operations.

These are not calibrated confidence probabilities. Training weights positive
contour labels three times more strongly to counter sparse edges. A low overlap
disagreement does not establish that a prediction is correct. Stride-two sampling
also introduces a phase dependence: translation consistency is tested for shifts
of two pixels, not claimed for arbitrary one-pixel shifts.

Training uses 48 synthetic scenes, 32×32 or 40×40, from seeds 1000–1047. Each scene
contributes 96 randomly located windows. Shapes include rectangles, ellipses, two
separate shapes, holes, nested islands, and border crops. Corruption includes
connected deleted runs of lengths 0–3 and bit-flip noise rates 0%, 0.5%, and 2%.
Targets come from the geometry **before corruption**. Initialization/window
selection/shuffling use seed 777. The default fit runs 30 epochs with weighted
binary cross-entropy and stochastic gradient descent, 138,240 window updates.

The patch trainer caps input at 128 scenes, 50 epochs, and 150,000 window updates;
all three restrictions apply. Each raster dimension is capped at 128. Training
and inference check interruption. The Swing panel performs training, analysis and
evaluation in cancellable background workers. Fitted models are session-local.

## From contours to inside/outside

Both the learned method and the simple baseline can apply square morphological
closing with radius 0, 1, or 2. Closing can bridge small gaps, but can also thicken
contours, join shapes, or erase narrow holes. It is an explicit heuristic, not a
learned operation. The raw baseline omits closing entirely.

Enclosure then proceeds without access to reference labels:

1. Identify four-connected components of non-boundary pixels and eight-connected
   contour bands. This connectivity convention avoids diagonal leaks at digital
   corners.
2. Build their adjacency graph. Non-boundary components touching the image frame
   are seeded as exterior.
3. Find the minimum number of contour-band crossings from the exterior using
   0–1 breadth-first search. Entering a contour band costs one; leaving costs zero.
4. Odd crossing depth means inside; even means outside. Boundary pixels remain 0.

Unlike simply filling every enclosed area, alternating parity preserves a hole
inside an outer contour, and an island nested inside that hole. This is a
convention for nested closed contours, not object recognition. Intersecting,
touching, or branched contours can violate it. Bands adjacent to other than two
free-space components, and bands touching the frame, are flagged. The flags are
diagnostics, not a proof that every unflagged topology is correct.

A gap can connect the interior to exterior and destroy the enclosure. A cropped
shape can genuinely continue outside the frame: the **Cut by image border** case
does not invent a closing contour along the crop. Its known interior is therefore
not recoverable with the exterior-seeding assumption. This case stays in the
evaluation and is reported rather than hidden.

## Measurements and observed limitations

Evaluation uses six 64×64 scenes (seeds 20000–20005), each with clean, three-pixel
gap, and gap-plus-2%-noise versions. These are different seeds and dimensions from
training. The scenarios still share the synthetic generator; this is not evidence
of generalization to natural images. Displayed scenes start at seed 404.

The table compares raw observed contours, closing alone, and learned restoration
plus closing. All methods share the same enclosure step. Precision, recall and F1
require an **exact pixel match** to the reference contour; there is no tolerance
radius. Interior IoU counts only +1 pixels in the reference and prediction, and
is intersection divided by union. Aggregated scores sum pixel counts across the
six shapes before taking ratios. `Flagged` counts images with one or more ambiguous
contour bands. The displayed-scene table uses the same measurements.

The default fixed run (threshold 0.5, closing radius 1) produced:

| Condition | Method | Boundary F1 | Interior IoU |
| --- | --- | ---: | ---: |
| Clean | Raw | 100.00% | 76.80% |
| Clean | Closing | 99.84% | 76.76% |
| Clean | Learned + closing | 99.46% | 76.70% |
| Three-pixel gaps | Raw | 98.58% | 12.81% |
| Three-pixel gaps | Closing | 98.43% | 12.81% |
| Three-pixel gaps | Learned + closing | 98.04% | 12.78% |
| Gaps + 2% noise | Raw | 83.16% | 0.47% |
| Gaps + 2% noise | Closing | 78.51% | 23.77% |
| Gaps + 2% noise | Learned + closing | 90.89% | 24.27% |

Clean interior IoU is below 100% because the aggregate includes the border crop.
With no closing, all five fully closed clean shapes have exact region recovery,
including holes and nested islands. Radius-one closing cannot reliably repair
three-pixel gaps. Learning improves contour F1 in the noisy condition but does
not solve enclosure: region IoU remains low. The test demonstrates both useful
local denoising and the insufficiency of local contour accuracy for global shape
consistency. It makes no claim of general superiority over explicit rules.

## API and validation

`TernarySegmentationExperiment.restore(network, observed, threshold, radius)` takes
only observed pixels; ground-truth masks are used separately for scoring.
`TernarySegmentation.regions` can also classify a supplied binary contour map.

Tests verify exact closed-shape/hole/island recovery, border-crop failure flags,
gap leakage and closing, label independence from corruption, deterministic
training, four-vote coverage including odd dimensions and borders, two-pixel
translation consistency, score ranges, cancellation and resource limits. Swing
tests exercise training, evaluation, settings invalidation and scene changes.
The tab is rendered offscreen and visually inspected as well.
