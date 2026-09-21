# Ternary image laboratory

Launch **Ternary image laboratory** from the main menu, or run
`name.ncg777.maths.neural.apps.TernaryImageMemoryApp`.
No additional dependencies, downloads, GPU, or external datasets are required.

The **Contours & regions** tab adds overlapping learned 8×8 windows, contour
restoration, hole-aware inside/outside labeling, and held-out comparisons on larger
synthetic images. See [the contour segmentation guide](ternary-contour-segmentation.md).

The **Ternary embeddings** tab compares 8-, 16-, 32-, and 64-trit autoencoders,
with editable codes and held-out reconstruction/denoising baselines. See
[the embedding guide](ternary-embeddings.md).

## Image memory

Each 8×8 image is a row-major vector of 64 trits:

| Pixel | Trit | Appearance |
| --- | --- | --- |
| Black | −1 | Black |
| Transparent | 0 | Checkerboard |
| White | +1 | White |
| Unknown | Separate mask | Amber question mark |

Transparency is observed data. It is **not** a missing pixel. Hiding a pixel clears
its known-value mask; it does not tell the network that the pixel is transparent.

1. Start with the eight contour examples: square, X, cross, and corner, in both colours.
2. Click **Train library** (100 epochs by default).
3. Select a stored image and click **Load selected**, or paint a cue with the four brushes.
4. Click **Hide** to conceal up to 16 known pixels, then **Recall**.
5. Compare the spin completion with the nearest stored image. Load either result
   into the editor to inspect, modify, store, or export it.

The spin model uses the existing sampled 64-trit trainer and conditional recall.
At 0% visible error, it must keep observed pixels unchanged. Positive error allows
correction of visible pixels. Nearest matching minimizes disagreements on observed
pixels; it can return a stored image that disagrees with them even at 0% error.

One top candidate per method is displayed. Spin percentages are frequencies among
256 samples from four chains, not a guarantee that the best completion was found.
Nearest percentages reflect example frequency among equally close memories, not
confidence in correctness. Ties use ternary ordering. The spin model can propose
images that were not stored; nearest matching cannot.

**Test 20 damaged memories** selects stored examples, hides the chosen number of
pixels, optionally corrupts visible pixels, and measures whole-image recovery.
Its seed is fixed at 777 for repeatability. This is recall of training examples,
not a test of generalization. With the default library, 100 epochs, 16 hidden
pixels and no visible corruption, both methods recovered 20/20 in validation.
There is no demonstrated learned-model advantage in that experiment.

You can store up to 128 examples in this playground; duplicates increase their
training frequency. Editing the library invalidates its fitted model. Changes to
the cue or observation-error setting clear the displayed completions. Training,
recall and benchmarks run off the Swing event thread and support cancellation.

PNG import/export is exact: only 8×8 images containing fully transparent pixels,
opaque black, and opaque white are accepted. Partial alpha and other colours are
rejected instead of silently quantized. Unknown pixels cannot be exported. A fully
transparent pixel's hidden RGB is discarded. Libraries and trained models are
session-local; export individual images to keep them.

## Local boundaries

This separate supervised experiment detects **opaque pixels whose four-neighbour
values differ**. It detects both black/white transitions and silhouette boundaries;
pixels outside the image are treated as transparent. Transparent pixels themselves
are never marked as edges. The rule is explicit in `TernaryContours.boundaries`.

Generate an 8–128 pixel square image, or use the complete 8×8 editor image. The
display compares the explicit rule with a small learned convolutional network.
Unknown pixels must be filled before using the editor image here.

Click **Train / evaluate small CNN** to train from scratch on 96 generated 8×8
images (seeds 777–872), for 30 epochs. Shapes include rectangles, ellipses, and
horizontal/vertical strokes, in both colours with possible overlaps. Evaluation
uses 24 separate 16×16 images (seeds 10000–10023); they cannot duplicate the smaller
training images. Initialization and shuffling use seed 777. There is no tuning on
the evaluation set in the app.

Architecture:

- Two input channels: brightness (−1/0/+1) and opacity (0/1).
- Eight shared 3×3 filters with ReLU activation, followed by a shared 1×1 sigmoid
  output; 161 real-valued trainable parameters.
- Binary cross-entropy updates using per-pixel stochastic gradient descent.
- Output threshold 0.5, with micro-aggregated precision, recall and F1 across pixels.

This is a fully convolutional model, implemented directly in Java. It is separate
from the pairwise spin model and uses real-valued weights, despite ternary inputs.
Sliding overlapping 3×3 neighbourhoods each produce one centre prediction, so
there are no conflicting patch-output seams to reconcile. The same learned filters
apply at every location and to larger images without retraining. The Java raster
API supports rectangular images with each dimension between 1 and 128.

The validated default run produced 1,073 true positives, zero false positives and
five false negatives: precision 100%, recall 99.54%, F1 99.77%. The explicit-rule
baseline has F1 100% **by construction**, since it supplies the labels. This proves
only that the small network can approximate this simple local synthetic task; it
does not justify replacing the rule or claim performance on photographs. It does
not reconstruct missing contours. That remains the separate memory experiment.

The trainer rejects more than 256 images, more than 100 epochs, or more than two
million pixel updates. Inference supports at most 128×128 pixels. Cancellation is
checked during both training and inference. These bounds keep this a small CPU
experiment rather than an unrestricted image-training framework.

## Validation

Tests cover pixel ordering, PNG alpha round trips, colour/mask rejection, observed
pixel clamping, nearest contour recall, boundary semantics, deterministic training,
held-out CNN quality, translation consistency, larger-image inference, resource
limits, cancellation, and mouse-brush/store/hide interactions on the Swing event
thread. Both tabs were also rendered offscreen and visually inspected.
