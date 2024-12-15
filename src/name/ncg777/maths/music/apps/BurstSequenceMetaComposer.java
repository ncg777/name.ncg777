package name.ncg777.maths.music.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.google.common.base.Joiner;

import name.ncg777.maths.enumerations.MetaCompositionOfListEnumeration;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.awt.event.ActionEvent;

public class BurstSequenceMetaComposer {

  private JFrame frmBurstSequenceMetacomposer;
  private JTextArea textResult;
  private JTextArea textInput;

 
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          BurstSequenceMetaComposer window = new BurstSequenceMetaComposer();
          window.frmBurstSequenceMetacomposer.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public BurstSequenceMetaComposer() {
    initialize();
  }

  private void initialize() {
    frmBurstSequenceMetacomposer = new JFrame();
    frmBurstSequenceMetacomposer.setTitle("Burst Sequence MetaComposer");
    frmBurstSequenceMetacomposer.setBounds(100, 100, 514, 302);
    frmBurstSequenceMetacomposer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JScrollPane scrollPane = new JScrollPane();
    
    JScrollPane scrollPane_1 = new JScrollPane();
    
    JLabel lblNewLabel = new JLabel("Parts on each line:");
    
    JLabel lblNewLabel_1 = new JLabel("Result:");
    
    JButton btnNewButton = new JButton("METACOMPOSE");
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        var parts = Arrays.asList(textInput.getText().replaceAll("\n+", "\n").trim().split("\n"));
        var mce = new MetaCompositionOfListEnumeration(Joiner.on(" ").join(parts),true);
        
        var sb = new StringBuilder();
        while(mce.hasMoreElements()) {
          sb.append(mce.nextElement().replace("-", "||").replace(" ", "--")+"\n");
        }
        textResult.setText(sb.toString());
      }
    });
    GroupLayout groupLayout = new GroupLayout(frmBurstSequenceMetacomposer.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addComponent(lblNewLabel, Alignment.LEADING)
            .addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
            .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
              .addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
              .addGap(1))
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED, 309, Short.MAX_VALUE)
              .addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)))
          .addGap(11))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addComponent(lblNewLabel)
          .addGap(5)
          .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
            .addGroup(groupLayout.createSequentialGroup()
              .addGap(12)
              .addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED))
            .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
              .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
              .addGap(7)))
          .addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
          .addContainerGap())
    );
    
    textInput = new JTextArea();
    scrollPane.setViewportView(textInput);
    
    textResult = new JTextArea();
    textResult.setEditable(false);
    scrollPane_1.setViewportView(textResult);
    frmBurstSequenceMetacomposer.getContentPane().setLayout(groupLayout);
  }

}
