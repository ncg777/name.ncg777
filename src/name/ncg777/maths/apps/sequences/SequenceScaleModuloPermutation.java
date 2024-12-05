package name.ncg777.maths.apps.sequences;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.maths.Numbers;
import name.ncg777.maths.objects.Sequence;

import javax.swing.JComboBox;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SequenceScaleModuloPermutation {

  private JFrame frmSequenceScaleModulo;
  private JTextField txtSequence;
  private JTextField txtResult;
  private JButton btnApply;
  private JComboBox<Integer> scale;
  
  private Sequence s;
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          SequenceScaleModuloPermutation window = new SequenceScaleModuloPermutation();
          window.frmSequenceScaleModulo.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public SequenceScaleModuloPermutation() {
    initialize();
  }
  
  private void ComputeScale() {
    String s0 = txtSequence.getText();
    try 
    {
      s = Sequence.parse(s0);
      if(s.size() == 0) return;
    }
    catch(Exception x){
      return;
    
    }
    
    int sz = s.size();
    DefaultComboBoxModel<Integer> m = (DefaultComboBoxModel<Integer>)scale.getModel();
    m.removeAllElements();
    
    for(int i=2;i<sz;i++) {
      if(Numbers.gcd(i, sz) == 1) {
        m.addElement(i);
      }
    }
    
    m.setSelectedItem(m.getElementAt(0));
    
    btnApply.setEnabled(true);
  }
  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmSequenceScaleModulo = new JFrame();
    frmSequenceScaleModulo.setTitle("Sequence Scale Modulo Permutation");
    frmSequenceScaleModulo.setBounds(100, 100, 450, 215);
    frmSequenceScaleModulo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblSequence = new JLabel("Sequence :");
    lblSequence.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtSequence = new JTextField();
    
    txtSequence.setColumns(10);
    
    JLabel lblNewLabel = new JLabel("Scale :");
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    
    scale = new JComboBox<Integer>();

    
    btnApply = new JButton("Apply");
    btnApply.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(s == null) {
          btnApply.setEnabled(false);
          ComputeScale();
          return;
        }
        
        Sequence o = new Sequence();
        while(o.size() != s.size()) o.add(0);
        int _scale = (Integer)scale.getSelectedItem();
        
        for(int i=0;i<o.size();i++) {
          int idx = (i*_scale)%o.size();
          o.set(i, s.get(idx));
        }
        
        txtResult.setText(o.toString());
      }
    });
    btnApply.setEnabled(false);
    
    txtResult = new JTextField();
    txtResult.setColumns(10);
    
    JLabel lblResult = new JLabel("Result :");
    lblResult.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JButton btnComputeScale = new JButton("Compute scale");
    btnComputeScale.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ComputeScale();
      }
    });
    GroupLayout groupLayout = new GroupLayout(frmSequenceScaleModulo.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
            .addComponent(lblResult, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblSequence, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(scale, Alignment.TRAILING, 0, 360, Short.MAX_VALUE)
            .addComponent(btnComputeScale, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
            .addComponent(txtSequence, GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
            .addGroup(groupLayout.createSequentialGroup()
              .addPreferredGap(ComponentPlacement.RELATED)
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(txtResult, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                .addComponent(btnApply, GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE))))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblSequence)
            .addComponent(txtSequence, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnComputeScale)
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(scale, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblNewLabel))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnApply)
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(txtResult, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblResult))
          .addContainerGap(23, Short.MAX_VALUE))
    );
    frmSequenceScaleModulo.getContentPane().setLayout(groupLayout);
  }
}
