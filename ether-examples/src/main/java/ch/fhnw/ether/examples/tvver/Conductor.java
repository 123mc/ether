package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.IAudioRenderTarget;

public class Conductor {

  /* ATTACK DETECTOR PARAMETERS */
  public static final float ATTACK_DIFFERENCE_THRESHOLD = 0.005f;
  public static final float ATTACK_ENERGY_THRESHOLD = 0.055f;
  public static final int   ATTACK_SUSPENSION_MS = 700;
  public static final int   ATTACK_BUFFER_SIZE = 3;

  /* SILENCE DETECTOR PARAMETERS */
  public static final float SILENCE_THRESHOLD = 0.0001f;
  public static final int   SILENCE_BUFFER_SIZE = 5;

  /* PITCH DETECTOR PARAMTERS */
  public static final int PITCH_DETECTION_DELAY_MS = 0;
  public static final int PITCH_DETECTION_FFT_CYCLES = 3;

  private final PianoEvents pianoEvents;
  private volatile double playOutTimeOfLastSilence;

  MadSchPCM2MIDI madSchPcm2Midi;

  public Conductor(MadSchPCM2MIDI pcm2midi) {
    madSchPcm2Midi = pcm2midi;
    pianoEvents = new PianoEvents();
    playOutTimeOfLastSilence = 0.0d;
  }

  public void noteOn(int midiNumber) {
    madSchPcm2Midi.noteOn(midiNumber, 64);
  }

  public void setAttackDetected(IAudioRenderTarget target) {
      pianoEvents.add(new PianoEvent(target.getFrame().playOutTime, playOutTimeOfLastSilence, PITCH_DETECTION_DELAY_MS, PITCH_DETECTION_FFT_CYCLES));
  }

  public PianoEvents getUndetectedPianoEvents() {
    return pianoEvents.getUndetectedPianoEvents();
  }

  public void setLastSilence(double playOutTime) { playOutTimeOfLastSilence = playOutTime;  }

}
