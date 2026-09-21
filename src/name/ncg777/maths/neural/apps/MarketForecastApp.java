package name.ncg777.maths.neural.apps;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.io.IOException;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import name.ncg777.maths.neural.market.MarketData;
import name.ncg777.maths.neural.market.MarketData.Interval;
import name.ncg777.maths.neural.market.MarketForecaster;

/** Background-refreshed market charts and transparent ternary forecasts; no order execution. */
public class MarketForecastApp {
  private final JFrame frame = new JFrame("Market forecast — name.ncg777");
  private final JComboBox<String> provider = new JComboBox<>(new String[] {"Yahoo · intraday & daily", "FRED · daily indices"});
  private final JComboBox<String> symbol = new JComboBox<>(new String[] {"SPY", "QQQ", "AAPL", "MSFT", "^GSPC", "^IXIC", "BTC-USD"});
  private final JComboBox<Interval> interval = new JComboBox<>(Interval.values());
  private final JComboBox<String> refresh = new JComboBox<>(new String[] {"Manual", "30 seconds", "1 minute", "5 minutes"});
  private final JSpinner history = new JSpinner(new SpinnerNumberModel(800, 150, 1200, 50));
  private final JSpinner epochs = new JSpinner(new SpinnerNumberModel(25, 1, 40, 5));
  private final JButton load = new JButton("Refresh & train"), cancel = new JButton("Cancel"), csv = new JButton("Open CSV…");
  private final JLabel status = new JLabel("Choose a symbol and press Refresh & train, or open a Date,Close CSV.");
  private final JLabel freshness = new JLabel("Free feeds may be delayed. Forecasts use completed bars only.");
  private final JLabel scores = new JLabel("Forecast quality will be compared with independent daily/bar movements.");
  private final JProgressBar progress = new JProgressBar();
  private final PriceChart prices = new PriceChart();
  private final ProbabilityChart probabilities = new ProbabilityChart();
  private final DefaultTableModel jointRows = new DefaultTableModel(new String[] {"Next bar", "Following bar", "Probability"}, 0) {
    @Override public boolean isCellEditable(int row, int column) { return false; }
  };
  private final Timer timer;
  private SwingWorker<View, View> worker;
  private MarketScreenerDialog screenerDialog;
  private View current;
  private int lastHistory, lastEpochs;
  private boolean imported, changing;
  private record View(MarketData.Snapshot snapshot, MarketForecaster.Result forecast, String message) {}
  private static final String[] STATE = {"Down", "Little change", "Up"};
  private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm z").withZone(ZoneId.systemDefault());
  private static String barTime(MarketData.Snapshot snapshot, Instant time) {
    return snapshot.interval().seconds >= 86400
        ? DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneOffset.UTC).format(time) : TIME.format(time);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
      new MarketForecastApp().frame.setVisible(true);
    });
  }
  public MarketForecastApp() {
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setMinimumSize(new Dimension(980, 700)); frame.setSize(1180, 830);
    symbol.setEditable(true); interval.setSelectedItem(Interval.DAY); cancel.setEnabled(false);
    JPanel root = new JPanel(new BorderLayout(10, 10)); root.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
    JPanel controls = new JPanel(new GridLayout(0, 1, 0, 4));
    JPanel first = new JPanel(new FlowLayout(FlowLayout.LEFT));
    first.add(new JLabel("Source")); first.add(provider); first.add(new JLabel("Symbol")); first.add(symbol);
    first.add(new JLabel("Bar size")); first.add(interval); first.add(load); first.add(cancel); first.add(csv);
    JPanel second = new JPanel(new FlowLayout(FlowLayout.LEFT));
    second.add(new JLabel("Refresh")); second.add(refresh); second.add(new JLabel("Recent completed bars")); second.add(history);
    second.add(new JLabel("Training passes")); second.add(epochs);
    JButton screener = new JButton("Symbols & screener…"); second.add(screener);
    screener.addActionListener(e -> {
      refresh.setSelectedIndex(0);
      if (screenerDialog == null || !screenerDialog.isDisplayable()) screenerDialog = new MarketScreenerDialog(frame, ticker -> {
        if (worker != null) { status.setText("Wait for the current chart refresh, then open the selected symbol."); return; }
        provider.setSelectedIndex(0); interval.setSelectedItem(Interval.DAY);
        symbol.setSelectedItem(ticker); start(null);
      });
      screenerDialog.setVisible(true); screenerDialog.toFront();
    });
    second.add(new JLabel("Chart bars"));
    JSlider chartBars = new JSlider(30, 500, 160); chartBars.setPreferredSize(new Dimension(140, 24));
    chartBars.addChangeListener(e -> { prices.visibleBars = chartBars.getValue(); prices.repaint(); }); second.add(chartBars);
    controls.add(first); controls.add(second); controls.add(freshness); root.add(controls, BorderLayout.NORTH);
    JPanel forecastPanel = new JPanel(new BorderLayout(8, 8));
    forecastPanel.add(probabilities, BorderLayout.CENTER);
    JTable table = new JTable(jointRows); table.setRowHeight(22); table.setFillsViewportHeight(true);
    JScrollPane tableScroll = new JScrollPane(table); tableScroll.setPreferredSize(new Dimension(340, 230));
    forecastPanel.add(tableScroll, BorderLayout.EAST); forecastPanel.add(scores, BorderLayout.SOUTH);
    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, prices, forecastPanel);
    split.setResizeWeight(.64); split.setBorder(BorderFactory.createEmptyBorder()); root.add(split, BorderLayout.CENTER);
    JPanel footer = new JPanel(new BorderLayout(4, 4)); footer.add(status, BorderLayout.CENTER);
    progress.setPreferredSize(new Dimension(130, 18)); footer.add(progress, BorderLayout.EAST);
    JLabel note = new JLabel("Two-bar scenarios are illustrative, not price bounds. Experimental forecasts; no trading execution.");
    note.setForeground(new Color(95, 95, 95)); footer.add(note, BorderLayout.SOUTH); root.add(footer, BorderLayout.SOUTH);
    frame.setContentPane(root); frame.setLocationByPlatform(true);
    timer = new Timer(60_000, e -> { if (!imported && worker == null) start(null); });
    refresh.addActionListener(e -> {
      timer.stop(); int choice = refresh.getSelectedIndex();
      if (choice > 0 && !imported) { timer.setDelay(choice == 1 ? 30_000 : choice == 2 ? 60_000 : 300_000); timer.setInitialDelay(timer.getDelay()); timer.start(); }
    });
    provider.addActionListener(e -> {
      changing = true;
      symbol.removeAllItems();
      String[] choices = provider.getSelectedIndex() == 0
          ? new String[] {"SPY", "QQQ", "AAPL", "MSFT", "^GSPC", "^IXIC", "BTC-USD"}
          : new String[] {"NASDAQCOM", "SP500", "DJIA"};
      for (String choice : choices) symbol.addItem(choice);
      if (provider.getSelectedIndex() == 1) interval.setSelectedItem(Interval.DAY);
      interval.setEnabled(provider.getSelectedIndex() == 0); changing = false; clear();
    });
    symbol.addActionListener(e -> { if (!changing) clear(); });
    interval.addActionListener(e -> { if (!changing) clear(); });
    history.addChangeListener(e -> clearForecast()); epochs.addChangeListener(e -> clearForecast());
    load.addActionListener(e -> start(null)); cancel.addActionListener(e -> { if (worker != null) worker.cancel(true); });
    csv.addActionListener(e -> {
      JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle("Open Date,Close CSV — timestamps must match the selected bar size");
      if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) start(chooser.getSelectedFile().toPath());
    });
    frame.addWindowListener(new WindowAdapter() {
      @Override public void windowClosed(WindowEvent e) { timer.stop(); if (worker != null) worker.cancel(true); }
    });
  }
  private void clear() {
    current = null; imported = false; prices.view = null; clearForecast();
    freshness.setText("Press Refresh & train for the selected symbol and bar size."); prices.repaint();
  }
  private void clearForecast() {
    if (current != null) { current = new View(current.snapshot, null, "Training settings changed"); prices.view = current; }
    probabilities.result = null; probabilities.repaint(); jointRows.setRowCount(0);
    scores.setText("Refresh to train and evaluate this configuration."); prices.repaint();
  }
  private void busy(boolean busy) {
    for (Component control : new Component[] {provider, symbol, interval, history, epochs, load, csv}) control.setEnabled(!busy);
    if (!busy) interval.setEnabled(provider.getSelectedIndex() == 0);
    cancel.setEnabled(busy); progress.setIndeterminate(busy);
  }
  private void render(View view) {
    current = view; prices.view = view; prices.repaint();
    MarketData.Snapshot snap = view.snapshot;
    var last = snap.bars().get(snap.bars().size() - 1);
    freshness.setText(snap.symbol() + " · " + snap.source() + " · Last bar: " + barTime(snap, last.time())
        + " · Checked: " + TIME.format(snap.fetchedAt()));
    freshness.setToolTipText("Intraday times use " + ZoneId.systemDefault() + "; daily/weekly/monthly bar dates use UTC. Feed delay varies by exchange. Last bar may be unfinished.");
    status.setText(view.message); status.setToolTipText(view.message);
    jointRows.setRowCount(0); probabilities.result = view.forecast; probabilities.repaint();
    if (view.forecast == null) { scores.setText("Chart loaded; forecast not yet available."); return; }
    var f = view.forecast;
    for (int i = 0; i < 9; i++) jointRows.addRow(new Object[] {STATE[i / 3], STATE[i % 3], String.format(Locale.ROOT, "%.1f%%", f.joint()[i] * 100)});
    scores.setText(String.format(Locale.ROOT,
        "Using %s · Held-out two-bar loss: network %.3f / independent %.3f (lower is better) · %d test windows",
        f.useNetwork() ? "pairwise network" : "independent baseline", f.modelTestLoss(), f.baselineTestLoss(), f.testRows()));
    scores.setToolTipText(String.format(Locale.ROOT,
        "Selected on validation only (network %.4f, independent %.4f). Split rows: %d / %d / %d. Selected epoch: %d. Work: %,d. Evaluation is historical and windows overlap within each split.",
        f.modelValidationLoss(), f.baselineValidationLoss(), f.trainRows(), f.validationRows(), f.testRows(), f.selectedEpoch(), f.work()));
  }
  private void start(Path file) {
    if (worker != null) return;
    String ticker = symbol.getEditor().getItem().toString().trim();
    Interval barSize = (Interval) interval.getSelectedItem();
    int source = provider.getSelectedIndex(), count = (Integer) history.getValue(), passes = (Integer) epochs.getValue();
    View previous = current;
    boolean reuseImport = file == null && imported && previous != null;
    if (file != null) { imported = true; refresh.setSelectedIndex(0); timer.stop(); }
    busy(true); status.setText(file == null ? "Fetching prices…" : "Reading CSV…");
    worker = new SwingWorker<>() {
      @Override protected View doInBackground() throws Exception {
        MarketData.Snapshot snap;
        if (file != null) {
          if (Files.size(file) > 8_000_000) throw new IOException("CSV exceeds 8 MB");
          snap = MarketData.parseCsv(Files.readString(file), "Local CSV", file.getFileName().toString(), barSize, Instant.now());
        } else if (reuseImport) snap = previous.snapshot;
        else snap = source == 0 ? MarketData.yahoo(ticker, barSize) : MarketData.fred(ticker);
        if (isCancelled()) throw new CancellationException();
        List<MarketData.Bar> completed = snap.completed();
        List<MarketData.Bar> selected = completed.subList(Math.max(0, completed.size() - count), completed.size());
        if (previous != null && previous.forecast != null && previous.snapshot.symbol().equals(snap.symbol())
            && previous.snapshot.source().equals(snap.source()) && previous.snapshot.interval() == snap.interval()
            && lastHistory == count && lastEpochs == passes && previous.forecast.trainingBars().equals(selected))
          return new View(snap, previous.forecast, "Prices refreshed. Completed bars unchanged; keeping the fitted model.");
        publish(new View(snap, null, "Training and evaluating on completed bars…"));
        MarketForecaster.Result result = MarketForecaster.fit(snap, count, passes);
        return new View(snap, result, "Ready · Forecast starts at completed bar "
            + barTime(snap, result.trainingBars().get(result.trainingBars().size() - 1).time())
            + String.format(Locale.ROOT, " · Little-change band: approximately ±%.3f%%", 100 * Math.expm1(result.threshold())));
      }
      @Override protected void process(List<View> updates) { if (!isCancelled()) render(updates.get(updates.size() - 1)); }
      @Override protected void done() {
        try { if (!isCancelled()) { render(get()); lastHistory = count; lastEpochs = passes; } }
        catch (Exception error) {
          Throwable cause = error.getCause() == null ? error : error.getCause();
          status.setText("Update failed: " + cause.getMessage()); status.setToolTipText(status.getText());
        } finally {
          if (isCancelled()) status.setText("Cancelled. No new forecast was applied.");
          worker = null; busy(false);
        }
      }
    };
    worker.execute();
  }

  /** Java2D chart: actual observations and nine categorical-return scenarios. */
  static class PriceChart extends JPanel {
    View view; int visibleBars = 160;
    private static final Color BG = new Color(20, 28, 41), GRID = new Color(48, 61, 78), TEXT = new Color(204, 215, 229);
    PriceChart() { setPreferredSize(new Dimension(1000, 390)); setToolTipText(""); }
    @Override protected void paintComponent(Graphics graphics) {
      super.paintComponent(graphics); Graphics2D g = (Graphics2D) graphics.create();
      try {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(BG); g.fillRect(0, 0, getWidth(), getHeight()); g.setColor(TEXT);
        if (view == null) { g.drawString("Load prices to view history and two-bar scenarios", 24, 35); return; }
        var all = view.snapshot.bars(); int start = Math.max(0, all.size() - visibleBars), n = all.size() - start;
        double min = Double.POSITIVE_INFINITY, max = 0;
        for (int i = start; i < all.size(); i++) { min = Math.min(min, all.get(i).close()); max = Math.max(max, all.get(i).close()); }
        int anchor = -1; double anchorPrice = 0;
        double[] representative = null;
        if (view.forecast != null) {
          var completed = view.forecast.trainingBars(); var end = completed.get(completed.size() - 1);
          for (int i = start; i < all.size(); i++) if (all.get(i).time().equals(end.time())) anchor = i - start;
          anchorPrice = end.close(); representative = view.forecast.representativeReturns();
          for (double a : representative) for (double b : representative) {
            min = Math.min(min, anchorPrice * Math.exp(a + b)); max = Math.max(max, anchorPrice * Math.exp(a + b));
          }
        }
        double pad = Math.max(max * .001, (max - min) * .12); min -= pad; max += pad;
        int left = 80, top = 45, bottom = getHeight() - 48, right = getWidth() - 42;
        double dx = (right - left) / (double) Math.max(2, n + 2), scale = (bottom - top) / (max - min);
        for (int tick = 0; tick <= 4; tick++) {
          int y = top + (bottom - top) * tick / 4; g.setColor(GRID); g.drawLine(left, y, right, y);
          g.setColor(TEXT); g.drawString(String.format(Locale.ROOT, "%.2f", max - tick * (max - min) / 4), 8, y + 4);
        }
        Path2D line = new Path2D.Double();
        for (int i = 0; i < n; i++) {
          double x = left + i * dx, y = bottom - (all.get(start + i).close() - min) * scale;
          if (i == 0) line.moveTo(x, y); else line.lineTo(x, y);
        }
        g.setColor(new Color(76, 202, 230)); g.setStroke(new BasicStroke(2)); g.draw(line);
        if (anchor >= 0 && representative != null) {
          double[] joint = view.forecast.joint();
          g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[] {5, 4}, 0));
          for (int i = 0; i < 9; i++) {
            double p1 = anchorPrice * Math.exp(representative[i / 3]), p2 = p1 * Math.exp(representative[i % 3]);
            g.setColor(new Color(239, 190, 91, Math.min(220, 45 + (int) (joint[i] * 400))));
            Path2D path = new Path2D.Double(); path.moveTo(left + anchor * dx, bottom - (anchorPrice - min) * scale);
            path.lineTo(left + (anchor + 1) * dx, bottom - (p1 - min) * scale);
            path.lineTo(left + (anchor + 2) * dx, bottom - (p2 - min) * scale); g.draw(path);
          }
          g.setColor(TEXT); g.drawString("+1", (int) (left + (anchor + 1) * dx), bottom + 20);
          g.drawString("+2 bars", (int) (left + (anchor + 2) * dx), bottom + 35);
        }
        g.setColor(TEXT); g.drawString(view.snapshot.symbol() + " · " + view.snapshot.interval() + " · " + view.snapshot.unit()
            + "      Cyan: observations    Gold: illustrative scenarios", 18, 25);
        g.drawString(barTime(view.snapshot, all.get(start).time()), left, bottom + 20);
        String last = barTime(view.snapshot, all.get(all.size() - 1).time());
        g.drawString(last, Math.max(left + 160, right - 250), getHeight() - 8);
      } finally { g.dispose(); }
    }
    @Override public String getToolTipText(MouseEvent e) {
      if (view == null) return null;
      var bars = view.snapshot.bars(); int start = Math.max(0, bars.size() - visibleBars), n = bars.size() - start;
      double dx = (getWidth() - 122.0) / Math.max(2, n + 2);
      int index = Math.max(0, Math.min(n - 1, (int) Math.round((e.getX() - 80) / dx)));
      var bar = bars.get(start + index);
      return barTime(view.snapshot, bar.time()) + String.format(Locale.ROOT, " · %.4f · %s", bar.close(), bar.complete() ? "completed" : "not yet complete");
    }
  }
  static class ProbabilityChart extends JPanel {
    MarketForecaster.Result result;
    ProbabilityChart() { setPreferredSize(new Dimension(600, 235)); }
    @Override protected void paintComponent(Graphics graphics) {
      super.paintComponent(graphics); Graphics2D g = (Graphics2D) graphics.create();
      try {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (result == null) { g.drawString("Forecast probabilities will appear after training.", 18, 28); return; }
        Color[] colors = {new Color(194, 75, 82), new Color(116, 132, 153), new Color(30, 147, 119)};
        int width = Math.max(1, (getWidth() - 70) / 6), floor = getHeight() - 40, height = Math.max(30, floor - 52);
        for (int horizon = 0; horizon < 2; horizon++) {
          double[] p = result.marginal(horizon);
          g.setColor(Color.DARK_GRAY); g.drawString(horizon == 0 ? "Next observed bar" : "Following observed bar", 25 + horizon * 3 * width, 20);
          for (int state = 0; state < 3; state++) {
            int x = 25 + (horizon * 3 + state) * width, h = (int) Math.round(p[state] * height);
            g.setColor(colors[state]); g.fillRoundRect(x, floor - h, Math.max(5, width - 18), h, 6, 6);
            g.setColor(Color.DARK_GRAY); g.drawString(String.format(Locale.ROOT, "%.1f%%", 100 * p[state]), x, floor - h - 5);
            g.drawString(STATE[state], x, floor + 20);
          }
        }
      } finally { g.dispose(); }
    }
  }
}
