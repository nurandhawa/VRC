package ca.sfu.teambeta.core;

/**
 * Enum holding the valid time slots for games to be played.
 */
public enum Time {
    NO_SLOT(""),
    SLOT_1("7:45 pm"),
    SLOT_2("9:15 pm"),
    DYNAMIC_SLOT_1("9:25 pm"),
    DYNAMIC_SLOT_2("9:35 pm"),
    DYNAMIC_SLOT_3("9:45 pm"),
    DYNAMIC_SLOT_4("9:55 pm"),
    DYNAMIC_SLOT_5("10:00 pm");

    private String timeString;

    Time(String timeString) {
        this.timeString = timeString;
    }

    public String getTimeString() {
        return timeString;
    }
}
