package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.AudioUtilities;
import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.audio.fx.BandsFFT;
import ch.fhnw.ether.audio.fx.DCRemove;
import ch.fhnw.ether.audio.fx.FFT;
import ch.fhnw.ether.media.*;

import javax.sound.midi.*;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * A fake PCM2MIDI implementation which jitters the reference notes
 * and signals the jittered reference notes. 
 * 
 * @author simon.schubiger@fhnw.ch
 *
 */
public class MadSchPCM2MIDI extends AbstractPCM2MIDI {

	SignalAnalyzer signalAnalyzer;

	public MadSchPCM2MIDI(File track) throws UnsupportedAudioFileException, IOException, MidiUnavailableException, InvalidMidiDataException, RenderCommandException {
		super(track, EnumSet.of(Flags.REPORT, Flags.WAVE));
		signalAnalyzer = new SignalAnalyzer(this);
	}

	@Override
	protected void initializePipeline(RenderProgram<IAudioRenderTarget> program) {

		program.addLast(new DCRemove());
		program.addLast(new AttackDetectionPipe(signalAnalyzer));
		FFT fft = new FFT(40, AudioUtilities.Window.HANN);
		fft.addLast(new PitchDetectionPipe(fft, this));
		program.addLast(fft);

	}

	// private static final Parameter P = new Parameter("p", "Probability", 0, 1, 1);

	// public class PCM2MIDIFFT extends AbstractRenderCommand<IAudioRenderTarget> {
 			// TODO implement FFT stuff here
	// }
}
