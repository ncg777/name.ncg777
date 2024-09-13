package name.NicolasCoutureGrenier.Music.Apps;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.annotation.Nonnull;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.NicolasCoutureGrenier.CS.DataStructures.CollectionUtils;
import name.NicolasCoutureGrenier.Maths.Graphs.DiGraph;
import name.NicolasCoutureGrenier.Maths.Relations.Relation;
import name.NicolasCoutureGrenier.Music.PCS12;
import name.NicolasCoutureGrenier.Music.PCS12Predicates.SizeIs;
import name.NicolasCoutureGrenier.Music.PCS12Predicates.SubsetOf;
import name.NicolasCoutureGrenier.Music.PCS12Relations.CloseIVs;
import name.NicolasCoutureGrenier.Music.PCS12Relations.CommonNotesAtLeast;
import name.NicolasCoutureGrenier.Music.PCS12Relations.Different;
import name.NicolasCoutureGrenier.Music.PCS12Relations.IVEQRotOrRev;

import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JCheckBox;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;


public class ChordWalker {

  private JFrame frmChordPleasure;
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          ChordWalker window = new ChordWalker();
          window.frmChordPleasure.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
  
  JTextArea txtrResult = new JTextArea();
  DiGraph<PCS12> d;
  /**
   * Create the application.
   */
  public ChordWalker() {
    initialize();
  }
  private Comparator<String> comparator = PCS12.ForteStringComparator.reversed();
  JComboBox<String> cbxStart = new JComboBox<>(); 
  JSpinner spinner = new JSpinner();
  JCheckBox chckbxReverse = new JCheckBox("reverse");
  
  JComboBox<String> cbxScale;
  JSpinner spinner_1 = new JSpinner(new SpinnerNumberModel(4, 1, 12, 1));
  private void fillChords() {
    TreeSet<PCS12> t = new TreeSet<PCS12>(); t.addAll(PCS12.getChords());
    CollectionUtils.filter(t, new SizeIs((int)spinner_1.getValue()));
    CollectionUtils.filter(t,new SubsetOf(PCS12.parse(cbxScale.getSelectedItem().toString())));
    d = new DiGraph<PCS12>(t, Relation.and(new Different(), Relation.and(Relation.or(new CloseIVs(), new IVEQRotOrRev()), new CommonNotesAtLeast(1))));
    CollectionUtils.filter(t, new Predicate<PCS12> () {

      @Override
      public boolean apply(@Nonnull PCS12 input) {
        return d.getSuccessorCount(input) > 0;
      }
      
    });
    List<String> s0 = new ArrayList<String>();
    
    for(PCS12 x : t)s0.add(x.toForteNumberString());
    s0.sort(comparator);
    String[] s = s0.toArray(new String[0]);
    ((DefaultComboBoxModel<String>)cbxStart.getModel()).removeAllElements();
    cbxStart.setModel(new DefaultComboBoxModel<String>(s));
  }
  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    String[] cs = PCS12.getForteChordDict().keySet().toArray(new String[0]);
    Arrays.sort(cs);
    
    cbxScale = new JComboBox<String>(new DefaultComboBoxModel<String>(cs));
    frmChordPleasure = new JFrame();
    frmChordPleasure.setTitle("PCS12 Walker");
    frmChordPleasure.getContentPane().setBackground(Color.DARK_GRAY);
    frmChordPleasure.setBounds(100, 100, 639, 311);
    frmChordPleasure.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    cbxScale.setSelectedIndex(Arrays.asList(cs).indexOf("8-23.11"));
    fillChords();
    JButton btnPouf = new JButton("Walk");
    btnPouf.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String s = (String) cbxStart.getSelectedItem();
        
        int n = (int) spinner.getValue();
        ArrayList<String> o = new ArrayList<String>(); o.add(s);

        while(n>1){
            PCS12 t = CollectionUtils.chooseAtRandom(d.getNeighbors(PCS12.parseForte(o.get(o.size()-1))));
            o.add(t.toString());
            n--;
        }
        if(chckbxReverse.isSelected()){Collections.reverse(o);}
        
        txtrResult.setText(Joiner.on(" ").join(o));
        
      }
    });
    
   
    chckbxReverse.setBackground(Color.BLACK);
    chckbxReverse.setForeground(Color.WHITE);
    
    JLabel lblNewLabel = new JLabel("Scale:");
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setForeground(Color.WHITE);
    
    JLabel lblStart = new JLabel("Start:");
    lblStart.setHorizontalAlignment(SwingConstants.RIGHT);
    lblStart.setForeground(Color.WHITE);
    
    JLabel lblLen = new JLabel("Len:");
    lblLen.setHorizontalAlignment(SwingConstants.RIGHT);
    lblLen.setForeground(Color.WHITE);
    
    JLabel lblK = new JLabel("k:");
    lblK.setHorizontalAlignment(SwingConstants.RIGHT);
    lblK.setForeground(Color.WHITE);
    spinner_1.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        fillChords();
      }
    });
        
    GroupLayout groupLayout = new GroupLayout(frmChordPleasure.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.TRAILING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(chckbxReverse)
              .addPreferredGap(ComponentPlacement.UNRELATED)
              .addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(cbxScale, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(lblStart, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(cbxStart, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(lblLen, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(lblK, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
            .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                .addComponent(txtrResult, GroupLayout.PREFERRED_SIZE, 593, Short.MAX_VALUE)
                .addComponent(btnPouf, GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE))
              .addGap(10)))
          .addGap(0))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(chckbxReverse)
            .addComponent(lblNewLabel)
            .addComponent(cbxScale, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblStart)
            .addComponent(cbxStart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblLen)
            .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblK)
            .addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnPouf)
          .addGap(9)
          .addComponent(txtrResult, GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
          .addGap(20))
    );
    cbxScale.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fillChords();
      }
    });
    txtrResult.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 13));
    txtrResult.setLineWrap(true);
    txtrResult.setEditable(false);
    txtrResult.setBackground(Color.BLACK);
    txtrResult.setForeground(Color.WHITE);
    spinner.setBackground(Color.BLACK);
    spinner.setForeground(Color.WHITE);
    spinner.setModel(new SpinnerNumberModel(2, 2, null, 1));
    frmChordPleasure.getContentPane().setLayout(groupLayout);
  }
}
