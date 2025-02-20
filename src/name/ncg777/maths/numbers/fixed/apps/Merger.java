package name.ncg777.maths.numbers.fixed.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.maths.Combination;
import name.ncg777.maths.numbers.Cipher;
import name.ncg777.maths.numbers.fixed.Quartal;
import name.ncg777.maths.numbers.BinaryNatural;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class Merger {

  private JFrame frmRhythmMerger;
  private JTextField txtResult;
  private JTextArea txtArea = new JTextArea();
  private JComboBox<Cipher.Name> comboBox = new JComboBox<Cipher.Name>(new DefaultComboBoxModel<Cipher.Name>(Cipher.Name.values()));

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Merger window = new Merger();
          window.frmRhythmMerger.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }


  public Merger() {
    initialize();
  }

  private void initialize() {
    frmRhythmMerger = new JFrame();
    frmRhythmMerger.setResizable(false);
    frmRhythmMerger.setTitle("Merger");
    frmRhythmMerger.setBounds(100, 100, 450, 273);
    frmRhythmMerger.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JButton btnMerge = new JButton("Merge");
    btnMerge.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String[] lines = txtArea.getText().trim().split("\n+");
        var arr = new ArrayList<BinaryNatural>();
        for(int i=0; i<lines.length;i++) {
          arr.add(
              Quartal.instance.newNaturalSequence(
                  (Cipher.Name)comboBox.getSelectedItem(),
                  lines[i].trim())
          .toBinaryNatural().reverse());
        }
        
        BinaryNatural result = BinaryNatural.build(Combination.mergeAll(arr));
          
        txtResult.setText(
            (Quartal.instance.newNaturalSequence((Cipher.Name)comboBox.getSelectedItem(),result)).toString()
        );
        
      }
    });
    
    JLabel lblResult = new JLabel("Result:");
    lblResult.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtResult = new JTextField();
    txtResult.setColumns(10);
    
    JScrollPane scrollPane = new JScrollPane();
    
    GroupLayout groupLayout = new GroupLayout(frmRhythmMerger.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.TRAILING)
        .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
          .addContainerGap()
          .addComponent(lblResult)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE)
            .addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
            .addComponent(txtResult, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
            .addComponent(btnMerge, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.TRAILING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnMerge)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblResult)
            .addComponent(txtResult, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addGap(31))
    );    
    
    txtArea.setText("88 88\r\nAA AA\r\n80 80\r\n80 00");
    scrollPane.setViewportView(txtArea);
    frmRhythmMerger.getContentPane().setLayout(groupLayout);
  }
}
