# Ternary contour embeddings

Open **Ternary image laboratory → Ternary embeddings**, then click **Train all
four code sizes**. This experiment encodes an 8×8 binary contour into 8, 16, 32,
or 64 learned trits and reconstructs the contour using only that discrete code.
No new dependencies or external datasets are needed.

## Explore the representation

- Select a code width and one of 64 held-out examples. The five images show the
  clean target, possibly corrupted encoder input, decoded code, nearest training
  image by pixel distance, and nearest training image by code distance.
- Adjust **Input flips %** to change the displayed observation. Noise is seeded
  by the example index, so returning to the same controls reproduces it.
- Click a coloured code cell to cycle −1 → 0 → +1 → −1. The reconstruction and
  nearest-by-code example update from that edited code. **Re-encode input** resets
  edits. `T` is the compact textual notation for −1.
- Inspect the number of distinct training codes, frequency of zero trits, and
  exact training-code matches. Different images can share a code; a nearest code
  is not guaranteed to be visually similar. Equal Hamming distances choose the
  first training example.

Black pixels mean empty and white pixels mean contour. The code colours represent
learned features: blue −1, gray 0, orange +1. A zero trit is a middle activation,
not transparency or missing data. Features do not have assigned semantic names.

The benchmark table is separate from the displayed example. It always evaluates
all 64 test images with clean inputs and with 5% independent bit-flip corruption.
Display edits do not alter its results. Models and codes are held in memory for
the current session; this is not a serialized image-compression format.

## Model and learning

`TernaryAutoencoder` is a small dense denoising autoencoder:

1. Convert each binary input pixel to −1/+1 and apply a learned affine encoder.
2. Apply tanh, then quantize each activation: below −1/3 becomes −1; above +1/3
   becomes +1; all other values become 0.
3. Feed **only those integer trits** through a learned affine sigmoid decoder to
   produce 64 pixel scores. Threshold at 0.5 for the binary reconstruction.

There is no source-image skip connection or continuous latent channel to the
decoder. Calling `decode(code)` requires no source image. Editing the code changes
its reconstruction directly. Each model has `129 × codeWidth + 64` real-valued
parameters, including biases.

The hard quantizer has no useful ordinary gradient. Training uses its discrete
values in every forward pass and a **straight-through tanh surrogate** for encoder
gradients. This is an optimization heuristic, not differentiation of rounding.
The decoder uses binary cross entropy. Decoder biases start from smoothed
per-pixel training frequencies. Learning alternates clean inputs and 5% corrupted
inputs while always targeting the clean contour. Each fit runs 80 epochs, with
initialization, shuffle and corruption seed 777 and a decaying learning rate.

This model learns reconstruction features, not a contrastive similarity metric.
No claim of translation invariance or semantic organization is made. The
nearest-code display is a way to inspect the geometry it actually learned.
Associative-memory repair of codes has not been added: first the quality and
stability of the code itself must be established.

## Dataset, baselines and measurements

The existing synthetic shape generator produces 8×8 black/white/transparent
rasters, and its explicit boundary rule extracts binary contours. With seed 777,
the experiment collects 256 distinct, nonempty contour patterns, rejecting exact
duplicates. The first 192 train the models; the remaining 64 are held out. This
prevents exact image overlap, but both sets still come from the same small shape
generator. It is not a test on natural images or unseen shape families.

All models receive identical test observations: corruption seed `10000 + index`
for each test example. Baselines are:

- **Copy input:** retain the observed pixels without reconstruction.
- **Mean training image:** threshold per-pixel training frequency at 0.5.
- **Nearest training image:** minimize observed-pixel Hamming distance over the
  training set; no test images are in the lookup library.

The learned models, mean image, and nearest-neighbour library use training data
only. The fixed evaluation does not select a model or tune its parameters.

Pixel error counts differing pixels divided by all 4,096 test pixels. Contour F1
aggregates true positives, false positives, and false negatives for white pixels.
Exact-image accuracy requires all 64 pixels to match. Changed-code-trit percentage
is the mean normalized Hamming distance between clean and noisy encodings; it is
not a measure of semantic similarity. The all-empty mean baseline in this split
illustrates why pixel error alone can obscure poor contour recovery.

Default results:

| Method | Clean pixel error | Clean contour F1 | Noisy pixel error | Noisy contour F1 |
| --- | ---: | ---: | ---: | ---: |
| Copy input | 0.00% | 100.00% | 4.79% | 89.21% |
| Mean training image | 20.90% | 0.00% | 20.90% | 0.00% |
| Nearest training image | 13.55% | 62.22% | 14.40% | 61.03% |
| 8 trits | 16.94% | 46.62% | 17.55% | 45.32% |
| 16 trits | 12.08% | 64.67% | 13.94% | 60.04% |
| 32 trits | 8.35% | 77.47% | 9.52% | 74.71% |
| 64 trits | 5.42% | 85.71% | 7.45% | 80.63% |

Wider codes improve reconstruction in this run, but **none beats simply retaining
the noisy input at 5% corruption**. Clean exact-image accuracy for 64 trits is
10.94%, falling to 4.69% under noise. This is a working learned representation
experiment, not a successful general denoiser or a lossless codec.

An N-trit vector has at most `N × log2(3)` bits of nominal capacity. For 8, 16, 32
and 64 trits that is approximately 12.7, 25.4, 50.7 and 101.4 bits. The original
binary patch contains 64 bits. Thus 64 trits are an over-capacity comparison,
not compression relative to the raw binary image. These figures exclude the
decoder weights and any storage-format overhead; Java integer arrays are not
packed ternary data.

## Practical bounds and validation

Training rejects more than 1,024 examples, more than 200 epochs, unsupported code
widths, or a work estimate `examples × epochs × width × 64` above 100 million.
Each example must be exactly 64 binary pixels. Cancellation is checked throughout
training and during inference. The four fits and evaluation run in a cancellable
Swing worker; a cancelled fit does not replace the previous completed models.

Tests verify a disjoint dataset split, ternary codes at every width, decoding
without source pixels, deterministic learning, recovery of a simple association,
corruption reproducibility, score bounds, work limits and cancellation. Swing
tests exercise training, selecting a width, changing a code cell, and restoring
the original encoding. The tab is also rendered offscreen and visually inspected.
