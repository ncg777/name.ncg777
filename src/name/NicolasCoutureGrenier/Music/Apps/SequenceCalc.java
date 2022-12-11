package name.NicolasCoutureGrenier.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence;

import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SequenceCalc {
  private enum Calctype {
    Difference, 
    CyclicalForwardDifference, 
    CyclicalBackwardDifference, 
    Antidifference, 
    CyclicalForwardAntidifference, 
    CyclicalBackwardAntidifference,
    AsOrdinalsBipolar,
    AsOrdinalsUnipolar,
    MapWithNextPermutation,
    Rotate,
    Reverse,
    Flip,
    AddToAll,
    Multiply,
    ApplyMin,
    ApplyMax
  }
  private JFrame frmSequenceCalc;
  private JTextField txtInput;
  private JTextField txtOutput;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          SequenceCalc window = new SequenceCalc();
          window.frmSequenceCalc.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public SequenceCalc() {
    initialize();
  }
  JComboBox<Calctype> comboBox;
  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmSequenceCalc = new JFrame();
    frmSequenceCalc.setTitle("Sequence Calc");
    frmSequenceCalc.setBounds(100, 100, 450, 231);
    frmSequenceCalc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblSequence = new JLabel("Sequence :");
    
    txtInput = new JTextField();
    txtInput.setFont(new Font("Unifont", Font.PLAIN, 12));
    txtInput.setColumns(10);
    
    JLabel lblOperation = new JLabel("Operation :");
    
    comboBox = new JComboBox<Calctype>();
    comboBox.setModel(new DefaultComboBoxModel<>(Calctype.values()));
    comboBox.setFont(new Font("Unifont", Font.PLAIN, 12));
    comboBox.setEditable(false);
    
    JLabel lblK = new JLabel("k* :");
    
    JSpinner spinner = new JSpinner();
    
    JLabel lblOutput = new JLabel("Output :");
    
    txtOutput = new JTextField();
    txtOutput.setColumns(10);
    
    JButton btnCalc = new JButton("Calc");
    btnCalc.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int k = 0;
        Sequence input = Sequence.parse(txtInput.getText());
        String output = "";
        switch((Calctype)comboBox.getSelectedItem()) {
          case Difference:
            output = input.difference().toString();
            break;
          case CyclicalForwardDifference:
            output = input.cyclicalForwardDifference().toString();
            break;
          case CyclicalBackwardDifference:
            output = input.cyclicalBackwardDifference().toString();
            break;
          case Antidifference:
            k = (Integer)spinner.getValue();
            output = input.antidifference(k).toString();
            break;
          case CyclicalForwardAntidifference:
            k = (Integer)spinner.getValue();
            output = input.cyclicalForwardAntidifference(k).toString();
            break;
          case CyclicalBackwardAntidifference:
            k = (Integer)spinner.getValue();
            output = input.cyclicalBackwardAntidifference(k).toString();
            break;
          case AsOrdinalsBipolar:
            output = input.asOrdinalsBipolar().toString();
            break;
          case AsOrdinalsUnipolar:
            output = input.asOrdinalsUnipolar().toString();
            break;
          case MapWithNextPermutation:
            output = input.mapWithNextPermutation().toString();
            break;
          case Rotate:
            k = (Integer)spinner.getValue();
            output = input.rotate(k).toString();
            break;
          case Reverse:
            output = input.reverse().toString();
            break;
          case Flip:
            output = input.flip().toString();
            break;
          case AddToAll:
            k = (Integer)spinner.getValue();
            output = input.addToAll(k).toString();
            break;
          case Multiply:
            k = (Integer)spinner.getValue();
            output = input.multiply(k).toString();
            break;
          case ApplyMin:
            k = (Integer)spinner.getValue();
            output = input.applyMin(k).toString();
            break;
          case ApplyMax:
            k = (Integer)spinner.getValue();
            output = input.applyMin(k).toString();
            break;
        }
        txtOutput.setText(output.trim());
        
      }
    });
    
    JButton btnFeedback = new JButton("Feedback");
    btnFeedback.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        txtInput.setText(txtOutput.getText());
      }
    });
    GroupLayout groupLayout = new GroupLayout(frmSequenceCalc.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(btnFeedback, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(lblSequence)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(txtInput, GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE))
            .addGroup(groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(lblOperation)
                .addComponent(lblK))
              .addPreferredGap(ComponentPlacement.RELATED)
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(spinner, GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
                .addComponent(comboBox, 0, 355, Short.MAX_VALUE)))
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(lblOutput)
              .addGap(18)
              .addComponent(txtOutput, GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE))
            .addComponent(btnCalc, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblSequence)
            .addComponent(txtInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblOperation)
            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblK)
            .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnCalc)
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(txtOutput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblOutput))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnFeedback)
          .addContainerGap(49, Short.MAX_VALUE))
    );
    frmSequenceCalc.getContentPane().setLayout(groupLayout);
  }
}
