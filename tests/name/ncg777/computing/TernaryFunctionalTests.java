package name.ncg777.computing;

import static org.junit.Assert.*;
import java.math.BigInteger;
import java.util.*;
import org.junit.Test;
import name.ncg777.maths.Trit;

public class TernaryFunctionalTests {
  private int run(String source, Map<String, Integer> inputs) {
    var compiled = TernaryFunctional.compile(source);
    var machine = compiled.newMachine(inputs);
    assertTrue(machine.run(1000000));
    assertEquals(BigInteger.valueOf(compiled.resultCell()), machine.head());
    for (int i = 0; i < compiled.inputs().size(); i++)
      assertEquals((int) inputs.get(compiled.inputs().get(i)), machine.read(BigInteger.valueOf(i)));
    return compiled.result(machine);
  }

  @Test public void allExistingGatesRunOnTape() {
    for (String operation : TernaryExpression.UNARY.split(" "))
      for (int a = -1; a <= 1; a++)
        assertEquals(Trit.unaryOperator(operation, a), run("(inputs a) (" + operation + " a)", Map.of("a", a)));
    for (String operation : TernaryExpression.BINARY.split(" "))
      for (int a = -1; a <= 1; a++) for (int b = -1; b <= 1; b++)
        assertEquals(Trit.binaryOperator(operation, a, b),
            run("(inputs a b) (" + operation + " a b)", Map.of("a", a, "b", b)));
  }

  @Test public void constantsCompositionAndFreshExecutions() {
    assertEquals(-1, run("T", Map.of()));
    assertEquals(0, run("0", Map.of()));
    assertEquals(1, run("1", Map.of()));
    for (int a = -1; a <= 1; a++) for (int b = -1; b <= 1; b++) for (int c = -1; c <= 1; c++)
      assertEquals(Math.max(Math.min(a, -b), c),
          run("(inputs a b c) (or (and a (not b)) c)", Map.of("a", a, "b", b, "c", c)));
    var compiled = TernaryFunctional.compile("(inputs a) (NOT a)");
    var first = compiled.newMachine(Map.of("a", -1));
    var second = compiled.newMachine(Map.of("a", 1));
    assertTrue(first.run(1000));
    assertTrue(second.run(1000));
    assertEquals(1, compiled.result(first));
    assertEquals(-1, compiled.result(second));
  }

  @Test public void lexicalBindingsAndFunctionCalls() {
    assertEquals(1, run("(let x T (let x (NOT x) x))", Map.of()));
    assertEquals(-1, run("(let x T (AND (let x 1 x) x))", Map.of()));
    assertEquals(1, run("(def identity (x) x) (let x T (identity 1))", Map.of()));
    assertEquals(0, run("(def forward (x) (later x)) (def later (y) (NOT y)) (forward 0)", Map.of()));
    assertEquals(1, run("(def one () 1) (one)", Map.of()));
    assertEquals(-1, run("(def flip (x) (NOT x)) (def twice (y) (flip (flip y))) (twice T)", Map.of()));
    assertEquals(-1, run("(def choose (x y) x) (let x T (choose x (let x 1 x)))", Map.of()));
  }

  @Test public void branchSelectionIsLazyAtRuntime() {
    String expensive = "(NOT (NOT (NOT (NOT (NOT (NOT 1))))))";
    String source = "(inputs a) (case a T 0 " + expensive + ")";
    var compiled = TernaryFunctional.compile(source);
    long[] steps = new long[3];
    for (int a = -1; a <= 1; a++) {
      var machine = compiled.newMachine(Map.of("a", a));
      assertTrue(machine.run(100000));
      assertEquals(a, compiled.result(machine));
      steps[a + 1] = machine.steps();
    }
    assertTrue("The positive branch should execute its extra operations", steps[2] > steps[0] + 20);
    assertEquals(1, run("(case T 1 T 0)", Map.of()));
    assertEquals(-1, run("(case 0 1 T 0)", Map.of()));
    assertEquals(0, run("(case 1 1 T 0)", Map.of()));
  }

  @Test public void supportsManyInputsWithoutTruthTableExpansion() {
    Map<String, Integer> values = new LinkedHashMap<>();
    StringBuilder source = new StringBuilder("(inputs");
    for (int i = 0; i < 32; i++) { source.append(" x").append(i); values.put("x" + i, i == 31 ? 1 : 0); }
    source.append(") (NOT x31)");
    assertEquals(-1, run(source.toString(), values));
    assertTrue(TernaryFunctional.compile(source.toString()).program().rules().size() < 1000);
  }

  @Test public void budgetExhaustionIsNotAResultAndCanResume() {
    var compiled = TernaryFunctional.compile("(SUM 1 1)");
    var machine = compiled.newMachine(Map.of());
    assertFalse(machine.run(1));
    assertThrows(IllegalStateException.class, () -> compiled.result(machine));
    assertTrue(machine.run(10000));
    assertEquals(-1, compiled.result(machine));
  }

  @Test public void rejectsInvalidProgramsIncludingUnusedDefinitions() {
    for (String source : List.of("", "()", ")", "(NOT 1", "2", "a", "(AND 1)", "(NOT 1 0)",
        "(case 1 0 1)", "(inputs a a) a", "(inputs a) (inputs b) a", "(inputs)", "1 0",
        "(def f (x x) x) 0", "(def f () 0) (def f () 1) 0", "(def NOT (x) x) 0",
        "(def f (x) y) 0", "(def f (x) x) (f)", "(inputs a) (def f () a) (f)",
        "(def f () (f)) 0", "(def f () (g)) (def g () (f)) 0",
        "(let x x x)", "(let T 0 1)", "(case 0 missing 1 0)", "(missing 1)"))
      assertThrows(source, IllegalArgumentException.class, () -> TernaryFunctional.compile(source));
  }

  @Test public void validatesInputsAndResourceLimits() {
    var compiled = TernaryFunctional.compile("(inputs a) a");
    assertThrows(IllegalArgumentException.class, () -> compiled.newMachine(Map.of()));
    assertThrows(IllegalArgumentException.class, () -> compiled.newMachine(Map.of("a", 2)));
    assertThrows(IllegalArgumentException.class, () -> compiled.newMachine(Map.of("a", 0, "b", 1)));
    assertThrows(IllegalArgumentException.class, () -> TernaryFunctional.compile(" ".repeat(16385)));
    assertThrows(IllegalArgumentException.class, () -> TernaryFunctional.compile("(NOT ".repeat(66) + "0" + ")".repeat(66)));
    assertThrows(IllegalArgumentException.class, () -> TernaryFunctional.compile("(inputs " +
        String.join(" ", java.util.stream.IntStream.range(0,33).mapToObj(i -> "x" + i).toList()) + ") 0"));
    StringBuilder exponential = new StringBuilder("(def f0 (x) (NOT x)) ");
    for (int i = 1; i < 12; i++) exponential.append("(def f").append(i).append(" (x) (AND (f")
        .append(i - 1).append(" x) (f").append(i - 1).append(" x))) ");
    exponential.append("(f11 0)");
    assertThrows(IllegalArgumentException.class, () -> TernaryFunctional.compile(exponential.toString()));
    assertEquals(1, run("; comment\n (NOT -1) ; trailing comment", Map.of()));
  }
}
