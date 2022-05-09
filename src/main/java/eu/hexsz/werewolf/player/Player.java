package eu.hexsz.werewolf.player;

import eu.hexsz.werewolf.api.IllegalRequestException;
import eu.hexsz.werewolf.api.Request;
import eu.hexsz.werewolf.api.RequestHandler;
import eu.hexsz.werewolf.api.Session;
import eu.hexsz.werewolf.role.PlayerController;
import eu.hexsz.werewolf.update.AutoPlayerUpdateService;
import lombok.Getter;
import lombok.NonNull;

import java.util.HashSet;

/**
 * Represents a player in a certain game.
 * Holds all information and passes requests from the client to the {@link PlayerController}.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
//TODO add more tests
public class Player implements RequestHandler {
    public static final String PATH = "game";
    /**
     * {@inheritDoc}
     * */
    @Override
    public String PATH() {
        return PATH;
    }

    private final @Getter String playerID;
    private final @Getter String nickname;
    private final @Getter String avatar;
    private final @Getter Session session;
    private @Getter PlayerController playerController;
    private @Getter Status status;
    private final @Getter HashSet<Tag> tags;

    //dependencies
    private final AutoPlayerUpdateService autoPlayerUpdateService;

    /**
     * Creates new Player. This should only be done by some kind of {@code GameSetupService}.
     * @param playerID Same as the userID which is generated when a client joins a room.
     * @param nickname The nickname of the player specified by the client.
     * @param avatar The avatar of the player either as a Base64 image or as an url referring to an image.
     * @param session The session the player is bound to.
     * @param playerController The PlayerController / role of the player.
     * @param autoPlayerUpdateService
     * @since 1.0-SNAPSHOT
     * */
    public Player(String playerID,
                  String nickname,
                  String avatar,
                  @NonNull Session session,
                  @NonNull PlayerController playerController,
                  PlayerRegistry playerRegistry,
                  AutoPlayerUpdateService autoPlayerUpdateService) {
        this.playerID = playerID;
        this.nickname = nickname;
        this.avatar = avatar;
        this.session = session;
        this.playerController = playerController;
        this.autoPlayerUpdateService = autoPlayerUpdateService;
        status = Status.SLEEPING;
        tags = new HashSet<>();
        session.bindReceiver(this);
        playerRegistry.addPlayer(this);
    }

    /**
     * Sets a new {@link PlayerController} and informs the {@link AutoPlayerUpdateService}.
     * @param playerController The new {@code PlayerController}
     * @since 1.0-SNAPSHOT
     * */
    public void setPlayerController(PlayerController playerController) {
        if (playerController == null || playerController == this.playerController) {
            return;
        }
        this.playerController = playerController;
        autoPlayerUpdateService.onRoleChange(this);
    }

    /**
     * Sets the status of the Player and informs the {@link AutoPlayerUpdateService}.
     * @param status The new status.
     * @since 1.0-SNAPSHOT
     * */
    public void setStatus(Status status) {
        if (status == null || status == this.status) {
            return;
        }
        Status old = this.status;
        this.status = status;
        autoPlayerUpdateService.onStatusChange(this, old);
    }

    /**
     * Adds a {@link Tag} to the Player.
     * @param tag The tag to add.
     * @see Tag
     * @since 1.0-SNAPSHOT
     * */
    public void addTag(Tag tag) {
        if (tag != null) {
            tags.add(tag);
        }
    }

    /**
     * Removes a {@link Tag} from the Player.
     * @param tag The tag to remove.
     * @see Tag
     * @since 1.0-SNAPSHOT
     * */
    public void removeTag(Tag tag) {
        if (tag != null) {
            tags.remove(tag);
        }
    }



    /**
     * {@inheritDoc}
     * */
    @Override
    public void receive(Request request) throws IllegalRequestException {
        playerController.handle(request);
    }
}
