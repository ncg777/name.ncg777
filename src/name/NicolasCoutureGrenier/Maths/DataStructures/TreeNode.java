package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;

import com.google.common.base.Joiner;

public class TreeNode<T> extends ArrayList<TreeNode<T>> {
  private static final long serialVersionUID = 1L;

  T content;
  TreeNode<T> parent = null;

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
  
  public String toString() {
    return this.toString(0, " ", 1,"\n","[", "]", (s) -> s.toString()); 
  }
  
  public String toString(
      final int level, 
      final String indentationStr, 
      final int indentationCount,
      final String nodeSeparator,
      final String leftEnclose, 
      final String rightEnclose, 
      final Function<T,String> printer
      ) {
    String indent = "";
    for(int x=0;x<(indentationCount*level);x++) indent+=indentationStr;
    final String indent1 = indent;
    
    StringBuilder b = new StringBuilder();
    Iterator<TreeNode<T>> i = this.iterator();
    while (i.hasNext()) {
      var t = i.next();
      
      b.append(
          indent1 +
          leftEnclose +
          printer.apply(t.getNode()) + 
          rightEnclose +
          nodeSeparator + 
          Joiner.on(nodeSeparator).join(
          t.stream().<String>map(
              (q) -> { 
                return 
                  ( q.toString(
                      level+1, 
                      indentationStr, 
                      indentationCount, 
                      nodeSeparator, 
                      leftEnclose, 
                      rightEnclose,
                      printer));
                }
              ).toList()
       ) + nodeSeparator);
    }
    return b.toString();
  }
}
