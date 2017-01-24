package ch.fhnw.ether.examples.tvver;

import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.audio.fx.FFT;
import ch.fhnw.ether.media.AbstractRenderCommand;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.scene.mesh.IMesh;

import java.util.List;
import java.util.Queue;


public class PitchDetectionPipe extends AbstractRenderCommand<IAudioRenderTarget> {

    private final Conductor conductor;
    private final FFT fft;
    private final PitchDetector pitchDetector;

    MadSchPCM2MIDI madSchPcm2Midi;
    private PwrRingBuffer pwr;
    int blockCount;

    int totalFrames;
    int totalEnergy;

    IAudioRenderTarget myTarget;

    public PitchDetectionPipe(FFT fastfuriousTransform, Conductor c, MadSchPCM2MIDI pcm2midi) {
        madSchPcm2Midi = pcm2midi;
        conductor = c;
        fft = fastfuriousTransform;
        pitchDetector = new PitchDetector(fft, conductor.PITCH_DETECTION_DELAY_MS, conductor.PITCH_DETECTION_FFT_CYCLES);
        pwr = new PwrRingBuffer(4,29);
        blockCount = 0;
        totalFrames=0;
        totalEnergy=0;
    }

    @Override
    protected void init(IAudioRenderTarget target) throws RenderCommandException {
    }

    @Override
    protected void run(IAudioRenderTarget target) throws RenderCommandException {
        myTarget = target;
        this.printFftFreqLive();
    }

    /**
     * @return
     * res[0] = highestFreqPower
     * res[1] = highestFreq
     * @param freqs
     *
     */
    private float[] getHighestFreq(float[] freqs){
        float[] res = new float[2];
        res[0]=0;//pwr
        res[1]=0;//freq

        for (int i = 0; i+1 < freqs.length; i++) {
            float power = fft.power(freqs[i],freqs[i+1]);
            if(res[0] <power){
                res[0]  = power;
                res[1]  = freqs[i];
            }
        }
        return res;
    }

    /**
     * prints the freq on the console
     * detects the pitch
     * detects the note
     * send the note
     */
    private void printFftFreqLive(){
        Piano piano = new Piano();
        float[] freqs = piano.getAllFrequencies();System.out.println("lol 1 ");
        float[] freqAnal = getHighestFreq(freqs);System.out.println("lol 1 ");
        float highestFreqPower = freqAnal[0];
        double highestFreq = freqAnal[1];

        PianoNote foundNote = findPianoNoteByFrequency(highestFreq);
        totalFrames++;
        totalEnergy += highestFreqPower;
        pwr.push((int) Math.floor(highestFreqPower));

        int recentAverage = pwr.getAvg()/2;
        if(recentAverage<(getTotalAvgEnergy()*0.20)){
            recentAverage = (int) Math.floor(getTotalAvgEnergy()*0.20);
        }
        System.out.println(highestFreqPower+"> TotalPwr:"+getTotalAvgEnergy()+"  recentPwr: "+(recentAverage));
        //System.out.println("oder "+(highestFreqPower+">"+recentAverage*6));
        if(blockCount <0 && null!=foundNote && (( highestFreqPower > (getTotalAvgEnergy()+(recentAverage))) /*|| highestFreqPower>(recentAverage*3)*/)){ //cpn(6)=>12 oder cpn(1|11)=>29
            blockCount = 30;
            madSchPcm2Midi.noteOn(foundNote.getMidiNumber(),64);
            System.out.print(String.format("%05d%n", (int) highestFreq));
            System.out.println((char)27 + "[32m"+"=>"+foundNote.getScientificName()+" ("+foundNote.getMidiNumber()+")"+"  "+this.strengthToString(highestFreqPower) + "("+highestFreqPower+")"+ (char)27 + "[0m");
        }
        blockCount--;
        System.out.print(String.format("%05d%n", (int) highestFreq));
        System.out.println("=>K#A (XX)"+this.strengthToString(highestFreqPower) + "("+highestFreqPower+")");
    }


    private int getTotalAvgEnergy(){
        int res = (int) Math.floor(((totalEnergy/totalFrames)/*0.75*/));//
        return res;
    }

    /**
     * Only for display reasons
     * @param highestFreqPower
     * @return String of param times ":"
     */
    private String strengthToString(float highestFreqPower){
        String strength = "";
        for (int i = 0; i < highestFreqPower; i++) {
            strength += ":";
        }
        return strength;
    }

    /**
     *
     * @param highestFreq
     * @return pianonote for that freq
     */
    private PianoNote findPianoNoteByFrequency(double highestFreq){
        Piano piano = new Piano();
        List<PianoNote> pianoNotes =  piano.getPianoNotes();
        PianoNote pianoNote = null;
        for (PianoNote note : pianoNotes ) {
            if(highestFreq > note.getLowBorder() && highestFreq < note.getHighBorder()){
                pianoNote = note;
            }
        }
        return pianoNote;
    }

}

