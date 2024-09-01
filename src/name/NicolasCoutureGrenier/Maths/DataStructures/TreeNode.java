package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.common.base.Joiner;

public class TreeNode<T> extends ArrayList<TreeNode<T>> {
  private static final int tabSpaces = 1;
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
    return this.directDescendantsString(0); 
  }
  public String directDescendantsString(int level) {
    String indent = "";
    for(int x=0;x<tabSpaces;x++) indent+="\t";
    final String indent1 = indent;
    
    StringBuilder b = new StringBuilder();
    Iterator<TreeNode<T>> i = this.iterator();
    while (i.hasNext()) {
      var t = i.next();
      
      b.append(Joiner.on(",\n").join(t.stream().map((q) -> indent1 + q.directDescendantsString(level+1)).toList()));
    }
    b.append("\n");
    return b.toString();
  }
}
