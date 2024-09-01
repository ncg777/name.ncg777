package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.util.List;


public class Tuple<T extends Tuple<? super T>> extends ComparableList<T> {
  private static final long serialVersionUID = 1L;
  /*
  public static <
    T extends Comparable<? super U>, 
    R extends Tuple<? super T>
    > Tuple<T extends Tuple<? super T>> Tuple<? super T> create(T obj) {
      if (!(obj instanceof Tuple)){
        return Tuple.<T,R>create(obj);
      } else if(obj instanceof Tuple) {
        return (Tuple<R>)obj;
      } else if(obj instanceof List) {
        return new Tuple<R>((List<R>)obj));  
      } else if(obj.getClass().isArray()) {
        return new Tuple<R>(
            UnmodifiableList.<R>unmodifiableList(
                new ComparableList<R>((List<R>)Arrays.asList(obj))));
      }
  }
  */
  private Tuple(List<T> l) {
    super(l);
  }
  
  /*
    
    toString(Function<T,String> printer) {
      return 
          "<"+
            Joiner.on(",").join(
                this.stream().<String>map(
                    (t) -> {
                      if(t instanceof Tuple) { 
                         return ((Tuple<T>) t).toString(printer);
                      } else if(t instanceof List || t.getClass().isArray()) {
                        return Tuple.create(t).toString(printer);
                      } else {
                        return printer.apply(t);
                      }
                    } 
                ).toList()
              ) +
          ">";
  }
  */
  /*
  public static <S> Tuple 
    fromString(String s, Function<String, S> parser) {
      return Tuple.create(Arrays.asList(s.substring(1, s.length()-1).split(",")).stream().map(parser).toList());
  }
  */
}
