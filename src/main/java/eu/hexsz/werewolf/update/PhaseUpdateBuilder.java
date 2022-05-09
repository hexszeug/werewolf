package eu.hexsz.werewolf.update;

import eu.hexsz.werewolf.api.Message;
import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.time.Phase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Used to build a {@link Message} which contains information about a new phase.
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Accessors(chain = true) @Setter
public class PhaseUpdateBuilder {
    private Phase phase;

    public Message build() {
        if (phase == null) {
            return null;
        }
        return new Message(Player.PATH, "phase",
                new ClassNameSerializer(phase).value()
                        + "."
                        + phase
        );
    }
}
