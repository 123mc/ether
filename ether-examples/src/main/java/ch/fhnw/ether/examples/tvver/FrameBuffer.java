package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.AudioFrame;

public class FrameBuffer {

  private Frame[] frames;

  public FrameBuffer(int capacity) {
    frames = new Frame[capacity];
  }

  public void add(Frame frame) {
    if(frames[0] != null) {
      shiftAllFramesToRight();
    }
    frames[0] = frame;
  }

  public void add(AudioFrame audioFrame) {
    add(new Frame(audioFrame));
  }

  private void shiftAllFramesToRight() {
    for(int i = frames.length - 1; i > 0; i--) {
      frames[i] = frames[i-1];
    }
  }

  public Frame get(int index) {
    if(index > frames.length-1 || index < 0 || frames[index] == null) {
      return new Frame(); // catch invalid index, or position at index is null
    }
    return frames[index];
  }

  public int size() {
    return frames.length;
  }

//  public Frame getLastFrame() {
//    return getFrameByIndex(frames.length - 1);
//  }
//
//  public Frame[] getLastNFrames(int numberOfFrames) {
//    Frame[] lastFrames = new Frame[numberOfFrames];
//    for(int i = 0; i < numberOfFrames; i++) {
//      lastFrames[i] = getFrameByIndex(i);
//    }
//    return lastFrames;
//  }



}
