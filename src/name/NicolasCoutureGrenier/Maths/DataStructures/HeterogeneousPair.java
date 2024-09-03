/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */

package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

/**
 * This class represents an ordered pair of objects of 2 different types. It overrides hashCode and
 * equals and the pairs can be compared. The generic types must be comparable. Values stored in the
 * pair may be null without it causing problems with equals, hashcode or compareTo.
 * 
 * @author Nicolas Couture-Grenier
 * 
 * @param <T> The type of the first element
 * @param <U> The type of the second element
 */
public class HeterogeneousPair<T extends Comparable<? super T>, U extends Comparable<? super U>>
    implements
      Comparable<HeterogeneousPair<T, U>> {
  /**
   * Static method to construct a pair.
   * 
   * @param first
   * @param second
   * @return HeterogeneousPair<T,U>
   */
  public static <T extends Comparable<? super T>, U extends Comparable<? super U>> HeterogeneousPair<T, U> makeHeterogeneousPair(
      T first, U second) {
    return new HeterogeneousPair<T, U>(first, second);
  }

  protected T x;
  protected U y;

  public T getFirst() {
    return x;
  }

  public U getSecond() {
    return y;
  }

  public HeterogeneousPair<U,T> converse() {return makeHeterogeneousPair(y, x);}
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (x != null) {
      result = prime * result + x.hashCode();
    }
    if (y != null) {
      result = prime * result + y.hashCode();
    }
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    @SuppressWarnings("unchecked")
    HeterogeneousPair<T, U> other = (HeterogeneousPair<T, U>) obj;
    return this.compareTo(other) == 0;
  }

  protected HeterogeneousPair(T p_x, U p_y) {
    x = p_x;
    y = p_y;
  }

  /**
   * This compareTo function supports HeterogeneousPair with null values.
   */
  @Override
  public int compareTo(HeterogeneousPair<T, U> o) {
    return ComparisonChain.start().compare(x, o.x, Ordering.natural().nullsFirst())
        .compare(y,o.y,Ordering.natural().nullsFirst()).result();
  }
  
  @Override
  public String toString() {
    return toString((t) -> t.toString(), (u) -> u.toString());
  }
  
  public String toString(Function<T,String> printer1, Function<U,String> printer2) {
    return this.toJSONObjectString(printer1, printer2);
  }
  
  public void printToJSON(Function<T,String> printer1, Function<U,String> printer2, String path) throws FileNotFoundException {
    PrintWriter pw = new PrintWriter(path);
    printToJSON(printer1,printer2,pw);
    pw.flush();
    pw.close();
  }
  public void printToJSON(Function<T,String> printer1, Function<U,String> printer2, Writer sw) {
    try {
      var gen = new JsonFactory(new JsonFactoryBuilder()).createGenerator(sw);
      toJSONObjectString(printer1,printer2, this, gen);
      gen.flush();  
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public String toJSONObjectString(Function<T,String> printer1, Function<U,String> printer2) {
    StringWriter sw = new StringWriter();
    this.printToJSON(printer1,printer2,sw);
    return sw.getBuffer().toString();
  }
  
  public static <T extends Comparable<? super T>, U extends Comparable<? super U>> void toJSONObjectString(Function<T,String> printer1, Function<U,String> printer2, HeterogeneousPair<T,U> pair, JsonGenerator gen) throws IOException {
    gen.writeStartArray();
    String[] arr = new String[2];
    
    arr[0] = printer1.apply(pair.getFirst());
    arr[1] = printer2.apply(pair.getSecond());
    
    gen.writeArray(arr, 0, 2);
    gen.writeEndArray();
  }
  
  
  public static <
    T extends Comparable<? super T>, 
    U extends Comparable<? super U>> 
      HeterogeneousPair<T,U> parseJSONObject(
          String str, 
          Function<String,T> parser1,
          Function<String,U> parser2) {
    try {
      var b = new JsonFactoryBuilder().build();
      var p = b.setCodec(new ObjectMapper()).createParser(str).readValueAsTree();
      if(!p.isArray() || p.size()!=2) {
        throw new RuntimeException("invalid");
      }
      
      return makeHeterogeneousPair(parser1.apply(p.get(0).toString()),parser2.apply(p.get(1).toString()));  
    } catch (IOException e) {
      throw new RuntimeException("invalid input");
    }
    
  }
    

}
