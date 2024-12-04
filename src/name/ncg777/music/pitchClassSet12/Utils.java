package name.ncg777.music.pitchClassSet12;

import java.util.TreeSet;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import name.ncg777.computerScience.dataStructures.CollectionUtils;
import name.ncg777.computerScience.dataStructures.JaggedList;
import name.ncg777.maths.relations.Relation;
import name.ncg777.music.pitchClassSet12.relations.NNotesDifference;

public class Utils {

  public static JaggedList<PitchClassSet12> randomChordTree(TreeSet<PitchClassSet12> t, Integer[] n, int nb) {
    PitchClassSet12 i = CollectionUtils.chooseAtRandom(t);
    
    Predicate<PitchClassSet12> p = Relation.bindFirst(i, new NNotesDifference(1));
    return randomChordTreeSub(null, i, n, t, p, nb);

  }

  private static JaggedList<PitchClassSet12> randomChordTreeSub(JaggedList<PitchClassSet12> parent, PitchClassSet12 z, Integer[] n,
      TreeSet<PitchClassSet12> t0, Predicate<PitchClassSet12> p, int nb) {
    JaggedList<PitchClassSet12> o = new JaggedList<PitchClassSet12>(z, parent);

    int x = 0;
    Integer[] n2 = new Integer[0];

    if (n.length != 0) {
      x = n[0];
      n2 = new Integer[n.length - 1];
      for (int i = 1; i < n.length; i++) {
        n2[i - 1] = n[i];
      }
    }

    TreeSet<PitchClassSet12> t = new TreeSet<PitchClassSet12>();
    t.addAll(t0);
    CollectionUtils.filter(t, p);
    t.remove(z);

    PitchClassSet12[] cs = new PitchClassSet12[x];

    for (int i = 0; i < x; i++) {
      if (t.isEmpty()) {
        throw new RuntimeException("Ran out of chords.");
      }

      PitchClassSet12 c = CollectionUtils.chooseAtRandom(t);
      t.remove(c);
      cs[i] = c;

    }

    for (int i = 0; i < x; i++) {
      o.addChild(randomChordTreeSub(o, cs[i], n2, t,
          Predicates.and(p, 
              Relation.bindFirst(cs[i], new NNotesDifference(1))
              ),
          nb));
    }

    return o;

  }



}
