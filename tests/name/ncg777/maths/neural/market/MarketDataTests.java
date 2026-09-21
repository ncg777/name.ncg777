package name.ncg777.maths.neural.market;

import static org.junit.Assert.*;
import java.io.IOException;
import java.time.*;
import org.junit.Test;
import name.ncg777.maths.neural.market.MarketData.Interval;

public class MarketDataTests {
  @Test public void csvSortsAndOmitsMissingWithoutInventingBars() throws Exception {
    var data = MarketData.parseCsv("observation_date,NASDAQCOM\n2024-01-03,105\n2024-01-01,100\n2024-01-02,.\n",
        "fixture", "TEST", Interval.DAY, Instant.parse("2024-01-04T12:00:00Z"));
    assertEquals(2, data.bars().size()); assertEquals(100, data.bars().get(0).close(), 0);
    assertEquals(2, data.completed().size());
  }
  @Test public void adjustedColumnTakesPrecedence() throws Exception {
    var data = MarketData.parseCsv("Date,Adj Close,Close\n2024-01-01,50,100\n", "fixture", "TEST", Interval.DAY,
        Instant.parse("2024-01-03T00:00:00Z"));
    assertEquals(50, data.bars().get(0).close(), 0);
  }
  @Test(expected = IOException.class) public void rejectsDuplicateTimes() throws Exception {
    MarketData.parseCsv("Date,Close\n2024-01-01,100\n2024-01-01,101", "fixture", "TEST", Interval.DAY, Instant.now());
  }
  @Test(expected = IOException.class) public void rejectsInvalidPrice() throws Exception {
    MarketData.parseCsv("Date,Close\n2024-01-01,NaN", "fixture", "TEST", Interval.DAY, Instant.now());
  }
  @Test(expected = IOException.class) public void exposesProviderError() throws Exception {
    MarketData.parseYahoo("{\"chart\":{\"result\":null,\"error\":{\"description\":\"Missing symbol\"}}}", "BAD", Interval.DAY, Instant.now());
  }
  @Test public void yahooUsesAdjustedHistoryAndMarksIncompleteBars() throws Exception {
    String json = """
        {"chart":{"error":null,"result":[{"meta":{"currency":"USD"},
          "timestamp":[1704067200,1704153600,1704240000],
          "indicators":{"quote":[{"close":[100,102,null]}],"adjclose":[{"adjclose":[50,51,null]}]}}]}}
        """;
    var daily = MarketData.parseYahoo(json, "TEST", Interval.DAY, Instant.parse("2024-01-02T12:00:00Z"));
    assertEquals(2, daily.bars().size()); assertEquals(50, daily.bars().get(0).close(), 0);
    assertEquals(1, daily.completed().size());
    var intraday = MarketData.parseYahoo(json, "TEST", Interval.HOUR, Instant.parse("2024-01-02T00:30:00Z"));
    assertEquals(100, intraday.bars().get(0).close(), 0); assertEquals(1, intraday.completed().size());
  }
  @Test public void monthCompletionUsesCalendarMonths() {
    assertFalse(Interval.MONTH.complete(Instant.parse("2024-02-01T00:00:00Z"), Instant.parse("2024-02-29T12:00:00Z")));
    assertTrue(Interval.MONTH.complete(Instant.parse("2024-02-01T00:00:00Z"), Instant.parse("2024-03-01T00:00:00Z")));
  }
}
