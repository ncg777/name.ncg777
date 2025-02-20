package name.ncg777.maths.music.apps;

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

import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.numbers.fixed.Quartal;
import name.ncg777.maths.numbers.fixed.Quartal.NaturalSequence;
import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.numbers.predicates.Even;
import name.ncg777.maths.sequences.Sequence;

import javax.swing.SwingConstants;

public class x2mid {

  private JFrame frmXmid;
  private JTextField textField;
  private JCheckBox chckbxNewCheckBox = new JCheckBox("\u00D72");

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

  public x2mid() {
    initialize();
  }

  private void initialize() {
    
    frmXmid = new JFrame();
    frmXmid.setBackground(SystemColor.menu);
    frmXmid.getContentPane().setBackground(SystemColor.window);
    frmXmid.setTitle("\u00D72mid");
    frmXmid.setBounds(100, 100, 434, 198);
    frmXmid.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblRhythm = new JLabel("BinaryNatural");
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
        NaturalSequence r = Quartal.instance.newNaturalSequence(Cipher.Name.Hexadecimal, textField.getText());
        if(chckbxNewCheckBox.isSelected()) {
          r = Quartal.instance.expand(r, 2, false);  
        } else {
          if(!(new Even()).apply(r.toNatural().toBinaryNatural())) {
            txtrOutput.setText("BinaryNatural is not even");
          }
        }
        
        Sequence s = r.toNatural().toBinaryNatural().asSequence();
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
        NaturalSequence o = Quartal.instance.newNaturalSequence(Cipher.Name.Hexadecimal);
        for(int i=0;i<r.size();i++){
          TreeSet<Integer> t = new TreeSet<Integer>();
          for(int j=0;j<16;j++){
            if(mid.contains((i*16)+j)){t.add(j);}
          }
          o.add(Quartal.instance.newNatural(Cipher.Name.Hexadecimal, new BinaryNatural(t, 16).toNatural(Cipher.Name.Hexadecimal)));
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
