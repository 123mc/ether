package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.IAudioRenderTarget;

public class SilenceDetector {

  private final float SILENCE_THRESHOLD;

  FrameBuffer frameBuffer;


  public SilenceDetector(float silenceThreshold, int bufferSize) {
    SILENCE_THRESHOLD = silenceThreshold;
    frameBuffer = new FrameBuffer(bufferSize);

    System.out.println("SilenceDetector: (SILENCE_THRESHOLD: " + silenceThreshold + ", bufferSize: " + bufferSize + ")");
  }

  public IAudioRenderTarget analyze(IAudioRenderTarget target) {
    frameBuffer.add(target.getFrame());

    if(averageOfBufferedFrames() < SILENCE_THRESHOLD) {
      return target;
    }

    return null;
  }

  private float averageOfBufferedFrames() {
    float sumOfFrameAverages = 0f;

    for(int i = 0; i < frameBuffer.size(); i++) {
      Frame frame = frameBuffer.get(i);
      sumOfFrameAverages += frame.getAbsoluteAverage();
    }

    return sumOfFrameAverages / frameBuffer.size();
  }

}
