package name.NicolasCoutureGrenier.Maths.Objects;

import java.util.List;

public class VectorOfBooleans extends Vector<Boolean> {
  
  private VectorOfBooleans(Vector<Boolean> v) {
    this.values.addAll(v.values);
  }
  @SafeVarargs
  public static VectorOfBooleans of(Boolean...  values){
    return new VectorOfBooleans(of(List.of(values)));
  }
  
  
  public static VectorOfBooleans fromString(String s) {
    s = s.substring(1, s.length()-1);
    return new VectorOfBooleans(
        of(
            List.of(s.split(","))
              .stream()
                .map((v) -> Boolean.parseBoolean(v.trim())).toList()));
  }
}
