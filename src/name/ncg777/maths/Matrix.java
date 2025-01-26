/**
 * Generic mutable Matrix class with basic functionnality.
 * 
 * Creation date : 2013-05-17
 * 
 * @author Nicolas Couture-Grenier
 */
package name.ncg777.maths;

import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import org.apache.commons.collections4.list.UnmodifiableList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.common.base.Joiner;
import com.google.common.collect.ComparisonChain;

import name.ncg777.computing.Parsers;
import name.ncg777.computing.structures.HomoPair;
import name.ncg777.computing.structures.JaggedList;
import name.ncg777.maths.enumerations.MixedRadixEnumeration;
import name.ncg777.maths.sequences.Sequence;

/**
 * Generic mutable Matrix class with basic functionality.
 * 
 * @author Nicolas Couture-Grenier
 * 
 * @param <T>
 */
public class Matrix<T extends Comparable<? super T>> implements Comparable<Matrix<? super T>> {
  protected TreeMap<HomoPair<Integer>, T> mat;
  /** Natural of rows */
  protected int m;
  /** Natural of columns */
  protected int n;
  private T defaultValue = null;

  /** @return Gets number of columns */
  public int columnCount() {
    return n;
  }

  /** @return Gets number of rows */
  public int rowCount() {
    return m;
  }

  public void apply(Function<T,T> transform) {
    for(int i=0;i<this.m;i++) {
      for(int j=0;j<this.n;j++) {
        this.set(i, j, transform.apply(this.get(i,j)));
      }
    }
  }
  
  /**
   * Constructor from an array.
   * 
   * @param arr
   */
  public Matrix(T[][] arr) {
    this(arr.length, arr[0].length);
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        set(i, j, arr[i][j]);
      }
    }
  }

  public Matrix(Iterable<T> iterable) {
    super();
    var list = new ArrayList<T>();
    for(var t : iterable) list.add(t);
    init(list.size(),1,null);
    setColumn(0, list);
  }
  
  public MatrixOfDoubles toMatrixOfDoubles(Function<T,Double> f) {
    MatrixOfDoubles o = new MatrixOfDoubles(this.rowCount(),this.columnCount(), f.apply(defaultValue));
    for(var e : mat.entrySet()) o.set(e.getKey().getFirst(), e.getKey().getSecond(), f.apply(e.getValue()));
    return o;
  }
 
  public MatrixOfIntegers toMatrixOfIntegers(Function<T,Integer> f) {
    MatrixOfIntegers o = new MatrixOfIntegers(this.rowCount(),this.columnCount(), f.apply(defaultValue));
    for(var e : mat.entrySet()) o.set(e.getKey().getFirst(), e.getKey().getSecond(), f.apply(e.getValue()));
    return o;
  }
  /**
   * Copy constructor.
   * 
   * @param m
   */
  public Matrix(Matrix<T> p_mat) {
    this(p_mat.m, p_mat.n);
    copy(p_mat);
  }


  /**
   * Copies matrix specified as parameter into this one.
   * 
   * @param p_mat
   */
  public void copy(Matrix<T> p_mat) {
    clear();
    this.m = p_mat.m;
    this.n = p_mat.n;
    this.defaultValue = p_mat.defaultValue;
    init(p_mat.rowCount(), p_mat.columnCount(), null);
    setBlock(p_mat, 0, 0);
  }

  /**
   * Constructs an empty matrix.
   */
  public Matrix() {
    super();
    init(0,0, null);
  }

  /**
   * Construct a matrix filled with nulls.
   * 
   * @param m Natural of rows
   * @param n Natural of columns
   */
  public Matrix(int m, int n) {
    this();
    init(m,n, null);
  }
  
  public Matrix(int m, int n, T fill) {
    this();
    init(m,n,fill);
  }

  /**
   * Initializes matrix with a default value..
   * 
   * @param m
   * @param n
   * @param defaultValue
   */
  protected void init(int p_m, int p_n, T fill) {
    mat = new TreeMap<>();

    this.m = p_m;
    this.n = p_n;
    if(fill != null) {
      for(int i=0;i<p_m;i++) {
        for(int j=0;j<p_n;j++) {
          set(i,j,fill);
        }
      }
    }
  }

  
  public Matrix<T> product(Matrix<T> other, T initValue, BinaryOperator<T> sum, BinaryOperator<T> product) {
    if (this.n != other.m) {
      throw new IllegalArgumentException("Matrix dimensions do not match for multiplication");
    }
  
    Matrix<T> result = new Matrix<>(this.m, other.n);
  
    mat.keySet().parallelStream().forEach(pos -> {
        int i = pos.getFirst();
        int j = pos.getSecond();
        T total = initValue;
        for (int k = 0; k < this.n; k++) {
            total = sum.apply(total, product.apply(this.get(i, k), other.get(k, j)));
        }
        result.set(i, j, total);
    });
  
    return result;
  }
  
  /**
   * Kronecker product.
   * 
   * @param other
   * @param sum
   * @param product
   * @return
   */
  public Matrix<T> kronecker(Matrix<T> other, BinaryOperator<T> product) {
      int resultRows = this.m * other.m;
      int resultCols = this.n * other.n;

      Matrix<T> result = new Matrix<>(resultRows, resultCols);

      var mre = new MixedRadixEnumeration(Arrays.asList(this.m, this.n, other.m, other.n));
      
      Set<Sequence> t = new TreeSet<Sequence>();
      while(mre.hasMoreElements()) {
        t.add(new Sequence(mre.nextElement()));
      }
      
      t.parallelStream().forEach((s) -> {
        int i = s.get(0);
        int j = s.get(1);
        int k = s.get(2);
        int l = s.get(3);
        
        T value = product.apply(this.get(i, j), other.get(k, l));
        
        result.set((i * other.m) + k, (j * other.n) + l, value);
      });

      return result;
  }
  
  @Override
  public String toString() {
    return toString((t) -> t == null ? "∅" : t.toString());
  }
  /**
   * Only prints each row on a line with values separated by tabs,
   * 
   * @return String
   */
  public String toString(Function<T,String> printer) {
    StringBuilder sb = new StringBuilder();
    var joiner = Joiner.on(" ");
    for (int i = 0; i < m; i++) {
      sb.append(joiner.join(getRow(i).stream().map((t) -> printer.apply(t)).toList()) + "\n");
    }
    return sb.toString();
  }


  /**
   * Gets value at row i, column j
   * 
   * @param i
   * @param j
   * @return
   */
  public synchronized T get(int i, int j) {
    var p = HomoPair.makeHomoPair(i, j);
    if (!mat.containsKey(p)) {
      return defaultValue;
    }
    return mat.get(p);
  }
  private boolean isDefaultValue(Object v) {
    if(v == null) return v == defaultValue;
    return v.equals(defaultValue);
  }
  /**
   * Sets value at row i, column j..
   * 
   * @param i
   * @param j
   * @return The previous value of the element at this position
   */
  public synchronized void set(int i, int j, T val) {
    var p = HomoPair.makeHomoPair(i, j);
    if(isDefaultValue(val)) {mat.remove(p);}
    else  mat.put(p, val);
  }

  public void clear() {
    mat.clear();
  }

  /**
   * Adds row at bottom of matrix..
   * 
   * @param l
   */
  public void appendRow(List<T> l) {
    insertRow(m, l);
  }

  /**
   * Inserts a row filled with value v..
   * 
   * @param v
   */
  public void appendRow(T v) {
    insertRow(m, v);
  }

  public void appendRow() {
    insertRow(m);
  }

  /**
   * Concatenate matrix c vertically..
   * 
   * @param c
   */
  public void appendRows(Matrix<T> c) {
    for (int i = 0; i < c.m; i++) {
      appendRow(c.getRow(i));
    }
  }

  /**
   * Moves row from a position to another.
   * 
   * @param from
   * @param to
   */
  public void moveRow(int from, int to) {
    if (from == to) {
      return;
    }
    if (from < 0 || to < 0 || from >= m || to >= m) {
      throw new IllegalArgumentException();
    }
    List<T> r = getRow(from);
    removeRow(from);
    insertRow(to, r);
  }

  /**
   * Adds column at right end of matrix.
   * 
   * @param c
   */
  public void appendColumn(List<T> c) {
    insertColumn(n, c);
  }

  public void appendColumn() {
    insertColumn(n);
  }

  /**
   * Appends column filled with value v..
   * 
   * @param v
   */
  public void appendColumn(T v) {
    insertColumn(n, v);
  }

  /**
   * Concatenates matrix horizontally..
   * 
   * @param c
   */
  public void appendColumns(Matrix<T> c) {
    for (int j = 0; j < c.n; j++) {
      appendColumn(c.getColumn(j));
    }
  }

  /**
   * Moves column from a position to another.
   * 
   * @param from
   * @param to
   */
  public void moveColumn(int from, int to) {
    if (from == to) {
      return;
    }
    if (from < 0 || to < 0 || from >= n || to >= n) {
      throw new IllegalArgumentException();
    }
    List<T> c = getColumn(from);
    removeColumn(from);
    insertColumn(to, c);
  }

  /**
   * Sets a block of the matrix to a copy of values in supplied matrix (i0 and j0 is upper left
   * corner)..
   * 
   * @param p_mat
   * @param i0
   * @param j0
   */
  public void setBlock(Matrix<T> p_mat, int i0, int j0) {
    
    int mm = p_mat.rowCount();
    int mn = p_mat.columnCount();

    if (i0 < 0 || j0 < 0 || i0 + mm > this.m || j0 + mn > this.n) {
      throw new IndexOutOfBoundsException("Matrix::setBlock index out of bounds");
    }

    for (int i = 0; i < mm; i++) {
      for (int j = 0; j < mn; j++) {
        T v = p_mat.get(i, j);
        Matrix.this.set(i + i0, j + j0, v);
      }
    }
  }

  private synchronized void shiftColumnsRight(int j) {
    n++;
    for(var _e : getTranspose().mat.descendingKeySet().stream().filter((x) -> x.getFirst() >=j).toList()) {
      var e = _e.converse();
      mat.put(
            HomoPair.makeHomoPair(e.getFirst(),e.getSecond()+1), 
            mat.remove(e)
          );
    }
  }

  private synchronized void shiftRowsDown(int i) {
    m++;
    for(var e : mat.descendingKeySet().stream().filter((x) -> x.getFirst() >=i).toList()) {  
      mat.put(
            HomoPair.makeHomoPair(e.getFirst()+1,e.getSecond()), 
            mat.remove(e)
          );
    }
  }

  /**
   * Inserts column before position j. If matrix was empty, inserting column sets the number of
   * rows..
   * 
   * @param j
   * @param c
   */
  public void insertColumn(int j, List<T> c) { 
    if (c.size() != m && n != 0) {
      throw new IllegalArgumentException("Matrix::insertColumn list size don't match matrix.");
    }

    shiftColumnsRight(j);

    int i = 0;
    for (T v : c) {
      set(i++, j, v);
    }
  }
  /**
   * Inserts a column with all values set to v..
   * 
   * @param j
   * @param v
   */
  public void insertColumn(int j, T v) {
    shiftColumnsRight(j);
    for (int i = 0; i < m; i++) {
      set(i, j, v);
    }

  }

  /**
   * Inserts a column with all values set to null. Near duplicate of insertColumn(int j, T v), but
   * is needed to avoid ambiguity..
   * 
   * @param j
   */
  public void insertColumn(int j) {
    shiftColumnsRight(j);
  }

  /**
   * Inserts row before position i. If matrix was empty, inserting row sets the number of columns.
   *.
   * 
   * @param i
   * @param l
   */
  public void insertRow(int i, List<T> r) {
    if (r.size() != n && m != 0) {
      throw new IllegalArgumentException("Matrix::insertRow list size don't match matrix.");
    }
    shiftRowsDown(i);
    for (int j = 0; j < n; j++) {
      set(i, j, r.get(j));
    }
  }
  /**
   * Insert a row with all values set to v..
   * 
   * @param i
   * @param v
   */
  public void insertRow(int i, T v) {
    shiftRowsDown(i);
    for (int j = 0; j < n; j++) {
      set(i, j, v);
    }
  }

  /**
   * Insert a row with all values set to null. Near duplicate of insertRow(int i, T v), but is
   * needed to avoid ambiguity..
   * 
   * @param i
   */
  public void insertRow(int i) {
    shiftRowsDown(i);
  }

  /**
   * Removes column at position j.
   * 
   * @param j
   */
  public synchronized void removeColumn(int j) {
    n--;
    for(var _e : this.getTranspose().mat.keySet().stream().toList()) {
      var e = _e.converse();
      if(e.getSecond()==j) mat.remove(e);
      if(e.getSecond() > j) {
        mat.put(
              HomoPair.makeHomoPair(e.getFirst(),e.getSecond()-1), 
              mat.remove(e)
            );
      }
    }
  }

  /**
   * Removes row at position i.
   * 
   * @param i
   */
  public synchronized void removeRow(int i) {
    m--;
    for(var e : mat.keySet().stream().toList()) {
      if(e.getFirst()==i) mat.remove(e);
      if(e.getFirst() > i) {
        mat.put(
              HomoPair.makeHomoPair(e.getFirst()-1,e.getSecond()), 
              mat.remove(e)
            );
      }
    }
  }

  /**
   * Overwrites column j with ArrayList c.
   * 
   * @param j
   * @param c
   */
  public void setColumn(int j, List<T> c) {  
    if (c.size() != m) {
      throw new IllegalArgumentException("Matrix::setColumn list size don't match matrix.");
    }
    int i = 0;
    for (T e : c) {
      set(i++, j, e);
    }
  }

  /**
   * Sets all elements in column j to c.
   * 
   * @param j
   * @param c
   */
  public void setColumn(int j, T c) {
    for (int i = 0; i < m; i++) {
      set(i, j, c);
    }
  }
  /**
   * Overwrites row i with ArrayList l.
   * 
   * @param i
   * @param l
   */
  public void setRow(int i, List<T> r) {
    if (r.size() != n) {
      throw new IllegalArgumentException("Matrix::setRow list size don't match matrix.");
    }
    int j = 0;
    for (T e : r) {
      set(i, j++, e);
    }
  }
  /**
   * Sets all elements in row i to r.
   * 
   * @param i
   * @param r
   */
  public void setRow(int i, T r) {
    for (int j = 0; j < n; j++) {
      set(i, j, r);
    }
  }

  /**
   * Returns column j Changes made to the column will not affect the matrix.
   * 
   * @param j >=0
   * @return
   */
  public List<T> getColumn(int j) {
    List<T> o = new ArrayList<T>();
    for (int i = 0; i < m; i++) {
      o.add(get(i, j));
    }
    return o;
  }
  
  public List<T> getColumnVector(int j) {
    return UnmodifiableList.unmodifiableList(getColumn(j));
  }
  
  /**
   * Returns row i. Changes made to the row will not affect the matrix.
   * 
   * @param i >=0
   * @return
   */
  public List<T> getRow(int i) {
    List<T> r = new ArrayList<T>();
    
    for (int j = 0; j < n; j++) {
      r.add(get(i, j));
    }
    return r;
  }
  
  public List<T> getRowVector(int i) {
    return UnmodifiableList.unmodifiableList(getRow(i));
  }
  
  /**
   * Returns the transpose of the matrix. Doesn't affect the original.
   * 
   * @return
   */
  public Matrix<T> getTranspose() {
    Matrix<T> o = new Matrix<T>(n, m);
    for(var e : mat.entrySet()) o.set(e.getKey().getSecond(),e.getKey().getFirst(), e.getValue());
    return o;
  }

  /**
   * Computes the determinant of the matrix using Laplace expansion.
   * 
   * @param sum     Binary operator representing the addition operation.
   * @param product Binary operator representing the multiplication operation.
   * @param negate  Function representing the additive inverse (negation) operation.
   * @return The determinant of the matrix.
   */
  public T computeDeterminant(BinaryOperator<T> sum, BinaryOperator<T> product, Function<T, T> negate) {
      if (m != n)
          throw new IllegalArgumentException("Matrix must be square to compute determinant.");
      return computeDeterminantRecursive(this, sum, product, negate);
  }

  private T computeDeterminantRecursive(Matrix<T> matrix, BinaryOperator<T> sum, BinaryOperator<T> product,
          Function<T, T> negate) {
      int size = matrix.m;
      if (size == 1) {
          return matrix.get(0, 0);
      }
      T determinant = null;
      for (int c = 0; c < size; c++) {
          Matrix<T> subMatrix = matrix.getSubMatrix(0, c);
          T subDeterminant = computeDeterminantRecursive(subMatrix, sum, product, negate);
          T cofactor = (c % 2 == 0) ? subDeterminant : negate.apply(subDeterminant);
          T term = product.apply(matrix.get(0, c), cofactor);
          determinant = (determinant == null) ? term : sum.apply(determinant, term);
      }
      return determinant;
  }

  /**
   * Gets a submatrix by excluding the specified row and column.
   * 
   * @param excludingRow
   * @param excludingCol
   * @return The submatrix
   */
  public Matrix<T> getSubMatrix(int excludingRow, int excludingCol) {
      Matrix<T> subMatrix = new Matrix<T>(m - 1, n - 1);
      for (int i = 0, subI = 0; i < m; i++) {
          if (i == excludingRow)
              continue;
          for (int j = 0, subJ = 0; j < n; j++) {
              if (j == excludingCol)
                  continue;
              subMatrix.set(subI, subJ, this.get(i, j));
              subJ++;
          }
          subI++;
      }
      return subMatrix;
  }

  /**
   * Computes the eigenvalues of the matrix.
   * Note: For generic type T, computing eigenvalues may not be feasible without additional operations like division.
   * This method assumes that T represents numbers where such operations are meaningful.
   * 
   * @param sum     Binary operator representing the addition operation.
   * @param product Binary operator representing the multiplication operation.
   * @return An array of eigenvalues.
   */
  public T[] computeEigenvalues(BinaryOperator<T> sum, BinaryOperator<T> product) {
      // Placeholder implementation
      throw new UnsupportedOperationException(
              "Eigenvalue computation requires solving polynomial equations, which may not be supported for generic type T.");
  }

  /**
   * Computes the eigenvectors of the matrix.
   * Note: For generic type T, computing eigenvectors may not be feasible without additional operations like division.
   * This method assumes that T represents numbers where such operations are meaningful.
   * 
   * @param sum     Binary operator representing the addition operation.
   * @param product Binary operator representing the multiplication operation.
   * @return An array of eigenvectors represented as matrices.
   */
  public Matrix<T>[] computeEigenvectors(BinaryOperator<T> sum, BinaryOperator<T> product) {
      // Placeholder implementation
      throw new UnsupportedOperationException(
              "Eigenvector computation requires solving linear systems, which may not be supported for generic type T.");
  }

  
  public boolean contains(T el) {
    if(isDefaultValue(el) && m>0 && n>0) return true;
    return mat.values().contains(el);
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + m;
    result = prime * result + ((mat == null) ? 0 : mat.hashCode());
    result = prime * result + n;
    return result;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Matrix other = (Matrix) obj;
    return this.compareTo(other) == 0;
  }
  
  public void printToJSON(Function<T,String> printer, String path) throws FileNotFoundException {
    this.toStringJaggedList(printer).printToJSON((s) -> s, path);
  }
  
  public String toJSONArrayString(Function<T,String> printer) {
    return this.toStringJaggedList(printer).toJSONArrayString((s) -> s);
  }
  
  public static <T extends Comparable<? super T>> 
      Matrix<T> parseJSONArray(
          String str, 
          Function<String,T> parser) {
      return Matrix.fromStringJaggedList(JaggedList.parseJSONArray(str, (s) -> s), parser);
  }
  public static <T extends Comparable<? super T>> 
    Matrix<T> parseJSONFile(
        String path, 
        Function<String,T> parser) throws JsonParseException, IOException { 
      return Matrix.fromStringJaggedList(JaggedList.<String>parseJSONFile(path, (s) -> s), parser);
  }
  
  public JaggedList<String> toStringJaggedList(Function<T,String> printer) {
    return toJaggedList(printer);
  }
  public <U extends Comparable<? super U>> Matrix<U> map(Function<T,U> transformer) {
    var o = new Matrix<U>(m,n,transformer.apply(defaultValue));
    
    for(int i=0;i<m;i++) {
      for(int j=0;j<n;j++) {
        o.set(i,j,transformer.apply(get(i,j)));
      }
    }
    return o;
  }
  public <U extends Comparable<? super U>> JaggedList<U> toJaggedList(Function<T,U> transformer) {
    var o = new JaggedList<U>();
    if(m>0) o.init(m,n);
    for(int i=0;i<m;i++) {
      for(int j=0;j<n;j++) {
        o.set(transformer.apply(get(i,j)), i,j);
      }
    }
    return o;
  }
  public static <T extends Comparable<? super T>> 
      Matrix<T> fromStringJaggedList(
          JaggedList<String> arr, 
          Function<String,T> parser) {
    
    parser = Parsers.nullDecorator(parser);
    return fromJaggedList(arr).map(parser);
  }
  public static <T extends Comparable<? super T>> 
  Matrix<T> fromJaggedList(
      JaggedList<T> arr) {
    var o = new Matrix<T>();
    for(int i=0;i<arr.size();i++) {
      o.appendRow();
      for(int j=0; j<arr.get(i).size();j++) {
        if(o.columnCount() < j+1) o.appendColumn();
        o.set(i, j, arr.get(i,j).getValue());
      }
    }
    return o;
  }

  @Override
  public int compareTo(Matrix<? super T> o) {
    int res = ComparisonChain.start().compare(this.m, o.m).compare(this.n, o.n).result();
    if(res != 0) return res;
    for(int i=0;i<n;i++) {
      for(int j=0;j<m;j++) {
        res = ComparisonChain.start().compare(this.get(i, j), o.get(i, j)).result();
        if(res != 0) return res;
      }
    }
    return 0;
  }
}
