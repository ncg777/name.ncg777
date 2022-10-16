package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class ComparableSet<T> extends TreeSet<T> implements Comparable<ComparableSet<T>> {

  private static final long serialVersionUID = 4148094676015292276L;

  public ComparableSet() {
    super();
  }

  @Override
  public int compareTo(ComparableSet<T> arg0) {
    return IterableComparator.compare(this.iterator(), arg0.iterator(), this.comparator());
  }

  public ComparableSet(Collection<? extends T> c) {
    super(c);
  }

  public ComparableSet(Comparator<? super T> comparator) {
    super(comparator);
  }

  public ComparableSet(SortedSet<T> s) {
    super(s);
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
