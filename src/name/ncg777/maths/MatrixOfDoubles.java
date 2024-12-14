package name.ncg777.maths;

import java.io.IOException;

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
  
  public static MatrixOfDoubles parseJSONFile(String path) throws JsonParseException, IOException {
    return new MatrixOfDoubles(Matrix.parseJSONFile(path, Parsers.doubleParser));
  }
}
