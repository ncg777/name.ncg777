package name.NicolasCoutureGrenier.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence;
import name.NicolasCoutureGrenier.Music.R12List;
import name.NicolasCoutureGrenier.Music.R16List;
import name.NicolasCoutureGrenier.Music.R48List;
import name.NicolasCoutureGrenier.Music.Rhythm;
import name.NicolasCoutureGrenier.Music.Rn;

import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.TreeSet;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;

public class MeasureGateSequence {

  private JFrame frmMeasureGateSequence;
  private JTextField textSeq;
  private JTextArea textResult;
  private JComboBox<Rn> comboBox = new JComboBox<Rn>(new DefaultComboBoxModel<Rn>(Rn.values()));
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          MeasureGateSequence window = new MeasureGateSequence();
          window.frmMeasureGateSequence.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public MeasureGateSequence() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmMeasureGateSequence = new JFrame();
    frmMeasureGateSequence.setTitle("Measure Gate Sequence");
    frmMeasureGateSequence.setBounds(100, 100, 450, 317);
    frmMeasureGateSequence.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmMeasureGateSequence.getContentPane().setLayout(null);
    
    JLabel lblNewLabel = new JLabel("Sequence:");
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setBounds(10, 35, 63, 14);
    frmMeasureGateSequence.getContentPane().add(lblNewLabel);
    
    textSeq = new JTextField();
    textSeq.setBounds(74, 32, 350, 20);
    frmMeasureGateSequence.getContentPane().add(textSeq);
    textSeq.setColumns(10);
    
    JButton btnGenerate = new JButton("Generate");
    btnGenerate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Sequence s = Sequence.parse(textSeq.getText().trim());
        if(s.getMin() < 0) {textResult.setText("invalid input");}
        
        int nRhythms = Double.valueOf(Math.ceil(Math.log10(s.getMax()+1)/Math.log10(2.0))).intValue();
        
        int n = s.size();
        
        var ro = new ArrayList<Rhythm>();
        var typ = (Rn)comboBox.getSelectedItem();
        int mult = typ == Rn.Hex ? 16 : typ == Rn.Octal ? 12 : 48;
        
        Rhythm empty = Rhythm.buildRhythm(new BitSet(),mult);
        Rhythm full = Rhythm.buildRhythm(new BitSet(),mult);
        full.set(0, mult, true);
        
        for(int i=0;i<nRhythms;i++) ro.add(Rhythm.buildRhythm(new BitSet(), 0));
        
        for(int i=0;i<n;i++) {
          int v = s.get(i);
          
          for(int j=0;j<nRhythms;j++) {
            int p = Long.valueOf(Math.round(Math.pow(2.0,j))).intValue();
            
            if((v & p) == p) { ro.set(j, ro.get(j).juxtapose(full));}
            else { ro.set(j, ro.get(j).juxtapose(empty));}
          }
        }
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<nRhythms;i++) {
          switch(typ) {
            case Hex:
              sb.append(R16List.fromRhythm(ro.get(i)).toString() + "\n");
              break;
            case Octal:
              sb.append(R12List.fromRhythm(ro.get(i)).toString() + "\n");
              break;
            
            case Tribble:
              sb.append(R48List.fromRhythm(ro.get(i)).toString() + "\n");
              break;
          }   
        }
        textResult.setText(sb.toString());
          
        
      }
    });
    btnGenerate.setBounds(74, 60, 350, 20);
    frmMeasureGateSequence.getContentPane().add(btnGenerate);
    
    JLabel lblNewLabel_1 = new JLabel("Result:");
    lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel_1.setBounds(27, 91, 46, 20);
    frmMeasureGateSequence.getContentPane().add(lblNewLabel_1);
    
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBounds(74, 91, 350, 183);
    frmMeasureGateSequence.getContentPane().add(scrollPane);
    
    textResult = new JTextArea();
    scrollPane.setViewportView(textResult);
    
    
    comboBox.setBounds(290, 0, 134, 22);
    frmMeasureGateSequence.getContentPane().add(comboBox);
  }
}
