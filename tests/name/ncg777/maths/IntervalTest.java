package name.ncg777.maths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import junit.framework.TestCase;

/**
 * Comprehensive unit tests for the Interval class.
 * Tests cover edge cases, degenerate intervals, empty intervals, 
 * half-open intervals, and validation of interval algebra relationships.
 * 
 * @author Nicolas Couture-Grenier
 */
public class IntervalTest extends TestCase {

    private Interval<Integer> closedInterval;
    private Interval<Integer> openInterval;
    private Interval<Integer> halfOpenLeft;
    private Interval<Integer> halfOpenRight;
    private Interval<Integer> emptyInterval;
    private Interval<Integer> degenerateInterval;

    @Before
    public void setUp() throws Exception {
        // [1, 5]
        closedInterval = Interval.makeClosedInterval(1, 5);
        // (2, 8)
        openInterval = Interval.makeOpenInterval(2, 8);
        // [3, 7)
        halfOpenLeft = Interval.make(3, true, 7, false);
        // (4, 6]
        halfOpenRight = Interval.make(4, false, 6, true);
        // Empty interval
        emptyInterval = Interval.getEmptyInterval();
        // [3, 3] - degenerate interval (single point)
        degenerateInterval = Interval.makeClosedInterval(3, 3);
    }

    @After
    public void tearDown() throws Exception {}

    // ===== Basic Property Tests =====
    
    @Test
    public void testBasicProperties() {
        // Closed interval properties
        assertTrue(closedInterval.isClosed());
        assertFalse(closedInterval.isOpen());
        assertFalse(closedInterval.isHalfOpen());
        assertTrue(closedInterval.isLeftClosed());
        assertTrue(closedInterval.isRightClosed());
        assertFalse(closedInterval.isEmpty());
        assertFalse(closedInterval.isDegenerate());
        
        // Open interval properties
        assertFalse(openInterval.isClosed());
        assertTrue(openInterval.isOpen());
        assertFalse(openInterval.isHalfOpen());
        assertFalse(openInterval.isLeftClosed());
        assertFalse(openInterval.isRightClosed());
        assertFalse(openInterval.isEmpty());
        assertFalse(openInterval.isDegenerate());
        
        // Half-open interval properties
        assertFalse(halfOpenLeft.isClosed());
        assertFalse(halfOpenLeft.isOpen());
        assertTrue(halfOpenLeft.isHalfOpen());
        assertTrue(halfOpenLeft.isLeftClosed());
        assertFalse(halfOpenLeft.isRightClosed());
        
        // Empty interval properties
        assertTrue(emptyInterval.isEmpty());
        assertFalse(emptyInterval.isClosed());
        assertTrue(emptyInterval.isOpen());  // Empty interval is considered open
        assertFalse(emptyInterval.isHalfOpen());
        
        // Degenerate interval properties
        assertTrue(degenerateInterval.isDegenerate());
        assertTrue(degenerateInterval.isClosed());
        assertFalse(degenerateInterval.isEmpty());
        assertFalse(degenerateInterval.isHalfOpen());
    }

    @Test
    public void testGetMinMax() {
        assertEquals(Integer.valueOf(1), closedInterval.getMinimum());
        assertEquals(Integer.valueOf(5), closedInterval.getMaximum());
        
        assertEquals(Integer.valueOf(2), openInterval.getMinimum());
        assertEquals(Integer.valueOf(8), openInterval.getMaximum());
        
        assertNull(emptyInterval.getMinimum());
        assertNull(emptyInterval.getMaximum());
    }

    // ===== Containment Tests =====
    
    @Test
    public void testContains() {
        // Closed interval [1, 5]
        assertTrue(closedInterval.contains(1));  // boundary included
        assertTrue(closedInterval.contains(3));  // interior
        assertTrue(closedInterval.contains(5));  // boundary included
        assertFalse(closedInterval.contains(0)); // outside
        assertFalse(closedInterval.contains(6)); // outside
        
        // Open interval (2, 8)
        assertFalse(openInterval.contains(2));   // boundary excluded
        assertTrue(openInterval.contains(3));    // interior
        assertTrue(openInterval.contains(7));    // interior
        assertFalse(openInterval.contains(8));   // boundary excluded
        assertFalse(openInterval.contains(1));   // outside
        
        // Half-open [3, 7)
        assertTrue(halfOpenLeft.contains(3));    // left boundary included
        assertTrue(halfOpenLeft.contains(5));    // interior
        assertFalse(halfOpenLeft.contains(7));   // right boundary excluded
        
        // Empty interval contains nothing
        assertFalse(emptyInterval.contains(1));
        assertFalse(emptyInterval.contains(0));
        
        // Degenerate interval [3, 3]
        assertTrue(degenerateInterval.contains(3));
        assertFalse(degenerateInterval.contains(2));
        assertFalse(degenerateInterval.contains(4));
    }

    // ===== Subset/Superset Tests =====
    
    @Test
    public void testSubsetSuperset() {
        Interval<Integer> large = Interval.makeClosedInterval(0, 10);
        Interval<Integer> small = Interval.makeClosedInterval(2, 4);
        
        assertTrue(small.isSubsetOf(large));
        assertTrue(large.isSupersetOf(small));
        assertFalse(large.isSubsetOf(small));
        assertFalse(small.isSupersetOf(large));
        
        // Proper subset/superset
        assertTrue(small.isProperSubsetOf(large));
        assertTrue(large.isProperSupersetOf(small));
        assertFalse(large.isProperSubsetOf(small));
        assertFalse(small.isProperSupersetOf(large));
        
        // Self is superset but not proper superset
        assertTrue(large.isSupersetOf(large));
        assertFalse(large.isProperSupersetOf(large));
        
        // Empty interval special cases
        assertTrue(emptyInterval.isSubsetOf(large));
        assertTrue(large.isSupersetOf(emptyInterval));
        assertTrue(emptyInterval.isSubsetOf(emptyInterval));
        assertTrue(emptyInterval.isSupersetOf(emptyInterval));
    }

    // ===== Overlap Tests =====
    
    @Test
    public void testOverlaps() {
        // Overlapping intervals
        assertTrue(closedInterval.overlapsWith(halfOpenLeft)); // [1,5] overlaps [3,7)
        assertTrue(halfOpenLeft.overlapsWith(closedInterval));
        
        // Non-overlapping intervals
        Interval<Integer> disjoint = Interval.makeClosedInterval(10, 15);
        assertFalse(closedInterval.overlapsWith(disjoint));
        assertFalse(disjoint.overlapsWith(closedInterval));
        
        // Adjacent intervals
        Interval<Integer> adjacent = Interval.makeClosedInterval(5, 10);
        assertTrue(closedInterval.overlapsWith(adjacent)); // [1,5] touches [5,10] at point 5
        
        // Adjacent intervals with open boundaries  
        Interval<Integer> adjacentOpen1 = Interval.make(1, true, 3, false);  // [1,3)
        Interval<Integer> adjacentOpen2 = Interval.make(3, true, 5, true);   // [3,5]
        assertFalse(adjacentOpen1.overlapsWith(adjacentOpen2)); // They touch but don't overlap
        
        // Truly disjoint adjacent intervals
        Interval<Integer> disjointOpen1 = Interval.make(1, true, 3, false);  // [1,3)
        Interval<Integer> disjointOpen2 = Interval.make(3, false, 5, true);  // (3,5]
        assertFalse(disjointOpen1.overlapsWith(disjointOpen2)); // Gap between them
        
        // Empty intervals don't overlap
        assertFalse(emptyInterval.overlapsWith(closedInterval));
        assertFalse(closedInterval.overlapsWith(emptyInterval));
        assertFalse(emptyInterval.overlapsWith(emptyInterval));
    }

    // ===== Merge Tests =====
    
    @Test
    public void testMergeOverlapping() {
        // Merge overlapping intervals
        Interval<Integer> interval1 = Interval.makeClosedInterval(1, 5);
        Interval<Integer> interval2 = Interval.makeClosedInterval(3, 8);
        Interval<Integer> merged = interval1.mergeWith(interval2);
        
        assertEquals(Integer.valueOf(1), merged.getMinimum());
        assertEquals(Integer.valueOf(8), merged.getMaximum());
        assertTrue(merged.isClosed());
        
        // Merge with mixed boundaries
        Interval<Integer> open1 = Interval.make(1, false, 5, true);    // (1,5]
        Interval<Integer> open2 = Interval.make(3, true, 8, false);    // [3,8)
        Interval<Integer> mergedMixed = open1.mergeWith(open2);
        
        assertEquals(Integer.valueOf(1), mergedMixed.getMinimum());
        assertEquals(Integer.valueOf(8), mergedMixed.getMaximum());
        assertFalse(mergedMixed.isLeftClosed());  // (1,...
        assertFalse(mergedMixed.isRightClosed()); // ...,8)
    }

    @Test
    public void testMergeNonOverlapping() {
        // Should throw exception for non-overlapping intervals
        Interval<Integer> interval1 = Interval.makeClosedInterval(1, 3);
        Interval<Integer> interval2 = Interval.makeClosedInterval(5, 8);
        
        try {
            interval1.mergeWith(interval2);
            fail("Expected InvalidIntervalOperationException for non-overlapping intervals");
        } catch (name.ncg777.maths.exceptions.InvalidIntervalOperationException e) {
            // Expected - verify the message is descriptive
            assertTrue(e.getMessage().contains("Cannot merge non-overlapping intervals"));
        }
    }

    // ===== Edge Cases =====
    
    @Test
    public void testSinglePointInterval() {
        Interval<Integer> point = Interval.makeClosedInterval(5, 5);
        assertTrue(point.isDegenerate());
        assertTrue(point.contains(5));
        assertFalse(point.contains(4));
        assertFalse(point.contains(6));
        
        // Open single point should be empty
        Interval<Integer> emptyPoint = Interval.makeOpenInterval(5, 5);
        assertTrue(emptyPoint.isEmpty());
        assertFalse(emptyPoint.contains(5));
    }

    @Test
    public void testSwappedMinMax() {
        // Constructor should handle swapped min/max
        Interval<Integer> swapped = Interval.makeClosedInterval(5, 1);
        assertEquals(Integer.valueOf(1), swapped.getMinimum());
        assertEquals(Integer.valueOf(5), swapped.getMaximum());
    }

    // ===== Comparison Tests =====
    
    @Test
    public void testCompareTo() {
        Interval<Integer> early = Interval.makeClosedInterval(1, 3);
        Interval<Integer> later = Interval.makeClosedInterval(5, 7);
        Interval<Integer> overlapping = Interval.makeClosedInterval(2, 6);
        
        assertTrue(early.compareTo(later) < 0);
        assertTrue(later.compareTo(early) > 0);
        assertTrue(early.compareTo(overlapping) < 0);
        assertEquals(0, early.compareTo(early));
    }

    @Test
    public void testEquals() {
        Interval<Integer> same1 = Interval.makeClosedInterval(1, 5);
        Interval<Integer> same2 = Interval.makeClosedInterval(1, 5);
        Interval<Integer> different = Interval.makeClosedInterval(1, 6);
        
        assertTrue(same1.equals(same2));
        assertTrue(same2.equals(same1));
        assertFalse(same1.equals(different));
        assertFalse(same1.equals(null));
        assertFalse(same1.equals("not an interval"));
        
        // Empty intervals should be equal
        Interval<Integer> empty1 = Interval.getEmptyInterval();
        Interval<Integer> empty2 = Interval.getEmptyInterval();
        assertTrue(empty1.equals(empty2));
    }

    @Test
    public void testHashCode() {
        Interval<Integer> interval1 = Interval.makeClosedInterval(1, 5);
        Interval<Integer> interval2 = Interval.makeClosedInterval(1, 5);
        Interval<Integer> different = Interval.makeClosedInterval(1, 6);
        
        assertEquals(interval1.hashCode(), interval2.hashCode());
        assertNotSame(interval1.hashCode(), different.hashCode()); // Very likely different
    }

    @Test
    public void testToString() {
        assertEquals("[1,5]", closedInterval.toString());
        assertEquals("(2,8)", openInterval.toString());
        assertEquals("[3,7)", halfOpenLeft.toString());
        assertEquals("(4,6]", halfOpenRight.toString());
        assertEquals("∅", emptyInterval.toString());
        assertEquals("[3,3]", degenerateInterval.toString());
    }
}