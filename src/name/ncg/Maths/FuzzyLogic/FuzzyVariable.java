package name.ncg.Maths.FuzzyLogic;


public class FuzzyVariable {
  private double value;

  public FuzzyVariable(double value) {
    if (value < 0.0 || value > 1.0) {
      throw new RuntimeException("FuzzyVariable : membership value out of range." + value);
    }
    this.value = value;
  }

  public double getValue() {
    return value;
  }

  public FuzzyVariable and(FuzzyVariable second) {
    return FuzzyVariable.and(this, second);
  }
  public FuzzyVariable nand(FuzzyVariable second) {
    return FuzzyVariable.nand(this, second);
  }
  public FuzzyVariable or(FuzzyVariable second) {
    return FuzzyVariable.or(this, second);
  }
  public FuzzyVariable nor(FuzzyVariable second) {
    return FuzzyVariable.nor(this, second);
  }
  public FuzzyVariable implies(FuzzyVariable second) {
    return FuzzyVariable.implies(this, second);
  }

  public FuzzyVariable equivalent_to(FuzzyVariable second) {
    return FuzzyVariable.equivalent_to(this, second);
  }
  public FuzzyVariable not_equivalent_to(FuzzyVariable second) {
    return FuzzyVariable.not_equivalent_to(this, second);
  }
  
//  public FuzzyVariable lessThan(FuzzyVariable second) {
//    return FuzzyVariable.lessThan(this, second);
//  }
//
//  public FuzzyVariable greaterThan(FuzzyVariable second) {
//    return FuzzyVariable.greaterThan(this, second);
//  }
//
//  public FuzzyVariable lessOrEqual(FuzzyVariable second) {
//    return FuzzyVariable.lessOrEqual(this, second);
//  }
//
//  public FuzzyVariable greaterOrEqual(FuzzyVariable second) {
//    return FuzzyVariable.greaterOrEqual(this, second);
//  }

  public FuzzyVariable not() {
    return FuzzyVariable.not(this);
  }

  public boolean quantize() {
    return FuzzyVariable.quantize(this);
  }
  public boolean quantize(double alpha) {
    return FuzzyVariable.quantize(this, alpha);
  }
  public boolean isEntropic(double delta) {
    return FuzzyVariable.isEntropic(this, delta);
  }
//  public static FuzzyVariable lessThan(FuzzyVariable a, FuzzyVariable b) {
//    return FuzzyVariable.from((a.value < b.value) ? 1.0 : 0.0);
//  }
//
//  public static FuzzyVariable lessOrEqual(FuzzyVariable a, FuzzyVariable b) {
//    return a.greaterThan(b).not();
//  }
//
//  public static FuzzyVariable greaterThan(FuzzyVariable a, FuzzyVariable b) {
//    return FuzzyVariable.from((a.value > b.value) ? 1.0 : 0.0);
//  }
//
//  public static FuzzyVariable greaterOrEqual(FuzzyVariable a, FuzzyVariable b) {
//    return a.lessThan(b).not();
//  }

  public static FuzzyVariable and(FuzzyVariable a, FuzzyVariable b) {
    return new FuzzyVariable(Math.min(a.value, b.value));
  }
  public static FuzzyVariable nand(FuzzyVariable a, FuzzyVariable b) {
    return a.and(b).not();
  }
  public static FuzzyVariable or(FuzzyVariable a, FuzzyVariable b) {
    return new FuzzyVariable(Math.max(a.value, b.value));
  }
  public static FuzzyVariable nor(FuzzyVariable a, FuzzyVariable b) {
    return a.or(b).not();
  }
  public static FuzzyVariable from(double value) {
    return new FuzzyVariable(value);
  }

  public static FuzzyVariable not(FuzzyVariable a) {
    if (a.value == 0.5) {
      return FuzzyVariable.from(0.5);
    } else {
      return new FuzzyVariable(1.0 - a.value);
    }
  }

//  public static FuzzyVariable implies(FuzzyVariable p, FuzzyVariable q) {
//    return p.lessOrEqual(q).or(q);
//  } // Godel implication

  public static FuzzyVariable implies(FuzzyVariable p, FuzzyVariable q) {
    return q.not().and(p).not();
  }

  public static FuzzyVariable equivalent_to(FuzzyVariable p, FuzzyVariable q) {
    return (p.and(q)).or(p.or(q).not());
  }
  public static FuzzyVariable not_equivalent_to(FuzzyVariable p, FuzzyVariable q) {
    return p.equivalent_to(q).not();
  }
  public static boolean quantize(FuzzyVariable a) {
    return quantize(a,0.5);
  }
  
  public static boolean quantize(FuzzyVariable a, double alpha) {
    return (a.value > alpha);
  }

  public static boolean isEntropic(FuzzyVariable a, double delta) {
    return Math.abs(a.value - 0.5) < delta;
  }
}
