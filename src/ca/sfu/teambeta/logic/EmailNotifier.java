package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.Player;

/**
 * Notifier class to send an email to players regarding their scheduled play time and group
 */
public class EmailNotifier implements Notifier {
    public static final String EMAIL_SMTP_SERVER = "localhost";
    public static final String EMAIL_SMTP_USERNAME = "test";
    public static final String EMAIL_SMTP_PASSWORD = "password";

    @Override
    public void notify(Player player) {
    }
}
