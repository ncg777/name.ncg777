package name.ncg.Maths.DataStructures;

import java.util.Comparator;

/**
 * Implements a comparator that compares arrays of integers.
 * 
 */
public class IntArrayComparator implements Comparator<int[]> {

  public IntArrayComparator() {}

  @Override
  public int compare(int[] arg0, int[] arg1) {
    if (arg0 == null) {
      if (arg1 != null) {
        return -1;
      } else {
        return 0;
      }
    } else {
      if (arg1 == null) {
        return 1;
      }
    }

    int sz0 = arg0.length;
    int sz1 = arg1.length;
    int i = 0;
    while (i < sz0 && i < sz1) {
      int c = 0;
      int a = arg0[i];
      int b = arg1[i];
      if (a < b) {
        c = -1;
      } else if (a > b) {
        c = 1;
      }

      if (c != 0) {
        return c;
      }
      i++;
    }

    if (sz0 == sz1) {
      return 0;
    } else if (sz0 < sz1) {
      return -1;
    } else {
      return 1;
    }

  }
}
