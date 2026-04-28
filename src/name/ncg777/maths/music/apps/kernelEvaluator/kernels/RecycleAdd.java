package name.ncg777.maths.music.apps.kernelEvaluator.kernels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import name.ncg777.maths.music.apps.kernelEvaluator.Kernel;
import name.ncg777.maths.music.apps.kernelEvaluator.ParameterType;
import name.ncg777.maths.sequences.Sequence;

/**
 * Sums N independently-recycled sequences, where N is controlled by the {@code n} parameter
 * (1 &le; n &le; 8). Parameters {@code s1} through {@code s8} hold the sequences; only the
 * first {@code n} are used.
 */
public class RecycleAdd implements Kernel {

  private static final int MAX_SEQUENCES = 8;
  private static final String[] SEQ_NAMES;

  static {
    SEQ_NAMES = new String[MAX_SEQUENCES];
    for (int j = 0; j < MAX_SEQUENCES; j++) {
      SEQ_NAMES[j] = "s" + (j + 1);
    }
  }

  @Override
  public int getValue(Map<String, Object> parameters, int i) {
    int n = Math.min(Math.max((Integer) parameters.get("n"), 1), MAX_SEQUENCES);
    int sum = 0;
    for (int j = 0; j < n; j++) {
      Sequence s = (Sequence) parameters.get(SEQ_NAMES[j]);
      sum += s.get(i % s.size());
    }
    return sum;
  }

  @Override
  public String getName() {
    return "RecycleAdd";
  }

  @Override
  public String getDocumentation() {
    return "Sums n recycled sequences (1 \u2264 n \u2264 8).\n"
        + "Each sequence s1..sn is independently recycled at full rate and the values are summed.\n"
        + "Set n to control how many sequences are active.";
  }

  @Override
  public List<String> getParameterNames() {
    ArrayList<String> names = new ArrayList<>();
    names.add("n");
    for (String seq : SEQ_NAMES) {
      names.add(seq);
    }
    return names;
  }

  @Override
  public ParameterType getParameterType(String name) {
    if (name.equals("n")) return ParameterType.INT;
    for (String seq : SEQ_NAMES) {
      if (name.equals(seq)) return ParameterType.SEQUENCE;
    }
    return null;
  }
}
