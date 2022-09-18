package name.ncg.Maths.DataStructures;

import java.util.Comparator;
import java.util.TreeMap;

public class ComparableMap<T, U> extends TreeMap<T, U> implements Comparable<ComparableMap<T, U>> {

  private static final long serialVersionUID = -1529794183235221230L;
  private SortedMapComparator<T, U> c;

  // can't support the single comparator version because U would require another comparator
  // public ComparableMap(Comparator<? super T> comparator) {super(comparator);}

  // public ComparableMap(Map<? extends T, ? extends U> m) {super(m);}

  // public ComparableMap(SortedMap<T, ? extends U> m) {super(m);}

  public ComparableMap(Comparator<? super T> keyComparator, Comparator<? super U> valueComparator) {
    super(keyComparator);
    c = new SortedMapComparator<T, U>(keyComparator, valueComparator);
  }

  public ComparableMap(ComparableMap<T, U> s) {
    super(s);
    c = s.c;
  }

  public ComparableMap() {
    c = new SortedMapComparator<T, U>();
  }

  @Override
  public int compareTo(ComparableMap<T, U> arg0) {
    return c.compare(this, arg0);
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

}
