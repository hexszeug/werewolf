package eu.hexsz.werewolf.time;

import eu.hexsz.werewolf.api.Message;
import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.player.PlayerRegistry;
import eu.hexsz.werewolf.update.PhaseUpdateBuilder;
import lombok.Getter;

/**
 * A {@code Time} instance represents the in-game time for a werewolf game.
 * It consists of two fields:
 * <ul>
 * <li>{@link Time#night} is the night counter. It is increased each time a new night starts.
 * <li>{@link Time#phase} contains ether a {@link DayPhase} or
 * {@link NightPhase} determinating which phase the game is currently in.
 * </ul>
 * <p>{@code Time} also provides {@link Time#nextPhase()} to automatically jump to the
 * next phase.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public class Time {
    private @Getter int night;
    private @Getter Phase phase;

    //dependencies
    private final PlayerRegistry playerRegistry;

    /**
     * Initialize new {@code Time} instance with default values:
     * <ul>
     * <li>{@link Time#night} is set to 0.
     * <li>{@link Time#phase} is set to the first {@link NightPhase}.
     * </ul>
     * @since 1.0-SNAPSHOT
     * */
    public Time(PlayerRegistry playerRegistry) {
        this.playerRegistry = playerRegistry;
        night = 0;
        phase = NightPhase.values()[0];
    }

    /**
     * @return If it is currently night.
     * @since 1.0-SNAPSHOT
     * */
    public boolean isNight() {
        return phase instanceof NightPhase;
    }


    /**
     * Jumps to the next phase automatically switching between day and night.
     * Increments {@link Time#night} when switching from a {@link DayPhase} to a {@link NightPhase}.
     * @since 1.0-SNAPSHOT
     * */
    public void nextPhase() {
        if (isNight()) {
            if (NightPhase.values().length > phase.ordinal() + 1) {
                phase = NightPhase.values()[phase.ordinal() + 1];
            } else {
                phase = DayPhase.values()[0];
            }
        } else {
            if (DayPhase.values().length > phase.ordinal() + 1) {
                phase = DayPhase.values()[phase.ordinal() + 1];
            } else {
                phase = NightPhase.values()[0];
                night++;
            }
        }
        Message message = new PhaseUpdateBuilder(phase).build();
        for (Player player : playerRegistry) {
            player.getSession().send(message);
        }
    }
}
