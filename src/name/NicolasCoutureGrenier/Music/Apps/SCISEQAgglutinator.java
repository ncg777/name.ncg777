package name.NicolasCoutureGrenier.Music.Apps;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.TreeSet;

import javax.swing.JFrame;

import name.NicolasCoutureGrenier.Maths.DataStructures.CollectionUtils;
import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence;
import name.NicolasCoutureGrenier.Music.SequencePredicates.SeqAllRhythmsSCI;

import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SCISEQAgglutinator {

  private JFrame frmSciSeqAgglutinator;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          SCISEQAgglutinator window = new SCISEQAgglutinator();
          window.frmSciSeqAgglutinator.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
  
  private TreeSet<Sequence> s12 = new TreeSet<Sequence>();
  private TreeSet<Sequence> s16 = new TreeSet<Sequence>();
  private JSpinner spinnerN;
  private JSpinner spinnerK;
  private JTextField result;
  private void readSequences(int n) {
    if(!(n==12 || n == 16)) throw new RuntimeException("invalid param");
    if(n==12) s12 = new TreeSet<Sequence>();
    if(n==16) s16 = new TreeSet<Sequence>();
    InputStream ist = Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/necklaces-" + Integer.toString(n)+ "-5-SCI-norep.csv");
    var isr = new InputStreamReader(ist);
    BufferedReader br = new BufferedReader(isr);
    
    while(true) {
      String r = null;
      try {
        r = br.readLine();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      if(r==null || r.isEmpty()) break;
      if(n==12) s12.add(Sequence.parse(r));
      else if(n==16) s16.add(Sequence.parse(r));
    }
    
  }
  /**
   * Create the application.
   */
  public SCISEQAgglutinator() {
    initialize();
    
    
    readSequences(12);
    readSequences(16);
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmSciSeqAgglutinator = new JFrame();
    frmSciSeqAgglutinator.setTitle("SCI SEQ Agglutinator");
    frmSciSeqAgglutinator.setBounds(100, 100, 271, 150);
    frmSciSeqAgglutinator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmSciSeqAgglutinator.getContentPane().setLayout(null);
    
    spinnerN = new JSpinner();
    spinnerN.setModel(new SpinnerListModel(new String[] {"12", "16"}));
    spinnerN.setBounds(67, 9, 46, 20);
    frmSciSeqAgglutinator.getContentPane().add(spinnerN);
    
    JLabel lblNewLabel = new JLabel("n:");
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setBounds(11, 12, 46, 14);
    frmSciSeqAgglutinator.getContentPane().add(lblNewLabel);
    
    JLabel lblK = new JLabel("k:");
    lblK.setHorizontalAlignment(SwingConstants.RIGHT);
    lblK.setBounds(123, 12, 46, 14);
    frmSciSeqAgglutinator.getContentPane().add(lblK);
    
    spinnerK = new JSpinner();
    spinnerK.setModel(new SpinnerNumberModel(Integer.valueOf(2), Integer.valueOf(2), null, Integer.valueOf(1)));
    spinnerK.setBounds(179, 11, 46, 17);
    frmSciSeqAgglutinator.getContentPane().add(spinnerK);
    
    JButton btnNewButton = new JButton("Agglutinate");
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        
        var pred = new SeqAllRhythmsSCI();
        int k = (int)spinnerK.getValue();
        int n = (int)spinnerK.getValue();
        Sequence o = new Sequence();
        int i=0;
        do {
          if(i==0) {
            o = CollectionUtils.chooseAtRandom(n==12 ? s12 : s16);
            i++;
          } else {
            boolean found = false;
            while(!found) {
              var candidate = CollectionUtils.chooseAtRandom(n==12 ? s12 : s16);
              
              TreeSet<Sequence> candidates = new TreeSet<Sequence>();
              for(int j=0;j<n;j++) {candidates.add(candidate.rotate(j));}
              
              while(true) {
                var rotated_candidate = CollectionUtils.chooseAtRandom(candidates);
                var j = o.juxtapose(rotated_candidate);
                if(pred.apply(j) && !j.cyclicalForwardDifference().contains(0)) {
                  found = true;
                  o = j;
                  break;
                } else {
                  candidates.remove(rotated_candidate);
                  if(candidates.isEmpty()) break;
                }
              }
            }
            i++;
          }
          
        } while(i<k);
        result.setText(o.toString());
      }
    });
    btnNewButton.setBounds(10, 39, 215, 23);
    frmSciSeqAgglutinator.getContentPane().add(btnNewButton);
    
    result = new JTextField();
    result.setBounds(10, 67, 215, 20);
    frmSciSeqAgglutinator.getContentPane().add(result);
    result.setColumns(10);
  }
}
