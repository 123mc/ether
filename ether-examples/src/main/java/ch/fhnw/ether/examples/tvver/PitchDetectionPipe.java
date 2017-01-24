package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.audio.fx.FFT;
import ch.fhnw.ether.media.AbstractRenderCommand;
import ch.fhnw.ether.media.RenderCommandException;

import java.util.ArrayList;


public class PitchDetectionPipe extends AbstractRenderCommand<IAudioRenderTarget> {

    private final Conductor conductor;
    private final FFT fft;
    private final PitchDetector pitchDetector;
    private final Piano piano;
    private static final int SPLIT_BASE = 10;
    private static final float LOW_BORDER_EXPAND_FACTOR = 1.02f;
    private static final float HIGH_BORDER_EXPAND_FACTOR = 1.06f;

    public PitchDetectionPipe(FFT fastfuriousTransform, Conductor c) {
        conductor = c;
        fft = fastfuriousTransform;
        pitchDetector = new PitchDetector(fft, conductor.PITCH_DETECTION_DELAY_MS, conductor.PITCH_DETECTION_FFT_CYCLES);
        piano = new Piano();
    }

    @Override
    protected void init(IAudioRenderTarget target) throws RenderCommandException {
    }

    @Override
    protected void run(IAudioRenderTarget target) throws RenderCommandException {
        PianoEvents undetectedPianoEvents = conductor.getUndetectedPianoEvents();

        for(int i = 0; i < undetectedPianoEvents.size(); i++) {
            PianoEvent pianoEvent = undetectedPianoEvents.get(i);

            if(pianoEvent.isReadyToBePitchDetected(target)) {
                PianoNote detectedPianoNote = divAndConquer();
                pianoEvent.setDetectedPianoNote(detectedPianoNote);
                conductor.noteOn(detectedPianoNote.getMidiNumber());
            }

        }
    }


    private PianoNote divAndConquer() {
        PianoNote highestPianoNote = piano.getHighestPianoNote();
        PianoNote lowestPianoNote = piano.getLowestPianoNote();

        int level = 1;
        float winningBand[] = splitAndFindBandWithHighestPower((float) lowestPianoNote.getFrequency(), (float) highestPianoNote.getFrequency(), level);

        float centerOfWinningBand = (winningBand[0] + winningBand[1]) / 2;

        return piano.findPianoNoteByFrequency(centerOfWinningBand);
    }


    private float[] splitAndFindBandWithHighestPower(float fromFrequency, float toFrequency, int level) {

        float[] maxBand = new float[2];
        ArrayList<float[]> maxBandList = new ArrayList<>();
        int splits = ((int) Math.pow(SPLIT_BASE, level));

        /* dont proceed if we are "deeper" than 3 recursion levels */
        if (level > 3) {
            maxBand[0] = fromFrequency;
            maxBand[1] = toFrequency;
            return maxBand;
        }

        float maxPower = 0f;
        float diff = toFrequency - fromFrequency;
        float bandWidth = diff / splits;

        /* set initial left and right borders */
        float leftBorder = fromFrequency;
        float rightBorder = leftBorder + bandWidth;

        System.out.println("\n----");
        System.out.println("Investigating from " + fromFrequency + " to " + toFrequency + " with " + splits + " splits");

        /* calculate power in each band */
        while(leftBorder < (toFrequency * 1.1)) {
            /* get the power in the frequency band, open the band by 2% on the borders to overlap a little */
            float power = fft.power(leftBorder * LOW_BORDER_EXPAND_FACTOR, rightBorder * HIGH_BORDER_EXPAND_FACTOR);

            System.out.println("--" + leftBorder + "-" + rightBorder + " : " + power);
            if(maxPower < power) {
                /* new peak detected*/
                maxPower = power;
                maxBand[0] = leftBorder;
                maxBand[1] = rightBorder;
                maxBandList.clear();
                maxBandList.add(maxBand);
            } else if (maxPower == power) {
                /* peak detected again */
                float anotherBand[] = new float[2];
                anotherBand[0] = leftBorder;
                anotherBand[1] = rightBorder;
                maxBandList.add(anotherBand);
            }

            /* shift borders to the right */
            leftBorder  = leftBorder + bandWidth;
            rightBorder = rightBorder + bandWidth;
        }

        /* get band with most power, if there are multiple bands with same power get the one at the middle of the list */
        maxBand = maxBandList.get((maxBandList.size() / 2));

        System.out.println("Winner is band from " + maxBand[0] + " to " + maxBand[1] + " with power " + maxPower);

        /* do another round with more splits */
        return splitAndFindBandWithHighestPower(maxBand[0], maxBand[1], level + 1);
    }

}
