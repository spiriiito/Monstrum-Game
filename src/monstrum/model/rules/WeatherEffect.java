package monstrum.model.rules;

import java.util.Random;

public enum WeatherEffect {
    RAIN ("All monsters lose -5 energy per turn."),
    STORM ("10% chance for each attack to backfire (deal 5 damage to attacker)."),
    FOG("25% chance to miss basic attacks."),
    HEATWAVE("Every 2 turns, monsters take 3 burn damage."),
    MAGNETIC_FIELD("Every 3 turns, special attacks are disabled for this turn.");

    private final String description;

    WeatherEffect(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static WeatherEffect getRandomWeather() {
        WeatherEffect[] effects = values();
        return effects[new Random().nextInt(effects.length)];
    }
}