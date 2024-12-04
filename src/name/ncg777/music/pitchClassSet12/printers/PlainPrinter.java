package name.ncg777.music.pitchClassSet12.printers;

import com.google.common.base.Function;

import name.ncg777.music.pitchClassSet12.PitchClassSet12;

import java.io.PrintWriter;
import java.util.List;


public class PlainPrinter implements Function<List<PitchClassSet12>, Void> {
  PrintWriter o;

  public PlainPrinter(PrintWriter o) {
    this.o = o;
  }

  public void close() {
    o.close();
  }

  public Void apply(List<PitchClassSet12> input) {

    for (PitchClassSet12 ch : input) {
      o.printf(ch.toString());
      o.printf(" ");
    }

    o.printf("%n");

    return null;
  }
}
