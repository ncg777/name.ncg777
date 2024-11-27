package name.ncg777.musical;

import java.util.TreeSet;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import name.ncg777.computerScience.dataStructures.CollectionUtils;
import name.ncg777.computerScience.dataStructures.JaggedList;
import name.ncg777.mathematics.relations.Relation;
import name.ncg777.musical.pitchClassSet12Relations.NNotesDifference;

public class PCS12Utils {

  public static JaggedList<pitchClassSet12> randomChordTree(TreeSet<pitchClassSet12> t, Integer[] n, int nb) {
    pitchClassSet12 i = CollectionUtils.chooseAtRandom(t);
    
    Predicate<pitchClassSet12> p = Relation.bindFirst(i, new NNotesDifference(1));
    return randomChordTreeSub(null, i, n, t, p, nb);

  }

  private static JaggedList<pitchClassSet12> randomChordTreeSub(JaggedList<pitchClassSet12> parent, pitchClassSet12 z, Integer[] n,
      TreeSet<pitchClassSet12> t0, Predicate<pitchClassSet12> p, int nb) {
    JaggedList<pitchClassSet12> o = new JaggedList<pitchClassSet12>(z, parent);

    int x = 0;
    Integer[] n2 = new Integer[0];

    if (n.length != 0) {
      x = n[0];
      n2 = new Integer[n.length - 1];
      for (int i = 1; i < n.length; i++) {
        n2[i - 1] = n[i];
      }
    }

    TreeSet<pitchClassSet12> t = new TreeSet<pitchClassSet12>();
    t.addAll(t0);
    CollectionUtils.filter(t, p);
    t.remove(z);

    pitchClassSet12[] cs = new pitchClassSet12[x];

    for (int i = 0; i < x; i++) {
      if (t.isEmpty()) {
        throw new RuntimeException("Ran out of chords.");
      }

      pitchClassSet12 c = CollectionUtils.chooseAtRandom(t);
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
