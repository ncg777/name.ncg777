package name.ncg777.maths.neural.market;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.*;
import com.fasterxml.jackson.databind.*;

/** Exchange directories are discovered on demand, never bundled as a stale symbol list. */
public final class SymbolCatalog {
  private SymbolCatalog() {}
  public record Symbol(String ticker, String name, String market, String exchange, String status) {}
  public record Catalog(List<Symbol> symbols, List<String> reports, Instant fetchedAt) {
    public Catalog { symbols = List.copyOf(symbols); reports = List.copyOf(reports); }
  }
  public static final String COVERAGE = "U.S. exchange listings, TSX, TSXV, CSE; excludes OTC and Cboe Canada. Quote coverage varies.";
  public static Catalog load() throws InterruptedException {
    String[][] sources = {
      {"Nasdaq", "https://www.nasdaqtrader.com/dynamic/SymDir/nasdaqlisted.txt"},
      {"Other U.S.", "https://www.nasdaqtrader.com/dynamic/SymDir/otherlisted.txt"},
      {"TSX", "https://www.tsx.com/json/company-directory/search/tsx/*"},
      {"TSXV", "https://www.tsx.com/json/company-directory/search/tsxv/*"},
      {"CSE", "https://thecse.com/api/webapi/listed-companies/"}};
    Map<String, Symbol> symbols = new TreeMap<>(); List<String> reports = new ArrayList<>();
    for (String[] source : sources) {
      if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
      try {
        String body = MarketData.get(URI.create(source[1]));
        List<Symbol> rows = source[0].equals("Nasdaq") || source[0].equals("Other U.S.")
            ? parseUS(body, source[0].equals("Nasdaq")) : parseCanada(body, source[0]);
        if (rows.isEmpty()) throw new IOException("Empty directory");
        rows.forEach(s -> symbols.put(s.market() + ":" + s.ticker(), s));
        reports.add(source[0] + ": " + rows.size() + " listings loaded");
      } catch (IOException | IllegalArgumentException e) { reports.add(source[0] + ": FAILED — " + e.getMessage()); }
    }
    return new Catalog(new ArrayList<>(symbols.values()), reports, Instant.now());
  }
  public static List<Symbol> parseUS(String text, boolean nasdaq) throws IOException {
    String[] lines = text.split("\\R");
    if (lines.length == 0 || !lines[0].startsWith(nasdaq ? "Symbol|" : "ACT Symbol|"))
      throw new IOException("Unexpected U.S. directory format");
    List<Symbol> result = new ArrayList<>();
    for (int i = 1; i < lines.length; i++) {
      if (lines[i].isBlank() || lines[i].startsWith("File Creation Time:")) continue;
      String[] row = lines[i].split("\\|", -1);
      if (row.length < 8) throw new IOException("Truncated U.S. directory row");
      if (row[nasdaq ? 3 : 6].equals("Y")) continue;
      result.add(new Symbol(row[0].replace('.', '-'), row[1], "U.S.", nasdaq ? "Nasdaq" : row[2], "Active"));
    }
    return result;
  }
  public static List<Symbol> parseCanada(String text, String exchange) throws IOException {
    JsonNode root = new ObjectMapper().readTree(text); List<Symbol> result = new ArrayList<>();
    if (exchange.equals("CSE")) {
      if (!root.isArray()) throw new IOException("Unexpected CSE format");
      for (JsonNode row : root) add(result, row.path("symbol").asText(), row.path("security_name").asText(), exchange, row.path("status").asText());
    } else {
      JsonNode rows = root.path("results");
      if (!rows.isArray() || root.path("length").asInt(-1) != rows.size()) throw new IOException("Incomplete TMX directory");
      for (JsonNode row : rows) {
        JsonNode instruments = row.path("instruments");
        if (!instruments.isArray() || instruments.isEmpty()) throw new IOException("Missing TMX instruments");
        for (JsonNode instrument : instruments) add(result, instrument.path("symbol").asText(),
            row.path("name").asText() + " — " + instrument.path("name").asText(), exchange, "Active");
      }
    }
    return result;
  }
  private static void add(List<Symbol> rows, String ticker, String name, String exchange, String status) throws IOException {
    if (ticker.isBlank() || name.isBlank()) throw new IOException("Missing listing symbol/name");
    String suffix = exchange.equals("TSX") ? ".TO" : exchange.equals("TSXV") ? ".V" : ".CN";
    rows.add(new Symbol(ticker.replace('.', '-') + suffix, name, "Canada", exchange, status));
  }
}
