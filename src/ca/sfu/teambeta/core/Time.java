package ca.sfu.teambeta.core;

/**
 * Enum holding the valid time slots for games to be played.
 */
public enum Time {
    NO_SLOT(""),
    SLOT_1("7:45 pm"),
    SLOT_2("9:15 pm");

    private String timeString;

    Time(String timeString) {
        this.timeString = timeString;
    }

    public String getTimeString() {
        return timeString;
    }
}
