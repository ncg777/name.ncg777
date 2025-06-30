package name.ncg777.maths;

import org.junit.Test;
import junit.framework.TestCase;
import name.ncg777.maths.exceptions.InvalidIntervalException;
import name.ncg777.maths.exceptions.InvalidIntervalOperationException;

/**
 * Additional tests for edge cases and Allen's interval algebra validation.
 * 
 * @author Nicolas Couture-Grenier
 */
public class IntervalEdgeCasesTest extends TestCase {

    @Test
    public void testConstructorValidation() {
        // Test contradictory single point interval
        try {
            Interval.make(5, true, 5, false);
            fail("Expected InvalidIntervalException for contradictory single point");
        } catch (InvalidIntervalException e) {
            assertTrue(e.getMessage().contains("contradictory arguments"));
        }
        
        // Test null mismatches
        try {
            Interval.make(5, true, null, false);
            fail("Expected InvalidIntervalException for null mismatch");
        } catch (InvalidIntervalException e) {
            assertTrue(e.getMessage().contains("minimum cannot be null if maximum is not"));
        }
        
        // Test invalid empty interval
        try {
            Interval.make(null, true, null, false);
            fail("Expected InvalidIntervalException for invalid empty interval");
        } catch (InvalidIntervalException e) {
            assertTrue(e.getMessage().contains("null minimum and maximum is only for the empty interval"));
        }
    }

    @Test
    public void testAllensIntervalRelationships() {
        // Test Allen's 13 possible interval relationships
        Interval<Integer> a = Interval.makeClosedInterval(1, 3);
        Interval<Integer> b;
        
        // 1. Before: A before B (A < B)
        b = Interval.makeClosedInterval(5, 7);
        assertFalse(a.overlapsWith(b));
        assertFalse(a.isSubsetOf(b));
        
        // 2. Meets: A meets B (A m B) 
        b = Interval.makeClosedInterval(3, 5);
        assertTrue(a.overlapsWith(b)); // They share point 3
        
        // 3. Overlaps: A overlaps B (A o B)
        b = Interval.makeClosedInterval(2, 4);
        assertTrue(a.overlapsWith(b));
        assertFalse(a.isSubsetOf(b));
        assertFalse(b.isSubsetOf(a));
        
        // 4. Finished by: A finished by B (A fi B)
        b = Interval.makeClosedInterval(2, 3);
        assertTrue(a.overlapsWith(b));
        assertTrue(a.isSupersetOf(b));
        
        // 5. Contains: A contains B (A contains B)
        b = Interval.makeClosedInterval(2, 2);
        assertTrue(a.overlapsWith(b));
        assertTrue(a.isProperSupersetOf(b));
        
        // 6. Starts: A starts B (A s B)
        b = Interval.makeClosedInterval(1, 5);
        assertTrue(a.overlapsWith(b));
        assertTrue(b.isProperSupersetOf(a));
        
        // 7. Equals: A equals B (A = B)
        b = Interval.makeClosedInterval(1, 3);
        assertTrue(a.overlapsWith(b));
        assertTrue(a.equals(b));
        assertFalse(a.isProperSupersetOf(b));
    }

    @Test
    public void testBoundaryInclusionLogic() {
        Interval<Integer> closed = Interval.makeClosedInterval(1, 3);      // [1,3]
        Interval<Integer> open = Interval.makeOpenInterval(1, 3);          // (1,3)
        Interval<Integer> leftOpen = Interval.make(1, false, 3, true);     // (1,3]  
        Interval<Integer> rightOpen = Interval.make(1, true, 3, false);    // [1,3)
        
        // Test boundary points
        assertTrue(closed.contains(1));
        assertTrue(closed.contains(3));
        
        assertFalse(open.contains(1));
        assertFalse(open.contains(3));
        
        assertFalse(leftOpen.contains(1));
        assertTrue(leftOpen.contains(3));
        
        assertTrue(rightOpen.contains(1));
        assertFalse(rightOpen.contains(3));
        
        // Test superset relationships with boundary logic
        assertTrue(closed.isProperSupersetOf(open));
        assertTrue(closed.isProperSupersetOf(leftOpen));
        assertTrue(closed.isProperSupersetOf(rightOpen));
        
        assertFalse(open.isProperSupersetOf(closed));
        assertFalse(leftOpen.isProperSupersetOf(closed));
        assertFalse(rightOpen.isProperSupersetOf(closed));
    }

    @Test
    public void testMergeWithBoundaryEdgeCases() {
        // Merge adjacent intervals that touch at boundary
        Interval<Integer> left = Interval.make(1, true, 3, true);    // [1,3]
        Interval<Integer> right = Interval.make(3, true, 5, true);   // [3,5]
        
        Interval<Integer> merged = left.mergeWith(right);
        assertEquals(Integer.valueOf(1), merged.getMinimum());
        assertEquals(Integer.valueOf(5), merged.getMaximum());
        assertTrue(merged.isLeftClosed());
        assertTrue(merged.isRightClosed()); // [1,5]
        
        // Merge with mixed boundaries
        Interval<Integer> leftOpen = Interval.make(1, false, 3, true);   // (1,3]
        Interval<Integer> rightOpen = Interval.make(2, true, 5, false);  // [2,5)
        
        Interval<Integer> mergedMixed = leftOpen.mergeWith(rightOpen);
        assertEquals(Integer.valueOf(1), mergedMixed.getMinimum());
        assertEquals(Integer.valueOf(5), mergedMixed.getMaximum());
        assertFalse(mergedMixed.isLeftClosed());  // (1,...
        assertFalse(mergedMixed.isRightClosed()); // ...,5)
    }

    @Test
    public void testOverlapEdgeCases() {
        // Test barely touching intervals
        Interval<Integer> a = Interval.make(1, true, 3, false);    // [1,3)
        Interval<Integer> b = Interval.make(3, false, 5, true);    // (3,5]
        
        assertFalse(a.overlapsWith(b)); // Gap at point 3
        
        // Test single point overlap
        Interval<Integer> c = Interval.make(1, true, 3, true);     // [1,3]
        Interval<Integer> d = Interval.make(3, true, 5, true);     // [3,5]
        
        assertTrue(c.overlapsWith(d)); // Share point 3
        
        // Test degenerate interval overlaps
        Interval<Integer> point = Interval.makeClosedInterval(3, 3);  // [3,3]
        assertTrue(c.overlapsWith(point));
        assertTrue(d.overlapsWith(point));
        assertFalse(a.overlapsWith(point)); // [1,3) doesn't include 3
    }

    @Test
    public void testEmptyIntervalBehavior() {
        Interval<Integer> empty = Interval.getEmptyInterval();
        Interval<Integer> normal = Interval.makeClosedInterval(1, 5);
        
        // Empty interval properties
        assertTrue(empty.isEmpty());
        assertNull(empty.getMinimum());
        assertNull(empty.getMaximum());
        
        // Empty interval doesn't contain anything
        assertFalse(empty.contains(1));
        assertFalse(empty.contains(0));
        
        // Empty interval relationships
        assertFalse(empty.overlapsWith(normal));
        assertFalse(normal.overlapsWith(empty));
        assertTrue(normal.isSupersetOf(empty));
        assertTrue(empty.isSubsetOf(normal));
        assertFalse(empty.isProperSupersetOf(normal));
        assertTrue(normal.isProperSupersetOf(empty));
        
        // Empty intervals are equal
        Interval<Integer> empty2 = Interval.getEmptyInterval();
        assertTrue(empty.equals(empty2));
        assertTrue(empty.isSupersetOf(empty2));
        assertFalse(empty.isProperSupersetOf(empty2));
    }

    @Test
    public void testDegenerateIntervalBehavior() {
        Interval<Integer> point = Interval.makeClosedInterval(5, 5);  // [5,5]
        
        assertTrue(point.isDegenerate());
        assertTrue(point.isClosed());
        assertFalse(point.isEmpty());
        assertFalse(point.isHalfOpen());
        
        assertTrue(point.contains(5));
        assertFalse(point.contains(4));
        assertFalse(point.contains(6));
        
        // Degenerate interval overlaps
        Interval<Integer> containing = Interval.makeClosedInterval(3, 7);
        assertTrue(containing.overlapsWith(point));
        assertTrue(containing.isProperSupersetOf(point));
        assertTrue(point.isProperSubsetOf(containing));
    }

    @Test
    public void testComparisonEdgeCases() {
        Interval<Integer> a = Interval.makeClosedInterval(1, 3);
        Interval<Integer> b = Interval.makeClosedInterval(1, 3);
        Interval<Integer> c = Interval.make(1, false, 3, true);  // (1,3]
        
        // Equal intervals
        assertEquals(0, a.compareTo(b));
        
        // Boundary inclusion affects comparison
        assertTrue(a.compareTo(c) < 0);  // [1,3] < (1,3] because left boundary
        assertTrue(c.compareTo(a) > 0);
        
        // Hash code consistency
        assertEquals(a.hashCode(), b.hashCode());
        // Note: Different intervals may have same hash, but equal intervals must have same hash
        
        // String representation
        assertEquals("[1,3]", a.toString());
        assertEquals("(1,3]", c.toString());
    }

    @Test
    public void testExceptionMessages() {
        try {
            Interval<Integer> a = Interval.makeClosedInterval(1, 3);
            Interval<Integer> b = Interval.makeClosedInterval(5, 7);
            a.mergeWith(b);
            fail("Should throw InvalidIntervalOperationException");
        } catch (InvalidIntervalOperationException e) {
            assertTrue("Exception message should be descriptive", 
                      e.getMessage().contains("Cannot merge non-overlapping intervals"));
            assertTrue("Exception message should include interval representations",
                      e.getMessage().contains("[1,3]") && e.getMessage().contains("[5,7]"));
        }
    }
}