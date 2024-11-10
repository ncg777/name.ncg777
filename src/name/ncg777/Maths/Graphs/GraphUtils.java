package name.ncg777.Maths.Graphs;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import edu.uci.ics.jung.graph.DirectedGraph;
import name.ncg777.CS.DataStructures.CollectionUtils;


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
  
  public static <T extends Comparable<? super T>> void writeToCsv(DiGraph<T> g, String path) throws FileNotFoundException {
    PrintWriter pw = new PrintWriter(path);
    
    for(var x : g.getVertices()) {
      for(var y:g.getSuccessors(x)) {
        pw.println("\"" + x.toString()+ "\",\"" + y.toString() + "\"");
      }
    }
    pw.close();
  }
  
  
  
  
  /**
   * Calculates the betweenness centrality for all vertices in a directed graph.
   * 
   * Betweenness centrality is a measure of the extent to which a vertex lies on the shortest paths between other vertices.
   * It is calculated as the sum of the number of shortest paths between all pairs of vertices that pass through a given vertex,
   * divided by the total number of shortest paths between all pairs of vertices.
   * 
   * @param graph the directed graph for which to calculate betweenness centrality
   * @return a map where the keys are the vertices and the values are their corresponding betweenness centralities
   */
  public static Map<Object, Double> calculateBetweennessCentrality(DirectedGraph<Object, Object> graph) {
    Map<Object, Double> betweennessCentrality = new HashMap<>();

    // Initialize betweenness centrality for all vertices to 0
    for (Object vertex : graph.getVertices()) {
        betweennessCentrality.put(vertex, 0.0);
    }

    int n = graph.getVertexCount();
    int k = n - 1;

    // Iterate over all possible source and target vertices
    for (Object source : graph.getVertices()) {
        for (Object target : graph.getVertices()) {
            if (!source.equals(target)) {
                // Perform a breadth-first search to find all shortest paths from source to target
                List<List<Object>> shortestPaths = bfs(graph, source, target);

                // Calculate the betweenness centrality for all vertices in the shortest paths
                for (List<Object> path : shortestPaths) {
                    int pathLength = path.size();
                    for (int i = 0; i < pathLength; i++) {
                        Object vertex = path.get(i);
                        double centrality = betweennessCentrality.get(vertex);
                        if (i > 0 && i < pathLength - 1) {
                            centrality += 1.0 / shortestPaths.size();
                        }
                        betweennessCentrality.put(vertex, centrality);
                    }
                }
            }
        }
    }

    // Normalize the betweenness centrality values
    double normalizationFactor = 1.0 / ((n - k) * (n - k - 1));
    for (Object vertex : graph.getVertices()) {
        double centrality = betweennessCentrality.get(vertex);
        betweennessCentrality.put(vertex, centrality * normalizationFactor);
    }

    return betweennessCentrality;
  }
  
  /**
   * Performs a breadth-first search (BFS) in a directed graph to find all shortest paths from a source vertex to a target vertex.
   * 
   * This method uses a queue to explore the graph level by level, starting from the source vertex, until it reaches the target vertex.
   * All shortest paths from the source vertex to the target vertex are then reconstructed from the predecessor information.
   * 
   * @param graph the directed graph to search
   * @param source the source vertex to start the search from
   * @param target the target vertex to find shortest paths to
   * @return a list of lists, where each inner list represents a shortest path from the source vertex to the target vertex
   */
  private static List<List<Object>> bfs(DirectedGraph<Object, Object> graph, Object source, Object target) {
    List<List<Object>> shortestPaths = new ArrayList<>();
    Queue<Object> queue = new LinkedList<>();
    Set<Object> visited = new HashSet<>();
    Map<Object, Object> predecessors = new HashMap<>();

    queue.add(source);
    visited.add(source);

    while (!queue.isEmpty()) {
        Object currentVertex = queue.poll();
        if (currentVertex.equals(target)) {
            // Reconstruct the shortest paths
            List<Object> path = new ArrayList<>();
            Object vertex = currentVertex;
            while (vertex != null) {
                path.add(0, vertex);
                vertex = predecessors.get(vertex);
            }
            shortestPaths.add(path);
        } else {
            for (Object neighbor : graph.getSuccessors(currentVertex)) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                    predecessors.put(neighbor, currentVertex);
                }
            }
        }
    }

    return shortestPaths;
  }

  
  
  
  
}
