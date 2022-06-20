package eu.hexsz.werewolf.player;

import eu.hexsz.werewolf.role.PlayerController;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * {@code Tags} can be used to hold custom information in a {@link Player}.
 * One player can hold zero to infinite tags.
 * Tags should be created by {@link PlayerController}s
 * and can be saved in the own player of the controller or in another player.
 * @see Player
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
@EqualsAndHashCode
@Getter
public abstract class Tag {

    private final String name;

    public Tag() {
        name = this.getClass().getSimpleName();
    }

    /**
     * Returns if the Player holding this tag should be killed
     * in {@link eu.hexsz.werewolf.time.DayPhase#DEAD_REVEAL Phase#DEAD_REVEAL}.
     * @return Default false. Can be overwritten to return true.
     * @since 1.0-SNAPSHOT
     * */
    public boolean isDeadly() {
        return false;
    }
}
