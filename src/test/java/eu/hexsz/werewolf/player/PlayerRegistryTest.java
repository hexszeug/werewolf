package eu.hexsz.werewolf.player;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerRegistryTest {

    @Test
    void getPlayersIterable() {
        //given
        PlayerRegistry playerRegistry = new PlayerRegistry();
        Player player = mock(Player.class);
        when(player.getPlayerID()).thenReturn("testID");

        //when
        playerRegistry.addPlayer(player);

        //expect
        for (Player player1 : playerRegistry) {
            assertEquals(player, player1);
        }
    }

    @Test
    void getPlayer() {
        //given
        PlayerRegistry playerRegistry = new PlayerRegistry();
        Player player = mock(Player.class);
        when(player.getPlayerID()).thenReturn("testID");

        //when
        playerRegistry.addPlayer(player);

        //expect
        assertEquals(player, playerRegistry.getPlayer("testID"));
        assertNull(playerRegistry.getPlayer("falseID"));
    }
}