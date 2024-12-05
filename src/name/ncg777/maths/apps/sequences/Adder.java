package name.ncg777.maths.apps.sequences;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import name.ncg777.maths.Numbers;
import name.ncg777.maths.sequences.Sequence;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Adder {

  private JFrame frmAddSequences;
  private JTextField textX;
  private JTextField textY;
  private JTextField textResult;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Adder window = new Adder();
          window.frmAddSequences.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public Adder() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmAddSequences = new JFrame();
    frmAddSequences.setTitle("Add sequences");
    frmAddSequences.setBounds(100, 100, 450, 155);
    frmAddSequences.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmAddSequences.getContentPane().setLayout(null);
    
    JLabel lblNewLabel = new JLabel("Sequence x:");
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setBounds(0, 11, 76, 14);
    frmAddSequences.getContentPane().add(lblNewLabel);
    
    textX = new JTextField();
    textX.setBounds(80, 8, 344, 17);
    frmAddSequences.getContentPane().add(textX);
    textX.setColumns(10);
    
    textY = new JTextField();
    textY.setColumns(10);
    textY.setBounds(80, 31, 344, 17);
    frmAddSequences.getContentPane().add(textY);
    
    JLabel lblSequenceY = new JLabel("Sequence y:");
    lblSequenceY.setHorizontalAlignment(SwingConstants.RIGHT);
    lblSequenceY.setBounds(0, 34, 76, 14);
    frmAddSequences.getContentPane().add(lblSequenceY);
    
    JButton btnNewButton = new JButton("Add");
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        var x = Sequence.parse(textX.getText());
        var y = Sequence.parse(textY.getText());
        var lcm = Numbers.lcm(x.size(), y.size());
        Sequence o = new Sequence();
        for(int i=0;i<lcm;i++) {
          o.add(x.get(i%x.size())+y.get(i%y.size()));
        }
        textResult.setText(o.toString());
      }
    });
    btnNewButton.setBounds(80, 53, 344, 23);
    frmAddSequences.getContentPane().add(btnNewButton);
    
    textResult = new JTextField();
    textResult.setColumns(10);
    textResult.setBounds(80, 85, 344, 17);
    frmAddSequences.getContentPane().add(textResult);
    
    JLabel lblXy = new JLabel("x+y:");
    lblXy.setHorizontalAlignment(SwingConstants.RIGHT);
    lblXy.setBounds(10, 88, 66, 14);
    frmAddSequences.getContentPane().add(lblXy);
  }

}
