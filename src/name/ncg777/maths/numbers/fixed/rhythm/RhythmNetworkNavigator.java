package name.ncg777.maths.numbers.fixed.rhythm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.jgrapht.graph.DefaultWeightedEdge;

import name.ncg777.maths.numbers.fixed.FixedLength;

/**
 * Probabilistic navigator over a {@link RhythmNetwork}.
 *
 * <h3>Transition probability model</h3>
 * <p>Each outgoing edge {@code a → b} carries a raw Boltzmann weight
 * {@code w = exp(−T · dist(a,b))}.  At navigation time these weights are transformed to
 * transition probabilities using a <em>navigation temperature</em> τ:
 *
 * <pre>
 *   p(a → b)  ∝  w(a,b)^(1/τ)
 * </pre>
 *
 * <ul>
 *   <li>τ = 1 → use raw weights directly (probabilities proportional to similarity).</li>
 *   <li>τ → ∞ → uniform distribution over reachable neighbours (maximum local entropy).</li>
 *   <li>τ → 0⁺ → deterministic, always follow the highest-weight edge.</li>
 * </ul>
 *
 * <p>Setting τ via {@link #setNavigationTemperature(double)} allows the user to trade
 * predictability for surprise independently of the graph-construction temperature.
 *
 * <h3>Usage</h3>
 * <pre>{@code
 *   RhythmNetworkNavigator nav = new RhythmNetworkNavigator(network);
 *   nav.setNavigationTemperature(2.0);   // spread the distribution
 *   List<FixedLength.Natural> walk = nav.walk(64);
 *   walk.forEach(r -> System.out.println(r));
 * }</pre>
 */
public final class RhythmNetworkNavigator {

  private final RhythmNetwork network;
  private double navTemperature = 1.0;  // τ
  private final Random random;

  /** Precomputed row-normalised transition distributions, keyed by source vertex. */
  private Map<FixedLength.Natural, List<double[]>> transitionMap; // double[0]=cumProb, [1]=targetIdx
  private List<FixedLength.Natural> vertexList;  // stable index

  public RhythmNetworkNavigator(RhythmNetwork network) {
    this(network, new Random());
  }

  public RhythmNetworkNavigator(RhythmNetwork network, long seed) {
    this(network, new Random(seed));
  }

  public RhythmNetworkNavigator(RhythmNetwork network, Random random) {
    this.network = network;
    this.random  = random;
    rebuildTransitions();
  }

  // ──────────────────────────────────────────────────── navigation temp ──

  /** Returns the current navigation temperature τ. */
  public double getNavigationTemperature() { return navTemperature; }

  /**
   * Sets the navigation temperature τ and rebuilds the transition distributions.
   *
   * @param tau  τ > 0; values close to 0 make navigation greedy, large values make it uniform
   */
  public void setNavigationTemperature(double tau) {
    if (tau <= 0) throw new IllegalArgumentException("Navigation temperature must be > 0");
    this.navTemperature = tau;
    rebuildTransitions();
  }

  // ─────────────────────────────────────────── transition-map building ──

  private void rebuildTransitions() {
    var g = network.getGraph();
    vertexList = new ArrayList<>(g.vertexSet());
    // Build vertex-to-index map for fast lookup
    Map<FixedLength.Natural, Integer> idx = new TreeMap<>();
    for (int i = 0; i < vertexList.size(); i++) idx.put(vertexList.get(i), i);

    transitionMap = new TreeMap<>();

    for (FixedLength.Natural src : vertexList) {
      Set<DefaultWeightedEdge> edges = g.outgoingEdgesOf(src);
      if (edges.isEmpty()) {
        transitionMap.put(src, List.of()); // sink node
        continue;
      }

      // Gather raw weights and apply navigation temperature
      List<int[]>    targets = new ArrayList<>();
      List<Double>   weights = new ArrayList<>();
      double           sum   = 0.0;

      for (DefaultWeightedEdge e : edges) {
        FixedLength.Natural tgt = g.getEdgeTarget(e);
        double w = Math.pow(g.getEdgeWeight(e), 1.0 / navTemperature);
        targets.add(new int[]{ idx.get(tgt) });
        weights.add(w);
        sum += w;
      }

      // Build cumulative distribution
      List<double[]> cdf = new ArrayList<>();
      double cumulative = 0.0;
      for (int i = 0; i < targets.size(); i++) {
        cumulative += weights.get(i) / sum;
        cdf.add(new double[]{ cumulative, targets.get(i)[0] });
      }
      // Ensure last entry reaches 1 to guard against floating-point under-shoot
      cdf.get(cdf.size() - 1)[0] = 1.0;

      transitionMap.put(src, cdf);
    }
  }

  // ─────────────────────────────────────────────────────────── sampling ──

  /** Samples the next rhythm reachable from {@code current}. */
  public FixedLength.Natural nextStep(FixedLength.Natural current) {
    List<double[]> cdf = transitionMap.get(current);
    if (cdf == null || cdf.isEmpty())
      throw new IllegalStateException("Vertex '" + current + "' is a sink — no outgoing edges.");
    double r = random.nextDouble();
    for (double[] entry : cdf) {
      if (r <= entry[0]) return vertexList.get((int) entry[1]);
    }
    // Fallback (should not happen)
    return vertexList.get((int) cdf.get(cdf.size() - 1)[1]);
  }

  /**
   * Generates a random walk starting from a uniformly-chosen vertex.
   *
   * @param steps total number of rhythm steps in the walk (including start)
   * @return the walk as an ordered list of rhythms
   */
  public List<FixedLength.Natural> walk(int steps) {
    if (vertexList.isEmpty()) return List.of();
    FixedLength.Natural start = vertexList.get(random.nextInt(vertexList.size()));
    return walk(start, steps);
  }

  /**
   * Generates a random walk starting from {@code start}.
   *
   * @param start the initial rhythm
   * @param steps total number of steps (including the start)
   * @return the walk as an ordered list of rhythms
   */
  public List<FixedLength.Natural> walk(FixedLength.Natural start, int steps) {
    List<FixedLength.Natural> path = new ArrayList<>(steps);
    FixedLength.Natural current = start;
    path.add(current);
    for (int i = 1; i < steps; i++) {
      current = nextStep(current);
      path.add(current);
    }
    return path;
  }

  /**
   * Returns the computed transition probability {@code p(from → to)}, using the current
   * navigation temperature.  Returns {@code 0} if no edge exists.
   */
  public double transitionProbability(FixedLength.Natural from, FixedLength.Natural to) {
    List<double[]> cdf = transitionMap.get(from);
    if (cdf == null || cdf.isEmpty()) return 0.0;
    // Recover individual probability from cumulative
    Map<Integer, Integer> toIdx = new TreeMap<>();
    int i2 = 0;
    for (FixedLength.Natural v : vertexList) { if (v.equals(to)) { toIdx.put(0, i2); break; } i2++; }
    if (toIdx.isEmpty()) return 0.0;
    int ti = toIdx.get(0);
    double prev = 0.0;
    for (double[] entry : cdf) {
      if ((int) entry[1] == ti) return entry[0] - prev;
      prev = entry[0];
    }
    return 0.0;
  }

  /** Exposes the vertex list in stable iteration order. */
  public List<FixedLength.Natural> getVertexList() {
    return new ArrayList<>(vertexList);
  }
}
