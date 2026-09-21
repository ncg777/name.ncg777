package name.ncg777.computing.apps;

import java.awt.BorderLayout;
import java.awt.Font;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import name.ncg777.computing.TernaryFunctional;

/** Editor and command-line runner for the small functional ternary language. */
public final class TernaryFunctionalApp {
  private static final String EXAMPLE = """
      ; Pick a branch for negative, neutral, or positive agreement.
      (inputs a b)
      (def agreement (x y)
        (CONS x y))

      (let answer (agreement a b)
        (case answer T 0 1))
      """;

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      SwingUtilities.invokeLater(TernaryFunctionalApp::show);
      return;
    }
    if (args.length < 2 || args.length > 3)
      throw new IllegalArgumentException("Usage: source.tfun maxSteps [\"a=T b=1\"] (no arguments opens the editor)");
    String source = Files.readString(Path.of(args[0]));
    System.out.println(run(source, args.length == 3 ? args[2] : "", Long.parseLong(args[1])));
  }

  private static Map<String, Integer> inputs(String text) {
    Map<String, Integer> result = new LinkedHashMap<>();
    if (text.isBlank()) return result;
    for (String assignment : text.strip().split("\\s+")) {
      String[] parts = assignment.split("=", -1);
      if (parts.length != 2) throw new IllegalArgumentException("Use input assignments such as a=T b=1");
      int value = switch (parts[1]) {
        case "T", "-1" -> -1;
        case "0" -> 0;
        case "1" -> 1;
        default -> throw new IllegalArgumentException("Input values must be T, -1, 0 or 1");
      };
      if (result.putIfAbsent(parts[0], value) != null)
        throw new IllegalArgumentException("Duplicate input " + parts[0]);
    }
    return result;
  }

  private static String run(String source, String inputText, long budget) {
    if (budget < 0) throw new IllegalArgumentException("Step budget must be nonnegative");
    var compiled = TernaryFunctional.compile(source);
    var machine = compiled.newMachine(inputs(inputText));
    boolean halted = machine.run(budget);
    String outcome = halted ? "Result: " + format(compiled.result(machine))
        : "Step budget exhausted. Execution is incomplete; no result is available.";
    return outcome + "\n" + machine.steps() + " steps executed; " + compiled.program().rules().size()
        + " compiled states; " + compiled.allocatedCells() + " tape cells allocated.\n"
        + "Result cell: " + compiled.resultCell() + ". Input cells are preserved.";
  }

  private static String format(int value) { return value == -1 ? "T (-1)" : Integer.toString(value); }

  private static void show() {
    JFrame frame = new JFrame("Ternary functional language");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    JTextArea source = new JTextArea(EXAMPLE, 16, 70);
    source.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
    JTextField values = new JTextField("a=1 b=1", 22);
    JSpinner budget = new JSpinner(new SpinnerNumberModel(100000, 1, 10000000, 10000));
    JButton run = new JButton("Compile and run");
    JPanel controls = new JPanel();
    controls.add(new JLabel("Inputs:"));
    controls.add(values);
    controls.add(new JLabel("Maximum steps:"));
    controls.add(budget);
    controls.add(run);
    JTextArea output = new JTextArea("Enter a program, then choose Compile and run.", 5, 70);
    output.setEditable(false);
    output.setLineWrap(true);
    output.setWrapStyleWord(true);
    JLabel help = new JLabel("<html>Values: T, 0, 1. Calls: (AND x y). Local value: (let x value body).<br>"
        + "Function: (def name (x y) body). Branch: (case value negative neutral positive). Comments begin with ;.</html>");
    JPanel top = new JPanel(new BorderLayout(8, 8));
    top.add(help, BorderLayout.NORTH);
    top.add(controls, BorderLayout.SOUTH);
    JPanel root = new JPanel(new BorderLayout(8, 8));
    root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    root.add(top, BorderLayout.NORTH);
    root.add(new JScrollPane(source), BorderLayout.CENTER);
    root.add(new JScrollPane(output), BorderLayout.SOUTH);
    run.addActionListener(event -> {
      String programText = source.getText(), inputText = values.getText();
      long maxSteps = ((Number) budget.getValue()).longValue();
      run.setEnabled(false);
      output.setText("Compiling and running…");
      new SwingWorker<String, Void>() {
        @Override protected String doInBackground() { return TernaryFunctionalApp.run(programText, inputText, maxSteps); }
        @Override protected void done() {
          try { output.setText(get()); }
          catch (InterruptedException e) { Thread.currentThread().interrupt(); output.setText("Interrupted."); }
          catch (ExecutionException e) { output.setText(e.getCause().getMessage()); }
          finally { run.setEnabled(true); }
        }
      }.execute();
    });
    frame.setContentPane(root);
    frame.pack();
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
  }
}
