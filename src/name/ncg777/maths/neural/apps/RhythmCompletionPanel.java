package name.ncg777.maths.neural.apps;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.event.*;
import name.ncg777.maths.neural.*;

/** Mask-based four-bar completion; the musical constraints need no training. */
public final class RhythmCompletionPanel extends JPanel {
  private final JTextField[] masks = new JTextField[4];
  private final JButton complete = new JButton("Complete rhythms"), cancel = new JButton("Cancel completion");
  private final JSpinner seed = new JSpinner(new SpinnerNumberModel(777, 0, Integer.MAX_VALUE, 1));
  private final JSpinner count = new JSpinner(new SpinnerNumberModel(1000, 1, 10000, 100));
  private final JButton export = new JButton("Export matrices and patterns");
  private final DefaultListModel<String> choices = new DefaultListModel<>();
  private final JList<String> list = new JList<>(choices);
  private final JTextArea output = new JTextArea(10, 30), status = new JTextArea(3, 60);
  private final Preview preview = new Preview();
  private List<RhythmContourStacks.Stack> results = List.of();
  private volatile ValidRhythmDecoder decoder;
  private SwingWorker<ValidRhythmDecoder.Completions, Void> worker;

  public RhythmCompletionPanel() {
    super(new BorderLayout(8, 8));
    JPanel top = new JPanel(new GridLayout(0, 1, 4, 4));
    top.add(new JLabel("Keep known digits; ? fills a gap. Each bar: 4 hex digits (8?8?) or 16 binary steps (1???0???1???0???)."));
    for (int row = 0; row < 4; row++) {
      masks[row] = new JTextField(row == 0 ? "8?8?" : "????", 20); masks[row].setName("Bar " + (row + 1));
      JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT)); line.add(new JLabel("Bar " + (row + 1) + ":")); line.add(masks[row]); top.add(line);
      masks[row].getDocument().addDocumentListener(new DocumentListener() {
        public void insertUpdate(DocumentEvent e) { clear(); }
        public void removeUpdate(DocumentEvent e) { clear(); }
        public void changedUpdate(DocumentEvent e) { clear(); }
      });
    }
    JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT)); actions.add(complete); actions.add(cancel); actions.add(new JLabel("Matrices:")); actions.add(count); actions.add(new JLabel("Variation seed:")); actions.add(seed); actions.add(export); top.add(actions);
    add(top, BorderLayout.NORTH);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); list.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
    JPanel selection = new JPanel(new BorderLayout()); selection.setBorder(BorderFactory.createTitledBorder("Completed matrices — select one")); selection.add(new JScrollPane(list));
    output.setEditable(false); output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14)); output.setBorder(BorderFactory.createTitledBorder("Selected bars — select text to copy"));
    JPanel right = new JPanel(new BorderLayout()); right.add(preview); right.add(new JScrollPane(output), BorderLayout.SOUTH);
    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, selection, right); split.setResizeWeight(.35); split.setDividerLocation(350); add(split);
    status.setEditable(false); status.setLineWrap(true); status.setWrapStyleWord(true); add(status, BorderLayout.SOUTH);
    cancel.setEnabled(false); complete.addActionListener(e -> run()); cancel.addActionListener(e -> cancel()); seed.addChangeListener(e -> clear());
    count.addChangeListener(e -> clear()); export.setEnabled(false); export.addActionListener(e -> export());
    list.addListSelectionListener(e -> {
      int index = list.getSelectedIndex(); if (index < 0 || index >= results.size()) return;
      var result = results.get(index); StringBuilder text = new StringBuilder("Hex (top to bottom):\n" + String.join(" ", result.hex()) + "\n\nBinary (hex digit order):\n");
      for (String hex : result.hex()) text.append(String.format(Locale.ROOT, "%16s", Integer.toBinaryString(Integer.parseInt(hex, 16))).replace(' ', '0')).append('\n');
      output.setText(text.toString()); preview.values = result.values(); preview.repaint();
    });
    status.setText("No training needed. Known digits remain fixed. Every completed bar and both differences of each pair must satisfy SCI; repeated bars are allowed. Change the seed to explore alternatives.");
  }
  public void cancel() { if (worker != null) worker.cancel(true); }
  private void clear() { choices.clear(); results = List.of(); export.setEnabled(false); output.setText(""); preview.values = null; preview.repaint(); status.setText("Press Complete rhythms for the current masks."); }
  private void busy(boolean value) { complete.setEnabled(!value); cancel.setEnabled(value); seed.setEnabled(!value); count.setEnabled(!value); export.setEnabled(!value && !results.isEmpty()); for (JTextField mask : masks) mask.setEnabled(!value); }
  private void export() {
    JFileChooser chooser = new JFileChooser(); chooser.setSelectedFile(new java.io.File("rhythm-completions.txt"));
    if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
    var file = chooser.getSelectedFile();
    if (file.exists() && JOptionPane.showConfirmDialog(this, "Replace " + file.getName() + "?", "Existing file", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
    StringBuilder text = new StringBuilder("# Four-bar SCI matrices; each line is one matrix, top to bottom\n");
    for (var result : results) text.append(String.join(" ", result.hex())).append('\n');
    text.append("\n# Distinct hexadecimal patterns\n");
    results.stream().flatMap(r -> r.hex().stream()).distinct().sorted().forEach(h -> text.append(h).append('\n'));
    try { java.nio.file.Files.writeString(file.toPath(), text); status.setText("Exported " + results.size() + " matrices and their distinct patterns to " + file.getName()); }
    catch (java.io.IOException e) { status.setText("Could not export: " + e.getMessage()); }
  }
  private void run() {
    if (worker != null) return;
    List<String> patterns = Arrays.stream(masks).map(JTextField::getText).toList(); long variation = ((Number) seed.getValue()).longValue(); int requested = (Integer) count.getValue();
    clear(); busy(true); status.setText("Searching compatible SCI bars…");
    worker = new SwingWorker<>() {
      @Override protected ValidRhythmDecoder.Completions doInBackground() {
        if (decoder == null) decoder = new ValidRhythmDecoder();
        return decoder.complete(patterns, requested, 100000, variation);
      }
      @Override protected void done() {
        try {
          var found = get(); results = found.stacks(); for (var stack : results) choices.addElement(String.join(" ", stack.hex()));
          if (!results.isEmpty()) list.setSelectedIndex(0);
          status.setText(found.searchLimitReached() ? "Search limit reached; " + results.size() + " completions found. More may exist. Try another seed or specify more digits."
              : results.isEmpty() ? "No completion satisfies these masks and SCI difference rules. Try replacing more digits with ?."
              : found.resultLimitReached() ? "Generated " + results.size() + " valid matrices. More may exist; change the seed for alternatives."
              : "Found all " + results.size() + " valid completions for these masks.");
          status.append(" Distinct rhythm patterns: " + results.stream().flatMap(r -> r.hex().stream()).distinct().count() + ".");
        } catch (CancellationException e) { status.setText("Completion cancelled."); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); status.setText("Interrupted."); }
        catch (ExecutionException e) { status.setText(e.getCause().getMessage()); }
        finally { worker = null; busy(false); }
      }
    }; worker.execute();
  }
  private static final class Preview extends JComponent {
    double[] values;
    Preview() { setPreferredSize(new Dimension(400, 230)); setBorder(BorderFactory.createTitledBorder("Completed contours — four bars")); }
    @Override protected void paintComponent(Graphics graphics) {
      super.paintComponent(graphics); if (values == null) return;
      Graphics2D g = (Graphics2D) graphics.create(); g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      int h = Math.max(20, (getHeight() - 24) / 4), w = Math.max(16, getWidth() - 50);
      for (int row = 0; row < 4; row++) {
        int top = 24 + row * h;
        g.setColor(Color.GRAY); g.drawString("" + (row + 1), 10, top + h / 2);
        g.setColor(new Color(45,115,180));
        for (int i = 1; i < 16; i++) g.drawLine(30 + (i - 1) * w / 15, top + (int) ((1 - values[row * 16 + i - 1]) * (h - 8) / 2), 30 + i * w / 15, top + (int) ((1 - values[row * 16 + i]) * (h - 8) / 2));
      }
      g.dispose();
    }
  }
}
