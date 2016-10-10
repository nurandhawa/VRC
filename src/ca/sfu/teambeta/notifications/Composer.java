package ca.sfu.teambeta.notifications;

import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.core.User;

/**
 * Specifies the interface for classes that compose/create messages to be sent by Notifiers
 */
public interface Composer {
    String composeSubject(User user, Scorecard scorecard);

    String composeMessage(User user, Scorecard scorecard);
}
