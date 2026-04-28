package name.ncg777.maths.sequences.apps;

import java.awt.EventQueue;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.sequences.Sequence.ArpType;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Swing GUI application for generating arpeggio index sequences.
 *
 * <p>Allows the user to pick an {@link ArpType}, set the number of positions {@code k},
 * toggle repeat-bottom / repeat-top options, and view the resulting sequence.</p>
 */
public class Arp {

  private JFrame frmArp;
  private JComboBox<ArpType> comboArpType;
  private JSpinner spinnerK;
  private JCheckBox chkRepeatBottom;
  private JCheckBox chkRepeatTop;
  private JTextField txtResult;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Arp window = new Arp();
          window.frmArp.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public Arp() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmArp = new JFrame();
    frmArp.setTitle("Arp");
    frmArp.setBounds(100, 100, 450, 260);
    frmArp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JLabel lblType = new JLabel("Type :");
    lblType.setHorizontalAlignment(SwingConstants.RIGHT);
    lblType.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));

    comboArpType = new JComboBox<>();
    comboArpType.setModel(new DefaultComboBoxModel<>(ArpType.values()));
    comboArpType.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));

    JLabel lblK = new JLabel("k :");
    lblK.setHorizontalAlignment(SwingConstants.RIGHT);
    lblK.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));

    spinnerK = new JSpinner();
    spinnerK.setModel(new SpinnerNumberModel(7, 2, null, 1));
    spinnerK.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));

    chkRepeatBottom = new JCheckBox("Repeat bottom");
    chkRepeatBottom.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));

    chkRepeatTop = new JCheckBox("Repeat top");
    chkRepeatTop.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));

    JButton btnGenerate = new JButton("Generate");
    btnGenerate.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    btnGenerate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          ArpType type = (ArpType) comboArpType.getSelectedItem();
          int k = (Integer) spinnerK.getValue();
          boolean repeatBottom = chkRepeatBottom.isSelected();
          boolean repeatTop = chkRepeatTop.isSelected();
          Sequence result = Sequence.arp(type, k, repeatBottom, repeatTop);
          txtResult.setText(result.toString());
        } catch (Exception ex) {
          txtResult.setText("Error: " + ex.getMessage());
        }
      }
    });

    JLabel lblResult = new JLabel("Result :");
    lblResult.setHorizontalAlignment(SwingConstants.RIGHT);
    lblResult.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));

    txtResult = new JTextField();
    txtResult.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 10));
    txtResult.setColumns(10);

    GroupLayout groupLayout = new GroupLayout(frmArp.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
            .addComponent(lblResult, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblK, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblType, GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(comboArpType, 0, 344, Short.MAX_VALUE)
            .addComponent(spinnerK, GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
            .addComponent(chkRepeatBottom)
            .addComponent(chkRepeatTop)
            .addComponent(btnGenerate, GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
            .addComponent(txtResult, GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblType)
            .addComponent(comboArpType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblK)
            .addComponent(spinnerK, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(chkRepeatBottom)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(chkRepeatTop)
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addComponent(btnGenerate)
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblResult)
            .addComponent(txtResult, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    frmArp.getContentPane().setLayout(groupLayout);
  }
}
