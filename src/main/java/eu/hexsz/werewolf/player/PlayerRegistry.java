package eu.hexsz.werewolf.player;

import eu.hexsz.werewolf.api.Session;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Used to provide central access to all {@link Player} in a certain game.
 * Each game should create its own instance of {@code PlayerRegistry}.
 * @see Player
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public class PlayerRegistry implements Iterable<Player> {
    private final HashMap<String, Player> players;

    public PlayerRegistry() {
        players = new HashMap<>();
    }

    /**
     * @return An iterator over all {@link Player}s registered in this {@code PlayerRegistry}.
     * @since 1.0-SNAPSHOT
     * */
    @Override
    public Iterator<Player> iterator() {
        return players.values().iterator();
    }

    /**
     * Registers a new Player in the registry.
     * Should only be called by {@link Player#Player(String, String, String, Session, PlayerController, PlayerRegistry)}.
     * @param player The player to register.
     * @since 1.0-SNAPSHOT
     * */
    public void addPlayer(Player player) {
        if (player != null) {
            players.put(player.getPlayerID(), player);
        }
    }

    /**
     * Returns the {@link Player} registered at this {@code PlayerRegistry}.
     * @throws NullPointerException When the specified {@code playerID} is not registered
     * at this {@code PlayerRegistry} instance.
     * @param playerID playerID of the requested {@link Player}.
     * @since 1.0-SNAPSHOT
     * */
    public Player getPlayer(String playerID) {
        if (playerID == null) {
            return null;
        }
        return players.get(playerID);
    }
}
