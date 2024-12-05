package name.ncg777.maths.objects.graphs;

public interface HasMarkableVertices<T> {
  public void markVertex(T u);

  public void unmarkVertex(T u);

  public boolean isVertexMarked(T u);

  public boolean areVerticesAllMarked();

  public void resetVertexMarkings();
}
