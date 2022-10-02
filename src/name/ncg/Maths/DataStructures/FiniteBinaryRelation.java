package name.ncg.Maths.DataStructures;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
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
 * This class attempts to implement a 
 * <a href="https://en.wikipedia.org/wiki/Binary_relation">binary relation</a> as used in a
 * <a href="https://en.wikipedia.org/wiki/Relation_algebra">relation algebra</a>.
 * <br />
 * 
 * See also "Action logic and pure induction" by Vaughan Pratt, 
 * section 3.2 second paragraph for explanations on left and right residual.
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
  public FiniteBinaryRelation(Iterable<X> domain, Iterable<Y> codomain, BiPredicate<X,Y> rel) {
    this(domain, codomain, Relation.fromBiPredicate(rel));
  }
  public FiniteBinaryRelation(Iterable<X> domain, Iterable<Y> codomain, Relation<X,Y> rel) {
    super(Collections.list(new OrderedPairEnumeration<X,Y>(domain, codomain)).stream()
      .filter((p) -> rel.apply(p.getFirst(), p.getSecond())).collect(Collectors.toSet()));
  }
  
  public static <
  X extends Comparable<? super X>,
  Y extends Comparable<? super Y>>  FiniteBinaryRelation<X,Y> universal(Iterable<X> domain,Iterable<Y> codomain) {
    return new FiniteBinaryRelation<X,Y>(domain, codomain, (X a,Y b) -> true);
  }
  
  public FiniteBinaryRelation<X,Y> complement(Iterable<X> domain,Iterable<Y> codomain) {
    var u = universal(domain, codomain);
    if(!u.containsAll(this)) {throw new RuntimeException("the relation exceeds the specified universe.");} 
    return u.minus(this);
  }
  
  public FiniteBinaryRelation<X,Y> implicitComplement() {
    return complement(domain(), codomain());
  }
  
  public boolean add(X a, Y b) {return super.add(OrderedPair.makeOrderedPair(a,b));}
  public boolean remove(X a, Y b) {return super.remove(OrderedPair.makeOrderedPair(a,b));}
  
  public FiniteBinaryRelation<X,Y> intersect(FiniteBinaryRelation<X,Y> S){
    FiniteBinaryRelation<X,Y> o = new FiniteBinaryRelation<X,Y>();
    o.addAll(this); o.retainAll(S);
    return o;
  }
  
  public FiniteBinaryRelation<X,Y> union(FiniteBinaryRelation<X,Y> S){
    FiniteBinaryRelation<X,Y> o = new FiniteBinaryRelation<X,Y>();
    o.addAll(this); o.addAll(S);
    return o;
  }
  
  public FiniteBinaryRelation<X,Y> minus(FiniteBinaryRelation<X,Y> e) {
    var o = new FiniteBinaryRelation<X,Y>(this);
    o.removeAll(e);
    return o;
  }
  /***
   * this • S, the <a href="https://en.wikipedia.org/wiki/Composition_of_relations">composition</a> of this relation with S.
   * 
   * @param <V>
   * @param S
   * @return
   */
  public <V extends Comparable<? super V>>
  FiniteBinaryRelation<X,V> compose(FiniteBinaryRelation<Y,V> S){
     return CollectionUtils.cartesianProduct(this,S).stream().filter(
      (t) -> t.getFirst().getSecond().equals(t.getSecond().getFirst()))
        .map((t) -> OrderedPair.makeOrderedPair(t.getFirst().getFirst(), t.getSecond().getSecond()))
        .collect(Collectors.toCollection(FiniteBinaryRelation<X,V>::new));
  }
  
  /**
   * R˘: the converse of R.
   * 
   * @return R˘
   */
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
   * RHS of R matching RHS of S where u_R_RHS ⊆ u_S_RHS.
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
        TreeSet<X> xSw = S.leftRelata(vw.getSecond());
        TreeSet<X> xRv = leftRelata(vw.getFirst());
        
        return xSw.containsAll(xRv);})
      .collect(Collectors.toCollection(FiniteBinaryRelation<Y,W>::new));
  }
  /**
   * this ← R
   * this / R
   * 
   * S ← R := {(u,v) |∀w[vRw → uSw]}
   * 
   * LHS of S matching LHS of R where LHS_R_w ⊆ LHS_S_w.
   * 
   * @param <V>
   * @param R
   * @return
   */
  public <V extends Comparable<? super V>>
  FiniteBinaryRelation<X,V> leftResidual(FiniteBinaryRelation<V,Y> R){
    return
      CollectionUtils.cartesianProduct(domain(), R.domain()).stream().filter((uv) -> {
        TreeSet<Y> uSy = rightRelata(uv.getFirst()); 
        TreeSet<Y> vRy = R.rightRelata(uv.getSecond());

        return uSy.containsAll(vRy);
      }).collect(Collectors.toCollection(FiniteBinaryRelation<X,V>::new));
  }
  
  public TreeSet<X> domain(){
    return stream().map((p) -> p.getFirst()).collect(
      Collectors.toCollection(TreeSet<X>::new));
  }
  public boolean domainCovers(Collection<X> s) {
    return domain().containsAll(s);
  }
  
  public TreeSet<Y> codomain(){
    return stream().map((p) -> p.getSecond()).collect(
      Collectors.toCollection(TreeSet<Y>::new));
  }
  public boolean codomainCovers(Collection<Y> s) {
    return codomain().containsAll(s);
  }
  public BiPredicate<X,Y> related(){
    return (t,u) -> contains(OrderedPair.makeOrderedPair(t, u));
  }
  
  public Predicate<Y> rightRelated(X e){
    return Functional.bindFirst(related(), e);
  }
  
  public TreeSet<Y> rightRelata(X e) {
    return this.stream().filter(
      (p) -> rightRelated(e).test(p.getSecond()))
        .map((p) -> p.getSecond()).distinct()
        .collect(Collectors.toCollection(TreeSet<Y>::new));
  }
  
  public Predicate<X> leftRelated(Y e){
    return Functional.bindSecond(related(), e);
  }
  
  public TreeSet<X> leftRelata(Y e) {
    return this.stream().filter(
      (p) -> leftRelated(e).test(p.getFirst()))
        .map((p) -> p.getFirst()).distinct()
        .collect(Collectors.toCollection(TreeSet<X>::new));
  }
  
  public boolean isLeftUnique() {
    return HomogeneousFiniteBinaryRelation.identity(domain()).containsAll(this.compose(this.converse()));
  }
  
  public boolean isLeftTotal(Iterable<X> domain) { return converse().isSurjective(domain); }
  public boolean isRightUnique() {
    return HomogeneousFiniteBinaryRelation.identity(codomain()).containsAll(this.converse().compose(this));
  }
  public boolean isSurjective(Iterable<Y> codomain) {
    return this.converse().compose(this).containsAll(HomogeneousFiniteBinaryRelation.identity(codomain));
  }
  public boolean isFunction(Iterable<X> domain) {return isRightUnique() && isLeftTotal(domain);}
  
  public boolean isManyToMany() {return !isLeftUnique() && !isRightUnique(); }
  public boolean isManyToOne() { return !isLeftUnique() && isRightUnique();}
  public boolean isOneToMany() { return isLeftUnique() && !isRightUnique(); }
  public boolean isOneToOne() { return isLeftUnique() && !isRightUnique(); }
  
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
