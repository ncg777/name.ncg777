package name.ncg777.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.Maths.Objects.Sequence;
import name.ncg777.Music.R12List;
import name.ncg777.Music.R16List;
import name.ncg777.Music.R48List;
import name.ncg777.Music.Rhythm;
import name.ncg777.Music.Rn;

import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import java.awt.Font;

public class SequenceConvolverAbsoluteTime {
  private JFrame frmArticulateAndSmooth;
  private JTextField txtS;
  private JTextField txtImpulse;
  private JTextField txtR;
  private JTextField txtResult;
  private JComboBox<Rn> comboBox;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          SequenceConvolverAbsoluteTime window = new SequenceConvolverAbsoluteTime();
          window.frmArticulateAndSmooth.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public SequenceConvolverAbsoluteTime() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmArticulateAndSmooth = new JFrame();
    frmArticulateAndSmooth.setTitle("Sequence Convolver Absolute Time");
    frmArticulateAndSmooth.setBounds(100, 100, 450, 224);
    frmArticulateAndSmooth.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblSequenceA = new JLabel("Sequence S:");
    lblSequenceA.setFont(new Font("Unifont", Font.PLAIN, 12));
    lblSequenceA.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblSequenceB = new JLabel("Impulse:");
    lblSequenceB.setFont(new Font("Unifont", Font.PLAIN, 12));
    lblSequenceB.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblSmoothingKernel = new JLabel("Rhythm:");
    lblSmoothingKernel.setFont(new Font("Unifont", Font.PLAIN, 11));
    lblSmoothingKernel.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblResult = new JLabel("Result:");
    lblResult.setFont(new Font("Unifont", Font.PLAIN, 11));
    lblResult.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtS = new JTextField();
    txtS.setColumns(10);
    
    txtImpulse = new JTextField();
    txtImpulse.setColumns(10);
    
    txtR = new JTextField();
    txtR.setColumns(10);
    

    
    txtResult = new JTextField();
    txtResult.setColumns(10);
    JButton btnArticulateAndSmooth = new JButton("Convolve");
    btnArticulateAndSmooth.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Sequence s = Sequence.parse(txtS.getText());
        Sequence i = Sequence.parse(txtImpulse.getText());
        Rhythm r = null;
        if(comboBox.getSelectedItem() == Rn.Hex) r = R16List.parseR16Seq(txtR.getText()).asRhythm();
        if(comboBox.getSelectedItem() == Rn.Octal) r = R12List.parseR12Seq(txtR.getText()).asRhythm();
        if(comboBox.getSelectedItem() == Rn.Tribble) r = R48List.parseR48Seq(txtR.getText()).asRhythm();
        txtResult.setText(s.absoluteTimeConvolve(r, i).toString().replaceAll("[()]", ""));
      }
    });
    
    comboBox = new JComboBox<Rn>(new DefaultComboBoxModel<Rn>(Rn.values()));
    comboBox.setFont(new Font("Unifont", Font.PLAIN, 11));
    
    
    GroupLayout groupLayout = new GroupLayout(frmArticulateAndSmooth.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.TRAILING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
              .addComponent(lblSmoothingKernel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(lblSequenceB, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(lblSequenceA, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(lblResult, GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(txtResult, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
            .addComponent(btnArticulateAndSmooth, GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
            .addComponent(txtImpulse, GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
            .addComponent(txtS, GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
            .addComponent(txtR, GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
            .addComponent(comboBox, 0, 312, Short.MAX_VALUE))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblSequenceA)
            .addComponent(txtS, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblSequenceB)
            .addComponent(txtImpulse, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblSmoothingKernel)
            .addComponent(txtR, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(btnArticulateAndSmooth))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(63)
              .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                .addComponent(txtResult, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(lblResult))))
          .addContainerGap(19, Short.MAX_VALUE))
    );
    frmArticulateAndSmooth.getContentPane().setLayout(groupLayout);
  }
}
