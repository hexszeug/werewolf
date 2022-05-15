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
    private final HashSet<Tag> tags;

    //dependencies
    private final AutoPlayerUpdateService autoPlayerUpdateService;

    /**
     * Creates new Player. This should only be done by some kind of {@code GameSetupService}.
     * @param playerID Same as the userID which is generated when a client joins a room.
     * @param nickname The nickname of the player specified by the client.
     * @param avatar The avatar of the player either as a Base64 image or as an url referring to an image.
     * @param session The session the player is bound to.
     * @param autoPlayerUpdateService The update service to notify when the status or role is changed.
     * @since 1.0-SNAPSHOT
     * */
    public Player(
            String playerID,
            String nickname,
            String avatar,
            Session session,
            PlayerRegistry playerRegistry,
            AutoPlayerUpdateService autoPlayerUpdateService
    ) {
        this.playerID = playerID;
        this.nickname = nickname;
        this.avatar = avatar;
        this.session = session;
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
        PlayerController old = this.playerController;
        this.playerController = playerController;
        autoPlayerUpdateService.onRoleChange(this, old); //must be called after the pC is set
    }

    /**
     * Sets the status of the Player and informs the {@link AutoPlayerUpdateService}.
     * @param status The new status.
     * @since 1.0-SNAPSHOT
     * */
    public void setStatus(Status status) {
        if (status == null || status == this.status || this.status == Status.DEAD) {
            return;
        }
        Status old = this.status;
        this.status = status;
        autoPlayerUpdateService.onStatusChange(this, old); //must be called after the status is set
    }

    /**
     * Adds a {@link Tag} to the Player.
     * @param tag The tag to add.
     * @since 1.0-SNAPSHOT
     * */
    public void addTag(Tag tag) {
        if (tag != null) {
            tags.add(tag);
        }
    }

    /**
     * Removes a {@link Tag} from the Player.
     * <br><b>Note: two tags are equal by default if their class names match.</b>
     * <br>If the Player doesn't have this tag happens nothing.
     * @param tag The tag to remove.
     * @since 1.0-SNAPSHOT
     * */
    public void removeTag(Tag tag) {
        if (tag != null) {
            tags.remove(tag);
        }
    }

    /**
     * Removes all {@link Tag} from the Player by the class of the Tags.
     * This works even if the subclass of {@code Tag} overwrote the {@code equals()} method.
     * If the Player doesn't have this tag happens nothing.
     * @param clazz The class of the Tags to remove.
     * */
    public void removeTag(Class<? extends Tag> clazz) {
        if (clazz == null) {
            return;
        }
        tags.removeIf(clazz::isInstance);
    }

    /**
     * Tests if the player has the passed tag.
     * @param tag The tag to test for if the player has it.
     * @return true - if the player has the tag
     * @since 1.0-SNAPSHOT
     * */
    public boolean hasTag(Tag tag) {
        return tags.contains(tag);
    }

    /**
     * Returns a clone of the tag {@link java.util.Set Set} of the player
     * so the returned object can't modify the tags of the player.
     * @return an {@link Iterable<Tag>} of the tags in this player.
     * @since 1.0-SNAPSHOT
     * */
    @SuppressWarnings("unchecked")
    public Iterable<Tag> tags() {
        return (Iterable<Tag>) tags.clone();
    }



    /**
     * {@inheritDoc}
     * */
    @Override
    public void receive(Request request) throws IllegalRequestException {
        if (playerController != null) {
            playerController.handle(request);
        }
    }
}
