package name.ncg777.maths.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.google.common.base.Joiner;

import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.awt.event.ActionEvent;

public class Susbstitute {

  private JFrame frmSubstitute;
  private JTextArea textMatch;
  private JTextArea textReplace;
  private JTextArea textInput;
  private JTextArea textOutput;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Susbstitute window = new Susbstitute();
          window.frmSubstitute.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public Susbstitute() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmSubstitute = new JFrame();
    frmSubstitute.setTitle("Substitute");
    frmSubstitute.setBounds(100, 100, 654, 480);
    frmSubstitute.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JScrollPane scrollPane = new JScrollPane();
    
    JScrollPane scrollPane_1 = new JScrollPane();
    
    JLabel lblNewLabel = new JLabel("Match");
    
    JLabel lblNewLabel_1 = new JLabel("Replace");
    
    JScrollPane scrollPane_1_1 = new JScrollPane();
    
    JLabel lblNewLabel_2 = new JLabel("Input");
    
    JButton btnSubstitute = new JButton(">>");
    btnSubstitute.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String[] match = textMatch.getText().split("\n+");
        String[] replace = textReplace.getText().split("\n+");
        String input = textInput.getText();
        
        if(match.length != replace.length) {textOutput.setText("Sizes don't match."); return;}
        
        int n = match.length;
        
        TreeMap<String,String> rep = new TreeMap<>();
        for(int i=0;i<n;i++) rep.put(match[i], replace[i]);
        
        TreeSet<Character> allCharsInInput = new TreeSet<>();
        
        for(int i=0;i<input.length();i++) allCharsInInput.add(input.charAt(i));
        { 
          var m = Joiner.on("").join(List.of(match));
          for(int i=0;i<m.length();i++) allCharsInInput.add(m.charAt(i));
          m = Joiner.on("").join(List.of(replace));
          for(int i=0;i<m.length();i++) allCharsInInput.add(m.charAt(i));
        }
        
        int max_chars=100;
        TreeSet<Character> unusedChars = new TreeSet<>();
        {
          char ch = (char)65;
          
          for(int i=0;i<max_chars;i++) {
            unusedChars.add((char)((int)ch+i));
          }
        }
        unusedChars.removeAll(allCharsInInput);
        TreeMap<String,String> safeMap = new TreeMap<>();
        TreeMap<String,String> safeMapRev = new TreeMap<>();
        var it = unusedChars.iterator();
        for(int i=0;i<n;i++) safeMap.put(match[i], String.valueOf(it.next())); 
        for(var entry: safeMap.entrySet()) safeMapRev.put(entry.getValue(), entry.getKey());
        String output = input;
        
        for(var entry : rep.entrySet()) {
          output = output.replaceAll(entry.getKey(), safeMap.get(entry.getKey()));
        }
        for(var entry : rep.entrySet()) {
          output = output.replaceAll(safeMap.get(entry.getKey()), entry.getValue());
        }
        textOutput.setText(output);
      }
    });
    
    JScrollPane scrollPane_2 = new JScrollPane();
    
    JLabel lblNewLabel_3 = new JLabel("Output");
    GroupLayout groupLayout = new GroupLayout(frmSubstitute.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(scrollPane_2, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 618, GroupLayout.PREFERRED_SIZE)
            .addComponent(scrollPane_1_1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE)
            .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(lblNewLabel)
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE))
              .addPreferredGap(ComponentPlacement.RELATED)
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(lblNewLabel_1)
                .addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)))
            .addComponent(btnSubstitute, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE)
            .addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblNewLabel_3))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addGap(13)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblNewLabel)
            .addComponent(lblNewLabel_1))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
            .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(lblNewLabel_2)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(scrollPane_1_1, GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnSubstitute, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(lblNewLabel_3)
          .addGap(7)
          .addComponent(scrollPane_2, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE)
          .addGap(7))
    );
    
    textOutput = new JTextArea();
    scrollPane_2.setViewportView(textOutput);
    
    textMatch = new JTextArea();
    scrollPane.setViewportView(textMatch);
    
    textReplace = new JTextArea();
    scrollPane_1.setViewportView(textReplace);
    
    textInput = new JTextArea();
    textInput.setLineWrap(true);
    scrollPane_1_1.setViewportView(textInput);
    frmSubstitute.getContentPane().setLayout(groupLayout);
  }
}
