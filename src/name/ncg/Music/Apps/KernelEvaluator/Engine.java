package name.ncg.Music.Apps.KernelEvaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import name.ncg.Maths.DataStructures.Pair;
import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Music.R16List;
import name.ncg.Music.Rhythm;
import name.ncg.Music.Apps.KernelEvaluator.Kernels.Dummy;
import name.ncg.Music.Apps.KernelEvaluator.Kernels.ModsRecycle;
import name.ncg.Music.Apps.KernelEvaluator.Kernels.Recycle;
import name.ncg.Music.Apps.KernelEvaluator.Kernels.Recycle8LevelsHex;
import name.ncg.Music.Apps.KernelEvaluator.Kernels.Recycle8LevelsOctal;
import name.ncg.Music.Apps.KernelEvaluator.Kernels.RecycleAdd4;
import name.ncg.Music.Apps.KernelEvaluator.Kernels.RecycleByteAdd;

public class Engine {
  public static List<Kernel> Kernels = new ArrayList<Kernel>();
  public static Map<String, Kernel> KernelsByName = new TreeMap<String, Kernel>();
  static {
    // Add kernels here
    // BEGIN
    Kernels.add(new Dummy());
    Kernels.add(new Recycle());
    Kernels.add(new RecycleAdd4());
    Kernels.add(new Recycle8LevelsHex());
    Kernels.add(new Recycle8LevelsOctal());
    Kernels.add(new RecycleByteAdd());
    Kernels.add(new ModsRecycle());
    // END
    
    for(Kernel kernel : Kernels) {
      KernelsByName.put(kernel.getName(), kernel);
    }
    
  }
  
  
  private boolean generatesDeltas = false;
  
  public boolean isGeneratesDeltas() {
    return generatesDeltas;
  }

  public void setGeneratesDeltas(boolean generatesDeltas) {
    this.generatesDeltas = generatesDeltas;
  }

  public Engine(boolean generatesDeltas) {
    super();
    this.generatesDeltas = generatesDeltas;
  }

  public Pair<Sequence> evaluate(Rhythm rhythm, final String _parameters, String kernel) {
    return evaluate(rhythm, _parameters, KernelsByName.get(kernel));
  }
  
  public Pair<Sequence> evaluate(Rhythm rhythm, final String _parameters, Kernel kernel) {

    final String lines[] = _parameters.split("\n");
    final TreeMap<String, String> tokens = new TreeMap<String, String>();
    
    for(String s : kernel.getParameterNames()) {
      for(String l : lines) {
        String assignment[] = l.split("=");
        String lhs = assignment[0].trim();
        String rhs = assignment[1].trim();
        if(lhs.equals(s)) {
          tokens.put(lhs, rhs);
          break;
        }
      }
    }
    TreeMap<String, Object> parameters = new TreeMap<String, Object>();
    
    Function<String, Object> fp = (p) -> {
      switch(kernel.getParameterType(p)) {
        case BOOL:
          return Boolean.getBoolean(tokens.get(p));
        case INT:
            return Integer.parseInt(tokens.get(p));
        case DOUBLE:
            return Double.parseDouble(tokens.get(p));
        case SEQUENCE:
            return Sequence.parse(tokens.get(p));
        default:
          return null;
      }
    };
    
    for(String s : kernel.getParameterNames()) {
      parameters.put(s, fp.apply(s));
    }
    
    Sequence o = new Sequence();
    
    for(int hit : rhythm.asSequence()) {
      o.add(kernel.getValue(parameters, hit));
    }
    
    if(generatesDeltas) {
      return Pair.makePair(o, o.cyclicalForwardAntidifference(0));
    } else {
      return Pair.makePair(o.cyclicalForwardDifference(), o);
    }
  }
  
  
  
}
