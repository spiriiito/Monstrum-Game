package monstrum.model.equipment;

import monstrum.model.Weapon;
import monstrum.model.Monster;

public class Shield extends Weapon {

    private boolean used = false;
    private final int blockAmount = 5;

    public Shield() {
        super("Shield", 0); // No attack bonus
    }

    public int getBlockAmount() {
        return blockAmount;
    }

    public boolean isUsed() {
        return used;
    }

    public void markUsed() {
        used = true;
    }

    @Override
    public int modifyDamage(int baseDamage) {
        return baseDamage; // no damage boost
    }

    @Override
    public void onEndTurn(Monster owner) {
        // No passive regeneration etc.
    }

    @Override
    public String getEffectDescription() {
        return "Blocks 10 damage once per battle.";
    }
}
