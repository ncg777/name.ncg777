package name.ncg777.Music.PCS12ListPrinters;

import com.google.common.base.Function;

import name.ncg777.Music.PCS12;

import java.io.PrintWriter;
import java.util.List;


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
