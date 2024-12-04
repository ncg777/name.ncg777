package name.ncg777.music.applications;
//
//import java.awt.EventQueue;
//
//import javax.swing.JFrame;
//import javax.swing.GroupLayout;
//import javax.swing.GroupLayout.Alignment;
//import javax.swing.JLabel;
//import javax.swing.JTextField;
//import javax.swing.LayoutStyle.ComponentPlacement;
//
//import name.ncg777.maths.objects.WordBinary;
//import name.ncg777.maths.objects.WordHexaList;
//import name.ncg777.maths.objects.WordOctalList;
//
//import javax.swing.JSpinner;
//import javax.swing.SpinnerNumberModel;
//import javax.swing.JButton;
//import java.awt.event.ActionListener;
//import java.awt.event.ActionEvent;
//import javax.swing.JCheckBox;
//import javax.swing.SwingConstants;
//import javax.swing.JComboBox;
//import javax.swing.DefaultComboBoxModel;
//
//public class RnExpander {
//
//  private JFrame frmRExpander;
//  private JTextField txtRhythm;
//  private JTextField txtResult;
//  private JTextField txtNot;
//  private JComboBox<Alphabet> comboBox = new JComboBox<Alphabet>(new DefaultComboBoxModel<Alphabet>(Alphabet.values()));
//  /**
//   * Launch the application.
//   */
//  public static void main(String[] args) {
//    EventQueue.invokeLater(new Runnable() {
//      public void run() {
//        try {
//          RnExpander window = new RnExpander();
//          window.frmRExpander.setVisible(true);
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
//      }
//    });
//  }
//
//  /**
//   * Create the application.
//   */
//  public RnExpander() {
//    initialize();
//  }
//
//  /**
//   * Initialize the contents of the frame.
//   */
//  private void initialize() {
//    frmRExpander = new JFrame();
//    frmRExpander.setResizable(false);
//    frmRExpander.setTitle("Rn Expander");
//    frmRExpander.setBounds(100, 100, 542, 209);
//    frmRExpander.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    
//    JLabel lblRhythm = new JLabel("WordBinary :");
//    
//    txtRhythm = new JTextField();
//    txtRhythm.setColumns(10);
//    
//    JLabel lblMult = new JLabel("Mult :");
//    
//    JSpinner spinner = new JSpinner();
//    spinner.setModel(new SpinnerNumberModel(1, 1, null, 1));
//    
//    JLabel lblResult = new JLabel("Result :");
//    
//    txtResult = new JTextField();
//    txtResult.setEditable(false);
//    txtResult.setColumns(10);
//    
//    JCheckBox chckbxFill = new JCheckBox("Fill");
//    JSpinner rot = new JSpinner();
//    JButton btnExpand = new JButton("Expand and Rotate");
//    btnExpand.addActionListener(new ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        if(comboBox.getSelectedItem() == Alphabet.Hexadecimal) {
//          WordHexaList o = WordHexaList.rotate(WordHexaList.expand(WordHexaList.parseRhythmHexaSeq(txtRhythm.getText()), (int)spinner.getValue(), chckbxFill.isSelected()), (int)rot.getValue());
//          txtResult.setText(o.toString());
//          txtNot.setText(WordHexaList.not(o).toString());
//        } else if(comboBox.getSelectedItem() == Alphabet.Octal) {
//          WordOctalList o = WordOctalList.rotate(WordOctalList.expand(WordOctalList.parseOctal(txtRhythm.getText()), (int)spinner.getValue(), chckbxFill.isSelected()), (int)rot.getValue());
//          txtResult.setText(o.toString());
//          txtNot.setText(WordOctalList.not(o).toString());
//        }
//      }
//    });
//    
//    JLabel lblNot = new JLabel("Not :");
//    lblNot.setHorizontalAlignment(SwingConstants.RIGHT);
//    
//    txtNot = new JTextField();
//    txtNot.setEditable(false);
//    txtNot.setColumns(10);
//    
//    JLabel lblRotate = new JLabel("Rotate :");
//    lblRotate.setHorizontalAlignment(SwingConstants.RIGHT);
//    
//    
//    
//    
//    
//    
//    GroupLayout groupLayout = new GroupLayout(frmRExpander.getContentPane());
//    groupLayout.setHorizontalGroup(
//      groupLayout.createParallelGroup(Alignment.LEADING)
//        .addGroup(groupLayout.createSequentialGroup()
//          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
//            .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
//              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
//                .addGroup(groupLayout.createSequentialGroup()
//                  .addGap(17)
//                  .addComponent(lblResult))
//                .addGroup(groupLayout.createSequentialGroup()
//                  .addGap(30)
//                  .addComponent(lblNot)))
//              .addPreferredGap(ComponentPlacement.RELATED)
//              .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
//                .addComponent(txtNot, Alignment.LEADING)
//                .addComponent(txtResult, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
//                .addComponent(btnExpand, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
//            .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
//              .addContainerGap()
//              .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
//                .addGroup(groupLayout.createSequentialGroup()
//                  .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
//                  .addPreferredGap(ComponentPlacement.RELATED)
//                  .addComponent(lblMult)
//                  .addPreferredGap(ComponentPlacement.RELATED)
//                  .addComponent(spinner, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
//                  .addPreferredGap(ComponentPlacement.RELATED, 2, Short.MAX_VALUE)
//                  .addComponent(chckbxFill)
//                  .addGap(18)
//                  .addComponent(lblRotate)
//                  .addPreferredGap(ComponentPlacement.RELATED)
//                  .addComponent(rot, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
//                  .addGap(94))
//                .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
//                  .addComponent(lblRhythm)
//                  .addPreferredGap(ComponentPlacement.RELATED)
//                  .addComponent(txtRhythm, GroupLayout.PREFERRED_SIZE, 449, GroupLayout.PREFERRED_SIZE)))
//              .addGap(82)))
//          .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
//    );
//    groupLayout.setVerticalGroup(
//      groupLayout.createParallelGroup(Alignment.LEADING)
//        .addGroup(groupLayout.createSequentialGroup()
//          .addContainerGap()
//          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
//            .addComponent(lblRhythm)
//            .addComponent(txtRhythm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//          .addPreferredGap(ComponentPlacement.UNRELATED)
//          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
//            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//            .addComponent(lblMult)
//            .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//            .addComponent(chckbxFill)
//            .addComponent(lblRotate)
//            .addComponent(rot, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//          .addPreferredGap(ComponentPlacement.UNRELATED)
//          .addComponent(btnExpand)
//          .addGap(4)
//          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
//            .addComponent(txtResult, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//            .addComponent(lblResult))
//          .addGap(8)
//          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
//            .addComponent(txtNot, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//            .addComponent(lblNot))
//          .addContainerGap(22, Short.MAX_VALUE))
//    );
//    frmRExpander.getContentPane().setLayout(groupLayout);
//  }
//}
