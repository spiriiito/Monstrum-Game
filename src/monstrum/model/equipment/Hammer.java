package monstrum.model.equipment;

import monstrum.model.Weapon;
import monstrum.model.Monster;

public class Hammer extends Weapon {

    public Hammer() {
        super("Hammer", 8);
    }

    @Override
    public int modifyDamage(int baseDamage) {
        return baseDamage + getBonusDamage(); // +8 damage
    }

    @Override
    public void onEndTurn(Monster owner) {
        owner.setEnergy(owner.getEnergy() - 2);
        System.out.println(owner.getType() + "'s Hammer drains -2 energy due to its weight!");
    }

    @Override
    public String getEffectDescription() {
        return "Adds +8 damage, but drains -2 energy at the end of each turn.";
    }
}
