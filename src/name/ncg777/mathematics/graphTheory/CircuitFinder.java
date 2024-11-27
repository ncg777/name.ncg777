package name.ncg777.mathematics.graphTheory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;


public class CircuitFinder<T extends Comparable<? super T>> {
  private HashMap<T, HashSet<T>> B;
  private DiGraph<T> c;
  private LinkedList<T> stack;
  private T s;
  private Consumer<List<T>> p;
  private Integer maxsize;

  {
    B = new HashMap<T, HashSet<T>>();
    stack = new LinkedList<T>();
  }

  public void filter(Predicate<T> p) {
    Iterator<T> i = c.getVertices().iterator();
    HashSet<T> h = new HashSet<T>();

    while (i.hasNext()) {
      T v = i.next();
      if (!p.apply(v)) {
        h.add(v);
      }
    }
    i = h.iterator();
    while (i.hasNext()) {
      c.removeVertex(i.next());
    }
  }

  public void setDiGraph(DiGraph<T> p_c) {
    c = p_c;
  }

  public DiGraph<T> getDiGraph() {
    return c;
  }

  public CircuitFinder(DiGraph<T> p_c, Consumer<List<T>> p_p) {
    c = p_c;
    p = p_p;
  }
  
  public void findCircuitsFrom(T p_s, Integer p_maxsize) {
    maxsize = p_maxsize;

    stack.clear();

    c.resetVertexMarkings();

    Iterator<T> i = c.getVertices().iterator();

    while (i.hasNext()) {
      B.put(i.next(), new HashSet<T>());
    }
    s = p_s;

    Circuit(s);

  }


  boolean Circuit(T v) {
    boolean f = false;

    stack.push(v);
    c.markVertex(v);
    Iterator<T> wi;

    wi = c.getSuccessors(v).iterator(); 
  

    while (wi.hasNext()) {
      T w = wi.next();
      if (w.equals(s) && ((maxsize != null) ? (stack.size() <= maxsize) : true)) {
        p.accept(Lists.reverse(stack));  
        
        f = true;
      } else if (!c.isVertexMarked(w)) {
        if (Circuit(w)) {
          f = true;
        }
      }
    }
    if (f) {
      Unblock(v);
    } else {
      wi = c.getSuccessors(v).iterator();  
      
      while (wi.hasNext()) {
        T w = wi.next();
        if (!B.get(w).contains(v)) {
          HashSet<T> x = B.get(w);
          x.add(v);
          B.put(w, x);
        }
      }
    }
    stack.pop();
    return f;
  }

  void Unblock(T u) {
    c.unmarkVertex(u);
    Iterator<T> i = B.get(u).iterator();
    while (i.hasNext()) {
      T tmp = i.next();
      i.remove();
      if (c.isVertexMarked(tmp)) {
        Unblock(tmp);
      }
    }
  }

}
