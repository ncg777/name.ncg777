package name.ncg777.computing.structures;

import java.util.Arrays;

public class ImmutableDoubleArray implements Comparable<ImmutableDoubleArray>{

  private double[] arr;
  
  public int size() {return arr.length;}
  public double get(int index) {return arr[index];}
  
  public ImmutableDoubleArray(double[] arr0) {
    arr = Arrays.copyOf(arr0, arr0.length);
  }
  
  @Override
  public int compareTo(ImmutableDoubleArray o) {
    for (int i = 0; i < Math.min(arr.length, o.arr.length); i++) {
      if (arr[i] != o.arr[i]) {
          return Double.compare(arr[i], o.arr[i]);
      }
  }
  return Integer.compare(arr.length, o.arr.length);
  }
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(arr);
    return result;
  }
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ImmutableDoubleArray other = (ImmutableDoubleArray) obj;
    return Arrays.equals(arr, other.arr);
  }

}