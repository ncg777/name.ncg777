package name.ncg.Music.Apps;

import java.awt.EventQueue;
import java.util.Arrays;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;


import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import name.ncg.Music.Rhythm;
import name.ncg.Music.Rhythm16;
import name.ncg.Music.RhythmPredicates.Bypass;
import name.ncg.Music.RhythmPredicates.EntropicDispersion;
import name.ncg.Music.RhythmPredicates.Even;
import name.ncg.Music.RhythmPredicates.HighDispersion;
import name.ncg.Music.RhythmPredicates.LowDispersion;
import name.ncg.Music.RhythmPredicates.LowEntropy;
import name.ncg.Music.RhythmPredicates.Oddity;
import name.ncg.Music.RhythmPredicates.ShadowContourIsomorphic;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.SwingConstants;

public class R16Navigator {

  private JFrame frmRNavigator;
  private JTextField textCurrentHex;
  private JCheckBox chkSCI = new JCheckBox("Shadow Contour Isomorphic");
  private JList<String> list = new JList<String>(new DefaultListModel<String>());
  private JCheckBox chkLowDispersion = new JCheckBox("Low dispersion");
  private JCheckBox chkHighDispersion = new JCheckBox("High dispersion");
  private JCheckBox chckbxOddity = new JCheckBox("Oddity");
  private JLabel lblSize = new JLabel("123");
  private JTextField textSpec;
  private final JCheckBox chkEntropicDispersion = new JCheckBox("Entropic dispersion");
  private final JLabel lblSumOfPairwise = new JLabel("Sum of pairwise distances:");
  private final JTextField textSumPwd = new JTextField();
  private JTextField textEntropy;
  private JCheckBox chkLowEntropy = new JCheckBox("Low entropy");
  JCheckBox chckbxEven = new JCheckBox("Even");
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          R16Navigator window = new R16Navigator();
          window.frmRNavigator.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
  private void fillR16List() {
    DefaultListModel<String> model = (DefaultListModel<String>)list.getModel();
    model.clear();
    TreeSet<Rhythm16> r16s0 = Rhythm16.getRhythms16();
    TreeSet<Rhythm16> r16s = new TreeSet<Rhythm16>();
    Predicate<Rhythm> pred = new Bypass();
    
    if(chkSCI.isSelected()) {
      pred = Predicates.and(pred, new ShadowContourIsomorphic());
    }
    
    if(chckbxOddity.isSelected()) {
      pred = Predicates.and(pred, new Oddity());
    }
    
    if(chkLowDispersion.isSelected()) {
      pred = Predicates.and(pred, new LowDispersion());
    }
    
    if(chkEntropicDispersion.isSelected()) {
      pred = Predicates.and(pred, new EntropicDispersion());
    }
    
    if(chkHighDispersion.isSelected()) {
      pred = Predicates.and(pred, new HighDispersion());
    }
    
    if(chkLowEntropy.isSelected()) {
      pred = Predicates.and(pred, new LowEntropy());
    }
    
    if(chckbxEven.isSelected()) {
      pred = Predicates.and(pred, new Even());
    }
    
    for(Rhythm16 r : r16s0) {
      if(pred.apply(r)) r16s.add(r);
    }
    
    lblSize.setText("" + r16s.size());
    
    String[] r16Ids = new String[r16s.size()];
    
    int i=0;
    for(Rhythm16 r : r16s) {
      r16Ids[i++] = r.getId();
    }
    
    Arrays.sort(r16Ids);
    for(String s : r16Ids) {
      model.addElement(s);
    }
  }
  
  private void setCurrent() {
    if(list.isSelectionEmpty()) return;
    
    DefaultListModel<String> model = (DefaultListModel<String>)list.getModel();
    String current = model.get(list.getSelectedIndex());
    Rhythm16 r = Rhythm16.parseRhythm16Id(current);
    textCurrentHex.setText(r.toString());
    textSpec.setText(Rhythm.calcSpectrum(r).toString().replaceAll("[)(]", ""));
    textSumPwd.setText("" + r.getComposition().asSequence().sumOfPairwiseDistances());
    textEntropy.setText(String.format("%1.6f", r.compositionEntropy()));
  }
  /**
   * Create the application.
   */
  public R16Navigator() {
    initialize();
    fillR16List();
  }
  
  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    textSumPwd.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textSumPwd.setEditable(false);
    textSumPwd.setColumns(10);
    lblSumOfPairwise.setHorizontalAlignment(SwingConstants.RIGHT);
    lblSumOfPairwise.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    chkEntropicDispersion.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fillR16List();
      }
    });
    chkEntropicDispersion.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    frmRNavigator = new JFrame();
    frmRNavigator.setResizable(false);
    frmRNavigator.setTitle("R16 Navigator");
    frmRNavigator.setBounds(100, 100, 435, 673);
    frmRNavigator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    chkSCI.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    chkSCI.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fillR16List();
      }
    });
    
    JScrollPane scrollPane = new JScrollPane();
    
    JLabel lblNewLabel = new JLabel("Hex");
    lblNewLabel.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    
    textCurrentHex = new JTextField();
    textCurrentHex.setEditable(false);
    textCurrentHex.setColumns(10);
    
    
    chckbxOddity.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fillR16List();
      }
    });
    chckbxOddity.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    
    JLabel lblSizelbl = new JLabel("Size");
    lblSizelbl.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    
    
    lblSize.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    
    JLabel lblSpectrum = new JLabel("Spectrum");
    lblSpectrum.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    
    textSpec = new JTextField();
    textSpec.setEditable(false);
    textSpec.setColumns(10);
    chkHighDispersion.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fillR16List();
      }
    });
    
    
    chkHighDispersion.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    
    
    chkLowDispersion.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fillR16List();
      }
    });
    chkLowDispersion.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    
    JLabel lblEntropy = new JLabel("Entropy:");
    lblEntropy.setHorizontalAlignment(SwingConstants.RIGHT);
    lblEntropy.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    
    textEntropy = new JTextField();
    textEntropy.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textEntropy.setEditable(false);
    textEntropy.setColumns(10);
    
    
    chkLowEntropy.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fillR16List();
      }
    });
    chkLowEntropy.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    chckbxEven.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fillR16List();
      }
    });
    
    
    chckbxEven.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    GroupLayout groupLayout = new GroupLayout(frmRNavigator.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
            .addComponent(chckbxEven, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 243, GroupLayout.PREFERRED_SIZE)
            .addComponent(chkLowEntropy, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 243, GroupLayout.PREFERRED_SIZE)
            .addComponent(chkHighDispersion, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 191, GroupLayout.PREFERRED_SIZE)
            .addComponent(chkEntropicDispersion, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 191, GroupLayout.PREFERRED_SIZE)
            .addComponent(chkLowDispersion, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 191, GroupLayout.PREFERRED_SIZE)
            .addComponent(chckbxOddity, Alignment.LEADING)
            .addComponent(chkSCI, Alignment.LEADING)
            .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(lblSizelbl, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
                .addComponent(lblSize, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE))
              .addGap(18)
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(lblNewLabel)
                .addComponent(textCurrentHex, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE))
              .addPreferredGap(ComponentPlacement.RELATED)
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                  .addComponent(lblSpectrum, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
                  .addGap(34))
                .addComponent(textSpec, GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)))
            .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
              .addComponent(lblSumOfPairwise, GroupLayout.PREFERRED_SIZE, 211, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(textSumPwd, GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE))
            .addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
              .addComponent(lblEntropy, GroupLayout.PREFERRED_SIZE, 211, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(textEntropy, GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)))
          .addContainerGap())
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addComponent(chkSCI)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(chckbxOddity, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addComponent(chkLowDispersion, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addComponent(chkEntropicDispersion, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addComponent(chkHighDispersion, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addComponent(chkLowEntropy, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addComponent(chckbxEven, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 372, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblSizelbl)
            .addComponent(lblNewLabel)
            .addComponent(lblSpectrum))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblSize)
            .addComponent(textCurrentHex, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(textSpec, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblSumOfPairwise)
            .addComponent(textSumPwd, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblEntropy)
            .addComponent(textEntropy, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))
          .addContainerGap())
    );
    list.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        setCurrent();
      }
    });
    
    
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    scrollPane.setViewportView(list);
    frmRNavigator.getContentPane().setLayout(groupLayout);
  }
}
