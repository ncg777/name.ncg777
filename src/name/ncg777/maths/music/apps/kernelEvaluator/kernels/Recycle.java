package name.ncg777.maths.music.apps.kernelEvaluator.kernels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import name.ncg777.maths.music.apps.kernelEvaluator.Kernel;
import name.ncg777.maths.music.apps.kernelEvaluator.ParameterType;
import name.ncg777.maths.sequences.Sequence;

public class Recycle implements Kernel {
  private Sequence s;
  
  @Override
  public int getValue(Map<String, Object> parameters, int i) {
    s = (Sequence)parameters.get("s");
    return s.get(i % s.size());
  }

  @Override
  public String getName() {
    return "Recycle";
  }

  @Override
  public String getDocumentation() {
    StringBuilder sb = new StringBuilder();
    sb.append("Recycles (or loops into) sequence");
    
    return sb.toString();
  }

  @Override
  public List<String> getParameterNames() {
    ArrayList<String> o = new ArrayList<String>();
    o.add("s");
    return o;
  }

  @Override
  public ParameterType getParameterType(String name) {
    if(name == "s") return ParameterType.SEQUENCE;
    return null;
  }

}
