package name.NicolasCoutureGrenier.Maths.DataStructures;

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
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections4.set.UnmodifiableSortedSet;

import com.google.common.collect.Iterators;
import com.google.common.collect.Ordering;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import name.NicolasCoutureGrenier.CS.Functional;
import name.NicolasCoutureGrenier.CS.Parsers;
import name.NicolasCoutureGrenier.CS.Printers;
import name.NicolasCoutureGrenier.Maths.Enumerations.HeterogeneousPairEnumeration;
import name.NicolasCoutureGrenier.Maths.Relations.Relation;

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
public class FiniteBinaryRelation<
  X extends Comparable<? super X>,
  Y extends Comparable<? super Y>> implements Iterable<HeterogeneousPair<X,Y>>, Relation<X,Y>, Comparable<FiniteBinaryRelation<X,Y>> {
  
  protected TreeSet<HeterogeneousPair<X,Y>> pairs = new TreeSet<>(Ordering.natural().nullsFirst());
  protected TreeSet<HeterogeneousPair<Y,X>> pairsReversed = new TreeSet<>(Ordering.natural().nullsFirst());
  protected TreeSet<X> domain = new TreeSet<X>(Ordering.natural().nullsFirst());
  protected TreeSet<Y> codomain = new TreeSet<Y>(Ordering.natural().nullsFirst());
  
  public FiniteBinaryRelation() {
  }
  public FiniteBinaryRelation(Map<X,Y> map) {
    for(var e : map.entrySet()) {
      add(e.getKey(),e.getValue());
    }
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
  public FiniteBinaryRelation(Iterable<X> domain, Function<X,Y> f) {
    for(var x : domain) {
      add(x, f.apply(x));
    }
  }
  @Override
  public boolean equals(Object other) {
    if(!(other instanceof FiniteBinaryRelation)){ return false;}
    @SuppressWarnings("unchecked")
    FiniteBinaryRelation<X,Y> t = (FiniteBinaryRelation<X,Y>)other;
    return this.size() == t.size() && t.pairs.stream().allMatch((p) -> this.contains(p));
  }
  
  public boolean equals(FiniteBinaryRelation<X,Y> other) {
    return this.size() == other.size() && other.pairs.stream().allMatch((p) -> this.contains(p));
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
    var f_a = this.pairs.ceiling(HeterogeneousPair.makeHeterogeneousPair(a, null));
    var a0=a==null?null:(f_a != null && f_a.getFirst().equals(a) ? f_a.getFirst() : a);
    
    var f_b = this.pairsReversed.ceiling(HeterogeneousPair.makeHeterogeneousPair(b, null));
    var b0=b==null?null:(f_b!=null && f_b.getFirst().equals(b) ? f_b.getFirst() : b);
    
    var p = HeterogeneousPair.makeHeterogeneousPair(a0,b0);
    boolean o = pairs.add(p);
    if(o) pairsReversed.add(p.converse());
    this.domain.add(a0);
    this.codomain.add(b0);
    return o;
  }
  
  public boolean remove(X a, Y b) {
    var p = HeterogeneousPair.makeHeterogeneousPair(a,b);
    boolean o = pairs.remove(p);
    if(o) pairsReversed.remove(p.converse());
    var f_a = this.pairs.ceiling(HeterogeneousPair.makeHeterogeneousPair(a, null));
    if(a==null||f_a==null || !f_a.getFirst().equals(a)) this.domain.remove(a); 
    var f_b = this.pairsReversed.ceiling(HeterogeneousPair.makeHeterogeneousPair(b, null));
    if(b==null||f_b==null||!f_b.getFirst().equals(b)) this.codomain.remove(b);
    return o;
  }
  
  public void spawnRight(Function<X,Iterable<Y>> multiplier, SortedSet<X> domain) {
    for(var x : domain) {
      for(var s : multiplier.apply(x)) add(x,s);
    }
  }
  public void spawnRight(Function<X,Iterable<Y>> multiplier) { spawnRight(multiplier,domain());  }
  
  public void spawnLeft(Function<Y,Iterable<X>> multiplier, SortedSet<Y> codomain) {
    for(var y : codomain) {
      for(var s : multiplier.apply(y)) add(s,y);
    }
  }
  public void spawnLeft(Function<Y,Iterable<X>> multiplier) { spawnLeft(multiplier,codomain());  }
  public boolean containsAll(FiniteBinaryRelation<X,Y> r) { return pairs.containsAll(r.pairs); }
  
  public FiniteBinaryRelation<X,Y> intersect(FiniteBinaryRelation<X,Y> S){
    FiniteBinaryRelation<X,Y> o = new FiniteBinaryRelation<X,Y>();
    for(var p : this) {
      if(S.apply(p.getFirst(), p.getSecond())) o.add(p.getFirst(), p.getSecond());
    }
    return o;
  }
  
  public FiniteBinaryRelation<X,Y> union(FiniteBinaryRelation<X,Y> S){
    FiniteBinaryRelation<X,Y> o = new FiniteBinaryRelation<X,Y>();
    for(var p : this) o.add(p.getFirst(), p.getSecond());
    for(var p : S) o.add(p.getFirst(), p.getSecond());
    return o;
  }
  
  public FiniteBinaryRelation<X,Y> minus(FiniteBinaryRelation<X,Y> e) {
    var o = new FiniteBinaryRelation<X,Y>(this);
    for(var p : this) o.add(p.getFirst(), p.getSecond());
    for(var p : e) o.remove(p.getFirst(), p.getSecond());
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
    var o = new FiniteBinaryRelation<X,V>();
    CollectionUtils.cartesianProduct(domain(), S.codomain()).stream()
      .filter((xv) -> {
        TreeSet<Y> common = rightRelata(xv.getFirst());
        common.retainAll(S.leftRelata(xv.getSecond()));
        return common.size() > 0;})
      .forEach((p) -> o.add(p.getFirst(),p.getSecond()));
    return o;
  }
  
  /**
   * R˘: the converse of R.
   * 
   * @return R˘
   */
  public FiniteBinaryRelation<Y,X> converse(){
    var o = new FiniteBinaryRelation<Y,X>();
    for(var p : this) o.add(p.getSecond(), p.getFirst());
    return o;
    }

  /**
   * this → S <br />
   * this \ S<br />
   * <br />
   * R → S := {(v,w) |∀u[uRv → uSw]}<br />
   * <br />
   * RHS of R matching RHS of S where u_R_RHS ⊆ u_S_RHS.<br />
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
   * this ← R<br />
   * this / R<br />
   * <br />
   * S ← R := {(u,v) |∀w[vRw → uSw]}<br />
   * <br />
   * LHS of S matching LHS of R where LHS_R_w ⊆ LHS_S_w.<br />
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
  
  public SortedSet<X> domain(){
    return UnmodifiableSortedSet.unmodifiableSortedSet(this.domain);
  }
  public boolean domainCovers(Collection<X> s) {
    return this.domain.containsAll(s);
  }
  
  public SortedSet<Y> codomain(){
    return UnmodifiableSortedSet.unmodifiableSortedSet(this.codomain);
  }
  public boolean codomainCovers(Collection<Y> s) {
    return this.codomain.containsAll(s);
  }
  public BiPredicate<X,Y> related(){
    return (t,u) -> pairs.contains(HeterogeneousPair.makeHeterogeneousPair(t, u));
  }
  
  public Predicate<Y> rightRelated(X e){
    return Functional.bindFirst(related(), e);
  }
  
  public TreeSet<Y> rightRelata(X e) {
    var o = new TreeSet<Y>(Ordering.natural().nullsFirst());
    HeterogeneousPair<X,Y> pivot = this.pairs.ceiling(HeterogeneousPair.makeHeterogeneousPair(e, null));
    if(pivot == null) return o;
    
    for(var p : this.pairs.tailSet(pivot)) {
      if(!((e==null && p.getFirst() == null) || (e != null && e.equals(p.getFirst())))) break;
      o.add(p.getSecond());
    }
    return o;
  }
  
  public Predicate<X> leftRelated(Y e){
    return Functional.bindSecond(related(), e);
  }
  
  public TreeSet<X> leftRelata(Y e) {
    var o = new TreeSet<X>(Ordering.natural().nullsFirst());
    HeterogeneousPair<Y,X> pivot = this.pairsReversed.ceiling(HeterogeneousPair.makeHeterogeneousPair(e, null));
    if(pivot == null) return o;
    
    for(var p : this.pairsReversed.tailSet(pivot)) {
      if(!((e==null && p.getFirst() == null) || (e != null && e.equals(p.getFirst())))) break;
      o.add(p.getSecond());
    }
    return o;
  }
  
  public boolean isLeftUnique(Iterable<Y> codomain) {
    return !this.codomain.stream().anyMatch((y) -> this.leftRelata(y).size() > 1);
  }
  public boolean isLeftUnique() {return isLeftUnique(codomain());}
  /***
   * Alias for isLeftUnique
   * @return
   */
  public boolean isInjective() { return isLeftUnique();}
  public boolean isLeftTotal(Iterable<X> domain) { 
    for(var x : domain) if(this.rightRelata(x).size() == 0) return false;
    return true;
  }
  public boolean isRightUnique(Iterable<X> domain) {
    return !this.domain.stream().anyMatch((x) -> this.rightRelata(x).size() > 1);
  }
  public boolean isRightUnique() {return isRightUnique(domain());}
  /***
   * Alias for isRightUnique
   * @return
   */
  public boolean isFunctional() {return isRightUnique(domain());}
  public boolean isSurjective(Iterable<Y> codomain) {
    for(var y : codomain) if(this.leftRelata(y).size() == 0) return false;
    return true;
  }
  public boolean isFunctional(Iterable<X> domain) {return isRightUnique(domain) && isLeftTotal(domain);}
  
  public boolean isManyToMany() {return !isLeftUnique() && !isRightUnique(); }
  public boolean isManyToOne() { return !isLeftUnique() && isRightUnique();}
  public boolean isOneToMany() { return isLeftUnique() && !isRightUnique(); }
  public boolean isOneToOne() { return isLeftUnique() && isRightUnique(); }
  
  public static <
    X extends Comparable<? super X>,
    Y extends Comparable<? super Y>> void writeToCSV(
        FiniteBinaryRelation<X,Y> rel,
        Function<X,String> xToString, 
        Function<Y,String> yToString, 
        String path, boolean useBase64) throws IOException {
    PrintWriter p = new PrintWriter(path);
    CSVWriter w = new CSVWriter(p,',', '"','\\', "\n");
    if(useBase64) { 
      xToString = Printers.base64Decorator(xToString);
      yToString = Printers.base64Decorator(yToString);
    }
    final var fxp = Printers.nullDecorator(xToString);
    final var fyp = Printers.nullDecorator(yToString);
    
    w.writeAll(rel.pairs.stream().map((t) -> {
      String[] arr = new String[2];
      arr[0] = fxp.apply(t.getFirst());
      arr[1] = fyp.apply(t.getSecond());
      return arr;
    }).collect(Collectors.toList()));
    
    w.close();
    p.close();
  }
  public void writeToCSV(Function<X,String> xToString, Function<Y,String> yToString, String path, boolean useBase64) throws IOException {
    writeToCSV(this,xToString,yToString,path,useBase64);
  }
  public void writeToCSV(Function<X,String> xToString, Function<Y,String> yToString, String path) throws IOException {
    writeToCSV(this,xToString,yToString,path,false);
  }
  
  public static <
  X extends Comparable<? super X>,
  Y extends Comparable<? super Y>> FiniteBinaryRelation<X,Y> readFromCSV (
    Function<String, X> xParser, 
    Function<String,Y> yParser, InputStream is) throws IOException, CsvException {
    return readFromCSV(xParser,yParser,is,false);
  }
  public static <
  X extends Comparable<? super X>,
  Y extends Comparable<? super Y>> FiniteBinaryRelation<X,Y> readFromCSV (
    Function<String, X> xParser, 
    Function<String,Y> yParser, InputStream is, boolean useBase64) throws IOException, CsvException {
    var st = new InputStreamReader(is);
    var o = readFromCSV(xParser, yParser, new BufferedReader(st), useBase64);
    st.close();
    return o;
  }
  
  public static <
  X extends Comparable<? super X>,
  Y extends Comparable<? super Y>> FiniteBinaryRelation<X,Y> readFromCSV (
    Function<String, X> xParser, 
    Function<String,Y> yParser, Reader reader) throws IOException, CsvException {
    return readFromCSV(xParser,yParser,reader,false);
  }
  public static <
  X extends Comparable<? super X>,
  Y extends Comparable<? super Y>> FiniteBinaryRelation<X,Y> readFromCSV (
    Function<String, X> xParser, 
    Function<String,Y> yParser, Reader reader, boolean useBase64) throws IOException, CsvException {
    CSVParserBuilder b = new CSVParserBuilder();
    
    CSVParser p = b.withSeparator(',').withQuoteChar('"').withEscapeChar('\\').build();
    CSVReaderBuilder rb = new CSVReaderBuilder(reader);
    CSVReader r = rb.withCSVParser(p).build();
    var o = new FiniteBinaryRelation<X,Y>();
    if(useBase64) { 
      xParser = Parsers.base64Decorator(xParser);
      yParser = Parsers.base64Decorator(yParser);
    }
    final var fxp = Parsers.nullDecorator(xParser);
    final var fyp = Parsers.nullDecorator(yParser);
    
    r.readAll().stream()
      .forEach((s) -> {
        o.add(fxp.apply(s[0]), fyp.apply(s[1]));
      });
    r.close();
    reader.close();
    return o;
  }
  
  public static <
  X extends Comparable<? super X>,
  Y extends Comparable<? super Y>> FiniteBinaryRelation<X,Y> readFromCSV (
    Function<String, X> xParser, 
    Function<String,Y> yParser, String path, boolean useBase64)  throws IOException, CsvException {
    return readFromCSV(xParser, yParser, new FileReader(path), useBase64);
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
  
  @Override
  public String toString() {
    return pairs.toString();
  }
  
  @Override
  public boolean apply(X a, Y b) {
    return pairs.contains(HeterogeneousPair.makeHeterogeneousPair(a, b));
  }
  @Override
  public int compareTo(FiniteBinaryRelation<X, Y> o) {
    var it = new IterableComparator<HeterogeneousPair<X,Y>>();
    
    return it.compare(this, o);
  }
 
  public static class ArrayRelationX <
    X extends Comparable<? super X>,
    Y extends Comparable<? super Y>
  > extends FiniteBinaryRelation<Tuple<X>,Y> {
    public boolean add(X[] x, Y y) {
      return super.add(Tuple.create(x), y);
    }
    
    public boolean remove(X[] a, Y b) {
      return super.remove(Tuple.create(a), b);
    }
    
    public Predicate<Y> rightRelated(X[] e) {
      return super.rightRelated(Tuple.create(e));
    }
    
    public TreeSet<Y> rightRelata(X[] e) {
      return super.rightRelata(Tuple.create(e));
    }
    
    public static <
      R extends Comparable<? super R>,
      S extends Comparable<? super S>
    > void writeToCSV(ArrayRelationX<R,S> rel,
        Function<R, String> xToString,
        Function<S, String> yToString,
        String path, boolean useBase64) throws IOException {
      rel.writeToCSV(Printers.tupleDecorator(xToString), yToString, path, useBase64);
    }
    
    public boolean apply(X[] a, Y b) {
      return super.apply(Tuple.create(a), b);
    }
  }

  public static <
    X extends Comparable<? super X>,
    Y extends Comparable<? super Y>
  > ArrayRelationX<X,Y> arrayRelationX() {
    return new ArrayRelationX<X,Y>();
  }
  
  public static <
    X extends Comparable<? super X>,
    Y extends Comparable<? super Y>
  > ArrayRelationX<X,Y> arrayRelationX(Map<X[],Y> map) {
    ArrayRelationX<X,Y> r = arrayRelationX();
    
    for(var e : map.entrySet()) {
      r.add(e.getKey(),e.getValue());
    }
    return r;
  }
  
  public static <
    X extends Comparable<? super X>,
    Y extends Comparable<? super Y>
  >  ArrayRelationX<X,Y> arrayRelationX(Iterable<X[]> domain, Iterable<Y> codomain, BiPredicate<X[],Y> rel) {
    ArrayRelationX<X,Y> r = arrayRelationX();
    TreeSet<Tuple<X>> d = new TreeSet<Tuple<X>>();
    for(var x : domain) d.add(Tuple.create(x));
    TreeSet<Y> c = new TreeSet<Y>();
    for(var y : codomain) c.add(y);
    
    for(var p : Collections.list(new HeterogeneousPairEnumeration<Tuple<X>,Y>(d,c))) {
      @SuppressWarnings("unchecked")
      var t = (X[])p.getFirst().toArray();
      if(rel.test(t, p.getSecond())) {
        r.add(p.getFirst(),p.getSecond());
      }
    }
    
    return r;
  }
  
  public static <
    X extends Comparable<? super X>,
    Y extends Comparable<? super Y>
  > ArrayRelationX<X,Y> arrayRelationX(Iterable<X[]> domain, Function<X[],Y> f) {
    var r = new ArrayRelationX<X,Y>();
    for(var x : domain) {
      r.add(x, f.apply(x));
    }
    return r;
  }
  
  public static class ArrayRelationY <
    X extends Comparable<? super X>,
    Y extends Comparable<? super Y>
  > extends ArrayRelationX<Y,X> {
    public boolean add(Y[] t, X x) {
      return super.add(t, x);
    }
    
    public boolean remove(Y[] a, X b) {
      return super.remove(Tuple.create(a), b);
    }

    public Predicate<X> rightRelated(Y[] e) {
      return super.rightRelated(Tuple.create(e));
    }

    public TreeSet<X> rightRelata(Y[] e) {
      return super.rightRelata(Tuple.create(e));
    }
    
    public static <
      R extends Comparable<? super R>,
      S extends Comparable<? super S>
    > void writeToCSV(ArrayRelationY<R,S> rel,
        Function<R, String> xToString,
        Function<S, String> yToString,
        String path, boolean useBase64) throws IOException {
      ArrayRelationX.writeToCSV(rel,yToString, xToString, path, useBase64);
    }
    
    public boolean apply(Y[] a, X b) {
      return super.apply(Tuple.create(a), b);
    }
  }

  public static <
    X extends Comparable<? super X>,
    Y extends Comparable<? super Y>
  > FiniteBinaryRelation<X,Tuple<Y>> arrayRelationY() {
    return FiniteBinaryRelation.<Y,X>arrayRelationX().converse();
  }
  
  public static class ArrayRelationXY <
    X extends Comparable<? super X>,
    Y extends Comparable<? super Y>
  > extends FiniteBinaryRelation<Tuple<X>,Tuple<Y>> {
    public boolean add(X[] a, Y[] b) {
      return super.add(Tuple.create(a), Tuple.create(b));
    }
    public boolean remove(X[] a, Y[] b) {
      return super.remove(Tuple.create(a), Tuple.create(b));
    }
    public Predicate<Y[]> rightRelated(X[] e) {
      return (t) -> super.rightRelated(Tuple.create(e)).test(Tuple.create(t));
    }
    
    @SuppressWarnings("unchecked")
    public TreeSet<Y[]> rightRelata(X[] e) {
      var o = new TreeSet<Y[]>();
      for(var t : super.rightRelata(Tuple.create(e))) {
        o.add((Y[])t.toArray());
      }
      return o;
    }
    
    public static <
      R extends Comparable<? super R>,
      S extends Comparable<? super S>
    > void writeToCSV(ArrayRelationXY<R,S> rel,
        Function<R, String> xToString,
        Function<S, String> yToString,
        String path, boolean useBase64) throws IOException {
      rel.writeToCSV(Printers.tupleDecorator(xToString), Printers.tupleDecorator(yToString), path, useBase64);
    }
    
    @SuppressWarnings("unused")
    public boolean apply(X[] a, Y[] b) {
      return super.apply(Tuple.create(a), Tuple.create(b));
    }
  }
  
  public static <
    X extends Comparable<? super X>,
    Y extends Comparable<? super Y>
  > ArrayRelationXY<X,Y> arrayRelationXY() {
    return new ArrayRelationXY<X,Y>();
  }
  public static <
    X extends Comparable<? super X>,
    Y extends Comparable<? super Y>
  > ArrayRelationXY<X,Y> arrayRelationXY(Map<X[],Y[]> map) {
    ArrayRelationXY<X,Y> r = arrayRelationXY();
    
    for(var e : map.entrySet()) {
      r.add(e.getKey(),e.getValue());
    }
    return r;
  }
  
  @SuppressWarnings("unchecked")
  public static <
    X extends Comparable<? super X>,
    Y extends Comparable<? super Y>
  >  ArrayRelationXY<X,Y> arrayRelationXY(Iterable<X[]> domain, Iterable<Y[]> codomain, BiPredicate<X[],Y[]> rel) {
    ArrayRelationXY<X,Y> r = arrayRelationXY();
    TreeSet<Tuple<X>> d = new TreeSet<Tuple<X>>();
    for(var x : domain) d.add(Tuple.create(x));
    TreeSet<Tuple<Y>> c = new TreeSet<Tuple<Y>>();
    for(var y : codomain) c.add(Tuple.create(y));
    
    for(var p : Collections.list(new HeterogeneousPairEnumeration<Tuple<X>,Tuple<Y>>(d,c))) {
      var xa = (X[]) p.getFirst().toArray();
      var xb = (Y[]) p.getSecond().toArray();
      if(rel.test(xa, xb)) {
        r.add(xa, xb);
      }
    }
    
    return r;
  }
  
  public static <
    X extends Comparable<? super X>,
    Y extends Comparable<? super Y>
  > ArrayRelationXY<X,Y> arrayRelationXY(Iterable<X[]> domain, Function<X[],Y[]> f) {
    var r = new ArrayRelationXY<X,Y>();
    for(var x : domain) {
      r.add(x, f.apply(x));
    }
    return r;
  } 
}