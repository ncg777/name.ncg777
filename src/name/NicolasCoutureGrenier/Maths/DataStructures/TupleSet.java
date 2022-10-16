package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TupleSet<E extends Comparable<? super E>> implements Set<E[]> {

  TreeSet<ComparableList<E>> ts = new TreeSet<ComparableList<E>>();
  private Integer dimension;
  
  public TupleSet(int dimension){this.dimension = dimension;}

  @Override public int size() {return ts.size();}

  @Override public boolean isEmpty() {return ts.isEmpty();}
  
  @SuppressWarnings("unchecked") private Function<ComparableList<E>, E[]>  comparableListToArray = 
      (t) -> ((E[]) t.toArray(new Object[t.size()]));
   
  private Function<E[],ComparableList<E>> arrayToComparableList = (t) -> {
       ComparableList<E> c = new ComparableList<E>();
       for(int i=0;i<t.length; i++){c.add(t[i]);}
       return c;
     };
 
  
  @SuppressWarnings("unchecked")
  @Override
  public boolean contains(Object o) {
    try{
      return containsArray((E[]) o);
    } catch(Exception e){return false;}
  }
  
  private boolean containsArray(E[] o){
    if(o == null){return false;}
    ComparableList<E> c = new ComparableList<E>();
    for(int i=0;i<o.length;i++){c.add(o[i]);}
    return ts.contains(c);
  }
  
  @Override
  public Iterator<E[]> iterator() {
    return new TransformedIterator<ComparableList<E>, E[]>(
        ts.iterator(), 
        comparableListToArray);
  }

  @Override
  public Object[] toArray() {
    Object[] o = new Object[ts.size()];
    int i = 0;
    for(ComparableList<E> c : ts){
      o[i++] = comparableListToArray.apply(c);
    }
    return o;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T[] toArray(T[] a) {
    return (T[]) toArray();
  }

  private void verifyLength(E[] e){
    if(e.length != dimension){throw new RuntimeException();}
  }
  @Override
  public boolean add(E[] e) {
    verifyLength(e);
    return ts.add(arrayToComparableList.apply(e));
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean remove(Object o) {
    return removeArray((E[]) o);
  }
  private boolean removeArray(E[] e) {
    return ts.remove(arrayToComparableList.apply(e));
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public boolean containsAll(Collection<?> c) {
    return !(c.stream().anyMatch((t) -> !(ts.contains(arrayToComparableList.apply((E[]) t)))));
  }

  @Override
  public boolean addAll(Collection<? extends E[]> c) {
    return ts.addAll(
    c.stream().map(arrayToComparableList).collect(
      Collectors.toCollection(TreeSet<ComparableList<E>>::new)));
    
  }
  @SuppressWarnings("unchecked")
  @Override
  public boolean retainAll(Collection<?> c) {
    return ts.retainAll(
      ((Collection<E[]>)c).stream().map(arrayToComparableList).collect(
        Collectors.toCollection(TreeSet<ComparableList<E>>::new)));
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public boolean removeAll(Collection<?> c) {
    return ts.removeAll(
      ((Collection<E[]>)c).stream().map(arrayToComparableList).collect(
        Collectors.toCollection(TreeSet<ComparableList<E>>::new)));
  }
  
  @Override
  public void clear() {ts.clear();}
}
