package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.fx.FFT;

public class PianoNote {

    private final int    INDEX;
    private final int    KEY_NUMBER;
    private final int    MIDI_NUMBER;
    private static final int MIDI_NUMBER_OFFSET = 20;
    private final int    OCTAVE;
    private final double FREQUENCY;
    private final double LOW_BORDER;
    private final double HIGH_BORDER;
    private final String SCIENTIFIC_NAME;
    private float        spectrumPower = -1.0f;

    private int velocity = 64;

    public PianoNote(int index) {
        INDEX       = index;
        KEY_NUMBER  = Piano.BASE_KEY + INDEX;
        MIDI_NUMBER = KEY_NUMBER + MIDI_NUMBER_OFFSET;
        OCTAVE      = (KEY_NUMBER + 8) / Piano.NOTES_IN_OCTAVE;
        FREQUENCY   = Piano.calculateFrequency(INDEX);
        // LOW_BORDER  = FREQUENCY;
        // HIGH_BORDER = calculateFrequencyOfNextNote(1);

        // original approach (calculate middle to previous and next note
         LOW_BORDER  = caclulateBorderToNote(-1);
         HIGH_BORDER = caclulateBorderToNote(1);

        // OK-ish approach
        // LOW_BORDER  = FREQUENCY - 0.003f;
        // HIGH_BORDER = FREQUENCY + 0.003f;

        //LOW_BORDER  = FREQUENCY;// * 1.02;  // * 0.94
        //HIGH_BORDER = FREQUENCY;// * 1.07; // * 1.072

        // x = 525Hz, x+1 = 530Hz
        // band von x 520Hz - 527Hz (funktioniert nicht gut)

        // band von x   525.8Hz - 528.9Hz
        // band von x+1 531Hz - 536Hz


        int mod     = ((KEY_NUMBER - 1) % Piano.NOTES_IN_OCTAVE);
        SCIENTIFIC_NAME = Piano.LETTER_MAP.get(mod) + OCTAVE;
    }

    public float setSpectrumPower(FFT fft) {
        if (spectrumPower < 0.0f) {
            spectrumPower = fft.power((float) getLowBorder(), (float) getHighBorder());
        }
        return spectrumPower;
    }

    public String toString() {
        return new String(
                "Key # " + KEY_NUMBER + " (" + SCIENTIFIC_NAME + "): " + FREQUENCY + " (from " + LOW_BORDER + " to " + HIGH_BORDER + ")\n"
        );
    }

    public String spectrumHeadersToString() {
        return "[" + SCIENTIFIC_NAME + ", " + FREQUENCY + "]";
    }


    /* freakkin getters */

    public int    getKeyNumber()      { return KEY_NUMBER; }
    public int    getMidiNumber()     { return MIDI_NUMBER; }
    public double getLowBorder()      { return LOW_BORDER; }
    public double getHighBorder()     { return HIGH_BORDER; }
    public double getFrequency()      { return FREQUENCY; }
    public String getScientificName() { return SCIENTIFIC_NAME; }
    public float  getSpectrumPower()  { return spectrumPower; }


    public boolean includesFrequency(double frequency) {
   //     System.err.println(frequency+ ">=" +LOW_BORDER +"&&"+ frequency +"<=" + HIGH_BORDER + "=>" + SCIENTIFIC_NAME);
        return frequency >= LOW_BORDER && frequency <= HIGH_BORDER;
    }

    private double caclulateBorderToNote(int otherIndex) {
        double otherKeyFrequency = Piano.calculateFrequency(INDEX + otherIndex);
        double border = Piano.averageFrequency(otherKeyFrequency, FREQUENCY);
        double borderIncrementIfLower = Piano.ROUNDING_INCREMENT * ( Math.pow(0, (Math.pow((otherIndex + 1), 2))));
        double result = (border + borderIncrementIfLower);

        return Math.round(result * Math.pow(10, Piano.FREQUENCY_ACCURACY)) / ((double) (Math.pow(10, Piano.FREQUENCY_ACCURACY)));
    }

    private double calculateFrequencyOfNextNote(int otherIndex) {
        return Piano.calculateFrequency(INDEX + otherIndex);
    }

    public int getVelocity() {
        return velocity;
    }

}
