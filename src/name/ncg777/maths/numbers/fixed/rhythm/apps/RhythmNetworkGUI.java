package name.ncg777.maths.numbers.fixed.rhythm.apps;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import name.ncg777.maths.Matrix;
import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.numbers.fixed.FixedLength;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmClauseParser;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmDistance;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmNetwork;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmNetworkAnalysis;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmNetworkNavigator;
import name.ncg777.maths.numbers.fixed.rhythm.RhythmPredicateRegistry;
import name.ncg777.maths.numbers.relations.PredicatedDifferences;
import name.ncg777.maths.numbers.relations.PredicatedJuxtaposition;

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
  private JSpinner walkCountSpinner;
  private JTextField successiveClauseField;
  private JTextField simultaneousClauseField;
  private JSpinner maxAttemptsSpinner;

  private JTextField networkNameField;
  private JList<String> networkList;
  private DefaultListModel<String> networkListModel;

  private JTextField savePathField;
  private JTextField loadPathField;
  
  private JTextArea outputArea;
  private JButton runButton;
  private JButton analyzeButton;
  private JButton buildAddButton;
  private JButton loadAddButton;
  private JButton saveSelectedButton;
  private JButton unloadSelectedButton;
  private JButton unloadAllButton;

  private final Map<String, RhythmNetwork> loadedNetworks = new LinkedHashMap<>();
  private final AtomicInteger networkCounter = new AtomicInteger(1);

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
    walkPanel.add(new JLabel("Walks N:"), gbc);
    gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.5;
    walkCountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
    walkPanel.add(walkCountSpinner, gbc);
    gbc.weightx = 0.0;

    gbc.gridx = 2; gbc.gridy = row;
    walkPanel.add(new JLabel("Max Attempts:"), gbc);
    gbc.gridx = 3; gbc.gridy = row; gbc.weightx = 0.5;
    maxAttemptsSpinner = new JSpinner(new SpinnerNumberModel(2000, 10, 5000000, 10));
    walkPanel.add(maxAttemptsSpinner, gbc);
    gbc.weightx = 0.0;

    row++;

    gbc.gridx = 0; gbc.gridy = row;
    walkPanel.add(new JLabel("Successive Clause:"), gbc);
    gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 3; gbc.weightx = 1.0;
    successiveClauseField = new JTextField("");
    successiveClauseField.setToolTipText("Predicate on each adjacent pair in a walk (via juxtaposition)");
    walkPanel.add(successiveClauseField, gbc);
    gbc.gridwidth = 1; gbc.weightx = 0.0;

    row++;

    gbc.gridx = 0; gbc.gridy = row;
    walkPanel.add(new JLabel("Simultaneous Clause:"), gbc);
    gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 3; gbc.weightx = 1.0;
    simultaneousClauseField = new JTextField("");
    simultaneousClauseField.setToolTipText("Predicate on pairwise differences between rows at each column");
    walkPanel.add(simultaneousClauseField, gbc);
    gbc.gridwidth = 1; gbc.weightx = 0.0;

    row++;

    gbc.gridx = 0; gbc.gridy = row;
    walkPanel.add(new JLabel("Seed (-1 random):"), gbc);
    gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.5;
    seedField = new JTextField("-1");
    seedField.setToolTipText("If >= 0, sets random seed for generation");
    walkPanel.add(seedField, gbc);
    gbc.weightx = 0.0;

    topPanel.add(walkPanel, ogbc);
    ogbc.gridy++;

    // --- 3. File Settings ---
    JPanel filePanel = new JPanel(new GridBagLayout());
    filePanel.setBorder(BorderFactory.createTitledBorder("Network Management"));
    
    row = 0;
    gbc.gridx = 0; gbc.gridy = row;
    filePanel.add(new JLabel("Name:"), gbc);
    gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.5;
    networkNameField = new JTextField("");
    networkNameField.setToolTipText("Optional label for loaded/generated network");
    filePanel.add(networkNameField, gbc);
    gbc.weightx = 0.0;
    gbc.gridx = 2; gbc.gridy = row;
    buildAddButton = new JButton("Build");
    buildAddButton.addActionListener(e -> buildAndAddNetwork());
    filePanel.add(buildAddButton, gbc);
    gbc.gridx = 3; gbc.gridy = row;
    loadAddButton = new JButton("Load");
    loadAddButton.addActionListener(e -> loadAndAddNetwork());
    filePanel.add(loadAddButton, gbc);

    row++;

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

    row++;

    gbc.gridx = 1; gbc.gridy = row;
    saveSelectedButton = new JButton("Save");
    saveSelectedButton.addActionListener(e -> saveSelectedNetwork());
    filePanel.add(saveSelectedButton, gbc);
    gbc.gridx = 2; gbc.gridy = row;
    unloadSelectedButton = new JButton("Unload");
    unloadSelectedButton.addActionListener(e -> unloadSelectedNetwork());
    filePanel.add(unloadSelectedButton, gbc);
    gbc.gridx = 3; gbc.gridy = row;
    unloadAllButton = new JButton("Unload All");
    unloadAllButton.addActionListener(e -> unloadAllNetworks());
    filePanel.add(unloadAllButton, gbc);

    topPanel.add(filePanel, ogbc);
    ogbc.gridy++;

    JPanel loadedPanel = new JPanel(new BorderLayout(5, 5));
    loadedPanel.setBorder(BorderFactory.createTitledBorder("Loaded Networks"));
    networkListModel = new DefaultListModel<>();
    networkList = new JList<>(networkListModel);
    JScrollPane loadedScroll = new JScrollPane(networkList);
    loadedScroll.setPreferredSize(new Dimension(250, 100));
    loadedPanel.add(loadedScroll, BorderLayout.CENTER);
    topPanel.add(loadedPanel, ogbc);
    ogbc.gridy++;

    // --- Action Button ---
    analyzeButton = new JButton("Analyze");
    analyzeButton.setFont(new Font("SansSerif", Font.BOLD, 14));
    analyzeButton.addActionListener(e -> analyzeSelectedNetwork());

    runButton = new JButton("Walk / Matrix");
    runButton.setFont(new Font("SansSerif", Font.BOLD, 14));
    runButton.addActionListener(e -> generateWalks());

    JButton clearButton = new JButton("Clear Output");
    clearButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
    clearButton.addActionListener(e -> outputArea.setText(""));

    JButton saveOutputButton = new JButton("Save Output");
    saveOutputButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
    saveOutputButton.addActionListener(e -> saveOutputToFile());

    JPanel btnPanel = new JPanel();
    btnPanel.add(analyzeButton);
    btnPanel.add(runButton);
    btnPanel.add(clearButton);
    btnPanel.add(saveOutputButton);
    topPanel.add(btnPanel, ogbc);

    frame.getContentPane().add(topPanel, BorderLayout.NORTH);

    // --- Output Area ---
    outputArea = new JTextArea();
    outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    outputArea.setEditable(false);
    
    // Enable word wrap so standard logs are readable without horizontal scrolling,
    // but leave horizontal scrolling available for wide matrix rows if needed.
    // However, given matrix outputs, often turning OFF line wrap is better so rows stay intact.
    outputArea.setLineWrap(false);
    outputArea.setWrapStyleWord(false);

    JScrollPane scrollPane = new JScrollPane(outputArea);
    // Explicitly add a horizontal scrollbar policy for large generated matrices
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setBorder(BorderFactory.createTitledBorder("Output (Walks and Matrices appear here)"));
    scrollPane.setPreferredSize(new Dimension(800, 300));
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

  private void logBlock(String text) {
    SwingUtilities.invokeLater(() -> {
      outputArea.append(text);
      if (!text.endsWith("\n")) {
        outputArea.append("\n");
      }
      outputArea.setCaretPosition(outputArea.getDocument().getLength());
    });
  }

  private void setBusy(boolean busy) {
    SwingUtilities.invokeLater(() -> {
      runButton.setEnabled(!busy);
      analyzeButton.setEnabled(!busy);
      buildAddButton.setEnabled(!busy);
      loadAddButton.setEnabled(!busy);
      saveSelectedButton.setEnabled(!busy);
      unloadSelectedButton.setEnabled(!busy);
      unloadAllButton.setEnabled(!busy);
    });
  }

  private String normalizedNetworkName(String preferred) {
    String base = preferred == null ? "" : preferred.trim();
    if (base.isEmpty()) {
      base = "network-" + networkCounter.getAndIncrement();
    }
    String name = base;
    int suffix = 2;
    while (loadedNetworks.containsKey(name)) {
      name = base + "-" + suffix;
      suffix++;
    }
    return name;
  }

  private String selectedNetworkName() {
    return networkList.getSelectedValue();
  }

  private RhythmNetwork selectedNetwork() {
    String name = selectedNetworkName();
    if (name == null) {
      return null;
    }
    return loadedNetworks.get(name);
  }

  private void addNetwork(String preferredName, RhythmNetwork network, String origin) {
    String name = normalizedNetworkName(preferredName);
    loadedNetworks.put(name, network);
    SwingUtilities.invokeLater(() -> {
      networkListModel.addElement(name);
      networkList.setSelectedValue(name, true);
    });
    log(String.format("Added network '%s' from %s: %,d vertices, %,d edges",
        name, origin, network.vertexCount(), network.edgeCount()));
  }

  private void unloadSelectedNetwork() {
    String name = selectedNetworkName();
    if (name == null) {
      log("No selected network to unload.");
      return;
    }
    loadedNetworks.remove(name);
    networkListModel.removeElement(name);
    if (!networkListModel.isEmpty()) {
      networkList.setSelectedIndex(Math.max(0, networkListModel.size() - 1));
    }
    log("Unloaded network '" + name + "'.");
  }

  private void unloadAllNetworks() {
    loadedNetworks.clear();
    networkListModel.clear();
    log("Unloaded all networks.");
  }

  private void buildAndAddNetwork() {
    outputArea.setText("");
    setBusy(true);
    log("Building network and adding to memory...");
    new Thread(() -> {
      long t0 = System.nanoTime();
      try {
        int length = (int) lengthSpinner.getValue();
        Cipher.Name cipher = (Cipher.Name) cipherComboBox.getSelectedItem();
        String vertexClause = vertexClauseField.getText().trim();
        String juxtClause = juxtClauseField.getText().trim();
        String distName = (String) distanceComboBox.getSelectedItem();
        double temp = Double.parseDouble(tempField.getText().trim());
        boolean selfLoops = selfLoopsCheckBox.isSelected();

        RhythmDistance dist = RhythmDistance.parse(distName);
        log("Building rhythm network...");
        log(String.format("  Parameters: L=%d, cipher=%s, T=%.2f", length, cipher, temp));
        log(String.format("  Vertex clause: \"%s\"", vertexClause));
        log(String.format("  Juxt clause:   \"%s\"", juxtClause));
        log(String.format("  Distance:      %s", distName));

        RhythmNetwork network = new RhythmNetwork.Builder()
            .length(length)
            .cipher(cipher)
            .vertexClause(vertexClause)
            .juxtapositionClause(juxtClause)
            .distance(dist)
            .temperature(temp)
            .selfLoops(selfLoops)
            .build();

        addNetwork(networkNameField.getText(), network, "build");
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
        setBusy(false);
      }
    }).start();
  }

  private void loadAndAddNetwork() {
    outputArea.setText("");
    String loadPath = loadPathField.getText().trim();
    if (loadPath.isEmpty()) {
      log("Load path is empty.");
      return;
    }
    setBusy(true);
    new Thread(() -> {
      long t0 = System.nanoTime();
      try {
        log("Loading network from " + loadPath + "...");
        RhythmNetwork network = RhythmNetwork.load(Path.of(loadPath));
        addNetwork(networkNameField.getText(), network, "load");
      } catch (Exception e) {
        log("ERROR: " + e.getMessage());
      } finally {
        long totalMs = (long) ((System.nanoTime() - t0) / 1e6);
        log(String.format("Total elapsed: %,d ms", totalMs));
        setBusy(false);
      }
    }).start();
  }

  private void saveSelectedNetwork() {
    String savePath = savePathField.getText().trim();
    if (savePath.isEmpty()) {
      log("Save path is empty.");
      return;
    }
    RhythmNetwork network = selectedNetwork();
    String name = selectedNetworkName();
    if (network == null || name == null) {
      log("No selected network to save.");
      return;
    }

    setBusy(true);
    new Thread(() -> {
      long t0 = System.nanoTime();
      try {
        network.save(Path.of(savePath));
        log(String.format("Saved '%s' to %s", name, savePath));
      } catch (Exception e) {
        log("ERROR: " + e.getMessage());
      } finally {
        long totalMs = (long) ((System.nanoTime() - t0) / 1e6);
        log(String.format("Total elapsed: %,d ms", totalMs));
        setBusy(false);
      }
    }).start();
  }

  private void saveOutputToFile() {
    JFileChooser chooser = new JFileChooser(".");
    if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      try {
        Files.writeString(file.toPath(), outputArea.getText());
        log("Saved output to: " + file.getAbsolutePath());
      } catch (IOException ex) {
        log("ERROR saving output: " + ex.getMessage());
      }
    }
  }

  private void analyzeSelectedNetwork() {
    outputArea.setText("");
    setBusy(true);
    new Thread(() -> {
      long t0 = System.nanoTime();
      try {
        RhythmNetwork network = selectedNetwork();
        String selectedName = selectedNetworkName();
        if (network == null || selectedName == null) {
          log("No selected network. Build/load and select one first.");
          return;
        }
        double navTemp = Double.parseDouble(navTempField.getText().trim());
        log("Running analysis (τ=" + navTemp + ") on '" + selectedName + "'...");
        RhythmNetworkAnalysis analysis = new RhythmNetworkAnalysis(network, navTemp);
        log(analysis.summary());
      } catch (Exception e) {
        log("ERROR: " + e.getMessage());
      } finally {
        long totalMs = (long) ((System.nanoTime() - t0) / 1e6);
        log(String.format("Total elapsed: %,d ms", totalMs));
        setBusy(false);
      }
    }).start();
  }

  private boolean passesSuccessive(
      List<FixedLength.Natural> walk,
      BiPredicate<BinaryNatural, BinaryNatural> relation) {
    for (int i = 1; i < walk.size(); i++) {
      if (!relation.test(walk.get(i - 1).toBinaryNatural(), walk.get(i).toBinaryNatural())) {
        return false;
      }
    }
    return true;
  }

  private boolean passesSimultaneous(
      List<FixedLength.Natural> candidate,
      List<List<FixedLength.Natural>> accepted,
      BiPredicate<BinaryNatural, BinaryNatural> relation) {
    for (List<FixedLength.Natural> existing : accepted) {
      for (int j = 0; j < candidate.size(); j++) {
        if (!relation.test(candidate.get(j).toBinaryNatural(), existing.get(j).toBinaryNatural())) {
          return false;
        }
      }
    }
    return true;
  }

  private void generateWalks() {
    outputArea.setText("");
    setBusy(true);
    new Thread(() -> {
      long t0 = System.nanoTime();
      try {
        RhythmNetwork network = selectedNetwork();
        String selectedName = selectedNetworkName();
        if (network == null || selectedName == null) {
          log("No selected network. Build/load and select one first.");
          return;
        }

        double navTemp = Double.parseDouble(navTempField.getText().trim());
        int steps = (int) stepsSpinner.getValue();
        int walkCount = (int) walkCountSpinner.getValue();
        int maxAttempts = (int) maxAttemptsSpinner.getValue();
        long seed = Long.parseLong(seedField.getText().trim());
        String successiveClause = successiveClauseField.getText().trim();
        String simultaneousClause = simultaneousClauseField.getText().trim();

        log(String.format("Using network '%s': %,d vertices, %,d edges",
            selectedName, network.vertexCount(), network.edgeCount()));

        if (network.vertexCount() == 0) {
          log("WARNING: Selected network has no vertices — nothing to do.");
          return;
        }
        if (network.edgeCount() == 0) {
          log("WARNING: Selected network has no edges — walk impossible.");
          return;
        }

        RhythmNetworkNavigator nav = seed < 0
            ? new RhythmNetworkNavigator(network)
            : new RhythmNetworkNavigator(network, seed);
        nav.setNavigationTemperature(navTemp);

        Predicate<BinaryNatural> successivePred = RhythmClauseParser.parse(successiveClause);
        Predicate<BinaryNatural> simultaneousPred = RhythmClauseParser.parse(simultaneousClause);

        BiPredicate<BinaryNatural, BinaryNatural> successiveRelation =
            new PredicatedJuxtaposition(successivePred);
        BiPredicate<BinaryNatural, BinaryNatural> simultaneousRelation =
            new PredicatedDifferences(simultaneousPred);

        if (walkCount <= 1 && successiveClause.isEmpty() && simultaneousClause.isEmpty()) {
          log(String.format("Generating walk: %,d steps, τ=%.2f", steps, navTemp));
          long tWalk = System.nanoTime();
          List<FixedLength.Natural> walk = nav.walk(steps);
          log("# walk (τ=" + navTemp + ", T=" + network.getTemperature() + ")");
          log(walk.stream().map(FixedLength.Natural::toString).collect(Collectors.joining(" ")));
          log(String.format("Walk generated in %.0f ms", (System.nanoTime() - tWalk) / 1e6));
          return;
        }

        log(String.format("Generating matrix: %,d walk(s), %,d steps, τ=%.2f",
            walkCount, steps, navTemp));
        log(String.format("  Successive clause: \"%s\"", successiveClause));
        log(String.format("  Simultaneous clause: \"%s\"", simultaneousClause));
        log(String.format("  Max attempts: %,d", maxAttempts));

        long tWalks = System.nanoTime();
        List<List<FixedLength.Natural>> accepted = new ArrayList<>();
        int attempts = 0;

        while (accepted.size() < walkCount && attempts < maxAttempts) {
          attempts++;
          List<FixedLength.Natural> candidate = nav.walk(steps);
          if (!passesSuccessive(candidate, successiveRelation)) {
            continue;
          }
          if (!passesSimultaneous(candidate, accepted, simultaneousRelation)) {
            continue;
          }
          accepted.add(candidate);
          if (accepted.size() % 10 == 0 || accepted.size() == walkCount) {
            log(String.format("  accepted %,d / %,d (attempts %,d)",
                accepted.size(), walkCount, attempts));
          }
        }

        if (accepted.isEmpty()) {
          log("No walk matched constraints.");
          return;
        }

        if (accepted.size() < walkCount) {
          log(String.format("WARNING: Only %,d / %,d walks accepted within %,d attempts.",
              accepted.size(), walkCount, attempts));
        }

        Matrix<FixedLength.Natural> matrix = new Matrix<>(accepted.size(), steps);
        for (int i = 0; i < accepted.size(); i++) {
          List<FixedLength.Natural> row = accepted.get(i);
          for (int j = 0; j < steps; j++) {
            matrix.set(i, j, row.get(j));
          }
        }

        log(String.format("# walk matrix rows=%d cols=%d", matrix.rowCount(), matrix.columnCount()));
        logBlock(matrix.toString());
        log(String.format("Walk matrix generated in %.0f ms", (System.nanoTime() - tWalks) / 1e6));
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
        setBusy(false);
      }
    }).start();
  }
}
