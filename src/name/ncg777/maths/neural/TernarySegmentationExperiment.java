package name.ncg777.maths.neural;

import java.util.*;
import java.util.function.IntConsumer;
import name.ncg777.maths.neural.TernaryContours.Raster;
import name.ncg777.maths.neural.TernarySegmentation.*;

/** Reproducible training and held-out comparisons; inference never reads truth labels. */
public final class TernarySegmentationExperiment {
  private TernarySegmentationExperiment() {}

  public static List<Example> trainingExamples() {
    List<Example> examples = new ArrayList<>(); Shape[] shapes = Shape.values();
    for (int i = 0; i < 48; i++) examples.add(TernarySegmentation.generate(
        i % 2 == 0 ? 32 : 40, shapes[i % shapes.length], i / 6 % 4, new double[] {0, .005, .02}[i / 12 % 3], 1000 + i));
    return List.copyOf(examples);
  }

  public static TernaryPatchNetwork train(IntConsumer progress) {
    return TernaryPatchNetwork.train(trainingExamples(), 30, 777, progress);
  }

  public record Result(TernaryPatchNetwork.Prediction prediction, Regions raw, Regions baseline, Regions learned) {}

  public static Result restore(TernaryPatchNetwork network, Raster observed, double threshold, int closingRadius) {
    int w = observed.width(), h = observed.height();
    boolean[] input = TernarySegmentation.observedBoundary(observed);
    Regions raw = TernarySegmentation.regions(w, h, input);
    Regions baseline = TernarySegmentation.regions(w, h, TernarySegmentation.close(w, h, input, closingRadius));
    if (network == null) return new Result(null, raw, baseline, null);
    var prediction = network.predict(observed);
    Regions learned = TernarySegmentation.regions(w, h, TernarySegmentation.close(w, h, prediction.threshold(threshold), closingRadius));
    return new Result(prediction, raw, baseline, learned);
  }

  public record Comparison(String condition, String method, int images, Score score, int ambiguousImages) {}

  /** Three corruption conditions, six shapes each. Seeds and image dimensions
   * differ from training. The test includes a deliberately ambiguous border crop. */
  public static List<Comparison> evaluate(TernaryPatchNetwork network, double threshold, int radius) {
    Objects.requireNonNull(network); List<Comparison> comparisons = new ArrayList<>();
    String[] conditions = {"Clean", "3-pixel gaps", "Gaps + 2% noise"};
    for (int c = 0; c < conditions.length; c++) {
      Score[] totals = {new Score(0,0,0,0,0), new Score(0,0,0,0,0), new Score(0,0,0,0,0)}; int[] ambiguous = new int[3];
      for (Shape shape : Shape.values()) {
        TernarySegmentation.check();
        Example example = TernarySegmentation.generate(64, shape, c == 0 ? 0 : 3, c == 2 ? .02 : 0, 20000 + shape.ordinal());
        Result result = restore(network, example.observed(), threshold, radius);
        Regions[] methods = {result.raw, result.baseline, result.learned};
        for (int m = 0; m < methods.length; m++) {
          totals[m] = totals[m].plus(TernarySegmentation.score(example, methods[m]));
          if (methods[m].ambiguousContours() > 0) ambiguous[m]++;
        }
      }
      String[] names = {"Observed + enclosure", "Closing + enclosure", "8×8 network + closing + enclosure"};
      for (int m = 0; m < names.length; m++) comparisons.add(new Comparison(conditions[c], names[m], Shape.values().length, totals[m], ambiguous[m]));
    }
    return List.copyOf(comparisons);
  }
}
