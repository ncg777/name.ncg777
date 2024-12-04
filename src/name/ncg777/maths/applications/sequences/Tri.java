package name.ncg777.maths.applications.sequences;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.maths.objects.Sequence;

import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SpinnerNumberModel;

public class Tri {

  private JFrame frmRange;
  private JTextField txtResult;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Tri window = new Tri();
          window.frmRange.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public Tri() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmRange = new JFrame();
    frmRange.setTitle("Tri");
    frmRange.setBounds(100, 100, 450, 192);
    frmRange.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JSpinner start = new JSpinner();
    
    JLabel lblStart = new JLabel("Start :");
    lblStart.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblLength = new JLabel("Half-Length :");
    lblLength.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JSpinner length = new JSpinner();
    length.setModel(new SpinnerNumberModel(4, null, null, 1));
    
    JLabel lblStep = new JLabel("Step :");
    lblStep.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JSpinner step = new JSpinner();
    step.setModel(new SpinnerNumberModel(1, null, null, 1));
    
    JButton btnGenerate = new JButton("Generate");
    btnGenerate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int _start = (int) start.getValue();
        int _length = (int) length.getValue();
        int _step = (int) step.getValue();
        
        Sequence o = new Sequence();
        for(int i=0; i<_length; i++) {
          o.add(_start + i*_step); 
        }
        
        for(int i=_length; i>0; i--) {
          o.add(_start + i*_step); 
        }
        txtResult.setText(o.toString());
      }
    });
    
    JLabel lblResult = new JLabel("Result :");
    lblResult.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtResult = new JTextField();
    txtResult.setColumns(10);
    GroupLayout groupLayout = new GroupLayout(frmRange.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
            .addComponent(lblStart, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblLength, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblStep, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
            .addComponent(lblResult, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(start, GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
            .addComponent(txtResult, GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
            .addComponent(step, GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
            .addComponent(btnGenerate, GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
            .addComponent(length, GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblStart)
            .addComponent(start, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(length, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblLength))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblStep)
            .addComponent(step, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addComponent(btnGenerate)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblResult)
            .addComponent(txtResult, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addContainerGap(12, Short.MAX_VALUE))
    );
    frmRange.getContentPane().setLayout(groupLayout);
  }
}
