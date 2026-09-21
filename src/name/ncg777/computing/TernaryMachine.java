package name.ncg777.computing;

import java.math.BigInteger;
import java.util.*;
import name.ncg777.computing.structures.TritWord64;
import name.ncg777.maths.Trit;

/** Deterministic three-symbol tape machine with explicit step limits. */
public final class TernaryMachine {
  public static final int HALT = -1;

  /** One rule per (control state, read trit). Moves -1, 0, or +1 tape cell. */
  public record Rule(int write, int move, int nextState) {
    public Rule {
      Trit.BUF(write);
      if (move < -1 || move > 1) throw new IllegalArgumentException("Move must be -1, 0 or +1");
      if (nextState < HALT) throw new IllegalArgumentException("Invalid next state");
    }
  }

  /** Each listed control state must define all three read cases. State zero starts execution. */
  public record Program(Map<Integer, List<Rule>> rules) {
    public Program {
      Objects.requireNonNull(rules);
      Map<Integer, List<Rule>> copy = new HashMap<>();
      for (var entry : rules.entrySet()) {
        if (entry.getKey() == null || entry.getKey() < 0) throw new IllegalArgumentException("Invalid control state");
        List<Rule> row = List.copyOf(entry.getValue());
        if (row.size() != 3) throw new IllegalArgumentException("Supply rules for -1, 0, +1");
        copy.put(entry.getKey(), row);
      }
      if (!copy.containsKey(0)) throw new IllegalArgumentException("Program needs state zero");
      for (List<Rule> row : copy.values())
        for (Rule rule : row)
          if (rule.nextState() != HALT && !copy.containsKey(rule.nextState()))
            throw new IllegalArgumentException("Undefined next state " + rule.nextState());
      rules = Map.copyOf(copy);
    }
    public Rule rule(int state, int read) { return rules.get(state).get(Trit.BUF(read) + 1); }

    /** One rule per line: state read write move next; read/write use T,0,1. */
    public static Program parse(String source) {
      Objects.requireNonNull(source);
      Map<Integer, Rule[]> rows = new HashMap<>();
      String[] lines = source.split("\\R");
      for (int line = 0; line < lines.length; line++) {
        String content = lines[line].split("#", 2)[0].strip();
        if (content.isEmpty()) continue;
        String[] parts = content.split("\\s+");
        if (parts.length != 5) throw new IllegalArgumentException("Line " + (line + 1) + ": expected five fields");
        try {
          int state = Integer.parseInt(parts[0]), read = parseTrit(parts[1]);
          Rule rule = new Rule(parseTrit(parts[2]), Integer.parseInt(parts[3]),
              parts[4].equalsIgnoreCase("HALT") ? HALT : Integer.parseInt(parts[4]));
          if (state < 0) throw new IllegalArgumentException("Negative state");
          Rule[] row = rows.computeIfAbsent(state, unused -> new Rule[3]);
          if (row[read + 1] != null) throw new IllegalArgumentException("Duplicate rule");
          row[read + 1] = rule;
        } catch (IllegalArgumentException e) {
          throw new IllegalArgumentException("Line " + (line + 1) + ": " + e.getMessage(), e);
        }
      }
      Map<Integer, List<Rule>> completed = new HashMap<>();
      for (var entry : rows.entrySet()) {
        if (Arrays.asList(entry.getValue()).contains(null))
          throw new IllegalArgumentException("State " + entry.getKey() + " is missing a read case");
        completed.put(entry.getKey(), Arrays.asList(entry.getValue()));
      }
      return new Program(completed);
    }

    private static int parseTrit(String text) {
      return switch (text) {
        case "T", "-1" -> -1;
        case "0" -> 0;
        case "1" -> 1;
        default -> throw new IllegalArgumentException("Trit must be T, 0 or 1");
      };
    }
  }

  /** A compact microcoded controller may include bounded control registers. Each
   * execution must own its controller; program data and call frames live on tape. */
  @FunctionalInterface
  public interface Controller { Rule rule(int state, int read); }

  private final Controller controller;
  private final Map<BigInteger, Integer> tape = new HashMap<>();
  private BigInteger head = BigInteger.ZERO;
  private int state;
  private long steps;
  private boolean halted;

  public TernaryMachine(Program program) { this(Objects.requireNonNull(program)::rule); }
  public TernaryMachine(Controller controller) { this.controller = Objects.requireNonNull(controller); }

  /** Loads a word at cells 0..63 before execution. */
  public TernaryMachine(Program program, TritWord64 input) {
    this(program);
    Objects.requireNonNull(input);
    for (int i = 0; i < TritWord64.WIDTH; i++) write(BigInteger.valueOf(i), input.trit(i));
  }

  public int read(BigInteger address) { return tape.getOrDefault(Objects.requireNonNull(address), 0); }
  public void write(BigInteger address, int trit) {
    Objects.requireNonNull(address);
    Trit.BUF(trit);
    if (trit == 0) tape.remove(address); else tape.put(address, trit);
  }
  public TritWord64 wordAt(BigInteger start) {
    Objects.requireNonNull(start);
    int[] digits = new int[TritWord64.WIDTH];
    for (int i = 0; i < digits.length; i++) digits[i] = read(start.add(BigInteger.valueOf(i)));
    return TritWord64.fromTrits(digits);
  }
  public BigInteger head() { return head; }
  public int state() { return state; }
  public long steps() { return steps; }
  public boolean halted() { return halted; }
  public int nonzeroCells() { return tape.size(); }

  /** Executes exactly one transition, or returns false after a halt. */
  public boolean step() {
    if (halted) return false;
    if (steps == Long.MAX_VALUE) throw new IllegalStateException("Step counter overflow");
    Rule rule = controller.rule(state, read(head));
    write(head, rule.write());
    head = head.add(BigInteger.valueOf(rule.move()));
    state = rule.nextState();
    halted = state == HALT;
    steps++;
    return true;
  }

  /** Returns true on HALT; false when the caller's step budget is exhausted. */
  public boolean run(long maxSteps) {
    if (maxSteps < 0) throw new IllegalArgumentException("Negative step budget");
    for (long i = 0; i < maxSteps && !halted; i++) step();
    return halted;
  }
}
