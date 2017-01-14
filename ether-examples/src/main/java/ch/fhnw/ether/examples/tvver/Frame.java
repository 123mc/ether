package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.AudioFrame;

public class Frame {

  private final AudioFrame audioFrame;

  public Frame() { audioFrame = new AudioFrame(0,1, 441000, null); }

  public Frame(AudioFrame frame) {
    audioFrame = frame;
  }

  public float getAbsoluteAverage() {

    float sum = 0f;

    if(audioFrameIsEmpty()) {
      return sum;
    }

    for(float sample : audioFrame.getMonoSamples()) {
      sum += sample;
    }

    return Math.abs(sum) / audioFrame.getMonoSamples().length;
  }

  public float getAbsolutePeak() {
    float peak = 0f;

    if(audioFrameIsEmpty()) {
      return peak;
    }

    for(float sample : audioFrame.getMonoSamples()) {
      if(Math.abs(sample) > peak) {
        peak = Math.abs(sample);
      }
    }

    return peak;
  }

  public long getSTime() {
    return audioFrame.sTime;
  }

  public boolean audioFrameIsEmpty() {
    return audioFrame.samples == null;
  }

}
