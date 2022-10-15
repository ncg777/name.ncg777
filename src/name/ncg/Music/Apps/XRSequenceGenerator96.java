package name.ncg.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;

import name.ncg.Maths.Numbers;
import name.ncg.Maths.DataStructures.CollectionUtils;
import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Music.R12List;
import name.ncg.Music.R16List;

import javax.swing.SpringLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import name.ncg.Music.Rn;

public class XRSequenceGenerator96 {

  private JFrame frmXrSequenceGenerator;
  private JTextField textRhythm;
  private JTextField textOutput;
  private JComboBox<Rn> comboBox = new JComboBox<Rn>(new DefaultComboBoxModel<Rn>(Rn.values()));
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          XRSequenceGenerator96 window = new XRSequenceGenerator96();
          window.frmXrSequenceGenerator.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public XRSequenceGenerator96() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmXrSequenceGenerator = new JFrame();
    frmXrSequenceGenerator.setTitle("XR Sequence Generator 96");
    frmXrSequenceGenerator.setBounds(100, 100, 450, 156);
    frmXrSequenceGenerator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    SpringLayout springLayout = new SpringLayout();
    frmXrSequenceGenerator.getContentPane().setLayout(springLayout);
    
    JLabel lblNewLabel = new JLabel("Rhythm:");
    springLayout.putConstraint(SpringLayout.WEST, lblNewLabel, 10, SpringLayout.WEST, frmXrSequenceGenerator.getContentPane());
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    frmXrSequenceGenerator.getContentPane().add(lblNewLabel);
    
    textRhythm = new JTextField();
    springLayout.putConstraint(SpringLayout.NORTH, textRhythm, 10, SpringLayout.NORTH, frmXrSequenceGenerator.getContentPane());
    springLayout.putConstraint(SpringLayout.WEST, textRhythm, 96, SpringLayout.WEST, frmXrSequenceGenerator.getContentPane());
    springLayout.putConstraint(SpringLayout.EAST, textRhythm, -10, SpringLayout.EAST, frmXrSequenceGenerator.getContentPane());
    springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel, 3, SpringLayout.NORTH, textRhythm);
    springLayout.putConstraint(SpringLayout.EAST, lblNewLabel, -6, SpringLayout.WEST, textRhythm);
    textRhythm.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    frmXrSequenceGenerator.getContentPane().add(textRhythm);
    textRhythm.setColumns(10);
    
    JButton btnGenerate = new JButton("Generate");
    btnGenerate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Sequence comp = null;
        if(comboBox.getSelectedItem() == Rn.Hex) {
          comp = R16List.parseR16Seq(textRhythm.getText()).asRhythm().getComposition().asSequence();
        }
        if(comboBox.getSelectedItem() == Rn.Octal) {
          comp = R12List.parseR12Seq(textRhythm.getText()).asRhythm().getComposition().asSequence();
        }
        
        ArrayList<Double> output = new ArrayList<Double>();
        String o = "";
        for(int i=0;i<comp.size();i++) {
          int n = comp.get(i);
          
          TreeSet<Double> possibles = new TreeSet<Double>();
          
          possibles.add(0.0);
          
          for(int j=8;j<=n;j++) {
            if(Numbers.lcm(n, j) == n && j%8 == 0) possibles.add(Integer.valueOf(j).doubleValue());
          }
          
          double[] weights = null;
          if(i > 0) {
            possibles.remove(output.get(i-1));
            weights = new double[possibles.size()];
            TreeSet<Double> dists = new TreeSet<Double>();
            
            for(var p : possibles){
              dists.add(Math.abs(p - output.get(i-1)));
            }
            double mind = dists.first();
            double maxd = dists.last();
            
            Iterator<Double> it = possibles.iterator();
            
            for(int j=0; j<possibles.size();j++){
              double p = it.next();
              double d = Math.abs(p - output.get(i-1));
              weights[j] = 1.0-((d-mind)/(maxd-mind));
            }
          }
          
          Double next = null;
          
          if(i==0) next = CollectionUtils.chooseAtRandom(possibles);
          else next = CollectionUtils.chooseAtRandomWithWeights(possibles, weights);
          output.add(next);          
          o += Long.valueOf(Math.round(next)).toString();
          
          if(i != comp.size()-1) o += " ";
        }
        
        textOutput.setText(o);
      }
    });
    springLayout.putConstraint(SpringLayout.NORTH, btnGenerate, 6, SpringLayout.SOUTH, textRhythm);
    springLayout.putConstraint(SpringLayout.WEST, btnGenerate, 0, SpringLayout.WEST, textRhythm);
    springLayout.putConstraint(SpringLayout.SOUTH, btnGenerate, 37, SpringLayout.SOUTH, textRhythm);
    springLayout.putConstraint(SpringLayout.EAST, btnGenerate, 0, SpringLayout.EAST, textRhythm);
    frmXrSequenceGenerator.getContentPane().add(btnGenerate);
    
    JLabel lblNewLabel_1 = new JLabel("Output:");
    springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel_1, 49, SpringLayout.SOUTH, lblNewLabel);
    springLayout.putConstraint(SpringLayout.WEST, lblNewLabel_1, 0, SpringLayout.WEST, lblNewLabel);
    springLayout.putConstraint(SpringLayout.EAST, lblNewLabel_1, 0, SpringLayout.EAST, lblNewLabel);
    lblNewLabel_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
    frmXrSequenceGenerator.getContentPane().add(lblNewLabel_1);
    
    textOutput = new JTextField();
    springLayout.putConstraint(SpringLayout.NORTH, textOutput, -3, SpringLayout.NORTH, lblNewLabel_1);
    springLayout.putConstraint(SpringLayout.WEST, textOutput, 0, SpringLayout.WEST, textRhythm);
    springLayout.putConstraint(SpringLayout.SOUTH, textOutput, 17, SpringLayout.NORTH, lblNewLabel_1);
    springLayout.putConstraint(SpringLayout.EAST, textOutput, 0, SpringLayout.EAST, textRhythm);
    textOutput.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    frmXrSequenceGenerator.getContentPane().add(textOutput);
    textOutput.setColumns(10);
    
    
    
    springLayout.putConstraint(SpringLayout.NORTH, comboBox, 14, SpringLayout.SOUTH, lblNewLabel);
    springLayout.putConstraint(SpringLayout.WEST, comboBox, 0, SpringLayout.WEST, lblNewLabel);
    springLayout.putConstraint(SpringLayout.EAST, comboBox, 0, SpringLayout.EAST, lblNewLabel);
    frmXrSequenceGenerator.getContentPane().add(comboBox);
  }
}
