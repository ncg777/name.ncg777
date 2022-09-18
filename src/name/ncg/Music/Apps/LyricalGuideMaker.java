package name.ncg.Music.Apps;

import java.awt.Dialog;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg.Maths.Numbers;
import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Music.R12List;
import name.ncg.Music.R16List;
import name.ncg.Music.Rhythm;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.BitSet;
import java.awt.event.ActionEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import name.ncg.Music.Rn;
public class LyricalGuideMaker {

  private JFrame frmLyricalGuideMaker;
  private JTextField textRhythm;
  private JTextField textBaseRhythm;
  private JTextField textSequence;
  private JTextArea textStrippedLyrics = new JTextArea();
  private JTextArea textCodedLyrics = new JTextArea();
  private JComboBox<Rn> comboBox = new JComboBox<Rn>(new DefaultComboBoxModel<Rn>(Rn.values()));
  private void compile() {
    try {
      Sequence scompo = new Sequence();
      Sequence sdurations = new Sequence();
      Sequence smultiples = new Sequence();
      Sequence completeSequence = new Sequence();
      String codedLyrics = textCodedLyrics.getText();
      String strippedLyrics = "";
      
      String[] lines = codedLyrics.split("\n+");
      
      boolean isSequenceLine = false;
      int currentNumberOfTriplesInLine = -1;
      
      Pattern pattern = Pattern.compile("(\\d+\\s+\\d+\\s+\\d+)");
      
      for(int i=0;i<lines.length;i++) {
        if(!isSequenceLine) {
          Matcher matcher = pattern.matcher(lines[i]);
          
          int k = 0;
          int start = 0;
          while(matcher.find(start)) {
            String triple = matcher.group(1);
            Sequence st = Sequence.parse(triple);
            if(st.size() != 3) throw new RuntimeException("invalid triple --> " + lines[i]);
            
            smultiples.add(st.get(0));
            sdurations.add(st.get(1));
            scompo.add(st.get(2));
            k++;
            start = matcher.end();
          }
          currentNumberOfTriplesInLine = k;
          strippedLyrics += lines[i].replaceAll("\\d+\\s+\\d+\\s+\\d+", "").replaceAll("\\s+", " ").trim() + "\n";
          
        } else {
          String[] seqs = lines[i].split("\\s*,\\s*");
          int k=0;
          for(int j=scompo.size()-currentNumberOfTriplesInLine;j<scompo.size();j++) {
            if(smultiples.get(j) != 0) {
              Sequence s = Sequence.parse(seqs[k++]);
              if(s.size() != smultiples.get(j)) throw new RuntimeException("invalid sequence --> " + lines[i]);
              
              completeSequence = completeSequence.juxtapose(s);
            }
          }
        }
        
        isSequenceLine = !isSequenceLine;
      }
      
      int scompo_sum = scompo.sum();
      if(comboBox.getSelectedItem() == Rn.Hex) {
        if(scompo_sum%16 != 0) throw new Exception("Sum of composition must be a multiple of 16.");
      }
      if(comboBox.getSelectedItem() == Rn.Octal) {
        if(scompo_sum%12 != 0) throw new Exception("Sum of composition must be a multiple of 12.");
      }
      
      if(sdurations.getMin() < 0) { throw new Exception("Periods must be positive"); }
    
      
      for(int i=0;i<scompo.size();i++) {
        if(scompo.get(i) < smultiples.get(i)*sdurations.get(i)) {
          throw new Exception("Duration*Multiple must not exceed corresponding composition element.");
        }
      }
      
      Rhythm rh = new Rhythm(new BitSet(scompo_sum), scompo_sum);
      Rhythm baserh = new Rhythm(new BitSet(scompo_sum), scompo_sum);
      
      int acc = 0;
      
      for(int i=0; i<scompo.size(); i++) {
        int c = scompo.get(i);
        
        int d = sdurations.get(i);
        if(d != 0) {
          int m = smultiples.get(i);
          
          int start = acc;
          int end = acc + c - 1;
          
          int inc = d;
          int cnt = 0;
          
          for(int j=start;j<=end;j+=inc) {
            if(cnt++ < m) {
              rh.set(j, true);
              if(cnt == 1) baserh.set(j, true);
            }
          }
        }
        
        acc+=c;
      }
      
      if(comboBox.getSelectedItem() == Rn.Hex) {
        textRhythm.setText(R16List.fromRhythm(rh).toString());
        textBaseRhythm.setText(R16List.fromRhythm(baserh).toString());
      }
      if(comboBox.getSelectedItem() == Rn.Octal) {
        textRhythm.setText(R12List.fromRhythm(rh).toString());
        textBaseRhythm.setText(R12List.fromRhythm(baserh).toString());
      }
      textSequence.setText(completeSequence.toString().replaceAll("[)(,]", ""));
      textStrippedLyrics.setText(strippedLyrics);
      
      
    } catch(Exception ex) {
      textRhythm.setText("");
      textBaseRhythm.setText("");
      textSequence.setText("");
      textStrippedLyrics.setText("");
      JOptionPane.showMessageDialog(frmLyricalGuideMaker, ex.getMessage());
    }
    
  }
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          LyricalGuideMaker window = new LyricalGuideMaker();
          window.frmLyricalGuideMaker.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public LyricalGuideMaker() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmLyricalGuideMaker = new JFrame();
    frmLyricalGuideMaker.setTitle("Lyrical Guide Maker");
    frmLyricalGuideMaker.setBounds(100, 100, 604, 635);
    frmLyricalGuideMaker.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JScrollPane scrollPane = new JScrollPane();
    
    JLabel lblNewLabel = new JLabel("Coded lyrics alternating with sequences");
    lblNewLabel.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    
    JButton btnNewButton = new JButton("Compile");
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { compile(); }
    });
    btnNewButton.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    
    JLabel lblNewLabel_1 = new JLabel("Rhythm:");
    lblNewLabel_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
    
    textRhythm = new JTextField();
    textRhythm.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textRhythm.setColumns(10);
    
    textBaseRhythm = new JTextField();
    textBaseRhythm.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textBaseRhythm.setColumns(10);
    
    JLabel lblNewLabel_2 = new JLabel("Base rhythm:");
    lblNewLabel_2.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblNewLabel_3 = new JLabel("Sequence:");
    lblNewLabel_3.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
    
    textSequence = new JTextField();
    textSequence.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textSequence.setColumns(10);
    
    JLabel lblNewLabel_4 = new JLabel("Stripped text:");
    lblNewLabel_4.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_4.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JScrollPane scrollPane_1 = new JScrollPane();
    
    
    
    GroupLayout groupLayout = new GroupLayout(frmLyricalGuideMaker.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(groupLayout.createSequentialGroup()
                  .addComponent(lblNewLabel_3, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(textSequence, GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
                  .addGap(14))
                .addGroup(groupLayout.createSequentialGroup()
                  .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                    .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
                      .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                          .addComponent(lblNewLabel_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                          .addComponent(lblNewLabel_2, GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                        .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE))
                      .addPreferredGap(ComponentPlacement.RELATED)
                      .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(btnNewButton, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
                        .addGroup(groupLayout.createSequentialGroup()
                          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                            .addComponent(textBaseRhythm, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
                            .addComponent(textRhythm, GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE))
                          .addGap(2))))
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE))
                  .addGap(12)))
              .addGap(0))
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(lblNewLabel_4, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
              .addGap(15))
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(lblNewLabel)
              .addContainerGap(305, Short.MAX_VALUE))))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addComponent(lblNewLabel)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 330, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
            .addComponent(textRhythm, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(textBaseRhythm, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblNewLabel_3, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
            .addComponent(textSequence, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(lblNewLabel_4, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
              .addGap(360))
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
              .addGap(271))))
    );
    
    
    scrollPane_1.setViewportView(textStrippedLyrics);
    
    
    textCodedLyrics.setText("a triple follows text 6 1 8 mul dur total 4 1 8\r\n3 0 0 0 0 0, -3 0 0 0\r\nlyric lines alternate 6 1 8 with sequence lines 5 1 8\r\n-1 0 0 0 0 0, 1 0 0 0 0\r\n0 0 4 two zeros and total 6 1 8 make a rest 3 1 4\r\n1 0 0 0 0 0, -1 0 0\r\n0 0 8 no sequence for rests 5 1 8\r\n2 0 0 0 0\r\nseparate 3 1 4 sequences 3 1 4 with commas 3 2 8\r\n1 1 1, -1 -1 -1, 0 0 0\r\nthat's it 2 2 8 have fun 2 2 8\r\n1 0, -1 0\r\n0 0 32");
    textCodedLyrics.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    scrollPane.setViewportView(textCodedLyrics);
    frmLyricalGuideMaker.getContentPane().setLayout(groupLayout);
  }
}
