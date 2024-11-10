package name.ncg777.Maths.Enumerations;

import java.util.Enumeration;
import java.util.Stack;
import java.util.TreeMap;

public class NonCrossingPartitionEnumeration implements Enumeration<int[]> {

  private final DyckWordEnumeration dwe;
  
  public NonCrossingPartitionEnumeration(int n) {
    dwe = new DyckWordEnumeration(n);
  }
  
  @Override
  public boolean hasMoreElements() {
    return dwe.hasMoreElements();
  }

  @Override
  public int[] nextElement() {
    return dyckToPartition(dwe.nextElement());
  }

  public static int[] dyckToPartition(String s) {
    int n = s.length()/2;
    
    
    Stack<Integer> stack = new Stack<>();
    TreeMap<Integer,Integer> matching_close = new TreeMap<>();
    TreeMap<Integer,Integer> close_label = new TreeMap<>();
    boolean rising = true;
    int k = 0;
    for(int i=0;i<2*n;i++) {
      if(s.charAt(i) == '(') {
        stack.push(i);
        rising = true;
      } else {
        int match = stack.pop();
        matching_close.put(match,i);
        if(rising) {
          rising = false;
          k++;
        }
        close_label.put(i, k-1);
        
        
      }
    }
    int j=0;
    int[] o = new int[n];
    for(int i=0;i<2*n;i++) {
      if(s.charAt(i) == '(') {
        o[j++] = close_label.get(matching_close.get(i));
        
      }
    }
    return o;
    
  }
  
}
