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
       /* PianoEvents undetectedPianoEvents = conductor.getUndetectedPianoEvents();

        for(int i = 0; i < undetectedPianoEvents.size(); i++) {
            PianoEvent pianoEvent = undetectedPianoEvents.get(i);

            if(pianoEvent.isReadyToBePitchDetected(target)) {
                Piano detectedPiano = pitchDetector.analyze(target);
                pianoEvent.addPiano(detectedPiano);
            }

            if(pianoEvent.isReadyToDetectPianoNote()) {
                pianoEvent.detectPianoNote();
  //              conductor.noteOn(pianoEvent.getDetectedPianoNote().getMidiNumber());
            }

        }*/
    }

    /**
     * @return
     * res[0] = highestFreqPower
     * res[1] = highestFreq
     * res[2] = secound highestFreqPower
     * res[3] = secound highestFreq
     * @param freqs
     *
     */
    private float[] getHighestFreq(float[] freqs){
        float[] res = new float[4];
        res[0]=0;//pwr
        res[1]=0;//freq

        for (int i = 0; i+1 < freqs.length; i++) {
            float power = fft.power(freqs[i],freqs[i+1]);
            if(res[0] <power){
                res[2] = res[0];
                res[3] = res[1];
                res[0]  = power;
                res[1]  = freqs[i];
            }
        }
        return res;
    }

    private void printFftFreqLive(){
        Piano piano = new Piano();
        float[] freqs = piano.getAllFrequencies();
        float[] freqAnal = getHighestFreq(freqs);
        float highestFreqPower = freqAnal[0];
        double highestFreq = freqAnal[1];
        float secondHighestFreqPower = freqAnal[2];
        double secondHighestFreq = freqAnal[3];


     //   PianoNote foundNote = piano.findPianoNoteByFrequency(highestFreq);
//        System.err.println("highestFreq: "+highestFreq);
        PianoNote foundNote = findPianoNoteByFrequency(highestFreq);
        totalFrames++;
        totalEnergy += highestFreqPower;
        pwr.push((int) Math.floor(highestFreqPower));

        int recentAverage = pwr.getAvg()/2;
        if(recentAverage<(getTotalAvgEnergy()*0.20)){
            recentAverage = (int) Math.floor(getTotalAvgEnergy()*0.20);
        }
//        System.out.println(highestFreqPower+"> TotalPwr:"+getTotalAvgEnergy()+"  recentPwr: "+(recentAverage));
        if(blockCount <0 && null!=foundNote && highestFreqPower > (getTotalAvgEnergy()+(recentAverage))){ //cpn(6)=>12 oder cpn(1|11)=>29
            blockCount = 30;
            madSchPcm2Midi.noteOn(foundNote.getMidiNumber(),64);
            System.out.print(String.format("%05d%n", (int) highestFreq));
            System.out.println((char)27 + "[32m"+"=>"+foundNote.getScientificName()+" ("+foundNote.getMidiNumber()+")"+"  "+this.strengthToString(highestFreqPower) + "("+highestFreqPower+")"+ (char)27 + "[0m");
/*
            boolean cNoteAndSecoundClose = false;

            //secound highest note
            PianoNote foundSecondNote = findPianoNoteByFrequency(secondHighestFreq);
      //      madSchPcm2Midi.noteOn(foundSecondNote.getMidiNumber(),64);
            System.out.print(String.format("%05d%n", (int) secondHighestFreq));
            System.out.println((char)27 + "[35m"+"=>"+foundSecondNote.getScientificName()+" ("+foundSecondNote.getMidiNumber()+")"+"  "+this.strengthToString(secondHighestFreqPower) + "("+secondHighestFreqPower+")"+ (char)27 + "[0m");

            //conductor note
            PianoNote cFoundNote = getLastIdentifiedNote();
            if(null != cFoundNote) {
                System.out.print(String.format("%05d%n", (int) cFoundNote.getFrequency()));
                System.out.println((char) 27 + "[36m" + "=>" + cFoundNote.getScientificName() + " (" + cFoundNote.getMidiNumber() + ")" + "  " + this.strengthToString(cFoundNote.getSpectrumPower()) + "(" + cFoundNote.getFrequency() + ")" + (char) 27 + "[0m");
                if(
                        Math.abs(cFoundNote.getMidiNumber()-foundSecondNote.getMidiNumber())
                                <
                                Math.abs(cFoundNote.getFrequency()-foundNote.getMidiNumber())
                        ){
                        cNoteAndSecoundClose=true;
                        //madSchPcm2Midi.noteOn(foundSecondNote.getMidiNumber(),64);
                }
            }else{
                System.out.println((char) 27 + "[36m" + " :( no cNote " + (char) 27 + "[0m");
            }

            if(!cNoteAndSecoundClose){
                //madSchPcm2Midi.noteOn(foundNote.getMidiNumber(),64);
            }
*/
        }
        blockCount--;
        System.out.print(String.format("%05d%n", (int) highestFreq));
        System.out.println("=>K#A (XX)"+this.strengthToString(highestFreqPower) + "("+highestFreqPower+")");
    }

    private PianoNote getLastIdentifiedNote(){
        PianoEvents pes = conductor.getUndetectedPianoEvents();
        for (int i = 0; i < pes.size(); i++) {
            PianoEvent pe = pes.get(i);
            PianoNote pn = pe.getDetectedPianoNote();
            if(pe.isReadyToBePitchDetected(myTarget)) {
                Piano detectedPiano = pitchDetector.analyze(myTarget);
                pe.addPiano(detectedPiano);
            }

            if(pe.isReadyToDetectPianoNote()) {
                pe.detectPianoNote();
                return pe.getDetectedPianoNote();
            }
        }
        return null;
    }

    private int getTotalAvgEnergy(){
        int res = (int) Math.floor(((totalEnergy/totalFrames)/*0.75*/));//
        return res;
    }

    private String strengthToString(float highestFreqPower){
        String strength = "";
        for (int i = 0; i < highestFreqPower; i++) {
            strength += ":";
        }
        return strength;
    }

    private PianoNote findPianoNoteByFrequency(double highestFreq){
        Piano fuck = new Piano();
        List<PianoNote> singleFucks =  fuck.getPianoNotes();
        PianoNote theFuck = null;
        for (PianoNote singleFuck : singleFucks ) {
            //System.out.println(highestFreq+ ">" +singleFuck.getLowBorder() +"&&"+ highestFreq+ "<"+ singleFuck.getHighBorder());
            if(highestFreq > singleFuck.getLowBorder() && highestFreq < singleFuck.getHighBorder()){
                theFuck = singleFuck;
            }
        }
        //System.out.println("ccc  "+theFuck);
        return theFuck;

        /*Piano p = new Piano();
        float[] freqs = p.getAllFrequencies();
        PianoNote x = null;
        for (int i = 0; i+1 < freqs.length; i++) {
            if(Math.floor(freqs[i]) == Math.floor(highestFreq)){
                x = new PianoNote(i);
            }
        }
        return x; */
    }

}

