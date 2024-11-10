package name.ncg777.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.Maths.Objects.Sequence;
import name.ncg777.Music.RhythmPredicates.EntropicDispersion;
import name.ncg777.Music.SequencePredicates.PredicatedSeqRhythms;

import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

public class RandomWalker1 {

  private JFrame frmRandomWalker1;
  private JTextField txtDelta;
  private JTextField txtXsmod;
  private JSpinner spinner_n = new JSpinner(new SpinnerNumberModel(Integer.valueOf(2), Integer.valueOf(2), null, Integer.valueOf(1)));
  private JTextField txtSeq;
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          RandomWalker1 window = new RandomWalker1();
          window.frmRandomWalker1.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public RandomWalker1() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmRandomWalker1 = new JFrame();
    frmRandomWalker1.setTitle("Random walker 1");
    frmRandomWalker1.setBounds(100, 100, 883, 167);
    frmRandomWalker1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblAmp = new JLabel("amp:");
    
    JSpinner spinner_amp = new JSpinner();
    spinner_amp.setModel(new SpinnerNumberModel(1, 1, null, 1));
    
    JLabel lblSum = new JLabel("sum:");
    
    JSpinner spinner_sum = new JSpinner();
    
    JLabel lblMaxamp = new JLabel("maxamp:");
    
    JSpinner spinner_maxamp = new JSpinner();
    spinner_maxamp.setModel(new SpinnerNumberModel(1, 1, null, 1));
    
    JCheckBox chckbxExclude = new JCheckBox("exclude 0");
    
    JLabel lblDelta = new JLabel("Delta:");
    lblDelta.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtDelta = new JTextField();
    txtDelta.setEditable(false);
    txtDelta.setColumns(10);
    JSpinner spinner = new JSpinner();
    spinner.setModel(new SpinnerNumberModel(8, 1, null, 1));
    
    txtXsmod = new JTextField();
    txtXsmod.setEditable(false);
    txtXsmod.setColumns(10);
    
    JButton btnGenerate = new JButton("Generate");
    btnGenerate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
 
        
          new Thread(() -> {
            try {
              btnGenerate.setEnabled(false);
              while(true)
              {
  
                int n = (int)spinner_n.getValue();
  
                var pred = new PredicatedSeqRhythms(new EntropicDispersion());
                Sequence rnd;
                while(true) {
                   rnd = Sequence.genRnd(
                    n, 
                    (int)spinner_amp.getValue(), 
                    (int)spinner_sum.getValue(), 
                    (int)spinner_maxamp.getValue(), 
                    chckbxExclude.isSelected());
                   
                   if(pred.apply(rnd) || n < 3) break;
                }
                txtSeq.setText(rnd.toString());
                txtDelta.setText(rnd.cyclicalDifference().toString());
                int xs = (int)spinner.getValue();
                            
                txtXsmod.setText(rnd.bounceseq(0, xs).toString());
                
                break;
                
              }
          }
          catch(Exception ex) {
            JOptionPane.showMessageDialog(frmRandomWalker1, ex.getMessage(), "Nope", JOptionPane.INFORMATION_MESSAGE);
          }
          btnGenerate.setEnabled(true);
            
          }).start();;
          
        
      }
    });
    
    JLabel lblXsMod = new JLabel("XS bounce:");
    lblXsMod.setHorizontalAlignment(SwingConstants.RIGHT);
    
    
    
    JLabel lblXsMod_1 = new JLabel("XS bounce:");
    
    JLabel lblN = new JLabel("n:");
    lblN.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblSeq = new JLabel("Seq:");
    lblSeq.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtSeq = new JTextField();
    txtSeq.setEditable(false);
    txtSeq.setColumns(10);
    


    
    
    GroupLayout groupLayout = new GroupLayout(frmRandomWalker1.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(lblN, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
              .addGap(4)
              .addComponent(spinner_n, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(lblAmp)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner_amp, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(lblSum)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner_sum, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(lblMaxamp)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner_maxamp, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(chckbxExclude)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(btnGenerate)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(lblXsMod_1)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE))
            .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
                  .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                    .addComponent(lblDelta, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblXsMod, GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE))
                  .addPreferredGap(ComponentPlacement.UNRELATED)
                  .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(txtDelta, GroupLayout.DEFAULT_SIZE, 761, Short.MAX_VALUE)
                    .addComponent(txtXsmod, GroupLayout.DEFAULT_SIZE, 761, Short.MAX_VALUE)))
                .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
                  .addComponent(lblSeq, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(ComponentPlacement.UNRELATED)
                  .addComponent(txtSeq, GroupLayout.DEFAULT_SIZE, 761, Short.MAX_VALUE)))
              .addGap(19)))
          .addGap(0))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(3)
              .addComponent(lblN))
            .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
              .addComponent(spinner_n, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addComponent(lblAmp)
              .addComponent(spinner_amp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addComponent(lblSum)
              .addComponent(spinner_sum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addComponent(lblMaxamp)
              .addComponent(spinner_maxamp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addComponent(chckbxExclude)
              .addComponent(btnGenerate)
              .addComponent(lblXsMod_1)
              .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(14)
              .addComponent(lblSeq))
            .addGroup(groupLayout.createSequentialGroup()
              .addPreferredGap(ComponentPlacement.UNRELATED)
              .addComponent(txtSeq, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblDelta)
            .addComponent(txtDelta, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblXsMod)
            .addComponent(txtXsmod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    frmRandomWalker1.getContentPane().setLayout(groupLayout);
  }
}
