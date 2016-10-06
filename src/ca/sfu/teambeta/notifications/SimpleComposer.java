package ca.sfu.teambeta.notifications;

import java.text.SimpleDateFormat;
import java.util.Date;

import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.core.User;

/**
 * Basic, plain text message Composer to work with any Notifier.
 */
public class SimpleComposer implements Composer {
    private static final String SUBJECT_TEMPLATE = "VRC Game Notification for %s";
    private static final String MESSAGE_TEMPLATE = "Hi %s, \n\n"
            + "You're scheduled to play in the VRC Doubles "
            + "Badminton Ladder today at %s.\n\n"
            + "VRC";

    @Override
    public String composeSubject(User user, Scorecard scorecard) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d");
        String formattedDate = dateFormat.format(new Date());

        return String.format(SUBJECT_TEMPLATE, formattedDate);
    }

    @Override
    public String composeMessage(User user, Scorecard scorecard) {
        Player player = user.getAssociatedPlayer();
        String playerName = player.getFirstName();
        String playTime = scorecard.getTimeSlot().getTimeString();

        return String.format(MESSAGE_TEMPLATE, playerName, playTime);
    }
}
