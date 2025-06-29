package name.ncg777.maths.enumerations;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.junit.Test;

import junit.framework.TestCase;
import name.ncg777.maths.sequences.Sequence;

public class BitSequenceEnumerationTests extends TestCase {

    @Test
    public void testGetPointSet_dimension1() {
        Set<Sequence> points = BitSequenceEnumeration.getPointSet(1);
        assertEquals(2, points.size());
        
        List<Sequence> pointsList = new ArrayList<>(points);
        
        // Should contain [0] and [1]
        assertEquals(1, pointsList.get(0).size());
        assertEquals(1, pointsList.get(1).size());
        assertEquals(Integer.valueOf(0), pointsList.get(0).get(0));
        assertEquals(Integer.valueOf(1), pointsList.get(1).get(0));
    }

    @Test
    public void testGetPointSet_dimension2() {
        Set<Sequence> points = BitSequenceEnumeration.getPointSet(2);
        assertEquals(4, points.size());
        
        List<Sequence> pointsList = new ArrayList<>(points);
        
        // Should contain [0,0], [0,1], [1,0], [1,1]
        for (Sequence seq : pointsList) {
            assertEquals(2, seq.size());
            assertTrue(seq.get(0) == 0 || seq.get(0) == 1);
            assertTrue(seq.get(1) == 0 || seq.get(1) == 1);
        }
    }

    @Test
    public void testGetPointSet_dimension3() {
        Set<Sequence> points = BitSequenceEnumeration.getPointSet(3);
        assertEquals(8, points.size()); // 2^3 = 8 points
        
        // Verify all sequences have length 3 and contain only 0s and 1s
        for (Sequence seq : points) {
            assertEquals(3, seq.size());
            for (int i = 0; i < 3; i++) {
                assertTrue(seq.get(i) == 0 || seq.get(i) == 1);
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
        Map<Sequence, List<Sequence>> relation = BitSequenceEnumeration.getHypercubeNeighborRelation(1);
        
        Set<Sequence> points = BitSequenceEnumeration.getPointSet(1);
        List<Sequence> pointsList = new ArrayList<>(points);
        
        Sequence point0 = pointsList.get(0); // [0]
        Sequence point1 = pointsList.get(1); // [1]
        
        // They should be neighbors of each other
        assertTrue(relation.get(point0).contains(point1));
        assertTrue(relation.get(point1).contains(point0));
        
        // Each point should have exactly one neighbor
        assertEquals(1, relation.get(point0).size());
        assertEquals(1, relation.get(point1).size());
    }

    @Test
    public void testGetHypercubeNeighborRelation_dimension2() {
        Map<Sequence, List<Sequence>> relation = BitSequenceEnumeration.getHypercubeNeighborRelation(2);
        
        // Create specific sequences for testing
        Sequence seq00 = new Sequence();
        seq00.add(0); seq00.add(0);
        
        Sequence seq01 = new Sequence();
        seq01.add(0); seq01.add(1);
        
        Sequence seq10 = new Sequence();
        seq10.add(1); seq10.add(0);
        
        Sequence seq11 = new Sequence();
        seq11.add(1); seq11.add(1);
        
        // Test neighbors: sequences that differ by exactly one bit
        // [0,0] should have neighbors [0,1] and [1,0]
        List<Sequence> neighbors00 = relation.get(seq00);
        assertEquals(2, neighbors00.size());
        assertTrue(neighbors00.contains(seq01));
        assertTrue(neighbors00.contains(seq10));
        assertFalse(neighbors00.contains(seq11));
        
        // [0,1] should have neighbors [0,0] and [1,1]
        List<Sequence> neighbors01 = relation.get(seq01);
        assertEquals(2, neighbors01.size());
        assertTrue(neighbors01.contains(seq00));
        assertTrue(neighbors01.contains(seq11));
        assertFalse(neighbors01.contains(seq10));
        
        // [1,0] should have neighbors [0,0] and [1,1]
        List<Sequence> neighbors10 = relation.get(seq10);
        assertEquals(2, neighbors10.size());
        assertTrue(neighbors10.contains(seq00));
        assertTrue(neighbors10.contains(seq11));
        assertFalse(neighbors10.contains(seq01));
        
        // [1,1] should have neighbors [0,1] and [1,0]
        List<Sequence> neighbors11 = relation.get(seq11);
        assertEquals(2, neighbors11.size());
        assertTrue(neighbors11.contains(seq01));
        assertTrue(neighbors11.contains(seq10));
        assertFalse(neighbors11.contains(seq00));
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
        Map<Sequence, List<Sequence>> relation = BitSequenceEnumeration.getHypercubeNeighborRelation(2);
        
        Sequence seq00 = new Sequence();
        seq00.add(0); seq00.add(0);
        
        // A sequence should not be a neighbor of itself (0 differences)
        assertFalse(relation.get(seq00).contains(seq00));
    }
}