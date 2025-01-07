package name.ncg777.maths;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Functions {
  public static BiFunction<Double,Double,Double> ROND = (x,t) -> Math.signum(x)*
      (
        1.0 - 
        Math.pow(
            1.0 - Math.pow(x, Math.sqrt(t)/Math.sqrt(1-t)),
            Math.sqrt(1-t)/Math.sqrt(t)
          )
      );
  public final static double DEFAULT_EPSILON = 1e-10;
  

  public static double normL2(double... x) {
      double sum = 0.0;
      for (double xi : x)
          sum += xi * xi;
      return Math.sqrt(sum);
  }
  
  public static Function<List<Double>, List<Double>> numericalGradient(Function<List<Double>, Double> f) {
    return numericalGradient(f, DEFAULT_EPSILON);
  }
  /**
   * Computes a numerical approximation of the gradient vector of a scalar-valued function.
   *
   * @param f       the scalar-valued function to differentiate, mapping from {@code List<Double>} to {@code Double}.
   * @param epsilon the small perturbation value used for finite difference approximation.
   * @return a function that computes the gradient vector for an input vector.
   * @throws IllegalArgumentException if {@code epsilon} is non-positive.
   */
  public static Function<List<Double>, List<Double>> numericalGradient(Function<List<Double>, Double> f, double epsilon) {
      if (epsilon <= 0) {
          throw new IllegalArgumentException("Epsilon must be positive.");
      }
      return (List<Double> x) -> {
          return numericalJacobian((z) -> List.of(f.apply(z)), epsilon).apply(x).getRowVector(0);
      };      
  }

  public static Function<List<Double>,MatrixOfDoubles> numericalJacobian(Function<List<Double>, List<Double>> F) {
    return numericalJacobian(F, DEFAULT_EPSILON);
  }
  /**
   * Computes a numerical approximation of the Jacobian matrix for a given vector-valued function.
   * <p>
   * The Jacobian matrix is computed using the central difference method, where small perturbations 
   * are applied to each input dimension to approximate partial derivatives.
   * </p>
   *
   * @param F       the vector-valued function to differentiate, mapping from {@code List<Double>} 
   *                to {@code List<Double>}.
   * @param epsilon the small perturbation value used for finite difference approximation. 
   *                Must be a positive value to ensure accurate results.
   * @return a function that takes a {@code List<Double>} as input and returns a {@code MatrixOfDoubles}
   *         representing the Jacobian matrix at that input. The resulting matrix has dimensions 
   *         {@code outputDim x inputDim}, where {@code outputDim} is the dimension of the function's output 
   *         and {@code inputDim} is the dimension of the function's input.
   * @throws IllegalArgumentException if {@code epsilon} is non-positive.
   * 
   * <p><b>Example Usage:</b></p>
   * <pre>
   * {@code
   * Function<List<Double>, List<Double>> F = (List<Double> x) -> List<Double>.of(new Double[] {
   *     x.get(0) * x.get(0), // x_1^2
   *     Math.sin(x.get(1))   // sin(x_2)
   * });
   * 
   * double epsilon = 1e-6;
   * Function<List<Double>, MatrixOfDoubles> jacobianFunction = numericalJacobian(F, epsilon);
   * 
   * List<Double> input = List<Double>.of(new Double[] { 2.0, Math.PI / 4 });
   * MatrixOfDoubles jacobian = jacobianFunction.apply(input);
   * System.out.println(jacobian); // Prints the numerical Jacobian matrix at the input.
   * }
   * </pre>
   *
   * <p><b>Notes:</b></p>
   * <ul>
   *   <li>The accuracy of the approximation depends on the choice of {@code epsilon}. 
   *       A smaller value may improve accuracy but can lead to numerical instability due to 
   *       floating-point precision issues.</li>
   *   <li>The function {@code F} should be well-defined and differentiable within a neighborhood 
   *       of the input vector to ensure meaningful results.</li>
   * </ul>
   */
  public static Function<List<Double>,MatrixOfDoubles> numericalJacobian(Function<List<Double>, List<Double>> F, double epsilon) {
    return (List<Double> x) -> {
      int inputDim = x.size();
      int outputDim = F.apply(x).size();
      Double[][] jacobian = new Double[outputDim][inputDim];

      for (int j = 0; j < inputDim; j++) {
        // Create perturbation vectors for the j-th input
        Double[] xPerturbPlus = x.toArray(new Double[0]).clone();
        Double[] xPerturbMinus = x.toArray(new Double[0]).clone();
        xPerturbPlus[j] += epsilon;
        xPerturbMinus[j] -= epsilon;

        // Evaluate F at perturbed points
        List<Double> fPlus = F.apply(List.of(xPerturbPlus));
        List<Double> fMinus = F.apply(List.of(xPerturbMinus));

        // Fill the Jacobian matrix column for the j-th input dimension
        for (int i = 0; i < outputDim; i++) {
          jacobian[i][j] = (fPlus.get(i) - fMinus.get(i)) / (2 * epsilon);
        }
      }

      return new MatrixOfDoubles(jacobian);  
    };
  }
  
  public static Function<List<Double>, Double[][][]> numericalHessian(Function<List<Double>, List<Double>> F) {
    return numericalHessian(F,DEFAULT_EPSILON);
  }

  /**
   * Computes a numerical approximation of the Hessian matrices for a vector-valued function.
   * <p>
   * The Hessian matrix is a second-order derivative matrix that provides information about the curvature of a function. 
   * For a vector-valued function {@code F: R^m -> R^n}, this method computes a Hessian matrix for each output dimension.
   * The computation uses the central difference method, applying small perturbations to the input dimensions.
   * </p>
   *
   * @param F       the vector-valued function to differentiate, mapping from {@code List<Double>} to {@code List<Double>}.
   * @param epsilon the small perturbation value used for finite difference approximation. 
   *                Must be a positive value to ensure accurate results.
   * @return a function that takes a {@code List<Double>} as input and returns a 3D array {@code Double[][][]} 
   *         representing the Hessian matrices. The array structure is {@code hessian[i][j][k]}, where:
   *         <ul>
   *             <li>{@code i} corresponds to the output dimension index.</li>
   *             <li>{@code j} and {@code k} correspond to the input dimensions.</li>
   *         </ul>
   *         Each Hessian matrix is of size {@code inputDim x inputDim}.
   * @throws IllegalArgumentException if {@code epsilon} is non-positive.
   *
   * <p><b>Example Usage:</b></p>
   * <pre>
   * {@code
   * Function<List<Double>, List<Double>> F = (List<Double> x) -> List<Double>.of(new Double[] {
   *     x.get(0) * x.get(0) + x.get(1),  // f1(x) = x_1^2 + x_2
   *     Math.sin(x.get(0) * x.get(1))    // f2(x) = sin(x_1 * x_2)
   * });
   * 
   * double epsilon = 1e-6;
   * Function<List<Double>, Double[][][]> hessianFunction = numericalHessian(F, epsilon);
   * 
   * List<Double> input = List<Double>.of(new Double[] { 1.0, 2.0 });
   * Double[][][] hessians = hessianFunction.apply(input);
   * 
   * // Hessian matrix for the first output function f1
   * Double[][] hessianF1 = hessians[0];
   * 
   * // Hessian matrix for the second output function f2
   * Double[][] hessianF2 = hessians[1];
   * }
   * </pre>
   *
   * <p><b>Notes:</b></p>
   * <ul>
   *   <li>The accuracy of the approximation depends on the choice of {@code epsilon}. A smaller value may improve accuracy
   *       but can lead to numerical instability due to floating-point precision issues.</li>
   *   <li>The function {@code F} should be twice differentiable in the neighborhood of the input vector for the Hessian
   *       approximation to be meaningful.</li>
   *   <li>The method is computationally expensive for high-dimensional inputs due to the need for multiple function evaluations.</li>
   * </ul>
   */
  public static Function<List<Double>, Double[][][]> numericalHessian(Function<List<Double>, List<Double>> F, double epsilon) {
    return (List<Double> x) -> {
      int inputDim = x.size();
      int outputDim = F.apply(x).size();
      
      Double[][][] hessian = new Double[outputDim][inputDim][inputDim];

      for (int i = 0; i < outputDim; i++) {
        for (int j = 0; j < inputDim; j++) {
          for (int k = 0; k < inputDim; k++) {
            // Perturbation vectors for the second-order partial derivatives
            Double[] xPerturbPlusPlus = x.toArray(new Double[0]).clone();
            Double[] xPerturbPlusMinus = x.toArray(new Double[0]).clone();
            Double[] xPerturbMinusPlus = x.toArray(new Double[0]).clone();
            Double[] xPerturbMinusMinus = x.toArray(new Double[0]).clone();

            xPerturbPlusPlus[j] += epsilon; xPerturbPlusPlus[k] += epsilon;
            xPerturbPlusMinus[j] += epsilon; xPerturbPlusMinus[k] -= epsilon;
            xPerturbMinusPlus[j] -= epsilon; xPerturbMinusPlus[k] += epsilon;
            xPerturbMinusMinus[j] -= epsilon; xPerturbMinusMinus[k] -= epsilon;

            // Evaluate F at the four perturbed points
            double fPlusPlus = F.apply(List.of(xPerturbPlusPlus)).get(i);
            double fPlusMinus = F.apply(List.of(xPerturbPlusMinus)).get(i);
            double fMinusPlus = F.apply(List.of(xPerturbMinusPlus)).get(i);
            double fMinusMinus = F.apply(List.of(xPerturbMinusMinus)).get(i);

            // Compute second-order partial derivative
            hessian[i][j][k] = (fPlusPlus - fPlusMinus - fMinusPlus + fMinusMinus) / (4 * epsilon * epsilon);
          }
        }
      }

      return hessian;  
    };
    
  }
}
