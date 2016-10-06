package ca.sfu.teambeta.core;

/**
 * Enum holding the valid time slots for games to be played.
 */
public enum Time {
    NO_SLOT(""),
    SLOT_1("8:00pm"),
    SLOT_2("9:30pm");

    private String timeString;

    Time(String timeString) {
        this.timeString = timeString;
    }

    public String getTimeString() {
        return timeString;
    }
}
