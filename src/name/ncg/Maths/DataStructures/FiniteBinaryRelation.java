package name.ncg.Maths.DataStructures;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.Iterators;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import name.ncg.CS.Functional;
import name.ncg.Maths.Enumerations.HeterogeneousPairEnumeration;
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
  Y extends Comparable<? super Y>> implements Iterable<HeterogeneousPair<X,Y>>{
  
  protected TreeSet<HeterogeneousPair<X,Y>> pairs = new TreeSet<>();
  protected TreeSet<HeterogeneousPair<Y,X>> pairsReversed = new TreeSet<>();
  public FiniteBinaryRelation() {
  }
  
  public FiniteBinaryRelation(FiniteBinaryRelation<X,Y> rel) {
    pairs.clear(); pairs.addAll(rel.pairs);
    pairsReversed.clear(); pairsReversed.addAll(rel.pairsReversed);
  }
  public FiniteBinaryRelation(Iterable<X> domain, Iterable<Y> codomain, BiPredicate<X,Y> rel) {
    this(domain, codomain, Relation.fromBiPredicate(rel));
  }
  public FiniteBinaryRelation(Iterable<X> domain, Iterable<Y> codomain, Relation<X,Y> rel) {
    Collections.list(new HeterogeneousPairEnumeration<X,Y>(domain, codomain)).stream()
    .filter((p) -> rel.apply(p.getFirst(), p.getSecond())).forEach((p) -> add(p.getFirst(), p.getSecond()));
  }
  
  public static <
  X extends Comparable<? super X>,
  Y extends Comparable<? super Y>>  FiniteBinaryRelation<X,Y> universal(Iterable<X> domain,Iterable<Y> codomain) {
    return new FiniteBinaryRelation<X,Y>(domain, codomain, (X a,Y b) -> true);
  }
  
  public FiniteBinaryRelation<X,Y> complement(Iterable<X> domain,Iterable<Y> codomain) {
    var u = universal(domain, codomain);
    if(!u.pairs.containsAll(this.pairs)) {throw new RuntimeException("the relation exceeds the specified universe.");} 
    return u.minus(this);
  }
  
  public FiniteBinaryRelation<X,Y> complement() {
    return complement(domain(), codomain());
  }
  
  public int size() { return pairs.size(); }
  public boolean isEmpty() { return pairs.isEmpty(); }
  public boolean contains(HeterogeneousPair<X,Y> p) { return this.pairs.contains(p); }
  public boolean add(X a, Y b) {
    var p = HeterogeneousPair.makeOrderedPair(a,b);
    boolean o = pairs.add(p);
    if(o) pairsReversed.add(p.converse());
    return o;
  }
  
  public boolean remove(X a, Y b) {
    var p = HeterogeneousPair.makeOrderedPair(a,b);
    boolean o = pairs.remove(p);
    if(o) pairsReversed.remove(p.converse());
    return o;
  }
  
  public boolean containsAll(FiniteBinaryRelation<X,Y> r) { return pairs.containsAll(r.pairs); }
  
  public FiniteBinaryRelation<X,Y> intersect(FiniteBinaryRelation<X,Y> S){
    FiniteBinaryRelation<X,Y> o = new FiniteBinaryRelation<X,Y>();
    o.pairs.addAll(this.pairs);
    o.pairsReversed.addAll(this.pairsReversed);
    o.pairs.retainAll(S.pairs);
    o.pairsReversed.retainAll(S.pairsReversed);
    return o;
  }
  
  public FiniteBinaryRelation<X,Y> union(FiniteBinaryRelation<X,Y> S){
    FiniteBinaryRelation<X,Y> o = new FiniteBinaryRelation<X,Y>();
    o.pairs.addAll(this.pairs);
    o.pairsReversed.addAll(this.pairsReversed);
    o.pairs.addAll(S.pairs);
    o.pairsReversed.addAll(S.pairsReversed);
    return o;
  }
  
  public FiniteBinaryRelation<X,Y> minus(FiniteBinaryRelation<X,Y> e) {
    var o = new FiniteBinaryRelation<X,Y>(this);
    o.pairs.removeAll(e.pairs);
    o.pairsReversed.removeAll(e.pairsReversed);
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
    FiniteBinaryRelation<X,V> o = new FiniteBinaryRelation<>();
    CollectionUtils.cartesianProduct(this.pairs,S.pairs).stream().filter(
      (t) -> t.getFirst().getSecond().equals(t.getSecond().getFirst()))
        .map((t) -> HeterogeneousPair.makeOrderedPair(t.getFirst().getFirst(), t.getSecond().getSecond()))
        .forEach((p) -> o.add(p.getFirst(), p.getSecond()));
    return o;
  }
  
  /**
   * R˘: the converse of R.
   * 
   * @return R˘
   */
  public FiniteBinaryRelation<Y,X> converse(){
    var o = new FiniteBinaryRelation<Y,X>();
    o.pairs.addAll(pairsReversed);
    o.pairsReversed.addAll(pairs);
    return o;
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
    var o = new FiniteBinaryRelation<Y,W>();
    
    CollectionUtils.cartesianProduct(codomain(), S.codomain()).stream()
      .filter((vw) -> {
        TreeSet<X> xSw = S.leftRelata(vw.getSecond());
        TreeSet<X> xRv = leftRelata(vw.getFirst());
        
        return xSw.containsAll(xRv);})
      .forEach((p) -> o.add(p.getFirst(),p.getSecond()));
    return o;
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
    var o = new FiniteBinaryRelation<X,V>();
    CollectionUtils.cartesianProduct(domain(), R.domain()).stream().filter((uv) -> {
        TreeSet<Y> uSy = rightRelata(uv.getFirst()); 
        TreeSet<Y> vRy = R.rightRelata(uv.getSecond());

        return uSy.containsAll(vRy);
      }).forEach((p) -> o.add(p.getFirst(), p.getSecond()));
    return o;
  }
  
  public TreeSet<X> domain(){
    return pairs.stream().map((p) -> p.getFirst()).collect(
      Collectors.toCollection(TreeSet<X>::new));
  }
  public boolean domainCovers(Collection<X> s) {
    return domain().containsAll(s);
  }
  
  public TreeSet<Y> codomain(){
    return pairs.stream().map((p) -> p.getSecond()).collect(
      Collectors.toCollection(TreeSet<Y>::new));
  }
  public boolean codomainCovers(Collection<Y> s) {
    return codomain().containsAll(s);
  }
  public BiPredicate<X,Y> related(){
    return (t,u) -> pairs.contains(HeterogeneousPair.makeOrderedPair(t, u));
  }
  
  public Predicate<Y> rightRelated(X e){
    return Functional.bindFirst(related(), e);
  }
  
  public TreeSet<Y> rightRelata(X e) {
    var o = new TreeSet<Y>();
    HeterogeneousPair<X,Y> pivot = this.pairs.higher(HeterogeneousPair.makeOrderedPair(e, null));
    if(pivot == null) return o;
    
    for(var p : this.pairs.tailSet(pivot)) {
      if(!p.getFirst().equals(e)) break;
      o.add(p.getSecond());
    }
    return o;
  }
  
  public Predicate<X> leftRelated(Y e){
    return Functional.bindSecond(related(), e);
  }
  
  public TreeSet<X> leftRelata(Y e) {
    var o = new TreeSet<X>();
    HeterogeneousPair<Y,X> pivot = this.pairsReversed.higher(HeterogeneousPair.makeOrderedPair(e, null));
    if(pivot == null) return o;
    
    for(var p : this.pairsReversed.tailSet(pivot)) {
      if(!p.getFirst().equals(e)) break;
      o.add(p.getSecond());
    }
    return o;
  }
  
  public boolean isLeftUnique() {
    return HomogeneousFiniteBinaryRelation.identity(domain()).containsAll(this.compose(this.converse()));
  }
  /***
   * Alias for isLeftUnique
   * @return
   */
  public boolean isInjective() { return isLeftUnique();}
  public boolean isLeftTotal(Iterable<X> domain) { return converse().isSurjective(domain); }
  public boolean isRightUnique() {
    return HomogeneousFiniteBinaryRelation.identity(codomain()).containsAll(this.converse().compose(this));
  }
  /***
   * Alias for isRightUnique
   * @return
   */
  public boolean isFunctional() {return isRightUnique();}
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
    
    w.writeAll(this.pairs.stream().map((t) -> {
      String[] arr = new String[2];
      arr[0] = xToString.apply(t.getFirst());
      arr[1] = yToString.apply(t.getSecond());
      return arr;
    }).collect(Collectors.toList()));
    
    w.close();
    p.close();
  }
  
  public static <
  X extends Comparable<? super X>,
  Y extends Comparable<? super Y>> FiniteBinaryRelation<X,Y> readFromCSV (
    Function<String, X> xParser, 
    Function<String,Y> yParser, InputStream is)  throws IOException, CsvException {
    var st = new InputStreamReader(is);
    var o = readFromCSV(xParser, yParser, new BufferedReader(st));
    st.close();
    return o;
  }
  
  public static <
  X extends Comparable<? super X>,
  Y extends Comparable<? super Y>> FiniteBinaryRelation<X,Y> readFromCSV (
    Function<String, X> xParser, 
    Function<String,Y> yParser, Reader reader)  throws IOException, CsvException {
    CSVParserBuilder b = new CSVParserBuilder();
    
    CSVParser p = b.withSeparator(',').withQuoteChar('"').withEscapeChar('\\').build();
    CSVReaderBuilder rb = new CSVReaderBuilder(reader);
    CSVReader r = rb.withCSVParser(p).build();
    var o = new FiniteBinaryRelation<X,Y>();
    
    r.readAll().stream()
      .forEach((s) -> {
        o.add(xParser.apply(s[0]), yParser.apply(s[1]));
      });
    r.close();
    reader.close();
    return o;
  }
  
  public static <
  X extends Comparable<? super X>,
  Y extends Comparable<? super Y>> FiniteBinaryRelation<X,Y> readFromCSV (
    Function<String, X> xParser, 
    Function<String,Y> yParser, String path)  throws IOException, CsvException {
    return readFromCSV(xParser, yParser, new FileReader(path));
  }

  @Override
  public Iterator<HeterogeneousPair<X, Y>> iterator() {
    return Iterators.unmodifiableIterator(this.pairs.iterator());
  }
}
