package name.ncg777.maths.numbers.fixed.rhythm.apps;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmDistance;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmNetwork;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmNetworkAnalysis;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Builds a {@link RhythmNetwork} and prints a single TSV line of quality metrics.
 *
 * <p>Output format (tab-separated):
 * <pre>
 *   clause  vertices  edges  strongly_connected  avg_transition_entropy  stationary_entropy  spectral_gap
 * </pre>
 *
 * <p>Exit codes:
 * <ul>
 *   <li>0 – success</li>
 *   <li>2 – build error</li>
 *   <li>3 – empty network (0 vertices or 0 edges) — no TSV line emitted</li>
 * </ul>
 *
 * <p>Intended for use in shell pipelines with {@code rhythmnetwork-explore.sh}: each clause
 * from {@code RhythmClauseEnumeratorApp} is fed to this tool in parallel, the resulting TSV
 * is sorted by composite score, and the top-N winners are handed to {@link RhythmNetworkApp}.
 */
@Command(
    name = "rhythm-score",
    mixinStandardHelpOptions = true,
    description = "Score a clause: build the rhythm network and print one TSV metrics line."
)
public final class RhythmNetworkScoreApp implements Callable<Integer> {

  @Option(names = {"-L", "--length"}, defaultValue = "4",
      description = "Cipher symbols per rhythm (default: 4)")
  private int length;

  @Option(names = {"-c", "--cipher"}, defaultValue = "Hexadecimal",
      description = "Cipher name (default: Hexadecimal)")
  private Cipher.Name cipher;

  @Option(names = {"--vertex"}, defaultValue = "",
      description = "Vertex predicate clause")
  private String vertexClause;

  @Option(names = {"--juxt"}, defaultValue = "",
      description = "Juxtaposition predicate clause")
  private String juxtClause;

  @Option(names = {"-d", "--distance"}, defaultValue = "HAMMING",
      description = "Distance measure (default: HAMMING)")
  private String distanceName;

  @Option(names = {"-T", "--temp"}, defaultValue = "1.0",
      description = "Construction temperature (default: 1.0)")
  private double temperature;

  @Option(names = {"-t", "--nav-temp"}, defaultValue = "1.0",
      description = "Navigation temperature for analysis (default: 1.0)")
  private double navTemperature;

  @Option(names = {"--save"}, defaultValue = "",
      description = "Save the built network to this .rnet file (optional)")
  private String savePath;

  @Option(names = {"-v", "--verbose"},
      description = "Show progress on stderr (default: off for batch use)")
  private boolean verbose;

  // ─────────────────────────────────────────────────────────────────────────

  public static void main(String[] args) {
    System.exit(new CommandLine(new RhythmNetworkScoreApp()).execute(args));
  }

  @Override
  public Integer call() {
    long t0 = System.nanoTime();
    if (verbose) System.err.printf("[score] clause: %s%n", vertexClause);

    RhythmNetwork net;
    try {
      net = new RhythmNetwork.Builder()
          .length(length)
          .cipher(cipher)
          .vertexClause(vertexClause)
          .juxtapositionClause(juxtClause)
          .distance(RhythmDistance.parse(distanceName))
          .temperature(temperature)
          .build();
    } catch (Exception e) {
      System.err.println("BUILD ERROR for clause [" + vertexClause + "]: " + e.getMessage());
      return 2;
    }

    if (net.vertexCount() == 0 || net.edgeCount() == 0) {
      if (verbose) System.err.println("[score] empty network — skipped");
      return 3;
    }

    // Optionally save the network for reuse by later pipeline stages
    if (savePath != null && !savePath.isBlank()) {
      try { net.save(Path.of(savePath)); }
      catch (Exception e) {
        System.err.println("[score] save warning: " + e.getMessage());
      }
    }

    RhythmNetworkAnalysis a = new RhythmNetworkAnalysis(net, navTemperature);
    a.setVerbose(verbose);

    System.out.printf("%s\t%d\t%d\t%b\t%.6f\t%.6f\t%.6f%n",
        vertexClause,
        net.vertexCount(),
        net.edgeCount(),
        a.isStronglyConnected(),
        a.averageTransitionEntropy(),
        a.stationaryEntropy(),
        a.spectralGap());

    if (verbose) {
      long ms = (long) ((System.nanoTime() - t0) / 1e6);
      System.err.printf("[score] done in %,d ms (V=%d, E=%d)%n",
          ms, net.vertexCount(), net.edgeCount());
    }
    return 0;
  }
}
