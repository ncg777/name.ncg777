package name.NicolasCoutureGrenier.Maths.DataStructures;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JaggedListTests {
    private JaggedList<String> jaggedList;

    @Before
    public void setUp() throws Exception {
        jaggedList = new JaggedList<>();
    }

    @After
    public void tearDown() throws Exception {
        jaggedList = null;
    }

    @Test
    public final void testInit() {
        jaggedList.init(3, 2); // 3 rows, 2 columns
        assertEquals(3, jaggedList.size());
        for (int i = 0; i < 3; i++) {
            assertNotNull(jaggedList.get(i));
        }
    }

    @Test
    public final void testSetIntT() {
        jaggedList.init(3);
        jaggedList.set(0, "First");
        assertEquals("First", jaggedList.getValue(0));
    }

    @Test
    public final void testSetIntJaggedListOfT() {
        jaggedList.init(3);
        JaggedList<String> child = new JaggedList<>("ChildValue");
        jaggedList.set(1, child);
        assertEquals("ChildValue", jaggedList.get(1).getValue());
    }

    @Test
    public final void testAdd() {
        jaggedList.add("NewChild");
        assertEquals("NewChild", jaggedList.getValue(0)); // Assuming new child is added at index 0
    }

    @Test
    public final void testNewChild() {
        JaggedList<String> child = jaggedList.newChild();
        assertNotNull(child);
        assertTrue(jaggedList.size() > 0); // Ensures a child was added
    }

    @Test
    public final void testAddChild() {
        JaggedList<String> child = new JaggedList<>("ChildValue");
        jaggedList.addChild(child);
        assertEquals(1, jaggedList.size());
        assertEquals("ChildValue", jaggedList.get(0).getValue());
    }

    @Test
    public final void testGet() {
        jaggedList.init(2);
        jaggedList.set(0, "FirstValue");
        assertEquals("FirstValue", jaggedList.get(0).getValue());
    }

    @Test
    public final void testSetTIntArray() {
        jaggedList.init(2);
        jaggedList.set("Value1", 0);
        jaggedList.set("Value2", 1);
        assertEquals("Value1", jaggedList.getValue(0));
        assertEquals("Value2", jaggedList.getValue(1));
    }

    @Test
    public final void testIsRoot() {
        assertTrue(jaggedList.isRoot());
    }

    @Test
    public final void testGetRoot() {
        assertEquals(jaggedList, jaggedList.getRoot());
    }

    @Test
    public final void testToJSONArrayString() {
        jaggedList.add("TestValue");
        String jsonString = jaggedList.toJSONArrayString(Object::toString);
        assertTrue(jsonString.contains("TestValue"));
    }

    @Test
    public final void testParseJSONArray() {
        String jsonArray = "[\"Value1\", \"Value2\"]";
        JaggedList<String> parsedList = JaggedList.parseJSONArray(jsonArray, String::new);
        assertNotNull(parsedList);
        assertEquals("Value1", parsedList.getValue(0));
        assertEquals("Value2", parsedList.getValue(1));
    }

    @Test
    public final void testFromArray() {
        Object[] array = { "One", "Two", "Three" };
        JaggedList<String> fromArrayList = JaggedList.fromArray(array);
        assertEquals("One", fromArrayList.getValue(0));
        assertEquals("Two", fromArrayList.getValue(1));
        assertEquals("Three", fromArrayList.getValue(2));
    }

    @Test
    public final void testToArray() {
        jaggedList.add("First");
        jaggedList.add("Second");
        Object[] array = jaggedList.toArray();
        assertArrayEquals(new Object[]{"First", "Second"}, array);
    }
}
