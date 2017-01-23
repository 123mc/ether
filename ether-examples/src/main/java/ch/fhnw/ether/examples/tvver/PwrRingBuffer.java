package ch.fhnw.ether.examples.tvver;

/**
 * Created by meins on 23.01.2017.
 */
public class PwrRingBuffer {
    int[] values;
    int pointer;

    public PwrRingBuffer(int size,int def){
        values = new int[size];
        pointer = 0;
    }

    private void fillWithDefault(int def){
        for (int i = 0; i < values.length; i++) {
            values[i] = def;
        }
    }

    public void push(int v){
        values[pointer] = v;
        pointer = ((pointer+1)%(values.length-1));
    }

    public int getAvg(){
        int avg = 0;
        int total=0;
        for (int i = 0; i < values.length; i++) {
            total+=values[i];
        }
        avg = total/values.length;
        if(avg==0){
            avg = 0;
        }
        return avg;
    }
}
