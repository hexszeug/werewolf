package eu.hexsz.werewolf.time;

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

    /**
     * Initialize new {@code Time} instance with default values:
     * <ul>
     * <li>{@link Time#night} is set to 0.
     * <li>{@link Time#phase} is set to the first {@link NightPhase}.
     * </ul>
     * @since 1.0-SNAPSHOT
     * */
    public Time() {
        //TODO inject PhaseBuilder
        night = 0;
        phase = nightPhase(0);
    }

    private static class PhaseOutOfBoundsException extends RuntimeException {
        public PhaseOutOfBoundsException(String message) {
            super(message);
        }
    }

    private NightPhase nightPhase(int ordinal) throws PhaseOutOfBoundsException {
        try {
            return NightPhase.values()[ordinal];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new PhaseOutOfBoundsException(String.format("NightPhase number %d doesn't exist.", ordinal));
        }
    }

    private DayPhase dayPhase(int ordinal) throws PhaseOutOfBoundsException {
        try {
            return DayPhase.values()[ordinal];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new PhaseOutOfBoundsException(String.format("DayPhase number %d doesn't exist.", ordinal));
        }
    }


    /**
     * Jumps to the next phase automatically switching between day and night.
     * Increments {@link Time#night} when switching from a {@link DayPhase} to a {@link NightPhase}.
     * @since 1.0-SNAPSHOT
     * */
    public void nextPhase() {
        if (phase instanceof NightPhase) {
            try {
                phase = nightPhase(phase.ordinal() + 1);
            } catch (PhaseOutOfBoundsException e) {
                phase = dayPhase(0);
            }
        } else {
            try {
                phase = dayPhase(phase.ordinal() + 1);
            } catch (PhaseOutOfBoundsException e) {
                phase = nightPhase(0);
                night++;
            }
        }
        //TODO send phase update
    }
}
