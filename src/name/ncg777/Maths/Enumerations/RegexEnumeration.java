package name.ncg777.Maths.Enumerations;
import java.util.*;
import dk.brics.automaton.*;

/**
 * This class implements the Enumeration interface to enumerate all strings
 * accepted by the regular expression provided.
 */
public class RegexEnumeration implements Enumeration<String> {
    private Queue<PathNode> queue;
    private Set<PathState> visited;
    private String nextElement;
    private Automaton automaton;
    private int stopAt = -1;
    private int count = 0;
    
    public RegexEnumeration(String regex) {
      this(regex,-1);
    }
    /**
     * Constructs a RegexEnumeration with the given regular expression.
     *
     * @param regex the regular expression to enumerate
     */
    public RegexEnumeration(String regex, int stopAt) {
      this.stopAt = stopAt;
      // Parse the regex into an automaton
      RegExp regExp = new RegExp(regex);
      automaton = regExp.toAutomaton();

      // Initialize BFS queue and visited set
      queue = new LinkedList<>();
      visited = new HashSet<>();

      // Start from the initial state with an empty string
      State initialState = automaton.getInitialState();
      queue.offer(new PathNode(initialState, ""));

      // Find the first accepted string
      this.nextElement = findNext();
    }

    @Override
    public boolean hasMoreElements() {
        return (stopAt == -1 || count <= stopAt) && nextElement != null;
    }

    @Override
    public String nextElement() {
      if(!hasMoreElements()) throw new NoSuchElementException();
      
      if (nextElement == null) {
          throw new NoSuchElementException("No more elements in the enumeration");
      }
      String result = nextElement;
      nextElement = findNext();
      return result;
    }

    /**
     * Performs BFS to find the next accepted string in the automaton.
     *
     * @return the next accepted string, or null if none are left
     */
    private String findNext() {
        while (!queue.isEmpty()) {
            PathNode node = queue.poll();
            State state = node.state;
            String str = node.str;

            PathState pathState = new PathState(state, str.length());
            if (visited.contains(pathState)) {
                continue; // Avoid processing the same state at the same string length
            }
            visited.add(pathState);

            // If this state is accepting, return the string
            if (state.isAccept()) {
                // Enqueue further transitions for future calls
                enqueueTransitions(state, str);
                count++;
                return str;
            } else {
                // Enqueue possible transitions
                enqueueTransitions(state, str);
            }
        }
        // All strings have been enumerated
        return null;
    }

    /**
     * Enqueues all possible transitions from the given state with the current string.
     *
     * @param state the current state in the automaton
     * @param str   the string built so far
     */
    private void enqueueTransitions(State state, String str) {
        for (Transition t : state.getTransitions()) {
            for (char c = t.getMin(); c <= t.getMax(); c++) {
                String nextStr = str + c;
                State dest = t.getDest();
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

    /**
     * Helper class to track visited states with string length to avoid infinite loops.
     */
    private static class PathState {
        State state;
        int length;

        PathState(State state, int length) {
            this.state = state;
            this.length = length;
        }

        @Override
        public int hashCode() {
            return state.hashCode() * 31 + length;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PathState)) {
                return false;
            }
            PathState other = (PathState) obj;
            return this.state.equals(other.state) && this.length == other.length;
        }
    }
}