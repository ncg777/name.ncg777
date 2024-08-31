package name.NicolasCoutureGrenier.Maths.DataStructures;

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
  public static <T extends Comparable<? super T>> Tuple<T> create(T[] arr) {
    return new Tuple<T>(
        UnmodifiableList.<T>unmodifiableList(
            new ComparableList<T>(Arrays.asList(arr))));
  }
  private Tuple(List<T> l) {
    super(l);
  }
  
  public String 
    toString(Function<T,String> printer) {
      return Joiner.on(",").join(this.stream().map(printer).toList());
  }
  
  public static <X extends Comparable<? super X>> Tuple<X> 
    fromString(String s, Function<String, X> parser) {
      return Tuple.create(Arrays.asList(s.split(",")).stream().map(parser).toList());
  }
}
