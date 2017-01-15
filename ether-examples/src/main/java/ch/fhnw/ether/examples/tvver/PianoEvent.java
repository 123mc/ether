package ch.fhnw.ether.examples.tvver;

import java.util.ArrayList;

public class PianoEvent {

  private final long S_TIME;
  private final int EXPECTED_TIME_TO_LIVE;
  private int timeToLive; // number of frames
  private PianoNote pianoNote;
  private static final PianoNote DEFAULT_PIANO_NOTE = new Piano().findPianoNoteByScientificName("C5");
  private static final int FRAMES_TO_SKIP = 20;

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
    if(frames.size()==50){
        PitchDetector pitchDetector = new PitchDetector(subSet(FRAMES_TO_SKIP,frames.size()-1));
        pitchDetector.call();
    }
  }

  private Frame[] subSet(int from, int to){
    Frame[] result = new Frame[to-from];
    for (int i = from; i < to; i++) {
      result[i-from] = frames.get(i);
    }

    return result;
  }

  public boolean isAlive() {
    return (timeToLive > 0);
  }

  public boolean isNew() {

    // TODO: return ONCE true as soon as Pitch was detected or timeToLive == 1 (pitch detection failed)
    //return (EXPECTED_TIME_TO_LIVE == timeToLive);

    return frames.size()==51;
  }

  public PianoNote getPianoNote() {
    if(pianoNote != null) {
      return pianoNote;

    }
    return DEFAULT_PIANO_NOTE;
  }

}
