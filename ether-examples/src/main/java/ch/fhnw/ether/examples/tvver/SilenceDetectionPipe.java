package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.media.AbstractRenderCommand;
import ch.fhnw.ether.media.RenderCommandException;

public class SilenceDetectionPipe extends AbstractRenderCommand<IAudioRenderTarget> {

    private final Conductor conductor;
    private final SilenceDetector silenceDetector;

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
        double playOutTimeOfLastSilence = silenceDetector.analyze(target); // returns negative value if no silence detected
        if(playOutTimeOfLastSilence > 0.0d) {
          conductor.setLastSilence(playOutTimeOfLastSilence);
        }
      } catch (Throwable t) {
        throw new RenderCommandException(t);
      }
    }

}
