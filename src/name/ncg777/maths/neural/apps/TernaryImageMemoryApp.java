package name.ncg777.maths.neural.apps;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import name.ncg777.maths.neural.*;
import name.ncg777.maths.neural.TernaryAssociativeMemory.Cue;
import name.ncg777.maths.neural.TernaryContours.Raster;

/** A pixel editor, approximate associative recall, and a small supervised edge experiment. */
public final class TernaryImageMemoryApp {
  private final JPanel root = new JPanel(new BorderLayout(8, 8));
  private final JTextArea status = new JTextArea(4, 90);
  private final List<JComponent> controls = new ArrayList<>();
  private final DefaultListModel<Cue> library = new DefaultListModel<>();
  private final JList<Cue> memories = new JList<>(library);
  private final PixelCanvas editor = new PixelCanvas();
  private final PixelCanvas recalled = new PixelCanvas(), nearest = new PixelCanvas();
  private final JLabel spinLabel = new JLabel("Train and recall to see a completion");
  private final JLabel nearestLabel = new JLabel("Closest stored image");
  private final JSpinner hideCount = new JSpinner(new SpinnerNumberModel(16, 1, 64, 1));
  private final JSpinner epochs = new JSpinner(new SpinnerNumberModel(100, 1, 500, 25));
  private final JSpinner error = new JSpinner(new SpinnerNumberModel(0, 0, 50, 5));
  private final JButton cancel = new JButton("Cancel");
  private final PixelCanvas source = new PixelCanvas(), rule = new PixelCanvas(), learned = new PixelCanvas();
  private final JSpinner side = new JSpinner(new SpinnerNumberModel(32, 8, 128, 8));
  private TernaryAssociativeMemory memory;
  private TernaryEdgeNetwork network;
  private Raster edgeSource = TernaryContours.generate(32, 777);
  private SwingWorker<?, ?> worker;
  private final TernarySegmentationPanel segmentation = new TernarySegmentationPanel();
  private final TernaryEmbeddingPanel embedding = new TernaryEmbeddingPanel();
  private final RhythmEmbeddingPanel rhythmEmbedding = new RhythmEmbeddingPanel();
  private long seed = 777;

  public TernaryImageMemoryApp() {
    root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    JTabbedPane tabs = new JTabbedPane(); tabs.addTab("Image memory", memoryPanel()); tabs.addTab("Local boundaries", edgePanel());
    tabs.addTab("Contours & regions", segmentation);
    tabs.addTab("Ternary embeddings", embedding);
    tabs.addTab("Rhythm contours", rhythmEmbedding);
    root.add(tabs, BorderLayout.CENTER);
    status.setEditable(false); status.setLineWrap(true); status.setWrapStyleWord(true);
    JPanel footer = new JPanel(new BorderLayout(8, 0)); footer.add(new JScrollPane(status), BorderLayout.CENTER);
    footer.add(cancel, BorderLayout.EAST); root.add(footer, BorderLayout.SOUTH);
    tabs.addChangeListener(e -> footer.setVisible(tabs.getSelectedComponent() != segmentation && tabs.getSelectedComponent() != embedding && tabs.getSelectedComponent() != rhythmEmbedding));
    cancel.setEnabled(false); cancel.addActionListener(e -> cancel());
    loadExamples(); updateEdges();
    status.setText("Paint an image, store it, and train. Hide pixels with ? and recall. Checkerboard is known transparency (0), not missing data.");
    root.setPreferredSize(new Dimension(1120, 790));
  }

  public JComponent component() { return root; }
  public void cancel() { if (worker != null) worker.cancel(true); segmentation.cancel(); embedding.cancel(); rhythmEmbedding.cancel(); }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      var app = new TernaryImageMemoryApp(); JFrame frame = new JFrame("Ternary image laboratory");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); frame.setContentPane(app.component());
      frame.addWindowListener(new WindowAdapter() { @Override public void windowClosed(WindowEvent e) { app.cancel(); } });
      frame.pack(); frame.setLocationRelativeTo(null); frame.setVisible(true);
    });
  }

  private JComponent memoryPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 8));
    JPanel actions = new JPanel(new GridLayout(0, 1));
    JPanel brushes = new JPanel(); brushes.add(new JLabel("Paint:")); ButtonGroup group = new ButtonGroup();
    String[] names = {"Black −1", "Transparent 0", "White +1", "Unknown ?"};
    for (int b = 0; b < names.length; b++) {
      int brush = b - 1; JRadioButton button = new JRadioButton(names[b], b == 0); group.add(button); brushes.add(button);
      button.addActionListener(e -> editor.brush = brush); controls.add(button);
    }
    actions.add(brushes);
    actions.add(row(button("Clear", () -> editor.setCue(complete(new int[64]))), button("Import PNG", this::importImage),
        button("Export PNG", this::exportImage), new JLabel("Hide pixels:"), hideCount, button("Hide", this::hide)));
    actions.add(row(new JLabel("Epochs:"), epochs, button("Train library", this::train), new JLabel("Visible error %:"), error,
        button("Recall", this::recall), button("Test 20 damaged memories", this::benchmark)));
    panel.add(actions, BorderLayout.NORTH); controls.add(hideCount); controls.add(epochs); controls.add(error);
    editor.editable = true; editor.onChange = this::clearRecall;
    error.addChangeListener(e -> clearRecall());
    JPanel pictures = new JPanel(new GridLayout(1, 3, 12, 0));
    pictures.add(titled("8 × 8 cue — click or drag to paint", editor, new JLabel("? = missing; checkerboard = transparent")));
    pictures.add(titled("Spin completion (approximate)", recalled, spinLabel));
    pictures.add(titled("Nearest stored image", nearest, nearestLabel));
    JPanel imageArea = new JPanel(new BorderLayout()); imageArea.add(pictures, BorderLayout.CENTER);
    imageArea.add(row(button("Edit spin completion", () -> copyResult(recalled)), button("Edit nearest image", () -> copyResult(nearest))), BorderLayout.SOUTH);
    panel.add(imageArea, BorderLayout.CENTER);
    memories.setVisibleRowCount(1); memories.setLayoutOrientation(JList.HORIZONTAL_WRAP); memories.setFixedCellWidth(92); memories.setFixedCellHeight(90);
    memories.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); controls.add(memories);
    memories.setCellRenderer((list, cue, index, selected, focus) -> {
      JLabel label = new JLabel("" + (index + 1), new ImageIcon(TernaryImage.encode(cue).getScaledInstance(56, 56, Image.SCALE_REPLICATE)), JLabel.CENTER);
      label.setOpaque(true); label.setBackground(selected ? new Color(180, 215, 240) : new Color(205, 205, 205));
      label.setHorizontalTextPosition(JLabel.CENTER); label.setVerticalTextPosition(JLabel.BOTTOM); return label;
    });
    JPanel saved = new JPanel(new BorderLayout()); saved.setBorder(BorderFactory.createTitledBorder("Stored images — repeated examples count more often"));
    JScrollPane scroll = new JScrollPane(memories); scroll.setPreferredSize(new Dimension(900, 112)); saved.add(scroll, BorderLayout.CENTER);
    saved.add(row(button("Store editor image", this::storeImage), button("Load selected", () -> {
      if (memories.getSelectedValue() != null) editor.setCue(memories.getSelectedValue());
    }), button("Remove selected", () -> { int i = memories.getSelectedIndex(); if (i >= 0) { library.remove(i); invalidate(); } }),
        button("Reset contour examples", this::loadExamples)), BorderLayout.SOUTH);
    panel.add(saved, BorderLayout.SOUTH); return panel;
  }

  private JComponent edgePanel() {
    JPanel panel = new JPanel(new BorderLayout(8, 8));
    JPanel top = new JPanel(new GridLayout(0, 1));
    top.add(new JLabel("Detect opaque pixels bordering transparency or the opposite colour (four neighbours; outside = transparent)."));
    top.add(row(new JLabel("Synthetic image size:"), side, button("Generate shapes", () -> {
      side.commitEdit(); edgeSource = TernaryContours.generate((Integer) side.getValue(), ++seed); updateEdges();
    }), button("Use editor image", () -> {
      TernaryImage.encode(editor.cue()); edgeSource = new Raster(8, 8, editor.cue().values()); updateEdges();
    }), button("Train / evaluate small CNN", this::trainEdges)));
    top.add(new JLabel("Two channels: brightness + opacity. Eight shared 3×3 filters → 1×1 output. Works on 8–128 pixel images."));
    panel.add(top, BorderLayout.NORTH); controls.add(side);
    JPanel pictures = new JPanel(new GridLayout(1, 3, 12, 0));
    pictures.add(titled("Source", source, new JLabel("Black / white / transparent")));
    pictures.add(titled("Explicit boundary rule", rule, new JLabel("White = edge; black = no edge")));
    pictures.add(titled("Learned boundary map", learned, new JLabel("Threshold 0.5; train to display")));
    panel.add(pictures, BorderLayout.CENTER);
    JTextArea note = new JTextArea("The rule also supplies the training labels, so its score is perfect by construction. This CNN experiment tests whether a shared local filter can learn that rule; it does not establish an advantage over it.\nTraining: 96 synthetic 8×8 images, 30 epochs. Evaluation: 24 separate 16×16 images. Missing-pixel reconstruction belongs to the Image memory tab.", 4, 80);
    note.setEditable(false); note.setLineWrap(true); note.setWrapStyleWord(true); panel.add(note, BorderLayout.SOUTH); return panel;
  }

  private void loadExamples() {
    library.clear(); TernaryContours.memories().forEach(library::addElement); memories.setSelectedIndex(0);
    editor.setCue(library.get(0)); invalidate();
  }
  private void storeImage() {
    TernaryImage.encode(editor.cue());
    if (library.size() >= 128) throw new IllegalArgumentException("This playground allows at most 128 stored images");
    library.addElement(editor.cue()); invalidate();
  }
  private void invalidate() { memory = null; clearRecall(); status.setText("Library changed. Train before recall or testing."); }
  private void clearRecall() { recalled.clear(); nearest.clear(); spinLabel.setText("Recall to see a completion"); nearestLabel.setText("Closest stored image"); }
  private int[][] rows() { return Collections.list(library.elements()).stream().map(Cue::values).toArray(int[][]::new); }
  private void train() throws Exception {
    epochs.commitEdit(); int[][] rows = rows(); int n = (Integer) epochs.getValue(); memory = null; clearRecall();
    start(() -> TernaryAssociativeMemory.train(rows, n), fitted -> { memory = fitted; status.setText("Memory trained. Hide pixels and recall; visible values stay fixed at 0% error."); });
  }
  private void requireMemory() { if (memory == null) throw new IllegalStateException("Train the image library first"); }
  private void recall() throws Exception {
    requireMemory(); error.commitEdit(); Cue cue = editor.cue(); double rate = (Integer) error.getValue() / 100.0;
    var model = memory;
    start(() -> model.recall(cue, rate), result -> {
      var a = result.spin().get(0); var b = result.nearest().get(0);
      recalled.setCue(TernaryAssociativeMemory.parseCue(a.pattern())); nearest.setCue(TernaryAssociativeMemory.parseCue(b.pattern()));
      spinLabel.setText(String.format(Locale.ROOT, "Top sample: %.1f%%; changed %d", 100 * a.weight(), a.mismatches()));
      nearestLabel.setText(String.format(Locale.ROOT, "Match share: %.1f%%; changed %d", 100 * b.weight(), b.mismatches()));
      status.setText("Showing one top candidate per method. Spin: 256 samples from four chains; may miss alternatives. Nearest shares are not probabilities of correctness. Ties are resolved by ternary ordering. Spin can invent images; nearest always returns a stored image.");
    });
  }
  private void hide() throws Exception {
    hideCount.commitEdit(); Cue cue = editor.cue(); boolean[] known = cue.known();
    List<Integer> indices = new ArrayList<>(); for (int i = 0; i < 64; i++) if (known[i]) indices.add(i);
    Collections.shuffle(indices, new Random(++seed)); int count = Math.min((Integer) hideCount.getValue(), indices.size());
    for (int i = 0; i < count; i++) known[indices.get(i)] = false;
    editor.setCue(new Cue(cue.values(), known));
  }
  private void benchmark() throws Exception {
    requireMemory(); hideCount.commitEdit(); error.commitEdit(); var model = memory;
    int hidden = (Integer) hideCount.getValue(); double rate = (Integer) error.getValue() / 100.0;
    start(() -> model.benchmark(20, hidden, rate, 777), b -> status.setText(String.format(Locale.ROOT,
        "Stored-memory test: %d trials, %d hidden pixels, %.0f%% visible error. Whole-image recovery: spin %d/%d, nearest %d/%d. Tied trials: spin %d, nearest %d. This measures recall of training images, not generalization to new shapes.",
        b.trials(), hidden, 100 * rate, b.spinCorrect(), b.trials(), b.nearestCorrect(), b.trials(), b.spinTies(), b.nearestTies())));
  }
  private void copyResult(PixelCanvas canvas) { if (canvas.values == null) throw new IllegalStateException("Recall an image first"); editor.setCue(canvas.cue()); }

  private void importImage() throws Exception {
    JFileChooser chooser = chooser(); if (chooser.showOpenDialog(root) == JFileChooser.APPROVE_OPTION) editor.setCue(TernaryImage.read(chooser.getSelectedFile().toPath()));
  }
  private void exportImage() throws Exception {
    Cue cue = editor.cue(); TernaryImage.encode(cue); JFileChooser chooser = chooser();
    if (chooser.showSaveDialog(root) != JFileChooser.APPROVE_OPTION) return;
    File file = chooser.getSelectedFile(); if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".png")) file = new File(file.getPath() + ".png");
    if (file.exists() && JOptionPane.showConfirmDialog(root, "Replace " + file.getName() + "?", "Export", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
    TernaryImage.write(file.toPath(), cue); status.setText("Exported exact 8×8 PNG with transparency.");
  }
  private JFileChooser chooser() { JFileChooser c = new JFileChooser(); c.setFileFilter(new FileNameExtensionFilter("8×8 ternary PNG", "png")); return c; }

  private record EdgeFit(TernaryEdgeNetwork network, TernaryEdgeNetwork.Score score) {}
  private void trainEdges() {
    start(() -> {
      List<Raster> training = new ArrayList<>(), testing = new ArrayList<>();
      for (int i = 0; i < 96; i++) training.add(TernaryContours.generate(8, 777 + i));
      for (int i = 0; i < 24; i++) testing.add(TernaryContours.generate(16, 10000 + i));
      var fitted = TernaryEdgeNetwork.train(training, 30, 777, null);
      return new EdgeFit(fitted, fitted.evaluate(testing));
    }, fit -> {
      network = fit.network(); updateEdges(); var s = fit.score();
      status.setText(String.format(Locale.ROOT, "Held-out synthetic images (24 × 16×16): precision %.1f%%, recall %.1f%%, F1 %.1f%% at threshold 0.5. Rule baseline F1: 100%% by construction. Training used 96 × 8×8 images. No evidence of performance on photographs or damaged contours.", 100 * s.precision(), 100 * s.recall(), 100 * s.f1()));
    });
  }
  private void updateEdges() {
    source.setRaster(edgeSource); boolean[] edges = TernaryContours.boundaries(edgeSource); int[] pixels = new int[edges.length];
    for (int i = 0; i < pixels.length; i++) pixels[i] = edges[i] ? 1 : -1;
    rule.setRaster(new Raster(edgeSource.width(), edgeSource.height(), pixels));
    if (network == null) learned.clear();
    else {
      double[] probabilities = network.predict(edgeSource);
      for (int i = 0; i < pixels.length; i++) pixels[i] = probabilities[i] >= .5 ? 1 : -1;
      learned.setRaster(new Raster(edgeSource.width(), edgeSource.height(), pixels));
    }
  }

  @FunctionalInterface private interface Action { void run() throws Exception; }
  private JButton button(String label, Action action) {
    JButton button = new JButton(label); controls.add(button);
    button.addActionListener(e -> { try { action.run(); } catch (Exception ex) { status.setText(ex.getMessage()); } }); return button;
  }
  private static JPanel row(Component... children) { JPanel row = new JPanel(); for (Component child : children) row.add(child); return row; }
  private static JPanel titled(String title, Component center, Component bottom) {
    JPanel panel = new JPanel(new BorderLayout(4, 6)); panel.setBorder(BorderFactory.createTitledBorder(title));
    panel.add(center, BorderLayout.CENTER); panel.add(bottom, BorderLayout.SOUTH); return panel;
  }
  private <T> void start(Callable<T> task, Consumer<T> completed) {
    if (worker != null) return;
    controls.forEach(c -> c.setEnabled(false)); editor.setEnabled(false); cancel.setEnabled(true); status.setText("Working…");
    worker = new SwingWorker<T, Void>() {
      @Override protected T doInBackground() throws Exception { return task.call(); }
      @Override protected void done() {
        try { completed.accept(get()); }
        catch (CancellationException e) { status.setText("Cancelled."); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); status.setText("Interrupted."); }
        catch (ExecutionException e) { status.setText("Could not complete: " + e.getCause().getMessage()); }
        finally { worker = null; controls.forEach(c -> c.setEnabled(true)); editor.setEnabled(true); cancel.setEnabled(false); }
      }
    };
    worker.execute();
  }

  /** Pixel display shares one coordinate mapping between painting and mouse editing. */
  public static final class PixelCanvas extends JComponent {
    private int width = 8, height = 8;
    private int[] values;
    private boolean[] known;
    private int brush = -1;
    private boolean editable;
    private Runnable onChange = () -> {};
    public PixelCanvas() {
      setPreferredSize(new Dimension(290, 290)); setMinimumSize(new Dimension(128, 128));
      MouseAdapter painter = new MouseAdapter() {
        @Override public void mousePressed(MouseEvent e) { paintAt(e); }
        @Override public void mouseDragged(MouseEvent e) { paintAt(e); }
      }; addMouseListener(painter); addMouseMotionListener(painter);
    }
    public void setCue(Cue cue) { if (cue.values().length != 64) throw new IllegalArgumentException("Expected 64 pixels"); width = height = 8; values = cue.values(); known = cue.known(); repaint(); onChange.run(); }
    public Cue cue() { if (values == null || width != 8 || height != 8) throw new IllegalStateException("No 8×8 image"); return new Cue(values, known); }
    public void setRaster(Raster raster) { width = raster.width(); height = raster.height(); values = raster.pixels(); known = new boolean[values.length]; Arrays.fill(known, true); repaint(); }
    public void clear() { values = null; known = null; repaint(); }
    private int cell() { return Math.max(1, Math.min(getWidth() / width, getHeight() / height)); }
    private void paintAt(MouseEvent e) {
      if (!editable || !isEnabled() || values == null || (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == 0) return;
      int c = cell(), x = e.getX() - (getWidth() - width * c) / 2, y = e.getY() - (getHeight() - height * c) / 2;
      if (x < 0 || y < 0 || x >= width * c || y >= height * c) return;
      int i = y / c * width + x / c; known[i] = brush != 2; values[i] = brush == 2 ? 0 : brush; repaint(); onChange.run();
    }
    @Override protected void paintComponent(Graphics g) {
      super.paintComponent(g); if (values == null) { g.setColor(Color.GRAY); g.drawString("No result yet", 15, 25); return; }
      int c = cell(), left = (getWidth() - width * c) / 2, top = (getHeight() - height * c) / 2;
      for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) {
        int i = y * width + x, px = left + x * c, py = top + y * c;
        if (!known[i]) {
          g.setColor(new Color(255, 225, 150)); g.fillRect(px, py, c, c);
          if (c >= 12) { g.setColor(Color.DARK_GRAY); g.drawString("?", px + c / 3, py + c * 2 / 3); }
        } else if (values[i] == 0) {
          g.setColor(new Color(220, 220, 220)); g.fillRect(px, py, c, c); g.setColor(new Color(180, 180, 180));
          int h = c / 2; g.fillRect(px, py, h, h); g.fillRect(px + h, py + h, c - h, c - h);
        } else { g.setColor(values[i] == 1 ? Color.WHITE : Color.BLACK); g.fillRect(px, py, c, c); }
        if (c >= 12) { g.setColor(new Color(130, 130, 130)); g.drawRect(px, py, c, c); }
      }
    }
  }
  private static Cue complete(int[] pixels) { boolean[] known = new boolean[pixels.length]; Arrays.fill(known, true); return new Cue(pixels, known); }
}
