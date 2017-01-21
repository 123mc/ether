package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.IAudioRenderTarget;

import java.util.*;

public class PianoEvent {

  private volatile PianoNote detectedPianoNote;

  private final double playOutTimeOfAttack;
  private final double playOutTimeOfLastSilence;
  private final int    pitchDetectionDelayMs;
  private final int    pitchDetectionFftCycles;

  private volatile ArrayList<Piano> detectedPianos;

  private volatile boolean dismissed = false;     // -1: Attack dismissed by FFT, 0 not yet verified, 1: approved by FFT
  private volatile boolean verified = false;

  private static final HashMap<Integer, Float> MIN_POWER_PER_OCTAVE = new HashMap<Integer, Float>() {{
    put(0, 1.0f);
    put(1, 8.0f);
    put(2, 6.0f);
    put(3, 4.0f);
    put(4, 2.0f);
    put(5, 1.0f);
    put(6, 0.5f);
    put(7, 0.25f);
    put(8, 0.115f);
    put(9, 0.08f);
  }};

  public PianoEvent(double attack, double lastSilence, int delayMs, int fftCycles) {
    playOutTimeOfAttack = attack;
    playOutTimeOfLastSilence = lastSilence;
    pitchDetectionDelayMs = delayMs;
    pitchDetectionFftCycles = fftCycles;
    detectedPianos = new ArrayList<>();
  }

//  public boolean isDismissed() {
//    return dismissed;
//  }
//
//  public void dismiss() {
//    dismissed = true;
//  }
//
//  public boolean isVerified() {
//    return verified;
//  }
//
//  public void verify() {
//    verified = true;
//  }

  public boolean isDetected() {
    return (detectedPianoNote != null /* && !isDismissed() */);
  }

  public PianoNote getDetectedPianoNote() {
    if(isDetected()) {
      return detectedPianoNote;
    }
    return null;
  }

  public void setDetectedPianoNote(PianoNote pn) {
    if(detectedPianoNote != null) {
      return;
    } // do not overwrite

    detectedPianoNote = pn;
  }

  public double getPlayOutTimeOfAttack() {
    return playOutTimeOfAttack;
  }

  public double getPlayOutTimeOfLastSilence() {
    return playOutTimeOfLastSilence;
  }

  public boolean isReadyToBePitchDetected(IAudioRenderTarget target) {
    return (target.getFrame().playOutTime - getDelayInSeconds() > getPlayOutTimeOfLastSilence());
  }

  private float getDelayInSeconds() {
    return pitchDetectionDelayMs / 1000;
  }

  public void addPiano(Piano p) {
    detectedPianos.add(p);
  }

  public boolean numberOfFftCyclesIsReached() {
    return detectedPianos.size() >= pitchDetectionFftCycles;
  }

  public boolean isReadyToDetectPianoNote() {
    return /* !isDismissed() && */ !isDetected() && numberOfFftCyclesIsReached();
  }

  public void detectPianoNote() {

    System.out.println("Trying to detect piano note with attack at play out time:  " + getPlayOutTimeOfAttack() );
/*
    System.out.println("Available pianos with highest power in spectrum");
    System.out.println("----");

    System.out.println(detectedPianos.get(0).getHeaderRow());
*/

    ArrayList<String> pianoNotesWithHighestPower = new ArrayList<>();

    for(Piano piano : detectedPianos) {
      pianoNotesWithHighestPower.add(piano.getPianoNoteWithHighestSpectrumPower().getScientificName());
    }

    HashMap<String, Integer> occurrencesOfPianoNotesWithHighestPower = new HashMap<>();

    for(String pianoNoteScientificName : pianoNotesWithHighestPower) {
      if(!occurrencesOfPianoNotesWithHighestPower.containsKey(pianoNoteScientificName)) {
        int occurrences = Collections.frequency(pianoNotesWithHighestPower, pianoNoteScientificName);
        occurrencesOfPianoNotesWithHighestPower.put(pianoNoteScientificName, occurrences);
      }
    }

    int maxOccurences = 0;
    String pianoNoteWithMaxOccurences = "";

    Iterator it = occurrencesOfPianoNotesWithHighestPower.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry) it.next();

      System.out.println("Key " + pair.getKey() + " has been detected " + pair.getValue() + " times ");
      if(Integer.valueOf((int) pair.getValue()) > maxOccurences){
        pianoNoteWithMaxOccurences = (String) pair.getKey();
      }

      it.remove(); // avoids a ConcurrentModificationException
    }

    System.out.println(" -> Key " + pianoNoteWithMaxOccurences + " wins ");
    setDetectedPianoNote(new Piano().findPianoNoteByScientificName(pianoNoteWithMaxOccurences));

  }


  public boolean hasEnoughPowerInAtLeastOneOctave(Piano piano) {

    Iterator it = MIN_POWER_PER_OCTAVE.entrySet().iterator();
    while(it.hasNext()) {

      Map.Entry pair = (Map.Entry) it.next();

      int octave = (int) pair.getKey();
      float minPower = (float) pair.getValue();

      if (minPower < piano.getAveragePowerInOctave(octave)) {
        return true;
      }
      it.remove(); // avoids a ConcurrentModificationException

    }
    return false;
  }


}
