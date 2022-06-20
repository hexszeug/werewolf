package eu.hexsz.werewolf.update;

import eu.hexsz.werewolf.api.Message;
import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.player.Status;
import eu.hexsz.werewolf.player.Tag;
import eu.hexsz.werewolf.role.PlayerController;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Used to build a {@link Message} which contains new information about a certain player.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
@Getter
@Accessors(chain = true) @Setter
public class PlayerUpdateBuilder {
    private Player player;
    private Status status;
    private PlayerController role;
    private String nickname;
    private String avatar;
    private boolean isMe;
    private final ArrayList<Tag> tags;

    public PlayerUpdateBuilder(Player player) {
        this.player = player;
        tags = new ArrayList<>();
    }

    public Message build() {
        if (player == null) {
            return null;
        }
        HashMap<String, Object> data = new HashMap<>();
        if (status != null) {
            data.put("status", status.toString());
        }
        if (role != null) {
            data.put("role", new ClassNameSerializer(role).value());
        }
        if (nickname != null) {
            data.put("nickname", nickname);
        }
        if (avatar != null) {
            data.put("avatar", avatar);
        }
        if (isMe) {
            data.put("isMe", true);
        }
        data.put("tags", tags);
        data.put("playerID", player.getPlayerID());
        return new Message(Player.PATH, "player", data);
    }

    public PlayerUpdateBuilder addTag(Tag tag) {
        if (tag != null) {
            tags.add(tag);
        }
        return this;
    }
}
