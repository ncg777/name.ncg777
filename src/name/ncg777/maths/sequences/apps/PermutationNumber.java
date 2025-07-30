package name.ncg777.maths.sequences.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.computing.structures.CollectionUtils;
import name.ncg777.maths.sequences.Sequence;

import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PermutationNumber {

  private JFrame frmPermutationNumber;
  private JTextField textSequence;
  private JTextField textResult;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          PermutationNumber window = new PermutationNumber();
          window.frmPermutationNumber.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public PermutationNumber() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmPermutationNumber = new JFrame();
    frmPermutationNumber.setTitle("Permutation number");
    frmPermutationNumber.setBounds(100, 100, 450, 153);
    frmPermutationNumber.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblNewLabel = new JLabel("Permutation :");
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    
    textSequence = new JTextField();
    textSequence.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 10));
    textSequence.setColumns(10);
    
    JButton btnMap = new JButton("Get number");
    btnMap.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          Sequence s = Sequence.parse(textSequence.getText());
          
          var result = CollectionUtils.getPermutationNumber(s);
          textResult.setText(Integer.toString(result));
        } catch(Exception ex) {
          textResult.setText("Error");
        }
      }
    });
    btnMap.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    
    JLabel lblNewLabel_2 = new JLabel("Result :");
    lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel_2.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    
    textResult = new JTextField();
    textResult.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 10));
    textResult.setColumns(10);
    GroupLayout groupLayout = new GroupLayout(frmPermutationNumber.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addContainerGap()
              .addComponent(lblNewLabel)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(textSequence, GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE))
            .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
              .addGap(82)
              .addComponent(btnMap, GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE))
            .addGroup(groupLayout.createSequentialGroup()
              .addContainerGap()
              .addComponent(lblNewLabel_2, GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
              .addComponent(textResult, GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(textSequence, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblNewLabel))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnMap)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblNewLabel_2)
            .addComponent(textResult, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addContainerGap(94, Short.MAX_VALUE))
    );
    frmPermutationNumber.getContentPane().setLayout(groupLayout);
  }
}
