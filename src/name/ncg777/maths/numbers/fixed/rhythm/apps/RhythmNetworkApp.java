package name.ncg777.maths.numbers.fixed.rhythm.apps;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.numbers.fixed.FixedLength;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmDistance;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmNetwork;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmNetworkAnalysis;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmNetworkNavigator;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmPredicateRegistry;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command-line application for building, navigating, and analysing rhythm networks.
 *
 * <h3>Quick start</h3>
 * <pre>
 *   # Walk 32 hex rhythms of length 4, filtered by EVEN AND NOT PERIODIC
 *   java ... RhythmNetworkApp -L 4 -c Hexadecimal --vertex "EVEN AND NOT PERIODIC" --steps 32
 *
 *   # Same but restrict transitions via juxtaposition and show analysis
 *   java ... RhythmNetworkApp \
 *       -L 4 -c Hexadecimal \
 *       --vertex "EVEN AND COPRIME_INTERVALS" \
 *       --juxt "HAS_NO_GAPS" \
 *       --distance HAMMING --temp 1.5 --nav-temp 2.0 \
 *       --steps 64 --analyze
 *
 *   # List all available predicate names
 *   java ... RhythmNetworkApp --list-predicates
 * </pre>
 */
@Command(
  name              = "rhythm-network",
  mixinStandardHelpOptions = true,
  description       = {
    "Build a rhythm network and generate a probabilistic walk.",
    "",
    "Vertices are FixedLength.Natural rhythms of length L over the chosen cipher.",
    "The vertex-clause and juxtaposition-clause predicates are Boolean expressions",
    "built from the names shown by --list-predicates."
  }
)
public final class RhythmNetworkApp implements Callable<Integer> {

  // ─────────────────────────────────────────────────── CLI parameters ──

  @Option(names = {"-L", "--length"}, defaultValue = "4",
      description = "Number of cipher symbols per rhythm (default: 4)")
  private int length;

  @Option(names = {"-c", "--cipher"}, defaultValue = "Hexadecimal",
      description = "Cipher name: Binary, Octal, Hexadecimal, … (default: Hexadecimal)")
  private Cipher.Name cipher;

  @Option(names = {"--vertex", "--vertex-clause"}, defaultValue = "",
      description = "Boolean predicate clause for vertex filtering (empty = accept all)."
                  + " E.g. \"EVEN AND NOT PERIODIC\"")
  private String vertexClause;

  @Option(names = {"--juxt", "--juxt-clause"}, defaultValue = "",
      description = "Boolean predicate applied to rhythm juxtapositions for edge filtering."
                  + " E.g. \"HAS_NO_GAPS\" (empty = all pairs connected).")
  private String juxtClause;

  @Option(names = {"-d", "--distance"}, defaultValue = "HAMMING",
      description = "Distance measure: HAMMING, JACCARD, INTERVAL_VECTOR (default: HAMMING)")
  private String distanceName;

  @Option(names = {"-T", "--temp"}, defaultValue = "1.0",
      description = "Construction temperature T (Boltzmann weight = exp(-T·dist)). Default: 1.0")
  private double temperature;

  @Option(names = {"-t", "--nav-temp"}, defaultValue = "1.0",
      description = "Navigation temperature τ (w^(1/τ) reweighting). "
                  + "τ→∞ = uniform; τ→0 = greedy. Default: 1.0")
  private double navTemperature;

  @Option(names = {"-s", "--steps"}, defaultValue = "32",
      description = "Number of steps in the generated walk (default: 32)")
  private int steps;

  @Option(names = {"--seed"}, defaultValue = "-1",
      description = "Random seed (-1 = random, default: -1)")
  private long seed;

  @Option(names = {"-a", "--analyze"},
      description = "Print information-theoretic quality analysis after the walk")
  private boolean analyze;

  @Option(names = {"--list-predicates"},
      description = "Print all available predicate names and exit")
  private boolean listPredicates;

  @Option(names = {"--no-walk"},
      description = "Skip walk generation (useful with --analyze)")
  private boolean noWalk;

  @Option(names = {"--self-loops"},
      description = "Allow self-loop edges in the network (default: false)")
  private boolean selfLoops;

  @Option(names = {"--save"},
      description = "Save the built network to this file path (.rnet)")
  private String savePath;

  @Option(names = {"--load"},
      description = "Load a previously saved network instead of building from scratch")
  private String loadPath;

  // ────────────────────────────────────────────────── entry points ──────

  public static void main(String[] args) {
    System.exit(new CommandLine(new RhythmNetworkApp()).execute(args));
  }

  @Override
  public Integer call() {
    long t0 = System.nanoTime();

    if (listPredicates) {
      System.out.println("Available predicate names:");
      RhythmPredicateRegistry.names().stream().sorted()
          .forEach(n -> System.out.println("  " + n));
      System.out.println("  MINIMUM_GAP(n)   – min interval gap ≥ n");
      System.out.println("  MAXIMUM_GAP(n)   – max interval gap ≤ n");
      System.out.println("  ORDINAL(n)       – ordinal rhythm of period n");
      System.out.println("  TRUE / ALL       – accept everything");
      System.out.println("  FALSE / NONE     – reject everything");
      return 0;
    }

    // ── 1. parse distance ───────────────────────────────────────────────
    RhythmDistance dist;
    try {
      dist = RhythmDistance.parse(distanceName);
    } catch (IllegalArgumentException e) {
      System.err.println("ERROR: " + e.getMessage());
      return 1;
    }

    // ── 2. build or load network ────────────────────────────────────────
    RhythmNetwork network;
    if (loadPath != null && !loadPath.isBlank()) {
      long tLoad = System.nanoTime();
      System.err.println("Loading network from " + loadPath + "…");
      try {
        network = RhythmNetwork.load(Path.of(loadPath));
        System.err.printf("Loaded: %,d vertices, %,d edges (%.0f ms)%n",
            network.vertexCount(), network.edgeCount(),
            (System.nanoTime() - tLoad) / 1e6);
      } catch (Exception e) {
        System.err.println("ERROR loading network: " + e.getMessage());
        return 2;
      }
    } else {
      System.err.println("Building rhythm network…");
      System.err.printf("  Parameters: L=%d, cipher=%s, T=%.2f%n", length, cipher, temperature);
      System.err.printf("  Vertex clause: \"%s\"%n", vertexClause);
      System.err.printf("  Juxt clause:   \"%s\"%n", juxtClause);
      System.err.printf("  Distance:      %s%n", distanceName);

      try {
        network = new RhythmNetwork.Builder()
            .length(length)
            .cipher(cipher)
            .vertexClause(vertexClause)
            .juxtapositionClause(juxtClause)
            .distance(dist)
            .temperature(temperature)
            .selfLoops(selfLoops)
            .build();
      } catch (Exception e) {
        System.err.println("ERROR building network: " + e.getMessage());
        e.printStackTrace(System.err);
        return 2;
      }
      System.err.printf("Network ready: %,d vertices, %,d edges%n",
          network.vertexCount(), network.edgeCount());
    }

    if (savePath != null && !savePath.isBlank()) {
      try {
        long tSave = System.nanoTime();
        network.save(Path.of(savePath));
        System.err.printf("Network saved to %s (%.0f ms)%n",
            savePath, (System.nanoTime() - tSave) / 1e6);
      } catch (Exception e) {
        System.err.println("ERROR saving network: " + e.getMessage());
      }
    }

    if (network.vertexCount() == 0) {
      System.err.println("WARNING: No vertices passed the filter — nothing to do.");
      return 0;
    }
    if (network.edgeCount() == 0) {
      System.err.println("WARNING: No edges in the network — walk impossible.");
    }

    // ── 3. optional analysis ────────────────────────────────────────────
    if (analyze) {
      System.err.println("Running analysis (τ=" + navTemperature + ")…");
      RhythmNetworkAnalysis analysis = new RhythmNetworkAnalysis(network, navTemperature);
      System.out.println(analysis.summary());
    }

    // ── 4. generate walk ─────────────────────────────────────────────────
    if (!noWalk && network.edgeCount() > 0) {
      System.err.printf("Generating walk: %,d steps, τ=%.2f%n", steps, navTemperature);
      long tWalk = System.nanoTime();

      RhythmNetworkNavigator nav = seed < 0
          ? new RhythmNetworkNavigator(network)
          : new RhythmNetworkNavigator(network, seed);
      nav.setNavigationTemperature(navTemperature);

      List<FixedLength.Natural> walk = nav.walk(steps);
      System.out.println("# walk (τ=" + navTemperature + ", T=" + temperature + ")");
      String walkStr = walk.stream()
          .map(FixedLength.Natural::toString)
          .collect(java.util.stream.Collectors.joining(" "));
      System.out.println(walkStr);

      System.err.printf("Walk generated in %.0f ms%n", (System.nanoTime() - tWalk) / 1e6);
    }

    long totalMs = (long) ((System.nanoTime() - t0) / 1e6);
    System.err.printf("Total elapsed: %,d ms%n", totalMs);
    return 0;
  }
}
