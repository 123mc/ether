package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.IAudioRenderTarget;

public class AttackDetector {

  private final float ATTACK_DIFFERENCE_THRESHOLD;
  private final float ATTACK_ENERGY_THRESHOLD;
  private final float ATTACK_SUSPENSION_MS;

  private final FrameBuffer frameBuffer;
  private static final int ATTACK_DETECTOR_FRAME_BUFFER_SIZE = 2;

  private volatile double suspendedUntilPlayOutTime = 0.0f;

  public AttackDetector(float attackDifferenceThreshold, float attackEnergyThreshold, int attackSuspensionMs) {
    ATTACK_DIFFERENCE_THRESHOLD = attackDifferenceThreshold;
    ATTACK_ENERGY_THRESHOLD = attackEnergyThreshold;
    ATTACK_SUSPENSION_MS = attackSuspensionMs;
    frameBuffer = new FrameBuffer(ATTACK_DETECTOR_FRAME_BUFFER_SIZE);

    System.out.println("AttackDetector: (ATTACK_DIFFERENCE_THRESHOLD: " + attackDifferenceThreshold + ", ATTACK_ENERGY_THRESHOLD: " +  attackEnergyThreshold + ", ATTACK_SUSPENSION_MS: " + ATTACK_SUSPENSION_MS + ")");
  }

  public IAudioRenderTarget analyze(IAudioRenderTarget target) {
    frameBuffer.add(target.getFrame());

    if(!isSuspended()) {
      float difference = calculateDifference(frameBuffer.get(0), frameBuffer.get(1));
      float highestPeak = maxHighestPeak(frameBuffer.get(0), frameBuffer.get(1));

      if(difference > ATTACK_DIFFERENCE_THRESHOLD && highestPeak > ATTACK_ENERGY_THRESHOLD) {
        suspendedUntilPlayOutTime = target.getFrame().playOutTime + getAttackSuspensionInSeconds();
        return target;
      }
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

  public boolean isSuspended() {
    return suspendedUntilPlayOutTime > frameBuffer.getNewest().getPlayOutTime();
  }


  public double getAttackSuspensionInSeconds() {
    return (ATTACK_SUSPENSION_MS / 1000);
  }

}
