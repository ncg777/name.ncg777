package name.ncg777.CS.DataStructures;

import static org.junit.Assert.*;

import org.junit.Test;

public class TritWord32Tests {

  @Test
  public void testSetAndGet() {
      TritWord32 tritWord32 = new TritWord32();

      tritWord32.set(0, -1);
      tritWord32.set(1, 0);
      tritWord32.set(2, 1);
      tritWord32.set(3, -1);

      assertEquals(-1, tritWord32.get(0));
      assertEquals(0, tritWord32.get(1));
      assertEquals(1, tritWord32.get(2));
      assertEquals(-1, tritWord32.get(3));
  }

}
