package name.ncg777.music.applications;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import name.ncg777.maths.Numbers;
import name.ncg777.maths.objects.Sequence;
import name.ncg777.music.PitchClassSet12;

import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class PitchClassSet12Rotator {

  private JFrame frmChordRotator;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          PitchClassSet12Rotator window = new PitchClassSet12Rotator();
          window.frmChordRotator.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
  private Comparator<String> comparator = PitchClassSet12.ForteStringComparator.reversed();
  /**
   * Create the application.
   */
  public PitchClassSet12Rotator() {
    initialize();
  }
  private PitchClassSet12 getSelectedChord() {
    if(cboScale.getSelectedIndex() < 0) return null;
    return PitchClassSet12.parseForte(cboScale.getSelectedItem().toString());
  }
  private void refreshPitches() {
    if(cboScale.getSelectedIndex() < 0 || textPitches == null) return;
    PitchClassSet12 ch = getSelectedChord();
    Sequence s = ch.asSequence();
    textPitches.setText(s.toString());
  }
  
  JComboBox<String> cboScale = new JComboBox<String>();
  
  private JTextField textField;
  private JTextField textPitches;
  private JTextArea textResult = new JTextArea();
  private JSpinner spinnerInc;
  
  private void initialize() {
    frmChordRotator = new JFrame();
    frmChordRotator.setResizable(false);
    frmChordRotator.setTitle("PitchClassSet12 Rotator");
    frmChordRotator.setBounds(100, 100, 321, 410);
    frmChordRotator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmChordRotator.getContentPane().setLayout(null);
    
    JLabel lblNewLabel = new JLabel("Scale:");
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
    
    
    String[] cs = PitchClassSet12.getForteChordDict().keySet().toArray(new String[0]);
    List<String> cs0 = new ArrayList<String>();
    for(var s: cs) cs0.add(s);
    cs0.sort(comparator);
    cs = cs0.toArray(new String[0]);
    cboScale.setModel(new DefaultComboBoxModel<String>(cs));
    cboScale.setSelectedIndex(Arrays.asList(cs).indexOf("8-23.11"));
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
        PitchClassSet12 ch = getSelectedChord();
        Sequence chs = ch.asSequence();

        int n = ch.getK();
        int inc = ((int)spinnerInc.getValue())%n;
        Sequence s = Sequence.parse(textField.getText().trim());
        int k = s.size();
        StringBuilder sb = new StringBuilder();
        ArrayList<PitchClassSet12> chords = new ArrayList<>();
        for(int i=0;i<n;i++) {
          TreeSet<Integer> si = new TreeSet<>();
          for(int j=0; j<k;j++) {
            si.add(chs.get(Numbers.correctMod((s.get(j)+(i*inc)),n)));
          }
          PitchClassSet12 chx = PitchClassSet12.identify(si);
          if(chords.contains(chx)) {break;}
          chords.add(chx);
          sb.append(chx.toForteNumberString()+ "\n");
        }
        textResult.setText(sb.toString());
      }
    });
    btnNewButton.setBounds(10, 121, 290, 23);
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
    
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBounds(10, 148, 285, 212);
    frmChordRotator.getContentPane().add(scrollPane);
    scrollPane.setViewportView(textResult);
    
    
    textResult.setEditable(false);
    textResult.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
    
    spinnerInc = new JSpinner();
    spinnerInc.setModel(new SpinnerNumberModel(Integer.valueOf(1), null, null, Integer.valueOf(1)));
    spinnerInc.setBounds(134, 90, 47, 20);
    frmChordRotator.getContentPane().add(spinnerInc);
    
    JLabel lblNewLabel_6_1_1 = new JLabel("Increment:");
    lblNewLabel_6_1_1.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel_6_1_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_6_1_1.setBounds(10, 90, 116, 23);
    frmChordRotator.getContentPane().add(lblNewLabel_6_1_1);
  }
}
