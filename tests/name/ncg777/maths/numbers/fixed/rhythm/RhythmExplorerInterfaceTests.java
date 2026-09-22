package name.ncg777.maths.numbers.fixed.rhythm;

import static org.junit.Assert.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.junit.Test;
import picocli.CommandLine;
import com.fasterxml.jackson.databind.ObjectMapper;
import name.ncg777.maths.numbers.fixed.rhythm.apps.*;
import name.ncg777.maths.numbers.fixed.rhythm.exploration.*;

public class RhythmExplorerInterfaceTests {
  private record Run(int code, String out, String err) {}
  private Run cli(String... args) {
    StringWriter out = new StringWriter(), err = new StringWriter();
    CommandLine command = new CommandLine(new RhythmExplorerApp());
    command.setOut(new PrintWriter(out)); command.setErr(new PrintWriter(err));
    return new Run(command.execute(args), out.toString(), err.toString());
  }
  @Test public void cliAndEngineReturnIdenticalSeededSteps() throws Exception {
    Run run = cli("evolve", "--start", "9249", "--where", "HITS(6)", "--iterations", "12");
    assertEquals(run.err(), 0, run.code());
    var settings = new RhythmExplorer.Settings("HITS(6)", "TRUE", 4, 1, RhythmMask.Erasure.SCATTERED, java.util.Set.of(), 512, 100000, 777,
        new RhythmExplorer.Selection(2, -1, .15, 8, 0, -1, 4));
    var session = new RhythmExplorer.Session("1001001001001001", settings);
    for (String line : run.out().lines().toList()) assertEquals(session.next(), new ObjectMapper().readValue(line, RhythmExplorer.Step.class));
    assertEquals(12, run.out().lines().count());
    Run octal = cli("complete", "--cipher", "Octal", "--mask", "4?2?", "--where", "EUCLIDEAN(5)", "--limit", "10000");
    assertEquals(octal.err(), 0, octal.code());
    for (String line : octal.out().lines().toList()) assertTrue(line.matches("4[0-7]2[0-7]"));
    assertEquals(1, cli("complete", "--mask", "0000", "--where", "NONEMPTY").code());
    assertEquals(3, cli("complete", "--mask", "????", "--where", "FALSE", "--budget", "1").code());
    assertNotEquals(0, cli("complete", "--mask", "????", "--format", "bogus").code());
    assertTrue(cli("predicates").out().contains("EUCLIDEAN(n)"));
  }
  private static List<Component> children(Component root) {
    List<Component> result = new ArrayList<>(); result.add(root);
    if (root instanceof Container c) for (Component child : c.getComponents()) result.addAll(children(child));
    return result;
  }
  private static JButton button(Component panel, String text) {
    return children(panel).stream().filter(c -> c instanceof JButton b && b.getText().equals(text)).map(c -> (JButton)c).findFirst().orElseThrow();
  }
  private static void layout(Component c) {
    if (c instanceof Container p) { p.doLayout(); for (Component child : p.getComponents()) layout(child); }
  }
  @Test public void builderGroupsWholeExpressionAndValidates() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      var builder = new RhythmClauseBuilder("HITS(3) OR EUCLIDEAN");
      button(builder, "Group selection").doClick();
      assertEquals("(HITS(3) OR EUCLIDEAN)", builder.clause()); builder.validateClause(16);
      builder.setClause("ORDINAL(3)"); assertThrows(IllegalArgumentException.class, () -> builder.validateClause(16));
    });
  }
  @Test public void guiCompletesReusesEvolvesAndRenders() throws Exception {
    RhythmExplorerGUI[] panel = new RhythmExplorerGUI[1];
    CountDownLatch completed = new CountDownLatch(1);
    SwingUtilities.invokeAndWait(() -> {
      panel[0] = new RhythmExplorerGUI(); panel[0].setSize(1280, 720); layout(panel[0]);
      var mask = children(panel[0]).stream().filter(c -> "Rhythm mask".equals(c.getName())).map(c -> (JTextField)c).findFirst().orElseThrow();
      mask.setText("9?4?");
      JButton stop = button(panel[0], "Stop generation");
      stop.addPropertyChangeListener("enabled", e -> { if (Boolean.FALSE.equals(e.getNewValue())) completed.countDown(); });
      button(panel[0], "Complete").doClick();
    });
    assertTrue(completed.await(20, TimeUnit.SECONDS));
    CountDownLatch evolved = new CountDownLatch(1);
    SwingUtilities.invokeAndWait(() -> {
      JTable table = children(panel[0]).stream().filter(c -> c instanceof JTable).map(c -> (JTable)c).findFirst().orElseThrow();
      assertTrue(table.getRowCount() > 0);
      table.setRowSelectionInterval(0, 0); button(panel[0], "Use selected").doClick();
      button(panel[0], "Stop generation").addPropertyChangeListener("enabled", e -> { if (Boolean.FALSE.equals(e.getNewValue())) evolved.countDown(); });
      button(panel[0], "Evolve batch").doClick();
    });
    assertTrue(evolved.await(20, TimeUnit.SECONDS));
    SwingUtilities.invokeAndWait(() -> {
      JTable table = children(panel[0]).stream().filter(c -> c instanceof JTable).map(c -> (JTable)c).findFirst().orElseThrow();
      assertEquals(64, table.getRowCount());
      try {
        String destination = System.getProperty("rhythm.preview");
        if (destination != null) {
          layout(panel[0]); BufferedImage image = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
          Graphics2D g = image.createGraphics(); panel[0].paint(g); g.dispose(); ImageIO.write(image, "png", new File(destination));
        }
      } catch (IOException e) { throw new UncheckedIOException(e); }
      panel[0].close();
    });
  }
  @Test public void continuousGenerationStopsAndRetainsResults() throws Exception {
    RhythmExplorerGUI[] panel = new RhythmExplorerGUI[1]; CountDownLatch stopped = new CountDownLatch(1);
    SwingUtilities.invokeAndWait(() -> {
      panel[0] = new RhythmExplorerGUI();
      JButton stop = button(panel[0], "Stop generation");
      stop.addPropertyChangeListener("enabled", e -> { if (Boolean.FALSE.equals(e.getNewValue())) stopped.countDown(); });
      JTable table = children(panel[0]).stream().filter(c -> c instanceof JTable).map(c -> (JTable)c).findFirst().orElseThrow();
      table.getModel().addTableModelListener(e -> { if (table.getRowCount() >= 2) stop.doClick(); });
      button(panel[0], "Run continuously").doClick();
    });
    assertTrue(stopped.await(20, TimeUnit.SECONDS));
    SwingUtilities.invokeAndWait(() -> {
      JTable table = children(panel[0]).stream().filter(c -> c instanceof JTable).map(c -> (JTable)c).findFirst().orElseThrow();
      assertTrue(table.getRowCount() >= 2); panel[0].close();
    });
  }
}
