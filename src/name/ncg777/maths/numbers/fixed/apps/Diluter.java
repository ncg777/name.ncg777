package name.ncg777.maths.numbers.fixed.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import name.ncg777.maths.Numbers;
import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.numbers.fixed.Quartal;
import name.ncg777.maths.numbers.BinaryNatural;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.BitSet;
import java.awt.event.ActionEvent;

public class Diluter {

  private JFrame frmRhythmDiluter;
  private JTextField textRhythm;
  private JTextField textResult;
  JComboBox<Cipher.Name> comboBox = new JComboBox<Cipher.Name>(new DefaultComboBoxModel<Cipher.Name>(Cipher.Name.values()));
  JSpinner spinnerFrom = new JSpinner(new SpinnerNumberModel(1, 1, null, 1));
  JSpinner spinnerOffset = new JSpinner();
  JSpinner spinnerTo = new JSpinner(new SpinnerNumberModel(2, 2, null, 1));
  
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Diluter window = new Diluter();
          window.frmRhythmDiluter.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public Diluter() {
    initialize();
  }

  private void initialize() {
    frmRhythmDiluter = new JFrame();
    frmRhythmDiluter.setResizable(false);
    frmRhythmDiluter.setTitle("Rhythm Diluter");
    frmRhythmDiluter.setBounds(100, 100, 644, 203);
    frmRhythmDiluter.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmRhythmDiluter.getContentPane().setLayout(null);
    
    comboBox.setBounds(129, 6, 114, 20);
    frmRhythmDiluter.getContentPane().add(comboBox);
    
    JLabel lblNewLabel = new JLabel("Rhythm:");
    lblNewLabel.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setBounds(10, 37, 114, 20);
    frmRhythmDiluter.getContentPane().add(lblNewLabel);
    
    textRhythm = new JTextField();
    textRhythm.setBounds(129, 37, 479, 20);
    frmRhythmDiluter.getContentPane().add(textRhythm);
    textRhythm.setColumns(10);
    
    JLabel lblNewLabel_1 = new JLabel("From:");
    lblNewLabel_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel_1.setBounds(129, 68, 43, 14);
    frmRhythmDiluter.getContentPane().add(lblNewLabel_1);
    
    
   
    spinnerFrom.setBounds(180, 68, 43, 17);
    frmRhythmDiluter.getContentPane().add(spinnerFrom);
    
    JLabel lblNewLabel_2 = new JLabel("To:");
    lblNewLabel_2.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel_2.setBounds(233, 68, 31, 14);
    frmRhythmDiluter.getContentPane().add(lblNewLabel_2);
    
    
    spinnerTo.setBounds(271, 68, 50, 17);
    frmRhythmDiluter.getContentPane().add(spinnerTo);
    
    JLabel lblNewLabel_3 = new JLabel("Offset:");
    lblNewLabel_3.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel_3.setBounds(326, 68, 55, 14);
    frmRhythmDiluter.getContentPane().add(lblNewLabel_3);
    
    
    spinnerOffset.setBounds(386, 66, 50, 17);
    frmRhythmDiluter.getContentPane().add(spinnerOffset);
    
    textResult = new JTextField();
    textResult.setBounds(129, 121, 479, 20);
    frmRhythmDiluter.getContentPane().add(textResult);
    textResult.setColumns(10);
    
    JLabel lblNewLabel_4 = new JLabel("Result:");
    lblNewLabel_4.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_4.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel_4.setBounds(45, 124, 79, 14);
    frmRhythmDiluter.getContentPane().add(lblNewLabel_4);
    
    JButton btnNewButton = new JButton("Dilute");
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dilute();
      }
    });
    btnNewButton.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    btnNewButton.setBounds(129, 93, 479, 20);
    frmRhythmDiluter.getContentPane().add(btnNewButton);
  }
  
  private void dilute() {
    var abc = (Cipher.Name)comboBox.getSelectedItem();
    var alphabet = Cipher.getAlphabet(abc);
    var str = textRhythm.getText().replaceAll("\\s+", "");
    BinaryNatural r = Quartal.instance.newNaturalSequence(
        abc, 
        str).toBinaryNatural();
 
    int n = r.getN();
    
    int from = (int)spinnerFrom.getValue();
    int to = (int)spinnerTo.getValue();
    int offset = (int)spinnerOffset.getValue();
    
    if(Numbers.lcm(n, from) != n) {
      textResult.setText("'From' must divide the length of the rhythm.");
      return;
    }
    
    if(((n/from)*to)% alphabet.size()  != 0) {
      textResult.setText("Length of rhythm 'to' / 'from' is not a multiple of " + alphabet.size());
      return;
    }
    
    if(offset < 0 || offset > (to-from)) {
      textResult.setText("Offset must be less than 'to' - 'from'");
      return;
    }
    
    int newLength = n * (to/from);
    
    BinaryNatural o = new BinaryNatural(new BitSet(), newLength);
    
    for(int i=0; i<(n/from);i++) {
      for(int j=0;j<from;j++) {
        o.set((i*to) + j + offset, r.reverse().get((i*from)+j));
      }
    }
    
    textResult.setText((Quartal.instance.newNaturalSequence(abc, o.reverse())).toString());
  }
}
