package name.ncg777.computing.apps;

import java.awt.BorderLayout;
import java.awt.Font;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import name.ncg777.computing.TernaryLanguage;

/** Editor and command-line runner for the small functional ternary language. */
public final class TernaryFunctionalApp {
  private static final String EXAMPLE = """
      ; Integers, recursion, and functions returned as values.
      (inputs n)
      (def factorial (x)
        (case (compare x 0)
          0
          1
          (* x (factorial (- x 1)))))

      (let add (lambda (x) (lambda (y) (+ x y)))
        ((add (factorial n)) 1))
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

  private static Map<String, BigInteger> inputs(String text) {
    Map<String, BigInteger> result = new LinkedHashMap<>();
    if (text.isBlank()) return result;
    for (String assignment : text.strip().split("\\s+")) {
      String[] parts = assignment.split("=", -1);
      if (parts.length != 2) throw new IllegalArgumentException("Use input assignments such as a=T b=1");
      if(parts[1].length()>4096)throw new IllegalArgumentException("Input integer text is too long");
      BigInteger value = parts[1].equals("T") ? BigInteger.valueOf(-1) : new BigInteger(parts[1]);
      if (result.putIfAbsent(parts[0], value) != null)
        throw new IllegalArgumentException("Duplicate input " + parts[0]);
    }
    return result;
  }

  private static String run(String source, String inputText, long budget) {
    if (budget < 0) throw new IllegalArgumentException("Step budget must be nonnegative");
    var compiled = TernaryLanguage.compile(source);
    var execution = compiled.start(inputs(inputText));
    boolean halted = execution.run(budget);
    String outcome = halted ? "Result: " + (execution.functionResult() ? "<function>" : execution.integerResult())
        : "Step budget exhausted. Execution is incomplete; no result is available.";
    return outcome + "\n" + execution.steps() + " tape steps; " + execution.allocatedCells()
        + " tape cells allocated; maximum pending continuations: " + execution.maximumContinuationDepth()
        + ".\nLimits: 131072 tape cells and 256 trits per integer. No general heap garbage collection.";
  }

  private static void show() {
    JFrame frame = new JFrame("Ternary functional language");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    JTextArea source = new JTextArea(EXAMPLE, 16, 70);
    source.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
    JTextField values = new JTextField("n=5", 22);
    JSpinner budget = new JSpinner(new SpinnerNumberModel(10000000, 1, 100000000, 100000));
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
    JLabel help = new JLabel("<html>Integers: (+ x y), (- x y), (* x y), (compare x y). Trit gates: (AND x y).<br>"
        + "Functions: (def f (x) body), (lambda (x) body). Branch: (case trit negative neutral positive).</html>");
    JPanel top = new JPanel(new BorderLayout(8, 8));
    top.add(help, BorderLayout.NORTH);
    top.add(controls, BorderLayout.SOUTH);
    JPanel root = new JPanel(new BorderLayout(8, 8));
    root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    root.add(top, BorderLayout.NORTH);
    root.add(new JScrollPane(source), BorderLayout.CENTER);
    root.add(new JScrollPane(output), BorderLayout.SOUTH);
    run.addActionListener(event -> {
      try { budget.commitEdit(); }
      catch (java.text.ParseException e) { output.setText("Enter a valid maximum step count."); return; }
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
