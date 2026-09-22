package name.ncg777.maths.neural;

import static org.junit.Assert.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import org.junit.Test;
import name.ncg777.maths.neural.apps.TernaryEmbeddingPanel;

public class TernaryEmbeddingAppTests {
  private static java.util.List<Component> children(Container p) {
    java.util.List<Component> result = new ArrayList<>();
    for (Component c : p.getComponents()) { result.add(c); if (c instanceof Container cc) result.addAll(children(cc)); } return result;
  }
  @Test public void fitInspectEditAndResetTheDiscreteCode() throws Exception {
    java.util.List<Component> components = new ArrayList<>(); CountDownLatch done = new CountDownLatch(1);
    SwingUtilities.invokeAndWait(() -> {
      components.addAll(children(new TernaryEmbeddingPanel()));
      JButton train = button(components, "Train all four code sizes"), cancel = button(components, "Cancel training");
      cancel.addPropertyChangeListener("enabled", e -> { if (Boolean.FALSE.equals(e.getNewValue())) done.countDown(); });
      train.doClick(); assertFalse(train.isEnabled()); assertTrue(cancel.isEnabled());
    });
    assertTrue(done.await(15, TimeUnit.SECONDS));
    SwingUtilities.invokeAndWait(() -> {
      JTable table = (JTable) components.stream().filter(c -> c instanceof JTable).findFirst().orElseThrow(); assertEquals(14, table.getRowCount());
      JComboBox<?> width = (JComboBox<?>) components.stream().filter(c -> c instanceof JComboBox<?> combo && combo.getItemAt(0) instanceof Integer).findFirst().orElseThrow(); width.setSelectedItem(8);
      JLabel detail = (JLabel) components.stream().filter(c -> c instanceof JLabel l && l.getText() != null && l.getText().startsWith("<html>8 trits:")).findFirst().orElseThrow();
      String original = detail.getText();
      JComponent code = (JComponent) components.stream().filter(c -> c.getClass().getSimpleName().equals("CodeCanvas")).findFirst().orElseThrow();
      code.setSize(240, 54); code.dispatchEvent(new MouseEvent(code, MouseEvent.MOUSE_PRESSED, 0, MouseEvent.BUTTON1_DOWN_MASK, 15, 20, 1, false, MouseEvent.BUTTON1));
      assertNotEquals(original, detail.getText()); button(components, "Re-encode input").doClick(); assertEquals(original, detail.getText());
    });
  }
  private static JButton button(java.util.List<Component> components, String text) {
    return (JButton) components.stream().filter(c -> c instanceof JButton b && b.getText().equals(text)).findFirst().orElseThrow();
  }
}
