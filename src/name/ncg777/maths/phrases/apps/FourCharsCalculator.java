package name.ncg777.maths.phrases.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import name.ncg777.maths.phrases.FourCharsPhrase;
import name.ncg777.maths.words.Alphabet;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class FourCharsCalculator {
  private enum Operation {
    And,
    Or,
    Xor,
    Minus,
    Convolve
  }
  private JFrame frmRhythmCalc;
  private JTextField rhA;
  private JTextField rhB;
  private JTextField output;
  private JComboBox<Alphabet.Name> comboBox = new JComboBox<Alphabet.Name>(new DefaultComboBoxModel<Alphabet.Name>(Alphabet.Name.values()));

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          FourCharsCalculator window = new FourCharsCalculator();
          window.frmRhythmCalc.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public FourCharsCalculator() {
    initialize();
  }

  private void initialize() {
    frmRhythmCalc = new JFrame();
    frmRhythmCalc.setTitle("FourCharsCalculator");
    frmRhythmCalc.setBounds(100, 100, 450, 325);
    frmRhythmCalc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblRhythmA = new JLabel("FourChars A :");
    lblRhythmA.setFont(new Font("Unifont", Font.PLAIN, 12));
    lblRhythmA.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblRhythmB = new JLabel("FourChars B :");
    lblRhythmB.setFont(new Font("Unifont", Font.PLAIN, 12));
    lblRhythmB.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblOperation = new JLabel("Operation :");
    lblOperation.setFont(new Font("Unifont", Font.PLAIN, 12));
    lblOperation.setHorizontalAlignment(SwingConstants.RIGHT);
    
    rhA = new JTextField();
    rhA.setFont(new Font("Unifont", Font.PLAIN, 12));
    rhA.setColumns(10);
    
    rhB = new JTextField();
    rhB.setFont(new Font("Unifont", Font.PLAIN, 12));
    rhB.setColumns(10);
    
    JComboBox<Operation> operation = new JComboBox<>();
    operation.setFont(new Font("Unifont", Font.PLAIN, 12));
    operation.setModel(new DefaultComboBoxModel<>(Operation.values()));
    
    JButton btnCalc = new JButton("Calc");
    btnCalc.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        var abc = comboBox.getSelectedItem();
        FourCharsPhrase a = new FourCharsPhrase((Alphabet.Name)abc, rhA.getText());
        FourCharsPhrase b = new FourCharsPhrase((Alphabet.Name)abc, rhB.getText());
        String o = "";
          
        switch((Operation) operation.getSelectedItem()) {
          case And:
            o = FourCharsPhrase.and(a, b).toString();
            break;
          case Convolve:
            o = FourCharsPhrase.convolve(a, b).toString();
            break;
          case Or:
            o = FourCharsPhrase.or(a, b).toString();
            break;
          case Xor:
            o = FourCharsPhrase.xor(a, b).toString();
            break;
          case Minus:
            o = FourCharsPhrase.minus(a, b).toString();
            break;
        }
       
        output.setText(o);
      }});
    
    JLabel lblOutput = new JLabel("Output :");
    lblOutput.setFont(new Font("Unifont", Font.PLAIN, 12));
    lblOutput.setHorizontalAlignment(SwingConstants.RIGHT);
    
    output = new JTextField();
    output.setFont(new Font("Unifont", Font.PLAIN, 12));
    output.setColumns(10);
    
    JButton btnFlipBits = new JButton("Flip bits");
    btnFlipBits.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        var str = FourCharsPhrase.not(
            new FourCharsPhrase(
                (Alphabet.Name)comboBox.getSelectedItem(), 
                output.getText())).toString();
        
        output.setText(str);
      }
    });
    
    GroupLayout groupLayout = new GroupLayout(frmRhythmCalc.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
              .addGroup(groupLayout.createSequentialGroup()
                .addComponent(lblRhythmB)
                .addGap(6))
              .addGroup(groupLayout.createSequentialGroup()
                .addComponent(lblOperation)
                .addPreferredGap(ComponentPlacement.RELATED)))
            .addComponent(lblRhythmA)
            .addComponent(lblOutput, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE))
          .addGap(4)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(rhA, GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
            .addComponent(output, GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
            .addComponent(operation, 0, 345, Short.MAX_VALUE)
            .addComponent(rhB, GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
            .addComponent(btnCalc, GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
            .addComponent(btnFlipBits, GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
          .addGap(11)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblRhythmA)
            .addComponent(rhA, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblRhythmB)
            .addComponent(rhB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(operation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblOperation))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnCalc)
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblOutput)
            .addComponent(output, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnFlipBits)
          .addContainerGap(61, Short.MAX_VALUE))
    );
    frmRhythmCalc.getContentPane().setLayout(groupLayout);
  }
}