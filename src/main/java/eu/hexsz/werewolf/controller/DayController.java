package eu.hexsz.werewolf.controller;

import eu.hexsz.werewolf.api.Message;
import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.player.PlayerRegistry;
import eu.hexsz.werewolf.player.Status;
import eu.hexsz.werewolf.player.Tag;
import eu.hexsz.werewolf.time.DayPhase;
import eu.hexsz.werewolf.time.Time;
import eu.hexsz.werewolf.update.PlayerUpdateBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
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
    private final static int ACCUSING_TIMEOUT = 5000;

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
            case DEAD_REVEAL -> {

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

            }

            case ACCUSING -> {
                raisedHands = new ArrayList<>();
                raisedHands.add(null);
                charges = new HashMap<>();
                accusingTimer = new Timer("accusing", ACCUSING_TIMEOUT, this::endPhase);

                /*
                 * End of ACCUSING
                 * */

            }

            case JUDGING -> {
                System.out.println("todo: judging");
                endPhase();

                /*
                 * End of JUDGING
                 * */

            }

            case EXECUTION -> {
                System.out.println("todo: execution");
                endPhase();

                /*
                 * End of EXECUTION
                 * */

            }
        }
    }

    private void endPhase() {
        time.nextPhase();
        startPhase();
    }

    private void broadcast(Message message) {
        for (Player player : playerRegistry) {
            player.getSession().send(message);
        }
    }

    /*
    * Code for the ACCUSING phase
    * */

    private Timer accusingTimer;

    private ArrayList<Player> raisedHands;
    private HashMap<Player, Player> charges;
    private @Getter @Setter boolean accuserDone;
    private @Getter @Setter boolean accusedDone;
    private Player oldAccusedOfSpeaker;

    public void raiseHand(Player player) {
        if (player == null || raisedHands.contains(player)) {
            return;
        }
        raisedHands.add(player);
        if (getSpeaker() == null) {
            nextSpeaker();
            return;
        }
        broadcastAccusing(player);
    }

    public void takeHandDown(Player player) {
        if (player == null || !raisedHands.contains(player)) {
            return;
        }
        if (raisedHands.get(0) == player) {
            nextSpeaker();
            return;
        }
        raisedHands.remove(player);
        broadcastAccusing(player);
    }

    private void nextSpeaker() {
        accusingTimer.cancel();
        Player oldSpeaker = getSpeaker();
        raisedHands.remove(0);
        accuserDone = accusedDone = false;
        Player speaker = getSpeaker();
        oldAccusedOfSpeaker = getAccused(speaker);
        broadcastAccusing(oldSpeaker);
        broadcastAccusing(speaker);
        if (raisedHands.isEmpty()) {
            raisedHands.add(null);
            if (!charges.isEmpty()) {
                accusingTimer.start();
            }
        }
    }

    public Player getSpeaker() {
        if (time.getPhase() != DayPhase.ACCUSING || raisedHands.isEmpty()) {
            return null;
        }
        return raisedHands.get(0);
    }

    public void charge(Player accuser, Player accused) {
        if (accuser == null) {
            return;
        }
        Player oldAccused = charges.get(accuser);
        accuserDone = accusedDone = false;
        if (accused == null) {
            charges.remove(accuser);
            accusingTimer.cancel();
            if (!charges.isEmpty()) {
                accusingTimer.start();
            }
        } else {
            charges.put(accuser, accused);
        }
        broadcastAccusing(oldAccused);
        broadcastAccusing(accused);
    }

    public Player getAccused(Player accuser) {
        if (accuser == null) {
            return null;
        }
        return charges.get(accuser);
    }

    public boolean hasSpeakerCharged() {
        Player accused = getAccused(getSpeaker());
        return accused != null && accused != oldAccusedOfSpeaker;
    }

    public static class Speaker extends Tag {}

    @Getter
    @RequiredArgsConstructor
    public static class Hand extends Tag {
        private final boolean raised;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Accused extends Tag {
        private final String accuserID;
    }

    private void broadcastAccusing(Player player) {
        if (player == null) {
            return;
        }
        PlayerUpdateBuilder playerUpdateBuilder = new PlayerUpdateBuilder(player);
        playerUpdateBuilder.addTag(new Hand(raisedHands.contains(player)));
        if (getSpeaker() == player) {
            playerUpdateBuilder.addTag(new Speaker());
        }
        for (Player accuser : charges.keySet()) {
            Player accused = charges.get(accuser);
            if (accused == player) {
                playerUpdateBuilder.addTag(new Accused(accuser.getPlayerID()));
            }
        }
        broadcast(playerUpdateBuilder.build());
    }

    /*
     * End of code for the ACCUSING phase
     * */
}
