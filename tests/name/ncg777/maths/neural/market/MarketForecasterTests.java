package name.ncg777.maths.neural.market;

import static org.junit.Assert.*;
import java.time.Instant;
import java.util.*;
import org.junit.Test;
import name.ncg777.maths.physics.TernarySpinModel;

public class MarketForecasterTests {
  public static MarketData.Snapshot fixture(int count) {
    var bars = new ArrayList<MarketData.Bar>(); var random = new Random(19); double price = 100;
    for (int i = 0; i < count; i++) {
      price *= Math.exp(.005 * Math.sin(i * .7) + random.nextGaussian() * .003);
      bars.add(new MarketData.Bar(Instant.parse("2020-01-01T00:00:00Z").plusSeconds(i * 86400L), price, true));
    }
    return new MarketData.Snapshot("Synthetic test fixture", "TEST", "USD", MarketData.Interval.DAY, Instant.now(), bars);
  }
  @Test public void windowsStayInsideTheirSegment() {
    double[] returns = new double[30]; Arrays.fill(returns, -1); Arrays.fill(returns, 10, 20, 1);
    var windows = MarketForecaster.windows(returns, 10, 20, .1);
    assertEquals(3, windows.length);
    for (int[] row : windows) for (int state : row) assertEquals(1, state);
  }
  @Test public void conditionalEnumeratesJointDependence() {
    double[][] coupling = new double[8][8]; coupling[6][7] = coupling[7][6] = 2;
    var model = new TernarySpinModel(coupling, new double[8], new double[8]);
    double[] p = MarketForecaster.conditional(model, new int[6]);
    assertEquals(1, Arrays.stream(p).sum(), 1e-12);
    assertEquals(Math.exp(4), p[0] / p[2], 1e-10);
    assertEquals(p[0], p[8], 1e-12);
  }
  @Test public void testPricesCannotAffectTrainingOrSelection() {
    var original = fixture(240);
    var changed = new ArrayList<>(original.bars());
    for (int i = 210; i < changed.size(); i++) {
      var b = changed.get(i); changed.set(i, new MarketData.Bar(b.time(), b.close() * 1.5, true));
    }
    var a = MarketForecaster.fit(original, 240, 2);
    var b = MarketForecaster.fit(new MarketData.Snapshot("Synthetic", "TEST", "USD", MarketData.Interval.DAY,
        Instant.now(), changed), 240, 2);
    assertEquals(a.threshold(), b.threshold(), 0);
    assertArrayEquals(a.model().fields(), b.model().fields(), 0);
    assertArrayEquals(a.model().penalties(), b.model().penalties(), 0);
    for (int i = 0; i < 8; i++) assertArrayEquals(a.model().couplings()[i], b.model().couplings()[i], 0);
    assertEquals(a.useNetwork(), b.useNetwork());
    assertEquals(a.modelValidationLoss(), b.modelValidationLoss(), 0);
    assertEquals(1, Arrays.stream(a.joint()).sum(), 1e-12);
    assertTrue(a.work() <= 100_000_000);
    assertTrue(Double.isFinite(a.modelTestLoss()));
  }
  @Test public void unfinishedObservationNeverEntersModelOrForecastContext() {
    var original = fixture(180); var extra = new ArrayList<>(original.bars());
    var last = extra.get(extra.size() - 1);
    extra.add(new MarketData.Bar(last.time().plusSeconds(86400), 1_000_000, false));
    var a = MarketForecaster.fit(original, 180, 1);
    var b = MarketForecaster.fit(new MarketData.Snapshot("Synthetic", "TEST", "USD", MarketData.Interval.DAY,
        Instant.now(), extra), 180, 1);
    assertArrayEquals(a.joint(), b.joint(), 0);
    assertEquals(180, b.trainingBars().size());
  }
  @Test(expected = IllegalArgumentException.class) public void insufficientHistoryRejected() {
    MarketForecaster.fit(fixture(100), 150, 1);
  }
  @Test(expected = java.util.concurrent.CancellationException.class) public void trainingCanBeInterrupted() {
    Thread.currentThread().interrupt();
    try { MarketForecaster.fit(fixture(180), 180, 1); }
    finally { Thread.interrupted(); }
  }
}
