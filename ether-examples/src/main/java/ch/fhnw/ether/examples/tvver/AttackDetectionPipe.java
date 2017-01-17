package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.media.AbstractRenderCommand;
import ch.fhnw.ether.media.RenderCommandException;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import java.util.ArrayList;
import java.util.List;

public class AttackDetectionPipe extends AbstractRenderCommand<IAudioRenderTarget> {
    private final List<List<MidiEvent>> midiRef = new ArrayList<>();
    private       int                   msTime;

    SignalAnalyzer signalAnalyzer = new SignalAnalyzer();

    AbstractPCM2MIDI madSchPcm2Midi;

    public AttackDetectionPipe(AbstractPCM2MIDI pcm2mid) {
        madSchPcm2Midi = pcm2mid;
    }

    @Override
    protected void init(IAudioRenderTarget target) throws RenderCommandException {
    }

    @Override
    protected void run(IAudioRenderTarget target) throws RenderCommandException {
        System.out.println("*running "+this.getClass().getName());
        try {

            signalAnalyzer.feedFrame(target.getFrame());

            if(signalAnalyzer.pianoNoteDetected()) {
                PianoNote detectedPianoNote = signalAnalyzer.getLastPianoEvent().getPianoNote();
                System.out.println("////////ONSET DETECTED");
                System.out.println("Note: " + detectedPianoNote.getMidiNumber() + detectedPianoNote.toString());
                madSchPcm2Midi.noteOn(detectedPianoNote.getMidiNumber(), detectedPianoNote.getVelocity());
            } else {

            }

        } catch (Throwable t) {
            throw new RenderCommandException(t);
        }
    }
}
