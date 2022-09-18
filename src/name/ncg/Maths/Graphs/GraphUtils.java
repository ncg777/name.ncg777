package name.ncg.Maths.Graphs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import name.ncg.Maths.DataStructures.CollectionUtils;


public class GraphUtils {

  public static <T extends Comparable<? super T>> T getRandomNode(DiGraph<T> g) {
    return CollectionUtils.chooseAtRandom(g.getVertices().iterator(), g.getVertexCount());
  }

  public static <T extends Comparable<? super T>> T getRandomNode(DiGraph<T> g, T n) {
    return CollectionUtils.chooseAtRandom(g.getSuccessors(n).iterator(), g.getSuccessorCount(n));
  }

  /**
   * Depth-first search.
   * 
   * @param c
   */
  public static <T extends Comparable<? super T>> void dfs(DiGraph<T> g, T c) {
    g.markVertex(c);
    Iterator<T> i = g.getSuccessors(c).iterator();
    while (i.hasNext()) {
      T k = i.next();
      if (!g.isVertexMarked(k)) {
        dfs(g, k);
      }
    }
  }

  
  public static <T extends Comparable<? super T>> List<T> getRandomWalk(
    DiGraph<T> g, int len){
    List<T> o = new ArrayList<T>();
    o.add(getRandomNode(g));
    if(len == 1) {
      return o;
    }
    for(int i=1;i<len;i++){
      o.add(getRandomNode(g, o.get(i-1)));
    }
    
    return o;
  }
  /**
   * May never halt...
   * 
   * @param g
   * @param len
   * @return
   */
  public static <T extends Comparable<? super T>> List<T> getRandomCircuit(
    DiGraph<T> g, int len){
    List<T> o = new ArrayList<T>();
    o.add(getRandomNode(g));
    if(len == 1) {
      return o;
    }
    for(int i=1;i<len;i++){
      o.add(getRandomNode(g, o.get(i-1)));
    }
    
    while(!g.isPredecessor(o.get(len-1), o.get(0))){
      o.remove(0);
      o.add(getRandomNode(g,o.get(len-2)));
    }
    return o;
  }

  /**
   * Iteratively removes the kernel of a copy of the graph either until the kernel is empty (graph
   * has cycle) or the graph is empty (graph has no cycle).
   * 
   * @return
   */
  public static <T extends Comparable<? super T>> boolean hasCycle(DiGraph<T> g0) {
    DiGraph<T> g = new DiGraph<T>(g0);
    TreeSet<T> t = new TreeSet<T>();

    while (g.getVertexCount() != 0) {
      t.clear();
      Iterator<T> it = g.getVertices().iterator();
      while (it.hasNext()) {
        T n = it.next();
        if (g.getPredecessorCount(n) == 0) {
          t.add(n);
        }
      }
      if (t.isEmpty() && (g.getVertexCount() != 0)) {
        return true;
      }
      for (T v : t) {
        g.removeVertex(v);
      }
    }
    return false;
  }

  public static <T extends Comparable<? super T>> boolean isStronglyConnected(DiGraph<T> g) {
    g.resetVertexMarkings();
    dfs(g, g.getVertices().iterator().next());
    boolean result = g.areVerticesAllMarked();
    g.resetVertexMarkings();
    return result;
  }

  public static <T extends Comparable<? super T>> ArrayList<DiGraph<T>> stronglyConnectedComponents(
      DiGraph<T> g) {
    g.resetVertexMarkings();
    ArrayList<DiGraph<T>> output = new ArrayList<DiGraph<T>>();
    TreeSet<T> m = new TreeSet<T>();

    while (true) {
      DiGraph<T> d = new DiGraph<T>();
      Iterator<T> i = g.getVertices().iterator();
      T root = null;
      while (i.hasNext()) {
        root = i.next();
        if (g.isVertexMarked(root)) {
          continue;
        } else {
          break;
        }
      }
      if (g.areVerticesAllMarked()) {
        break;
      }
      dfs(g, root);
      i = g.getVertices().iterator();

      while (i.hasNext()) {
        T x = i.next();
        if (!m.contains(x) && g.isVertexMarked(x)) {
          d.addVertex(x);
          m.add(x);
        }
      }

      i = d.getVertices().iterator();
      while (i.hasNext()) {
        T x = i.next();
        Iterator<T> j = g.getSuccessors(x).iterator();
        while (j.hasNext()) {
          T y = j.next();
          if (d.containsVertex(y)) {
            d.addEdge(x, y);
          }
        }
      }
      output.add(d);
    }
    return output;

  }
}
