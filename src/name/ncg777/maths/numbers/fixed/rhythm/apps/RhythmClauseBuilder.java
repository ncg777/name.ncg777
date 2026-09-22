package name.ncg777.maths.numbers.fixed.rhythm.apps;

import java.awt.*;
import javax.swing.*;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmPredicateRegistry;
import name.ncg777.maths.numbers.fixed.rhythm.exploration.RhythmExplorer;

/** Registry-backed editor. Text remains directly usable in the CLI. */
public final class RhythmClauseBuilder extends JPanel {
  private final JTextArea expression = new JTextArea(3, 34);
  private final JComboBox<RhythmPredicateRegistry.Descriptor> predicate = new JComboBox<>(RhythmPredicateRegistry.descriptors().toArray(RhythmPredicateRegistry.Descriptor[]::new));
  private final JSpinner argument = new JSpinner(new SpinnerNumberModel(4, 0, 512, 1));
  private final JTextArea help = new JTextArea(2, 30);
  public RhythmClauseBuilder(String clause) {
    super(new BorderLayout(4, 4));
    expression.setText(clause);
    expression.setLineWrap(true); expression.setWrapStyleWord(true);
    expression.setName("Predicate expression");
    expression.getAccessibleContext().setAccessibleName("Predicate expression");
    add(new JScrollPane(expression), BorderLayout.CENTER);
    JPanel tools = new JPanel(new GridLayout(0, 1, 2, 2));
    JPanel atom = new JPanel(new BorderLayout(4, 0));
    atom.add(predicate, BorderLayout.CENTER); atom.add(argument, BorderLayout.EAST);
    JButton insert = new JButton("Insert predicate");
    tools.add(atom); tools.add(insert);
    JPanel logic = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
    for (String token : new String[]{"AND", "OR", "NOT", "(" , ")"}) {
      JButton button = new JButton(token);
      button.addActionListener(e -> expression.replaceSelection(" " + token + " "));
      logic.add(button);
    }
    JButton group = new JButton("Group selection");
    group.addActionListener(e -> {
      if (expression.getSelectedText() == null) expression.setText("(" + expression.getText() + ")");
      else expression.replaceSelection("(" + expression.getSelectedText() + ")");
    });
    logic.add(group); tools.add(logic);
    help.setFont(help.getFont().deriveFont(11f));
    help.setEditable(false); help.setOpaque(false); help.setLineWrap(true); help.setWrapStyleWord(true);
    tools.add(help); add(tools, BorderLayout.SOUTH);
    predicate.addActionListener(e -> updateArgument());
    insert.addActionListener(e -> {
      var d = (RhythmPredicateRegistry.Descriptor) predicate.getSelectedItem();
      expression.replaceSelection(d.name() + (d.parameter() ? "(" + argument.getValue() + ")" : ""));
      expression.requestFocusInWindow();
    });
    updateArgument();
  }
  private void updateArgument() {
    var d = (RhythmPredicateRegistry.Descriptor) predicate.getSelectedItem();
    argument.setEnabled(d.parameter());
    argument.setModel(new SpinnerNumberModel(d.initial(), d.minimum(), 512, 1));
    help.setText(d.description());
  }
  public void refreshControls() { argument.setEnabled(((RhythmPredicateRegistry.Descriptor)predicate.getSelectedItem()).parameter()); }
  public String clause() { return expression.getText().trim(); }
  public void setClause(String clause) { expression.setText(clause); }
  public void validateClause(int steps) { RhythmExplorer.rule(clause(), steps); }
}
