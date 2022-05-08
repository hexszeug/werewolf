package eu.hexsz.werewolf.role.standard;

import eu.hexsz.werewolf.controller.Job;
import eu.hexsz.werewolf.controller.NightController;
import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.role.AbstractRole;
import eu.hexsz.werewolf.role.NightActive;
import eu.hexsz.werewolf.time.NightPhase;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Werewolf extends AbstractRole implements NightActive {

    //dependencies
    private final Player player;
    private final NightController nightController;

    @Override
    public void setAlarms() {
        nightController.setAlarm(NightPhase.WEREWOLVES, player);
    }

    @Override
    public void manageNightPhase(Job job) {
        //TODO manage werewolf phase
    }
}
