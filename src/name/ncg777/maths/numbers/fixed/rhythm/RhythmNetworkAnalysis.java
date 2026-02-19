package name.ncg777.maths.numbers.fixed.rhythm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;

import name.ncg777.maths.numbers.fixed.FixedLength;

/**
 * Information-theoretic and structural quality metrics for a {@link RhythmNetwork}.
 *
 * <h3>Metrics</h3>
 * <dl>
 *   <dt>{@link #isStronglyConnected()}</dt>
 *   <dd>True when every rhythm can eventually reach every other rhythm.</dd>
 *
 *   <dt>{@link #stronglyConnectedComponentCount()}</dt>
 *   <dd>Number of SCCs; ideal value is 1.</dd>
 *
 *   <dt>{@link #averageTransitionEntropy()}</dt>
 *   <dd>Average Shannon entropy (nats) of the per-row transition probability distribution.
 *   Higher values mean the walk is more <em>surprising</em> at each step.  Maximum is
 *   {@code ln(d)} where {@code d} is the mean out-degree.</dd>
 *
 *   <dt>{@link #stationaryEntropy()}</dt>
 *   <dd>Entropy of the stationary distribution π (computed via power iteration).
 *   Higher values mean the walk visits all rhythms more uniformly over long runs.</dd>
 *
 *   <dt>{@link #spectralGap()}</dt>
 *   <dd>Approximate spectral gap (1 − λ₂).  Larger values indicate faster mixing (shorter
 *   expected "memory" of the random walk).</dd>
 *
 *   <dt>{@link #averageOutDegree()}</dt>
 *   <dd>Mean number of successors per vertex.</dd>
 *
 *   <dt>{@link #summary(RhythmNetworkNavigator)}</dt>
 *   <dd>Human-readable report using the current navigation temperature.</dd>
 * </dl>
 */
public final class RhythmNetworkAnalysis {

  private static final int POWER_ITERS    = 2000;
  private static final double EPSILON     = 1e-10;

  private final RhythmNetwork              network;
  private final List<FixedLength.Natural>  vertices;
  private final int                        n;
  // Transition matrix P[i][j] = p(i→j), row-normalised from raw weights
  private final double[][]                 P;

  // Cached results (computed lazily, at most once)
  private Boolean                          cachedStronglyConnected;
  private Integer                          cachedSCCCount;
  private double[]                         cachedStationaryDist;
  private Double                           cachedSpectralGap;
  private boolean                          verbose = true;

  public RhythmNetworkAnalysis(RhythmNetwork network) {
    this(network, 1.0);
  }

  /**
   * @param navigationTemperature  τ used to build transition probabilities (same semantics as
   *                               {@link RhythmNetworkNavigator#setNavigationTemperature(double)})
   */
  public RhythmNetworkAnalysis(RhythmNetwork network, double navigationTemperature) {
    this.network  = network;
    this.vertices = new ArrayList<>(network.getGraph().vertexSet());
    this.n        = vertices.size();
    long t0 = System.nanoTime();
    this.P        = buildTransitionMatrix(navigationTemperature);
    if (verbose && n > 50)
      System.err.printf("  Transition matrix (%dx%d): %.0f ms%n", n, n,
          (System.nanoTime() - t0) / 1e6);
  }

  /** When false, suppresses all stderr progress messages (useful for batch/pipeline mode). */
  public void setVerbose(boolean v) { this.verbose = v; }

  // ──────────────────────────────────────── transition-matrix builder ──

  private double[][] buildTransitionMatrix(double tau) {
    var g = network.getGraph();
    // O(1) vertex-to-index lookup instead of O(n) indexOf
    Map<FixedLength.Natural, Integer> idxMap = new HashMap<>(n * 2);
    for (int i = 0; i < n; i++) idxMap.put(vertices.get(i), i);

    double[][] P = new double[n][n];
    for (int i = 0; i < n; i++) {
      FixedLength.Natural src = vertices.get(i);
      Set<DefaultWeightedEdge> edges = g.outgoingEdgesOf(src);
      double sum = 0.0;
      double[] row = new double[n];
      for (DefaultWeightedEdge e : edges) {
        FixedLength.Natural tgt = g.getEdgeTarget(e);
        int j = idxMap.get(tgt);
        double w = Math.pow(g.getEdgeWeight(e), 1.0 / tau);
        row[j] = w;
        sum   += w;
      }
      if (sum > 0) for (int j = 0; j < n; j++) P[i][j] = row[j] / sum;
      // else: row stays zero → sink node
    }
    return P;
  }

  // ──────────────────────────────────────────── structural metrics ──────

  /** Returns {@code true} if the graph has exactly one strongly connected component. */
  public boolean isStronglyConnected() {
    if (cachedStronglyConnected == null) computeSCC();
    return cachedStronglyConnected;
  }

  /** Returns the number of strongly connected components. */
  public int stronglyConnectedComponentCount() {
    if (cachedSCCCount == null) computeSCC();
    return cachedSCCCount;
  }

  private void computeSCC() {
    long t0 = System.nanoTime();
    if (verbose) System.err.print("  Computing SCC…");
    var inspector = new KosarajuStrongConnectivityInspector<>(network.getGraph());
    cachedStronglyConnected = inspector.isStronglyConnected();
    cachedSCCCount = inspector.stronglyConnectedSets().size();
    if (verbose) System.err.printf(" %d components (%.0f ms)%n",
        cachedSCCCount, (System.nanoTime() - t0) / 1e6);
  }

  /** Returns the mean out-degree of all vertices. */
  public double averageOutDegree() {
    if (n == 0) return 0.0;
    var g = network.getGraph();
    double sum = 0;
    for (FixedLength.Natural v : vertices) sum += g.outDegreeOf(v);
    return sum / n;
  }

  // ──────────────────────────────────────── information-theory metrics ──

  /**
   * Computes the average Shannon entropy (in nats) across all rows of the transition matrix.
   *
   * <p>This quantifies the average local "surprise" at each navigation step.
   */
  public double averageTransitionEntropy() {
    if (n == 0) return 0.0;
    double total = 0.0;
    int    count = 0;
    for (int i = 0; i < n; i++) {
      double h = rowEntropy(P[i]);
      // Only include non-sink rows
      double rowSum = Arrays.stream(P[i]).sum();
      if (rowSum > EPSILON) { total += h; count++; }
    }
    return count == 0 ? 0.0 : total / count;
  }

  /**
   * Computes the entropy (in nats) of the stationary distribution π.
   *
   * <p>Maximum is {@code ln(n)} (uniform visitation of all n rhythms).
   */
  public double stationaryEntropy() {
    double[] pi = stationaryDistribution();
    return rowEntropy(pi);
  }

  /**
   * Returns the stationary distribution π via power iteration.
   *
   * <p>Convergence is declared when the L1 change between iterations drops below 1e-10.
   * The result is cached for reuse by {@link #stationaryEntropy()} and {@link #spectralGap()}.
   */
  public double[] stationaryDistribution() {
    if (cachedStationaryDist != null) return cachedStationaryDist;
    if (n == 0) { cachedStationaryDist = new double[0]; return cachedStationaryDist; }

    long t0 = System.nanoTime();
    if (verbose) System.err.print("  Power iteration (stationary dist)…");

    double[] v = new double[n];
    Arrays.fill(v, 1.0 / n);  // start from uniform

    int convergedAt = POWER_ITERS;
    for (int iter = 0; iter < POWER_ITERS; iter++) {
      double[] vNext = new double[n];
      // v_next[j] = Σ_i v[i] * P[i][j]
      for (int i = 0; i < n; i++)
        for (int j = 0; j < n; j++)
          vNext[j] += v[i] * P[i][j];
      // Re-normalise (guards against sink accumulation)
      double sum = Arrays.stream(vNext).sum();
      if (sum > EPSILON) for (int j = 0; j < n; j++) vNext[j] /= sum;
      // Convergence check
      double l1 = 0;
      for (int j = 0; j < n; j++) l1 += Math.abs(vNext[j] - v[j]);
      v = vNext;
      if (l1 < EPSILON) { convergedAt = iter + 1; break; }
    }
    if (verbose) System.err.printf(" converged in %d iters (%.0f ms)%n",
        convergedAt, (System.nanoTime() - t0) / 1e6);

    cachedStationaryDist = v;
    return cachedStationaryDist;
  }

  /**
   * Estimates the spectral gap (1 − λ₂) of the transition matrix via power iteration on a
   * deflated matrix.
   *
   * <p>The spectral gap controls the mixing time of the Markov chain: a larger gap means
   * faster convergence to the stationary distribution (shorter "memory").  A value near 1 is
   * ideal; near 0 indicates very slow mixing.
   *
   * <p>The estimate is computed by measuring the geometric convergence rate of the deviation
   * from stationarity over multiple power-iteration steps.
   */
  public double spectralGap() {
    if (cachedSpectralGap != null) return cachedSpectralGap;
    if (n <= 1) { cachedSpectralGap = 1.0; return 1.0; }

    long t0 = System.nanoTime();
    if (verbose) System.err.print("  Spectral gap (deflated power iteration)…");

    double[] pi = stationaryDistribution();

    // Start with a random perturbation orthogonal to the stationary distribution
    double[] v = new double[n];
    for (int i = 0; i < n; i++) v[i] = (i % 2 == 0 ? 1.0 : -1.0) / n;
    // Project out the stationary component
    v = deflate(v, pi);

    double prevNorm = l2norm(v);
    if (prevNorm < EPSILON) {
      cachedSpectralGap = 1.0;
      if (verbose) System.err.printf(" 1.0000 (trivial, %.0f ms)%n", (System.nanoTime() - t0) / 1e6);
      return 1.0;
    }
    for (int k = 0; k < n; k++) v[k] /= prevNorm;

    double lambdaEst = 0.0;
    int    samples   = 0;
    final  int BURN  = 20;
    final  int MEAS  = 100;

    for (int iter = 0; iter < BURN + MEAS; iter++) {
      double[] vNext = matVec(P, v);
      vNext = deflate(vNext, pi);
      double norm = l2norm(vNext);
      if (norm < EPSILON) { lambdaEst = 0.0; break; }
      if (iter >= BURN) { lambdaEst += norm / l2norm(v); samples++; }
      for (int k = 0; k < n; k++) vNext[k] /= norm;
      v = vNext;
    }
    double lambda2 = (samples > 0) ? (lambdaEst / samples) : 0.0;
    lambda2 = Math.max(0.0, Math.min(1.0, lambda2));
    cachedSpectralGap = 1.0 - lambda2;
    if (verbose) System.err.printf(" %.4f (%.0f ms)%n",
        cachedSpectralGap, (System.nanoTime() - t0) / 1e6);
    return cachedSpectralGap;
  }

  // ──────────────────────────────────────────────────── summary report ──

  /**
   * Returns a human-readable summary of the network's quality metrics, using rthe navigation
   * temperature embedded in {@code navigator}.
   */
  public String summary(RhythmNetworkNavigator navigator) {
    RhythmNetworkAnalysis a = new RhythmNetworkAnalysis(
        network, navigator.getNavigationTemperature());
    return a.summary();
  }

  /** Returns a summary using the default (τ = 1) transition probabilities. */
  public String summary() {
    StringBuilder sb = new StringBuilder();
    sb.append("=== RhythmNetwork Analysis ===\n");
    sb.append(String.format("  Vertices              : %d%n",  network.vertexCount()));
    sb.append(String.format("  Edges                 : %d%n",  network.edgeCount()));
    sb.append(String.format("  Strongly connected    : %b%n",  isStronglyConnected()));
    sb.append(String.format("  SCC count             : %d%n",  stronglyConnectedComponentCount()));
    sb.append(String.format("  Avg out-degree        : %.2f%n", averageOutDegree()));
    sb.append(String.format("  Avg transition entropy: %.4f nats%n", averageTransitionEntropy()));
    sb.append(String.format("  Stationary entropy    : %.4f nats  (max ln(%d)=%.4f)%n",
        stationaryEntropy(), n, n > 0 ? Math.log(n) : 0.0));
    sb.append(String.format("  Spectral gap (est.)   : %.4f%n", spectralGap()));
    return sb.toString();
  }

  // ────────────────────────────────────────────────────── utilities ─────

  private static double rowEntropy(double[] row) {
    double h = 0.0;
    for (double p : row) if (p > EPSILON) h -= p * Math.log(p);
    return h;
  }

  private static double l2norm(double[] v) {
    double s = 0;
    for (double x : v) s += x * x;
    return Math.sqrt(s);
  }

  /** Multiply: result[j] = Σ_i v[i] * P[i][j]  */
  private static double[] matVec(double[][] P, double[] v) {
    int n = v.length;
    double[] r = new double[n];
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++)
        r[j] += v[i] * P[i][j];
    return r;
  }

  /** Project out the stationary-distribution component: v ← v − (v·π)·1̂  */
  private static double[] deflate(double[] v, double[] pi) {
    double dot = 0;
    for (int i = 0; i < v.length; i++) dot += v[i] * pi[i];
    double[] r = new double[v.length];
    for (int i = 0; i < v.length; i++) r[i] = v[i] - dot;
    return r;
  }
}
