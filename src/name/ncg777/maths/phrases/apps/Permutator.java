package name.ncg777.maths.phrases.apps;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.SwingConstants;

import name.ncg777.maths.enumerations.WordPermutationEnumeration;

import javax.swing.JTextField;

public class Permutator {

  private JFrame frmWordPermutator;
  private JTextField textField;
  JTextArea textArea = new JTextArea();
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Permutator window = new Permutator();
          window.frmWordPermutator.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public Permutator() {
    initialize();
  }

  private void initialize() {
    frmWordPermutator = new JFrame();
    frmWordPermutator.setResizable(false);
    frmWordPermutator.setTitle("WordPermutator");
    frmWordPermutator.setBounds(100, 100, 356, 328);
    frmWordPermutator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmWordPermutator.getContentPane().setLayout(null);
    
    JLabel lblNewLabel = new JLabel("Word:");
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setBounds(10, 11, 46, 14);
    frmWordPermutator.getContentPane().add(lblNewLabel);
    
    textField = new JTextField();
    textField.setBounds(66, 8, 262, 20);
    frmWordPermutator.getContentPane().add(textField);
    textField.setColumns(10);
    
    JButton btnNewButton = new JButton("Permutate");
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String word = textField.getText();
        
        ArrayList<Character> charsarr = new ArrayList<>();
        for(int i=0;i<word.length();i++) {
          if(!charsarr.contains(word.charAt(i))) {charsarr.add(word.charAt(i));}
        }
        
        int[] counts = new int[charsarr.size()];
        Arrays.fill(counts, 0);
        for(int i=0;i<counts.length;i++) {
          for(int j=0;j<word.length();j++) {
            if(word.charAt(j) == charsarr.get(i)) {counts[i]++;}
          }
        }
        
        var wpe = new WordPermutationEnumeration(counts);
        
        ArrayList<String> ps = new ArrayList<>();
        while(wpe.hasMoreElements()) {
          var p = wpe.nextElement();
          
          String x = "";
          for(int i=0;i<p.length;i++) {
            x += charsarr.get(p[i]);
          }
          ps.add(x);
        }
        StringBuilder o = new StringBuilder();
        
        for(int i=ps.size()-1;i>=0;i--) {
          o.append(ps.get(i) + "\n");
        }
        textArea.setText(o.toString());
      }
    });
    btnNewButton.setBounds(10, 34, 319, 23);
    frmWordPermutator.getContentPane().add(btnNewButton);
    
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBounds(10, 65, 318, 213);
    frmWordPermutator.getContentPane().add(scrollPane);
    
    
    scrollPane.setViewportView(textArea);
    
  }
}