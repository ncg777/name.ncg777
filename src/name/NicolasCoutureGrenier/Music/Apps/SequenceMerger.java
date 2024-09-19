package name.NicolasCoutureGrenier.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.NicolasCoutureGrenier.Maths.Objects.Sequence;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import java.awt.Font;

public class SequenceMerger {

  private JFrame frmSequenceMerger;
  

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          SequenceMerger window = new SequenceMerger();
          window.frmSequenceMerger.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public SequenceMerger() {
    initialize();
  }
  
  JTextArea txtrSequences = new JTextArea();
  JTextField textField = new JTextField();
  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmSequenceMerger = new JFrame();
    frmSequenceMerger.setTitle("Sequence Merger");
    frmSequenceMerger.setBounds(100, 100, 450, 373);
    frmSequenceMerger.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JScrollPane scrollPane = new JScrollPane();
    
    JButton btnGenerate = new JButton("Merge");
    
    btnGenerate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String[] lines = txtrSequences.getText().split("\n+");
        Sequence[] sequences = new Sequence[lines.length];
        int maxn = -1;
        for(int i=0;i<lines.length;i++) {
          sequences[i] = Sequence.parse(lines[i].trim());
          if(sequences[i].size() > maxn) maxn = sequences[i].size();
        }
        
        String output = "";
        
        for(int i=0;i<maxn;i++) {
          for(int j=0;j<sequences.length;j++) {
            Sequence s = sequences[j];
            output += s.get(i%s.size()) + " ";  
          }
        }
        
        textField.setText(output.trim());
      }
    });
    
    textField.setFont(new Font("Monospaced", Font.PLAIN, 13));
    textField.setColumns(10);
    GroupLayout groupLayout = new GroupLayout(frmSequenceMerger.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addComponent(textField, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
            .addComponent(btnGenerate, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
            .addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE))
          .addGap(13))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 245, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnGenerate)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addContainerGap(121, Short.MAX_VALUE))
    );

    
    txtrSequences.setFont(new Font("Monospaced", Font.PLAIN, 13));
    scrollPane.setViewportView(txtrSequences);
    frmSequenceMerger.getContentPane().setLayout(groupLayout);
  }
}
