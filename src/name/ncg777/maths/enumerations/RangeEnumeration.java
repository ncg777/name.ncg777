package name.ncg777.maths.enumerations;

import java.util.Enumeration;

public class RangeEnumeration implements Enumeration<Integer> {
  private Integer end_exclusive;
  private Integer step;
  private Integer current;
  
  public RangeEnumeration(int start_inclusive, int end_exclusive, int step_absolute) {
    if(step_absolute <= 0 )throw new IllegalArgumentException();
    if(start_inclusive == end_exclusive) throw new IllegalArgumentException();
    step = step_absolute;
    if(start_inclusive > end_exclusive) step *= -1;
    this.end_exclusive = end_exclusive;
    current = start_inclusive;
  }
  
  @Override
  public boolean hasMoreElements() {
    return !(step < 0 && current <= end_exclusive || step > 0 && current >= end_exclusive);
  }
  public Integer nextElement() {
    if(!hasMoreElements()) throw new RuntimeException();
    var o = current;
    current += step;
    return o;
  }
}
