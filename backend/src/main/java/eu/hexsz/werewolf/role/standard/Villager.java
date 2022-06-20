package eu.hexsz.werewolf.role.standard;

import eu.hexsz.werewolf.controller.DayController;
import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.player.PlayerRegistry;
import eu.hexsz.werewolf.role.AbstractRole;
import eu.hexsz.werewolf.time.Time;

public class Villager extends AbstractRole {
    public Villager(Player player, PlayerRegistry playerRegistry, DayController dayController, Time time) {
        super(player, playerRegistry, dayController, time);
    }
}
