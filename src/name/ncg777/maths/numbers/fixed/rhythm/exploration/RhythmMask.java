package name.ncg777.maths.numbers.fixed.rhythm.exploration;

import java.math.BigInteger;
import java.util.*;
import name.ncg777.maths.numbers.BinaryNatural;

/** Display order is chronological, most-significant bit first, as in rhythm completion. */
public record RhythmMask(String bits) {
  public enum Cipher {
    Binary(1), Octal(3), Hexadecimal(4);
    public final int width;
    Cipher(int width) { this.width = width; }
  }
  public enum Erasure { SCATTERED, BLOCK, PERIODIC }
  public RhythmMask {
    if (bits == null || !bits.matches("[01?]{1,256}"))
      throw new IllegalArgumentException("Use 1–256 binary steps (0, 1 or ?).");
  }
  public static RhythmMask parse(String text, Cipher cipher) {
    if (text == null || cipher == null) throw new IllegalArgumentException("Missing mask or cipher.");
    String s = text.replaceAll("\\s", "").toUpperCase(Locale.ROOT);
    if (s.isEmpty() || s.length() * cipher.width > 256) throw new IllegalArgumentException("Use 1–256 steps.");
    StringBuilder out = new StringBuilder();
    for (char c : s.toCharArray()) {
      if (c == '?') out.append("?".repeat(cipher.width));
      else {
        int v = Character.digit(c, 1 << cipher.width);
        if (v < 0 || c > 127) throw new IllegalArgumentException("Invalid " + cipher + " digit: " + c);
        String b = Integer.toBinaryString(v);
        out.append("0".repeat(cipher.width - b.length())).append(b);
      }
    }
    return new RhythmMask(out.toString());
  }
  /** Refuse conversions that would lose a partial digit or silently add steps. */
  public String format(Cipher cipher) {
    if (bits.length() % cipher.width != 0) throw new IllegalArgumentException("Step count must be divisible by " + cipher.width + ". Use Binary to preserve this mask.");
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < bits.length(); i += cipher.width) {
      String b = bits.substring(i, i + cipher.width);
      if (b.equals("?".repeat(cipher.width))) out.append('?');
      else if (b.contains("?")) throw new IllegalArgumentException("Partially known digits require Binary representation.");
      else out.append(Character.toUpperCase(Character.forDigit(Integer.parseInt(b, 2), 1 << cipher.width)));
    }
    return out.toString();
  }
  public boolean complete() { return bits.indexOf('?') < 0; }
  public BinaryNatural rhythm() {
    if (!complete()) throw new IllegalArgumentException("A complete rhythm is required.");
    return new BinaryNatural(new BigInteger(bits, 2), bits.length());
  }
  public boolean matches(String candidate) {
    if (candidate.length() != bits.length()) return false;
    for (int i = 0; i < bits.length(); i++) if (bits.charAt(i) != '?' && bits.charAt(i) != candidate.charAt(i)) return false;
    return true;
  }
  /** Protected positions use display indices, starting at zero. A protected bit protects its whole digit. */
  public RhythmMask erase(int count, int unitWidth, Erasure policy, Set<Integer> protectedSteps, Random random) {
    if (!complete()) throw new IllegalArgumentException("Complete the rhythm before erasing it.");
    if (count < 0 || unitWidth < 1 || bits.length() % unitWidth != 0) throw new IllegalArgumentException("Invalid erasure count or unit width.");
    for (int p : protectedSteps) if (p < 0 || p >= bits.length()) throw new IllegalArgumentException("Protected step out of range: " + p);
    int units = bits.length() / unitWidth;
    List<Integer> available = new ArrayList<>();
    for (int i = 0; i < units; i++) {
      boolean allowed = true;
      for (int j = 0; j < unitWidth; j++) if (protectedSteps.contains(i * unitWidth + j)) allowed = false;
      if (allowed) available.add(i);
    }
    if (count > available.size()) throw new IllegalArgumentException("Erasure count exceeds unprotected units (" + available.size() + ").");
    if (count == 0) return this;
    List<Integer> chosen = new ArrayList<>();
    if (policy == Erasure.SCATTERED) { Collections.shuffle(available, random); chosen.addAll(available.subList(0, count)); }
    else if (policy == Erasure.BLOCK) {
      List<Integer> starts = new ArrayList<>();
      for (int i = 0; i < units; i++) {
        boolean ok = true;
        for (int j = 0; j < count; j++) if (!available.contains((i + j) % units)) ok = false;
        if (ok) starts.add(i);
      }
      if (starts.isEmpty()) throw new IllegalArgumentException("No unprotected block of that size.");
      int start = starts.get(random.nextInt(starts.size()));
      for (int j = 0; j < count; j++) chosen.add((start + j) % units);
    } else {
      // Evenly spaced indices among eligible units, with a random phase.
      int phase = random.nextInt(available.size());
      for (int j = 0; j < count; j++) chosen.add(available.get((phase + j * available.size() / count) % available.size()));
    }
    char[] out = bits.toCharArray();
    for (int i : chosen) Arrays.fill(out, i * unitWidth, (i + 1) * unitWidth, '?');
    return new RhythmMask(new String(out));
  }
  public static Set<Integer> positions(String text) {
    Set<Integer> out = new TreeSet<>();
    if (text != null && !text.isBlank()) for (String s : text.trim().split("[,\\s]+")) out.add(Integer.parseInt(s));
    return Set.copyOf(out);
  }
}
