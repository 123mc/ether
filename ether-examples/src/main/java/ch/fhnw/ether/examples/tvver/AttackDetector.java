package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.IAudioRenderTarget;

public class AttackDetector {

  private final float ATTACK_DIFFERENCE_THRESHOLD;
  private final float ATTACK_ENERGY_THRESHOLD;

  private final FrameBuffer frameBuffer;
  private static final int ATTACK_DETECTOR_FRAME_BUFFER_SIZE = 2;

  public AttackDetector(float attackDifferenceThreshold, float attackEnergyThreshold) {
    ATTACK_DIFFERENCE_THRESHOLD = attackDifferenceThreshold;
    ATTACK_ENERGY_THRESHOLD = attackEnergyThreshold;
    frameBuffer = new FrameBuffer(ATTACK_DETECTOR_FRAME_BUFFER_SIZE);

    System.out.println("AttackDetector: (ATTACK_DIFFERENCE_THRESHOLD: " + attackDifferenceThreshold + ", ATTACK_ENERGY_THRESHOLD: " +  attackEnergyThreshold + ")");
  }

  public IAudioRenderTarget analyze(IAudioRenderTarget target) {
    frameBuffer.add(target.getFrame());

    float difference = calculateDifference(frameBuffer.get(0), frameBuffer.get(1));
    float highestPeak = maxHighestPeak(frameBuffer.get(0), frameBuffer.get(1));

    if(difference > ATTACK_DIFFERENCE_THRESHOLD && highestPeak > ATTACK_ENERGY_THRESHOLD) {
      return target;
    }
    return null;
  }

  private float calculateDifference(Frame thisFrame, Frame previousFrame) {
    return (thisFrame.getAbsoluteAverage() - previousFrame.getAbsoluteAverage());
  }

  private float maxHighestPeak(Frame firstFrame, Frame secondFrame) {
    if(firstFrame.getAbsolutePeak() > secondFrame.getAbsolutePeak()) {
      return firstFrame.getAbsolutePeak();
    }
    return secondFrame.getAbsolutePeak();
  }

}
