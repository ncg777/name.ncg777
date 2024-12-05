package name.ncg777.music.apps.kernelValuator;

import java.util.List;
import java.util.Map;

public interface Kernel {
  public static String getEmptyParameterString(Kernel kernel) {
    StringBuilder sb = new StringBuilder();
    for(String s : kernel.getParameterNames()) {
      sb.append(s + " = ");
      switch(kernel.getParameterType(s)) {
        case BOOL:
          sb.append("false");
          break;
        case INT:
          sb.append("0");
          break;
        case DOUBLE:
          sb.append("0.0");
          break;
        case SEQUENCE:
          sb.append("0");
          break;
        default:
          break;
      }
      sb.append("\n");
    }
    return sb.toString().trim();
  }

  public int getValue(Map<String, Object> parameters, int i);
  public String getName();
  public String getDocumentation();
  public List<String> getParameterNames();
  public ParameterType getParameterType(String name);
}
