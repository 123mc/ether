package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.IAudioRenderTarget;

public class AttackDetector {

  private final float ATTACK_DIFFERENCE_THRESHOLD;
  private final float ATTACK_ENERGY_THRESHOLD;
  private final float ATTACK_SUSPENSION_MS;
  private final int   ATTACK_BUFFER_SIZE;

  private final FrameBuffer frameBuffer;

  private volatile double suspendedUntilPlayOutTime = 0.0f;

  public AttackDetector(float attackDifferenceThreshold, float attackEnergyThreshold, int attackSuspensionMs, int attackBufferSize) {
    ATTACK_DIFFERENCE_THRESHOLD = attackDifferenceThreshold;
    ATTACK_ENERGY_THRESHOLD = attackEnergyThreshold;
    ATTACK_SUSPENSION_MS = attackSuspensionMs;
    ATTACK_BUFFER_SIZE = attackBufferSize;
    frameBuffer = new FrameBuffer(ATTACK_BUFFER_SIZE);

    System.out.println("AttackDetector: (ATTACK_DIFFERENCE_THRESHOLD: " + attackDifferenceThreshold + ", ATTACK_ENERGY_THRESHOLD: " +  attackEnergyThreshold + ", ATTACK_SUSPENSION_MS: " + ATTACK_SUSPENSION_MS + ", ATTACK_BUFFER_SIZE: " + ATTACK_BUFFER_SIZE +")");
  }

  public IAudioRenderTarget analyze(IAudioRenderTarget target) {
    frameBuffer.add(target.getFrame());

    if(!isSuspended()) {
      float difference = calculateAverageDifference();
      float highestPeak = maxHighestPeak();

      if(difference > ATTACK_DIFFERENCE_THRESHOLD && highestPeak > ATTACK_ENERGY_THRESHOLD) {
        suspendedUntilPlayOutTime = target.getFrame().playOutTime + getAttackSuspensionInSeconds();
        return target;
      }
    }

    return null;
  }

  private float calculateAverageDifference() {
    float averageDifferenceSum = 0.0f;

    for(int i = 0; i < frameBuffer.size(); i++) {
      averageDifferenceSum += frameBuffer.get(i).getAbsoluteAverage();
    }

    return averageDifferenceSum / frameBuffer.size();
  }

  private float maxHighestPeak() {

    float highestPeak = 0.0f;

    for(int i = 0; i < frameBuffer.size(); i++) {
      if(frameBuffer.get(i).getAbsolutePeak() > highestPeak) {
        highestPeak = frameBuffer.get(i).getAbsolutePeak();
      }
    }
    return highestPeak;
  }

  public boolean isSuspended() {
    return suspendedUntilPlayOutTime > frameBuffer.getNewest().getPlayOutTime();
  }


  public double getAttackSuspensionInSeconds() {
    return (ATTACK_SUSPENSION_MS / 1000);
  }

}
