package name.ncg.Maths.DataStructures;

import java.io.IOException;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Function;

import com.opencsv.exceptions.CsvException;

import name.ncg.Maths.Relations.Relation;

public class HomogeneousFiniteBinaryRelation<L extends Comparable<? super L>>
extends FiniteBinaryRelation<L, L> { 
  
  public HomogeneousFiniteBinaryRelation() {
    super();
  }
  public HomogeneousFiniteBinaryRelation(FiniteBinaryRelation<L,L> rel) {
    super(rel);
  }
  public HomogeneousFiniteBinaryRelation(Map<L,L> map) {
    super(map);
  }
  public HomogeneousFiniteBinaryRelation(Iterable<L> domain, BiPredicate<L,L> rel) {
    super(domain, domain,Relation.fromBiPredicate(rel));
  }
  
  public HomogeneousFiniteBinaryRelation(Iterable<L> domain, Relation<L,L> rel) {
    super(domain, domain,rel);
  }
  public HomogeneousFiniteBinaryRelation(Iterable<L> domain, Function<L,L> f) {
    super(domain, f);
  }
  public static <L extends Comparable<? super L>> HomogeneousFiniteBinaryRelation<L> identity(Iterable<L> domain) {
    return new HomogeneousFiniteBinaryRelation<L>(domain, Relation.fromBiPredicate((L a,L b) -> a.equals(b)));
  }
  public static <L extends Comparable<? super L>> HomogeneousFiniteBinaryRelation<L> universal(Iterable<L> domain) {
    return new HomogeneousFiniteBinaryRelation<L>(domain, Relation.fromBiPredicate((L a,L b) -> true));
  }
  @Override
  public TreeSet<L> domain(){
    TreeSet<L> o = new TreeSet<L>();
    o.addAll(super.domain());
    o.addAll(super.codomain());
    return o;
  }
  
  public TreeSet<L> codomain(){
    return this.domain();
  }
  
  public <V extends Comparable<? super V>> HomogeneousFiniteBinaryRelation<L> compose(
      HomogeneousFiniteBinaryRelation<L> e) {
    return new HomogeneousFiniteBinaryRelation<L>(super.compose(e));
  }
  
  public HomogeneousFiniteBinaryRelation<L> converse() {
    return new HomogeneousFiniteBinaryRelation<L>(super.converse());
  }
  
  public HomogeneousFiniteBinaryRelation<L> intersect(HomogeneousFiniteBinaryRelation<L> e) {
    return new HomogeneousFiniteBinaryRelation<L>(super.intersect(e));
  }
  
  public HomogeneousFiniteBinaryRelation<L> union(HomogeneousFiniteBinaryRelation<L> e) {
    return new HomogeneousFiniteBinaryRelation<L>(super.union(e));
  }
  
  public HomogeneousFiniteBinaryRelation<L> minus(HomogeneousFiniteBinaryRelation<L> e) {
    return new HomogeneousFiniteBinaryRelation<L>(super.minus(e));
  }
  
  public HomogeneousFiniteBinaryRelation<L> complement(Iterable<L> domain) {
    return new HomogeneousFiniteBinaryRelation<>(super.complement(domain, domain));
  }
  
  public HomogeneousFiniteBinaryRelation<L> complement() {
    return new HomogeneousFiniteBinaryRelation<>(super.complement());
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
  public HomogeneousFiniteBinaryRelation<L> rightResidual(
      HomogeneousFiniteBinaryRelation<L> S) {
    return new HomogeneousFiniteBinaryRelation<L>(super.rightResidual(S));
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
  public HomogeneousFiniteBinaryRelation<L> leftResidual(
      HomogeneousFiniteBinaryRelation<L> R) {
    return new HomogeneousFiniteBinaryRelation<L>(super.leftResidual(R));
  }
  
  public HomogeneousFiniteBinaryRelation<L> transitiveClosure() {
    HomogeneousFiniteBinaryRelation<L> o = new HomogeneousFiniteBinaryRelation<L>(this);
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
  public boolean isAntisymmetric(Iterable<L> domain) {return HomogeneousFiniteBinaryRelation
      .identity(domain).containsAll(this.intersect(this.converse()));}
  public boolean isAntisymmetric() {return isAntisymmetric(domain());}
  public boolean isIdempotent() { return this.compose(this).equals(this); }
  public boolean isReflexive(Iterable<L> domain) { return this.containsAll(identity(domain));}
  public boolean isReflexive() { return isReflexive(domain());}
  public boolean isCoreflexive(Iterable<L> domain) { return identity(domain).containsAll(this);}
  public boolean isCoreflexive() { return isCoreflexive(domain());}
  public boolean isIrreflexive(Iterable<L> domain) { return HomogeneousFiniteBinaryRelation.identity(domain).intersect(this).isEmpty();}
  public boolean isIrreflexive() { return isReflexive(domain());}
  public boolean isEquivalence() { return isPreorder() && isSymmetric();}
  public boolean isConnected(Iterable<L> domain) { return HomogeneousFiniteBinaryRelation
      .universal(domain)
      .equals(
          HomogeneousFiniteBinaryRelation.identity(domain)
          .union(this)
          .union(this.converse()));}
  public boolean isConnected() { return isConnected(domain());}
  public boolean isStronglyConnected(Iterable<L> domain) { 
    return this.union(this.converse()).equals(HomogeneousFiniteBinaryRelation.universal(domain));
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
  
  public void writeToCSV(Function<L,String> lToString, String path) throws IOException {
    super.writeToCSV(lToString, lToString, path);
  }
  public static <L extends Comparable<? super L>> 
    HomogeneousFiniteBinaryRelation<L> 
    readFromCSV (Function<String,L> lParser, String path) throws IOException, CsvException {
    return new HomogeneousFiniteBinaryRelation<L>(FiniteBinaryRelation.readFromCSV(lParser, lParser, path));
  }
}