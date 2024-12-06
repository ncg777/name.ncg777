package name.ncg777;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import java.awt.GridLayout;
import java.io.File;
import java.net.URISyntaxException;

public class MainMenuGUI {
  public static void main(String[] args) {
    JFrame mainFrame = new JFrame("Main Menu");
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.setSize(500, 800);
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(0, 1)); // Dynamic vertical layout
   
    addAppButton(panel, "maths.apps.MixedRadix",name.ncg777.maths.apps.MixedRadix.class);
    addAppButton(panel, "maths.apps.NecklaceGenerator",name.ncg777.maths.apps.NecklaceGenerator.class);
    addAppButton(panel, "maths.apps.Permutator",name.ncg777.maths.apps.Permutator.class);
    addAppButton(panel, "maths.music.apps.KernelEvaluator", name.ncg777.maths.music.apps.kernelEvaluator.Application.class);
    addAppButton(panel, "maths.music.apps.ModularArithmeticSequencer",name.ncg777.maths.music.apps.ModularArithmeticSequencer.class);
    addAppButton(panel, "maths.music.apps",name.ncg777.maths.music.apps.RexRandomizer.class);
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
    addAppButton(panel, "maths.music.pcs12.apps.RandomWalker1",name.ncg777.maths.music.pcs12.apps.RandomWalker1.class);
    addAppButton(panel, "maths.music.pcs12.apps.Rotator",name.ncg777.maths.music.pcs12.apps.Rotator.class);
    addAppButton(panel, "maths.music.pcs12.apps.SequenceMerger",name.ncg777.maths.music.pcs12.apps.SequenceMerger.class);
    addAppButton(panel, "maths.music.pcs12.apps.SequenceVisualizer",name.ncg777.maths.music.pcs12.apps.SequenceVisualizer.class);
    addAppButton(panel, "maths.music.pcs12.apps.Sorter",name.ncg777.maths.music.pcs12.apps.Sorter.class);
    addAppButton(panel, "maths.music.pcs12.apps.Walker",name.ncg777.maths.music.pcs12.apps.Walker.class);
    addAppButton(panel, "maths.sentences.apps.BitCounter",name.ncg777.maths.sentences.apps.BitCounter.class);
    addAppButton(panel, "maths.sentences.apps.Diluter",name.ncg777.maths.sentences.apps.Diluter.class);
    addAppButton(panel, "maths.sentences.apps.HexadecimalWordDivider",name.ncg777.maths.sentences.apps.HexadecimalWordDivider.class);
    addAppButton(panel, "maths.sentences.apps.Permutator",name.ncg777.maths.sentences.apps.Permutator.class);
    addAppButton(panel, "maths.sentences.apps.Pulsations",name.ncg777.maths.sentences.apps.Pulsations.class);
    addAppButton(panel, "maths.sentences.apps.ScaleModulo",name.ncg777.maths.sentences.apps.ScaleModulo.class);
    addAppButton(panel, "maths.sentences.apps.SequenceGenerator",name.ncg777.maths.sentences.apps.SequenceGenerator.class);
    addAppButton(panel, "maths.sentences.apps.Sequencer",name.ncg777.maths.sentences.apps.Sequencer.class);
    addAppButton(panel, "maths.sentences.apps.TetragraphMatrixGenerator",name.ncg777.maths.sentences.apps.TetragraphMatrixGenerator.class);
    addAppButton(panel, "maths.sentences.apps.TetragraphMerger",name.ncg777.maths.sentences.apps.TetragraphMerger.class);
    addAppButton(panel, "maths.sentences.apps.TetragraphPartitioner",name.ncg777.maths.sentences.apps.TetragraphPartitioner.class);
    addAppButton(panel, "maths.sequences.apps.Adder",name.ncg777.maths.sequences.apps.Adder.class);
    addAppButton(panel, "maths.sequences.apps.PseudoBase",name.ncg777.maths.sequences.apps.PseudoBase.class);
    addAppButton(panel, "maths.sequences.apps.Range",name.ncg777.maths.sequences.apps.Range.class);
    addAppButton(panel, "maths.sequences.apps.SequenceCalc",name.ncg777.maths.sequences.apps.SequenceCalc.class);
    addAppButton(panel, "maths.sequences.apps.SequenceConvolver",name.ncg777.maths.sequences.apps.SequenceConvolver.class);
    addAppButton(panel, "maths.sequences.apps.SequenceMap",name.ncg777.maths.sequences.apps.SequenceMap.class);
    addAppButton(panel, "maths.sequences.apps.SequenceMerger",name.ncg777.maths.sequences.apps.SequenceMerger.class);
    addAppButton(panel, "maths.sequences.apps.SequencePermutate",name.ncg777.maths.sequences.apps.SequencePermutate.class);
    addAppButton(panel, "maths.sequences.apps.SequenceScaleModuloPermutation",name.ncg777.maths.sequences.apps.SequenceScaleModuloPermutation.class);
    addAppButton(panel, "maths.sequences.apps.SequenceWrapper",name.ncg777.maths.sequences.apps.SequenceWrapper.class);
    addAppButton(panel, "maths.sequences.apps.SmoothArticulator",name.ncg777.maths.sequences.apps.SmoothArticulator.class);
    addAppButton(panel, "maths.sequences.apps.Tri",name.ncg777.maths.sequences.apps.Tri.class);
    addAppButton(panel, "maths.apps.LatticePath",name.ncg777.maths.apps.LatticePath.class);
    
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
    button.setHorizontalAlignment(SwingConstants.LEFT);
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
