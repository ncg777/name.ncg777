package name.ncg777.Music.RhythmRelations;

import java.util.BitSet;
import java.util.TreeSet;

import name.ncg777.Maths.Relations.Relation;
import name.ncg777.Music.Rhythm;
import name.ncg777.Music.Rhythm48;


public class CompatibleRhythms implements 
Relation<Rhythm, Rhythm>   {
  
  @Override
  public boolean apply(Rhythm a, Rhythm b) {
    int l = a.getN()/4;
    
    for(int i=0;i<4;i++) {
      BitSet ba = new BitSet(l);
      BitSet bb = new BitSet(l);
      for(int j=0;j<l;j++) {
        ba.set(j, a.get((i*l) +j));
        bb.set(j, b.get((i*l) +j));
      }
      if(!sub(Rhythm.buildRhythm(ba,l),Rhythm.buildRhythm(bb,l))) return false;
    }
    return true;
  }
  private TreeSet<Rhythm> valid3 = Rhythm48.getValid3Beats();
  private TreeSet<Rhythm> valid4 = Rhythm48.getValid4Beats();
  
  private boolean sub(Rhythm a, Rhythm b) {
    boolean a3 = valid3.contains(a);
    boolean a4 = valid4.contains(a);
    boolean b3 = valid3.contains(b);
    boolean b4 = valid4.contains(b);
    
    if(a3 && b3) return true;
    if(a4 && b4) return true;
    
    return false;
  }
}
