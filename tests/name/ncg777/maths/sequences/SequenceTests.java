package name.ncg777.maths.sequences;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.sequences.Sequence.ArpType;
import name.ncg777.maths.sequences.Sequence.Combiner;
import name.ncg777.maths.sequences.Sequence.Operation;

public class SequenceTests extends TestCase {

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    // Test basic Sequence operations
    @Test
    public void testSequenceCreation() {
        Sequence seq = new Sequence();
        assertTrue(seq.isEmpty());
        
        Sequence seq2 = new Sequence(Arrays.asList(1, 2, 3, 4, 5));
        assertEquals(5, seq2.size());
        assertEquals(Integer.valueOf(1), seq2.get(0));
        assertEquals(Integer.valueOf(5), seq2.get(4));
    }

    // Test deterministic operations
    @Test
    public void testAddOperation() {
        Sequence x = new Sequence(Arrays.asList(1, 2, 3));
        Sequence y = new Sequence(Arrays.asList(4, 5, 6));
        
        Sequence result = Sequence.combine(Combiner.Recycle, Operation.Add, x, y);
        
        assertEquals(3, result.size()); // LCM of 3,3 = 3
        assertEquals(Integer.valueOf(5), result.get(0)); // 1+4
        assertEquals(Integer.valueOf(7), result.get(1)); // 2+5
        assertEquals(Integer.valueOf(9), result.get(2)); // 3+6
    }

    @Test
    public void testSubtractOperation() {
        Sequence x = new Sequence(Arrays.asList(10, 8, 6));
        Sequence y = new Sequence(Arrays.asList(3, 2, 1));
        
        Sequence result = Sequence.combine(Combiner.Recycle, Operation.Subtract, x, y);
        
        assertEquals(Integer.valueOf(7), result.get(0)); // 10-3
        assertEquals(Integer.valueOf(6), result.get(1)); // 8-2
        assertEquals(Integer.valueOf(5), result.get(2)); // 6-1
    }

    @Test
    public void testMultiplyOperation() {
        Sequence x = new Sequence(Arrays.asList(2, 3, 4));
        Sequence y = new Sequence(Arrays.asList(5, 6, 7));
        
        Sequence result = Sequence.combine(Combiner.Recycle, Operation.Multiply, x, y);
        
        assertEquals(Integer.valueOf(10), result.get(0)); // 2*5
        assertEquals(Integer.valueOf(18), result.get(1)); // 3*6
        assertEquals(Integer.valueOf(28), result.get(2)); // 4*7
    }

    @Test
    public void testDivideOperation() {
        Sequence x = new Sequence(Arrays.asList(20, 15, 12));
        Sequence y = new Sequence(Arrays.asList(4, 3, 2));
        
        Sequence result = Sequence.combine(Combiner.Recycle, Operation.Divide, x, y);
        
        assertEquals(Integer.valueOf(5), result.get(0)); // 20/4
        assertEquals(Integer.valueOf(5), result.get(1)); // 15/3
        assertEquals(Integer.valueOf(6), result.get(2)); // 12/2
    }

    @Test
    public void testXYOperations() {
        Sequence x = new Sequence(Arrays.asList(1, 2, 3));
        Sequence y = new Sequence(Arrays.asList(4, 5, 6));
        
        Sequence resultX = Sequence.combine(Combiner.Recycle, Operation.X, x, y);
        Sequence resultY = Sequence.combine(Combiner.Recycle, Operation.Y, x, y);
        
        assertEquals(Integer.valueOf(1), resultX.get(0)); // returns x
        assertEquals(Integer.valueOf(2), resultX.get(1)); 
        assertEquals(Integer.valueOf(3), resultX.get(2));
        
        assertEquals(Integer.valueOf(4), resultY.get(0)); // returns y
        assertEquals(Integer.valueOf(5), resultY.get(1));
        assertEquals(Integer.valueOf(6), resultY.get(2));
    }

    @Test
    public void testMinMaxOperations() {
        Sequence x = new Sequence(Arrays.asList(3, 8, 1));
        Sequence y = new Sequence(Arrays.asList(5, 2, 6));
        
        Sequence minResult = Sequence.combine(Combiner.Recycle, Operation.Min, x, y);
        Sequence maxResult = Sequence.combine(Combiner.Recycle, Operation.Max, x, y);
        
        assertEquals(Integer.valueOf(3), minResult.get(0)); // min(3,5)
        assertEquals(Integer.valueOf(2), minResult.get(1)); // min(8,2)
        assertEquals(Integer.valueOf(1), minResult.get(2)); // min(1,6)
        
        assertEquals(Integer.valueOf(5), maxResult.get(0)); // max(3,5)
        assertEquals(Integer.valueOf(8), maxResult.get(1)); // max(8,2)
        assertEquals(Integer.valueOf(6), maxResult.get(2)); // max(1,6)
    }

    @Test
    public void testModuloOperation() {
        Sequence x = new Sequence(Arrays.asList(17, 23, 15));
        Sequence y = new Sequence(Arrays.asList(5, 7, 4));
        
        Sequence result = Sequence.combine(Combiner.Recycle, Operation.Modulo, x, y);
        
        assertEquals(Integer.valueOf(2), result.get(0)); // 17%5
        assertEquals(Integer.valueOf(2), result.get(1)); // 23%7
        assertEquals(Integer.valueOf(3), result.get(2)); // 15%4
    }

    @Test
    public void testLogicalOperations() {
        Sequence x = new Sequence(Arrays.asList(5, 3, 7)); // 101, 011, 111 in binary
        Sequence y = new Sequence(Arrays.asList(3, 5, 1)); // 011, 101, 001 in binary
        
        Sequence andResult = Sequence.combine(Combiner.Recycle, Operation.And, x, y);
        Sequence orResult = Sequence.combine(Combiner.Recycle, Operation.Or, x, y);
        Sequence xorResult = Sequence.combine(Combiner.Recycle, Operation.Xor, x, y);
        
        assertEquals(Integer.valueOf(1), andResult.get(0)); // 5&3 = 101&011 = 001 = 1
        assertEquals(Integer.valueOf(1), andResult.get(1)); // 3&5 = 011&101 = 001 = 1
        assertEquals(Integer.valueOf(1), andResult.get(2)); // 7&1 = 111&001 = 001 = 1
        
        assertEquals(Integer.valueOf(7), orResult.get(0)); // 5|3 = 101|011 = 111 = 7
        assertEquals(Integer.valueOf(7), orResult.get(1)); // 3|5 = 011|101 = 111 = 7
        assertEquals(Integer.valueOf(7), orResult.get(2)); // 7|1 = 111|001 = 111 = 7
        
        assertEquals(Integer.valueOf(6), xorResult.get(0)); // 5^3 = 101^011 = 110 = 6
        assertEquals(Integer.valueOf(6), xorResult.get(1)); // 3^5 = 011^101 = 110 = 6
        assertEquals(Integer.valueOf(6), xorResult.get(2)); // 7^1 = 111^001 = 110 = 6
    }

    @Test
    public void testComparisonOperations() {
        Sequence x = new Sequence(Arrays.asList(3, 8, 5));
        Sequence y = new Sequence(Arrays.asList(5, 2, 5));
        
        Sequence equalResult = Sequence.combine(Combiner.Recycle, Operation.Equal, x, y);
        Sequence lessThanResult = Sequence.combine(Combiner.Recycle, Operation.LessThan, x, y);
        Sequence greaterThanResult = Sequence.combine(Combiner.Recycle, Operation.GreaterThan, x, y);
        
        assertEquals(Integer.valueOf(0), equalResult.get(0)); // 3==5 -> 0
        assertEquals(Integer.valueOf(0), equalResult.get(1)); // 8==2 -> 0  
        assertEquals(Integer.valueOf(1), equalResult.get(2)); // 5==5 -> 1
        
        assertEquals(Integer.valueOf(1), lessThanResult.get(0)); // 3<5 -> 1
        assertEquals(Integer.valueOf(0), lessThanResult.get(1)); // 8<2 -> 0
        assertEquals(Integer.valueOf(0), lessThanResult.get(2)); // 5<5 -> 0
        
        assertEquals(Integer.valueOf(0), greaterThanResult.get(0)); // 3>5 -> 0
        assertEquals(Integer.valueOf(1), greaterThanResult.get(1)); // 8>2 -> 1
        assertEquals(Integer.valueOf(0), greaterThanResult.get(2)); // 5>5 -> 0
    }

    // Test RandInt operation
    @Test
    public void testRandIntOperation() {
        Sequence x = new Sequence(Arrays.asList(1, 5, 10));
        Sequence y = new Sequence(Arrays.asList(3, 2, 15));
        
        Sequence result = Sequence.combine(Combiner.Recycle, Operation.RandInt, x, y);
        
        assertEquals(3, result.size());
        
        // For first pair: min(1,3)=1, max(1,3)=3, result should be 1,2, or 3
        int val1 = result.get(0);
        assertTrue("RandInt result should be between 1 and 3", val1 >= 1 && val1 <= 3);
        
        // For second pair: min(5,2)=2, max(5,2)=5, result should be 2,3,4, or 5
        int val2 = result.get(1);
        assertTrue("RandInt result should be between 2 and 5", val2 >= 2 && val2 <= 5);
        
        // For third pair: min(10,15)=10, max(10,15)=15, result should be 10-15
        int val3 = result.get(2);
        assertTrue("RandInt result should be between 10 and 15", val3 >= 10 && val3 <= 15);
    }

    @Test
    public void testRandIntWithEqualValues() {
        Sequence x = new Sequence(Arrays.asList(5, 5, 5));
        Sequence y = new Sequence(Arrays.asList(5, 5, 5));
        
        Sequence result = Sequence.combine(Combiner.Recycle, Operation.RandInt, x, y);
        
        // When x==y, RandInt should always return that value
        assertEquals(Integer.valueOf(5), result.get(0));
        assertEquals(Integer.valueOf(5), result.get(1));
        assertEquals(Integer.valueOf(5), result.get(2));
    }

    @Test
    public void testRandIntWithNegativeValues() {
        Sequence x = new Sequence(Arrays.asList(-3, -10));
        Sequence y = new Sequence(Arrays.asList(-1, -5));
        
        Sequence result = Sequence.combine(Combiner.Recycle, Operation.RandInt, x, y);
        
        // For first pair: min(-3,-1)=-3, max(-3,-1)=-1
        int val1 = result.get(0);
        assertTrue("RandInt result should be between -3 and -1", val1 >= -3 && val1 <= -1);
        
        // For second pair: min(-10,-5)=-10, max(-10,-5)=-5
        int val2 = result.get(1);
        assertTrue("RandInt result should be between -10 and -5", val2 >= -10 && val2 <= -5);
    }
    
    @Test
    public void testProjectBits() {
        Sequence x = new Sequence(Arrays.asList(1, 1, 2, 2));
        Sequence y = new Sequence(Arrays.asList(5, -5));
        
        Sequence result = Sequence.combine(Combiner.Recycle, Operation.ProjectBits, x, y);
        
        int val1 = result.get(0);
        assertTrue("ProjectBit result should be 1", val1 == 1);
        
        int val2 = result.get(1);
        assertTrue("ProjectBit result should be -1", val2 == -1);
        
        int val3 = result.get(2);
        assertTrue("ProjectBit result should be 4", val3 == 4);
        
        int val4 = result.get(3);
        assertTrue("ProjectBit result should be -4", val4 == -4);
    }

    @Test
    public void testRandIntDistribution() {
        // Test that RandInt produces different values over multiple runs
        Sequence x = new Sequence(Arrays.asList(1));
        Sequence y = new Sequence(Arrays.asList(10));
        
        Set<Integer> observedValues = new HashSet<>();
        
        // Run multiple times to check we get different values
        for (int i = 0; i < 50; i++) {
            Sequence result = Sequence.combine(Combiner.Recycle, Operation.RandInt, x, y);
            int value = result.get(0);
            assertTrue("Value should be in range [1,10]", value >= 1 && value <= 10);
            observedValues.add(value);
        }
        
        // Should observe multiple different values (with high probability)
        assertTrue("Should observe multiple different values", observedValues.size() > 3);
    }

    // Test different combiner modes
    @Test
    public void testProductCombiner() {
        Sequence x = new Sequence(Arrays.asList(1, 2));
        Sequence y = new Sequence(Arrays.asList(3, 4));
        
        Sequence result = Sequence.combine(Combiner.Product, Operation.Add, x, y);
        
        assertEquals(4, result.size()); // 2*2 = 4 combinations
        assertEquals(Integer.valueOf(4), result.get(0)); // 1+3
        assertEquals(Integer.valueOf(5), result.get(1)); // 1+4
        assertEquals(Integer.valueOf(5), result.get(2)); // 2+3
        assertEquals(Integer.valueOf(6), result.get(3)); // 2+4
    }

    @Test
    public void testTriangularCombiner() {
        Sequence x = new Sequence(Arrays.asList(1, 2, 3));
        Sequence y = new Sequence(Arrays.asList(4, 5, 6));
        
        Sequence result = Sequence.combine(Combiner.Triangular, Operation.Add, x, y);
        
        // Triangular: only when j <= i
        assertEquals(6, result.size()); 
        assertEquals(Integer.valueOf(5), result.get(0)); // 1+4 (i=0,j=0)
        assertEquals(Integer.valueOf(6), result.get(1)); // 2+4 (i=1,j=0)
        assertEquals(Integer.valueOf(7), result.get(2)); // 2+5 (i=1,j=1)
        assertEquals(Integer.valueOf(7), result.get(3)); // 3+4 (i=2,j=0)
        assertEquals(Integer.valueOf(8), result.get(4)); // 3+5 (i=2,j=1)
        assertEquals(Integer.valueOf(9), result.get(5)); // 3+6 (i=2,j=2)
    }

    @Test
    public void testApplyCombiner() {
        Sequence x = new Sequence(Arrays.asList(10, 20, 30, 40));
        Sequence y = new Sequence(Arrays.asList(0, 2, 1)); // indices
        
        Sequence result = Sequence.combine(Combiner.Apply, Operation.Add, x, y);
        
        assertEquals(3, result.size());
        assertEquals(Integer.valueOf(10), result.get(0)); // x[0%4] + y[0] = 10 + 0
        assertEquals(Integer.valueOf(32), result.get(1)); // x[2%4] + y[1] = 30 + 2  
        assertEquals(Integer.valueOf(21), result.get(2)); // x[1%4] + y[2] = 20 + 1
    }

    // ===== CONSTRUCTOR AND FACTORY METHOD TESTS =====
    
    @Test
    public void testConstructors() {
        // Test empty constructor
        Sequence seq1 = new Sequence();
        assertTrue(seq1.isEmpty());
        
        // Test list constructor
        Sequence seq2 = new Sequence(Arrays.asList(1, 2, 3));
        assertEquals(3, seq2.size());
        assertEquals(Integer.valueOf(1), seq2.get(0));
        assertEquals(Integer.valueOf(3), seq2.get(2));
        
        // Test Integer array constructor
        Sequence seq3 = new Sequence(new Integer[]{4, 5, 6});
        assertEquals(3, seq3.size());
        assertEquals(Integer.valueOf(4), seq3.get(0));
        assertEquals(Integer.valueOf(6), seq3.get(2));
        
        // Test int array constructor
        Sequence seq4 = new Sequence(new int[]{7, 8, 9});
        assertEquals(3, seq4.size());
        assertEquals(Integer.valueOf(7), seq4.get(0));
        assertEquals(Integer.valueOf(9), seq4.get(2));
    }
    
    @Test
    public void testParse() {
        // Test basic parsing
        Sequence seq1 = Sequence.parse("1 2 3 4 5");
        assertEquals(5, seq1.size());
        assertEquals(Integer.valueOf(1), seq1.get(0));
        assertEquals(Integer.valueOf(5), seq1.get(4));
        
        // Test parsing with brackets
        Sequence seq2 = Sequence.parse("[10 20 30]");
        assertEquals(3, seq2.size());
        assertEquals(Integer.valueOf(10), seq2.get(0));
        assertEquals(Integer.valueOf(30), seq2.get(2));
        
        // Test parsing with commas
        Sequence seq3 = Sequence.parse("1, 2, 3");
        assertEquals(3, seq3.size());
        assertEquals(Integer.valueOf(1), seq3.get(0));
        assertEquals(Integer.valueOf(3), seq3.get(2));
        
        // Test parsing with negative numbers
        Sequence seq4 = Sequence.parse("-5 0 5");
        assertEquals(3, seq4.size());
        assertEquals(Integer.valueOf(-5), seq4.get(0));
        assertEquals(Integer.valueOf(5), seq4.get(2));
    }
    
    @Test
    public void testStair() {
        // Test ascending stair
        Sequence stair1 = Sequence.stair(0, 5, 1);
        assertEquals(5, stair1.size());
        assertEquals(Integer.valueOf(0), stair1.get(0));
        assertEquals(Integer.valueOf(1), stair1.get(1));
        assertEquals(Integer.valueOf(4), stair1.get(4));
        
        // Test descending stair
        Sequence stair2 = Sequence.stair(10, 3, -2);
        assertEquals(3, stair2.size());
        assertEquals(Integer.valueOf(10), stair2.get(0));
        assertEquals(Integer.valueOf(8), stair2.get(1));
        assertEquals(Integer.valueOf(6), stair2.get(2));
        
        // Test zero length
        Sequence stair3 = Sequence.stair(5, 0, 1);
        assertEquals(0, stair3.size());
    }
    
    @Test
    public void testTri() {
        // Test triangular sequence
        Sequence tri = Sequence.tri(0, 3, 1);
        assertEquals(6, tri.size()); // 3 + 3 elements
        assertEquals(Integer.valueOf(0), tri.get(0));
        assertEquals(Integer.valueOf(1), tri.get(1));
        assertEquals(Integer.valueOf(2), tri.get(2));
        assertEquals(Integer.valueOf(3), tri.get(3));
        assertEquals(Integer.valueOf(2), tri.get(4));
        assertEquals(Integer.valueOf(1), tri.get(5));
    }
    
    @Test
    public void testArp() {
        // Test UP arpeggio
        Sequence arpUp = Sequence.arp(ArpType.UP, 4);
        assertEquals(4, arpUp.size());
        assertEquals(Integer.valueOf(0), arpUp.get(0));
        assertEquals(Integer.valueOf(1), arpUp.get(1));
        assertEquals(Integer.valueOf(2), arpUp.get(2));
        assertEquals(Integer.valueOf(3), arpUp.get(3));
        
        // Test DOWN arpeggio
        Sequence arpDown = Sequence.arp(ArpType.DOWN, 4);
        assertEquals(4, arpDown.size());
        assertEquals(Integer.valueOf(3), arpDown.get(0));
        assertEquals(Integer.valueOf(2), arpDown.get(1));
        assertEquals(Integer.valueOf(1), arpDown.get(2));
        assertEquals(Integer.valueOf(0), arpDown.get(3));
        
        // Test UPDOWN arpeggio
        Sequence arpUpDown = Sequence.arp(ArpType.UPDOWN, 3);
        assertEquals(4, arpUpDown.size()); // up: 0,1,2 then down: 1 (k-2 to k-2 with step -1)
        assertEquals(Integer.valueOf(0), arpUpDown.get(0));
        assertEquals(Integer.valueOf(1), arpUpDown.get(1));
        assertEquals(Integer.valueOf(2), arpUpDown.get(2));
        assertEquals(Integer.valueOf(1), arpUpDown.get(3));
    }
    
    @Test
    public void testFromMethods() {
        // Test from List
        Sequence seq1 = Sequence.from(Arrays.asList(1, 2, 3));
        assertEquals(3, seq1.size());
        assertEquals(Integer.valueOf(1), seq1.get(0));
        
        // Test from Integer array
        Sequence seq2 = Sequence.from(new Integer[]{4, 5, 6});
        assertEquals(3, seq2.size());
        assertEquals(Integer.valueOf(4), seq2.get(0));
    }
    
    // ===== BASIC TRANSFORMATION TESTS =====
    
    @Test
    public void testReverse() {
        Sequence seq = new Sequence(Arrays.asList(1, 2, 3, 4, 5));
        Sequence reversed = seq.reverse();
        
        assertEquals(5, reversed.size());
        assertEquals(Integer.valueOf(5), reversed.get(0));
        assertEquals(Integer.valueOf(4), reversed.get(1));
        assertEquals(Integer.valueOf(3), reversed.get(2));
        assertEquals(Integer.valueOf(2), reversed.get(3));
        assertEquals(Integer.valueOf(1), reversed.get(4));
        
        // Original should be unchanged
        assertEquals(Integer.valueOf(1), seq.get(0));
        assertEquals(Integer.valueOf(5), seq.get(4));
    }
    
    @Test
    public void testRotation() {
        Sequence seq = new Sequence(Arrays.asList(1, 2, 3, 4, 5));
        
        // Test rotate right (positive)
        Sequence rotRight = seq.rotate(2);
        assertEquals(5, rotRight.size());
        assertEquals(Integer.valueOf(4), rotRight.get(0)); // moved 2 positions right
        assertEquals(Integer.valueOf(5), rotRight.get(1));
        assertEquals(Integer.valueOf(1), rotRight.get(2));
        assertEquals(Integer.valueOf(2), rotRight.get(3));
        assertEquals(Integer.valueOf(3), rotRight.get(4));
        
        // Test rotate left (negative)
        Sequence rotLeft = seq.rotate(-1);
        assertEquals(5, rotLeft.size());
        assertEquals(Integer.valueOf(2), rotLeft.get(0)); // moved 1 position left
        assertEquals(Integer.valueOf(3), rotLeft.get(1));
        assertEquals(Integer.valueOf(4), rotLeft.get(2));
        assertEquals(Integer.valueOf(5), rotLeft.get(3));
        assertEquals(Integer.valueOf(1), rotLeft.get(4));
        
        // Test convenience methods
        Sequence rotRight1 = seq.rotateRight();
        assertEquals(Integer.valueOf(5), rotRight1.get(0));
        
        Sequence rotLeft1 = seq.rotateLeft();
        assertEquals(Integer.valueOf(2), rotLeft1.get(0));
    }
    
    @Test
    public void testFlip() {
        Sequence seq = new Sequence(Arrays.asList(1, 3, 5, 7));
        Sequence flipped = seq.flip();
        
        assertEquals(4, flipped.size());
        // Original: min=1, max=7, so flip around center
        assertEquals(Integer.valueOf(7), flipped.get(0)); // 7-(1-1) = 7
        assertEquals(Integer.valueOf(5), flipped.get(1)); // 7-(3-1) = 5
        assertEquals(Integer.valueOf(3), flipped.get(2)); // 7-(5-1) = 3
        assertEquals(Integer.valueOf(1), flipped.get(3)); // 7-(7-1) = 1
    }
    
    @Test 
    public void testMultiply() {
        Sequence seq = new Sequence(Arrays.asList(2, 3, 4));
        Sequence multiplied = seq.multiply(3);
        
        assertEquals(3, multiplied.size());
        assertEquals(Integer.valueOf(6), multiplied.get(0));
        assertEquals(Integer.valueOf(9), multiplied.get(1));
        assertEquals(Integer.valueOf(12), multiplied.get(2));
    }
    
    @Test
    public void testAddToEach() {
        Sequence seq = new Sequence(Arrays.asList(1, 2, 3));
        
        // Test adding scalar
        Sequence added = seq.addToEach(10);
        assertEquals(3, added.size());
        assertEquals(Integer.valueOf(11), added.get(0));
        assertEquals(Integer.valueOf(12), added.get(1));
        assertEquals(Integer.valueOf(13), added.get(2));
        
        // Test adding sequence
        Sequence other = new Sequence(Arrays.asList(100, 200));
        Sequence addedSeq = seq.addToEach(other);
        assertEquals(6, addedSeq.size()); // 3*2 = 6
        assertEquals(Integer.valueOf(101), addedSeq.get(0)); // 1+100
        assertEquals(Integer.valueOf(102), addedSeq.get(1)); // 2+100
        assertEquals(Integer.valueOf(103), addedSeq.get(2)); // 3+100
        assertEquals(Integer.valueOf(201), addedSeq.get(3)); // 1+200
        assertEquals(Integer.valueOf(202), addedSeq.get(4)); // 2+200
        assertEquals(Integer.valueOf(203), addedSeq.get(5)); // 3+200
    }
    
    @Test
    public void testPowerOperations() {
        Sequence seq = new Sequence(Arrays.asList(2, 3, 4));
        
        // Test powExp (element^power)
        Sequence powExp = seq.powExp(2);
        assertEquals(3, powExp.size());
        assertEquals(Integer.valueOf(4), powExp.get(0)); // 2^2
        assertEquals(Integer.valueOf(9), powExp.get(1)); // 3^2
        assertEquals(Integer.valueOf(16), powExp.get(2)); // 4^2
        
        // Test powBase (base^element)
        Sequence powBase = seq.powBase(2);
        assertEquals(3, powBase.size());
        assertEquals(Integer.valueOf(4), powBase.get(0)); // 2^2
        assertEquals(Integer.valueOf(8), powBase.get(1)); // 2^3
        assertEquals(Integer.valueOf(16), powBase.get(2)); // 2^4
    }
    
    // ===== MATHEMATICAL ANALYSIS TESTS =====
    
    @Test
    public void testMinMax() {
        Sequence seq = new Sequence(Arrays.asList(5, 1, 9, 3, 7));
        
        assertEquals(1, seq.getMin());
        assertEquals(9, seq.getMax());
        assertEquals(9, seq.rangeSize()); // max-min+1 = 9-1+1 = 9
    }
    
    @Test
    public void testStatistics() {
        Sequence seq = new Sequence(Arrays.asList(1, 2, 3, 4, 5));
        
        assertEquals(15, seq.sum());
        assertEquals(3.0, seq.getMean(), 0.001);
        assertEquals(Math.sqrt(2.0), seq.getStdDev(), 0.001);
    }
    
    @Test
    public void testSequenceProperties() {
        Sequence seq = new Sequence(Arrays.asList(1, 2, 3, 1, 2, 3));
        
        // Test period detection
        assertEquals(3, seq.getPeriod());
        
        // Test natural sequence detection
        Sequence natural = new Sequence(Arrays.asList(1, 2, 3));
        assertTrue(natural.isNatural());
        
        Sequence notNatural = new Sequence(Arrays.asList(-1, 2, 3));
        assertFalse(notNatural.isNatural());
    }
    
    // ===== SEQUENCE MANIPULATION TESTS =====
    
    @Test
    public void testJuxtapose() {
        Sequence seq1 = new Sequence(Arrays.asList(1, 2, 3));
        Sequence seq2 = new Sequence(Arrays.asList(4, 5));
        
        Sequence result = seq1.juxtapose(seq2);
        assertEquals(5, result.size());
        assertEquals(Integer.valueOf(1), result.get(0));
        assertEquals(Integer.valueOf(2), result.get(1));
        assertEquals(Integer.valueOf(3), result.get(2));
        assertEquals(Integer.valueOf(4), result.get(3));
        assertEquals(Integer.valueOf(5), result.get(4));
    }
    
    @Test
    public void testDifference() {
        Sequence seq = new Sequence(Arrays.asList(1, 4, 7, 11));
        
        Sequence diff = seq.difference();
        assertEquals(3, diff.size()); // n-1 elements
        assertEquals(Integer.valueOf(3), diff.get(0)); // 4-1
        assertEquals(Integer.valueOf(3), diff.get(1)); // 7-4
        assertEquals(Integer.valueOf(4), diff.get(2)); // 11-7
        
        Sequence cyclicalDiff = seq.cyclicalDifference();
        assertEquals(4, cyclicalDiff.size()); // same size
        assertEquals(Integer.valueOf(3), cyclicalDiff.get(0)); // 4-1
        assertEquals(Integer.valueOf(3), cyclicalDiff.get(1)); // 7-4
        assertEquals(Integer.valueOf(4), cyclicalDiff.get(2)); // 11-7
        assertEquals(Integer.valueOf(-10), cyclicalDiff.get(3)); // 1-11 (cyclical)
    }
    
    @Test
    public void testDistinctAndCount() {
        Sequence seq = new Sequence(Arrays.asList(1, 2, 2, 3, 1, 4));
        
        Set<Integer> distinct = seq.distinct();
        assertEquals(4, distinct.size());
        assertTrue(distinct.contains(1));
        assertTrue(distinct.contains(2));
        assertTrue(distinct.contains(3));
        assertTrue(distinct.contains(4));
        
        assertEquals(2, seq.count(1));
        assertEquals(2, seq.count(2));
        assertEquals(1, seq.count(3));
        assertEquals(1, seq.count(4));
        assertEquals(0, seq.count(5));
    }
    
    @Test
    public void testFrequencyMap() {
        Sequence seq = new Sequence(Arrays.asList(1, 2, 2, 3, 1));
        TreeMap<Integer, Integer> freqMap = seq.frequencyMap();
        
        assertEquals(3, freqMap.size());
        assertEquals(Integer.valueOf(2), freqMap.get(1));
        assertEquals(Integer.valueOf(2), freqMap.get(2));
        assertEquals(Integer.valueOf(1), freqMap.get(3));
    }
    
    @Test
    public void testCopy() {
        Sequence original = new Sequence(Arrays.asList(1, 2, 3));
        Sequence copy = original.copy();
        
        assertEquals(original.size(), copy.size());
        assertEquals(original.get(0), copy.get(0));
        assertEquals(original.get(1), copy.get(1));
        assertEquals(original.get(2), copy.get(2));
        
        // Modify copy and ensure original is unchanged
        copy.set(0, 999);
        assertEquals(Integer.valueOf(1), original.get(0));
        assertEquals(Integer.valueOf(999), copy.get(0));
    }
    
    // ===== MAPPING AND EQUIVALENCE TESTS =====
    
    @Test
    public void testMapping() {
        Sequence seq = new Sequence(Arrays.asList(1, 2, 3, 2, 1));
        TreeMap<Integer, Integer> map = new TreeMap<>();
        map.put(1, 10);
        map.put(2, 20);
        map.put(3, 30);
        
        Sequence mapped = seq.map(map);
        assertEquals(5, mapped.size());
        assertEquals(Integer.valueOf(10), mapped.get(0));
        assertEquals(Integer.valueOf(20), mapped.get(1));
        assertEquals(Integer.valueOf(30), mapped.get(2));
        assertEquals(Integer.valueOf(20), mapped.get(3));
        assertEquals(Integer.valueOf(10), mapped.get(4));
    }
    
    @Test
    public void testEquivalentUnderRotation() {
        Sequence seq1 = new Sequence(Arrays.asList(1, 2, 3, 4));
        Sequence seq2 = new Sequence(Arrays.asList(3, 4, 1, 2)); // seq1 rotated by 2
        Sequence seq3 = new Sequence(Arrays.asList(1, 2, 3, 5)); // different sequence
        
        assertTrue(Sequence.equivalentUnderRotation(seq1, seq2));
        assertFalse(Sequence.equivalentUnderRotation(seq1, seq3));
        
        Integer shift = Sequence.equivalenceShift(seq1, seq2);
        assertEquals(Integer.valueOf(2), shift);
        
        Integer noShift = Sequence.equivalenceShift(seq1, seq3);
        assertNull(noShift);
    }
    
    @Test
    public void testMapTo() {
        Sequence seq1 = new Sequence(Arrays.asList(1, 2, 1, 3));
        Sequence seq2 = new Sequence(Arrays.asList(10, 20, 10, 30));
        
        Map<Integer, Integer> mapping = seq1.mapTo(seq2);
        assertNotNull(mapping);
        assertEquals(Integer.valueOf(10), mapping.get(1));
        assertEquals(Integer.valueOf(20), mapping.get(2));
        assertEquals(Integer.valueOf(30), mapping.get(3));
        
        assertTrue(seq1.existsMapTo(seq2));
        
        // Test inconsistent mapping - same source maps to different targets
        Sequence seq3 = new Sequence(Arrays.asList(1, 2, 1, 2));
        Sequence seq4 = new Sequence(Arrays.asList(10, 20, 10, 30)); // 1->10, 2->20, 1->10, 2->30 - inconsistent for 2
        Map<Integer, Integer> badMapping = seq3.mapTo(seq4);
        assertNull(badMapping); // Should fail because 2 maps to both 20 and 30
        
        // Test valid consistent mapping
        Sequence seq5 = new Sequence(Arrays.asList(1, 2, 1, 2));
        Sequence seq6 = new Sequence(Arrays.asList(10, 30, 10, 30)); // 1->10, 2->30 consistently
        Map<Integer, Integer> goodMapping = seq5.mapTo(seq6);
        assertNotNull(goodMapping); // Should succeed
        assertEquals(Integer.valueOf(10), goodMapping.get(1));
        assertEquals(Integer.valueOf(30), goodMapping.get(2));
    }
    
    // ===== ADVANCED OPERATIONS TESTS =====
    
    @Test
    public void testHoldAndExtract() {
        Sequence seq = new Sequence(Arrays.asList(1, 2, 3));
        BinaryNatural rhythm = new BinaryNatural(new Boolean[]{true, false, true, true, false});
        
        Sequence held = seq.hold(rhythm);
        assertEquals(5, held.size());
        assertEquals(Integer.valueOf(1), held.get(0)); // first element
        assertEquals(Integer.valueOf(1), held.get(1)); // hold previous
        assertEquals(Integer.valueOf(2), held.get(2)); // next element  
        assertEquals(Integer.valueOf(3), held.get(3)); // next element
        assertEquals(Integer.valueOf(3), held.get(4)); // hold previous
        
        // Test extract
        Sequence original = new Sequence(Arrays.asList(10, 20, 30, 40, 50));
        BinaryNatural extractPattern = new BinaryNatural(new Boolean[]{true, false, true, false, true});
        
        Sequence extracted = original.extract(extractPattern);
        assertEquals(3, extracted.size());
        assertEquals(Integer.valueOf(10), extracted.get(0));
        assertEquals(Integer.valueOf(30), extracted.get(1));
        assertEquals(Integer.valueOf(50), extracted.get(2));
    }
    
    @Test
    public void testConvolution() {
        Sequence seq = new Sequence(Arrays.asList(1, 2, 3));
        Sequence impulse = new Sequence(Arrays.asList(1, 1));
        
        Sequence convolved = seq.convolveWith(impulse);
        assertEquals(3, convolved.size());
        // Convolution with circular wrap
        assertEquals(Integer.valueOf(4), convolved.get(0)); // 1*1 + 3*1 = 4 (circular)
        assertEquals(Integer.valueOf(3), convolved.get(1)); // 2*1 + 1*1 = 3
        assertEquals(Integer.valueOf(5), convolved.get(2)); // 3*1 + 2*1 = 5
    }
    
    @Test
    public void testToString() {
        Sequence seq = new Sequence(Arrays.asList(1, 2, 3));
        
        String regular = seq.toString();
        assertEquals("1 2 3", regular);
        
        String json = seq.toString(true);
        assertEquals("[1, 2, 3]", json);
    }
    
    @Test
    public void testComparison() {
        Sequence seq1 = new Sequence(Arrays.asList(1, 2, 3));
        Sequence seq2 = new Sequence(Arrays.asList(1, 2, 3));
        Sequence seq3 = new Sequence(Arrays.asList(1, 2, 4));
        
        assertEquals(0, seq1.compareTo(seq2));
        assertTrue(seq1.compareTo(seq3) < 0);
        assertTrue(seq3.compareTo(seq1) > 0);
    }
}