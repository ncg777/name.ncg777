package name.ncg.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Music.BeatRhythmList;
import name.ncg.Music.R16List;

import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Font;

public class RhythmMatrix96Seq {

  private JFrame frmRseq;
  private JTextArea textBeatRhythmList;
  private JTextField textSequence;
  private JTextArea textResult;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          RhythmMatrix96Seq window = new RhythmMatrix96Seq();
          window.frmRseq.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public RhythmMatrix96Seq() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmRseq = new JFrame();
    frmRseq.setTitle("RhythmMatrix96 Seq");
    frmRseq.setBounds(100, 100, 471, 587);
    frmRseq.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblNewLabel = new JLabel("Matrix :");
    lblNewLabel.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblNewLabel_1 = new JLabel("Sequence :");
    lblNewLabel_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
    
    textSequence = new JTextField();
    textSequence.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textSequence.setColumns(10);
    
    JButton btnGenerate = new JButton("Generate");
    btnGenerate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String[] r96str = textBeatRhythmList.getText().split("\n+"); 
        int m = r96str.length;
        BeatRhythmList[] r96s = new BeatRhythmList[r96str.length];
        for(int i=0;i<m;i++) {
          r96s[i] = BeatRhythmList.parseBeatRhythmSeq(r96str[i]);
        }
        
        Sequence s = Sequence.parse(textSequence.getText());
        
        try {
          String o = "";
          for(int i=0;i<m;i++) {
            String line ="";
            for(int j : s) {
              line += r96s[i].get(j%r96s[i].size()).toString() + " ";
            }
            o+=line.trim();
            if(i<m-1) {o+="\n";}
          }
          
          textResult.setText(o.trim());
        } catch(Exception ex) {
          textResult.setText("Index out of bounds");
        }
        
      }
    });
    
    JLabel lblNewLabel_2 = new JLabel("Result :");
    lblNewLabel_2.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JScrollPane scrollPane = new JScrollPane();
    
    JScrollPane scrollPane_1 = new JScrollPane();
    GroupLayout groupLayout = new GroupLayout(frmRseq.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
              .addComponent(lblNewLabel_2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(lblNewLabel_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
            .addComponent(scrollPane_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
            .addComponent(textSequence, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
            .addComponent(btnGenerate, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblNewLabel))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblNewLabel_1)
            .addComponent(textSequence, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnGenerate)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(lblNewLabel_2)
            .addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE))
          .addContainerGap())
    );
    
    textResult = new JTextArea();
    textResult.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    scrollPane_1.setViewportView(textResult);
    textResult.setColumns(10);
    
    textBeatRhythmList = new JTextArea();
    textBeatRhythmList.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    scrollPane.setViewportView(textBeatRhythmList);
    textBeatRhythmList.setColumns(10);
    frmRseq.getContentPane().setLayout(groupLayout);
  }
}
