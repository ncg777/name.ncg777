package name.NicolasCoutureGrenier.Maths.Objects;

import java.util.List;

public class VectorOfIntegers extends Vector<Integer> {
  
  private VectorOfIntegers(Vector<Integer> v) {
    this.values.addAll(v.values);
  }
  @SafeVarargs
  public static VectorOfIntegers of(Integer...  values){
    return new VectorOfIntegers(of(List.of(values)));
  }
  
  
  public static VectorOfIntegers fromString(String s) {
    s = s.substring(1, s.length()-1);
    return new VectorOfIntegers(
        of(
            List.of(s.split(","))
              .stream()
                .map((v) -> Integer.parseInt(v.trim())).toList()));
  }
}
