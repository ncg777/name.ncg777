package name.ncg.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JComboBox;

import javax.swing.DefaultComboBoxModel;

import javax.swing.JScrollPane;

import javax.swing.SwingConstants;

import name.ncg.Maths.DataStructures.CollectionUtils;
import name.ncg.Music.PCS12;

import name.ncg.Music.PCS12Predicates.SubsetOf;

import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.TreeSet;

import com.google.common.base.Joiner;

import javax.swing.JTextArea;

public class PCS12IntersectionAndUnion {

  private JFrame frmPCS12IntersectionAndUnion;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          PCS12IntersectionAndUnion window = new PCS12IntersectionAndUnion();
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
  public PCS12IntersectionAndUnion() {
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
    PCS12 s1 = PCS12.parse(cboScale.getSelectedItem().toString());
    PCS12 s2 = PCS12.parse(cboScale_1.getSelectedItem().toString());
    
    PCS12 inter = s1.intersect(s2);
    textInter.setText(inter.toString());
    textIntersectionPitches.setText(inter.asSequence().toString().replaceAll("[)(]", ""));
    
    PCS12 union = s1.combineWith(s2);
    textUnion.setText(union.toString());
    textUnionPitches.setText(union.asSequence().toString().replaceAll("[)(]", ""));
    
  }
  
  private void initialize() {
    frmPCS12IntersectionAndUnion = new JFrame();
    frmPCS12IntersectionAndUnion.setResizable(false);
    frmPCS12IntersectionAndUnion.setTitle("PCS12 Intersection and Union");
    frmPCS12IntersectionAndUnion.setBounds(100, 100, 373, 197);
    frmPCS12IntersectionAndUnion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmPCS12IntersectionAndUnion.getContentPane().setLayout(null);
    
    JLabel lblNewLabel = new JLabel("Scale 1:");
    lblNewLabel.setToolTipText("<html>\r\n07-26.04 Major Locrian<br/>\r\n07-28.11 Persian<br/>\r\n07-29.06 Hungarian<br/>\r\n07-38.11 Harmonic minor<br/>\r\n07-39.11 Melodic minor<br/>\r\n07-42.11 Harmonic major<br/>\r\n07-43.11 Major<br/>\r\n08-35.00 Octatonic<br/>\r\n</html>");
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
    
    String[] cs = PCS12.getChordDict().keySet().toArray(new String[0]);
    Arrays.sort(cs);
    cboScale.setModel(new DefaultComboBoxModel<String>(cs));
    cboScale_1.setModel(new DefaultComboBoxModel<String>(cs));
    cboScale.setSelectedIndex(Arrays.asList(cs).indexOf("07-43.11"));
    cboScale_1.setSelectedIndex(Arrays.asList(cs).indexOf("07-43.04"));
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
    lblScale.setToolTipText("<html>\r\n07-26.04 Major Locrian<br/>\r\n07-28.11 Persian<br/>\r\n07-29.06 Hungarian<br/>\r\n07-38.11 Harmonic minor<br/>\r\n07-39.11 Melodic minor<br/>\r\n07-42.11 Harmonic major<br/>\r\n07-43.11 Major<br/>\r\n08-35.00 Octatonic<br/>\r\n</html>");
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
    textUnion.setText("06-69.00");
    textUnion.setHorizontalAlignment(SwingConstants.CENTER);
    textUnion.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textUnion.setEditable(false);
    textUnion.setColumns(10);
    textUnion.setBounds(10, 111, 153, 23);
    frmPCS12IntersectionAndUnion.getContentPane().add(textUnion);
    
    textUnionPitches = new JTextField();
    textUnionPitches.setText("0 2 4 5 7 9");
    textUnionPitches.setHorizontalAlignment(SwingConstants.CENTER);
    textUnionPitches.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textUnionPitches.setEditable(false);
    textUnionPitches.setColumns(10);
    textUnionPitches.setBounds(193, 110, 153, 23);
    frmPCS12IntersectionAndUnion.getContentPane().add(textUnionPitches);
  }
}
