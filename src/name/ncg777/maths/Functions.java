package name.ncg777.maths;

import java.util.function.BiFunction;

public class Functions {
  public static BiFunction<Double,Double,Double> ROND = (x,t) -> 1.0 - 
      Math.pow(1.0 - Math.pow(x, Math.sqrt(t)/Math.sqrt(1-t)),
      Math.sqrt(1-t)/Math.sqrt(t));
}
