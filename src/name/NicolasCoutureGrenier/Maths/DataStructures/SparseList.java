package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeMap;

public class SparseList<T> implements List<T> {
  private TreeMap<Integer,T> map = new TreeMap<>();
  private int n = 0;
  
  @Override
  public int size() {
    return n;
  }

  @Override
  public boolean isEmpty() {
    return n == 0;
  }

  @Override
  public boolean contains(Object o) {
    return (o == null && map.values().size() < n) ? true : map.values().contains(o);
  }

  @Override
  public Iterator<T> iterator() {
    return new SparseListIterator(); 
  }

  @Override
  public Object[] toArray() {
    Object[] o = new Object[n];
    for(var v : map.entrySet()) o[v.getKey()] = v.getValue();
    return o;
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public <U> U[] toArray(U[] a) {
    Object[] o = toArray();
    for(var v : map.entrySet()) o[v.getKey()] = v.getValue();
    return (U[])o;
  }
  @Override
  public boolean add(T e) {
    n++;
    if(e!=null) map.put(n-1, e);
    return true;
  }

  @Override
  public boolean remove(Object obj) {
    var o = false;
    for(var e : map.descendingMap().entrySet()) {
      if(e.getValue().equals(obj)) {
        o = true;
        map.remove(e.getKey());
        n--;
      }
    }
    return o;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    for(var v : c) {
      if(!this.contains(v)) return false;
    }
    return true;
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    for(var v : c) add(v);
    return c.size()>0;
  }

  @Override
  public boolean addAll(int index, Collection<? extends T> c) {
    for(var i : map.descendingKeySet()) {
      if(i < index) break;
      map.put(i+c.size(),map.remove(i));
    }
    for(var v:c) add(v);
    return c.size()>0;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    boolean o = false;
    for(var v : c) o = (remove(v) ? true : o);
    return o;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    var o = false;
    for(var e : c) o = (remove(e) ? true : o);
    return o;
  }

  @Override
  public void clear() {
    n=0;
    map.clear();
  }

  @Override
  public T get(int index) {
    if(index >= n )throw new IndexOutOfBoundsException("out of bounds");
    return map.containsKey(index) ? map.get(index) : null;
  }

  @Override
  public T set(int index, T element) {
    if(index >= n )throw new IndexOutOfBoundsException("out of bounds");
    var o = get(index);
    if(element == null) map.remove(index);
    map.put(index, element);
    return o;
  }

  @Override
  public void add(int index, T element) {
    if(index > n )throw new IndexOutOfBoundsException("out of bounds");
    for(var i : map.descendingKeySet()) {
      if(i < index) break;
      map.put(i+1,map.remove(i));
    }
    if(element != null) map.put(index, element);
    n++;
  }

  @Override
  public T remove(int index) {
    var o = map.remove(index);
    for(var i : map.descendingKeySet()) {
      if(i <= index) break;
      map.put(i-1,map.remove(i));
    }
    return o;
  }

  @Override
  public int indexOf(Object obj) {
    if(obj == null) {
      if(map.size() == n) return -1;
      int o = 0;
      for(var k : map.keySet()) {
        if(o < k) return o;
        else {o = k+1;}
      }
      return o;
    } else {
      for(var e : map.entrySet()) {
        if(e.getValue().equals(obj)) return e.getKey();
      }
    }
    
    return -1;
  }

  @Override
  public int lastIndexOf(Object obj) {
    if(obj == null) {
      if(map.size() == n) return -1;
      int o = n-1;
      for(var k : map.keySet()) {
        if(o > k) return o;
        else {o = k-1;}
      }
      return o;
    } else {
      for(var e : map.descendingKeySet()) {
        if(map.get(e).equals(obj)) return e;
      }
    }
    return -1;
  }

  private class SparseListIterator implements ListIterator<T> {
    private int current = 0;
    
    public SparseListIterator(int index) {
      current = index;
    }
    public SparseListIterator() {
      current = 0;
    }
    @Override
    public boolean hasNext() {
      return current < n;
    }

    @Override
    public T next() {
      return get(current++);
    }

    @Override
    public boolean hasPrevious() {
      return current > 0;
    }

    @Override
    public T previous() {
      return get(current--);
    }

    @Override
    public int nextIndex() {
      return hasNext() ? n : current+1;
    }

    @Override
    public int previousIndex() {
      return hasPrevious() ? -1 : current-1;
    }

    @Override
    public void remove() {
      SparseList.this.remove(current);
    }

    @Override
    public void set(T e) {
      SparseList.this.set(current, e);
    }

    @Override
    public void add(T e) {
      SparseList.this.add(e);
    }
    
  }
  @Override
  public ListIterator<T> listIterator() {
      return new SparseListIterator();
  }

  @Override
  public ListIterator<T> listIterator(int index) {
    return new SparseListIterator(index);
  }

  @Override
  public List<T> subList(int fromIndex, int toIndex) {
    var o = new SparseList<T>();
    for(var v : map.headMap(toIndex).tailMap(fromIndex).entrySet()) {
      o.map.put(v.getKey()-fromIndex, v.getValue());
    }
    o.n = toIndex-fromIndex;
    return o;
  }
}
