package name.ncg777.maths;

import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.function.Function;

import junit.framework.TestCase;
public class FunctionTests extends TestCase{

  @Override
  protected void setUp() throws Exception { }

  public void testNumericalGradient() {
    Function<List<Double>,Double> function = (List<Double> x) -> x.get(0) * x.get(0) + x.get(1) * x.get(1);
    var gradient = Functions.numericalGradient(function).apply(List.of(1.0, 2.0));
    assertThat(gradient.get(0).intValue(), is(2));
    assertThat(gradient.get(1).intValue(), is(4));
  }

  public void testNumericalJacobian() {
    Function<List<Double>,List<Double>> function = (List<Double> x) -> List.of(x.get(0) + x.get(1), x.get(0) * x.get(1));
    var jacobian = Functions.numericalJacobian(function).apply(List.of(1.0, 2.0));
    assertThat(
        jacobian.isEqual(
            new MatrixOfDoubles(new Double[][]{{1.0, 1.0},{2.0, 1.0}}), 
            0.00001), 
        is(true));
  }

  public void testNumericalHessian() {
    int u = 2;
    int v = 3;
    
    Function<List<Double>, List<Double>> function = (List<Double> x) -> List.of(x.get(0) * x.get(0) + x.get(1), x.get(1) * x.get(1) + x.get(0), x.get(0));
    
    Double[][][] hessian = Functions.numericalHessian(function).apply(List.of(1.0, 2.0));
    
    assertThat(hessian.length, is(v));
    assertThat(hessian[0].length, is(u));
    assertThat(hessian[0][0].length, is(u));
  }
}
