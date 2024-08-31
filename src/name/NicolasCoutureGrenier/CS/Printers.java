package name.NicolasCoutureGrenier.CS;

import java.util.Base64;
import java.util.function.Function;

import com.google.common.base.Joiner;

import name.NicolasCoutureGrenier.Maths.DataStructures.HomogeneousPair;
import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence;
import name.NicolasCoutureGrenier.Maths.DataStructures.Tuple;
import name.NicolasCoutureGrenier.Music.PCS12;

public class Printers {
  public static Function<String, String> stringPrinter = (s) -> s;
  public static Function<Integer, String> integerPrinter = (i) -> Integer.toString(i);
  public static Function<Double, String> doublePrinter = (d) -> Double.toString(d);
  public static Function<Integer[], String> intArrayPrinter = (a) -> Joiner.on(" ").join(a);
  public static Function<Sequence, String> sequencePrinter = (s) -> s.toString();
  public static Function<PCS12, String> PCS12Printer = (pcs) -> pcs.toString();
  public static Function<Tuple<Integer>, String> integerTuplePrinter = tupleDecorator(integerPrinter);
  public static Function<Tuple<Double>, String> doubleTuplePrinter = tupleDecorator(doublePrinter);
  public static Function<HomogeneousPair<Integer>, String> intPairPrinter = (p) -> {
    Sequence ss = new Sequence();
    ss.add(p.getFirst());
    ss.add(p.getSecond());
    return ss.toString();
  };
  
  public static <X> Function<X,String> base64Decorator(Function<X,String> printer) {
    return (X x) -> new String(Base64.getEncoder().encode(printer.apply(x).getBytes()));
  }
  
  public static <X> Function<X,String> nullDecorator(Function<X,String> printer) {
    return (x) -> x == null ? "null" : printer.apply(x);
  }
  public static <X extends Comparable<? super X>> 
    Function<Tuple<X>, String> tupleDecorator(Function<X,String> printer) {
      return (t) -> t.toString(printer);
  }
 }