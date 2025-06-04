package monstrum.model.monsters;

// Fire monster: burst attacker
/* Stats: HP 90
          Energy 100
          SA: fireball
          SD: 30
          SA cost: 40
          Cooldown: 2 turns
          Basic attack: 10
 */

import monstrum.exceptions.NotEnoughEnergyException;
import monstrum.game.RuleConfig;
import monstrum.model.interfaces.Combatant;
import monstrum.model.Monster;
import monstrum.model.rules.WeatherEffect;

public class FireMonster extends Monster implements Combatant {

    private int cooldownRemaining;

    public FireMonster(){
        super(90, 100, 10);
        this.type = "FireMonster";
        this.cooldownRemaining = 0; // SA ready at start
    }

    @Override
    public boolean specialAttack(Monster target) throws NotEnoughEnergyException {
        if (cooldownRemaining > 0) {
            System.out.println(type + "'s Fireball Attack needs " + cooldownRemaining + " more turn(s) to recharge!");
            return false;
        }

        if (ruleConfig.getWeatherEffect() == WeatherEffect.MAGNETIC_FIELD && turnsSurvived % 3 == 0) {
            System.out.println("🧲 " + this.type + "'s special attack is disabled by magnetic interference!");
            return false;
        }

        if (this.energy >= 40) {
            System.out.println(type + " used Fireball Attack on " + target.getType() + "!");
            target.takeDamage(30, "fire");
            this.energy -= 40;
            this.cooldownRemaining = 2;

            if (ruleConfig.getWeatherEffect() == WeatherEffect.STORM && rng.nextInt(100) < 10) {
                this.takeDamage(5);
                System.out.println("⚡ Storm backfires! " + this.type + " takes 5 self-damage.");
            }

            return true;
        }
        else {
            throw new NotEnoughEnergyException(type + " doesn't have enough energy to use Fireball Attack!");
        }
    }

    @Override
    public int getSpecialCost() {
        return 40;
    }

    // method called at the end of each turn to reduce cooldown
    public void endTurn(){
        if (cooldownRemaining > 0){
            cooldownRemaining--;
        }
        // Energy regeneration (temporary)
        this.energy += 5;
        if (this.energy > 100) this.energy = 100;
        // poison effect
        this.handlePoisonEffect();
        this.runWeaponEffects();
        applyWeatherEffectsEndTurn(ruleConfig);
    }

    // checks if special attack is ready

    public boolean isSpecialReady() {
        return cooldownRemaining == 0 && energy >= 40;
    }

    @Override
    protected void applyWeatherEffectsEndTurn(RuleConfig config) {
        if (config.getWeatherEffect() == WeatherEffect.RAIN) {
            this.energy -= 5;
            if (this.energy < 0) this.energy = 0;
            System.out.println("🌧️ Rain drains energy from " + this.type + "! (-5 Energy)");
        }
        this.turnsSurvived++;
    }
}

