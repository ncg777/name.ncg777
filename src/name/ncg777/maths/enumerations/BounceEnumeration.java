package name.ncg777.maths.enumerations;

import java.util.Enumeration;

public class BounceEnumeration implements Enumeration<Integer> {
  private Integer start_inclusive;
  private Integer end_exclusive;
  private Integer step;
  private Integer current;
  private Integer direction = 1;;
  
  public BounceEnumeration(int start_inclusive, int end_exclusive, int step_absolute) {
    if(step_absolute <= 0 )throw new IllegalArgumentException();
    if(start_inclusive == end_exclusive) throw new IllegalArgumentException();
    step = step_absolute;
    if(start_inclusive > end_exclusive) step *= -1;
    this.start_inclusive = start_inclusive;
    this.end_exclusive = end_exclusive;
    current = start_inclusive;
  }
  
  @Override
  public boolean hasMoreElements() {
    return hasMoreElements(direction, step, start_inclusive, current);
  }
  
  private boolean hasMoreElements(int direction, int step, int start_inclusive, int current) {
    return !(direction == -1 && 
        (step > 0 && current <= start_inclusive) || 
        (step < 0 && current >= start_inclusive));
  }
  
  private boolean rangeHasMoreElements(int step, int current, int end_exclusice) {
    return !(step < 0 && current <= end_exclusive || step > 0 && current >= end_exclusive);
  }
  
  public Integer nextElement() {
    if(!hasMoreElements()) throw new RuntimeException();
    
    if(!rangeHasMoreElements(step,  current+(step*direction), end_exclusive)) {
      direction *= -1;
    }
    var o = current;
    current += step*direction;
    
    return o;
  }
}