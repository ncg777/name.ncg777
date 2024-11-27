package name.ncg777.mathematics.enumerations;

import static org.junit.Assert.*;

import java.util.NoSuchElementException;

import org.junit.Test;

import junit.framework.TestCase;

public class FixedSetPartitionEnumerationTests extends TestCase {

    @Test
    public void testNextElement_n4_k2() {
        FixedSetPartitionEnumeration enumeration = new FixedSetPartitionEnumeration(4, 2);
        assertArrayEquals(new int[]{0, 0, 0, 1}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 0, 1, 0}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 0, 1, 1}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 0, 0}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 0, 1}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 1, 0}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 1, 1}, enumeration.nextElement());
        assertThrows(NoSuchElementException.class, enumeration::nextElement);
    }

    @Test
    public void testNextElement_n5_k3() {
        FixedSetPartitionEnumeration enumeration = new FixedSetPartitionEnumeration(5, 3);
        assertArrayEquals(new int[]{0, 0, 0, 1, 2}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 0, 1, 0, 2}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 0, 1, 1, 2}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 0, 1, 2, 0}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 0, 1, 2, 1}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 0, 1, 2, 2}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 0, 0, 2}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 0, 1, 2}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 0, 2, 0}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 0, 2, 1}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 0, 2, 2}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 1, 0, 2}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 1, 1, 2}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 1, 2, 0}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 1, 2, 1}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 1, 2, 2}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 2, 0, 0}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 2, 0, 1}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 2, 0, 2}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 2, 1, 0}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 2, 1, 1}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 2, 1, 2}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 2, 2, 0}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 2, 2, 1}, enumeration.nextElement());
        assertArrayEquals(new int[]{0, 1, 2, 2, 2}, enumeration.nextElement());
        assertThrows(NoSuchElementException.class, enumeration::nextElement);
    }
}