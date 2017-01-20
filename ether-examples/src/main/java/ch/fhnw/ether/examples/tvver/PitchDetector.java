package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.audio.fx.FFT;

import java.util.List;

public class PitchDetector {

    private final FFT fft;
    private final Piano piano = new Piano();
    private final int PITCH_DETECTION_DELAY_MS;

    public PitchDetector(FFT fastFourierTransform, int pitchDetectionDelayMs) {
        fft = fastFourierTransform;
        PITCH_DETECTION_DELAY_MS = pitchDetectionDelayMs;
        System.out.println("PitchDetector: (PITCH_DETECTION_DELAY_MS: " + PITCH_DETECTION_DELAY_MS + ")");
    }
    
    public PianoNote analyze(IAudioRenderTarget target, PianoEvent pianoEvent) {

        System.out.println("ready to be detected!");

        // TODO
//        System.out.println("*running "+this.getClass().getName());
//        System.out.println("--20-100 " + fft.power(20.0f,100.0f));
//        System.out.println("--100-300 " + fft.power(100.0f,300.0f));
//        System.out.println("--300-500 " + fft.power(300.0f,500.0f));
//        System.out.println("--500-700 " + fft.power(500.0f,700.0f));
//        Piano piano = new Piano();
//        float[] freqs = piano.getAllFrequencies();
//        for (int j = 0; j+1 < freqs.length; j++) {
//            System.out.println("--"+freqs[j]+"- " + freqs[j+1]+"->"+ fft.power(freqs[j],freqs[j+1]));
//            fft.power(freqs[j],freqs[j+1]);
//        }

        /* check every frequency band of each note */
        float maxPower = 0.0f;
        PianoNote pianoNoteWithMaxPower = new PianoNote(0);
        List<PianoNote> pianoNotes = piano.getPianoNotes();

        for(int i = 0; i < pianoNotes.size(); i++) {
            PianoNote thisPianoNote = pianoNotes.get(i);
            float fLow  = (float) thisPianoNote.getLowBorder();
            float fHigh = (float) thisPianoNote.getHighBorder();

            float power = fft.power(fLow,fHigh);

            if(power > maxPower) {
                pianoNoteWithMaxPower = thisPianoNote;
                maxPower = power;
            }


            // System.out.println("--"+ fLow + "- " + fHigh + " -> "+  + " ... " + thisPianoNote.toString());
        }

        System.out.println("detected piano note ... : " + pianoNoteWithMaxPower.toString());
        return pianoNoteWithMaxPower;
    }

    public boolean pianoEventIsReadyToBePitchDetected(IAudioRenderTarget target, PianoEvent pianoEvent) {
        return (target.getFrame().playOutTime - getDelayInSeconds() > pianoEvent.getPlayOutTimeOfLastSilence());
    }

    private float getDelayInSeconds() {
        return PITCH_DETECTION_DELAY_MS / 1000;
    }

}
