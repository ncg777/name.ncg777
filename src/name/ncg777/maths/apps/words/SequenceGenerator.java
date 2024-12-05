package name.ncg777.maths.apps.words;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.maths.sentences.TetragraphSentence;
import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.words.Alphabet;
import name.ncg777.maths.words.BinaryWord;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class SequenceGenerator {

  private JFrame frmRhythmicSeqGen;
  
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          SequenceGenerator window = new SequenceGenerator();
          window.frmRhythmicSeqGen.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public SequenceGenerator() {
    initialize();
  }
  
  JTextArea txtrAaBb = new JTextArea();
  JTextField textField = new JTextField();
  private final JComboBox<Alphabet.Names> comboBox = new JComboBox<Alphabet.Names>(new DefaultComboBoxModel<Alphabet.Names>(Alphabet.Names.values()));

  private void initialize() {
    comboBox.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    frmRhythmicSeqGen = new JFrame();
    frmRhythmicSeqGen.setTitle("Rhythmic Sequence Generator");
    frmRhythmicSeqGen.setBounds(100, 100, 450, 373);
    frmRhythmicSeqGen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JScrollPane scrollPane = new JScrollPane();
    
    JButton btnGenerate = new JButton("Generate");
    
    btnGenerate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String[] lines = txtrAaBb.getText().split("\n+");
        
        Sequence muls = new Sequence();
        List<BinaryWord> binaryWords = new ArrayList<BinaryWord>();
        for(int i=0; i<lines.length; i++) {
          String[] l = lines[i].split(",");
          if(l.length != 2) {
            textField.setText("ERROR on line" + " " + i);
            return;
          }
          
          binaryWords.add(
              new TetragraphSentence(
                  Alphabet.getAlphabet((Alphabet.Names)comboBox.getSelectedItem()), 
                  l[0].trim()).toWord().toBinaryWord());
          
          muls.add(Integer.valueOf(l[1].trim()));
        }
        
        if(binaryWords.size() != muls.size()) {
          textField.setText("ERROR");
          return;
        }
        
        for(int i=0;i<binaryWords.size();i++) {
          if(binaryWords.get(i).size() != binaryWords.get(0).size()) {
            textField.setText("ERROR");
            return;
          }
        }
        
        int n = binaryWords.size();
        int size = binaryWords.get(0).getN();
        Sequence output = new Sequence();
        for(int i=0;i<size;i++) {
          int t = 0;
          
          for(int j=0;j<n;j++) {
            if(binaryWords.get(j).get(i)) t += muls.get(j);
          }
          
          output.add(t);
        }
        textField.setText(output.toString().replaceAll("[()]", ""));
      }
    });
    
    
    textField.setFont(new Font("Monospaced", Font.PLAIN, 13));
    textField.setColumns(10);
    GroupLayout groupLayout = new GroupLayout(frmRhythmicSeqGen.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.TRAILING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                .addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                .addComponent(textField, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                .addComponent(btnGenerate, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE))
              .addGap(13))
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 172, GroupLayout.PREFERRED_SIZE)
              .addContainerGap(252, Short.MAX_VALUE))))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
          .addGap(3)
          .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 221, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnGenerate)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addContainerGap(19, Short.MAX_VALUE))
    );
    txtrAaBb.setText("AA BB, 2\r\n88 88, 5");
    
    
    txtrAaBb.setFont(new Font("Monospaced", Font.PLAIN, 13));
    scrollPane.setViewportView(txtrAaBb);
    frmRhythmicSeqGen.getContentPane().setLayout(groupLayout);
  }
}
