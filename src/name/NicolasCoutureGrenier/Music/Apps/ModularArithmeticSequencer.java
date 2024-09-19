package name.NicolasCoutureGrenier.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;

import name.NicolasCoutureGrenier.Maths.Objects.Sequence;
import name.NicolasCoutureGrenier.Music.R16List;
import name.NicolasCoutureGrenier.Music.Rhythm;

import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.awt.event.ActionEvent;
import javax.swing.SpinnerListModel;
import javax.swing.JScrollPane;

public class ModularArithmeticSequencer {

  private JFrame frmModularArithmeticSequencer;
  private JTextField textField;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          ModularArithmeticSequencer window = new ModularArithmeticSequencer();
          window.frmModularArithmeticSequencer.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public ModularArithmeticSequencer() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmModularArithmeticSequencer = new JFrame();
    frmModularArithmeticSequencer.setResizable(false);
    frmModularArithmeticSequencer.setTitle("Modular Arithmetic Sequencer");
    frmModularArithmeticSequencer.setBounds(100, 100, 456, 471);
    frmModularArithmeticSequencer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmModularArithmeticSequencer.getContentPane().setLayout(null);
    
    JLabel lblNewLabel = new JLabel("Mods sequence:");
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel.setBounds(10, 11, 112, 14);
    frmModularArithmeticSequencer.getContentPane().add(lblNewLabel);
    
    textField = new JTextField();
    textField.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textField.setBounds(132, 8, 292, 20);
    frmModularArithmeticSequencer.getContentPane().add(textField);
    textField.setColumns(10);
    
    JSpinner spinner = new JSpinner();
    spinner.setModel(new SpinnerListModel(new String[] {"16", "32", "64", "128", "256", "512", "1024", "2048", "4096"}));
    spinner.setBounds(132, 39, 72, 20);
    frmModularArithmeticSequencer.getContentPane().add(spinner);
    
    JLabel lblN = new JLabel("N:");
    lblN.setHorizontalAlignment(SwingConstants.RIGHT);
    lblN.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblN.setBounds(10, 42, 112, 14);
    frmModularArithmeticSequencer.getContentPane().add(lblN);
    JTextArea textArea = new JTextArea();
    JButton btnNewButton = new JButton("Generate");
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int n = Integer.valueOf(spinner.getValue().toString());
        Sequence mods = Sequence.parse(textField.getText().trim());
        Integer[] modsa = mods.toArray(new Integer[0]);
        Arrays.sort(modsa);;
        Sequence o = new Sequence();

        for(int i=0;i<n;i++) {
          int t = i;
          
          for(int j=modsa.length-1;j>=0;j--) {
            t = t % modsa[j];
          }
          o.add(t);
          
        }
        
        var distinct = o.distinct();
        
        ArrayList<R16List> rs = new ArrayList<>();
        
        for(int i: distinct) {
          Rhythm r = Rhythm.buildRhythm(new BitSet(), n);
          
          for(int j=0;j<n;j++) {
            if(o.get(j).equals(i)) {r.set(j, true);}
          }
          rs.add(R16List.fromRhythm(r));
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(o.toString() + "\n");
        for(R16List rl : rs) {
          sb.append(rl.toString() + "\n");
        }
        
        textArea.setText(sb.toString().trim());
      }
    });
    btnNewButton.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    btnNewButton.setBounds(10, 72, 414, 29);
    frmModularArithmeticSequencer.getContentPane().add(btnNewButton);
    
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBounds(10, 115, 414, 304);
    frmModularArithmeticSequencer.getContentPane().add(scrollPane);
    
    
    scrollPane.setViewportView(textArea);
  }
}
