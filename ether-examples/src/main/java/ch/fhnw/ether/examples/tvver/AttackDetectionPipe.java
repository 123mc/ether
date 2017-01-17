package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.media.AbstractRenderCommand;
import ch.fhnw.ether.media.RenderCommandException;

import javax.sound.midi.MidiEvent;
import java.util.ArrayList;
import java.util.List;

public class AttackDetectionPipe extends AbstractRenderCommand<IAudioRenderTarget> {

    SignalAnalyzer signalAnalyzer;

    public AttackDetectionPipe(SignalAnalyzer sa) {
        signalAnalyzer = sa;
    }

    @Override
    protected void init(IAudioRenderTarget target) throws RenderCommandException {
    }

    @Override
    protected void run(IAudioRenderTarget target) throws RenderCommandException {
        try {

            signalAnalyzer.feedFrame(target.getFrame());

            if(signalAnalyzer.pianoNoteDetected()) {
                PianoNote detectedPianoNote = signalAnalyzer.getLastPianoEvent().getPianoNote();
                System.out.println("////////ONSET DETECTED");
                System.out.println("Note: " + detectedPianoNote.getMidiNumber() + detectedPianoNote.toString());
                signalAnalyzer.noteOn(detectedPianoNote.getMidiNumber());
            } else {

            }

        } catch (Throwable t) {
            throw new RenderCommandException(t);
        }
    }
}
