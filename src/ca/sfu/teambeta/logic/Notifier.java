package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.Player;

/**
 * Interface for classes that will notify players of events
 */
public interface Notifier {
    void notify(Player player);
}
