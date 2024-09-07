package name.NicolasCoutureGrenier.CS.DataStructures;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

import java.io.File;
import java.io.FileNotFoundException;

import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Ordering;

import name.NicolasCoutureGrenier.CS.Parsers;

public class JaggedList<T extends Comparable<? super T>>
    implements Comparable<JaggedList<T>>, Iterable<JaggedList<T>> {
  private Ordering<T> ordering = Ordering.natural().nullsFirst();

  T value = null;
  JaggedList<T> parent = null;
  SparseList<JaggedList<T>> children;

  public JaggedList() {
    super();
  }

  public JaggedList(T t) {
    super();
    setValue(t);
  }

  public JaggedList(int... dimensions) {
    this();
    init(dimensions);
  }

  public int size() {
    if (children == null) return -1;
    return children.size();
  }
  public void init(int... dimensions) {
    this.init(null, dimensions);
  }
  public void init(T fillValue, int... dimensions) {
    if (dimensions == null || dimensions.length == 0) {
      this.setValue(fillValue);
      return;
    }
    
    for(int i=0;i<dimensions[0];i++) {
      newChild();
    }
    for (var c : children) {
      c.init(fillValue, Arrays.copyOfRange(dimensions, 1, dimensions.length));
    }
  }

  public boolean isValue() {
    return this.children == null;
  }

  public JaggedList<T> set(int index, T element) {
    children.get(index).parent = null;
    return children.set(index, new JaggedList<T>(element, this));
  }

  public JaggedList<T> set(int index, JaggedList<T> element) {
    element.parent = this;
    var current = children.get(index);
    if(current == element) return current;
    current.parent = null;
    return children.set(index, element);
  }

  public boolean add(T element) {
    var c = newChild();
    return c.setValue(element);
  }

  public JaggedList<T> newChild() {
    var o = new JaggedList<T>();
    addChild(o);
    return o;
  }

  public boolean addChild(JaggedList<T> t) {
    t.parent = this;
    this.value = null;
    if (children == null) children = new SparseList<>();
    return children.add(t);
  }

  public JaggedList<T> get(int... coordinates) {
    JaggedList<T> o = this;
    for (var i : coordinates) {
      o = o.children.get(i);
    }
    return o;
  }

  public T getValue(int... coordinates) {
    return get(coordinates).getValue();
  }

  public boolean set(T value, int... coordinates) {
    return get(coordinates).setValue(value);
  }

  public JaggedList(T t, JaggedList<T> parent) {
    this(t);
    if (parent == null) throw new RuntimeException("parent is null.");
    parent.addChild(this);
  }
  public boolean setValue(T t) {
    var o = ordering.compare(t, getValue()) != 0;
    value = t;
    children = null;
    return o;
  }

  public T getValue() {
    if (!isValue()) throw new RuntimeException("is not value");
    return value;
  }

  public boolean isRoot() {
    return this.parent==null;
  }

  public JaggedList<T> getParent() {
    return this.parent;
  }

  public int getDepth() {
    if(parent != null) return parent.getDepth()+1;
    return 0;
  }
  
  public int getHeight() {
    if(isValue()) return 0;
    int o = -1;
    for(var c : children) {
      var h = c.getHeight();
      o = h > o ? h : o;
    }
    return o+1;
  }
  public int getIndex() {
    if(parent == null) return -1;
    for(int i=0;i<parent.size();i++) {
      if(parent.get(i) == this) return i;
    }
    throw new RuntimeException("not found?");
  }
  
  public int[] getCoordinates() {
    var d = getDepth();
    
    int[] o = new int[d];
    
    int i = d;
    var current = this;
    while(i>0) {
      o[--i] = current.getIndex();
      current = current.parent;
    }
    return o;
  }
  
  public JaggedList<T> getRoot() {
    JaggedList<T> current = this;

    while (!current.isRoot()) {
      current = current.getParent();
    }
    return current;
  }

  public void printToJSON(String path) throws FileNotFoundException {
    printToJSON((e) -> e.toString(), path);
  }

  public void printToJSON(Function<T, String> printer, String path) throws FileNotFoundException {
    PrintWriter pw = new PrintWriter(path);
    printToJSON(printer, pw);
    pw.flush();
    pw.close();
  }

  private void printToJSON(Function<T, String> printer, Writer sw) {
    try {
      var builder = new JsonFactoryBuilder().build();
      var mapper = new ObjectMapper();
      builder.setCodec(mapper);
      var gen = builder.createGenerator(sw);
      gen.useDefaultPrettyPrinter();
      
      toJSONArrayString(printer, this, gen);
      gen.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public String toJSONArrayString(Function<T, String> printer) {
    StringWriter sw = new StringWriter();
    this.printToJSON(printer, sw);
    return sw.getBuffer().toString();
  }

  private static <T extends Comparable<? super T>> void toJSONArrayString(
      Function<T, String> printer, JaggedList<T> arr, JsonGenerator gen) {
    try {
      if (arr.isValue()) {
        if (arr.getValue() == null) {
          gen.writeNull();
        } else {
          gen.writeString(printer.apply(arr.getValue()));
        }
      } else if ((arr != null)) {
        gen.writeStartArray();
        for (int i = 0; i < arr.children.size(); i++) {
          toJSONArrayString(printer, arr.children.get(i), gen);
        }
        gen.writeEndArray();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static <T extends Comparable<? super T>> JaggedList<T> parseJSONFile(String path,
      Function<String, T> parser) throws JsonParseException, IOException {
    var b = new JsonFactoryBuilder().build();
    var p = b.setCodec(new ObjectMapper()).createParser(new File(path)).readValueAsTree();
    var o = new JaggedList<T>();
    parser = Parsers.nullDecorator(Parsers.quoteRemoverDecorator(parser));
    return parseJSONFile(parser, o, p);
  }

  private static <T extends Comparable<? super T>> JaggedList<T> parseJSONFile(
      Function<String, T> parser, JaggedList<T> root, TreeNode p) {
    if (p == null) return root;
    if (p.isArray()) {
      JaggedList<T> arr = new JaggedList<T>(null, root);
      arr.children = new SparseList<>();
      for (int i = 0; i < p.size(); i++) {
        parseJSONFile(parser, arr, p.get(i));
      }
      return arr;
    } else {
      return new JaggedList<T>(parser.apply(p.toString()), root);
    }
  }

  public static <T extends Comparable<? super T>> JaggedList<T> parseJSONArray(String str,
      Function<String, T> parser) {
    return JaggedList.parseJSONArray(str,
        Parsers.nullDecorator(Parsers.quoteRemoverDecorator(parser)), new JaggedList<T>());
  }

  private static <T extends Comparable<? super T>> JaggedList<T> parseJSONArray(String str,
      Function<String, T> parser, JaggedList<T> root) {
    try {
      var b = new JsonFactoryBuilder().build();
      var p = b.setCodec(new ObjectMapper()).createParser(str).readValueAsTree();
      if (p == null) return root;
      if (p.isArray()) {
        JaggedList<T> arr = new JaggedList<T>(null, root);
        arr.children = new SparseList<>();
        for (int i = 0; i < p.size(); i++) {
          parseJSONArray(p.get(i).toString(), parser, arr);
        }
        return arr;
      } else {
        return new JaggedList<T>(parser.apply(str), root);
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

  public String toString(Function<T, String> printer) {
    return this.toJSONArrayString(printer);
  }

  @SuppressWarnings({"unchecked"})
  private static <T extends Comparable<? super T>> JaggedList<T> fromArray(Object object,
      JaggedList<T> parent) {
    if (object != null && object.getClass().isArray()) {
      int l = Array.getLength(object);
      JaggedList<T> t = new JaggedList<T>(null, parent);
      t.children = new SparseList<>();
      for (int i = 0; i < l; i++) {
        var o = Array.get(object, i);
        fromArray(o, t);
      }
      return t;
    } else {
      return new JaggedList<T>((T) object, parent);
    }
  }

  public static <T extends Comparable<? super T>> JaggedList<T> fromArray(Object object) {
    return fromArray(object, new JaggedList<T>());
  }

  private Class<T> getContentClass() {
    var o = searchContentClass();
    return o == null ? getRoot().searchContentClass() : o;
  }

  private Class<T> searchContentClass() {
    @SuppressWarnings("unchecked")
    final Function<T, Class<T>> f = (c) -> c == null ? null : (Class<T>) c.getClass();
    var o = f.apply(this.value);
    if (o != null) {
      return o;
    } else {
      for (var n : this.children) {
        o = n.searchContentClass();
        if (o != null) return o;
      }
    }
    return o;
  }
  
  public Object toArray() {
    if(isValue()) throw new RuntimeException("is not array");
    
    int[] dimensions = new int[getHeight()];
    dimensions[0] = this.size();
    var o = Array.newInstance(getContentClass(), dimensions);
    
    for(int i=0;i<this.size();i++) {
      if(get(i).isValue()) Array.set(o, i, get(i).getValue());
      else Array.set(o, i, get(i).toArray());
    }
    return o;
  }

  public int compareTo(JaggedList<T> other) {
    var o = ordering.compare(this.getValue(), other.getValue());
    return o == 0
        ? IterableComparator.compare(this.children.iterator(), other.children.iterator())
        : o;
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

    JaggedList other = (JaggedList) obj;
    return this.compareTo(other) == 0;
  }

  @Override
  public Iterator<JaggedList<T>> iterator() {
    if (children == null) return null;
    return children.iterator();
  }
}
