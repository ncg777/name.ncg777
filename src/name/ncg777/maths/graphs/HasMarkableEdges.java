package name.ncg777.maths.graphs;

public interface HasMarkableEdges<T> {
  public void markEdge(T u, T v);

  public void unmarkEdge(T u, T v);

  public boolean isEdgeMarked(T u, T v);

  public boolean areEdgesAllMarked();

  public void resetEdgeMarkings();
}
