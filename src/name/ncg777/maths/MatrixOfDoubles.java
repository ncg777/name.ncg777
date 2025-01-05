package name.ncg777.maths;

import java.io.IOException;
import java.util.List;

import org.apache.commons.collections4.list.UnmodifiableList;

import com.fasterxml.jackson.core.JsonParseException;

import name.ncg777.computing.Parsers;

public class MatrixOfDoubles extends Matrix<Double> {
  public MatrixOfDoubles() {
    super();
  }

  public MatrixOfDoubles(Double[][] doubles) {
    super(doubles);
  }
  public MatrixOfDoubles(double[][] doubles) {
    super(doubles.length,doubles.length == 0 ? 0 : doubles[0].length);
    for(int i=0;i<doubles.length;i++) {
      for(int j=0;j<doubles[0].length;j++) {
        set(i,j,doubles[i][j]);
      }
    }
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

  /**
   * Computes the Haar matrix from a given vector.
   * 
   * @param size
   * @return A MatrixOfIntegers representing the Haar matrix.
   * @throws IllegalArgumentException if the vector length is not a power of 2.
   */
  public static MatrixOfDoubles getHaarMatrix(int size) {
    return new MatrixOfDoubles((new MatrixOfDoubles(generateHaarMatrix(size))).getTranspose());
  }

  /**
   * Generates a Haar matrix of size n x n.
   * 
   * @param n The size of the matrix (must be a power of 2).
   * @return A 2D array representing the Haar matrix.
   * @throws IllegalArgumentException if n is not a power of 2.
   */
  private static double[][] generateHaarMatrix(int n) {
      // Validate that n is a power of 2
      if ((n & (n - 1)) != 0) {
          throw new IllegalArgumentException("Matrix size must be a power of 2.");
      }

      // Initialize the Haar matrix
      double[][] haarMatrix = new double[n][n];

      // Fill the matrix
      fillHaarMatrix(haarMatrix, 0, 0, n, 1.0);

      return haarMatrix;
  }

  /**
   * Recursive helper method to fill the Haar matrix.
   * 
   * @param matrix The matrix being populated.
   * @param rowStart The starting row index for the current block.
   * @param colStart The starting column index for the current block.
   * @param size The size of the current block.
   * @param scale The scaling factor for normalization.
   */
  private static void fillHaarMatrix(double[][] matrix, int rowStart, int colStart, int size, double scale) {
      if (size == 1) {
          // Base case: Single value, fill with the scale
          matrix[rowStart][colStart] = scale;
          return;
      }

      int half = size / 2;

      // Low-pass filter (averaging rows)
      for (int i = 0; i < half; i++) {
          matrix[rowStart + i][colStart + i] = scale / Math.sqrt(2);
          matrix[rowStart + i][colStart + i + half] = scale / Math.sqrt(2);
      }

      // High-pass filter (differencing rows)
      for (int i = 0; i < half; i++) {
          matrix[rowStart + half + i][colStart + i] = scale / Math.sqrt(2);
          matrix[rowStart + half + i][colStart + i + half] = -scale / Math.sqrt(2);
      }

      // Recursive calls for the next levels of detail
      fillHaarMatrix(matrix, rowStart, colStart, half, scale / Math.sqrt(2)); // Low-pass
      fillHaarMatrix(matrix, rowStart + half, colStart, half, scale / Math.sqrt(2)); // High-pass
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
  public List<Double> computeEigenvalues(int maxIterations, double epsilon) {
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
      Double[] eigenvalues = new Double[size];
      for (int i = 0; i < size; i++) {
          eigenvalues[i] = Ak[i][i];
      }

      return List.of(eigenvalues);
  }
  /**
   * Computes the eigenvalues of the matrix using the QR algorithm.
   * Assumes that T is Double.
   * 
   * @param maxIterations Maximum number of iterations to perform
   * @param epsilon       Convergence threshold
   * @return An array of eigenvalues.
   */
  public List<Double> computeEigenvalues() {
    return computeEigenvalues(1000,Functions.DEFAULT_EPSILON);
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
      double[][] V = identityMatrix(size).toDoubleArray();

      for (int iter = 0; iter < maxIterations; iter++) {
          var Q = new double[size][size];
          var R = new double[size][size];

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
      double normX = Functions.normL2(x);
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
      double normV = Functions.normL2(v);
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

  public MatrixOfDoubles identityMatrix(int size) {
      Double[][] I = new Double[size][size];
      for (int i = 0; i < size; i++)
          I[i][i] = 1.0;
      return new MatrixOfDoubles(I);
  }


  public MatrixOfDoubles matrixMultiply(MatrixOfDoubles B) {
    int rows = this.rowCount();
    int cols = B.columnCount();
    int n = this.columnCount();
    var o = new MatrixOfDoubles(rows,cols);

    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            double total = 0.0;
            for (int k = 0; k < n; k++) {
                total += this.get(i, k) * B.get(k, j);
            }
            o.set(i, j, total);
        }
    }
    return o;
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
