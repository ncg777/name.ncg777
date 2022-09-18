package name.ncg.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;


import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.JTextField;

import name.ncg.Music.R12List;
import name.ncg.Music.R16List;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import name.ncg.Music.Rn;


public class Decimator {

  private JFrame frmSeqGen;
  private JTextField textField;
  private JTextArea textField_1;
  private JComboBox<Rn> comboBox;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Decimator window = new Decimator();
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
  public Decimator() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmSeqGen = new JFrame();
    frmSeqGen.getContentPane().setBackground(Color.DARK_GRAY);
    frmSeqGen.setTitle("Decimator");
    frmSeqGen.setBounds(100, 100, 398, 323);
    frmSeqGen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmSeqGen.getContentPane().setLayout(null);
    
    JLabel lblRhythm = new JLabel("Rhythm");
    lblRhythm.setForeground(Color.WHITE);
    lblRhythm.setHorizontalAlignment(SwingConstants.RIGHT);
    lblRhythm.setBounds(10, 43, 46, 14);
    frmSeqGen.getContentPane().add(lblRhythm);
    
    JLabel lblSeq = new JLabel("Sub");
    lblSeq.setForeground(Color.WHITE);
    lblSeq.setHorizontalAlignment(SwingConstants.RIGHT);
    lblSeq.setBounds(10, 76, 46, 14);
    frmSeqGen.getContentPane().add(lblSeq);
    
    JButton btnGenerate = new JButton("Decimate");
    btnGenerate.setForeground(Color.WHITE);
    btnGenerate.setBackground(Color.BLACK);
    btnGenerate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if(comboBox.getSelectedItem() == Rn.Hex) {
          String str_R = textField.getText().trim();
          R16List r = R16List.parseR16Seq(str_R);
          textField_1.setText("");
          R16List beforeLast = r;
          R16List last = r.decimate();
          while(true) {
            textField_1.append(last.toString() + "\n");
            beforeLast = last;
            last = last.decimate();
            if(beforeLast.equals(last)) {
              break;
            }
          }
        } else if(comboBox.getSelectedItem() == Rn.Octal) {
          String str_R = textField.getText().trim();
          R12List r = R12List.parseR12Seq(str_R);
          textField_1.setText("");
          R12List beforeLast = r;
          R12List last = r.decimate();
          while(true) {
            textField_1.append(last.toString() + "\n");
            beforeLast = last;
            last = last.decimate();
            if(beforeLast.equals(last)) {
              break;
            }
          }
        }
        
        
      }
    });
    btnGenerate.setBounds(10, 251, 362, 23);
    frmSeqGen.getContentPane().add(btnGenerate);
    
    textField = new JTextField();
    textField.setBackground(Color.BLACK);
    textField.setForeground(Color.WHITE);
    textField.setBounds(66, 40, 311, 20);
    frmSeqGen.getContentPane().add(textField);
    textField.setColumns(10);
    
    textField_1 = new JTextArea();
    textField_1.setForeground(Color.WHITE);
    textField_1.setBackground(Color.BLACK);
    textField_1.setBounds(66, 71, 311, 169);
    frmSeqGen.getContentPane().add(textField_1);
    textField_1.setColumns(10);
    
    comboBox = new JComboBox<Rn>(new DefaultComboBoxModel<Rn>(Rn.values()));
    comboBox.setBounds(66, 11, 117, 23);
    frmSeqGen.getContentPane().add(comboBox);
  }
}
