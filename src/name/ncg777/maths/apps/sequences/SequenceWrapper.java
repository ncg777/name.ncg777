package name.ncg777.maths.apps.sequences;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.maths.sequences.Sequence;

import javax.swing.SwingConstants;
import javax.swing.JSpinner;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SpinnerNumberModel;

public class SequenceWrapper {

  private JFrame frmSequenceWrapperbouncer;
  private JTextField txtSequence;
  private JTextField txtResult;
  private JTextField txtResult2;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          SequenceWrapper window = new SequenceWrapper();
          window.frmSequenceWrapperbouncer.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public SequenceWrapper() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmSequenceWrapperbouncer = new JFrame();
    frmSequenceWrapperbouncer.setTitle("Sequence Wrapper/Bouncer");
    frmSequenceWrapperbouncer.setBounds(100, 100, 450, 242);
    frmSequenceWrapperbouncer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblSequence = new JLabel("\u0394 Sequence :");
    lblSequence.setHorizontalAlignment(SwingConstants.RIGHT);
    lblSequence.setFont(new Font("Unifont", Font.PLAIN, 12));
    
    txtSequence = new JTextField();
    txtSequence.setColumns(10);
    
    JLabel lblMin = new JLabel("\u0394^-1 Min :");
    lblMin.setHorizontalAlignment(SwingConstants.RIGHT);
    lblMin.setFont(new Font("Unifont", Font.PLAIN, 12));
    
    JSpinner minSpinner = new JSpinner();
    minSpinner.setModel(new SpinnerNumberModel(0, null, null, 1));
    
    JLabel lblAmp = new JLabel("\u0394^-1 Amp :");
    lblAmp.setFont(new Font("Unifont", Font.PLAIN, 12));
    lblAmp.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JSpinner ampSpinner = new JSpinner();
    ampSpinner.setModel(new SpinnerNumberModel(12, 1, null, 1));
    
    JCheckBox chckbxBounce = new JCheckBox("Bounce");
    chckbxBounce.setSelected(true);
    
    JButton btnApply = new JButton("Apply");
    btnApply.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Sequence s = Sequence.parse(txtSequence.getText());
        if(s==null) return;
        
        int _min = (Integer)minSpinner.getValue();
        int _amp = (Integer)ampSpinner.getValue();
        
        Sequence o;
        if(chckbxBounce.isSelected()) {
          o = s.antidifference(0).bounceseq(_min, _amp).difference();
        }
        else {
          o = s.antidifference(0).wrapseq(_min, _amp).difference();
        }
        
        txtResult.setText(o.toString());
        Sequence o2 = o.antidifference(0);
        o2.remove(0);
        txtResult2.setText(o2.toString());
      }
    });
    
    JLabel lblResult = new JLabel("Result :");
    lblResult.setHorizontalAlignment(SwingConstants.RIGHT);
    lblResult.setFont(new Font("Unifont", Font.PLAIN, 12));
    
    txtResult = new JTextField();
    txtResult.setColumns(10);
    
    JLabel lblResult_1 = new JLabel("\u0394^-1 Result :");
    lblResult_1.setFont(new Font("Unifont", Font.PLAIN, 12));
    lblResult_1.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtResult2 = new JTextField();
    txtResult2.setColumns(10);
    GroupLayout groupLayout = new GroupLayout(frmSequenceWrapperbouncer.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addComponent(lblMin, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
            .addComponent(lblAmp, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
            .addComponent(lblSequence, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
            .addComponent(lblResult, GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
            .addComponent(lblResult_1, GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(chckbxBounce)
            .addComponent(ampSpinner, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
            .addComponent(txtSequence, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
            .addComponent(minSpinner, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
            .addComponent(btnApply, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
            .addComponent(txtResult, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
            .addComponent(txtResult2, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblMin)
            .addComponent(minSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblAmp)
            .addComponent(ampSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addGap(2)
          .addComponent(chckbxBounce)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(txtSequence, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblSequence))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnApply)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblResult)
            .addComponent(txtResult, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblResult_1)
            .addComponent(txtResult2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addContainerGap(45, Short.MAX_VALUE))
    );
    frmSequenceWrapperbouncer.getContentPane().setLayout(groupLayout);
  }
}
