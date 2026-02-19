package name.ncg777.maths.numbers.fixed.rhythm;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import name.ncg777.maths.numbers.BinaryNatural;
import name.ncg777.maths.numbers.predicates.CoprimeIntervals;
import name.ncg777.maths.numbers.predicates.DuplePartitioned;
import name.ncg777.maths.numbers.predicates.EntropicDispersion;
import name.ncg777.maths.numbers.predicates.Even;
import name.ncg777.maths.numbers.predicates.HasNoGaps;
import name.ncg777.maths.numbers.predicates.HighDispersion;
import name.ncg777.maths.numbers.predicates.LowDispersion;
import name.ncg777.maths.numbers.predicates.LowEntropy;
import name.ncg777.maths.numbers.predicates.MaximumGap;
import name.ncg777.maths.numbers.predicates.MinimumGap;
import name.ncg777.maths.numbers.predicates.Oddity;
import name.ncg777.maths.numbers.predicates.Ordinal; // parameterised: ORDINAL(n)
import name.ncg777.maths.numbers.predicates.Periodic;
import name.ncg777.maths.numbers.predicates.RelativelyFlat;
import name.ncg777.maths.numbers.predicates.ShadowContourIsomorphic;

/**
 * Registry of named predicates applicable to {@link BinaryNatural} instances (rhythms).
 *
 * <p>Predicate names are <strong>case-insensitive</strong>.  Parameterised predicates accept an
 * integer argument in parentheses, e.g. {@code MINIMUM_GAP(2)} or {@code MAXIMUM_GAP(5)}.
 *
 * <p>Available predicate names:
 * <ul>
 *   <li>EVEN</li>
 *   <li>ENTROPIC  (alias ENTROPIC_DISPERSION)</li>
 *   <li>HAS_NO_GAPS</li>
 *   <li>HIGH_DISPERSION</li>
 *   <li>LOW_DISPERSION</li>
 *   <li>LOW_ENTROPY</li>
 *   <li>ODDITY</li>
 *   <li>PERIODIC</li>
 *   <li>RELATIVELY_FLAT</li>
 *   <li>ORDINAL</li>
 *   <li>COPRIME_INTERVALS</li>
 *   <li>DUPLE_PARTITIONED</li>
 *   <li>SHADOW_CONTOUR_ISOMORPHIC</li>
 *   <li>MINIMUM_GAP(n)</li>
 *   <li>MAXIMUM_GAP(n)</li>
 * </ul>
 */
public final class RhythmPredicateRegistry {

  /** Nullary (zero-argument) predicates stored by upper-cased name. */
  private static final Map<String, Predicate<BinaryNatural>> NULLARY;

  static {
    Map<String, Predicate<BinaryNatural>> m = new LinkedHashMap<>();
    m.put("EVEN",               new Even());
    m.put("ENTROPIC",           new EntropicDispersion());
    m.put("ENTROPIC_DISPERSION",new EntropicDispersion());
    m.put("HAS_NO_GAPS",        new HasNoGaps());
    m.put("HIGH_DISPERSION",    new HighDispersion());
    m.put("LOW_DISPERSION",     new LowDispersion());
    m.put("LOW_ENTROPY",        new LowEntropy());
    m.put("ODDITY",             new Oddity());
    m.put("PERIODIC",           new Periodic());
    m.put("RELATIVELY_FLAT",    new RelativelyFlat());
    m.put("COPRIME_INTERVALS",         new CoprimeIntervals());
    m.put("DUPLE_PARTITIONED",          new DuplePartitioned());
    m.put("SHADOW_CONTOUR_ISOMORPHIC",  new ShadowContourIsomorphic());
    NULLARY = Collections.unmodifiableMap(m);
  }

  private RhythmPredicateRegistry() {}

  /**
   * Looks up a predicate by name (and optional integer argument).
   *
   * @param name  predicate name, case-insensitive, e.g. {@code "EVEN"} or {@code "MINIMUM_GAP"}
   * @param arg   integer argument (ignored for nullary predicates; required for parameterised ones)
   * @return the matching {@link Predicate}
   * @throws IllegalArgumentException if the name is unknown
   */
  public static Predicate<BinaryNatural> get(String name, Integer arg) {
    String key = name.trim().toUpperCase();
    if (NULLARY.containsKey(key)) {
      return NULLARY.get(key);
    }
    if (key.equals("MINIMUM_GAP")) {
      if (arg == null) throw new IllegalArgumentException("MINIMUM_GAP requires an integer argument.");
      return new MinimumGap(arg);
    }
    if (key.equals("MAXIMUM_GAP")) {
      if (arg == null) throw new IllegalArgumentException("MAXIMUM_GAP requires an integer argument.");
      return new MaximumGap(arg);
    }
    if (key.equals("ORDINAL")) {
      if (arg == null) throw new IllegalArgumentException("ORDINAL requires an integer argument, e.g. ORDINAL(12).");
      return new Ordinal(arg);
    }
    throw new IllegalArgumentException("Unknown predicate name: '" + name + "'");
  }

  /** Convenience overload for nullary predicates. */
  public static Predicate<BinaryNatural> get(String name) {
    return get(name, null);
  }

  /** Returns an unmodifiable view of all registered nullary predicate names. */
  public static Set<String> names() {
    return NULLARY.keySet();
  }
}
