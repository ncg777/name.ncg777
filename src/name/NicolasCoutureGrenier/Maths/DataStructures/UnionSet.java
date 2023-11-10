package name.NicolasCoutureGrenier.Maths.DataStructures;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.common.base.Equivalence;

/**
 * Not really standard union set...
 *
 * @param <T>
 */
public class UnionSet<T> {

  private TreeMap<Integer,T> representants = new TreeMap<>();
  private TreeMap<Integer,TreeSet<T>> instances = new TreeMap<>();
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
    int h = equivalence.hash(item);
    boolean found = instances.containsKey(h);
    if(found) {
      instances.get(h).add(item);
    } else {
      TreeSet<T> inst = new TreeSet<>();
      inst.add(item);
      instances.put(h, inst);
      representants.put(h, item);
    }
  }
  
  /**
   * Obtains representant of item or null if it does not exist.
   * 
   * @param item
   * @return The representant or null if there isn't any.
   */
  public T getRepresentant(T item) {
    return representants.get(equivalence.hash(item));
  }
  
  /**
   * Removes the item from the union set
   * 
   * @param item
   * @return True if item was found and deleted.
   */
  public boolean remove(T item) {
    var h = equivalence.hash(item);
    var inst = instances.get(h);
    if(inst == null) {
      return false;
    } else {
      if(inst.remove(item)) {
        if(inst.size() == 0) {
          representants.remove(h);
          instances.remove(h);
        }
        return true;
      } else {
        return false;
      }
    }
  }
  
  public ArrayList<T> getRepresentants(){
    ArrayList<T> o = new ArrayList<>();
    o.addAll(representants.values());
    return o;
  }
  public ArrayList<TreeSet<T>> getTreeSets() {
    ArrayList<TreeSet<T>> o = new ArrayList<>();
    o.addAll(instances.values());
    return o;
  }
}
