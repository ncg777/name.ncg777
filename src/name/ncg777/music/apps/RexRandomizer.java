package name.ncg777.music.apps;

//import java.awt.EventQueue;
//
//import javax.swing.JFrame;
//import javax.swing.DefaultComboBoxModel;
//import javax.swing.GroupLayout;
//import javax.swing.GroupLayout.Alignment;
//import javax.swing.JLabel;
//import javax.swing.JTextField;
//import javax.swing.LayoutStyle.ComponentPlacement;
//
//import name.ncg777.computing.structures.CollectionUtils;
//import name.ncg777.computing.structures.HomoPair;
//import name.ncg777.maths.objects.Sequence;
//import name.ncg777.maths.objects.BinaryWord;
//import name.ncg777.maths.objects.OctalSentence;
//import name.ncg777.music.WordHexaList;
//import name.ncg777.music.Rn;
//
//import javax.swing.JSpinner;
//import javax.swing.SpinnerNumberModel;
//import javax.swing.SwingConstants;
//import javax.swing.JButton;
//import javax.swing.JTextArea;
//import java.awt.event.ActionListener;
//import java.util.TreeMap;
//import java.util.TreeSet;
//import java.awt.event.ActionEvent;
//import javax.swing.JScrollPane;
//import java.awt.Font;
//import javax.swing.JComboBox;
//
//public class RexRandomizer {
//
//  private JFrame frmRexRandomizer;
//  private JTextField textRex;
//  private JTextField textEQClasses;
//  private JTextField textResynth;
//  private JComboBox<Rn> comboBox = new JComboBox<Rn>(new DefaultComboBoxModel<Rn>(Rn.values()));
//  /**
//   * Launch the application.
//   */
//  public static void main(String[] args) {
//    EventQueue.invokeLater(new Runnable() {
//      public void run() {
//        try {
//          RexRandomizer window = new RexRandomizer();
//          window.frmRexRandomizer.setVisible(true);
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
//  public RexRandomizer() {
//    initialize();
//  }
//
//  /**
//   * Initialize the contents of the frame.
//   */
//  private void initialize() {
//    frmRexRandomizer = new JFrame();
//    frmRexRandomizer.setTitle("REX Randomizer");
//    frmRexRandomizer.setBounds(100, 100, 510, 346);
//    frmRexRandomizer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    
//    JLabel lblRexRhythm = new JLabel("REX rhythm :");
//    lblRexRhythm.setFont(new Font("Unifont", Font.PLAIN, 11));
//    lblRexRhythm.setHorizontalAlignment(SwingConstants.RIGHT);
//    
//    textRex = new JTextField();
//    textRex.setColumns(10);
//    
//    JSpinner spinner = new JSpinner();
//    spinner.setModel(new SpinnerNumberModel(1, 1, null, 1));
//    
//    JLabel lblbars = new JLabel("#BARS :");
//    lblbars.setFont(new Font("Unifont", Font.PLAIN, 11));
//    lblbars.setHorizontalAlignment(SwingConstants.RIGHT);
//    JButton btnGenerate = new JButton("Generate");
//    
//    JScrollPane scrollPane = new JScrollPane();
//    JTextArea txtrResult = new JTextArea();
//    
//    btnGenerate.addActionListener(new ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        
//        Sequence map = Sequence.parse(textEQClasses.getText().trim());
//        Sequence resynth = Sequence.parse(textResynth.getText().trim());
//        
//        int modul = map.size();
//        int modulr = resynth.size();
//        
//        int nbBars = (int)spinner.getValue();
//        int mult = -1;
//        switch((Rn)comboBox.getSelectedItem()) {
//          case Hex:
//            mult = 16;
//            break;
//          case Octal:
//            mult = 12;
//            break;
//        }
//        int len = nbBars*mult;
//        
//        BinaryWord r = null;
//        
//        switch((Rn)comboBox.getSelectedItem()) {
//          case Hex:
//            r = HexadecimalSentence.parseRhythmHexaSeq(textRex.getText()).asRhythm();
//            break;
//          case Octal:
//            r = OctalSentence.parseOctal(textRex.getText()).asRhythm();
//            break;
//        }
//        
//        @SuppressWarnings("null")
//        Sequence c = r.getComposition().asSequence();
//        
//        TreeMap<Integer, TreeSet<HomoPair<Integer>>> posM = new TreeMap<>();
//        
//        for(int i=0;i<map.distinct().size();i++) {
//          posM.put(i, new TreeSet<HomoPair<Integer>>());
//        }
//        
//        int pos = 0;
//        int pitch = 36;
//
//        for(int i=0;i<c.size();i++) {
//          int dur = c.get(i);
//          int pm = pos % modul;
//          HomoPair<Integer> p = HomoPair.makeHomoPair(pitch, dur);
//          posM.get(map.get(pm)).add(p);
//          pos += dur;
//          pitch++;
//        }
//        
//        TreeSet<Integer> newRhythm = new TreeSet<>();
//        Sequence pitchSeq = new Sequence();
//        pos = 0;
//       
//        while(pos < len) {
//          int pm = resynth.get(pos % modulr);
//          
//          if(posM.get(pm).size() == 0) {
//            pos++;
//            continue;
//          } 
//          
//          HomoPair<Integer> p = CollectionUtils.chooseAtRandom(posM.get(pm));
//          
//          int pch = p.getFirst();
//          int dur = p.getSecond();
//          
//          newRhythm.add(pos);
//          pitchSeq.add(pch);
//          pos += dur;
//          
//        }
//        
//        String ns = "";
//        switch((Rn)comboBox.getSelectedItem()) {
//          case Hex:
//            ns = HexadecimalSentence.fromRhythm(BinaryWord.buildRhythm(len, newRhythm)).toString();
//            break;
//          case Octal:
//            ns = OctalSentence.fromRhythm(BinaryWord.buildRhythm(len, newRhythm)).toString();
//            break;
//        }
//        
//        String output = "";
//        
//        output = ns + "\n" + pitchSeq.toString();
//        
//        txtrResult.setText(output);
//       
//      }
//    });
//    
//    JLabel lblEqClases = new JLabel("EQ Classes :");
//    lblEqClases.setHorizontalAlignment(SwingConstants.RIGHT);
//    lblEqClases.setFont(new Font("Dialog", Font.PLAIN, 11));
//    
//    textEQClasses = new JTextField();
//    textEQClasses.setText("0 1 2 3 4 5 6 7");
//    textEQClasses.setColumns(10);
//    
//    textResynth = new JTextField();
//    textResynth.setText("0 1 2 3 4 5 6 7");
//    textResynth.setColumns(10);
//    
//    JLabel lblResynth = new JLabel("Reseq :");
//    lblResynth.setHorizontalAlignment(SwingConstants.RIGHT);
//    lblResynth.setFont(new Font("Dialog", Font.PLAIN, 11));
//    
//    
//    
//    
//    
//    
//    GroupLayout groupLayout = new GroupLayout(frmRexRandomizer.getContentPane());
//    groupLayout.setHorizontalGroup(
//      groupLayout.createParallelGroup(Alignment.TRAILING)
//        .addGroup(groupLayout.createSequentialGroup()
//          .addContainerGap()
//          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
//            .addComponent(lblbars, GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
//            .addComponent(lblRexRhythm, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//            .addComponent(lblEqClases, GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
//            .addComponent(lblResynth, GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
//            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE))
//          .addPreferredGap(ComponentPlacement.RELATED)
//          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
//            .addComponent(textResynth, GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
//            .addComponent(btnGenerate, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
//            .addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
//            .addComponent(textRex, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
//            .addComponent(spinner, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
//            .addComponent(textEQClasses, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE))
//          .addGap(23))
//    );
//    groupLayout.setVerticalGroup(
//      groupLayout.createParallelGroup(Alignment.LEADING)
//        .addGroup(groupLayout.createSequentialGroup()
//          .addContainerGap()
//          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
//            .addComponent(textRex, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//            .addComponent(lblRexRhythm))
//          .addPreferredGap(ComponentPlacement.RELATED)
//          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
//            .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//            .addComponent(lblbars))
//          .addPreferredGap(ComponentPlacement.RELATED)
//          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
//            .addComponent(textEQClasses, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//            .addComponent(lblEqClases, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE))
//          .addPreferredGap(ComponentPlacement.RELATED)
//          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
//            .addComponent(lblResynth, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
//            .addComponent(textResynth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//          .addGap(12)
//          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
//            .addComponent(btnGenerate)
//            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
//          .addPreferredGap(ComponentPlacement.RELATED)
//          .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
//          .addGap(10))
//    );
//    
//    
//    scrollPane.setViewportView(txtrResult);
//    frmRexRandomizer.getContentPane().setLayout(groupLayout);
//  }
//}
