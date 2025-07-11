package monstrum.game;

import monstrum.exceptions.InvalidTargetException;
import monstrum.model.Monster;
import java.util.ArrayList;
import java.util.List;
public class Team {
    private List<Monster> monsters;
    private int currentMonsterIndex; // tells us which monster is currently active

    private int healingPotions = 2;
    private int energyPotions = 2;
    private boolean enablePotionUsage = false;
    private int maxSize = 3;

    // Constructor
    public Team() {
        this(3);
    }
    public Team(int maxSize) {
        this.maxSize = maxSize;
        monsters = new ArrayList<>();
        currentMonsterIndex = 0;
    }
    // Adds a monster to the team
    public void addMonster(Monster monster, int maxSize) {
        if (monsters.size() < maxSize) {
            monsters.add(monster);
        }
        else {
            System.out.println("Your team is full! You can only have " + maxSize + " monsters.");
        }
    }
    // Returns the current active monster
    public Monster getActiveMonster() {
        Monster current = monsters.get(currentMonsterIndex);
        if (current.isAlive()) {
            return current;
        }
        else {
            return switchToNextAliveMonster();
        }
    }
    // lets the player switch which monster they're using
    public void switchMonster(int index) {
        if (index >= 0 && index < monsters.size() && monsters.get(index).isAlive()) {
            currentMonsterIndex = index;
            System.out.println("Switched to " + monsters.get(index).getType());
        }
        else {
            System.out.println("Invalid switch. Monster might be dead or out of range.");
        }
    }

    // checks if any monster in the team is still alive
    public boolean isTeamAlive() {
        for (Monster m: monsters) {
            if (m.isAlive()) return true;
        }
        return false;
    }

    private Monster switchToNextAliveMonster(){
        for (int i = 0; i < monsters.size(); i++) {
            if (i != currentMonsterIndex && monsters.get(i).isAlive()) {
                currentMonsterIndex = i;
                return monsters.get(i);
            }
        }
        return null; // if no monster is alive
    }

    // each monster's current status
    public void showTeamStatus() {
        for (int i = 0; i < monsters.size(); i++) {
            Monster m = monsters.get(i);
            System.out.println("[" + (i + 1) + "]" + m.getType() +
                    " | HP: " + m.getHealth() +
                    " | Energy: " + m.getEnergy() +
                    " | Alive: " + m.isAlive());
        }
    }

    // healing potion
    public void useHealingPotion(Monster target) throws InvalidTargetException {
        if (!target.isAlive()) {
            throw new InvalidTargetException("⚠\uFE0F You cannot heal a dead monster!");
        }
        if (healingPotions > 0) {
            target.heal(50);
            healingPotions--;
            System.out.println(target.getType() + " used Healing Potion and healed for 50 HP!");
        } else {
            System.out.println("No Healing Potions left!");
        }
    }

    // energy potion
    public void useEnergyPotion(Monster target) throws InvalidTargetException {
        if (!target.isAlive()) {
            throw new InvalidTargetException("\uFE0F You cannot restore energy of a dead monster!");
        }
        if (energyPotions > 0) {
            target.restoreEnergy(50);
            energyPotions--;
            System.out.println(target.getType() + " used Energy Potion and regained 50 energy!");
        } else {
            System.out.println("No Energy Potions left!");
        }
    }

    public Monster getMonster(int index) {
        if (index >= 0 && index < monsters.size()) {
            return monsters.get(index);
        }
        return getActiveMonster();
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

    public boolean hasHealingPotions() { return healingPotions > 0; }
    public boolean hasEnergyPotions() { return energyPotions > 0; }

    public int getMonsterCount() {
        return monsters.size();
    }

    public void setEnablePotionUsage(boolean enabled) {
        this.enablePotionUsage = enabled;
    }

    public boolean isPotionUsageEnabled() {
        return this.enablePotionUsage;
    }
}
