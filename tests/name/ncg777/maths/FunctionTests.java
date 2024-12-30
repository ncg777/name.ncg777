package name.ncg777.maths;

import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.function.Function;

import junit.framework.TestCase;
public class FunctionTests extends TestCase{

  @Override
  protected void setUp() throws Exception { }

  public void testNumericalGradient() {
    Function<VectorOfDoubles,VectorOfDoubles> function = (VectorOfDoubles x) -> VectorOfDoubles.of(x.get(0) * x.get(0), x.get(1) * x.get(1));
    var gradient = Functions.numericalGradient(function, 1e-5).apply(VectorOfDoubles.of(1.0, 2.0));
    assertThat(gradient.get(0).intValue(), is(2));
    assertThat(gradient.get(1).intValue(), is(4));
  }

  public void testNumericalJacobian() {
    Function<VectorOfDoubles,VectorOfDoubles> function = (VectorOfDoubles x) -> VectorOfDoubles.of(x.get(0) + x.get(1), x.get(0) * x.get(1));
    var jacobian = Functions.numericalJacobian(function, 1e-5).apply(VectorOfDoubles.of(1.0, 2.0));
    assertThat(
        jacobian.isEqual(
            new MatrixOfDoubles(new Double[][]{{1.0, 1.0},{2.0, 1.0}}), 
            0.00001), 
        is(true));
  }

  public void testNumericalHessian() {
    int u = 2;
    int v = 3;
    
    Function<VectorOfDoubles, VectorOfDoubles> function = (VectorOfDoubles x) -> VectorOfDoubles.of(x.get(0) * x.get(0) + x.get(1), x.get(1) * x.get(1) + x.get(0), x.get(0));
    
    Double[][][] hessian = Functions.numericalHessian(function, 1e-5).apply(VectorOfDoubles.of(1.0, 2.0));
    
    assertThat(hessian.length, is(v));
    assertThat(hessian[0].length, is(u));
    assertThat(hessian[0][0].length, is(u));
  }
}
