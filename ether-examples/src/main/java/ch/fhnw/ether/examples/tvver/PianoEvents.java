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
      return null;
    }
    return pianoEvents.get(pianoEvents.size() - 1);
  }

  public int size() {
    return pianoEvents.size();
  }

  public PianoEvent get(int index) {
    return pianoEvents.get(index);
  }

  public PianoEvents getUndetectedPianoEvents() {
    PianoEvents undetectedPianoEvents = new PianoEvents();
    for(PianoEvent pianoEvent : pianoEvents) {
      if(!pianoEvent.isDetected()) {
        undetectedPianoEvents.add(pianoEvent);
      }
    }
    return undetectedPianoEvents;
  }
}
