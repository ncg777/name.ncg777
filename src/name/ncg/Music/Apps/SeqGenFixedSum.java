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
import name.ncg.Music.SequencePredicates.PredicatedSeqRhythms;

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
import name.ncg.Music.RhythmPredicates.EntropicDispersion;
import name.ncg.Music.RhythmPredicates.MaximizeQuality;

public class SeqGenFixedSum {

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
          SeqGenFixedSum window = new SeqGenFixedSum();
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
  public SeqGenFixedSum() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmSeqgen = new JFrame();
    frmSeqgen.setResizable(false);
    frmSeqgen.setTitle("Sequence Generator (fixed sum)");
    frmSeqgen.setBounds(100, 100, 1012, 166);
    frmSeqgen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblAmp = new JLabel("amp:");
    
    JSpinner spinner_amp = new JSpinner();
    spinner_amp.setModel(new SpinnerNumberModel(new Integer(4), new Integer(1), null, new Integer(1)));
    
    JLabel lblSum = new JLabel("sum:");
    
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
    
    
    
    JLabel lblRhythm = new JLabel("Rhythm :");
    lblRhythm.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblXsMod = new JLabel("Sequence:");
    lblXsMod.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblDeltaMin = new JLabel("delta^-1 min:");
    lblDeltaMin.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblDeltaAmp = new JLabel("delta^-1 amp:");
    lblDeltaAmp.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JButton btnGenerate = new JButton("Generate");
    
    btnGenerate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        new Thread(() -> {
          try {
            btnGenerate.setEnabled(false);
            while(true)
            {
              String str_R = txtRhythm.getText().trim();
                         
              int n = -1;
              if(comboBox.getSelectedItem() == Rn.Hex) {
                n = R16List.parseR16Seq(str_R).asRhythm().getK();
              }
              if(comboBox.getSelectedItem() == Rn.Octal) {
                n = R12List.parseR12Seq(str_R).asRhythm().getK();
              }
              if(n < 2) {
                throw new RuntimeException("rhythm is empty or too small");
              };
              var pred = new PredicatedSeqRhythms(new EntropicDispersion());
              Sequence rnd;
              while(true) {
                 rnd = Sequence.genRnd(
                  n, 
                  (int)spinner_amp.getValue(), 
                  (int)spinner_sum.getValue(), 
                  (int)spinner_maxamp.getValue(), 
                  chckbxExclude.isSelected());
                 
                 if(pred.apply(rnd)) break;
              }
              
              int _min = (Integer)spinner_bounce_min.getValue();
              int _amp = (Integer)spinner_bounce_amp.getValue();
              
              Sequence o = rnd;
              
              Sequence t = o.bounceseq(_min, _amp);
              t.add(0,0);
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
          btnGenerate.setEnabled(true);
        }).start();
        
        
        
      }
    });
    
    
    
    GroupLayout groupLayout = new GroupLayout(frmSeqgen.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(67)
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
              .addGap(11)
              .addComponent(lblDeltaMin, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner_bounce_min, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(lblDeltaAmp, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner_bounce_amp, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(btnGenerate, GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE))
            .addGroup(groupLayout.createSequentialGroup()
              .addContainerGap()
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(lblDelta, GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                .addComponent(lblRhythm, GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                .addComponent(lblXsMod, GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE))
              .addPreferredGap(ComponentPlacement.RELATED)
              .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                .addComponent(txtRhythm, GroupLayout.DEFAULT_SIZE, 893, Short.MAX_VALUE)
                .addComponent(txtDelta, GroupLayout.DEFAULT_SIZE, 893, Short.MAX_VALUE)
                .addComponent(txtSequence, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 893, Short.MAX_VALUE))))
          .addGap(32))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblAmp)
            .addComponent(spinner_amp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblSum)
            .addComponent(spinner_sum, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblMaxamp)
            .addComponent(spinner_maxamp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(chckbxExclude)
            .addComponent(lblDeltaMin)
            .addComponent(spinner_bounce_min, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblDeltaAmp)
            .addComponent(spinner_bounce_amp, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
            .addComponent(btnGenerate))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(txtRhythm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblRhythm))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblDelta)
            .addComponent(txtDelta, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblXsMod)
            .addComponent(txtSequence, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addContainerGap(25, Short.MAX_VALUE))
    );
    frmSeqgen.getContentPane().setLayout(groupLayout);
  }
}
