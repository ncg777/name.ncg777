package name.NicolasCoutureGrenier.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence;
import name.NicolasCoutureGrenier.Maths.Enumerations.BitSetEnumeration;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.BitSet;
import java.awt.event.ActionEvent;

public class PseudoBase {

  private JFrame frmPseudoBase;
  private JTextField textSequence;
  private JTextField txtResult;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          PseudoBase window = new PseudoBase();
          window.frmPseudoBase.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public PseudoBase() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmPseudoBase = new JFrame();
    frmPseudoBase.setTitle("Pseudo Base");
    frmPseudoBase.setBounds(100, 100, 450, 143);
    frmPseudoBase.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblSequence = new JLabel("Sequence :");
    
    textSequence = new JTextField();
    textSequence.setColumns(10);
    txtResult = new JTextField();
    txtResult.setColumns(10);
    JButton btnGenerate = new JButton("Generate");
    btnGenerate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Sequence s = Sequence.parse(textSequence.getText());
        int n = s.size();
        
        BitSetEnumeration en = new BitSetEnumeration(n);
        Sequence o = new Sequence();
        while(en.hasMoreElements()) {
          BitSet b = en.nextElement();
          int sum = 0;
          for(int i=0;i<n;i++) {
            if(b.get(i)) {sum += s.get(i);}
          }
          o.add(sum);
        }
        txtResult.setText(o.toString());
      }
    });
    
    
    
    JLabel lblResult = new JLabel("Result :");
    GroupLayout groupLayout = new GroupLayout(frmPseudoBase.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addComponent(lblSequence)
            .addComponent(lblResult))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(btnGenerate, GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
            .addComponent(textSequence, GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
            .addComponent(txtResult, GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblSequence)
            .addComponent(textSequence, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnGenerate)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(txtResult, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblResult))
          .addContainerGap(175, Short.MAX_VALUE))
    );
    frmPseudoBase.getContentPane().setLayout(groupLayout);
  }
}
