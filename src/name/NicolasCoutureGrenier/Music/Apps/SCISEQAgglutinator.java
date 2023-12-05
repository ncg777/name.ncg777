package name.NicolasCoutureGrenier.Music.Apps;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.function.Predicate;

import javax.swing.JFrame;

import name.NicolasCoutureGrenier.Maths.DataStructures.CollectionUtils;
import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence;
import name.NicolasCoutureGrenier.Maths.Enumerations.PermutationEnumeration;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.ShadowContourIsomorphic;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.SpectrumRising;
import name.NicolasCoutureGrenier.Music.SequencePredicates.PredicatedSeqRhythms;
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
  private TreeSet<Sequence> s8 = new TreeSet<Sequence>();
  private TreeSet<Sequence> s12 = new TreeSet<Sequence>();
  private TreeSet<Sequence> s16 = new TreeSet<Sequence>();
  private JSpinner spinnerN;
  private JSpinner spinnerK;
  private JTextField result;
  private boolean isComputing = false;
  
  private void readSequences(int n) {
    if(!(n==8 || n==12 || n == 16)) throw new RuntimeException("invalid param");
    if(n==8) s8 = new TreeSet<Sequence>();
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
      var theset = n==8?s8:n==12?s12:s16;
      
      var s0 = Sequence.parse(r);
      for(int i=0;i<s0.size();i++) {
        var sr = s0.rotate(i);
        var pe = new PermutationEnumeration(5);
        while(pe.hasMoreElements()) {
          theset.add(sr.map(new Sequence(pe.nextElement())));
        }  
      }
    }
  }
  /**
   * Create the application.
   */
  public SCISEQAgglutinator() {
    initialize();
    
    readSequences(8);
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
    spinnerN.setModel(new SpinnerListModel(new String[] {"8", "12", "16"}));
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
        if(isComputing) {
          btnNewButton.setText("Agglutinate");
          isComputing = false;
          result.setText("");
          return;
        } else {
          btnNewButton.setText("Cancel");
          isComputing = true;
          result.setText("searching...");
          int k = ((int)spinnerK.getValue());
          int n = Integer.parseInt(spinnerN.getValue().toString());
          
          var th = new Thread(new Runnable() {
            
            @Override
            public void run() {
              var pred = new Predicate<Sequence>() {
                private Predicate<Sequence> innerPr = new PredicatedSeqRhythms(new ShadowContourIsomorphic());
                @Override
                public boolean test(Sequence t) {
                  int u = (t.size()/n);
                  for(int i=0;i<u-1;i++) {
                    var a = t.permutate(Sequence.stair(i*n, n, 1));
                    var b = t.permutate(Sequence.stair(((i+1)%u)*n, n, 1));
                  
                    if(!innerPr.test(a.juxtapose(b)) || a.equals(b)) {
                      return false;
                    }
                  }
                  return true;
                }
                
              };
              
              
              Sequence o = new Sequence();
              int i=0;
              do {
                var theset = new TreeSet<Sequence>();
                theset.addAll(n==8 ? s8 : n==12 ? s12 : s16);
                
                if(i==0) {
                  o = CollectionUtils.chooseAtRandom(theset);
                  i++;
                } else {
                  boolean found = false;
                  while(!found) {
                    var candidate = CollectionUtils.chooseAtRandom(theset);
                    
                    var j = o.juxtapose(candidate);
                    if(pred.test(j)) {
                      found = true;
                      o = j;
                      i++;
                      break;
                    } else { 
                      theset.remove(candidate); 
                      if(theset.isEmpty()) {
                        i=0;
                        break;
                      }
                    }
                    if(!isComputing) return;
                  }
                }
              } while(i<k);
                
              isComputing = false;
              btnNewButton.setText("Agglutinate");
              
              result.setText(o.toString());
            }
          });
          
          th.start();
          
        }
        
        
        
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
