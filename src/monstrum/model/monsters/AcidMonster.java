package monstrum.model.monsters;

// Acid Monster: Sneaky poison debuffer
/*
    Stats:
        HP 105
        Energy 110
        SA: Acid Arrows (+ poison damage over time)
        SA Damage: 20 + poison
        SA Cost: 30
        Cooldown: 2 turns
        Basic attack: 10
        Poison effect: the target takes 5 extra damage at the end of each turn for 2 turns
 */

import monstrum.exceptions.NotEnoughEnergyException;
import monstrum.model.interfaces.Combatant;
import monstrum.model.Monster;
import monstrum.model.rules.WeatherEffect;

public class AcidMonster extends Monster implements Combatant {

    private int cooldownRemaining;
    private int poisonDamage = 5;

    public AcidMonster() {
        super(105, 110, 10);
        this.type = "AcidMonster";
        this.cooldownRemaining = 0;
    }

    @Override
    public boolean specialAttack(Monster target) throws NotEnoughEnergyException {
        if (cooldownRemaining > 0) {
            System.out.println(type + "'s Acid Arrows need " + cooldownRemaining + " more turn(s) to recharge!");
            return false;
        }

        if (ruleConfig.getWeatherEffect() == WeatherEffect.MAGNETIC_FIELD && turnsSurvived % 3 == 0) {
            System.out.println("🧲 " + this.type + "'s special attack is disabled by magnetic interference!");
            return false;
        }

        if (this.energy >= 30) {
            System.out.println(type + " fired Acid Arrows at " + target.getType() + "!");
            target.takeDamage(20);
            target.applyPoison(2, poisonDamage);
            this.energy -= 30;
            this.cooldownRemaining = 3;

            if (ruleConfig.getWeatherEffect() == WeatherEffect.STORM && rng.nextInt(100) < 10) {
                this.takeDamage(5);
                System.out.println("⚡ Storm backfires! " + this.type + " takes 5 self-damage.");
            }

            return true;
        }
        else {
            throw new NotEnoughEnergyException(type + " doesn't have enough energy to use Acid Arrows!");
        }
    }

    @Override
    public int getSpecialCost() {
        return 30;
    }

    @Override
    public int getCooldownRemaining() {
        return cooldownRemaining;
    }

    public void endTurn() {
        if (cooldownRemaining > 0){
            cooldownRemaining--;
        }
        this.energy += 8;
        if (this.energy > 110) this.energy = 110;
        this.handlePoisonEffect();
        this.runWeaponEffects();
        applyWeatherEffectsEndTurn(ruleConfig);
    }

    public boolean isSpecialReady() {
        return cooldownRemaining == 0 && energy >= 30;
    }

    public void setPoisonDamage(int damage) {
        this.poisonDamage = damage;
    }
}