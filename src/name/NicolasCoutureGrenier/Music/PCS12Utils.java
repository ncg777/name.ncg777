package name.NicolasCoutureGrenier.Music;

import java.util.TreeSet;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import name.NicolasCoutureGrenier.Maths.DataStructures.CollectionUtils;
import name.NicolasCoutureGrenier.Maths.DataStructures.JaggedList;
import name.NicolasCoutureGrenier.Maths.Predicates.BoundRelationFirst;
import name.NicolasCoutureGrenier.Music.PCS12Relations.NNotesDifference;

public class PCS12Utils {

  public static JaggedList<PCS12> randomChordTree(TreeSet<PCS12> t, Integer[] n, int nb) {
    PCS12 i = CollectionUtils.chooseAtRandom(t);
    Predicate<PCS12> p = new BoundRelationFirst<PCS12, PCS12>(i, new NNotesDifference(1));
    return randomChordTreeSub(null, i, n, t, p, nb);

  }

  private static JaggedList<PCS12> randomChordTreeSub(JaggedList<PCS12> parent, PCS12 z, Integer[] n,
      TreeSet<PCS12> t0, Predicate<PCS12> p, int nb) {
    JaggedList<PCS12> o = new JaggedList<PCS12>(z, parent);

    int x = 0;
    Integer[] n2 = new Integer[0];

    if (n.length != 0) {
      x = n[0];
      n2 = new Integer[n.length - 1];
      for (int i = 1; i < n.length; i++) {
        n2[i - 1] = n[i];
      }
    }

    TreeSet<PCS12> t = new TreeSet<PCS12>();
    t.addAll(t0);
    CollectionUtils.filter(t, p);
    t.remove(z);

    PCS12[] cs = new PCS12[x];

    for (int i = 0; i < x; i++) {
      if (t.isEmpty()) {
        throw new RuntimeException("Ran out of chords.");
      }

      PCS12 c = CollectionUtils.chooseAtRandom(t);
      t.remove(c);
      cs[i] = c;

    }

    for (int i = 0; i < x; i++) {
      o.addChild(randomChordTreeSub(o, cs[i], n2, t,
          Predicates.and(p, new BoundRelationFirst<PCS12, PCS12>(cs[i], new NNotesDifference(1))),
          nb));
    }

    return o;

  }



}
