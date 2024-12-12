package name.ncg777.maths.music.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.maths.phrases.QuartalWordsPhrase;
import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.words.Alphabet;
import name.ncg777.statistics.RandomNumberGenerator;

import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class SeqGenContourFollow {

  private JFrame frmSeqgen;
  private JTextField txtDelta;
  private JTextField txtRhythm;
  private JTextField txtSequence;
  private JComboBox<Alphabet.Name> comboBox = new JComboBox<Alphabet.Name>(new DefaultComboBoxModel<Alphabet.Name>(Alphabet.Name.values()));
  JSpinner spinner_bounce_amp = new JSpinner(new SpinnerNumberModel(12, 1, null, 1));
  JSpinner spinner_bounce_min = new JSpinner(new SpinnerNumberModel(0, null, null, 1));
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          SeqGenContourFollow window = new SeqGenContourFollow();
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
  public SeqGenContourFollow() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmSeqgen = new JFrame();
    frmSeqgen.setResizable(false);
    frmSeqgen.setTitle("Sequence Generator (follows rhythm contour)");
    frmSeqgen.setBounds(100, 100, 958, 190);
    frmSeqgen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JLabel lblAmp = new JLabel("amp:");
    lblAmp.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JSpinner spinner_amp = new JSpinner();
    spinner_amp.setModel(new SpinnerNumberModel(4, 1, null, 1));
    
    JLabel lblMaxamp = new JLabel("maxamp:");
    
    JSpinner spinner_maxamp = new JSpinner();
    spinner_maxamp.setModel(new SpinnerNumberModel(7, 1, null, 1));
    
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
        new Thread(() -> {
          btnGenerate.setEnabled(false);
          try {
            while(true)
            {
              String str_R = txtRhythm.getText().trim();
              
              Sequence C = new QuartalWordsPhrase(
                  (Alphabet.Name)comboBox.getSelectedItem(), 
                  str_R
              ).toWord().toBinaryWord().getComposition().asSequence();;
              
              Sequence s = C.cyclicalDifference().signs().circularHoldNonZero();
              
              int k=0;
              for(int i=0;i<s.size();i++) {
                k += s.get(i);
                s.set(i, k-1);
              }
              
              int n = s.asOrdinalsUnipolar().getMax();
              if(n < 2) {
                throw new RuntimeException("rhythm is empty or too small");
              }
              var maxamp = (int)spinner_maxamp.getValue();
              int sum = RandomNumberGenerator.nextInt(maxamp);
              Sequence rnd = Sequence.genRnd(
                n, 
                (int)spinner_amp.getValue(), 
                sum, 
                maxamp, 
                chckbxExclude.isSelected()).cyclicalDifference();

              for(int i=0;i<s.size();i++) {
                int index = s.get(i);
                while(index < 0) index += rnd.size();
                while(index >= rnd.size()) index -= rnd.size();
                s.set(i, rnd.get(index));
              }
              
              int _min = (Integer)spinner_bounce_min.getValue();
              int _amp = (Integer)spinner_bounce_amp.getValue();
              
              Sequence o = s.cyclicalAntidifference(0);
              txtDelta.setText(o.bounceseq(_min, _amp).cyclicalDifference().toString());
              txtSequence.setText(o.bounceseq(_min, _amp).toString());
              
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
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(lblXsMod, GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
              .addGap(1)
              .addComponent(lblAmp)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner_amp, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(lblMaxamp)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner_maxamp, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(chckbxExclude)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(lblDeltaMin, GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner_bounce_min, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(lblDeltaAmp, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner_bounce_amp, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(btnGenerate, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE))
            .addGroup(groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                .addComponent(lblDelta, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                .addComponent(lblRhythm, GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE))
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
            .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
              .addContainerGap()
              .addComponent(btnGenerate)
              .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
              .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                .addComponent(spinner_amp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(lblMaxamp)
                .addComponent(spinner_maxamp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(chckbxExclude)
                .addComponent(lblDeltaMin)
                .addComponent(spinner_bounce_min, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                .addComponent(lblDeltaAmp)
                .addComponent(spinner_bounce_amp, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE))))
          .addGap(48))
    );
    frmSeqgen.getContentPane().setLayout(groupLayout);
  }
}
