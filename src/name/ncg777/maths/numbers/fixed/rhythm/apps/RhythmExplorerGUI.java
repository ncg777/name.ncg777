package name.ncg777.maths.numbers.fixed.rhythm.apps;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Random;
import java.util.concurrent.CancellationException;
import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import name.ncg777.maths.numbers.fixed.rhythm.exploration.*;
import name.ncg777.maths.numbers.fixed.rhythm.exploration.RhythmMask.*;
import name.ncg777.maths.numbers.fixed.rhythm.exploration.RhythmExplorer.*;

/** A Swing front end over the same services as RhythmExplorerApp; all searches run off the EDT. */
public final class RhythmExplorerGUI extends JPanel {
  private final JComboBox<Cipher> cipher = new JComboBox<>(Cipher.values());
  private Cipher previousCipher = Cipher.Hexadecimal;
  private final JTextField mask = new JTextField("9249", 24), protect = new JTextField("", 15);
  private final RhythmClauseBuilder where = new RhythmClauseBuilder("NONEMPTY AND HITS(6)");
  private final RhythmClauseBuilder transition = new RhythmClauseBuilder("TRUE");
  private final JComboBox<Erasure> policy = new JComboBox<>(Erasure.values());
  private final JComboBox<String> unit = new JComboBox<>(new String[]{"bit", "digit"});
  private final JSpinner erase = integer(4, 0, 256), changes = integer(2, 0, 256), hits = integer(-1, -1, 256);
  private final JSpinner memory = integer(8, 0, 1000), seed = new JSpinner(new SpinnerNumberModel(777L, Long.MIN_VALUE, Long.MAX_VALUE, 1L));
  private final JSpinner temperature = decimal(.15, .001, 100, .05), euclidean = decimal(0, 0, 100, .1), sync = decimal(-1, -1, 1, .1);
  private final JSpinner beat = integer(4, 1, 256), limit = integer(512, 1, 10000), budget = integer(100000, 1, 100000);
  private final JSpinner iterations = integer(64, 1, 10000), interval = integer(300, 50, 10000), bpm = integer(100, 20, 300), drum = integer(36, 0, 127);
  private final JLabel status = new JLabel("Edit a mask or click a step, then Complete. Shift-click protects a step.");
  private final JLabel preview = new JLabel(" ");
  private final StepGrid grid = new StepGrid();
  private final DefaultTableModel tableModel = new DefaultTableModel(new String[]{"#", "Binary rhythm", "Hits", "Changes", "Energy", "Pool probability", "Search"}, 0) {
    @Override public boolean isCellEditable(int row, int col) { return false; }
  };
  private final JTable table = new JTable(tableModel);
  private final List<Row> rows = new ArrayList<>();
  private final List<String> undo = new ArrayList<>();
  private final JPanel controls = new JPanel(new BorderLayout());
  private final JPanel actionControls = new JPanel(new GridLayout(0, 1, 3, 3));
  private final JButton stop = new JButton("Stop generation"), stopAudio = new JButton("Stop audio");
  private Job job;
  private Session session;
  private Settings lastSettings;
  private String sessionCurrent;
  private boolean changingCipher;
  private Sequencer sequencer;
  private Synthesizer synthesizer;
  private record Row(String bits, Step step) {}
  public record Document(int version, String mask, Cipher cipher, Settings settings, int iterations, int interval, int bpm, int drum) {
    public Document {
      if (version != 1) throw new IllegalArgumentException("Unsupported setup version.");
      RhythmMask parsed = RhythmMask.parse(mask, cipher);
      java.util.Objects.requireNonNull(settings, "Missing settings.");
      RhythmExplorer.rule(settings.where(), parsed.bits().length());
      RhythmExplorer.rule(settings.transition(), parsed.bits().length() * 2);
      settings.selection().validateLength(parsed.bits().length());
      if ((settings.unitWidth() != 1 && settings.unitWidth() != cipher.width) || settings.eraseCount() > parsed.bits().length() / settings.unitWidth())
        throw new IllegalArgumentException("Invalid erasure unit/count for this setup.");
      for (int i : settings.protectedSteps()) if (i < 0 || i >= parsed.bits().length()) throw new IllegalArgumentException("Protected step out of range.");
      if (iterations < 1 || iterations > 10000 || interval < 50 || interval > 10000 || bpm < 20 || bpm > 300 || drum < 0 || drum > 127)
        throw new IllegalArgumentException("Invalid run/MIDI settings.");
    }
  }

  public RhythmExplorerGUI() {
    super(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    JLabel title = new JLabel("Rhythm Explorer — complete, erase, evolve");
    title.setFont(title.getFont().deriveFont(Font.BOLD, 21f)); add(title, BorderLayout.NORTH);
    cipher.setSelectedItem(Cipher.Hexadecimal);
    mask.setName("Rhythm mask"); protect.setName("Protected steps");
    JPanel editor = new JPanel(new BorderLayout(5, 5));
    JPanel input = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
    input.add(new JLabel("Cipher")); input.add(cipher); input.add(mask);
    input.add(button("Load mask", this::loadGrid)); editor.add(input, BorderLayout.NORTH);
    JScrollPane gridScroll = new JScrollPane(grid); gridScroll.setPreferredSize(new Dimension(550, 84));
    editor.add(gridScroll, BorderLayout.CENTER);
    JPanel help = new JPanel(new GridLayout(0, 1));
    help.add(new JLabel("Click: rest → onset → unknown. Shift-click: protect/unprotect. Step indices start at 0."));
    help.add(preview); editor.add(help, BorderLayout.SOUTH);
    JPanel right = new JPanel(new BorderLayout(6, 6)); right.add(editor, BorderLayout.NORTH);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    int[] widths = {45, 300, 45, 60, 65, 100, 135};
    for (int i = 0; i < widths.length; i++) table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    table.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    table.setRowHeight(24); table.setName("Rhythm results");
    JScrollPane resultsScroll = new JScrollPane(table);
    resultsScroll.setColumnHeaderView(table.getTableHeader());
    right.add(resultsScroll, BorderLayout.CENTER);
    JPanel resultActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
    resultActions.add(button("Use selected", this::useSelected));
    resultActions.add(button("Copy selected", this::copySelected));
    resultActions.add(button("Audition selected", this::audition)); resultActions.add(stopAudio);
    resultActions.add(button("Export results…", this::export));
    stopAudio.addActionListener(e -> closeAudio());
    right.add(resultActions, BorderLayout.SOUTH);

    JTabbedPane clauses = new JTabbedPane();
    clauses.addTab("Rhythm rule", where); clauses.addTab("Transition rule", transition);
    clauses.setToolTipTextAt(1, "Applied to previous rhythm followed by candidate: twice the step count.");
    JPanel ruleTab = new JPanel(new BorderLayout(5, 5)); ruleTab.add(clauses, BorderLayout.CENTER);
    ruleTab.add(button("Validate expressions", () -> {
      RhythmMask m = parsed(); where.validateClause(m.bits().length()); transition.validateClause(m.bits().length() * 2);
      status.setText("Expressions compile. Rhythm: " + m.bits().length() + " steps; transition: " + m.bits().length() * 2 + " steps.");
    }), BorderLayout.SOUTH);
    JPanel evolveTab = new JPanel(new GridLayout(0, 2, 5, 5));
    field(evolveTab, "Release units", erase); field(evolveTab, "Unit", unit); field(evolveTab, "Mask policy", policy);
    field(evolveTab, "Protected steps (0-based)", protect);
    field(evolveTab, "Target changed steps", changes); field(evolveTab, "Target hits (-1: off)", hits);
    field(evolveTab, "Temperature", temperature); field(evolveTab, "Recent history", memory);
    field(evolveTab, "Euclidean weight", euclidean); field(evolveTab, "Sync target (-1: off)", sync);
    field(evolveTab, "Steps per beat", beat);
    sync.setToolTipText("0..1: fraction of weak onsets whose next beat boundary is silent. -1 disables this preference.");
    JPanel runTab = new JPanel(new GridLayout(0, 2, 5, 5));
    field(runTab, "Seed", seed); field(runTab, "Candidate limit", limit); field(runTab, "Attempt budget", budget);
    field(runTab, "Batch iterations", iterations); field(runTab, "Continuous delay (ms)", interval);
    field(runTab, "Audition/MIDI BPM", bpm); field(runTab, "GM percussion note", drum);
    JTabbedPane settings = new JTabbedPane(); settings.addTab("Predicates", ruleTab); settings.addTab("Evolution", evolveTab); settings.addTab("Run / MIDI", runTab);
    controls.add(settings, BorderLayout.CENTER);
    JPanel first = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
    first.add(button("Complete", this::complete)); first.add(button("Erase current", this::eraseCurrent)); first.add(button("Undo", this::undo));
    actionControls.add(first);
    JPanel second = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
    second.add(button("Evolve once", () -> evolve(1))); second.add(button("Evolve batch", () -> evolve(number(iterations))));
    second.add(button("Run continuously", () -> evolve(0))); actionControls.add(second);
    JPanel files = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
    files.add(button("Save setup…", this::save)); files.add(button("Load setup…", this::load));
    actionControls.add(files); controls.add(actionControls, BorderLayout.SOUTH);
    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, controls, right);
    controls.setMinimumSize(new Dimension(400, 300)); right.setMinimumSize(new Dimension(400, 300));
    split.setResizeWeight(.4); split.setDividerLocation(470); add(split, BorderLayout.CENTER);
    JPanel bottom = new JPanel(new BorderLayout(8, 8)); bottom.add(status, BorderLayout.CENTER); bottom.add(stop, BorderLayout.EAST);
    stop.setEnabled(false); stop.addActionListener(e -> { if (job != null) job.requestStop(); }); add(bottom, BorderLayout.SOUTH);
    cipher.addActionListener(e -> convertCipher());
    mask.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
      private void update() { try { loadGrid(); } catch (IllegalArgumentException ignored) {} }
      public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
      public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
      public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
    });
    loadGrid();
  }
  private static JSpinner integer(int value, int min, int max) { return new JSpinner(new SpinnerNumberModel(value, min, max, 1)); }
  private static JSpinner decimal(double value, double min, double max, double step) { return new JSpinner(new SpinnerNumberModel(value, min, max, step)); }
  private static int number(JSpinner spinner) { return ((Number)spinner.getValue()).intValue(); }
  private static double real(JSpinner spinner) { return ((Number)spinner.getValue()).doubleValue(); }
  private static void field(JPanel panel, String label, JComponent value) {
    JLabel l = new JLabel(label); l.setLabelFor(value); panel.add(l); panel.add(value);
    value.getAccessibleContext().setAccessibleName(label);
  }
  private JButton button(String name, Runnable action) {
    JButton b = new JButton(name); b.addActionListener(e -> safe(action)); return b;
  }
  private void safe(Runnable action) {
    try { action.run(); }
    catch (Exception e) { status.setText("Error: " + message(e)); }
  }
  private static String message(Throwable e) {
    while (e.getCause() != null) e = e.getCause();
    return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
  }
  private RhythmMask parsed() { return RhythmMask.parse(mask.getText(), (Cipher)cipher.getSelectedItem()); }
  private void commitSpinners(Component c) {
    if (c instanceof JSpinner s) {
      try { s.commitEdit(); } catch (java.text.ParseException e) { throw new IllegalArgumentException("Invalid numeric setting."); }
    } else if (c instanceof Container container) for (Component child : container.getComponents()) commitSpinners(child);
  }
  private Settings settings() {
    commitSpinners(controls);
    return new Settings(where.clause(), transition.clause(), number(erase), unit.getSelectedIndex() == 0 ? 1 : ((Cipher)cipher.getSelectedItem()).width,
        (Erasure)policy.getSelectedItem(), RhythmMask.positions(protect.getText()), number(limit), number(budget), ((Number)seed.getValue()).longValue(),
        new Selection(number(changes), number(hits), real(temperature), number(memory), real(euclidean), real(sync), number(beat)));
  }
  private void loadGrid() {
    grid.bits = parsed().bits(); grid.revalidate(); grid.repaint(); updatePreview();
  }
  private void updatePreview() {
    StringBuilder s = new StringBuilder(grid.bits.length() + " steps");
    for (Cipher c : new Cipher[]{Cipher.Octal, Cipher.Hexadecimal}) {
      try { s.append("  |  ").append(c).append(": ").append(new RhythmMask(grid.bits).format(c)); }
      catch (IllegalArgumentException ignored) { /* Binary preserves partial digit information. */ }
    }
    preview.setText(s.toString());
  }
  private void convertCipher() {
    if (changingCipher) return;
    Cipher next = (Cipher)cipher.getSelectedItem();
    try {
      String converted = RhythmMask.parse(mask.getText(), previousCipher).format(next);
      mask.setText(converted); previousCipher = next; loadGrid();
    } catch (IllegalArgumentException e) {
      changingCipher = true; cipher.setSelectedItem(previousCipher); changingCipher = false; status.setText(e.getMessage());
    }
  }
  private void setBits(String bits) {
    Cipher c = (Cipher)cipher.getSelectedItem(); String text;
    try { text = new RhythmMask(bits).format(c); }
    catch (IllegalArgumentException e) { c = Cipher.Binary; text = bits; }
    changingCipher = true; cipher.setSelectedItem(c); previousCipher = c; changingCipher = false;
    mask.setText(text); loadGrid();
  }
  private void remember() { undo.add(parsed().bits()); if (undo.size() > 1000) undo.remove(0); }
  private void undo() { requireIdle(); if (!undo.isEmpty()) { setBits(undo.remove(undo.size() - 1)); session = null; } }
  private void requireIdle() { if (job != null) throw new IllegalStateException("Stop generation before editing."); }
  private String selected() {
    int row = table.getSelectedRow();
    if (row < 0) throw new IllegalArgumentException("Select a result first.");
    return rows.get(table.convertRowIndexToModel(row)).bits();
  }
  private void useSelected() { requireIdle(); String value = selected(); remember(); setBits(value); session = null; }
  private void copySelected() { Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(selected()), null); status.setText("Copied binary rhythm."); }
  private void eraseCurrent() {
    requireIdle(); Settings s = settings();
    RhythmMask next = parsed().erase(s.eraseCount(), s.unitWidth(), s.erasure(), s.protectedSteps(), new Random(s.seed()));
    remember(); setBits(next.bits()); session = null; status.setText("Released positions are ?. Complete to explore alternatives.");
  }
  private void clearRows() { rows.clear(); tableModel.setRowCount(0); }
  private void addRow(String bits, Step step) {
    if (step != null && rows.size() == 2000) { rows.remove(0); tableModel.removeRow(0); }
    rows.add(new Row(bits, step));
    tableModel.addRow(new Object[]{step == null ? rows.size() : step.iteration(), bits, new RhythmMask(bits).rhythm().getK(),
        step == null ? "" : step.changed(), step == null ? "" : String.format(java.util.Locale.ROOT, "%.3f", step.score().total()),
        step == null ? "" : String.format(java.util.Locale.ROOT, "%.3f", step.probability()), step == null ? "" : step.stop() + (step.held() ? " / held" : "")});
  }
  private void complete() {
    requireIdle(); RhythmMask m = parsed(); Settings s = settings(); var predicate = RhythmExplorer.rule(s.where(), m.bits().length());
    clearRows(); session = null; loadGrid();
    start(new Job() {
      Search result;
      @Override void work() { result = RhythmExplorer.complete(m, predicate, s.limit(), s.budget(), s.seed()); }
      @Override void finished() {
        for (String bits : result.candidates()) addRow(bits, null);
        status.setText(result.candidates().size() + " completions; " + result.attempts() + " attempts; " + result.stop() + ". Select a result to reuse, copy or audition.");
        if (!rows.isEmpty()) table.setRowSelectionInterval(0, 0);
      }
    });
  }
  private void evolve(int count) {
    requireIdle(); String initial = parsed().bits(); Settings s = settings();
    if (session == null || !s.equals(lastSettings) || !initial.equals(sessionCurrent)) {
      session = new Session(initial, s); lastSettings = s; sessionCurrent = initial; clearRows();
    }
    Session active = session; int delay = number(interval); remember();
    start(new Job() {
      @Override void work() throws Exception {
        for (int i = 0; count == 0 || i < count; i++) {
          RhythmExplorer.check();
          Step step = active.next(); publish(step);
          if (count == 0) Thread.sleep(delay);
        }
      }
      @Override protected void process(List<Step> steps) {
        for (Step step : steps) addRow(step.bits(), step);
        Step last = steps.get(steps.size() - 1); sessionCurrent = last.bits(); setBits(last.bits());
        status.setText("Iteration " + last.iteration() + ": " + last.changed() + " changes; " + last.candidates() + " candidates; " + last.stop() + ". History keeps latest 2,000.");
      }
      @Override void finished() { status.setText("Evolution finished. Reuse a result, adjust settings, or continue evolving."); }
    });
  }
  private abstract class Job extends SwingWorker<Void, Step> {
    volatile Thread thread;
    volatile boolean stopping;
    abstract void work() throws Exception;
    void finished() {}
    void requestStop() { stopping = true; Thread t = thread; if (t != null) t.interrupt(); status.setText("Stopping…"); }
    @Override protected Void doInBackground() throws Exception {
      thread = Thread.currentThread();
      try { if (stopping) throw new CancellationException(); work(); return null; }
      finally { thread = null; Thread.interrupted(); }
    }
    @Override protected void done() {
      try { get(); finished(); }
      catch (Exception e) { status.setText(stopping ? "Generation stopped. Completed results are retained." : "Error: " + message(e)); }
      finally { job = null; busy(false); }
    }
  }
  private void start(Job next) { job = next; busy(true); status.setText("Working…"); next.execute(); }
  private static void enabled(Component c, boolean enabled) {
    c.setEnabled(enabled); if (c instanceof Container container) for (Component child : container.getComponents()) enabled(child, enabled);
  }
  private void busy(boolean value) { enabled(controls, !value); mask.setEnabled(!value); cipher.setEnabled(!value); stop.setEnabled(value); if (!value) { where.refreshControls(); transition.refreshControls(); } grid.repaint(); }

  private Path chooseFile(boolean save) {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle(save ? "Save (.json setup, .jsonl results, or .mid MIDI)" : "Load rhythm setup (.json)");
    if ((save ? chooser.showSaveDialog(this) : chooser.showOpenDialog(this)) != JFileChooser.APPROVE_OPTION) return null;
    Path path = chooser.getSelectedFile().toPath();
    if (save && Files.exists(path) && JOptionPane.showConfirmDialog(this, "Replace " + path.getFileName() + "?", "Existing file", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return null;
    return path;
  }
  private void save() {
    Settings s = settings(); parsed();
    Document doc = new Document(1, mask.getText(), (Cipher)cipher.getSelectedItem(), s, number(iterations), number(interval), number(bpm), number(drum));
    Path file = chooseFile(true); if (file == null) return;
    try { new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(file.toFile(), doc); status.setText("Saved setup. Loading restarts its seed/history."); }
    catch (Exception e) { throw new IllegalStateException(e); }
  }
  private void load() {
    requireIdle(); Path file = chooseFile(false); if (file == null) return;
    try {
      Document d = new ObjectMapper().readValue(file.toFile(), Document.class);
      if (d.version() != 1) throw new IllegalArgumentException("Unsupported setup version.");
      RhythmMask parsed = RhythmMask.parse(d.mask(), d.cipher()); Settings s = d.settings();
      RhythmExplorer.rule(s.where(), parsed.bits().length()); RhythmExplorer.rule(s.transition(), parsed.bits().length() * 2);
      s.selection().validateLength(parsed.bits().length());
      changingCipher = true; cipher.setSelectedItem(d.cipher()); previousCipher = d.cipher(); changingCipher = false;
      mask.setText(d.mask()); where.setClause(s.where()); transition.setClause(s.transition());
      erase.setValue(s.eraseCount()); unit.setSelectedIndex(s.unitWidth() == 1 ? 0 : 1); policy.setSelectedItem(s.erasure());
      protect.setText(s.protectedSteps().stream().sorted().map(Object::toString).collect(java.util.stream.Collectors.joining(",")));
      seed.setValue(s.seed()); limit.setValue(s.limit()); budget.setValue(s.budget());
      Selection o = s.selection(); changes.setValue(o.targetChanges()); hits.setValue(o.targetHits()); temperature.setValue(o.temperature()); memory.setValue(o.memory());
      euclidean.setValue(o.euclideanWeight()); sync.setValue(o.targetSyncopation()); beat.setValue(o.beatSteps());
      iterations.setValue(d.iterations()); interval.setValue(d.interval()); bpm.setValue(d.bpm()); drum.setValue(d.drum());
      clearRows(); undo.clear(); session = null; loadGrid(); status.setText("Setup loaded. Seed/history will restart on the next evolution.");
    } catch (Exception e) { throw new IllegalArgumentException(e); }
  }
  private void export() {
    requireIdle(); if (rows.isEmpty()) throw new IllegalArgumentException("Generate results first.");
    Path file = chooseFile(true); if (file == null) return;
    try {
      if (file.toString().toLowerCase(java.util.Locale.ROOT).endsWith(".mid")) {
        Sequence sequence = RhythmMidi.sequence(rows.stream().map(Row::bits).toList(), number(bpm), number(beat), number(drum));
        MidiSystem.write(sequence, 1, file.toFile()); status.setText("Exported MIDI in table order.");
      } else {
        ObjectMapper json = new ObjectMapper();
        try (var writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
          for (Row row : rows) { writer.write(json.writeValueAsString(row.step() == null ? java.util.Map.of("bits", row.bits()) : row.step())); writer.newLine(); }
        }
        status.setText("Exported JSONL. Use a .mid extension to export MIDI.");
      }
    } catch (Exception e) { throw new IllegalStateException(e); }
  }
  private void audition() {
    requireIdle(); String bits = selected(); commitSpinners(controls);
    closeAudio();
    int tempo = number(bpm), steps = number(beat), note = number(drum);
    start(new Job() {
      Sequencer player; Synthesizer synth;
      @Override void work() throws Exception {
        try {
          player = MidiSystem.getSequencer(false); player.open();
          synth = MidiSystem.getSynthesizer(); synth.open();
          player.getTransmitter().setReceiver(synth.getReceiver());
          player.setSequence(RhythmMidi.sequence(List.of(bits), tempo, steps, note));
          RhythmExplorer.check();
        } catch (Exception e) { if (player != null) player.close(); if (synth != null) synth.close(); throw e; }
      }
      @Override void finished() {
        if (stopping) { player.close(); synth.close(); return; }
        sequencer = player; synthesizer = synth; sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY); sequencer.start();
        status.setText("Auditioning selected rhythm. Stop audio to end the loop.");
      }
    });
  }
  private void closeAudio() {
    if (sequencer != null) { sequencer.stop(); sequencer.close(); sequencer = null; }
    if (synthesizer != null) { synthesizer.close(); synthesizer = null; }
  }
  public void close() { if (job != null) job.requestStop(); closeAudio(); }

  private final class StepGrid extends JPanel {
    String bits = "1001001001001001";
    StepGrid() {
      setToolTipText("Click to cycle 0, 1, ?. Shift-click to protect a step.");
      addMouseListener(new MouseAdapter() {
        @Override public void mousePressed(MouseEvent e) { safe(() -> {
          requireIdle(); int index = e.getX() / 30; if (index < 0 || index >= bits.length()) return;
          if (e.isShiftDown()) {
            Set<Integer> positions = new TreeSet<>(RhythmMask.positions(protect.getText()));
            if (!positions.remove(index)) positions.add(index);
            protect.setText(positions.stream().map(Object::toString).collect(java.util.stream.Collectors.joining(","))); repaint();
          } else {
            remember(); char[] b = bits.toCharArray(); b[index] = b[index] == '0' ? '1' : b[index] == '1' ? '?' : '0';
            setBits(new String(b)); session = null;
          }
        }); }
      });
    }
    @Override public Dimension getPreferredSize() { return new Dimension(bits.length() * 30, 62); }
    @Override protected void paintComponent(Graphics graphics) {
      super.paintComponent(graphics); Graphics2D g = (Graphics2D)graphics.create();
      Set<Integer> protectedSteps;
      try { protectedSteps = RhythmMask.positions(protect.getText()); } catch (Exception e) { protectedSteps = Set.of(); }
      for (int i = 0; i < bits.length(); i++) {
        char c = bits.charAt(i);
        g.setColor(c == '1' ? new Color(38, 148, 143) : c == '?' ? new Color(235, 184, 78) : new Color(230, 233, 237));
        g.fillRoundRect(i * 30 + 2, 5, 26, 32, 5, 5);
        g.setColor(Color.BLACK); g.drawString(String.valueOf(c), i * 30 + 11, 26); g.drawString(String.valueOf(i), i * 30 + 8, 53);
        if (protectedSteps.contains(i)) { g.setColor(new Color(120, 50, 170)); g.setStroke(new BasicStroke(2)); g.drawRect(i * 30 + 2, 5, 26, 32); }
      }
      g.dispose();
    }
  }
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
      RhythmExplorerGUI panel = new RhythmExplorerGUI(); JFrame frame = new JFrame("Rhythm Explorer");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); frame.setContentPane(panel); frame.setSize(1280, 740);
      frame.addWindowListener(new WindowAdapter() { @Override public void windowClosed(WindowEvent e) { panel.close(); } });
      frame.setLocationByPlatform(true); frame.setVisible(true);
    });
  }
}
