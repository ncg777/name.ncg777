package name.ncg777.maths.enumerations.infinite;

import java.util.Enumeration;
import java.util.function.Supplier;

public interface InfiniteEnumeration<E> extends Enumeration<E> {
  default public boolean hasMoreElements() {
    return true;
  }
  
  default public Supplier<E> asSupplier() {
    return () -> this.nextElement();
  }
}