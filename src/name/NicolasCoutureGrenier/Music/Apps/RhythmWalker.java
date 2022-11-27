package name.NicolasCoutureGrenier.Music.Apps;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import com.google.common.base.Joiner;

import name.NicolasCoutureGrenier.Maths.DataStructures.CollectionUtils;
import name.NicolasCoutureGrenier.Maths.Graphs.DiGraph;
import name.NicolasCoutureGrenier.Music.Rhythm;
import name.NicolasCoutureGrenier.Music.Rhythm16;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.ShadowContourIsomorphic;
import name.NicolasCoutureGrenier.Music.RhythmRelations.PredicatedJuxtaposition;

import java.awt.Font;


public class RhythmWalker {

  private JFrame frmRhythmPleasure;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          RhythmWalker window = new RhythmWalker();
          window.frmRhythmPleasure.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
  JTextArea txtrResult = new JTextArea();
  DiGraph<Rhythm> d;
  /**
   * Create the application.
   */
  public RhythmWalker() {
    initialize();
  }
  JComboBox<String> comboBox; 
  JSpinner spinner = new JSpinner();
  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmRhythmPleasure = new JFrame();
    frmRhythmPleasure.setTitle("Rhythm Walker");
    frmRhythmPleasure.getContentPane().setBackground(Color.DARK_GRAY);
    frmRhythmPleasure.setBounds(100, 100, 450, 300);
    frmRhythmPleasure.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    TreeSet<Rhythm> t = new TreeSet<Rhythm>(); t.addAll(Rhythm16.Generate());
    CollectionUtils.filter(t,new ShadowContourIsomorphic());
    d = new DiGraph<Rhythm>(t,new PredicatedJuxtaposition(new ShadowContourIsomorphic()));
    List<String> s0 =new ArrayList<String>();
    for(Rhythm x : t){if(d.getSuccessorCount(x)>0){s0.add(x.toString());}}
    s0.sort(null);
    Collections.reverse(s0);
    String[] s = new String[s0.size()];
    s0.toArray(s);
    comboBox = new JComboBox<String>(new DefaultComboBoxModel<String>(s));
    comboBox.setForeground(Color.WHITE);
    comboBox.setBackground(Color.BLACK);
    
   
    
    JButton btnPouf = new JButton("Walk");
    btnPouf.setForeground(Color.YELLOW);
    btnPouf.setBackground(Color.BLACK);
    btnPouf.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String s = (String) comboBox.getSelectedItem();
        int n = (int) spinner.getValue();
        ArrayList<String> o = new ArrayList<String>(); o.add(s);
        
        while(n>1){
          o.add(CollectionUtils.chooseAtRandom(d.getNeighbors(Rhythm16.parseRhythm16Hex(o.get(o.size()-1)))).toString());
          n--;
        }
        
        txtrResult.setText(Joiner.on(" ").join(o));
        
      }
    });
    GroupLayout groupLayout = new GroupLayout(frmRhythmPleasure.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.TRAILING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(txtrResult, GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
              .addGap(7))
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(btnPouf)
              .addContainerGap(175, Short.MAX_VALUE))))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(btnPouf)
            .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(txtrResult, GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
          .addGap(9))
    );
    txtrResult.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
    txtrResult.setLineWrap(true);
    txtrResult.setEditable(false);
    txtrResult.setBackground(Color.BLACK);
    txtrResult.setForeground(Color.WHITE);
    spinner.setBackground(Color.BLACK);
    spinner.setForeground(Color.WHITE);
    spinner.setModel(new SpinnerNumberModel(2, 2, null, 1));
    frmRhythmPleasure.getContentPane().setLayout(groupLayout);
  }
}
