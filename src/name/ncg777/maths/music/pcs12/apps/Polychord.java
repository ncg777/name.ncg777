package name.ncg777.maths.music.pcs12.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.google.common.base.Joiner;

import name.ncg777.maths.music.pcs12.Pcs12;
import name.ncg777.maths.sequences.Sequence;

import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.SpinnerNumberModel;
import javax.swing.JComboBox;

public class Polychord {

  private JFrame frmPolychord;
  private JTextField textChords;
  private JTextField textResult;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Polychord window = new Polychord();
          window.frmPolychord.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
  private Comparator<String> comparator = Pcs12.ForteStringComparator.reversed();
  private JComboBox<String> comboBox;
  /**
   * Create the application.
   */
  public Polychord() {
    initialize();
  }
  //private Comparator<String> comparator = Pcs12.ForteStringComparator.reversed();
  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmPolychord = new JFrame();
    frmPolychord.setTitle("Polychord");
    frmPolychord.setBounds(100, 100, 450, 204);
    frmPolychord.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmPolychord.getContentPane().setLayout(null);
    
    JLabel lblNewLabel = new JLabel("Chords:");
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setBounds(10, 38, 46, 14);
    frmPolychord.getContentPane().add(lblNewLabel);
    
    textChords = new JTextField();
    textChords.setBounds(66, 35, 358, 20);
    frmPolychord.getContentPane().add(textChords);
    textChords.setColumns(10);
    
    JLabel lblNewLabel_2 = new JLabel("Result:");
    lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel_2.setBounds(10, 98, 56, 14);
    frmPolychord.getContentPane().add(lblNewLabel_2);
    
    JButton btnCalc = new JButton("Calc");
    btnCalc.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        var scale = Pcs12.parseForte(comboBox.getSelectedItem().toString());
        
        var scaleseq = scale.asSequence();
        var shift = scaleseq.indexOf(scale.getTranspose());
        final var scaleseqrot = scaleseq.rotate(-shift);
        var chstr = textChords.getText().trim().split("\\s+");
        var chlist = new ArrayList<Pcs12>();
        for(int i = 0;i<chstr.length;i++) {chlist.add(Pcs12.parseForte(chstr[i]));}
        
        ArrayList<Pcs12> orig = new ArrayList<Pcs12>();
        for(var c : chlist) orig.add(c);
        
        int o = 0;
        for(int i=0;i<chlist.size();i++) {
          int mult = (int)Math.round(Math.pow(2, i*scale.getK()));
          var ch = chlist.get(i).asSequence();
          var indexes = new ArrayList<Integer>(ch.stream().map((v) -> scaleseqrot.indexOf(v)).toList());
          int k = 0;
          for(var x : indexes) {
            if(x==-1) continue;
            k+= (int)Math.round(Math.pow(2.0, x));
          }
          o += k*mult;
        }
        
        
        textResult.setText(Integer.toString(o));
        
      }
    });
    btnCalc.setBounds(10, 66, 414, 23);
    frmPolychord.getContentPane().add(btnCalc);
    
    textResult = new JTextField();
    textResult.setBounds(76, 97, 348, 17);
    frmPolychord.getContentPane().add(textResult);
    textResult.setColumns(10);
    
    JLabel lblScale = new JLabel("Scale:");
    lblScale.setHorizontalAlignment(SwingConstants.RIGHT);
    lblScale.setBounds(10, 13, 46, 14);
    frmPolychord.getContentPane().add(lblScale);
    
    
    
    List<String> s0 = new ArrayList<String>();
    
    for(Pcs12 x : Pcs12.getChords())s0.add(x.toForteNumberString());
    s0.sort(comparator);
    String[] s = s0.toArray(new String[0]);
    comboBox = new JComboBox<String>(new DefaultComboBoxModel<String>(s));
    comboBox.setBounds(66, 9, 172, 18);
    comboBox.setSelectedIndex(0);
    frmPolychord.getContentPane().add(comboBox);
  }
}
