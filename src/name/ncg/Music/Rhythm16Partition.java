package name.ncg.Music;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.collections4.list.UnmodifiableList;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;

import name.ncg.Maths.Combination;
import name.ncg.Maths.DataStructures.IterableComparator;
import name.ncg.Maths.DataStructures.Sequence;
import name.ncg.Maths.Enumerations.BitSetEnumeration;
import name.ncg.Maths.Enumerations.FixedSetPartitionEnumeration;
import name.ncg.Music.RhythmPredicates.BypassPlain;
import name.ncg.Music.RhythmPredicates.NoRepeat;
import name.ncg.Music.RhythmPredicates.PredicatedPartition;
import name.ncg.Music.RhythmPredicates.ShadowContourIsomorphic;
import name.ncg.Music.RhythmPredicates.WellDistributed;

public class Rhythm16Partition extends ArrayList<Rhythm16> implements Comparable<Rhythm16Partition>, Serializable{

  private Rhythm16 rh;
  private Integer[] partition;
  public Integer[] getPartition(){return partition;}
  private List<Rhythm16> rhythms;
  /**
   * 
   */
  private static final long serialVersionUID = 5416420842118737878L;

  public static HashSet<Rhythm16Partition> generate(){
    HashSet<Rhythm16Partition> o = new HashSet<Rhythm16Partition>();
  
    
    Predicate<Rhythm> r = new BypassPlain();

    @SuppressWarnings("unchecked")
    Predicate<Rhythm16Partition> pred = 
    Predicates.and(new NoRepeat(), new WellDistributed(),
      new PredicatedPartition(new ShadowContourIsomorphic()));
    
    BitSetEnumeration b = new BitSetEnumeration(16);
    int lower = 2;
    int upper = 4;
    long k = 0;
    long total = 65536;
    long count = 0;
    while(b.hasMoreElements()){
      System.out.println(Double.toString(100.0*(double)k++/(double)total) + " %");
      Rhythm16 rh = Rhythm16.identifyRhythm16(new Combination(b.nextElement(),16));
      
      if(!r.apply(rh)){continue;}
      
      if(rh.getPhase() == 0){
        
        for(int i=lower;i<=upper;i++){
          if(i>rh.getK()){continue;}
          FixedSetPartitionEnumeration fspe = new FixedSetPartitionEnumeration(rh.getK(), i);
          
          while(fspe.hasMoreElements()){
            
            Integer[] p = fspe.nextElement();
            
            Rhythm16Partition rp = new Rhythm16Partition(rh,p);
            
            if(pred.apply(rp)){
              o.add(rp);
              
              do{
                Sequence s = new Sequence(p);
                Sequence s0 = s.mapWithNextPermutation();
                if(s0 == null){p = null;}
                else{
                  p = s0.getArray();
                  System.out.println(++count);
                  o.add(new Rhythm16Partition(rh,p));
                }
              } while(p!=null);
            }
          }
        }
      }
    }
    return o;
  }
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + java.util.Arrays.hashCode(partition);
    result = prime * result + ((rh == null) ? 0 : rh.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    Rhythm16Partition other = (Rhythm16Partition) obj;
    if (!java.util.Arrays.equals(partition, other.partition)) return false;
    if (rh == null) {
      if (other.rh != null) return false;
    } else if (!rh.equals(other.rh)) return false;
    return true;
  }
  public Rhythm16Partition(String r, String p){
    this.rh = Rhythm16.parseRhythm16Hex(r);
    Integer[] p0 = new Integer[rh.getK()];
    int k = 0;
    for(String s : Splitter.on(' ').split(p)){
      p0[k++] = Integer.valueOf(s);
    }
    this.partition = p0;
    init();
  }
  
  private void init(){
    this.rhythms = new ArrayList<Rhythm16>();
    Combination[] c = rh.partition(partition);

    for(Combination x : c){
      this.rhythms.add(Rhythm16.identifyRhythm16(x));
    }

  }
  
  public Rhythm16Partition nextPermutation(){
    Sequence s = new Sequence(partition);
    Sequence s1 = s.mapWithNextPermutation();
    if(s1 == null) { return null; }
    return new Rhythm16Partition(rh,s1);
  }
  
  public Rhythm16Partition(Rhythm16 r, Sequence partition){
    this(r,partition.getArray());
  }
  
  public Rhythm16Partition(Rhythm16 r, Integer[] partition){
    this.partition = partition;
    this.rh = r;
    init();
  }
  
  public int getN(){return rhythms.size();}
  public List<Rhythm16> getRhythms(){
    return new UnmodifiableList<Rhythm16>(rhythms);
  }
  public Rhythm16 getRhythm(){
    return Rhythm16.identifyRhythm16(new Combination(rh));
  }
    
  public String toString(){
    return rh.toString() + ";" + Joiner.on(",").join(partition);
  }
  public static Rhythm16Partition fromString(String s){
    String[] s0 = s.trim().split(";");
    Rhythm16 r = Rhythm16.parseRhythm16Hex(s0[0].trim());
    List<String> list0 = Splitter.on(",").splitToList(",");
    Integer[] p = new Integer[list0.size()];
    for(int i=0;i<list0.size();i++){
      p[i] = Integer.parseInt(list0.get(i));
    }
    return new Rhythm16Partition(r,p);
  }
  @Override
  public int compareTo(Rhythm16Partition arg0) {
    int k = rh.compareTo(arg0.rh);
    if(k != 0){
      return k;
    };
    
    k = IterableComparator.compare(
      Arrays.asList(this.partition).iterator(), 
      Arrays.asList(arg0.partition).iterator());
    
    if(k != 0){
      return k;
    };
    
    return 0;
  }
}
