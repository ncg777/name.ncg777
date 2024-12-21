package name.ncg777.maths.apps;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.maths.enumerations.CombinationEnumeration;
import name.ncg777.maths.enumerations.KPermutationEnumeration;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JCheckBox;

public class Permutator {

  private JFrame frmPermutator;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Permutator window = new Permutator();
          window.frmPermutator.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public Permutator() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmPermutator = new JFrame();
    frmPermutator.setTitle("WordPermutator/Combinator");
    frmPermutator.setBounds(100, 100, 427, 255);
    frmPermutator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JScrollPane scrollPane = new JScrollPane();
    
    JLabel lblWriteEachItem = new JLabel("Write each item on a separate line");
    
    final JSpinner spinner = new JSpinner();
    spinner.setModel(new SpinnerNumberModel(2, 1, null, 1));
    
    JLabel lblPick = new JLabel("Pick :");
    
    JScrollPane scrollPane_1 = new JScrollPane();
    final JTextArea textArea_1 = new JTextArea();
    final JTextArea textArea = new JTextArea();
    final JCheckBox chckbxCombination = new JCheckBox("Order matters");
    chckbxCombination.setSelected(true);
    JButton btnPermutate = new JButton("Permutate");
    btnPermutate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        String[] s = textArea.getText().split("\n");
        int n = s.length;
        int k = (Integer)spinner.getValue();
        if(chckbxCombination.isSelected()){
          KPermutationEnumeration kpe = new KPermutationEnumeration(n, k);
          
          String o = "";
          while(kpe.hasMoreElements()){
            ArrayList<Integer> p = kpe.nextElement();
            String l = "";
            for(int i=0;i<k;i++){
              l += s[p.get(i)] + ((i!=k-1)?" ":"");
            }
            o += l.trim() + "\n";
          }
          textArea_1.setText(o);
        } else{
          CombinationEnumeration kpe = new CombinationEnumeration(n, k);
          
          String o = "";
          while(kpe.hasMoreElements()){
            ArrayList<Integer> p = kpe.nextElement().asSequence();
            String l = "";
            for(int i=0;i<k;i++){
              l += s[p.get(i)] + ((i!=k-1)?" ":"");
            }
            o += l.trim() + "\n";
          }
          textArea_1.setText(o);
        }
      }
    });
    
    JLabel lblOutput = new JLabel("Output");
    
    
    GroupLayout groupLayout = new GroupLayout(frmPermutator.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
            .addComponent(lblWriteEachItem, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(lblOutput)
              .addPreferredGap(ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
              .addComponent(chckbxCombination))
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(btnPermutate)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(lblPick)
              .addPreferredGap(ComponentPlacement.UNRELATED)
              .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE))
          .addGap(32))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblWriteEachItem)
            .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblPick)
            .addComponent(btnPermutate))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(22)
              .addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE))
            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
              .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
              .addComponent(lblOutput)
              .addComponent(chckbxCombination, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)))
          .addGap(5))
    );
    
    
    scrollPane_1.setViewportView(textArea_1);
    
    
    scrollPane.setViewportView(textArea);
    frmPermutator.getContentPane().setLayout(groupLayout);
  }
}
