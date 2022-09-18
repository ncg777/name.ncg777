package name.ncg.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.TreeSet;

import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Music.PCS12;

import javax.swing.JTextArea;

public class ChordRotator {

  private JFrame frmChordRotator;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          ChordRotator window = new ChordRotator();
          window.frmChordRotator.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public ChordRotator() {
    initialize();
  }
  private PCS12 getSelectedChord() {
    if(cboScale.getSelectedIndex() < 0) return null;
    return PCS12.parse(cboScale.getSelectedItem().toString());
  }
  private void refreshPitches() {
    if(cboScale.getSelectedIndex() < 0 || textPitches == null) return;
    PCS12 ch = getSelectedChord();
    Sequence s = ch.asSequence();
    textPitches.setText(s.toString().replaceAll("[)(]", ""));
  }
  
  JComboBox<String> cboScale = new JComboBox<String>();
  
  private JTextField textField;
  private JTextField textPitches;
  private JTextArea textResult = new JTextArea();
  
  private void initialize() {
    frmChordRotator = new JFrame();
    frmChordRotator.setResizable(false);
    frmChordRotator.setTitle("PCS12 Rotator");
    frmChordRotator.setBounds(100, 100, 321, 410);
    frmChordRotator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmChordRotator.getContentPane().setLayout(null);
    
    JLabel lblNewLabel = new JLabel("Scale:");
    lblNewLabel.setToolTipText("<html>\r\n07-26.04 Major Locrian<br/>\r\n07-28.11 Persian<br/>\r\n07-29.06 Hungarian<br/>\r\n07-38.11 Harmonic minor<br/>\r\n07-39.11 Melodic minor<br/>\r\n07-42.11 Harmonic major<br/>\r\n07-43.11 Major<br/>\r\n08-35.00 Octatonic<br/>\r\n</html>");
    lblNewLabel.setBounds(10, 15, 114, 14);
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    frmChordRotator.getContentPane().add(lblNewLabel);
    cboScale.setBounds(134, 12, 166, 20);
    
   
    cboScale.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        refreshPitches();
      }
    });
    
    
    String[] cs = PCS12.getChordDict().keySet().toArray(new String[0]);
    Arrays.sort(cs);
    cboScale.setModel(new DefaultComboBoxModel<String>(cs));
    cboScale.setSelectedIndex(Arrays.asList(cs).indexOf("07-43.11"));
    frmChordRotator.getContentPane().add(cboScale);
    
    JLabel lblNewLabel_6_1 = new JLabel("Index sequence:");
    lblNewLabel_6_1.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel_6_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_6_1.setBounds(10, 65, 116, 23);
    frmChordRotator.getContentPane().add(lblNewLabel_6_1);
    
    textField = new JTextField();
    textField.setText("0 2 4");
    textField.setBounds(135, 66, 165, 20);
    frmChordRotator.getContentPane().add(textField);
    textField.setColumns(10);
    
    JButton btnNewButton = new JButton("Generate");
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PCS12 ch = getSelectedChord();
        Sequence chs = ch.asSequence();

        int n = ch.getK();
        Sequence s = Sequence.parse(textField.getText().trim());
        int k = s.size();
        StringBuilder sb = new StringBuilder();
        
        for(int i=0;i<n;i++) {
          TreeSet<Integer> si = new TreeSet<>();
          for(int j=0; j<k;j++) {
            si.add(chs.get((s.get(j)+i)%n));
          }
          
          sb.append(PCS12.identify(si).toString() + "\n");
        }
        textResult.setText(sb.toString());
      }
    });
    btnNewButton.setBounds(10, 92, 290, 23);
    frmChordRotator.getContentPane().add(btnNewButton);
    
    JLabel lblPitches = new JLabel("Pitches:");
    lblPitches.setToolTipText("<html>\r\n07-26.04 Major Locrian<br/>\r\n07-28.11 Persian<br/>\r\n07-29.06 Hungarian<br/>\r\n07-38.11 Harmonic minor<br/>\r\n07-39.11 Melodic minor<br/>\r\n07-42.11 Harmonic major<br/>\r\n07-43.11 Major<br/>\r\n08-35.00 Octatonic<br/>\r\n</html>");
    lblPitches.setHorizontalAlignment(SwingConstants.RIGHT);
    lblPitches.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblPitches.setBounds(70, 40, 56, 14);
    frmChordRotator.getContentPane().add(lblPitches);
    
    textPitches = new JTextField();
    textPitches.setEditable(false);
    textPitches.setBounds(134, 37, 166, 20);
    refreshPitches();
    frmChordRotator.getContentPane().add(textPitches);
    textPitches.setColumns(10);
    
    
    textResult.setEditable(false);
    textResult.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
    textResult.setBounds(10, 126, 290, 233);
    frmChordRotator.getContentPane().add(textResult);
  }
}
