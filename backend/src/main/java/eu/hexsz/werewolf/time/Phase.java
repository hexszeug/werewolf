package eu.hexsz.werewolf.time;

/**
 * Phase in a werewolf game. Is implemented by {@link NightPhase} and {@link DayPhase}.
 * @see Time
 * @see NightPhase
 * @see DayPhase
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public interface Phase {
    int ordinal();
}
