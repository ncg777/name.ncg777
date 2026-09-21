package name.ncg777.computing;

import static org.junit.Assert.*;
import java.math.BigInteger;
import java.util.Map;
import java.util.List;
import org.junit.Test;
import name.ncg777.maths.Trit;

public class TernaryExpressionTests {
  private void verify(String source, Map<String, Integer> values, int expected) {
    var expression = TernaryExpression.parse(source);
    assertEquals(expected, expression.evaluate(values));
    var machine = new TernaryMachine(expression.compile());
    for (int i = 0; i < expression.variables().size(); i++)
      machine.write(BigInteger.valueOf(i), values.get(expression.variables().get(i)));
    int count = expression.variables().size();
    machine.write(BigInteger.valueOf(count), 1);
    assertTrue(machine.run(count + 1));
    assertEquals(count + 1, machine.steps());
    assertEquals(BigInteger.valueOf(count), machine.head());
    assertEquals(expected, machine.read(machine.head()));
    for (int i = 0; i < count; i++)
      assertEquals((int) values.get(expression.variables().get(i)), machine.read(BigInteger.valueOf(i)));
  }

  @Test public void everyGateMatchesExistingTablesOnEveryInput() {
    for (String operation : TernaryExpression.UNARY.split(" "))
      for (int a = -1; a <= 1; a++)
        verify(operation + "(a)", Map.of("a", a), Trit.unaryOperator(operation, a));
    for (String operation : TernaryExpression.BINARY.split(" "))
      for (int a = -1; a <= 1; a++) for (int b = -1; b <= 1; b++)
        verify(operation + "(a,b)", Map.of("a", a, "b", b), Trit.binaryOperator(operation, a, b));
  }

  @Test public void nestedExpressionsAndConstants() {
    for (int a = -1; a <= 1; a++) for (int b = -1; b <= 1; b++)
      verify(" and(a, NOT(b)) ", Map.of("a", a, "b", b), Math.min(a, -b));
    verify("OR(a, NOT(a))", Map.of("a", 0), 0);
    verify("SUM(1,1)", Map.of(), -1);
    verify("AND(T, -1)", Map.of(), -1);
    verify("0", Map.of(), 0);
    assertEquals(List.of("a", "b"), TernaryExpression.parse("OR(b,a)").variables());
  }

  @Test public void rejectsMalformedOrIncompleteInput() {
    for (String source : List.of("", "2", "-2", "AND(a)", "NOT(a,b)", "AND(a,b", "a b", "WHAT(a)", "a()", "(a)"))
      assertThrows(source, IllegalArgumentException.class, () -> TernaryExpression.parse(source));
    var expression = TernaryExpression.parse("a");
    assertThrows(IllegalArgumentException.class, () -> expression.evaluate(Map.of()));
    assertThrows(IllegalArgumentException.class, () -> expression.evaluate(Map.of("a", 2)));
    assertThrows(IllegalArgumentException.class, () -> TernaryExpression.parse("NOT(".repeat(66) + "a" + ")".repeat(66)));
    assertThrows(IllegalArgumentException.class, () -> TernaryExpression.parse(" ".repeat(4097)));
  }

  @Test public void compilationBudgetDoesNotLimitDirectEvaluation() {
    var expression = TernaryExpression.parse("OR(a,OR(b,OR(c,OR(d,OR(e,OR(f,g))))))");
    assertEquals(0, expression.evaluate(Map.of("a",0,"b",0,"c",0,"d",0,"e",0,"f",0,"g",0)));
    assertThrows(IllegalArgumentException.class, expression::compile);
    var six = TernaryExpression.parse("OR(a,OR(b,OR(c,OR(d,OR(e,f)))))");
    var program = six.compile();
    assertEquals(1093, program.rules().size());
    for (int assignment = 0; assignment < 729; assignment++) {
      var machine = new TernaryMachine(program);
      int digits = assignment, expected = -1;
      for (int i = 0; i < 6; i++) {
        int value = digits % 3 - 1;
        digits /= 3;
        machine.write(BigInteger.valueOf(i), value);
        expected = Math.max(expected, value);
      }
      assertTrue(machine.run(7));
      assertEquals(expected, machine.read(BigInteger.valueOf(6)));
    }
  }
}
