package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.util.ArrayList;
import java.util.Collection;

public class ComparableList<T extends Comparable<? super T>> extends ArrayList<T>
    implements
      Comparable<ComparableList<T>> {

  private static final long serialVersionUID = 3544674044705816624L;

  public int compareTo(ComparableList<T> o) {
    return IterableComparator.compare(this.iterator(), o.iterator());
  }

  public ComparableList() {
    super();
  }

  public ComparableList(Collection<? extends T> c) {
    super(c);
  }

  public ComparableList(int initialCapacity) {
    super(initialCapacity);
  }

  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof ComparableList)) return false;
    @SuppressWarnings("unchecked")
    ComparableList<T> x = (ComparableList<T>)obj;
    return compareTo(x) == 0;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
