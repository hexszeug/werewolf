package eu.hexsz.werewolf.role;

import eu.hexsz.werewolf.api.IllegalRequestException;
import eu.hexsz.werewolf.api.Request;
import eu.hexsz.werewolf.controller.DayController;
import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.player.PlayerRegistry;
import eu.hexsz.werewolf.player.Status;
import eu.hexsz.werewolf.time.DayPhase;
import eu.hexsz.werewolf.time.Time;
import lombok.RequiredArgsConstructor;

import static eu.hexsz.werewolf.api.RequestHandler.*;

/**
 * Contains the handling for the standard player actions. Must be inherited by each role.
 * The class of the {@link PlayerController} stored in the corresponding
 * {@link Player} determines which role the player has.
 * Also contains the implementation for handling standard requests.
 * @implNote All specific role behavior should only be in the implementation of this class and not in other classes.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
@RequiredArgsConstructor
public abstract class AbstractRole implements PlayerController {

    //dependencies
    private final Player player;
    private final PlayerRegistry playerRegistry;
    private final DayController dayController;
    private final Time time;

    /**
     * Contains the request handling for all non role-specific requests.
     * Should be called by new implementations when the request is not role-specific.
     * @param request The request
     * @throws IllegalRequestException When the request type doesn't exist,
     * the request can't be handled or
     * additional data is missing.
     * @since 1.0-SNAPSHOT
     * */
    @Override
    public void handle(Request request) throws IllegalRequestException {
        if (request == null) {
            return;
        }
        switch (request.getType()) {
            case "accusing/raise-hand" -> {

                /*
                 * Invoked by a player to raise / take down their hand.
                 * */

                checkPhase(time, DayPhase.ACCUSING, request);
                checkAwake(player, request);

                /*
                 * When the data is...
                 * ...true : hand is raised.
                 * ...false: hand is taken down.
                 * */

                if (request.getData(Boolean.class)) {

                    dayController.raiseHand(player);

                } else {

                    if (dayController.getSpeaker() == player && dayController.hasSpeakerCharged()) {
                        throw new IllegalRequestException(
                                "Cannot be invoked when you are currently speaking and have charged someone.",
                                request
                        );
                    }

                    dayController.takeHandDown(player);

                }

                /*
                 * End of case "accusing/raise-hand"
                 * */

            }

            case "accusing/done" -> {

                /*
                 * Is invoked by an accuser or an accused to set if they are done with their discussion.
                 * */

                checkPhase(time, DayPhase.ACCUSING, request);
                checkAwake(player, request);

                Player accuser = dayController.getSpeaker();
                Player accused = dayController.getAccused(accuser);

                if (!dayController.hasSpeakerCharged() ||
                        player != accuser && player != accused) {
                    throw new IllegalRequestException(
                            "Can only be invoked when you are currently accusing or being accused by someone.",
                            request
                    );
                }

                /*
                 * When the data is...
                 * ...true: the player is done.
                 * ...false: the player changed their mind and is not yet done.
                 * */

                boolean data = request.getData(Boolean.class);

                /*
                 * Takes the hand of the accuser down if both players confirmed they don't want to say anymore.
                 * */

                if (dayController.getSpeaker() == player) {
                    dayController.setAccuserDone(data);
                    if (dayController.isAccuserDone() && dayController.isAccusedDone()) {
                        dayController.takeHandDown(player);
                    }
                } else if (dayController.getAccused(dayController.getSpeaker()) == player) {
                    dayController.setAccusedDone(data);
                    if (dayController.isAccuserDone() && dayController.isAccusedDone()) {
                        dayController.takeHandDown(dayController.getSpeaker());
                    }
                }

                /*
                 * End of case "accusing/done"
                 * */

            }

            case "accusing/charge" -> {

                /*
                 * Is invoked by the current speaker when they announce who they charge.
                 * Can also be invoked by anybody who wants to withdraw his charge.
                 * */

                checkPhase(time, DayPhase.ACCUSING, request);
                checkAwake(player, request);

                /*
                 * Withdraw charge if passed accused is null.
                 * */

                if (request.getData() == null) {
                    dayController.charge(player, null);
                    break;
                }

                /*
                 * Charge passed accused.
                 * */

                if (dayController.getSpeaker() != player) {
                    throw new IllegalRequestException(
                            "Can only be invoked when you are currently speaking.",
                            request
                    );
                }
                Player accused = playerRegistry.getPlayer(request.getData(String.class));
                if (accused == null) {
                    throw new IllegalRequestException(
                            "Passed playerID doesn't exist.",
                            request
                    );
                }
                if (accused.getStatus() == Status.DEAD) {
                    throw new IllegalRequestException(
                            "Player is already dead.",
                            request
                    );
                }
                if (accused == player) {
                    throw new IllegalRequestException(
                            "Cannot charge yourself.",
                            request
                    );
                }
                dayController.charge(player, accused);

                /*
                 * End of case "accusing/charge"
                 * */

            }

            case "judging/vote" -> {

                /*
                * Is invoked to vote for the current defendant.
                * Can only be invoked if the player didn't vote yet.
                * */

                checkPhase(time, DayPhase.JUDGING, request);
                checkAwake(player, request);

                if (dayController.getVote(player) != null) {
                    throw new IllegalRequestException(
                            "You can only vote once.",
                            request
                    );
                }
                if (dayController.getCurrentDefendant() == player) {
                    throw new IllegalRequestException(
                            "You cannot vote for yourself.",
                            request
                    );
                }

                dayController.vote(player);

                /*
                 * End of case "judging/vote"
                 * */

            }

            default -> throw new IllegalRequestException(
                    String.format("Request type \"%s\" is not a method.", request.getType()),
                    request
            );
        }
    }
}
