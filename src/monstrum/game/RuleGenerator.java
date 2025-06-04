package monstrum.game;

import monstrum.model.rules.WeatherEffect;
import monstrum.game.RuleConfig.ArenaType;

public class RuleGenerator {
    public static RuleConfig generateRandom() {
        RuleConfig config = new RuleConfig();
        config.setGameMode(RuleConfig.GameMode.RANDOM);
        config.setArenaType(ArenaType.getRandomArena());
        config.setWeatherEffect(WeatherEffect.getRandomWeather());
        config.setPotionsEnabled(true);
        config.setWeaponsEnabled(true);
        config.setMaxTeamSize(3);

        return config;
    }
}