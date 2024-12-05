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
    mainFrame.setSize(500, 800);
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(0, 1)); // Dynamic vertical layout

    addAppButton(panel, "maths.apps.LatticePath",name.ncg777.maths.apps.LatticePath.class);
    addAppButton(panel, "maths.apps.MixedRadix",name.ncg777.maths.apps.MixedRadix.class);
    addAppButton(panel, "maths.apps.NecklaceGenerator",name.ncg777.maths.apps.NecklaceGenerator.class);
    addAppButton(panel, "maths.apps.sequences.Adder",name.ncg777.maths.apps.sequences.Adder.class);
    addAppButton(panel, "maths.apps.sequences.PseudoBase",name.ncg777.maths.apps.sequences.PseudoBase.class);
    addAppButton(panel, "maths.apps.sequences.Range",name.ncg777.maths.apps.sequences.Range.class);
    addAppButton(panel, "maths.apps.sequences.SequenceCalc",name.ncg777.maths.apps.sequences.SequenceCalc.class);
    addAppButton(panel, "maths.apps.sequences.SequenceConvolver",name.ncg777.maths.apps.sequences.SequenceConvolver.class);
    addAppButton(panel, "maths.apps.sequences.SequenceMap",name.ncg777.maths.apps.sequences.SequenceMap.class);
    addAppButton(panel, "maths.apps.sequences.SequenceMerger",name.ncg777.maths.apps.sequences.SequenceMerger.class);
    addAppButton(panel, "maths.apps.sequences.SequencePermutate",name.ncg777.maths.apps.sequences.SequencePermutate.class);
    addAppButton(panel, "maths.apps.sequences.SequenceScaleModuloPermutation",name.ncg777.maths.apps.sequences.SequenceScaleModuloPermutation.class);
    addAppButton(panel, "maths.apps.sequences.SequenceWrapper",name.ncg777.maths.apps.sequences.SequenceWrapper.class);
    addAppButton(panel, "maths.apps.sequences.SmoothArticulator",name.ncg777.maths.apps.sequences.SmoothArticulator.class);
    addAppButton(panel, "maths.apps.sequences.Tri",name.ncg777.maths.apps.sequences.Tri.class);
    addAppButton(panel, "maths.apps.words.BitCounter",name.ncg777.maths.apps.words.BitCounter.class);
    addAppButton(panel, "maths.apps.words.Diluter",name.ncg777.maths.apps.words.Diluter.class);
    addAppButton(panel, "maths.apps.words.HexadecimalWordDivider",name.ncg777.maths.apps.words.HexadecimalWordDivider.class);
    addAppButton(panel, "maths.apps.words.Permutator",name.ncg777.maths.apps.words.Permutator.class);
    addAppButton(panel, "maths.apps.words.Permutator",name.ncg777.maths.apps.words.Permutator.class);
    addAppButton(panel, "maths.apps.words.Pulsations",name.ncg777.maths.apps.words.Pulsations.class);
    addAppButton(panel, "maths.apps.words.SequenceGenerator",name.ncg777.maths.apps.words.SequenceGenerator.class);
    addAppButton(panel, "maths.apps.words.Sequencer",name.ncg777.maths.apps.words.Sequencer.class);
    addAppButton(panel, "maths.music.apps.ModularArithmeticSequencer",name.ncg777.maths.music.apps.ModularArithmeticSequencer.class);
    addAppButton(panel, "maths.music.apps.SeqGenBySegments",name.ncg777.maths.music.apps.SeqGenBySegments.class);
    addAppButton(panel, "maths.music.apps.SeqGenContourFollow",name.ncg777.maths.music.apps.SeqGenContourFollow.class);
    addAppButton(panel, "maths.music.apps.SeqGenFS",name.ncg777.maths.music.apps.SeqGenFS.class);
    addAppButton(panel, "maths.music.apps.SeqGenFixedSum",name.ncg777.maths.music.apps.SeqGenFixedSum.class);
    addAppButton(panel, "maths.music.apps.SequenceConvolverAbsoluteTime",name.ncg777.maths.music.apps.SequenceConvolverAbsoluteTime.class);
    addAppButton(panel, "maths.music.apps.XORCircularConvolver",name.ncg777.maths.music.apps.XORCircularConvolver.class);
    addAppButton(panel, "maths.music.apps.XRSequenceGenerator",name.ncg777.maths.music.apps.XRSequenceGenerator.class);
    addAppButton(panel, "maths.music.apps.x2mid",name.ncg777.maths.music.apps.x2mid.class);
    addAppButton(panel, "maths.music.pcs12.apps.Combiner",name.ncg777.maths.music.pcs12.apps.Combiner.class);
    addAppButton(panel, "maths.music.pcs12.apps.GraphExplorer",name.ncg777.maths.music.pcs12.apps.GraphExplorer.class);
    addAppButton(panel, "maths.music.pcs12.apps.Identifier",name.ncg777.maths.music.pcs12.apps.Identifier.class);
    addAppButton(panel, "maths.music.pcs12.apps.IntersectionAndUnion",name.ncg777.maths.music.pcs12.apps.IntersectionAndUnion.class);
    addAppButton(panel, "maths.music.pcs12.apps.KComplexExplorer",name.ncg777.maths.music.pcs12.apps.KComplexExplorer.class);
    addAppButton(panel, "maths.music.pcs12.apps.MatrixGenerator",name.ncg777.maths.music.pcs12.apps.MatrixGenerator.class);
    addAppButton(panel, "maths.music.pcs12.apps.Permutator",name.ncg777.maths.music.pcs12.apps.Permutator.class);
    addAppButton(panel, "maths.music.pcs12.apps.Permutator",name.ncg777.maths.music.pcs12.apps.Permutator.class);
    addAppButton(panel, "maths.music.pcs12.apps.RandomWalker1",name.ncg777.maths.music.pcs12.apps.RandomWalker1.class);
    addAppButton(panel, "maths.music.pcs12.apps.Rotator",name.ncg777.maths.music.pcs12.apps.Rotator.class);
    addAppButton(panel, "maths.music.pcs12.apps.SequenceMerger",name.ncg777.maths.music.pcs12.apps.SequenceMerger.class);
    addAppButton(panel, "maths.music.pcs12.apps.SequenceVisualizer",name.ncg777.maths.music.pcs12.apps.SequenceVisualizer.class);
    addAppButton(panel, "maths.music.pcs12.apps.Sorter",name.ncg777.maths.music.pcs12.apps.Sorter.class);
    addAppButton(panel, "maths.music.pcs12.apps.Walker",name.ncg777.maths.music.pcs12.apps.Walker.class);
    
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
