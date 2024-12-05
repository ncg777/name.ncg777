package name.ncg777.maths.music.pcs12;

import java.util.TreeSet;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import name.ncg777.computing.structures.CollectionUtils;
import name.ncg777.computing.structures.JaggedList;
import name.ncg777.maths.music.pcs12.relations.NNotesDifference;
import name.ncg777.maths.relations.Relation;

public class Utils {

  public static JaggedList<Pcs12> randomChordTree(TreeSet<Pcs12> t, Integer[] n, int nb) {
    Pcs12 i = CollectionUtils.chooseAtRandom(t);
    
    Predicate<Pcs12> p = Relation.bindFirst(i, new NNotesDifference(1));
    return randomChordTreeSub(null, i, n, t, p, nb);

  }

  private static JaggedList<Pcs12> randomChordTreeSub(JaggedList<Pcs12> parent, Pcs12 z, Integer[] n,
      TreeSet<Pcs12> t0, Predicate<Pcs12> p, int nb) {
    JaggedList<Pcs12> o = new JaggedList<Pcs12>(z, parent);

    int x = 0;
    Integer[] n2 = new Integer[0];

    if (n.length != 0) {
      x = n[0];
      n2 = new Integer[n.length - 1];
      for (int i = 1; i < n.length; i++) {
        n2[i - 1] = n[i];
      }
    }

    TreeSet<Pcs12> t = new TreeSet<Pcs12>();
    t.addAll(t0);
    CollectionUtils.filter(t, p);
    t.remove(z);

    Pcs12[] cs = new Pcs12[x];

    for (int i = 0; i < x; i++) {
      if (t.isEmpty()) {
        throw new RuntimeException("Ran out of chords.");
      }

      Pcs12 c = CollectionUtils.chooseAtRandom(t);
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
