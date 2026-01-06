package name.ncg777.maths;

import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.MatcherAssert.assertThat;

import junit.framework.TestCase;
import java.util.List;
import java.util.Random;

public class MatrixOfDoublesTests extends TestCase{

  private MatrixOfDoubles matrix;
  
  @Override
  protected void setUp() throws Exception {
      matrix = new MatrixOfDoubles(new Double[][]{{1.0, 2.0}, {3.0, 4.0}});
  }

  public void testProduct() {
      MatrixOfDoubles other = new MatrixOfDoubles(new Double[][]{{5.0, 6.0}, {7.0, 8.0}});
      MatrixOfDoubles result = matrix.product(other);
      assertThat(result.toDoubleArray(), is(new Double[][]{{19.0, 22.0}, {43.0, 50.0}}));
  }

  public void testComputeDeterminant() {
      Double determinant = matrix.computeDeterminant();
      assertThat(determinant, is(-2.0));
  }

  public void testComputeEigenvalues() {
      List<Double> eigenvalues = matrix.computeEigenvalues(100, 1e-10);
      assertThat(Double.valueOf(eigenvalues.get(0)*10000).intValue(), is(53722));
      assertThat(Double.valueOf(eigenvalues.get(1)*10000).intValue(), is(-3722));
  }  

  public void testGenerateDoublyStochasticMatrix() {
      int size = 5;
      MatrixOfDoubles m = MatrixOfDoubles.generateDoublyStochasticMatrix(size, 10, new Random(1234));
      double[][] arr = m.toDoubleArray();
      double epsilon = 1e-9;

      for (int i = 0; i < size; i++) {
          double rowSum = 0.0;
          for (int j = 0; j < size; j++) {
              double v = arr[i][j];
              assertTrue("Entry is negative at (" + i + "," + j + ")", v >= -epsilon);
              rowSum += v;
          }
          assertTrue("Row sum not close to 1 at row " + i + ": " + rowSum, Math.abs(rowSum - 1.0) <= 1e-8);
      }

      for (int j = 0; j < size; j++) {
          double colSum = 0.0;
          for (int i = 0; i < size; i++) {
              colSum += arr[i][j];
          }
          assertTrue("Column sum not close to 1 at column " + j + ": " + colSum, Math.abs(colSum - 1.0) <= 1e-8);
      }
  }
}
