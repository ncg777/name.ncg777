package name.ncg777.computing;

import java.math.BigInteger;
import java.util.*;
import name.ncg777.maths.Trit;

/** A small, pure, first-order functional language compiled to tape transitions. */
public final class TernaryFunctional {
  private TernaryFunctional() {}
  public static final int MAX_INPUTS = 32;
  public static final int MAX_CELLS = 256;
  public static final int MAX_STATES = 50000;
  private static final Set<String> UNARY = Set.of(TernaryExpression.UNARY.split(" "));
  private static final Set<String> BINARY = Set.of(TernaryExpression.BINARY.split(" "));

  public record Compiled(TernaryMachine.Program program, List<String> inputs,
      int resultCell, int allocatedCells) {
    public Compiled { inputs = List.copyOf(inputs); }

    /** New execution with fresh scratch cells. Input names must match exactly. */
    public TernaryMachine newMachine(Map<String, Integer> values) {
      Objects.requireNonNull(values);
      if (!values.keySet().equals(new HashSet<>(inputs)))
        throw new IllegalArgumentException("Expected inputs " + inputs);
      TernaryMachine machine = new TernaryMachine(program);
      for (int i = 0; i < inputs.size(); i++) {
        Integer value = values.get(inputs.get(i));
        if (value == null) throw new IllegalArgumentException("Missing value for " + inputs.get(i));
        machine.write(BigInteger.valueOf(i), value);
      }
      return machine;
    }

    /** The result is valid only after this program halts. */
    public int result(TernaryMachine machine) {
      if (!machine.halted()) throw new IllegalStateException("Execution has not halted; no result yet");
      return machine.read(BigInteger.valueOf(resultCell));
    }
  }

  private record Form(String atom, List<Form> children, int offset) {
    IllegalArgumentException error(String message) {
      return new IllegalArgumentException(message + " at character " + (offset + 1));
    }
    String word() {
      if (atom == null) throw error("Expected a name");
      return atom;
    }
    List<Form> list() {
      if (children == null) throw error("Expected a parenthesized form");
      return children;
    }
  }
  private record Function(List<String> parameters, Form body) {}

  public static Compiled compile(String source) {
    List<Form> forms = new Reader(source).readAll();
    if (forms.isEmpty()) throw new IllegalArgumentException("Expected a final expression");
    List<String> inputs = new ArrayList<>();
    Map<String, Function> functions = new LinkedHashMap<>();
    boolean hasInputs = false;
    for (int i = 0; i < forms.size() - 1; i++) {
      Form form = forms.get(i);
      List<Form> parts = form.list();
      if (parts.isEmpty()) throw form.error("Empty declaration");
      switch (parts.get(0).word()) {
        case "inputs" -> {
          if (hasInputs) throw form.error("Duplicate inputs declaration");
          hasInputs = true;
          for (Form input : parts.subList(1, parts.size())) {
            String name = name(input);
            if (inputs.contains(name)) throw input.error("Duplicate input " + name);
            inputs.add(name);
          }
          if (inputs.size() > MAX_INPUTS) throw form.error("At most " + MAX_INPUTS + " inputs");
        }
        case "def" -> {
          arity(form, 3);
          String name = name(parts.get(1));
          List<String> parameters = new ArrayList<>();
          for (Form parameter : parts.get(2).list()) {
            String parameterName = name(parameter);
            if (parameters.contains(parameterName)) throw parameter.error("Duplicate parameter " + parameterName);
            parameters.add(parameterName);
          }
          if (functions.putIfAbsent(name, new Function(List.copyOf(parameters), parts.get(3))) != null)
            throw form.error("Duplicate function " + name);
        }
        default -> throw form.error("Only inputs and def declarations may precede the final expression");
      }
    }
    Form body = forms.get(forms.size() - 1);
    // Validate even unused functions and untaken branches, including their call graph.
    Map<String, Set<String>> calls = new HashMap<>();
    for (var entry : functions.entrySet()) {
      Set<String> dependencies = new HashSet<>();
      validate(entry.getValue().body(), new HashSet<>(entry.getValue().parameters()), functions, dependencies);
      calls.put(entry.getKey(), dependencies);
    }
    for (String function : functions.keySet()) checkAcyclic(function, calls, new HashSet<>(), new HashSet<>());
    validate(body, new HashSet<>(inputs), functions, new HashSet<>());
    Compiler compiler = new Compiler(functions, inputs.size() + 1);
    Map<String, Integer> environment = new HashMap<>();
    for (int i = 0; i < inputs.size(); i++) environment.put(inputs.get(i), i);
    int halt = compiler.go(0, inputs.size(), TernaryMachine.HALT);
    int entry = compiler.emit(body, environment, inputs.size(), halt, 0);
    compiler.rules.put(0, compiler.preserve(0, entry));
    return new Compiled(new TernaryMachine.Program(compiler.rules), inputs, inputs.size(), compiler.cells);
  }

  private static String name(Form form) {
    String name = form.word();
    if (!name.matches("[a-zA-Z_][a-zA-Z_0-9]*") || name.equals("T")
        || Set.of("let", "case", "def", "inputs").contains(name)
        || UNARY.contains(name.toUpperCase(Locale.ROOT)) || BINARY.contains(name.toUpperCase(Locale.ROOT)))
      throw form.error("Invalid or reserved name " + name);
    return name;
  }
  private static Integer literal(String atom) {
    return switch (atom) { case "T", "-1" -> -1; case "0" -> 0; case "1" -> 1; default -> null; };
  }
  private static void arity(Form form, int arguments) {
    if (form.list().size() != arguments + 1) throw form.error("Expected " + arguments + " arguments");
  }
  private static void validate(Form form, Set<String> scope, Map<String, Function> functions, Set<String> calls) {
    if (form.atom() != null) {
      if (literal(form.atom()) == null && !scope.contains(form.atom())) throw form.error("Unbound variable " + form.atom());
      return;
    }
    List<Form> parts = form.list();
    if (parts.isEmpty()) throw form.error("Empty expression");
    String operation = parts.get(0).word();
    if (operation.equals("let")) {
      arity(form, 3);
      String variable = name(parts.get(1));
      validate(parts.get(2), scope, functions, calls);
      Set<String> nested = new HashSet<>(scope);
      nested.add(variable);
      validate(parts.get(3), nested, functions, calls);
      return;
    }
    String gate = operation.toUpperCase(Locale.ROOT);
    if (operation.equals("case")) arity(form, 4);
    else if (UNARY.contains(gate)) arity(form, 1);
    else if (BINARY.contains(gate)) arity(form, 2);
    else {
      Function function = functions.get(operation);
      if (function == null) throw form.error("Unknown function " + operation);
      arity(form, function.parameters().size());
      calls.add(operation);
    }
    for (Form argument : parts.subList(1, parts.size())) validate(argument, scope, functions, calls);
  }
  private static void checkAcyclic(String function, Map<String, Set<String>> calls,
      Set<String> active, Set<String> complete) {
    if (complete.contains(function)) return;
    if (active.size() >= 64) throw new IllegalArgumentException("Function call chain exceeds 64 levels");
    if (!active.add(function)) throw new IllegalArgumentException("Recursive call involving " + function + " is not supported");
    for (String dependency : calls.get(function)) checkAcyclic(dependency, calls, active, complete);
    active.remove(function);
    complete.add(function);
  }

  /** Every expression fragment enters at cell zero and returns there to its continuation. */
  private static final class Compiler {
    final Map<String, Function> functions;
    final Map<Integer, List<TernaryMachine.Rule>> rules = new HashMap<>();
    int cells;
    int nextState = 1;
    int expansions;
    Compiler(Map<String, Function> functions, int cells) { this.functions = functions; this.cells = cells; }
    int cell() {
      if (cells >= MAX_CELLS) throw new IllegalArgumentException("Compilation exceeds " + MAX_CELLS + " tape cells");
      return cells++;
    }
    int state(List<TernaryMachine.Rule> row) {
      if (nextState >= MAX_STATES) throw new IllegalArgumentException("Compilation exceeds " + MAX_STATES + " states");
      int id = nextState++;
      rules.put(id, row);
      return id;
    }
    List<TernaryMachine.Rule> preserve(int move, int next) {
      return List.of(new TernaryMachine.Rule(-1, move, next), new TernaryMachine.Rule(0, move, next),
          new TernaryMachine.Rule(1, move, next));
    }
    int go(int from, int to, int next) {
      int move = Integer.compare(to, from);
      for (int steps = Math.abs(to - from); steps > 0; steps--) next = state(preserve(move, next));
      return next;
    }
    int write(int from, int target, int value, int nextAtZero) {
      int back = go(target, 0, nextAtZero);
      var rule = new TernaryMachine.Rule(value, 0, back);
      return go(from, target, state(List.of(rule, rule, rule)));
    }
    int unary(String gate, int source, int target, int next) {
      List<TernaryMachine.Rule> row = new ArrayList<>();
      for (int value = -1; value <= 1; value++)
        row.add(new TernaryMachine.Rule(value, 0, write(source, target, Trit.unaryOperator(gate, value), next)));
      return go(0, source, state(row));
    }
    int binary(String gate, int left, int right, int target, int next) {
      List<TernaryMachine.Rule> outer = new ArrayList<>();
      for (int a = -1; a <= 1; a++) {
        List<TernaryMachine.Rule> inner = new ArrayList<>();
        for (int b = -1; b <= 1; b++)
          inner.add(new TernaryMachine.Rule(b, 0, write(right, target, Trit.binaryOperator(gate, a, b), next)));
        outer.add(new TernaryMachine.Rule(a, 0, go(left, right, state(inner))));
      }
      return go(0, left, state(outer));
    }
    int emit(Form form, Map<String, Integer> environment, int target, int next, int depth) {
      if (depth > 64 || ++expansions > 4096) throw form.error("Expanded program exceeds depth or size budget");
      if (form.atom() != null) {
        Integer value = literal(form.atom());
        return value != null ? write(0, target, value, next) : unary("BUF", environment.get(form.atom()), target, next);
      }
      List<Form> parts = form.list();
      String operation = parts.get(0).word();
      if (operation.equals("let")) {
        int local = cell();
        Map<String, Integer> nested = new HashMap<>(environment);
        nested.put(parts.get(1).word(), local);
        int body = emit(parts.get(3), nested, target, next, depth + 1);
        return emit(parts.get(2), environment, local, body, depth + 1);
      }
      if (operation.equals("case")) {
        int condition = cell();
        List<TernaryMachine.Rule> choices = new ArrayList<>();
        for (int value = -1; value <= 1; value++) {
          int branch = emit(parts.get(value + 3), environment, target, next, depth + 1);
          choices.add(new TernaryMachine.Rule(value, 0, go(condition, 0, branch)));
        }
        return emit(parts.get(1), environment, condition, go(0, condition, state(choices)), depth + 1);
      }
      String gate = operation.toUpperCase(Locale.ROOT);
      if (UNARY.contains(gate)) {
        int argument = cell();
        return emit(parts.get(1), environment, argument, unary(gate, argument, target, next), depth + 1);
      }
      if (BINARY.contains(gate)) {
        int left = cell(), right = cell();
        int apply = binary(gate, left, right, target, next);
        int evaluateRight = emit(parts.get(2), environment, right, apply, depth + 1);
        return emit(parts.get(1), environment, left, evaluateRight, depth + 1);
      }
      Function function = functions.get(operation);
      Map<String, Integer> parameters = new HashMap<>();
      for (String parameter : function.parameters()) parameters.put(parameter, cell());
      int entry = emit(function.body(), parameters, target, next, depth + 1);
      for (int i = function.parameters().size() - 1; i >= 0; i--)
        entry = emit(parts.get(i + 1), environment, parameters.get(function.parameters().get(i)), entry, depth + 1);
      return entry;
    }
  }

  private static final class Reader {
    final String source;
    int position;
    int forms;
    Reader(String source) {
      this.source = Objects.requireNonNull(source);
      if (source.length() > 16384) throw new IllegalArgumentException("Source exceeds 16384 characters");
    }
    void space() {
      while (position < source.length()) {
        char character = source.charAt(position);
        if (Character.isWhitespace(character)) position++;
        else if (character == ';') {
          while (position < source.length() && source.charAt(position) != '\n') position++;
        } else break;
      }
    }
    List<Form> readAll() {
      List<Form> result = new ArrayList<>();
      space();
      while (position < source.length()) { result.add(read(0)); space(); }
      return result;
    }
    Form read(int depth) {
      space();
      int start = position;
      if (depth > 64 || ++forms > 4096) throw new IllegalArgumentException("Source exceeds nesting or form budget");
      if (position >= source.length()) throw new IllegalArgumentException("Unexpected end of source");
      if (source.charAt(position) == '(') {
        position++;
        List<Form> children = new ArrayList<>();
        space();
        while (position < source.length() && source.charAt(position) != ')') { children.add(read(depth + 1)); space(); }
        if (position == source.length()) throw new IllegalArgumentException("Unclosed '(' at character " + (start + 1));
        position++;
        return new Form(null, List.copyOf(children), start);
      }
      while (position < source.length() && !Character.isWhitespace(source.charAt(position))
          && "();".indexOf(source.charAt(position)) < 0) position++;
      if (start == position) throw new IllegalArgumentException("Unexpected ')' at character " + (position + 1));
      return new Form(source.substring(start, position), null, start);
    }
  }
}
