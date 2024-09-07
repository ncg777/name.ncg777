package name.NicolasCoutureGrenier.Music.Apps.KernelEvaluator.Kernels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import name.NicolasCoutureGrenier.Maths.Objects.Sequence;
import name.NicolasCoutureGrenier.Music.Apps.KernelEvaluator.Kernel;
import name.NicolasCoutureGrenier.Music.Apps.KernelEvaluator.ParameterType;

public class ModsRecycle implements Kernel {

  @Override
  public int getValue(Map<String, Object> parameters, int i) {
    Sequence mods = (Sequence)parameters.get("mods");
    Sequence s = (Sequence)parameters.get("s");
    int index = i;
    
    for(int j=mods.size()-1; j>=0; j--) {
      index = index % mods.get(j);
    }
    return s.get(index%s.size());
  }

  @Override
  public String getName() {
    return "ModsRecycle";
  }

  @Override
  public String getDocumentation() {
    return "Mods then recycle";
  }

  @Override
  public List<String> getParameterNames() {
    ArrayList<String> o = new ArrayList<String>();
    o.add("mods");
    o.add("s");
    return o;
  }

  @Override
  public ParameterType getParameterType(String name) {
    switch(name) {
      case "mods":
        return ParameterType.SEQUENCE;
      case "s":
        return ParameterType.SEQUENCE;
    }
    return null;
  }

}
