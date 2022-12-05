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
import name.NicolasCoutureGrenier.Music.R12List;
import name.NicolasCoutureGrenier.Music.R16List;
import name.NicolasCoutureGrenier.Music.R48List;
import name.NicolasCoutureGrenier.Music.Rhythm;
import name.NicolasCoutureGrenier.Music.Rhythm12;
import name.NicolasCoutureGrenier.Music.Rhythm16;
import name.NicolasCoutureGrenier.Music.Rhythm48;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.Bypass;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.EntropicDispersion;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.Even;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.LowEntropy;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.MaximumGap;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.Oddity;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.Ordinal;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.SecondOrderDifferenceSum;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.SecondOrderDifferenceSum.Keep;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.ShadowContourIsomorphic;
import name.NicolasCoutureGrenier.Music.RhythmRelations.PredicatedDifferences;
import name.NicolasCoutureGrenier.Music.RhythmRelations.PredicatedJuxtaposition;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.awt.event.ActionEvent;
import java.awt.Font;


import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import name.NicolasCoutureGrenier.Music.Rn;
import javax.swing.JTextField;

public class RhythmMatrix {

  private JFrame frmRhythmMatrix;
  private JTextArea textArea_1 = new JTextArea();
  private JTextArea textArea = new JTextArea();
  private JComboBox<Rn> comboBox = new JComboBox<Rn>(new DefaultComboBoxModel<Rn>(Rn.values()));
  private JTextField textFilterModes;
  private JTextField textDiffFilterModes;
  private TreeSet<Rhythm16> r16set = Rhythm16.Generate();
  private TreeSet<Rhythm12> r12set = Rhythm12.Generate();
  //private TreeSet<Rhythm48> r48set = Rhythm48.Generate();
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          RhythmMatrix window = new RhythmMatrix();
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
  public RhythmMatrix() {
    initialize();
  }
  private double[] adjustWeights(ArrayList<Rhythm> row, ArrayList<Rhythm> possibles, int j, Double[] weights) {
    double[] o = new double[possibles.size()];
    for(int i=0;i<possibles.size();i++) {
      int count = 0;
      Rhythm r = possibles.get(i);
      for(int k=0;k<j;k++) {
        if(row.get(k).equals(r)) count++;
      }
      
      o[i] = weights[i]/(double)(count+1);
    }
    return o;
  }
  private void updateFixed() {
    if(comboBox.getSelectedItem() == Rn.Hex) textArea_1.setText("80 80\r\n08 08");
    if(comboBox.getSelectedItem() == Rn.Octal) textArea_1.setText("40 40\r\n04 04");
    if(comboBox.getSelectedItem() == Rn.Tribble) textArea_1.setText("800 000 800 000\r\n000 800 000 800");
  }
  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmRhythmMatrix = new JFrame();
    frmRhythmMatrix.setResizable(false);
    frmRhythmMatrix.setTitle("Rhythm Matrix");
    frmRhythmMatrix.setBounds(100, 100, 670, 541);
    frmRhythmMatrix.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JSpinner spinner = new JSpinner();
    spinner.setModel(new SpinnerNumberModel(1, 1, null, 1));
        
    JLabel lblRows = new JLabel("Rows:");
    lblRows.setHorizontalAlignment(SwingConstants.RIGHT);
    lblRows.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
    
    JLabel lblColumns = new JLabel("Columns:");
    lblColumns.setHorizontalAlignment(SwingConstants.RIGHT);
    lblColumns.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
    
    JSpinner spinner_1 = new JSpinner();
    spinner_1.setModel(new SpinnerNumberModel(1, 1, null, 1));
    
    JButton btnGenerate = new JButton("Generate");
    btnGenerate.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
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
              for(int i=0;i<filterModes.size();i++) {
                int filterMode = filterModes.get(i);
                Predicate<Rhythm> pred0 = null;
                switch(filterMode) {
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
                    if(comboBox.getSelectedItem() == Rn.Hex) pred0 = new Ordinal(16);
                    if(comboBox.getSelectedItem() == Rn.Octal) pred0 = new Ordinal(12);
                    if(comboBox.getSelectedItem() == Rn.Tribble) pred0 = new Ordinal(48);
                    break;
                  case 8:
                    if(comboBox.getSelectedItem() == Rn.Hex) pred0 = new Ordinal(8);
                    if(comboBox.getSelectedItem() == Rn.Octal) pred0 = new Ordinal(6);
                    if(comboBox.getSelectedItem() == Rn.Tribble) pred0 = new Ordinal(24);
                    break;
                  case 9:
                    if(comboBox.getSelectedItem() == Rn.Hex) pred0 = new Ordinal(4);
                    if(comboBox.getSelectedItem() == Rn.Octal) pred0 = new Ordinal(3);
                    if(comboBox.getSelectedItem() == Rn.Tribble) pred0 = new Ordinal(12);
                    break;
                  case 10:
                    if(comboBox.getSelectedItem() == Rn.Hex) pred0 = new Ordinal(2);
                    if(comboBox.getSelectedItem() == Rn.Octal) pred0 = new Ordinal(2);
                    if(comboBox.getSelectedItem() == Rn.Tribble) pred0 = new Ordinal(6);
                    break;
                  case 11:
                    if(comboBox.getSelectedItem() == Rn.Hex) pred0 = new MaximumGap(16);
                    if(comboBox.getSelectedItem() == Rn.Octal) pred0 = new MaximumGap(12);
                    if(comboBox.getSelectedItem() == Rn.Tribble) pred0 = new MaximumGap(48);
                    break;
                  case 12:
                    if(comboBox.getSelectedItem() == Rn.Hex) pred0 = new MaximumGap(8);
                    if(comboBox.getSelectedItem() == Rn.Octal) pred0 = new MaximumGap(6);
                    if(comboBox.getSelectedItem() == Rn.Tribble) pred0 = new MaximumGap(24);
                    break;
                  case 13:
                    if(comboBox.getSelectedItem() == Rn.Hex) pred0 = new MaximumGap(4);
                    if(comboBox.getSelectedItem() == Rn.Octal) pred0 = new MaximumGap(3);
                    if(comboBox.getSelectedItem() == Rn.Tribble) pred0 = new MaximumGap(12);
                    break;
                  case 14:
                    pred0 = new SecondOrderDifferenceSum(Keep.Zero);
                    break;
                }
                pred = pred.and(pred0);
              }
              
              TreeSet<Rhythm> t = new TreeSet<Rhythm>();
              TreeSet<Rhythm> t0 = new TreeSet<Rhythm>();
              if(comboBox.getSelectedItem() == Rn.Hex) {
                for(Rhythm16 r : r16set) t0.add(r.asRhythm());
              }
              if(comboBox.getSelectedItem() == Rn.Octal) {
                for(Rhythm12 r : r12set) t0.add(r.asRhythm());
              }
//              if(comboBox.getSelectedItem() == Rn.Tribble) {
//                for(Rhythm48 r : r48set) t0.add(r.asRhythm());
//              }
              for(Rhythm r : t0){
                if(pred.test(r)) {
                  t.add(r);
                }
              }
              Relation<Rhythm, Rhythm> relHoriz = new PredicatedJuxtaposition(pred);
              
              Relation<Rhythm, Rhythm> relSimul = null;
              if(comboBox.getSelectedItem().equals(Rn.Tribble)) {
                relSimul = new name.NicolasCoutureGrenier.Music.RhythmRelations.CompatibleRhythms();
              } else {
                relSimul = new name.NicolasCoutureGrenier.Music.RhythmRelations.Bypass();
              }
              
              for(int i=0;i<diffModes.size();i++) {
                int diffMode = diffModes.get(i);
                Relation<Rhythm, Rhythm> relSimul0 = null;
                switch(diffMode) {
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
                    if(comboBox.getSelectedItem() == Rn.Hex) relSimul0 = new PredicatedDifferences(new Ordinal(16));
                    if(comboBox.getSelectedItem() == Rn.Octal) relSimul0 = new PredicatedDifferences(new Ordinal(12));
                    if(comboBox.getSelectedItem() == Rn.Tribble) relSimul0 = new PredicatedDifferences(new Ordinal(48));
                    break;
                  case 8:
                    if(comboBox.getSelectedItem() == Rn.Hex) relSimul0 = new PredicatedDifferences(new Ordinal(8));
                    if(comboBox.getSelectedItem() == Rn.Octal) relSimul0 = new PredicatedDifferences(new Ordinal(6));
                    if(comboBox.getSelectedItem() == Rn.Tribble) relSimul0 = new PredicatedDifferences(new Ordinal(24));
                    break;
                  case 9:
                    if(comboBox.getSelectedItem() == Rn.Hex) relSimul0 = new PredicatedDifferences(new Ordinal(4));
                    if(comboBox.getSelectedItem() == Rn.Octal) relSimul0 = new PredicatedDifferences(new Ordinal(3));
                    if(comboBox.getSelectedItem() == Rn.Tribble) relSimul0 = new PredicatedDifferences(new Ordinal(12));
                    break;
                  case 10:
                    if(comboBox.getSelectedItem() == Rn.Hex) relSimul0 = new PredicatedDifferences(new Ordinal(2));
                    if(comboBox.getSelectedItem() == Rn.Octal) relSimul0 = new PredicatedDifferences(new Ordinal(2));
                    if(comboBox.getSelectedItem() == Rn.Tribble) relSimul0 = new PredicatedDifferences(new Ordinal(6));
                    break;
                  case 11:
                    if(comboBox.getSelectedItem() == Rn.Hex) relSimul0 = new PredicatedDifferences(new MaximumGap(16));
                    if(comboBox.getSelectedItem() == Rn.Octal) relSimul0 = new PredicatedDifferences(new MaximumGap(12));
                    if(comboBox.getSelectedItem() == Rn.Tribble) relSimul0 = new PredicatedDifferences(new MaximumGap(48));
                    break;
                  case 12:
                    if(comboBox.getSelectedItem() == Rn.Hex) relSimul0 = new PredicatedDifferences(new MaximumGap(8));
                    if(comboBox.getSelectedItem() == Rn.Octal) relSimul0 = new PredicatedDifferences(new MaximumGap(6));
                    if(comboBox.getSelectedItem() == Rn.Tribble) relSimul0 = new PredicatedDifferences(new MaximumGap(24));
                    break;
                  case 13:
                    if(comboBox.getSelectedItem() == Rn.Hex) relSimul0 = new PredicatedDifferences(new MaximumGap(4));
                    if(comboBox.getSelectedItem() == Rn.Octal) relSimul0 = new PredicatedDifferences(new MaximumGap(3));
                    if(comboBox.getSelectedItem() == Rn.Tribble) relSimul0 = new PredicatedDifferences(new MaximumGap(12));
                    break;
                  case 14:
                    relSimul0 = new PredicatedDifferences(new SecondOrderDifferenceSum(Keep.Zero));
                    break;
                }
                relSimul = Relation.and(relSimul, relSimul0);
              }
              int fixedSize = 0;
              int n = (int)spinner_1.getValue();
              int m = (int)spinner.getValue();
              
              Matrix<Rhythm> output = new Matrix<>(m,n);
              
              for(int i=0;i<strFixed.length;i++) {
                if(strFixed[i].trim().length() == 0) continue;
                if(comboBox.getSelectedItem() == Rn.Hex) {
                  output.insertRow(fixedSize);
                  R16List r = R16List.parseR16Seq(strFixed[i].trim());
                  for(int j=0;j<n;j++) {
                    output.set(i, j, r.get(j%r.size()));
                  } 
                } else if(comboBox.getSelectedItem() == Rn.Octal) {
                  output.insertRow(fixedSize);
                  R12List r = R12List.parseR12Seq(strFixed[i].trim());
                  for(int j=0;j<n;j++) {
                    output.set(i, j, r.get(j%r.size()));
                  }
                } else if(comboBox.getSelectedItem() == Rn.Tribble) {
                  output.insertRow(fixedSize);
                  R48List r = R48List.parseR48Seq(strFixed[i].trim());
                  for(int j=0;j<n;j++) {
                    output.set(i, j, r.get(j%r.size()));
                  }
                }
                fixedSize++;
              }
              m += fixedSize;
              Function<String, ArrayList<Rhythm>> possibles = (String str) -> {
                Rhythm r = null;
                ArrayList<Rhythm> p = new ArrayList<>();
                if(comboBox.getSelectedItem() == Rn.Hex) r = Rhythm16.parseRhythm16Hex(str).asRhythm();
                if(comboBox.getSelectedItem() == Rn.Octal) r = Rhythm12.parseRhythm12Octal(str).asRhythm();
                if(comboBox.getSelectedItem() == Rn.Tribble) {
                  Rhythm48 r48 = Rhythm48.parseRhythm48Tribbles(str);
                  List<Rhythm48> l = r48.randomizeBeat(8, Relation.bindFirst(r48, relHoriz));
                  for(var rr : l) p.add(rr.asRhythm());
                  return p;
                }
                
                for(Rhythm s : t) {
                  if(relHoriz.apply(r, s)) { 
                      p.add(s); 
                   }
                }
                
                return p;
              };
              
              BiFunction<Rhythm, ArrayList<Rhythm>, Double[]> calcWeights =
                  (Rhythm r, ArrayList<Rhythm> p) -> {
                    Double weights[] = new Double[p.size()];

                    for (int i = 0; i < p.size(); i++) {
                      weights[i] = 1.0 - Math.sqrt(r.calcNormalizedDistanceWith(p.get(i)));
                    }
                    return weights;
                  };
              
              
              int maxFailures = n*m*1000;
              int failures = 0;

              for(int i=fixedSize;i<m;i++) {
                outside:
                for(int j=0;j<n;j++) {
                  while(true) {
                    failures++;
                    ArrayList<Rhythm> p = null;
                    if(j>0) {
                      if(comboBox.getSelectedItem() == Rn.Hex) {
                        p = possibles.apply(Rhythm16.fromRhythm(output.get(i, j-1)).toString());
                      } else if(comboBox.getSelectedItem() == Rn.Octal) {
                        p = possibles.apply(Rhythm12.fromRhythm(output.get(i, j-1)).toString());
                      } else if(comboBox.getSelectedItem() == Rn.Tribble) {                        
                        p = possibles.apply(Rhythm48.fromRhythm(output.get(i, j-1)).toString());
                      }
                      
                    }
                    
                    
                    Rhythm r = null;
                    
                    if(j > 0) {
                      r = CollectionUtils.chooseAtRandomWithWeights(p, adjustWeights(output.getRow(i), p, j, calcWeights.apply(output.get(i, j-1), p)));  
                    } else {
                      if(comboBox.getSelectedItem() == Rn.Tribble) {
                        r = Rhythm48.getRandomRhythm48(pred).asRhythm();
                      } else {
                        r = CollectionUtils.chooseAtRandom(t);  
                      }
                      
                    }
                    
                    output.set(i, j, r);
                    
                    if(i > 0) {
                      boolean failed = false;
                      
                      for(int k=i-1;k>=0;k--) {
                        if(!relSimul.apply(output.get(i, j), output.get(k, j))) {
                          failed = true;
                          failures++;
                          break;
                        }
                        textArea.setText("i=" + i + ", j=" + j + " ...\n");
                      }
                      
                      if(failures > maxFailures || (t.size() == 0 && comboBox.getSelectedItem() != Rn.Tribble)||(p!=null && p.size() == 0)) {
                        failures = 0;
                        i = fixedSize;
                        j = 0;
                        break outside;
                      }
                      
                      if(failed) {
                        continue;
                      }
                    }
                    break;
                  }

                }
              }
              
              
              if(comboBox.getSelectedItem() == Rn.Hex) {
                
                Matrix<Rhythm16> tmpMat = new Matrix<Rhythm16>(m,n);
                Matrix<Rhythm16> tmpMatNot = new Matrix<Rhythm16>(m,n);
                for(int i=0;i<m;i++) {
                  for(int j=0;j<n;j++) {
                    tmpMat.set(i, j, Rhythm16.fromRhythm(output.get(i, j)));
                    tmpMatNot.set(i, j, Rhythm16.not(Rhythm16.fromRhythm(output.get(i, j))));
                  }
                }
                textArea.setText(tmpMat.toString() + "\nNOT:\n" + tmpMatNot.toString());
              } else if(comboBox.getSelectedItem() == Rn.Octal) {
                
                Matrix<Rhythm12> tmpMat = new Matrix<Rhythm12>(m,n);
                Matrix<Rhythm12> tmpMatNot = new Matrix<Rhythm12>(m,n);
                for(int i=0;i<m;i++) {
                  for(int j=0;j<n;j++) {
                    tmpMat.set(i, j, Rhythm12.fromRhythm(output.get(i, j)));
                    tmpMatNot.set(i, j, Rhythm12.not(Rhythm12.fromRhythm(output.get(i, j))));
                  }
                }
                textArea.setText(tmpMat.toString() + "\nNOT:\n" + tmpMatNot.toString());
              } else if(comboBox.getSelectedItem() == Rn.Tribble) {
                
                Matrix<Rhythm48> tmpMat = new Matrix<Rhythm48>(m,n);
                Matrix<Rhythm48> tmpMatNot = new Matrix<Rhythm48>(m,n);
                for(int i=0;i<m;i++) {
                  for(int j=0;j<n;j++) {
                    tmpMat.set(i, j, Rhythm48.fromRhythm(output.get(i, j)));
                    tmpMatNot.set(i, j, Rhythm48.not(Rhythm48.fromRhythm(output.get(i, j))));
                  }
                }
                textArea.setText(tmpMat.toString() + "\nNOT:\n" + tmpMatNot.toString());
              }
              
            } catch(Exception x) {
              textArea.setText("Error");
            }
            btnGenerate.setEnabled(true);
          }}).start();
        
        
        
      }
    });
    
    JScrollPane scrollPane = new JScrollPane();
    
    JScrollPane scrollPane_1 = new JScrollPane();
    
    JLabel lblNewLabel = new JLabel("Fixed - 1 rhythm per line");
    lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    
    JLabel lblNewLabel_1 = new JLabel("Result");
    lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    
    JLabel lblMode = new JLabel("Filters:");
    lblMode.setHorizontalAlignment(SwingConstants.RIGHT);
    lblMode.setToolTipText("<html>\r\n<ol><li>Bypass</li>\r\n<li>ShadowContourIsomorphic</li>\r\n<li>Oddity</li>\r\n<li>Entropic dispersion</li>\r\n<li>Low entropy</li>\r\n<li>Even</li>\r\n<li>Ordinal(1/1)</li>\r\n<li>Ordinal(1/2)</li>\r\n<li>Ordinal(1/4)</li><li>Ordinal(4:4=1/8, 4:3=1/6)</li><li>MaximumGap(1/1)</li><li>MaximumGap(1/2)</li><li>MaximumGap(1/4)</li>\r\n<li>SecondOrderDifferenceSumZero</li></ol></html>");
    lblMode.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
    
    JLabel lblDiffs = new JLabel("Diffs:");
    lblDiffs.setToolTipText("<html>\r\n<ol><li>Bypass</li>\r\n\r\n<li>ShadowContourIsomorphic</li>\r\n<li>Oddity</li>\r\n<li>Entropic dispersion</li>\r\n<li>Low entropy</li>\r\n<li>Even</li>\r\n<li>Ordinal(1/1)</li>\r\n<li>Ordinal(1/2)</li>\r\n<li>Ordinal(1/4)</li><li>Ordinal(4:4=1/8, 4:3=1/6)</li>\r\n<li>MaximumGap(1/1)</li><li>MaximumGap(1/2)</li><li>MaximumGap(1/4)</li><li>SecondOrderDifferenceSumZero</li></ol></html>");
    lblDiffs.setHorizontalAlignment(SwingConstants.RIGHT);
    lblDiffs.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
    comboBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        updateFixed();
      }
    });
    
    textFilterModes = new JTextField("1");
    textFilterModes.setColumns(10);
    
    textDiffFilterModes = new JTextField("1");
    textDiffFilterModes.setColumns(10);
    
    GroupLayout groupLayout = new GroupLayout(frmRhythmMatrix.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.TRAILING)
        .addGroup(groupLayout.createSequentialGroup()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addContainerGap()
              .addComponent(lblNewLabel_1, GroupLayout.DEFAULT_SIZE, 644, Short.MAX_VALUE))
            .addGroup(groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                  .addContainerGap()
                  .addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGroup(groupLayout.createSequentialGroup()
                  .addContainerGap()
                  .addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE))
                .addGroup(groupLayout.createSequentialGroup()
                  .addContainerGap()
                  .addComponent(btnGenerate, GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE))
                .addGroup(groupLayout.createSequentialGroup()
                  .addGap(13)
                  .addComponent(lblRows)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(spinner, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(lblColumns)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(lblMode, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(textFilterModes, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(lblDiffs, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(textDiffFilterModes, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(ComponentPlacement.UNRELATED)
                  .addComponent(comboBox, 0, 129, Short.MAX_VALUE)))
              .addGap(4))
            .addGroup(groupLayout.createSequentialGroup()
              .addContainerGap()
              .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
              .addGap(4)))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap(13, Short.MAX_VALUE)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
              .addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addComponent(lblMode, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
              .addComponent(textFilterModes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addComponent(lblDiffs, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
              .addComponent(textDiffFilterModes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
              .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addComponent(lblRows)
              .addComponent(lblColumns))
            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addGap(17)
          .addComponent(btnGenerate)
          .addGap(9)
          .addComponent(lblNewLabel)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addComponent(lblNewLabel_1)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
          .addGap(9))
    );
    
    updateFixed();
    
    
    scrollPane_1.setViewportView(textArea_1);
    
    
    scrollPane.setViewportView(textArea);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
    frmRhythmMatrix.getContentPane().setLayout(groupLayout);
  }
}
