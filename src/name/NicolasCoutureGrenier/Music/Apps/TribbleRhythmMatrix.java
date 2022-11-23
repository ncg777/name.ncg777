package name.NicolasCoutureGrenier.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;

import name.NicolasCoutureGrenier.Maths.DataStructures.CollectionUtils;
import name.NicolasCoutureGrenier.Maths.DataStructures.Matrix;
import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence;
import name.NicolasCoutureGrenier.Maths.Relations.Relation;
import name.NicolasCoutureGrenier.Music.BeatRhythm;
import name.NicolasCoutureGrenier.Music.MeasureRhythm;
import name.NicolasCoutureGrenier.Music.Rhythm;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.Bypass;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.EntropicDispersion;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.Even;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.LowEntropy;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.Oddity;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.Ordinal;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.SecondOrderDifferenceSum;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.SecondOrderDifferenceSum.Keep;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.ShadowContourIsomorphic;
import name.NicolasCoutureGrenier.Music.RhythmRelations.PredicatedDifferences;
import name.NicolasCoutureGrenier.Music.RhythmRelations.PredicatedJuxtaposition;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.awt.event.ActionEvent;
import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import java.awt.Color;

public class TribbleRhythmMatrix {

  private JFrame frmRhythmMatrix;
  private JTextArea textArea_1 = new JTextArea();
  private JTextArea textArea = new JTextArea();
  private JTextField textFilterModes;
  private JTextField textDiffFilterModes;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          TribbleRhythmMatrix window = new TribbleRhythmMatrix();
          window.frmRhythmMatrix.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public TribbleRhythmMatrix() {
    initialize();
  }

  private double[] adjustWeights(ArrayList<Rhythm> row, ArrayList<Rhythm> possibles, int j,
      Double[] weights) {
    double[] o = new double[possibles.size()];
    for (int i = 0; i < possibles.size(); i++) {
      int count = 0;
      Rhythm r = possibles.get(i);
      for (int k = 0; k < j; k++) {
        if (row.get(k).equals(r)) count++;
      }

      o[i] = weights[i] / (double) (count + 1);
    }
    return o;
  }

  JButton btnGenerate = new JButton("Loading...");
  private TreeSet<MeasureRhythm> measureRhythm = null;

  /**
   * Initialize the contents of the frame.
   */
  @SuppressWarnings("unchecked")
  private void initialize() {
    btnGenerate.setForeground(new Color(0, 0, 0));
    btnGenerate.setBackground(new Color(127, 255, 0));
    btnGenerate.setEnabled(false);
    
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/measureRhythms.bin");
    ObjectInputStream objectInputStream;
    try {

      objectInputStream = new ObjectInputStream(is);
      measureRhythm = (TreeSet<MeasureRhythm>) objectInputStream.readObject();
      objectInputStream.close();
    } catch (FileNotFoundException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (ClassNotFoundException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    
    btnGenerate.setText("GENERATE");
    btnGenerate.setEnabled(true);
  

    frmRhythmMatrix = new JFrame();
    frmRhythmMatrix.setBackground(Color.GRAY);
    frmRhythmMatrix.getContentPane().setBackground(Color.GRAY);
    frmRhythmMatrix.setResizable(false);
    frmRhythmMatrix.setTitle(
        "Random tribble rhythm matrix — 12 bit words  — Mixed 4/1, 4/2, 4/3, 4/4 timesigs, synchronized");
    frmRhythmMatrix.setBounds(100, 100, 832, 545);
    frmRhythmMatrix.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JSpinner spinner = new JSpinner();
    spinner.setModel(new SpinnerNumberModel(1, 1, null, 1));

    JLabel lblRows = new JLabel("Rows:");
    lblRows.setForeground(Color.GREEN);
    lblRows.setHorizontalAlignment(SwingConstants.RIGHT);
    lblRows.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 10));

    JLabel lblColumns = new JLabel("#Measures:");
    lblColumns.setForeground(Color.GREEN);
    lblColumns.setHorizontalAlignment(SwingConstants.RIGHT);
    lblColumns.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 10));

    JSpinner spinner_1 = new JSpinner();
    spinner_1.setModel(new SpinnerNumberModel(1, 1, null, 1));

    btnGenerate.setFont(new Font("DejaVu Sans Mono", Font.BOLD, 16));
    btnGenerate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new Thread(new Runnable() {

          @Override
          public void run() {
            try {
              btnGenerate.setEnabled(false);
              Sequence filterModes = Sequence.parse(textFilterModes.getText());
              Sequence diffModes = Sequence.parse(textDiffFilterModes.getText());

              String[] strFixed = textArea_1.getText().split("\n+");

              Predicate<Rhythm> pred = new Bypass();
              for (int i = 0; i < filterModes.size(); i++) {
                int filterMode = filterModes.get(i);
                Predicate<Rhythm> pred0 = null;
                switch (filterMode) {
                  case 1:
                    pred0 = new Bypass();
                    break;
                  case 2:
                    pred0 = new ShadowContourIsomorphic();
                    break;
                  case 3:
                    pred0 = new Oddity();
                    break;
                  case 4:
                    pred0 = new EntropicDispersion();
                    break;
                  case 5:
                    pred0 = new LowEntropy();
                    break;
                  case 6:
                    pred0 = new Even();
                    break;
                  case 7:
                    pred0 = new Ordinal(BeatRhythm.NbBits * 4);
                    break;
                  case 8:
                    pred0 = new Ordinal(BeatRhythm.NbBits * 2);
                    break;
                  case 9:
                    pred0 = new SecondOrderDifferenceSum(Keep.Zero);
                    break;
                }
                pred = pred.and(pred0);
              }

              TreeSet<Rhythm> t = new TreeSet<Rhythm>();
              TreeSet<Rhythm> t0 = new TreeSet<Rhythm>();
              for (MeasureRhythm r : measureRhythm)
                t0.add(r.asRhythm());
              for (Rhythm r : t0) {
                if (pred.test(r)) {
                  t.add(r);
                }
              }
              Relation<Rhythm, Rhythm> relHoriz = new PredicatedJuxtaposition(pred);

              Relation<Rhythm, Rhythm> relSimul =
                  new name.NicolasCoutureGrenier.Music.RhythmRelations.CompatibleBeatRhythms();
              for (int i = 0; i < diffModes.size(); i++) {
                int diffMode = diffModes.get(i);
                Relation<Rhythm, Rhythm> relSimul0 = null;
                switch (diffMode) {
                  case 1:
                    relSimul0 = new PredicatedDifferences(new Bypass());
                    break;
                  case 2:
                    relSimul0 = new PredicatedDifferences(new ShadowContourIsomorphic());
                    break;
                  case 3:
                    relSimul0 = new PredicatedDifferences(new Oddity());
                    break;
                  case 4:
                    relSimul0 = new PredicatedDifferences(new EntropicDispersion());
                    break;
                  case 5:
                    relSimul0 = new PredicatedDifferences(new LowEntropy());
                    break;
                  case 6:
                    relSimul0 = new PredicatedDifferences(new Even());
                    break;
                  case 7:
                    relSimul0 = new PredicatedDifferences(new Ordinal(BeatRhythm.NbBits * 4));
                    break;
                  case 8:
                    relSimul0 = new PredicatedDifferences(new Ordinal(BeatRhythm.NbBits * 2));
                    break;
                  case 9:
                    relSimul0 = new PredicatedDifferences(new SecondOrderDifferenceSum(Keep.Zero));
                    break;
                }
                relSimul = Relation.and(relSimul, relSimul0);
              }
              int fixedSize = 0;
              int n = (int) spinner_1.getValue();
              int m = (int) spinner.getValue();

              Matrix<Rhythm> output = new Matrix<>(m, n);

              for (int i = 0; i < strFixed.length; i++) {
                if (strFixed[i].trim().length() == 0) continue;
                output.insertRow(fixedSize);
                ArrayList<MeasureRhythm> r =
                    MeasureRhythm.parseMeasureRhythm(strFixed[i].trim()).splitInChunks(4);
                for (int j = 0; j < n; j++) {
                  output.set(i, j, r.get(j % r.size()).asRhythm());
                }
                fixedSize++;
              }
              m += fixedSize;

              TreeMap<Rhythm, Double[]> cacheWeights = new TreeMap<>();
              BiFunction<Rhythm, ArrayList<Rhythm>, Double[]> calcWeights =
                  (Rhythm r, ArrayList<Rhythm> p) -> {
                    if (cacheWeights.containsKey(r)) return cacheWeights.get(r);
                    Double weights[] = new Double[p.size()];

                    for (int i = 0; i < p.size(); i++) {
                      weights[i] = 1.0 - Math.sqrt(r.calcNormalizedDistanceWith(p.get(i)));
                    }
                    cacheWeights.put(r, weights);
                    return weights;
                  };

              TreeMap<Rhythm, ArrayList<Rhythm>> cachePossibles = new TreeMap<>();
              BiFunction<Rhythm, Integer, ArrayList<Rhythm>> possibles = (Rhythm r, Integer j) -> {
                if (cachePossibles.containsKey(r)) return cachePossibles.get(r);
                ArrayList<Rhythm> p = new ArrayList<>();

                for (Rhythm s : t) {
                  if (relHoriz.apply(r, s)) {
                    p.add(s);
                  }
                }
                cachePossibles.put(r, p);
                return p;
              };



              int maxFailures = n * m * 500;
              int failures = 0;

              for (int i = fixedSize; i < m; i++) {
                outside: for (int j = 0; j < n; j++) {
                  while (true) {
                    failures++;
                    ArrayList<Rhythm> p = null;
                    if (j > 0) {
                      p = possibles.apply(output.get(i, j - 1), j);
                    }

                    Rhythm r = null;

                    if (j > 0) {
                      r = CollectionUtils.chooseAtRandomWithWeights(p, adjustWeights(
                          output.getRow(i), p, j, calcWeights.apply(output.get(i, j - 1), p)));
                    } else {
                      r = CollectionUtils.chooseAtRandom(t);
                    }

                    output.set(i, j, r);

                    if (i > 0) {
                      boolean failed = false;

                      for (int k = i - 1; k >= 0; k--) {
                        if (!relSimul.apply(output.get(i, j), output.get(k, j))) {
                          failed = true;
                          failures++;
                          break;
                        }
                        textArea.setText("i=" + i + ", j=" + j + " ...\n");
                      }

                      if (failures > maxFailures || t.size() == 0 || (p != null && p.size() == 0)) {
                        failures = 0;
                        i = fixedSize;
                        j = 0;
                        break outside;
                      }

                      if (failed) {
                        continue;
                      }
                    }
                    break;
                  }

                }
              }

              Matrix<MeasureRhythm> tmpMat = new Matrix<MeasureRhythm>(m, n);
              for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                  tmpMat.set(i, j, MeasureRhythm.fromRhythm(output.get(i, j)));
                }
              }
              textArea.setText(tmpMat.toString());

            } catch (Exception x) {
              textArea.setText(x.getMessage());
            }
            btnGenerate.setEnabled(true);
          }
        }).start();
      }
    });

    JScrollPane scrollPane = new JScrollPane();

    JScrollPane scrollPane_1 = new JScrollPane();

    JLabel lblNewLabel = new JLabel(
        "Fixed rhythms — One rhythm per line — Line lenth can vary — #tribble must be multiple of 4");
    lblNewLabel.setForeground(Color.ORANGE);
    lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel.setFont(new Font("DejaVu Sans Mono", Font.BOLD, 11));

    JLabel lblNewLabel_1 = new JLabel("Result");
    lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));

    JLabel lblMode = new JLabel("Predicates:");
    lblMode.setForeground(Color.CYAN);
    lblMode.setHorizontalAlignment(SwingConstants.RIGHT);
    lblMode.setToolTipText(
        "<html>\r\n<ol><li>Bypass</li>\r\n<li>ShadowContourIsomorphic</li>\r\n<li>Oddity</li>\r\n<li>Entropic dispersion</li>\r\n<li>Low entropy</li><li>Even</li>\r\n<li>Ordinal (1/1)</li>\r\n<li>Ordinal (1/2)</li>\r\n<li>ZeroSecondOrderDifferenceSum</li></ol></html>");
    lblMode.setFont(new Font("DejaVu Sans Mono", Font.BOLD, 10));

    JLabel lblDiffs = new JLabel("Difference predicates:");
    lblDiffs.setForeground(Color.CYAN);
    lblDiffs.setToolTipText(
        "<html>\r\n<ol><li>Bypass</li>\r\n\r\n<li>ShadowContourIsomorphic</li>\r\n<li>Oddity</li>\r\n<li>Entropic dispersion</li>\r\n<li>Low entropy</li><li>Even</li>\r\n<li>Ordinal (1/1)</li>\r\n<li>Ordinal (1/2)</li>\r\n<li>ZeroSecondOrderDifferenceSum</li></ol></html>");
    lblDiffs.setHorizontalAlignment(SwingConstants.RIGHT);
    lblDiffs.setFont(new Font("DejaVu Sans Mono", Font.BOLD, 10));

    textFilterModes = new JTextField("1");
    textFilterModes.setColumns(10);

    textDiffFilterModes = new JTextField("1");
    textDiffFilterModes.setColumns(10);

    JLabel lblNewLabel_2 = new JLabel("1 tribble = 1 beat");
    lblNewLabel_2.setForeground(Color.RED);
    lblNewLabel_2.setHorizontalAlignment(SwingConstants.LEFT);
    lblNewLabel_2.setFont(new Font("DejaVu Sans Mono", Font.BOLD, 10));

    GroupLayout groupLayout = new GroupLayout(frmRhythmMatrix.getContentPane());
    groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
        .addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout
            .createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                    .addGroup(groupLayout.createSequentialGroup().addContainerGap()
                        .addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 741, Short.MAX_VALUE))
                    .addGroup(
                        groupLayout.createSequentialGroup().addGap(13).addComponent(lblRows)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(spinner, GroupLayout.PREFERRED_SIZE, 57,
                                GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED).addComponent(lblColumns)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, 51,
                                GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lblMode, GroupLayout.PREFERRED_SIZE, 72,
                                GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(textFilterModes, GroupLayout.PREFERRED_SIZE, 70,
                                GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lblDiffs, GroupLayout.PREFERRED_SIZE, 132,
                                GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(textDiffFilterModes, GroupLayout.PREFERRED_SIZE, 69,
                                GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED).addComponent(lblNewLabel_2,
                                GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE))
                    .addGroup(groupLayout.createSequentialGroup().addContainerGap().addComponent(
                        btnGenerate, GroupLayout.PREFERRED_SIZE, 782, GroupLayout.PREFERRED_SIZE))
                    .addGroup(groupLayout.createSequentialGroup().addContainerGap().addComponent(
                        scrollPane_1, GroupLayout.PREFERRED_SIZE, 781, GroupLayout.PREFERRED_SIZE)))
                .addGap(4))
            .addGroup(groupLayout.createSequentialGroup().addContainerGap()
                .addComponent(lblNewLabel_1, GroupLayout.DEFAULT_SIZE, 745, Short.MAX_VALUE))
            .addGroup(groupLayout.createSequentialGroup().addContainerGap().addComponent(scrollPane,
                GroupLayout.PREFERRED_SIZE, 781, GroupLayout.PREFERRED_SIZE)))
            .addContainerGap()));
    groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRows).addComponent(lblColumns))
                .addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                    GroupLayout.PREFERRED_SIZE)
                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(lblMode, GroupLayout.PREFERRED_SIZE, 15,
                        GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDiffs, GroupLayout.PREFERRED_SIZE, 15,
                        GroupLayout.PREFERRED_SIZE)
                    .addComponent(textFilterModes, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(textDiffFilterModes, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 24,
                        GroupLayout.PREFERRED_SIZE)))
            .addGap(17).addComponent(btnGenerate).addGap(9).addComponent(lblNewLabel)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED).addComponent(lblNewLabel_1)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 281, GroupLayout.PREFERRED_SIZE)
            .addGap(20)));
    textArea_1.setForeground(new Color(0, 0, 0));
    textArea_1.setBackground(new Color(255, 255, 255));
    textArea_1.setFont(new Font("Monospaced", Font.PLAIN, 10));

    textArea_1.setText(
        "800 000 800 000\n000 800 000 800");


    scrollPane_1.setViewportView(textArea_1);
    textArea.setBackground(new Color(255, 255, 255));
    textArea.setForeground(new Color(0, 0, 0));


    scrollPane.setViewportView(textArea);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
    frmRhythmMatrix.getContentPane().setLayout(groupLayout);
  }
}
