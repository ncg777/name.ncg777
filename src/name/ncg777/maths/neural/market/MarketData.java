package name.ncg777.maths.neural.market;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Free remote feeds and a small, explicit date/close CSV interchange format. */
public final class MarketData {
  private MarketData() {}
  public enum Interval {
    MINUTE("1 minute", "1m", "5d", 60), FIVE_MINUTES("5 minutes", "5m", "1mo", 300),
    FIFTEEN_MINUTES("15 minutes", "15m", "1mo", 900), HOUR("1 hour", "1h", "3mo", 3600),
    DAY("1 day", "1d", "5y", 86400), WEEK("1 week", "1wk", "10y", 604800),
    MONTH("1 month", "1mo", "max", 2678400);
    public final String label, code, range;
    public final long seconds;
    Interval(String label, String code, String range, long seconds) {
      this.label = label; this.code = code; this.range = range; this.seconds = seconds;
    }
    @Override public String toString() { return label; }
    public boolean complete(Instant start, Instant now) {
      Instant end = this == MONTH ? start.atZone(ZoneOffset.UTC).plusMonths(1).toInstant() : start.plusSeconds(seconds);
      return !end.isAfter(now);
    }
  }
  public record Bar(Instant time, double close, boolean complete) {
    public Bar {
      Objects.requireNonNull(time);
      if (!Double.isFinite(close) || close <= 0) throw new IllegalArgumentException("Prices must be positive and finite");
    }
  }
  public record Snapshot(String source, String symbol, String unit, Interval interval,
      Instant fetchedAt, List<Bar> bars) {
    public Snapshot {
      Objects.requireNonNull(source); Objects.requireNonNull(symbol); Objects.requireNonNull(unit);
      Objects.requireNonNull(interval); Objects.requireNonNull(fetchedAt);
      bars = List.copyOf(bars);
      if (bars.isEmpty()) throw new IllegalArgumentException("No usable price observations");
      for (int i = 1; i < bars.size(); i++)
        if (!bars.get(i).time().isAfter(bars.get(i - 1).time()))
          throw new IllegalArgumentException("Price times must be unique and increasing");
    }
    public List<Bar> completed() { return bars.stream().filter(Bar::complete).toList(); }
  }
  private static final HttpClient HTTP = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(12))
      .followRedirects(HttpClient.Redirect.NORMAL).build();
  private static final ObjectMapper JSON = new ObjectMapper();

  static String get(URI uri) throws IOException, InterruptedException {
    var request = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(25))
        .header("User-Agent", "Mozilla/5.0 name.ncg777 MarketForecast/1.0")
        .header("Accept", "application/json,text/csv,text/plain").GET().build();
    var future = HTTP.sendAsync(request, info -> new LimitedBody());
    try {
      var response = future.get(25, TimeUnit.SECONDS);
      if (response.statusCode() != 200)
        throw new IOException("Data provider returned HTTP " + response.statusCode() + ". Try later, FRED, or a CSV file.");
      return new String(response.body(), StandardCharsets.UTF_8);
    } catch (TimeoutException e) {
      future.cancel(true); throw new IOException("Data provider timed out after 25 seconds. Try later or open a CSV.", e);
    } catch (ExecutionException e) {
      throw new IOException("Could not load prices: " + e.getCause().getMessage(), e.getCause());
    } catch (InterruptedException e) {
      future.cancel(true); throw e;
    }
  }
  /** Limit bytes while receiving, and let cancellation stop the network subscription. */
  private static final class LimitedBody implements HttpResponse.BodySubscriber<byte[]> {
    private final HttpResponse.BodySubscriber<byte[]> delegate = HttpResponse.BodySubscribers.ofByteArray();
    private Flow.Subscription subscription;
    private long size;
    @Override public CompletionStage<byte[]> getBody() { return delegate.getBody(); }
    @Override public void onSubscribe(Flow.Subscription value) { subscription = value; delegate.onSubscribe(value); }
    @Override public void onNext(List<ByteBuffer> buffers) {
      for (ByteBuffer buffer : buffers) size += buffer.remaining();
      if (size > 8_000_000) { subscription.cancel(); delegate.onError(new IOException("Data response exceeds 8 MB")); }
      else delegate.onNext(buffers);
    }
    @Override public void onError(Throwable error) { delegate.onError(error); }
    @Override public void onComplete() { delegate.onComplete(); }
  }
  public static Snapshot yahoo(String symbol, Interval interval) throws IOException, InterruptedException {
    symbol = symbol.trim().toUpperCase(Locale.ROOT);
    if (!symbol.matches("[A-Z0-9^._=\\-]{1,32}")) throw new IllegalArgumentException("Enter a ticker such as SPY, AAPL, ^GSPC, or BTC-USD");
    String encoded = URLEncoder.encode(symbol, StandardCharsets.UTF_8);
    URI uri = URI.create("https://query1.finance.yahoo.com/v8/finance/chart/" + encoded
        + "?interval=" + interval.code + "&range=" + interval.range + "&includePrePost=false");
    return parseYahoo(get(uri), symbol, interval, Instant.now());
  }
  public static Snapshot parseYahoo(String text, String symbol, Interval interval, Instant now) throws IOException {
    JsonNode chart = JSON.readTree(text).path("chart");
    if (!chart.path("error").isNull() && !chart.path("error").isMissingNode())
      throw new IOException("Provider: " + chart.path("error").path("description").asText("Unavailable symbol"));
    JsonNode result = chart.path("result").path(0);
    JsonNode times = result.path("timestamp");
    JsonNode closes = result.path("indicators").path("quote").path(0).path("close");
    JsonNode adjusted = result.path("indicators").path("adjclose").path(0).path("adjclose");
    boolean useAdjusted = interval.seconds >= 86400 && adjusted.isArray() && adjusted.size() == times.size();
    var bars = new TreeMap<Instant, Bar>();
    for (int i = 0; i < times.size(); i++) {
      JsonNode price = useAdjusted ? adjusted.path(i) : closes.path(i);
      if (!times.path(i).isIntegralNumber() || !price.isNumber()) continue;
      double value = price.asDouble();
      if (!Double.isFinite(value) || value <= 0) continue;
      Instant time = Instant.ofEpochSecond(times.get(i).asLong());
      if (time.isAfter(now)) continue;
      bars.put(time, new Bar(time, value, interval.complete(time, now)));
    }
    if (bars.isEmpty()) throw new IOException("No usable prices returned for " + symbol);
    return new Snapshot("Yahoo (unofficial; " + (useAdjusted ? "adjusted close" : "close") + ")",
        symbol, result.path("meta").path("currency").asText("price"), interval, now, new ArrayList<>(bars.values()));
  }
  public static Snapshot fred(String symbol) throws IOException, InterruptedException {
    if (!Set.of("NASDAQCOM", "SP500", "DJIA").contains(symbol))
      throw new IllegalArgumentException("FRED symbols: NASDAQCOM, SP500, DJIA");
    return parseCsv(get(URI.create("https://fred.stlouisfed.org/graph/fredgraph.csv?id=" + symbol
        + "&cosd=" + LocalDate.now(ZoneOffset.UTC).minusYears(8))),
        "FRED daily close", symbol, Interval.DAY, Instant.now());
  }
  /** CSV with date/observation_date/timestamp and close/adjusted close or a single value column. */
  public static Snapshot parseCsv(String csv, String source, String symbol, Interval interval, Instant now) throws IOException {
    if (csv.length() > 8_000_000) throw new IOException("CSV exceeds 8 MB");
    String[] lines = csv.replace("\uFEFF", "").split("\\R");
    if (lines.length < 2) throw new IOException("CSV requires a header and observations");
    String[] header = lines[0].split(",", -1);
    int date = -1, close = -1;
    for (int i = 0; i < header.length; i++) {
      String name = header[i].replace("\"", "").trim().toLowerCase(Locale.ROOT);
      if (Set.of("date", "observation_date", "timestamp").contains(name)) date = i;
      if (name.equals("close") && close < 0) close = i;
      if (Set.of("adj close", "adjusted_close", "adjusted close").contains(name)) close = i;
    }
    if (close < 0 && header.length == 2) close = 1;
    if (date < 0 || close < 0 || date == close) throw new IOException("Expected Date,Close CSV columns");
    var bars = new TreeMap<Instant, Bar>();
    for (int line = 1; line < lines.length; line++) {
      if (lines[line].isBlank()) continue;
      String[] fields = lines[line].replace("\"", "").split(",", -1);
      if (fields.length != header.length) throw new IOException("Malformed CSV at line " + (line + 1));
      String price = fields[close].trim();
      if (price.isEmpty() || price.equals(".") || price.equalsIgnoreCase("null")) continue;
      try {
        String dateText = fields[date].trim();
        Instant time = dateText.length() == 10 ? LocalDate.parse(dateText).atStartOfDay(ZoneOffset.UTC).toInstant()
            : Instant.parse(dateText);
        double value = Double.parseDouble(price);
        if (time.isAfter(now)) continue;
        Bar bar = new Bar(time, value, interval.complete(time, now));
        if (bars.put(time, bar) != null) throw new IOException("Duplicate timestamp at line " + (line + 1));
      } catch (IllegalArgumentException | DateTimeException e) {
        throw new IOException("Invalid date or price at CSV line " + (line + 1), e);
      }
    }
    if (bars.isEmpty()) throw new IOException("No usable CSV prices");
    return new Snapshot(source, symbol, "price", interval, now, new ArrayList<>(bars.values()));
  }
}
