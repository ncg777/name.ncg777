package name.ncg777.computing;

import java.util.*;
import name.ncg777.maths.Trit;

/** Functional ternary expressions, evaluated using the project's Trit tables. */
public final class TernaryExpression {
  public static final String UNARY = "BUF NOT PNOT NNOT ABS CLU CLD INC DEC RTU RTD ISP ISZ ISN";
  public static final String BINARY = "AND NAND OR NOR CONS NCONS ANY NANY MUL NMUL SUM NSUM";
  public static final int MAX_COMPILED_VARIABLES = 6;
  private interface Node { int evaluate(Map<String, Integer> values); }
  private final Node root;
  private final List<String> variables;

  private TernaryExpression(Node root, Collection<String> variables) {
    this.root = root;
    this.variables = List.copyOf(variables);
  }

  public static TernaryExpression parse(String source) {
    Objects.requireNonNull(source);
    if (source.length() > 4096) throw new IllegalArgumentException("Expression exceeds 4096 characters");
    Parser parser = new Parser(source);
    Node root = parser.expression(0);
    parser.space();
    if (parser.position != source.length()) throw parser.error("Unexpected trailing text");
    return new TernaryExpression(root, parser.variables);
  }

  /** Variables in alphabetical, case-sensitive order; T is reserved for -1. */
  public List<String> variables() { return variables; }

  public int evaluate(Map<String, Integer> values) {
    Objects.requireNonNull(values);
    for (String variable : variables) {
      if (values.get(variable) == null) throw new IllegalArgumentException("Missing variable " + variable);
      Trit.BUF(values.get(variable));
    }
    return root.evaluate(values);
  }

  /**
   * Compiles the complete decision tree. Input variables occupy cells 0..n-1
   * in variables() order; HALT leaves the result and head at cell n.
   * Inputs are preserved. This bounded compiler enumerates 3^n assignments.
   */
  public TernaryMachine.Program compile() {
    if (variables.size() > MAX_COMPILED_VARIABLES)
      throw new IllegalArgumentException("Tape compilation supports at most 6 variables (729 assignments)");
    Map<Integer, List<TernaryMachine.Rule>> rules = new HashMap<>();
    compileNode(0, new HashMap<>(), rules, new int[] {0});
    return new TernaryMachine.Program(rules);
  }

  private int compileNode(int depth, Map<String, Integer> values,
      Map<Integer, List<TernaryMachine.Rule>> rules, int[] next) {
    int state = next[0]++;
    List<TernaryMachine.Rule> row = new ArrayList<>();
    if (depth == variables.size()) {
      int result = evaluate(values);
      for (int read = -1; read <= 1; read++) row.add(new TernaryMachine.Rule(result, 0, TernaryMachine.HALT));
    } else {
      for (int read = -1; read <= 1; read++) {
        values.put(variables.get(depth), read);
        int child = compileNode(depth + 1, values, rules, next);
        row.add(new TernaryMachine.Rule(read, 1, child));
      }
    }
    rules.put(state, row);
    return state;
  }

  private static final class Parser {
    private final String source;
    private final Set<String> variables = new TreeSet<>();
    private int position;
    Parser(String source) { this.source = source; }
    void space() { while (position < source.length() && Character.isWhitespace(source.charAt(position))) position++; }
    IllegalArgumentException error(String message) {
      return new IllegalArgumentException(message + " at character " + (position + 1));
    }
    void expect(char character) {
      space();
      if (position == source.length() || source.charAt(position) != character) throw error("Expected '" + character + "'");
      position++;
    }
    Node expression(int depth) {
      if (depth > 64) throw error("Nesting exceeds 64 levels");
      space();
      int start = position;
      while (position < source.length() && (Character.isLetterOrDigit(source.charAt(position))
          || source.charAt(position) == '_' || (position == start && source.charAt(position) == '-'))) position++;
      if (start == position) throw error("Expected a trit, variable or operation");
      String token = source.substring(start, position);
      space();
      if (position < source.length() && source.charAt(position) == '(') {
        String operation = token.toUpperCase(Locale.ROOT);
        boolean unary = Arrays.asList(UNARY.split(" ")).contains(operation);
        if (!unary && !Arrays.asList(BINARY.split(" ")).contains(operation)) throw error("Unknown operation " + token);
        position++;
        Node left = expression(depth + 1);
        Node right;
        if (unary) {
          expect(')');
          return values -> Trit.unaryOperator(operation, left.evaluate(values));
        }
        expect(',');
        right = expression(depth + 1);
        expect(')');
        return values -> Trit.binaryOperator(operation, left.evaluate(values), right.evaluate(values));
      }
      if (token.equals("T") || token.equals("-1")) return values -> -1;
      if (token.equals("0")) return values -> 0;
      if (token.equals("1")) return values -> 1;
      if (!token.matches("[A-Za-z_][A-Za-z_0-9]*")) throw error("Invalid variable or trit " + token);
      variables.add(token);
      return values -> values.get(token);
    }
  }
}
