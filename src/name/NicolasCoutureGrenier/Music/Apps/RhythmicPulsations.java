package name.NicolasCoutureGrenier.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import name.NicolasCoutureGrenier.Maths.Objects.Sequence;
import name.NicolasCoutureGrenier.Music.R12List;
import name.NicolasCoutureGrenier.Music.R16List;
import name.NicolasCoutureGrenier.Music.R48List;
import name.NicolasCoutureGrenier.Music.Rhythm;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.BitSet;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import name.NicolasCoutureGrenier.Music.Rn;

public class RhythmicPulsations {

  private JFrame frmRhythmicPulsations;
  private JTextField composition;
  private JTextField headTails;
  private JTextField durations;
  private JTextField multiples;
  private JTextField result;
  private JComboBox<Rn> comboBox = new JComboBox<Rn>(new DefaultComboBoxModel<Rn>(Rn.values()));;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          RhythmicPulsations window = new RhythmicPulsations();
          window.frmRhythmicPulsations.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public RhythmicPulsations() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmRhythmicPulsations = new JFrame();
    frmRhythmicPulsations.setResizable(false);
    frmRhythmicPulsations.setTitle("Rhythmic Pulsations");
    frmRhythmicPulsations.setBounds(100, 100, 450, 235);
    frmRhythmicPulsations.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmRhythmicPulsations.getContentPane().setLayout(null);
    
    JLabel lblNewLabel = new JLabel("Composition:");
    lblNewLabel.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setBounds(10, 5, 92, 14);
    frmRhythmicPulsations.getContentPane().add(lblNewLabel);
    
    JLabel lblheadOrtail = new JLabel("(H)ead or (T)ail:");
    lblheadOrtail.setHorizontalAlignment(SwingConstants.RIGHT);
    lblheadOrtail.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
    lblheadOrtail.setBounds(10, 30, 92, 14);
    frmRhythmicPulsations.getContentPane().add(lblheadOrtail);
    
    JLabel lblPeriods = new JLabel("Durations:");
    lblPeriods.setHorizontalAlignment(SwingConstants.RIGHT);
    lblPeriods.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
    lblPeriods.setBounds(10, 55, 92, 14);
    frmRhythmicPulsations.getContentPane().add(lblPeriods);
    
    JLabel lblLimits = new JLabel("Multiples:");
    lblLimits.setHorizontalAlignment(SwingConstants.RIGHT);
    lblLimits.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
    lblLimits.setBounds(10, 80, 92, 14);
    frmRhythmicPulsations.getContentPane().add(lblLimits);
    
    composition = new JTextField();
    composition.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
    composition.setBounds(112, 5, 312, 20);
    frmRhythmicPulsations.getContentPane().add(composition);
    composition.setColumns(10);
    
    headTails = new JTextField();
    headTails.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
    headTails.setColumns(10);
    headTails.setBounds(112, 30, 312, 20);
    frmRhythmicPulsations.getContentPane().add(headTails);
    
    durations = new JTextField();
    durations.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
    durations.setColumns(10);
    durations.setBounds(112, 55, 312, 20);
    frmRhythmicPulsations.getContentPane().add(durations);
    
    multiples = new JTextField();
    multiples.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
    multiples.setColumns(10);
    multiples.setBounds(112, 80, 312, 20);
    frmRhythmicPulsations.getContentPane().add(multiples);
    
    JButton btnNewButton = new JButton("Generate");
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          Sequence scompo = Sequence.parse(composition.getText());
          int scompo_sum = scompo.sum();
          if(comboBox.getSelectedItem() == Rn.Hex && scompo_sum%16 != 0) throw new Exception("Sum of composition must be a multiple of 16.");
          if(comboBox.getSelectedItem() == Rn.Octal && scompo_sum%12 != 0) throw new Exception("Sum of composition must be a multiple of 12.");
          if(comboBox.getSelectedItem() == Rn.Tribble && scompo_sum%48 != 0) throw new Exception("Sum of composition must be a multiple of 48.");
          
          ArrayList<String> hOrT = new ArrayList<String>();
          String[] hOrTarr = headTails.getText().split("\\s+");
          for(String s : hOrTarr) {
            if(!(s.equals("H") || s.equals("T"))) throw new Exception("H or T, nothing else");
            hOrT.add(s);
          }
          
          Sequence sdurations = Sequence.parse(durations.getText());
          for(int i=0;i<sdurations.size();i++) {
            if(sdurations.get(i) < 0) { throw new Exception("Periods must be positive"); }
          }
          Sequence smultiples = Sequence.parse(multiples.getText());
          for(int i=0;i<scompo.size();i++) {
            if(scompo.get(i) < (smultiples.get(i % smultiples.size()))*sdurations.get(i % sdurations.size())) {
              throw new Exception("Duration*Multiple must not exceed corresponding composition element.");
            }
          }
          
          Rhythm rh = Rhythm.buildRhythm(new BitSet(scompo_sum), scompo_sum);
          int acc = 0;
          
          for(int i=0; i<scompo.size(); i++) {
            int c = scompo.get(i);
            
            boolean isTail = hOrT.get(i % hOrT.size()).equals("T");
            int d = sdurations.get(i%sdurations.size());
            
            int m = smultiples.get(i%smultiples.size());
                        
            for(int j=0; j<m; j++) {
              if(!isTail) {
                rh.set(acc+j*d, true);
              } else {
                rh.set(acc + c-((j+1)*d), true); 
              }
            }
            
            acc+=c;
          }
          if(comboBox.getSelectedItem() == Rn.Hex) result.setText(R16List.fromRhythm(rh).toString());
          if(comboBox.getSelectedItem() == Rn.Octal) result.setText(R12List.fromRhythm(rh).toString());
          if(comboBox.getSelectedItem() == Rn.Tribble) result.setText(R48List.fromRhythm(rh).toString());
        } catch(Exception ex) {
          result.setText(ex.getMessage());
        }
        
      }
    });
    btnNewButton.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
    btnNewButton.setBounds(112, 108, 312, 20);
    frmRhythmicPulsations.getContentPane().add(btnNewButton);
    
    JLabel lblResult = new JLabel("Result:");
    lblResult.setHorizontalAlignment(SwingConstants.RIGHT);
    lblResult.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
    lblResult.setBounds(10, 168, 92, 14);
    frmRhythmicPulsations.getContentPane().add(lblResult);
    
    result = new JTextField();
    result.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
    result.setColumns(10);
    result.setBounds(112, 165, 312, 20);
    frmRhythmicPulsations.getContentPane().add(result);
    comboBox.setFont(new Font("Source Sans Pro", Font.PLAIN, 11));
    
    comboBox.setBounds(112, 139, 116, 20);
    frmRhythmicPulsations.getContentPane().add(comboBox);
  }
}
