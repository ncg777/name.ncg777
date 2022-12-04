package name.NicolasCoutureGrenier.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.NicolasCoutureGrenier.Music.R12List;
import name.NicolasCoutureGrenier.Music.R16List;
import name.NicolasCoutureGrenier.Music.R48List;

import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import name.NicolasCoutureGrenier.Music.Rn;

public class BitCounter {

  private JFrame frmBitcountermagicHappens;
  private JTextField textField;
  private JComboBox<Rn> comboBox = new JComboBox<Rn>(new DefaultComboBoxModel<Rn>(Rn.values()));
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          BitCounter window = new BitCounter();
          window.frmBitcountermagicHappens.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public BitCounter() {
    initialize();
  }
  JLabel lblCount = new JLabel("");
  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmBitcountermagicHappens = new JFrame();
    frmBitcountermagicHappens.setTitle("BitCounter (magic happens on enter)");
    frmBitcountermagicHappens.getContentPane().setBackground(Color.BLACK);
    frmBitcountermagicHappens.setBounds(100, 100, 450, 300);
    frmBitcountermagicHappens.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    textField = new JTextField();
    textField.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_ENTER){
          String str = textField.getText().trim();
          if(comboBox.getSelectedItem() == Rn.Hex) {
            lblCount.setText(Integer.toString(R16List.parseR16Seq(str).asRhythm().getK()));
          } else if(comboBox.getSelectedItem() == Rn.Octal) {
            lblCount.setText(Integer.toString(R12List.parseR12Seq(str).asRhythm().getK()));
          }else if(comboBox.getSelectedItem() == Rn.Tribble) {
            lblCount.setText(Integer.toString(R48List.parseR48Seq(str).asRhythm().getK()));
          }
          
          
        }
      }
    });
    textField.setForeground(Color.WHITE);
    textField.setBackground(Color.DARK_GRAY);
    textField.setColumns(10);
    lblCount.setHorizontalAlignment(SwingConstants.CENTER);
    
   
    lblCount.setFont(new Font("Tahoma", Font.PLAIN, 99));
    lblCount.setBackground(Color.BLACK);
    lblCount.setForeground(Color.WHITE);
    
    
    GroupLayout groupLayout = new GroupLayout(frmBitcountermagicHappens.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.TRAILING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(lblCount, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
            .addComponent(textField, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
          .addContainerGap()
          .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(lblCount, GroupLayout.PREFERRED_SIZE, 190, GroupLayout.PREFERRED_SIZE)
          .addContainerGap())
    );
    frmBitcountermagicHappens.getContentPane().setLayout(groupLayout);
  }
}
