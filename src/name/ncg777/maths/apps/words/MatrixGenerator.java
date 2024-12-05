package name.ncg777.maths.apps.words;
//
//import java.awt.EventQueue;
//
//import javax.swing.JFrame;
//import javax.swing.JTextArea;
//import javax.swing.GroupLayout;
//import javax.swing.GroupLayout.Alignment;
//import javax.swing.JSpinner;
//import javax.swing.LayoutStyle.ComponentPlacement;
//
//import name.ncg777.computerScience.dataStructures.CollectionUtils;
//import name.ncg777.maths.objects.Matrix;
//import name.ncg777.maths.objects.Sequence;
//import name.ncg777.maths.relations.Relation;
//import name.ncg777.music.WordOctalList;
//import name.ncg777.music.WordHexaList;
//import name.ncg777.music.Word;
//import name.ncg777.music.WordOctal;
//import name.ncg777.music.WordHexa;
//import name.ncg777.music.Rn;
//import name.ncg777.music.wordPredicates.Bypass;
//import name.ncg777.music.wordPredicates.EntropicDispersion;
//import name.ncg777.music.wordPredicates.Even;
//import name.ncg777.music.wordPredicates.LowEntropy;
//import name.ncg777.music.wordPredicates.Oddity;
//import name.ncg777.music.wordPredicates.RelativelyFlat;
//import name.ncg777.music.wordPredicates.ShadowContourIsomorphic;
//import name.ncg777.music.wordRelations.PredicatedDifferences;
//import name.ncg777.music.wordRelations.PredicatedJuxtaposition;
//
//import javax.swing.JLabel;
//import javax.swing.SpinnerNumberModel;
//import javax.swing.JButton;
//import java.awt.event.ActionListener;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.TreeSet;
//import java.util.function.BiFunction;
//import java.util.function.Function;
//import java.util.function.Predicate;
//import java.awt.event.ActionEvent;
//import java.awt.Font;
//
//
//import javax.swing.JScrollPane;
//import javax.swing.SwingConstants;
//import javax.swing.JComboBox;
//import javax.swing.DefaultComboBoxModel;
//import javax.swing.JTextField;
//
//public class RhythmMatrix {
//
//  private JFrame frmRhythmMatrix;
//  private JTextArea textArea_1 = new JTextArea();
//  private JTextArea textArea = new JTextArea();
//  private JComboBox<Rn> comboBox = new JComboBox<Rn>(new DefaultComboBoxModel<Rn>(Rn.values()));
//  private JTextField textFilterModes;
//  private JTextField textDiffFilterModes;
//  /**
//   * Launch the application.
//   */
//  public static void main(String[] args) {
//    EventQueue.invokeLater(new Runnable() {
//      public void run() {
//        try {
//          RhythmMatrix window = new RhythmMatrix();
//          window.frmRhythmMatrix.setVisible(true);
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
//      }
//    });
//  }
//  /**
//   * Create the application.
//   */
//  public RhythmMatrix() {
//    initialize();
//  }
//  private double[] adjustWeights(List<BinaryWord> row, ArrayList<BinaryWord> possibles, int j, Double[] weights) {
//    double[] o = new double[possibles.size()];
//    for(int i=0;i<possibles.size();i++) {
//      int count = 0;
//      BinaryWord r = possibles.get(i);
//      for(int k=0;k<j;k++) {
//        if(row.get(k).equals(r)) count++;
//      }
//      
//      o[i] = weights[i]/(double)(count+1);
//    }
//    return o;
//  }
//  private void updateFixed() {
//    if(comboBox.getSelectedItem() == Rn.Hex) textArea_1.setText("80");
//    if(comboBox.getSelectedItem() == Rn.Octal) textArea_1.setText("40");
//  }
//  private boolean running = false;
//  /**
//   * Initialize the contents of the frame.
//   */
//  private void initialize() {
//    frmRhythmMatrix = new JFrame();
//    frmRhythmMatrix.setResizable(false);
//    frmRhythmMatrix.setTitle("BinaryWord Matrix");
//    frmRhythmMatrix.setBounds(100, 100, 711, 541);
//    frmRhythmMatrix.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    
//    JSpinner spinner = new JSpinner();
//    spinner.setModel(new SpinnerNumberModel(1, 1, null, 1));
//        
//    JLabel lblRows = new JLabel("Rows:");
//    lblRows.setHorizontalAlignment(SwingConstants.RIGHT);
//    lblRows.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
//    
//    JLabel lblColumns = new JLabel("Columns:");
//    lblColumns.setHorizontalAlignment(SwingConstants.RIGHT);
//    lblColumns.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
//    
//    JSpinner spinner_1 = new JSpinner();
//    spinner_1.setModel(new SpinnerNumberModel(1, 1, null, 1));
//    
//    JButton btnGenerate = new JButton("Generate");
//    btnGenerate.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
//    btnGenerate.addActionListener(new ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        if(running) {running = false; return;}
//        new Thread(new Runnable() {
//
//          @Override
//          public void run() {
//            try {
//              btnGenerate.setText("Cancel");
//              running = true;
//              Sequence filterModes = Sequence.parse(textFilterModes.getText());
//              Sequence diffModes = Sequence.parse(textDiffFilterModes.getText());
//              
//              String[] strFixed = textArea_1.getText().split("\n+");
//        
//              Predicate<BinaryWord> pred = new Bypass();
//              for(int i=0;i<filterModes.size();i++) {
//                int filterMode = filterModes.get(i);
//                Predicate<BinaryWord> pred0 = null;
//                switch(filterMode) {
//                  case 1:
//                    pred0 = new Bypass();
//                    break;
//                  case 2:
//                    pred0 = new ShadowContourIsomorphic();
//                    break;
//                  case 3:
//                    pred0 = new Oddity();
//                    break;
//                  case 4:
//                    pred0 = new EntropicDispersion();
//                    break;
//                  case 5:
//                    pred0 = new LowEntropy();
//                    break;
//                  case 6:
//                    pred0 = new Even();
//                    break;
//                  case 7:
//                    pred0 = new RelativelyFlat();
//                    break;
//                }
//                pred = pred.and(pred0);
//              }
//              
//              TreeSet<BinaryWord> t = new TreeSet<BinaryWord>();
//              TreeSet<BinaryWord> t0 = new TreeSet<BinaryWord>();
//              if(comboBox.getSelectedItem() == Rn.Hex) {
//                for(HexadecimalWord r : rhexaset) t0.add(r.asRhythm());
//              }
//              if(comboBox.getSelectedItem() == Rn.Octal) {
//                for(OctalWord r : roctalset) t0.add(r.asRhythm());
//              }
//              
//              for(BinaryWord r : t0){
//                if(pred.test(r)) {
//                  t.add(r);
//                }
//              }
//              Relation<BinaryWord, BinaryWord> relHoriz = new PredicatedJuxtaposition(pred);
//              
//              Relation<BinaryWord, BinaryWord> relSimul = null;
//              relSimul = new name.ncg777.music.wordRelations.Bypass();
//              
//              for(int i=0;i<diffModes.size();i++) {
//                int diffMode = diffModes.get(i);
//                Relation<BinaryWord, BinaryWord> relSimul0 = null;
//                switch(diffMode) {
//                  case 1:
//                    relSimul0 = new PredicatedDifferences(new Bypass());
//                    break;
//                  case 2:
//                    relSimul0 = new PredicatedDifferences(new ShadowContourIsomorphic());
//                    break;
//                  case 3:
//                    relSimul0 = new PredicatedDifferences(new Oddity());
//                    break;
//                  case 4:
//                    relSimul0 = new PredicatedDifferences(new EntropicDispersion());
//                    break;
//                  case 5:
//                    relSimul0 = new PredicatedDifferences(new LowEntropy());
//                    break;
//                  case 6:
//                    relSimul0 = new PredicatedDifferences(new Even());
//                    break;
//                  case 7:
//                    relSimul0 = new PredicatedDifferences(new RelativelyFlat());
//                    break;
//                }
//                relSimul = Relation.and(relSimul, relSimul0);
//              }
//              int fixedSize = 0;
//              int n = (int)spinner_1.getValue();
//              int m = (int)spinner.getValue();
//              
//              Matrix<BinaryWord> output = new Matrix<>(m,n);
//              
//              for(int i=0;i<strFixed.length;i++) {
//                if(strFixed[i].trim().length() == 0) continue;
//                if(comboBox.getSelectedItem() == Rn.Hex) {
//                  output.insertRow(fixedSize);
//                  HexadecimalSentence r = HexadecimalSentence.parseR16Seq(strFixed[i].trim());
//                  for(int j=0;j<n;j++) {
//                    output.set(i, j, r.get(j%r.size()));
//                  } 
//                } else if(comboBox.getSelectedItem() == Rn.Octal) {
//                  output.insertRow(fixedSize);
//                  OctalSentence r = OctalSentence.parseR12Seq(strFixed[i].trim());
//                  for(int j=0;j<n;j++) {
//                    output.set(i, j, r.get(j%r.size()));
//                  }
//                }
//                fixedSize++;
//              }
//              m += fixedSize;
//              Function<String, ArrayList<BinaryWord>> possibles = (String str) -> {
//                BinaryWord r = null;
//                ArrayList<BinaryWord> p = new ArrayList<>();
//                if(comboBox.getSelectedItem() == Rn.Hex) r = HexadecimalWord.parseRhythmHexa(str).asRhythm();
//                if(comboBox.getSelectedItem() == Rn.Octal) r = OctalWord.parseRhythm12Octal(str).asRhythm();
//                for(BinaryWord s : t) {
//                  if(relHoriz.apply(r, s)) { 
//                      p.add(s); 
//                   }
//                }
//                
//                return p;
//              };
//              
//              BiFunction<BinaryWord, List<BinaryWord>, Double[]> calcWeights =
//                  (BinaryWord r, List<BinaryWord> p) -> {
//                    Double weights[] = new Double[p.size()];
//  
//                    for (int i = 0; i < p.size(); i++) {
//                      weights[i] = 1.0 - Math.sqrt(r.calcNormalizedDistanceWith(p.get(i)));
//                      if(r.equals(p.get(i))) weights[i]=weights[i]/8;
//                    }
//                    return weights;
//                  };
//              
//              
//              //int maxFailures = n*m*1000;
//              //int failures = 0;
//
//              for(int i=fixedSize;i<m;i++) {
//                outside:
//                for(int j=0;j<n;j++) {
//                  while(true) {
//                    if(!running) {
//                      break outside;
//                    }
//                    //failures++;
//                    ArrayList<BinaryWord> p = null;
//                    if(j>0) {
//                      if(comboBox.getSelectedItem() == Rn.Hex) {
//                        p = possibles.apply(HexadecimalWord.fromRhythm(output.get(i, j-1)).toString());
//                      } else if(comboBox.getSelectedItem() == Rn.Octal) {
//                        p = possibles.apply(OctalWord.fromRhythm(output.get(i, j-1)).toString());
//                      }
//                    }
//                    
//                    
//                    BinaryWord r = null;
//                    
//                    if(j > 0) {
//                      r = CollectionUtils.chooseAtRandomWithWeights(p, adjustWeights(output.getRow(i), p, j, calcWeights.apply(output.get(i, j-1), p)));  
//                    } else {
//                      r = CollectionUtils.chooseAtRandom(t); 
//                    }
//                    
//                    output.set(i, j, r);
//                    
//                    if(i > 0) {
//                      boolean failed = false;
//                      
//                      for(int k=i-1;k>=0;k--) {
//                        if(!relSimul.apply(output.get(i, j), output.get(k, j))) {
//                          failed = true;
//                          //failures++;
//                          break;
//                        }
//                        textArea.setText("i=" + i + ", j=" + j + " ...\n");
//                      }
//                      
//                      //if(failures > maxFailures || (t.size() == 0 && comboBox.getSelectedItem() != Rn.Tribble)||(p!=null && p.size() == 0)) {
//                      //  failures = 0;
//                      //  i = fixedSize;
//                      //  j = 0;
//                     //   break outside;
//                      //}
//                      
//                      if(failed) {
//                        continue;
//                      }
//                    }
//                    break;
//                  }
//
//                }
//              }
//              
//              if(running) {
//                if(comboBox.getSelectedItem() == Rn.Hex) {
//                  
//                  Matrix<HexadecimalWord> tmpMat = new Matrix<HexadecimalWord>(m,n);
//                  Matrix<HexadecimalWord> tmpMatNot = new Matrix<HexadecimalWord>(m,n);
//                  for(int i=0;i<m;i++) {
//                    for(int j=0;j<n;j++) {
//                      tmpMat.set(i, j, HexadecimalWord.fromRhythm(output.get(i, j)));
//                      tmpMatNot.set(i, j, HexadecimalWord.not(HexadecimalWord.fromRhythm(output.get(i, j))));
//                    }
//                  }
//                  textArea.setText(tmpMat.toString() + "\nNOT:\n" + tmpMatNot.toString());
//                } else if(comboBox.getSelectedItem() == Rn.Octal) {
//                  
//                  Matrix<OctalWord> tmpMat = new Matrix<OctalWord>(m,n);
//                  Matrix<OctalWord> tmpMatNot = new Matrix<OctalWord>(m,n);
//                  for(int i=0;i<m;i++) {
//                    for(int j=0;j<n;j++) {
//                      tmpMat.set(i, j, OctalWord.fromRhythm(output.get(i, j)));
//                      tmpMatNot.set(i, j, OctalWord.not(OctalWord.fromRhythm(output.get(i, j))));
//                    }
//                  }
//                  textArea.setText(tmpMat.toString() + "\nNOT:\n" + tmpMatNot.toString());
//                }
//              }
//            } catch(Exception x) {
//              textArea.setText("Error");
//            }
//            if(!running) {
//              textArea.setText("");
//            }
//            btnGenerate.setText("Generate");
//            running = false;
//          }}).start();
//        
//        
//        
//        
//      }
//    });
//    
//    JScrollPane scrollPane = new JScrollPane();
//    
//    JScrollPane scrollPane_1 = new JScrollPane();
//    
//    JLabel lblNewLabel = new JLabel("Fixed - 1 rhythm per line");
//    lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
//    lblNewLabel.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
//    
//    JLabel lblNewLabel_1 = new JLabel("Result");
//    lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
//    lblNewLabel_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
//    
//    JLabel lblMode = new JLabel("Filters:");
//    lblMode.setHorizontalAlignment(SwingConstants.RIGHT);
//    lblMode.setToolTipText("<html>\r\n<ol><li>Bypass</li>\r\n<li>ShadowContourIsomorphic</li>\r\n<li>Oddity</li>\r\n<li>Entropic dispersion</li>\r\n<li>Low entropy</li>\r\n<li>Even</li><li>RelativelyFlat</li></ol></html>");
//    lblMode.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
//    
//    JLabel lblDiffs = new JLabel("Diffs:");
//    lblDiffs.setToolTipText("<html>\r\n<ol><li>Bypass</li>\r\n\r\n<li>ShadowContourIsomorphic</li>\r\n<li>Oddity</li>\r\n<li>Entropic dispersion</li>\r\n<li>Low entropy</li>\r\n<li>Even</li>\r\n<li>RelativelyFlat</li></ol></html>");
//    lblDiffs.setHorizontalAlignment(SwingConstants.RIGHT);
//    lblDiffs.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
//    comboBox.addActionListener(new ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        updateFixed();
//      }
//    });
//    
//    textFilterModes = new JTextField("1");
//    textFilterModes.setColumns(10);
//    
//    textDiffFilterModes = new JTextField("1");
//    textDiffFilterModes.setColumns(10);
//    
//    GroupLayout groupLayout = new GroupLayout(frmRhythmMatrix.getContentPane());
//    groupLayout.setHorizontalGroup(
//      groupLayout.createParallelGroup(Alignment.TRAILING)
//        .addGroup(groupLayout.createSequentialGroup()
//          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
//            .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
//              .addContainerGap()
//              .addComponent(scrollPane))
//            .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
//              .addContainerGap()
//              .addComponent(lblNewLabel_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
//            .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
//              .addContainerGap()
//              .addComponent(scrollPane_1))
//            .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
//              .addContainerGap()
//              .addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
//            .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
//              .addContainerGap()
//              .addComponent(btnGenerate, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
//            .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
//              .addGap(13)
//              .addComponent(lblRows)
//              .addPreferredGap(ComponentPlacement.RELATED)
//              .addComponent(spinner, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
//              .addPreferredGap(ComponentPlacement.RELATED)
//              .addComponent(lblColumns)
//              .addPreferredGap(ComponentPlacement.RELATED)
//              .addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
//              .addPreferredGap(ComponentPlacement.RELATED)
//              .addComponent(lblMode, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
//              .addPreferredGap(ComponentPlacement.RELATED)
//              .addComponent(textFilterModes, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
//              .addPreferredGap(ComponentPlacement.RELATED)
//              .addComponent(lblDiffs, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
//              .addPreferredGap(ComponentPlacement.RELATED)
//              .addComponent(textDiffFilterModes, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE)
//              .addPreferredGap(ComponentPlacement.UNRELATED)
//              .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)))
//          .addGap(149))
//    );
//    groupLayout.setVerticalGroup(
//      groupLayout.createParallelGroup(Alignment.LEADING)
//        .addGroup(groupLayout.createSequentialGroup()
//          .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
//            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
//              .addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//              .addComponent(lblMode, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
//              .addComponent(textFilterModes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//              .addComponent(lblDiffs, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
//              .addComponent(textDiffFilterModes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
//              .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//              .addComponent(lblRows)
//              .addComponent(lblColumns))
//            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//          .addGap(17)
//          .addComponent(btnGenerate)
//          .addGap(9)
//          .addComponent(lblNewLabel)
//          .addPreferredGap(ComponentPlacement.RELATED)
//          .addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
//          .addPreferredGap(ComponentPlacement.UNRELATED)
//          .addComponent(lblNewLabel_1)
//          .addPreferredGap(ComponentPlacement.RELATED)
//          .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
//          .addGap(9))
//    );
//    
//    updateFixed();
//    
//    
//    scrollPane_1.setViewportView(textArea_1);
//    
//    
//    scrollPane.setViewportView(textArea);
//    textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
//    frmRhythmMatrix.getContentPane().setLayout(groupLayout);
//  }
//}
