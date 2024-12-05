package name.ncg777.maths.music.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.maths.sentences.HexadecimalSentence;
import name.ncg777.maths.sentences.OctalSentence;
import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.sequences.predicates.PredicatedSeqRhythms;
import name.ncg777.maths.words.Alphabet;
import name.ncg777.maths.words.BinaryWord;
import name.ncg777.maths.words.predicates.LowEntropy;

import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class SeqGenFS {

  private JFrame frmSeqgen;
  private JTextField txtDelta;
  private JTextField txtRhythm;
  private JTextField txtSequence;
  private JComboBox<Alphabet> comboBox = new JComboBox<Alphabet>(new DefaultComboBoxModel<Alphabet>(Alphabet.values()));
  JCheckBox chckbxF = new JCheckBox("F");
  JCheckBox chckbxS = new JCheckBox("S");
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          SeqGenFS window = new SeqGenFS();
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
  public SeqGenFS() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmSeqgen = new JFrame();
    frmSeqgen.setResizable(false);
    frmSeqgen.setTitle("Sequence Generator FS (Factors & Subsequence words)");
    frmSeqgen.setBounds(100, 100, 955, 193);
    frmSeqgen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JLabel lblAmp = new JLabel("amp:");
    lblAmp.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JSpinner spinner_amp = new JSpinner();
    spinner_amp.setModel(new SpinnerNumberModel(4, 1, null, 1));
    JSpinner spinner_bounce_amp = new JSpinner(new SpinnerNumberModel(12, 1, null, 1));
    JLabel lblMaxamp = new JLabel("maxamp:");
    JSpinner spinner_bounce_min = new JSpinner(new SpinnerNumberModel(0, null, null, 1));
    JSpinner spinner_maxamp = new JSpinner();
    spinner_maxamp.setModel(new SpinnerNumberModel(7, 1, null, 1));
    
    JLabel lblDelta = new JLabel("Delta sequence:");
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
            try {
              btnGenerate.setEnabled(false);
              while(true)
              {
                String str_R = txtRhythm.getText().trim();
                
                BinaryWord R = null;
                if(comboBox.getSelectedItem() == Alphabet.Hexadecimal) {
                  HexadecimalSentence r = HexadecimalSentence.parseHexadecimalWord(str_R);
                  R = r.asBinaryWord();
                }
                if(comboBox.getSelectedItem() == Alphabet.Octal) {
                  OctalSentence r = OctalSentence.parse(str_R);
                  R = r.asBinary();
                }
                
                Sequence s;
                var pred = new PredicatedSeqRhythms(new LowEntropy());
                do {
                  s = Sequence.genRndOnRhythm(R,(int)spinner_amp.getValue(), (int)spinner_maxamp.getValue(), chckbxF.isSelected(), chckbxS.isSelected());
                } while(!pred.apply(s));
                
                int _min = (Integer)spinner_bounce_min.getValue();
                int _amp = (Integer)spinner_bounce_amp.getValue();
  
                Sequence o = s;
                Sequence t = o.bounceseq(_min, _amp);
                t.add(0,0);
                o = t.difference();
                txtDelta.setText(o.toString());
                
                Sequence o2 = o.antidifference(0);
                o2.remove(0);
                txtSequence.setText(o2.toString());
                
                break;
              } 
            } catch(Exception ex) {
              JOptionPane.showMessageDialog(frmSeqgen, ex.getMessage(), "Nope", JOptionPane.INFORMATION_MESSAGE);
            }
            btnGenerate.setEnabled(true);
          }).start();
        }
      });
    
    JLabel lblRhythm = new JLabel("BinaryWord :");
    lblRhythm.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblXsMod = new JLabel("Sequence:");
    lblXsMod.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblDeltaMin = new JLabel("delta^-1 min:");
    lblDeltaMin.setHorizontalAlignment(SwingConstants.RIGHT);
    
    
    
    
    JLabel lblDeltaAmp = new JLabel("delta^-1 amp:");
    lblDeltaAmp.setHorizontalAlignment(SwingConstants.RIGHT);
    
    
    chckbxF.setSelected(true);
    
    
    chckbxS.setSelected(true);
    
    
    
    
    
    
    
    
    
    GroupLayout groupLayout = new GroupLayout(frmSeqgen.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(18)
              .addComponent(lblAmp)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner_amp, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(lblMaxamp)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner_maxamp, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(chckbxF, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(chckbxS, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(lblDeltaMin, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner_bounce_min, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(lblDeltaAmp, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner_bounce_amp, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
              .addGap(27)
              .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(btnGenerate, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
                .addComponent(lblRhythm, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblDelta, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblXsMod, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 99, GroupLayout.PREFERRED_SIZE))
              .addGap(3)
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(txtDelta, GroupLayout.DEFAULT_SIZE, 817, Short.MAX_VALUE)
                .addComponent(txtRhythm)
                .addComponent(txtSequence))))
          .addGap(20))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(13)
              .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(16)
              .addComponent(spinner_bounce_amp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(18)
              .addComponent(lblDeltaAmp)
              .addPreferredGap(ComponentPlacement.RELATED))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(16)
              .addComponent(spinner_bounce_min, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(18)
              .addComponent(lblDeltaMin)
              .addPreferredGap(ComponentPlacement.RELATED))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(15)
              .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                .addComponent(chckbxF)
                .addComponent(chckbxS, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
              .addPreferredGap(ComponentPlacement.RELATED))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(18)
              .addComponent(spinner_maxamp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(21)
              .addComponent(lblMaxamp)
              .addPreferredGap(ComponentPlacement.RELATED))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(20)
              .addComponent(spinner_amp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(23)
              .addComponent(lblAmp))
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(13)
              .addComponent(btnGenerate)))
          .addGap(17)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(txtRhythm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblRhythm))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(txtDelta, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblDelta))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblXsMod)
            .addComponent(txtSequence, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addGap(20))
    );
    frmSeqgen.getContentPane().setLayout(groupLayout);
  }
}
