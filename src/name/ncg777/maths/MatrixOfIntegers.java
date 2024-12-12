package name.ncg777.maths;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
  
  /**
   * Construct a matrix filled with nulls.
   * 
   * @param m Number of rows
   * @param n Number of columns
   */
  public MatrixOfIntegers(int m, int n) {
    this(m,n,Integer.valueOf(0));
  }
  
  public MatrixOfIntegers(int m, int n, Integer fill) {
    super(m,n,fill);
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
  
  public void printToJSON(String path) throws FileNotFoundException {
    PrintWriter pw = new PrintWriter(path);
    printToJSON(pw);
    pw.flush();
    pw.close();
  }

  private void printToJSON(Writer sw) {
    try {
      var builder = new JsonFactoryBuilder().build();
      var mapper = new ObjectMapper();
      builder.setCodec(mapper);
      var gen = builder.createGenerator(sw);
      gen.useDefaultPrettyPrinter();
      
      toJSONArrayString(gen);
      gen.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public String toJSONArrayString() {
    StringWriter sw = new StringWriter();
    this.printToJSON(sw);
    return sw.getBuffer().toString();
  }

  private void toJSONArrayString(JsonGenerator gen) {
    try {
      
      for(int i=0;i<m;i++) {
        int[] arr = new int[n];
        int j=0;
        for(var v : getRow(i)) arr[j++] = v;
        gen.writeArray(arr, i, i);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static MatrixOfIntegers parseJSONFile(String path) throws JsonParseException, IOException {
    var b = new JsonFactoryBuilder().build();
    var p = b.setCodec(new ObjectMapper()).createParser(new File(path)).readValueAsTree();
    return parseJSONNode(p);
  }

  private static MatrixOfIntegers parseJSONNode(TreeNode p) {
    if (p == null) return null;
          
    MatrixOfIntegers arr = null; 
    if(p.size() == 0) {
      arr = new MatrixOfIntegers(0, 0);
    } else {
      arr = new MatrixOfIntegers(p.size(), p.get(0).size());
    }
    
    for (int i = 0; i < arr.rowCount(); i++) {
      for(int j=0;j<arr.columnCount();j++) {
        arr.set(i, j, Integer.parseInt(p.get(i).get(j).toString()));
      }
    }
    return arr;  
  }

  public static MatrixOfIntegers parseJSONString(String str) {
    try {
      var b = new JsonFactoryBuilder().build();
      var p = b.setCodec(new ObjectMapper()).createParser(str).readValueAsTree();
      return parseJSONNode(p);
    } catch (JsonParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }
}
