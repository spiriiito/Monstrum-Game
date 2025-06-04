package monstrum.game;

public class Main {
    public static void main(String[] args) {

        RuleConfig config = new RuleConfig();
        config.setGameMode(RuleConfig.GameMode.RANDOM);
        config.setPotionsEnabled(true);
        config.setHardcoreMode(false);
        //config.setArenaType(RuleConfig.ArenaType.DEFAULT);
        config.setMaxTeamSize(4);

        Game game = new Game(config);
        game.startGame();
        //game.runSurvivalMode();
    }
}
