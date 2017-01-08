package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.AudioFrame;

public class OnsetDetector {

  /*
  *
  * The OnsetDetector compares the difference of the "average energy" of a frame to the previous frame.
  *
  * The OnsetDetector has three states: IDLING, DETECTION and DECAY.
  *
  * It starts with "IDLING", which means the difference between the compared frames are not significant enough.
  *
  * If the difference between two frame averages exceeds a certain threshold (AVERAGE_DIFFERENCE_THRESHOLD) the "DETECTION" Phase starts.
  *
  * Within a "DETECTION" phase, that threshold has to be exceeded multiple
  * times (MINIMUM_THRESHOLDS_TO_EXCEED_WITHIN_DETECTION_PHASE), in order that an onset is detected.
  * The "DETECTION" phase is only valid for a certain number of cycles. It can end in 2 ways:
  *
  *    1) An Onset is detected !
  *
  *      If an onset is detected, the "DETECTION" phase ends and the "DECAY" phase starts.
  *
  *      Within the "DECAY" phase (DECAY_PHASE_CYCLES), there won't be any
  *      comparisons between frame and previous frame. No onsets can be detected during this time.
  *
  *    2) No Onset detected
  *
  *    If no onset is detected (the threshold has not exceeded multiple times, the "IDLING" phase starts (again).
  *
  *
  * */

  private static float AVERAGE_DIFFERENCE_THRESHOLD                         = 0.05f;
  private static int   FRAME_QUEUE_CAPACITY                                 = 5;
  private static int   DECAY_PHASE_CYCLES = 25;
  private static int   DETECTION_PHASE_CYCLES = 5;
  private static int   MINIMUM_THRESHOLDS_TO_EXCEED_WITHIN_DETECTION_PHASE  = 3;

  private int cyclesUntilEndOfDetectionPhase = -1;
  private int cyclesUntilEndOfDecayPhase = -1;
  private int thresholdReachedWithinDetectionPhase = 0;

  private FrameQueue frameQueue;

  public OnsetDetector() {
    frameQueue = new FrameQueue(FRAME_QUEUE_CAPACITY);
  }

  public void feedFrame(AudioFrame audioFrame) {
    Frame frame = new Frame(audioFrame);
    frameQueue.add(frame);

    analyzeNewestTwoFrames();
  }

  public boolean onsetIsDetected() {
    boolean answer = false;

    if(thresholdReachedWithinDetectionPhase >= MINIMUM_THRESHOLDS_TO_EXCEED_WITHIN_DETECTION_PHASE) {
      answer = true;
      startDecayPhase();
    }

    return answer;
  }

  private void analyzeNewestTwoFrames() {

    if(decayIsOngoing()) {
      decrementDecayPhase();
      return;
    }

    Frame newestFrame       = frameQueue.getFrameByIndex(0);
    Frame secondNewestFrame = frameQueue.getFrameByIndex(1);

    if (secondNewestFrame == null) {
      return;
    }

    float differenceBetweenFrameAverages = newestFrame.getAbsoluteAverage() - secondNewestFrame.getAbsoluteAverage();

    if(differenceBetweenFrameAverages > AVERAGE_DIFFERENCE_THRESHOLD) {
      incrementThresholdReached();
      startDetectionCycle();
    }
    else {
      decrementDetectionPhase();
    }
  }

  private void startDetectionCycle() {
    cyclesUntilEndOfDetectionPhase = DETECTION_PHASE_CYCLES;
  }

  private void stopDetectionPhase() {
    cyclesUntilEndOfDetectionPhase = -1;
    thresholdReachedWithinDetectionPhase  = 0;
  }

  private boolean detectionPhaseIsOngoing() {
    return (cyclesUntilEndOfDetectionPhase >= 0);
  }

  private void startDecayPhase() {
    if(decayIsOngoing()) {
      return;
    }

    stopDetectionPhase();
    cyclesUntilEndOfDecayPhase = DECAY_PHASE_CYCLES;
  }

  private boolean decayIsOngoing() {
    return (cyclesUntilEndOfDecayPhase >= 0);
  }

  private void decrementDecayPhase() {
    if(cyclesUntilEndOfDecayPhase > -1) {
      cyclesUntilEndOfDecayPhase -= 1;
    }
  }

  private void decrementDetectionPhase() {
    if(cyclesUntilEndOfDetectionPhase > -1) {
      cyclesUntilEndOfDetectionPhase -= 1;
    }
  }

  private void incrementThresholdReached() {
    thresholdReachedWithinDetectionPhase += 1;
  }

  public String status() {
    if(decayIsOngoing()) {
      return "DECAYING";
    } else if(detectionPhaseIsOngoing()) {
      return "DETECTING";
    }

    return "IDLING";
  }

}
