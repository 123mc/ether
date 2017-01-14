package ch.fhnw.ether.examples.tvver;

import java.util.ArrayList;

public class PianoEvents {
  ArrayList<PianoEvent> pianoEvents;
  public PianoEvents() {
    pianoEvents = new ArrayList<>();
  }

  public void add(PianoEvent pianoEvent) {
    pianoEvents.add(pianoEvent);
  }

  public PianoEvent getLast() {
    if(pianoEvents.size() < 1) {
      return new PianoEvent(0, 0);
    }
    return pianoEvents.get(pianoEvents.size() - 1);
  }

  public boolean newPianoNoteIsDetected() {
    return getLast().isNew();
  }
}
