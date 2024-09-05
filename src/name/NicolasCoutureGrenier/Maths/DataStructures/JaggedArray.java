package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.io.FileNotFoundException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Ordering;

import name.NicolasCoutureGrenier.CS.Parsers;
import name.NicolasCoutureGrenier.CS.Printers;

public class JaggedArray<T extends Comparable<? super T>>  
  implements Comparable<JaggedArray<T>> {
  private Ordering<T> ordering = Ordering.natural().nullsFirst();
  
  T value = null;
  JaggedArray<T> parent = null;
  SparseList<JaggedArray<T>> children;
  public JaggedArray() {
    super();
  }
  public JaggedArray(T t) {
    super();
    value = t;
  }
  public boolean isValue() {
    return this.children == null;
  }
  
  public JaggedArray<T> set(int index, T element) {
    return children.set(index, new JaggedArray<T>(element,this));
  }
  public JaggedArray<T> set(int index, JaggedArray<T> element) {
    element.parent = this;
    return children.set(index, element);
  }
  public boolean add(T element) {
    return children.add(new JaggedArray<T>(element,this));
  }
  
  public boolean add(JaggedArray<T> t) {
    if(t.parent != null) {
      t.parent = this;
    }
    if(children == null) children = new SparseList<>();
    return children.add(t); 
  }

  public JaggedArray<T> get(Integer...coordinates) {
    JaggedArray<T> o = this;
    for(var i : coordinates) {
      o = o.children.get(i);
    }
    return o;  
  }
  public T getValue(Integer...coordinates) {
    return get(coordinates).getValue();
  }
  
  public boolean set(T value, Integer... coordinates) {
    return get(coordinates).setValue(value);
  }
  public JaggedArray(T t, JaggedArray<T> parent) {
    this(t);
    if(parent == null) throw new RuntimeException("parent is null.");
    parent.add(this);
  }
  
  public boolean setValue(T t) {
    var o = ordering.compare(t, getValue()) == 0;
    value = t;
    children = null;
    return o;
  }

  public T getValue() {
    if(!isValue()) throw new RuntimeException("is not value");
    return value;
  }
  
  public boolean isRoot() { return this.getRoot().equals(this); }
  public JaggedArray<T> getParent() {return this.parent;}
  public JaggedArray<T> getRoot(){ 
    JaggedArray<T> current = this;
    
    while(!current.isRoot()) { 
      current = current.getParent();
    }
    return current;
  }
  
  public void printToJSON(Function<T,String> printer, String path) throws FileNotFoundException {
    PrintWriter pw = new PrintWriter(path);
    printToJSON(printer,pw);
    pw.flush();
    pw.close();
  }
  
  public void printToJSON(Function<T,String> printer, Writer sw) {
    try {
      var gen = new JsonFactory(new JsonFactoryBuilder()).createGenerator(sw);
      toJSONArrayString(printer, this, gen);
      gen.flush();  
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public String toJSONArrayString(Function<T,String> printer) {
    StringWriter sw = new StringWriter();
    this.printToJSON(Printers.nullDecorator(printer),sw);
    return sw.getBuffer().toString();
  }
  
  private static <T extends Comparable<? super T>> void toJSONArrayString(Function<T,String> printer, JaggedArray<T> arr, JsonGenerator gen) {
    try {
      if(arr.isValue()) {
        if(arr.getValue() == null) {
          gen.writeNull();
        } else {
          gen.writeString(printer.apply(arr.getValue()));  
        }
      } else if((arr != null)) {
        gen.writeStartArray();
        for(int i=0;i<arr.children.size();i++) {
          toJSONArrayString(printer, arr.children.get(i), gen); 
        }
        gen.writeEndArray();
      }
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
  
  public static <T extends Comparable<? super T>> JaggedArray<T> parseJSONArray(String str, Function<String,T> parser) {
    return JaggedArray.parseJSONArray(str,Parsers.nullDecorator(Parsers.quoteRemoverDecorator(parser)),new JaggedArray<T>());
  }
  
  private static <T extends Comparable<? super T>> JaggedArray<T> parseJSONArray(String str, Function<String,T> parser, JaggedArray<T> root) {
    try {      
      var b = new JsonFactoryBuilder().build();
      var p = b.setCodec(new ObjectMapper()).createParser(str).readValueAsTree();
      if(p == null) return root;
      if(p.isArray()) {
        JaggedArray<T> arr = new JaggedArray<T>(null, root);
        arr.children = new SparseList<>();
        for(int i=0;i<p.size();i++) {
          parseJSONArray(p.get(i).toString(), parser, arr);
        }
        return arr;
      } else {
        return new JaggedArray<T>(parser.apply(str),root);
      }  
    } catch (JsonParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
    
  }
  
  @Override
  public String toString() {
    return this.toString((s) -> s.toString());
  }
  
  public String toString(Function<T,String> printer) {
    return this.toJSONArrayString(Printers.nullDecorator(printer)); 
  }
  
  @SuppressWarnings({"unchecked"})
  private static <T extends Comparable<? super T>> JaggedArray<T> fromArray(Object object, JaggedArray<T> parent) {  
    if(object != null && object.getClass().isArray()) {
      int l = Array.getLength(object);
      JaggedArray<T> t = new JaggedArray<T>(null, parent);
      t.children = new SparseList<>();
      for(int i=0;i<l;i++) {
        var o = Array.get(object, i);
        fromArray(o,t);
      }
      return parent;
    } else {
      return new JaggedArray<T>((T)object,parent);
    }
  }
  public static <T extends Comparable<? super T>> JaggedArray<T> fromArray(Object object) {
    return fromArray(object,new JaggedArray<T>());
  }
  
  private Class<T> getContentClass() {
    var o = searchContentClass();
    return o == null ? getRoot().searchContentClass() : o;
  }
  
  private Class<T> searchContentClass() {
    @SuppressWarnings("unchecked")
    final Function<T,Class<T>> f = (c) -> c == null ? null : (Class<T>) c.getClass();
    var o = f.apply(this.value);
    if(o != null) {
      return o;
    } else {
      for(var n : this.children) {
        o = n.searchContentClass();
        if(o!= null) return o;
      } 
    }
    return o;
  }
  
  public Object[] toArray() {
    var o = toJaggedArray();
    return (Object[])o;
  }
  
  private Object toJaggedArray() {   
    if(this.isValue()) {
      return this.getValue();
    } else {
      Class<T> c = this.getContentClass();
      
      if(c==null) return null;
      List<Object> l = new ArrayList<Object>();
      for(int i=0;i<this.children.size();i++) {
        if(this.children.get(i) == null) { l.add(null);}
        else {
          var arr = this.children.get(i).toJaggedArray();
          l.add(arr);  
        }
      }
      Object o = Array.newInstance(Object.class, l.size());
      
      for(int i=0;i<l.size();i++) {
        Array.set(o,i,l.get(i));
      }
      
      return o;
    }
  }
  
  public int compareTo(JaggedArray<T> other) {
    var o = ordering.compare(this.getValue(), other.getValue());
    return o == 0 ? IterableComparator.compare(this.children.iterator(), other.children.iterator()) : o;
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(children, ordering, parent, value);
  }
  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    
    JaggedArray other = (JaggedArray) obj;
    return this.compareTo(other) == 0;
  }
}
