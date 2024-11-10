package name.ncg777.Maths.Objects;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonParseException;
import com.opencsv.exceptions.CsvException;

import name.ncg777.CS.DataStructures.JaggedList;
import name.ncg777.Maths.Relations.Relation;

public class FiniteHomoRelation<L extends Comparable<? super L>>
extends FiniteRelation<L, L> { 
  
  public FiniteHomoRelation() {
    super();
  }
  public FiniteHomoRelation(FiniteRelation<L,L> rel) {
    super(rel);
  }
  public FiniteHomoRelation(Map<L,L> map) {
    super(map);
  }
  public FiniteHomoRelation(Iterable<L> domain, BiPredicate<L,L> rel) {
    super(domain, domain,Relation.fromBiPredicate(rel));
  }
  
  public FiniteHomoRelation(Iterable<L> domain, Relation<L,L> rel) {
    super(domain, domain,rel);
  }
  public FiniteHomoRelation(Iterable<L> domain, Function<L,L> f) {
    super(domain, f);
  }
  public static <L extends Comparable<? super L>> FiniteHomoRelation<L> identity(Iterable<L> domain) {
    return new FiniteHomoRelation<L>(domain, Relation.fromBiPredicate((L a,L b) -> a.equals(b)));
  }
  public static <L extends Comparable<? super L>> FiniteHomoRelation<L> universal(Iterable<L> domain) {
    return new FiniteHomoRelation<L>(domain, Relation.fromBiPredicate((L a,L b) -> true));
  }
    
  public <V extends Comparable<? super V>> FiniteHomoRelation<L> compose(
      FiniteHomoRelation<L> e) {
    return new FiniteHomoRelation<L>(super.compose(e));
  }
  
  public FiniteHomoRelation<L> converse() {
    return new FiniteHomoRelation<L>(super.converse());
  }
  
  public FiniteHomoRelation<L> intersect(FiniteHomoRelation<L> e) {
    return new FiniteHomoRelation<L>(super.intersect(e));
  }
  
  public FiniteHomoRelation<L> union(FiniteHomoRelation<L> e) {
    return new FiniteHomoRelation<L>(super.union(e));
  }
  
  public FiniteHomoRelation<L> minus(FiniteHomoRelation<L> e) {
    return new FiniteHomoRelation<L>(super.minus(e));
  }
  
  public FiniteHomoRelation<L> complement(Iterable<L> domain) {
    return new FiniteHomoRelation<>(super.complement(domain, domain));
  }
  
  public FiniteHomoRelation<L> complement() {
    return new FiniteHomoRelation<>(super.complement());
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
  public FiniteHomoRelation<L> rightResidual(
      FiniteHomoRelation<L> S) {
    return new FiniteHomoRelation<L>(super.rightResidual(S));
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
  public FiniteHomoRelation<L> leftResidual(
      FiniteHomoRelation<L> R) {
    return new FiniteHomoRelation<L>(super.leftResidual(R));
  }
  
  public FiniteHomoRelation<L> transitiveClosure() {
    FiniteHomoRelation<L> o = new FiniteHomoRelation<L>(this);
    int n = o.size();
    while(true){
      o = o.union(o.compose(o));
      if(o.size() == n) break;
      n = o.size();
    }
    return o;
  }
  
  public boolean isTransitive() { return this.containsAll(this.compose(this));}
  public boolean isSymmetric() { return this.equals(this.converse()); }
  public boolean isAsymmetric() { return this.intersect(this.converse()).isEmpty(); }
  public boolean isAntisymmetric(Iterable<L> domain) {return FiniteHomoRelation
      .identity(domain).containsAll(this.intersect(this.converse()));}
  public boolean isAntisymmetric() {return isAntisymmetric(domain());}
  public boolean isIdempotent() { return this.compose(this).equals(this); }
  public boolean isReflexive(Iterable<L> domain) { return this.containsAll(identity(domain));}
  public boolean isReflexive() { return isReflexive(domain());}
  public boolean isCoreflexive(Iterable<L> domain) { return identity(domain).containsAll(this);}
  public boolean isCoreflexive() { return isCoreflexive(domain());}
  public boolean isIrreflexive(Iterable<L> domain) { return FiniteHomoRelation.identity(domain).intersect(this).isEmpty();}
  public boolean isIrreflexive() { return isReflexive(domain());}
  public boolean isEquivalence() { return isPreorder() && isSymmetric();}
  public boolean isConnected(Iterable<L> domain) { return FiniteHomoRelation
      .universal(domain)
      .equals(
          FiniteHomoRelation.identity(domain)
          .union(this)
          .union(this.converse()));}
  public boolean isConnected() { return isConnected(domain());}
  public boolean isStronglyConnected(Iterable<L> domain) { 
    return this.union(this.converse()).equals(FiniteHomoRelation.universal(domain));
  }
  public boolean isStronglyConnected() { return isStronglyConnected(domain());}
  public boolean isPreorder(Iterable<L> domain) { return isTransitive() && isReflexive(domain); }
  public boolean isPreorder() { return isPreorder(domain()); }
  public boolean isPartialOrder(Iterable<L> domain) { return isPreorder(domain) && isAntisymmetric(domain);}
  public boolean isPartialOrder() { return isPartialOrder(domain()); }
  public boolean isTotalOrder(Iterable<L> domain) { return isStronglyConnected(domain) && isPartialOrder(domain);}
  public boolean isTotalOrder() { return isTotalOrder(domain());}
  public boolean isStrictPartialOrder(Iterable<L> domain) { return isTransitive() && isIrreflexive(domain);}
  public boolean isStrictPartialOrder() { return isStrictPartialOrder(domain());} 
  public boolean isStrictTotalOrder(Iterable<L> domain) { return isConnected(domain) && isStrictPartialOrder(domain);}
  public boolean isStrictTotalOrder() { return isStrictTotalOrder(domain());} 
  public boolean isBijective(Iterable<L> domain) { return isLeftTotal(domain) && isSurjective(domain);}
  public boolean isBijective() { return isBijective(domain()); }
  
  /***
   * 
   * @return R ∧ I− ≤ (R ∧ I−)•(R ∧ I−)
   */
  public boolean isDense(Iterable<L> domain) {
    var I = identity(domain);
    var IComplement = I.complement();
    var RIntersectIComplement = this.intersect(IComplement);
    return RIntersectIComplement.compose(RIntersectIComplement).containsAll(RIntersectIComplement);
  }
  
  public boolean isDense() { return isDense(domain()); }
  
  public void writeToCSV(Function<L,String> lToString, String path, boolean useBase64) throws IOException {
    super.writeToCSV(lToString, lToString, path, useBase64);
  }
  public void writeToCSV(Function<L,String> lToString, String path) throws IOException {
    super.writeToCSV(lToString, lToString, path, false);
  }
  public static <L extends Comparable<? super L>> 
  FiniteHomoRelation<L> 
  readFromCSV (Function<String,L> lParser, String path) throws IOException, CsvException {
    return readFromCSV(lParser,path,false);
  }
  public static <L extends Comparable<? super L>> 
    FiniteHomoRelation<L> 
    readFromCSV (Function<String,L> lParser, String path,boolean useBase64) throws IOException, CsvException {
    return new FiniteHomoRelation<L>(FiniteRelation.readFromCSV(lParser, lParser, path, useBase64));
  }
  
  @Override
  public String toString() {
    return toString((t) -> t.toString());
  }
  
  public String toString(Function<L,String> printer1) {
    return this.toJSONArrayString(printer1, printer1);
  }
  
  public void printToJSON(Function<L,String> printer, String path) throws FileNotFoundException {
    printToJSON(printer,printer,path);
  }
  
  public String toJSONObjectString(Function<L,String> printer) {
    return this.toJSONArrayString(printer,printer);
  }
  
  public static <L extends Comparable<? super L>>  
      FiniteHomoRelation<L> parseJSONObject(
          String str, 
          Function<String,L> parser) {
    return new FiniteHomoRelation<L>(parseJSONArray(str, parser, parser));
    
  }
  public static <L extends Comparable<? super L>> 
    FiniteHomoRelation<L> parseJSONFile(
        String path, 
        Function<String,L> parser) throws JsonParseException, IOException {
    return new FiniteHomoRelation<L>(parseJSONFile(path,parser,parser));
  }
  
  @SuppressWarnings("unchecked")
  public L[][] toArray() {
   if(this.size()==0) return null;
   var c = this.pairs.first().getFirst().getClass();
   
   L[][] o = (L[][]) Array.newInstance(c, this.pairs.size(),2);
   int i=0;
   for(var p : this) {
     o[i][0] = p.getFirst();
     o[i][1] = p.getSecond();
     i++;
   }
   return o;
  }
  public JaggedList<String> toStringJaggedList(Function<L,String> printer) {
    return super.toStringJaggedList(printer, printer);
  }
  public static <T extends Comparable<? super T>> 
    FiniteHomoRelation<T> fromStringJaggedList(
        JaggedList<String> arr, 
        Function<String,T> parser){
    return new FiniteHomoRelation<T>(FiniteRelation.fromStringJaggedList(arr, parser, parser));
  }
}