package ch.fhnw.ether.examples.tvver;

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

  private void shiftAllFramesToRight() {
    for(int i = frames.length - 1; i > 0; i--) {
      frames[i] = frames[i-1];
    }
  }

  public Frame getFrameByIndex(int index) {
    if(index > frames.length-1 || index < 0 || frames[index] == null) {
      return new Frame(); // catch invalid index, or position at index is null
    }
    return frames[index];
  }

  public Frame getLastFrame() {
    return getFrameByIndex(frames.length - 1);
  }

  public Frame[] getLastNFrames(int numberOfFrames) {
    Frame[] lastFrames = new Frame[numberOfFrames];
    for(int i = 0; i < numberOfFrames; i++) {
      lastFrames[i] = getFrameByIndex(i);
    }
    return lastFrames;
  }

}
