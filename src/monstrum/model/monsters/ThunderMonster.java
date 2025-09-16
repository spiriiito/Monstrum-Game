package monstrum.model.monsters;

// Thunder Monster: Tanky and durable
/*
    Stats:
        HP 120
        Energy 90
        SA: Lightning Strike
        SA Damage: 28
        SA Cost: 40
        Cooldown: 2 turns
        Basic attack: 10
 */

import monstrum.exceptions.NotEnoughEnergyException;
import monstrum.model.interfaces.Combatant;
import monstrum.model.Monster;
import monstrum.model.rules.WeatherEffect;

public class ThunderMonster extends Monster implements Combatant {

    private int cooldownRemaining;
    private int specialDamage = 28;
    private int specialCost = 40;

    public ThunderMonster() {
        super(120, 90, 10);
        this.type = "ThunderMonster";
        this.cooldownRemaining = 0;
    }

    @Override
    public boolean specialAttack(Monster target) throws NotEnoughEnergyException {
        if (cooldownRemaining > 0) {
            System.out.println(type + "'s Lightning Strike needs " + cooldownRemaining + " more turn(s) to recharge!");
            return false;
        }

        if (ruleConfig.getWeatherEffect() == WeatherEffect.MAGNETIC_FIELD && turnsSurvived % 3 == 0) {
            System.out.println("🧲 " + this.type + "'s special attack is disabled by magnetic interference!");
            return false;
        }

        if (this.energy >= specialCost) {
            System.out.println(type + " used Lightning Strike on " + target.getType() + "!");
            target.takeDamage(specialDamage);
            this.energy -= specialCost;
            this.cooldownRemaining = 2;
            return true;
        }
        else {
            throw new NotEnoughEnergyException(type + " doesn't have enough energy to use Lightning Strike!");
        }
    }

    @Override
    public int getSpecialCost() {
        return specialCost;
    }

    @Override
    public void endTurn() {
        if (cooldownRemaining > 0){
            cooldownRemaining--;
        }
        this.energy += 4;
        if (this.energy > 90) this.energy = 90;
        this.handlePoisonEffect();
        this.runWeaponEffects();
        applyWeatherEffectsEndTurn(ruleConfig);
    }

    @Override
    public boolean isSpecialReady() {
        return cooldownRemaining == 0 && energy >= specialCost;
    }

    @Override
    public boolean isAlive() {
        return this.health > 0;
    }

    @Override
    public void basicAttack(Monster target) {
        super.basicAttack(target);
    }

    @Override
    public int getCooldownRemaining() {
        return cooldownRemaining;
    }

    public void setSpecialDamage(int damage) {
        this.specialDamage = damage;
    }

    public void setSpecialCost(int cost) {
        this.specialCost = cost;
    }
}