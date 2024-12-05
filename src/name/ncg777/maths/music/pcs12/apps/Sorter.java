package name.ncg777.maths.music.pcs12.apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.google.common.base.Joiner;

import name.ncg777.maths.music.pcs12.Pcs12;
import name.ncg777.maths.sequences.Sequence;

import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.SpinnerNumberModel;

public class Sorter {

  private JFrame frmChordSorter;
  private JTextField textChords;
  private JTextField textResult;
  private JSpinner spinnerRotation;
  private JTextField textPermutation;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Sorter window = new Sorter();
          window.frmChordSorter.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public Sorter() {
    initialize();
  }
  //private Comparator<String> comparator = Pcs12.ForteStringComparator.reversed();
  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmChordSorter = new JFrame();
    frmChordSorter.setTitle("Chord sorter");
    frmChordSorter.setBounds(100, 100, 450, 204);
    frmChordSorter.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmChordSorter.getContentPane().setLayout(null);
    
    JLabel lblNewLabel = new JLabel("Chords:");
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setBounds(10, 11, 46, 14);
    frmChordSorter.getContentPane().add(lblNewLabel);
    
    textChords = new JTextField();
    textChords.setBounds(66, 8, 358, 20);
    frmChordSorter.getContentPane().add(textChords);
    textChords.setColumns(10);
    
    spinnerRotation = new JSpinner();
    spinnerRotation.setModel(new SpinnerNumberModel(0, 0, 11, 1));
    spinnerRotation.setBounds(352, 36, 72, 20);
    frmChordSorter.getContentPane().add(spinnerRotation);
    
    JLabel lblNewLabel_1 = new JLabel("Rotation:");
    lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel_1.setBounds(262, 39, 87, 14);
    frmChordSorter.getContentPane().add(lblNewLabel_1);
    
    JLabel lblNewLabel_2 = new JLabel("Result:");
    lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel_2.setBounds(10, 98, 56, 14);
    frmChordSorter.getContentPane().add(lblNewLabel_2);
    
    JButton btnSort = new JButton("Sort");
    btnSort.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        var chstr = textChords.getText().trim().split("\\s+");
        var chlist = new ArrayList<Pcs12>();
        for(int i = 0;i<chstr.length;i++) {chlist.add(Pcs12.parseForte(chstr[i]));}
        
        ArrayList<Pcs12> orig = new ArrayList<Pcs12>();
        for(var c : chlist) orig.add(c);
        int r = (Integer)spinnerRotation.getValue();
        chlist.sort((a,b) -> a.rotatedCompareTo(b,r));
        
        Sequence permu = new Sequence();
        for(int i=0;i<chlist.size();i++) {
          int x = 0;
          while(!orig.get(x).equals(chlist.get(i))) x++;
          permu.add(x);
        }
        
        textResult.setText(Joiner.on(" ").join(chlist.stream().map(c -> c.toForteNumberString()).toList()));
        textPermutation.setText(permu.toString());
      }
    });
    btnSort.setBounds(10, 66, 414, 23);
    frmChordSorter.getContentPane().add(btnSort);
    
    textResult = new JTextField();
    textResult.setBounds(76, 97, 348, 17);
    frmChordSorter.getContentPane().add(textResult);
    textResult.setColumns(10);
    
    JLabel lblNewLabel_2_1 = new JLabel("Permutation:");
    lblNewLabel_2_1.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel_2_1.setBounds(10, 123, 62, 14);
    frmChordSorter.getContentPane().add(lblNewLabel_2_1);
    
    textPermutation = new JTextField();
    textPermutation.setColumns(10);
    textPermutation.setBounds(76, 120, 348, 17);
    frmChordSorter.getContentPane().add(textPermutation);
  }
}
