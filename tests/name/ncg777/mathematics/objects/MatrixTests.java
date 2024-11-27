package name.ncg777.mathematics.objects;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class MatrixTests {

    private Matrix<Integer> matrix;

    @Before
    public void setUp() throws Exception {
        matrix = new Matrix<>(3, 3); // 3x3 matrix initialized
    }

    @After
    public void tearDown() throws Exception {
        matrix = null; // Cleanup
    }

    @Test
    public final void testColumnCount() {
        assertEquals(3, matrix.columnCount());
    }

    @Test
    public final void testRowCount() {
        assertEquals(3, matrix.rowCount());
    }

    @Test
    public final void testMatrixTArrayArray() {
        Integer[][] arr = { {1, 2, 3}, {4, 5, 6}, {7, 8, 9} };
        Matrix<Integer> matrixFromArr = new Matrix<>(arr);
        assertEquals(3, matrixFromArr.rowCount());
        assertEquals(3, matrixFromArr.columnCount());
        assertEquals(Integer.valueOf(5), matrixFromArr.get(1, 1)); // Check the center element
    }

    @Test
    public final void testMatrixMatrixOfT() {
        Matrix<Integer> copyMatrix = new Matrix<>(matrix); // Copy constructor
        assertEquals(matrix.rowCount(), copyMatrix.rowCount());
        assertEquals(matrix.columnCount(), copyMatrix.columnCount());
    }

    @Test
    public final void testGetAndSet() {
        matrix.set(1, 1, 10); // Set middle element to 10
        assertEquals(Integer.valueOf(10), matrix.get(1, 1));
        
        matrix.set(0, 0, 5);
        assertEquals(Integer.valueOf(5), matrix.get(0, 0));
    }

    @Test
    public final void testClear() {
        matrix.set(0, 0, 1);
        matrix.clear();
        assertEquals(null, matrix.get(0, 0)); // Default is null after clear
    }

    @Test
    public final void testAppendRowListOfT() {
        List<Integer> newRow = Arrays.asList(10, 11, 12);
        matrix.appendRow(newRow);
        assertEquals(4, matrix.rowCount()); // Should now have 4 rows
        assertEquals(Integer.valueOf(10), matrix.get(3, 0)); // First element of new row
    }

    @Test
    public final void testRemoveRow() {
        matrix.set(0, 0, 1);
        matrix.removeRow(0);
        assertEquals(null, matrix.get(0, 0)); // After removal, should be null
    }

    @Test
    public final void testGetTranspose() {
        Integer[][] arr = { {1, 2}, {3, 4}, {5, 6} };
        Matrix<Integer> transposedMatrix = new Matrix<>(arr).getTranspose();
        assertEquals(2, transposedMatrix.rowCount()); // Transpose should now have 2 rows
        assertEquals(3, transposedMatrix.columnCount()); // Transpose should have 3 columns
        assertEquals(Integer.valueOf(2), transposedMatrix.get(0, 1)); // Check value in transposed position
    }

    @Test
    public final void testContains() {
        matrix.set(0, 0, 1);
        assertTrue(matrix.contains(1));
        assertFalse(matrix.contains(2)); // Should not contain a value that hasn't been added
    }

    // Additional test methods can be added below...

}
