package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.io.FileNotFoundException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

import name.NicolasCoutureGrenier.CS.Parsers;
import name.NicolasCoutureGrenier.CS.Printers;

public class Tree<T> extends ArrayList<Tree<T>> {
  private static final long serialVersionUID = 1L;

  T content = null;
  Tree<T> parent = null;

  public Tree() {
    super();
  }
  public Tree(T t) {
    super();
    content = t;
  }
  
  public boolean add(Tree<T> t) {
    t.parent = this;
    return super.add(t);
  }

  public Tree(T t, Tree<T> parent) {
    this(t);
    parent.add(this);
  }
  public Map<T,Tree<T>> toMap(){
    HashMap<T, Tree<T>> h = new HashMap<>();
    for(var i: this) h.put(i.content, i);
    h.put(this.content,this);
    return h;
  }
  public void setContent(T t) {
    content = t;
  }

  public T getContent() {
    return content;
  }
  public boolean isRoot() { return this.parent == null; }
  public Tree<T> getParent() {return this.parent;}
  public Tree<T> getRoot(){ 
    Tree<T> current = this;
    
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
      toJSONObjectString(printer, this, gen);
      gen.flush();  
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public String toJSONObjectString(Function<T,String> printer) {
    StringWriter sw = new StringWriter();
    this.printToJSON(Printers.nullDecorator(printer),sw);
    return sw.getBuffer().toString();
  }
  
  private static <T> void toJSONObjectString(Function<T,String> printer, Tree<T> root, JsonGenerator gen) {
    try {
      String fieldName= printer.apply(root.content);
      
      gen.writeStartObject();
      
      
      gen.writeArrayFieldStart(fieldName);
      for(int i=0;i<root.size();i++) {
        toJSONObjectString(printer, root.get(i), gen);
      }
      gen.writeEndArray();
      gen.writeEndObject();
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
  public static <T> Tree<T> parseJSONObject(String str, Function<String,T> parser) {
    return Tree.parseJSONObject(str,Parsers.nullDecorator(parser),new Tree<T>());
  }
  private static <T> Tree<T> parseJSONObject(String str, Function<String,T> parser, Tree<T> root) {
    try {
      
      var b = new JsonFactoryBuilder().build();
      var p = b.setCodec(new ObjectMapper()).createParser(str).readValueAsTree();
      if(!p.isObject()) {
        throw new RuntimeException("invalid");
      }
      
      String fieldname = p.fieldNames().next();
      root.content = parser.apply(fieldname);
      
      var arr = p.get(fieldname);
      if(!arr.isArray()) throw new RuntimeException("invalid");
     
      for(int i=0;i<arr.size();i++) {
        var t = Tree.parseJSONObject(arr.get(i).toString(), parser);
        root.add(t);
      }
      
      return root;  
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
    return this.toJSONObjectString(Printers.nullDecorator(printer)); 
  }
  public String toIndentedString(
      final int level,
      final String indentationStr,
      final String nodeSeparator,
      final String leftEnclose, 
      final String rightEnclose, 
      final Function<T,String> printer
      ) {
    String indent = "";
    for(int i=0;i<level;i++) indent += indentationStr;
    final String indent1 = indent;
    var subItems = this.stream().<String>map(
        (q) -> { 
          return  
            (q.toIndentedString(
                level+1,
                indentationStr,
                nodeSeparator, 
                leftEnclose, 
                rightEnclose,
                printer));
          }
        ).toList();
    
    StringBuilder b = new StringBuilder();
    
    b.append(indent1+leftEnclose+printer.apply(this.content)+rightEnclose);  
    
    
    if(subItems.size() > 0) {
      b.append(nodeSeparator+Joiner.on(nodeSeparator).join(subItems));  
    }
    
    return b.toString();
  }

  
  @SuppressWarnings("unchecked")
  private static <T> Tree<T> fromArray(Object object, Tree<T> parent) {  
    if(object != null && object.getClass().isArray()) {
      int l = Array.getLength(object);
      Tree<T> x = new Tree<T>();
      for(int i=0;i<l;i++) {
        fromArray(Array.get(object, i),x);
        
      }
      parent.add(x);
      return parent;
    
    } else {
      if(object == null) return new Tree<T>(null,parent);
      return new Tree<T>((T)object,parent);
    }
  }
  public static <T> Tree<T> fromArray(Object object) {
    Tree<T> o = new Tree<T>();
    fromArray(object,o);
    return o;
  }
  
  @SuppressWarnings("unchecked")
  public Class<T> getContentClass() {
    if(this.content != null) {
      return (Class<T>)this.content.getClass();
    } else {
      for(var n : this) {
        var c = n.getContentClass();
        if(c!= null) return c;
      } 
    }
  
    return null;
  }
  
  public int getDepth() {
    if(this.size() == 0) return 0;
    
    int max = -1;
    for(Tree<T> i : this) {
      int x = i.getDepth();
      if(x > max) max = x;
    }
    return max+1;
  }
  
  /**
   * @return A jagged array representing this tree
   */
  @Override
  public Object[] toArray() {
    var o = toJaggedArray();
    return (Object[])o;
  }
  
  private Object toJaggedArray() {   
    if(this.getDepth() == 0) {
      if(this.content == null) {
        Object[] o = new Object[0];
        return o;
      }
      Class<T> c = this.getContentClass();
      var o = Array.newInstance(c, 1);
      Array.set(o, 0, this.content);
      return o;
    } else if(this.getDepth() == 1) {
      Class<T> c = this.getContentClass();
      var o = Array.newInstance(c, this.size(),1);
      for(int i=0;i<this.size();i++) {
        Array.set(o, i, this.get(i).toJaggedArray());  
      }
      
      return o;
    } else {
      Class<T> c = this.getContentClass();
      
      if(c==null) return null;
      List<Object> l = new ArrayList<Object>();
      for(int i=0;i<this.size();i++) {
        var arr = this.get(i).toJaggedArray();
        l.add(arr);
      }
      Object o = Array.newInstance(Object.class, l.size(),1);
      
      for(int i=0;i<l.size();i++) {
        Array.set(o,i,l.get(i));
      }
      
      return o;
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(content, parent);
    return result;
  }
  @SuppressWarnings("rawtypes")
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    Tree other = (Tree) obj;
    if(this.size()!=other.size()) return false;
    if(!this.content.equals(other.content)) return false;
    return this.containsAll(other);
  }
}
