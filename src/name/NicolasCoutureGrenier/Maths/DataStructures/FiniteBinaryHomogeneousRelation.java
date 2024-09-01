package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import com.opencsv.exceptions.CsvException;

import name.NicolasCoutureGrenier.CS.Printers;
import name.NicolasCoutureGrenier.Maths.Enumerations.HeterogeneousPairEnumeration;
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
  
  private static class ArrayRelation <
    X extends Comparable<? super X>
  > extends FiniteBinaryHomogeneousRelation<Tuple<X>> {
    @SuppressWarnings({"unused"})
    public boolean add(X[] a, X[] b) {
      return super.add(Tuple.create(a), Tuple.create(b));
    }
    @SuppressWarnings("unused")
    public boolean remove(X[] a, X[] b) {
      return super.remove(Tuple.create(a), Tuple.create(b));
    }
    @SuppressWarnings("unused")
    public Predicate<X[]> rightRelated(X[] e) {
      return (t) -> super.rightRelated(Tuple.create(e)).test(Tuple.create(t));
    }
    
    @SuppressWarnings({"unchecked", "unused"})
    public TreeSet<X[]> rightRelata(X[] e) {
      var o = new TreeSet<X[]>();
      for(var t : super.rightRelata(Tuple.create(e))) {
        o.add((X[])t.toArray());
      }
      
      return o;
    }
    
    @SuppressWarnings("unused")
    public void writeArraysToCSV(
        Function<X, String> xToString,
        String path, boolean useBase64) throws IOException {
      super.writeToCSV(Printers.tupleDecorator(xToString), path, useBase64);
    }
    
    @SuppressWarnings("unused")
    public boolean apply(X[] a, X[] b) {
      return super.apply(Tuple.create(a), Tuple.create(b));
    }
  }
  
  public static <
    X extends Comparable<? super X>
  > ArrayRelation<X> arrayRelation() {
    return new ArrayRelation<X>();
  }
  public static <
    X extends Comparable<? super X>
  > ArrayRelation<X> arrayRelation(Map<X[],X[]> map) {
    ArrayRelation<X> r = arrayRelation();
    
    for(var e : map.entrySet()) {
      r.add(e.getKey(),e.getValue());
    }
    return r;
  }
  
  @SuppressWarnings("unchecked")
  public static <
    X extends Comparable<? super X>
  >  ArrayRelation<X> arrayRelation(Iterable<X[]> domain, Iterable<X[]> codomain, BiPredicate<X[],X[]> rel) {
    ArrayRelation<X> r = arrayRelation();
    TreeSet<Tuple<X>> d = new TreeSet<Tuple<X>>();
    for(var x : domain) d.add(Tuple.create(x));
    TreeSet<Tuple<X>> c = new TreeSet<Tuple<X>>();
    for(var y : codomain) c.add(Tuple.create(y));
    
    for(var p : Collections.list(new HeterogeneousPairEnumeration<Tuple<X>,Tuple<X>>(d,c))) {
      var xa = (X[]) p.getFirst().toArray();
      var xb = (X[]) p.getSecond().toArray();
      if(rel.test(xa, xb)) {
        r.add(xa, xb);
      }
    }
    
    return r;
  }
  
  public static <
    X extends Comparable<? super X>,
    Y extends Comparable<? super Y>
  > ArrayRelation<X> arrayRelation(Iterable<X[]> domain, Function<X[],X[]> f) {
    var r = new ArrayRelation<X>();
    for(var x : domain) {
      r.add(x, f.apply(x));
    }
    return r;
  }
}