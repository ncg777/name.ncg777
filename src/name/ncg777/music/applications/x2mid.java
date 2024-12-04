package name.ncg777.music.applications;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.TreeSet;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JCheckBox;
import java.awt.SystemColor;
import javax.swing.border.LineBorder;

import name.ncg777.maths.objects.Sequence;
import name.ncg777.maths.objects.WordHexa;
import name.ncg777.maths.objects.WordHexaList;
import name.ncg777.maths.words.predicates.Even;

import javax.swing.SwingConstants;

public class x2mid {

  private JFrame frmXmid;
  private JTextField textField;
  private JCheckBox chckbxNewCheckBox = new JCheckBox("\u00D72");
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          x2mid window = new x2mid();
          window.frmXmid.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public x2mid() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    
    frmXmid = new JFrame();
    frmXmid.setBackground(SystemColor.menu);
    frmXmid.getContentPane().setBackground(SystemColor.window);
    frmXmid.setTitle("\u00D72mid");
    frmXmid.setBounds(100, 100, 434, 198);
    frmXmid.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblRhythm = new JLabel("WordBinary");
    lblRhythm.setHorizontalAlignment(SwingConstants.RIGHT);
    lblRhythm.setBackground(Color.BLACK);
    lblRhythm.setForeground(SystemColor.textText);
    
    textField = new JTextField();
    textField.setBackground(SystemColor.control);
    textField.setForeground(SystemColor.textText);
    textField.setColumns(10);
    
    JLabel lblXmid = new JLabel("\u00D72mid");
    lblXmid.setForeground(SystemColor.textText);
    lblXmid.setBackground(SystemColor.window);
    
    JTextArea txtrOutput = new JTextArea();
    txtrOutput.setBorder(new LineBorder(new Color(0, 0, 0)));
    txtrOutput.setForeground(SystemColor.textText);
    txtrOutput.setBackground(SystemColor.control);
    
    JButton btnXmid = new JButton("x2mid");
    btnXmid.setBackground(Color.DARK_GRAY);
    btnXmid.setForeground(Color.LIGHT_GRAY);
    btnXmid.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        WordHexaList r = WordHexaList.parseHexadecimalWord(textField.getText());
        if(chckbxNewCheckBox.isSelected()) {
          r = WordHexaList.expand(r, 2, false);  
        } else {
          if(!(new Even()).apply(r.asBinaryWord())) {
            txtrOutput.setText("WordBinary is not even");
          }
        }
        
        Sequence s = r.asBinaryWord().asSequence();
        Sequence mid = new Sequence();
        int total = r.size()*16;
        for(int i=0;i<s.size();i++){
          int a = s.get(i);
          int b = s.get((i+1)%s.size());
          
          if(i==s.size()-1){
            b+=total;
          }
          int d = b-a;
          int m = a+(d/2);
          mid.add(m%total);
        }
        WordHexaList o = new WordHexaList();
        for(int i=0;i<r.size();i++){
          TreeSet<Integer> t = new TreeSet<Integer>();
          for(int j=0;j<16;j++){
            if(mid.contains((i*16)+j)){t.add(j);}
          }
          o.add(WordHexa.identifyRhythm16(t));
        }
        txtrOutput.setText(r.toString()+"\n"+o.toString());
      }
    });
    
    
    chckbxNewCheckBox.setSelected(true);
    chckbxNewCheckBox.setForeground(SystemColor.textText);
    chckbxNewCheckBox.setBackground(SystemColor.window);
    GroupLayout groupLayout = new GroupLayout(frmXmid.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(lblXmid, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
                .addComponent(chckbxNewCheckBox)
                .addComponent(lblRhythm, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE))
              .addPreferredGap(ComponentPlacement.RELATED)
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(txtrOutput, GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                .addComponent(textField, GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)))
            .addComponent(btnXmid, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblRhythm)
            .addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(lblXmid)
              .addPreferredGap(ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
              .addComponent(chckbxNewCheckBox)
              .addGap(18))
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(txtrOutput, GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
              .addGap(4)))
          .addGap(21)
          .addComponent(btnXmid)
          .addContainerGap())
    );
    frmXmid.getContentPane().setLayout(groupLayout);
  }
}
