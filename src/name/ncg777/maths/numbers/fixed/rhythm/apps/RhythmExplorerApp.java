package name.ncg777.maths.numbers.fixed.rhythm.apps;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.Callable;
import com.fasterxml.jackson.databind.ObjectMapper;
import picocli.CommandLine;
import picocli.CommandLine.*;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmPredicateRegistry;
import name.ncg777.maths.numbers.fixed.rhythm.exploration.*;
import name.ncg777.maths.numbers.fixed.rhythm.exploration.RhythmMask.*;
import name.ncg777.maths.numbers.fixed.rhythm.exploration.RhythmExplorer.*;

@Command(name="rhythm", mixinStandardHelpOptions=true, description="Complete, mask and evolve rhythms, or open the Swing explorer.",
    subcommands={RhythmExplorerApp.Complete.class, RhythmExplorerApp.Mask.class, RhythmExplorerApp.Evolve.class, RhythmExplorerApp.Predicates.class, RhythmExplorerApp.Gui.class})
public final class RhythmExplorerApp implements Runnable {
  @Spec CommandLine.Model.CommandSpec spec;
  @Override public void run() { spec.commandLine().usage(spec.commandLine().getOut()); }
  public static void main(String[] args) {
    int code = new CommandLine(new RhythmExplorerApp()).execute(args);
    if (code != 0 || args.length == 0 || !args[0].equals("gui")) System.exit(code);
  }
  static class Input {
    @Option(names="--cipher", defaultValue="Hexadecimal", description="Binary, Octal or Hexadecimal.") Cipher cipher;
    @Option(names="--seed", defaultValue="777") long seed;
  }
  static class SearchOptions {
    @Option(names="--where", defaultValue="NONEMPTY", description="Boolean rhythm predicate clause.") String where;
    @Option(names="--limit", defaultValue="512", description="Candidate pool limit, 1..10000.") int limit;
    @Option(names="--budget", defaultValue="100000", description="Assignment attempts, 1..100000.") int budget;
  }
  static class EraseOptions {
    @Option(names="--erase-count", defaultValue="4") int count;
    @Option(names="--erase-unit", defaultValue="bit", description="bit or digit.") String unit;
    @Option(names="--erase-policy", defaultValue="SCATTERED", description="SCATTERED, BLOCK or PERIODIC.") Erasure policy;
    @Option(names="--protect", defaultValue="", description="Comma-separated zero-based displayed step indices.") String protect;
    int width(Cipher cipher) {
      return switch(unit.toLowerCase(Locale.ROOT)) { case "bit" -> 1; case "digit" -> cipher.width; default -> throw new IllegalArgumentException("Erasure unit must be bit or digit."); };
    }
  }
  @Command(name="complete", mixinStandardHelpOptions=true, description="List distinct valid completions; status is written to stderr.")
  static class Complete implements Callable<Integer> {
    @Mixin Input input; @Mixin SearchOptions search;
    @Option(names="--mask", required=true) String mask;
    @Option(names="--format", defaultValue="text", description="text or jsonl.") String format;
    @Spec CommandLine.Model.CommandSpec spec;
    public Integer call() throws Exception {
      validateFormat(format);
      RhythmMask parsed = RhythmMask.parse(mask, input.cipher);
      Search result = RhythmExplorer.complete(parsed, RhythmExplorer.rule(search.where, parsed.bits().length()), search.limit, search.budget, input.seed);
      PrintWriter out = spec.commandLine().getOut(); ObjectMapper json = new ObjectMapper();
      for (String bits : result.candidates()) {
        String value = new RhythmMask(bits).format(input.cipher);
        out.println(format.equals("jsonl") ? json.writeValueAsString(Map.of("bits", bits, "rhythm", value, "seed", input.seed)) : value);
      }
      spec.commandLine().getErr().printf("%d completions; %d attempts; %s%n", result.candidates().size(), result.attempts(), result.stop());
      return result.candidates().isEmpty() ? (result.stop() == Stop.EXHAUSTED ? 1 : 3) : 0;
    }
  }
  @Command(name="mask", mixinStandardHelpOptions=true, description="Release positions; bit masks are emitted in Binary if digit conversion would lose information.")
  static class Mask implements Callable<Integer> {
    @Mixin Input input; @Mixin EraseOptions erase;
    @Option(names="--start", required=true) String start;
    @Spec CommandLine.Model.CommandSpec spec;
    public Integer call() {
      RhythmMask mask = RhythmMask.parse(start, input.cipher).erase(erase.count, erase.width(input.cipher), erase.policy, RhythmMask.positions(erase.protect), new Random(input.seed));
      Cipher outputCipher = input.cipher;
      String text;
      try { text = mask.format(outputCipher); }
      catch (IllegalArgumentException e) { outputCipher = Cipher.Binary; text = mask.bits(); }
      spec.commandLine().getErr().println("Output cipher: " + outputCipher);
      spec.commandLine().getOut().println(text);
      return 0;
    }
  }
  @Command(name="evolve", mixinStandardHelpOptions=true, description="Seeded evolution, one result per line. Selection probabilities apply only to each candidate pool.")
  static class Evolve implements Callable<Integer> {
    @Mixin Input input; @Mixin SearchOptions search; @Mixin EraseOptions erase;
    @Option(names="--start", required=true) String start;
    @Option(names="--transition", defaultValue="TRUE", description="Clause over previous + candidate, in chronological order.") String transition;
    @Option(names="--iterations", defaultValue="64", description="1..1000000; 0 streams until interrupted.") int iterations;
    @Option(names="--target-changes", defaultValue="2") int changes;
    @Option(names="--target-hits", defaultValue="-1", description="-1 disables density preference.") int hits;
    @Option(names="--temperature", defaultValue="0.15") double temperature;
    @Option(names="--avoid-recent", defaultValue="8") int memory;
    @Option(names="--euclidean-weight", defaultValue="0") double euclidean;
    @Option(names="--sync-target", defaultValue="-1", description="-1 disables; otherwise 0..1 weak-onset/silent-next-beat fraction.") double sync;
    @Option(names="--steps-per-beat", defaultValue="4") int beatSteps;
    @Option(names="--format", defaultValue="jsonl", description="text or jsonl.") String format;
    @Spec CommandLine.Model.CommandSpec spec;
    public Integer call() throws Exception {
      validateFormat(format);
      if (iterations < 0 || iterations > 1000000) throw new IllegalArgumentException("Iterations must be 0..1000000.");
      var settings = new Settings(search.where, transition, erase.count, erase.width(input.cipher), erase.policy,
          RhythmMask.positions(erase.protect), search.limit, search.budget, input.seed,
          new Selection(changes, hits, temperature, memory, euclidean, sync, beatSteps));
      var session = new Session(RhythmMask.parse(start, input.cipher).bits(), settings);
      ObjectMapper json = new ObjectMapper(); PrintWriter out = spec.commandLine().getOut();
      for (int i = 0; iterations == 0 || i < iterations; i++) {
        RhythmExplorer.check(); Step step = session.next();
        out.println(format.equals("jsonl") ? json.writeValueAsString(step) : new RhythmMask(step.bits()).format(input.cipher));
        out.flush(); if (out.checkError()) break;
      }
      return 0;
    }
  }
  @Command(name="predicates", mixinStandardHelpOptions=true, description="List expression-builder predicates and parameter descriptions.")
  static class Predicates implements Runnable {
    @Spec CommandLine.Model.CommandSpec spec;
    public void run() { for (var d : RhythmPredicateRegistry.descriptors()) spec.commandLine().getOut().println(d + " — " + d.description()); }
  }
  @Command(name="gui", mixinStandardHelpOptions=true, description="Launch the Swing explorer.")
  static class Gui implements Callable<Integer> {
    public Integer call() { RhythmExplorerGUI.main(new String[0]); return 0; }
  }
  private static void validateFormat(String format) {
    if (!format.equals("text") && !format.equals("jsonl")) throw new IllegalArgumentException("Format must be text or jsonl.");
  }
}
