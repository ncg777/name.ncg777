package name.NicolasCoutureGrenier.CS;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.math3.util.Precision;

/**
 * The {@code Backtracker} class implements a backtracking algorithm for searching through
 * combinatorial problems, allowing for the evaluation of solutions based on a provided 
 * scoring function. It can be configured to either minimize or maximize the score based 
 * on the criteria specified during instantiation.
 * 
 * <p>This class uses two main functional interfaces:</p>
 * <ul>
 * <li>{@code next_function}: A function that, given a current state, provides a list of 
 * possible next states.</li>
 * <li>{@code evaluation_function}: A function that evaluates a given state and returns a 
 * corresponding score (double).</li>
 * </ul>
 * 
 * <p>The backtracking algorithm explores all possible states by recursively traversing through 
 * the state space defined by the {@code next_function}. It retains the best states based on 
 * evaluation scores according to the optimization direction (minimization or maximization).</p>
 * 
 * <p>Usage example:</p>
 * <pre>
 * Backtracker<MyStateType> backtracker = Backtracker.Minimizer(
 *     state -> getNextStates(state),
 *     state -> evaluateScore(state));
 * 
 * List<MyStateType> optimalStates = backtracker.backtrack(initialState);
 * </pre>
 * 
 * @param <T> The type representing the state in the backtracking process.
 */
public class Backtracker<T> {

  private Function<T, List<T>> next_function;
  private Function<T, Double> evaluation_function;

  private static enum MIN_OR_MAX {
    MIN, MAX
  };

  private MIN_OR_MAX min_or_max;

  public static <T> Backtracker<T> Minimizer(Function<T, List<T>> next_function,
      Function<T, Double> evaluation_function) {
    return new Backtracker<T>(next_function, evaluation_function, MIN_OR_MAX.MIN);
  }

  public static <T> Backtracker<T> Maximizer(Function<T, List<T>> next_function,
      Function<T, Double> evaluation_function) {
    return new Backtracker<T>(next_function, evaluation_function, MIN_OR_MAX.MAX);
  }

  public List<T> backtrack(T start) {
    List<T> o = new ArrayList<T>();
    backtrack(start, o);
    return o;
  }

  public Double backtrack(T start, List<T> out_list) {
    double score = Precision.round(evaluation_function.apply(start), 8);

    List<T> n = next_function.apply(start);

    if (n == null || n.size() == 0) {
      out_list.add(start);
      return score;
    }
    double[] scores = new double[n.size()];
    double opt = score;

    for (int i = 0; i < n.size(); i++) {
      scores[i] = Precision.round(evaluation_function.apply(n.get(i)), 8);

      if ((min_or_max == MIN_OR_MAX.MAX && scores[i] > opt)
          || (min_or_max == MIN_OR_MAX.MIN && scores[i] < opt)) {
        opt = scores[i];
      }
    }
    if (opt == score) {
      out_list.add(start);
      return score;
    }

    Double scores_opt[] = new Double[n.size()];
    List<List<T>> l2 = new ArrayList<List<T>>();

    for (int i = 0; i < n.size(); i++) {
      l2.add(new ArrayList<T>());
      if (scores[i] == opt) {
        scores_opt[i] = backtrack(n.get(i), l2.get(i));
        if (scores_opt[i] != null && (min_or_max == MIN_OR_MAX.MAX && scores_opt[i] > opt)
            || (min_or_max == MIN_OR_MAX.MIN && scores_opt[i] < opt)) {
          opt = scores_opt[i];
        }

      }
    }
    for (int i = 0; i < n.size(); i++) {
      if (scores_opt[i] != null && scores_opt[i] == opt) {
        out_list.addAll(l2.get(i));
      }
    }
    return opt;

  }

  private Backtracker(Function<T, List<T>> next_function, Function<T, Double> evaluation_function,
      MIN_OR_MAX m) {
    if (m == null || next_function == null || evaluation_function == null) {
      throw new IllegalArgumentException();
    }
    this.next_function = next_function;
    this.evaluation_function = evaluation_function;
    this.min_or_max = m;

  }

}
