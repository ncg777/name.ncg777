package name.ncg777;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.GridLayout;
import java.io.File;
import java.net.URISyntaxException;

public class MainMenuGUI {
  public static void main(String[] args) {
    // Create the main frame
    JFrame mainFrame = new JFrame("Main Menu");
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setSize(800, 600);
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(0, 1)); // Dynamic vertical layout

    addAppButton(panel, "maths.applications.LatticePath", name.ncg777.maths.applications.LatticePath.class);
    addAppButton(panel, "maths.applications.MixedRadix", name.ncg777.maths.applications.MixedRadix.class);
    addAppButton(panel, "maths.applications.NecklaceGenerator", name.ncg777.maths.applications.NecklaceGenerator.class);
    addAppButton(panel, "maths.applicationssequences.PseudoBase", name.ncg777.maths.applications.sequences.PseudoBase.class);
    addAppButton(panel, "maths.applications.ScaleModulo", name.ncg777.maths.applications.ScaleModulo.class);
    addAppButton(panel, "maths.applications.sequences.Adder", name.ncg777.maths.applications.sequences.Adder.class);
    addAppButton(panel, "maths.applications.sequences.Range", name.ncg777.maths.applications.sequences.Range.class);
    addAppButton(panel, "maths.applications.sequences.SequenceCalc", name.ncg777.maths.applications.sequences.SequenceCalc.class);
    addAppButton(panel, "maths.applications.sequences.SequenceConvolver", name.ncg777.maths.applications.sequences.SequenceConvolver.class);
    addAppButton(panel, "maths.applications.sequences.SequenceMap", name.ncg777.maths.applications.sequences.SequenceMap.class);
    addAppButton(panel, "maths.applications.sequences.SequenceMerger", name.ncg777.maths.applications.sequences.SequenceMerger.class);
    addAppButton(panel, "maths.applications.sequences.SequencePermutate", name.ncg777.maths.applications.sequences.SequencePermutate.class);
    addAppButton(panel, "maths.applications.sequences.SequenceScaleModuloPermutation", name.ncg777.maths.applications.sequences.SequenceScaleModuloPermutation.class);
    addAppButton(panel, "maths.applications.sequences.SequenceWrapper", name.ncg777.maths.applications.sequences.SequenceWrapper.class);
    addAppButton(panel, "maths.applications.sequences.SmoothArticulator", name.ncg777.maths.applications.sequences.SmoothArticulator.class);
    addAppButton(panel, "maths.applications.sequences.Tri", name.ncg777.maths.applications.sequences.Tri.class);
    addAppButton(panel, "maths.applications.words.BitCounter", name.ncg777.maths.applications.words.BitCounter.class);
    addAppButton(panel, "maths.applications.words.Diluter", name.ncg777.maths.applications.words.Diluter.class);
    addAppButton(panel, "maths.applications.words.HexadecimalWordDivider", name.ncg777.maths.applications.words.HexadecimalWordDivider.class);
    addAppButton(panel, "maths.applications.words.Permutator", name.ncg777.maths.applications.words.Permutator.class);
    addAppButton(panel, "maths.applications.words.Permutator", name.ncg777.maths.applications.words.Permutator.class);
    addAppButton(panel, "maths.applications.words.Pulsations", name.ncg777.maths.applications.words.Pulsations.class);
    addAppButton(panel, "maths.applications.words.SequenceGenerator", name.ncg777.maths.applications.words.SequenceGenerator.class);
    addAppButton(panel, "maths.applications.words.Sequencer", name.ncg777.maths.applications.words.Sequencer.class);
    addAppButton(panel, "music.applications.ModularArithmeticSequencer", name.ncg777.music.applications.ModularArithmeticSequencer.class);
    addAppButton(panel, "music.applications.SeqGenBySegments", name.ncg777.music.applications.SeqGenBySegments.class);
    addAppButton(panel, "music.applications.SeqGenContourFollow", name.ncg777.music.applications.SeqGenContourFollow.class);
    addAppButton(panel, "music.applications.SeqGenFS", name.ncg777.music.applications.SeqGenFS.class);
    addAppButton(panel, "music.applications.SeqGenFixedSum", name.ncg777.music.applications.SeqGenFixedSum.class);
    addAppButton(panel, "music.applications.SequenceConvolverAbsoluteTime", name.ncg777.music.applications.SequenceConvolverAbsoluteTime.class);
    addAppButton(panel, "music.applications.XORCircularConvolver", name.ncg777.music.applications.XORCircularConvolver.class);
    addAppButton(panel, "music.applications.XRSequenceGenerator", name.ncg777.music.applications.XRSequenceGenerator.class);
    addAppButton(panel, "music.applications.pitchClassSet12.Combiner", name.ncg777.music.applications.pitchClassSet12.Combiner.class);
    addAppButton(panel, "music.applications.pitchClassSet12.GraphExplorer", name.ncg777.music.applications.pitchClassSet12.GraphExplorer.class);
    addAppButton(panel, "music.applications.pitchClassSet12.Identifier", name.ncg777.music.applications.pitchClassSet12.Identifier.class);
    addAppButton(panel, "music.applications.pitchClassSet12.IntersectionAndUnion", name.ncg777.music.applications.pitchClassSet12.IntersectionAndUnion.class);
    addAppButton(panel, "music.applications.pitchClassSet12.KComplexExplorer", name.ncg777.music.applications.pitchClassSet12.KComplexExplorer.class);
    addAppButton(panel, "music.applications.pitchClassSet12.MatrixGenerator", name.ncg777.music.applications.pitchClassSet12.MatrixGenerator.class);
    addAppButton(panel, "music.applications.pitchClassSet12.Permutator", name.ncg777.music.applications.pitchClassSet12.Permutator.class);
    addAppButton(panel, "music.applications.pitchClassSet12.Permutator", name.ncg777.music.applications.pitchClassSet12.Permutator.class);
    addAppButton(panel, "music.applications.pitchClassSet12.RandomWalker1", name.ncg777.music.applications.pitchClassSet12.RandomWalker1.class);
    addAppButton(panel, "music.applications.pitchClassSet12.Rotator", name.ncg777.music.applications.pitchClassSet12.Rotator.class);
    addAppButton(panel, "music.applications.pitchClassSet12.SequenceMerger", name.ncg777.music.applications.pitchClassSet12.SequenceMerger.class);
    addAppButton(panel, "music.applications.pitchClassSet12.SequenceVisualizer", name.ncg777.music.applications.pitchClassSet12.SequenceVisualizer.class);
    addAppButton(panel, "music.applications.pitchClassSet12.Sorter", name.ncg777.music.applications.pitchClassSet12.Sorter.class);
    addAppButton(panel, "music.applications.pitchClassSet12.Walker", name.ncg777.music.applications.pitchClassSet12.Walker.class);
    addAppButton(panel, "music.applications.x2mid", name.ncg777.music.applications.x2mid.class);

    JScrollPane scrollPane = new JScrollPane(panel);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    // Add the scroll pane to the main frame
    mainFrame.add(scrollPane);

    // Set frame visibility
    mainFrame.setVisible(true);
  }

  private static void addAppButton(JPanel panel, String appName, Class<?> appClass) {
    JButton button = new JButton(appName);
    button.addActionListener(e -> {
      openApp(appClass);
    });
    panel.add(button);
  }

  private static void openApp(Class<?> appClass) {
    try {
      var appClassName = appClass.getCanonicalName();
      String currentJarPath = getCurrentJarPath();

      String classpath =
          currentJarPath + File.pathSeparator + System.getProperty("java.class.path");

      ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", classpath, appClassName);
      processBuilder.start();

    } catch (Exception ex) {
      System.err.println("Error opening application: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  private static String getCurrentJarPath() throws URISyntaxException {
    String jarPath = MainMenuGUI.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
    
    return jarPath;
  }
}
