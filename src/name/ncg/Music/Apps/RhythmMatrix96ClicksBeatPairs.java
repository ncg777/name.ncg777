package name.ncg.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;

import name.ncg.Maths.DataStructures.CollectionUtils;
import name.ncg.Maths.DataStructures.Matrix;
import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Maths.Relations.Relation;
import name.ncg.Music.BeatRhythm;
import name.ncg.Music.MeasureRhythm;
import name.ncg.Music.Rhythm;
import name.ncg.Music.RhythmPredicates.Bypass;
import name.ncg.Music.RhythmPredicates.EntropicDispersion;
import name.ncg.Music.RhythmPredicates.Even;
import name.ncg.Music.RhythmPredicates.LowEntropy;
import name.ncg.Music.RhythmPredicates.Oddity;
import name.ncg.Music.RhythmPredicates.Ordinal;
import name.ncg.Music.RhythmPredicates.ShadowContourIsomorphic;
import name.ncg.Music.RhythmRelations.PredicatedDifferences;
import name.ncg.Music.RhythmRelations.PredicatedJuxtaposition;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.awt.event.ActionEvent;
import java.awt.Font;


import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import name.ncg.Music.Rn;
import javax.swing.JTextField;

public class RhythmMatrix96ClicksBeatPairs {

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
          RhythmMatrix96ClicksBeatPairs window = new RhythmMatrix96ClicksBeatPairs();
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
  public RhythmMatrix96ClicksBeatPairs() {
    initialize();
  }
  private double[] adjustWeights(ArrayList<Rhythm> row, ArrayList<Rhythm> possibles, int j, double[] weights) {
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
  JButton btnGenerate = new JButton("Loading...");
  private TreeSet<MeasureRhythm> measureRhythm = null;
  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    btnGenerate.setEnabled(false);
    new Thread(() -> {
        measureRhythm = MeasureRhythm.generate(2);
        btnGenerate.setText("Generate");
        btnGenerate.setEnabled(true);
      }).start();
    
    frmRhythmMatrix = new JFrame();
    frmRhythmMatrix.setResizable(false);
    frmRhythmMatrix.setTitle("Random hex rhythm matrix — 96 Clicks/beat  — 1 bit/click — Mixed 2/1, 2/2, 2/3, 2/4 timesigs, synchronized");
    frmRhythmMatrix.setBounds(100, 100, 781, 545);
    frmRhythmMatrix.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JSpinner spinner = new JSpinner();
    spinner.setModel(new SpinnerNumberModel(1, 1, null, 1));
        
    JLabel lblRows = new JLabel("Rows:");
    lblRows.setHorizontalAlignment(SwingConstants.RIGHT);
    lblRows.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
    
    JLabel lblColumns = new JLabel("#BeatPairs:");
    lblColumns.setHorizontalAlignment(SwingConstants.RIGHT);
    lblColumns.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
    
    JSpinner spinner_1 = new JSpinner();
    spinner_1.setModel(new SpinnerNumberModel(1, 1, null, 1));
    
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
                    pred0 = new Ordinal(BeatRhythm.Clicks*2);
                    break;
                  case 8:
                    pred0 = new Ordinal(BeatRhythm.Clicks);
                    break;
                }
                pred = Predicates.and(pred, pred0);
              }
              
              TreeSet<Rhythm> t = new TreeSet<Rhythm>();
              TreeSet<Rhythm> t0 = new TreeSet<Rhythm>();
              for(MeasureRhythm r : measureRhythm) t0.add(r.asRhythm());
              for(Rhythm r : t0){
                if(pred.apply(r)) {
                  t.add(r);
                }
              }
              Relation<Rhythm, Rhythm> relHoriz = new PredicatedJuxtaposition(pred);
              
              Relation<Rhythm, Rhythm> relSimul = new name.ncg.Music.RhythmRelations.CompatibleBeatRhythms();
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
                    relSimul0 = new PredicatedDifferences(new Ordinal(BeatRhythm.Clicks*2));
                    break;
                  case 8:
                    relSimul0 = new PredicatedDifferences(new Ordinal(BeatRhythm.Clicks));
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
                output.insertRow(fixedSize);
                ArrayList<MeasureRhythm> r = MeasureRhythm.parseMeasureRhythm(strFixed[i].trim()).splitInPairs();
                for(int j=0;j<n;j++) {
                  output.set(i, j, r.get(j%r.size()).asRhythm());
                } 
                fixedSize++;
              }
              m += fixedSize;
              TreeMap<String, ArrayList<Rhythm>> cachePossibles = new TreeMap<>();
              TreeMap<String, double[]> cacheWeights = new TreeMap<>();
              Function<String, ArrayList<Rhythm>> possibles = (String str) -> {
                if(cachePossibles.containsKey(str)) return (ArrayList<Rhythm>)cachePossibles.get(str);
                Rhythm r = Rhythm.buildRhythm(str);
                ArrayList<Rhythm> p = new ArrayList<>();
                
                for(Rhythm s : t) {
                  if(relHoriz.apply(r, s)) { 
                      p.add(s); 
                   }
                }
                
                cachePossibles.put(str, p);
                double weights[] = new double[p.size()];
                
                for(int i=0;i<p.size();i++) {
                  weights[i] = 1.0-Math.sqrt(r.calcNormalizedDistanceWith(p.get(i)));
                }
                cacheWeights.put(str, weights);
                
                return p;
              };
              
              int maxFailures = n*m*500;
              int failures = 0;

              for(int i=fixedSize;i<m;i++) {
                outside:
                for(int j=0;j<n;j++) {
                  while(true) {
                    failures++;
                    ArrayList<Rhythm> p = null;
                    if(j>0) {
                      p = possibles.apply(output.get(i, j-1).toBinaryString());
                    }
                    
                    Rhythm r = null;
                    
                    if(j > 0) {
                      r = CollectionUtils.chooseAtRandomWithWeights(p, adjustWeights(output.getRow(i), p, j, cacheWeights.get(output.get(i, j-1).toBinaryString())));  
                    } else {
                      r = CollectionUtils.chooseAtRandom(t);
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
                      
                      if(failures > maxFailures || t.size() == 0||(p!=null && p.size() == 0)) {
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
              
              Matrix<MeasureRhythm> tmpMat = new Matrix<MeasureRhythm>(m,n);
              for(int i=0;i<m;i++) {
                for(int j=0;j<n;j++) {
                  tmpMat.set(i, j, MeasureRhythm.fromRhythm(output.get(i, j)));
                }
              }
              textArea.setText(tmpMat.toString());
              
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
    lblMode.setToolTipText("<html>\r\n<ol><li>Bypass</li>\r\n<li>ShadowContourIsomorphic</li>\r\n<li>Oddity</li>\r\n<li>Entropic dispersion</li>\r\n<li>Low entropy</li><li>Even</li>\r\n<li>Ordinal (1/1)</li>\r\n<li>Ordinal (1/2)</li>\r\n</ol></html>");
    lblMode.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
    
    JLabel lblDiffs = new JLabel("Diffs:");
    lblDiffs.setToolTipText("<html>\r\n<ol><li>Bypass</li>\r\n\r\n<li>ShadowContourIsomorphic</li>\r\n<li>Oddity</li>\r\n<li>Entropic dispersion</li>\r\n<li>Low entropy</li><li>Even</li>\r\n<li>Ordinal (1/1)</li>\r\n<li>Ordinal (1/2)</li>\r\n</ol></html>");
    lblDiffs.setHorizontalAlignment(SwingConstants.RIGHT);
    lblDiffs.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
    
    textFilterModes = new JTextField("1");
    textFilterModes.setColumns(10);
    
    textDiffFilterModes = new JTextField("1");
    textDiffFilterModes.setColumns(10);
    
    JLabel lblNewLabel_2 = new JLabel("12 bytes = 1 beat");
    lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_2.setFont(new Font("DejaVu Sans Mono", Font.ITALIC, 12));
    
    GroupLayout groupLayout = new GroupLayout(frmRhythmMatrix.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.TRAILING)
        .addGroup(groupLayout.createSequentialGroup()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addContainerGap()
              .addComponent(lblNewLabel_1, GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE))
            .addGroup(groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                  .addContainerGap()
                  .addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE))
                .addGroup(groupLayout.createSequentialGroup()
                  .addContainerGap()
                  .addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 730, GroupLayout.PREFERRED_SIZE))
                .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
                  .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
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
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblNewLabel_2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                  .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(btnGenerate, GroupLayout.PREFERRED_SIZE, 731, GroupLayout.PREFERRED_SIZE))))
              .addGap(4))
            .addGroup(groupLayout.createSequentialGroup()
              .addContainerGap()
              .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 731, GroupLayout.PREFERRED_SIZE)))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                  .addComponent(lblColumns)))
              .addGap(17))
            .addGroup(groupLayout.createSequentialGroup()
              .addContainerGap()
              .addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)))
          .addComponent(btnGenerate)
          .addGap(9)
          .addComponent(lblNewLabel)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addComponent(lblNewLabel_1)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
          .addGap(9))
    );
    textArea_1.setFont(new Font("Monospaced", Font.PLAIN, 10));
    
    textArea_1.setText("80 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00\n00 00 00 00 00 00 00 00 00 00 00 00 80 00 00 00 00 00 00 00 00 00 00 00");
    
    
    scrollPane_1.setViewportView(textArea_1);
    
    
    scrollPane.setViewportView(textArea);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
    frmRhythmMatrix.getContentPane().setLayout(groupLayout);
  }
}
