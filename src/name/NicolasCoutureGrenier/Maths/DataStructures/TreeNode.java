package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

public class TreeNode<T> extends ArrayList<TreeNode<T>> {
  private static final long serialVersionUID = 1L;

  T content = null;
  TreeNode<T> parent = null;

  public TreeNode() {
    super();
  }
  public TreeNode(T t) {
    super();
    content = t;
  }

  public TreeNode(T t, TreeNode<T> parent) {
    super();
    this.parent = parent;
    content = t;
  }
  public Map<T,TreeNode<T>> toMap(){
    HashMap<T, TreeNode<T>> h = new HashMap<>();
    for(var i: this) h.put(i.content, i);
    h.put(this.content,this);
    return h;
  }
  public void setContent(T t) {
    content = t;
  }

  public T getNode() {
    return content;
  }
  public boolean isRoot() { return this.parent == null; }
  public TreeNode<T> getParent() {return this.parent;}
  public TreeNode<T> getRoot(){ 
    TreeNode<T> current = this;
    
    while(!current.isRoot()) { 
      current = current.getParent();
    }
    return current;
  }
  
  public String toJSONObjectString(Function<T,String> printer) {
    try {
      StringWriter sw = new StringWriter();
      var gen = new JsonFactory(new JsonFactoryBuilder()).createGenerator(sw);
      toJSONObjectString(printer, this, gen);
      gen.flush();
      return sw.getBuffer().toString();  
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }
  
  private static <T> void toJSONObjectString(Function<T,String> printer, TreeNode<T> root, JsonGenerator gen) {
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
  public static <T> TreeNode<T> parseJSONObject(String str, Function<String,T> parser) {
    return TreeNode.parseJSONObject(str,parser,new TreeNode<T>());
  }
  public static <T> TreeNode<T> parseJSONObject(String str, Function<String,T> parser, TreeNode<T> root) {
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
        var t = TreeNode.parseJSONObject(arr.get(i).toString(), parser);
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
    return this.toString((e) -> e.toString());
  }
  
  public String toString(Function<T,String> printer) {
    return this.toJSONObjectString(printer); 
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
    TreeNode other = (TreeNode) obj;
    if(this.size()!=other.size()) return false;
    if(!this.content.equals(other.content)) return false;
    return this.containsAll(other);
  }

}
