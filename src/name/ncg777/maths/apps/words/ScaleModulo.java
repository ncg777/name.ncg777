package name.ncg777.maths.apps.words;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;

import name.ncg777.maths.sentences.TetragraphSentence;
import name.ncg777.maths.words.Alphabet;

public class ScaleModulo {

  private JFrame frmScaleModulo;
  private JTextField textField;
  private final JSpinner k = new JSpinner();
  private JTextField txtOutput;

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          ScaleModulo window = new ScaleModulo();
          window.frmScaleModulo.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public ScaleModulo() {
    initialize();
  }

  private void initialize() {
    frmScaleModulo = new JFrame();
    frmScaleModulo.setTitle("Scale Modulo");
    frmScaleModulo.setBounds(100, 100, 400, 143);
    frmScaleModulo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmScaleModulo.getContentPane().setLayout(null);
    
    JLabel lblRhythm = new JLabel("Hexadecimal :");
    lblRhythm.setHorizontalAlignment(SwingConstants.RIGHT);
    lblRhythm.setBounds(1, 10, 54, 23);
    frmScaleModulo.getContentPane().add(lblRhythm);
    
    textField = new JTextField();
    textField.setBounds(64, 11, 152, 20);
    frmScaleModulo.getContentPane().add(textField);
    textField.setColumns(10);
    
    JLabel lblK = new JLabel("K :");
    lblK.setBounds(226, 11, 20, 20);
    frmScaleModulo.getContentPane().add(lblK);
    k.setBounds(250, 10, 40, 23);
    frmScaleModulo.getContentPane().add(k);
    
    JLabel lblN = new JLabel("N :");
    lblN.setBounds(300, 10, 20, 23);
    frmScaleModulo.getContentPane().add(lblN);
    
    JSpinner n = new JSpinner();
    n.setBounds(325, 10, 40, 23);
    frmScaleModulo.getContentPane().add(n);
    
    JButton btnGo = new JButton("Go");
    
    btnGo.setBounds(1, 34, 378, 23);
    frmScaleModulo.getContentPane().add(btnGo);
    
    txtOutput = new JTextField();
    txtOutput.setBounds(1, 68, 379, 23);
    frmScaleModulo.getContentPane().add(txtOutput);
    txtOutput.setColumns(10);
    btnGo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        txtOutput.setText(new TetragraphSentence(Alphabet.Hexadecimal,
          (new TetragraphSentence(
              Alphabet.Hexadecimal, 
              textField.getText().trim()))
            .toWord().toBinaryWord().scaleModulo(
                (int)k.getValue(), 
                (int)n.getValue())).toString());
        }
    });
  }

}