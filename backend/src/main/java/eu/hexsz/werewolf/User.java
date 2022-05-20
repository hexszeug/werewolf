package eu.hexsz.werewolf;

import eu.hexsz.werewolf.api.Session;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class User {
    private final String playerID;
    private String nickname;
    private String avatar;
    private final Session session;
}
