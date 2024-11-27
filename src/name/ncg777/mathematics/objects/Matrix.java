/**
 * Generic mutable Matrix class with basic functionnality.
 * 
 * Creation date : 2013-05-17
 * 
 * @author Nicolas Couture-Grenier
 */
package name.ncg777.mathematics.objects;

import java.util.TreeMap;
import java.util.function.Function;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.common.base.Joiner;
import com.google.common.collect.ComparisonChain;

import name.ncg777.computerScience.Parsers;
import name.ncg777.computerScience.dataStructures.HomoPair;
import name.ncg777.computerScience.dataStructures.JaggedList;

/**
 * Generic mutable Matrix class with basic functionality.
 * 
 * @author Nicolas Couture-Grenier
 * 
 * @param <T>
 */
public class Matrix<T extends Comparable<? super T>> implements Comparable<Matrix<? super T>> {
  protected TreeMap<HomoPair<Integer>, T> mat;
  /** Number of rows */
  protected int m;
  /** Number of columns */
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
   * @param m Number of rows
   * @param n Number of columns
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
   * Initializes matrix with a default value. Matrix must be unlocked.
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

  @Override
  public String toString() {
    return toString((t) -> t.toString());
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
  public T get(int i, int j) {
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
   * Sets value at row i, column j. Matrix must be unlocked.
   * 
   * @param i
   * @param j
   * @return The previous value of the element at this position
   */
  public void set(int i, int j, T val) {
    var p = HomoPair.makeHomoPair(i, j);
    if(isDefaultValue(val)) {mat.remove(p);}
    else  mat.put(p, val);
  }

  public void clear() {
    mat.clear();
  }

  /**
   * Adds row at bottom of matrix. Matrix must be unlocked.
   * 
   * @param l
   */
  public void appendRow(List<T> l) {
    insertRow(m, l);
  }

  /**
   * Inserts a row filled with value v. Matrix must be unlocked.
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
   * Concatenate matrix c vertically. Matrix must be unlocked.
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
   * Adds column at right end of matrix Matrix must be unlocked.
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
   * Appends column filled with value v. Matrix must be unlocked.
   * 
   * @param v
   */
  public void appendColumn(T v) {
    insertColumn(n, v);
  }

  /**
   * Concatenates matrix horizontally. Matrix must be unlocked.
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
   * corner). Matrix must be unlocked.
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

  private void shiftColumnsRight(int j) {
    n++;
    for(var e : mat.descendingMap().entrySet()) {
      if(e.getKey().getSecond() >= j) {
        mat.put(
              HomoPair.makeHomoPair(e.getKey().getFirst(),e.getKey().getSecond()+1), 
              mat.remove(e.getKey())
            );
      }
    }
  }

  private void shiftRowsDown(int i) {
    m++;
    for(var e : mat.descendingMap().entrySet()) {
      if(e.getKey().getFirst() >= i) {
        mat.put(
              HomoPair.makeHomoPair(e.getKey().getFirst()+1,e.getKey().getSecond()), 
              mat.remove(e.getKey())
            );
      }
    }
  }

  /**
   * Inserts column before position j. If matrix was empty, inserting column sets the number of
   * rows. Matrix must be unlocked.
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
  public void insertColumn(int j, Vector<T> c) { 
    if (c.getDimension() != m && n != 0) {
      throw new IllegalArgumentException("Matrix::insertColumn vector dimension don't match matrix.");
    }

    shiftColumnsRight(j);

    for (int i=0;i<m;i++) {
      set(i, j, c.get(i));
    }
  }
  /**
   * Inserts a column with all values set to v. Matrix must be unlocked.
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
   * is needed to avoid ambiguity. Matrix must be unlocked.
   * 
   * @param j
   */
  public void insertColumn(int j) {
    shiftColumnsRight(j);
  }

  /**
   * Inserts row before position i. If matrix was empty, inserting row sets the number of columns.
   * Matrix must be unlocked.
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
  public void insertRow(int i, Vector<T> r) {
    if (r.getDimension() != n && m != 0) {
      throw new IllegalArgumentException("Matrix::insertRow vector dimension don't match matrix.");
    }
    shiftRowsDown(i);
    for (int j = 0; j < n; j++) {
      set(i, j, r.get(j));
    }
  }
  /**
   * Insert a row with all values set to v. Matrix must be unlocked.
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
   * needed to avoid ambiguity. Matrix must be unlocked.
   * 
   * @param i
   */
  public void insertRow(int i) {
    shiftRowsDown(i);
  }

  /**
   * Removes column at position j Matrix must be unlocked.
   * 
   * @param j
   */
  public void removeColumn(int j) {
    n--;
    
    for(var e : mat.descendingMap().entrySet()) {
      if(e.getKey().getSecond() == j) mat.remove(e.getKey());
      if(e.getKey().getSecond() > j) {
        mat.put(
              HomoPair.makeHomoPair(e.getKey().getFirst(),e.getKey().getSecond()-1), 
              mat.remove(e.getKey())
            );
      }
    }
  }

  /**
   * Removes row at position i Matrix must be unlocked.
   * 
   * @param i
   */
  public void removeRow(int i) {
    m--;
    
    for(var e : mat.descendingMap().entrySet()) {
      if(e.getKey().getFirst() == i) mat.remove(e.getKey());
      if(e.getKey().getFirst() > i) {
        mat.put(
              HomoPair.makeHomoPair(e.getKey().getFirst()-1,e.getKey().getSecond()), 
              mat.remove(e.getKey())
            );
      }
    }
  }

  /**
   * Overwrites column j with ArrayList c Matrix must be unlocked.
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
   * Sets all elements in column j to c Matrix must be unlocked.
   * 
   * @param j
   * @param c
   */
  public void setColumn(int j, T c) {
    for (int i = 0; i < m; i++) {
      set(i, j, c);
    }
  }
  public void setColumn(int j, Vector<T> c) {
    if (c.getDimension() != m) {
      throw new IllegalArgumentException("Matrix::setColumn vector dimension don't match matrix.");
    }
    for (int i=0;i<c.getDimension();i++) {
      set(i, j, c.get(i));
    }
  }
  /**
   * Overwrites row i with ArrayList l Matrix must be unlocked.
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
  public void setRow(int i, Vector<T> r) {
    if (r.getDimension() != n) {
      throw new IllegalArgumentException("Matrix::setRow vector dimension don't match matrix.");
    }
    for (int j=0;j<r.getDimension();j++) {
      set(i, j, r.get(j));
    }
  }
  /**
   * Sets all elements in row i to r Matrix must be unlocked.
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
  
  public Vector<T> getColumnVector(int j) {
    return Vector.of(getColumn(j));
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
  
  public Vector<T> getRowVector(int i) {
    return Vector.of(getRow(i));
  }
  
  /**
   * Returns the transpose of the matrix. Doesn't affect the original.
   * 
   * @return
   */
  public Matrix<T> getTranspose() {
    Matrix<T> o = new Matrix<T>(n, m);
    for(var e : mat.entrySet()) o.set(e.getKey().getFirst(),e.getKey().getSecond(), e.getValue());
    return o;
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
    o.init(m,n);
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
