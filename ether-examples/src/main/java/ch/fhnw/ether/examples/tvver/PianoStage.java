package ch.fhnw.ether.examples.tvver;

public class PianoStage {

    /* this is just a playground for the Piano and PianoNotes */

    public static void main(String[] args) {
        Piano piano = new Piano();

        // System.out.println(piano.toString());

        PianoNote e4 = piano.findPianoNoteByFrequency(604.79183);
        System.out.println(e4.toString());

        PianoNote c5 = piano.findPianoNoteByScientificName("C5");
        PianoNote e0 = piano.findPianoNoteByScientificName("E0"); // note nr  7
        PianoNote b6 = piano.findPianoNoteByScientificName("B6"); // note nr 74

        System.out.printf(c5.toString());
        System.out.printf(e0.toString());
        System.out.printf(b6.toString());
    }
}
