package name.ncg777.maths.sequences.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import name.ncg777.maths.sequences.Sequence;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Sequencer {

  private JFrame frmWordSequencer;
  private JTextField textWords;
  private JTextField textSequence;
  private JTextField textOutput;

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Sequencer window = new Sequencer();
          window.frmWordSequencer.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public Sequencer() {
    initialize();
  }

  private void initialize() {
    frmWordSequencer = new JFrame();
    frmWordSequencer.setTitle("BinaryNatural Sequencer");
    frmWordSequencer.setBounds(100, 100, 450, 170);
    frmWordSequencer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    SpringLayout springLayout = new SpringLayout();
    frmWordSequencer.getContentPane().setLayout(springLayout);
    
    JLabel lblNewLabel = new JLabel("Words:");
    springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel, 10, SpringLayout.NORTH, frmWordSequencer.getContentPane());
    springLayout.putConstraint(SpringLayout.WEST, lblNewLabel, 10, SpringLayout.WEST, frmWordSequencer.getContentPane());
    springLayout.putConstraint(SpringLayout.SOUTH, lblNewLabel, 24, SpringLayout.NORTH, frmWordSequencer.getContentPane());
    springLayout.putConstraint(SpringLayout.EAST, lblNewLabel, 86, SpringLayout.WEST, frmWordSequencer.getContentPane());
    lblNewLabel.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    frmWordSequencer.getContentPane().add(lblNewLabel);
    
    textWords = new JTextField();
    springLayout.putConstraint(SpringLayout.NORTH, textWords, 7, SpringLayout.NORTH, frmWordSequencer.getContentPane());
    springLayout.putConstraint(SpringLayout.WEST, textWords, 2, SpringLayout.EAST, lblNewLabel);
    springLayout.putConstraint(SpringLayout.SOUTH, textWords, 27, SpringLayout.NORTH, frmWordSequencer.getContentPane());
    springLayout.putConstraint(SpringLayout.EAST, textWords, -10, SpringLayout.EAST, frmWordSequencer.getContentPane());
    frmWordSequencer.getContentPane().add(textWords);
    textWords.setColumns(10);
    
    JLabel lblNewLabel_1 = new JLabel("Sequence:");
    springLayout.putConstraint(SpringLayout.WEST, lblNewLabel_1, 10, SpringLayout.WEST, frmWordSequencer.getContentPane());
    springLayout.putConstraint(SpringLayout.EAST, lblNewLabel_1, 0, SpringLayout.EAST, lblNewLabel);
    lblNewLabel_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
    frmWordSequencer.getContentPane().add(lblNewLabel_1);
    
    textSequence = new JTextField();
    springLayout.putConstraint(SpringLayout.NORTH, textSequence, 11, SpringLayout.SOUTH, textWords);
    springLayout.putConstraint(SpringLayout.WEST, textSequence, 2, SpringLayout.EAST, lblNewLabel_1);
    springLayout.putConstraint(SpringLayout.EAST, textSequence, -10, SpringLayout.EAST, frmWordSequencer.getContentPane());
    springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel_1, 3, SpringLayout.NORTH, textSequence);
    frmWordSequencer.getContentPane().add(textSequence);
    textSequence.setColumns(10);
    
    JButton btnNewButton = new JButton("Sequence");
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String[] words = textWords.getText().split("\\s+");
        Sequence s = Sequence.parse(textSequence.getText());
        
        String o = "";
        for(int i=0;i<s.size();i++) {
          o += words[s.get(i)] + " ";
        }
        textOutput.setText(o.trim());
      }
    });
    springLayout.putConstraint(SpringLayout.NORTH, btnNewButton, 6, SpringLayout.SOUTH, textSequence);
    springLayout.putConstraint(SpringLayout.WEST, btnNewButton, 0, SpringLayout.WEST, textWords);
    springLayout.putConstraint(SpringLayout.SOUTH, btnNewButton, 29, SpringLayout.SOUTH, textSequence);
    springLayout.putConstraint(SpringLayout.EAST, btnNewButton, 0, SpringLayout.EAST, textWords);
    frmWordSequencer.getContentPane().add(btnNewButton);
    
    JLabel lblNewLabel_2 = new JLabel("Output:");
    springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel_2, 39, SpringLayout.SOUTH, lblNewLabel_1);
    springLayout.putConstraint(SpringLayout.WEST, lblNewLabel_2, 0, SpringLayout.WEST, lblNewLabel);
    springLayout.putConstraint(SpringLayout.EAST, lblNewLabel_2, 0, SpringLayout.EAST, lblNewLabel);
    lblNewLabel_2.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
    frmWordSequencer.getContentPane().add(lblNewLabel_2);
    
    textOutput = new JTextField();
    springLayout.putConstraint(SpringLayout.NORTH, textOutput, 6, SpringLayout.SOUTH, btnNewButton);
    springLayout.putConstraint(SpringLayout.WEST, textOutput, 0, SpringLayout.WEST, textWords);
    springLayout.putConstraint(SpringLayout.EAST, textOutput, 0, SpringLayout.EAST, textWords);
    frmWordSequencer.getContentPane().add(textOutput);
    textOutput.setColumns(10);
  }
}
