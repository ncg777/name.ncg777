package name.ncg.CS;

import java.util.function.Function;

import com.google.common.base.Joiner;

import name.ncg.Music.PCS12;

public class Printers {
  public static Function<String, String> stringPrinter = (s) -> s;
  public static Function<Integer, String> integerPrinter = (i) -> Integer.toString(i);
  public static Function<Double, String> doublePrinter = (d) -> Double.toString(d);
  public static Function<Integer[], String> intArrayPrinter = (a) -> "[" + Joiner.on(",").join(a) + "]";
  public static Function<PCS12, String> PCS12Printer = (pcs) -> pcs.toString();
 }