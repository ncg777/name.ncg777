package name.ncg.Music.Apps.KernelEvaluator.Kernels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Music.Apps.KernelEvaluator.Kernel;
import name.ncg.Music.Apps.KernelEvaluator.ParameterType;

public class Recycle8LevelsOctal implements Kernel {
  private Sequence s1;
  private Sequence s2;
  private Sequence s3;
  private Sequence s6;
  private Sequence s12;
  private Sequence s24;
  private Sequence s48;
  private Sequence s96;
  
  @Override
  public int getValue(Map<String, Object> parameters, int i) {
    s1 = (Sequence)parameters.get("s1");
    s2 = (Sequence)parameters.get("s2");
    s3 = (Sequence)parameters.get("s3");
    s6 = (Sequence)parameters.get("s6");
    s12 = (Sequence)parameters.get("s12");
    s24 = (Sequence)parameters.get("s24");
    s48 = (Sequence)parameters.get("s48");
    s96 = (Sequence)parameters.get("s96");
    
    return s1.get(i % s1.size()) +
        s2.get((i/2)%s2.size()) +
        s3.get((i/3)%s3.size()) +
        s6.get((i/6)%s6.size()) +
        s12.get((i/12)%s12.size()) +
        s24.get((i/24)%s24.size()) +
        s48.get((i/48)%s48.size()) +
        s96.get((i/96)%s96.size());
  }

  @Override
  public String getName() {
    return "Recycle8LevelsOctal";
  }

  @Override
  public String getDocumentation() {
    StringBuilder sb = new StringBuilder();
    sb.append("Recycles 8 sequences and sums them, \nsequence sn is incremented every n steps");
    
    return sb.toString();
  }

  @Override
  public List<String> getParameterNames() {
    ArrayList<String> o = new ArrayList<String>();
    o.add("s1");
    o.add("s2");
    o.add("s3");
    o.add("s6");
    o.add("s12");
    o.add("s24");
    o.add("s48");
    o.add("s96");
    return o;
  }

  @Override
  public ParameterType getParameterType(String name) {
    if(name == "s1") return ParameterType.SEQUENCE;
    if(name == "s2") return ParameterType.SEQUENCE;
    if(name == "s3") return ParameterType.SEQUENCE;
    if(name == "s6") return ParameterType.SEQUENCE;
    if(name == "s12") return ParameterType.SEQUENCE;
    if(name == "s24") return ParameterType.SEQUENCE;
    if(name == "s48") return ParameterType.SEQUENCE;
    if(name == "s96") return ParameterType.SEQUENCE;
    return null;
  }

}
