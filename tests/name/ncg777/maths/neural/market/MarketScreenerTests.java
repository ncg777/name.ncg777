package name.ncg777.maths.neural.market;
import static org.junit.Assert.*;
import org.junit.Test;
import java.time.Instant;
public class MarketScreenerTests {
 @Test public void usExcludesTestIssuesAndFooter() throws Exception {
  var rows=SymbolCatalog.parseUS("Symbol|Security Name|Market Category|Test Issue|Financial Status|Round Lot Size|ETF|NextShares\nAAPL|Apple|Q|N|N|100|N|N\nTEST|Test|Q|Y|N|100|N|N\nFile Creation Time: today",true);
  assertEquals(1,rows.size()); assertEquals("AAPL",rows.get(0).ticker());
 }
 @Test public void tmxExpandsInstrumentsAndMapsSuffixes() throws Exception {
  var rows=SymbolCatalog.parseCanada("{\"length\":1,\"results\":[{\"name\":\"Fund\",\"instruments\":[{\"symbol\":\"ABC\",\"name\":\"CAD\"},{\"symbol\":\"ABC.U\",\"name\":\"USD\"}]}]}","TSX");
  assertEquals(2,rows.size()); assertEquals("ABC-U.TO",rows.get(1).ticker());
 }
 @Test(expected=java.io.IOException.class) public void rejectsPartialTmxDirectory() throws Exception {
  SymbolCatalog.parseCanada("{\"length\":2,\"results\":[]}","TSXV");
 }
 @Test public void cseRetainsListingStatus() throws Exception {
  var rows=SymbolCatalog.parseCanada("[{\"symbol\":\"ABC\",\"security_name\":\"Company\",\"status\":\"Suspended\"}]","CSE");
  assertEquals("ABC.CN",rows.get(0).ticker()); assertEquals("Suspended",rows.get(0).status());
 }
 @Test public void growthProbabilitiesAndBrierAreBounded() {
  var fit=MarketForecaster.fit(MarketForecasterTests.fixture(240),240,1); var score=MarketScreener.score(fit);
  assertTrue(score.oneDay()>0 && score.oneDay()<1); assertTrue(score.twoDays()>0 && score.twoDays()<1);
  assertTrue(score.testBrier()>=0 && score.testBrier()<=1); assertEquals(fit.testRows(),score.testWindows());
 }
 @Test(expected=IllegalArgumentException.class) public void excludesStalePrices() {
  MarketScreener.requireFresh(MarketForecasterTests.fixture(240),Instant.parse("2026-01-01T00:00:00Z"));
 }
}
