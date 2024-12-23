package name.ncg777.maths.enumerations.infinite;

import java.util.Enumeration;

public interface InfiniteEnumeration<E> extends Enumeration<E> {
  default public boolean hasMoreElements() {
    return true;
  }
}