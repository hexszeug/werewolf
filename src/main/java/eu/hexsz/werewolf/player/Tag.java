package eu.hexsz.werewolf.player;

/**
 * {@code Tags} can be used to hold custom information in a {@link Player}.
 * One player can hold zero to infinite tags.
 * Tags should be created by {@link PlayerController}s
 * and can be saved in the own player of the controller or in another player.
 * @see Player
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public abstract class Tag {
    /**
     * Should return a constant. If a {@link Player} holds a tag which {@code isDeadly()} returns true,
     * the player will be killed in the {@link eu.hexsz.werewolf.time.DayPhase#DEAD_REVEAL} phase.
     * @since 1.0-SNAPSHOT
     * */
    public abstract boolean isDeadly();

    /**
     * Returns the name of the {@code Tag}. Defaults to the simple class name.
     * Should be overwritten with a constant return when creating a tag with an uninformative class name.
     * @since 1.0-SNAPSHOT
     * */
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
