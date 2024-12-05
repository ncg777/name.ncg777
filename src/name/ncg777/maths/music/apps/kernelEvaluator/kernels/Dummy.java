package name.ncg777.maths.music.apps.kernelEvaluator.kernels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import name.ncg777.maths.music.apps.kernelEvaluator.Kernel;
import name.ncg777.maths.music.apps.kernelEvaluator.ParameterType;

public class Dummy implements Kernel{

  @Override
  public int getValue(Map<String, Object> parameters, int i) {
    return i;
  }

  @Override
  public String getName() {
    return "Dummy";
  }

  @Override
  public String getDocumentation() {
    return "Returns i";
  }

  @Override
  public List<String> getParameterNames() {
    return new ArrayList<String>();
  }

  @Override
  public ParameterType getParameterType(String name) {
    return null;
  }

}
