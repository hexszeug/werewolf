package eu.hexsz.werewolf.controller;

import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.role.NightActive;
import eu.hexsz.werewolf.role.PlayerController;
import eu.hexsz.werewolf.player.PlayerRegistry;
import eu.hexsz.werewolf.player.Status;
import eu.hexsz.werewolf.time.DayPhase;
import eu.hexsz.werewolf.time.Phase;
import eu.hexsz.werewolf.time.Time;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Controls a night. Reminds every {@link PlayerController}
 * to register in which phase the player wants to wake up when the night starts.
 * Then iterates through the night phases, wakes up the players registered for this phase
 * and notifies the manager of the phase who is either
 * a random awake player or the player who registered himself as manager for the specific phase.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
@RequiredArgsConstructor
public class NightController {
    private Job job;
    private HashMap<Phase, HashSet<Player>> alarms = new HashMap<>();
    private HashMap<Phase, Player> managers = new HashMap<>();

    //dependencies
    private final Time time;
    private final PlayerRegistry playerRegistry;

    /**
     * Notifies the {@code NightController} to manage the current night.
     * It will reset the alarms and the managers
     * and calls {@link NightActive#setAlarms()} of every night active player.
     * Then it will start with the first phase of the night.
     * @param job The job to close when the night ends.
     * @since 1.0-SNAPSHOT
     * */
    public void manageNight(Job job) {
        if (job == null) {
            return;
        }
        this.job = job;
        alarms = new HashMap<>();
        managers = new HashMap<>();
        for (Player player : playerRegistry) {
            if (player.getPlayerController() instanceof NightActive nightActive
                    && player.getStatus() != Status.DEAD) {
                nightActive.setAlarms();
            }
        }
        startPhase();
    }

    private void startPhase() {
        Phase phase = time.getPhase();
        HashSet<Player> players = alarms.get(phase);
        Player manager = managers.get(phase);
        if (players == null) {
            players = new HashSet<>();
        }
        if (manager == null) {
            if (players.isEmpty()) {
                endPhase();
                return;
            }
            manager = players.iterator().next();
        }
        if (!(manager.getPlayerController() instanceof NightActive)) {
            endPhase();
            return;
        }
        for (Player player : players) {
            if (player.getStatus() != Status.DEAD) {
                player.setStatus(Status.AWAKE);
            }
        }
        new Job(
                String.format("%s:%s", time.getNight(), phase.toString().toLowerCase()),
                ((NightActive) manager.getPlayerController())::manageNightPhase,
                this::endPhase
        ).start();
    }

    private void endPhase() {
        HashSet<Player> players = alarms.get(time.getPhase());
        if (players != null) {
            for (Player player : players) {
                if (player.getStatus() != Status.DEAD) {
                    player.setStatus(Status.SLEEPING);
                }
            }
        }
        time.nextPhase();
        if (time.getPhase() == DayPhase.SUNRISE) {
            if (job != null) {
                job.done();
            }
            return;
        }
        startPhase();
    }

    public void setAlarm(Phase phase, Player player) {
        if (phase == null || player == null) {
            return;
        }
        alarms.computeIfAbsent(phase, k -> new HashSet<>()).add(player);
    }

    public void removeAlarm(Phase phase, Player player) {
        if (phase == null || player == null) {
            return;
        }
        HashSet<Player> players = alarms.getOrDefault(phase, new HashSet<>());
        players.remove(player);
    }

    public void setManager(Phase phase, Player player) {
        if (phase == null || player == null) {
            return;
        }
        managers.put(phase, player);
    }

    public void unsetManager(Phase phase) {
        if (phase == null) {
            return;
        }
        managers.remove(phase);
    }
}
