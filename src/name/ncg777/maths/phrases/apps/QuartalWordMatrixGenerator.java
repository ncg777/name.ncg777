package name.ncg777.maths.phrases.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.computing.structures.CollectionUtils;
import name.ncg777.maths.Matrix;
import name.ncg777.maths.phrases.QuartalWordsPhrase;
import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.words.QuartalWord;
import name.ncg777.maths.words.Alphabet;
import name.ncg777.maths.words.BinaryWord;
import name.ncg777.maths.words.predicates.EntropicDispersion;
import name.ncg777.maths.words.predicates.Even;
import name.ncg777.maths.words.predicates.LowEntropy;
import name.ncg777.maths.words.predicates.Oddity;
import name.ncg777.maths.words.predicates.RelativelyFlat;
import name.ncg777.maths.words.predicates.ShadowContourIsomorphic;
import name.ncg777.maths.words.relations.PredicatedDifferences;
import name.ncg777.maths.words.relations.PredicatedJuxtaposition;

import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.awt.event.ActionEvent;
import java.awt.Font;


import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;

public class QuartalWordMatrixGenerator {

  private JFrame frmQuartalWordMatrixGenerator;
  private JTextArea textArea_1 = new JTextArea();
  private JTextArea textArea = new JTextArea();
  private JComboBox<Alphabet.Name> comboBox = new JComboBox<Alphabet.Name>(new DefaultComboBoxModel<Alphabet.Name>(Alphabet.Name.values()));
  private JTextField textFilterModes;
  private JTextField textDiffFilterModes;

  private static TreeMap<Alphabet.Name, TreeSet<QuartalWord>> sets = new TreeMap<>();
  
  static {
    for(var n : Alphabet.Name.values()) {
      sets.put(n, QuartalWord.generate((Alphabet.Name)n));
    }
  }
  
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          QuartalWordMatrixGenerator window = new QuartalWordMatrixGenerator();
          window.frmQuartalWordMatrixGenerator.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public QuartalWordMatrixGenerator() {
    initialize();
  }
  
  private double[] adjustWeights(List<BinaryWord> row, ArrayList<BinaryWord> possibles, int j, Double[] weights) {
    double[] o = new double[possibles.size()];
    for(int i=0;i<possibles.size();i++) {
      int count = 0;
      BinaryWord r = possibles.get(i);
      for(int k=0;k<j;k++) {
        if(row.get(k).equals(r)) count++;
      }
      
      o[i] = weights[i]/(double)(count+1);
    }
    return o;
  }
  private void updateFixed() {
    if(comboBox.getSelectedItem() == Alphabet.Name.Binary) textArea_1.setText("10 10");
    if(comboBox.getSelectedItem() == Alphabet.Name.Hexadecimal) textArea_1.setText("80 80");
    if(comboBox.getSelectedItem() == Alphabet.Name.Octal) textArea_1.setText("40 40");
  }
  private boolean running = false;

  private void initialize() {
    frmQuartalWordMatrixGenerator = new JFrame();
    frmQuartalWordMatrixGenerator.setResizable(false);
    frmQuartalWordMatrixGenerator.setTitle("QuartalWord Matrix");
    frmQuartalWordMatrixGenerator.setBounds(100, 100, 711, 541);
    frmQuartalWordMatrixGenerator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
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
        if(running) {running = false; return;}
        new Thread(new Runnable() {

          @Override
          public void run() {
            var abc = (Alphabet.Name)comboBox.getSelectedItem();
            
            ArrayList<Predicate<BinaryWord>> filterModes = new ArrayList<>();
            filterModes.add((a) -> true);
            filterModes.add(new ShadowContourIsomorphic());
            filterModes.add(new Oddity());
            filterModes.add(new EntropicDispersion());
            filterModes.add(new LowEntropy());
            filterModes.add(new Even());
            filterModes.add(new RelativelyFlat());
           
            ArrayList<BiPredicate<BinaryWord, BinaryWord>> diffModes = new ArrayList<>();
            for(var predicate : filterModes) diffModes.add(new PredicatedDifferences(predicate));
            
            try {
              btnGenerate.setText("Cancel");
              running = true;
              
              String[] strFixed = textArea_1.getText().split("\n+");
              var _str = textFilterModes.getText();
              
              Predicate<BinaryWord> pred = Sequence.parse(_str)
                    .stream().map(
                        (i) -> filterModes.get(i-1))
                          .reduce((r) -> true, (a,b) -> a.and(b));
              
              BiPredicate<BinaryWord, BinaryWord> relHoriz = new PredicatedJuxtaposition(pred);
              
              BiPredicate<BinaryWord, BinaryWord> relSimul = Sequence.parse(
                  textDiffFilterModes.getText()).stream().map(
                      (i) -> diffModes.get(i-1)).reduce(
                          (a, b) -> true, (a,b) -> a.and(b));
              
              TreeSet<BinaryWord> t = new TreeSet<BinaryWord>();
              TreeSet<BinaryWord> t0 = new TreeSet<BinaryWord>();
             
              for(QuartalWord r : sets.get(comboBox.getSelectedItem())) t0.add(r.toBinaryWord());
              
              for(BinaryWord r : t0){
                if(pred.test(r)) {
                  t.add(r);
                }
              }
              
              int fixedSize = 0;
              int n = (int)spinner_1.getValue();
              int m = (int)spinner.getValue();
              
              Matrix<BinaryWord> output = new Matrix<>(m,n);
              
              for(int i=0;i<strFixed.length;i++) {
                if(strFixed[i].trim().length() == 0) continue;
                
                output.insertRow(fixedSize);
                QuartalWordsPhrase r = new QuartalWordsPhrase(
                    abc, 
                    strFixed[i].trim());
                for(int j=0;j<n;j++) {
                  output.set(i, j, 
                      r.get(j%r.size()).toBinaryWord()
                  );
                }
                
                fixedSize++;
              }
              m += fixedSize;
              var possibles = new Function<String, ArrayList<BinaryWord>>() {
                @Override  
                public ArrayList<BinaryWord> apply(String str) {
                  BinaryWord r = (BinaryWord.build(str));
                  
                  ArrayList<BinaryWord> p = new ArrayList<>();
                  
                  for(BinaryWord s : t) {
                    if(relHoriz.test(r, s)) { 
                        p.add(s); 
                     }
                  }
                  
                  return p;
                };
              };
              
              BiFunction<BinaryWord, List<BinaryWord>, Double[]> calcWeights =
                  (BinaryWord r, List<BinaryWord> p) -> {
                    Double weights[] = new Double[p.size()];
  
                    for (int i = 0; i < p.size(); i++) {
                      weights[i] = 1.0 - Math.sqrt(r.calcNormalizedDistanceWith(p.get(i)));
                      if(r.equals(p.get(i))) weights[i]=weights[i]/8;
                    }
                    return weights;
                  };
              
              
              //int maxFailures = n*m*1000;
              //int failures = 0;

              for(int i=fixedSize;i<m;i++) {
                outside:
                for(int j=0;j<n;j++) {
                  while(true) {
                    if(!running) {
                      break outside;
                    }
                    //failures++;
                    ArrayList<BinaryWord> p = null;
                    if(j>0) {
                      p = possibles.apply(output.get(i, j-1).toString());
                    }
                    
                    
                    BinaryWord r = null;
                    
                    if(j>0) {
                      r = CollectionUtils.chooseAtRandomWithWeights(p, 
                          adjustWeights(output.getRow(i), p, j, 
                              calcWeights.apply(output.get(i, j-1), p)));  
                    } else {
                      r = CollectionUtils.chooseAtRandom(t); 
                    }
                    
                    output.set(i, j, r);
                    
                    if(i > 0) {
                      boolean failed = false;
                      
                      for(int k=i-1;k>=0;k--) {
                        if(!relSimul.test(output.get(i, j), output.get(k, j))) {
                          failed = true;
                          //failures++;
                          break;
                        }
                        textArea.setText("i=" + i + ", j=" + j + " ...\n");
                      }
                      
                      //if(failures > maxFailures || (t.size() == 0 && comboBox.getSelectedItem() != Rn.Tribble)||(p!=null && p.size() == 0)) {
                      //  failures = 0;
                      //  i = fixedSize;
                      //  j = 0;
                     //   break outside;
                      //}
                      
                      if(failed) {
                        continue;
                      }
                    }
                    break;
                  }

                }
              }
              
              if(running) {

                  Matrix<QuartalWord> tmpMat = new Matrix<QuartalWord>(m,n);
                  Matrix<QuartalWord> tmpMatNot = new Matrix<QuartalWord>(m,n);
                  for(int i=0;i<m;i++) {
                    for(int j=0;j<n;j++) {
                      tmpMat.set(i, j, new QuartalWord(output.get(i, j).toWord(abc)));
                      tmpMatNot.set(i, j, 
                          new QuartalWord(output.get(i, j).invert().toWord(abc))
                      );
                    }
                  }
                  textArea.setText(tmpMat.toString() + "\nNOT:\n" + tmpMatNot.toString());
              }
            } catch(Exception x) {
              textArea.setText("Error");
            }
            if(!running) {
              textArea.setText("");
            }
            btnGenerate.setText("Generate");
            running = false;
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
    lblMode.setToolTipText("<html><ol><li>Bypass</li><li>ShadowContourIsomorphic</li><li>Oddity</li><li>Entropic dispersion</li><li>Low entropy</li><li>Even</li><li>RelativelyFlat</li></ol></html>");
    lblMode.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
    
    JLabel lblDiffs = new JLabel("Diffs:");
    lblDiffs.setToolTipText("<html><ol><li>Bypass</li><li>ShadowContourIsomorphic</li><li>Oddity</li><li>Entropic dispersion</li><li>Low entropy</li><li>Even</li><li>RelativelyFlat</li></ol></html>");
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
    
    GroupLayout groupLayout = new GroupLayout(frmQuartalWordMatrixGenerator.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.TRAILING)
        .addGroup(groupLayout.createSequentialGroup()
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
            .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
              .addContainerGap()
              .addComponent(scrollPane))
            .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
              .addContainerGap()
              .addComponent(lblNewLabel_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
              .addContainerGap()
              .addComponent(scrollPane_1))
            .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
              .addContainerGap()
              .addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
              .addContainerGap()
              .addComponent(btnGenerate, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
              .addPreferredGap(ComponentPlacement.UNRELATED)
              .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)))
          .addGap(149))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
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
          .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
          .addGap(9))
    );
    
    updateFixed();
    
    
    scrollPane_1.setViewportView(textArea_1);
    
    
    scrollPane.setViewportView(textArea);
    textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
    frmQuartalWordMatrixGenerator.getContentPane().setLayout(groupLayout);
  }
}
