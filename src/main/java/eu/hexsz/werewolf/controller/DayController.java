package eu.hexsz.werewolf.controller;

import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.player.PlayerRegistry;
import eu.hexsz.werewolf.player.Status;
import eu.hexsz.werewolf.player.Tag;
import eu.hexsz.werewolf.time.DayPhase;
import eu.hexsz.werewolf.time.Time;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;

/**
 * Controls the day. The day is split in four phases:
 * <ul>
 *     <li><b>DEAD_REVEAL</b>: every player who was marked as dead
 *     (have a tag whose {@code isDeadly} method returns true)
 *     in the last night is publicly killed.
 *     <li><b>ACCUSING</b>: everyone can raise a hand.
 *     If you are called on you can charge somebody for being a werewolf.
 *     <li><b>JUDGING</b>: everyone votes for the accused they think is most likely a werewolf.
 *     <li><b>EXECUTION</b>: the voted executee is killed.
 * </ul>
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
@RequiredArgsConstructor
public class DayController {

    private Job job;

    //dependencies
    private final PlayerRegistry playerRegistry;
    private final ExecutionService executionService;
    private final Time time;

    /**
     * Will start the day described in the class docs.
     * @param job The job which is closed when the day ends.
     * @since 1.0-SNAPSHOT
     * */
    public void manageDay(Job job) {
        if (job == null) {
            return;
        }

        if (time.isNight()) {
            job.done();
            return;
        }

        this.job = job;

        for (Player player : playerRegistry) {
            if (player.getStatus() != Status.DEAD) {
                player.setStatus(Status.AWAKE);
            }
        }

        startPhase();
    }

    private void startPhase() {
        if (!(time.getPhase() instanceof DayPhase phase) || time.isNight()) {
            for (Player player : playerRegistry) {
                if (player.getStatus() != Status.DEAD) {
                    player.setStatus(Status.SLEEPING);
                }
            }
            job.done();
            return;
        }
        switch (phase) {
            case DEAD_REVEAL:

                /*
                * Searches for Players with at least one tag, which is deadly (isDeadly() returns true).
                * This tag might be applied by any game mechanic.
                * */

                HashSet<Player> executees = new HashSet<>();
                for (Player player : playerRegistry) {
                    for (Tag tag : player.tags()) {
                        if (tag.isDeadly()) {
                            executees.add(player);
                            player.removeTag(tag);
                        }
                    }
                }
                new Job(
                        String.format("%s:dead_reveal", time.getNight()),
                        (Job executionJob) -> {
                            executionService.kill(executionJob, executees.toArray(new Player[0]));
                        },
                        this::endPhase
                ).start();

                /*
                * End of DEAD_REVEAL
                * */

                break;
            case ACCUSING:

                System.out.println("todo: accusing");
                endPhase();

                /*
                * End of ACCUSING
                * */

                break;
            case JUDGING:

                System.out.println("todo: judging");
                endPhase();

                /*
                * End of JUDGING
                * */

                break;
            case EXECUTION:

                System.out.println("todo: execution");
                endPhase();

                /*
                * End of EXECUTION
                * */

                break;
        }
    }

    private void endPhase() {
        time.nextPhase();
        startPhase();
    }
}
