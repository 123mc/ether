package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.audio.fx.FFT;
import ch.fhnw.ether.media.AbstractRenderCommand;
import ch.fhnw.ether.media.RenderCommandException;


public class PitchDetectionPipe extends AbstractRenderCommand<IAudioRenderTarget> {

    AbstractPCM2MIDI madSchPcm2Midi;
    FFT fft;

    public PitchDetectionPipe(FFT fastfuriousTransform, AbstractPCM2MIDI pcm2mid) {
        madSchPcm2Midi = pcm2mid;
        fft = fastfuriousTransform;
    }

    @Override
    protected void init(IAudioRenderTarget target) throws RenderCommandException {

    }

    @Override
    protected void run(IAudioRenderTarget target) throws RenderCommandException {
        System.out.println("*running "+this.getClass().getName());
        System.out.println("--20-100 " + fft.power(20.0f,100.0f));
        System.out.println("--100-300 " + fft.power(100.0f,300.0f));
        System.out.println("--300-500 " + fft.power(300.0f,500.0f));
        System.out.println("--500-700 " + fft.power(500.0f,700.0f));
        Piano piano = new Piano();
        float[] freqs = piano.getAllFrequencies();
        for (int i = 0; i+1 < freqs.length; i++) {
            System.out.println("--"+freqs[i]+"- " + freqs[i+1]+"->"+ fft.power(freqs[i],freqs[i+1]));
            fft.power(freqs[i],freqs[i+1]);
        }

    }

}
