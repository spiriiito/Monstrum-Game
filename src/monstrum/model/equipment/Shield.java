package monstrum.model.equipment;

import monstrum.model.Weapon;
import monstrum.model.Monster;

public class Shield extends Weapon {

    private boolean used = false;
    private final int blockAmount = 5;

    public Shield() {
        super("Shield", 0);
    }

    public int getBlockAmount() {
        return blockAmount;
    }

    @Override
    public int modifyDamage(int baseDamage) {
        return baseDamage;
    }

    @Override
    public void onEndTurn(Monster owner) {

    }

    @Override
    public String getEffectDescription() {
        return "Blocks 10 damage once per battle.";
    }
}
