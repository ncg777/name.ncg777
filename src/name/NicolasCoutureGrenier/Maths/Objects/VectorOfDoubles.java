package name.NicolasCoutureGrenier.Maths.Objects;

import java.util.List;

public class VectorOfDoubles extends Vector<Double> {
  
  private VectorOfDoubles(Vector<Double> v) {
    this.values.addAll(v.values);
  }
  @SafeVarargs
  public static VectorOfDoubles of(Double...  values){
    return new VectorOfDoubles(of(List.of(values)));
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
