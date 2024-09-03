package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

import com.opencsv.exceptions.CsvException;

import name.NicolasCoutureGrenier.Maths.Relations.Relation;

public class FiniteBinaryHomogeneousRelation<L extends Comparable<? super L>>
extends FiniteBinaryRelation<L, L> { 
  
  public FiniteBinaryHomogeneousRelation() {
    super();
  }
  public FiniteBinaryHomogeneousRelation(FiniteBinaryRelation<L,L> rel) {
    super(rel);
  }
  public FiniteBinaryHomogeneousRelation(Map<L,L> map) {
    super(map);
  }
  public FiniteBinaryHomogeneousRelation(Iterable<L> domain, BiPredicate<L,L> rel) {
    super(domain, domain,Relation.fromBiPredicate(rel));
  }
  
  public FiniteBinaryHomogeneousRelation(Iterable<L> domain, Relation<L,L> rel) {
    super(domain, domain,rel);
  }
  public FiniteBinaryHomogeneousRelation(Iterable<L> domain, Function<L,L> f) {
    super(domain, f);
  }
  public static <L extends Comparable<? super L>> FiniteBinaryHomogeneousRelation<L> identity(Iterable<L> domain) {
    return new FiniteBinaryHomogeneousRelation<L>(domain, Relation.fromBiPredicate((L a,L b) -> a.equals(b)));
  }
  public static <L extends Comparable<? super L>> FiniteBinaryHomogeneousRelation<L> universal(Iterable<L> domain) {
    return new FiniteBinaryHomogeneousRelation<L>(domain, Relation.fromBiPredicate((L a,L b) -> true));
  }
    
  public <V extends Comparable<? super V>> FiniteBinaryHomogeneousRelation<L> compose(
      FiniteBinaryHomogeneousRelation<L> e) {
    return new FiniteBinaryHomogeneousRelation<L>(super.compose(e));
  }
  
  public FiniteBinaryHomogeneousRelation<L> converse() {
    return new FiniteBinaryHomogeneousRelation<L>(super.converse());
  }
  
  public FiniteBinaryHomogeneousRelation<L> intersect(FiniteBinaryHomogeneousRelation<L> e) {
    return new FiniteBinaryHomogeneousRelation<L>(super.intersect(e));
  }
  
  public FiniteBinaryHomogeneousRelation<L> union(FiniteBinaryHomogeneousRelation<L> e) {
    return new FiniteBinaryHomogeneousRelation<L>(super.union(e));
  }
  
  public FiniteBinaryHomogeneousRelation<L> minus(FiniteBinaryHomogeneousRelation<L> e) {
    return new FiniteBinaryHomogeneousRelation<L>(super.minus(e));
  }
  
  public FiniteBinaryHomogeneousRelation<L> complement(Iterable<L> domain) {
    return new FiniteBinaryHomogeneousRelation<>(super.complement(domain, domain));
  }
  
  public FiniteBinaryHomogeneousRelation<L> complement() {
    return new FiniteBinaryHomogeneousRelation<>(super.complement());
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
  public FiniteBinaryHomogeneousRelation<L> rightResidual(
      FiniteBinaryHomogeneousRelation<L> S) {
    return new FiniteBinaryHomogeneousRelation<L>(super.rightResidual(S));
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
  public FiniteBinaryHomogeneousRelation<L> leftResidual(
      FiniteBinaryHomogeneousRelation<L> R) {
    return new FiniteBinaryHomogeneousRelation<L>(super.leftResidual(R));
  }
  
  public FiniteBinaryHomogeneousRelation<L> transitiveClosure() {
    FiniteBinaryHomogeneousRelation<L> o = new FiniteBinaryHomogeneousRelation<L>(this);
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
  public boolean isAntisymmetric(Iterable<L> domain) {return FiniteBinaryHomogeneousRelation
      .identity(domain).containsAll(this.intersect(this.converse()));}
  public boolean isAntisymmetric() {return isAntisymmetric(domain());}
  public boolean isIdempotent() { return this.compose(this).equals(this); }
  public boolean isReflexive(Iterable<L> domain) { return this.containsAll(identity(domain));}
  public boolean isReflexive() { return isReflexive(domain());}
  public boolean isCoreflexive(Iterable<L> domain) { return identity(domain).containsAll(this);}
  public boolean isCoreflexive() { return isCoreflexive(domain());}
  public boolean isIrreflexive(Iterable<L> domain) { return FiniteBinaryHomogeneousRelation.identity(domain).intersect(this).isEmpty();}
  public boolean isIrreflexive() { return isReflexive(domain());}
  public boolean isEquivalence() { return isPreorder() && isSymmetric();}
  public boolean isConnected(Iterable<L> domain) { return FiniteBinaryHomogeneousRelation
      .universal(domain)
      .equals(
          FiniteBinaryHomogeneousRelation.identity(domain)
          .union(this)
          .union(this.converse()));}
  public boolean isConnected() { return isConnected(domain());}
  public boolean isStronglyConnected(Iterable<L> domain) { 
    return this.union(this.converse()).equals(FiniteBinaryHomogeneousRelation.universal(domain));
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
  FiniteBinaryHomogeneousRelation<L> 
  readFromCSV (Function<String,L> lParser, String path) throws IOException, CsvException {
    return readFromCSV(lParser,path,false);
  }
  public static <L extends Comparable<? super L>> 
    FiniteBinaryHomogeneousRelation<L> 
    readFromCSV (Function<String,L> lParser, String path,boolean useBase64) throws IOException, CsvException {
    return new FiniteBinaryHomogeneousRelation<L>(FiniteBinaryRelation.readFromCSV(lParser, lParser, path, useBase64));
  }
  


  @Override
  public String toString() {
    return toString((t) -> t.toString());
  }
  
  public String toString(Function<L,String> printer1) {
    return this.toJSONObjectString(printer1, printer1);
  }
  
  public void printToJSON(Function<L,String> printer, String path) throws FileNotFoundException {
    printToJSON(printer,printer,path);
  }
  public void printToJSON(Function<L,String> printer, Writer sw) {
    printToJSON(printer,printer,sw);
  }
  
  public String toJSONObjectString(Function<L,String> printer) {
    return this.toJSONObjectString(printer,printer);
  }
  
  
  
  public static <L extends Comparable<? super L>>  
      FiniteBinaryHomogeneousRelation<L> parseJSONObject(
          String str, 
          Function<String,L> parser) {
    return new FiniteBinaryHomogeneousRelation<L>(parseJSONObject(str, parser, parser));
    
  }
  public static <L extends Comparable<? super L>> 
    FiniteBinaryHomogeneousRelation<L> parseJSONFile(
        String path, 
        Function<String,L> parser) {
    return new FiniteBinaryHomogeneousRelation<L>(parseJSONFile(path,parser,parser));
  }
 
}