package ch.fhnw.ether.examples.tvver;

import java.util.ArrayList;

public class PianoEvent {

  private final long S_TIME;
  private final int EXPECTED_TIME_TO_LIVE;
  private int timeToLive; // number of frames
  private PianoNote pianoNote;
  private static final PianoNote DEFAULT_PIANO_NOTE = new Piano().findPianoNoteByScientificName("C5");

  ArrayList<Frame> frames;

  public PianoEvent(long time, int ttl) {
    S_TIME = time;
    EXPECTED_TIME_TO_LIVE = ttl - 1;
    timeToLive = ttl;

    frames = new ArrayList<>();
  }

  public void feedFrame(Frame frame) {
    frames.add(frame);
    timeToLive--;
  }

  public boolean isAlive() {
    return (timeToLive > 0);
  }

  public boolean isNew() {
    // TODO: return ONCE true as soon as Pitch was detected or timeToLive == 1 (pitch detection failed)
    return (EXPECTED_TIME_TO_LIVE == timeToLive);
  }

  public PianoNote getPianoNote() {
    if(pianoNote != null) {
      return pianoNote;

    }
    return DEFAULT_PIANO_NOTE;
  }

}
