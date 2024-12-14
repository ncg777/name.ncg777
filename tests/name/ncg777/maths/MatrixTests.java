package name.ncg777.maths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;

public class MatrixTests {

    private Matrix<Integer> matrix;
    private Matrix<Integer> matrixA;
    private Matrix<Integer> matrixB;
    private BinaryOperator<Integer> sum;
    private BinaryOperator<Integer> product;
    
    @Before
    public void setUp() throws Exception {
        matrix = new Matrix<>(3, 3); // 3x3 matrix initialized
        matrixA = new Matrix<>(new Integer[][]{{1, 2}, {3, 4}});
        matrixB = new Matrix<>(new Integer[][]{{5, 6}, {7, 8}});
        sum = (a,b) -> a+b;
        product = (a, b) -> a * b;
    }

    @After
    public void tearDown() throws Exception {
        matrix = null; // Cleanup
    }

    @Test
    public final void testColumnCount() {
        assertThat(matrix.columnCount(), is(3));
    }

    @Test
    public final void testRowCount() {
        assertThat(matrix.rowCount(), is(3));
    }

    @Test
    public final void testMatrixTArrayArray() {
        Integer[][] arr = { {1, 2, 3}, {4, 5, 6}, {7, 8, 9} };
        Matrix<Integer> matrixFromArr = new Matrix<>(arr);
        assertThat(matrixFromArr.rowCount(), is(3));
        assertThat(matrixFromArr.columnCount(), is(3));
        assertThat(matrixFromArr.get(1, 1), is(Integer.valueOf(5))); // Check the center element
    }

    @Test
    public final void testMatrixMatrixOfT() {
        Matrix<Integer> copyMatrix = new Matrix<>(matrix); // Copy constructor
        assertThat(copyMatrix.rowCount(), is(matrix.rowCount()));
        assertThat(copyMatrix.columnCount(), is(matrix.columnCount()));
    }

    @Test
    public final void testGetAndSet() {
        matrix.set(1, 1, 10); // Set middle element to 10
        assertThat(matrix.get(1, 1), is(Integer.valueOf(10)));
        
        matrix.set(0, 0, 5);
        assertThat(matrix.get(0, 0), is(Integer.valueOf(5)));
    }

    @Test
    public final void testClear() {
        matrix.set(0, 0, 1);
        matrix.clear();
        assertThat(matrix.get(0, 0), nullValue()); // Default is null after clear
    }

    @Test
    public final void testAppendRowListOfT() {
        List<Integer> newRow = Arrays.asList(10, 11, 12);
        matrix.appendRow(newRow);
        assertThat(matrix.rowCount(), is(4)); // Should now have 4 rows
        assertThat(matrix.get(3, 0), is(Integer.valueOf(10))); // First element of new row
    }

    @Test
    public final void testRemoveRow() {
        matrix.set(0, 0, 1);
        matrix.removeRow(0);
        assertThat(matrix.get(0, 0), nullValue()); // After removal, should be null
    }

    @Test
    public final void testGetTranspose() {
        Integer[][] arr = { {1, 2}, {3, 4}, {5, 6} };
        Matrix<Integer> transposedMatrix = new Matrix<>(arr).getTranspose();
        assertThat(transposedMatrix.rowCount(), is(2)); // Transpose should now have 2 rows
        assertThat(transposedMatrix.columnCount(), is(3)); // Transpose should have 3 columns
        assertThat(transposedMatrix.get(0, 1), is(Integer.valueOf(2))); // Check value in transposed position
    }

    @Test
    public final void testContains() {
        matrix.set(0, 0, 1);
        assertThat(matrix.contains(1), is(true));
        assertThat(matrix.contains(2), is(false)); // Should not contain a value that hasn't been added
    }

    @Test
    public void testProductWithValidMatrices() {
        Matrix<Integer> result = matrixA.product(matrixB, 0, sum, product);
        assertThat(result.get(0, 0).intValue(), is(19));
        assertThat(result.get(0, 1).intValue(), is(22));
        assertThat(result.get(1, 0).intValue(), is(43));
        assertThat(result.get(1, 1).intValue(), is(50));
    }

    @Test
    public void testProductWithDifferentDimensions() {
        Matrix<Integer> matrixC = new Matrix<>(new Integer[][]{{1, 2, 3}});
        try {
            matrixA.product(matrixC, 0, sum, product);
        } catch (IllegalArgumentException e) {
            assertThat("Matrix dimensions do not match for multiplication", is(e.getMessage()));
        }
    }

    @Test
    public void testProductWithZeroInitValue() {
        Matrix<Integer> result = matrixA.product(matrixB, 0, sum, product);
        assertThat(result.get(0, 0),is(19));
    }

    @Test
    public void testProductWithNegativeValues() {
        Matrix<Integer> matrixD = new Matrix<>(new Integer[][]{{-1, -2}, {-3, -4}});
        Matrix<Integer> result = matrixA.product(matrixD, 0, sum, product);
        System.out.println(result);
        assertThat(result.get(0, 0), is(-7));
        assertThat(result.get(0, 1), is(-10));
        assertThat(result.get(1, 0), is(-15));
        assertThat(result.get(1, 1), is(-22));
    }
}
