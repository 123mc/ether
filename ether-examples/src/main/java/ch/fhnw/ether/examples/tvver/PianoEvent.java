package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.IAudioRenderTarget;

import java.util.ArrayList;

public class PianoEvent {

  private volatile PianoNote pianoNote;
  private final IAudioRenderTarget targetWhereAttackWasDetected;

  public PianoEvent(IAudioRenderTarget target, IAudioRenderTarget targetOfLastSilence) {
    targetWhereAttackWasDetected = target;
    targetOfLastSilence = targetOfLastSilence; // could be used for the onset (= last silent part before the attack)
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

}
