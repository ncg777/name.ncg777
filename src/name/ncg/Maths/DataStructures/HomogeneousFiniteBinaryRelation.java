package name.ncg.Maths.DataStructures;

import java.io.IOException;
import java.util.TreeSet;
import java.util.function.Function;

import com.opencsv.exceptions.CsvException;

import name.ncg.Maths.Relations.Relation;

@SuppressWarnings("serial")
public class HomogeneousFiniteBinaryRelation<L extends Comparable<? super L>>
extends FiniteBinaryRelation<L, L> { 
  
  public HomogeneousFiniteBinaryRelation() {
    super();
  }
  public HomogeneousFiniteBinaryRelation(FiniteBinaryRelation<L,L> rel) {
    super(rel);
  }
  
  public HomogeneousFiniteBinaryRelation(Iterable<L> domain, Relation<L,L> rel) {
    super(domain, domain,rel);
  }
  
  public static <L extends Comparable<? super L>> HomogeneousFiniteBinaryRelation<L> identity(Iterable<L> domain) {
    return new HomogeneousFiniteBinaryRelation<L>(domain, Relation.fromBiPredicate((L a,L b) -> a.equals(b)));
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
   * LHS of S matching LHS of R where LHS_R_w ⊇ LHS_S_w.
   * 
   * @param <V>
   * @param R
   * @return
   */
  public HomogeneousFiniteBinaryRelation<L> leftResidual(
      HomogeneousFiniteBinaryRelation<L> R) {
    return new HomogeneousFiniteBinaryRelation<L>(super.leftResidual(R));
  }
  
  public HomogeneousFiniteBinaryRelation<L> calcTransitiveClosure() {
    var o = new HomogeneousFiniteBinaryRelation<L>();
    boolean grew = false;
    
    do {
      grew = false;
      for(var p : this) {
        if(!o.contains(p)) {
          o.add(p);
          grew = true;
        }
        
        for(var q : this) {
          if(!o.contains(q)) {
            o.add(q);
            grew = true;
          }
          if(p.getSecond().equals(q.getFirst())) {
            var t = OrderedPair.makeOrderedPair(p.getFirst(), q.getSecond());
            if(!o.contains(t)) {
              o.add(t);
              grew = true;
            }
            
          }
        }
      }
    } while(grew);
    return o;
  }
  
  public void writeToCSV(Function<L,String> lToString, String path) throws IOException {
    super.writeToCSV(lToString, lToString, path);
  }
  public static <L extends Comparable<? super L>> 
    HomogeneousFiniteBinaryRelation<L> 
    readFromCSV (Function<String,L> lParser, String path) throws IOException, CsvException {
    return new HomogeneousFiniteBinaryRelation<L>(FiniteBinaryRelation.readFromCSV(lParser, lParser, path));
  }
}