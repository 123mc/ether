package ch.fhnw.ether.examples.tvver;

public class FrameQueue {

  private Frame[] frames;

  public FrameQueue(int capacity) {
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
    if(index > frames.length-1 || index < 0) {
      return new Frame(); // catch invalid index
    }
    return frames[index];
  }

}
