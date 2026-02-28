package name.ncg777.maths.numbers.fixed.rhythm.apps;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.numbers.fixed.FixedLength;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmDistance;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmNetwork;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmNetworkAnalysis;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmNetworkNavigator;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmPredicateRegistry;

public class RhythmNetworkGUI {

  private JFrame frame;
  private JSpinner lengthSpinner;
  private JComboBox<Cipher.Name> cipherComboBox;
  private JTextField vertexClauseField;
  private JTextField juxtClauseField;
  private JComboBox<String> distanceComboBox;
  private JTextField tempField;
  private JCheckBox selfLoopsCheckBox;

  private JSpinner stepsSpinner;
  private JTextField navTempField;
  private JTextField seedField;
  private JCheckBox analyzeCheckBox;
  private JCheckBox noWalkCheckBox;

  private JTextField savePathField;
  private JTextField loadPathField;
  
  private JTextArea outputArea;

  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      try {
        RhythmNetworkGUI window = new RhythmNetworkGUI();
        window.frame.setVisible(true);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  public RhythmNetworkGUI() {
    initialize();
  }

  private void initialize() {
    frame = new JFrame();
    frame.setTitle("Rhythm Network");
    frame.setBounds(100, 100, 900, 800);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout(10, 10));

    JPanel topPanel = new JPanel(new GridBagLayout());
    GridBagConstraints ogbc = new GridBagConstraints();
    ogbc.gridx = 0;
    ogbc.gridy = 0;
    ogbc.fill = GridBagConstraints.HORIZONTAL;
    ogbc.weightx = 1.0;
    ogbc.insets = new Insets(5, 5, 5, 5);

    // --- 1. Network Settings ---
    JPanel networkPanel = new JPanel(new GridBagLayout());
    networkPanel.setBorder(BorderFactory.createTitledBorder("Network Generation Settings"));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 0.0;
    
    int row = 0;

    gbc.gridx = 0; gbc.gridy = row;
    networkPanel.add(new JLabel("Length L:"), gbc);
    gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.5;
    lengthSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 1024, 1));
    networkPanel.add(lengthSpinner, gbc);
    gbc.weightx = 0.0;

    gbc.gridx = 2; gbc.gridy = row;
    networkPanel.add(new JLabel("Cipher:"), gbc);
    gbc.gridx = 3; gbc.gridy = row; gbc.weightx = 0.5;
    cipherComboBox = new JComboBox<>(Cipher.Name.values());
    cipherComboBox.setSelectedItem(Cipher.Name.Hexadecimal);
    networkPanel.add(cipherComboBox, gbc);
    gbc.weightx = 0.0;
    
    row++;

    gbc.gridx = 0; gbc.gridy = row;
    networkPanel.add(new JLabel("Vertex Clause:"), gbc);
    gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 2; gbc.weightx = 1.0;
    vertexClauseField = new JTextField("");
    networkPanel.add(vertexClauseField, gbc);
    gbc.gridx = 3; gbc.gridwidth = 1; gbc.weightx = 0.0;
    JButton vHelpBtn = new JButton("?");
    vHelpBtn.setToolTipText("Show syntax and available predicates");
    vHelpBtn.addActionListener(e -> showClauseHelp());
    networkPanel.add(vHelpBtn, gbc);

    row++;

    gbc.gridx = 0; gbc.gridy = row;
    networkPanel.add(new JLabel("Juxt Clause:"), gbc);
    gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 2; gbc.weightx = 1.0;
    juxtClauseField = new JTextField("");
    networkPanel.add(juxtClauseField, gbc);
    gbc.gridx = 3; gbc.gridwidth = 1; gbc.weightx = 0.0;
    JButton jHelpBtn = new JButton("?");
    jHelpBtn.setToolTipText("Show syntax and available predicates");
    jHelpBtn.addActionListener(e -> showClauseHelp());
    networkPanel.add(jHelpBtn, gbc);

    row++;

    gbc.gridx = 0; gbc.gridy = row;
    networkPanel.add(new JLabel("Distance:"), gbc);
    gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.5;
    distanceComboBox = new JComboBox<>(new String[]{"HAMMING", "JACCARD", "INTERVAL_VECTOR"});
    networkPanel.add(distanceComboBox, gbc);
    gbc.weightx = 0.0;

    gbc.gridx = 2; gbc.gridy = row;
    networkPanel.add(new JLabel("Temperature T:"), gbc);
    gbc.gridx = 3; gbc.gridy = row; gbc.weightx = 0.5;
    tempField = new JTextField("1.0");
    networkPanel.add(tempField, gbc);
    gbc.weightx = 0.0;
    
    row++;

    gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 3;
    selfLoopsCheckBox = new JCheckBox("Allow Self Loops");
    networkPanel.add(selfLoopsCheckBox, gbc);
    gbc.gridwidth = 1;

    topPanel.add(networkPanel, ogbc);
    ogbc.gridy++;

    // --- 2. Walk Settings ---
    JPanel walkPanel = new JPanel(new GridBagLayout());
    walkPanel.setBorder(BorderFactory.createTitledBorder("Walk & Analysis Settings"));
    
    row = 0;
    gbc.gridx = 0; gbc.gridy = row;
    walkPanel.add(new JLabel("Steps:"), gbc);
    gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.5;
    stepsSpinner = new JSpinner(new SpinnerNumberModel(32, 0, 1000000, 1));
    walkPanel.add(stepsSpinner, gbc);
    gbc.weightx = 0.0;

    gbc.gridx = 2; gbc.gridy = row;
    walkPanel.add(new JLabel("Nav Temp τ:"), gbc);
    gbc.gridx = 3; gbc.gridy = row; gbc.weightx = 0.5;
    navTempField = new JTextField("1.0");
    walkPanel.add(navTempField, gbc);
    gbc.weightx = 0.0;

    row++;

    gbc.gridx = 0; gbc.gridy = row;
    walkPanel.add(new JLabel("Seed (-1 random):"), gbc);
    gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.5;
    seedField = new JTextField("-1");
    walkPanel.add(seedField, gbc);
    gbc.weightx = 0.0;

    gbc.gridx = 2; gbc.gridy = row; gbc.gridwidth = 2;
    JPanel optPanel = new JPanel();
    analyzeCheckBox = new JCheckBox("Analyze");
    noWalkCheckBox = new JCheckBox("Skip Walk");
    optPanel.add(analyzeCheckBox);
    optPanel.add(noWalkCheckBox);
    walkPanel.add(optPanel, gbc);
    gbc.gridwidth = 1;

    topPanel.add(walkPanel, ogbc);
    ogbc.gridy++;

    // --- 3. File Settings ---
    JPanel filePanel = new JPanel(new GridBagLayout());
    filePanel.setBorder(BorderFactory.createTitledBorder("File Settings"));
    
    row = 0;
    gbc.gridx = 0; gbc.gridy = row;
    filePanel.add(new JLabel("Save Path:"), gbc);
    gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 2; gbc.weightx = 1.0;
    savePathField = new JTextField("");
    filePanel.add(savePathField, gbc);
    gbc.gridwidth = 1; gbc.weightx = 0.0;
    gbc.gridx = 3; gbc.gridy = row;
    JButton saveBrowseBtn = new JButton("Browse...");
    saveBrowseBtn.addActionListener(e -> browseFile(savePathField, true));
    filePanel.add(saveBrowseBtn, gbc);

    row++;

    gbc.gridx = 0; gbc.gridy = row;
    filePanel.add(new JLabel("Load Path:"), gbc);
    gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 2; gbc.weightx = 1.0;
    loadPathField = new JTextField("");
    filePanel.add(loadPathField, gbc);
    gbc.gridwidth = 1; gbc.weightx = 0.0;
    gbc.gridx = 3; gbc.gridy = row;
    JButton loadBrowseBtn = new JButton("Browse...");
    loadBrowseBtn.addActionListener(e -> browseFile(loadPathField, false));
    filePanel.add(loadBrowseBtn, gbc);

    topPanel.add(filePanel, ogbc);
    ogbc.gridy++;

    // --- Action Button ---
    JButton runButton = new JButton("Execute");
    runButton.setFont(new Font("SansSerif", Font.BOLD, 14));
    runButton.addActionListener(e -> executeApp());
    JPanel btnPanel = new JPanel();
    btnPanel.add(runButton);
    topPanel.add(btnPanel, ogbc);

    frame.getContentPane().add(topPanel, BorderLayout.NORTH);

    // --- Output Area ---
    outputArea = new JTextArea();
    outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    outputArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(outputArea);
    scrollPane.setBorder(BorderFactory.createTitledBorder("Output"));
    frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
  }

  private void showClauseHelp() {
    JDialog helpDialog = new JDialog(frame, "Clause Syntax & Predicates", false);
    helpDialog.setSize(new Dimension(500, 600));
    helpDialog.setLocationRelativeTo(frame);

    JTextArea helpText = new JTextArea();
    helpText.setEditable(false);
    helpText.setFont(new Font("Monospaced", Font.PLAIN, 12));
    helpText.setMargin(new Insets(10, 10, 10, 10));

    StringBuilder sb = new StringBuilder();
    sb.append("=== Syntax ===\n");
    sb.append("expr   := term  ( 'OR'  term  )*\n");
    sb.append("term   := factor ( 'AND' factor )*\n");
    sb.append("factor := 'NOT' factor | '(' expr ')' | atom\n");
    sb.append("atom   := NAME ( '(' INT ')' )?\n");
    sb.append("\n");
    sb.append("Examples:\n");
    sb.append("  EVEN AND NOT PERIODIC\n");
    sb.append("  (EVEN OR ODDITY) AND MINIMUM_GAP(2)\n");
    sb.append("  TRUE      (matches everything)\n");
    sb.append("  FALSE     (matches nothing)\n");
    sb.append("\n");

    sb.append("=== Available Predicates ===\n");
    
    // Fetch names directly from Registry
    List<String> names = RhythmPredicateRegistry.names().stream()
      .sorted()
      .collect(Collectors.toList());
      
    for (String name : names) {
      sb.append("  ").append(name).append("\n");
    }

    sb.append("\n=== Parameterized Predicates ===\n");
    sb.append("  MINIMUM_GAP(n) : Min interval gap \u2265 n\n");
    sb.append("  MAXIMUM_GAP(n) : Max interval gap \u2264 n\n");
    sb.append("  ORDINAL(n)     : Ordinal rhythm of period n\n");

    helpText.setText(sb.toString());
    helpText.setCaretPosition(0);

    JScrollPane scroll = new JScrollPane(helpText);
    helpDialog.add(scroll);
    helpDialog.setVisible(true);
  }

  private void browseFile(JTextField textField, boolean isSave) {
    JFileChooser chooser = new JFileChooser(".");
    int ret = isSave ? chooser.showSaveDialog(frame) : chooser.showOpenDialog(frame);
    if (ret == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      textField.setText(file.getAbsolutePath());
    }
  }

  private void log(String msg) {
    SwingUtilities.invokeLater(() -> {
      outputArea.append(msg + "\n");
      outputArea.setCaretPosition(outputArea.getDocument().getLength());
    });
  }

  private void executeApp() {
    outputArea.setText("");
    log("Starting execution...");
    new Thread(() -> {
      long t0 = System.nanoTime();
      try {
        // Parse parameters
        int length = (int) lengthSpinner.getValue();
        Cipher.Name cipher = (Cipher.Name) cipherComboBox.getSelectedItem();
        String vertexClause = vertexClauseField.getText().trim();
        String juxtClause = juxtClauseField.getText().trim();
        String distName = (String) distanceComboBox.getSelectedItem();
        double temp = Double.parseDouble(tempField.getText().trim());
        boolean selfLoops = selfLoopsCheckBox.isSelected();

        double navTemp = Double.parseDouble(navTempField.getText().trim());
        int steps = (int) stepsSpinner.getValue();
        long seed = Long.parseLong(seedField.getText().trim());
        boolean analyze = analyzeCheckBox.isSelected();
        boolean noWalk = noWalkCheckBox.isSelected();

        String savePath = savePathField.getText().trim();
        String loadPath = loadPathField.getText().trim();

        RhythmDistance dist = RhythmDistance.parse(distName);
        RhythmNetwork network;

        if (!loadPath.isEmpty()) {
          long tLoad = System.nanoTime();
          log("Loading network from " + loadPath + "...");
          network = RhythmNetwork.load(Path.of(loadPath));
          log(String.format("Loaded: %,d vertices, %,d edges (%.0f ms)",
              network.vertexCount(), network.edgeCount(), (System.nanoTime() - tLoad) / 1e6));
        } else {
          log("Building rhythm network...");
          log(String.format("  Parameters: L=%d, cipher=%s, T=%.2f", length, cipher, temp));
          log(String.format("  Vertex clause: \"%s\"", vertexClause));
          log(String.format("  Juxt clause:   \"%s\"", juxtClause));
          log(String.format("  Distance:      %s", distName));

          network = new RhythmNetwork.Builder()
              .length(length)
              .cipher(cipher)
              .vertexClause(vertexClause)
              .juxtapositionClause(juxtClause)
              .distance(dist)
              .temperature(temp)
              .selfLoops(selfLoops)
              .build();

          log(String.format("Network ready: %,d vertices, %,d edges",
              network.vertexCount(), network.edgeCount()));
        }

        if (!savePath.isEmpty()) {
          long tSave = System.nanoTime();
          network.save(Path.of(savePath));
          log(String.format("Network saved to %s (%.0f ms)", savePath, (System.nanoTime() - tSave) / 1e6));
        }

        if (network.vertexCount() == 0) {
          log("WARNING: No vertices passed the filter — nothing to do.");
          return;
        }

        if (network.edgeCount() == 0) {
          log("WARNING: No edges in the network — walk impossible.");
        }

        if (analyze) {
          log("Running analysis (τ=" + navTemp + ")...");
          RhythmNetworkAnalysis analysis = new RhythmNetworkAnalysis(network, navTemp);
          log(analysis.summary());
        }

        if (!noWalk && network.edgeCount() > 0) {
          log(String.format("Generating walk: %,d steps, τ=%.2f", steps, navTemp));
          long tWalk = System.nanoTime();

          RhythmNetworkNavigator nav = seed < 0
              ? new RhythmNetworkNavigator(network)
              : new RhythmNetworkNavigator(network, seed);
          nav.setNavigationTemperature(navTemp);

          List<FixedLength.Natural> walk = nav.walk(steps);
          log("# walk (τ=" + navTemp + ", T=" + temp + ")");
          String walkStr = walk.stream()
              .map(FixedLength.Natural::toString)
              .collect(Collectors.joining(" "));
          log(walkStr);

          log(String.format("Walk generated in %.0f ms", (System.nanoTime() - tWalk) / 1e6));
        }
      } catch (NumberFormatException e) {
          log("ERROR: Invalid number format for one of the fields.");
      } catch (Exception e) {
        log("ERROR: " + e.getMessage());
        for (StackTraceElement ste : e.getStackTrace()) {
          log("  " + ste.toString());
        }
      } finally {
        long totalMs = (long) ((System.nanoTime() - t0) / 1e6);
        log(String.format("Total elapsed: %,d ms", totalMs));
      }
    }).start();
  }
}
