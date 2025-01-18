package name.ncg777.maths.numbers.quartal.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.numbers.quartal.QuartalNumbersSequence;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class Expander {
  private JFrame frmExpander;
  private JTextField txtRhythm;
  private JTextField txtResult;
  private JTextField txtNot;
  private JComboBox<Cipher.Name> comboBox = new JComboBox<Cipher.Name>(new DefaultComboBoxModel<Cipher.Name>(Cipher.Name.values()));
  private JTextField textPattern;

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Expander window = new Expander();
          window.frmExpander.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public Expander() {
    initialize();
  }

  private void initialize() {
    frmExpander = new JFrame();
    frmExpander.setResizable(false);
    frmExpander.setTitle("Expander");
    frmExpander.setBounds(100, 100, 667, 239);
    frmExpander.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblRhythm = new JLabel("Rhythm :");
    
    txtRhythm = new JTextField();
    txtRhythm.setColumns(10);
    
    JLabel lblMult = new JLabel("Mult :");
    
    JSpinner spinner = new JSpinner();
    spinner.setModel(new SpinnerNumberModel(1, 1, null, 1));
    
    JLabel lblResult = new JLabel("Result :");
    
    txtResult = new JTextField();
    txtResult.setEditable(false);
    txtResult.setColumns(10);
    
    JCheckBox chckbxFill = new JCheckBox("Fill");
    JSpinner rot = new JSpinner();
    JButton btnExpand = new JButton("Expand and Rotate");
    btnExpand.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        var abc = (Cipher.Name)comboBox.getSelectedItem();
        var o = QuartalNumbersSequence.rotate(
            QuartalNumbersSequence.expand(
                new QuartalNumbersSequence(
                    abc, 
                    txtRhythm.getText()), 
                    (int)spinner.getValue(), 
                    chckbxFill.isSelected(),
                    new QuartalNumbersSequence(abc,textPattern.getText())), 
                    (int)rot.getValue());
        
        txtResult.setText(o.toString());
        txtNot.setText(QuartalNumbersSequence.not(o).toString());
      }
    });
    
    JLabel lblNot = new JLabel("Not :");
    lblNot.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtNot = new JTextField();
    txtNot.setEditable(false);
    txtNot.setColumns(10);
    
    JLabel lblRotate = new JLabel("Rotate :");
    lblRotate.setHorizontalAlignment(SwingConstants.RIGHT);
    
    textPattern = new JTextField();
    textPattern.setColumns(10);
    
    JLabel lblNewLabel = new JLabel("Pattern:");
    
    GroupLayout groupLayout = new GroupLayout(frmExpander.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                  .addGap(7)
                  .addComponent(lblResult))
                .addGroup(groupLayout.createSequentialGroup()
                  .addGap(20)
                  .addComponent(lblNot)))
              .addPreferredGap(ComponentPlacement.RELATED)
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                .addComponent(txtNot)
                .addComponent(btnExpand, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtResult, GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE))
              .addPreferredGap(ComponentPlacement.RELATED, 20, Short.MAX_VALUE))
            .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
              .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.UNRELATED)
              .addComponent(lblMult)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
              .addGap(18)
              .addComponent(chckbxFill)
              .addGap(18)
              .addComponent(lblRotate)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(rot, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
              .addGap(91))
            .addGroup(groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(lblRhythm)
                .addComponent(lblNewLabel))
              .addPreferredGap(ComponentPlacement.RELATED)
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                .addComponent(textPattern)
                .addComponent(txtRhythm, GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE))
              .addPreferredGap(ComponentPlacement.RELATED, 12, Short.MAX_VALUE)))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblRhythm)
            .addComponent(txtRhythm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(textPattern, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblNewLabel))
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(chckbxFill)
            .addComponent(lblRotate)
            .addComponent(rot, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblMult)
            .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addComponent(btnExpand)
          .addGap(4)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(txtResult, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblResult))
          .addGap(8)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(txtNot, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblNot))
          .addGap(22))
    );
    frmExpander.getContentPane().setLayout(groupLayout);
  }
}
