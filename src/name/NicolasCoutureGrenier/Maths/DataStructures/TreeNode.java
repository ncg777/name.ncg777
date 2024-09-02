package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
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
  
  public String toJSONArrayString(Function<T,String> printer) {
    try {
      StringWriter sw = new StringWriter();
      var gen = new JsonFactory(new JsonFactoryBuilder()).createGenerator(sw);
      toJSONArrayString(printer, this, gen);
      return sw.toString();  
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }
  
  private void toJSONArrayString(Function<T,String> printer, TreeNode<T> root, JsonGenerator gen) {
    try {
      if(root.content != null) { gen.writeRaw(printer.apply(this.content));}
      for(int i=0;i<this.size();i++) {
        toJSONArrayString(printer, root.get(i), gen);
      }  
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
  public static <T> TreeNode<T> parseJSONArray(String str, Function<String,T> parser) {
    return TreeNode.parseArray(str,parser,new TreeNode<T>());
  }
  public static <T> TreeNode<T> parseArray(String str, Function<String,T> parser, TreeNode<T> root) {
    try {
      var b = new JsonFactoryBuilder().build();
      var p = b.createParser(str).readValueAsTree();
      
      for(int i=0;i<p.size();i++) {
        
        var t = p.get(i);
        if(!t.isArray()) {
          root.add(new TreeNode<T>(parser.apply(t.toString()), root));
        } else { 
          var n = parseArray(t.toString(), parser, root);
          
          root.add(n);
        } 
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
  public String toString() {
    return this.toString(0, "  ","\n","[", "]", (s) -> s.toString()); 
  }
  public String toString(
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
            (q.toString(
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

}
