package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import com.opencsv.exceptions.CsvException;

import name.NicolasCoutureGrenier.CS.Printers;
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
  @SuppressWarnings("unchecked")
  public static class ArrayRelation <
    X extends Comparable<? super X>,
    R extends Comparable<? super R>
  > extends FiniteBinaryHomogeneousRelation<R> {
    
    public boolean add(R a, R b) {
      return super.add((R)Tuple.create(a), (R)Tuple.create(b));
    }
    
    public boolean remove(R a, R b) {
      return super.remove((R)Tuple.create(a), (R)Tuple.create(b));
    }
    
    public Predicate<R> rightRelated(R e) {
      return (t) -> super.rightRelated((R)Tuple.create(e)).test((R)Tuple.create(t));
    }
    
    public TreeSet<R> rightRelata(R e) {
      var o = new TreeSet<R>();
      for(var t : super.rightRelata((R)Tuple.create(e))) {
        o.add(t);
      }
      return o;
    }
    
    public static <
      X extends Comparable<? super X>,
      Y extends Comparable<? super Y>,
      R extends Comparable<? super R>,
      S extends Comparable<? super S>
    > void writeToCSV(ArrayRelation<X,R> rel,
      Function<X, String> xToString,
      String path, boolean useBase64) throws IOException {
    rel.writeToCSV((Function<R,String>)Printers.tupleDecorator(xToString), path, useBase64);
  }
    
    public boolean apply(R a, R b) {
      return super.apply(a, b);
    }
  }
  
  public static <
    X extends Comparable<? super X>,
    R extends Comparable<? super R>
  > ArrayRelation<X,R> arrayRelation() {
    return new ArrayRelation<X,R>();
  }
  public static <
    X extends Comparable<? super X>,
    R extends Comparable<? super R>
  > ArrayRelation<X,R> arrayRelation(Map<R,R> map) {
    ArrayRelation<X,R> r = arrayRelation();
    
    for(var e : map.entrySet()) {
      r.add(e.getKey(),e.getValue());
    }
    return r;
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <
    X extends Comparable<? super X>,
    R extends Comparable<? super R>
  >  ArrayRelation<X,R> arrayRelation(Iterable<R> domain, BiPredicate<R,R> rel) {
    ArrayRelation<X,R> r = new ArrayRelation<X,R>();
    var d = new TreeSet<Tuple>();
    for(var x : domain) d.add(Tuple.create(x));
    
    for(var p : CollectionUtils.cartesianProduct(d,d)) {
      R ta = ((R)(((HeterogeneousPair)p).getFirst()));
      R tb = ((R)(((HeterogeneousPair)p).getSecond()));
      if(((BiPredicate)rel).test((Object)ta, (Object)tb)) {
        if(ta instanceof Tuple) {
          if(tb instanceof Tuple) {
            r.add(ta,tb);
          } else {
            r.add(ta, (R)Tuple.create(tb));
          }
        } else {
          if(tb instanceof Tuple) {
            r.add((R)Tuple.create(ta),tb);
          } else {
            r.add((R)Tuple.create(ta),(R)Tuple.create(tb));
          }
        }
      }
    }
    
    return r;
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <
    X extends Comparable<? super X>,
    R extends Comparable<? super R>
  > ArrayRelation arrayRelation(R[] domain, R[] codomain, BiPredicate<R,R> rel) {
    return arrayRelation((Iterable<R>)Arrays.asList(domain),(BiPredicate)rel);
  }
  
  public static <
    X extends Comparable<? super X>,
    R extends Comparable<? super R>
  > ArrayRelation<X,R> arrayRelation(Iterable<R> domain, Function<R,R> f) {
    ArrayRelation<X,R> r = new ArrayRelation<X,R>();
    for(var x : domain) {
      r.add(x, f.apply(x));
    }
    return r;
  }
}