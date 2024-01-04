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

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import name.NicolasCoutureGrenier.Maths.Numbers;
import name.NicolasCoutureGrenier.Maths.DataStructures.CollectionUtils;
import name.NicolasCoutureGrenier.Maths.DataStructures.Matrix;
import name.NicolasCoutureGrenier.Maths.Graphs.DiGraph;
import name.NicolasCoutureGrenier.Maths.Relations.Relation;
import name.NicolasCoutureGrenier.Music.PCS12;
import name.NicolasCoutureGrenier.Music.PCS12Predicates.Consonant;
import name.NicolasCoutureGrenier.Music.PCS12Predicates.SubsetOf;
import name.NicolasCoutureGrenier.Music.PCS12Relations.CloseIVs;
import name.NicolasCoutureGrenier.Music.PCS12Relations.CommonNotesAtLeast;
import name.NicolasCoutureGrenier.Music.PCS12Relations.Different;
import name.NicolasCoutureGrenier.Music.PCS12Relations.IVEQRotOrRev;
import name.NicolasCoutureGrenier.Music.PCS12Relations.PredicatedDifferences;
import name.NicolasCoutureGrenier.Music.PCS12Relations.PredicatedUnion;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;

public class ChordMatrix {

  private JFrame frmChordMatrix;
  
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          ChordMatrix window = new ChordMatrix();
          window.frmChordMatrix.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
  private int getN() {return (int)spinner_1.getValue();}
  private int getM() {return (int)spinner.getValue();}


  public ChordMatrix() {
    initialize();
   
  }
  
  JComboBox<String> comboFilter = new JComboBox<String>();
  JTextArea textArea; 
  JSpinner spinner_k = new JSpinner(new SpinnerNumberModel(4, 2, 11, 1));
  JSpinner spinner_1 = new JSpinner(new SpinnerNumberModel(1, 1, null, 1));
  JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, null, 1));
  private boolean running = false;
  private void initialize() {
    frmChordMatrix = new JFrame();
    frmChordMatrix.setTitle("PCS12 Matrix");
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
              TreeSet<PCS12> t0 = PCS12.getChords();
              TreeSet<PCS12> t = new TreeSet<PCS12>();
              PCS12 scale = PCS12.parse(comboFilter.getSelectedItem().toString());
              Predicate<PCS12> pred = Predicates.and(new SubsetOf(scale),new Consonant());
              
              for(PCS12 r : t0){
                if(pred.apply(r) && r.getK() == (int)spinner_k.getValue()) {
                  t.add(r);
                }
              }
              
              int m = getM();
              int n = getN();

              Matrix<PCS12> output = new Matrix<>(m,n);
             
              Relation<PCS12, PCS12> rel_horiz = Relation.and(new Different(), Relation.and(new CloseIVs(), new CommonNotesAtLeast(1)));
              Relation<PCS12, PCS12> rel_vert = new PredicatedUnion(new Consonant());
              DiGraph<PCS12> d = new DiGraph<>(t, rel_horiz);
              
              Function<PCS12, List<PCS12>> possibles = new Function<PCS12, List<PCS12>>() {

                @Override
                public List<PCS12> apply(PCS12 x) {
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
                        List<PCS12> p = possibles.apply(output.get(i, j-1));
                        if(p.size() == 0 ) {
                            failures = 0;
                            output.clear();
                            i=0;
                            j=0;
                            break outside;
                        }
                        output.set(i, j, CollectionUtils.chooseAtRandom(p));
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
                          if(!rel_vert.apply(output.get(i, j), output.get(k, j))) {
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
              
              String str_output = output.toString();
              
              Matrix<PCS12> complements = new Matrix<PCS12>(m,n);
              
              for(int i=0;i<m;i++) {
                for(int j=0;j<n;j++) {
                  complements.set(i, j, scale.minus(output.get(i, j)));
                }
              }
              
              str_output += "\n\n" + complements.toString();
              
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
    String[] cs = PCS12.getChordDict().keySet().toArray(new String[0]);
    Arrays.sort(cs);
    comboFilter.setModel(new DefaultComboBoxModel<String>(cs));
    comboFilter.setSelectedIndex(Arrays.asList(cs).indexOf("07-43.11"));
    
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
