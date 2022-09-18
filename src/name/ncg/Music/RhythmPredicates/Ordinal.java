package name.ncg.Music.RhythmPredicates;

import java.util.BitSet;
import java.util.TreeSet;

import com.google.common.base.Predicate;

import name.ncg.Maths.Combination;
import name.ncg.Maths.Numbers;
import name.ncg.Music.Rhythm;

public class Ordinal implements Predicate<Rhythm>  {
  private int n = -1;
  TreeSet<Combination> words = new TreeSet<Combination>();
  public Ordinal(int n) {
    if(n < 2) throw new RuntimeException("Ordinal::Invalid n");
    this.n=n;
    TreeSet<Integer> factors = Numbers.factors(n);
    // add the empty rhythm
    words.add(new Combination(new BitSet(n), this.n));
    for(int f : factors) {
      int k = n / f;
      
      for(int i=1; i<=k; i++) {
        BitSet b = new BitSet(n);
        for(int j=0; j<i; j++) {
          b.set(j*f, true);
        }
        words.add(new Combination(b, this.n));
      }
    }
  }
  
  @Override
  public boolean apply(Rhythm input) {
    if(input.getN() % this.n != 0) throw new RuntimeException("Ordinal:: rhythm is incompatible with n");
    
    int k = input.getN()/this.n;
    BitSet b = new BitSet(this.n);
    for(int i=0; i<k; i++) {
      b.clear();
      for(int j=0;j<this.n;j++) {
        b.set(j, input.get((i*this.n)+j));
      }
      if(!this.words.contains(new Combination(b, this.n))) return false;
    }
    return true;
  }

}
