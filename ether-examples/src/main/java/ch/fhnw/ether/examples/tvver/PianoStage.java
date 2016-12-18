package ch.fhnw.ether.examples.tvver;

public class PianoStage {

    public static void main(String[] args) {
        Piano piano = new Piano();

        // System.out.println(piano.toString());

        PianoNote e4 = piano.findPianoNoteByFrequency(604.79183);
        System.out.println(e4.toString());
    }
}
