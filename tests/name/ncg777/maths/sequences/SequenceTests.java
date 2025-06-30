package name.ncg777.maths.sequences;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
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
}