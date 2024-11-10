package name.ncg777.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.Maths.Objects.Sequence;
import name.ncg777.Music.R16List;
import name.ncg777.Music.R48List;
import name.ncg777.Music.Rn;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Font;
import javax.swing.JComboBox;

public class RhythmMatrixSeq {

  private JFrame frmRseq;
  private JTextArea textR16List;
  private JTextField textSequence;
  private JTextArea textResult;
  private JComboBox<Rn> comboBox = new JComboBox<Rn>(Rn.values());
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          RhythmMatrixSeq window = new RhythmMatrixSeq();
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
  public RhythmMatrixSeq() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmRseq = new JFrame();
    frmRseq.setTitle("Rhythm Matrix Seq");
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
        if(comboBox.getSelectedItem().equals(Rn.Hex) || comboBox.getSelectedItem().equals(Rn.Octal)) {
          String[] rstr = textR16List.getText().split("\n+"); 
          int m = rstr.length;
          R16List[] r16s = new R16List[rstr.length];
          for(int i=0;i<m;i++) {
            r16s[i] = R16List.parseR16Seq(rstr[i]);
          }
          
          Sequence s = Sequence.parse(textSequence.getText());
          
          try {
            String o = "";
            for(int i=0;i<m;i++) {
              String line ="";
              for(int j : s) {
                line += r16s[i].get(j%r16s[i].size()).toString() + " ";
              }
              o+=line.trim();
              if(i<m-1) {o+="\n";}
            }
            
            textResult.setText(o.trim());
          } catch(Exception ex) {
            textResult.setText("Index out of bounds");
          }
        } else if(comboBox.getSelectedItem().equals(Rn.Tribble)) {
          String[] rstr = textR16List.getText().split("\n+"); 
          int m = rstr.length;
          R48List[] r48s = new R48List[rstr.length];
          for(int i=0;i<m;i++) {
            r48s[i] = R48List.parseR48Seq(rstr[i]);
          }
          
          Sequence s = Sequence.parse(textSequence.getText());
          
          try {
            String o = "";
            for(int i=0;i<m;i++) {
              String line ="";
              for(int j : s) {
                line += r48s[i].get(j%r48s[i].size()).toString() + " ";
              }
              o+=line.trim();
              if(i<m-1) {o+="\n";}
            }
            
            textResult.setText(o.trim());
          } catch(Exception ex) {
            textResult.setText("Index out of bounds");
          }
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
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
              .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
                .addComponent(lblNewLabel_2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblNewLabel_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
              .addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE))
            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
            .addComponent(scrollPane_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
            .addComponent(textSequence, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
            .addComponent(btnGenerate, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE)
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(lblNewLabel)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)))
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
    
    textR16List = new JTextArea();
    textR16List.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    scrollPane.setViewportView(textR16List);
    textR16List.setColumns(10);
    frmRseq.getContentPane().setLayout(groupLayout);
  }
}
