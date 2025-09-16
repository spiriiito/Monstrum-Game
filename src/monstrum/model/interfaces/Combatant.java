package monstrum.model.interfaces;

import monstrum.exceptions.NotEnoughEnergyException;
import monstrum.model.Monster;

public interface Combatant {
    void basicAttack(Monster target);
    boolean specialAttack(Monster target) throws NotEnoughEnergyException;
    void endTurn();
    boolean isSpecialReady();
    boolean isAlive();
}