package name.ncg.Maths.FuzzyLogic;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.common.base.Function;

public class FuzzySet<T extends Comparable<? super T>> {
  private TreeMap<T, FuzzyVariable> membership;

  public FuzzySet() {
    membership = new TreeMap<T, FuzzyVariable>();
  }

  public FuzzySet<T> normalize() {
    double min = Double.MAX_VALUE;
    double max = Double.MIN_NORMAL;
    FuzzySet<T> output = new FuzzySet<T>(membership.keySet());
    Iterator<FuzzyVariable> i = membership.values().iterator();
    while (i.hasNext()) {
      double v = i.next().getValue();
      if (v < min) {
        min = v;
      }
      if (v > max) {
        max = v;
      }
    }
    Iterator<T> j = membership.keySet().iterator();

    while (j.hasNext()) {
      T v = j.next();
      output.setMembership(v, (this.getMembership(v).getValue() - min) / (max - min));

    }
    return output;
  }

  public FuzzySet(Set<T> universe) {
    this();
    for (T v : universe) {
      membership.put(v, FuzzyVariable.from(1.0));
    }
  }

  public FuzzySet(TreeSet<T> universe, Function<? super T, FuzzyVariable> valuator) {
    this();
    for (T v : universe) {
      membership.put(v, valuator.apply(v));
    }
  }

  public Iterator<Entry<T, FuzzyVariable>> iterator() {
    return membership.entrySet().iterator();
  }

  public void add(T v, FuzzyVariable m) {
    add(v, m.getValue());
  }

  public void add(T v, double m) {
    setMembership(v, m);
  }

  public void setMembership(T v, FuzzyVariable a) {
    setMembership(v, a.getValue());
  }

  public void setMembership(T v, double m) {
    membership.put(v, FuzzyVariable.from(m));
  }

  public FuzzyVariable getMembership(T v) {
    if (!membership.keySet().contains(v)) {
      throw new RuntimeException("FuzzySet: Unknown element.");
    }
    return membership.get(v);
  }

  public double getMagnitude() {
    Double m = 0.0;
    for (FuzzyVariable d : membership.values()) {
      m += d.getValue();
    }
    return m;
  }

  public double getOverlap() {
    return this.cap(this.complement()).getMagnitude();
  }

  public double getUnderlap() {
    return this.cup(this.complement()).getMagnitude();
  }

  public double getEntropy() {
    return this.getOverlap() / this.getUnderlap();
  }

  public FuzzySet<T> cup(FuzzySet<T> second) {
    return FuzzySet.cup(this, second);
  }

  public FuzzySet<T> cap(FuzzySet<T> second) {
    return FuzzySet.cap(this, second);
  }

  public FuzzySet<T> complement() {
    return FuzzySet.complement(this);
  }

  public TreeSet<T> quantize() {
    return FuzzySet.quantize(this);
  }

  public double subsethood(FuzzySet<T> second) {
    return FuzzySet.subsethood(this, second);
  }

  public static <T extends Comparable<? super T>> double subsethood(FuzzySet<T> first,
      FuzzySet<T> second) {
    return first.cap(second).getMagnitude() / first.getMagnitude();
  }

  public static <T extends Comparable<? super T>> FuzzySet<T> cup(FuzzySet<T> first,
      FuzzySet<T> second) {
    FuzzySet<T> o = new FuzzySet<T>();
    TreeSet<T> values = new TreeSet<T>();
    values.addAll(first.membership.keySet());
    values.addAll(second.membership.keySet());
    for (T v : values) {
      o.add(v, FuzzyVariable.or(first.getMembership(v), second.getMembership(v)));
    }
    return o;
  }

  public static <T extends Comparable<? super T>> FuzzySet<T> cap(FuzzySet<T> first,
      FuzzySet<T> second) {
    FuzzySet<T> o = new FuzzySet<T>();
    TreeSet<T> values = new TreeSet<T>();
    values.addAll(first.membership.keySet());
    values.addAll(second.membership.keySet());
    for (T v : values) {
      o.add(v, FuzzyVariable.and(first.getMembership(v), second.getMembership(v)));
    }
    return o;
  }

  public static <T extends Comparable<? super T>> FuzzySet<T> complement(FuzzySet<T> f) {
    FuzzySet<T> o = new FuzzySet<T>();
    for (Entry<T, FuzzyVariable> e : f.membership.entrySet()) {
      o.add(e.getKey(), e.getValue().not());
    }
    return o;
  }

  public static <T extends Comparable<? super T>> TreeSet<T> quantize(FuzzySet<T> f) {
    TreeSet<T> output = new TreeSet<T>();
    for (Entry<T, FuzzyVariable> e : f.membership.entrySet()) {
      if (e.getValue().quantize()) {
        output.add(e.getKey());
      }
    }
    return output;
  }


}
