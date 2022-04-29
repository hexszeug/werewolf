package eu.hexsz.werewolf.player;

import eu.hexsz.werewolf.api.Request;
import eu.hexsz.werewolf.api.RequestHandler;
import eu.hexsz.werewolf.api.Session;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashSet;

/**
 * Represents a player in a certain game.
 * Holds all information and passes requests from the client to the {@link PlayerController}.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
//TODO add more tests + add auto updater + pass requests to the playerController
@Getter
public class Player implements RequestHandler {
    private final static String PATH = "game";

    private final String playerID;
    private final String nickname;
    private final String avatar;
    private final @NonNull Session session;
    private @NonNull PlayerController playerController;
    private @NonNull Status status;
    private final HashSet<Tag> tags;

    /**
     * Creates new Player. This should only be done by some kind of {@code GameSetupService}.
     * @param playerID Same as the userID which is generated when a client joins a room.
     * @param nickname The nickname of the player specified by the client.
     * @param avatar The avatar of the player either as a Base64 image or as an url referring to an image.
     * @param session The session the player is bound to.
     * @param playerController The PlayerController / role of the player.
     * @since 1.0-SNAPSHOT
     * */
    public Player(String playerID, String nickname, String avatar, Session session, PlayerController playerController,
                  PlayerRegistry playerRegistry) {
        this.playerID = playerID;
        this.nickname = nickname;
        this.avatar = avatar;
        this.session = session;
        this.playerController = playerController;
        status = Status.SLEEPING;
        tags = new HashSet<>();
        session.bindReceiver(PATH, this);
        playerRegistry.addPlayer(this);
    }

    /**
     * Sets a new {@link PlayerController} and informs the {@link AutoPlayerUpdateService}.
     * @param playerController The new {@code PlayerController}
     * @since 1.0-SNAPSHOT
     * */
    public void setPlayerController(PlayerController playerController) {
        this.playerController = playerController;
        //TODO inform AutoPlayerUpdateService
    }

    /**
     * Sets the status of the Player. If the new status is {@link Status#DEAD} a {@link SecurityException} is raises.
     * For killing the player you must use {@link Player#setStatus(Status, Object)}
     * and pass the {@link PlayerController} of that player. This should only be done by the PlayerController itself.
     * @param status The new status
     * @see Player#setStatus(Status, Object)
     * @since 1.0-SNAPSHOT
     * */
    public void setStatus(Status status) {
        if (status == Status.DEAD) {
            throw new SecurityException("Players can only be killed by their PlayerControllers.");
        }
        setStatus(status, playerController);
    }

    /**
     * Sets the status of the Player.
     * Automatically informs the {@link AutoPlayerUpdateService}.
     * @param status The new status.
     * @param caller Must be the object which calls the method.
     *               If the object is not the PlayerController of the Player a {@link SecurityException} is raised.
     * @since 1.0-SNAPSHOT
     * */
    public void setStatus(Status status, Object caller) {
        if (status == Status.DEAD && !playerController.equals(caller)) {
            throw new SecurityException("Players can only be killed by their PlayerControllers.");
        }
        this.status = status;
        //TODO inform AutoPlayerUpdateService
    }

    /**
     * Adds a {@link Tag} to the Player.
     * @param tag The tag to add.
     * @see Tag
     * @since 1.0-SNAPSHOT
     * */
    public void addTag(Tag tag) {
        tags.add(tag);
    }

    /**
     * Removes a {@link Tag} from the Player.
     * @param tag The tag to remove.
     * @see Tag
     * @since 1.0-SNAPSHOT
     * */
    public void removeTag(Tag tag) {
        tags.remove(tag);
    }



    /**
     * {@inheritDoc}
     * */
    @Override
    public void receive(Request request) {
        //TODO pass request to playerController
    }
}
