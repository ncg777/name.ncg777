package name.ncg.Music.PCS12ListPrinters;

import com.google.common.base.Function;
import java.io.PrintWriter;
import java.util.List;
import name.ncg.Music.PCS12;


public class PlainPrinter implements Function<List<PCS12>, Void> {
  PrintWriter o;

  public PlainPrinter(PrintWriter o) {
    this.o = o;
  }

  public void close() {
    o.close();
  }

  public Void apply(List<PCS12> input) {

    for (PCS12 ch : input) {
      o.printf(ch.toString());
      o.printf(" ");
    }

    o.printf("%n");

    return null;
  }
}
