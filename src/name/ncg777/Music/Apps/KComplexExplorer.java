package name.ncg777.Music.Apps;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import name.ncg777.CS.Utils;
import name.ncg777.Maths.Objects.Sequence;
import name.ncg777.Music.PCS12;
import name.ncg777.Music.PCS12Predicates.Consonant;
import name.ncg777.Music.PCS12Predicates.SubsetOf;
import name.ncg777.Music.PCS12Predicates.SupersetOf;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.ListSelectionModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class KComplexExplorer {

  private JFrame frmKComplexExplorer;
  private JTextField textCurrent = new JTextField();;
  private JTextField textPitches = new JTextField();;
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          KComplexExplorer window = new KComplexExplorer();
          window.frmKComplexExplorer.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public KComplexExplorer() {
    initialize();
    refreshPcs();
  }

  private void refreshPcs() {
    
    TreeSet<PCS12> pCS12s = PCS12.getChords();
    PCS12 scale = PCS12.parseForte(cboScale.getSelectedItem().toString());
          
    Predicate<PCS12> pred = new SubsetOf(scale);
    if(chckbxNoMinorSecond.isSelected()) pred = Predicates.and(pred, new Consonant());
    
    ArrayList<String> filtered = new ArrayList<String>();
    for(PCS12 ch : pCS12s) {
      if(pred.apply(ch)) {
        filtered.add(ch.toForteNumberString());
      }
    }
    
    filtered.sort(PCS12.ForteStringComparator.reversed());
    
    DefaultListModel<String> modelPcs = (DefaultListModel<String>) pitchclasssets.getModel();
    
    modelPcs.clear();
    
    for(String s : filtered) modelPcs.addElement(s);
    setCurrent(null);
  }
  
  private void setComplex() {
    ((DefaultListModel<String>) subsets.getModel()).clear();
    ((DefaultListModel<String>) supersets.getModel()).clear();
    
    if(current == null) {
      return;
    }
    DefaultListModel<String> modelPcs = (DefaultListModel<String>) pitchclasssets.getModel();
    
    Predicate<PCS12> pred1 = new SubsetOf(current);
    DefaultListModel<String> modelSubsets = (DefaultListModel<String>) subsets.getModel();
    for(int i=0;i<modelPcs.size();i++) {
      PCS12 tmp = PCS12.parseForte(modelPcs.get(i));
      if(pred1.apply(tmp)) modelSubsets.add(modelSubsets.size(), tmp.toForteNumberString());
    }
    
    Predicate<PCS12> pred2 = new SupersetOf(current);
    DefaultListModel<String> modelSupersets = (DefaultListModel<String>) supersets.getModel();
    for(int i=0;i<modelPcs.size();i++) {
      PCS12 tmp = PCS12.parseForte(modelPcs.get(i));
      if(pred2.apply(tmp)) modelSupersets.add(modelSupersets.size(), tmp.toForteNumberString());
    }
  }
  
  JComboBox<String> cboScale = new JComboBox<String>();
  JCheckBox chckbxNoMinorSecond = new JCheckBox("Consonant");
  JList<String> pitchclasssets = new JList<String>(new DefaultListModel<String>());
  JList<String> supersets = new JList<String>(new DefaultListModel<String>());
  JList<String> subsets = new JList<String>(new DefaultListModel<String>());
  private JTextField textIV = new JTextField();;
  private PCS12 current = null;
  /**
   * Initialize the contents of the frame.
   */
  
  private void setCurrent(PCS12 ch) {
    if(ch == null) {
      textCurrent.setText(""); 
      textPitches.setText("");
      textIV.setText("");
      textComplement.setText("");
      textPCS12.setText("");
      textSymmetries.setText("");
      this.current = null;
      textIntervals.setText("");
      textTonicDistance.setText("");
    } else {
      textCurrent.setText(ch.toForteNumberString());
      textPitches.setText(ch.combinationString().replaceAll("[,}{]", "").trim()); 
      textIV.setText(ch.getIntervalVector().toString().replaceAll("[,})({]", ""));
      PCS12 scale = PCS12.parseForte(cboScale.getSelectedItem().toString());
      textComplement.setText(scale.minus(ch).toForteNumberString());
      textPCS12.setText(ch.toString());
      textSymmetries.setText(Joiner.on(", ").join(ch.getSymmetries()));
      var commonName = ch.getCommonName();
      if(commonName == null) commonName = "";
      textCommonName.setText(commonName);
      textIntervals.setText(ch.transpose(-ch.getTranspose()).getComposition().asSequence().toString());
      textTonicDistance.setText(Double.toString(ch.calcCenterTuning((int)spinnerCenter.getValue())));
      this.current = ch;
    }
  }
  private Synthesizer midiSynth = null;
  private JTextField textComplement = new JTextField();
  private JTextField textPCS12 = new JTextField();
  private JTextField textSymmetries = new JTextField();
  private JTextField textCommonName = new JTextField();
  private JTextField textIntervals = new JTextField();
  private JTextField textTonicDistance = new JTextField();
  private JSpinner spinnerCenter = new JSpinner(new SpinnerNumberModel(0, 0, 11, 1));
  
  private void initMidiSynth() {
    try {
      this.midiSynth = MidiSystem.getSynthesizer();
      Instrument[] instr = midiSynth.getDefaultSoundbank().getInstruments();
      
      
      midiSynth.loadInstrument(instr[0]);//load an instrument
    } catch (MidiUnavailableException e) {

    }
  }
  private void playChord(PCS12 chord) {
    if(chord == null) return;
    Utils.copyStringToClipboard(chord.toString());
    Thread t = new Thread(new Runnable() {
      public void run() {
        int stepdur = 250;
        try{
          
          if(midiSynth == null) {
            return;
          }
          MidiChannel[] mChannels = midiSynth.getChannels();
          Sequence s = chord.asSequence();
          for(int i=0;i<s.size();i++) {
            int note = 48 + s.get(i);
          
            mChannels[0].noteOn(note, 100);//On channel 0, play note number 60 with velocity 100 
            
            Thread.sleep(stepdur);
            
            mChannels[0].noteOff(note);
          }
          
          Thread.sleep(250);
        
          
          for(int i=0;i<s.size();i++) {
            int note = 48 + s.get(i);
          
            mChannels[0].noteOn(note, 100);
          }    
          Thread.sleep(500);
          
          for(int i=0;i<s.size();i++) {
            int note = 48 + s.get(i);
          
            mChannels[0].noteOff(note);
          }
        }  catch( InterruptedException e ) {
          e.printStackTrace();
        }
        
      }
      
    });
    t.start();
  }
  
  private void initialize() {
    this.initMidiSynth();
    try {
      this.midiSynth.open();
    } catch (MidiUnavailableException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    frmKComplexExplorer = new JFrame();
    frmKComplexExplorer.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        midiSynth.close();
      }
    });
    frmKComplexExplorer.setResizable(false);
    frmKComplexExplorer.setTitle("K-Complex Explorer");
    frmKComplexExplorer.setBounds(100, 100, 588, 712);
    frmKComplexExplorer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmKComplexExplorer.getContentPane().setLayout(null);
    
    JLabel lblNewLabel = new JLabel("Scale:");
    lblNewLabel.setBounds(10, 15, 42, 14);
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    frmKComplexExplorer.getContentPane().add(lblNewLabel);
    chckbxNoMinorSecond.setBounds(164, 11, 100, 23);
    chckbxNoMinorSecond.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DefaultListModel<String> modelAv = (DefaultListModel<String>) supersets.getModel();
        modelAv.clear();
        refreshPcs();
      }
    });
    chckbxNoMinorSecond.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    chckbxNoMinorSecond.setSelected(false);
    frmKComplexExplorer.getContentPane().add(chckbxNoMinorSecond);
    
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBounds(358, 64, 174, 282);
    frmKComplexExplorer.getContentPane().add(scrollPane);
    scrollPane.setViewportView(supersets);
    
    subsets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    subsets.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        PCS12 ch = subsets.isSelectionEmpty() ? null : PCS12.parseForte(subsets.getSelectedValue());
        setCurrent(ch);
      }
    });
    
    supersets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    supersets.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        PCS12 ch = supersets.isSelectionEmpty() ? null : PCS12.parseForte(supersets.getSelectedValue());
        setCurrent(ch);
      }
    });
    
    JScrollPane scrollPane_1 = new JScrollPane();
    scrollPane_1.setBounds(164, 64, 174, 573);
    frmKComplexExplorer.getContentPane().add(scrollPane_1);

    pitchclasssets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    pitchclasssets.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        PCS12 ch = pitchclasssets.isSelectionEmpty() ? null : PCS12.parseForte(pitchclasssets.getSelectedValue());
        setCurrent(ch);
        setComplex();
      }
    });
    
    
    scrollPane_1.setViewportView(pitchclasssets);
    cboScale.setBounds(58, 12, 100, 20);
    
   
    cboScale.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DefaultListModel<String> modelAv = (DefaultListModel<String>) supersets.getModel();
        modelAv.clear();
        refreshPcs();
        setComplex();
      }
    });
    String[] cs = PCS12.getForteChordDict().keySet().toArray(new String[0]);
    Arrays.sort(cs, PCS12.ForteStringComparator);
    cboScale.setModel(new DefaultComboBoxModel<String>(cs));
    cboScale.setSelectedIndex(Arrays.asList(cs).indexOf("8-23.04"));
    frmKComplexExplorer.getContentPane().add(cboScale);
    
    JLabel lblNewLabel_2 = new JLabel("Pitch class sets");
    lblNewLabel_2.setBounds(164, 41, 174, 24);
    lblNewLabel_2.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
    frmKComplexExplorer.getContentPane().add(lblNewLabel_2);
    
    JLabel lblNewLabel_3 = new JLabel("Current");
    lblNewLabel_3.setBounds(10, 64, 132, 14);
    lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_3.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    frmKComplexExplorer.getContentPane().add(lblNewLabel_3);
    
    textCurrent.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        playChord(current);
      }
    });
    textCurrent.setBounds(11, 77, 132, 23);
    textCurrent.setEditable(false);
    textCurrent.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textCurrent.setHorizontalAlignment(SwingConstants.CENTER);
    frmKComplexExplorer.getContentPane().add(textCurrent);
    textCurrent.setColumns(10);
    
    JLabel lblNewLabel_4 = new JLabel("Pitches");
    lblNewLabel_4.setBounds(10, 100, 132, 14);
    lblNewLabel_4.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);
    frmKComplexExplorer.getContentPane().add(lblNewLabel_4);
    
    textPitches.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        playChord(current);
      }
    });
    textPitches.setBounds(11, 117, 132, 23);
    textPitches.setEditable(false);
    textPitches.setHorizontalAlignment(SwingConstants.CENTER);
    textPitches.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    frmKComplexExplorer.getContentPane().add(textPitches);
    textPitches.setColumns(10);
    
    JLabel lblNewLabel_6_1 = new JLabel("Interval vector");
    lblNewLabel_6_1.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_6_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_6_1.setBounds(10, 140, 132, 23);
    frmKComplexExplorer.getContentPane().add(lblNewLabel_6_1);
    
    textIV.setHorizontalAlignment(SwingConstants.CENTER);
    textIV.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textIV.setEditable(false);
    textIV.setColumns(10);
    textIV.setBounds(11, 163, 132, 23);
    frmKComplexExplorer.getContentPane().add(textIV);
    
    textComplement.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        playChord(PCS12.parseForte(textComplement.getText()));
      }
    });
    textComplement.setHorizontalAlignment(SwingConstants.CENTER);
    textComplement.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textComplement.setEditable(false);
    textComplement.setColumns(10);
    textComplement.setBounds(10, 256, 132, 23);
    frmKComplexExplorer.getContentPane().add(textComplement);
    
    JLabel lblNewLabel_6_2 = new JLabel("Complement");
    lblNewLabel_6_2.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_6_2.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_6_2.setBounds(10, 235, 132, 23);
    frmKComplexExplorer.getContentPane().add(lblNewLabel_6_2);
    
    JLabel lblNewLabel_6_3 = new JLabel("PCS12");
    lblNewLabel_6_3.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_6_3.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_6_3.setBounds(11, 282, 132, 23);
    frmKComplexExplorer.getContentPane().add(lblNewLabel_6_3);
    
    textPCS12.setHorizontalAlignment(SwingConstants.CENTER);
    textPCS12.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textPCS12.setEditable(false);
    textPCS12.setColumns(10);
    textPCS12.setBounds(11, 306, 132, 23);
    frmKComplexExplorer.getContentPane().add(textPCS12);
    
    textSymmetries.setHorizontalAlignment(SwingConstants.CENTER);
    textSymmetries.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textSymmetries.setEditable(false);
    textSymmetries.setColumns(10);
    textSymmetries.setBounds(11, 350, 132, 23);
    frmKComplexExplorer.getContentPane().add(textSymmetries);
    
    JLabel lblSymmetries = new JLabel("Symmetries");
    lblSymmetries.setHorizontalAlignment(SwingConstants.CENTER);
    lblSymmetries.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblSymmetries.setBounds(11, 329, 132, 23);
    frmKComplexExplorer.getContentPane().add(lblSymmetries);
    
    textCommonName.setHorizontalAlignment(SwingConstants.CENTER);
    textCommonName.setFont(new Font("Dialog", Font.PLAIN, 11));
    textCommonName.setEditable(false);
    textCommonName.setColumns(10);
    textCommonName.setBounds(11, 408, 132, 23);
    frmKComplexExplorer.getContentPane().add(textCommonName);
    
    JLabel lblNewLabel_6_4 = new JLabel("Common Name");
    lblNewLabel_6_4.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_6_4.setFont(new Font("Dialog", Font.PLAIN, 11));
    lblNewLabel_6_4.setBounds(11, 384, 132, 23);
    frmKComplexExplorer.getContentPane().add(lblNewLabel_6_4);
    
    JLabel lblNewLabel_6_2_1 = new JLabel("Intervals");
    lblNewLabel_6_2_1.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_6_2_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_6_2_1.setBounds(11, 188, 132, 23);
    frmKComplexExplorer.getContentPane().add(lblNewLabel_6_2_1);
    
    textIntervals.setHorizontalAlignment(SwingConstants.CENTER);
    textIntervals.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textIntervals.setEditable(false);
    textIntervals.setColumns(10);
    textIntervals.setBounds(11, 211, 132, 23);
    frmKComplexExplorer.getContentPane().add(textIntervals);
    
    JLabel lblNewLabel_6_4_1 = new JLabel("Center tuning");
    lblNewLabel_6_4_1.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_6_4_1.setFont(new Font("Dialog", Font.PLAIN, 11));
    lblNewLabel_6_4_1.setBounds(10, 430, 132, 23);
    frmKComplexExplorer.getContentPane().add(lblNewLabel_6_4_1);
    
    textTonicDistance.setHorizontalAlignment(SwingConstants.CENTER);
    textTonicDistance.setFont(new Font("Dialog", Font.PLAIN, 11));
    textTonicDistance.setEditable(false);
    textTonicDistance.setColumns(10);
    textTonicDistance.setBounds(10, 449, 132, 23);
    frmKComplexExplorer.getContentPane().add(textTonicDistance);
    
    spinnerCenter.setBounds(461, 11, 50, 23);
    frmKComplexExplorer.getContentPane().add(spinnerCenter);
    
    JLabel lblNewLabel_7 = new JLabel("Center:");
    lblNewLabel_7.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel_7.setBounds(405, 15, 46, 14);
    frmKComplexExplorer.getContentPane().add(lblNewLabel_7);
    
    JLabel lblNewLabel_2_1 = new JLabel("Supersets");
    lblNewLabel_2_1.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_2_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_2_1.setBounds(358, 43, 175, 24);
    frmKComplexExplorer.getContentPane().add(lblNewLabel_2_1);
    
    JLabel lblNewLabel_2_1_1 = new JLabel("Subsets");
    lblNewLabel_2_1_1.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_2_1_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_2_1_1.setBounds(358, 346, 175, 24);
    frmKComplexExplorer.getContentPane().add(lblNewLabel_2_1_1);
    
    JScrollPane scrollPane_2 = new JScrollPane();
    scrollPane_2.setBounds(358, 370, 174, 267);
    frmKComplexExplorer.getContentPane().add(scrollPane_2);
    
    
    scrollPane_2.setViewportView(subsets);
    subsets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  }
}
