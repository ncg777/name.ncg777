package name.ncg777.maths;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Functions {
  public static BiFunction<Double,Double,Double> ROND = (x,t) -> 1.0 - 
      Math.pow(1.0 - Math.pow(x, Math.sqrt(t)/Math.sqrt(1-t)),
      Math.sqrt(1-t)/Math.sqrt(t));
  
  
  /**
   * Computes the gradient of a vector-valued function F at a given point x.
   * 
   * The gradient is computed using the central difference method for each input dimension.
   * If F is vector-valued, the result is the sum of partial derivatives across all output dimensions.
   *
   * @param F       The function whose gradient is to be computed. It maps a vector to another vector.
   * @param x       The point at which to compute the gradient.
   * @param epsilon A small positive value used for finite difference perturbations.
   * @return A VectorOfDoubles representing the gradient of F at x.
   */
  public static Function<VectorOfDoubles,VectorOfDoubles> numericalGradient(Function<VectorOfDoubles, VectorOfDoubles> F, double epsilon) {
    return (VectorOfDoubles x) -> {
      int inputDim = x.getDimension();
      int outputDim = F.apply(x).getDimension();
      Double[] gradient = new Double[outputDim];

      for (int i = 0; i < inputDim; i++) {
     // Create perturbation vector for the i-th input
        Double[] xPerturbPlus = x.toDoubleArray().clone();
        Double[] xPerturbMinus = x.toDoubleArray().clone();

        xPerturbPlus[i] += epsilon;   // Positive perturbation
        xPerturbMinus[i] -= epsilon; // Negative perturbation

        // Evaluate F at perturbed points
        VectorOfDoubles fPlus = F.apply(VectorOfDoubles.of(xPerturbPlus));
        VectorOfDoubles fMinus = F.apply(VectorOfDoubles.of(xPerturbMinus));

        // Compute partial derivative for the i-th input dimension using central difference
        double partialDerivative = 0.0;
        for (int j = 0; j < outputDim; j++) {
            partialDerivative += (fPlus.get(j) - fMinus.get(j)) / (2 * epsilon);
        }

        gradient[i] = partialDerivative;
      }

      return VectorOfDoubles.of(gradient); 
    };
  }

  /**
   * Computes the Jacobian matrix of a vector-valued function F at a given point x.
   * 
   * The Jacobian matrix contains partial derivatives of each output of F with respect to each input dimension,
   * computed using the central difference method.
   *
   * @param F       The function whose Jacobian is to be computed. It maps a vector to another vector.
   * @param x       The point at which to compute the Jacobian.
   * @param epsilon A small positive value used for finite difference perturbations.
   * @return A MatrixOfDoubles where the (i, j)-th entry represents the partial derivative of the i-th output of F 
   *         with respect to the j-th input of x.
   */
  public static Function<VectorOfDoubles,MatrixOfDoubles> numericalJacobian(Function<VectorOfDoubles, VectorOfDoubles> F, double epsilon) {
    return (VectorOfDoubles x) -> {
      int inputDim = x.getDimension();
      int outputDim = F.apply(x).getDimension();
      Double[][] jacobian = new Double[outputDim][inputDim];

      for (int j = 0; j < inputDim; j++) {
        // Create perturbation vectors for the j-th input
        Double[] xPerturbPlus = x.toDoubleArray().clone();
        Double[] xPerturbMinus = x.toDoubleArray().clone();
        xPerturbPlus[j] += epsilon;
        xPerturbMinus[j] -= epsilon;

        // Evaluate F at perturbed points
        VectorOfDoubles fPlus = F.apply(VectorOfDoubles.of(xPerturbPlus));
        VectorOfDoubles fMinus = F.apply(VectorOfDoubles.of(xPerturbMinus));

        // Fill the Jacobian matrix column for the j-th input dimension
        for (int i = 0; i < outputDim; i++) {
          jacobian[i][j] = (fPlus.get(i) - fMinus.get(i)) / (2 * epsilon);
        }
      }

      return new MatrixOfDoubles(jacobian);  
    };
  }

  /**
   * Computes the Hessian tensor of a vector-valued function F at a given point x.
   * 
   * The Hessian tensor is a three-dimensional array containing second-order partial derivatives.
   * For each output of F, the Hessian is a symmetric matrix representing mixed and pure second derivatives
   * with respect to input dimensions.
   *
   * @param F       The function whose Hessian is to be computed. It maps a vector to another vector.
   * @param x       The point at which to compute the Hessian.
   * @param epsilon A small positive value used for finite difference perturbations.
   * @return A 3D array of doubles, where the entry [i][j][k] represents the second-order partial derivative
   *         of the i-th output of F with respect to the j-th and k-th input dimensions of x.
   */
  public static Function<VectorOfDoubles, Double[][][]> numericalHessian(Function<VectorOfDoubles, VectorOfDoubles> F, double epsilon) {
    return (VectorOfDoubles x) -> {
      int inputDim = x.getDimension();
      int outputDim = F.apply(x).getDimension();
      
      Double[][][] hessian = new Double[outputDim][inputDim][inputDim];

      for (int i = 0; i < outputDim; i++) {
        for (int j = 0; j < inputDim; j++) {
          for (int k = 0; k < inputDim; k++) {
            // Perturbation vectors for the second-order partial derivatives
            Double[] xPerturbPlusPlus = x.toDoubleArray().clone();
            Double[] xPerturbPlusMinus = x.toDoubleArray().clone();
            Double[] xPerturbMinusPlus = x.toDoubleArray().clone();
            Double[] xPerturbMinusMinus = x.toDoubleArray().clone();

            xPerturbPlusPlus[j] += epsilon; xPerturbPlusPlus[k] += epsilon;
            xPerturbPlusMinus[j] += epsilon; xPerturbPlusMinus[k] -= epsilon;
            xPerturbMinusPlus[j] -= epsilon; xPerturbMinusPlus[k] += epsilon;
            xPerturbMinusMinus[j] -= epsilon; xPerturbMinusMinus[k] -= epsilon;

            // Evaluate F at the four perturbed points
            double fPlusPlus = F.apply(VectorOfDoubles.of(xPerturbPlusPlus)).get(i);
            double fPlusMinus = F.apply(VectorOfDoubles.of(xPerturbPlusMinus)).get(i);
            double fMinusPlus = F.apply(VectorOfDoubles.of(xPerturbMinusPlus)).get(i);
            double fMinusMinus = F.apply(VectorOfDoubles.of(xPerturbMinusMinus)).get(i);

            // Compute second-order partial derivative
            hessian[i][j][k] = (fPlusPlus - fPlusMinus - fMinusPlus + fMinusMinus) / (4 * epsilon * epsilon);
          }
        }
      }

      return hessian;  
    };
    
  }
}
