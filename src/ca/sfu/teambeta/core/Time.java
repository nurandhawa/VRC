package ca.sfu.teambeta.core;

/**
 * Created by constantin on 10/07/16.
 */
public enum Time {
    NO_SLOT(""),
    SLOT_1("08:00 pm"),
    SLOT_2("09:30 pm");

    private String time;

    Time(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }
}
