package eu.hexsz.werewolf.controller;

import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.player.PlayerRegistry;
import eu.hexsz.werewolf.player.Status;
import eu.hexsz.werewolf.role.DeathListener;
import eu.hexsz.werewolf.role.OnwDeathActive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Should be used to kill a player. Handles custom role actions triggered by deaths.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
@RequiredArgsConstructor
public class ExecutionService {

    //dependencies
    private final PlayerRegistry playerRegistry;

    /**
     * Creates a new {@link ExecutionQueue ExecutionQueue} with the given players.
     * @param job The job which is closed if all passed players are dead.
     * @param players The players to kill.
     * @see ExecutionQueue
     * @since 1.0-SNAPSHOT
     * */
    public void kill(Job job, Player... players) {
        if (job == null) {
            return;
        }
        if (players == null || players.length == 0) {
            job.done();
            return;
        }
        for (Player player : players) {
            if (player == null) {
                job.done();
                return;
            }
        }
        new ExecutionQueue(job, new ArrayList<>(List.of(players)));
    }

    /**
     * Holds one to infinite players and kills them one by one.
     *
     * <p>If a player is killed whose player controller implements {@link OnwDeathActive}
     * the player controller is told to kill the player.
     * This would only make sense if the role allows some action that is triggered on death.
     *
     * <p>If the player was killed, either by setting its status to {@code DEAD} or by telling the
     * player controller to do it every player controller who implements {@link DeathListener} is notified.
     *
     * <p>In both cases is a reference to the {@code ExecutionQueue} passed to the player controller.
     * The can use {@link ExecutionQueue#addExecutee(Player)} to schedule the execution of another player.
     * <br>The new executee would be added directly after the current player and not at the end of the queue.
     *
     * <p><b>Note</b>: This class can only be constructed by {@link ExecutionService#kill(Job, Player...)}.
     * @since 1.0-SNAPSHOT
     * @author hexszeug
     * */
    public final class ExecutionQueue {
        private final Job job;
        private final ArrayList<Player> executees;
        private int pointer;

        private ExecutionQueue(Job job, ArrayList<Player> executees) {
            this.job = job;
            this.executees = executees;
            pointer = -1;
            next();
        }

        /**
         * Adds an executee to the current queue directly after the current player.
         * @param executee The executee to add
         * @since 1.0-SNAPSHOT
         * */
        public void addExecutee(Player executee) {
            if (executee == null || pointer >= executees.size()) {
                return;
            }
            executees.add(pointer + 1, executee);
        }

        private void next() {
            pointer++;
            if (pointer >= executees.size()) {
                job.done();
                return;
            }
            Player executee = executees.get(pointer);
            if (executee.getStatus() == Status.DEAD) {
                next();
                return;
            }
            if (!(executee.getPlayerController() instanceof OnwDeathActive onwDeathActive)) {
                executee.setStatus(Status.DEAD);
                funeral();
                return;
            }
            new Job(
                    "self-execution-of:" + executee.getPlayerID(),
                    (Job job) -> {onwDeathActive.die(job, new Execution(executee, this));},
                    this::funeral
            ).start();
        }

        private void funeral() {
            Player executee = executees.get(pointer);
            Execution execution = new Execution(executee, this);
            for (Player player : playerRegistry) {
                if (player.getPlayerController() instanceof DeathListener deathListener) {
                    deathListener.onDeath(execution);
                }
            }
            next();
        }
    }

    /**
     * Container which holds
     * <ul>
     *     <li>a {@link Player} who is currently executed by an {@link ExecutionQueue ExecutionQueue}
     *     <li>and the corresponding {@code ExecutionQueue}.
     * </ul>
     * @since 1.0-SNAPSHOT
     * @author hexszeug
     * */
    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Execution {
        private final Player executee;
        private final ExecutionQueue executionQueue;
    }
}
