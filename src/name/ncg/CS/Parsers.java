package name.ncg.CS;

import java.util.function.Function;

import name.ncg.Music.PCS12;

public class Parsers {
  public static Function<String, String> stringParser = (s) -> s;
  public static Function<String, Integer> integerParser = (s) -> Integer.parseInt(s.trim());
  public static Function<String, Double> doubleParser = (s) -> Double.parseDouble(s.trim());
  public static Function<String, Integer[]> intArrayParser  = (s) -> {
    String t = s.replaceAll("\\[", "").replaceAll("\\]", "");
    String[] a = t.split(",");
    Integer[] o = new Integer[a.length];
    int i = 0;
    for(String x : a) {
      o[i++] = integerParser.apply(x);
    }
    return o;
  };
  
  public static Function<String, PCS12> PCS12parser = (s) -> PCS12.parse(s);
}