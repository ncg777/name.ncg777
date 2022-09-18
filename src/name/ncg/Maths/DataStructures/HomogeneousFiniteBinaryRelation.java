package name.ncg.Maths.DataStructures;

import java.util.TreeSet;
@SuppressWarnings("serial")
public class HomogeneousFiniteBinaryRelation<L extends Comparable<? super L>>
extends FiniteBinaryRelation<L, L> { 
  
  public HomogeneousFiniteBinaryRelation() {
    super();
  }
  public HomogeneousFiniteBinaryRelation(FiniteBinaryRelation<L,L> rel) {
    super(rel);
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
  
}