package ch.fhnw.ether.examples.tvver;

public class SilenceDetector {

  private final float SILENCE_THRESHOLD;
  private long lastSilence = 0;

  public SilenceDetector(float silenceThreshold) {
    SILENCE_THRESHOLD = silenceThreshold;
    System.out.println("SilenceDetector: (SILENCE_THRESHOLD: " + silenceThreshold + ")");
  }

  public void analyze(Frame[] frames) {

    /* Rule: if frames average is less than SILENCE_THRESHOLD, the lastSilence will be updated */

    float sumOfFrameAverages = 0f;

    for(int i = 0; i < frames.length; i++) {
      sumOfFrameAverages += frames[i].getAbsoluteAverage();
    }

    float averagesOFrames = sumOfFrameAverages / frames.length;

    if(averagesOFrames < SILENCE_THRESHOLD) {
      updateLastSilence(frames[frames.length-1].getSTime());
    }
  }

  public long getLastSilence() {
    return lastSilence;
  }

  private void updateLastSilence(long sTime) {
    lastSilence = sTime;
  }

}
