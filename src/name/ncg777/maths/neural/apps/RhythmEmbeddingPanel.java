package name.ncg777.maths.neural.apps;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import name.ncg777.maths.neural.*;
import name.ncg777.maths.neural.RhythmContourStacks.Dataset;

/** Four full-width bar contours, encoded jointly as one ternary vector. */
public final class RhythmEmbeddingPanel extends JPanel {
  private final JButton train = new JButton("Train rhythm contours"), cancel = new JButton("Cancel");
  private final JComboBox<Integer> width = new JComboBox<>(new Integer[] {8,16,32,64});
  private final JSpinner example = new JSpinner(new SpinnerNumberModel(1, 1, 64, 1)), noise = new JSpinner(new SpinnerNumberModel(0, 0, 25, 1));
  private final JTextField code = new JTextField(65);
  private final JButton apply = new JButton("Decode edited code"), reset = new JButton("Re-encode");
  private final JLabel bars = new JLabel("Four 16-step bars; sixteen normalized contour samples per row."), details = new JLabel(" ");
  private final JTextArea status = new JTextArea(3, 80);
  private final DefaultTableModel table = new DefaultTableModel(new Object[] {"Method", "Clean RMSE (% range)", "5% noise RMSE (% range)"}, 0) {
    @Override public boolean isCellEditable(int row, int column) { return false; }
  };
  private final Plot[] plots = new Plot[4];
  private final List<JComponent> controls;
  private Study study;
  private double[][] shown;
  private SwingWorker<Study, String> worker;
  private record Study(Dataset data, Map<Integer, TernaryCurveAutoencoder> models, List<Object[]> scores) {}

  public RhythmEmbeddingPanel() {
    super(new BorderLayout(6, 6)); width.setSelectedItem(16); code.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
    JPanel top = new JPanel(new GridLayout(0, 1));
    top.add(row(train, cancel, new JLabel("Trits:"), width, new JLabel("Test stack:"), example, new JLabel("Input noise %:"), noise)); top.add(bars);
    add(top, BorderLayout.NORTH);
    JPanel pictures = new JPanel(new GridLayout(1, 4, 8, 0)); String[] names = {"Reference contours", "Encoder input", "Learned distortion", "Nearest training stack"};
    for (int i = 0; i < 4; i++) { plots[i] = new Plot(i); JPanel p = new JPanel(new BorderLayout()); p.setBorder(BorderFactory.createTitledBorder(names[i])); p.add(plots[i]); pictures.add(p); }
    JPanel editing = new JPanel(new GridLayout(0, 1)); editing.add(row(new JLabel("Code (T = −1):"), code)); editing.add(row(apply, reset, details));
    JPanel middle = new JPanel(new BorderLayout(6, 6)); middle.add(pictures, BorderLayout.CENTER); middle.add(editing, BorderLayout.SOUTH);
    JTable metrics = new JTable(table); metrics.setRowHeight(20); JScrollPane scroll = new JScrollPane(metrics); scroll.setColumnHeaderView(metrics.getTableHeader());
    scroll.setBorder(BorderFactory.createTitledBorder("64 held-out four-bar stacks — fixed tests, lower error is better"));
    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, middle, scroll); split.setDividerLocation(405); split.setResizeWeight(.65);
    middle.setMinimumSize(new Dimension(300, 250)); scroll.setMinimumSize(new Dimension(300, 80)); add(split, BorderLayout.CENTER);
    status.setEditable(false); status.setLineWrap(true); status.setWrapStyleWord(true); add(new JScrollPane(status), BorderLayout.SOUTH);
    controls = List.of(train, width, example, noise, code, apply, reset); cancel.setEnabled(false);
    train.addActionListener(e -> train()); cancel.addActionListener(e -> cancel()); width.addActionListener(e -> refresh());
    example.addChangeListener(e -> refresh()); noise.addChangeListener(e -> refresh()); reset.addActionListener(e -> refresh());
    apply.addActionListener(e -> {
      if (study == null) { status.setText("Train the contour models first."); return; }
      String value = code.getText().replaceAll("\\s", ""); int w = (Integer) width.getSelectedItem();
      if (!value.matches("[Tt01]{" + w + "}")) { status.setText("Enter exactly " + w + " trits using T, 0, 1."); return; }
      shown[2] = study.models.get(w).decode(TernaryAssociativeMemory.parseCue(value).values()); updateDisplay();
      status.setText("Decoded the edited ternary code. These continuous curves are a learned distortion, not a conversion back to valid hexadecimal rhythms.");
    });
    status.setText("Uses the project's SCI predicate and Contours-app contour sequence. Four bars are stacked using Matrix Generator's all-previous-row difference rule. Each contour is resampled across sixteen positions; input heights and decoded curves are continuous, only the internal code is ternary.");
  }
  public void cancel() { if (worker != null) worker.cancel(true); }
  private static JPanel row(Component... items) { JPanel p = new JPanel(); for (Component c : items) p.add(c); return p; }
  private void train() {
    if (worker != null) return; controls.forEach(c -> c.setEnabled(false)); cancel.setEnabled(true); status.setText("Preparing compatible SCI contour stacks…");
    worker = new SwingWorker<Study, String>() {
      @Override protected Study doInBackground() {
        Dataset data = RhythmContourStacks.dataset(n -> publish("Examined " + n + "/65535 hexadecimal patterns…"));
        double[][] input = data.training().stream().map(RhythmContourStacks.Stack::values).toArray(double[][]::new);
        Map<Integer, TernaryCurveAutoencoder> models = new LinkedHashMap<>();
        for (int w : new int[] {8,16,32,64}) { models.put(w, TernaryCurveAutoencoder.train(input, w, 80, 777)); publish("Trained " + w + "-trit curve model…"); }
        return new Study(data, models, evaluate(data, models));
      }
      @Override protected void process(List<String> updates) { if (!isCancelled()) status.setText(updates.get(updates.size() - 1)); }
      @Override protected void done() {
        try { study = get(); table.setRowCount(0); study.scores.forEach(table::addRow); refresh(); status.setText("Trained on 192 four-bar stacks; tested on 64 stacks built from a separate rhythm pool. Gray in the distortion panel is the reference. RMSE is measured against clean heights. Decoded curves need not correspond to valid SCI rhythms; no automatic snapping or correction is applied."); }
        catch (CancellationException e) { status.setText("Cancelled; previous models retained."); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); status.setText("Interrupted."); }
        catch (ExecutionException e) { status.setText("Could not complete: " + e.getCause().getMessage()); }
        finally { worker = null; controls.forEach(c -> c.setEnabled(true)); cancel.setEnabled(false); }
      }
    }; worker.execute();
  }
  private static List<Object[]> evaluate(Dataset data, Map<Integer, TernaryCurveAutoencoder> models) {
    double[][] training = data.training().stream().map(RhythmContourStacks.Stack::values).toArray(double[][]::new);
    double[] mean = new double[64]; for (double[] row : training) for (int i = 0; i < 64; i++) mean[i] += row[i] / training.length;
    double[][] errors = new double[7][2];
    for (int c = 0; c < 2; c++) for (int i = 0; i < data.testing().size(); i++) {
      double[] target = data.testing().get(i).values(), input = TernaryCurveAutoencoder.noise(target, c == 0 ? 0 : .05, 10000 + i);
      errors[0][c] += TernaryCurveAutoencoder.error(target, input); errors[1][c] += TernaryCurveAutoencoder.error(target, mean);
      errors[2][c] += TernaryCurveAutoencoder.error(target, nearest(input, training)); int m = 3;
      for (var model : models.values()) errors[m++][c] += TernaryCurveAutoencoder.error(target, model.decode(model.encode(input)));
    }
    List<Object[]> rows = new ArrayList<>(); String[] names = {"Copy input", "Mean training contours", "Nearest training stack", "8 trits", "16 trits", "32 trits", "64 trits"};
    for (int i = 0; i < names.length; i++) rows.add(new Object[] {names[i], String.format(Locale.ROOT, "%.2f", 100 * Math.sqrt(errors[i][0] / data.testing().size())), String.format(Locale.ROOT, "%.2f", 100 * Math.sqrt(errors[i][1] / data.testing().size()))});
    return rows;
  }
  private static double[] nearest(double[] input, double[][] training) {
    double best = Double.POSITIVE_INFINITY; double[] result = null;
    for (double[] row : training) { double error = TernaryCurveAutoencoder.error(input, row); if (error < best) { best = error; result = row; } }
    return result.clone();
  }
  private void refresh() {
    if (study == null) return;
    int index = (Integer) example.getValue() - 1; var target = study.data.testing().get(index);
    double[] input = TernaryCurveAutoencoder.noise(target.values(), (Integer) noise.getValue() / 100.0, 10000 + index);
    var model = study.models.get((Integer) width.getSelectedItem()); int[] encoded = model.encode(input);
    code.setText(TernaryAssociativeMemory.format(encoded));
    shown = new double[][] {target.values(), input, model.decode(encoded), nearest(input, study.data.training().stream().map(RhythmContourStacks.Stack::values).toArray(double[][]::new))};
    bars.setText("Top to bottom: " + String.join(" / ", target.hex()) + "   |   Four full-width bars, sixteen normalized samples each"); updateDisplay();
  }
  private void updateDisplay() {
    details.setText(String.format(Locale.ROOT, "Current reconstruction RMSE: %.2f%% of range", 100 * Math.sqrt(TernaryCurveAutoencoder.error(shown[0], shown[2]))));
    for (Plot plot : plots) plot.repaint();
  }
  private final class Plot extends JComponent {
    private final int index;
    Plot(int index) { this.index = index; setPreferredSize(new Dimension(240, 320)); }
    @Override protected void paintComponent(Graphics graphics) {
      super.paintComponent(graphics); if (shown == null) { graphics.drawString("Train to display", 12, 25); return; }
      Graphics2D g = (Graphics2D) graphics.create(); g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      int left = 24, right = Math.max(left + 16, getWidth() - 10), height = Math.max(30, getHeight() / 4);
      for (int bar = 0; bar < 4; bar++) {
        int top = bar * height + 10, bottom = (bar + 1) * height - 16;
        g.setColor(new Color(220, 220, 220));
        for (int step = 0; step < 16; step++) { int x = left + step * (right - left) / 15; g.drawLine(x, top, x, bottom); }
        g.setColor(Color.GRAY); g.drawString("" + (bar + 1), 5, (top + bottom) / 2);
        if (index == 2) draw(g, shown[0], bar, left, right, top, bottom, new Color(175, 175, 175));
        draw(g, shown[index], bar, left, right, top, bottom, index == 2 ? new Color(215, 110, 30) : new Color(45, 115, 180));
      }
      g.dispose();
    }
    private void draw(Graphics2D g, double[] values, int bar, int left, int right, int top, int bottom, Color colour) {
      g.setColor(colour); g.setStroke(new BasicStroke(1.8f));
      for (int step = 1; step < 16; step++) {
        int x0 = left + (step - 1) * (right - left) / 15, x1 = left + step * (right - left) / 15;
        int y0 = bottom - (int) Math.round((values[bar * 16 + step - 1] + 1) / 2 * (bottom - top));
        int y1 = bottom - (int) Math.round((values[bar * 16 + step] + 1) / 2 * (bottom - top)); g.drawLine(x0, y0, x1, y1);
      }
    }
  }
}
