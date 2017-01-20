package ch.fhnw.ether.examples.tvver;


import ch.fhnw.ether.audio.AudioUtilities;
import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.audio.fx.BandsFFT;
import ch.fhnw.ether.audio.fx.FFT;

public class PitchDetector {

    private final FFT fft;
    private final Piano piano = new Piano();

    public PitchDetector(FFT fastFourierTransform){
        fft = fastFourierTransform;
    }
    
    public PianoNote analyze(IAudioRenderTarget target) {
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

        return piano.findPianoNoteByScientificName("C5"); // REMOVE ME

    }

}
