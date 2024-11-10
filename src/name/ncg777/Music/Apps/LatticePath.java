package name.ncg777.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.Maths.Objects.Composition;
import name.ncg777.Maths.Objects.Sequence;
import name.ncg777.Music.R16List;
import name.ncg777.Music.Rhythm;

import javax.swing.JButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTextField;
import java.awt.Font;

public class LatticePath {

  private JFrame frmLatticePath;
  private JTextField txtPermutation;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          LatticePath window = new LatticePath();
          window.frmLatticePath.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public LatticePath() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmLatticePath = new JFrame();
    frmLatticePath.setTitle("Lattice Path");
    frmLatticePath.setBounds(100, 100, 573, 260);
    frmLatticePath.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JScrollPane scrollPane = new JScrollPane();
    final JSpinner spinner_total = new JSpinner();
    spinner_total.setModel(new SpinnerNumberModel(16, 1, null, 1));
    final JTextPane textPane = new JTextPane();
    textPane.setFont(new Font("Unifont", Font.PLAIN, 12));
    JButton btnGenerate = new JButton("Generate");
    btnGenerate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        int total = (Integer)spinner_total.getValue();
        Sequence s = new Sequence();
        String[] str = txtPermutation.getText().trim().split("\\s+");;
        for(String t : str){s.add(Integer.parseInt(t));}
        
        String o = "";
        Composition c = new Composition(total);
        
        boolean r16 = false;
        
        for(int i=0;i<s.size();i++){
          int m = s.get(i);
          c.set(m-1);
          if((total % 16) == 0) {
            r16 = true;
            o += R16List.fromRhythm(Rhythm.buildRhythm(c.asCombination(), total)).toString() + " ";
          }
          o+= Integer.valueOf(i+2).toString() + " : ";
          for(Integer j : c.asSequence()){o+=j.toString() + " ";}
          o+="\n";
          
        }
        
        int npad = ((total/16)) - 1;
        String pad = "80 00";
        for(int i=0;i<npad;i++) {pad += " 00 00";}
        pad += " ";
        textPane.setText((r16?pad:"") + "1 : " + Integer.valueOf(total).toString()+ "\n" + o);
      }
    });
    

    JLabel lblStep = new JLabel("Permutation [1, Total-1]:");
    
    JLabel lblTotal = new JLabel("Total");
    
    txtPermutation = new JTextField();
    txtPermutation.setColumns(10);
    GroupLayout groupLayout = new GroupLayout(frmLatticePath.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
              .addGap(13))
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(btnGenerate)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(lblStep)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(txtPermutation, GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(lblTotal)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner_total, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
              .addGap(24))))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(spinner_total, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(btnGenerate)
            .addComponent(lblTotal)
            .addComponent(txtPermutation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblStep))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
          .addContainerGap())
    );
    
    
    scrollPane.setViewportView(textPane);
    frmLatticePath.getContentPane().setLayout(groupLayout);
  }
}
