package monstrum.model.monsters;

// Dark Monster: Scary mage with a healing ability
/*
    Stats:
         HP 100
         Energy 100
         SA: Dark Magic + life steal (heals 15 HP after a successful SA)
         SA Damage: 35
         SA cost: 45
         Cooldown: 3 turns
         Basic attack: 10
 */

import monstrum.exceptions.NotEnoughEnergyException;
import monstrum.model.interfaces.Combatant;
import monstrum.model.Monster;
import monstrum.model.rules.WeatherEffect;

public class DarkMonster extends Monster implements Combatant {

    private int cooldownRemaining;
    private int lifeStealAmount = 15;

    public DarkMonster() {
        super(100, 100, 10);
        this.type = "DarkMonster";
        this.cooldownRemaining = 0;
    }

    @Override
    public boolean specialAttack(Monster target) throws NotEnoughEnergyException {
        if (cooldownRemaining > 0){
            System.out.println(type + "'s Dark Magic needs " + cooldownRemaining + " more turn(s) to recharge!");
            return false;
        }

        if (ruleConfig.getWeatherEffect() == WeatherEffect.MAGNETIC_FIELD && turnsSurvived % 3 == 0) {
            System.out.println("🧲 " + this.type + "'s special attack is disabled by magnetic interference!");
            return false;
        }

        if (this.energy >= 45) {
            System.out.println(type + " used Dark Magic on " + target.getType() + "!");
            target.takeDamage(35);
            this.energy -= 45;
            this.cooldownRemaining = 3;

            // Life steal (heals itself for 15 HP)
            this.health += lifeStealAmount;
            if (this.health > 100) {
                this.health = 100;
            }
            System.out.println(type + " absorbed life and healed for " + lifeStealAmount + "HP!");

            if (ruleConfig.getWeatherEffect() == WeatherEffect.STORM && rng.nextInt(100) < 10) {
                this.takeDamage(5);
                System.out.println("⚡ Storm backfires! " + this.type + " takes 5 self-damage.");
            }

            return true;
        }
        else {
            throw new NotEnoughEnergyException(type + " doesn't have enough energy to use Dark Magic!");
        }
    }

    @Override
    public int getSpecialCost() {
        return 45;
    }

    @Override
    public int getCooldownRemaining() {
        return cooldownRemaining;
    }

    public void endTurn() {
        if (cooldownRemaining > 0){
            cooldownRemaining--;
        }
        this.energy += 6;
        if (this.energy > 100) this.energy = 100;
        this.handlePoisonEffect();
        this.runWeaponEffects();
        applyWeatherEffectsEndTurn(ruleConfig);
    }

    public boolean isSpecialReady() {
        return cooldownRemaining == 0 && energy >= 45;
    }

    public void setLifeStealAmount(int amount) {
        this.lifeStealAmount = amount;
    }
}