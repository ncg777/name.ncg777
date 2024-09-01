package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.collections4.list.UnmodifiableList;

import com.google.common.base.Joiner;


public class Tuple<T extends Comparable<? super T>> extends ComparableList<T> {
  private static final long serialVersionUID = 1L;
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <S> Tuple create(List<S> arr) {
    return new Tuple(
        UnmodifiableList.<S>unmodifiableList(
            new ComparableList(arr)));
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <S> Tuple create(S obj) {
    if(obj instanceof List) return create((List)obj);
    
    var arrClass = obj.getClass();
    var arrayType = arrClass.arrayType();
    if(arrayType.isArray()) {
      List<Tuple> o = new ArrayList<>();
      for(var e : (Object[])obj) {
         o.add(create(e));    
      }
      return (Tuple)Tuple.create(o);
    }
    return new Tuple(
        UnmodifiableList.unmodifiableList(
            new ComparableList(Arrays.asList(obj))));
  }
  
  private Tuple(List<T> l) {
    super(l);
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  public String 
    toString(Function<T,String> printer) {
      return 
          "["+
            Joiner.on(",").join(
                this.stream().<String>map(
                    (t) -> 
                        (t instanceof Tuple) ? 
                            ((Tuple) t).toString(printer) : 
                            printer.apply(t)
                ).toList()
              ) +
          "]";
  }
  
  
  @SuppressWarnings("rawtypes")
  public static <S> Tuple 
    fromString(String s, Function<String, S> parser) {
      return Tuple.create(Arrays.asList(s.substring(1, s.length()-1).split(",")).stream().map(parser).toList());
  }
}
