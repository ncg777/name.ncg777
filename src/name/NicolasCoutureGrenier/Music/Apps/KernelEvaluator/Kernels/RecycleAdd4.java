package name.NicolasCoutureGrenier.Music.Apps.KernelEvaluator.Kernels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import name.NicolasCoutureGrenier.Maths.Objects.Sequence;
import name.NicolasCoutureGrenier.Music.Apps.KernelEvaluator.Kernel;
import name.NicolasCoutureGrenier.Music.Apps.KernelEvaluator.ParameterType;

public class RecycleAdd4 implements Kernel {
  private Sequence s1;
  private Sequence s2;
  private Sequence s3;
  private Sequence s4;
  
  @Override
  public int getValue(Map<String, Object> parameters, int i) {
    s1 = (Sequence)parameters.get("s1");
    s2 = (Sequence)parameters.get("s2");
    s3 = (Sequence)parameters.get("s3");
    s4 = (Sequence)parameters.get("s4");
    return s1.get(i % s1.size()) + s2.get(i % s2.size()) + s3.get(i % s3.size()) + s4.get(i % s4.size());
  }

  @Override
  public String getName() {
    return "RecycleAdd4";
  }

  @Override
  public String getDocumentation() {
    StringBuilder sb = new StringBuilder();
    sb.append("4 Sequences Recycled and added at the same rate");
    
    return sb.toString();
  }

  @Override
  public List<String> getParameterNames() {
    ArrayList<String> o = new ArrayList<String>();
    o.add("s1");
    o.add("s2");
    o.add("s3");
    o.add("s4");
    return o;
  }

  @Override
  public ParameterType getParameterType(String name) {
    if(name == "s1") return ParameterType.SEQUENCE;
    if(name == "s2") return ParameterType.SEQUENCE;
    if(name == "s3") return ParameterType.SEQUENCE;
    if(name == "s4") return ParameterType.SEQUENCE;
    return null;
  }

}
