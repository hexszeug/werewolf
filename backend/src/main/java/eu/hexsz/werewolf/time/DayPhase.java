package eu.hexsz.werewolf.time;

/**
 * Contains the four phases of a werewolf day:
 * <ul>
 * <li>{@link DayPhase#DEAD_REVEAL}: the werewolves' victim and other deaths of the last night are revealed.
 * <li>{@link DayPhase#ACCUSING}: players can charge others for being a werewolf.
 * <li>{@link DayPhase#JUDGING}: players vote which accused to kill.
 * <li>{@link DayPhase#EXECUTION}: the convict is killed.
 * </ul>
 * @see Time
 * @see Phase
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public enum DayPhase implements Phase {
    SUNRISE,
    DEAD_REVEAL,
    ACCUSING,
    JUDGING,
    EXECUTION,
    SUNSET;
}
