package name.ncg777.maths.enumerations;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.junit.Test;

import junit.framework.TestCase;
import name.ncg777.computing.structures.ImmutableIntArray;

public class BitSequenceEnumerationTests extends TestCase {

    @Test
    public void testGetPointSet_dimension1() {
        Set<ImmutableIntArray> points = BitSequenceEnumeration.getPointSet(1);
        assertEquals(2, points.size());
        
        List<ImmutableIntArray> pointsList = new ArrayList<>(points);
        
        // Should contain [0] and [1]
        assertEquals(1, pointsList.get(0).size());
        assertEquals(1, pointsList.get(1).size());
        assertEquals(0, pointsList.get(0).get(0));
        assertEquals(1, pointsList.get(1).get(0));
    }

    @Test
    public void testGetPointSet_dimension2() {
        Set<ImmutableIntArray> points = BitSequenceEnumeration.getPointSet(2);
        assertEquals(4, points.size());
        
        List<ImmutableIntArray> pointsList = new ArrayList<>(points);
        
        // Should contain [0,0], [0,1], [1,0], [1,1]
        for (ImmutableIntArray arr : pointsList) {
            assertEquals(2, arr.size());
            assertTrue(arr.get(0) == 0 || arr.get(0) == 1);
            assertTrue(arr.get(1) == 0 || arr.get(1) == 1);
        }
    }

    @Test
    public void testGetPointSet_dimension3() {
        Set<ImmutableIntArray> points = BitSequenceEnumeration.getPointSet(3);
        assertEquals(8, points.size()); // 2^3 = 8 points
        
        // Verify all arrays have length 3 and contain only 0s and 1s
        for (ImmutableIntArray arr : points) {
            assertEquals(3, arr.size());
            for (int i = 0; i < 3; i++) {
                assertTrue(arr.get(i) == 0 || arr.get(i) == 1);
            }
        }
    }

    @Test
    public void testGetPointSet_invalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            BitSequenceEnumeration.getPointSet(0);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            BitSequenceEnumeration.getPointSet(-1);
        });
    }

    @Test
    public void testGetHypercubeNeighborRelation_dimension1() {
        Map<ImmutableIntArray, List<ImmutableIntArray>> relation = BitSequenceEnumeration.getHypercubeNeighborRelation(1);
        
        Set<ImmutableIntArray> points = BitSequenceEnumeration.getPointSet(1);
        List<ImmutableIntArray> pointsList = new ArrayList<>(points);
        
        ImmutableIntArray point0 = pointsList.get(0); // [0]
        ImmutableIntArray point1 = pointsList.get(1); // [1]
        
        // They should be neighbors of each other
        assertTrue(relation.get(point0).contains(point1));
        assertTrue(relation.get(point1).contains(point0));
        
        // Each point should have exactly one neighbor
        assertEquals(1, relation.get(point0).size());
        assertEquals(1, relation.get(point1).size());
    }

    @Test
    public void testGetHypercubeNeighborRelation_dimension2() {
        Map<ImmutableIntArray, List<ImmutableIntArray>> relation = BitSequenceEnumeration.getHypercubeNeighborRelation(2);
        
        // Create specific arrays for testing
        ImmutableIntArray arr00 = new ImmutableIntArray(new int[]{0, 0});
        ImmutableIntArray arr01 = new ImmutableIntArray(new int[]{0, 1});
        ImmutableIntArray arr10 = new ImmutableIntArray(new int[]{1, 0});
        ImmutableIntArray arr11 = new ImmutableIntArray(new int[]{1, 1});
        
        // Test neighbors: arrays that differ by exactly one bit
        // [0,0] should have neighbors [0,1] and [1,0]
        List<ImmutableIntArray> neighbors00 = relation.get(arr00);
        assertEquals(2, neighbors00.size());
        assertTrue(neighbors00.contains(arr01));
        assertTrue(neighbors00.contains(arr10));
        assertFalse(neighbors00.contains(arr11));
        
        // [0,1] should have neighbors [0,0] and [1,1]
        List<ImmutableIntArray> neighbors01 = relation.get(arr01);
        assertEquals(2, neighbors01.size());
        assertTrue(neighbors01.contains(arr00));
        assertTrue(neighbors01.contains(arr11));
        assertFalse(neighbors01.contains(arr10));
        
        // [1,0] should have neighbors [0,0] and [1,1]
        List<ImmutableIntArray> neighbors10 = relation.get(arr10);
        assertEquals(2, neighbors10.size());
        assertTrue(neighbors10.contains(arr00));
        assertTrue(neighbors10.contains(arr11));
        assertFalse(neighbors10.contains(arr01));
        
        // [1,1] should have neighbors [0,1] and [1,0]
        List<ImmutableIntArray> neighbors11 = relation.get(arr11);
        assertEquals(2, neighbors11.size());
        assertTrue(neighbors11.contains(arr01));
        assertTrue(neighbors11.contains(arr10));
        assertFalse(neighbors11.contains(arr00));
    }

    @Test
    public void testGetHypercubeNeighborRelation_invalidInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            BitSequenceEnumeration.getHypercubeNeighborRelation(0);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            BitSequenceEnumeration.getHypercubeNeighborRelation(-1);
        });
    }

    @Test
    public void testGetHypercubeNeighborRelation_selfNotNeighbor() {
        Map<ImmutableIntArray, List<ImmutableIntArray>> relation = BitSequenceEnumeration.getHypercubeNeighborRelation(2);
        
        ImmutableIntArray arr00 = new ImmutableIntArray(new int[]{0, 0});
        
        // An array should not be a neighbor of itself (0 differences)
        assertFalse(relation.get(arr00).contains(arr00));
    }
}