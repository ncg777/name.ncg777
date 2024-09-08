package name.NicolasCoutureGrenier.CS.DataStructures;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import name.NicolasCoutureGrenier.CS.DataStructures.SparseList;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class SparseListTests {

    private SparseList<String> list;

    @Before
    public void setUp() throws Exception {
        list = new SparseList<>();
    }

    @After
    public void tearDown() throws Exception {
        list.clear();
    }

    @Test
    public void testSparseList() {
        SparseList<Integer> emptyList = new SparseList<>();
        assertEquals(0, emptyList.size());
    }

    @Test
    public void testSize() {
        assertEquals(0, list.size());
        list.add("one");
        assertEquals(1, list.size());
        list.add("two");
        assertEquals(2, list.size());
    }

    @Test
    public void testResize() {
        list.add("one");
        list.add("two");
        list.resize(5);
        assertEquals(5, list.size());
        assertEquals("one", list.get(0));
        assertEquals("two", list.get(1));
        list.resize(1);
        assertEquals(1, list.size());
        assertEquals("one", list.get(0));
    }

    @Test(expected = RuntimeException.class)
    public void testResizeNegative() {
        list.resize(-1);
    }

    @Test
    public void testIsEmpty() {
        assertTrue(list.isEmpty());
        list.add("one");
        assertFalse(list.isEmpty());
    }

    @Test
    public void testContains() {
        list.add("one");
        assertTrue(list.contains("one"));
        assertFalse(list.contains("two"));
    }

    @Test
    public void testIterator() {
        list.add("one");
        list.add("two");
        int count = 0;
        for (String item : list) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void testToArray() {
        list.add("one");
        list.add("two");
        Object[] array = list.toArray();
        assertArrayEquals(new Object[] {"one", "two"}, array);
    }

    @Test
    public void testToArrayUArray() {
        list.add("one");
        list.add("two");
        String[] array = list.toArray(new String[0]);
        assertArrayEquals(new String[] {"one", "two"}, array);
    }

    @Test
    public void testAddT() {
        assertTrue(list.add("one"));
        assertEquals(1, list.size());
        assertTrue(list.add("two"));
        assertEquals(2, list.size());
    }

    @Test
    public void testRemoveObject() {
        list.add("one");
        list.add("two");
        assertTrue(list.remove("one"));
        assertEquals(1, list.size());
        assertFalse(list.remove("three"));
    }

    @Test
    public void testContainsAll() {
        Collection<String> collection = Arrays.asList("one", "two");
        list.add("one");
        list.add("two");
        assertTrue(list.containsAll(collection));
        collection = Arrays.asList("one", "three");
        assertFalse(list.containsAll(collection));
    }

    @Test
    public void testAddAllCollectionOfQextendsT() {
        Collection<String> collection = Arrays.asList("one", "two", "three");
        assertTrue(list.addAll(collection));
        assertEquals(3, list.size());
        assertTrue(list.contains("one"));
        assertTrue(list.contains("three"));
    }

    @Test
    public void testAddAllIntCollectionOfQextendsT() {
        list.add("one");
        Collection<String> collection = Arrays.asList("two", "three");
        list.addAll(1, collection);
        assertEquals(3, list.size());
        assertEquals("one", list.get(0));
        assertEquals("two", list.get(1));
        assertEquals("three", list.get(2));
    }

    @Test
    public void testRemoveAll() {
        list.add("one");
        list.add("two");
        list.add("three");
        Collection<String> collection = Arrays.asList("one", "two");
        assertTrue(list.removeAll(collection));
        assertEquals(1, list.size());
        assertTrue(list.contains("three"));
        assertFalse(list.contains("one"));
    }

    @Test
    public void testRetainAll() {
        list.add("one");
        list.add("two");
        list.add("three");
        Collection<String> collection = Arrays.asList("one", "three");
        assertTrue(list.retainAll(collection));
        assertEquals(2, list.size());
        assertTrue(list.contains("one"));
        assertTrue(list.contains("three"));
        assertFalse(list.contains("two"));
    }

    @Test
    public void testClear() {
        list.add("one");
        list.add("two");
        list.clear();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    public void testGet() {
        list.add("one");
        list.add("two");
        assertEquals("one", list.get(0));
        assertEquals("two", list.get(1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetOutOfBounds() {
        list.get(0); // Should throw exception since list is empty
    }

    @Test
    public void testSet() {
        list.add("one");
        list.set(0, "updated");
        assertEquals("updated", list.get(0));
        assertEquals(1, list.size());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetOutOfBounds() {
        list.set(0, "out of bounds"); // Should throw exception since list is empty
    }

    @Test
    public void testAddIntT() {
        list.add("one");
        list.add(0, "zero");
        assertEquals(2, list.size());
        assertEquals("zero", list.get(0));
        assertEquals("one", list.get(1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testAddIntTOutOfBounds() {
        list.add(1, "too far"); // Should throw exception since index 1 is out of bounds for an empty list
    }

    @Test
    public void testRemoveInt() {
        list.add("one");
        list.add("two");
        assertEquals("one", list.remove(0));
        assertEquals(1, list.size());
        assertEquals("two", list.get(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveIntOutOfBounds() {
        list.remove(0); // Should throw exception since list is empty
    }

    @Test
    public void testIndexOf() {
        list.add("one");
        list.add("two");
        assertEquals(0, list.indexOf("one"));
        assertEquals(1, list.indexOf("two"));
        assertEquals(-1, list.indexOf("three")); // Not found
    }

    @Test
    public void testLastIndexOf() {
        list.add("one");
        list.add("two");
        assertEquals(1, list.lastIndexOf("two"));
        list.add("two");
        assertEquals(2, list.lastIndexOf("two")); // Last occurrence
        assertEquals(-1, list.lastIndexOf("three")); // Not found
    }

    @Test
    public void testListIterator() {
        list.add("one");
        list.add("two");
        ListIterator<String> iterator = list.listIterator();
        assertTrue(iterator.hasNext());
        assertEquals("one", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("two", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testListIteratorInt() {
        list.add("one");
        list.add("two");
        ListIterator<String> iterator = list.listIterator(1);
        assertTrue(iterator.hasPrevious());
        assertEquals("one", iterator.previous());
        assertFalse(iterator.hasPrevious());
    }

    @Test
    public void testSubList() {
        list.add("one");
        list.add("two");
        list.add("three");
        List<String> subList = list.subList(0, 2);
        assertEquals(2, subList.size());
        assertEquals("one", subList.get(0));
        assertEquals("two", subList.get(1));
    }
}