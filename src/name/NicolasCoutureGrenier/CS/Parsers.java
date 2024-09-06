package name.NicolasCoutureGrenier.CS;

import java.util.Base64;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import name.NicolasCoutureGrenier.Maths.DataStructures.Combination;
import name.NicolasCoutureGrenier.Maths.DataStructures.Composition;
import name.NicolasCoutureGrenier.Maths.DataStructures.HeteroPair;
import name.NicolasCoutureGrenier.Maths.DataStructures.HomoPair;
import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence;
import name.NicolasCoutureGrenier.Maths.DataStructures.JaggedList;
import name.NicolasCoutureGrenier.Music.PCS12;

public class Parsers {
  public static Function<String, String> stringParser = (s) -> s;
  public static ToIntFunction<String> intParser =new ToIntFunction<String>() {
    @Override
    public int applyAsInt(String value) {
        return Integer.parseInt(value);
    }  
  };
  
  public static Function<String, Integer> integerParser = (s) -> Integer.parseInt(s.trim());
  public static Function<String, Double> doubleParser = (s) -> Double.parseDouble(s.trim());
  public static Function<String, Object[]> doubleArrayParser = arrayDecorator(doubleParser);
  public static Function<String, Object[]> integerArrayParser = arrayDecorator(integerParser);
  public static Function<String, Combination> combinationParser = (s) -> Combination.fromBinaryString(s);
  public static Function<String, Composition> compositionParser = (s) -> new Composition(Combination.fromBinaryString(s));
  
  public static Function<String, Integer[]> intArrayParser  = (s) -> {
    String[] a = s.trim().split("\\s+");
    Integer[] o = new Integer[a.length];
    int i = 0;
    for(String x : a) {
      o[i++] = integerParser.apply(x);
    }
    return o;
  };
  
  public static <T extends Comparable<? super T>> Function<String,Object[]> arrayDecorator(Function<String,T> parser) {
    return (s) -> (Object[])JaggedList.parseJSONArray(s, parser).toArray();
  }
  public static <T> Function<String, T> quoteRemoverDecorator(Function<String, T> parser) {
    return (s) -> {
      String u = s;
      u = u.substring(1);
      u = u.substring(0, u.length()-1);
      
      return parser.apply(u);
    };
  }
  public static Function<String, Sequence> sequenceParser  = (s) -> Sequence.parse(s);
  public static Function<String, PCS12> PCS12parser = (s) -> PCS12.parse(s);
  public static Function<String, HomoPair<Integer>> intPairParser = (s) -> {
    Sequence ss = Sequence.parse(s);
    return HomoPair.makeHomoPair(ss.get(0), ss.get(1));
  };
  
  public static <X> Function<String,X> base64Decorator(Function<String,X> parser) {
    return (String s) -> parser.apply(new String(Base64.getDecoder().decode(s.getBytes())));
  }
  
  public static <X> Function<String,X> nullDecorator(Function<String,X> parser) {
    return (s) -> s == null || s.equals("null") ? null : parser.apply(s);
  }
  
  public static <X extends Comparable<? super X>> Function<String,JaggedList<X>> 
    jaggedListDecorator(Function<String,X> parser) {
    return (str) -> {
      return JaggedList.<X>parseJSONArray(str,parser);
    };
  }
  
  public static <
    T extends Comparable<? super T>, 
    U extends Comparable<? super U>> 
      Function<String, HeteroPair<T,U>>
      heteroPairDecorator(Function<String,T> parser1, Function<String,U> parser2) {
      return (s) -> HeteroPair.parseJSONObject(s, parser1, parser2);
  }
  public static <
  T extends Comparable<? super T>> 
    Function<String, HomoPair<T>>
    homoPairDecorator(Function<String,T> parser) {
    return (s) -> HomoPair.fromHeteroPair(HeteroPair.parseJSONObject(s, parser, parser));
  }
}