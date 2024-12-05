package name.ncg777.maths.music.pcs12.printers;

import com.google.common.base.Function;

import name.ncg777.maths.music.pcs12.Pcs12;

import java.io.PrintWriter;
import java.util.List;


public class PlainPrinter implements Function<List<Pcs12>, Void> {
  PrintWriter o;

  public PlainPrinter(PrintWriter o) {
    this.o = o;
  }

  public void close() {
    o.close();
  }

  public Void apply(List<Pcs12> input) {

    for (Pcs12 ch : input) {
      o.printf(ch.toString());
      o.printf(" ");
    }

    o.printf("%n");

    return null;
  }
}
