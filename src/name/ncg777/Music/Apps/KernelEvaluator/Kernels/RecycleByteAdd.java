package name.ncg777.Music.Apps.KernelEvaluator.Kernels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import name.ncg777.Maths.Objects.Sequence;
import name.ncg777.Music.Apps.KernelEvaluator.Kernel;
import name.ncg777.Music.Apps.KernelEvaluator.ParameterType;

public class RecycleByteAdd implements Kernel {
  private Sequence s;
  private Sequence b;
  
  @Override
  public int getValue(Map<String, Object> parameters, int i) {
    s = (Sequence)parameters.get("s");
    b = (Sequence)parameters.get("b");
    return s.get(i % s.size())+b.get((i/8)%b.size());
  }

  @Override
  public String getName() {
    return "RecycleByteAdd";
  }

  @Override
  public String getDocumentation() {
    StringBuilder sb = new StringBuilder();
    sb.append("Recycles sequence s, adding element from b corresponding to byte number");
    
    return sb.toString();
  }

  @Override
  public List<String> getParameterNames() {
    ArrayList<String> o = new ArrayList<String>();
    o.add("s");
    o.add("b");
    return o;
  }

  @Override
  public ParameterType getParameterType(String name) {
    if(name == "s") return ParameterType.SEQUENCE;
    if(name == "b") return ParameterType.SEQUENCE;
    return null;
  }

}
