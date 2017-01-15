package ch.fhnw.ether.examples.tvver;


import ch.fhnw.ether.audio.AudioUtilities;
import ch.fhnw.ether.audio.fx.BandsFFT;
import ch.fhnw.ether.audio.fx.FFT;

/**
 * Created by meins on 15.01.2017.
 */
public class PitchDetector {
    private final Frame[] frames;
    private final Piano piano;
    private PianoNote detectedPianoNote;


    private final FFT fft   = new FFT(5, AudioUtilities.Window.HAMMING);
    private final BandsFFT bandsFFT;


    public PitchDetector(Frame[] frames){
        this.frames = frames;
        piano = new Piano();
        detectedPianoNote = null;
        bandsFFT = new BandsFFT(fft, piano.getAllFrequencies());
    }
    
    public void call(){
        //fft
        //http://royvanrijn.com/blog/2010/06/creating-shazam-in-java/
        float maxFrequency = (float)piano.findPianoNoteByScientificName("C5").getFrequency();
        detectedPianoNote = piano.findPianoNoteByFrequency(maxFrequency);
    }

    public PianoNote getDetectedPianoNote(){
        return detectedPianoNote;
    }
}
