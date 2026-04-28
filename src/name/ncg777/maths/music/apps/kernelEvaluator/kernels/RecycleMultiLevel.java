package name.ncg777.maths.music.apps.kernelEvaluator.kernels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import name.ncg777.maths.music.apps.kernelEvaluator.Kernel;
import name.ncg777.maths.music.apps.kernelEvaluator.ParameterType;
import name.ncg777.maths.sequences.Sequence;

/**
 * Multi-level recycling kernel with fully configurable step sizes.
 *
 * <p>The number of active levels is determined by the length of the {@code steps} sequence
 * (up to 8). For level {@code j} (0-indexed), the value at position {@code i} is
 * {@code s<j>.get((i / steps[j]) % s<j>.size())}. All level values are summed.
 *
 * <p>Examples:
 * <ul>
 *   <li>Binary/hex levels: {@code steps = 1 2 4 8 16 32 64 128}</li>
 *   <li>Octal/mixed levels: {@code steps = 1 2 3 6 12 24 48 96}</li>
 *   <li>Arbitrary: any combination of positive divisors</li>
 * </ul>
 */
public class RecycleMultiLevel implements Kernel {

  private static final int MAX_LEVELS = 8;
  private static final String[] SEQ_NAMES;

  static {
    SEQ_NAMES = new String[MAX_LEVELS];
    for (int j = 0; j < MAX_LEVELS; j++) {
      SEQ_NAMES[j] = "s" + j;
    }
  }

  @Override
  public int getValue(Map<String, Object> parameters, int i) {
    Sequence steps = (Sequence) parameters.get("steps");
    int levels = Math.min(steps.size(), MAX_LEVELS);
    int sum = 0;
    for (int j = 0; j < levels; j++) {
      Sequence s = (Sequence) parameters.get(SEQ_NAMES[j]);
      int step = steps.get(j);
      if (step <= 0) step = 1;
      sum += s.get((i / step) % s.size());
    }
    return sum;
  }

  @Override
  public String getName() {
    return "RecycleMultiLevel";
  }

  @Override
  public String getDocumentation() {
    return "Multi-level recycling with configurable step sizes.\n"
        + "The steps sequence defines one divisor per level (up to 8 levels).\n"
        + "Level j uses sequence sj, advancing one step every steps[j] hits.\n"
        + "All level values are summed.\n"
        + "Examples:\n"
        + "  Binary levels:  steps = 1 2 4 8 16 32 64 128\n"
        + "  Octal/mixed:    steps = 1 2 3 6 12 24 48 96";
  }

  @Override
  public List<String> getParameterNames() {
    ArrayList<String> names = new ArrayList<>();
    names.add("steps");
    for (String seq : SEQ_NAMES) {
      names.add(seq);
    }
    return names;
  }

  @Override
  public ParameterType getParameterType(String name) {
    if (name.equals("steps")) return ParameterType.SEQUENCE;
    for (String seq : SEQ_NAMES) {
      if (name.equals(seq)) return ParameterType.SEQUENCE;
    }
    return null;
  }
}
