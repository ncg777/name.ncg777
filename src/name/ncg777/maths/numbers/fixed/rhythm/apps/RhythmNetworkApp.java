package name.ncg777.maths.numbers.fixed.rhythm.apps;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import name.ncg777.maths.Matrix;
import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.numbers.fixed.FixedLength;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmClauseParser;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmDistance;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmNetwork;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmNetworkAnalysis;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmNetworkNavigator;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmPredicateRegistry;
import name.ncg777.maths.numbers.relations.PredicatedDifferences;
import name.ncg777.maths.numbers.relations.PredicatedJuxtaposition;
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

    @Option(names = {"-i", "--interactive"},
      description = "Start interactive session mode with multiple in-memory networks")
    private boolean interactive;

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

    if (interactive) {
      return runInteractiveSession();
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

  // ───────────────────────────────────────────── interactive mode ───────

  private static final class SessionNetwork {
    final String name;
    final RhythmNetwork network;

    SessionNetwork(String name, RhythmNetwork network) {
      this.name = name;
      this.network = network;
    }
  }

  private Integer runInteractiveSession() {
    System.out.println("rhythm-network interactive mode");
    System.out.println("Type 'help' for commands.");

    Map<String, SessionNetwork> networks = new LinkedHashMap<>();
    String selected = null;
    int autoId = 1;

    try (Scanner scanner = new Scanner(System.in)) {
      while (true) {
        System.out.print("rhythm-network> ");
        if (!scanner.hasNextLine()) break;

        String line = scanner.nextLine().trim();
        if (line.isEmpty()) continue;

        String[] tokens = line.split("\\s+");
        String cmd = tokens[0].toLowerCase();

        try {
          switch (cmd) {
            case "help":
              printInteractiveHelp();
              break;

            case "quit":
            case "exit":
              return 0;

            case "list": {
              if (networks.isEmpty()) {
                System.out.println("No networks loaded.");
                break;
              }
              System.out.println("Loaded networks:");
              for (SessionNetwork sn : networks.values()) {
                String mark = sn.name.equals(selected) ? "*" : " ";
                System.out.printf(" %s %s  [L=%d, cipher=%s, T=%.2f, V=%,d, E=%,d]%n",
                    mark,
                    sn.name,
                    sn.network.getL(),
                    sn.network.getCipher(),
                    sn.network.getTemperature(),
                    sn.network.vertexCount(),
                    sn.network.edgeCount());
              }
              break;
            }

            case "select": {
              if (tokens.length < 2) {
                System.out.println("Usage: select <name>");
                break;
              }
              String name = tokens[1];
              if (!networks.containsKey(name)) {
                System.out.println("No such network: " + name);
                break;
              }
              selected = name;
              System.out.println("Selected: " + selected);
              break;
            }

            case "unload": {
              if (tokens.length < 2) {
                System.out.println("Usage: unload <name|selected|all>");
                break;
              }
              String target = tokens[1];
              if ("all".equalsIgnoreCase(target)) {
                networks.clear();
                selected = null;
                System.out.println("Unloaded all networks.");
              } else {
                String name = "selected".equalsIgnoreCase(target) ? selected : target;
                if (name == null || !networks.containsKey(name)) {
                  System.out.println("No such network: " + target);
                  break;
                }
                networks.remove(name);
                if (name.equals(selected)) {
                  selected = networks.isEmpty() ? null : networks.keySet().iterator().next();
                }
                System.out.println("Unloaded: " + name);
              }
              break;
            }

            case "load": {
              String load = prompt(scanner, "Load path", loadPath == null ? "" : loadPath).trim();
              if (load.isEmpty()) {
                System.out.println("Cancelled (empty path).");
                break;
              }
              String defaultName = "network-" + autoId;
              String name = uniqueName(
                  prompt(scanner, "Name", defaultName).trim(),
                  networks);
              long ts = System.nanoTime();
              RhythmNetwork network = RhythmNetwork.load(Path.of(load));
              networks.put(name, new SessionNetwork(name, network));
              selected = name;
              autoId++;
              System.out.printf("Loaded '%s': %,d vertices, %,d edges (%.0f ms)%n",
                  name,
                  network.vertexCount(),
                  network.edgeCount(),
                  (System.nanoTime() - ts) / 1e6);
              break;
            }

            case "build": {
              int l = parseIntOrDefault(prompt(scanner, "Length L", Integer.toString(length)), length);
              Cipher.Name c = parseCipherOrDefault(prompt(scanner, "Cipher", cipher.name()), cipher);
              String vClause = prompt(scanner, "Vertex clause", vertexClause == null ? "" : vertexClause);
              String jClause = prompt(scanner, "Juxt clause", juxtClause == null ? "" : juxtClause);
              RhythmDistance d = parseDistanceOrDefault(prompt(scanner, "Distance", distanceName), RhythmDistance.parse(distanceName));
              double temp = parseDoubleOrDefault(prompt(scanner, "Temperature T", Double.toString(temperature)), temperature);
              boolean loops = parseBooleanOrDefault(prompt(scanner, "Allow self loops (true/false)", Boolean.toString(selfLoops)), selfLoops);

              String defaultName = "network-" + autoId;
              String name = uniqueName(prompt(scanner, "Name", defaultName).trim(), networks);

              long ts = System.nanoTime();
              RhythmNetwork network = new RhythmNetwork.Builder()
                  .length(l)
                  .cipher(c)
                  .vertexClause(vClause)
                  .juxtapositionClause(jClause)
                  .distance(d)
                  .temperature(temp)
                  .selfLoops(loops)
                  .build();
              networks.put(name, new SessionNetwork(name, network));
              selected = name;
              autoId++;
              System.out.printf("Built '%s': %,d vertices, %,d edges (%.0f ms)%n",
                  name,
                  network.vertexCount(),
                  network.edgeCount(),
                  (System.nanoTime() - ts) / 1e6);
              break;
            }

            case "save": {
              if (networks.isEmpty()) {
                System.out.println("No networks loaded.");
                break;
              }
              String defaultName = selected == null ? "" : selected;
              String toSave = prompt(scanner, "Network name", defaultName).trim();
              if (toSave.isEmpty() || !networks.containsKey(toSave)) {
                System.out.println("Unknown network.");
                break;
              }
              String path = prompt(scanner, "Save path", savePath == null ? "" : savePath).trim();
              if (path.isEmpty()) {
                System.out.println("Cancelled (empty path).");
                break;
              }
              long ts = System.nanoTime();
              networks.get(toSave).network.save(Path.of(path));
              System.out.printf("Saved '%s' to %s (%.0f ms)%n", toSave, path, (System.nanoTime() - ts) / 1e6);
              break;
            }

            case "analyze": {
              SessionNetwork sn = requireSelected(networks, selected);
              if (sn == null) break;
              double navT = parseDoubleOrDefault(
                  tokens.length > 1 ? tokens[1] : "",
                  navTemperature);
              RhythmNetworkAnalysis analysis = new RhythmNetworkAnalysis(sn.network, navT);
              System.out.println(analysis.summary());
              break;
            }

            case "walk": {
              SessionNetwork sn = requireSelected(networks, selected);
              if (sn == null) break;
              int stepCount = parseIntOrDefault(tokens.length > 1 ? tokens[1] : "", steps);
              double navT = parseDoubleOrDefault(tokens.length > 2 ? tokens[2] : "", navTemperature);
              long walkSeed = parseLongOrDefault(tokens.length > 3 ? tokens[3] : "", seed);

              long ts = System.nanoTime();
              RhythmNetworkNavigator nav = walkSeed < 0
                  ? new RhythmNetworkNavigator(sn.network)
                  : new RhythmNetworkNavigator(sn.network, walkSeed);
              nav.setNavigationTemperature(navT);

              List<FixedLength.Natural> walk = nav.walk(stepCount);
              System.out.println("# walk (τ=" + navT + ", T=" + sn.network.getTemperature() + ")");
              System.out.println(walk.stream()
                  .map(FixedLength.Natural::toString)
                  .collect(java.util.stream.Collectors.joining(" ")));
              System.err.printf("Walk generated in %.0f ms%n", (System.nanoTime() - ts) / 1e6);
              break;
            }

            case "matrix": {
              SessionNetwork sn = requireSelected(networks, selected);
              if (sn == null) break;

              int nWalks = parseIntOrDefault(prompt(scanner, "Walk count N", "8"), 8);
              int stepCount = parseIntOrDefault(prompt(scanner, "Steps", Integer.toString(steps)), steps);
              double navT = parseDoubleOrDefault(prompt(scanner, "Navigation τ", Double.toString(navTemperature)), navTemperature);
              long walkSeed = parseLongOrDefault(prompt(scanner, "Seed (-1 random)", Long.toString(seed)), seed);
              String successiveClause = prompt(scanner, "Successive clause", sn.network.getJuxtClause()).trim();
              String simultaneousClause = prompt(scanner, "Simultaneous clause", sn.network.getVertexClause()).trim();
              int maxAttempts = parseIntOrDefault(prompt(scanner, "Max attempts", "2000"), 2000);

              Predicate<BinaryNatural> successivePred = RhythmClauseParser.parse(successiveClause);
              Predicate<BinaryNatural> simultaneousPred = RhythmClauseParser.parse(simultaneousClause);
              BiPredicate<BinaryNatural, BinaryNatural> successiveRelation = new PredicatedJuxtaposition(successivePred);
              BiPredicate<BinaryNatural, BinaryNatural> simultaneousRelation = new PredicatedDifferences(simultaneousPred);

              RhythmNetworkNavigator nav = walkSeed < 0
                  ? new RhythmNetworkNavigator(sn.network)
                  : new RhythmNetworkNavigator(sn.network, walkSeed);
              nav.setNavigationTemperature(navT);

              long ts = System.nanoTime();
              List<List<FixedLength.Natural>> accepted = new ArrayList<>();
              int attempts = 0;

              while (accepted.size() < nWalks && attempts < maxAttempts) {
                attempts++;
                List<FixedLength.Natural> candidate = nav.walk(stepCount);
                if (!passesSuccessive(candidate, successiveRelation)) continue;
                if (!passesSimultaneous(candidate, accepted, simultaneousRelation)) continue;
                accepted.add(candidate);
              }

              if (accepted.isEmpty()) {
                System.out.println("No walk matched constraints.");
                break;
              }

              if (accepted.size() < nWalks) {
                System.out.printf("WARNING: Only %,d / %,d walks accepted within %,d attempts.%n",
                    accepted.size(), nWalks, attempts);
              }

              Matrix<FixedLength.Natural> matrix = new Matrix<>(accepted.size(), stepCount);
              for (int i = 0; i < accepted.size(); i++) {
                List<FixedLength.Natural> row = accepted.get(i);
                for (int j = 0; j < stepCount; j++) {
                  matrix.set(i, j, row.get(j));
                }
              }

              System.out.printf("# walk matrix rows=%d cols=%d%n", matrix.rowCount(), matrix.columnCount());
              System.out.print(matrix.toString());
              System.err.printf("Matrix generated in %.0f ms (attempts %,d)%n", (System.nanoTime() - ts) / 1e6, attempts);
              break;
            }

            default:
              System.out.println("Unknown command: " + cmd + " (type 'help')");
              break;
          }
        } catch (Exception e) {
          System.err.println("ERROR: " + e.getMessage());
        }
      }
    }
    return 0;
  }

  private static void printInteractiveHelp() {
    System.out.println("Commands:");
    System.out.println("  help                       show this help");
    System.out.println("  list                       list loaded networks (* = selected)");
    System.out.println("  build                      interactively build and add a network");
    System.out.println("  load                       interactively load and add a network");
    System.out.println("  save                       interactively save one loaded network");
    System.out.println("  select <name>              select active network");
    System.out.println("  unload <name|selected|all> unload network(s)");
    System.out.println("  analyze [navTemp]          run analysis on selected network");
    System.out.println("  walk [steps] [navTemp] [seed]");
    System.out.println("                             generate one walk from selected network");
    System.out.println("  matrix                     interactively generate N constrained walks");
    System.out.println("  quit | exit                leave interactive mode");
  }

  private static String prompt(Scanner scanner, String label, String defaultValue) {
    String shown = defaultValue == null ? "" : defaultValue;
    if (shown.isEmpty()) {
      System.out.print(label + ": ");
    } else {
      System.out.print(label + " [" + shown + "]: ");
    }
    if (!scanner.hasNextLine()) return shown;
    String s = scanner.nextLine();
    if (s == null) return shown;
    s = s.trim();
    return s.isEmpty() ? shown : s;
  }

  private static int parseIntOrDefault(String text, int defaultValue) {
    if (text == null || text.isBlank()) return defaultValue;
    return Integer.parseInt(text.trim());
  }

  private static long parseLongOrDefault(String text, long defaultValue) {
    if (text == null || text.isBlank()) return defaultValue;
    return Long.parseLong(text.trim());
  }

  private static double parseDoubleOrDefault(String text, double defaultValue) {
    if (text == null || text.isBlank()) return defaultValue;
    return Double.parseDouble(text.trim());
  }

  private static boolean parseBooleanOrDefault(String text, boolean defaultValue) {
    if (text == null || text.isBlank()) return defaultValue;
    return Boolean.parseBoolean(text.trim());
  }

  private static Cipher.Name parseCipherOrDefault(String text, Cipher.Name defaultValue) {
    if (text == null || text.isBlank()) return defaultValue;
    return Cipher.Name.valueOf(text.trim());
  }

  private static RhythmDistance parseDistanceOrDefault(String text, RhythmDistance defaultValue) {
    if (text == null || text.isBlank()) return defaultValue;
    return RhythmDistance.parse(text.trim());
  }

  private static String uniqueName(String candidate, Map<String, SessionNetwork> networks) {
    String base = (candidate == null || candidate.isBlank()) ? "network" : candidate.trim();
    String name = base;
    int n = 2;
    while (networks.containsKey(name)) {
      name = base + "-" + n;
      n++;
    }
    return name;
  }

  private static SessionNetwork requireSelected(Map<String, SessionNetwork> networks, String selected) {
    if (selected == null) {
      System.out.println("No selected network. Use 'select', 'build', or 'load'.");
      return null;
    }
    SessionNetwork sn = networks.get(selected);
    if (sn == null) {
      System.out.println("Selected network no longer exists: " + selected);
      return null;
    }
    if (sn.network.vertexCount() == 0) {
      System.out.println("Selected network has zero vertices.");
      return null;
    }
    if (sn.network.edgeCount() == 0) {
      System.out.println("Selected network has zero edges.");
      return null;
    }
    return sn;
  }

  private static boolean passesSuccessive(
      List<FixedLength.Natural> walk,
      BiPredicate<BinaryNatural, BinaryNatural> relation) {
    for (int i = 1; i < walk.size(); i++) {
      if (!relation.test(walk.get(i - 1).toBinaryNatural(), walk.get(i).toBinaryNatural())) {
        return false;
      }
    }
    return true;
  }

  private static boolean passesSimultaneous(
      List<FixedLength.Natural> candidate,
      List<List<FixedLength.Natural>> accepted,
      BiPredicate<BinaryNatural, BinaryNatural> relation) {
    for (List<FixedLength.Natural> existing : accepted) {
      for (int j = 0; j < candidate.size(); j++) {
        if (!relation.test(candidate.get(j).toBinaryNatural(), existing.get(j).toBinaryNatural())) {
          return false;
        }
      }
    }
    return true;
  }
}
