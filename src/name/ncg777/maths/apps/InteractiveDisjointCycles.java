package name.ncg777.maths.apps;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.awt.geom.QuadCurve2D;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import name.ncg777.computing.structures.CollectionUtils;

public class InteractiveDisjointCycles {
  private static final Color BACKGROUND = new Color(50, 50, 50);
  private static final Color PANEL = new Color(100, 100, 100);
  private static final Color ACCENT = new Color(120, 210, 255);
  private static final Color HOT = new Color(255, 113, 181);
  private static final Color TEXT = new Color(0, 0, 0);
  private static final Color MUTED = new Color(75, 75, 100);
  private static final Color[] CYCLE_COLORS = {
      new Color(255, 113, 181), new Color(92, 225, 230), new Color(255, 211, 105),
      new Color(157, 122, 255), new Color(121, 255, 177), new Color(255, 151, 87)
  };

  private JFrame frame;
  private CycleCanvas canvas;
  private JSpinner sizeSpinner;
  private JTextField tokensField;
  private JTextArea permutationArea;
  private JTextArea tokensArea;
  private JTextArea orbitArea;
  private JLabel statusLabel;
  private JLabel orderLabel;
  private DefaultListModel<String> cycleListModel;

  private final List<List<Integer>> cycles = new ArrayList<>();
  private final List<Integer> currentCycle = new ArrayList<>();
  private final List<String> gapFillers = new ArrayList<>();
  private int objectCount = 6;
  private Integer[] permutation = identity(objectCount);

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          InteractiveDisjointCycles window = new InteractiveDisjointCycles();
          window.frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public InteractiveDisjointCycles() {
    initialize();
    ensureGapFillerSize();
    refreshAll("Click nodes to build a cycle, then press Add cycle.");
  }

  private void initialize() {
    frame = new JFrame("Disjoint Cycle Permutation Lab");
    frame.setBounds(100, 100, 1060, 720);
    frame.setMinimumSize(new Dimension(920, 620));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel root = new JPanel(new BorderLayout(12, 12));
    root.setBackground(BACKGROUND);
    root.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
    frame.setContentPane(root);

    JLabel title = new JLabel("Disjoint Cycle Permutation Lab", SwingConstants.CENTER);
    title.setForeground(TEXT);
    title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
    title.setBorder(BorderFactory.createEmptyBorder(8, 0, 12, 0));
    root.add(title, BorderLayout.NORTH);

    canvas = new CycleCanvas();
    canvas.setPreferredSize(new Dimension(560, 560));
    root.add(wrapPanel(canvas, "Railway diagram"), BorderLayout.CENTER);

    JPanel controls = new JPanel(new GridBagLayout());
    controls.setOpaque(false);
    root.add(controls, BorderLayout.EAST);

    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(4, 4, 4, 4);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
    c.gridx = 0;
    c.gridy = 0;

    JPanel inputPanel = styledPanel(new GridBagLayout());
    controls.add(inputPanel, c);
    addInputControls(inputPanel);

    c.gridy++;
    JPanel cyclePanel = styledPanel(new BorderLayout(6, 6));
    controls.add(cyclePanel, c);
    addCycleControls(cyclePanel);

    c.gridy++;
    c.fill = GridBagConstraints.BOTH;
    c.weighty = 1.0;
    JPanel outputPanel = styledPanel(new GridBagLayout());
    controls.add(outputPanel, c);
    addOutputControls(outputPanel);

    statusLabel = new JLabel(" ");
    statusLabel.setForeground(MUTED);
    statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 4, 0, 4));
    root.add(statusLabel, BorderLayout.SOUTH);
  }

  private void addInputControls(JPanel panel) {
    GridBagConstraints c = constraints();

    JLabel nLabel = label("Objects:");
    panel.add(nLabel, c);

    sizeSpinner = new JSpinner(new SpinnerNumberModel(objectCount, 1, 128, 1));
    sizeSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        setObjectCount((Integer) sizeSpinner.getValue());
      }
    });
    c.gridx = 1;
    panel.add(sizeSpinner, c);

    c.gridx = 0;
    c.gridy++;
    c.gridwidth = 2;
    panel.add(label("Tokens (space separated, optional):"), c);

    c.gridy++;
    tokensField = new JTextField("zero one two three four five");
    panel.add(tokensField, c);

    c.gridy++;
    JButton useTokens = button("Use token count");
    useTokens.addActionListener(e -> {
      List<String> tokens = rawTokens();
      if (!tokens.isEmpty()) {
        sizeSpinner.setValue(tokens.size());
      }
    });
    panel.add(useTokens, c);
  }

  private void addCycleControls(JPanel panel) {
    JPanel buttons = new JPanel(new GridBagLayout());
    buttons.setOpaque(false);
    panel.add(buttons, BorderLayout.NORTH);

    GridBagConstraints c = constraints();
    JButton addCycle = button("Add cycle");
    addCycle.addActionListener(e -> commitCurrentCycle());
    buttons.add(addCycle, c);

    c.gridx = 1;
    JButton undo = button("Undo node");
    undo.addActionListener(e -> {
      if (!currentCycle.isEmpty()) {
        currentCycle.remove(currentCycle.size() - 1);
        refreshAll("Removed the last node from the draft cycle.");
      }
    });
    buttons.add(undo, c);

    c.gridx = 0;
    c.gridy++;
    JButton clearDraft = button("Clear draft");
    clearDraft.addActionListener(e -> {
      currentCycle.clear();
      refreshAll("Draft cycle cleared.");
    });
    buttons.add(clearDraft, c);

    c.gridx = 1;
    JButton reset = button("Reset all");
    reset.addActionListener(e -> {
      cycles.clear();
      currentCycle.clear();
      refreshAll("All cycles cleared.");
    });
    buttons.add(reset, c);

    cycleListModel = new DefaultListModel<>();
    JList<String> cycleList = new JList<>(cycleListModel);
    cycleList.setBackground(new Color(124, 129, 149));
    cycleList.setForeground(TEXT);
    cycleList.setSelectionBackground(HOT);
    cycleList.setVisibleRowCount(5);
    panel.add(new JScrollPane(cycleList), BorderLayout.CENTER);

    JPanel bottom = new JPanel(new BorderLayout(4, 4));
    bottom.setOpaque(false);
    JButton removeSelected = button("Remove selected cycle");
    removeSelected.addActionListener(e -> {
      int selected = cycleList.getSelectedIndex();
      if (selected >= 0 && selected < cycles.size()) {
        cycles.remove(selected);
        refreshAll("Removed selected cycle.");
      }
    });
    bottom.add(removeSelected, BorderLayout.NORTH);

    orderLabel = label("Order: 1");
    bottom.add(orderLabel, BorderLayout.SOUTH);
    panel.add(bottom, BorderLayout.SOUTH);
  }

  private void addOutputControls(JPanel panel) {
    GridBagConstraints c = constraints();
    c.gridwidth = 2;
    panel.add(label("Permutation list"), c);

    c.gridy++;
    permutationArea = outputArea(2);
    panel.add(new JScrollPane(permutationArea), c);

    c.gridy++;
    JButton copyPermutation = button("Copy permutation");
    copyPermutation.addActionListener(e -> copy(permutationArea.getText()));
    panel.add(copyPermutation, c);

    c.gridy++;
    panel.add(label("Permuted tokens"), c);

    c.gridy++;
    tokensArea = outputArea(2);
    panel.add(new JScrollPane(tokensArea), c);

    c.gridy++;
    JButton copyTokens = button("Copy permuted tokens");
    copyTokens.addActionListener(e -> copy(tokensArea.getText()));
    panel.add(copyTokens, c);

    c.gridy++;
    panel.add(label("Full orbit (one sequence per line)"), c);

    c.gridy++;
    c.fill = GridBagConstraints.BOTH;
    c.weighty = 1.0;
    orbitArea = outputArea(8);
    panel.add(new JScrollPane(orbitArea), c);

    c.gridy++;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weighty = 0.0;
    JButton copyOrbit = button("Copy full orbit");
    copyOrbit.addActionListener(e -> copy(orbitArea.getText()));
    panel.add(copyOrbit, c);
  }

  private void setObjectCount(int n) {
    objectCount = n;
    ensureGapFillerSize();
    cycles.clear();
    currentCycle.clear();
    refreshAll("Object count changed; cycles reset.");
  }

  private void addNodeToCurrentCycle(int node) {
    if (isCommitted(node)) {
      refreshAll("Node " + node + " already belongs to a committed cycle.");
      return;
    }
    if (currentCycle.contains(node)) {
      refreshAll("Node " + node + " is already in the draft cycle.");
      return;
    }
    currentCycle.add(node);
    refreshAll("Draft cycle: " + formatCycle(currentCycle));
  }

  private void commitCurrentCycle() {
    if (currentCycle.isEmpty()) {
      refreshAll("Click one or more nodes before adding a cycle.");
      return;
    }
    cycles.add(new ArrayList<>(currentCycle));
    currentCycle.clear();
    refreshAll("Cycle added.");
  }

  private void refreshAll(String status) {
    updatePermutation();
    updateCycleList();
    updateOutputs();
    canvas.repaint();
    if (statusLabel != null) {
      statusLabel.setText(status);
    }
  }

  private void updatePermutation() {
    TreeSet<ArrayList<Integer>> cycleSet = new TreeSet<>(new Comparator<ArrayList<Integer>>() {
      public int compare(ArrayList<Integer> a, ArrayList<Integer> b) {
        int n = Math.min(a.size(), b.size());
        for (int i = 0; i < n; i++) {
          int cmp = Integer.compare(a.get(i), b.get(i));
          if (cmp != 0) {
            return cmp;
          }
        }
        return Integer.compare(a.size(), b.size());
      }
    });

    Set<Integer> seen = new HashSet<>();
    for (List<Integer> cycle : cycles) {
      if (!cycle.isEmpty()) {
        cycleSet.add(new ArrayList<>(cycle));
        seen.addAll(cycle);
      }
    }
    for (int i = 0; i < objectCount; i++) {
      if (!seen.contains(i)) {
        cycleSet.add(new ArrayList<>(List.of(i)));
      }
    }

    permutation = CollectionUtils.getPermutationFromDisjointCycles(cycleSet);
  }

  private void updateCycleList() {
    if (cycleListModel == null) {
      return;
    }
    cycleListModel.clear();
    for (List<Integer> cycle : cycles) {
      cycleListModel.addElement(formatCycle(cycle));
    }
    if (!currentCycle.isEmpty()) {
      cycleListModel.addElement("draft " + formatCycle(currentCycle));
    }
  }

  private void updateOutputs() {
    if (permutationArea == null) {
      return;
    }
    ensureGapFillerSize();
    List<Integer> p = Arrays.asList(permutation);
    List<String> tokens = tokensForObjects();
    List<String> permuted = CollectionUtils.permutate(p, tokens);
    List<String> rendered = insertGapFillersForOrbit(permuted);
    long order = CollectionUtils.getPermutationOrder(permutation);

    permutationArea.setText(joinIntegers(p));
    tokensArea.setText(String.join(" ", rendered));
    orderLabel.setText("Order: " + order);

    StringBuilder orbit = new StringBuilder();
    List<String> current = new ArrayList<>(tokens);
    for (long i = 0; i < order; i++) {
      orbit.append(i).append(": ").append(String.join(" ", insertGapFillersForOrbit(current))).append("\n");
      current = CollectionUtils.permutate(p, current);
    }
    orbitArea.setText(orbit.toString());
  }

  private void ensureGapFillerSize() {
    int target = Math.max(0, objectCount + 1);
    while (gapFillers.size() < target) {
      gapFillers.add("");
    }
    while (gapFillers.size() > target) {
      gapFillers.remove(gapFillers.size() - 1);
    }
  }

  private List<String> insertGapFillers(List<String> tokens) {
    if (tokens.isEmpty()) {
      return new ArrayList<>(tokens);
    }
    ArrayList<String> rendered = new ArrayList<>(tokens.size() * 2 + 1);
    String pre = !gapFillers.isEmpty() ? gapFillers.get(0) : "";
    if (!pre.isBlank()) {
      rendered.add(pre);
    }
    for (int i = 0; i < tokens.size(); i++) {
      rendered.add(tokens.get(i));
      String filler = (i + 1) < gapFillers.size() ? gapFillers.get(i + 1) : "";
      if (!filler.isBlank()) {
        rendered.add(filler);
      }
    }
    return rendered;
  }

  private List<String> insertGapFillersForOrbit(List<String> tokens) {
    if (tokens.isEmpty()) {
      return new ArrayList<>(tokens);
    }
    ArrayList<String> rendered = new ArrayList<>(tokens.size() * 2 + 1);
    String pre = !gapFillers.isEmpty() ? gapFillers.get(0) : "";
    if (!pre.isBlank()) {
      rendered.add(pre);
    }
    for (int i = 0; i < tokens.size(); i++) {
      rendered.add(tokens.get(i));
      String filler = (i + 1) < gapFillers.size() ? gapFillers.get(i + 1) : "";
      if (!filler.isBlank()) {
        rendered.add(filler);
      }
    }
    return rendered;
  }

  private List<String> tokensForObjects() {
    List<String> tokens = rawTokens();
    ArrayList<String> normalized = new ArrayList<>();
    for (int i = 0; i < objectCount; i++) {
      normalized.add(i < tokens.size() ? tokens.get(i) : String.valueOf(i));
    }
    return normalized;
  }

  private List<String> rawTokens() {
    if (tokensField == null || tokensField.getText().trim().isEmpty()) {
      return new ArrayList<>();
    }
    return Arrays.stream(tokensField.getText().trim().split("\\s+"))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private boolean isCommitted(int node) {
    for (List<Integer> cycle : cycles) {
      if (cycle.contains(node)) {
        return true;
      }
    }
    return false;
  }

  private static Integer[] identity(int n) {
    Integer[] o = new Integer[n];
    for (int i = 0; i < n; i++) {
      o[i] = i;
    }
    return o;
  }

  private static String joinIntegers(List<Integer> values) {
    return values.stream().map(String::valueOf).collect(Collectors.joining(" "));
  }

  private static String formatCycle(List<Integer> cycle) {
    return "(" + joinIntegers(cycle) + ")";
  }

  private static void copy(String text) {
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
  }

  private static JPanel styledPanel(java.awt.LayoutManager layout) {
    JPanel panel = new JPanel(layout);
    panel.setBackground(PANEL);
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(72, 84, 126)),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    return panel;
  }

  private static JPanel wrapPanel(JPanel content, String title) {
    JPanel wrapper = styledPanel(new BorderLayout(6, 6));
    JLabel label = label(title);
    label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
    wrapper.add(label, BorderLayout.NORTH);
    wrapper.add(content, BorderLayout.CENTER);
    return wrapper;
  }

  private static GridBagConstraints constraints() {
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.insets = new Insets(4, 4, 4, 4);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
    return c;
  }

  private static JLabel label(String text) {
    JLabel label = new JLabel(text);
    label.setForeground(TEXT);
    return label;
  }

  private static JButton button(String text) {
    JButton button = new JButton(text);
    button.setFocusPainted(false);
    button.setBackground(new Color(49, 63, 105));
    button.setForeground(TEXT);
    button.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(ACCENT),
        BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    return button;
  }

  private static JTextArea outputArea(int rows) {
    JTextArea area = new JTextArea(rows, 28);
    area.setEditable(false);
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setBackground(new Color(117, 122, 108));
    area.setForeground(TEXT);
    area.setCaretColor(TEXT);
    return area;
  }

  private class CycleCanvas extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final int NODE_RADIUS = 22;
    private static final int ARC_BASE_OFFSET = 55;
    private static final int ARC_LANE_HEIGHT = 50;
    private final ArrayList<Point> nodeLocations = new ArrayList<>();
    private final ArrayList<Point> fillerLocations = new ArrayList<>();
    private final ArrayList<Rectangle> fillerBounds = new ArrayList<>();

    CycleCanvas() {
      setBackground(new Color(16, 20, 55));
      addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          int node = nodeAt(e.getPoint());
          if (node >= 0) {
            addNodeToCurrentCycle(node);
            return;
          }
          int fillerIndex = fillerAt(e.getPoint());
          if (fillerIndex >= 0) {
            editFillerAt(fillerIndex);
          }
        }
      });
    }

    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      updateNodeLocations();
      drawTrack(g2);
      for (int i = 0; i < cycles.size(); i++) {
        drawRailwayArcs(g2, cycles.get(i), CYCLE_COLORS[i % CYCLE_COLORS.length], true, i);
      }
      drawRailwayArcs(g2, currentCycle, ACCENT, false, cycles.size());
      drawFillerLabels(g2);
      drawNodes(g2);
      g2.dispose();
    }

    private void updateNodeLocations() {
      nodeLocations.clear();
      fillerLocations.clear();
      if (objectCount == 0) return;
      int w = getWidth();
      int h = getHeight();
      int nodeY = h - 90;
      int fillerY = h - 48;
      int marginX = 60;
      int nodeSpacing = objectCount > 1 ? (w - 2 * marginX) / (objectCount - 1) : 0;
      for (int i = 0; i < objectCount; i++) {
        int x = objectCount == 1 ? w / 2 : marginX + i * nodeSpacing;
        nodeLocations.add(new Point(x, nodeY));
      }
      // n+1 filler positions: before first node, between each pair, after last node
      int edgeOffset = objectCount > 1 ? nodeSpacing / 2 : 50;
      fillerLocations.add(new Point(
          Math.max(10, nodeLocations.get(0).x - edgeOffset), fillerY));
      for (int i = 1; i < objectCount; i++) {
        int mx = (nodeLocations.get(i - 1).x + nodeLocations.get(i).x) / 2;
        fillerLocations.add(new Point(mx, fillerY));
      }
      fillerLocations.add(new Point(
          Math.min(w - 10, nodeLocations.get(objectCount - 1).x + edgeOffset), fillerY));
    }

    private void drawFillerLabels(Graphics2D g2) {
      ensureGapFillerSize();
      fillerBounds.clear();
      Font font = getFont().deriveFont(Font.BOLD, 12f);
      g2.setFont(font);
      FontMetrics metrics = g2.getFontMetrics();
      for (int i = 0; i < fillerLocations.size(); i++) {
        Point p = fillerLocations.get(i);
        String filler = gapFillers.get(i);
        String text = filler.isBlank() ? "..." : filler;
        int pad = 5;
        int tw = metrics.stringWidth(text);
        int th = metrics.getHeight();
        int w = tw + pad * 2;
        int h = th + 2;
        int x = p.x - w / 2;
        int y = p.y - h / 2;

        g2.setColor(new Color(10, 14, 35, 220));
        g2.fillRoundRect(x, y, w, h, 8, 8);
        g2.setColor(new Color(120, 210, 255));
        g2.drawRoundRect(x, y, w, h, 8, 8);
        g2.setColor(filler.isBlank() ? new Color(180, 190, 220) : new Color(242, 247, 255));
        g2.drawString(text, x + pad, y + metrics.getAscent() + 1);

        fillerBounds.add(new Rectangle(x, y, w, h));
      }
    }

    private void drawTrack(Graphics2D g2) {
      if (nodeLocations.isEmpty()) return;
      int leftX = nodeLocations.get(0).x;
      int rightX = nodeLocations.get(nodeLocations.size() - 1).x;
      int y = nodeLocations.get(0).y;
      int margin = 40;
      g2.setColor(new Color(45, 60, 105));
      g2.setStroke(new BasicStroke(3f));
      g2.drawLine(leftX - margin, y, rightX + margin, y);
      g2.setColor(new Color(120, 210, 255, 40));
      g2.setStroke(new BasicStroke(10f));
      g2.drawLine(leftX - margin, y, rightX + margin, y);
    }

    private void drawRailwayArcs(Graphics2D g2, List<Integer> cycle, Color color,
        boolean closed, int laneIndex) {
      if (cycle.size() < 2 || nodeLocations.isEmpty()) return;
      g2.setColor(color);
      g2.setStroke(new BasicStroke(closed ? 3.5f : 2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      int nodeY = nodeLocations.get(0).y;
      int arcPeakY = Math.max(20, nodeY - ARC_BASE_OFFSET - laneIndex * ARC_LANE_HEIGHT);
      int limit = closed ? cycle.size() : cycle.size() - 1;
      for (int i = 0; i < limit; i++) {
        int fromNode = cycle.get(i);
        int toNode = cycle.get((i + 1) % cycle.size());
        drawArc(g2, fromNode, toNode, arcPeakY, color);
      }
    }

    private void drawArc(Graphics2D g2, int fromNode, int toNode, int arcPeakY, Color color) {
      if (fromNode >= nodeLocations.size() || toNode >= nodeLocations.size()) return;
      Point from = nodeLocations.get(fromNode);
      Point to = nodeLocations.get(toNode);
      int x1 = from.x, y1 = from.y;
      int x2 = to.x, y2 = to.y;
      int midY = (y1 + y2) / 2;
      // Set control point so the arc's midpoint actually reaches arcPeakY
      int ctrlX = (x1 + x2) / 2;
      int ctrlY = 2 * arcPeakY - midY;
      g2.draw(new QuadCurve2D.Float(x1, y1, ctrlX, ctrlY, x2, y2));
      // Tangent at end of quadratic bezier: direction = (P2 - P1)
      double tx = x2 - ctrlX;
      double ty = y2 - ctrlY;
      double tlen = Math.max(1.0, Math.hypot(tx, ty));
      tx /= tlen;
      ty /= tlen;
      // Place arrowhead tip at the node edge
      int tipX = (int) (x2 - tx * NODE_RADIUS);
      int tipY = (int) (y2 - ty * NODE_RADIUS);
      drawArrowhead(g2, tipX, tipY, tx, ty, color);
    }

    private void drawArrowhead(Graphics2D g2, int tipX, int tipY, double tx, double ty,
        Color color) {
      int arrow = 11;
      double angle = Math.atan2(ty, tx);
      int ax1 = (int) (tipX - arrow * Math.cos(angle - Math.PI / 6));
      int ay1 = (int) (tipY - arrow * Math.sin(angle - Math.PI / 6));
      int ax2 = (int) (tipX - arrow * Math.cos(angle + Math.PI / 6));
      int ay2 = (int) (tipY - arrow * Math.sin(angle + Math.PI / 6));
      g2.setColor(color);
      g2.fillPolygon(new int[] {tipX, ax1, ax2}, new int[] {tipY, ay1, ay2}, 3);
    }

    private void drawNodes(Graphics2D g2) {
      Font font = getFont().deriveFont(Font.BOLD, 14f);
      g2.setFont(font);
      FontMetrics metrics = g2.getFontMetrics();
      for (int i = 0; i < nodeLocations.size(); i++) {
        Point p = nodeLocations.get(i);
        boolean inDraft = currentCycle.contains(i);
        boolean committed = isCommitted(i);
        Color fill = committed ? HOT : (inDraft ? ACCENT : new Color(238, 243, 255));
        g2.setColor(new Color(fill.getRed(), fill.getGreen(), fill.getBlue(), 80));
        g2.fillOval(p.x - 30, p.y - 30, 60, 60);
        g2.setColor(fill);
        g2.fillOval(p.x - 22, p.y - 22, 44, 44);
        g2.setColor(new Color(21, 25, 43));
        String text = String.valueOf(i);
        g2.drawString(text, p.x - metrics.stringWidth(text) / 2, p.y + metrics.getAscent() / 2 - 2);
      }
    }

    private int nodeAt(Point p) {
      for (int i = 0; i < nodeLocations.size(); i++) {
        Point node = nodeLocations.get(i);
        if (node.distance(p) <= 28.0) {
          return i;
        }
      }
      return -1;
    }

    private int fillerAt(Point p) {
      for (int i = 0; i < fillerBounds.size(); i++) {
        if (fillerBounds.get(i).contains(p)) {
          return i;
        }
      }
      return -1;
    }

    private void editFillerAt(int index) {
      if (index < 0 || index >= gapFillers.size()) {
        return;
      }
      String current = gapFillers.get(index);
      String label;
      if (index == 0) {
        label = "Filler before token 0:";
      } else if (index == objectCount) {
        label = "Filler after token " + (objectCount - 1) + ":";
      } else {
        label = "Filler between token " + (index - 1) + " and token " + index + ":";
      }
      String value = JOptionPane.showInputDialog(frame, label, current);
      if (value != null) {
        gapFillers.set(index, value.trim());
        refreshAll("Updated filler " + index + ".");
      }
    }
  }
}
