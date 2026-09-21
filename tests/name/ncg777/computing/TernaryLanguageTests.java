package name.ncg777.computing;

import static org.junit.Assert.*;
import java.math.BigInteger;
import java.util.Map;
import org.junit.Test;
import name.ncg777.maths.Trit;

public class TernaryLanguageTests {
  private BigInteger run(String source) {
    var execution=TernaryLanguage.compile(source).start(Map.of());
    assertTrue("Budget exhausted after "+execution.steps(),execution.run(20000000));
    return execution.integerResult();
  }
  @Test public void arithmetic() {
    for(int a=-2;a<=2;a++)for(int b=-2;b<=2;b++){
      assertEquals(BigInteger.valueOf(a+b),run("(+ "+a+" "+b+")"));
      assertEquals(BigInteger.valueOf(a-b),run("(- "+a+" "+b+")"));
      assertEquals(BigInteger.valueOf(a*b),run("(* "+a+" "+b+")"));
      assertEquals(BigInteger.valueOf(Integer.compare(a,b)),run("(compare "+a+" "+b+")"));
    }
  }
  @Test public void everyExistingGateRetainsItsSemantics() {
    for(String gate:TernaryExpression.UNARY.split(" "))for(int a=-1;a<=1;a++)
      assertEquals(BigInteger.valueOf(Trit.unaryOperator(gate,a)),run("("+gate+" "+a+")"));
    for(String gate:TernaryExpression.BINARY.split(" "))for(int a=-1;a<=1;a++)for(int b=-1;b<=1;b++)
      assertEquals(BigInteger.valueOf(Trit.binaryOperator(gate,a,b)),run("("+gate+" "+a+" "+b+")"));
  }
  @Test public void closuresAndRecursion() {
    assertEquals(BigInteger.valueOf(7),run("(let add (lambda (x) (lambda (y) (+ x y))) ((add 3) 4))"));
    assertEquals(BigInteger.valueOf(120),run("(def fact (n) (case (compare n 0) 0 1 (* n (fact (- n 1))))) (fact 5)"));
    assertEquals(BigInteger.valueOf(3),run("(let x 3 (let f (lambda () x) (let x 8 (f))))"));
  }
  @Test public void arbitraryPrecisionAndPause() {
    BigInteger value=BigInteger.ONE.shiftLeft(120);
    var execution=TernaryLanguage.compile("(inputs x) (+ x 1)").start(Map.of("x",value));
    assertFalse(execution.run(1));
    assertThrows(IllegalStateException.class,execution::integerResult);
    assertTrue(execution.run(20000000));
    assertEquals(value.add(BigInteger.ONE),execution.integerResult());
    var multiply=TernaryLanguage.compile("(inputs x) (* x -3)").start(Map.of("x",value));
    assertTrue(multiply.run(20000000));
    assertEquals(value.multiply(BigInteger.valueOf(-3)),multiply.integerResult());
  }

  @Test public void largerSignedArithmeticAndCanonicalZero() {
    for(int[] pair:new int[][]{{17,29},{-37,13},{81,-28},{-121,-9},{400,-400},{0,-100}}) {
      int a=pair[0],b=pair[1];
      assertEquals(BigInteger.valueOf((long)a*b),run("(* "+a+" "+b+")"));
      assertEquals(BigInteger.valueOf((long)a+b),run("(+ "+a+" "+b+")"));
      assertEquals(BigInteger.valueOf((long)a-b),run("(- "+a+" "+b+")"));
    }
    assertEquals(BigInteger.ONE,run("(ISZ (- 500 500))"));
    assertEquals(BigInteger.ONE,run("(case (- 100 100) T 1 0)"));
  }

  @Test public void mutualRecursionHigherOrderCallsAndLazyBranches() {
    String parity="(def even (n) (case (compare n 0) 0 1 (odd (- n 1)))) "
        +"(def odd (n) (case (compare n 0) 0 0 (even (- n 1)))) ";
    assertEquals(BigInteger.ONE,run(parity+"(even 8)"));
    assertEquals(BigInteger.ZERO,run(parity+"(even 7)"));
    assertEquals(BigInteger.valueOf(7),run("(def apply (f x y) (f x y)) (apply + 3 4)"));
    assertEquals(BigInteger.ONE,run("(def forever () (forever)) (case 0 (forever) 1 (forever))"));
    assertEquals(BigInteger.valueOf(9),run("(let x 4 (let f (lambda (y) (+ x y)) (let x 100 (f 5))))"));
    assertEquals(BigInteger.ONE,run("((lambda (f) (f T)) NOT)"));
    var function=TernaryLanguage.compile("(lambda (x) (+ x 1))").start(Map.of());
    assertTrue(function.run(100000)); assertTrue(function.functionResult());
    assertThrows(IllegalStateException.class,function::integerResult);
  }

  @Test public void tailCallsDoNotAccumulateContinuations() {
    String loop="(def loop (n acc) (case (compare n 0) acc acc (loop (- n 1) (+ acc 1)))) ";
    var small=TernaryLanguage.compile(loop+"(loop 3 0)").start(Map.of());
    var large=TernaryLanguage.compile(loop+"(loop 60 0)").start(Map.of());
    assertTrue(small.run(20000000)); assertTrue(large.run(100000000));
    assertEquals(BigInteger.valueOf(60),large.integerResult());
    assertEquals(small.maximumContinuationDepth(),large.maximumContinuationDepth());
    var nonTail=TernaryLanguage.compile("(def f (n) (case (compare n 0) 0 0 (+ 1 (f (- n 1))))) (f 10)").start(Map.of());
    assertTrue(nonTail.run(20000000));
    assertTrue(nonTail.maximumContinuationDepth()>large.maximumContinuationDepth());
  }

  @Test public void typeErrorsLimitsAndValidation() {
    for(String program: new String[]{"(1 2)","(+ 1)","(NOT 5)","(case 2 0 0 0)","(+ (lambda () 0) 1)","((lambda (x) x))"}) {
      var execution=TernaryLanguage.compile(program).start(Map.of());
      assertThrows(program,IllegalArgumentException.class,()->execution.run(1000000));
      assertThrows(IllegalStateException.class,()->execution.run(1));
    }
    for(String program:new String[]{"", "()", "(lambda (x x) x)","(def f (x) y) 0", "(let x x x)","(inputs a a) a", "1 2", "(NOT 1"})
      assertThrows(program,IllegalArgumentException.class,()->TernaryLanguage.compile(program));
    assertThrows(IllegalArgumentException.class,()->TernaryLanguage.compile("(inputs a) a").start(Map.of()));
    var overflow=TernaryLanguage.compile("(+ 13 1)").start(Map.of(),new TernaryLanguage.Limits(8192,3));
    assertThrows(IllegalStateException.class,()->overflow.run(1000000));
    var infinite=TernaryLanguage.compile("(def forever () (forever)) (forever)").start(Map.of());
    assertFalse(infinite.run(10000)); assertFalse(infinite.run(10000));
    assertTrue(infinite.maximumContinuationDepth()<=1);
    assertThrows(IllegalArgumentException.class,()->TernaryLanguage.compile("1000").start(Map.of(),new TernaryLanguage.Limits(8192,2)));
    var exhaustion=TernaryLanguage.compile("(def f (n) (f (+ n 1))) (f 0)").start(Map.of(),new TernaryLanguage.Limits(4096,20));
    assertThrows(IllegalStateException.class,()->exhaustion.run(10000000));
  }
}
