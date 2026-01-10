package name.ncg777.maths;

import java.util.Arrays;
import java.util.Set;

import junit.framework.TestCase;

public class CompositionTests extends TestCase {

  private Composition fromParts(int... parts) {
    if (parts.length == 0) {
      throw new IllegalArgumentException("parts must be non-empty");
    }
    int total = 0;
    for (int part : parts) {
      if (part <= 0) {
        throw new IllegalArgumentException("parts must be positive");
      }
      total += part;
    }
    Boolean[] marks = new Boolean[Math.max(0, total - 1)];
    Arrays.fill(marks, Boolean.FALSE);
    int acc = 0;
    for (int i = 0; i < parts.length - 1; i++) {
      acc += parts[i];
      marks[acc - 1] = Boolean.TRUE;
    }
    return new Composition(marks);
  }

  public void testFactorRefinementsSinglePart() {
    Composition c = fromParts(2);
    Set<Composition> refinements = c.factorRefinements();
    assertEquals(2, refinements.size());
    assertTrue(refinements.contains(fromParts(2)));
    assertTrue(refinements.contains(fromParts(1, 1)));
  }

  public void testFactorRefinementsMultipleParts() {
    Composition c = fromParts(2, 2);
    Set<Composition> refinements = c.factorRefinements();
    assertEquals(4, refinements.size());
    assertTrue(refinements.contains(fromParts(2, 2)));
    assertTrue(refinements.contains(fromParts(2, 1, 1)));
    assertTrue(refinements.contains(fromParts(1, 1, 2)));
    assertTrue(refinements.contains(fromParts(1, 1, 1, 1)));
  }

  public void testFactorRefinementsPreserveTotal() {
    Composition c = fromParts(2, 3, 1);
    int total = c.getTotal();
    for (Composition r : c.factorRefinements()) {
      assertEquals(total, r.getTotal().intValue());
    }
  }
}
