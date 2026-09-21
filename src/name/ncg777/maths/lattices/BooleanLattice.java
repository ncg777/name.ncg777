package name.ncg777.maths.lattices;

/** Transforms of real functions on subsets, indexed by bit masks. */
public final class BooleanLattice {
  private BooleanLattice() {}

  /** Returns g(S) = sum over T subset of S of f(T), in O(n 2^n) time. */
  public static double[] zetaTransform(double[] values) {
    return transform(values, 1);
  }

  /** Inverts the subset zeta transform by inclusion-exclusion. */
  public static double[] mobiusTransform(double[] values) {
    return transform(values, -1);
  }

  private static double[] transform(double[] values, int sign) {
    if (values.length == 0 || (values.length & (values.length - 1)) != 0) {
      throw new IllegalArgumentException("Array length must be a nonzero power of two");
    }
    double[] result = values.clone();
    for (double value : result) {
      if (!Double.isFinite(value)) throw new IllegalArgumentException("Values must be finite");
    }
    for (int bit = 1; bit < result.length; bit <<= 1) {
      for (int mask = 0; mask < result.length; mask++) {
        if ((mask & bit) != 0) {
          result[mask] += sign * result[mask ^ bit];
          if (!Double.isFinite(result[mask])) throw new ArithmeticException("Transform overflow");
        }
      }
    }
    return result;
  }
}
