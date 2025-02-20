package name.ncg777.maths.sequences.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.sequences.Sequence.Combiner;
import name.ncg777.maths.sequences.Sequence.Operation;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;

public class Operator {

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
          Operator window = new Operator();
          window.frmAddSequences.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
  
  private Sequence.Combiner combiner = Combiner.Product;
  private Sequence.Operation operation = Operation.Add;
  private JComboBox<Combiner> comboCombiner;
  private JComboBox<Operation> comboOperation;
  public Operator() {
    initialize();
  }

  
  private void initialize() {
   
    frmAddSequences = new JFrame();
    frmAddSequences.setTitle("Operator");
    frmAddSequences.setBounds(100, 100, 450, 260);
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
    
    JLabel lblSequenceY = new JLabel("y:");
    lblSequenceY.setHorizontalAlignment(SwingConstants.RIGHT);
    lblSequenceY.setBounds(0, 34, 76, 14);
    frmAddSequences.getContentPane().add(lblSequenceY);
    
    JButton btnNewButton = new JButton("Apply");
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        var x = Sequence.parse(textX.getText());
        var y = Sequence.parse(textY.getText());
        
        textResult.setText(Sequence.combine(combiner, operation,x,y).toString());
      }
    });
    btnNewButton.setBounds(80, 53, 344, 23);
    frmAddSequences.getContentPane().add(btnNewButton);
    
    textResult = new JTextField();
    textResult.setColumns(10);
    textResult.setBounds(80, 183, 344, 17);
    frmAddSequences.getContentPane().add(textResult);
    
    JLabel lblYixi = new JLabel("Result:");
    lblYixi.setHorizontalAlignment(SwingConstants.LEFT);
    lblYixi.setBounds(80, 158, 48, 14);
    frmAddSequences.getContentPane().add(lblYixi);
    
    comboCombiner = new JComboBox<Combiner>(new DefaultComboBoxModel<>(Combiner.values()));
    comboCombiner.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        combiner = (Combiner)comboCombiner.getSelectedItem();
      }
    });
    comboCombiner.setBounds(80, 87, 344, 23);
    frmAddSequences.getContentPane().add(comboCombiner);
    
    comboOperation = new JComboBox<Operation>(new DefaultComboBoxModel<>(Operation.values()));
    comboOperation.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        operation = (Operation)comboOperation.getSelectedItem();
      }
    });
    comboOperation.setBounds(80, 121, 344, 23);
    frmAddSequences.getContentPane().add(comboOperation);
    
    JButton btnNewButton_1 = new JButton("x");
    btnNewButton_1.setBounds(150, 154, 48, 23);
    btnNewButton_1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        textX.setText(textResult.getText());
      }
    });
    frmAddSequences.getContentPane().add(btnNewButton_1);
    
    JButton btnNewButton_1_1 = new JButton("y");
    btnNewButton_1_1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        textY.setText(textResult.getText());
      }
    });
    btnNewButton_1_1.setBounds(208, 154, 48, 23);
    frmAddSequences.getContentPane().add(btnNewButton_1_1);
  }
}
