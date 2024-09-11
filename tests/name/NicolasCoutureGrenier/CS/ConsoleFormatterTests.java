package name.NicolasCoutureGrenier.CS;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConsoleFormatterTests {
  private ConsoleFormatter consoleFormatter;

  @Before
  public void setUp() throws Exception {
    this.consoleFormatter = new ConsoleFormatter(80);
  }

  @After
  public void tearDown() throws Exception {}
  
  @Test
  public void testFormatAnswer() {
    String result = consoleFormatter.format(
        "the elements of a set in a particular order. For example, if we have a set of three elements {a, b, c}, the permutations of this set are:");
    String expected =
        "the elements of a set in a particular order. For example, if we have a set of\nthree elements {a, b, c}, the permutations of this set are:";
    assertEquals(expected, result);

    result = consoleFormatter.format(
        "skhfkh slsg slkjglsjdlg sldjglsjglk sljglsjg sldjglsjdlg sldjglsdgl {abcdefg abcdefg abcdefg abcdefg abcdefg abcdefg abcdefg abcdefg abcdefgh}");
    expected =
        "skhfkh slsg slkjglsjdlg sldjglsjglk sljglsjg sldjglsjdlg sldjglsdgl\n{abcdefg abcdefg abcdefg abcdefg abcdefg abcdefg abcdefg abcdefg abcdefgh}";
    assertEquals(expected, result);

    result = consoleFormatter.format(
        "skhfkh slsg slkjglsjdlg sldjglsjglk sljglsjg sldjglsjdlg sldjglsdgl {abcdefg abcdefg abcdefg abcdefg abcdefg abcdefg abcdefg abcdefg abcdefg abcdefgh}");
    expected = "skhfkh slsg slkjglsjdlg sldjglsjglk sljglsjg sldjglsjdlg sldjglsdgl {\n"
        + "  abcdefg\n" + "  abcdefg\n" + "  abcdefg\n" + "  abcdefg\n" + "  abcdefg\n"
        + "  abcdefg\n" + "  abcdefg\n" + "  abcdefg\n" + "  abcdefg\n" + "  abcdefgh\n" + "}";
    assertEquals(expected, result);

    result = consoleFormatter.format(
        "A combination is a selection of objects from a collection where the order doesn't matter. In other words, it is a way of choosing elements from a set in which the order of selection does not affect the outcome.");
    expected =
        "A combination is a selection of objects from a collection where the order\ndoesn't matter. In other words, it is a way of choosing elements from a set in\nwhich the order of selection does not affect the outcome.";
    assertEquals(expected, result);

    result = consoleFormatter.format(
        "abcdefg abcdefg abcdefg abcdefg abcdefg abcdefg abcdefg abcdefg abcdefg -abcdefgh test");
    expected =
        "abcdefg abcdefg abcdefg abcdefg abcdefg abcdefg abcdefg abcdefg abcdefg\n-abcdefgh test";
    assertEquals(expected, result);

    result = consoleFormatter.format(
        "abcdefghabcdefghabcdefghabcdefghabcdefghabcdefghabcdefghabcdefghabcdefgh-abcdefgh");
    expected =
        "abcdefghabcdefghabcdefghabcdefghabcdefghabcdefghabcdefghabcdefghabcdefgh-abcdefg\nh";
    assertEquals(expected, result);
  }


}
