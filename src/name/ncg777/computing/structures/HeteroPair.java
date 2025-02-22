package name.ncg777.computing.structures;

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

import name.ncg777.computing.Parsers;

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
public class HeteroPair<T extends Comparable<? super T>, U extends Comparable<? super U>>
    implements
      Comparable<HeteroPair<T, U>> {
  /**
   * Static method to construct a pair.
   * 
   * @param first
   * @param second
   * @return HeteroPair<T,U>
   */
  public static <T extends Comparable<? super T>, U extends Comparable<? super U>> HeteroPair<T, U> makeHeteroPair(
      T first, U second) {
    return new HeteroPair<T, U>(first, second);
  }

  protected T x;
  protected U y;

  public T getFirst() {
    return x;
  }

  public U getSecond() {
    return y;
  }

  public HeteroPair<U,T> converse() {return makeHeteroPair(y, x);}
  
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
    HeteroPair<T, U> other = (HeteroPair<T, U>) obj;
    return this.compareTo(other) == 0;
  }

  protected HeteroPair(T p_x, U p_y) {
    x = p_x;
    y = p_y;
  }

  /**
   * This compareTo function supports HeteroPair with null values.
   */
  @Override
  public int compareTo(HeteroPair<T, U> o) {
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
  
  public static <T extends Comparable<? super T>, U extends Comparable<? super U>> void toJSONObjectString(Function<T,String> printer1, Function<U,String> printer2, HeteroPair<T,U> pair, JsonGenerator gen) throws IOException {
    gen.writeStartArray();
    
    if(pair.getFirst() == null) {
      gen.writeNull();
    } else {
      gen.writeString(printer1.apply(pair.getFirst()));
    }
    if(pair.getSecond() == null) {
      gen.writeNull();
    } else {
      gen.writeString(printer2.apply(pair.getSecond()));
    }
    gen.writeEndArray();
  }
  
  
  public static <
    T extends Comparable<? super T>, 
    U extends Comparable<? super U>> 
      HeteroPair<T,U> parseJSONObject(
          String str, 
          Function<String,T> parser1,
          Function<String,U> parser2) {
    try {
      var b = new JsonFactoryBuilder().build();
      var p = b.setCodec(new ObjectMapper()).createParser(str).readValueAsTree();
      if(!p.isArray() || p.size()!=2) {
        throw new RuntimeException("invalid");
      }
      
      String l = p.get(0).toString();
      String r = p.get(1).toString();
      
      return makeHeteroPair(
          Parsers.quoteRemoverDecorator(
              Parsers.nullDecorator(parser1)).apply(l),
          Parsers.quoteRemoverDecorator(
              Parsers.nullDecorator(parser2)).apply(r));  
    } catch (IOException e) {
      throw new RuntimeException("invalid input");
    }    
  }
  public static <
  T extends Comparable<? super T>, 
  U extends Comparable<? super U>> 
    HeteroPair<T,U> parseJSONObject(
        com.fasterxml.jackson.core.TreeNode node, 
        Function<String,T> parser1,
        Function<String,U> parser2) {
  
    if(!node.isArray() || node.size()!=2) {
      throw new RuntimeException("invalid");
    }
    
    return makeHeteroPair(
        Parsers.quoteRemoverDecorator(
            Parsers.nullDecorator(parser1)).apply(node.get(0).toString()),
        Parsers.quoteRemoverDecorator(
            Parsers.nullDecorator(parser2)).apply(node.get(1).toString()));
  }  

}
