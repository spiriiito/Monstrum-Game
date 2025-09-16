package monstrum.model;

import monstrum.exceptions.NotEnoughEnergyException;
import monstrum.game.RuleConfig;
import monstrum.model.equipment.*;
import monstrum.model.rules.WeatherEffect;

import java.util.Random;

public abstract class Monster {
    protected int health;
    protected int energy;
    protected int attackPower;
    protected String type;
    protected boolean specialAttackReady;
    protected int poisonTurnsRemaining = 0;
    protected int poisonDamagePerTurn = 5;
    protected int maxHealth;
    protected int maxEnergy;
    protected RuleConfig ruleConfig;
    protected Weapon weapon;
    protected SurvivalPerk survivalPerk;
    protected Random rng = new Random();
    protected int turnsSurvived = 0;
    protected int cooldownRemaining = 0;
    protected void applyWeatherEffectsEndTurn(RuleConfig config) {
        if (config.getWeatherEffect() == WeatherEffect.RAIN) {
            this.energy -=5;
            if (this.energy < 0) this.energy = 0;
            System.out.println("🌧️ Rain drains energy from " + this.type + "! (-5 Energy)");
        }

        this.turnsSurvived++;

        if (config.getWeatherEffect() == WeatherEffect.HEATWAVE && turnsSurvived % 2 == 0) {
            this.takeDamage(5);
            System.out.println("🔥 " + this.type + " is burned by the heatwave! (-3 HP)");
        }
    }

    // constructor

    public Monster(int health, int energy, int attackPower) {
        this.health = health;
        this.maxHealth = health;
        this.energy = energy;
        this.maxEnergy = energy;
        this.attackPower = attackPower;
        this.specialAttackReady = true;
    }

    // basic attack (same for all types of monsters)

    public void basicAttack(Monster target) {
        int damage = attackPower;

        if (ruleConfig.getWeatherEffect() == WeatherEffect.FOG && rng.nextInt(100) < 25) {
            System.out.println("🌫️ " + this.type + "'s attack missed due to thick fog!");
            return;
        }
        if (weapon != null) {
            damage = weapon.modifyDamage(attackPower);
            System.out.println(" Weapon effect: Attack modifies to deal " + damage + " damage!");
        }
        System.out.println(this.type + " used Basic Attack!");
        target.takeDamage(damage);

        if (ruleConfig.getWeatherEffect() == WeatherEffect.STORM && rng.nextInt(100) < 10) {
            this.health -= 5;
            System.out.println("⚡ Storm backfires on " + this.type + "! Takes 5 self-damage.");
        }
    }

    public abstract boolean specialAttack(Monster target) throws NotEnoughEnergyException;

    public void takeDamage(int damage){
        if (weapon instanceof Shield shield) {
            int block = shield.getBlockAmount();
            damage -= block;
            if (damage < 0) damage = 0;
            System.out.println("\uD83D\uDEE1\uFE0F Shield blocked " + block + " damage!");
        }
        this.health -= damage;
        if (this.health <= 0) {
            this.health = 0;
            System.out.println(this.type + " is dead!");
        }
    }

    // compile-time polymorphism
    public void takeDamage(int damage, String type) {
        if (type.equalsIgnoreCase("poison")) {
            System.out.println(this.type + " suffers poison damage!");
            this.health -= (damage + 5);
        } else if (type.equalsIgnoreCase("fire")) {
            System.out.println(this.type + " is scorched by fire!");
            this.health -= (damage + 3);
        } else {
            takeDamage(damage);
            return;
        }

        if (this.health <= 0) {
            this.health = 0;
            System.out.println(this.type + " is dead!");
        }
    }

    public boolean isAlive(){
        return this.health > 0;
    }

    // getter methods (encapsulation)
    public int getHealth() { return health; }
    public int getEnergy() { return energy; }
    public String getType() { return type; }

    // setter methods (encapsulation)
    public void setHealth(int health) {
        System.out.println("Setting health of " + this.type + " to " + health);
        this.health = Math.max(0, Math.min(health, maxHealth));
    }

    public void setEnergy(int energy) { this.energy = energy; }
    public void setType(String type) { this.type = type; }


    public void rechargeSpecialAttack() {
        this.specialAttackReady = true;
    }

    // Poison effect of Acid monster
    public void applyPoison(int turns, int damage) {
        this.poisonTurnsRemaining = turns;
        this.poisonDamagePerTurn = damage;
        System.out.println(this.type + " has been poisoned for " + turns + " turns!");
    }

    public void handlePoisonEffect() {
        if (poisonTurnsRemaining > 0){
            this.takeDamage(poisonDamagePerTurn);
            poisonTurnsRemaining--;
            System.out.println(this.type + " takes" + poisonDamagePerTurn + "poison damage! (" + poisonTurnsRemaining + "turn(s) left)");
        }
    }

    public abstract void endTurn();

    public abstract boolean isSpecialReady();

    public void heal(int amount) {
        this.health += amount;
        if (this.health > this.maxHealth) {
            this.health = this.maxHealth;
        }
    }

    public void restoreEnergy(int amount) {
        this.energy += amount;
        if (this.energy > this.maxEnergy) {
            this.energy = this.maxEnergy;
        }
    }
    public abstract int getSpecialCost();

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }
    public int getMaxEnergy(){
        return this.maxEnergy;
    }

    public void setSpecialBonusDamage(int bonus) {}
    public void setSpecialDamage (int damage) {}
    public void setLifeStealAmount(int amount) {}
    public void setPoisonDamage(int damage) {}
    public void setSpecialCost(int cost) {}
    public void equipWeapon(Weapon weapon) {
        this.weapon = weapon;
        System.out.println(this.getType() + " equipped " + weapon.getName() + "!");
    }

    public Weapon getWeapon() {
        return this.weapon;
    }

    public void runWeaponEffects() {
        if (weapon != null) {
            weapon.onEndTurn(this);
        }
    }

    public int getCooldownRemaining() {
        return cooldownRemaining;
    }

    public void reduceCooldown() {
        if (cooldownRemaining > 0) {
            cooldownRemaining--;
        }
    }

    public void setSurvivalPerk(SurvivalPerk perk) {
        this.survivalPerk = perk;
    }

    public SurvivalPerk getSurvivalPerk() {
        return survivalPerk;
    }

    public void setRuleConfig(RuleConfig ruleConfig) {
        this.ruleConfig = ruleConfig;
    }
}