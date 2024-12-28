package name.ncg777.maths;

import java.io.IOException;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonParseException;

import name.ncg777.computing.Parsers;

public class MatrixOfDoubles extends Matrix<Double> {
  public MatrixOfDoubles() {
    super();
  }

  public MatrixOfDoubles(Double[][] ints) {
    super(ints);
  }
  
  public MatrixOfDoubles product(MatrixOfDoubles other) {
    return new MatrixOfDoubles(product(other, 0.0, (a,b) -> a+b, (a,b) -> a*b));
  }
  
  public MatrixOfDoubles(Matrix<Double> other) {
    super(other);
  }
  
  public MatrixOfDoubles(Iterable<Double> iterable) {
    super(iterable);
  }

  public MatrixOfDoubles(int m, int n) {
    this(m,n,Double.valueOf(0));
  }
  
  public MatrixOfDoubles(int m, int n, Double fill) {
    super(m,n,fill);
  }
  
  public MatrixOfDoubles kronecker(MatrixOfDoubles other) {
    return new MatrixOfDoubles(super.kronecker(other, (a,b) -> a*b));
  }
  
  @Override
  public VectorOfDoubles getColumnVector(int j) {
    return new VectorOfDoubles(super.getColumnVector(j));
  }
  
  @Override
  public VectorOfDoubles getRowVector(int i) {
    return new VectorOfDoubles(super.getRowVector(i));
  }
  
  /**
   * Computes the determinant of the matrix using LU decomposition.
   * Assumes that T is Double.
   * 
   * @return The determinant of the matrix.
   */
  public Double computeDeterminant() {
      if (m != n)
          throw new IllegalArgumentException("Matrix must be square to compute determinant.");
      return computeDeterminantLU();
  }

  private Double computeDeterminantLU() {
      int size = this.m;
      double[][] A = this.toDoubleArray();

      double det = 1.0;
      int[] pivot = new int[size];

      // LU Decomposition with partial pivoting
      for (int i = 0; i < size; i++) {
          pivot[i] = i;
      }

      for (int j = 0; j < size; j++) {
          double max = 0.0;
          int row = -1;
          for (int i = j; i < size; i++) {
              double val = Math.abs(A[i][j]);
              if (val > max) {
                  max = val;
                  row = i;
              }
          }

          if (max == 0.0)
              return 0.0; // Singular matrix

          if (row != j) {
              // Swap rows
              double[] temp = A[j];
              A[j] = A[row];
              A[row] = temp;

              int tmp = pivot[j];
              pivot[j] = pivot[row];
              pivot[row] = tmp;

              det *= -1.0;
          }

          det *= A[j][j];

          for (int i = j + 1; i < size; i++) {
              A[i][j] /= A[j][j];
              for (int k = j + 1; k < size; k++) {
                  A[i][k] -= A[i][j] * A[j][k];
              }
          }
      }
      return det;
  }

  /**
   * Computes the eigenvalues of the matrix using the QR algorithm.
   * Assumes that T is Double.
   * 
   * @param maxIterations Maximum number of iterations to perform
   * @param epsilon       Convergence threshold
   * @return An array of eigenvalues.
   */
  public double[] computeEigenvalues(int maxIterations, double epsilon) {
      if (m != n)
          throw new IllegalArgumentException("Matrix must be square to compute eigenvalues.");

      double[][] A = this.toDoubleArray();
      int size = this.m;

      double[][] Q = new double[size][size];
      double[][] R = new double[size][size];
      double[][] Ak = A;

      for (int iter = 0; iter < maxIterations; iter++) {
          // QR Decomposition
          qrDecomposition(Ak, Q, R);

          // Ak+1 = R * Q
          Ak = matrixMultiply(R, Q);

          // Check for convergence
          if (isUpperTriangular(Ak, epsilon)) {
              break;
          }
      }

      // The eigenvalues are on the diagonal of Ak
      double[] eigenvalues = new double[size];
      for (int i = 0; i < size; i++) {
          eigenvalues[i] = Ak[i][i];
      }

      return eigenvalues;
  }

  /**
   * Computes the eigenvectors of the matrix using the QR algorithm.
   * Assumes that T is Double.
   * 
   * @param maxIterations Maximum number of iterations to perform
   * @param epsilon       Convergence threshold
   * @return A matrix where each column is an eigenvector.
   */
  public MatrixOfDoubles computeEigenvectors(int maxIterations, double epsilon) {
      if (m != n)
          throw new IllegalArgumentException("Matrix must be square to compute eigenvectors.");

      int size = this.m;
      double[][] A = this.toDoubleArray();
      double[][] V = identityMatrix(size);

      for (int iter = 0; iter < maxIterations; iter++) {
          double[][] Q = new double[size][size];
          double[][] R = new double[size][size];

          // QR Decomposition of A
          qrDecomposition(A, Q, R);

          // Update A
          A = matrixMultiply(R, Q);

          // Update eigenvector matrix V
          V = matrixMultiply(V, Q);

          // Check for convergence
          if (isUpperTriangular(A, epsilon)) {
              break;
          }
      }

      // Convert V to Matrix<Double>
      MatrixOfDoubles eigenvectors = new MatrixOfDoubles(size, size);
      for (int i = 0; i < size; i++) {
          for (int j = 0; j < size; j++) {
              eigenvectors.set(i, j, V[i][j]);
          }
      }

      return eigenvectors;
  }

  // Helper methods for the QR algorithm
  private void qrDecomposition(double[][] A, double[][] Q, double[][] R) {
      int size = A.length;
      double[][] Ai = new double[size][size];
      for (int i = 0; i < size; i++)
          System.arraycopy(A[i], 0, Ai[i], 0, size);

      double[][] Qt = new double[size][size];
      for (int i = 0; i < size; i++)
          Qt[i][i] = 1.0;

      for (int j = 0; j < size - 1; j++) {
          double[] x = new double[size - j];
          for (int i = j; i < size; i++) {
              x[i - j] = Ai[i][j];
          }
          double[] v = computeHouseholderVector(x);
          double[][] H = computeHouseholderMatrix(v, size - j);
          Ai = applyHouseholder(Ai, H, j);
          Qt = applyHouseholder(Qt, H, j);
      }
      // R is the resulting upper triangular matrix
      for (int i = 0; i < size; i++)
          System.arraycopy(Ai[i], 0, R[i], 0, size);

      // Q is the transpose of Qt
      for (int i = 0; i < size; i++)
          for (int j = 0; j < size; j++)
              Q[i][j] = Qt[j][i];
  }

  private double[] computeHouseholderVector(double[] x) {
      double normX = norm(x);
      double[] v = new double[x.length];
      System.arraycopy(x, 0, v, 0, x.length);
      if (normX == 0.0) {
          return v;
      }
      if (x[0] >= 0.0) {
          v[0] += normX;
      } else {
          v[0] -= normX;
      }
      double normV = norm(v);
      for (int i = 0; i < v.length; i++) {
          v[i] /= normV;
      }
      return v;
  }

  private double[][] computeHouseholderMatrix(double[] v, int size) {
      double[][] H = new double[size][size];
      for (int i = 0; i < size; i++) {
          H[i][i] = 1.0;
          for (int j = i; j < size; j++) {
              H[i][j] -= 2.0 * v[i] * v[j];
              if (i != j) {
                  H[j][i] = H[i][j];
              }
          }
      }
      return H;
  }

  private double[][] applyHouseholder(double[][] A, double[][] H, int offset) {
      int size = A.length;
      double[][] result = new double[size][size];
      for (int i = 0; i < offset; i++)
          System.arraycopy(A[i], 0, result[i], 0, size);

      for (int i = offset; i < size; i++) {
          for (int j = 0; j < size; j++) {
              result[i][j] = 0.0;
              for (int k = offset; k < size; k++) {
                  result[i][j] += H[i - offset][k - offset] * A[k][j];
              }
          }
      }
      return result;
  }

  private double norm(double[] x) {
      double sum = 0.0;
      for (double xi : x)
          sum += xi * xi;
      return Math.sqrt(sum);
  }

  private double[][] identityMatrix(int size) {
      double[][] I = new double[size][size];
      for (int i = 0; i < size; i++)
          I[i][i] = 1.0;
      return I;
  }

  private double[][] matrixMultiply(double[][] A, double[][] B) {
      int rows = A.length;
      int cols = B[0].length;
      int n = A[0].length;
      double[][] C = new double[rows][cols];

      for (int i = 0; i < rows; i++) {
          for (int j = 0; j < cols; j++) {
              double total = 0.0;
              for (int k = 0; k < n; k++) {
                  total += A[i][k] * B[k][j];
              }
              C[i][j] = total;
          }
      }
      return C;
  }
  /**
   * Converts the matrix to a 2D array of doubles.
   * Assumes that T is Double.
   * 
   * @return
   */
  public double[][] toDoubleArray() {
      double[][] array = new double[m][n];
      for (int i = 0; i < m; i++) {
          for (int j = 0; j < n; j++) {
              Double value = get(i, j);
              array[i][j] = (value != null) ? ((Double) value) : 0.0;
          }
      }
      return array;
  }
  
  private boolean isUpperTriangular(double[][] A, double epsilon) {
      int size = A.length;
      for (int i = 1; i < size; i++) {
          for (int j = 0; j < i; j++) {
              if (Math.abs(A[i][j]) > epsilon)
                  return false;
          }
      }
      return true;
  }
  
  public static MatrixOfDoubles parseJSONFile(String path) throws JsonParseException, IOException {
    return new MatrixOfDoubles(Matrix.parseJSONFile(path, Parsers.doubleParser));
  }

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
  public VectorOfDoubles computeGradient(Function<VectorOfDoubles, VectorOfDoubles> F, VectorOfDoubles x, double epsilon) {
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
  public MatrixOfDoubles computeJacobian(Function<VectorOfDoubles, VectorOfDoubles> F, VectorOfDoubles x, double epsilon) {
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
  public Double[][][] computeHessian(Function<VectorOfDoubles, VectorOfDoubles> F, VectorOfDoubles x, double epsilon) {
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
  }
  
  /**
   * Compares two matrices for equality within a given epsilon.
   *
   * @param other The matrix to compare with
   * @param epsilon The tolerance for comparison
   * @return true if the matrices are approximately equal, false otherwise
   */
  public boolean isEqual(MatrixOfDoubles other, double epsilon) {
      if (this.rowCount() != other.rowCount() || this.columnCount() != other.columnCount()) {
          return false;
      }

      for (int i = 0; i < this.rowCount(); i++) {
          for (int j = 0; j < this.columnCount(); j++) {
              if (Math.abs(this.get(i, j) - other.get(i, j)) > epsilon) {
                  return false;
              }
          }
      }
      return true;
  }
}
