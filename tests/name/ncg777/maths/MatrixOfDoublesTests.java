package name.ncg777.maths;

import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.function.Function;

import junit.framework.TestCase;
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
      double[] eigenvalues = matrix.computeEigenvalues(100, 1e-10);
      assertThat(Double.valueOf(eigenvalues[0]*10000).intValue(), is(53722));
      assertThat(Double.valueOf(eigenvalues[1]*10000).intValue(), is(-3722));
  }

  public void testComputeGradient() {
    Function<VectorOfDoubles,VectorOfDoubles> function = (VectorOfDoubles x) -> VectorOfDoubles.of(x.get(0) * x.get(0), x.get(1) * x.get(1));
    var gradient = matrix.computeGradient(function, VectorOfDoubles.of(1.0, 2.0), 1e-5).toDoubleArray();
    assertThat(gradient[0].intValue(), is(2));
    assertThat(gradient[1].intValue(), is(4));
  }

  public void testComputeJacobian() {
    Function<VectorOfDoubles,VectorOfDoubles> function = (VectorOfDoubles x) -> VectorOfDoubles.of(x.get(0) + x.get(1), x.get(0) * x.get(1));
    var jacobian = matrix.computeJacobian(function, VectorOfDoubles.of(1.0, 2.0), 1e-5);
    assertThat(
        jacobian.isEqual(
            new MatrixOfDoubles(new Double[][]{{1.0, 1.0},{2.0, 1.0}}), 
            0.00001), 
        is(true));
  }

  public void testComputeHessian() {
    int u = 2;
    int v = 3;
    
    Function<VectorOfDoubles, VectorOfDoubles> function = (VectorOfDoubles x) -> VectorOfDoubles.of(x.get(0) * x.get(0) + x.get(1), x.get(1) * x.get(1) + x.get(0), x.get(0));
    
    Double[][][] hessian = matrix.computeHessian(function, VectorOfDoubles.of(1.0, 2.0), 1e-5);
    
    assertThat(hessian.length, is(v));
    assertThat(hessian[0].length, is(u));
    assertThat(hessian[0][0].length, is(u));
  }
}
