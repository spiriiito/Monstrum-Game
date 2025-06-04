package monstrum.model;

public abstract class Weapon {
    protected final String name;
    protected final int bonusDamage;

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

    // Modify damage dealt by a monster
    public int modifyDamage(int baseDamage) {
        return baseDamage;
    }

    public void onEndTurn(Monster owner) {
        // default = no effect
    }

    public abstract String getEffectDescription();
}