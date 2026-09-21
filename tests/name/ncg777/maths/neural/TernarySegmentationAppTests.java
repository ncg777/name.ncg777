package name.ncg777.maths.neural;

import static org.junit.Assert.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import org.junit.Test;
import name.ncg777.maths.neural.apps.TernarySegmentationPanel;

public class TernarySegmentationAppTests {
  private static java.util.List<Component> children(Container parent) {
    java.util.List<Component> all = new ArrayList<>();
    for (Component c : parent.getComponents()) { all.add(c); if (c instanceof Container p) all.addAll(children(p)); }
    return all;
  }
  private static JButton button(java.util.List<Component> components, String text) {
    return (JButton) components.stream().filter(c -> c instanceof JButton b && b.getText().equals(text)).findFirst().orElseThrow();
  }
  private static void runWorker(java.util.List<Component> components, String action) throws Exception {
    CountDownLatch done = new CountDownLatch(1);
    SwingUtilities.invokeAndWait(() -> {
      JButton cancel = button(components, "Cancel job");
      cancel.addPropertyChangeListener("enabled", e -> { if (Boolean.FALSE.equals(e.getNewValue())) done.countDown(); });
      button(components, action).doClick(); assertTrue(cancel.isEnabled()); assertFalse(button(components, action).isEnabled());
    });
    assertTrue("Background operation completed", done.await(15, TimeUnit.SECONDS));
  }
  @Test public void trainingEvaluationAndSceneChangesUseTheSwingWorkerWorkflow() throws Exception {
    java.util.List<Component> components = new ArrayList<>();
    SwingUtilities.invokeAndWait(() -> components.addAll(children(new TernarySegmentationPanel())));
    JTable table = (JTable) components.stream().filter(c -> c instanceof JTable).findFirst().orElseThrow();
    runWorker(components, "Train 8×8 model");
    SwingUtilities.invokeAndWait(() -> assertEquals(3, table.getRowCount()));
    runWorker(components, "Evaluate 18 scenes");
    SwingUtilities.invokeAndWait(() -> {
      assertEquals(9, table.getRowCount());
      JComboBox<?> shapes = (JComboBox<?>) components.stream().filter(c -> c instanceof JComboBox<?>).findFirst().orElseThrow();
      shapes.setSelectedItem(TernarySegmentation.Shape.BORDER_CUT); assertEquals(0, table.getRowCount());
    });
    runWorker(components, "New scene");
    SwingUtilities.invokeAndWait(() -> { assertEquals(3, table.getRowCount()); assertEquals("1/1", table.getValueAt(0, 6)); });
  }
}
