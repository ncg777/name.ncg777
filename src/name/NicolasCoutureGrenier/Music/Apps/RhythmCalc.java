package name.NicolasCoutureGrenier.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.NicolasCoutureGrenier.Music.R12List;
import name.NicolasCoutureGrenier.Music.R16List;

import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import name.NicolasCoutureGrenier.Music.Rn;

public class RhythmCalc {
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
  private JComboBox<Rn> comboBox = new JComboBox<Rn>(new DefaultComboBoxModel<Rn>(Rn.values()));
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          RhythmCalc window = new RhythmCalc();
          window.frmRhythmCalc.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public RhythmCalc() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmRhythmCalc = new JFrame();
    frmRhythmCalc.setTitle("Rhythm Calc");
    frmRhythmCalc.setBounds(100, 100, 450, 325);
    frmRhythmCalc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblRhythmA = new JLabel("Rhythm A :");
    lblRhythmA.setFont(new Font("Unifont", Font.PLAIN, 12));
    lblRhythmA.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblRhythmB = new JLabel("Rhythm B :");
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
        if(comboBox.getSelectedItem() == Rn.Hex) {
          R16List a = R16List.parseR16Seq(rhA.getText());
          R16List b = R16List.parseR16Seq(rhB.getText());
          String o = "";
          
          switch((Operation) operation.getSelectedItem()) {
            case And:
              o = R16List.and(a, b).toString();
              break;
            case Convolve:
              o = R16List.convolve(a, b).toString();
              break;
            case Or:
              o = R16List.or(a, b).toString();
              break;
            case Xor:
              o = R16List.xor(a, b).toString();
              break;
            case Minus:
              o = R16List.minus(a, b).toString();
          }
          output.setText(o);
        } else if(comboBox.getSelectedItem() == Rn.Octal) {
          R12List a = R12List.parseR12Seq(rhA.getText());
          R12List b = R12List.parseR12Seq(rhB.getText());
          String o = "";
          
          switch((Operation) operation.getSelectedItem()) {
            case And:
              o = R12List.and(a, b).toString();
              break;
            case Convolve:
              o = R12List.convolve(a, b).toString();
              break;
            case Or:
              o = R12List.or(a, b).toString();
              break;
            case Xor:
              o = R12List.xor(a, b).toString();
              break;
            case Minus:
              o = R12List.minus(a, b).toString();
          }
          output.setText(o);
        }
        
      }
    });
    
    JLabel lblOutput = new JLabel("Output :");
    lblOutput.setFont(new Font("Unifont", Font.PLAIN, 12));
    lblOutput.setHorizontalAlignment(SwingConstants.RIGHT);
    
    output = new JTextField();
    output.setFont(new Font("Unifont", Font.PLAIN, 12));
    output.setColumns(10);
    
    JButton btnFlipBits = new JButton("Flip bits");
    btnFlipBits.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(comboBox.getSelectedItem() == Rn.Hex) {
          output.setText(R16List.not(R16List.parseR16Seq(output.getText())).toString());
        } else if(comboBox.getSelectedItem() == Rn.Octal) {
          output.setText(R12List.not(R12List.parseR12Seq(output.getText())).toString());
        }
        
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
