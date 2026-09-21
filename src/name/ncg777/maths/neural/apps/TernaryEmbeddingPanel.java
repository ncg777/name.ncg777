package name.ncg777.maths.neural.apps;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import name.ncg777.maths.neural.*;
import name.ncg777.maths.neural.TernaryContours.Raster;
import name.ncg777.maths.neural.TernaryEmbeddingExperiment.*;
import name.ncg777.maths.neural.apps.TernaryImageMemoryApp.PixelCanvas;

/** Inspect a discrete code, decode edits, and compare held-out reconstruction. */
public final class TernaryEmbeddingPanel extends JPanel {
  private final Dataset data = TernaryEmbeddingExperiment.dataset();
  private final int[][] training = data.training(), testing = data.testing();
  private final JComboBox<Integer> width = new JComboBox<>(new Integer[] {8,16,32,64});
  private final JSpinner example = new JSpinner(new SpinnerNumberModel(1, 1, 64, 1));
  private final JSpinner noise = new JSpinner(new SpinnerNumberModel(5, 0, 30, 1));
  private final PixelCanvas[] pictures = new PixelCanvas[5];
  private final CodeCanvas codeCanvas = new CodeCanvas();
  private final JTextArea status = new JTextArea(3, 80);
  private final JLabel detail = new JLabel("Train to inspect an embedding");
  private final DefaultTableModel metrics = new DefaultTableModel(new Object[] {"Method", "Test input", "Pixel error", "Contour F1", "Exact image", "Changed code trits"}, 0) {
    @Override public boolean isCellEditable(int row, int column) { return false; }
  };
  private final List<JComponent> controls = new ArrayList<>();
  private final JButton train = new JButton("Train all four code sizes"), cancel = new JButton("Cancel training"), reset = new JButton("Re-encode input");
  private final Map<Integer, int[][]> trainingCodes = new HashMap<>();
  private Fit fit;
  private int[] currentCode;
  private SwingWorker<Fit, Integer> worker;

  public TernaryEmbeddingPanel() {
    super(new BorderLayout(8, 8)); width.setSelectedItem(16);
    JPanel top = new JPanel(new GridLayout(0, 1));
    top.add(row(train, cancel, new JLabel("Code width:"), width, new JLabel("Test example:"), example, new JLabel("Input flips %:"), noise, reset));
    top.add(new JLabel("Encode an 8×8 contour → ternary code → reconstruct. Click a code cell to cycle −1, 0, +1 and decode it."));
    add(top, BorderLayout.NORTH);
    JPanel images = new JPanel(new GridLayout(1, 5, 6, 6));
    String[] titles = {"Clean target", "Encoder input", "Decoded code", "Nearest by pixels", "Nearest by code"};
    for (int i = 0; i < pictures.length; i++) {
      pictures[i] = new PixelCanvas(); pictures[i].setPreferredSize(new Dimension(185, 185));
      JPanel tile = new JPanel(new BorderLayout()); tile.setBorder(BorderFactory.createTitledBorder(titles[i])); tile.add(pictures[i]); images.add(tile);
    }
    JPanel code = new JPanel(new BorderLayout()); code.setBorder(BorderFactory.createTitledBorder("Learned features — blue −1 / gray 0 / orange +1; no fixed semantic names"));
    code.add(codeCanvas, BorderLayout.CENTER); code.add(detail, BorderLayout.SOUTH);
    JPanel upper = new JPanel(new BorderLayout(4, 8)); upper.add(images, BorderLayout.CENTER); upper.add(code, BorderLayout.SOUTH);
    JTable table = new JTable(metrics); table.setFillsViewportHeight(true); table.setRowHeight(18);
    JScrollPane scroll = new JScrollPane(table); scroll.setColumnHeaderView(table.getTableHeader());
    scroll.setBorder(BorderFactory.createTitledBorder("64 held-out images — fixed clean and 5% noise tests (independent of display controls)"));
    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upper, scroll); split.setDividerLocation(290); split.setResizeWeight(.5);
    upper.setMinimumSize(new Dimension(300, 230)); scroll.setMinimumSize(new Dimension(300, 100)); add(split, BorderLayout.CENTER);
    status.setEditable(false); status.setLineWrap(true); status.setWrapStyleWord(true); add(new JScrollPane(status), BorderLayout.SOUTH);
    controls.addAll(List.of(train, width, example, noise, reset, codeCanvas)); cancel.setEnabled(false);
    train.addActionListener(e -> train()); cancel.addActionListener(e -> cancel()); reset.addActionListener(e -> refresh());
    width.addActionListener(e -> refresh()); example.addChangeListener(e -> refresh()); noise.addChangeListener(e -> refresh());
    refresh(); status.setText("192 unique training contours and 64 separate test contours. No exact duplicates across the split. Baselines: copy input, thresholded training mean, nearest training image. Codes and model weights are session-local.");
  }
  public void cancel() { if (worker != null) worker.cancel(true); }
  private static JPanel row(Component... components) { JPanel panel = new JPanel(); for (Component c : components) panel.add(c); return panel; }
  private void train() {
    if (worker != null) return;
    controls.forEach(c -> c.setEnabled(false)); cancel.setEnabled(true); status.setText("Training four quantized autoencoders…");
    worker = new SwingWorker<Fit, Integer>() {
      @Override protected Fit doInBackground() { return TernaryEmbeddingExperiment.train(data, w -> publish(w)); }
      @Override protected void process(List<Integer> widths) { if (!isCancelled()) status.setText("Completed " + widths.get(widths.size() - 1) + "-trit fit; continuing training / evaluation…"); }
      @Override protected void done() {
        try {
          Fit completed = get(); fit = completed; trainingCodes.clear();
          for (var entry : fit.models().entrySet()) trainingCodes.put(entry.getKey(), Arrays.stream(training).map(entry.getValue()::encode).toArray(int[][]::new));
          metrics.setRowCount(0);
          for (Row r : fit.rows()) metrics.addRow(new Object[] {r.method(), r.input(), percent(r.score().errorRate()), percent(r.score().f1()), percent(r.score().exactRate()), Double.isNaN(r.codeChange()) ? "—" : percent(r.codeChange())});
          refresh(); status.setText("Trained 80 epochs each, with alternating clean and 5%-corrupted inputs. The decoder sees only hard trits. 8–32 trits reduce nominal code capacity; 64 trits do not compress a 64-bit binary image. Model weights are extra. Results measure this synthetic generator, not general shape understanding.");
        } catch (CancellationException e) { status.setText("Cancelled; previous models retained."); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); status.setText("Interrupted."); }
        catch (ExecutionException e) { status.setText("Training failed: " + e.getCause().getMessage()); }
        finally { worker = null; controls.forEach(c -> c.setEnabled(true)); cancel.setEnabled(false); }
      }
    }; worker.execute();
  }
  private void refresh() {
    int index = (Integer) example.getValue() - 1;
    int[] clean = testing[index], input = TernaryAutoencoder.corrupt(clean, (Integer) noise.getValue() / 100.0, 10000 + index);
    show(0, clean); show(1, input); show(3, training[TernaryEmbeddingExperiment.nearest(input, training)]);
    if (fit == null) { currentCode = null; pictures[2].clear(); pictures[4].clear(); codeCanvas.repaint(); return; }
    currentCode = fit.models().get((Integer) width.getSelectedItem()).encode(input); decode();
  }
  private void decode() {
    if (currentCode == null || fit == null) return;
    int w = currentCode.length; var model = fit.models().get(w); int[] reconstruction = TernaryAutoencoder.binary(model.decode(currentCode));
    show(2, reconstruction); int[][] codes = trainingCodes.get(w); show(4, training[TernaryEmbeddingExperiment.nearest(currentCode, codes)]);
    Set<String> unique = new HashSet<>(); int matches = 0, zeros = 0;
    for (int[] code : codes) { unique.add(Arrays.toString(code)); if (Arrays.equals(currentCode, code)) matches++; for (int t : code) if (t == 0) zeros++; }
    int error = TernaryEmbeddingExperiment.distance(testing[(Integer) example.getValue() - 1], reconstruction);
    detail.setText(String.format(Locale.ROOT, "<html>%d trits: %s<br>Wrong pixels %d/64   |   Training codes %d/192 unique, %.1f%% zero trits   |   Exact code matches %d</html>",
        w, TernaryAssociativeMemory.format(currentCode), error, unique.size(), 100.0 * zeros / (codes.length * w), matches));
    codeCanvas.repaint();
  }
  private void show(int index, int[] binary) {
    int[] pixels = binary.clone(); for (int i = 0; i < pixels.length; i++) pixels[i] = pixels[i] == 0 ? -1 : 1;
    pictures[index].setRaster(new Raster(8, 8, pixels));
  }
  private static String percent(double p) { return String.format(Locale.ROOT, "%.1f%%", p * 100); }
  private final class CodeCanvas extends JComponent {
    CodeCanvas() {
      setPreferredSize(new Dimension(900, 54));
      addMouseListener(new MouseAdapter() { @Override public void mousePressed(MouseEvent e) {
        if (!isEnabled() || currentCode == null || e.getButton() != MouseEvent.BUTTON1) return;
        int cell = Math.max(1, Math.min(30, getWidth() / currentCode.length));
        int x = e.getX() - (getWidth() - currentCode.length * cell) / 2;
        if (x < 0 || x >= cell * currentCode.length || e.getY() < 8 || e.getY() >= 42) return;
        int i = x / cell; currentCode[i] = currentCode[i] == 1 ? -1 : currentCode[i] + 1; decode();
        status.setText("Manually edited code: reconstruction now comes from these trits. Nearest-by-code compares training embeddings using Hamming distance; a match does not guarantee a similar shape. Re-encode input resets the edits.");
      }});
    }
    @Override protected void paintComponent(Graphics g) {
      super.paintComponent(g); if (currentCode == null) { g.setColor(Color.GRAY); g.drawString("Train to generate a ternary embedding", 12, 28); return; }
      int cell = Math.max(1, Math.min(30, getWidth() / currentCode.length)), left = (getWidth() - currentCode.length * cell) / 2;
      for (int i = 0; i < currentCode.length; i++) {
        int t = currentCode[i]; g.setColor(t < 0 ? new Color(80, 145, 215) : t == 0 ? new Color(200, 200, 200) : new Color(245, 180, 70));
        g.fillRect(left + i * cell, 8, cell, 34); g.setColor(Color.DARK_GRAY); g.drawRect(left + i * cell, 8, cell, 34);
        if (cell >= 14) g.drawString(t < 0 ? "T" : "" + t, left + i * cell + 3, 30);
      }
    }
  }
}
