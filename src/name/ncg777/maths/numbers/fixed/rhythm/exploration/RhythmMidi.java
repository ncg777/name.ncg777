package name.ncg777.maths.numbers.fixed.rhythm.exploration;

import java.util.List;
import javax.sound.midi.*;

/** General MIDI percussion (channel 10), constant velocity; binary strings are chronological. */
public final class RhythmMidi {
  private RhythmMidi() {}
  public static Sequence sequence(List<String> patterns, int bpm, int stepsPerBeat, int drum) throws InvalidMidiDataException {
    if (patterns.isEmpty() || bpm < 20 || bpm > 300 || stepsPerBeat < 1 || stepsPerBeat > 256 || drum < 0 || drum > 127)
      throw new IllegalArgumentException("Invalid MIDI settings.");
    int resolution = 96 * stepsPerBeat;
    Sequence sequence = new Sequence(Sequence.PPQ, resolution);
    Track track = sequence.createTrack();
    int tempo = 60000000 / bpm;
    MetaMessage tempoMessage = new MetaMessage();
    tempoMessage.setMessage(0x51, new byte[]{(byte)(tempo >> 16), (byte)(tempo >> 8), (byte)tempo}, 3);
    track.add(new MidiEvent(tempoMessage, 0));
    long tick = 0;
    for (String pattern : patterns) {
      if (!new RhythmMask(pattern).complete()) throw new IllegalArgumentException("Complete patterns are required for MIDI.");
      for (char c : pattern.toCharArray()) {
        if (c == '1') {
          track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 9, drum, 96), tick));
          track.add(new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 9, drum, 0), tick + 48));
        }
        tick += 96;
      }
    }
    MetaMessage end = new MetaMessage(); end.setMessage(0x2f, new byte[0], 0);
    track.add(new MidiEvent(end, tick));
    return sequence;
  }
}
