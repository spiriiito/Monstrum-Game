package monstrum.game;

import monstrum.game.UI.*;
import monstrum.model.SurvivalPerk;
import monstrum.game.Team;
import monstrum.model.Monster;
import monstrum.model.equipment.WeaponFactory;
import monstrum.model.rules.WeatherEffect;

public class GameFlowManager {

    private static RuleConfig ruleConfig;
    private static Team player1Team;
    private static Team player2Team;
    private static boolean isPlayerOneTurn = true;

    public static void startGameFlow(RuleConfig config) {
        ruleConfig = config;

        switch (ruleConfig.getGameMode()) {
            case PLAYER_VS_AI:
            case PLAYER_VS_PLAYER:
                new TeamSizeUI(ruleConfig);
                break;

            case SURVIVAL:
                ruleConfig.setMaxTeamSize(1);
                new TeamSelectionUI(ruleConfig, true, null, null, 1);
                break;

            case RANDOM:
                ruleConfig = RuleGenerator.generateRandom();
                Team randomTeam1 = TeamBuilder.buildRandomTeam(3, ruleConfig);
                Team randomTeam2 = TeamBuilder.buildRandomTeam(3, ruleConfig);

                for (Monster m : randomTeam1.getMonsters()) m.setRuleConfig(ruleConfig);
                for (Monster m : randomTeam2.getMonsters()) m.setRuleConfig(ruleConfig);

                // Assign random weapon to one monster
                if (!randomTeam1.getMonsters().isEmpty()) {
                    int index = (int) (Math.random() * randomTeam1.getMonsters().size());
                    Monster chosen = randomTeam1.getMonsters().get(index);
                    TeamBuilder.assignWeaponToRandomMonster(randomTeam1, WeaponFactory.getRandomWeapon());
                }

                Game game = new Game(ruleConfig, randomTeam1, randomTeam2);
                new BattleUI(game, ruleConfig, randomTeam1, randomTeam2, false);

                break;
        }

        if (ruleConfig.isHardcoreMode()) {
            ruleConfig.setPotionsEnabled(false);

        }
    }

    public static void handleTeamSizeSelected(RuleConfig config) {
        int maxSize = config.getMaxTeamSize();
        new TeamSelectionUI(config, true, null, null, maxSize);
    }

    public static void handleTeamSelected(Team selectedTeam) {
        if (ruleConfig.getGameMode() == RuleConfig.GameMode.PLAYER_VS_PLAYER) {
            if (isPlayerOneTurn) {
                player1Team = selectedTeam;
                isPlayerOneTurn = false;
                new TeamSelectionUI(ruleConfig, false, player1Team, null, ruleConfig.getMaxTeamSize());
            } else {
                player2Team = selectedTeam;
                new WeaponArenaUI(ruleConfig, player1Team, player2Team);
            }
        } else {
            player1Team = selectedTeam;
            new WeaponArenaUI(ruleConfig, player1Team, null);
        }
    }

    public static void handleWeaponAndArenaSelected(String weapon, String arena) {
        ruleConfig.setArenaType(RuleConfig.ArenaType.valueOf(arena));

        if (ruleConfig.isHardcoreMode()) {
            ruleConfig.setPotionsEnabled(false);
            if (ruleConfig.getWeatherEffect() == null)
                ruleConfig.setWeatherEffect(WeatherEffect.getRandomWeather());
        }

        if (ruleConfig.getGameMode() == RuleConfig.GameMode.SURVIVAL) {
            new SurvivalPerkUI(ruleConfig, player1Team);
        } else {
            Game game = new Game(ruleConfig, player1Team, player2Team);
            new BattleUI(game, ruleConfig, player1Team, player2Team, false);
        }
    }
}
