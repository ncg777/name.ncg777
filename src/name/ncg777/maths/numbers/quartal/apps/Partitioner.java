package name.ncg777.maths.numbers.quartal.apps;

//import java.awt.EventQueue;
//
//import javax.swing.JFrame;
//import javax.swing.GroupLayout;
//import javax.swing.GroupLayout.Alignment;
//import javax.swing.JLabel;
//import javax.swing.JTextField;
//import javax.swing.LayoutStyle.ComponentPlacement;
//import javax.swing.SwingConstants;
//import javax.swing.JButton;
//import javax.swing.JTextArea;
//import java.awt.event.ActionListener;
//import java.util.ArrayList;
//import java.util.BitSet;
//import java.awt.event.ActionEvent;
//import javax.swing.JSpinner;
//import javax.swing.SpinnerNumberModel;
//import java.awt.Font;
//import javax.swing.JComboBox;
//import javax.swing.DefaultComboBoxModel;
//import javax.swing.border.LineBorder;
//
//import name.ncg777.maths.objects.Sequence;
//import name.ncg777.maths.objects.BinaryWord;
//import name.ncg777.maths.objects.HexadecimalSentence;
//import name.ncg777.maths.objects.OctalSentence;
//
//import java.awt.Color;
//import javax.swing.JScrollPane;
//
//public class WordPartitioner {
//
//  private JFrame frmRPartitioner;
//  private JTextField txtR;
//  private JTextField txtPartition;
//  private JTextArea txtResult;
//  private JSpinner spinner;
//  private JComboBox<Cipher> comboBox = new JComboBox<Cipher>(new DefaultComboBoxModel<Cipher>(Cipher.values()));;
//  /**
//   * Launch the application.
//   */
//  public static void main(String[] args) {
//    EventQueue.invokeLater(new Runnable() {
//      public void run() {
//        try {
//          RnPartitioner window = new RnPartitioner();
//          window.frmRPartitioner.setVisible(true);
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
//  public RnPartitioner() {
//    initialize();
//  }
//
//  /**
//   * Initialize the contents of the frame.
//   */
//  private void initialize() {
//    frmRPartitioner = new JFrame();
//    frmRPartitioner.setTitle("Rn Partitioner");
//    frmRPartitioner.setBounds(100, 100, 456, 411);
//    frmRPartitioner.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    
//    JLabel lblR = new JLabel("Rn:");
//    lblR.setFont(new Font("Unifont", Font.PLAIN, 11));
//    lblR.setHorizontalAlignment(SwingConstants.RIGHT);
//    
//    txtR = new JTextField();
//    txtR.setFont(new Font("Unifont", Font.PLAIN, 11));
//    txtR.setColumns(10);
//    
//    JLabel lblPartition = new JLabel("Partition:");
//    lblPartition.setFont(new Font("Unifont", Font.PLAIN, 11));
//    lblPartition.setHorizontalAlignment(SwingConstants.RIGHT);
//    
//    txtPartition = new JTextField();
//    txtPartition.setFont(new Font("Unifont", Font.PLAIN, 11));
//    txtPartition.setColumns(10);
//    
//    JButton btnPartition = new JButton("Partition");
//    btnPartition.setFont(new Font("Unifont", Font.PLAIN, 11));
//    spinner = new JSpinner();
//    spinner.setFont(new Font("Unifont", Font.PLAIN, 11));
//    btnPartition.addActionListener(new ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        if(comboBox.getSelectedItem() == Cipher.Hexadecimal) {
//          HexadecimalSentence r = HexadecimalSentence.parseHexadecimalWord(txtR.getText().trim());
//          BinaryNumber r1 = r.asWord();
//          Sequence p0 = Sequence.parse(txtPartition.getText());
//          Sequence p = p0.asOrdinalsUnipolar().addToAll(-1);
//          int k = p.size();
//          int pdistinct = p.distinct().size();
//          int n= r1.getN();
//          ArrayList<BinaryNumber> output = new ArrayList<BinaryNumber>();
//          
//          for(int i=0;i<pdistinct;i++) output.add(BinaryNumber.buildRhythm(new BitSet(), n));
//          for(int i=0;i<n;i++) {
//            output.get(p.get(i%k)).set(i, r1.get(i));
//          }
//          
//          String strOut = "";
//          for(int i=0; i<output.size();i++) {
//            strOut += HexadecimalSentence.expand(HexadecimalSentence.fromRhythm(output.get(i)), (int)spinner.getValue(), true).toString() + "\n";
//          }
//          txtResult.setText(strOut);
//        } else if(comboBox.getSelectedItem() == Cipher.Octal) {
//          OctalSentence r = OctalSentence.parseOctalWord(txtR.getText().trim());
//          BinaryNumber r1 = r.asRhythm();
//          Sequence p0 = Sequence.parse(txtPartition.getText());
//          Sequence p = p0.asOrdinalsUnipolar().addToAll(-1);
//          int k = p.size();
//          int pdistinct = p.distinct().size();
//          int n= r1.getN();
//          ArrayList<BinaryNumber> output = new ArrayList<BinaryNumber>();
//          
//          for(int i=0;i<pdistinct;i++) output.add(BinaryNumber.buildRhythm(new BitSet(), n));
//          for(int i=0;i<n;i++) {
//            output.get(p.get(i%k)).set(i, r1.get(i));
//          }
//          
//          String strOut = "";
//          for(int i=0; i<output.size();i++) {
//            strOut += OctalSentence.expand(OctalSentence.fromRhythm(output.get(i)), (int)spinner.getValue(), true).toString() + "\n";
//          }
//          txtResult.setText(strOut);
//        }
//        
//        
//        /* 
//        HexadecimalSentence r = HexadecimalSentence.parseR16Seq(txtR.getText().trim());
//        BinaryNumber r1 = r.asRhythm();
//        Sequence p0 = Sequence.parse(txtPartition.getText());
//        Sequence p = new Sequence();
//        for(int i=0; i <r1.getK();i++) {
//          p.add(p0.get(i%p0.size()));
//        }
//        
//        Combination[] c = r1.partition(p);
//        String output = "";
//        for(Combination x : c) {
//          output += HexadecimalSentence.expand(HexadecimalSentence.fromRhythm(BinaryNumber.buildRhythm(x, r.size()*16)), (int)spinner.getValue(), true) + "\n";
//        }
//        
//        txtResult.setText(output);
//        */
//        
//      }
//    });
//    
//    
//    spinner.setModel(new SpinnerNumberModel(1, 1, null, 1));
//    
//    JLabel lblMult = new JLabel("Mult:");
//    lblMult.setFont(new Font("Unifont", Font.PLAIN, 11));
//    lblMult.setHorizontalAlignment(SwingConstants.RIGHT);
//    
//    JScrollPane scrollPane = new JScrollPane();
//    
//    
//    
//    GroupLayout groupLayout = new GroupLayout(frmRPartitioner.getContentPane());
//    groupLayout.setHorizontalGroup(
//      groupLayout.createParallelGroup(Alignment.TRAILING)
//        .addGroup(groupLayout.createSequentialGroup()
//          .addGap(7)
//          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
//            .addComponent(lblMult, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//            .addComponent(lblPartition, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//            .addComponent(lblR, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
//          .addPreferredGap(ComponentPlacement.RELATED)
//          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
//            .addComponent(comboBox, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE)
//            .addComponent(txtPartition, Alignment.LEADING)
//            .addComponent(btnPartition, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//            .addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
//            .addComponent(txtR, Alignment.LEADING)
//            .addComponent(spinner, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE))
//          .addGap(19))
//    );
//    groupLayout.setVerticalGroup(
//      groupLayout.createParallelGroup(Alignment.LEADING)
//        .addGroup(groupLayout.createSequentialGroup()
//          .addContainerGap()
//          .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
//          .addGap(12)
//          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
//            .addComponent(lblR)
//            .addComponent(txtR, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//          .addPreferredGap(ComponentPlacement.RELATED)
//          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
//            .addComponent(lblPartition)
//            .addComponent(txtPartition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//          .addPreferredGap(ComponentPlacement.RELATED)
//          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
//            .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//            .addComponent(lblMult))
//          .addPreferredGap(ComponentPlacement.RELATED)
//          .addComponent(btnPartition)
//          .addPreferredGap(ComponentPlacement.RELATED)
//          .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
//          .addContainerGap())
//    );
//    txtResult = new JTextArea();
//    scrollPane.setViewportView(txtResult);
//    txtResult.setBorder(new LineBorder(new Color(0, 0, 0)));
//    txtResult.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 13));
//    txtResult.setEditable(false);
//    frmRPartitioner.getContentPane().setLayout(groupLayout);
//  }
//}
