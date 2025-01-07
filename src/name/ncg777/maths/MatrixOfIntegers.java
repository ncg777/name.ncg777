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
  
  public MatrixOfIntegers kronecker(MatrixOfIntegers other) {
    return new MatrixOfIntegers(super.kronecker(other, (a,b) -> a*b));
  }
  
  public MatrixOfDoubles toMatrixOfDoubles() {
    var o = new MatrixOfDoubles(m,n); 
    for(int i=0;i<rowCount();i++) {
      for(int j=0;j<columnCount();j++) {
        o.set(i, j, this.get(i, j).doubleValue());
      }
    }
    return o;
  }
  
  public static MatrixOfIntegers parseJSONFile(String path) throws JsonParseException, IOException {
    return new MatrixOfIntegers(Matrix.parseJSONFile(path, Parsers.integerParser));
  }
}
