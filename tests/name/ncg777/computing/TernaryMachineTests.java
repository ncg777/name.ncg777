package name.ncg777.computing;

import static org.junit.Assert.*;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import name.ncg777.computing.TernaryMachine.Program;
import name.ncg777.computing.TernaryMachine.Rule;
import name.ncg777.computing.structures.TritWord64;

public class TernaryMachineTests {
  @Test public void wordRepresentsEntireBalancedRange() {
    assertEquals(new BigInteger("3433683820292512484657849089281"), TritWord64.MODULUS);
    assertEquals(new BigInteger("1716841910146256242328924544640"), TritWord64.MAX);
    assertEquals(TritWord64.MAX, TritWord64.fromTrits(java.util.Collections.nCopies(64, 1).stream().mapToInt(Integer::intValue).toArray()).value());
    assertEquals(TritWord64.MAX.negate(), new TritWord64(TritWord64.MAX).negate().value());
    assertEquals(64, TritWord64.zero().toString().length());
  }

  @Test public void wordCarriesAndWrapsInBaseThree() {
    TritWord64 two = TritWord64.fromTrits(-1, 1);
    assertEquals(BigInteger.valueOf(2), two.value());
    assertEquals(1, two.trit(1)); assertEquals(-1, two.trit(0));
    assertEquals(BigInteger.valueOf(3), two.addWrapped(TritWord64.fromTrits(1)).value());
    assertEquals(TritWord64.MAX.negate(), new TritWord64(TritWord64.MAX).addWrapped(TritWord64.fromTrits(1)).value());
    assertEquals(0, two.withTrit(1, 0).trit(1));
    assertEquals(two, TritWord64.parse("1T"));
  }

  @Test(expected=IllegalArgumentException.class) public void rejectsOutOfRangeWord() {
    new TritWord64(TritWord64.MAX.add(BigInteger.ONE));
  }

  /** Rewrites a run of positive trits, stopping on zero. This exercises read-based branching and tape motion. */
  @Test public void machineFollowsRulesAndReadsBackWord() {
    Program program = new Program(Map.of(0, List.of(
        new Rule(-1, 0, TernaryMachine.HALT),
        new Rule(0, 0, TernaryMachine.HALT),
        new Rule(-1, 1, 0))));
    var machine = new TernaryMachine(program, TritWord64.fromTrits(1, 1, 1));
    assertFalse(machine.run(2)); assertEquals(2, machine.steps());
    assertTrue(machine.run(2)); assertEquals(4, machine.steps());
    assertEquals(BigInteger.valueOf(3), machine.head());
    assertEquals(TritWord64.fromTrits(-1, -1, -1), machine.wordAt(BigInteger.ZERO));
    assertFalse(machine.step());
  }

  @Test public void tapeCanMovePastEitherEndOfAWord() {
    Program program = new Program(Map.of(0, List.of(
        new Rule(0, -1, TernaryMachine.HALT),
        new Rule(1, -1, TernaryMachine.HALT),
        new Rule(0, -1, TernaryMachine.HALT))));
    var machine = new TernaryMachine(program);
    assertTrue(machine.run(1));
    assertEquals(BigInteger.valueOf(-1), machine.head());
    machine.write(BigInteger.valueOf(-1), -1);
    assertEquals(-1, machine.read(BigInteger.valueOf(-1)));
    machine.write(BigInteger.valueOf(-1), 0);
    assertEquals(1, machine.nonzeroCells());
    machine.write(BigInteger.ZERO, 0);
    assertEquals(0, machine.nonzeroCells());
  }

  @Test(expected=IllegalArgumentException.class) public void rejectsIncompleteProgram() {
    new Program(Map.of(0, List.of(new Rule(0, 0, -1))));
  }

  @Test public void parsesAndRunsTextProgram() {
    String source = "# state read write move next\n0 T T 0 HALT\n0 0 0 0 HALT\n0 1 T 1 0\n";
    var machine = new TernaryMachine(Program.parse(source), TritWord64.parse("111"));
    assertTrue(machine.run(10));
    assertEquals(TritWord64.parse("TTT"), machine.wordAt(BigInteger.ZERO));
  }

  @Test(expected=IllegalArgumentException.class) public void rejectsDuplicateRules() {
    Program.parse("0 0 0 0 HALT\n0 0 1 0 HALT");
  }
}
