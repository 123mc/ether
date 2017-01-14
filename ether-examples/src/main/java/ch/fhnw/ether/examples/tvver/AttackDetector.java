package ch.fhnw.ether.examples.tvver;

public class AttackDetector {

  private final float ATTACK_DIFFERENCE_THRESHOLD;
  private final float ATTACK_ENERGY_THRESHOLD;

  private boolean attackDetected;

  public AttackDetector(float attackDifferenceThreshold, float attackEnergyThreshold) {
    ATTACK_DIFFERENCE_THRESHOLD = attackDifferenceThreshold;
    ATTACK_ENERGY_THRESHOLD = attackEnergyThreshold;

    System.out.println("AttackDetector: (ATTACK_DIFFERENCE_THRESHOLD: " + attackDifferenceThreshold + ", ATTACK_ENERGY_THRESHOLD: " +  attackEnergyThreshold + ")");
  }

  public void analyze(Frame[] frames) {
    attackDetected = false;
    float difference = calculateDifference(frames[0], frames[1]);
    float highestPeak = maxHighestPeak(frames[0], frames[1]);

    if(difference > ATTACK_DIFFERENCE_THRESHOLD && highestPeak > ATTACK_ENERGY_THRESHOLD) {
      attackDetected = true;
    }
  }

  public boolean attackIsDetected() {
    return attackDetected;
  }

  private float calculateDifference(Frame thisFrame, Frame previousFrame) {
    return (thisFrame.getAbsoluteAverage() - previousFrame.getAbsoluteAverage());
  }

  private float calculateAveragePeak(Frame firstFrame, Frame secondFrame) {
    if(firstFrame.getAbsoluteAverage() > secondFrame.getAbsoluteAverage()) {
      return firstFrame.getAbsoluteAverage();
    }
    return secondFrame.getAbsoluteAverage();
  }

  private float maxHighestPeak(Frame firstFrame, Frame secondFrame) {
    if(firstFrame.getAbsolutePeak() > secondFrame.getAbsolutePeak()) {
      return firstFrame.getAbsolutePeak();
    }
    return secondFrame.getAbsolutePeak();
  }

}
