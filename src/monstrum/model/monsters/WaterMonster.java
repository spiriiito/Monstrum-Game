package monstrum.model.monsters;

// Water Monster: balanced and reliable fighter, fast SA cooldown
/*
   Stats:
        HP 110
        Energy 120
        SA: Water blast
        SA Damage: 25
        SA Cost: 35
        Cooldown: 1 turn
        Basic attack: 10
 */

import monstrum.exceptions.NotEnoughEnergyException;
import monstrum.model.interfaces.Combatant;
import monstrum.model.Monster;
import monstrum.game.RuleConfig;
import monstrum.model.rules.WeatherEffect;

public class WaterMonster extends Monster implements Combatant {

    private int cooldownRemaining;
    private int specialBonus = 0;

    public WaterMonster(){
        super(110, 120, 10);
        this.type = "WaterMonster";
        this.cooldownRemaining = 0;
    }

    @Override
    public boolean specialAttack(Monster target) throws NotEnoughEnergyException {
        if (cooldownRemaining > 0){
            System.out.println(type + "'s Water Blast needs " + cooldownRemaining + " more turn(s) to recharge!");
            return false;
        }

        if (ruleConfig.getWeatherEffect() == WeatherEffect.MAGNETIC_FIELD && turnsSurvived % 3 == 0) {
            System.out.println("🧲 " + this.type + "'s special attack is disabled by magnetic interference!");
            return false;
        }

        if (this.energy >= 35) {
            int totalDamage = 25 + specialBonus;
            System.out.println(type + " used Water Blast on " + target.getType() + "for" + totalDamage + "damage!");
            target.takeDamage(totalDamage);
            this.energy -= 35;
            this.cooldownRemaining = 2;

            if (ruleConfig.getWeatherEffect() == WeatherEffect.STORM && rng.nextInt(100) < 10) {
                this.takeDamage(5);
                System.out.println("⚡ Storm backfires! " + this.type + " takes 5 self-damage.");
            }

            return true;
        }
        else{
            throw new NotEnoughEnergyException(type + " doesn't have enough energy to use Water Blast!");
        }

    }

    @Override
    public int getSpecialCost() {
        return 35;
    }

    @Override
    public int getCooldownRemaining() {
        return cooldownRemaining;
    }

    public void endTurn() {
        if (cooldownRemaining > 0) {
            cooldownRemaining--;
        }
        this.energy += 10;
        if (this.energy > 120) this.energy = 120;

        this.handlePoisonEffect();
        this.runWeaponEffects();

        applyWeatherEffectsEndTurn(ruleConfig);
    }

    public boolean isSpecialReady() {
        return cooldownRemaining == 0 && energy >= 35;
    }

    public void setSpecialBonusDamage(int bonus) {
        this.specialBonus = bonus;
    }

    @Override
    protected void applyWeatherEffectsEndTurn(RuleConfig config) {
        this.turnsSurvived++;

        if (config.getWeatherEffect() == WeatherEffect.HEATWAVE && turnsSurvived % 2 == 0) {
            this.takeDamage(3);
            System.out.println("🔥 " + this.type + " is burned by the heatwave! (-3 HP)");
        }
    }

}