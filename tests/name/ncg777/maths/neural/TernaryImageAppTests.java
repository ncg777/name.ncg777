package name.ncg777.maths.neural;

import static org.junit.Assert.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.*;
import javax.swing.*;
import org.junit.Test;
import name.ncg777.maths.neural.apps.TernaryImageMemoryApp;
import name.ncg777.maths.neural.apps.TernaryImageMemoryApp.PixelCanvas;

public class TernaryImageAppTests {
  private static java.util.List<Component> children(Container parent) {
    java.util.List<Component> all = new ArrayList<>();
    for (Component c : parent.getComponents()) { all.add(c); if (c instanceof Container p) all.addAll(children(p)); }
    return all;
  }
  private static AbstractButton button(java.util.List<Component> components, String text) {
    return components.stream().filter(c -> c instanceof AbstractButton b && b.getText().equals(text)).map(c -> (AbstractButton)c).findFirst().orElseThrow();
  }
  @Test public void editorBrushesDoNotConfuseTransparencyWithMissingness() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      var app = new TernaryImageMemoryApp(); var components = children(app.component());
      PixelCanvas editor = (PixelCanvas) components.stream().filter(c -> c instanceof PixelCanvas).findFirst().orElseThrow();
      editor.setSize(256, 256);
      button(components, "Unknown ?").doClick();
      editor.dispatchEvent(new MouseEvent(editor, MouseEvent.MOUSE_PRESSED, 0, MouseEvent.BUTTON1_DOWN_MASK, 16, 16, 1, false, MouseEvent.BUTTON1));
      assertFalse(editor.cue().known()[0]);
      button(components, "Store editor image").doClick();
      JList<?> library = (JList<?>) components.stream().filter(c -> c instanceof JList<?>).findFirst().orElseThrow();
      assertEquals(8, library.getModel().getSize());
      button(components, "Transparent 0").doClick();
      editor.dispatchEvent(new MouseEvent(editor, MouseEvent.MOUSE_PRESSED, 0, MouseEvent.BUTTON1_DOWN_MASK, 16, 16, 1, false, MouseEvent.BUTTON1));
      assertTrue(editor.cue().known()[0]); assertEquals(0, editor.cue().values()[0]);
      button(components, "Store editor image").doClick(); assertEquals(9, library.getModel().getSize());
      button(components, "Hide").doClick(); int missing = 0; for (boolean k : editor.cue().known()) if (!k) missing++;
      assertEquals(16, missing); app.cancel();
    });
  }
}
