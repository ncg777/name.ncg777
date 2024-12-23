package name.ncg777.maths.enumerations.infinite;

import java.util.Enumeration;
import java.util.function.Supplier;

public class Looper<E> implements InfiniteEnumeration<E> {
  private Supplier<Enumeration<E>> c;
  private Enumeration<E> current;
  
  public Looper(Supplier<Enumeration<E>> c) {
    this.c = c;
    current = c.get();
  }
  
  public E nextElement() {
    if(!current.hasMoreElements()) current = c.get();
    return current.nextElement();
  }
}