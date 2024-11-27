package name.ncg777.mathematics.enumerations;
import java.util.*;
import dk.brics.automaton.*;

/**
 * This class implements the Enumeration interface to enumerate all strings
 * accepted by the regular expression provided.
 */
public class RegexEnumeration implements Enumeration<String> {
  private Queue<PathNode> queue;
  private String nextElement;
  private Automaton automaton;
  private int stopAt;    // Optional stopping index
  private int count;     // Count of enumerated elements
  private int maxLength; // Maximum string length to prevent infinite loops

  /**
   * Constructs a RegexEnumeration with the given regular expression.
   *
   * @param regex the regular expression to enumerate
   */
  public RegexEnumeration(String regex) {
      this(regex, -1, Integer.MAX_VALUE); // By default, no limit
  }
  public RegexEnumeration(String regex, int stopAt) {
    this(regex, stopAt, Integer.MAX_VALUE); // By default, no limit
  }
  /**
   * Constructs a RegexEnumeration with the given regular expression,
   * optional stopping index, and maximum string length.
   *
   * @param regex     the regular expression to enumerate
   * @param stopAt    the maximum number of elements to enumerate (-1 for no limit)
   * @param maxLength the maximum length of strings to generate
   */
  public RegexEnumeration(String regex, int stopAt, int maxLength) {
      this.stopAt = stopAt;
      this.count = 0;
      this.maxLength = maxLength;

      // Parse the regex into an automaton
      RegExp regExp = new RegExp(regex, RegExp.ALL);
      automaton = regExp.toAutomaton();

      // Initialize BFS queue
      queue = new LinkedList<>();
      queue.offer(new PathNode(automaton.getInitialState(), ""));

      // Prepare the next element
      nextElement = findNext();
  }

  @Override
  public boolean hasMoreElements() {
      return nextElement != null && (stopAt == -1 || count < stopAt);
  }

  @Override
  public String nextElement() {
      if (!hasMoreElements()) {
          throw new NoSuchElementException("No more elements in the enumeration");
      }
      String result = nextElement;
      count++;
      nextElement = findNext();
      return result;
  }

  private String findNext() {
      while (!queue.isEmpty()) {
          PathNode node = queue.poll();
          State state = node.state;
          String str = node.str;

          // Skip strings longer than maxLength
          if (str.length() > maxLength) {
              continue;
          }

          // If this state is accepting and we've not exceeded stopAt, return the string
          if (state.isAccept()) {
              // Enqueue further transitions for future calls
              enqueueTransitions(state, str);
              return str;
          }

          // Enqueue possible transitions
          enqueueTransitions(state, str);
      }
      return null;
  }

  /**
   * Enqueues all possible transitions from the given state with the current string.
   *
   * @param state the current state in the automaton
   * @param str   the string built so far
   */
  private void enqueueTransitions(State state, String str) {
      // Skip enqueuing if we've reached the maximum length
      if (str.length() >= maxLength) {
          return;
      }

      for (Transition t : state.getTransitions()) {
          char min = t.getMin();
          char max = t.getMax();
          State dest = t.getDest();

          for (char c = min; c <= max; c++) {
              String nextStr = str + c;
              queue.offer(new PathNode(dest, nextStr));
          }
      }
  }

  /**
   * Helper class to represent a node in the BFS traversal.
   */
  private static class PathNode {
      State state;
      String str;

      PathNode(State state, String str) {
          this.state = state;
          this.str = str;
      }
  }
}