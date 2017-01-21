package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.audio.fx.FFT;

import java.util.List;

public class PitchDetector {

    private final FFT fft;
//    private final float PITCH_DETECTION_MIN_POWER_THRESHOLD = 10.0f; // TODO: parameterize me

    public PitchDetector(FFT fastFourierTransform, int pitchDetectionDelayMs, int pitchDetectionCycles) {
        fft = fastFourierTransform;
        System.out.println("PitchDetector");
    }
    
    public Piano analyze(IAudioRenderTarget target) {
        return new Piano(fft, target.getFrame().playOutTime);
    }


}
