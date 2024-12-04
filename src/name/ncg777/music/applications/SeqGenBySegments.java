package name.ncg777.music.applications;

import java.awt.EventQueue;

import javax.swing.JFrame;


import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import name.ncg777.maths.objects.Alphabet;
import name.ncg777.maths.objects.Sequence;
import name.ncg777.maths.objects.sentences.HexadecimalSentence;
import name.ncg777.maths.objects.sentences.OctalSentence;
import name.ncg777.maths.objects.words.BinaryWord;

import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;


public class SeqGenBySegments {

  private JFrame frmSeqGen;
  private JTextField textField;
  private JTextField textField_1;
  private JComboBox<Alphabet> comboBox = new JComboBox<Alphabet>(new DefaultComboBoxModel<Alphabet>(Alphabet.values()));
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          SeqGenBySegments window = new SeqGenBySegments();
          window.frmSeqGen.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public SeqGenBySegments() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmSeqGen = new JFrame();
    frmSeqGen.getContentPane().setBackground(Color.DARK_GRAY);
    frmSeqGen.setTitle("Sequence Generator (by segments)");
    frmSeqGen.setBounds(100, 100, 398, 169);
    frmSeqGen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmSeqGen.getContentPane().setLayout(null);
    
    JLabel lblRhythm = new JLabel("BinaryWord");
    lblRhythm.setForeground(Color.WHITE);
    lblRhythm.setHorizontalAlignment(SwingConstants.RIGHT);
    lblRhythm.setBounds(10, 11, 46, 14);
    frmSeqGen.getContentPane().add(lblRhythm);
    
    JLabel lblSeq = new JLabel("Seq");
    lblSeq.setForeground(Color.WHITE);
    lblSeq.setHorizontalAlignment(SwingConstants.RIGHT);
    lblSeq.setBounds(10, 36, 46, 14);
    frmSeqGen.getContentPane().add(lblSeq);
    
    JButton btnGenerate = new JButton("Generate");
    btnGenerate.setForeground(Color.GREEN);
    btnGenerate.setBackground(Color.BLACK);
    btnGenerate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        new Thread(() -> {
          btnGenerate.setEnabled(false);
          String str_R = textField.getText().trim();
          BinaryWord r = null;
          if(comboBox.getSelectedItem() == Alphabet.Hexadecimal) {
            r = HexadecimalSentence.parseHexadecimalWord(str_R).asBinaryWord();
          }
          if(comboBox.getSelectedItem() == Alphabet.Octal) {
            r = OctalSentence.parse(str_R).asBinary();
          }
          @SuppressWarnings("null")
          Sequence C = r.getComposition().asSequence();
          Sequence S = r.getComposition().segment().get(0).asSequence();
          
          int n = C.size();
          int m = S.size();
          int[] A = new int[m];
          int k = 0;
          for(int i=0;i<m;i++) {
            int t = 0;
            for(int j=0; j<S.get(i);j++) {
              t+= C.get(k++);
            }
            double d = (double)t/(double)S.get(i);
            if(Math.random() < 0.5){
              A[i] = (int)Math.floor(d);
            } else {
              A[i] = (int)Math.ceil(d);
            }
          }
          
          int[] D = new int[n];
          k=0;
          int s = 0;
          for(int i=0;i<m;i++) {
            int c = 1;
            if(Math.random() < 0.5){
              c = -1;
            }
            for(int j=0; j<S.get(i);j++) {

              D[k] = ((int)Math.signum(C.get(k) - A[i]))*c;
              s+= D[k];
              k++;
            }
          }
          
          Sequence O = null;
          int amp = 4;
          int max = Math.abs(s)+1;
          while(O == null) {
            try{
              O = Sequence.genRnd(m, amp, -s, max, true);
              O.add(0,0);
              O = O.difference();
            } catch(Exception e) {
              max++;
            }
          }
          

          k = 0;
          for(int i=0;i<m;i++) {
            D[k] = D[k] + O.get(i);
            k+=S.get(i);
          }
          
          String o = "";
          for(int i=0;i<n;i++) {
            o+= Integer.toString(D[i]) + " ";
          }
          textField_1.setText(o.trim());
          
          btnGenerate.setEnabled(true);
        }).start();
        
      }
    });
    btnGenerate.setBounds(10, 61, 367, 23);
    frmSeqGen.getContentPane().add(btnGenerate);
    
    textField = new JTextField();
    textField.setForeground(Color.WHITE);
    textField.setBackground(Color.BLACK);
    textField.setBounds(66, 8, 311, 20);
    frmSeqGen.getContentPane().add(textField);
    textField.setColumns(10);
    
    textField_1 = new JTextField();
    textField_1.setForeground(Color.WHITE);
    textField_1.setBackground(Color.BLACK);
    textField_1.setBounds(66, 33, 311, 20);
    frmSeqGen.getContentPane().add(textField_1);
    textField_1.setColumns(10);
    
    
    
    comboBox.setBounds(10, 95, 111, 24);
    frmSeqGen.getContentPane().add(comboBox);
  }
}
