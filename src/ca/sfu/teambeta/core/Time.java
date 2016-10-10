package ca.sfu.teambeta.core;

/**
 * Enum holding the valid time slots for games to be played.
 */
public enum Time {
    NO_SLOT(""),
    SLOT_1("7:45pm"),
    SLOT_2("7:45pm");

    private String timeString;

    Time(String timeString) {
        this.timeString = timeString;
    }

    public String getTimeString() {
        return timeString;
    }
}
