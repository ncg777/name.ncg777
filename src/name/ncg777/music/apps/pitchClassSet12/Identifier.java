package name.ncg777.music.apps.pitchClassSet12;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.ncg777.music.pitchClassSet12.PitchClassSet12;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.TreeSet;
import java.awt.Color;

public class Identifier {

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
          Identifier window = new Identifier();
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
  public Identifier() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmChordId = new JFrame();
    frmChordId.getContentPane().setBackground(Color.DARK_GRAY);
    frmChordId.setTitle("PitchClassSet12 identifier");
    frmChordId.setBounds(100, 100, 556, 146);
    frmChordId.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblPitches = new JLabel("Pitches :");
    lblPitches.setForeground(Color.WHITE);
    
    textField = new JTextField();
    textField.setBackground(Color.BLACK);
    textField.setForeground(Color.WHITE);
    textField.setColumns(10);
    
    JLabel lblChord = new JLabel("Forte number :");
    lblChord.setForeground(Color.WHITE);
    
    final JLabel lblChord_1 = new JLabel("");
    lblChord_1.setBackground(new Color(0, 0, 0));
    lblChord_1.setForeground(Color.WHITE);
    
    JButton btnId = new JButton("Pitches -> Forte number");
    btnId.setBackground(Color.BLACK);
    btnId.setForeground(Color.WHITE);
    btnId.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        TreeSet<Integer> p = new TreeSet<Integer>();
        
        for(String s : textField.getText().trim().split("\\s+")){
          p.add(Integer.parseInt(s));
          
        }
        lblChord_1.setText(PitchClassSet12.identify(p).toForteNumberString());
      }
    });
    
    textField_1 = new JTextField();
    textField_1.setForeground(Color.WHITE);
    textField_1.setBackground(Color.BLACK);
    textField_1.setColumns(10);
    final JLabel lblPitches_1 = new JLabel(" ");
    lblPitches_1.setForeground(Color.WHITE);
    lblPitches_1.setBackground(Color.BLACK);
    JLabel lblChord_2 = new JLabel("PitchClassSet12 :");
    lblChord_2.setForeground(Color.WHITE);
    
    JButton btnNewButton = new JButton("Forte number -> Pitches");
    btnNewButton.setForeground(Color.WHITE);
    btnNewButton.setBackground(Color.BLACK);
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        lblPitches_1.setText(
          PitchClassSet12.parseForte(textField_1.getText())
          .combinationString().replace("{", "").replace("}","").replace(",", ""));
      }
    });
    
    JLabel lblPitches_2 = new JLabel("Pitches :");
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
                .addComponent(lblChord_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
              .addComponent(lblPitches_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE))
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
            .addComponent(lblPitches_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
              .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                .addComponent(lblChord_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
