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
import name.NicolasCoutureGrenier.Maths.Graphs.DiGraph;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.ShadowContourIsomorphic;
import name.NicolasCoutureGrenier.Music.RhythmPredicates.SpectrumRising;
import name.NicolasCoutureGrenier.Music.SequencePredicates.PredicatedSeqRhythms;
import name.NicolasCoutureGrenier.Music.SequencePredicates.SeqAllRhythmsSCI;
import name.NicolasCoutureGrenier.Statistics.RandomNumberGenerator;

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
  private TreeSet<Sequence> s4 = new TreeSet<Sequence>();
  private TreeSet<Sequence> s8 = new TreeSet<Sequence>();
  
  private DiGraph<Sequence> d4 = null;
  private DiGraph<Sequence> d8 = null;
  
  private JSpinner spinnerN;
  private JSpinner spinnerK;
  private JTextField result;
  private boolean isComputing = false;
  
  private void readSequences(int n) {
    
    if(!(n==4 || n==8)) throw new RuntimeException("invalid param");
    if(n==4) s4 = new TreeSet<Sequence>();
    if(n==8) s8 = new TreeSet<Sequence>();
    
    InputStream ist = Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/necklaces-N" + Integer.toString(n)+ "-K8-SCI-norep.csv");
    var isr = new InputStreamReader(ist);
    BufferedReader br = new BufferedReader(isr);
    var theset = n==4?s4:s8;
    
    while(true) {
      String r = null;
      try {
        r = br.readLine();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      if(r==null || r.isEmpty()) break;
      
      var s0 = Sequence.parse(r);
      for(int i=0;i<s0.size();i++) {
        var sr = s0.rotate(i);
        theset.add(sr);
      }
    }
    
    var pred = new PredicatedSeqRhythms(new ShadowContourIsomorphic());
    
    if(n == 4) {
      d4 = new DiGraph<Sequence>(theset, (Sequence a,Sequence b) -> pred.test(a.juxtapose(b)));
    }
    if(n == 8) {
      TreeSet<Sequence> theset2 = new TreeSet<Sequence>();
      for(var s : theset) if(RandomNumberGenerator.nextDouble() < 0.005) theset2.add(s);
      s8 = theset2;
      d8 = new DiGraph<Sequence>(theset2, (Sequence a,Sequence b) -> pred.test(a.juxtapose(b)));
    }
  }
  /**
   * Create the application.
   */
  public SCISEQAgglutinator() {
    initialize();
    readSequences(4);
    readSequences(8);
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
    spinnerN.setModel(new SpinnerListModel(new String[] {"4","8"}));
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
              var thegraph = n==4 ? d4:d8;
              
              ArrayList<Sequence> a = new ArrayList<Sequence>();
              int i=0;
              do {
                
                
                var theset = new TreeSet<Sequence>();
                theset.addAll(n==4 ? s4 : s8);
                
                
                if(i==0) {
                  a.clear();
                  a.add(CollectionUtils.chooseAtRandom(theset));
                  i++;
                } else {
                  if(thegraph.getNeighborCount(a.get(i-1)) == 0) {
                    i=0;
                    break;
                  }
                  
                  a.add(CollectionUtils.chooseAtRandom(thegraph.getNeighbors(a.get(i-1))));
                  i++;
                  if(!isComputing) return;
                }
              } while(i<k);
                
              isComputing = false;
              btnNewButton.setText("Agglutinate");
              Sequence o = new Sequence();
              for(var s : a) o.addAll(s);
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
