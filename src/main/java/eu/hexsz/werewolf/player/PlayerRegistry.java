package eu.hexsz.werewolf.player;

import eu.hexsz.werewolf.api.Session;

import java.util.HashMap;

/**
 * Used to provide central access to all {@link Player} in a certain game.
 * Each game should create its own instance of {@code PlayerRegistry}.
 * @see Player
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public class PlayerRegistry {
    private HashMap<String, Player> players = new HashMap<>();

    /**
     * Returns an Iterable for all {@link Player}s registered in this {@code PlayerRegistry}.
     * @since 1.0-SNAPSHOT
     * */
    public Iterable<Player> getPlayersIterable() {
        return players.values();
    }

    /**
     * Registers a new Player in the registry.
     * Should only be called by {@link Player#Player(String, String, String, Session, PlayerController, PlayerRegistry)}.
     * @param player The player to register.
     * @since 1.0-SNAPSHOT
     * */
    public void addPlayer(Player player) {
        players.put(player.getPlayerID(), player);
    }

    /**
     * Returns the {@link Player} registered at this {@code PlayerRegistry}.
     * @throws NullPointerException When the specified {@code playerID} is not registered
     * at this {@code PlayerRegistry} instance.
     * @param playerID playerID of the requested {@link Player}.
     * @since 1.0-SNAPSHOT
     * */
    public Player getPlayer(String playerID) throws NullPointerException {
        Player player = players.get(playerID);
        if (player == null) {
            players.remove(playerID);
            throw new NullPointerException(String.format("Player with id %s does not exit.", playerID));
        }
        return player;
    }
}