package name.NicolasCoutureGrenier.CS;

import java.util.Base64;
import java.util.function.Function;

import name.NicolasCoutureGrenier.Maths.DataStructures.HeterogeneousPair;
import name.NicolasCoutureGrenier.Maths.DataStructures.HomogeneousPair;
import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence;
import name.NicolasCoutureGrenier.Maths.DataStructures.Tree;
import name.NicolasCoutureGrenier.Music.PCS12;

public class Parsers {
  public static Function<String, String> stringParser = (s) -> s;
  public static Function<String, Integer> integerParser = (s) -> Integer.parseInt(s.trim());
  public static Function<String, Double> doubleParser = (s) -> Double.parseDouble(s.trim());
  public static Function<String, Object[]> doubleArrayParser = arrayDecorator(doubleParser);
  public static Function<String, Object[]> integerArrayParser = arrayDecorator(integerParser);
  
  public static Function<String, Integer[]> intArrayParser  = (s) -> {
    String[] a = s.trim().split("\\s+");
    Integer[] o = new Integer[a.length];
    int i = 0;
    for(String x : a) {
      o[i++] = integerParser.apply(x);
    }
    return o;
  };
  
  public static <T> Function<String,Object[]> arrayDecorator(Function<String,T> parser) {
    return (s) -> (Object[])Tree.parseJSONObject(s, parser).toArray();
  }
  
  public static Function<String, Sequence> sequenceParser  = (s) -> Sequence.parse(s);
  public static Function<String, PCS12> PCS12parser = (s) -> PCS12.parse(s);
  public static Function<String, HomogeneousPair<Integer>> intPairParser = (s) -> {
    Sequence ss = Sequence.parse(s);
    return HomogeneousPair.makeHomogeneousPair(ss.get(0), ss.get(1));
  };
  
  public static <X> Function<String,X> base64Decorator(Function<String,X> parser) {
    return (String s) -> parser.apply(new String(Base64.getDecoder().decode(s.getBytes())));
  }
  
  public static <X> Function<String,X> nullDecorator(Function<String,X> parser) {
    return (s) -> s.equals("null") ? null : parser.apply(s);
  }
  
  public static <X> Function<String,Tree<X>> 
    treeNodeDecorator(Function<String,X> parser) {
    return (str) -> {
      return Tree.<X>parseJSONObject(str,parser);
    };
  }
  
  public static <
    T extends Comparable<? super T>, 
    U extends Comparable<? super U>> 
      Function<String, HeterogeneousPair<T,U>>
      heteroGeneousPairDecorator(Function<String,T> parser1, Function<String,U> parser2) {
      return (s) -> HeterogeneousPair.parseJSONObject(s, parser1, parser2);
  }
  public static <
  T extends Comparable<? super T>> 
    Function<String, HomogeneousPair<T>>
    homoGeneousPairDecorator(Function<String,T> parser) {
    return (s) -> HomogeneousPair.fromHeterogeneousPair(HeterogeneousPair.parseJSONObject(s, parser, parser));
  }
}