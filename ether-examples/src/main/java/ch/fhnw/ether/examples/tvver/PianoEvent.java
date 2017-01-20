package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.IAudioRenderTarget;

public class PianoEvent {

  private volatile PianoNote pianoNote;

  private final double playOutTimeOfAttack;
  private final double playOutTimeOfLastSilence;

  public PianoEvent(double attack, double lastSilence) {
    playOutTimeOfAttack = attack;
    playOutTimeOfLastSilence = lastSilence;
  }

  public boolean isDetected() {
    return (pianoNote != null);
  }

  public PianoNote getPianoNote() {
    if(isDetected()) {
      return pianoNote;
    }
    return null;
  }

  public void setPianoNote(PianoNote pn) {
    if(pianoNote != null) {
      return;
    } // do not overwrite

    pianoNote = pn;
  }

  public double getPlayOutTimeOfAttack() {
    return playOutTimeOfAttack;
  }

  public double getPlayOutTimeOfLastSilence() {
    return playOutTimeOfLastSilence;
  }


}
