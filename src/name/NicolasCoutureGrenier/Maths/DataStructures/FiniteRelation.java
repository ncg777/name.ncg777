package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class FiniteRelation<
  X extends Comparable<? super X>,
  Y extends Comparable<? super Y>> implements Iterable<HeteroPair<X,Y>>, Relation<X,Y>, Comparable<FiniteRelation<X,Y>> {
  
  protected TreeSet<HeteroPair<X,Y>> pairs = new TreeSet<>(Ordering.natural().nullsFirst());
  protected TreeSet<HeteroPair<Y,X>> pairsReversed = new TreeSet<>(Ordering.natural().nullsFirst());
  protected TreeSet<X> domain = new TreeSet<X>(Ordering.natural().nullsFirst());
  protected TreeSet<Y> codomain = new TreeSet<Y>(Ordering.natural().nullsFirst());
  
  public FiniteRelation() {
  }
  public FiniteRelation(Map<X,Y> map) {
    for(var e : map.entrySet()) {
      add(e.getKey(),e.getValue());
    }
  }
  public FiniteRelation(FiniteRelation<X,Y> rel) {
    pairs.clear(); pairs.addAll(rel.pairs);
    pairsReversed.clear(); pairsReversed.addAll(rel.pairsReversed);
  }
  public FiniteRelation(Iterable<X> domain, Iterable<Y> codomain, BiPredicate<X,Y> rel) {
    this(domain, codomain, Relation.fromBiPredicate(rel));
  }
  public FiniteRelation(Iterable<X> domain, Iterable<Y> codomain, Relation<X,Y> rel) {
    Collections.list(new HeterogeneousPairEnumeration<X,Y>(domain, codomain)).stream()
    .filter((p) -> rel.apply(p.getFirst(), p.getSecond())).forEach((p) -> add(p.getFirst(), p.getSecond()));
  }
  public FiniteRelation(Iterable<X> domain, Function<X,Y> f) {
    for(var x : domain) {
      add(x, f.apply(x));
    }
  }
  @Override
  public boolean equals(Object other) {
    if(!(other instanceof FiniteRelation)){ return false;}
    @SuppressWarnings("unchecked")
    FiniteRelation<X,Y> t = (FiniteRelation<X,Y>)other;
    return this.size() == t.size() && t.pairs.stream().allMatch((p) -> this.contains(p));
  }
  
  public boolean equals(FiniteRelation<X,Y> other) {
    return this.size() == other.size() && other.pairs.stream().allMatch((p) -> this.contains(p));
  }
  
  public static <
  X extends Comparable<? super X>,
  Y extends Comparable<? super Y>>  FiniteRelation<X,Y> universal(Iterable<X> domain,Iterable<Y> codomain) {
    return new FiniteRelation<X,Y>(domain, codomain, (X a,Y b) -> true);
  }
  
  public FiniteRelation<X,Y> complement(Iterable<X> domain,Iterable<Y> codomain) {
    var u = universal(domain, codomain);
    if(!u.pairs.containsAll(this.pairs)) {throw new RuntimeException("the relation exceeds the specified universe.");} 
    return u.minus(this);
  }
  
  public FiniteRelation<X,Y> complement() {
    return complement(domain(), codomain());
  }
  
  public int size() { return pairs.size(); }
  public boolean isEmpty() { return pairs.isEmpty(); }
  public boolean contains(HeteroPair<X,Y> p) { return this.pairs.contains(p); }
  public boolean add(X a, Y b) {
    var f_a = this.pairs.ceiling(HeteroPair.makeHeteroPair(a, null));
    var a0=a==null?null:(f_a != null && f_a.getFirst().equals(a) ? f_a.getFirst() : a);
    
    var f_b = this.pairsReversed.ceiling(HeteroPair.makeHeteroPair(b, null));
    var b0=b==null?null:(f_b!=null && f_b.getFirst().equals(b) ? f_b.getFirst() : b);
    
    var p = HeteroPair.makeHeteroPair(a0,b0);
    boolean o = pairs.add(p);
    if(o) pairsReversed.add(p.converse());
    this.domain.add(a0);
    this.codomain.add(b0);
    return o;
  }
  
  public boolean remove(X a, Y b) {
    var p = HeteroPair.makeHeteroPair(a,b);
    boolean o = pairs.remove(p);
    if(o) pairsReversed.remove(p.converse());
    var f_a = this.pairs.ceiling(HeteroPair.makeHeteroPair(a, null));
    if(a==null||f_a==null || !f_a.getFirst().equals(a)) this.domain.remove(a); 
    var f_b = this.pairsReversed.ceiling(HeteroPair.makeHeteroPair(b, null));
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
  public boolean containsAll(FiniteRelation<X,Y> r) { return pairs.containsAll(r.pairs); }
  
  public FiniteRelation<X,Y> intersect(FiniteRelation<X,Y> S){
    FiniteRelation<X,Y> o = new FiniteRelation<X,Y>();
    for(var p : this) {
      if(S.apply(p.getFirst(), p.getSecond())) o.add(p.getFirst(), p.getSecond());
    }
    return o;
  }
  
  public FiniteRelation<X,Y> union(FiniteRelation<X,Y> S){
    FiniteRelation<X,Y> o = new FiniteRelation<X,Y>();
    for(var p : this) o.add(p.getFirst(), p.getSecond());
    for(var p : S) o.add(p.getFirst(), p.getSecond());
    return o;
  }
  
  public FiniteRelation<X,Y> minus(FiniteRelation<X,Y> e) {
    var o = new FiniteRelation<X,Y>(this);
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
  FiniteRelation<X,V> compose(FiniteRelation<Y,V> S){
    var o = new FiniteRelation<X,V>();
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
  public FiniteRelation<Y,X> converse(){
    var o = new FiniteRelation<Y,X>();
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
  FiniteRelation<Y,W> rightResidual(FiniteRelation<X,W> S){
    var o = new FiniteRelation<Y,W>();
    
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
  FiniteRelation<X,V> leftResidual(FiniteRelation<V,Y> R){
    var o = new FiniteRelation<X,V>();
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
    return (t,u) -> pairs.contains(HeteroPair.makeHeteroPair(t, u));
  }
  
  public Predicate<Y> rightRelated(X e){
    return Functional.bindFirst(related(), e);
  }
  
  public TreeSet<Y> rightRelata(X e) {
    var o = new TreeSet<Y>(Ordering.natural().nullsFirst());
    HeteroPair<X,Y> pivot = this.pairs.ceiling(HeteroPair.makeHeteroPair(e, null));
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
    HeteroPair<Y,X> pivot = this.pairsReversed.ceiling(HeteroPair.makeHeteroPair(e, null));
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
        FiniteRelation<X, Y> finiteBinaryRelation,
        Function<X, String> xToString, 
        Function<Y, String> yToString, 
        String path, boolean useBase64) throws IOException {
    PrintWriter p = new PrintWriter(path);
    CSVWriter w = new CSVWriter(p,',', '"','\\', "\n");
    Function<X,String> xp = (x) -> xToString.apply((X)x);
    Function<Y,String> yp = (y) -> yToString.apply((Y)y);
    
    if(useBase64) { 
      xp = Printers.base64Decorator(xp);
      yp = Printers.base64Decorator(yp);
    }
    final var fxp = xp;
    final var fyp = yp;
    
    w.writeAll(finiteBinaryRelation.pairs.stream().map((t) -> {
      String[] arr = new String[2];
      arr[0] = fxp.apply(t.getFirst());
      arr[1] = fyp.apply(t.getSecond());
      return arr;
    }).collect(Collectors.toList()));
    
    w.close();
    p.close();
  }
  public void writeToCSV(Function<X, String> xFunction, Function<Y,String> yToString, String path, boolean useBase64) throws IOException {
    writeToCSV(this,xFunction,yToString,path,useBase64);
  }
  public void writeToCSV(Function<X,String> xToString, Function<Y,String> yToString, String path) throws IOException {
    writeToCSV(this,xToString,yToString,path,false);
  }
  
  public static <
  X extends Comparable<? super X>,
  Y extends Comparable<? super Y>> FiniteRelation<X,Y> readFromCSV (
    Function<String, X> xParser, 
    Function<String,Y> yParser, InputStream is) throws IOException, CsvException {
    return readFromCSV(xParser,yParser,is,false);
  }
  public static <
  X extends Comparable<? super X>,
  Y extends Comparable<? super Y>> FiniteRelation<X,Y> readFromCSV (
    Function<String, X> xParser, 
    Function<String,Y> yParser, InputStream is, boolean useBase64) throws IOException, CsvException {
    var st = new InputStreamReader(is);
    var o = readFromCSV(xParser, yParser, new BufferedReader(st), useBase64);
    st.close();
    return o;
  }
  
  public static <
  X extends Comparable<? super X>,
  Y extends Comparable<? super Y>> FiniteRelation<X,Y> readFromCSV (
    Function<String, X> xParser, 
    Function<String,Y> yParser, Reader reader) throws IOException, CsvException {
    return readFromCSV(xParser,yParser,reader,false);
  }
  public static <
  X extends Comparable<? super X>,
  Y extends Comparable<? super Y>> FiniteRelation<X,Y> readFromCSV (
    Function<String, X> xParser, 
    Function<String,Y> yParser, Reader reader, boolean useBase64) throws IOException, CsvException {
    CSVParserBuilder b = new CSVParserBuilder();
    
    CSVParser p = b.withSeparator(',').withQuoteChar('"').withEscapeChar('\\').build();
    CSVReaderBuilder rb = new CSVReaderBuilder(reader);
    CSVReader r = rb.withCSVParser(p).build();
    var o = new FiniteRelation<X,Y>();
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
  Y extends Comparable<? super Y>> FiniteRelation<X,Y> readFromCSV (
    Function<String, X> xParser, 
    Function<String,Y> yParser, String path, boolean useBase64)  throws IOException, CsvException {
    return readFromCSV(xParser, yParser, new FileReader(path), useBase64);
  }
  
  public static <
  X extends Comparable<? super X>,
  Y extends Comparable<? super Y>> FiniteRelation<X,Y> readFromCSV (
    Function<String, X> xParser, 
    Function<String,Y> yParser, String path)  throws IOException, CsvException {
    return readFromCSV(xParser, yParser, new FileReader(path));
  }

  @Override
  public Iterator<HeteroPair<X, Y>> iterator() {
    return Iterators.unmodifiableIterator(this.pairs.iterator());
  }
  

  @Override
  public String toString() {
    return toString((t) -> t.toString(), (u) -> u.toString());
  }
  
  public String toString(Function<X,String> printer1, Function<Y,String> printer2) {
    return this.toJSONObjectString(printer1, printer2);
  }
  
  @Override
  public boolean apply(X a, Y b) {
    return pairs.contains(HeteroPair.makeHeteroPair(a, b));
  }
  @Override
  public int compareTo(FiniteRelation<X, Y> o) {
    var it = new IterableComparator<HeteroPair<X,Y>>();
    
    return it.compare(this, o);
  }
  

  public void printToJSON(Function<X,String> printer1, Function<Y,String> printer2, String path) throws FileNotFoundException {
    PrintWriter pw = new PrintWriter(path);
    printToJSON(printer1,printer2,pw);
    pw.flush();
    pw.close();
  }
  public void printToJSON(Function<X,String> printer1, Function<Y,String> printer2, Writer sw) {
    try {
      var gen = new JsonFactory(new JsonFactoryBuilder()).createGenerator(sw);
      toJSONObjectString(printer1,printer2, this, gen);
      gen.flush();  
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public String toJSONObjectString(Function<X,String> printer1, Function<Y,String> printer2) {
    StringWriter sw = new StringWriter();
    this.printToJSON(printer1,printer2,sw);
    return sw.getBuffer().toString();
  }
  
  private static <T extends Comparable<? super T>, U extends Comparable<? super U>> void 
  toJSONObjectString(Function<T,String> printer1, Function<U,String> printer2, FiniteRelation<T,U> rel, JsonGenerator gen) throws IOException {
    gen.writeStartArray();
    for(var v : rel.pairs) {
      HeteroPair.toJSONObjectString(printer1, printer2, v, gen);
    }
    gen.writeEndArray();
  }
  
  
  public static <
    T extends Comparable<? super T>, 
    U extends Comparable<? super U>> 
      FiniteRelation<T,U> parseJSONObject(
          String str, 
          Function<String,T> parser1,
          Function<String,U> parser2) {
    try {
      FiniteRelation<T,U> o = new FiniteRelation<>();
      var b = new JsonFactoryBuilder().build();
      var p = b.setCodec(new ObjectMapper()).createParser(str).readValueAsTree();
      if(!p.isArray()) {
        throw new RuntimeException("invalid");
      }
      for(int i=0;i<p.size();i++) {
        var pair =  HeteroPair.parseJSONObject(p.get(i).toString(), parser1, parser2);
        o.add(pair.getFirst(), pair.getSecond());
      }
      return o;   
    } catch (IOException e) {
      throw new RuntimeException("invalid input");
    }
    
  }
  public static <
  T extends Comparable<? super T>, 
  U extends Comparable<? super U>> 
    FiniteRelation<T,U> parseJSONFile(
        String path, 
        Function<String,T> parser1,
        Function<String,U> parser2) {
    try {
      FiniteRelation<T,U> o = new FiniteRelation<>();
      
      var b = new JsonFactoryBuilder().build();
      File f = new File(path);
      var p = b.setCodec(new ObjectMapper()).createParser(f).readValueAsTree();
      if(!p.isArray()) {
        throw new RuntimeException("invalid");
      }
      for(int i=0;i<p.size();i++) {
        var pair =  HeteroPair.parseJSONObject(p.get(i), parser1, parser2);
        o.add(pair.getFirst(), pair.getSecond());
      }
      return o;   
    } catch (IOException e) {
      throw new RuntimeException("invalid input");
    }
  }
  
  public JaggedList<String> toStringJaggedList(Function<X,String> printer1,Function<Y,String> printer2) {
    var o = new JaggedList<String>();
    
    for(var p : pairs) {
      var x = o.newChild();
      x.add(p.getFirst() == null ? null : printer1.apply(p.getFirst()));
      x.add(p.getSecond() == null ? null : printer2.apply(p.getSecond()));
    }
    return o;
  }
  public static <
    T extends Comparable<? super T>, 
    U extends Comparable<? super U>> 
      FiniteRelation<T,U> fromStringJaggedList(
          JaggedList<String> arr, 
          Function<String,T> parser1,
          Function<String,U> parser2) {
    var o = new FiniteRelation<T,U>();
    parser1 = Parsers.nullDecorator(parser1);
    parser2 = Parsers.nullDecorator(parser2);
    for(int i=0;i<arr.size();i++) {
      o.add(parser1.apply(arr.get(i,0).getValue()), parser2.apply(arr.get(i,1).getValue()));
    }
    return o;
  }
}