package name.ncg777.maths;

import java.util.List;

public class VectorOfDoubles extends Vector<Double> {
  
  public VectorOfDoubles(Vector<Double> v) {
    this.values.addAll(v.values);
  }
  
  public VectorOfDoubles(Double...  values){
    this(of(List.of(values)));
  }
  
  @SafeVarargs
  public static VectorOfDoubles of(Double...  values){
    return new VectorOfDoubles(of(List.of(values)));
  }
  
  public Double[] toDoubleArray() {
    int m = getDimension();
    Double[] array = new Double[m];
    for (int i = 0; i < m; i++) {
      Double value = get(i);
      array[i] = (value != null) ? ((Double) value) : 0.0;

    }
    return array;
  }
  
  public static VectorOfDoubles fromString(String s) {
    s = s.substring(1, s.length()-1);
    return new VectorOfDoubles(
        of(
            List.of(s.split(","))
              .stream()
                .map((v) -> Double.parseDouble(v.trim())).toList()));
  }
}
