package name.ncg777.maths.apps.sequences;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.maths.objects.Sequence;

import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import java.awt.Font;

public class SmoothArticulator {
  private enum Direction {LookForward, LookBehind};
  private JFrame frmArticulateAndSmooth;
  private JTextField txtA;
  private JTextField txtB;
  private JTextField txtK;
  private JTextField txtResult;
  private JComboBox<Direction> comboBox;
  private JLabel lblSmoothingDirection;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          SmoothArticulator window = new SmoothArticulator();
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
  public SmoothArticulator() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmArticulateAndSmooth = new JFrame();
    frmArticulateAndSmooth.setTitle("Articulate and Smooth");
    frmArticulateAndSmooth.setBounds(100, 100, 450, 224);
    frmArticulateAndSmooth.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblSequenceA = new JLabel("Sequence \u0394A:");
    lblSequenceA.setFont(new Font("Unifont", Font.PLAIN, 12));
    lblSequenceA.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblSequenceB = new JLabel("Sequence \u0394B:");
    lblSequenceB.setFont(new Font("Unifont", Font.PLAIN, 12));
    lblSequenceB.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblSmoothingKernel = new JLabel("Smoothing Kernel:");
    lblSmoothingKernel.setFont(new Font("Unifont", Font.PLAIN, 11));
    lblSmoothingKernel.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblResult = new JLabel("Result:");
    lblResult.setFont(new Font("Unifont", Font.PLAIN, 11));
    lblResult.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtA = new JTextField();
    txtA.setColumns(10);
    
    txtB = new JTextField();
    txtB.setColumns(10);
    
    txtK = new JTextField();
    txtK.setColumns(10);
    

    
    txtResult = new JTextField();
    txtResult.setColumns(10);
    JButton btnArticulateAndSmooth = new JButton("Articulate and Smooth");
    btnArticulateAndSmooth.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Sequence a = Sequence.parse(txtA.getText());
        Sequence b = Sequence.parse(txtB.getText());
        
        a = a.antidifference(0);
        a.remove(0);
        
        b = b.antidifference(0);
        b.remove(0);
        
        String[] k_text = txtK.getText().split("\\s");
        List<Double> k = new ArrayList<Double>();
        Double sum = 0.0;
        for(String s : k_text) {
          Double v = Double.parseDouble(s);
          sum += v;
          k.add(v);
        }
        for(int i=0;i<k.size();i++) {
          k.set(i, k.get(i)/sum);
        }
        Sequence t = new Sequence();
        for(int i=0;i<a.size();i++) {
          for(int j=0;j<b.size();j++) {
            t.add(a.get(i) + b.get(j));
          }
        }
        
        Sequence output = new Sequence();
        for(int i=0;i<t.size();i++) {
          double v = 0;
          switch((Direction)comboBox.getSelectedItem()) {
            case LookForward :
              for(int j=0;j<k.size();j++) {
                v += (t.get((i+j)%t.size()) * k.get(j));
              }
              break;
            case LookBehind :
              for(int j=0;j<k.size();j++) {
                int i1 = (i-j);
                while(i1 < 0) {
                  i1 += t.size();
                }
                while(i1 >= t.size()) {
                  i1 -= t.size();
                }
                int i2 = -j;
                while(i2 < 0) {
                  i2 += k.size();
                }
                while(i2 >= k.size()) {
                  i2 -= k.size();
                }
                v += t.get(i1) * k.get(i2);
              }
              break;
          }
          
          output.add((int)Math.round(v));
        }
        output.add(0, 0);
        output = output.difference();
        txtResult.setText(output.toString().replaceAll("[()]", ""));
      }
    });
    
    comboBox = new JComboBox<Direction>();
    comboBox.setFont(new Font("Unifont", Font.PLAIN, 11));
    comboBox.setModel(new DefaultComboBoxModel<>(Direction.values()));
    
    lblSmoothingDirection = new JLabel("Smoothing Direction:");
    lblSmoothingDirection.setHorizontalAlignment(SwingConstants.RIGHT);
    lblSmoothingDirection.setFont(new Font("Unifont", Font.PLAIN, 11));
    
    GroupLayout groupLayout = new GroupLayout(frmArticulateAndSmooth.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.TRAILING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, groupLayout.createParallelGroup(Alignment.TRAILING, false)
              .addComponent(lblSmoothingKernel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(lblSequenceB, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(lblSequenceA, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(lblResult, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
            .addComponent(lblSmoothingDirection))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(txtResult, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
            .addComponent(btnArticulateAndSmooth, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
            .addComponent(txtB, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
            .addComponent(txtA, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
            .addComponent(txtK, GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
            .addComponent(comboBox, 0, 324, Short.MAX_VALUE))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblSequenceA)
            .addComponent(txtA, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblSequenceB)
            .addComponent(txtB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblSmoothingKernel)
            .addComponent(txtK, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addPreferredGap(ComponentPlacement.RELATED)
              .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(lblSmoothingDirection))
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
