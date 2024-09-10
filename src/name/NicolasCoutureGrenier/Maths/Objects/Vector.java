package name.NicolasCoutureGrenier.Maths.Objects;

import java.util.ArrayList;
import java.util.List;

import name.NicolasCoutureGrenier.CS.DataStructures.IterableComparator;

public class Vector<T extends Comparable<? super T>> implements Comparable<Vector<T>> {
  private ArrayList<T> values = new ArrayList<T>();
  private IterableComparator<T> it = new IterableComparator<>();
  @SafeVarargs
  public static <T extends Comparable<? super T>> Vector<T> of(T...  values){
    Vector<T> o = new Vector<T>();
    o.values.addAll(List.of(values));
    return o;
  }
  public int getDimension() { return values.size(); }
  @Override
  public int compareTo(Vector<T> o) {
    return it.compare(this.values, o.values);
  }
}
