package name.ncg777.maths.numbers.fixed.rhythm;

import static org.junit.Assert.*;
import java.util.*;
import java.util.concurrent.CancellationException;
import org.junit.Test;
import name.ncg777.maths.numbers.Natural;
import name.ncg777.maths.numbers.predicates.Euclidean;
import name.ncg777.maths.numbers.fixed.rhythm.exploration.*;
import name.ncg777.maths.numbers.fixed.rhythm.exploration.RhythmMask.*;
import name.ncg777.maths.numbers.fixed.rhythm.exploration.RhythmExplorer.*;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RhythmExplorerTests {
  @Test public void masksRoundTripAndMatchLegacyOrientation() {
    for (Cipher cipher : Cipher.values()) {
      String input = switch(cipher) { case Binary -> "1000000010000000"; case Octal -> "4010"; case Hexadecimal -> "8080"; };
      RhythmMask mask = RhythmMask.parse(input, cipher);
      assertEquals(input, mask.format(cipher));
      assertEquals(new Natural(name.ncg777.maths.numbers.Cipher.Name.valueOf(cipher.name()), input).toBinaryNatural(), mask.rhythm());
      assertEquals("?".repeat(cipher.width) + mask.bits().substring(cipher.width), RhythmMask.parse("?" + input.substring(1), cipher).bits());
    }
    assertEquals("000001", RhythmMask.parse("01", Cipher.Octal).bits());
    assertThrows(IllegalArgumentException.class, () -> RhythmMask.parse("8", Cipher.Octal));
    assertThrows(IllegalArgumentException.class, () -> new RhythmMask("1?00").format(Cipher.Hexadecimal));
    assertThrows(IllegalArgumentException.class, () -> new RhythmMask("1010").format(Cipher.Octal));
    assertThrows(IllegalArgumentException.class, () -> new RhythmMask("?".repeat(257)));
  }
  @Test public void completionAgreesWithExhaustiveOracleAndPreservesRests() {
    var mask = new RhythmMask("1?0??0??");
    var rule = RhythmExplorer.rule("HITS(3) OR EUCLIDEAN(4)", 8);
    var result = RhythmExplorer.complete(mask, rule, 1000, 100000, 777);
    Set<String> expected = new HashSet<>();
    for (int value = 0; value < 256; value++) {
      String bits = String.format("%8s", Integer.toBinaryString(value)).replace(' ', '0');
      if (mask.matches(bits) && rule.test(new RhythmMask(bits).rhythm())) expected.add(bits);
    }
    assertEquals(expected, new HashSet<>(result.candidates()));
    assertEquals(expected.size(), result.candidates().size());
    assertEquals(32, result.attempts()); assertEquals(Stop.EXHAUSTED, result.stop());
    assertEquals(result, RhythmExplorer.complete(mask, rule, 1000, 100000, 777));
    assertEquals(Stop.RESULT_LIMIT, RhythmExplorer.complete(new RhythmMask("????????"), r -> true, 1, 100, 2).stop());
    assertEquals(Stop.BUDGET, RhythmExplorer.complete(new RhythmMask("????????"), r -> false, 10, 2, 2).stop());
    assertEquals(Stop.EXHAUSTED, RhythmExplorer.complete(new RhythmMask("0000"), r -> false, 10, 2, 2).stop());
    assertEquals(100, RhythmExplorer.complete(new RhythmMask("?".repeat(256)), r -> true, 100, 100, 2).candidates().size());
  }
  @Test public void euclideanGoldenNecklacesAndNonEuclideanPermutation() {
    for (String pattern : List.of("10010010", "10110110", "1001010010100", "00000000", "11111111")) {
      for (int i = 0; i < pattern.length(); i++) {
        String rotation = pattern.substring(i) + pattern.substring(0, i);
        assertTrue(pattern, new Euclidean().test(new RhythmMask(rotation).rhythm()));
      }
    }
    // Same gap multiset as E(5,13), but long gaps clustered instead of distributed.
    assertFalse(new Euclidean().test(new RhythmMask("1001001001010").rhythm()));
    assertTrue(RhythmClauseParser.parse("EUCLIDEAN(3) AND HITS(3)").test(new RhythmMask("10010010").rhythm()));
    assertFalse(RhythmClauseParser.parse("EUCLIDEAN(4)").test(new RhythmMask("10010010").rhythm()));
    assertThrows(IllegalArgumentException.class, () -> RhythmClauseParser.parse("EUCLIDEAN(-1)"));
    assertThrows(IllegalArgumentException.class, () -> RhythmClauseParser.parse("EVEN(2)"));
    assertThrows(IllegalArgumentException.class, () -> RhythmExplorer.rule("NOT ORDINAL(3)", 16));
    assertThrows(IllegalArgumentException.class, () -> RhythmClauseParser.parse("HITS"));
    assertThrows(IllegalArgumentException.class, () -> RhythmClauseParser.parse("HITS(3) junk"));
  }
  @Test public void erasureIsExactAndProtectsBothOnsetsAndRests() {
    var start = new RhythmMask("1001001001001001");
    for (Erasure policy : Erasure.values()) {
      var mask = start.erase(4, 1, policy, Set.of(0, 1), new Random(777));
      assertEquals(4, mask.bits().chars().filter(c -> c == '?').count());
      assertEquals('1', mask.bits().charAt(0)); assertEquals('0', mask.bits().charAt(1));
      assertTrue(mask.matches(start.bits()));
      assertEquals(mask, start.erase(4, 1, policy, Set.of(0, 1), new Random(777)));
    }
    assertEquals(8, start.erase(2, 4, Erasure.SCATTERED, Set.of(0), new Random(1)).bits().chars().filter(c -> c == '?').count());
    assertThrows(IllegalArgumentException.class, () -> start.erase(4, 4, Erasure.SCATTERED, Set.of(0), new Random(1)));
    assertThrows(IllegalArgumentException.class, () -> start.erase(2, 1, Erasure.BLOCK, Set.of(0,2,4,6,8,10,12,14), new Random(1)));
    assertEquals(start, start.erase(0, 1, Erasure.BLOCK, Set.of(), new Random(1)));
  }
  private Settings settings(String transition) {
    return new Settings("HITS(6)", transition, 4, 1, Erasure.SCATTERED, Set.of(0,1), 512, 100000, 777,
        new Selection(2, 6, .15, 8, .1, -1, 4));
  }
  @Test public void evolutionIsReproducibleAndConstrainedIncludingTransitions() throws Exception {
    Settings config = settings("HITS(12)");
    // Settings round-trip is what the GUI saves.
    ObjectMapper json = new ObjectMapper();
    assertEquals(config, json.readValue(json.writeValueAsString(config), Settings.class));
    var a = new Session("1001001001001001", config);
    var b = new Session("1001001001001001", config);
    boolean changed = false;
    for (int i = 0; i < 64; i++) {
      Step x = a.next(); assertEquals(x, b.next()); assertEquals(i, x.iteration());
      assertTrue(new RhythmMask(x.mask()).matches(x.bits())); assertEquals(6, x.hits());
      assertEquals('1', x.bits().charAt(0)); assertEquals('0', x.bits().charAt(1));
      assertTrue(x.changed() <= 4); assertTrue(x.probability() > 0 && x.probability() <= 1);
      changed |= x.changed() > 0;
      assertEquals(x, json.readValue(json.writeValueAsString(x), Step.class));
    }
    assertTrue(changed);
    assertThrows(IllegalStateException.class, () -> new Session("1001001001001001", settings("FALSE")).next());
    assertThrows(IllegalArgumentException.class, () -> new Session("0000000000000000", config));
  }
  @Test public void selectionTargetsChangeAndHandlesLowTemperaturesAndHolds() {
    Selection o = new Selection(2, -1, .00000001, 0, 0, -1, 4);
    Choice x = RhythmExplorer.choose("1000", List.of("1000", "0100", "0111"), List.of(), o, new Random(777));
    assertEquals("0100", x.bits()); assertEquals(1, x.probability(), 1e-9);
    var hold = new Settings("HITS(1)", "TRUE", 0, 1, Erasure.SCATTERED, Set.of(), 512, 10000, 777, o);
    assertTrue(new Session("1000", hold).next().held());
    assertEquals(1, RhythmExplorer.syncopation("0100", 4), 1e-9);
    assertEquals(0, RhythmExplorer.syncopation("1000", 4), 1e-9);
    assertThrows(IllegalArgumentException.class, () -> new Selection(0, -1, Double.NaN, 0, 0, -1, 4));
  }
  @Test public void midiPreservesTimingRestsAndPercussionChannel() throws Exception {
    var sequence = RhythmMidi.sequence(List.of("1000", "0010"), 120, 4, 36);
    assertEquals(768, sequence.getTickLength()); assertEquals(384, sequence.getResolution());
    List<Long> onsets = new ArrayList<>();
    for (int i = 0; i < sequence.getTracks()[0].size(); i++) {
      var event = sequence.getTracks()[0].get(i);
      if (event.getMessage() instanceof javax.sound.midi.ShortMessage m && m.getCommand() == javax.sound.midi.ShortMessage.NOTE_ON) {
        assertEquals(9, m.getChannel()); assertEquals(36, m.getData1()); onsets.add(event.getTick());
      }
    }
    assertEquals(List.of(0L, 576L), onsets);
  }
  @Test public void cancellationWorksBeforeLongSearch() {
    Thread.currentThread().interrupt();
    try { assertThrows(CancellationException.class, () -> RhythmExplorer.complete(new RhythmMask("?".repeat(256)), r -> false, 1000, 100000, 777)); }
    finally { Thread.interrupted(); }
  }
}
