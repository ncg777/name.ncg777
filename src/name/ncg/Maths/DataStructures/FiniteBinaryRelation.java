package name.ncg.Maths.DataStructures;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import name.ncg.CS.Functional;
import name.ncg.Maths.Enumerations.OrderedPairEnumeration;
import name.ncg.Maths.Relations.Relation;

/**
 * This class attempts to implement a binary relation as defined in 
 * relation algebra.
 * 
 * See "Action logic and pure induction" by Vaughan Pratt, 
 * section 3.2 second paragraph.
 * 
 * @author Nicolas Couture-Grenier
 *
 * @param <X>
 * @param <Y>
 */
@SuppressWarnings("serial")
public class FiniteBinaryRelation<
  X extends Comparable<? super X>,
  Y extends Comparable<? super Y>> 
  extends TreeSet<OrderedPair<X,Y>> {
  
  public FiniteBinaryRelation() {
    super();
  }
  
  public FiniteBinaryRelation(FiniteBinaryRelation<X,Y> rel) {
    super(rel);
  }
  
  public FiniteBinaryRelation(Set<X> domain, Set<Y> codomain, Relation<X,Y> rel) {
    super(Collections.list(new OrderedPairEnumeration<X,Y>(domain, codomain)).stream()
      .filter((p) -> rel.apply(p.getFirst(), p.getSecond())).collect(Collectors.toSet()));
  }
  
  public boolean add(X a, Y b) {return super.add(OrderedPair.makeOrderedPair(a,b));}
  public boolean remove(X a, Y b) {return super.remove(OrderedPair.makeOrderedPair(a,b));}
  
  public FiniteBinaryRelation<X,Y> intersect(FiniteBinaryRelation<X,Y> e){
    FiniteBinaryRelation<X,Y> o = new FiniteBinaryRelation<X,Y>();
    o.addAll(this); o.retainAll(e);
    return o;
  }
  
  public FiniteBinaryRelation<X,Y> union(FiniteBinaryRelation<X,Y> e){
    FiniteBinaryRelation<X,Y> o = new FiniteBinaryRelation<X,Y>();
    o.addAll(this); o.addAll(e);
    return o;
  }
  
  
  public <V extends Comparable<? super V>>
  FiniteBinaryRelation<X,V> compose(FiniteBinaryRelation<Y,V> e){
    TreeSet<OrderedPair<OrderedPair<X,Y>,OrderedPair<Y,V>>> cartesian = 
        new TreeSet<OrderedPair<OrderedPair<X,Y>,OrderedPair<Y,V>>>();
    for(OrderedPair<X,Y> x : this){
      for(OrderedPair<Y,V> y : e){
        cartesian.add(OrderedPair.makeOrderedPair(x, y));
      }
    }
    
    return cartesian.stream().filter(
      (t) -> t.getFirst().getSecond().equals(t.getSecond().getFirst()))
        .map((t) -> OrderedPair.makeOrderedPair(t.getFirst().getFirst(), t.getSecond().getSecond()))
        .collect(Collectors.toCollection(FiniteBinaryRelation<X,V>::new));
  }
  
  public FiniteBinaryRelation<Y,X> converse(){
    return stream()
        .map((t) -> OrderedPair.makeOrderedPair(t.getSecond(), t.getFirst()))
        .collect(Collectors.toCollection(FiniteBinaryRelation<Y,X>::new));
    }
  

  /**
   * this → S 
   * this \ S
   * 
   * R → S := {(v,w) |∀u[uRv → uSw]}
   * 
   * RHS of R matching RHS of S where u_R_RHS ⊇ u_S_RHS.
   * 
   * @param <W>
   * @param S
   * @return
   */
  public <W extends Comparable<? super W>>
  FiniteBinaryRelation<Y,W> rightResidual(FiniteBinaryRelation<X,W> S){
    return
      CollectionUtils.cartesianProduct(codomain(), S.codomain()).stream()
      .filter((vw) -> {
        TreeSet<X> xSw = S.domain().stream().filter(S.leftRelated(vw.getSecond())).collect(
          Collectors.toCollection(TreeSet<X>::new));
        TreeSet<X> xRv = domain().stream().filter(leftRelated(vw.getFirst())).collect(
          Collectors.toCollection(TreeSet<X>::new));
        // xRv ⊇ xSw
        return xRv.containsAll(xSw);})
      .collect(Collectors.toCollection(FiniteBinaryRelation<Y,W>::new));
  }
  /**
   * this ← R
   * this / R
   * 
   * S ← R := {(u,v) |∀w[vRw → uSw]}
   * 
   * LHS of S matching LHS of R where LHS_R_w ⊇ LHS_S_w.
   * 
   * @param <V>
   * @param R
   * @return
   */
  public <V extends Comparable<? super V>>
  FiniteBinaryRelation<X,V> leftResidual(FiniteBinaryRelation<V,Y> R){
    return
      CollectionUtils.cartesianProduct(domain(), R.domain()).stream().filter((uv) -> {
        TreeSet<Y> uSy = codomain().stream().filter(rightRelated(uv.getFirst())).collect(
          Collectors.toCollection(TreeSet<Y>::new));
        TreeSet<Y> vRy = R.codomain().stream().filter(R.rightRelated(uv.getSecond())).collect(
          Collectors.toCollection(TreeSet<Y>::new));
        // vRy ⊇ uSy
        return vRy.containsAll(uSy);
      }).collect(Collectors.toCollection(FiniteBinaryRelation<X,V>::new));
  }
  
  public TreeSet<X> domain(){
    return stream().map((p) -> p.getFirst()).collect(
      Collectors.toCollection(TreeSet<X>::new));
  }
  
  public TreeSet<Y> codomain(){
    return stream().map((p) -> p.getSecond()).collect(
      Collectors.toCollection(TreeSet<Y>::new));
  }

  public BiPredicate<X,Y> related(){
    return (t,u) -> contains(OrderedPair.makeOrderedPair(t, u));
  }
  
  public Predicate<Y> rightRelated(X e){
    return Functional.bindFirst(related(), e);
  }
  
  public List<Y> rightRelata(X e) {
    return this.stream().filter(
      (p) -> rightRelated(e).test(p.getSecond()))
        .map((p) -> p.getSecond()).distinct()
        .collect(Collectors.toList());
  }
  
  public Predicate<X> leftRelated(Y e){
    return Functional.bindSecond(related(), e);
  }
  
  public List<X> leftRelata(Y e) {
    return this.stream().filter(
      (p) -> leftRelated(e).test(p.getFirst()))
        .map((p) -> p.getFirst()).distinct()
        .collect(Collectors.toList());
  }
  
  public void writeToCSV(Function<X,String> xToString, Function<Y,String> yToString, String path) throws IOException {
    PrintWriter p = new PrintWriter(path);
    CSVWriter w = new CSVWriter(p,',', '"','\\', "\n");
    
    w.writeAll(this.stream().map((t) -> {
      String[] arr = new String[2];
      arr[0] = xToString.apply(t.getFirst());
      arr[1] = yToString.apply(t.getSecond());
      return arr;
    }).collect(Collectors.toList()));
    
    w.close();
  }
  
  public static <
  X extends Comparable<? super X>,
  Y extends Comparable<? super Y>> FiniteBinaryRelation<X,Y> readFromCSV (
    Function<String, X> xParser, 
    Function<String,Y> yParser, String path)  throws IOException, CsvException {
    FileReader f = new FileReader(path);
    CSVParserBuilder b = new CSVParserBuilder();
    
    CSVParser p = b.withSeparator(',').withQuoteChar('"').withEscapeChar('\\').build();
    CSVReaderBuilder rb = new CSVReaderBuilder(f);
    CSVReader r = rb.withCSVParser(p).build();
    FiniteBinaryRelation<X,Y>  o =
      r.readAll().stream()
      .map((s) -> {
        return OrderedPair.makeOrderedPair(xParser.apply(s[0]), yParser.apply(s[1]));
      })
      .collect(Collectors.toCollection(FiniteBinaryRelation<X,Y>::new));
    r.close();
    return o;
  }
}
 
