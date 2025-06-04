package monstrum.model.equipment;

import monstrum.model.Weapon;

import java.util.Random;

public class WeaponFactory {

    private static final Random rng = new Random();

    public static Weapon getRandomWeapon() {
        int roll = rng.nextInt(3);
        return switch (roll) {
            case 0 -> new Trident();
            case 1 -> new Hammer();
            case 2 -> new Shield();
            default -> new Trident();
        };
    }

    public static Weapon getWeaponByName (String name) {
        return switch (name.toLowerCase()) {
            case "trident" -> new Trident();
            case "hammer" -> new Hammer();
            case "shield" -> new Shield();
            default -> null;
        };
    }
}
