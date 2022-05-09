package eu.hexsz.werewolf.update;

import eu.hexsz.werewolf.api.IllegalRequestException;
import eu.hexsz.werewolf.api.Message;
import eu.hexsz.werewolf.api.Request;
import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.player.Status;
import eu.hexsz.werewolf.role.PlayerController;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerUpdateBuilderTest {

    @Test
    void build() {
        //given
        class NiceABRole implements PlayerController {
            @Override
            public void handle(Request request) {}
        }
        Player player = mock(Player.class);
        when(player.getPlayerID()).thenReturn("test-id");
        Message message1 = new PlayerUpdateBuilder(player).build();
        Message message2 = new PlayerUpdateBuilder(player).setStatus(Status.AWAKE).build();
        Message message3 = new PlayerUpdateBuilder(player).setRole(new NiceABRole()).build();
        Message message4 = new PlayerUpdateBuilder(player)
                .setStatus(Status.SLEEPING)
                .setRole(new NiceABRole())
                .build();

        //when


        //expect
        assertNull(message1);
        assertEquals("game", message2.getPath());
        assertEquals("player", message2.getType());
        assertEquals("test-id", ((HashMap<String, Object>)message2.getData()).get("playerID"));
        assertEquals("AWAKE", ((HashMap<String, Object>)message2.getData()).get("status"));
        assertFalse(((HashMap<String, Object>)message2.getData()).containsKey("role"));
        assertEquals("NICE_AB_ROLE", ((HashMap<String, Object>)message3.getData()).get("role"));
        assertFalse(((HashMap<String, Object>)message3.getData()).containsKey("status"));
        assertEquals("SLEEPING", ((HashMap<String, Object>)message4.getData()).get("status"));
        assertEquals("NICE_AB_ROLE", ((HashMap<String, Object>)message4.getData()).get("role"));
    }
}