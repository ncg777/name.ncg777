package name.NicolasCoutureGrenier.Maths.Objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import name.NicolasCoutureGrenier.CS.DataStructures.IterableComparator;

public class Vector<T extends Comparable<? super T>> implements Comparable<Vector<T>> {
  private ArrayList<T> values = new ArrayList<T>();
  private IterableComparator<T> it = new IterableComparator<>();
  @SafeVarargs
  public static <T extends Comparable<? super T>> Vector<T> of(T...  values){
    return of(List.of(values));
  }
  public static <T extends Comparable<? super T>> Vector<T> of(Iterable<T>  values){
    Vector<T> o = new Vector<T>();
    for(var x : values) o.values.add(x);
    return o;
  }
  public T get(int i) {return values.get(i);}
  public T set(int i, T value) {return values.set(i,value);}
  
  public int getDimension() { return values.size(); }
  @Override
  public int compareTo(Vector<T> o) {
    return it.compare(this.values, o.values);
  }
  @Override
  public int hashCode() {
    return Objects.hash(it, values);
  }
  @SuppressWarnings("rawtypes")
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Vector other = (Vector) obj;
    return Objects.equals(it, other.it) && Objects.equals(values, other.values);
  }
  @Override
  public String toString() {
    return values.toString();
  }
  
}