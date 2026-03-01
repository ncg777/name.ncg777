package name.ncg777.maths.numbers.fixed.rhythm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.numbers.fixed.FixedLength;
import name.ncg777.maths.numbers.relations.PredicatedJuxtaposition;

/**
 * A weighted directed graph whose vertices are {@link FixedLength.Natural} rhythm patterns and
 * whose directed edges represent allowed transitions between rhythms.
 *
 * <h3>Construction</h3>
 * <ol>
 *   <li>All rhythms of length {@code L} over {@code cipher} are enumerated.</li>
 *   <li>Those that satisfy the <em>vertex clause</em> predicate are kept as vertices.</li>
 *   <li>For each ordered pair {@code (a, b)} of distinct vertices:
 *     <ul>
 *       <li>If the <em>edge (juxtaposition) predicate</em> is satisfied for the pair, a directed
 *           edge {@code a → b} is added.</li>
 *       <li>The raw edge weight is computed as
 *           {@code exp(-temperature × distance(a, b))}, where {@code distance} lies in [0,1].</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <p>The raw weights are stored on the JGraphT edges.  The {@link RhythmNetworkNavigator} converts
 * them to row-normalised transition probabilities at walk time.
 *
 * <h3>Juxtaposition predicate</h3>
 * <p>The optional juxtaposition clause is parsed as a {@link RhythmClauseParser} clause applied
 * to the <em>concatenation</em> of two rhythms (using {@link PredicatedJuxtaposition}).  Pass
 * {@code null} or {@code ""} to allow all edges.
 *
 * <h3>Example</h3>
 * <pre>{@code
 *   RhythmNetwork net = new RhythmNetwork.Builder()
 *       .length(4)
 *       .cipher(Cipher.Name.Hexadecimal)
 *       .vertexClause("EVEN AND NOT PERIODIC")
 *       .juxtapositionClause("HAS_NO_GAPS")
 *       .distance(RhythmDistance.hamming())
 *       .temperature(1.5)
 *       .build();
 * }</pre>
 */
public final class RhythmNetwork implements Serializable {

  private static final long serialVersionUID = 1L;

  private final DefaultDirectedWeightedGraph<FixedLength.Natural, DefaultWeightedEdge> graph;
  private final int L;
  private final Cipher.Name cipher;
  private final double temperature;
  private final String vertexClause;
  private final String juxtClause;

  private RhythmNetwork(
      DefaultDirectedWeightedGraph<FixedLength.Natural, DefaultWeightedEdge> graph,
      int L,
      Cipher.Name cipher,
      double temperature,
      String vertexClause,
      String juxtClause) {
    this.graph = graph;
    this.L = L;
    this.cipher = cipher;
    this.temperature = temperature;
    this.vertexClause = vertexClause != null ? vertexClause : "";
    this.juxtClause = juxtClause != null ? juxtClause : "";
  }

  // ───────────────────────────────────────── serialisation ──────────────

  /**
   * Serialises this network to a file using Java object serialisation.
   * Reload with {@link #load(Path)}.
   */
  public void save(Path path) throws IOException {
    try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
      oos.writeObject(this);
    }
  }

  /**
   * Deserialises a {@link RhythmNetwork} previously saved with {@link #save(Path)}.
   */
  public static RhythmNetwork load(Path path) throws IOException, ClassNotFoundException {
    try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
      return (RhythmNetwork) ois.readObject();
    }
  }

  // ─────────────────────────────────────────────────────────── accessors ──

  /** Returns the underlying JGraphT directed weighted graph (read-only view recommended). */
  public DefaultDirectedWeightedGraph<FixedLength.Natural, DefaultWeightedEdge> getGraph() {
    return graph;
  }

  /** Returns the rhythm length (number of cipher symbols). */
  public int getL() { return L; }

  /** Returns the cipher used to represent rhythms. */
  public Cipher.Name getCipher() { return cipher; }

  /** Returns the Boltzmann temperature used to compute edge weights. */
  public double getTemperature() { return temperature; }

  /** Returns the vertex-filter clause string (empty string means accept all). */
  public String getVertexClause() { return vertexClause; }

  /** Returns the juxtaposition-filter clause string (empty string means accept all pairs). */
  public String getJuxtClause() { return juxtClause; }

  /** Returns an unmodifiable sorted set of all vertex rhythms. */
  public Set<FixedLength.Natural> getVertices() {
    return Collections.unmodifiableSet(new TreeSet<>(graph.vertexSet()));
  }

  /** Returns the number of vertices (rhythm nodes). */
  public int vertexCount() { return graph.vertexSet().size(); }

  /** Returns the total number of directed edges. */
  public int edgeCount() { return graph.edgeSet().size(); }

  /**
   * Returns the raw (unnormalised) weight of edge {@code a → b}, or {@code 0} if no such edge
   * exists.
   */
  public double getRawWeight(FixedLength.Natural a, FixedLength.Natural b) {
    DefaultWeightedEdge e = graph.getEdge(a, b);
    return e == null ? 0.0 : graph.getEdgeWeight(e);
  }

  // ──────────────────────────────────────────────────────────── builder ──

  /** Fluent builder for {@link RhythmNetwork}. */
  public static final class Builder {

    private int    L            = 4;
    private Cipher.Name cipher  = Cipher.Name.Hexadecimal;
    private String vertexClause = "";        // empty → accept all
    private String juxtClause   = "";        // empty → accept all pairs
    private RhythmDistance distance = RhythmDistance.hamming();
    private double temperature  = 1.0;
    private boolean selfLoops   = false;

    public Builder length(int L)                       { this.L = L; return this; }
    public Builder cipher(Cipher.Name c)               { this.cipher = c; return this; }
    public Builder vertexClause(String clause)         { this.vertexClause = clause != null ? clause : ""; return this; }
    public Builder juxtapositionClause(String clause)  { this.juxtClause = clause != null ? clause : ""; return this; }
    public Builder distance(RhythmDistance d)          { this.distance = d; return this; }
    public Builder temperature(double t)               { this.temperature = t; return this; }
    public Builder selfLoops(boolean v)                { this.selfLoops = v; return this; }

    /**
     * Builds the {@link RhythmNetwork} by enumerating, filtering, and connecting all rhythms.
     *
     * <p>This operation can be expensive for large {@code L} or rich ciphers: the number of
     * candidates is {@code |cipher|^L}.
     *
     * <p>Progress and timing information are written to {@code System.err}.
     */
    public RhythmNetwork build() {
      long t0 = System.nanoTime();

      // ── 1. compile predicates ──────────────────────────────────────────
      System.err.print("  Compiling predicates…");
      Predicate<BinaryNatural> vertexFilter = RhythmClauseParser.parse(vertexClause);

      BiPredicate<BinaryNatural, BinaryNatural> edgeFilter;
      if (juxtClause == null || juxtClause.isBlank()) {
        edgeFilter = (a, b) -> true;
      } else {
        Predicate<BinaryNatural> juxtPred = RhythmClauseParser.parse(juxtClause);
        PredicatedJuxtaposition pj = new PredicatedJuxtaposition(juxtPred);
        edgeFilter = pj::test;
      }
      System.err.printf(" done (%.0f ms)%n", (System.nanoTime() - t0) / 1e6);

      // ── 2. generate & filter vertices ─────────────────────────────────
      long t1 = System.nanoTime();
      var allCandidates = FixedLength.generate(L, cipher);
      System.err.printf("  Filtering vertices (%,d candidates)…", allCandidates.size());

      List<FixedLength.Natural> vertices = allCandidates.stream()
          .filter(r -> vertexFilter.test(r.toBinaryNatural()))
          .toList();

      System.err.printf(" %,d passed (%.0f ms)%n",
          vertices.size(), (System.nanoTime() - t1) / 1e6);

      // ── 3. precompute BinaryNatural representations ────────────────────
      BinaryNatural[] binaryCache = new BinaryNatural[vertices.size()];
      for (int i = 0; i < vertices.size(); i++) {
        binaryCache[i] = vertices.get(i).toBinaryNatural();
      }

      // ── 4. build graph ────────────────────────────────────────────────
      long t2  = System.nanoTime();
      long totalPairs = (long) vertices.size() * vertices.size();
      System.err.printf("  Building edges (%,d vertex pairs to evaluate)…%n", totalPairs);

      DefaultDirectedWeightedGraph<FixedLength.Natural, DefaultWeightedEdge> g =
          new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);

      for (FixedLength.Natural v : vertices) g.addVertex(v);

      int edgesAdded  = 0;
      int outerDone   = 0;
      int progressPct = -1;
      int vSize       = vertices.size();

      for (int ai = 0; ai < vSize; ai++) {
        FixedLength.Natural a = vertices.get(ai);
        BinaryNatural ba = binaryCache[ai];
        for (int bi = 0; bi < vSize; bi++) {
          if (!selfLoops && ai == bi) continue;
          if (edgeFilter.test(ba, binaryCache[bi])) {
            double dist   = distance.distance(ba, binaryCache[bi]);
            double weight = Math.exp(-temperature * dist);
            DefaultWeightedEdge edge = g.addEdge(a, vertices.get(bi));
            if (edge != null) { g.setEdgeWeight(edge, weight); edgesAdded++; }
          }
        }
        outerDone++;
        int pct = (int) (100L * outerDone / vSize);
        if (pct > progressPct && pct % 10 == 0) {
          progressPct = pct;
          long elapsed = System.nanoTime() - t2;
          double etaSec = (elapsed / 1e9) * (vSize - outerDone) / outerDone;
          System.err.printf("    %3d%% (%,d edges so far, ETA %.1f s)%n",
              pct, edgesAdded, etaSec);
        }
      }

      long buildMs = (long) ((System.nanoTime() - t2) / 1e6);
      long totalMs = (long) ((System.nanoTime() - t0) / 1e6);
      System.err.printf("  Edge building: %,d edges in %,d ms%n", edgesAdded, buildMs);
      System.err.printf("  Total build time: %,d ms%n", totalMs);

      return new RhythmNetwork(g, L, cipher, temperature, vertexClause, juxtClause);
    }
  }
}
