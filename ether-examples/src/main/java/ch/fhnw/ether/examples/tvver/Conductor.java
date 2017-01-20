package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.IAudioRenderTarget;

public class Conductor {

  /* ATTACK DETECTOR PARAMETERS */
  public static final float ATTACK_DIFFERENCE_THRESHOLD = 0.003f;
  public static final float ATTACK_ENERGY_THRESHOLD = 0.05f;

  /* SILENCE DETECTOR PARAMETERS */
  public static final float SILENCE_THRESHOLD = 0.0001f;
  public static final int   SILENCE_BUFFER_SIZE = 5;

  private final PianoEvents pianoEvents;
  private volatile IAudioRenderTarget targetOfLastSilence;
  MadSchPCM2MIDI madSchPcm2Midi;

  public Conductor(MadSchPCM2MIDI pcm2midi) {
    madSchPcm2Midi = pcm2midi;
    pianoEvents = new PianoEvents();
    targetOfLastSilence = null;
  }

  public void noteOn(int midiNumber) {
    madSchPcm2Midi.noteOn(midiNumber, 64);
  }

  public void setAttackDetected(IAudioRenderTarget target) {
    if(target != null) {
      pianoEvents.add(new PianoEvent(target, targetOfLastSilence));
    }
  }

  public PianoEvents getUndetectedPianoEvents() {
    return pianoEvents.getUndetectedPianoEvents();
  }

  public void setLastSilence(IAudioRenderTarget target) {
    if(target != null) {
      targetOfLastSilence = target;
    }
  }

}
