package monstrum.model.equipment;

import monstrum.model.Weapon;
import monstrum.model.Monster;

public class Trident extends Weapon {

    public Trident() {
        super("Trident", 5);
    }

    @Override
    public int modifyDamage(int baseDamage) {
        return baseDamage + getBonusDamage(); // +5 damage
    }

    @Override
    public void onEndTurn(Monster owner) {
        // No passive effect
    }

    @Override
    public String getEffectDescription() {
        return "Water-aligned weapon. Adds +5 to basic attack.";
    }
}
