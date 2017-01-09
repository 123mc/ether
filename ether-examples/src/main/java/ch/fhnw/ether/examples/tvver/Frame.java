package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.AudioFrame;

public class Frame {

  private final AudioFrame audioFrame;

  public Frame() { audioFrame = new AudioFrame(0,1, 441000, null); }

  public Frame(AudioFrame frame) {
    audioFrame = frame;
  }

  public float getAbsoluteAverage() {

    if(length() < 1) {
      return 0f;
    }

    float sum = 0f;

    for(float sample : audioFrame.getMonoSamples()) {
      sum += sample;
    }

    return sum / length();
  }

  public int length() {
    return audioFrame.getMonoSamples().length;
  }

  public float getPeak() {
    float peak = 0f;

    for(float sample : audioFrame.getMonoSamples()) {
      if(Math.abs(sample) > peak) {
        peak = Math.abs(sample);
      }
    }

    return peak;
  }

}
