package ch.fhnw.ether.examples.tvver;

public class PianoNote {

    private final int    INDEX;
    private final int    KEY_NUMBER;
    private final int    OCTAVE;
    private final double FREQUENCY;
    private final double LOW_BORDER;
    private final double HIGH_BORDER;
    private final String SCIENTIFIC_NAME;

    public PianoNote(int index) {
        INDEX       = index;
        KEY_NUMBER  = Piano.BASE_KEY + INDEX;
        OCTAVE      = KEY_NUMBER / Piano.NOTES_IN_OCTAVE;
        FREQUENCY   = Piano.calculateFrequency(INDEX);
        LOW_BORDER  = caclulateBorderToNote(-1);
        HIGH_BORDER = caclulateBorderToNote(1);

        int mod     = (KEY_NUMBER % Piano.NOTES_IN_OCTAVE);
        SCIENTIFIC_NAME = Piano.LETTER_MAP.get(mod) + OCTAVE;
    }

    public String toString() {
        return new String(
                "Key # " + KEY_NUMBER + " (" + SCIENTIFIC_NAME + "): " + FREQUENCY + " (from " + LOW_BORDER + " to " + HIGH_BORDER + ")\n"
        );
    }


    /* freakkin getters */

    public int    getKeyNumber()      { return KEY_NUMBER; }
    public double getLowBorder()      { return LOW_BORDER; }
    public double getHighBorder()     { return HIGH_BORDER; }
    public double getFrequency()      { return FREQUENCY; }
    public String getScientificName() { return SCIENTIFIC_NAME; }


    public boolean includesFrequency(double frequency) {
        return frequency >= LOW_BORDER && frequency <= HIGH_BORDER;
    }

    private double caclulateBorderToNote(int otherIndex) {
        double otherKeyFrequency = Piano.calculateFrequency(INDEX + otherIndex);
        double border = Piano.averageFrequency(otherKeyFrequency, FREQUENCY);
        double borderIncrementIfLower = Piano.ROUNDING_INCREMENT * ( Math.pow(0, (Math.pow((otherIndex + 1), 2))));
        double result = (border + borderIncrementIfLower);

        return Math.round(result * Math.pow(10, Piano.FREQUENCY_ACCURACY)) / ((double) (Math.pow(10, Piano.FREQUENCY_ACCURACY)));
    }

}
