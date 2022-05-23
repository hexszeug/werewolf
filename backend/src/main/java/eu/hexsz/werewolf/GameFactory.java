package eu.hexsz.werewolf;

import eu.hexsz.werewolf.api.Message;
import eu.hexsz.werewolf.api.Session;
import eu.hexsz.werewolf.controller.*;
import eu.hexsz.werewolf.player.Player;
import eu.hexsz.werewolf.player.PlayerRegistry;
import eu.hexsz.werewolf.role.standard.Villager;
import eu.hexsz.werewolf.role.standard.Werewolf;
import eu.hexsz.werewolf.time.Time;
import eu.hexsz.werewolf.update.AutoPlayerUpdateService;
import eu.hexsz.werewolf.update.PhaseUpdateBuilder;

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
            String[] roleNames = new String[]{
                    "VILLAGER"
                    ,"WEREWOLF"
            };
            if (roleNames.length > 1) {
                roles.add(roleNames[random.nextInt(roleNames.length)]);
            } else {
                roles.add(roleNames[0]);
            }
        }
    }

    public void build() {
        if (built) {
            return;
        }
        built = true;

        /*
        * Create singletons and inject them in each other
        * */

        PlayerRegistry playerRegistry = new PlayerRegistry();
        AutoPlayerUpdateService autoPlayerUpdateService = new AutoPlayerUpdateService(playerRegistry);
        //create players as early as possible
        for (User user : users) {
            new Player(
                    user.getPlayerID(),
                    user.getNickname(),
                    user.getAvatar(),
                    user.getSession(),
                    playerRegistry,
                    autoPlayerUpdateService
            );
        }
        Time time = new Time(playerRegistry);
        ExecutionService executionService = new ExecutionService(playerRegistry);
        NightController nightController = new NightController(time, playerRegistry);
        DayController dayController = new DayController(playerRegistry, executionService, time);
        GameController gameController = new GameController(time, nightController, dayController,
                playerRegistry, autoPlayerUpdateService);

        /*
        * Distribute roles
        * */

        Random random = new Random();
        for (Player player : playerRegistry) {
            player.setPlayerController(
                    switch (roles.remove(random.nextInt(roles.size()))) {
                        case "WEREWOLF" -> new Werewolf(player, playerRegistry, dayController, nightController, time);
                        default -> new Villager(player, playerRegistry, dayController, time);
                    }
            );
        }

        /*
        * Start game
        * */

        new Job("game", gameController::startGame, null).start(); //TODO handle game end
    }
}
