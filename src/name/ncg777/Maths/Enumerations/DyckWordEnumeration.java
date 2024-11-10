package name.ncg777.Maths.Enumerations;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Adapted into an Enumeration from code by nl.dvberkel.
 */
public class DyckWordEnumeration implements Enumeration<String> {

  private static class State {
    private final State parent;
    public final String word;
    public final int index;

    public State(String word, int index) {
      this.parent = null;
      this.word = word;
      this.index = index;
    }

    public State(State parent, String word, int index) {

      this.parent = parent;
      this.word = word;
      this.index = index;
    }

    public State update(int index) {
      return new State(parent, word, index);
    }

    public State push(String word, int index) {
      return new State(this, word, index);
    }

    public State pop() {
      return parent;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      State state = (State) o;

      if (index != state.index) return false;
      if (parent != null ? !parent.equals(state.parent) : state.parent != null) return false;
      return word.equals(state.word);

    }

    @Override
    public int hashCode() {
      int result = parent != null ? parent.hashCode() : 0;
      result = 31 * result + word.hashCode();
      result = 31 * result + index;
      return result;
    }

    @Override
    public String toString() {
      if (parent != null) {
        return String.format("State{word='%s', index=%d, parent=%s}", word, index, parent);
      }
      return String.format("State{word='%s', index=%d}", word, index);
    }
  }

  private State currentState;
  private final List<String> symbols = Arrays.asList("(", ")");
  private final String target = symbols.get(1) + symbols.get(0);
  private final String replacement = symbols.get(0) + symbols.get(1);

  
  public DyckWordEnumeration(int nbOfPairs) {    
    currentState = initialStateOfLength(nbOfPairs*2);
  }

  /**
   * Return the next {@code State} after {@code state} as determined by the an algorithm found in
   * <a href="http://arxiv.org/abs/1002.2625">Generating and Ranking of Dyck Words</a>.
   *
   * The returned state is meant to be fed into this method again to for a continuous stream of {@code State}s.
   *
   * @param state the {@code State} that is used as a starting ground for the algorithm.
   * @return the {@code State} following {@code state}.
   * @see <a href="http://arxiv.org/abs/1002.2625">Generating and Ranking of Dyck Words</a>
   */
  private State next(State state) {
      do {
          if (state.index < state.word.length()) {
              int j = state.index;
              while (j < (state.word.length() - 1) && !state.word.substring(j, j + 2).equals(target)) {
                  j++;
              }
              if (j < (state.word.length() - 1)) {
                  String word = state.word;
                  String nextWord = word.substring(0, j) + replacement + word.substring(j+2);
                  return state.update(j + 2).push(nextWord, j - 1);
              }
          }
          state = state.pop();
      } while(state != null);
      return null;
  }

  private State initialStateOfLength(int n) {
      return new State(initialWordOfLength(n), 0);
  }

  private String initialWordOfLength(int n) {
      StringBuilder builder = new StringBuilder();
      for (int index = 0; index < n; index++) {
          builder.append(symbols.get(index % 2));
      }
      return builder.toString();
  }

  
  @Override
  public boolean hasMoreElements() {
    return currentState != null;
  }

  @Override
  public String nextElement() {
    if(currentState == null) throw new NoSuchElementException();
    String o = currentState.word;
    currentState = next(currentState);
    return o;
  }

}
