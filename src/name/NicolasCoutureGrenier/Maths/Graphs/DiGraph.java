package name.NicolasCoutureGrenier.Maths.Graphs;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiPredicate;

import name.NicolasCoutureGrenier.Maths.Graphs.Interfaces.HasMarkableEdges;
import name.NicolasCoutureGrenier.Maths.Graphs.Interfaces.HasMarkableVertices;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class DiGraph<T> extends DirectedSparseGraph<T, Long>
    implements
      HasMarkableVertices<T>,
      HasMarkableEdges<T>, Serializable{

  private static final long serialVersionUID = 5641635441044219192L;
  protected long edge_seq = 1;

  private VertexMarker vertexMarker = new VertexMarker();

  @Override
  public void markVertex(T u) {
    vertexMarker.markVertex(u);
  }

  @Override
  public void unmarkVertex(T u) {
    vertexMarker.unmarkVertex(u);
  }

  @Override
  public boolean isVertexMarked(T u) {
    return vertexMarker.isVertexMarked(u);
  }

  @Override
  public boolean areVerticesAllMarked() {
    return vertexMarker.areVerticesAllMarked();
  }

  @Override
  public void resetVertexMarkings() {
    vertexMarker.resetVertexMarkings();
  }

  private EdgeMarker edgeMarker = new EdgeMarker();

  @Override
  public void resetEdgeMarkings() {
    edgeMarker.resetEdgeMarkings();
  }

  @Override
  public void markEdge(T u, T v) {
    edgeMarker.markEdge(u, v);
  }

  @Override
  public void unmarkEdge(T u, T v) {
    edgeMarker.unmarkEdge(u, v);
  }

  @Override
  public boolean isEdgeMarked(T u, T v) {
    return edgeMarker.isEdgeMarked(u, v);
  }

  @Override
  public boolean areEdgesAllMarked() {
    return edgeMarker.areEdgesAllMarked();
  }


  public DiGraph(DiGraph<T> g) {
    super();

    for (long p : g.getEdges()) {
      this.addEdge(p, g.getSource(p), g.getDest(p));
    }

    vertexMarker = new VertexMarker(g.vertexMarker);
    edgeMarker = new EdgeMarker(g.edgeMarker);
  }

  public DiGraph() {
    super();

  }
    
  public DiGraph(final Set<T> nodes, 
                 final BiPredicate<? super T, ? super T> relation) {
    for (T a : nodes) {
      addVertex(a);
    }
    
    for (final T a : this.getVertices()) {
      for (final T b : this.getVertices()) {
        if (relation.test(a,b)) {
          addEdge(a, b);
        }
      }
    }
  }

  @Override
  public boolean addVertex(T vertex) {
    boolean b = super.addVertex(vertex);
    vertexMarker.unmarkVertex(vertex);
    return b;
  }

  @Override
  public boolean removeVertex(T arg0) {
    boolean b = super.removeVertex(arg0);
    vertexMarker.deleteMark(arg0);
    return b;
  }

  public boolean removeEdge(T v1, T v2) {
    edgeMarker.deleteMark(v1, v2);
    return super.removeEdge(findEdge(v1, v2));

  }

  public boolean addEdge(T v1, T v2) {
    edgeMarker.unmarkEdge(v1, v2);
    return super.addEdge(edge_seq++, v1, v2);
  }

  /**
   * A utility class to mark vertices in a DiGraph.
   * 
   * @param <T>
   */
  private class VertexMarker implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -788353072774016606L;
    private HashMap<T, Boolean> marked = new HashMap<T, Boolean>();

    public VertexMarker() {

    }

    /**
     * Performs a deep copy.
     * 
     * @param n
     */
    public VertexMarker(VertexMarker n) {
      marked = new HashMap<T, Boolean>();

      Iterator<Entry<T, Boolean>> it = n.marked.entrySet().iterator();
      while (it.hasNext()) {
        Entry<T, Boolean> e = it.next();
        marked.put(e.getKey(), e.getValue().booleanValue());
      }
    }

    public void deleteMark(T n) {
      if (marked.containsKey(n)) {
        marked.remove(n);
      }
    }

    public void markVertex(T u) {
      marked.put(u, true);
    }

    public void unmarkVertex(T u) {
      marked.put(u, false);
    }

    public boolean isVertexMarked(T u) {
      return marked.get(u).booleanValue();
    }

    public boolean areVerticesAllMarked() {
      return !marked.containsValue(Boolean.FALSE);
    }

    public void resetVertexMarkings() {
      for (T n : marked.keySet()) {
        marked.put(n, false);
      }
    }

  }

  /**
   * A utility class to mark edges in a DiGraph.
   * 
   * @param <T>
   */
  private class EdgeMarker  implements Serializable  {

    /**
     * 
     */
    private static final long serialVersionUID = -7783022745808575408L;
    private HashMap<Long, Boolean> m_Marked;


    public EdgeMarker() {
      m_Marked = new HashMap<Long, Boolean>();
    }


    public EdgeMarker(EdgeMarker e) {
      m_Marked = new HashMap<Long, Boolean>();

      Iterator<Entry<Long, Boolean>> it = e.m_Marked.entrySet().iterator();
      while (it.hasNext()) {
        Entry<Long, Boolean> en = it.next();
        m_Marked.put(en.getKey(), en.getValue().booleanValue());
      }
    }

    public void deleteMark(T u, T v) {
      Long l = findEdge(u, v);
      if (l != null && m_Marked.containsKey(l)) {
        m_Marked.remove(l);
      }
    }


    public void markEdge(T u, T v) {
      m_Marked.put(findEdge(u, v), true);
    }

    public void unmarkEdge(T u, T v) {
      m_Marked.put(findEdge(u, v), false);
    }

    public boolean isEdgeMarked(T u, T v) {
      Long p = findEdge(u, v);
      if (p != null && !m_Marked.containsKey(p)) {
        m_Marked.put(p, false);
      }
      return m_Marked.get(p);
    }

    public boolean areEdgesAllMarked() {
      return !m_Marked.containsValue(false);

    }

    public void resetEdgeMarkings() {
      for (Long p : m_Marked.keySet()) {
        m_Marked.put(p, false);
      }
    }

  }
}
