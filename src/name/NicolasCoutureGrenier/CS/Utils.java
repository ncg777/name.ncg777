package name.NicolasCoutureGrenier.CS;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class Utils {

  public static void copyStringToClipboard(String s) {
    StringSelection stringSelection = new StringSelection(s);
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(stringSelection, null);
  } 
}
