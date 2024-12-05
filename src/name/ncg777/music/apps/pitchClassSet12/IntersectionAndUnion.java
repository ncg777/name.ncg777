package name.ncg777.music.apps.pitchClassSet12;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JComboBox;

import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;

import name.ncg777.music.pitchClassSet12.PitchClassSet12;

import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class IntersectionAndUnion {

  private JFrame frmPCS12IntersectionAndUnion;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          IntersectionAndUnion window = new IntersectionAndUnion();
          window.frmPCS12IntersectionAndUnion.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public IntersectionAndUnion() {
    initialize();
    refresh();
  }
  JComboBox<String> cboScale = new JComboBox<String>();
  JComboBox<String> cboScale_1 = new JComboBox<String>();
  private JTextField textInter = new JTextField();
  private JTextField textIntersectionPitches = new JTextField();;
  private JTextField textUnion = new JTextField();
  private JTextField textUnionPitches = new JTextField();
  
  private void refresh() {
    PitchClassSet12 s1 = PitchClassSet12.parseForte(cboScale.getSelectedItem().toString());
    PitchClassSet12 s2 = PitchClassSet12.parseForte(cboScale_1.getSelectedItem().toString());
    
    PitchClassSet12 inter = s1.intersect(s2);
    textInter.setText(inter.toForteNumberString());
    textIntersectionPitches.setText(inter.asSequence().toString());
    
    PitchClassSet12 union = s1.combineWith(s2);
    textUnion.setText(union.toForteNumberString());
    textUnionPitches.setText(union.asSequence().toString());
    
  }
  private Comparator<String> comparator = PitchClassSet12.ForteStringComparator.reversed();
  private void initialize() {
    frmPCS12IntersectionAndUnion = new JFrame();
    frmPCS12IntersectionAndUnion.setResizable(false);
    frmPCS12IntersectionAndUnion.setTitle("PitchClassSet12 Pair Intersection and Union");
    frmPCS12IntersectionAndUnion.setBounds(100, 100, 373, 197);
    frmPCS12IntersectionAndUnion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmPCS12IntersectionAndUnion.getContentPane().setLayout(null);
    
    JLabel lblNewLabel = new JLabel("Scale 1:");
    lblNewLabel.setBounds(10, 15, 64, 14);
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    frmPCS12IntersectionAndUnion.getContentPane().add(lblNewLabel);
    cboScale.setBounds(73, 12, 100, 20);
    
   
    cboScale.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        refresh();
      }
    });
    
    cboScale_1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        refresh();
      }
    });
    
    String[] cs = PitchClassSet12.getForteChordDict().keySet().toArray(new String[0]);
    List<String> cs0 = new ArrayList<String>();
    for(var x : cs) cs0.add(x);
    cs0.sort(comparator);
    cs = cs0.toArray(new String[0]);
    cboScale.setModel(new DefaultComboBoxModel<String>(cs));
    cboScale_1.setModel(new DefaultComboBoxModel<String>(cs));
    cboScale.setSelectedIndex(Arrays.asList(cs).indexOf("5-20B.01"));
    cboScale_1.setSelectedIndex(Arrays.asList(cs).indexOf("6-z26.08"));
    frmPCS12IntersectionAndUnion.getContentPane().add(cboScale);
    
    JLabel lblNewLabel_5 = new JLabel("Intersection");
    lblNewLabel_5.setBounds(20, 34, 132, 23);
    lblNewLabel_5.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_5.setHorizontalAlignment(SwingConstants.CENTER);
    frmPCS12IntersectionAndUnion.getContentPane().add(lblNewLabel_5);
    
    
    JLabel lblNewLabel_6 = new JLabel("Intersection Pitches");
    lblNewLabel_6.setBounds(193, 34, 153, 23);
    lblNewLabel_6.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_6.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    frmPCS12IntersectionAndUnion.getContentPane().add(lblNewLabel_6);
    
    textIntersectionPitches.setBounds(193, 55, 153, 23);
    textIntersectionPitches.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textIntersectionPitches.setEditable(false);
    textIntersectionPitches.setHorizontalAlignment(SwingConstants.CENTER);
    frmPCS12IntersectionAndUnion.getContentPane().add(textIntersectionPitches);
    textIntersectionPitches.setColumns(10);
    
    JLabel lblScale = new JLabel("Scale 2:");
    lblScale.setHorizontalAlignment(SwingConstants.RIGHT);
    lblScale.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblScale.setBounds(183, 15, 64, 14);
    frmPCS12IntersectionAndUnion.getContentPane().add(lblScale);
    
    cboScale_1.setBounds(246, 12, 100, 20);
    frmPCS12IntersectionAndUnion.getContentPane().add(cboScale_1);
    
    textInter.setHorizontalAlignment(SwingConstants.CENTER);
    textInter.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textInter.setEditable(false);
    textInter.setColumns(10);
    textInter.setBounds(10, 56, 153, 23);
    frmPCS12IntersectionAndUnion.getContentPane().add(textInter);
    
    JLabel lblNewLabel_5_1 = new JLabel("Union");
    lblNewLabel_5_1.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_5_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_5_1.setBounds(20, 89, 132, 23);
    frmPCS12IntersectionAndUnion.getContentPane().add(lblNewLabel_5_1);
    
    JLabel lblNewLabel_6_1 = new JLabel("Union Pitches");
    lblNewLabel_6_1.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_6_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_6_1.setBounds(193, 89, 153, 23);
    frmPCS12IntersectionAndUnion.getContentPane().add(lblNewLabel_6_1);
    
    textUnion = new JTextField();
    //textUnion.setText("8-23.11");
    textUnion.setHorizontalAlignment(SwingConstants.CENTER);
    textUnion.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textUnion.setEditable(false);
    textUnion.setColumns(10);
    textUnion.setBounds(10, 111, 153, 23);
    frmPCS12IntersectionAndUnion.getContentPane().add(textUnion);
    
    textUnionPitches = new JTextField();
    //textUnionPitches.setText("0 2 4 5 7 9");
    textUnionPitches.setHorizontalAlignment(SwingConstants.CENTER);
    textUnionPitches.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textUnionPitches.setEditable(false);
    textUnionPitches.setColumns(10);
    textUnionPitches.setBounds(193, 110, 153, 23);
    frmPCS12IntersectionAndUnion.getContentPane().add(textUnionPitches);
  }
}
