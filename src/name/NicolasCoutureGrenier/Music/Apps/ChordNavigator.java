package name.NicolasCoutureGrenier.Music.Apps;

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

import name.NicolasCoutureGrenier.CS.Utils;
import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence;
import name.NicolasCoutureGrenier.Maths.Relations.Relation;
import name.NicolasCoutureGrenier.Music.PCS12;
import name.NicolasCoutureGrenier.Music.PCS12Predicates.Consonant;
import name.NicolasCoutureGrenier.Music.PCS12Predicates.SubsetOf;
import name.NicolasCoutureGrenier.Music.PCS12Predicates.SupersetOf;

import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import name.NicolasCoutureGrenier.Music.PCS12Relations.PredicatedUnion;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.ListSelectionModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChordNavigator {

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
          ChordNavigator window = new ChordNavigator();
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
  public ChordNavigator() {
    initialize();
    refreshAvailable();
    refreshButtons();
  }

  private void refreshAvailable() {
    
    TreeSet<PCS12> pCS12s = PCS12.getChords();
    PCS12 scale = PCS12.parse(cboScale.getSelectedItem().toString());
    DefaultListModel<String> modelIncl = (DefaultListModel<String>) included.getModel();
    
    TreeSet<String> incl = new TreeSet<String>();
    for(int i=0;i<modelIncl.size();i++) incl.add(modelIncl.elementAt(i));
    TreeSet<PCS12> inclCh = new TreeSet<PCS12>();
    for(String s : incl) inclCh.add(PCS12.parse(s));
    PCS12 union = null;
    for(PCS12 ch : inclCh) {
      if(union == null) union = ch;
      else {
        union = union.combineWith(ch);
      }
    }
          
    Predicate<PCS12> pred = new SubsetOf(scale);
    if(chckbxNoMinorSecond.isSelected()) pred = Predicates.and(pred, new Consonant());
    if(union != null) pred = Relation.bindFirst(union, new PredicatedUnion(pred));
    
    ArrayList<String> filtered = new ArrayList<String>();
    for(PCS12 ch : pCS12s) {
      if(pred.apply(ch) && !incl.contains(ch.toString())) {
        filtered.add(ch.toString());
      }
    }
    
    filtered.sort(new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        return o2.compareTo(o1);
      }
    });
    
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
      textForte.setText("");
      textSymmetries.setText("");
      this.current = null;
      textIntervals.setText("");
    } else {
      textCurrent.setText(ch.toString());
      textPitches.setText(ch.combinationString().replaceAll("[,}{]", "").trim()); 
      textIV.setText(ch.getIntervalVector().toString().replaceAll("[,})({]", ""));
      PCS12 scale = PCS12.parse(cboScale.getSelectedItem().toString());
      textComplement.setText(scale.minus(ch).toString());
      textForte.setText(ch.toForteNumberString());
      textSymmetries.setText(Joiner.on(", ").join(ch.getSymmetries()));
      var commonName = ch.getCommonName();
      if(commonName == null) commonName = "";
      textCommonName.setText(commonName);
      textIntervals.setText(ch.transpose(-ch.getTranspose()).getComposition().asSequence().toString());
      this.current = ch;
    }
    
  }
  private PCS12 union = null;
  
  private void refreshUnion() {
    DefaultListModel<String> modelIncl = (DefaultListModel<String>) included.getModel();
    
    TreeSet<String> incl = new TreeSet<String>();
    for(int i=0;i<modelIncl.size();i++) incl.add(modelIncl.elementAt(i));
    TreeSet<PCS12> inclCh = new TreeSet<PCS12>();
    for(String s : incl) inclCh.add(PCS12.parse(s));
    PCS12 union = null;
    for(PCS12 ch : inclCh) {
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
      textUnion.setText(union.toString());
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
    PCS12 selected = PCS12.parse(included.getSelectedValue());
    Predicate<PCS12> pred = new SubsetOf(selected);
    DefaultListModel<String> modelAvailable = (DefaultListModel<String>) available.getModel();
    DefaultListModel<String> modelIncl = (DefaultListModel<String>) included.getModel();
    for(int i=0;i<modelAvailable.size();i++) {
      PCS12 tmp = PCS12.parse(modelAvailable.get(i));
      if(pred.apply(tmp)) modelIncl.add(modelIncl.size(), tmp.toString());
    }
    refreshAvailable();
    refreshButtons();
    refreshUnion();
  }
  
  private void addAllSuperchords() {
    if(included.isSelectionEmpty()) return;
    PCS12 selected = PCS12.parse(included.getSelectedValue());
    Predicate<PCS12> pred = new SupersetOf(selected);
    DefaultListModel<String> modelAvailable = (DefaultListModel<String>) available.getModel();
    DefaultListModel<String> modelIncl = (DefaultListModel<String>) included.getModel();
    for(int i=0;i<modelAvailable.size();i++) {
      PCS12 tmp = PCS12.parse(modelAvailable.get(i));
      if(pred.apply(tmp)) modelIncl.add(modelIncl.size(), tmp.toString());
    }
    refreshAvailable();
    refreshButtons();
    refreshUnion();
  }
  private Synthesizer midiSynth = null;
  private JTextField textComplement;
  private JTextField textForte;
  private JTextField textSymmetries;
  private JTextField textCommonName;
  private JTextField textIntervals;
  
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
    frmChordNavigator = new JFrame();
    frmChordNavigator.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        midiSynth.close();
      }
    });
    frmChordNavigator.setResizable(false);
    frmChordNavigator.setTitle("PCS12 Navigator");
    frmChordNavigator.setBounds(100, 100, 534, 684);
    frmChordNavigator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frmChordNavigator.getContentPane().setLayout(null);
    
    JLabel lblNewLabel = new JLabel("Scale:");
    lblNewLabel.setToolTipText("<html>\r\n07-26.04 Major Locrian<br/>\r\n07-28.11 Persian<br/>\r\n07-29.06 Hungarian<br/>\r\n07-38.11 Harmonic minor<br/>\r\n07-39.11 Melodic minor<br/>\r\n07-42.11 Harmonic major<br/>\r\n07-43.11 Major<br/>\r\n08-35.00 Octatonic<br/>\r\n</html>");
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
    scrollPane.setBounds(10, 79, 174, 556);
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
        PCS12 ch = included.isSelectionEmpty() ? null : PCS12.parse(included.getSelectedValue());
        setCurrent(ch);
        refreshButtons();
      }
    });
    
    
    scrollPane.setViewportView(included);
    
    JScrollPane scrollPane_1 = new JScrollPane();
    scrollPane_1.setBounds(337, 79, 174, 555);
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
        PCS12 ch = available.isSelectionEmpty() ? null : PCS12.parse(available.getSelectedValue());
        setCurrent(ch);
        refreshButtons();
      }
    });
    
    
    scrollPane_1.setViewportView(available);
    btnInclude.setBounds(194, 82, 132, 23);
    
    
    btnInclude.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        include();
      }
    });
    btnInclude.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    frmChordNavigator.getContentPane().add(btnInclude);
    btnExclude.setBounds(194, 114, 132, 23);
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
    String[] cs = PCS12.getChordDict().keySet().toArray(new String[0]);
    Arrays.sort(cs);
    cboScale.setModel(new DefaultComboBoxModel<String>(cs));
    cboScale.setSelectedIndex(Arrays.asList(cs).indexOf("07-43.11"));
    frmChordNavigator.getContentPane().add(cboScale);
    
    JLabel lblNewLabel_1 = new JLabel("Included");
    lblNewLabel_1.setBounds(10, 49, 183, 24);
    lblNewLabel_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
    frmChordNavigator.getContentPane().add(lblNewLabel_1);
    
    JLabel lblNewLabel_2 = new JLabel("Available");
    lblNewLabel_2.setBounds(309, 49, 185, 24);
    lblNewLabel_2.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
    frmChordNavigator.getContentPane().add(lblNewLabel_2);
    
    JLabel lblNewLabel_3 = new JLabel("Current");
    lblNewLabel_3.setBounds(195, 136, 132, 14);
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
    textCurrent.setBounds(195, 149, 132, 23);
    textCurrent.setEditable(false);
    textCurrent.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textCurrent.setHorizontalAlignment(SwingConstants.CENTER);
    frmChordNavigator.getContentPane().add(textCurrent);
    textCurrent.setColumns(10);
    
    JLabel lblNewLabel_4 = new JLabel("Pitches");
    lblNewLabel_4.setBounds(194, 179, 132, 14);
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
    textPitches.setBounds(195, 197, 132, 23);
    textPitches.setEditable(false);
    textPitches.setHorizontalAlignment(SwingConstants.CENTER);
    textPitches.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    frmChordNavigator.getContentPane().add(textPitches);
    textPitches.setColumns(10);
    
    JLabel lblNewLabel_5 = new JLabel("Union");
    lblNewLabel_5.setBounds(195, 447, 132, 23);
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
    textUnion.setBounds(195, 470, 132, 23);
    textUnion.setEditable(false);
    textUnion.setHorizontalAlignment(SwingConstants.CENTER);
    textUnion.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    frmChordNavigator.getContentPane().add(textUnion);
    textUnion.setColumns(10);
    
    JLabel lblNewLabel_6 = new JLabel("Union Pitches");
    lblNewLabel_6.setBounds(194, 491, 132, 23);
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
    textUnionPitches.setBounds(194, 512, 132, 23);
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
    btnAllSubchords.setBounds(194, 583, 132, 24);
    frmChordNavigator.getContentPane().add(btnAllSubchords);
    
    
    btnAllSuperchords.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addAllSuperchords();
      }
    });
    btnAllSuperchords.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 10));
    btnAllSuperchords.setEnabled(false);
    btnAllSuperchords.setBounds(194, 611, 132, 24);
    frmChordNavigator.getContentPane().add(btnAllSuperchords);
    
    JLabel lblNewLabel_6_1 = new JLabel("Interval vector");
    lblNewLabel_6_1.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_6_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_6_1.setBounds(195, 220, 132, 23);
    frmChordNavigator.getContentPane().add(lblNewLabel_6_1);
    
    textIV = new JTextField();
    textIV.setHorizontalAlignment(SwingConstants.CENTER);
    textIV.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textIV.setEditable(false);
    textIV.setColumns(10);
    textIV.setBounds(195, 241, 132, 23);
    frmChordNavigator.getContentPane().add(textIV);
    
    textComplement = new JTextField();
    textComplement.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        playChord(PCS12.parse(textComplement.getText()));
      }
    });
    textComplement.setHorizontalAlignment(SwingConstants.CENTER);
    textComplement.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textComplement.setEditable(false);
    textComplement.setColumns(10);
    textComplement.setBounds(194, 338, 132, 23);
    frmChordNavigator.getContentPane().add(textComplement);
    
    JLabel lblNewLabel_6_2 = new JLabel("Complement");
    lblNewLabel_6_2.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_6_2.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_6_2.setBounds(195, 314, 132, 23);
    frmChordNavigator.getContentPane().add(lblNewLabel_6_2);
    
    JLabel lblNewLabel_6_3 = new JLabel("Forte number");
    lblNewLabel_6_3.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_6_3.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_6_3.setBounds(195, 360, 132, 23);
    frmChordNavigator.getContentPane().add(lblNewLabel_6_3);
    
    textForte = new JTextField();
    textForte.setHorizontalAlignment(SwingConstants.CENTER);
    textForte.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textForte.setEditable(false);
    textForte.setColumns(10);
    textForte.setBounds(195, 381, 132, 23);
    frmChordNavigator.getContentPane().add(textForte);
    
    textSymmetries = new JTextField();
    textSymmetries.setHorizontalAlignment(SwingConstants.CENTER);
    textSymmetries.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textSymmetries.setEditable(false);
    textSymmetries.setColumns(10);
    textSymmetries.setBounds(195, 425, 132, 23);
    frmChordNavigator.getContentPane().add(textSymmetries);
    
    JLabel lblSymmetries = new JLabel("Symmetries");
    lblSymmetries.setHorizontalAlignment(SwingConstants.CENTER);
    lblSymmetries.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblSymmetries.setBounds(195, 404, 132, 23);
    frmChordNavigator.getContentPane().add(lblSymmetries);
    
    textCommonName = new JTextField();
    textCommonName.setHorizontalAlignment(SwingConstants.CENTER);
    textCommonName.setFont(new Font("Dialog", Font.PLAIN, 11));
    textCommonName.setEditable(false);
    textCommonName.setColumns(10);
    textCommonName.setBounds(194, 554, 132, 23);
    frmChordNavigator.getContentPane().add(textCommonName);
    
    JLabel lblNewLabel_6_4 = new JLabel("Common Name");
    lblNewLabel_6_4.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_6_4.setFont(new Font("Dialog", Font.PLAIN, 11));
    lblNewLabel_6_4.setBounds(194, 533, 132, 23);
    frmChordNavigator.getContentPane().add(lblNewLabel_6_4);
    
    JLabel lblNewLabel_6_2_1 = new JLabel("Intervals");
    lblNewLabel_6_2_1.setHorizontalAlignment(SwingConstants.CENTER);
    lblNewLabel_6_2_1.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    lblNewLabel_6_2_1.setBounds(195, 266, 132, 23);
    frmChordNavigator.getContentPane().add(lblNewLabel_6_2_1);
    
    textIntervals = new JTextField();
    textIntervals.setHorizontalAlignment(SwingConstants.CENTER);
    textIntervals.setFont(new Font("DejaVu Sans Mono", Font.PLAIN, 11));
    textIntervals.setEditable(false);
    textIntervals.setColumns(10);
    textIntervals.setBounds(194, 290, 132, 23);
    frmChordNavigator.getContentPane().add(textIntervals);
  }
}
