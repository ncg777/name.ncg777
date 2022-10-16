package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.util.ArrayList;
import java.util.Iterator;

public class TreeNode<T> extends ArrayList<TreeNode<T>> {

  private static final long serialVersionUID = 1L;

  T node;
  TreeNode<T> parent = null;

  public TreeNode(T t) {
    super();
    node = t;
  }

  public TreeNode(T t, TreeNode<T> parent) {
    super();
    this.parent = parent;
    node = t;
  }

  public void setNode(T t) {
    node = t;
  }

  public T getNode() {
    return node;
  }

  public String directDescendantsString() {
    String o = "";
    Iterator<TreeNode<T>> i = this.iterator();
    while (i.hasNext()) {
      o += i.next().getNode().toString();
      if (i.hasNext()) {
        o += " ";
      }
    }
    return o;
  }
}
