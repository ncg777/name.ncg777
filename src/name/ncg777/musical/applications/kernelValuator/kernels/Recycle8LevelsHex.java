package name.ncg777.musical.applications.kernelValuator.kernels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import name.ncg777.mathematics.objects.Sequence;
import name.ncg777.musical.applications.kernelValuator.Kernel;
import name.ncg777.musical.applications.kernelValuator.ParameterType;

public class Recycle8LevelsHex implements Kernel {
  private Sequence s1;
  private Sequence s2;
  private Sequence s4;
  private Sequence s8;
  private Sequence s16;
  private Sequence s32;
  private Sequence s64;
  private Sequence s128;
  
  @Override
  public int getValue(Map<String, Object> parameters, int i) {
    s1 = (Sequence)parameters.get("s1");
    s2 = (Sequence)parameters.get("s2");
    s4 = (Sequence)parameters.get("s4");
    s8 = (Sequence)parameters.get("s8");
    s16 = (Sequence)parameters.get("s16");
    s32 = (Sequence)parameters.get("s32");
    s64 = (Sequence)parameters.get("s64");
    s128 = (Sequence)parameters.get("s128");
    
    return s1.get(i % s1.size()) +
        s2.get((i/2)%s2.size()) +
        s4.get((i/4)%s4.size()) +
        s8.get((i/8)%s8.size()) +
        s16.get((i/16)%s16.size()) +
        s32.get((i/32)%s32.size()) +
        s64.get((i/64)%s64.size()) +
        s128.get((i/128)%s128.size());
  }

  @Override
  public String getName() {
    return "Recycle8LevelsHex";
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
    o.add("s4");
    o.add("s8");
    o.add("s16");
    o.add("s32");
    o.add("s64");
    o.add("s128");
    return o;
  }

  @Override
  public ParameterType getParameterType(String name) {
    if(name == "s1") return ParameterType.SEQUENCE;
    if(name == "s2") return ParameterType.SEQUENCE;
    if(name == "s4") return ParameterType.SEQUENCE;
    if(name == "s8") return ParameterType.SEQUENCE;
    if(name == "s16") return ParameterType.SEQUENCE;
    if(name == "s32") return ParameterType.SEQUENCE;
    if(name == "s64") return ParameterType.SEQUENCE;
    if(name == "s128") return ParameterType.SEQUENCE;
    return null;
  }

}
