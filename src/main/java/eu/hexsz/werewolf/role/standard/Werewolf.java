package eu.hexsz.werewolf.role.standard;

import eu.hexsz.werewolf.api.IllegalRequestException;
import eu.hexsz.werewolf.api.Message;
import eu.hexsz.werewolf.api.Request;
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

import java.util.HashSet;

@RequiredArgsConstructor
public class Werewolf extends AbstractRole implements NightActive {
    private @Setter @Getter Player currentTarget;
    private @Getter Job job;

    //dependencies
    private final Player player;
    private final PlayerRegistry playerRegistry;
    private final NightController nightController;
    private final Time time;

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
            case "werewolf/point":

                /*
                * Invoked by a werewolf when he points on a player he wants to kill this night.
                * (Works only in the werewolves phase.)
                * */

                if (time.getPhase() != NightPhase.WEREWOLVES || player.getStatus() != Status.AWAKE) {
                    throw new IllegalRequestException(
                            "Can only be invoked when it's werewolves phase and the werewolf is awake.",
                            request
                    );
                }

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
                if (target == currentTarget) {
                    break;
                }
                currentTarget = target;

                /*
                * Broadcast that you are pointing on the target to all awake and dead players.
                * */

                Message message = new PlayerUpdateBuilder(target)
                        .addTag(new WerewolfPointer(player.getPlayerID()))
                        .build();
                for (Player player : playerRegistry) {
                    if (player.getStatus() != Status.SLEEPING) {
                        player.getSession().send(message);
                    }
                }

                /*
                * Mark the target as the werewolves victim and end the phase
                * if all werewolves are pointing on the same target.
                * */

                HashSet<Player> targets = new HashSet<>();
                for (Player player : playerRegistry) {
                    if (player.getPlayerController() instanceof Werewolf werewolf
                            && player.getStatus() == Status.AWAKE) {
                        targets.add(werewolf.getCurrentTarget());
                    }
                }
                if (targets.size() != 1) {
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

                /*
                * End of case "werewolf-point"
                * */

                break;


            default: super.handle(request);
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
