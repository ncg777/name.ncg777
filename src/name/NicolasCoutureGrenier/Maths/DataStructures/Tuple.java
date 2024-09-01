package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.collections4.list.UnmodifiableList;

import com.google.common.base.Joiner;

public class Tuple<T extends Comparable<? super T>> extends ComparableList<T> {
  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  public static <T extends Comparable<? super T>> Tuple<T> create(List<T> arr) {
    return new Tuple<T>(
        UnmodifiableList.<T>unmodifiableList(
            new ComparableList<T>(arr)));
  }
  @SuppressWarnings("unchecked")
  public static <T extends Comparable<? super T>> Tuple<T> create(T[] arr) {
    var arrClass = arr.getClass();
    var arrayType = arrClass.arrayType();
    if(arrayType.isArray()) {
      List<Tuple<T>> o = new ArrayList<>();
      for(var e : arr) {
         o.add(create(Arrays.asList(e)));    
      }
      return (Tuple<T>)Tuple.create(o);
    }
    return new Tuple<T>(
        UnmodifiableList.<T>unmodifiableList(
            new ComparableList<T>(Arrays.asList(arr))));
  }
  private Tuple(List<T> l) {
    super(l);
  }
  
  public String 
    toString(Function<T,String> printer) {
      return "["+Joiner.on(",").join(this.stream().map(printer).toList())+"]";
  }
  
  public static <X extends Comparable<? super X>> Tuple<X> 
    fromString(String s, Function<String, X> parser) {
      return Tuple.create(Arrays.asList(s.substring(1, s.length()-1).split(",")).stream().map(parser).toList());
  }
}
