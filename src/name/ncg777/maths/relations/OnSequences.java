package name.ncg777.maths.relations;

import javax.annotation.Nonnull;

import com.google.common.base.Equivalence;

import name.ncg777.maths.objects.Sequence;

public class OnSequences {
  public static Relation<Sequence,Sequence> equivalentUnderRotation = 
      Relation.fromBiPredicate((a,b) -> Sequence.equivalentUnderRotation(a, b));
  public static Relation<Sequence,Sequence> equivalentUnderRotationOrSymmetry = 
      Relation.fromBiPredicate((a,b) -> Sequence.equivalentUnderRotation(a, b) 
        || Sequence.equivalentUnderRotation(a, b.reverse())); 
  
  public static Relation<Sequence,Sequence> lessThan = new Relation<Sequence,Sequence>()  {
    @Override
    public boolean apply(Sequence a, Sequence b) {
      if(a.size()!=b.size() || a.size()==0) return false;
      
      int n = a.size();
      
      for(int i=0;i<n;i++) {
        if(b.get(i) < a.get(i)) return false;
      }
      return !a.equals(b);
    }
  };
  
  /**
   * This equivalence states that 2 sequences s1 and s2 are considered equal if there exist a
   * bijective mapping m and an integer r such that s2 = rotate(m(s1), r)
   * 
   * @author Nicolas Couture-Grenier
   * 
   */
  public static Equivalence<Sequence> upToIsomorphismAndRotation = new Equivalence<Sequence>() {
    @Override
    protected boolean doEquivalent(@Nonnull Sequence a, @Nonnull Sequence b) {
      if (a.size() != b.size() || a.distinct().size() != b.distinct().size()) {
        return false;
      }

      int n = a.size();

      for (int i = 0; i < n; i++) {
        if (a.existsMapTo(b.rotate(i))) {
          return true;
        }
      }

      return false;
    }

    @Override
    protected int doHash(@Nonnull Sequence t) {
      return t.distinct().size() * 3 + 5 * t.size();
    }

  };
}