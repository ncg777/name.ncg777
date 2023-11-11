package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.common.base.Equivalence;

/**
 * 
 * This class is crap... Sorry world.
 *
 * @param <T>
 */
public class UnionSet<T extends Comparable<? super T>> {

  private ArrayList<T> representants = new ArrayList<>();
  private TreeMap<T,TreeSet<T>> instances = new TreeMap<>();
  private Equivalence<T> equivalence;
  
  public UnionSet(Equivalence<T> equivalence) {
    this.equivalence = equivalence;
  }
  /**
   * Adds item to union set.
   * 
   * @param item
   */
  public void add(T item) {
    boolean found = false;
    for(T r : representants) {
      if(this.equivalence.equivalent(r, item)) {
        found=true;
        instances.get(r).add(item);
      }
    }
    if(!found) {
      TreeSet<T> inst = new TreeSet<>();
      inst.add(item);
      instances.put(item, inst);
      representants.add(item);
    }
  }
  
  /**
   * Obtains representant of item or null if it does not exist.
   * 
   * @param item
   * @return The representant or null if there isn't any.
   */
  public T getRepresentant(T item) {
    for(T r : representants) {
      if(this.equivalence.equivalent(r, item)) {
        return r;
      }
    }
    return null;
  }
  
  /**
   * Removes the item from the union set
   * 
   * @param item
   * @return True if item was found and deleted.
   */
  public boolean remove(T item) {
    boolean found = false;
    boolean removeRep = false;
    T rep = null;
    for(T r : representants) {
      if(this.equivalence.equivalent(r, item)) {
        found=true;
        instances.get(r).remove(item);
        
        if(instances.get(r).size() == 0) {
          instances.remove(r);
          removeRep = true;
          rep = r;
        }
        break;
      }
    }
    if(removeRep) representants.remove(rep);
    return found;
  }
  
  public ArrayList<T> getRepresentants(){return representants;}
  public ArrayList<TreeSet<T>> getTreeSets() {
    ArrayList<TreeSet<T>> o = new ArrayList<>();
    o.addAll(instances.values());
    return o;
  }
}