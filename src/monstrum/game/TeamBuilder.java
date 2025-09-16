package monstrum.game;

import monstrum.game.Team;
import monstrum.model.Monster;
import monstrum.model.monsters.*;
import monstrum.model.Weapon;

import java.util.*;

public class TeamBuilder {

    public static Team buildRandomTeam(int size, RuleConfig ruleConfig) {
        Team team = new Team(size);
        List<Integer> indexes = Arrays.asList(1, 2, 3, 4, 5);
        Collections.shuffle(indexes);

        for (int i = 0; i < size; i++) {
            Monster m = createMonsterByChoice(indexes.get(i));
            m.setRuleConfig(ruleConfig);
            applyArenaEffects(m, ruleConfig);
            team.addMonster(m, size);
        }

        return team;
    }

    public static void assignWeaponToRandomMonster(Team team, Weapon weapon) {
        List<Monster> monsters = team.getMonsters();
        if (monsters.isEmpty()) return;
        int index = new Random().nextInt(monsters.size());
        monsters.get(index).equipWeapon(weapon);
    }

    private static Monster createMonsterByChoice(int choice) {
        return switch (choice) {
            case 1 -> new FireMonster();
            case 2 -> new WaterMonster();
            case 3 -> new ThunderMonster();
            case 4 -> new DarkMonster();
            case 5 -> new AcidMonster();
            default -> null;
        };
    }

    private static void applyArenaEffects(Monster m, RuleConfig config) {
        RuleConfig.ArenaType arena = config.getArenaType();

        if (arena == RuleConfig.ArenaType.VOLCANO && m instanceof FireMonster) {
            m.setHealth(110);
            m.setMaxHealth(110);
        } else if (arena == RuleConfig.ArenaType.OCEAN && m instanceof WaterMonster) {
            m.setSpecialBonusDamage(7);
        } else if (arena == RuleConfig.ArenaType.ZEUS_MOUNTAIN && m instanceof ThunderMonster) {
            m.setSpecialDamage(30);
            m.setSpecialCost(25);
        } else if (arena == RuleConfig.ArenaType.DARK_FOREST && m instanceof DarkMonster) {
            m.setLifeStealAmount(25);
        } else if (arena == RuleConfig.ArenaType.SWAMP && m instanceof AcidMonster) {
            m.setPoisonDamage(10);
        }
    }
}
