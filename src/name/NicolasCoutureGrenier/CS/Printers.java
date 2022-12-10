package name.NicolasCoutureGrenier.CS;

import java.util.function.Function;

import com.google.common.base.Joiner;

import name.NicolasCoutureGrenier.Maths.DataStructures.HomogeneousPair;
import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence;
import name.NicolasCoutureGrenier.Music.PCS12;

public class Printers {
  public static Function<String, String> stringPrinter = (s) -> s;
  public static Function<Integer, String> integerPrinter = (i) -> Integer.toString(i);
  public static Function<Double, String> doublePrinter = (d) -> Double.toString(d);
  public static Function<Integer[], String> intArrayPrinter = (a) -> Joiner.on(" ").join(a);
  public static Function<Sequence, String> sequencePrinter = (s) -> s.toString();
  public static Function<PCS12, String> PCS12Printer = (pcs) -> pcs.toString();
  public static Function<HomogeneousPair<Integer>, String> intPairPrinter = (p) -> {
    Sequence ss = new Sequence();
    ss.add(p.getFirst());
    ss.add(p.getSecond());
    return ss.toString();
  };
 }