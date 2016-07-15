package ca.sfu.teambeta.core;

/**
 * Created by constantin on 10/07/16.
 */
public enum Time {
    NO_SLOT(""),
    TH_8_30("Th 8:30"),
    TH_9_00("Th 9:00");

    private String time;

    Time(String time){
        this.time = time;
    }

    public String getTime(){
        return time;
    }
}
