package name.ncg.Maths.DataStructures;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

/**
 * Compares sorted maps.
 * 
 * @author Nicolas Couture-Grenier
 * 
 * @param <T> type of the key; must be comparable
 * @param <U> type of the value; must be comparable
 */
public class SortedMapComparator<T, U> implements Comparator<SortedMap<T, U>> {
  private Comparator<? super T> keyComparator;
  private Comparator<? super U> valueComparator;

  public SortedMapComparator(Comparator<? super T> k, Comparator<? super U> v) {
    if ((k == null && v != null) || (k != null && v == null)) {
      throw new RuntimeException(
          "SortedMapComparator::SortedMapComparator : only one comparator is not null.");
    }
    keyComparator = k;
    valueComparator = v;
  }

  public SortedMapComparator() {
    this(null, null);
  }

  @SuppressWarnings("unchecked")
  @Override
  public int compare(SortedMap<T, U> arg0, SortedMap<T, U> arg1) {
    Iterator<Map.Entry<T, U>> i = arg0.entrySet().iterator();
    Iterator<Map.Entry<T, U>> j = arg1.entrySet().iterator();
    int c;
    Map.Entry<T, U> a;
    Map.Entry<T, U> b;
    while (i.hasNext() && j.hasNext()) {
      a = i.next();
      b = j.next();
      if (keyComparator == null) {
        c = ((Comparable<? super T>) a.getKey()).compareTo(b.getKey());
        if (c != 0) {
          return c;
        }
      } else {
        c = keyComparator.compare(a.getKey(), b.getKey());
        if (c != 0) {
          return c;
        }
      }
      if (valueComparator == null) {
        c = ((Comparable<? super U>) a.getValue()).compareTo(b.getValue());
        if (c != 0) {
          return c;
        }
      } else {
        c = valueComparator.compare(a.getValue(), b.getValue());
        if (c != 0) {
          return c;
        }
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
}
