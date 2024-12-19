package name.ncg777.maths.phrases.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import name.ncg777.maths.phrases.QuartalWordsPhrase;
import name.ncg777.maths.sequences.Sequence;
import name.ncg777.maths.words.Alphabet;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JScrollPane;

public class Contours {

  private JFrame frmRhythmContours;
  private JTextField textField;
  private JTextField txtContour;
  private JTextField txtShadowContour;
  private JTextField txtComposition;
  private JComboBox<Alphabet.Name> comboBox = new JComboBox<Alphabet.Name>(new DefaultComboBoxModel<Alphabet.Name>(Alphabet.Name.values()));
  private JTextField textFieldBinary;
  private JTextField textCompositionPartition;;
  private JTextArea textAreaPartitions = new JTextArea();
  private JLabel lblNewLabel_1;
  private JTextField textContourSeq;
  private JLabel lblContourSeqMax;

  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Contours window = new Contours();
          window.frmRhythmContours.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public Contours() {
    initialize();
  }

  private void initialize() {
    frmRhythmContours = new JFrame();
    frmRhythmContours.setResizable(false);
    frmRhythmContours.setTitle("Contours");
    frmRhythmContours.setBounds(100, 100, 480, 444);
    frmRhythmContours.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblRhythm = new JLabel("Rhythm :");
    lblRhythm.setHorizontalAlignment(SwingConstants.RIGHT);
    
    textField = new JTextField();
    textField.setColumns(10);
    
    JButton btnCalccontours = new JButton("Calculate Contours");
    btnCalccontours.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        var abc = (Alphabet.Name)comboBox.getSelectedItem();
        QuartalWordsPhrase r4 = new QuartalWordsPhrase(abc, textField.getText());
        
        txtContour.setText(r4.toWord().getContour().toString());
        txtShadowContour.setText(r4.toWord().getShadowContour().toString());
        txtComposition.setText(r4.toBinaryWord().reverse().getComposition().asSequence().toString());
        textFieldBinary.setText(r4.toBinaryWord().toString());
        textCompositionPartition.setText(r4.clusterPartition(abc).toString());
        String o = "";
        
        var clusters = QuartalWordsPhrase.clusterRhythmPartition(abc, r4.toBinaryWord().decomposeIntoHomogeneousRegions());
        for(QuartalWordsPhrase r : clusters) {
          o += r.toString() + "\n";
        }
        textAreaPartitions.setText(o);
        
        Sequence contourseq = r4.toWord().getContour().circularHoldNonZero().cyclicalAntidifference(0).asOrdinalsUnipolar().addToEach(-1);

        textContourSeq.setText(contourseq.toString());
        lblContourSeqMax.setText(Integer.valueOf(contourseq.getMax()).toString());
        
      }
    });
    
    JLabel lblContour = new JLabel("Contour :");
    lblContour.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JLabel lblShadowContour = new JLabel("Shadow Contour :");
    lblShadowContour.setHorizontalAlignment(SwingConstants.RIGHT);
    
    txtContour = new JTextField();
    txtContour.setColumns(10);
    
    txtShadowContour = new JTextField();
    txtShadowContour.setColumns(10);
    
    JLabel lblComposition = new JLabel("Composition :");
    
    txtComposition = new JTextField();
    txtComposition.setColumns(10);
    
    textFieldBinary = new JTextField();
    textFieldBinary.setColumns(10);
    
    JLabel lblBinary = new JLabel("Binary :");
    lblBinary.setHorizontalAlignment(SwingConstants.RIGHT);
    
    textCompositionPartition = new JTextField();
    textCompositionPartition.setColumns(10);
    
    JLabel lblNewLabel = new JLabel("Compo. partition:");
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    
    lblNewLabel_1 = new JLabel("Partition:");
    lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
    
    JScrollPane scrollPane = new JScrollPane();
    
    JLabel lblContourSeq = new JLabel("Contour seq :");
    lblContourSeq.setHorizontalAlignment(SwingConstants.RIGHT);
    
    textContourSeq = new JTextField();
    textContourSeq.setColumns(10);
    
    JLabel lblNewLabel_2 = new JLabel("Contour seq max:");
    
    lblContourSeqMax = new JLabel("");
    
    GroupLayout groupLayout = new GroupLayout(frmRhythmContours.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.TRAILING)
        .addGroup(groupLayout.createSequentialGroup()
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addGroup(groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                  .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblBinary, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE))
                  .addComponent(lblComposition)
                  .addComponent(lblRhythm, GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                  .addComponent(lblContour, GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                  .addComponent(lblShadowContour, GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                  .addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))
                .addGroup(groupLayout.createSequentialGroup()
                  .addContainerGap()
                  .addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)))
              .addPreferredGap(ComponentPlacement.RELATED))
            .addGroup(groupLayout.createSequentialGroup()
              .addContainerGap()
              .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                .addComponent(lblNewLabel_2)
                .addComponent(lblContourSeq, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE))
              .addGap(4)))
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
              .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 132, GroupLayout.PREFERRED_SIZE)
              .addContainerGap())
            .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(textField, GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addComponent(btnCalccontours, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addComponent(txtContour, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addComponent(txtShadowContour, GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addComponent(txtComposition, GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE))
              .addGap(20))
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(lblContourSeqMax)
              .addContainerGap())
            .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                .addComponent(textFieldBinary, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addComponent(textContourSeq, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addComponent(textCompositionPartition, GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE))
              .addGap(20))))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblRhythm)
            .addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(btnCalccontours)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblContour)
            .addComponent(txtContour, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addGap(8)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblShadowContour)
            .addComponent(txtShadowContour, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblComposition)
            .addComponent(txtComposition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblBinary)
            .addComponent(textFieldBinary, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(textCompositionPartition, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
            .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblContourSeq)
            .addComponent(textContourSeq, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblNewLabel_2)
            .addComponent(lblContourSeqMax))
          .addContainerGap(23, Short.MAX_VALUE))
    );
    scrollPane.setViewportView(textAreaPartitions);
    textAreaPartitions.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 12));
    textAreaPartitions.setBorder(new LineBorder(new Color(0, 0, 0)));
    frmRhythmContours.getContentPane().setLayout(groupLayout);
  }
}