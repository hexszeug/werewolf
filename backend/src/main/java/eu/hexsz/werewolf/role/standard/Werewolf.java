package eu.hexsz.werewolf.role.standard;

import eu.hexsz.werewolf.api.IllegalRequestException;
import eu.hexsz.werewolf.api.Message;
import eu.hexsz.werewolf.api.Request;
import eu.hexsz.werewolf.controller.DayController;
import eu.hexsz.werewolf.controller.Job;
import eu.hexsz.werewolf.controller.NightController;
import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.player.PlayerRegistry;
import eu.hexsz.werewolf.player.Status;
import eu.hexsz.werewolf.player.Tag;
import eu.hexsz.werewolf.role.AbstractRole;
import eu.hexsz.werewolf.role.NightActive;
import eu.hexsz.werewolf.time.NightPhase;
import eu.hexsz.werewolf.time.Time;
import eu.hexsz.werewolf.update.PlayerUpdateBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;

import static eu.hexsz.werewolf.api.RequestHandler.*;

public class Werewolf extends AbstractRole implements NightActive {
    private @Setter @Getter Player currentTarget;
    private @Getter Job job;

    //dependencies
    private final Player player;
    private final PlayerRegistry playerRegistry;
    private final NightController nightController;
    private final Time time;

    public Werewolf(Player player, PlayerRegistry playerRegistry, DayController dayController, NightController nightController, Time time) {
        super(player, playerRegistry, dayController, time);
        this.player = player;
        this.playerRegistry = playerRegistry;
        this.nightController = nightController;
        this.time = time;
    }

    @Override
    public void setAlarms() {
        nightController.setAlarm(NightPhase.WEREWOLVES, player);
    }

    @Override
    public void manageNightPhase(Job job) {
        this.job = job;
    }

    @Override
    public void handle(Request request) throws IllegalRequestException {
        if (request == null) {
            return;
        }
        switch (request.getType()) {
            case "werewolf/point" -> {

                /*
                 * Invoked by a werewolf when he points on a player he wants to kill this night.
                 * */

                checkPhase(time, NightPhase.WEREWOLVES, request);
                checkAwake(player, request);

                /*
                 * Set the current target to the passed player
                 * */

                Player target = playerRegistry.getPlayer(request.getData(String.class));
                if (target == null) {
                    throw new IllegalRequestException(
                            String.format(
                                    "Player with id \"%s\" does not exist.",
                                    request.getData(String.class)
                            ),
                            request
                    );
                }
                if (target.getStatus() == Status.DEAD) {
                    throw new IllegalRequestException(
                            "Targeted player is already dead.",
                            request
                    );
                }
                if (target == currentTarget) {
                    break;
                }

                Player oldTarget = currentTarget;
                currentTarget = target;

                /*
                 * Broadcast that you are pointing on the target to all awake and dead players.
                 * */

                HashMap<Player, HashSet<Player>> targetWerewolfMap = new HashMap<>();
                boolean nullTarget = false;
                for (Player player : playerRegistry) {
                    if (!(player.getPlayerController() instanceof Werewolf werewolf)
                            || player.getStatus() != Status.AWAKE) {
                        continue;
                    }
                    if (werewolf.currentTarget == null) {
                        nullTarget = true;
                    }
                    targetWerewolfMap.computeIfAbsent(werewolf.currentTarget, (key) -> new HashSet<>()).add(player);
                }

                PlayerUpdateBuilder targetUpdate = new PlayerUpdateBuilder(target);
                PlayerUpdateBuilder oldTargetUpdate = new PlayerUpdateBuilder(oldTarget);
                for (Player werewolf : targetWerewolfMap.getOrDefault(target, new HashSet<>())) {
                    targetUpdate.addTag(new WerewolfPointer(werewolf.getPlayerID()));
                }
                for (Player werewolf : targetWerewolfMap.getOrDefault(oldTarget, new HashSet<>())) {
                    oldTargetUpdate.addTag(new WerewolfPointer(werewolf.getPlayerID()));
                }

                Message targetMessage = targetUpdate.build();
                Message oldTargetMessage = oldTargetUpdate.build();

                for (Player player : playerRegistry) {
                    if (player.getStatus() != Status.SLEEPING) {
                        player.getSession().send(oldTargetMessage);
                        player.getSession().send(targetMessage);
                    }
                }

                /*
                 * Mark the target as the werewolves victim and end the phase
                 * if all werewolves are pointing on the same target.
                 * */

                if (nullTarget || targetWerewolfMap.size() != 1) {
                    break;
                }

                currentTarget.addTag(new WerewolfVictim());
                Job phaseJob = null;
                for (Player player : playerRegistry) {
                    if (player.getPlayerController() instanceof Werewolf werewolf) {
                        werewolf.setCurrentTarget(null);
                        Job job = werewolf.getJob();
                        if (job != null && job.isRunning()) {
                            phaseJob = job;
                        }
                    }
                }
                if (phaseJob != null) {
                    phaseJob.done();
                }
            }

            /*
             * End of case "werewolf-point"
             * */

            default -> super.handle(request);
        }
    }

    public static class WerewolfVictim extends Tag {
        @Override
        public boolean isDeadly() {
            return true;
        }
    }

    @RequiredArgsConstructor
    @Getter
    public static class WerewolfPointer extends Tag {
        private final String pointerID;
    }
}
