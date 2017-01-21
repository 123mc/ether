package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.fx.FFT;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Arrays.asList;

public class Piano {

    public static final int          BASE_KEY                = 40;
    public static final float        BASE_KEY_FREQUENCY      = 440f;
    public static final int          LOWEST_KEY              = -39;
    public static final int          HIGHEST_KEY             = 83;
    public static final int          NOTES_IN_OCTAVE         = 12;
    public static final int          FREQUENCY_ACCURACY      = 5;
    public static final double       ROUNDING_INCREMENT      = (1 / (Math.pow(10, FREQUENCY_ACCURACY)));
    public static final List<String> LETTER_MAP              = asList("A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#");

    private double playOutTime = -1.0d;

    private ArrayList<PianoNote> pianoNotes  = new ArrayList<>();

    public Piano() {
        createNotes();
    }

    public Piano(FFT fft, double time) {
        playOutTime = time;
        createNotesWithPower(fft);
    }

    public String toString() {
        String string = new String();
        for(PianoNote note : pianoNotes) {
            string += note.toString();
        }
        return string;
    }

    public String getHeaderRow() {
        String headerRow = "";
        for(PianoNote pianoNote : pianoNotes) {
            headerRow += ";" + pianoNote.spectrumHeadersToString();
        }

        return headerRow;
    }

    public String getPowerRow() {
        String powerRow = "";
        for(PianoNote pianoNote : pianoNotes) {
            powerRow += ";" + pianoNote.getSpectrumPower();
        }

        return powerRow;
    }


    private void createNotes() {
        createNotesWithPower(null);
    }

    private void createNotesWithPower(FFT fft) {
        for(int i = LOWEST_KEY; i <= HIGHEST_KEY; i++) {
            PianoNote pianoNote = new PianoNote(i);
            if(fft != null) {
                pianoNote.setSpectrumPower(fft);
            }
            pianoNotes.add(pianoNote);
        }
    }

    public static double calculateFrequency(int relativeIndexToC4) {
        // https://en.wikipedia.org/wiki/Twelfth_root_of_two
        BigDecimal dividend     = new BigDecimal(relativeIndexToC4 - 9);
        BigDecimal bigTwelve    = new BigDecimal(12);
        BigDecimal potency      = dividend.divide(bigTwelve, Piano.FREQUENCY_ACCURACY + 1, BigDecimal.ROUND_HALF_UP);
        BigDecimal bigTwo       = new BigDecimal(2);
        double factor           = Math.pow(bigTwo.intValue(), potency.doubleValue());

        return (BASE_KEY_FREQUENCY * factor);
    }

    public PianoNote findPianoNoteByFrequency(double frequency) {
        for(PianoNote note : pianoNotes) {
            if(note.includesFrequency(frequency)) {
                return note;
            }
        }

        return new PianoNote(0);
    }

    public PianoNote findPianoNoteByKeyNumber(int keyNumber) {
        for(PianoNote note : pianoNotes) {
            if(note.getKeyNumber() == keyNumber) {
                return note;
            }
        }

        return new PianoNote(0);
    }

    public PianoNote findPianoNoteByScientificName(String scientificName) {
        for(PianoNote note : pianoNotes) {
            if(note.getScientificName().equals(scientificName)) {
                return note;
            }
        }

        return new PianoNote(0);
    }

    public static double averageFrequency(double leftFrequency, double rightFrequency) {
        return (leftFrequency + rightFrequency) / 2;
    }

    public float[] getAllFrequencies(){
        float result[] = new float[pianoNotes.size()];
        for (int i = 0; i < pianoNotes.size(); i++) {
            result[i] = (float) pianoNotes.get(i).getFrequency();
        }

        return result;
    }

    public List<PianoNote> getPianoNotes() {
        return pianoNotes;
    }

    public PianoNote getPianoNoteWithHighestSpectrumPower() {
        PianoNote pianoNoteWithHigestSpectrumPower = pianoNotes.get(0);
        for(PianoNote pianoNote : pianoNotes) {
            if(pianoNoteWithHigestSpectrumPower.getSpectrumPower() < pianoNote.getSpectrumPower()) {
                pianoNoteWithHigestSpectrumPower = pianoNote;
            }
        }
        return pianoNoteWithHigestSpectrumPower;
    }

    public float getAveragePowerInOctave(int octaveNumber) {
        float powerSum = 0.0f;

        if(playOutTime >  0.0d) {
            for(String l : LETTER_MAP) {
                powerSum += findPianoNoteByScientificName(l + octaveNumber).getSpectrumPower();
            }
        }

        return powerSum / NOTES_IN_OCTAVE;
    }



}
