package name.ncg777.maths.neural.apps;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import name.ncg777.maths.neural.*;
import name.ncg777.maths.neural.TernarySegmentation.*;
import name.ncg777.maths.neural.TernarySegmentation.Shape;
import name.ncg777.maths.neural.TernarySegmentationExperiment.*;

/** Shows local patch predictions, global enclosure and reference labels separately. */
public final class TernarySegmentationPanel extends JPanel {
  private final JComboBox<Shape> shape = new JComboBox<>(Shape.values());
  private final JSpinner size = spinner(64, 24, 128, 8), gap = spinner(0, 0, 8, 1), noise = spinner(0, 0, 10, 1);
  private final JSpinner threshold = new JSpinner(new SpinnerNumberModel(.5, .05, .95, .05));
  private final JSpinner closing = spinner(1, 0, 2, 1);
  private final JCheckBox heatmap = new JCheckBox("Show contour scores");
  private final JTextArea status = new JTextArea(3, 80);
  private final DefaultTableModel metrics = new DefaultTableModel(new Object[] {"Scenes", "Method", "Precision", "Recall", "Boundary F1", "Inside IoU", "Flagged"}, 0) {
    @Override public boolean isCellEditable(int row, int col) { return false; }
  };
  private final List<JComponent> controls = new ArrayList<>();
  private final MapCanvas[] canvases = new MapCanvas[6];
  private final JButton cancel = new JButton("Cancel job");
  private Example example;
  private Result result;
  private TernaryPatchNetwork network;
  private SwingWorker<?, ?> worker;
  private long seed = 404;
  private int hoverX = -1, hoverY = -1;

  public TernarySegmentationPanel() {
    super(new BorderLayout(6, 6)); shape.setSelectedItem(Shape.HOLE);
    JPanel top = new JPanel(new GridLayout(0, 1));
    top.add(row(new JLabel("Shape:"), shape, new JLabel("Size:"), size, new JLabel("Gap length:"), gap, new JLabel("Noise %:"), noise,
        new JLabel("Threshold:"), threshold, new JLabel("Closing radius:"), closing));
    top.add(row(button("New scene", () -> analyse(true)), button("Reanalyse", () -> analyse(false)), button("Train 8×8 model", this::train),
        button("Evaluate 18 scenes", this::evaluate), heatmap, cancel));
    top.add(new JLabel("Regions: blue = inside (+1), orange = boundary (0), dark = outside (−1). Hover to inspect an 8×8 window."));
    add(top, BorderLayout.NORTH);
    JPanel pictures = new JPanel(new GridLayout(2, 3, 8, 8));
    String[] titles = {"Observed outlines (possibly damaged)", "Reference contours", "Restored contours / scores", "Reference regions", "Closing + enclosure baseline", "8×8 model + closing + enclosure"};
    for (int i = 0; i < canvases.length; i++) {
      canvases[i] = new MapCanvas(i); JPanel tile = new JPanel(new BorderLayout());
      tile.setBorder(BorderFactory.createTitledBorder(titles[i])); tile.add(canvases[i]); pictures.add(tile);
    }
    JTable table = new JTable(metrics); table.setFillsViewportHeight(true); table.setRowHeight(19);
    table.getColumnModel().getColumn(0).setPreferredWidth(150); table.getColumnModel().getColumn(1).setPreferredWidth(270);
    JScrollPane results = new JScrollPane(table); results.setColumnHeaderView(table.getTableHeader());
    results.setBorder(BorderFactory.createTitledBorder("Pixel metrics — exact contour matches; interior-only intersection over union"));
    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pictures, results); split.setResizeWeight(.72); split.setDividerLocation(345);
    pictures.setMinimumSize(new Dimension(300, 200)); results.setMinimumSize(new Dimension(300, 85)); add(split, BorderLayout.CENTER);
    status.setEditable(false); status.setLineWrap(true); status.setWrapStyleWord(true); add(new JScrollPane(status), BorderLayout.SOUTH);
    controls.addAll(List.of(shape, size, gap, noise, threshold, closing));
    cancel.setEnabled(false); cancel.addActionListener(e -> cancel());
    heatmap.addActionListener(e -> repaintMaps());
    shape.addActionListener(e -> settingsChanged());
    for (JSpinner spinner : List.of(size, gap, noise, threshold, closing)) spinner.addChangeListener(e -> settingsChanged());
    example = TernarySegmentation.generate(64, Shape.HOLE, 0, 0, seed);
    result = TernarySegmentationExperiment.restore(null, example.observed(), .5, 1); showCurrent();
    status.setText("Start with a closed shape, then add gaps or noise. Train the 8×8 model to restore contours. Enclosure alternates across nested contours, preserving holes. Border-cut shapes deliberately violate the outside-at-frame assumption.");
  }

  public void cancel() { if (worker != null) worker.cancel(true); }
  private static JSpinner spinner(int n, int min, int max, int step) { return new JSpinner(new SpinnerNumberModel(n, min, max, step)); }
  private static JPanel row(Component... components) { JPanel p = new JPanel(); for (Component c : components) p.add(c); return p; }
  private interface Action { void run() throws Exception; }
  private JButton button(String label, Action action) {
    JButton b = new JButton(label); controls.add(b);
    b.addActionListener(e -> { try { action.run(); } catch (Exception ex) { status.setText(ex.getMessage()); } }); return b;
  }
  private void commit() throws Exception { for (JSpinner s : List.of(size, gap, noise, threshold, closing)) s.commitEdit(); }
  private void settingsChanged() {
    if (worker != null) return;
    result = null; metrics.setRowCount(0); repaintMaps();
    status.setText("Settings changed. New scene applies shape/size/gap/noise; Reanalyse keeps the observed image and applies threshold/closing only. Evaluation uses its own fixed scenes.");
  }
  private void analyse(boolean generate) throws Exception {
    commit();
    Example input = generate ? TernarySegmentation.generate((Integer) size.getValue(), (Shape) shape.getSelectedItem(),
        (Integer) gap.getValue(), (Integer) noise.getValue() / 100.0, ++seed) : example;
    double t = (Double) threshold.getValue(); int radius = (Integer) closing.getValue(); var fitted = network;
    start(() -> TernarySegmentationExperiment.restore(fitted, input.observed(), t, radius), restored -> {
      example = input; result = restored; hoverX = hoverY = -1; showCurrent(); status.setText(summary());
    });
  }
  private record Fit(TernaryPatchNetwork network, Result result) {}
  private void train() throws Exception {
    commit(); Example input = example; double t = (Double) threshold.getValue(); int radius = (Integer) closing.getValue();
    start(() -> {
      TernaryPatchNetwork fitted = TernarySegmentationExperiment.train(null);
      return new Fit(fitted, TernarySegmentationExperiment.restore(fitted, input.observed(), t, radius));
    }, fit -> { network = fit.network(); result = fit.result(); showCurrent(); status.setText("Trained on 48 separate 32/40-pixel scenes, 30 epochs. " + summary()); });
  }
  private void evaluate() throws Exception {
    commit(); if (network == null) throw new IllegalStateException("Train the 8×8 model first");
    double t = (Double) threshold.getValue(); int radius = (Integer) closing.getValue(); var fitted = network;
    start(() -> TernarySegmentationExperiment.evaluate(fitted, t, radius), comparisons -> {
      metrics.setRowCount(0);
      for (Comparison c : comparisons) addScore(c.condition(), c.method(), c.score(), c.ambiguousImages() + "/" + c.images());
      status.setText(String.format(Locale.ROOT, "Held-out benchmark: six 64×64 shapes × three damage conditions; threshold %.2f, closing radius %d. Includes a border cut in each condition. F1 measures exact contour pixels; IoU measures interior only. Flagged = images with ambiguous/open/border-touching contour bands. High contour F1 can still leave a gap that destroys enclosure.", t, radius));
    });
  }
  private void showCurrent() {
    metrics.setRowCount(0);
    if (result != null) {
      addCurrent("Observed + enclosure", result.raw()); addCurrent("Closing + enclosure", result.baseline());
      if (result.learned() != null) addCurrent("8×8 + closing + enclosure", result.learned());
    }
    repaintMaps();
  }
  private void addCurrent(String method, Regions regions) { addScore("Displayed scene", method, TernarySegmentation.score(example, regions), regions.ambiguousContours() > 0 ? "1/1" : "0/1"); }
  private void addScore(String scene, String method, Score score, String flagged) {
    metrics.addRow(new Object[] {scene, method, percent(score.precision()), percent(score.recall()), percent(score.f1()), percent(score.iou()), flagged});
  }
  private static String percent(double n) { return String.format(Locale.ROOT, "%.1f%%", n * 100); }
  private String summary() {
    String local = result.prediction() == null ? "Train to see learned predictions. " : String.format(Locale.ROOT,
        "Four overlapping votes per pixel; mean score disagreement %.3f (not calibrated uncertainty). ", result.prediction().meanDisagreement());
    Regions r = result.learned() == null ? result.baseline() : result.learned();
    return local + r.ambiguousContours() + " ambiguous contour bands; touches frame: " + r.touchesBorder()
        + ". Open contours can leak; intersecting bands and border crops are not guaranteed to have correct inside/outside labels. Scores use synthetic truth, never supplied to inference.";
  }
  private void repaintMaps() { for (MapCanvas canvas : canvases) if (canvas != null) canvas.repaint(); }
  private <T> void start(Callable<T> task, Consumer<T> complete) {
    if (worker != null) return;
    controls.forEach(c -> c.setEnabled(false)); cancel.setEnabled(true); status.setText("Working… training and inference run in the background.");
    worker = new SwingWorker<T, Void>() {
      @Override protected T doInBackground() throws Exception { return task.call(); }
      @Override protected void done() {
        try { complete.accept(get()); }
        catch (CancellationException e) { status.setText("Cancelled; previous model and images retained."); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); status.setText("Interrupted."); }
        catch (ExecutionException e) { status.setText("Could not complete: " + e.getCause().getMessage()); }
        finally { worker = null; controls.forEach(c -> c.setEnabled(true)); cancel.setEnabled(false); }
      }
    }; worker.execute();
  }

  private final class MapCanvas extends JComponent {
    private final int mode;
    MapCanvas(int mode) {
      this.mode = mode; setPreferredSize(new Dimension(320, 180)); setToolTipText("");
      MouseAdapter mouse = new MouseAdapter() {
        @Override public void mouseMoved(MouseEvent e) {
          if (example == null) return; int scale = scale(), left = left(scale), top = top(scale);
          int x = e.getX() - left, y = e.getY() - top;
          hoverX = x < 0 || x >= example.observed().width() * scale ? -1 : x / scale;
          hoverY = y < 0 || y >= example.observed().height() * scale ? -1 : y / scale; repaintMaps();
        }
        @Override public void mouseExited(MouseEvent e) { hoverX = hoverY = -1; repaintMaps(); }
      }; addMouseMotionListener(mouse); addMouseListener(mouse);
    }
    private int scale() { return Math.max(1, Math.min(getWidth() / example.observed().width(), getHeight() / example.observed().height())); }
    private int left(int s) { return (getWidth() - s * example.observed().width()) / 2; }
    private int top(int s) { return (getHeight() - s * example.observed().height()) / 2; }
    @Override public String getToolTipText(MouseEvent e) {
      if (hoverX < 0 || hoverY < 0 || example == null || hoverX >= example.observed().width() || hoverY >= example.observed().height()) return null;
      int i = hoverY * example.observed().width() + hoverX;
      String text = "(" + hoverX + ", " + hoverY + "): reference trit " + example.regions()[i];
      if (result != null && result.prediction() != null) text += String.format(Locale.ROOT,
          "; contour score %.3f; vote SD %.3f; %d votes", result.prediction().scores()[i], result.prediction().disagreement()[i], result.prediction().votes()[i]);
      return text;
    }
    @Override protected void paintComponent(Graphics graphics) {
      super.paintComponent(graphics); if (example == null) return;
      if ((mode == 2 || mode == 5) && (result == null || result.prediction() == null) || mode == 4 && result == null) {
        graphics.setColor(Color.GRAY); graphics.drawString("Train / reanalyse to display", 12, 24); return;
      }
      int w = example.observed().width(), h = example.observed().height(), s = scale(), left = left(s), top = top(s);
      int[] values = mode == 0 ? example.observed().pixels() : mode == 3 ? example.regions()
          : mode == 4 ? result.baseline().trits() : mode == 5 || mode == 2 ? result.learned().trits() : null;
      boolean[] truth = mode == 1 ? example.boundary() : null;
      double[] scores = mode == 2 && heatmap.isSelected() ? result.prediction().scores() : null;
      for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
        int i = y * w + x; Color colour;
        if (scores != null) { int gray = (int) Math.round(255 * scores[i]); colour = new Color(gray, gray, gray); }
        else if (mode < 3) colour = (mode == 0 ? values[i] != 0 : mode == 1 ? truth[i] : values[i] == 0) ? Color.WHITE : Color.BLACK;
        else colour = values[i] == 0 ? new Color(245, 160, 50) : values[i] == 1 ? new Color(65, 155, 225) : new Color(35, 42, 50);
        graphics.setColor(colour); graphics.fillRect(left + x * s, top + y * s, s, s);
      }
      if (hoverX >= 0 && hoverY >= 0) {
        Graphics2D g = (Graphics2D) graphics.create(); g.clipRect(left, top, w * s, h * s);
        int x = (hoverX / 2 * 2 - 2) * s + left, y = (hoverY / 2 * 2 - 2) * s + top;
        g.setColor(Color.CYAN); g.drawRect(x, y, 8 * s, 8 * s);
        g.setColor(Color.MAGENTA); g.drawRect(x + 2 * s, y + 2 * s, 4 * s, 4 * s); g.dispose();
      }
    }
  }
}
