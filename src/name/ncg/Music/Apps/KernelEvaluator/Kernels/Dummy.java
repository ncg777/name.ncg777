package name.ncg.Music.Apps.KernelEvaluator.Kernels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import name.ncg.Music.Apps.KernelEvaluator.Kernel;
import name.ncg.Music.Apps.KernelEvaluator.ParameterType;

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
