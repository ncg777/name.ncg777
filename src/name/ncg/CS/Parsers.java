package name.ncg.CS;

import java.util.function.Function;

public class Parsers {
  public static Function<String, String> stringParser = (s) -> s;
  public static Function<String, Integer> integerParser = (s) -> Integer.parseInt(s);
  public static Function<String, Double> doubleParser = (s) -> Double.parseDouble(s);
}