package name.ncg777.maths.music.apps.kernelEvaluator.kernels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import name.ncg777.maths.music.apps.kernelEvaluator.Kernel;
import name.ncg777.maths.music.apps.kernelEvaluator.ParameterType;

/**
 * Returns the same constant integer value for every hit position.
 * Useful as a baseline pitch, velocity, or offset.
 */
public class Constant implements Kernel {

  @Override
  public int getValue(Map<String, Object> parameters, int i) {
    return (Integer) parameters.get("value");
  }

  @Override
  public String getName() {
    return "Constant";
  }

  @Override
  public String getDocumentation() {
    return "Returns the constant integer 'value' for every hit position.\n"
        + "Useful as a fixed pitch, velocity, or offset baseline.";
  }

  @Override
  public List<String> getParameterNames() {
    ArrayList<String> names = new ArrayList<>();
    names.add("value");
    return names;
  }

  @Override
  public ParameterType getParameterType(String name) {
    if (name.equals("value")) return ParameterType.INT;
    return null;
  }
}
