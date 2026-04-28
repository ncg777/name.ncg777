package name.ncg777.maths.music.apps.kernelEvaluator.kernels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import name.ncg777.maths.music.apps.kernelEvaluator.Kernel;
import name.ncg777.maths.music.apps.kernelEvaluator.ParameterType;
import name.ncg777.maths.sequences.Sequence;

/**
 * Recycles two sequences at different speeds and sums their values.
 *
 * <p>Sequence {@code s} is recycled at full speed (one step per hit).
 * Sequence {@code b} advances one element every {@code step} hits.
 * The result is {@code s[i % s.size()] + b[(i / step) % b.size()]}.
 *
 * <p>This is a generalization of {@code RecycleByteAdd} (which hard-coded {@code step = 8}).
 */
public class RecycleStepAdd implements Kernel {

  @Override
  public int getValue(Map<String, Object> parameters, int i) {
    Sequence s = (Sequence) parameters.get("s");
    Sequence b = (Sequence) parameters.get("b");
    int step = (Integer) parameters.get("step");
    if (step <= 0) step = 1;
    return s.get(i % s.size()) + b.get((i / step) % b.size());
  }

  @Override
  public String getName() {
    return "RecycleStepAdd";
  }

  @Override
  public String getDocumentation() {
    return "Recycles sequence s at full rate and sequence b at a slower rate, then sums them.\n"
        + "s advances every hit; b advances every 'step' hits.\n"
        + "Result: s[i % s.size()] + b[(i / step) % b.size()].\n"
        + "Equivalent to RecycleByteAdd when step = 8.";
  }

  @Override
  public List<String> getParameterNames() {
    ArrayList<String> names = new ArrayList<>();
    names.add("step");
    names.add("s");
    names.add("b");
    return names;
  }

  @Override
  public ParameterType getParameterType(String name) {
    switch (name) {
      case "step": return ParameterType.INT;
      case "s":    return ParameterType.SEQUENCE;
      case "b":    return ParameterType.SEQUENCE;
      default:     return null;
    }
  }
}
