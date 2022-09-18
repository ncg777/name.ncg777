package name.ncg.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import name.ncg.Maths.DataStructures.CollectionUtils;
import name.ncg.Maths.DataStructures.Pair;
import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Music.R16List;
import name.ncg.Music.Rhythm;

import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.util.TreeMap;
import java.util.TreeSet;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import java.awt.Font;

public class RexRandomizer {

  private JFrame frmRexRandomizer;
  private JTextField textRex;
  private JTextField textEQClasses;
  private JTextField textResynth;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          RexRandomizer window = new RexRandomizer();
          window.frmRexRandomizer.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public RexRandomizer() {
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frmRexRandomizer = new JFrame();
    frmRexRandomizer.setTitle("REX Randomizer");
    frmRexRandomizer.setBounds(100, 100, 510, 346);
    frmRexRandomizer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JLabel lblRexRhythm = new JLabel("REX rhythm :");
    lblRexRhythm.setFont(new Font("Unifont", Font.PLAIN, 11));
    lblRexRhythm.setHorizontalAlignment(SwingConstants.RIGHT);
    
    textRex = new JTextField();
    textRex.setColumns(10);
    
    JSpinner spinner = new JSpinner();
    spinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
    
    JLabel lblbars = new JLabel("#BARS :");
    lblbars.setFont(new Font("Unifont", Font.PLAIN, 11));
    lblbars.setHorizontalAlignment(SwingConstants.RIGHT);
    JButton btnGenerate = new JButton("Generate");
    
    JScrollPane scrollPane = new JScrollPane();
    JTextArea txtrResult = new JTextArea();
    
    btnGenerate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        
        Sequence map = Sequence.parse(textEQClasses.getText().trim());
        Sequence resynth = Sequence.parse(textResynth.getText().trim());
        
        int modul = map.size();
        int modulr = resynth.size();
        
        int nbBars = (int)spinner.getValue();
        int len = nbBars*16;
        
        R16List r = R16List.parseR16Seq(textRex.getText());
        
        Sequence c = r.asRhythm().getComposition().asSequence();
        
        TreeMap<Integer, TreeSet<Pair<Integer>>> posM = new TreeMap<>();
        
        for(int i=0;i<map.distinct().size();i++) {
          posM.put(i, new TreeSet<Pair<Integer>>());
        }
        
        int pos = 0;
        int pitch = 36;

        for(int i=0;i<c.size();i++) {
          int dur = c.get(i);
          int pm = pos % modul;
          Pair<Integer> p = Pair.makePair(pitch, dur);
          posM.get(map.get(pm)).add(p);
          pos += dur;
          pitch++;
        }
        
        TreeSet<Integer> newRhythm = new TreeSet<>();
        Sequence pitchSeq = new Sequence();
        pos = 0;
       
        while(pos < len) {
          int pm = resynth.get(pos % modulr);
          
          if(posM.get(pm).size() == 0) {
            pos++;
            continue;
          } 
          
          Pair<Integer> p = CollectionUtils.chooseAtRandom(posM.get(pm));
          
          int pch = p.getFirst();
          int dur = p.getSecond();
          
          newRhythm.add(pos);
          pitchSeq.add(pch);
          pos += dur;
          
        }
        
        R16List nr = R16List.fromRhythm(Rhythm.buildRhythm(len, newRhythm));
        String output = "";
        
        output = nr.toString() + "\n" + pitchSeq.toString().replaceAll("[)(]", "");
        
        txtrResult.setText(output);
       
      }
    });
    
    JLabel lblEqClases = new JLabel("EQ Classes :");
    lblEqClases.setHorizontalAlignment(SwingConstants.RIGHT);
    lblEqClases.setFont(new Font("Dialog", Font.PLAIN, 11));
    
    textEQClasses = new JTextField();
    textEQClasses.setText("0 1 2 3 4 3 2 1");
    textEQClasses.setColumns(10);
    
    textResynth = new JTextField();
    textResynth.setText("0 1 2 3 4 3 2 1");
    textResynth.setColumns(10);
    
    JLabel lblResynth = new JLabel("Reseq :");
    lblResynth.setHorizontalAlignment(SwingConstants.RIGHT);
    lblResynth.setFont(new Font("Dialog", Font.PLAIN, 11));
    
    
    
    
    GroupLayout groupLayout = new GroupLayout(frmRexRandomizer.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.TRAILING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(lblbars, GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
            .addComponent(lblRexRhythm, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblEqClases, GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
            .addComponent(lblResynth, GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(textResynth, GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)
              .addGap(23))
            .addGroup(groupLayout.createSequentialGroup()
              .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                .addComponent(btnGenerate, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                .addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                .addComponent(textRex, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                .addComponent(spinner, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                .addComponent(textEQClasses, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE))
              .addGap(23))))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(textRex, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblRexRhythm))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblbars))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(textEQClasses, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblEqClases, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblResynth, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
            .addComponent(textResynth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addGap(12)
          .addComponent(btnGenerate)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
          .addGap(10))
    );
    
    
    scrollPane.setViewportView(txtrResult);
    frmRexRandomizer.getContentPane().setLayout(groupLayout);
  }
}
