package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.audio.fx.FFT;
import ch.fhnw.ether.media.AbstractRenderCommand;
import ch.fhnw.ether.media.RenderCommandException;


public class PitchDetectionPipe extends AbstractRenderCommand<IAudioRenderTarget> {

    private final Conductor conductor;
    private final FFT fft;
    private final PitchDetector pitchDetector;

    public PitchDetectionPipe(FFT fastfuriousTransform, Conductor c) {
        conductor = c;
        fft = fastfuriousTransform;
        pitchDetector = new PitchDetector(fft, conductor.PITCH_DETECTION_DELAY_MS);
    }

    @Override
    protected void init(IAudioRenderTarget target) throws RenderCommandException {
    }

    @Override
    protected void run(IAudioRenderTarget target) throws RenderCommandException {
        PianoEvents undetectedPianoEvents = conductor.getUndetectedPianoEvents();

        for(int i = 0; i < undetectedPianoEvents.size(); i++) {
            PianoEvent pianoEvent = undetectedPianoEvents.get(i);
            if(pitchDetector.pianoEventIsReadyToBePitchDetected(target, pianoEvent)) {
                PianoNote pianoNote = pitchDetector.analyze(target, pianoEvent);
                pianoEvent.setPianoNote(pianoNote);
                conductor.noteOn(pianoNote.getMidiNumber());
            }
        }
    }

}
