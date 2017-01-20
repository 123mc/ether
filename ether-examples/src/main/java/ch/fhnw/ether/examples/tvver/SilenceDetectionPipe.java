package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.media.AbstractRenderCommand;
import ch.fhnw.ether.media.RenderCommandException;

public class SilenceDetectionPipe extends AbstractRenderCommand<IAudioRenderTarget> {

    private final Conductor conductor;
    private final SilenceDetector silenceDetector;
    private IAudioRenderTarget targetOfLastSilence = null;

    public SilenceDetectionPipe(Conductor c) {
      conductor = c;
      silenceDetector = new SilenceDetector(conductor.SILENCE_THRESHOLD, conductor.SILENCE_BUFFER_SIZE);
    }

    @Override
    protected void init(IAudioRenderTarget target) throws RenderCommandException {
    }

    @Override
    protected void run(IAudioRenderTarget target) throws RenderCommandException {
      try {
        targetOfLastSilence = silenceDetector.analyze(target); // returns null if no attack was detected
        conductor.setLastSilence(targetOfLastSilence);
      } catch (Throwable t) {
        throw new RenderCommandException(t);
      }
    }

}
