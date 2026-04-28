package name.ncg777.maths.music.apps.kernelEvaluator.kernels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import name.ncg777.maths.music.apps.kernelEvaluator.Kernel;
import name.ncg777.maths.music.apps.kernelEvaluator.ParameterType;
import name.ncg777.maths.sequences.Sequence;

/**
 * Recycles a sequence and applies an integer linear transform to each value:
 * {@code result = s[i % s.size()] * multiplier + offset}.
 *
 * <p>Useful for transposing (offset only), stretching (multiplier only),
 * or combining both to remap a sequence into a desired range.
 */
public class RecycleScale implements Kernel {

  @Override
  public int getValue(Map<String, Object> parameters, int i) {
    Sequence s = (Sequence) parameters.get("s");
    int multiplier = (Integer) parameters.get("multiplier");
    int offset = (Integer) parameters.get("offset");
    return s.get(i % s.size()) * multiplier + offset;
  }

  @Override
  public String getName() {
    return "RecycleScale";
  }

  @Override
  public String getDocumentation() {
    return "Recycles sequence s and linearly transforms each value:\n"
        + "  result = s[i % s.size()] * multiplier + offset\n"
        + "Use multiplier = 1 and offset != 0 to transpose.\n"
        + "Use offset = 0 and multiplier != 1 to scale the range.\n"
        + "Use multiplier = -1 to invert the sequence.";
  }

  @Override
  public List<String> getParameterNames() {
    ArrayList<String> names = new ArrayList<>();
    names.add("s");
    names.add("multiplier");
    names.add("offset");
    return names;
  }

  @Override
  public ParameterType getParameterType(String name) {
    switch (name) {
      case "s":          return ParameterType.SEQUENCE;
      case "multiplier": return ParameterType.INT;
      case "offset":     return ParameterType.INT;
      default:           return null;
    }
  }
}
