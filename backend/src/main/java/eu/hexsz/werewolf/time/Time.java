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
    private static final NightPhase[] NIGHT_PHASES = NightPhase.values();
    private static final DayPhase[] DAY_PHASES = DayPhase.values();
    private static final NightPhase FIRST_NIGHT_PHASE = NIGHT_PHASES[0];
    private static final NightPhase LAST_NIGHT_PHASE = NIGHT_PHASES[NIGHT_PHASES.length - 1];
    private static final DayPhase FIRST_DAY_PHASE = DayPhase.SUNRISE;
    private static final DayPhase LAST_DAY_PHASE = DayPhase.SUNSET;

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
        night = -1;
        phase = SpecialPhase.GAME_START;
        sendPhaseUpdate();
    }

    /**
     * @return If it is currently night.
     * @since 1.0-SNAPSHOT
     * */
    public boolean isNight() {
        return phase instanceof NightPhase;
    }

    //todo replace is night
    public boolean isDay() {
        return phase instanceof DayPhase;
    }


    /**
     * Jumps to the next phase automatically switching between day and night.
     * Increments {@link Time#night} when switching from a {@link DayPhase} to a {@link NightPhase}.
     * @since 1.0-SNAPSHOT
     * */
    public void nextPhase() {
        //increment night counter
        if (phase == DayPhase.SUNSET) {
            night++;
        }

        //set phase
        if (phase instanceof NightPhase) {
            phase = (phase == LAST_NIGHT_PHASE) ? FIRST_DAY_PHASE : NIGHT_PHASES[phase.ordinal() + 1];
        } else if (phase instanceof DayPhase) {
            phase = (phase == LAST_DAY_PHASE) ? FIRST_NIGHT_PHASE : DAY_PHASES[phase.ordinal() + 1];
        } else if (phase == SpecialPhase.GAME_START) {
            phase = DayPhase.SUNSET;
        }

        //update players
        sendPhaseUpdate();
    }

    private void sendPhaseUpdate() {
        Message message = new PhaseUpdateBuilder(phase).build();
        for (Player player : playerRegistry) {
            player.getSession().send(message);
        }
    }
}
