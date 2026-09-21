package name.ncg777.computing.apps;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import name.ncg777.computing.TernaryMachine;
import name.ncg777.computing.TernaryMachine.Program;
import name.ncg777.computing.TernaryMachine.Rule;
import name.ncg777.computing.structures.TritWord64;

/** Tiny deterministic program: rewrite a positive-trit run until the first zero. */
public final class TernaryMachineDemo {
  private TernaryMachineDemo() {}
  public static void main(String[] args) {
    var program = new Program(Map.of(0, List.of(
        new Rule(-1, 0, TernaryMachine.HALT),
        new Rule(0, 0, TernaryMachine.HALT),
        new Rule(-1, 1, 0))));
    var input = TritWord64.fromTrits(1, 1, 1);
    var machine = new TernaryMachine(program, input);
    System.out.println("Input:  " + input + " = " + input.value());
    System.out.println("Halted: " + machine.run(100) + " after " + machine.steps() + " steps");
    var output = machine.wordAt(BigInteger.ZERO);
    System.out.println("Output: " + output + " = " + output.value());
  }
}
