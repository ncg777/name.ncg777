package name.ncg777.computing.apps;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import name.ncg777.computing.TernaryMachine;
import name.ncg777.computing.TernaryMachine.Program;
import name.ncg777.computing.structures.TritWord64;

/** Runs a textual transition program against a balanced-ternary word. */
public final class TernaryMachineApp {
  private TernaryMachineApp() {}
  public static void main(String[] args) throws Exception {
    if (args.length < 2 || args.length > 3) {
      System.err.println("Usage: TernaryMachineApp program.tri input[T01] [maxSteps]");
      System.exit(2);
    }
    var program = Program.parse(Files.readString(Path.of(args[0])));
    var machine = new TernaryMachine(program, TritWord64.parse(args[1]));
    long budget = args.length == 3 ? Long.parseLong(args[2]) : 100_000;
    boolean halted = machine.run(budget);
    System.out.println("halted=" + halted + " steps=" + machine.steps() + " state=" + machine.state()
        + " head=" + machine.head() + " nonzeroCells=" + machine.nonzeroCells());
    var output = machine.wordAt(BigInteger.ZERO);
    System.out.println("word=" + output + " value=" + output.value());
  }
}
