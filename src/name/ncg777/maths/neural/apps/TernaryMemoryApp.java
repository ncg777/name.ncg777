package name.ncg777.maths.neural.apps;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import name.ncg777.maths.neural.TernaryAssociativeMemory;
import name.ncg777.maths.neural.TernaryAssociativeMemory.Candidate;

/** A small visual comparison of associative recall methods. */
public final class TernaryMemoryApp {
  private final JFrame frame = new JFrame("Ternary associative memory");
  private final JTextArea patterns = new JTextArea("11T0\n11T0\nTT10\nTT10\n0000\n", 8, 18);
  private final JTextField cue = new JTextField("1?T0", 24);
  private final JSpinner epochs = new JSpinner(new SpinnerNumberModel(200, 1, 500, 25));
  private final JSpinner error = new JSpinner(new SpinnerNumberModel(0, 0, 50, 5));
  private final JSpinner hidden = new JSpinner(new SpinnerNumberModel(1, 0, 64, 1));
  private final JButton example = new JButton("Load 64-trit example"), cancel = new JButton("Cancel");
  private final JButton train = new JButton("Train memories"), recall = new JButton("Recall cue"), test = new JButton("Test 200 damaged cues");
  private final JTable spin = new JTable(), nearest = new JTable();
  private final JTextArea status = new JTextArea("Enter patterns, then train the memory.", 5, 80);
  private TernaryAssociativeMemory memory;
  private SwingWorker<?, ?> worker;

  public static void main(String[] args) { SwingUtilities.invokeLater(() -> new TernaryMemoryApp().show()); }

  private void show() {
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    patterns.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 17));
    cue.setFont(patterns.getFont());
    status.setEditable(false); status.setLineWrap(true); status.setWrapStyleWord(true);
    JPanel entry = new JPanel(new BorderLayout(8, 8));
    entry.setBorder(BorderFactory.createTitledBorder("Stored patterns: one per line, 1–64 trits each"));
    entry.add(new JScrollPane(patterns), BorderLayout.CENTER);
    JPanel training = new JPanel(); training.add(new JLabel("Training epochs:")); training.add(epochs); training.add(train);
    entry.add(training, BorderLayout.SOUTH);
    JPanel actions = new JPanel(new GridLayout(0, 1, 4, 4));
    actions.add(new JLabel("T = −1, 0 = neutral, 1 = +1. Only ? means unknown."));
    JPanel examples = new JPanel(); examples.add(example); examples.add(cancel); actions.add(examples);
    actions.add(new JLabel("1–6 trits: exact. 7–64 trits: sampled training and approximate recall."));
    JPanel cueRow = new JPanel(); cueRow.add(new JLabel("Cue:")); cueRow.add(cue); cueRow.add(recall); actions.add(cueRow);
    JPanel errorRow = new JPanel(); errorRow.add(new JLabel("Chance each visible trit is wrong (%):")); errorRow.add(error); actions.add(errorRow);
    actions.add(new JLabel("At 0%, the spin model keeps visible values fixed."));
    JPanel testRow = new JPanel(); testRow.add(new JLabel("Hide this many positions:")); testRow.add(hidden); testRow.add(test); actions.add(testRow);
    actions.add(new JLabel("The test damages stored memories; it does not test unseen patterns."));
    JPanel top = new JPanel(new BorderLayout(12, 8)); top.add(entry, BorderLayout.WEST); top.add(actions, BorderLayout.CENTER);
    JPanel tables = new JPanel(new GridLayout(1, 2, 10, 0));
    tables.add(tablePanel("Spin model — exact probability or sampled frequency", spin));
    tables.add(tablePanel("Nearest stored patterns — share among closest matches", nearest));
    JPanel root = new JPanel(new BorderLayout(10, 10)); root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    root.add(top, BorderLayout.NORTH); root.add(tables, BorderLayout.CENTER); root.add(new JScrollPane(status), BorderLayout.SOUTH);
    patterns.getDocument().addDocumentListener(new DocumentListener() {
      private void changed() {
        memory = null; clear(); recall.setEnabled(false); test.setEnabled(false);
        status.setText("Patterns changed. Train again to use these memories.");
      }
      public void insertUpdate(DocumentEvent e) { changed(); }
      public void removeUpdate(DocumentEvent e) { changed(); }
      public void changedUpdate(DocumentEvent e) { changed(); }
    });
    example.addActionListener(event -> {
      patterns.setText("1".repeat(32) + "T".repeat(32) + "\n" + "T".repeat(32) + "1".repeat(32) + "\n" + "0".repeat(64));
      cue.setText("1".repeat(16) + "?".repeat(16) + "T".repeat(16) + "?".repeat(16));
      hidden.setValue(32);
    });
    cancel.addActionListener(event -> { if (worker != null) worker.cancel(true); });
    train.addActionListener(event -> {
      try {
        commit(); int[][] rows = TernaryAssociativeMemory.parsePatterns(patterns.getText());
        int count = (Integer) epochs.getValue(); memory = null; clear();
        start(() -> TernaryAssociativeMemory.train(rows, count), fitted -> {
          memory = fitted;
          try { if (TernaryAssociativeMemory.parseCue(cue.getText()).values().length != fitted.width()) cue.setText("?".repeat(fitted.width())); }
          catch (IllegalArgumentException e) { cue.setText("?".repeat(fitted.width())); }
          hidden.setValue(Math.min((Integer) hidden.getValue(), fitted.width()));
          test.setText("Test " + fitted.defaultTrials() + " damaged cues");
          status.setText("Trained " + rows.length + " examples with " + fitted.width() + " trits using "
              + (fitted.approximate() ? "sampled learning. Recall will be approximate." : "exact learning.")
              + " Repeated rows count more often. Choose Recall cue or test damaged cues.");
        });
      } catch (RuntimeException e) { status.setText(e.getMessage()); }
    });
    recall.addActionListener(event -> {
      try {
        commit(); var query = TernaryAssociativeMemory.parseCue(cue.getText());
        double rate = ((Integer) error.getValue()) / 100.0; String queryText = cue.getText();
        start(() -> memory.recall(query, rate), result -> {
          fill(spin, result.spin()); fill(nearest, result.nearest());
          spin.getColumnModel().getColumn(1).setHeaderValue(result.approximate() ? "Sample %" : "Probability %");
          nearest.getColumnModel().getColumn(1).setHeaderValue("Match share %");
          status.setText("Cue: " + queryText + "; visible-error chance: " + Math.round(rate * 100) + "%. Showing up to 10 candidates per method.\n"
              + (result.approximate() ? "Approximate: " + result.samples() + " samples from four chains. Frequencies may miss modes and do not guarantee the best completion.\n" : "Exact probabilities over all completions.\n")
              + "The spin model may propose a pattern never stored. Nearest-match shares reflect example frequency among equally close memories; they are not calibrated probabilities.\n"
              + "Changed = disagreements with visible cue values. Near-equal scores indicate ambiguous recall.");
        });
      } catch (RuntimeException e) { status.setText(e.getMessage()); }
    });
    test.addActionListener(event -> {
      try {
        commit(); int masked = (Integer) hidden.getValue(); double rate = ((Integer) error.getValue()) / 100.0;
        int trials = memory.defaultTrials(); clear();
        start(() -> memory.benchmark(trials, masked, rate, 777), result -> status.setText((memory.approximate()
            ? "Approximate spin recall: 256 samples per cue; rankings depend on finite sampling.\n" : "Exact spin recall.\n") + String.format(
            "Stored-memory recovery: %d trials, %d hidden positions, %.0f%% visible-error chance; fixed seed 777.\n"
            + "Whole pattern recovered — spin: %d/%d (%.1f%%); nearest: %d/%d (%.1f%%).\n"
            + "Ambiguous top scores — spin: %d; nearest: %d. A single top choice is scored, so ties can count as failures.\n"
            + "These are new damaged cues from the training memories, not a held-out generalization test.",
            result.trials(), masked, rate * 100, result.spinCorrect(), result.trials(), 100.0 * result.spinCorrect() / result.trials(),
            result.nearestCorrect(), result.trials(), 100.0 * result.nearestCorrect() / result.trials(), result.spinTies(), result.nearestTies())));
      } catch (RuntimeException e) { status.setText(e.getMessage()); }
    });
    frame.addWindowListener(new WindowAdapter() {
      @Override public void windowClosed(WindowEvent event) { if (worker != null) worker.cancel(true); }
    });
    busy(false); frame.setContentPane(root); frame.setSize(1100, 700); frame.setLocationByPlatform(true); frame.setVisible(true);
  }
  private JPanel tablePanel(String title, JTable table) {
    JPanel panel = new JPanel(new BorderLayout()); panel.setBorder(BorderFactory.createTitledBorder(title));
    panel.add(new JScrollPane(table)); return panel;
  }
  private void commit() {
    try { epochs.commitEdit(); error.commitEdit(); hidden.commitEdit(); }
    catch (java.text.ParseException e) { throw new IllegalArgumentException("Enter valid numeric settings"); }
  }
  private void clear() { spin.setModel(new DefaultTableModel()); nearest.setModel(new DefaultTableModel()); }
  private static void fill(JTable table, List<Candidate> candidates) {
    var model = new DefaultTableModel(new Object[]{"Pattern", "Weight (%)", "Changed", "Stored?"}, 0) {
      @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    for (Candidate candidate : candidates.subList(0, Math.min(10, candidates.size())))
      model.addRow(new Object[]{candidate.pattern(), String.format("%.3f", 100 * candidate.weight()), candidate.mismatches(), candidate.stored() ? "Yes" : "No"});
    table.setModel(model);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.getColumnModel().getColumn(0).setPreferredWidth(candidates.isEmpty() ? 100 : Math.max(100, candidates.get(0).pattern().length() * 9 + 16));
    table.getColumnModel().getColumn(1).setPreferredWidth(110);
  }
  private void busy(boolean running) {
    train.setEnabled(!running); recall.setEnabled(!running && memory != null); test.setEnabled(!running && memory != null);
    patterns.setEditable(!running); cue.setEditable(!running); epochs.setEnabled(!running); error.setEnabled(!running); hidden.setEnabled(!running);
    example.setEnabled(!running); cancel.setEnabled(running);
  }
  private <T> void start(Callable<T> task, Consumer<T> completed) {
    busy(true); status.setText("Working…");
    worker = new SwingWorker<T, Void>() {
      @Override protected T doInBackground() throws Exception { return task.call(); }
      @Override protected void done() {
        try { completed.accept(get()); }
        catch (CancellationException e) { status.setText("Cancelled."); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); status.setText("Interrupted."); }
        catch (ExecutionException e) { clear(); status.setText(e.getCause().getMessage()); }
        finally { busy(false); }
      }
    };
    worker.execute();
  }
}
