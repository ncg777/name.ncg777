package name.ncg777.maths;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;

import name.ncg777.computing.Parsers;

public class MatrixOfIntegers extends Matrix<Integer> {
  public MatrixOfIntegers() {
    super();
  }

  public MatrixOfIntegers(Integer[][] ints) {
    super(ints);
  }
  
  public MatrixOfIntegers product(MatrixOfIntegers other) {
    return new MatrixOfIntegers(product(other, 0, (a,b) -> a+b, (a,b) -> a*b));
  }
  
  public MatrixOfIntegers(Matrix<Integer> other) {
    super(other);
  }
  
  public MatrixOfIntegers(Iterable<Integer> iterable) {
    super(iterable);
  }

  public MatrixOfIntegers(int m, int n) {
    this(m,n,Integer.valueOf(0));
  }
  
  public MatrixOfIntegers(int m, int n, Integer fill) {
    super(m,n,fill);
  }
  
  /**
   * Computes the Haar matrix from a given vector.
   * 
   * @param vector The input vector of integers.
   * @return A MatrixOfIntegers representing the Haar matrix.
   * @throws IllegalArgumentException if the vector length is not a power of 2.
   */
  public static MatrixOfIntegers computeHaarMatrix(VectorOfIntegers vector) {
    int size = vector.getDimension();
    if ((size & (size - 1)) != 0) {
      throw new IllegalArgumentException("Vector size must be a power of 2.");
    }

    Integer[][] haarMatrix = new Integer[size][size];
    fillHaarMatrix(haarMatrix, 0, size, 1);

    return new MatrixOfIntegers(haarMatrix);
  }

  /**
   * Recursive helper method to fill the Haar matrix.
   * 
   * @param matrix The matrix being populated.
   * @param start  The starting index of the current block.
   * @param size   The size of the current block.
   * @param scale  The scaling factor for normalization.
   */
  private static void fillHaarMatrix(Integer[][] matrix, int start, int size, int scale) {
    if (size == 1) {
      matrix[start][start] = scale;
      return;
    }

    int half = size / 2;
    for (int i = 0; i < half; i++) {
      for (int j = 0; j < size; j++) {
        matrix[start + i][j] = (j < half) ? scale : 0; // Low-pass filter
        matrix[start + half + i][j] = (j < half) ? 0 : scale; // High-pass filter
      }
    }

    // Recursive calls for low-pass and high-pass components
    fillHaarMatrix(matrix, start, half, scale);
    fillHaarMatrix(matrix, start + half, half, scale);
  }
  
  public MatrixOfIntegers kronecker(MatrixOfIntegers other) {
    return new MatrixOfIntegers(super.kronecker(other, (a,b) -> a*b));
  }
  
  @Override
  public VectorOfIntegers getColumnVector(int j) {
    return new VectorOfIntegers(super.getColumnVector(j));
  }
  
  @Override
  public VectorOfIntegers getRowVector(int i) {
    return new VectorOfIntegers(super.getRowVector(i));
  }
  
  public static MatrixOfIntegers parseJSONFile(String path) throws JsonParseException, IOException {
    return new MatrixOfIntegers(Matrix.parseJSONFile(path, Parsers.integerParser));
  }
}
