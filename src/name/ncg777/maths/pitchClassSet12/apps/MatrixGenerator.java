package name.ncg777.maths.pitchClassSet12.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import name.ncg777.computing.structures.CollectionUtils;
import name.ncg777.maths.Matrix;
import name.ncg777.maths.graphs.MarkableDirectedGraph;
import name.ncg777.maths.pitchClassSet12.PitchClassSet12;
import name.ncg777.maths.pitchClassSet12.predicates.Consonant;
import name.ncg777.maths.pitchClassSet12.predicates.SubsetOf;
import name.ncg777.maths.pitchClassSet12.relations.CloseIVs;
import name.ncg777.maths.pitchClassSet12.relations.CommonNotesAtLeast;
import name.ncg777.maths.pitchClassSet12.relations.Different;
import name.ncg777.maths.pitchClassSet12.relations.IVEQRotOrRev;
import name.ncg777.maths.pitchClassSet12.relations.PredicatedUnion;
import name.ncg777.maths.relations.Relation;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;

public class MatrixGenerator {

  private JFrame frmChordMatrix;
  
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          MatrixGenerator window = new MatrixGenerator();
          window.frmChordMatrix.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
  private int getN() {return (int)spinner_1.getValue();}
  private int getM() {return (int)spinner.getValue();}


  public MatrixGenerator() {
    initialize();
   
  }
  private Comparator<String> comparator = PitchClassSet12.ForteStringComparator.reversed();
  JComboBox<String> comboFilter = new JComboBox<String>();
  JTextArea textArea; 
  JSpinner spinner_k = new JSpinner(new SpinnerNumberModel(3, 2, 11, 1));
  JSpinner spinner_1 = new JSpinner(new SpinnerNumberModel(1, 1, null, 1));
  JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, null, 1));
  private boolean running = false;
  private void initialize() {
    frmChordMatrix = new JFrame();
    frmChordMatrix.setTitle("PitchClassSet12 Matrix");
    frmChordMatrix.setBounds(100, 100, 549, 300);
    frmChordMatrix.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    
    JLabel lblRows = new JLabel("Rows:");
    
    JLabel lblColumns = new JLabel("Columns:");
    
    JButton btnGenerate = new JButton("Generate");
    btnGenerate.addActionListener(new ActionListener() {
      
      public void actionPerformed(ActionEvent e) {
        if(running) {running = false; return;}
        new Thread(new Runnable() {

          @Override
          public void run() {
            try {
              btnGenerate.setText("Cancel");
              textArea.setText("Searching...");
              running = true;
              TreeSet<PitchClassSet12> t0 = PitchClassSet12.getChords();
              TreeSet<PitchClassSet12> t = new TreeSet<PitchClassSet12>();
              PitchClassSet12 scale = PitchClassSet12.parseForte(comboFilter.getSelectedItem().toString());
              Predicate<PitchClassSet12> pred = Predicates.and(new SubsetOf(scale),new Consonant());
              
              for(PitchClassSet12 r : t0){
                if(pred.apply(r) && r.getK() == (int)spinner_k.getValue()) {
                  t.add(r);
                }
              }
              
              int m = getM();
              int n = getN();

              Matrix<PitchClassSet12> output = new Matrix<>(m,n);
             
              Relation<PitchClassSet12, PitchClassSet12> rel_horiz = Relation.and(new Different(), Relation.and(Relation.or(new CloseIVs(), new IVEQRotOrRev()), new CommonNotesAtLeast(1)));
              BiPredicate<PitchClassSet12, PitchClassSet12> rel_vert = new PredicatedUnion(new Consonant());
              MarkableDirectedGraph<PitchClassSet12> d = new MarkableDirectedGraph<>(t, rel_horiz);
              
              Function<PitchClassSet12, List<PitchClassSet12>> possibles = new Function<PitchClassSet12, List<PitchClassSet12>>() {

                @Override
                public List<PitchClassSet12> apply(PitchClassSet12 x) {
                  return new ArrayList<>(d.getSuccessors(x));
                }
                
              };
              while(true) {
                int failures = 0;
                boolean successful = false;
                outside:
                for(int i=0;i<m;i++) {
                  for(int j=0;j<n;j++) {
                    while(true) {
                      if(!running) {btnGenerate.setText("Generate"); textArea.setText(""); return;}
                      if(j==0) {
                        output.set(i, j, CollectionUtils.chooseAtRandom(t));
                      } else {
                        List<PitchClassSet12> p = possibles.apply(output.get(i, j-1));
                        if(p.size() == 0 ) {
                            failures = 0;
                            output.clear();
                            i=0;
                            j=0;
                            break outside;
                        }
                        
                        double[] w = new double[p.size()];
                        int ii=0;
                        for(var c : p) {
                          w[ii++]=1.0/(output.get(i, j-1).intersect(c).getN()+1);
                        }
                        
                        
                        output.set(i, j, CollectionUtils.chooseAtRandomWithWeights(p,w));
                      }
                      
                      if(j > 0) {
                        if(!rel_horiz.apply(output.get(i, j-1), output.get(i, j))){
                          failures++;
                          if(failures > m*n*1000) {
                            failures = 0;
                            output.clear();
                            i=0;
                            j=0;
                            break outside;
                          }
                          continue;
                        }
                      }
                      
                      if(i > 0) {
                        boolean failed = false;
                        
                        for(int k=i-1;k>=0;k--) {
                          if(!rel_vert.test(output.get(i, j), output.get(k, j))) {
                            failed = true;
                            break;
                          }
                        }
                        if(failed) {
                          continue;
                        }
                      }
                      break;
                    }
                    
                    if(i == m-1 && j == n-1) successful = true;
                  }
                }
                if(successful) break;
              }
              
              String str_output = output.toString((c) -> c.toForteNumberString());
              
              Matrix<PitchClassSet12> complements = new Matrix<PitchClassSet12>(m,n);
              
              for(int i=0;i<m;i++) {
                for(int j=0;j<n;j++) {
                  complements.set(i, j, scale.minus(output.get(i, j)));
                }
              }
              
              str_output += "\n\n" + complements.toString((c) -> c.toForteNumberString());
              
              textArea.setText(str_output);
            } catch(Exception ex) {textArea.setText(ex.getMessage());}
            running = false;
            btnGenerate.setText("Generate");
          }}).start();
      }
    });
    
    
    comboFilter.setEditable(true);
    // jazz 06-56.03
    // major locrian 07-26.04
    // persian 07-28.11
    // harmin 07-38.11
    // melmin 07-39.11
    // harm maj 07-42.11
    // maj 07-43.11
    // enigmatic 07-65.04
    // hung 07-29.06
    // enigmatic 07-65.04
    // oct 08-35.00
    String[] cs = PitchClassSet12.getForteChordDict().keySet().toArray(new String[0]);
    List<String> cs0 = new ArrayList<String>();
    for(var s : cs) cs0.add(s);
    cs0.sort(comparator);
    cs = cs0.toArray(new String[0]);
    comboFilter.setModel(new DefaultComboBoxModel<String>(cs));
    comboFilter.setSelectedIndex(Arrays.asList(cs).indexOf("8-23.11"));
    
    JScrollPane scrollPane = new JScrollPane();
    
    
    
    GroupLayout groupLayout = new GroupLayout(frmChordMatrix.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(13)
              .addComponent(lblRows)
              .addGap(6)
              .addComponent(spinner, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.UNRELATED)
              .addComponent(lblColumns)
              .addGap(6)
              .addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
              .addGap(12)
              .addComponent(comboFilter, 0, 110, Short.MAX_VALUE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner_k, GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(btnGenerate, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE))
            .addGroup(groupLayout.createSequentialGroup()
              .addContainerGap()
              .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addContainerGap()
              .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                .addComponent(lblRows)
                .addComponent(lblColumns)
                .addComponent(btnGenerate)
                .addComponent(spinner_k, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(15)
              .addComponent(comboFilter, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
          .addContainerGap())
    );
    
    textArea = new JTextArea();
    scrollPane.setViewportView(textArea);
    textArea.setFont(new Font("Courier New", Font.PLAIN, 13));
    frmChordMatrix.getContentPane().setLayout(groupLayout);
  }
}
