package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.AudioFrame;

public class SignalAnalyzer {

  private static float ATTACK_DIFFERENCE_THRESHOLD = 0.003f;
  private static float ATTACK_ENERGY_THRESHOLD = 0.05f;

  private static int FRAME_BUFFER_SIZE = 5;  /* minimum 5 */
  private static float SILENCE_THRESHOLD = 0.0001f;

  private SilenceDetector silenceDetector;
  private AttackDetector attackDetector;

  private FrameBuffer frameBuffer;

  private PianoEvents pianoEvents;

  public SignalAnalyzer() {
    pianoEvents = new PianoEvents();
    frameBuffer = new FrameBuffer(FRAME_BUFFER_SIZE);
    silenceDetector = new SilenceDetector(SILENCE_THRESHOLD);
    attackDetector = new AttackDetector(ATTACK_DIFFERENCE_THRESHOLD, ATTACK_ENERGY_THRESHOLD);
  }

  public void feedFrame(AudioFrame audioFrame) {
    Frame frame = new Frame(audioFrame);
    frameBuffer.add(frame);

    analyze();
  }

  public boolean pianoNoteDetected() {
    if(pianoEvents.newPianoNoteIsDetected()) {
      return true;
    }
    return false;
  }

  public PianoEvent getLastPianoEvent() {
    return pianoEvents.getLast();
  }

  private void analyze() {
    silenceDetector.analyze(frameBuffer.getLastNFrames(5));

    PianoEvent lastPianoEvent = pianoEvents.getLast();

    if(lastPianoEvent.isAlive()) {
      System.out.println("a piano event is alive");
      lastPianoEvent.feedFrame(frameBuffer.getLastFrame());
    } else {

      attackDetector.analyze(frameBuffer.getLastNFrames(2));

      if (attackDetector.attackIsDetected()) {
        System.out.println("attack detected");
        pianoEvents.add(new PianoEvent(silenceDetector.getLastSilence(), 100)); // 0.29 seconds
      } else {
        System.out.println("idling");
      }

    }

  }

}
