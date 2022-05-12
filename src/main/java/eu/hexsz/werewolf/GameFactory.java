package eu.hexsz.werewolf;

import eu.hexsz.werewolf.api.Session;
import eu.hexsz.werewolf.controller.*;
import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.player.PlayerRegistry;
import eu.hexsz.werewolf.role.standard.Villager;
import eu.hexsz.werewolf.role.standard.Werewolf;
import eu.hexsz.werewolf.time.Time;
import eu.hexsz.werewolf.update.AutoPlayerUpdateService;

import java.util.ArrayList;
import java.util.Random;

public class GameFactory {
    private boolean built;
    private ArrayList<User> users;
    private ArrayList<String> roles;

    public GameFactory(Session[] sessions) {
        built = false;
        users = new ArrayList<>();
        roles = new ArrayList<>();
        Random random = new Random();
        for (Session session : sessions) {
            users.add(new User(
                    session.getSessionID(),
                    "name" + session.getSessionID(),
                    "avatar",
                    session
            ));
            if (random.nextBoolean()) {
                roles.add("VILLAGER");
            } else {
                roles.add("WEREWOLF");
            }
        }
    }

    public void build() {
        if (built) {
            return;
        }
        built = true;
        Time time = new Time();
        PlayerRegistry playerRegistry = new PlayerRegistry();
        AutoPlayerUpdateService autoPlayerUpdateService = new AutoPlayerUpdateService(playerRegistry);
        NightController nightController = new NightController(time, playerRegistry);
        DayController dayController = new DayController(time);
        GameController gameController = new GameController(time, nightController, dayController);
        ExecutionService executionService = new ExecutionService();
        Random random = new Random();
        for (User user : users) {
            Player player = new Player(
                    user.getPlayerID(),
                    user.getNickname(),
                    user.getAvatar(),
                    user.getSession(),
                    playerRegistry,
                    autoPlayerUpdateService
            );
            player.setPlayerController(
                    switch (roles.remove(random.nextInt(roles.size()))) {
                        case "WEREWOLF" -> new Werewolf(player, playerRegistry, nightController, time);
                        default -> new Villager();
                    }
            );
        }
        for (Player player : playerRegistry) {
            autoPlayerUpdateService.onPlayerCreated(player);
        }
        new Job("game", gameController::startGame, null).start(); //TODO handle game end
    }
}
