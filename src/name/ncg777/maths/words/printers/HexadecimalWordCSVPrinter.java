package name.ncg777.maths.words.printers;


import com.google.common.base.Function;

import name.ncg777.maths.objects.words.HexadecimalWord;

import java.io.PrintWriter;
import java.util.List;

public class HexadecimalWordCSVPrinter implements Function<List<HexadecimalWord>, Void> {
  PrintWriter o;

  public HexadecimalWordCSVPrinter(PrintWriter o) {
    this.o = o;
  }

  public void close() {
    o.close();
  }

  public void flush() {
    o.flush();
  }

  @Override
  public Void apply(List<HexadecimalWord> input) {
    int x = 0;

    for (int i = input.size() - 1; i >= 0; i--) {
      o.printf(input.get(i).toString());
      x += input.get(i).getN();
      if (i != 0) {
        o.printf(" ");
      }
    }
    o.printf(",%d,%d%n", input.size(), x);

    o.flush();

    return null;
  }
}
