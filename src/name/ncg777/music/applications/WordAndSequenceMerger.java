package name.ncg777.music.applications;
//
//import java.awt.EventQueue;
//
//import javax.swing.JFrame;
//import javax.swing.GroupLayout;
//import javax.swing.GroupLayout.Alignment;
//import javax.swing.JLabel;
//import javax.swing.LayoutStyle.ComponentPlacement;
//
//import name.ncg777.maths.objects.Sequence;
//import name.ncg777.maths.objects.BinaryWord;
//import name.ncg777.maths.objects.OctalSentence;
//import name.ncg777.music.WordHexaList;
//import name.ncg777.music.Rn;
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
//public class RhythmAndSequenceMerger {
//
//  private JFrame frmRhythmAndSequenceMerger;
//  private JTextField txtResult;
//  private JTextArea txtArea = new JTextArea();
//  private JComboBox<Rn> comboBox = new JComboBox<Rn>(new DefaultComboBoxModel<Rn>(Rn.values()));
//  private JTextField textSeq;
//  /**
//   * Launch the application.
//   */
//  public static void main(String[] args) {
//    EventQueue.invokeLater(new Runnable() {
//      public void run() {
//        try {
//          RhythmAndSequenceMerger window = new RhythmAndSequenceMerger();
//          window.frmRhythmAndSequenceMerger.setVisible(true);
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
//  public RhythmAndSequenceMerger() {
//    initialize();
//  }
//
//  /**
//   * Initialize the contents of the frame.
//   */
//  private void initialize() {
//    frmRhythmAndSequenceMerger = new JFrame();
//    frmRhythmAndSequenceMerger.setResizable(false);
//    frmRhythmAndSequenceMerger.setTitle("BinaryWord and Sequence Merger");
//    frmRhythmAndSequenceMerger.setBounds(100, 100, 450, 293);
//    frmRhythmAndSequenceMerger.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    
//    JButton btnMerge = new JButton("Merge");
//    btnMerge.addActionListener(new ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        String[] lines = txtArea.getText().trim().split("\n+");
//        ArrayList<BinaryWord> arr = new ArrayList<BinaryWord>();
//        ArrayList<Sequence> sequences = new ArrayList<>();
//        for(int i=0; i<lines.length;i++) {
//          String[] parts = lines[i].split(",");
//          Sequence s = new Sequence();
//          if(parts.length > 1) {s = Sequence.parse(parts[1].trim());}
//          sequences.add(s);
//          if(comboBox.getSelectedItem()==Rn.Hex) {
//            arr.add(HexadecimalSentence.parseRhythmHexaSeq(parts[0].trim()).asRhythm());
//          } else if(comboBox.getSelectedItem()==Rn.Octal) {
//            arr.add(OctalSentence.parseOctal(parts[0].trim()).asRhythm());
//          }
//        }
//        
//        BinaryWord result = BinaryWord.merge(arr);
//          
//        if(comboBox.getSelectedItem()==Rn.Hex) {
//          txtResult.setText(HexadecimalSentence.fromRhythm(result).toString());
//        } else if(comboBox.getSelectedItem()==Rn.Octal) {
//          txtResult.setText(OctalSentence.fromRhythm(result).toString());
//        }
//        
//        Sequence s = new Sequence();
//        Sequence indices = new Sequence();
//        for(int i=0;i<arr.size();i++) {indices.add(0);}
//        
//        for(int i=0;i<result.getN();i++) {
//          int ir = i%arr.size();
//          if(result.get(i)) {
//            s.add(sequences.get(ir).get(indices.get(ir)));
//            indices.set(ir, (indices.get(ir)+1)%sequences.get(ir).size());
//          }
//        }
//        textSeq.setText(s.toString());
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
//    JLabel lblNewLabel = new JLabel("Sequence:");
//    
//    textSeq = new JTextField();
//    textSeq.setColumns(10);
//    
//    
//    
//    
//    GroupLayout groupLayout = new GroupLayout(frmRhythmAndSequenceMerger.getContentPane());
//    groupLayout.setHorizontalGroup(
//      groupLayout.createParallelGroup(Alignment.LEADING)
//        .addGroup(groupLayout.createSequentialGroup()
//          .addContainerGap()
//          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
//            .addComponent(lblNewLabel)
//            .addComponent(lblResult))
//          .addPreferredGap(ComponentPlacement.RELATED)
//          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
//            .addComponent(textSeq, GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
//            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE)
//            .addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
//            .addComponent(txtResult, GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
//            .addComponent(btnMerge, GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE))
//          .addContainerGap())
//    );
//    groupLayout.setVerticalGroup(
//      groupLayout.createParallelGroup(Alignment.TRAILING)
//        .addGroup(groupLayout.createSequentialGroup()
//          .addContainerGap()
//          .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
//          .addPreferredGap(ComponentPlacement.RELATED)
//          .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
//          .addPreferredGap(ComponentPlacement.RELATED)
//          .addComponent(btnMerge)
//          .addPreferredGap(ComponentPlacement.RELATED)
//          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
//            .addComponent(lblResult)
//            .addComponent(txtResult, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//          .addPreferredGap(ComponentPlacement.UNRELATED)
//          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
//            .addComponent(lblNewLabel)
//            .addComponent(textSeq, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//          .addGap(6))
//    );
//    comboBox.setSelectedIndex(1);
//    
//    
//    txtArea.setText("88 88, -1 1\r\nAA AA, 2 -2\r\n00 00\r\n80 00, 0");
//    scrollPane.setViewportView(txtArea);
//    frmRhythmAndSequenceMerger.getContentPane().setLayout(groupLayout);
//  }
//}
