package ch.fhnw.ether.examples.tvver;

import java.util.EmptyStackException;

/**
 * Created by meins on 12.12.2016.
 *
 * init
 * ------
 * avgPeakOverTime = new AvgPeakOverTime(5,0.003f);
 *
 * fill
 * -----
 * avgPeakOverTime.push(0.005f)
 *
 * check
 * ------
 * if(avgPeakOverTime.isPeak(peak))
 */
public class PeakFinder {
    private int top;
    private float[] storage;
    private float avgPeak;


    PeakFinder(int capacity, float avgPeak){
        this.avgPeak = avgPeak;
        if (capacity <= 0){
            throw new IllegalArgumentException(
                    "Stack's capacity must be positive");
        }
        storage = new float[capacity];
        top=-1;
        init();
    }

    private void init(){
        for(int i = 0; i <= storage.length; i ++ ){
            push(avgPeak);
        }
    }


    void push(float value){
        top = (top+1)%(storage.length-1);
        storage[top] = value;
    }

    float getAvg(){
        if (top == -1)
            throw new EmptyStackException();
        float total = 0;
        for (int i = 0; i < storage.length; i++) {
            total += storage[i];
        }

        return total/storage.length;
    }

    /**
     * if given peak is higher than the threshhold + the average difference of the last capacity values
     * @param currentPeak
     * @return
     */
    public boolean isPeak(float currentPeak){
        if(currentPeak>(avgPeak+(avgPeak-getAvg()))){
            return true;
        }
        return false;
    }


}
