package name.ncg777.maths.music.pcs12.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import name.ncg777.maths.music.pcs12.Pcs12;
import name.ncg777.maths.sequences.Sequence;

import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTextArea;

public class Permutator {

  private JFrame frmChordPermutator;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Permutator window = new Permutator();
          window.frmChordPermutator.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public Permutator() {
    initialize();
  }
  private Pcs12 getSelectedChord() {
    if(cboScale.getSelectedIndex() < 0) return null;
    return Pcs12.parseForte(cboScale.getSelectedItem().toString());
  }
  private Comparator<String> comparator = Pcs12.ForteStringComparator.reversed();
  private void refreshPitches() {
    if(cboScale.getSelectedIndex() < 0 || textPitches == null) return;
    Pcs12 ch = getSelectedChord();
    Sequence s = ch.asSequence();
    textPitches.setText(s.toString());
  }
  
  JComboBox<String> cboScale = new JComboBox<String>();
  
  private JTextField textField;
  private JTextField textPitches;
  private JTextArea textResult = new JTextArea();
  
  private void initialize() {
    frmChordPermutator = new JFrame();
    frmChordPermutator.setResizable(false);
    frmChordPermutator.setTitle("Pcs12 WordPermutator");
    frmChordPermutator.setBounds(100, 100, 321, 410);
    frmChordPermutator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmChordPermutator.getContentPane().setLayout(null);
    
    JLabel lblNewLabel = new JLabel("Pcs12:");
    lblNewLabel.setToolTipText("<html>\r\n07-26.04 Major Locrian<br/>\r\n07-28.11 Persian<br/>\r\n07-29.06 Hungarian<br/>\r\n07-38.11 Harmonic minor<br/>\r\n07-39.11 Melodic minor<br/>\r\n07-42.11 Harmonic major<br/>\r\n07-43.11 Major<br/>\r\n08-35.00 Octatonic<br/>\r\n</html>");
    lblNewLabel.setBounds(10, 15, 114, 14);
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    frmChordPermutator.getContentPane().add(lblNewLabel);
    cboScale.setBounds(134, 12, 166, 20);
    
   
    cboScale.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        refreshPitches();
      }
    });
    
    
    String[] cs = Pcs12.getForteChordDict().keySet().toArray(new String[0]);
    List<String> cs0 = new ArrayList<String>();
    for(var s:cs) cs0.add(s);
    cs0.sort(comparator);
    cs = cs0.toArray(new String[0]);
    cboScale.setModel(new DefaultComboBoxModel<String>(cs));
    cboScale.setSelectedIndex(Arrays.asList(cs).indexOf("8-23.11"));
    frmChordPermutator.getContentPane().add(cboScale);
    
    JLabel lblNewLabel_6_1 = new JLabel("S12 Permutation:");
    lblNewLabel_6_1.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel_6_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_6_1.setBounds(10, 65, 116, 23);
    frmChordPermutator.getContentPane().add(lblNewLabel_6_1);
    
    textField = new JTextField();
    textField.setText("0 1 2 3 4 5 6 7 8 9 10 11");
    textField.setBounds(135, 66, 165, 20);
    frmChordPermutator.getContentPane().add(textField);
    textField.setColumns(10);
    
    JButton btnNewButton = new JButton("Permutate");
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          Pcs12 ch = getSelectedChord();
          Pcs12 initialChord = ch;
          Sequence s = Sequence.parse(textField.getText().trim());
          
          StringBuilder sb = new StringBuilder();
          
          do {
            sb.append(ch.toForteNumberString() + "\n");
            ch = ch.S12Permutate(s);
          } while(!ch.equals(initialChord));
          
          textResult.setText(sb.toString());
        } catch(Exception ex) {
          textResult.setText(ex.getMessage());
        }
       
      }
    });
    btnNewButton.setBounds(10, 92, 290, 23);
    frmChordPermutator.getContentPane().add(btnNewButton);
    
    JLabel lblPitches = new JLabel("Pitches:");
    lblPitches.setToolTipText("<html>\r\n07-26.04 Major Locrian<br/>\r\n07-28.11 Persian<br/>\r\n07-29.06 Hungarian<br/>\r\n07-38.11 Harmonic minor<br/>\r\n07-39.11 Melodic minor<br/>\r\n07-42.11 Harmonic major<br/>\r\n07-43.11 Major<br/>\r\n08-35.00 Octatonic<br/>\r\n</html>");
    lblPitches.setHorizontalAlignment(SwingConstants.RIGHT);
    lblPitches.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblPitches.setBounds(70, 40, 56, 14);
    frmChordPermutator.getContentPane().add(lblPitches);
    
    textPitches = new JTextField();
    textPitches.setEditable(false);
    textPitches.setBounds(134, 37, 166, 20);
    refreshPitches();
    frmChordPermutator.getContentPane().add(textPitches);
    textPitches.setColumns(10);
    
    
    textResult.setEditable(false);
    textResult.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
    textResult.setBounds(10, 126, 290, 233);
    frmChordPermutator.getContentPane().add(textResult);
  }
}
