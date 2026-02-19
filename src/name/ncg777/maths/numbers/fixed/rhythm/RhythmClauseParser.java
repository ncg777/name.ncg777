package name.ncg777.maths.numbers.fixed.rhythm;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import name.ncg777.maths.numbers.BinaryNatural;

/**
 * Recursive-descent parser for Boolean predicate clauses over rhythms.
 *
 * <h3>Grammar</h3>
 * <pre>
 *   expr   := term  ( 'OR'  term  )*
 *   term   := factor ( 'AND' factor )*
 *   factor := 'NOT' factor
 *           | '(' expr ')'
 *           | atom
 *   atom   := NAME ( '(' INT ')' )?
 * </pre>
 *
 * <p>Names are matched to {@link RhythmPredicateRegistry} (case-insensitive).
 * The special atom {@code TRUE} or {@code ALL} yields a predicate that always returns true.
 * The special atom {@code FALSE} or {@code NONE} yields a predicate that always returns false.
 *
 * <h3>Examples</h3>
 * <pre>
 *   "EVEN AND NOT PERIODIC"
 *   "ENTROPIC OR HAS_NO_GAPS"
 *   "(EVEN OR ODDITY) AND MINIMUM_GAP(2)"
 *   "COPRIME_INTERVALS AND NOT LOW_ENTROPY AND MAXIMUM_GAP(4)"
 * </pre>
 */
public final class RhythmClauseParser {

  private RhythmClauseParser() {}

  // ─────────────────────────────────────────────────────────── tokeniser ──

  private enum TokKind { NAME, INT, LPAREN, RPAREN, AND, OR, NOT, EOF }

  private static final class Token {
    final TokKind kind;
    final String  text;
    Token(TokKind k, String t) { kind = k; text = t; }
    @Override public String toString() { return kind + "(" + text + ")"; }
  }

  private static List<Token> tokenise(String input) {
    List<Token> tokens = new ArrayList<>();
    int i = 0;
    while (i < input.length()) {
      char c = input.charAt(i);
      if (Character.isWhitespace(c)) { i++; continue; }
      if (c == '(') { tokens.add(new Token(TokKind.LPAREN, "(")); i++; continue; }
      if (c == ')') { tokens.add(new Token(TokKind.RPAREN, ")")); i++; continue; }
      if (Character.isDigit(c) || (c == '-' && i+1 < input.length() && Character.isDigit(input.charAt(i+1)))) {
        int start = i++;
        while (i < input.length() && Character.isDigit(input.charAt(i))) i++;
        tokens.add(new Token(TokKind.INT, input.substring(start, i)));
        continue;
      }
      if (Character.isLetter(c) || c == '_') {
        int start = i++;
        while (i < input.length() && (Character.isLetterOrDigit(input.charAt(i)) || input.charAt(i) == '_')) i++;
        String word = input.substring(start, i);
        switch (word.toUpperCase()) {
          case "AND": tokens.add(new Token(TokKind.AND, word)); break;
          case "OR":  tokens.add(new Token(TokKind.OR,  word)); break;
          case "NOT": tokens.add(new Token(TokKind.NOT, word)); break;
          default:    tokens.add(new Token(TokKind.NAME, word)); break;
        }
        continue;
      }
      throw new IllegalArgumentException("Unexpected character '" + c + "' at position " + i);
    }
    tokens.add(new Token(TokKind.EOF, ""));
    return tokens;
  }

  // ──────────────────────────────────────────────────────────── parser ──

  private static final class Parser {
    private final List<Token> tokens;
    private int pos = 0;

    Parser(List<Token> tokens) { this.tokens = tokens; }

    Token peek() { return tokens.get(pos); }
    Token consume() { return tokens.get(pos++); }
    Token expect(TokKind kind) {
      Token t = consume();
      if (t.kind != kind)
        throw new IllegalArgumentException("Expected " + kind + " but got " + t);
      return t;
    }

    Predicate<BinaryNatural> parseExpr() {
      Predicate<BinaryNatural> left = parseTerm();
      while (peek().kind == TokKind.OR) {
        consume(); // OR
        Predicate<BinaryNatural> right = parseTerm();
        final Predicate<BinaryNatural> l = left, r = right;
        left = bn -> l.test(bn) || r.test(bn);
      }
      return left;
    }

    Predicate<BinaryNatural> parseTerm() {
      Predicate<BinaryNatural> left = parseFactor();
      while (peek().kind == TokKind.AND) {
        consume(); // AND
        Predicate<BinaryNatural> right = parseFactor();
        final Predicate<BinaryNatural> l = left, r = right;
        left = bn -> l.test(bn) && r.test(bn);
      }
      return left;
    }

    Predicate<BinaryNatural> parseFactor() {
      if (peek().kind == TokKind.NOT) {
        consume();
        Predicate<BinaryNatural> inner = parseFactor();
        return bn -> !inner.test(bn);
      }
      if (peek().kind == TokKind.LPAREN) {
        consume();
        Predicate<BinaryNatural> inner = parseExpr();
        expect(TokKind.RPAREN);
        return inner;
      }
      return parseAtom();
    }

    Predicate<BinaryNatural> parseAtom() {
      Token t = expect(TokKind.NAME);
      String name = t.text.toUpperCase();

      // Special universals
      if (name.equals("TRUE") || name.equals("ALL"))   return bn -> true;
      if (name.equals("FALSE") || name.equals("NONE")) return bn -> false;

      // Check for optional parameter
      Integer arg = null;
      if (peek().kind == TokKind.LPAREN) {
        consume();
        Token argTok = expect(TokKind.INT);
        arg = Integer.parseInt(argTok.text);
        expect(TokKind.RPAREN);
      }
      return RhythmPredicateRegistry.get(name, arg);
    }
  }

  /**
   * Parses a predicate clause string and returns a {@link Predicate} over {@link BinaryNatural}.
   *
   * @param clause  the clause string, e.g. {@code "EVEN AND NOT PERIODIC"}
   * @return        the compiled predicate
   */
  public static Predicate<BinaryNatural> parse(String clause) {
    if (clause == null || clause.isBlank()) return bn -> true;
    List<Token> tokens = tokenise(clause);
    Parser parser = new Parser(tokens);
    Predicate<BinaryNatural> result = parser.parseExpr();
    if (parser.peek().kind != TokKind.EOF)
      throw new IllegalArgumentException("Unexpected token after end of clause: " + parser.peek());
    return result;
  }
}
