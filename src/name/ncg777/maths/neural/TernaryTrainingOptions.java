package name.ncg777.maths.neural;

import java.util.Objects;

/** Immutable limits and optimizer settings for a fully visible ternary network. */
public record TernaryTrainingOptions(
    Mode mode, int epochs, double learningRate, double beta, double l2,
    double maxAbsParameter, int maxExactStates, int particles, int sweeps,
    int burnIn, long seed, long maxWork, int patience, double minImprovement) {
  public enum Mode { EXACT, PERSISTENT_GIBBS }

  public TernaryTrainingOptions {
    Objects.requireNonNull(mode);
    if (epochs < 1 || epochs > 10000 || particles < 1 || particles > 4096
        || sweeps < 1 || sweeps > 10000 || burnIn < 0 || burnIn > 10000
        || maxExactStates < 1 || maxExactStates > 19683 || maxWork < 1
        || patience < 1 || patience > 10000) {
      throw new IllegalArgumentException("Training counts or limits out of range");
    }
    positive(learningRate, "learningRate");
    positive(beta, "beta");
    positive(maxAbsParameter, "maxAbsParameter");
    nonnegative(l2, "l2");
    nonnegative(minImprovement, "minImprovement");
  }

  private static void positive(double value, String name) {
    if (!Double.isFinite(value) || value <= 0) throw new IllegalArgumentException(name + " must be finite and positive");
  }

  private static void nonnegative(double value, String name) {
    if (!Double.isFinite(value) || value < 0) throw new IllegalArgumentException(name + " must be finite and nonnegative");
  }

  public static Builder builder() { return new Builder(); }

  public static final class Builder {
    private Mode mode = Mode.EXACT;
    private int epochs = 200, maxExactStates = 6561, particles = 128, sweeps = 2, burnIn = 50, patience = 20;
    private double learningRate = 0.05, beta = 1, l2 = 0.001, maxAbsParameter = 5, minImprovement = 1e-6;
    private long seed = 777, maxWork = 100_000_000;

    public Builder mode(Mode value) { mode = value; return this; }
    public Builder epochs(int value) { epochs = value; return this; }
    public Builder learningRate(double value) { learningRate = value; return this; }
    public Builder beta(double value) { beta = value; return this; }
    public Builder l2(double value) { l2 = value; return this; }
    public Builder maxAbsParameter(double value) { maxAbsParameter = value; return this; }
    public Builder maxExactStates(int value) { maxExactStates = value; return this; }
    public Builder particles(int value) { particles = value; return this; }
    public Builder sweeps(int value) { sweeps = value; return this; }
    public Builder burnIn(int value) { burnIn = value; return this; }
    public Builder seed(long value) { seed = value; return this; }
    public Builder maxWork(long value) { maxWork = value; return this; }
    public Builder patience(int value) { patience = value; return this; }
    public Builder minImprovement(double value) { minImprovement = value; return this; }

    public TernaryTrainingOptions build() {
      return new TernaryTrainingOptions(mode, epochs, learningRate, beta, l2, maxAbsParameter,
          maxExactStates, particles, sweeps, burnIn, seed, maxWork, patience, minImprovement);
    }
  }
}
