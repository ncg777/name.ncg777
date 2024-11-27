package name.ncg777.computerScience.dataStructures;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;



public class IterableComparator<T> implements Comparator<Iterable<T>> {

  Comparator<? super T> comparator;

  public IterableComparator() {
    this(null);
  }

  public IterableComparator(Comparator<? super T> comparator) {
    super();
    this.comparator = comparator;
  }

  @Override
  public int compare(Iterable<T> arg0, Iterable<T> arg1) {
    Iterator<T> i = arg0.iterator();
    Iterator<T> j = arg1.iterator();
    return compare(i, j, comparator);
  }

  public static <T> int compare(Iterator<T> i, Iterator<T> j) {
    return compare(i, j, null);
  }

  @SuppressWarnings("unchecked")
  public static <T> int compare(Iterator<T> i, Iterator<T> j,
      Comparator<? super T> comparator) {

    while (i.hasNext() && j.hasNext()) {
      T i_el = i.next();
      T j_el = j.next();

      if (i_el == null && j_el == null) {
        continue;
      }
      if (i_el == null) {
        return -1;
      }
      if (j_el == null) {
        return 1;
      }

      int c;
      if (comparator == null) {
        c = ((Comparable<? super T>) i_el).compareTo(j_el);
      } else {
        c = comparator.compare(i_el, j_el);
      }
      if (c != 0) {
        return c;
      }
    }

    if (!i.hasNext() && j.hasNext()) {
      return -1;
    }
    if (!j.hasNext() && i.hasNext()) {
      return 1;
    }

    return 0;
  }

  @SuppressWarnings("unchecked")
  private static <T> int reverseCompare(ListIterator<T> i, ListIterator<T> j,
      Comparator<? super T> comparator) {

    while (i.hasPrevious() && j.hasPrevious()) {
      int c;
      if (comparator == null) {
        c = ((Comparable<? super T>) i.previous()).compareTo(j.previous());
      } else {
        c = comparator.compare(i.previous(), j.previous());
      }
      if (c != 0) {
        return c;
      }
    }

    if (!i.hasPrevious() && j.hasPrevious()) {
      return -1;
    }
    if (!j.hasPrevious() && i.hasPrevious()) {
      return 1;
    }

    return 0;
  }

  public static <T> int reverseCompare(List<T> i, List<T> j) {
    return reverseCompare(i, j, null);
  }

  public static <T> int reverseCompare(List<T> i, List<T> j, Comparator<? super T> comparator) {
    return reverseCompare(i.listIterator(i.size()), j.listIterator(j.size()), comparator);
  }
}
