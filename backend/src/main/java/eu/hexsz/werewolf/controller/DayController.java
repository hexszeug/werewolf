package eu.hexsz.werewolf.controller;

import eu.hexsz.werewolf.api.Message;
import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.player.PlayerRegistry;
import eu.hexsz.werewolf.player.Status;
import eu.hexsz.werewolf.player.Tag;
import eu.hexsz.werewolf.time.DayPhase;
import eu.hexsz.werewolf.time.Phase;
import eu.hexsz.werewolf.time.Time;
import eu.hexsz.werewolf.update.PlayerUpdateBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.*;

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
    final static int ACCUSING_TIMEOUT = 5000;
    static final int HEARING_TIMEOUT = 20000;

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

        this.job = job;

        startPhase();
    }

    private void startPhase() {
        if (!(time.getPhase() instanceof DayPhase phase)) {
            job.done();
            return;
        }
        switch (phase) {
            case SUNRISE -> {

                /*
                * Awakes all players that are not dead.
                * */

                for (Player player : playerRegistry) {
                    if (player.getStatus() != Status.DEAD) {
                        player.setStatus(Status.AWAKE);
                    }
                }

                endPhase();

                /*
                 * End of SUNRISE
                 * */

            }

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
                        (Job job) -> {
                            executionService.kill(job, executees.toArray(new Player[0]));
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
                ArrayList<Player> hearingList = new ArrayList<>(new HashSet<>(charges.values()));
                Collections.shuffle(hearingList);
                startCourt(hearingList);

                /*
                 * End of JUDGING
                 * */

            }

            case EXECUTION -> {

                /*
                *
                * */

                new Job(
                        String.format("%s:execution", time.getNight()),
                        (Job job) -> {
                            executionService.kill(job, executee);
                        },
                        this::endPhase
                ).start();

                /*
                 * End of EXECUTION
                 * */

            }

            case SUNSET -> {

                /*
                * Let all players fall asleep and ends the day (phase stays at SUNSET).
                * */

                for (Player player : playerRegistry) {
                    if (player.getStatus() == Status.AWAKE) {
                        player.setStatus(Status.SLEEPING);
                    }
                }

                job.done();

                /*
                 * End of SUNSET
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

    public static class Hand extends Tag {}

    @Getter
    public static class Accused extends Tag {
        private final String accuserID;

        private Accused(Player accuser) {
            accuserID = accuser.getPlayerID();
        }
    }

    private void broadcastAccusing(Player player) {
        if (player == null) {
            return;
        }
        PlayerUpdateBuilder playerUpdateBuilder = new PlayerUpdateBuilder(player);
        if (raisedHands.contains(player)) {
            playerUpdateBuilder.addTag(new Hand());
        }
        if (getSpeaker() == player) {
            playerUpdateBuilder.addTag(new Speaker());
        }
        for (Map.Entry<Player, Player> entry : charges.entrySet()) {
            if (entry.getValue() == player) {
                playerUpdateBuilder.addTag(new Accused(entry.getKey()));
            }
        }
        broadcast(playerUpdateBuilder.build());
    }

    /*
     * End of code for the ACCUSING phase
     * */

    /*
    * Code for the JUDGING phase
    * */

    private Iterator<Player> courtHearings;
    private @Getter Player currentDefendant;
    private Timer hearingTimer;
    private HashMap<Player, Player> votes;
    private HashMap<Player, HashSet<Player>> voters;

    private void startCourt(Collection<Player> hearingList) {
        if (hearingList == null || hearingList.isEmpty()) {
            endPhase();
            return;
        }
        courtHearings = hearingList.iterator();
        votes = new HashMap<>();
        voters = new HashMap<>();
        for (Player defendant : hearingList) {
            voters.put(defendant, new HashSet<>());
            broadcastJudging(defendant);
        }
        nextHearing();
    }

    private void nextHearing() {
        if (!courtHearings.hasNext()) {
            countVotes();
            return;
        }
        Player oldDefendant = currentDefendant;
        currentDefendant = courtHearings.next();
        broadcastJudging(oldDefendant);
        broadcastJudging(currentDefendant);
        if (courtHearings.hasNext()) {
            hearingTimer = new Timer("hearing", HEARING_TIMEOUT, this::nextHearing);
            hearingTimer.start();
            return;
        }

        //auto-vote for last defendant (only players who didn't vote yet)
        for (Player voter : playerRegistry) {
            vote(voter);
        }
        countVotes();
    }

    public void vote(Player voter) {
        if (voter == null || currentDefendant == null || votes.containsKey(voter)) {
            return;
        }
        votes.put(voter, currentDefendant);
        voters.get(currentDefendant).add(voter);
        broadcastJudging(currentDefendant);

        //skip rest of voting when all players voted
        if (votes.size() >= playerRegistry.size()) {
            hearingTimer.cancel();
            countVotes();
        }
    }

    public Player getVote(Player voter) {
        return votes.get(voter);
    }

    private void countVotes() {
        HashSet<Player> highest = new HashSet<>();
        int highestVotes = 0;
        for (Map.Entry<Player, HashSet<Player>> entry : voters.entrySet()) {
            int votes = entry.getValue().size();
            if (votes > highestVotes) {
                highest = new HashSet<>();
                highestVotes = votes;
            }
            if (votes == highestVotes) {
                highest.add(entry.getKey());
            }
        }
        if (highest.size() > 1) {
            //todo add mayor
            startCourt(highest);
            return;
        }
        executee = highest.toArray(new Player[0])[0];
        broadcastJudging(executee);
        endPhase();
    }

    private void broadcastJudging(Player player) {
        if (player == null) {
            return;
        }
        PlayerUpdateBuilder playerUpdateBuilder = new PlayerUpdateBuilder(player);
        if (voters.containsKey(player)) {
            playerUpdateBuilder.addTag(new Defendant(player == currentDefendant));
            for (Player voter : voters.get(player)) {
                playerUpdateBuilder.addTag(new Vote(voter));
            }
            if (player == executee) {
                playerUpdateBuilder.addTag(new Executee());
            }
        }
        broadcast(playerUpdateBuilder.build());
    }

    @RequiredArgsConstructor
    @Getter
    public static class Defendant extends Tag {
        private final boolean current;
    }

    @Getter
    public static class Vote extends Tag {
        private final String voterID;

        private Vote(Player voter) {
            this.voterID = voter.getPlayerID();
        }
    }

    /*
     * End of code for the JUDGING phase
     * */

    /*
     * Code for the EXECUTION phase
     * */

    private Player executee;

    public static class Executee extends Tag {}
}
