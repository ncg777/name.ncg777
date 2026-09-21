package name.ncg777.computing.apps;

import java.awt.BorderLayout;
import java.math.BigInteger;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import name.ncg777.computing.TernaryExpression;
import name.ncg777.computing.TernaryMachine;

/** Small truth-table workbench; every row also executes on the tape machine. */
public final class TernaryExpressionApp {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(TernaryExpressionApp::show);
  }

  private static void show() {
    JFrame frame = new JFrame("Ternary expressions");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    JTextField expression = new JTextField("AND(a, NOT(b))", 32);
    JButton evaluate = new JButton("Evaluate all inputs");
    JPanel controls = new JPanel(new BorderLayout(8, 8));
    controls.add(expression, BorderLayout.CENTER);
    controls.add(evaluate, BorderLayout.EAST);
    JTextArea help = new JTextArea("T = -1; 0 = neutral; 1 = +1. Variables are case-sensitive.\n"
        + "Examples: OR(a, NOT(a))   CONS(a,b)   SUM(a,b)   OR(ISN(a), ISP(b))\n"
        + "Unary: " + TernaryExpression.UNARY + "\nBinary: " + TernaryExpression.BINARY
        + "\nUp to 6 variables; every input combination is run on the tape machine.");
    help.setEditable(false);
    help.setLineWrap(true);
    help.setWrapStyleWord(true);
    JPanel top = new JPanel(new BorderLayout(8, 8));
    top.add(controls, BorderLayout.NORTH);
    top.add(help, BorderLayout.CENTER);
    JTable table = new JTable();
    JLabel status = new JLabel(" ");
    Runnable refresh = () -> {
      try {
        TernaryExpression parsed = TernaryExpression.parse(expression.getText());
        TernaryMachine.Program program = parsed.compile();
        int count = parsed.variables().size();
        Object[] columns = new Object[count + 2];
        for (int i = 0; i < count; i++) columns[i] = parsed.variables().get(i);
        columns[count] = "Result";
        columns[count + 1] = "Tape result";
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
          @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        int combinations = (int) Math.pow(3, count);
        for (int assignment = 0; assignment < combinations; assignment++) {
          var values = new HashMap<String, Integer>();
          var machine = new TernaryMachine(program);
          Object[] row = new Object[count + 2];
          int digits = assignment;
          for (int i = count - 1; i >= 0; i--) {
            int value = digits % 3 - 1;
            digits /= 3;
            values.put(parsed.variables().get(i), value);
            machine.write(BigInteger.valueOf(i), value);
            row[i] = format(value);
          }
          if (!machine.run(count + 1)) throw new IllegalStateException("Tape execution did not halt");
          int result = parsed.evaluate(values);
          int tapeResult = machine.read(BigInteger.valueOf(count));
          if (result != tapeResult) throw new IllegalStateException("Tape result disagrees with expression");
          row[count] = format(result);
          row[count + 1] = format(tapeResult);
          model.addRow(row);
        }
        table.setModel(model);
        status.setText(combinations + " inputs verified; " + program.rules().size()
            + " machine states; " + (count + 1) + " steps per input. Result at tape cell " + count + ".");
      } catch (IllegalArgumentException | IllegalStateException e) {
        table.setModel(new DefaultTableModel());
        status.setText(e.getMessage());
      }
    };
    evaluate.addActionListener(event -> refresh.run());
    expression.addActionListener(event -> refresh.run());
    JPanel root = new JPanel(new BorderLayout(8, 8));
    root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    root.add(top, BorderLayout.NORTH);
    root.add(new JScrollPane(table), BorderLayout.CENTER);
    root.add(status, BorderLayout.SOUTH);
    frame.setContentPane(root);
    frame.setSize(900, 600);
    frame.setLocationByPlatform(true);
    refresh.run();
    frame.setVisible(true);
  }

  private static String format(int value) { return value == -1 ? "T" : Integer.toString(value); }
}
