package name.ncg777.maths.applications.words;

//import java.awt.EventQueue;
//
//import javax.swing.JFrame;
//import javax.swing.GroupLayout;
//import javax.swing.GroupLayout.Alignment;
//import javax.swing.JLabel;
//import javax.swing.LayoutStyle.ComponentPlacement;
//
//import name.ncg777.maths.objects.BinaryWord;
//import name.ncg777.maths.objects.HexadecimalSentence;
//import name.ncg777.maths.objects.OctalSentence;
//
//import javax.swing.JTextField;
//import javax.swing.JButton;
//import javax.swing.SwingConstants;
//
//import java.awt.event.ActionListener;
//import java.util.ArrayList;
//import java.awt.event.ActionEvent;
//import javax.swing.JComboBox;
//import javax.swing.DefaultComboBoxModel;
//import javax.swing.JTextArea;
//import javax.swing.JScrollPane;
//
//public class RhythmMerger {
//
//  private JFrame frmRhythmMerger;
//  private JTextField txtResult;
//  private JTextArea txtArea = new JTextArea();
//  private JComboBox<Alphabet> comboBox = new JComboBox<Alphabet>(new DefaultComboBoxModel<Alphabet>(Alphabet.values()));
//  /**
//   * Launch the application.
//   */
//  public static void main(String[] args) {
//    EventQueue.invokeLater(new Runnable() {
//      public void run() {
//        try {
//          RhythmMerger window = new RhythmMerger();
//          window.frmRhythmMerger.setVisible(true);
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
//  public RhythmMerger() {
//    initialize();
//  }
//
//  /**
//   * Initialize the contents of the frame.
//   */
//  private void initialize() {
//    frmRhythmMerger = new JFrame();
//    frmRhythmMerger.setResizable(false);
//    frmRhythmMerger.setTitle("BinaryWord Merger");
//    frmRhythmMerger.setBounds(100, 100, 450, 273);
//    frmRhythmMerger.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    
//    JButton btnMerge = new JButton("Merge");
//    btnMerge.addActionListener(new ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        String[] lines = txtArea.getText().trim().split("\n+");
//        ArrayList<BinaryWord> arr = new ArrayList<BinaryWord>();
//        for(int i=0; i<lines.length;i++) {
//          if(comboBox.getSelectedItem()==Alphabet.Hexadecimal) {
//            arr.add(HexadecimalSentence.parseHexadecimalWord(lines[i].trim()).asBinaryWord());
//          } else if(comboBox.getSelectedItem()==Alphabet.Octal) {
//            arr.add(OctalSentence.parseOctalWord(lines[i].trim()).asBinary());
//          }
//        }
//        
//        BinaryWord result = BinaryWord.merge(arr);
//          
//        if(comboBox.getSelectedItem()==Alphabet.Hexadecimal) {
//          txtResult.setText(HexadecimalSentence.fromRhythm(result).toString());
//        } else if(comboBox.getSelectedItem()==Alphabet.Octal) {
//          txtResult.setText(OctalSentence.fromRhythm(result).toString());
//        }
//      }
//    });
//    
//    JLabel lblResult = new JLabel("Result:");
//    lblResult.setHorizontalAlignment(SwingConstants.RIGHT);
//    
//    txtResult = new JTextField();
//    txtResult.setColumns(10);
//    
//    JScrollPane scrollPane = new JScrollPane();
//    
//    
//    
//    
//    GroupLayout groupLayout = new GroupLayout(frmRhythmMerger.getContentPane());
//    groupLayout.setHorizontalGroup(
//      groupLayout.createParallelGroup(Alignment.TRAILING)
//        .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
//          .addContainerGap()
//          .addComponent(lblResult)
//          .addPreferredGap(ComponentPlacement.RELATED)
//          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
//            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE)
//            .addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
//            .addComponent(txtResult, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
//            .addComponent(btnMerge, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
//          .addContainerGap())
//    );
//    groupLayout.setVerticalGroup(
//      groupLayout.createParallelGroup(Alignment.TRAILING)
//        .addGroup(groupLayout.createSequentialGroup()
//          .addContainerGap()
//          .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
//          .addPreferredGap(ComponentPlacement.RELATED)
//          .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
//          .addPreferredGap(ComponentPlacement.RELATED)
//          .addComponent(btnMerge)
//          .addPreferredGap(ComponentPlacement.RELATED)
//          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
//            .addComponent(lblResult)
//            .addComponent(txtResult, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//          .addGap(31))
//    );
//    comboBox.setSelectedIndex(1);
//    
//    
//    txtArea.setText("88 88\r\nAA AA\r\n80 80\r\n80 00");
//    scrollPane.setViewportView(txtArea);
//    frmRhythmMerger.getContentPane().setLayout(groupLayout);
//  }
//}
