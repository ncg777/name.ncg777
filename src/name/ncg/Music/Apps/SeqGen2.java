package name.ncg.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Music.R12List;
import name.ncg.Music.R16List;

import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import name.ncg.Music.Rn;
import javax.swing.SpinnerModel;

public class SeqGen2 {

  private JFrame frmSeqgen;
  private JTextField txtDelta;
  private JTextField txtRhythm;
  private JTextField txtSequence;
  private JComboBox<Rn> comboBox = new JComboBox<Rn>(new DefaultComboBoxModel<Rn>(Rn.values()));
  JSpinner spinner_bounce_amp = new JSpinner(new SpinnerNumberModel(new Integer(12), new Integer(1), null, new Integer(1)));
  JSpinner spinner_bounce_min = new JSpinner(new SpinnerNumberModel(new Integer(0), null, null, new Integer(1)));
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          SeqGen2 window = new SeqGen2();
          window.frmSeqgen.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public SeqGen2() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmSeqgen = new JFrame();
    frmSeqgen.setResizable(false);
    frmSeqgen.setTitle("Sequence Generator 2");
    frmSeqgen.setBounds(100, 100, 958, 190);
    frmSeqgen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JLabel lblAmp = new JLabel("amp:");
    lblAmp.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JSpinner spinner_amp = new JSpinner();
    spinner_amp.setModel(new SpinnerNumberModel(new Integer(4), new Integer(1), null, new Integer(1)));
    
    JLabel lblSum = new JLabel("sum:");
    lblSum.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JSpinner spinner_sum = new JSpinner();
    
    JLabel lblMaxamp = new JLabel("maxamp:");
    
    JSpinner spinner_maxamp = new JSpinner();
    spinner_maxamp.setModel(new SpinnerNumberModel(new Integer(7), new Integer(1), null, new Integer(1)));
    
    JCheckBox chckbxExclude = new JCheckBox("exclude 0");
    
    JLabel lblDelta = new JLabel("Delta:");
    lblDelta.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtDelta = new JTextField();
    txtDelta.setEditable(false);
    txtDelta.setColumns(10);
    txtRhythm = new JTextField();
    txtRhythm.setColumns(10);
    
    txtSequence = new JTextField();
    txtSequence.setEditable(false);
    txtSequence.setColumns(10);
    
    JButton btnGenerate = new JButton("Generate");
    btnGenerate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
 
        try {
          while(true)
          {
            String str_R = txtRhythm.getText().trim();
            
            Sequence C = null;
            if(comboBox.getSelectedItem() == Rn.Hex) {
              R16List r = R16List.parseR16Seq(str_R);
              C = r.asRhythm().getComposition().asSequence();
            }
            if(comboBox.getSelectedItem() == Rn.Octal) {
              R12List r = R12List.parseR12Seq(str_R);
              C = r.asRhythm().getComposition().asSequence();
            }
            Sequence s = new Sequence();
            int current_dir = 0;

            for(int i=0;i<C.size();i++) {
              int v = (C.get((i+1)%C.size())) - C.get(i);
              if(v > 0) {
                current_dir = 1;
              }
              else if(v < 0) {
                current_dir = -1;
              }
              
              s.add(current_dir);
            }
            
            if(current_dir == 0) {
              for(int i=0;i<s.size();i++) {
                s.set(i, 1);
              }
            } else {
              for(int i=0;i<s.size();i++) {
                if(s.get(i) != 0) {
                  break;
                }
                s.set(i, current_dir);
              }
            }
            
            int k=0;
            for(int i=0;i<s.size();i++) {
              k += s.get(i);
              s.set(i, k-1);
            }
            
            int n = s.asOrdinalsUnipolar().getMax()+1;
            if(n < 2) {
              throw new RuntimeException("rhythm is empty or too small");
            }
            Sequence rnd = Sequence.genRnd(
              n, 
              (int)spinner_amp.getValue(), 
              (int)spinner_sum.getValue(), 
              (int)spinner_maxamp.getValue(), 
              chckbxExclude.isSelected());
            rnd.add(0,0);
            rnd = rnd.difference();

            for(int i=0;i<s.size();i++) {
              int index = s.get(i);
              while(index < 0) index += rnd.size();
              while(index >= rnd.size()) index -= rnd.size();
              s.set(i, rnd.get(index));
            }
            
            int _min = (Integer)spinner_bounce_min.getValue();
            int _amp = (Integer)spinner_bounce_amp.getValue();
            
            Sequence o = s.antidifference(0);
            Sequence t = o.bounceseq(_min, _amp);
            o = t.difference();
            txtDelta.setText(o.toString());
            
            Sequence o2 = o.antidifference(0);
            o2.remove(0);
            txtSequence.setText(o2.toString());
            
            break;
            
          }
        }
        catch(Exception ex) {
          JOptionPane.showMessageDialog(frmSeqgen, ex.getMessage(), "Nope", JOptionPane.INFORMATION_MESSAGE);
        }
        
        
      }
    });
    
    
    
    JLabel lblRhythm = new JLabel("Rhythm :");
    lblRhythm.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblXsMod = new JLabel("Sequence:");
    lblXsMod.setHorizontalAlignment(SwingConstants.RIGHT);
    
    
    
    JLabel lblDeltaAmp = new JLabel("delta^-1 amp:");
    lblDeltaAmp.setHorizontalAlignment(SwingConstants.RIGHT);
    
   
    
    JLabel lblDeltaMin = new JLabel("delta^-1 min:");
    lblDeltaMin.setHorizontalAlignment(SwingConstants.RIGHT);
    
    
    
    
    
    
    
    GroupLayout groupLayout = new GroupLayout(frmSeqgen.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(lblXsMod, GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
              .addGap(1)
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
              .addComponent(lblDeltaMin, GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner_bounce_min, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(lblDeltaAmp, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner_bounce_amp, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
              .addGap(10)
              .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
              .addGap(2)
              .addComponent(btnGenerate, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE))
            .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                .addComponent(lblDelta, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                .addComponent(lblRhythm, GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE))
              .addPreferredGap(ComponentPlacement.RELATED)
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                .addComponent(txtRhythm, Alignment.TRAILING)
                .addComponent(txtDelta, Alignment.TRAILING)
                .addComponent(txtSequence, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 843, Short.MAX_VALUE))))
          .addGap(20))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addContainerGap(12, Short.MAX_VALUE)
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(btnGenerate, Alignment.TRAILING)
                .addComponent(comboBox, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE))
              .addGap(6)
              .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                .addComponent(lblRhythm)
                .addComponent(txtRhythm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
              .addPreferredGap(ComponentPlacement.RELATED)
              .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                .addComponent(lblDelta)
                .addComponent(txtDelta, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
              .addPreferredGap(ComponentPlacement.RELATED)
              .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                .addComponent(txtSequence, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(lblXsMod)))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(18)
              .addComponent(lblAmp))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(16)
              .addComponent(spinner_amp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(18)
              .addComponent(lblSum))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(16)
              .addComponent(spinner_sum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(18)
              .addComponent(lblMaxamp))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(16)
              .addComponent(spinner_maxamp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(14)
              .addComponent(chckbxExclude))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(15)
              .addComponent(spinner_bounce_amp, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(18)
              .addComponent(lblDeltaAmp))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(15)
              .addComponent(spinner_bounce_min, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(18)
              .addComponent(lblDeltaMin)))
          .addGap(48))
    );
    frmSeqgen.getContentPane().setLayout(groupLayout);
  }
}
