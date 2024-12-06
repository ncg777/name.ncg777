package name.ncg777.maths.phrases.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.maths.phrases.FourCharsPhrase;
import name.ncg777.maths.words.Alphabet;
import name.ncg777.maths.words.BinaryWord;
import name.ncg777.maths.words.Word;
import name.ncg777.maths.words.FourChars;

import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.BitSet;
import java.awt.event.ActionEvent;
import javax.swing.JSpinner;
import java.awt.Font;
import javax.swing.SpinnerNumberModel;

public class HexadecimalWordDivider {
  private JFrame frmDivider;
  private JTextField txtR;
  private JTextArea txtResult;
  private JSpinner spinner;

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          HexadecimalWordDivider window = new HexadecimalWordDivider();
          window.frmDivider.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public HexadecimalWordDivider() {
    initialize();
  }

  private void initialize() {
    frmDivider = new JFrame();
    frmDivider.setTitle("R16 Divider");
    frmDivider.setBounds(100, 100, 450, 300);
    frmDivider.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblR = new JLabel("R16:");
    lblR.setFont(new Font("Unifont", Font.PLAIN, 11));
    lblR.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtR = new JTextField();
    txtR.setFont(new Font("Unifont", Font.PLAIN, 11));
    txtR.setColumns(10);
    
    JButton btnDivide = new JButton("Divide");
    btnDivide.setFont(new Font("Unifont", Font.PLAIN, 11));
    txtResult = new JTextArea();
    txtResult.setFont(new Font("Monospaced", Font.PLAIN, 13));
    txtResult.setEditable(false);
    spinner = new JSpinner();
    spinner.setFont(new Font("Unifont", Font.PLAIN, 11));
    btnDivide.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        var r1 = (new FourCharsPhrase(
            Alphabet.Name.Hexadecimal, txtR.getText().trim()))
            .toBinaryWord();
        int div = (int)spinner.getValue();
        int len = r1.getN();
        int d = len/div;
        
        if(len%div != 0) {
          txtResult.setText("Divisor does not divide length of rhythm");
        } else if(d < 16) {
          txtResult.setText("too few bytes");  
        }else {
          ArrayList<BinaryWord> o = new ArrayList<BinaryWord>();
          for(int i=0;i<div;i++) {
            o.add(BinaryWord.build(new BitSet(), d));}
          
          for(int i=0;i<d;i++) {
            for(int j=0;j<div;j++) {
              o.get(j).set(i, r1.get((i*div)+j));
            }
          }
          
          String output = "";
          
          for(int i=0;i<div;i++) {
            var t = new FourChars(Alphabet.Name.Hexadecimal, new Word(Alphabet.Name.Hexadecimal, o.get(i).toString()));
            output += t.toString(true) + "\n";
          }
          txtResult.setText(output);
        }
        
        
      }
    });
    
    
    spinner.setModel(new SpinnerNumberModel(Integer.valueOf(2), Integer.valueOf(2), null, Integer.valueOf(1)));
    
    JLabel lblMult = new JLabel("Div:");
    lblMult.setFont(new Font("Unifont", Font.PLAIN, 11));
    lblMult.setHorizontalAlignment(SwingConstants.RIGHT);
    
    
    GroupLayout groupLayout = new GroupLayout(frmDivider.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.TRAILING)
        .addGroup(groupLayout.createSequentialGroup()
          .addGap(74)
          .addComponent(btnDivide, GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
          .addContainerGap())
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addComponent(lblR, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblMult, GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(txtResult, GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
              .addContainerGap())
            .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
              .addComponent(spinner, GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
              .addContainerGap())
            .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
              .addComponent(txtR, GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
              .addGap(7))))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblR)
            .addComponent(txtR, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblMult))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnDivide)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(txtResult, GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
          .addContainerGap())
    );
    frmDivider.getContentPane().setLayout(groupLayout);
  }
}
