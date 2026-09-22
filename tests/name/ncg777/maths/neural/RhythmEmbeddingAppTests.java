package name.ncg777.maths.neural;

import static org.junit.Assert.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;
import org.junit.Test;
import name.ncg777.maths.neural.apps.*;

public class RhythmEmbeddingAppTests {
  private static java.util.List<Component> children(Container p) {
    java.util.List<Component> result = new ArrayList<>(); for (Component c : p.getComponents()) { result.add(c); if (c instanceof Container cc) result.addAll(children(cc)); } return result;
  }
  private static JButton button(java.util.List<Component> components, String text) { return (JButton) components.stream().filter(c -> c instanceof JButton b && b.getText().equals(text)).findFirst().orElseThrow(); }
  @Test public void curveTrainingAndCodeEditingCompleteOnTheEventThread() throws Exception {
    java.util.List<Component> components = new ArrayList<>(); CountDownLatch done = new CountDownLatch(1);
    SwingUtilities.invokeAndWait(() -> {
      components.addAll(children(new RhythmEmbeddingPanel())); JButton cancel = button(components, "Cancel");
      cancel.addPropertyChangeListener("enabled", e -> { if (Boolean.FALSE.equals(e.getNewValue())) done.countDown(); });
      button(components, "Train rhythm contours").doClick(); assertTrue(cancel.isEnabled());
    });
    assertTrue(done.await(20, TimeUnit.SECONDS));
    SwingUtilities.invokeAndWait(() -> {
      JTable table = (JTable) components.stream().filter(c -> c instanceof JTable).findFirst().orElseThrow(); assertEquals(7, table.getRowCount());
      JTextField code = (JTextField) components.stream().filter(c -> c instanceof JTextField f && f.getColumns() == 65).findFirst().orElseThrow();
      String original = code.getText(); assertEquals(16, original.length()); code.setText("0".repeat(16)); button(components, "Decode edited code").doClick();
      button(components, "Re-encode").doClick(); assertEquals(original, code.getText());
      for (int row = 0; row < 7; row++) System.out.println("Rhythm benchmark: " + table.getValueAt(row, 0) + " / " + table.getValueAt(row, 1) + " / " + table.getValueAt(row, 2));
    });
  }
  @Test public void changingTheImageExampleSourceLoadsRhythmsAndInvalidatesModels() throws Exception {
    java.util.List<Component> components = new ArrayList<>(); CountDownLatch done = new CountDownLatch(1);
    SwingUtilities.invokeAndWait(() -> {
      components.addAll(children(new TernaryEmbeddingPanel()));
      JButton cancel = button(components, "Cancel training"); cancel.addPropertyChangeListener("enabled", e -> { if (Boolean.FALSE.equals(e.getNewValue())) done.countDown(); });
      JComboBox<?> source = (JComboBox<?>) components.stream().filter(c -> c instanceof JComboBox<?> combo && combo.getItemAt(0) instanceof String).findFirst().orElseThrow();
      source.setSelectedIndex(1); assertTrue(cancel.isEnabled());
    });
    assertTrue(done.await(20, TimeUnit.SECONDS));
    SwingUtilities.invokeAndWait(() -> {
      assertTrue(components.stream().anyMatch(c -> c instanceof JLabel l && l.getText() != null && l.getText().startsWith("Hex ")));
      JTable table = (JTable) components.stream().filter(c -> c instanceof JTable).findFirst().orElseThrow(); assertEquals(0, table.getRowCount());
      JComboBox<?> source = (JComboBox<?>) components.stream().filter(c -> c instanceof JComboBox<?> combo && combo.getItemAt(0) instanceof String).findFirst().orElseThrow(); source.setSelectedIndex(0);
      assertTrue(button(components, "Train all four code sizes").isEnabled());
    });
  }
}
