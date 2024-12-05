package name.ncg777.maths.pitchClassSet12.apps;

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
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import name.ncg777.computing.Utils;
import name.ncg777.maths.pitchClassSet12.PitchClassSet12;
import name.ncg777.maths.pitchClassSet12.predicates.Consonant;
import name.ncg777.maths.pitchClassSet12.predicates.SubsetOf;
import name.ncg777.maths.pitchClassSet12.predicates.SupersetOf;
import name.ncg777.maths.pitchClassSet12.relations.PredicatedUnion;
import name.ncg777.maths.relations.Relation;
import name.ncg777.maths.sequences.Sequence;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.ListSelectionModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class Combiner {

  private JFrame frmChordNavigator;
  private JTextField textCurrent;
  private JTextField textPitches;
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          Combiner window = new Combiner();
          window.frmChordNavigator.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public Combiner() {
    initialize();
    refreshAvailable();
    refreshButtons();
  }
  private Comparator<String> comparator = PitchClassSet12.ForteStringComparator.reversed();
  private void refreshAvailable() {
    
    TreeSet<PitchClassSet12> PitchClassSet12s = PitchClassSet12.getChords();
    PitchClassSet12 scale = PitchClassSet12.parseForte(cboScale.getSelectedItem().toString());
    DefaultListModel<String> modelIncl = (DefaultListModel<String>) included.getModel();
    
    TreeSet<String> incl = new TreeSet<String>();
    for(int i=0;i<modelIncl.size();i++) incl.add(modelIncl.elementAt(i));
    TreeSet<PitchClassSet12> inclCh = new TreeSet<PitchClassSet12>();
    for(String s : incl) inclCh.add(PitchClassSet12.parseForte(s));
    PitchClassSet12 union = null;
    for(PitchClassSet12 ch : inclCh) {
      if(union == null) union = ch;
      else {
        union = union.combineWith(ch);
      }
    }
          
    Predicate<PitchClassSet12> pred = new SubsetOf(scale);
    if(chckbxNoMinorSecond.isSelected()) pred = Predicates.and(pred, new Consonant());
    if(union != null) pred = Relation.bindFirst(union, new PredicatedUnion(pred));
    
    ArrayList<String> filtered = new ArrayList<String>();
    for(PitchClassSet12 ch : PitchClassSet12s) {
      if(pred.apply(ch) && !incl.contains(ch.toForteNumberString())) {
        filtered.add(ch.toForteNumberString());
      }
    }
    
    filtered.sort(comparator);
    
    DefaultListModel<String> modelAv = (DefaultListModel<String>) available.getModel();
    
    modelAv.clear();
    
    for(String s : filtered) modelAv.addElement(s);
  }
  
  private void refreshButtons() {
    btnInclude.setEnabled(!available.isSelectionEmpty());
    btnExclude.setEnabled(!included.isSelectionEmpty());
    btnAllSubchords.setEnabled(!included.isSelectionEmpty());
    btnAllSuperchords.setEnabled(!included.isSelectionEmpty());
  }
  
  JComboBox<String> cboScale = new JComboBox<String>();
  JCheckBox chckbxNoMinorSecond = new JCheckBox("Consonant");
  JList<String> available = new JList<String>(new DefaultListModel<String>());
  JList<String> included = new JList<String>(new DefaultListModel<String>());
  JButton btnInclude = new JButton("Include");
  JButton btnExclude = new JButton("Exclude");
  private JTextField textUnion;
  private JTextField textUnionPitches;
  JButton btnAllSubchords = new JButton("All subchords");
  JButton btnAllSuperchords = new JButton("All superchords");
  private JTextField textIV;
  private PitchClassSet12 current = null;
  /**
   * Initialize the contents of the frame.
   */
  
  private void setCurrent(PitchClassSet12 ch) {
    if(ch == null) {
      textCurrent.setText(""); 
      textPitches.setText("");
      textIV.setText("");
      textComplement.setText("");
      textSymmetries.setText("");
      this.current = null;
      textIntervals.setText("");
      textTonicDistance.setText("");
    } else {
      textCurrent.setText(ch.toForteNumberString());
      textPitches.setText(ch.combinationString().replaceAll("[,}{]", "").trim()); 
      textIV.setText(ch.getIntervalVector().toString().replaceAll("[,})({]", ""));
      PitchClassSet12 scale = PitchClassSet12.parseForte(cboScale.getSelectedItem().toString());
      textComplement.setText(scale.minus(ch).toForteNumberString());
      textSymmetries.setText(Joiner.on(", ").join(ch.getSymmetries()));
      var commonName = ch.getCommonName();
      if(commonName == null) commonName = "";
      textCommonName.setText(commonName);
      textIntervals.setText(ch.transpose(-ch.getTranspose()).getComposition().asSequence().toString());
      textTonicDistance.setText(Double.toString(ch.calcCenterTuning((int)spinnerCenter.getValue())));
      this.current = ch;
    }
    
  }
  private PitchClassSet12 union = null;
  
  private void refreshUnion() {
    DefaultListModel<String> modelIncl = (DefaultListModel<String>) included.getModel();
    
    TreeSet<String> incl = new TreeSet<String>();
    for(int i=0;i<modelIncl.size();i++) incl.add(modelIncl.elementAt(i));
    TreeSet<PitchClassSet12> inclCh = new TreeSet<PitchClassSet12>();
    for(String s : incl) inclCh.add(PitchClassSet12.parseForte(s));
    PitchClassSet12 union = null;
    for(PitchClassSet12 ch : inclCh) {
      if(union == null) union = ch;
      else {
        union = union.combineWith(ch);
      }
    }
    this.union = union;
    if(union == null) {
      if(textUnion != null) {
        textUnion.setText("");
        textUnionPitches.setText("");  
      }
    } else {
      textUnion.setText(union.toForteNumberString());
      textUnionPitches.setText(union.combinationString().replaceAll("[,}{]", "").trim());
    }
    
  }
  
  private void include() {
    if(available.isSelectionEmpty()) return;
    DefaultListModel<String> modelIncl = (DefaultListModel<String>) included.getModel();
    modelIncl.addElement(available.getSelectedValue());
    refreshAvailable();
    refreshButtons();
    refreshUnion();
  }
  
  private void exclude() {
    if(included.isSelectionEmpty()) return;
    DefaultListModel<String> modelIncl = (DefaultListModel<String>) included.getModel();
    modelIncl.removeElement(included.getSelectedValue());
    refreshAvailable();
    refreshButtons();
    refreshUnion();
  }
  
  private void addAllSubchords() {
    if(included.isSelectionEmpty()) return;
    PitchClassSet12 selected = PitchClassSet12.parseForte(included.getSelectedValue());
    Predicate<PitchClassSet12> pred = new SubsetOf(selected);
    DefaultListModel<String> modelAvailable = (DefaultListModel<String>) available.getModel();
    DefaultListModel<String> modelIncl = (DefaultListModel<String>) included.getModel();
    for(int i=0;i<modelAvailable.size();i++) {
      PitchClassSet12 tmp = PitchClassSet12.parseForte(modelAvailable.get(i));
      if(pred.apply(tmp)) modelIncl.add(modelIncl.size(), tmp.toForteNumberString());
    }
    refreshAvailable();
    refreshButtons();
    refreshUnion();
  }
  
  private void addAllSuperchords() {
    if(included.isSelectionEmpty()) return;
    PitchClassSet12 selected = PitchClassSet12.parseForte(included.getSelectedValue());
    Predicate<PitchClassSet12> pred = new SupersetOf(selected);
    DefaultListModel<String> modelAvailable = (DefaultListModel<String>) available.getModel();
    DefaultListModel<String> modelIncl = (DefaultListModel<String>) included.getModel();
    for(int i=0;i<modelAvailable.size();i++) {
      PitchClassSet12 tmp = PitchClassSet12.parseForte(modelAvailable.get(i));
      if(pred.apply(tmp)) modelIncl.add(modelIncl.size(), tmp.toForteNumberString());
    }
    refreshAvailable();
    refreshButtons();
    refreshUnion();
  }
  private Synthesizer midiSynth = null;
  private JTextField textComplement;
  private JTextField textSymmetries;
  private JTextField textCommonName;
  private JTextField textIntervals;
  private JTextField textTonicDistance;
  private JSpinner spinnerCenter;
  
  private void initMidiSynth() {
    try {
      this.midiSynth = MidiSystem.getSynthesizer();
      Instrument[] instr = midiSynth.getDefaultSoundbank().getInstruments();
      
      
      midiSynth.loadInstrument(instr[0]);//load an instrument
    } catch (MidiUnavailableException e) {

    }
  }
  private void playChord(PitchClassSet12 chord) {
    if(chord == null) return;
    Utils.copyStringToClipboard(chord.toForteNumberString());
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
    frmChordNavigator = new JFrame();
    frmChordNavigator.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        midiSynth.close();
      }
    });
    frmChordNavigator.setResizable(false);
    frmChordNavigator.setTitle("Chord Navigator");
    frmChordNavigator.setBounds(100, 100, 534, 684);
    frmChordNavigator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmChordNavigator.getContentPane().setLayout(null);
    
    JLabel lblNewLabel = new JLabel("Scale:");
    lblNewLabel.setBounds(10, 15, 42, 14);
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    frmChordNavigator.getContentPane().add(lblNewLabel);
    chckbxNoMinorSecond.setBounds(164, 11, 100, 23);
    chckbxNoMinorSecond.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DefaultListModel<String> modelAv = (DefaultListModel<String>) included.getModel();
        modelAv.clear();
        refreshAvailable();
        refreshButtons();
        refreshUnion();
      }
    });
    chckbxNoMinorSecond.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    chckbxNoMinorSecond.setSelected(false);
    frmChordNavigator.getContentPane().add(chckbxNoMinorSecond);
    
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBounds(10, 61, 174, 574);
    frmChordNavigator.getContentPane().add(scrollPane);
    included.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount()>=2) exclude();
      }
    });
    
    included.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    included.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        PitchClassSet12 ch = included.isSelectionEmpty() ? null : PitchClassSet12.parseForte(included.getSelectedValue());
        setCurrent(ch);
        refreshButtons();
      }
    });
    
    
    scrollPane.setViewportView(included);
    
    JScrollPane scrollPane_1 = new JScrollPane();
    scrollPane_1.setBounds(337, 61, 174, 573);
    frmChordNavigator.getContentPane().add(scrollPane_1);
    available.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount()>=2) include();
      }
    });
    available.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    available.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        PitchClassSet12 ch = available.isSelectionEmpty() ? null : PitchClassSet12.parseForte(available.getSelectedValue());
        setCurrent(ch);
        refreshButtons();
      }
    });
    
    
    scrollPane_1.setViewportView(available);
    btnInclude.setBounds(195, 59, 132, 14);
    
    
    btnInclude.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        include();
      }
    });
    btnInclude.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    frmChordNavigator.getContentPane().add(btnInclude);
    btnExclude.setBounds(194, 77, 132, 14);
    btnExclude.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    
    
    btnExclude.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        exclude();
      }
    });
    frmChordNavigator.getContentPane().add(btnExclude);
    cboScale.setBounds(58, 12, 100, 20);
    
   
    cboScale.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DefaultListModel<String> modelAv = (DefaultListModel<String>) included.getModel();
        modelAv.clear();
        refreshAvailable();
        refreshButtons();
        refreshUnion();
      }
    });
    ArrayList<String> cs = new ArrayList<String>();
    cs.addAll(PitchClassSet12.getForteChordDict().keySet());
    cs.sort(PitchClassSet12.ForteStringComparator.reversed());
    cboScale.setModel(new DefaultComboBoxModel<String>(cs.toArray(new String[0])));
    cboScale.setSelectedIndex(cs.indexOf("12-1.00"));
    frmChordNavigator.getContentPane().add(cboScale);
    
    JLabel lblNewLabel_1 = new JLabel("Included");
    lblNewLabel_1.setBounds(10, 40, 183, 24);
    lblNewLabel_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
    frmChordNavigator.getContentPane().add(lblNewLabel_1);
    
    JLabel lblNewLabel_2 = new JLabel("Available");
    lblNewLabel_2.setBounds(326, 40, 185, 24);
    lblNewLabel_2.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
    frmChordNavigator.getContentPane().add(lblNewLabel_2);
    
    JLabel lblNewLabel_3 = new JLabel("Current");
    lblNewLabel_3.setBounds(194, 102, 132, 14);
    lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_3.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    frmChordNavigator.getContentPane().add(lblNewLabel_3);
    
    textCurrent = new JTextField();
    textCurrent.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        playChord(current);
      }
    });
    textCurrent.setBounds(195, 115, 132, 23);
    textCurrent.setEditable(false);
    textCurrent.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textCurrent.setHorizontalAlignment(SwingConstants.CENTER);
    frmChordNavigator.getContentPane().add(textCurrent);
    textCurrent.setColumns(10);
    
    JLabel lblNewLabel_4 = new JLabel("Pitches");
    lblNewLabel_4.setBounds(194, 138, 132, 14);
    lblNewLabel_4.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);
    frmChordNavigator.getContentPane().add(lblNewLabel_4);
    
    textPitches = new JTextField();
    textPitches.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        playChord(current);
      }
    });
    textPitches.setBounds(195, 155, 132, 23);
    textPitches.setEditable(false);
    textPitches.setHorizontalAlignment(SwingConstants.CENTER);
    textPitches.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    frmChordNavigator.getContentPane().add(textPitches);
    textPitches.setColumns(10);
    
    JLabel lblNewLabel_5 = new JLabel("Union");
    lblNewLabel_5.setBounds(194, 372, 132, 23);
    lblNewLabel_5.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_5.setHorizontalAlignment(SwingConstants.CENTER);
    frmChordNavigator.getContentPane().add(lblNewLabel_5);
    
    textUnion = new JTextField();
    textUnion.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        playChord(union);
      }
    });
    textUnion.setBounds(194, 394, 132, 23);
    textUnion.setEditable(false);
    textUnion.setHorizontalAlignment(SwingConstants.CENTER);
    textUnion.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    frmChordNavigator.getContentPane().add(textUnion);
    textUnion.setColumns(10);
    
    JLabel lblNewLabel_6 = new JLabel("Union Pitches");
    lblNewLabel_6.setBounds(194, 417, 132, 23);
    lblNewLabel_6.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_6.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    frmChordNavigator.getContentPane().add(lblNewLabel_6);
    
    textUnionPitches = new JTextField();
    textUnionPitches.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        playChord(union);
      }
    });
    textUnionPitches.setBounds(195, 438, 132, 23);
    textUnionPitches.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textUnionPitches.setEditable(false);
    textUnionPitches.setHorizontalAlignment(SwingConstants.CENTER);
    frmChordNavigator.getContentPane().add(textUnionPitches);
    textUnionPitches.setColumns(10);
    btnAllSubchords.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addAllSubchords();
      }
    });
    
    
    btnAllSubchords.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    btnAllSubchords.setBounds(195, 560, 132, 14);
    frmChordNavigator.getContentPane().add(btnAllSubchords);
    
    
    btnAllSuperchords.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addAllSuperchords();
      }
    });
    btnAllSuperchords.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 10));
    btnAllSuperchords.setEnabled(false);
    btnAllSuperchords.setBounds(194, 579, 132, 17);
    frmChordNavigator.getContentPane().add(btnAllSuperchords);
    
    JLabel lblNewLabel_6_1 = new JLabel("Interval vector");
    lblNewLabel_6_1.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_6_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_6_1.setBounds(194, 178, 132, 23);
    frmChordNavigator.getContentPane().add(lblNewLabel_6_1);
    
    textIV = new JTextField();
    textIV.setHorizontalAlignment(SwingConstants.CENTER);
    textIV.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textIV.setEditable(false);
    textIV.setColumns(10);
    textIV.setBounds(195, 201, 132, 23);
    frmChordNavigator.getContentPane().add(textIV);
    
    textComplement = new JTextField();
    textComplement.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        playChord(PitchClassSet12.parseForte(textComplement.getText()));
      }
    });
    textComplement.setHorizontalAlignment(SwingConstants.CENTER);
    textComplement.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textComplement.setEditable(false);
    textComplement.setColumns(10);
    textComplement.setBounds(194, 294, 132, 23);
    frmChordNavigator.getContentPane().add(textComplement);
    
    JLabel lblNewLabel_6_2 = new JLabel("Complement");
    lblNewLabel_6_2.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_6_2.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_6_2.setBounds(194, 273, 132, 23);
    frmChordNavigator.getContentPane().add(lblNewLabel_6_2);
    
    textSymmetries = new JTextField();
    textSymmetries.setHorizontalAlignment(SwingConstants.CENTER);
    textSymmetries.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textSymmetries.setEditable(false);
    textSymmetries.setColumns(10);
    textSymmetries.setBounds(195, 349, 132, 23);
    frmChordNavigator.getContentPane().add(textSymmetries);
    
    JLabel lblSymmetries = new JLabel("Symmetries");
    lblSymmetries.setHorizontalAlignment(SwingConstants.CENTER);
    lblSymmetries.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblSymmetries.setBounds(195, 328, 132, 23);
    frmChordNavigator.getContentPane().add(lblSymmetries);
    
    textCommonName = new JTextField();
    textCommonName.setHorizontalAlignment(SwingConstants.CENTER);
    textCommonName.setFont(new Font("Dialog", Font.PLAIN, 11));
    textCommonName.setEditable(false);
    textCommonName.setColumns(10);
    textCommonName.setBounds(195, 485, 132, 23);
    frmChordNavigator.getContentPane().add(textCommonName);
    
    JLabel lblNewLabel_6_4 = new JLabel("Common Name");
    lblNewLabel_6_4.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_6_4.setFont(new Font("Dialog", Font.PLAIN, 11));
    lblNewLabel_6_4.setBounds(195, 461, 132, 23);
    frmChordNavigator.getContentPane().add(lblNewLabel_6_4);
    
    JLabel lblNewLabel_6_2_1 = new JLabel("Intervals");
    lblNewLabel_6_2_1.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_6_2_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_6_2_1.setBounds(195, 226, 132, 23);
    frmChordNavigator.getContentPane().add(lblNewLabel_6_2_1);
    
    textIntervals = new JTextField();
    textIntervals.setHorizontalAlignment(SwingConstants.CENTER);
    textIntervals.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textIntervals.setEditable(false);
    textIntervals.setColumns(10);
    textIntervals.setBounds(195, 249, 132, 23);
    frmChordNavigator.getContentPane().add(textIntervals);
    
    JLabel lblNewLabel_6_4_1 = new JLabel("Center tuning");
    lblNewLabel_6_4_1.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_6_4_1.setFont(new Font("Dialog", Font.PLAIN, 11));
    lblNewLabel_6_4_1.setBounds(194, 507, 132, 23);
    frmChordNavigator.getContentPane().add(lblNewLabel_6_4_1);
    
    textTonicDistance = new JTextField();
    textTonicDistance.setHorizontalAlignment(SwingConstants.CENTER);
    textTonicDistance.setFont(new Font("Dialog", Font.PLAIN, 11));
    textTonicDistance.setEditable(false);
    textTonicDistance.setColumns(10);
    textTonicDistance.setBounds(194, 526, 132, 23);
    frmChordNavigator.getContentPane().add(textTonicDistance);
    
    spinnerCenter = new JSpinner();
    spinnerCenter.setModel(new SpinnerNumberModel(0, 0, 11, 1));
    spinnerCenter.setBounds(461, 11, 50, 23);
    frmChordNavigator.getContentPane().add(spinnerCenter);
    
    JLabel lblNewLabel_7 = new JLabel("Center:");
    lblNewLabel_7.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel_7.setBounds(405, 15, 46, 14);
    frmChordNavigator.getContentPane().add(lblNewLabel_7);
  }
}
