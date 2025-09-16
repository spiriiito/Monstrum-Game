package monstrum.model;

public abstract class Weapon {
    private final String name;
    private final int bonusDamage;

    public Weapon(String name, int bonusDamage) {
        this.name = name;
        this.bonusDamage = bonusDamage;
    }

    public String getName() {
        return name;
    }

    public int getBonusDamage() {
        return bonusDamage;
    }

    public int modifyDamage(int baseDamage) {
        return baseDamage;
    }

    public void onEndTurn(Monster owner) {
        // default = no effect
    }

    public abstract String getEffectDescription();
}