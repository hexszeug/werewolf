package eu.hexsz.werewolf.update;

import eu.hexsz.werewolf.api.Message;
import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.player.PlayerRegistry;
import eu.hexsz.werewolf.player.Status;
import eu.hexsz.werewolf.role.PlayerController;
import lombok.RequiredArgsConstructor;

/**
 * Listens on {@link Player#setStatus(Status)} and {@link Player#setPlayerController(PlayerController)}
 * and automatically updates all clients with the new information.
 * @see AutoPlayerUpdateService#onStatusChange(Player, Status)
 * @see AutoPlayerUpdateService#onRoleChange(Player)
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
@RequiredArgsConstructor
public class AutoPlayerUpdateService {
    //dependencies
    private final PlayerRegistry playerRegistry;

    /**
     * Listens of {@link Player#setStatus(Status)} and informs all clients about the change in the four cases:
     * <ol>
     *     <li>{@link Status#AWAKE} -> {@link Status#SLEEPING}
     *     <ul>
     *         <li>Informs every awake and dead player about the updated sleeping player.
     *         <li>Informs the updated player that they are sleeping.
     *     </ul>
     *     <li>{@link Status#SLEEPING} -> {@link Status#AWAKE}
     *     <ul>
     *         <li>Informs every awake and dead player about the updated awake player.
     *         <li>Informs the updated player that they are awake.
     *         <li>Informs the updated player about the status of all other players.
     *     </ul>
     *     <li>{@link Status#SLEEPING} -> {@link Status#DEAD}
     *     <ul>
     *         <li>Informs every player about the updated dead player.
     *         <li>Informs every player about the role of the updated player.
     *         <li>Informs the updated player that they are dead.
     *         <li>Informs the updated player about the status of all other player.
     *     </ul>
     *     <li>{@link Status#AWAKE} -> {@link Status#DEAD}
     *     <ul>
     *         <li>Informs every player about the updated dead player.
     *         <li>Informs every player about the role of the updated player.
     *         <li>Informs the updated player that they are dead.
     *     </ul>
     * </ol>
     * @since 1.0-SNAPSHOT
     * */
    public void onStatusChange(Player updatedPlayer, Status oldStatus) {
        if (updatedPlayer == null || oldStatus == null || updatedPlayer.getStatus() == oldStatus) {
            return;
        }
        PlayerUpdateBuilder playerUpdateBuilder = new PlayerUpdateBuilder(updatedPlayer)
                .setStatus(updatedPlayer.getStatus());
        if (updatedPlayer.getStatus() == Status.DEAD) {
            playerUpdateBuilder.setRole(updatedPlayer.getPlayerController());
        }
        Message message = playerUpdateBuilder.build();
        updatedPlayer.getSession().send(message);
        for (Player player : playerRegistry) {
            if (player != updatedPlayer) {
                if (updatedPlayer.getStatus() == Status.DEAD || player.getStatus() != Status.SLEEPING) {
                    player.getSession()
                            .send(
                                    message
                            );
                }
                if (oldStatus == Status.SLEEPING) {
                    updatedPlayer.getSession()
                            .send(
                                    new PlayerUpdateBuilder(player)
                                            .setStatus(player.getStatus())
                                            .build()
                            );
                }
            }
        }
    }

    /**
     * Listens of {@link Player#setPlayerController(PlayerController)} and informs the client about his new role.
     * @since 1.0-SNAPSHOT
     * */
    public void onRoleChange(Player updatedPlayer) {
        if (updatedPlayer == null) {
            return;
        }
        updatedPlayer
                .getSession()
                .send(
                        new PlayerUpdateBuilder(updatedPlayer)
                                .setRole(updatedPlayer.getPlayerController())
                                .build()
                );
    }
}
