/**
 * Generic mutable Matrix class with basic functionnality.
 * 
 * Creation date : 2013-05-17
 * 
 * @author Nicolas Couture-Grenier
 */
package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.google.common.base.Joiner;


// TODO: code tests

/**
 * Generic mutable Matrix class with basic functionality.
 * 
 * @author Nicolas Couture-Grenier
 * 
 * @param <T>
 */
public class Matrix<T> {
  protected TreeMap<Integer, TreeMap<Integer, T>> mat;
  /** lock holds the flag specifying if the matrix is read-only or not. */
  protected boolean lock = false;
  /** Number of rows */
  protected int m;
  /** Number of columns */
  protected int n;
  private T defaultValue = null;

  /**
   * Checks whether the matrix is locked and throws a runtime exception if it is. It must be called
   * by all methods that may change the values of the matrix.
   */
  protected void checkLock() {
    if (lock) {
      throw new IllegalArgumentException(
          "Matrix This operation is not allowed because the matrix is locked.");
    }
  }

  /** Locks the matrix; makes it read-only. Public. */
  public void lock() {
    lock = true;
  }

  /** Unlocks the matrix. Protected. */
  protected void unlock() {
    lock = false;
  }

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
    this(p_mat.m, p_mat.n, p_mat.defaultValue);
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
    init(p_mat.rowCount(), p_mat.columnCount());
    setBlock(p_mat, 0, 0);
  }

  /**
   * Constructs an empty matrix.
   */
  public Matrix() {
    this.m = 0;
    this.n = 0;
    mat = new TreeMap<Integer, TreeMap<Integer, T>>();
  }

  /**
   * Construct a matrix filled with nulls.
   * 
   * @param m Number of rows
   * @param n Number of columns
   */
  public Matrix(int m, int n) {
    this(m, n, null);
  }

  /**
   * Construct a matrix filled with a default value.
   * 
   * @param m Number of rows
   * @param n Number of columns
   * @param defaultValue
   */
  public Matrix(int m, int n, T defaultValue) {
    init(m, n, defaultValue);
  }

  /**
   * 
   * @param m
   * @param n
   */
  protected void init(int m, int n) {
    init(m, n, null);
  }

  /**
   * Initializes matrix with a default value. Matrix must be unlocked.
   * 
   * @param m
   * @param n
   * @param defaultValue
   */
  protected void init(int p_m, int p_n, T defaultValue) {
    checkLock();
    mat = new TreeMap<Integer, TreeMap<Integer, T>>();
    this.defaultValue = defaultValue;

    this.m = p_m;
    this.n = p_n;
  }

  /**
   * Only prints each row on a line with values separated by tabs,
   * 
   * @return String
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    var joiner = Joiner.on(" ");
    for (int i = 0; i < m; i++) {
      sb.append(joiner.join(getRow(i)) + "\n");
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
    if (!mat.containsKey(i) || (mat.containsKey(i) && !mat.get(i).containsKey(j))) {
      return defaultValue;
    }
    return mat.get(i).get(j);
  }

  /**
   * Sets value at row i, column j. Matrix must be unlocked.
   * 
   * @param i
   * @param j
   * @return The previous value of the element at this position
   */
  public T set(int i, int j, T val) {
    checkLock();
    if (!mat.containsKey(i)) {
      mat.put(i, new TreeMap<Integer, T>());
    }
    return mat.get(i).put(j, val);
  }

  public void clear() {
    checkLock();
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
    ArrayList<T> r = getRow(from);
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
    ArrayList<T> c = getColumn(from);
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
    checkLock();
    int mm = p_mat.rowCount();
    int mn = p_mat.columnCount();

    if (i0 < 0 || j0 < 0 || i0 + mm > this.m || j0 + mn > this.n) {
      throw new IndexOutOfBoundsException("Matrix::setBlock index out of bounds");
    }

    for (int i = 0; i < mm; i++) {
      for (int j = 0; j < mn; j++) {
        T v = p_mat.get(i, j);
        if (v == null && defaultValue == null || (v != null && v.equals(defaultValue))) {
          continue;
        }
        Matrix.this.set(i + i0, j + j0, v);
      }
    }
  }

  private void shiftColumnsRight(int j) {
    n++;
    for (Integer i : mat.keySet()) {
      TreeSet<Integer> incr = new TreeSet<Integer>();

      for (Integer k : mat.get(i).keySet()) {
        if (k >= j) {
          incr.add(k);
        }
      }

      Iterator<Integer> it = incr.descendingIterator();

      while (it.hasNext()) {
        Integer k = it.next();
        mat.get(i).put(k + 1, mat.get(i).remove(k));
      }
    }
  }

  private void shiftRowsDown(int i) {
    m++;
    TreeSet<Integer> incr = new TreeSet<Integer>();
    for (Integer k : mat.keySet()) {
      if (k >= i) {
        incr.add(k);
      }
    }

    Iterator<Integer> it = incr.descendingIterator();

    while (it.hasNext()) {
      Integer k = it.next();
      mat.put(k + 1, mat.remove(k));
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
    checkLock();
    if (c.size() != m && n != 0) {
      throw new IllegalArgumentException("Matrix::insertColumn list size don't match matrix.");
    }

    shiftColumnsRight(j);

    int i = 0;
    for (T v : c) {
      set(i++, j, v);
    }
    m = c.size();
  }

  /**
   * Inserts a column with all values set to v. Matrix must be unlocked.
   * 
   * @param j
   * @param v
   */
  public void insertColumn(int j, T v) {
    checkLock();

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
    checkLock();
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
    checkLock();
    if (r.size() != n && m != 0) {
      throw new IllegalArgumentException("Matrix::insertRow list size don't match matrix.");
    }
    n = r.size();
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
    checkLock();
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
    checkLock();
    shiftRowsDown(i);
  }

  /**
   * Removes column at position j Matrix must be unlocked.
   * 
   * @param j
   */
  public void removeColumn(int j) {
    checkLock();
    n--;
    for (Integer i : mat.keySet()) {
      mat.get(i).remove(j);

      TreeSet<Integer> decr = new TreeSet<Integer>();

      for (Integer k : mat.get(i).keySet()) {
        if (k > j) {
          decr.add(k);
        }
      }

      Iterator<Integer> it = decr.iterator();

      while (it.hasNext()) {
        Integer k = it.next();
        mat.get(i).put(k - 1, mat.get(i).remove(k));
      }
    }

  }

  /**
   * Removes row at position i Matrix must be unlocked.
   * 
   * @param i
   */
  public void removeRow(int i) {
    checkLock();
    mat.remove(i);
    m--;

    TreeSet<Integer> decr = new TreeSet<Integer>();
    for (Integer k : mat.keySet()) {
      if (k > i) {
        decr.add(k);
      }
    }

    Iterator<Integer> it = decr.iterator();

    while (it.hasNext()) {
      Integer k = it.next();
      mat.put(k - 1, mat.remove(k));
    }
  }

  /**
   * Overwrites column j with ArrayList c Matrix must be unlocked.
   * 
   * @param j
   * @param c
   */
  public void setColumn(int j, List<T> c) {
    checkLock();
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
    checkLock();
    for (int i = 0; i < m; i++) {
      set(i, j, c);
    }
  }

  /**
   * Overwrites row i with ArrayList l Matrix must be unlocked.
   * 
   * @param i
   * @param l
   */
  public void setRow(int i, List<T> r) {
    checkLock();
    if (r.size() != n) {
      throw new IllegalArgumentException("Matrix::setRow list size don't match matrix.");
    }
    int j = 0;
    for (T e : r) {
      set(i, j++, e);
    }
  }

  /**
   * Sets all elements in row i to r Matrix must be unlocked.
   * 
   * @param i
   * @param r
   */
  public void setRow(int i, T r) {
    checkLock();
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
  public ArrayList<T> getColumn(int j) {
    ArrayList<T> o = new ArrayList<T>();
    for (int i = 0; i < m; i++) {
      o.add(get(i, j));
    }
    return o;
  }

  /**
   * Returns row i. Changes made to the row will not affect the matrix.
   * 
   * @param i >=0
   * @return
   */
  public ArrayList<T> getRow(int i) {
    ArrayList<T> r = new ArrayList<T>(n);
    for (int j = 0; j < n; j++) {
      r.add(get(i, j));
    }
    return r;
  }

  /**
   * Returns the transpose of the matrix. Doesn't affect the original.
   * 
   * @return
   */
  public Matrix<T> getTranspose() {
    Matrix<T> o = new Matrix<T>(n, m);
    for (Map.Entry<Integer, TreeMap<Integer, T>> e : mat.entrySet()) {
      for (Map.Entry<Integer, T> f : e.getValue().entrySet()) {
        o.set(f.getKey(), e.getKey(), f.getValue());
      }
    }
    return o;

  }

  public boolean contains(T el) {
    for (Integer i : mat.keySet()) {
      if (mat.get(i).containsValue(el)) {
        return true;
      }
    }
    return false;
  }

  

  private class RowIterator implements Iterator<T>{

    private int row=0;
    int j = 0;
    public RowIterator(int row0){
      this.row = row0;
      
    }
    @Override
    public boolean hasNext() {
      return j < n;
    }

    @Override
    public T next() {
      return get(row,j++);
    }

    @Override
    public void remove() {
      mat.get(row).remove(j-1);
    }
    
  }
  

  public void distinctRowsSorted() {
    sortRows();
    TreeMap<Integer,TreeMap<Integer,T>> t = 
        new TreeMap<Integer,TreeMap<Integer,T>>(); 
    
    int last = -1;
    
    int k = 0;
    
    for(int i=0;i<m;i++){
      if (last == -1 || 
          IterableComparator.compare(
              new RowIterator(i), new RowIterator(last)) != 0) {
        TreeMap<Integer,T> x = mat.get(i);
        if(x != null){
          t.put(k, x);
        }
        k++;
        last = i;
      }
    }
    mat = t;
    m = k;

  }

  private void swapRows(int a, int b){
    checkLock();
    TreeMap<Integer,T> tmp = null;
    if(mat.containsKey(a)){
      tmp = mat.get(a);  
    }
    
    if(mat.containsKey(b)){
      mat.put(a,mat.get(b));
    } else{
      mat.remove(a);
    }
    if(tmp != null){
      mat.put(b,tmp);
    } else{
      mat.remove(b);
    }
    
  }
  
  private int partition(int left, int right, int pivotIndex){
    checkLock();
    swapRows(pivotIndex,right);
    
    int storeIndex = left;
    
    for(int i=left;i<right;i++){
      int c = IterableComparator.compare(
          new RowIterator(i), new RowIterator(right));
      if(c < 1){
        swapRows(i,storeIndex);
        storeIndex++;
      }
      
    }
    swapRows(storeIndex,right);

    return storeIndex;
            
  }
    
  
     
  public void sortRows(){
    quicksort(0,m-1);
  }
  
  private void quicksort(int left, int right){
    if(left < right){
      int pivotIndex = findMedianOf3(left,right);

      int pivotNewIndex = partition(left, right, pivotIndex);

      quicksort(left, pivotNewIndex - 1);

      quicksort(pivotNewIndex + 1, right);
    }
  }
   
  private int findMedianOf3(int left, int right){
    int middle = (left + right)/2;
    Matrix<T> tmp = new Matrix<T>();
    tmp.n = n;
    tmp.m = 3;
    if(mat.containsKey(left)){tmp.mat.put(0,mat.get(left));}
    if(mat.containsKey(middle)){tmp.mat.put(1,mat.get(middle));}
    if(mat.containsKey(right)){tmp.mat.put(2,mat.get(right));}
    int o = 2 - tmp.partition(0,2,0);
    switch(o){
      case 0: return left;
      case 1: return middle;
      case 2: return right;
      default: throw new RuntimeException();
    }
    
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (lock ? 1231 : 1237);
    result = prime * result + m;
    result = prime * result + ((mat == null) ? 0 : mat.hashCode());
    result = prime * result + n;
    return result;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Matrix other = (Matrix) obj;
    if (lock != other.lock) return false;
    if (n != other.n) return false;
    if (m != other.m) return false;
    if (mat == null) {
      if (other.mat != null) return false;
    }
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        if (this.get(i, j) == null && other.get(i, j) == null) {
          continue;
        } else if ((this.get(i, j) == null && other.get(i, j) != null)
            || (this.get(i, j) != null && !this.get(i, j).equals(other.get(i, j)))) {
          return false;
        }
      }

    }

    return true;
  }

}
