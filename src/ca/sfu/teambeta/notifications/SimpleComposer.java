package ca.sfu.teambeta.notifications;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ca.sfu.teambeta.core.Pair;
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
            + "The other pairs in your group are:\n%s\n\n"
            + "See you tonight!\nVRC";

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
        String otherPairsList = createPairList(player, scorecard);

        return String.format(MESSAGE_TEMPLATE, playerName, playTime, otherPairsList);
    }

    private String createPairList(Player currentPlayer, Scorecard scorecard) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Pair pair: scorecard.getPairs()) {
            if (!pair.hasPlayer(currentPlayer)) {
                stringBuilder.append(" - ");

                List<Player> players = pair.getPlayers();
                Player player1 = players.get(0);
                Player player2 = players.get(1);

                stringBuilder.append(player1.getFirstName()).append(" ").append(player1.getLastName());
                stringBuilder.append(" and ");
                stringBuilder.append(player2.getFirstName()).append(" ").append(player2.getLastName());

                stringBuilder.append("\n");
            }
        }

        return stringBuilder.toString();
    }
}
