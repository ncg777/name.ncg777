package name.ncg777.maths.numbers.quartal.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.computing.structures.CollectionUtils;
import name.ncg777.maths.Matrix;
import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.numbers.predicates.EntropicDispersion;
import name.ncg777.maths.numbers.predicates.Even;
import name.ncg777.maths.numbers.predicates.LowEntropy;
import name.ncg777.maths.numbers.predicates.Oddity;
import name.ncg777.maths.numbers.predicates.RelativelyFlat;
import name.ncg777.maths.numbers.predicates.ShadowContourIsomorphic;
import name.ncg777.maths.numbers.quartal.QuartalNumber;
import name.ncg777.maths.numbers.quartal.QuartalNumbersSequence;
import name.ncg777.maths.numbers.relations.PredicatedDifferences;
import name.ncg777.maths.numbers.relations.PredicatedJuxtaposition;
import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.numbers.predicates.Ordinal;
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

public class MatrixGenerator {

  private JFrame frmQuartalWordMatrixGenerator;
  private JTextArea textArea_1 = new JTextArea();
  private JTextArea textArea = new JTextArea();
  private JComboBox<Cipher.Name> comboBox = new JComboBox<Cipher.Name>(new DefaultComboBoxModel<Cipher.Name>(Cipher.Name.values()));
  private JTextField textFilterModes;
  private JTextField textDiffFilterModes;

  private static TreeMap<Cipher.Name, TreeSet<QuartalNumber>> sets = new TreeMap<>();
  
  static {
    for(var n : Cipher.Name.values()) {
      sets.put(n, QuartalNumber.generate((Cipher.Name)n));
    }
  }
  
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          MatrixGenerator window = new MatrixGenerator();
          window.frmQuartalWordMatrixGenerator.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public MatrixGenerator() {
    initialize();
  }
  
  private double[] adjustWeights(List<BinaryNatural> row, ArrayList<BinaryNatural> possibles, int j, Double[] weights) {
    double[] o = new double[possibles.size()];
    for(int i=0;i<possibles.size();i++) {
      int count = 0;
      BinaryNatural r = possibles.get(i);
      for(int k=0;k<j;k++) {
        if(row.get(k).equals(r)) count++;
      }
      
      o[i] = weights[i]/(double)(count+1);
    }
    return o;
  }
  private void updateFixed() {
    if(comboBox.getSelectedItem() == Cipher.Name.Binary) textArea_1.setText("10 10");
    if(comboBox.getSelectedItem() == Cipher.Name.Hexadecimal) textArea_1.setText("80 80");
    if(comboBox.getSelectedItem() == Cipher.Name.Octal) textArea_1.setText("40 40");
  }
  private boolean running = false;

  private void initialize() {
    frmQuartalWordMatrixGenerator = new JFrame();
    frmQuartalWordMatrixGenerator.setResizable(false);
    frmQuartalWordMatrixGenerator.setTitle("QuartalNumber Matrix");
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
            var abc = (Cipher.Name)comboBox.getSelectedItem();
            int ordinal_n = -1;
            if(abc.equals(Cipher.Name.Binary)) ordinal_n=4; 
            if(abc.equals(Cipher.Name.Hexadecimal)) ordinal_n=16;
            if(abc.equals(Cipher.Name.Octal)) ordinal_n=12;
            
            ArrayList<Predicate<BinaryNatural>> filterModes = new ArrayList<>();
            filterModes.add((a) -> true);
            filterModes.add(new ShadowContourIsomorphic());
            filterModes.add(new Oddity());
            filterModes.add(new EntropicDispersion());
            filterModes.add(new LowEntropy());
            filterModes.add(new Even());
            filterModes.add(new RelativelyFlat());
            filterModes.add(new Ordinal(ordinal_n));
            ArrayList<BiPredicate<BinaryNatural, BinaryNatural>> diffModes = new ArrayList<>();
            for(var predicate : filterModes) diffModes.add(new PredicatedDifferences(predicate));
            
            try {
              btnGenerate.setText("Cancel");
              running = true;
              
              String[] strFixed = textArea_1.getText().split("\n+");
              var _str = textFilterModes.getText();
              
              Predicate<BinaryNatural> pred = Sequence.parse(_str)
                    .stream().map(
                        (i) -> filterModes.get(i-1))
                          .reduce((r) -> true, (a,b) -> a.and(b));
              
              BiPredicate<BinaryNatural, BinaryNatural> relHoriz = new PredicatedJuxtaposition(pred);
              
              BiPredicate<BinaryNatural, BinaryNatural> relSimul = Sequence.parse(
                  textDiffFilterModes.getText()).stream().map(
                      (i) -> diffModes.get(i-1)).reduce(
                          (a, b) -> true, (a,b) -> a.and(b));
              
              TreeSet<BinaryNatural> t = new TreeSet<BinaryNatural>();
              TreeSet<BinaryNatural> t0 = new TreeSet<BinaryNatural>();
             
              for(QuartalNumber r : sets.get(comboBox.getSelectedItem())) t0.add(r.toBinaryWord());
              
              for(BinaryNatural r : t0){
                if(pred.test(r)) {
                  t.add(r);
                }
              }
              
              int fixedSize = 0;
              int n = (int)spinner_1.getValue();
              int m = (int)spinner.getValue();
              
              Matrix<BinaryNatural> output = new Matrix<>(m,n);
              
              for(int i=0;i<strFixed.length;i++) {
                if(strFixed[i].trim().length() == 0) continue;
                
                output.insertRow(fixedSize);
                QuartalNumbersSequence r = new QuartalNumbersSequence(
                    abc, 
                    strFixed[i].trim());
                for(int j=0;j<n;j++) {
                  output.set(i, n-j-1, 
                      r.get((n-j)%r.size()).toBinaryWord()
                  );
                }
                
                fixedSize++;
              }
              m += fixedSize;
              var possibles = new Function<String, ArrayList<BinaryNatural>>() {
                @Override  
                public ArrayList<BinaryNatural> apply(String str) {
                  BinaryNatural r = (BinaryNatural.build(str));
                  
                  ArrayList<BinaryNatural> p = new ArrayList<>();
                  
                  for(BinaryNatural s : t) {
                    if(relHoriz.test(r, s)) { 
                        p.add(s); 
                     }
                  }
                  
                  return p;
                };
              };
              
              BiFunction<BinaryNatural, List<BinaryNatural>, Double[]> calcWeights =
                  (BinaryNatural r, List<BinaryNatural> p) -> {
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
                    ArrayList<BinaryNatural> p = null;
                    if(j>0) {
                      p = possibles.apply(output.get(i, j-1).toString());
                      if(p.size() == 0) {
                        i=fixedSize;
                        j=0;
                        continue;
                      }
                    }
                   
                    
                    
                    BinaryNatural r = null;
                    
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

                  Matrix<QuartalNumber> tmpMat = new Matrix<QuartalNumber>(m,n);
                  Matrix<QuartalNumber> tmpMatNot = new Matrix<QuartalNumber>(m,n);
                  for(int i=0;i<m;i++) {
                    for(int j=0;j<n;j++) {
                      tmpMat.set(i, j, new QuartalNumber(output.get(i, j).toNatural(abc)));
                      tmpMatNot.set(i, j, 
                          new QuartalNumber(output.get(i, j).invert().toNatural(abc))
                      );
                    }
                  }
                  textArea.setText(tmpMat.toString() + "\nNOT:\n" + tmpMatNot.toString());
              }
            } catch(Exception x) {
              x.printStackTrace(System.out);
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
    lblMode.setToolTipText("<html><ol><li>Bypass</li><li>ShadowContourIsomorphic</li><li>Oddity</li><li>Entropic dispersion</li><li>Low entropy</li><li>Even</li><li>RelativelyFlat</li><li>Ordinal</li></ol></html>");
    lblMode.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
    
    JLabel lblDiffs = new JLabel("Diffs:");
    lblDiffs.setToolTipText("<html><ol><li>Bypass</li><li>ShadowContourIsomorphic</li><li>Oddity</li><li>Entropic dispersion</li><li>Low entropy</li><li>Even</li><li>RelativelyFlat</li><li>Ordinal</li></ol></html>");
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
