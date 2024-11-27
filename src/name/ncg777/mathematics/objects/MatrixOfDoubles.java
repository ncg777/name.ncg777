package name.ncg777.mathematics.objects;

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

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;

public class MatrixOfDoubles extends Matrix<Double> {
  public MatrixOfDoubles() {
    super();
  }

  /**
   * Construct a matrix filled with nulls.
   * 
   * @param m Number of rows
   * @param n Number of columns
   */
  public MatrixOfDoubles(int m, int n) {
    this(m,n,Double.valueOf(0.0));
  }
  
  public MatrixOfDoubles(int m, int n, Double fill) {
    super(m,n,fill);
  }
  
  @Override
  public VectorOfDoubles getColumnVector(int j) {
    return new VectorOfDoubles(super.getColumnVector(j));
  }
  
  @Override
  public VectorOfDoubles getRowVector(int i) {
    return new VectorOfDoubles(super.getRowVector(i));
  }
  
  public static MatrixOfDoubles fromColtMatix(DoubleMatrix2D mat) {
    var o = new MatrixOfDoubles(mat.rows(),mat.columns());
    for(int i=0;i<o.rowCount();i++) {
      for(int j=0;j<o.columnCount();j++) {
        o.set(i, j, mat.get(i, j));
      }
    }
    return o;
  }
  public DoubleMatrix2D toColtMatix() {
    var o = DoubleFactory2D.sparse.make(m,n);
    
    for(int i=0;i<o.rows();i++) {
      for(int j=0;j<o.columns();j++) {
        o.set(i, j, get(i, j).doubleValue());
      }
    }
    return o;
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
        double[] arr = new double[n];
        int j=0;
        for(var v : getRow(i)) arr[j++] = v;
        gen.writeArray(arr, i, i);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static MatrixOfDoubles parseJSONFile(String path) throws JsonParseException, IOException {
    var b = new JsonFactoryBuilder().build();
    var p = b.setCodec(new ObjectMapper()).createParser(new File(path)).readValueAsTree();
    return parseJSONNode(p);
  }

  private static MatrixOfDoubles parseJSONNode(TreeNode p) {
    if (p == null) return null;
          
    MatrixOfDoubles arr = null; 
    if(p.size() == 0) {
      arr = new MatrixOfDoubles(0, 0);
    } else {
      arr = new MatrixOfDoubles(p.size(), p.get(0).size());
    }
    
    for (int i = 0; i < arr.rowCount(); i++) {
      for(int j=0;j<arr.columnCount();j++) {
        arr.set(i, j, Double.parseDouble(p.get(i).get(j).toString()));
      }
    }
    return arr;  
  }

  public static MatrixOfDoubles parseJSONString(String str) {
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
