package ca.sfu.teambeta.notifications;

import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.core.User;

/**
 * Interface for classes that will notify players of events
 */
public interface Notifier {
    void notify(User user, Scorecard scorecard);
}
