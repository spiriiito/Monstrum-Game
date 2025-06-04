package monstrum.game;

import monstrum.model.rules.WeatherEffect;

import java.util.Random;
import monstrum.model.rules.WeatherEffect;

public class RuleConfig {

    public enum GameMode {
        PLAYER_VS_PLAYER,
        PLAYER_VS_AI,
        SURVIVAL,
        RANDOM,
    }

    public enum ArenaType {
        DEFAULT,
        VOLCANO,
        SWAMP,
        DARK_FOREST,
        OCEAN,
        ZEUS_MOUNTAIN;

        private static final Random rng = new Random();

        public static ArenaType getRandomArena() {
            ArenaType[] values = ArenaType.values();
            return values[rng.nextInt(values.length)];
        }
    }

    private int maxTeamSize = 3;
    private boolean potionsEnabled = true;
    private boolean hardcoreMode = false;
    private GameMode gameMode = GameMode.PLAYER_VS_AI;
    private ArenaType arenaType = ArenaType.DEFAULT;
    private WeatherEffect weatherEffect;
    private boolean weaponsEnabled = true;
    private String selectedWeapon;

    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    public void setMaxTeamSize(int maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
    }

    public boolean isPotionsEnabled() {
        return potionsEnabled;
    }

    public void setPotionsEnabled(boolean potionsEnabled) {
        this.potionsEnabled = potionsEnabled;
    }

    public boolean isHardcoreMode() {
        return hardcoreMode;
    }

    public void setHardcoreMode(boolean hardcoreMode) {
        this.hardcoreMode = hardcoreMode;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public ArenaType getArenaType() {
        return arenaType;
    }

    public void setArenaType(ArenaType arenaType) {
        this.arenaType = arenaType;
    }

    public WeatherEffect getWeatherEffect() {
        return weatherEffect;
    }

    public void setWeatherEffect(WeatherEffect weatherEffect) {
        this.weatherEffect = weatherEffect;
    }

    public boolean isWeaponsEnabled() {
        return weaponsEnabled;
    }
    public void setWeaponsEnabled(boolean weaponsEnabled) {
        this.weaponsEnabled = weaponsEnabled;
    }

    public String getSelectedWeapon() {
        return selectedWeapon;
    }
    public void setSelectedWeapon(String selectedWeapon) {
        this.selectedWeapon = selectedWeapon;
    }

}
