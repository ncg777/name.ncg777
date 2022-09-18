package name.ncg.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;

import name.ncg.Music.Rhythm16;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class R16ID {

  private JFrame frmChordId;
  private JTextField textField;
  private JTextField textField_1;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          R16ID window = new R16ID();
          window.frmChordId.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public R16ID() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmChordId = new JFrame();
    frmChordId.getContentPane().setBackground(Color.DARK_GRAY);
    frmChordId.setTitle("R16 Identifier");
    frmChordId.setBounds(100, 100, 468, 135);
    frmChordId.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblPitches = new JLabel("Hex :");
    lblPitches.setForeground(Color.WHITE);
    
    textField = new JTextField();
    textField.setBackground(Color.BLACK);
    textField.setForeground(Color.WHITE);
    textField.setColumns(10);
    
    JLabel lblChord = new JLabel("ID :");
    lblChord.setForeground(Color.WHITE);
    
    final JLabel lblID_1 = new JLabel("");
    lblID_1.setBackground(new Color(0, 0, 0));
    lblID_1.setForeground(Color.WHITE);
    
    JButton btnId = new JButton("HEX -> ID");
    btnId.setBackground(Color.BLACK);
    btnId.setForeground(Color.WHITE);
    btnId.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        lblID_1.setText(Rhythm16.parseRhythm16Hex(textField.getText().trim()).getId());
      }
    });
    
    textField_1 = new JTextField();
    textField_1.setForeground(Color.WHITE);
    textField_1.setBackground(Color.BLACK);
    textField_1.setColumns(10);
    final JLabel lblHEX = new JLabel(" ");
    lblHEX.setForeground(Color.WHITE);
    lblHEX.setBackground(Color.BLACK);
    JLabel lblChord_2 = new JLabel("ID :");
    lblChord_2.setForeground(Color.WHITE);
    
    JButton btnNewButton = new JButton("ID -> HEX");
    btnNewButton.setForeground(Color.WHITE);
    btnNewButton.setBackground(Color.BLACK);
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        lblHEX.setText(Rhythm16.parseRhythm16Id(textField_1.getText().trim()).toString());
      }
    });
    
    
    
    JLabel lblPitches_2 = new JLabel("HEX :");
    lblPitches_2.setForeground(Color.WHITE);
    GroupLayout groupLayout = new GroupLayout(frmChordId.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(lblChord)
                .addComponent(lblPitches))
              .addPreferredGap(ComponentPlacement.UNRELATED)
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                .addComponent(lblID_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(textField, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
              .addPreferredGap(ComponentPlacement.RELATED)
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(lblChord_2)
                .addComponent(lblPitches_2)))
            .addComponent(btnId))
          .addGap(15)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
              .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(textField_1))
              .addComponent(lblHEX, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE))
            .addComponent(btnNewButton))
          .addGap(184))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblPitches)
            .addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblChord_2))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(lblHEX, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
              .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                .addComponent(lblID_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblPitches_2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
              .addComponent(lblChord, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
            .addComponent(btnNewButton, 0, 0, Short.MAX_VALUE)
            .addComponent(btnId, GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE))
          .addContainerGap(11, Short.MAX_VALUE))
    );
    frmChordId.getContentPane().setLayout(groupLayout);
  }
}
