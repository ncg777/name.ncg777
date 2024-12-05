package name.ncg777.maths.sequences.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.maths.sequences.Sequence;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SequenceConvolver {

  private JFrame frmSequenceConvolver;
  private JTextField txtCarrier;
  private JTextField txtImpulse;
  private JTextField txtResult;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          SequenceConvolver window = new SequenceConvolver();
          window.frmSequenceConvolver.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public SequenceConvolver() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmSequenceConvolver = new JFrame();
    frmSequenceConvolver.setTitle("Sequence Convolver");
    frmSequenceConvolver.setBounds(100, 100, 507, 180);
    frmSequenceConvolver.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblCarrier = new JLabel("Carrier :");
    lblCarrier.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblImpulse = new JLabel("Impulse :");
    lblImpulse.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblResult = new JLabel("Result :");
    lblResult.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtCarrier = new JTextField();
    txtCarrier.setColumns(10);
    
    txtImpulse = new JTextField();
    txtImpulse.setColumns(10);
    
    txtResult = new JTextField();
    txtResult.setColumns(10);
    
    JButton btnConvolve = new JButton("Convolve");
    btnConvolve.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        
        txtResult.setText(
          Sequence.parse(txtCarrier.getText().trim())
            .convolveWith(
              Sequence.parse(txtImpulse.getText().trim()))
              .toString()); 
          
      }
    });
    GroupLayout groupLayout = new GroupLayout(frmSequenceConvolver.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addGap(27)
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
            .addComponent(lblImpulse)
            .addComponent(lblCarrier)
            .addGroup(groupLayout.createSequentialGroup()
              .addPreferredGap(ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
              .addComponent(lblResult)))
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(txtImpulse)
            .addComponent(btnConvolve, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtCarrier, GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
            .addComponent(txtResult, Alignment.TRAILING))
          .addGap(4))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(txtCarrier, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblCarrier))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(txtImpulse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblImpulse))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnConvolve)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(txtResult, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblResult))
          .addContainerGap(29, Short.MAX_VALUE))
    );
    frmSequenceConvolver.getContentPane().setLayout(groupLayout);
  }
}
