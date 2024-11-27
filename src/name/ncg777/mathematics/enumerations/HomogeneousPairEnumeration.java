package name.ncg777.mathematics.enumerations;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

import name.ncg777.computerScience.dataStructures.HomoPair;

public class  HomogeneousPairEnumeration<
  T extends Comparable<? super T>> implements Enumeration<HomoPair<T>> {
  
  private Iterator<T> T1Iterator;
  private T T1Current;
  private Iterator<T> T2Iterator;
  private T T2Current;
  private Iterable<T> T2Iterable;
  public HomogeneousPairEnumeration(Iterable<T> IT1, Iterable<T> IT2) {
    T1Iterator = IT1.iterator();
    T1Current = T1Iterator.next();
    T2Iterator = IT2.iterator();
    T2Iterable = IT2;
  }
  
  @Override
  public boolean hasMoreElements() {
    if(T1Iterator.hasNext()) { return true;}
    if(T2Iterator.hasNext()) { return true;}
    return false;
  }

  @Override
  public HomoPair<T> nextElement() {
    if(T2Iterator.hasNext()) {
      T2Current = T2Iterator.next();
      return HomoPair.makeHomoPair(T1Current, T2Current); 
    } else if(T1Iterator.hasNext()) {
      T1Current = T1Iterator.next();
      T2Iterator = T2Iterable.iterator();
      T2Current = T2Iterator.next();
      return HomoPair.makeHomoPair(T1Current, T2Current);
    }
    throw new NoSuchElementException("No more elements.");
  }
}