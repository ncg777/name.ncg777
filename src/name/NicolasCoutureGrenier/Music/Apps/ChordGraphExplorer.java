package name.NicolasCoutureGrenier.Music.Apps;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;

import name.NicolasCoutureGrenier.CS.Utils;
import name.NicolasCoutureGrenier.Maths.DataStructures.CollectionUtils;
import name.NicolasCoutureGrenier.Maths.DataStructures.Sequence;
import name.NicolasCoutureGrenier.Maths.Graphs.DiGraph;
import name.NicolasCoutureGrenier.Maths.Relations.Relation;
import name.NicolasCoutureGrenier.Music.PCS12;
import name.NicolasCoutureGrenier.Music.PCS12Predicates.Consonant;
import name.NicolasCoutureGrenier.Music.PCS12Predicates.SizeIs;
import name.NicolasCoutureGrenier.Music.PCS12Predicates.SubsetOf;
import name.NicolasCoutureGrenier.Music.PCS12Relations.CloseIVs;
import name.NicolasCoutureGrenier.Music.PCS12Relations.CommonNotesAtLeast;
import name.NicolasCoutureGrenier.Music.PCS12Relations.Different;
import name.NicolasCoutureGrenier.Music.PCS12Relations.IVEQRotOrRev;

import javax.swing.SpinnerNumberModel;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class ChordGraphExplorer {

  private JFrame frmChordPleasure;
  private JLabel lblStartIV = new JLabel("");
  private JLabel lblSuccIV = new JLabel("");
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          ChordGraphExplorer window = new ChordGraphExplorer();
          window.frmChordPleasure.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  DiGraph<PCS12> d;

  /**
   * Create the application.
   */
  public ChordGraphExplorer() {
    initialize();
  }

  JComboBox<String> cbxStart = new JComboBox<>();

  JComboBox<String> cbxScale;
  JSpinner spinner_1 = new JSpinner(new SpinnerNumberModel(4, 1, 12, 1));
  JLabel lblStartC = new JLabel("");
  JLabel lblSuccC = new JLabel("");
  private void fillChords() {
    
    TreeSet<PCS12> t = new TreeSet<PCS12>();
    t.addAll(PCS12.getChords());
    CollectionUtils.filter(t, new SizeIs((int) spinner_1.getValue()));
    CollectionUtils.filter(t, Predicates.and(new SubsetOf(PCS12.parse(cbxScale.getSelectedItem().toString())), new Consonant()));
    d = new DiGraph<PCS12>(t, Relation.and(new Different(), Relation.and(Relation.or(new CloseIVs(), new IVEQRotOrRev()), new CommonNotesAtLeast(1))));
    CollectionUtils.filter(t, new Predicate<PCS12> () {

      @Override
      public boolean apply(PCS12 input) {
        return d.getSuccessorCount(input) > 0;
      }
      
    });
    String[] s = new String[t.size()];
    int k = 0;
    for (PCS12 x : t) {
      s[k++] = x.toString();
    }
    Arrays.sort(s);

    cbxStart.setModel(new DefaultComboBoxModel<String>(s));
    fillSuccessors();
    updateLabels();
  }

  JComboBox<String> cbxSuccessors = new JComboBox<String>();

  private void fillSuccessors() {
    PCS12 start = PCS12.parse(cbxStart.getSelectedItem().toString());
    ArrayList<String> successors = new ArrayList<String>(
        this.d.getSuccessors(start).stream().map(c -> c.toString()).collect(Collectors.toList()));
    successors.sort((a, b) -> a.compareTo(b));

    cbxSuccessors.setModel(new DefaultComboBoxModel<String>(successors.toArray(new String[0])));
    
    
  }

  private Synthesizer midiSynth = null;

  private void initMidiSynth() {
    try {
      midiSynth = MidiSystem.getSynthesizer();
      Instrument[] instr = midiSynth.getDefaultSoundbank().getInstruments();


      midiSynth.loadInstrument(instr[0]);// load an instrument
    } catch (MidiUnavailableException e) {

    }
  }
  private void updateLabels() {
    PCS12 s = PCS12.parse(cbxScale.getSelectedItem().toString());
    
    PCS12 c = PCS12.parse(cbxStart.getSelectedItem().toString());
    lblStartIV.setText(c.getIntervalVector().toString());
    lblStartC.setText(PCS12.identify(s.minus(c)).toString());
    pitches_start.setText(c.asSequence().toString());
    PCS12 c1 = PCS12.parse(cbxSuccessors.getSelectedItem().toString());
    lblSuccIV.setText(c1.getIntervalVector().toString());
    lblSuccC.setText(PCS12.identify(s.minus(c1)).toString());
    pitches_end.setText(c1.asSequence().toString());
  }
  private void playChord(PCS12 chord, int durInMs) {
    if (chord == null) return;
    Utils.copyStringToClipboard(chord.toString());
    Thread t = new Thread(new Runnable() {
      public void run() {
        try {

          if (midiSynth == null) {
            return;
          }
          MidiChannel[] mChannels = midiSynth.getChannels();
          Sequence s = chord.asSequence();


          for (int i = 0; i < s.size(); i++) {
            int note = 48 + s.get(i);

            mChannels[0].noteOn(note, 100);
            Thread.sleep(durInMs);
            mChannels[0].noteOff(note);
            
          }
          Thread.sleep(durInMs);
          for (int i = 0; i < s.size(); i++) {
            int note = 48 + s.get(i);
            mChannels[0].noteOn(note, 100); 
          }
          Thread.sleep(durInMs);
          for (int i = 0; i < s.size(); i++) {
            int note = 48 + s.get(i);

            mChannels[0].noteOff(note);
          }

        } catch (InterruptedException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
    });
    t.start();

  }

  private int play_len = 250;
  private JLabel pitches_start = new JLabel("");;
  private JLabel pitches_end = new JLabel("");
  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    this.initMidiSynth();
    try {
      this.midiSynth.open();
    } catch (MidiUnavailableException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    String[] cs = PCS12.getChordDict().keySet().toArray(new String[0]);
    Arrays.sort(cs);

    cbxScale = new JComboBox<String>(new DefaultComboBoxModel<String>(cs));
    frmChordPleasure = new JFrame();
    frmChordPleasure.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        midiSynth.close();
      }
    });
    frmChordPleasure.setTitle("PCS12 Graph Explorer");
    frmChordPleasure.getContentPane().setBackground(Color.DARK_GRAY);
    frmChordPleasure.setBounds(100, 100, 641, 160);
    frmChordPleasure.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    cbxScale.setSelectedIndex(Arrays.asList(cs).indexOf("07-43.11"));
    fillChords();


    JLabel lblNewLabel = new JLabel("Scale:");
    lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    lblNewLabel.setForeground(Color.WHITE);

    JLabel lblStart = new JLabel("Start:");
    lblStart.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        
        playChord(PCS12.parse(cbxStart.getSelectedItem().toString()), play_len);
        
      }
    });
    lblStart.setHorizontalAlignment(SwingConstants.RIGHT);
    lblStart.setForeground(Color.WHITE);

    JLabel lblK = new JLabel("k:");
    lblK.setHorizontalAlignment(SwingConstants.RIGHT);
    lblK.setForeground(Color.WHITE);
    spinner_1.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        fillChords();
        updateLabels();
      }
    });

    JLabel lblSuccessor = new JLabel("Successor:");
    lblSuccessor.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        playChord(PCS12.parse(cbxSuccessors.getSelectedItem().toString()), play_len);
      }
    });
    lblSuccessor.setHorizontalAlignment(SwingConstants.RIGHT);
    lblSuccessor.setForeground(Color.WHITE);


    cbxStart.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fillSuccessors();
        
        updateLabels();
        
      }
    });
    cbxSuccessors.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        updateLabels();
      }
    });
    lblStartIV.setText(PCS12.parse(cbxStart.getSelectedItem().toString()).getIntervalVector().toString());
    lblStartIV.setForeground(Color.WHITE);
    
    
    lblSuccIV.setForeground(Color.WHITE);
    lblSuccIV.setText(PCS12.parse(cbxSuccessors.getSelectedItem().toString()).getIntervalVector().toString());
    lblStartC.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        playChord(PCS12.parse(lblStartC.getText()), play_len);
      }
    });
    lblSuccC.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        playChord(PCS12.parse(lblSuccC.getText()), play_len);
      }
    });
    lblStartC.setForeground(Color.WHITE);
    
    
    lblSuccC.setForeground(Color.WHITE);
    
    pitches_start.setForeground(new Color(255, 255, 240));
    
    pitches_end.setForeground(new Color(255, 255, 240));
    GroupLayout groupLayout = new GroupLayout(frmChordPleasure.getContentPane());
    groupLayout.setHorizontalGroup(
      groupLayout.createParallelGroup(Alignment.TRAILING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(cbxScale, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(lblK, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addComponent(lblStart, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addComponent(cbxStart, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
              .addGap(18)
              .addComponent(lblSuccessor, GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE))
            .addComponent(lblStartIV, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblStartC, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
            .addComponent(pitches_start, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE))
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(groupLayout.createSequentialGroup()
              .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(pitches_end, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
                .addComponent(lblSuccC, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                .addComponent(lblSuccIV, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))
              .addGap(99))
            .addGroup(groupLayout.createSequentialGroup()
              .addPreferredGap(ComponentPlacement.RELATED)
              .addComponent(cbxSuccessors, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
              .addContainerGap())))
    );
    groupLayout.setVerticalGroup(
      groupLayout.createParallelGroup(Alignment.LEADING)
        .addGroup(groupLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblNewLabel)
            .addComponent(cbxScale, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblK)
            .addComponent(spinner_1, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblStart)
            .addComponent(cbxStart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblSuccessor)
            .addComponent(cbxSuccessors, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.UNRELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(lblStartIV, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblSuccIV, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(lblSuccC, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
            .addComponent(lblStartC, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE))
          .addPreferredGap(ComponentPlacement.RELATED)
          .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
            .addComponent(pitches_end, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
            .addComponent(pitches_start, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))
          .addContainerGap(20, Short.MAX_VALUE))
    );
    cbxScale.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fillChords();
      }
    });
    frmChordPleasure.getContentPane().setLayout(groupLayout);
  }
}
