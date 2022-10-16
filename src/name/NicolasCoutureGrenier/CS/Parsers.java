package name.NicolasCoutureGrenier.CS;

import java.util.function.Function;

import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence;
import name.NicolasCoutureGrenier.Music.PCS12;

public class Parsers {
  public static Function<String, String> stringParser = (s) -> s;
  public static Function<String, Integer> integerParser = (s) -> Integer.parseInt(s.trim());
  public static Function<String, Double> doubleParser = (s) -> Double.parseDouble(s.trim());
  public static Function<String, Integer[]> intArrayParser  = (s) -> {
    String[] a = s.trim().split("\\s+");
    Integer[] o = new Integer[a.length];
    int i = 0;
    for(String x : a) {
      o[i++] = integerParser.apply(x);
    }
    return o;
  };
  public static Function<String, Sequence> sequenceParser  = (s) -> Sequence.parse(s);
  
  public static Function<String, PCS12> PCS12parser = (s) -> PCS12.parse(s);
}