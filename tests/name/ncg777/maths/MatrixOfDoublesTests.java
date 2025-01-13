package name.ncg777.maths;

import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.MatcherAssert.assertThat;

import junit.framework.TestCase;
import java.util.List;

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
}