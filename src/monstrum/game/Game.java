package monstrum.game;

import monstrum.exceptions.InvalidTargetException;
import monstrum.exceptions.NotEnoughEnergyException;
import monstrum.model.*;
import monstrum.model.monsters.*;
import monstrum.model.equipment.*;
import monstrum.model.rules.WeatherEffect;

import java.util.*;

public class Game {

    // attributes
    private Team playerTeam;
    private Team enemyTeam;
    private Scanner scanner;
    private RuleConfig ruleConfig;
    private int waitCooldown = 0;
    private int currentWave = 1;
    private SurvivalPerk selectedSurvivalPerk;


    // constructor
    public Game(RuleConfig ruleConfig) {
        this.playerTeam = new Team();
        this.enemyTeam = new Team();
        this.scanner = new Scanner(System.in);
        this.ruleConfig = ruleConfig;
    }

    public Game(RuleConfig config, Team playerTeam, Team enemyTeam) {
        this.ruleConfig = config;
        this.playerTeam = playerTeam;
        this.enemyTeam = enemyTeam;
    }

    public void startGame() {
        System.out.println("\uD83D\uDC7E Welcome to Monstrum! \uD83D\uDC7E");
        System.out.println("\uD83C\uDFAE Selected Game Mode: " + ruleConfig.getGameMode());
        if (ruleConfig.isHardcoreMode()) {
            System.out.println("☠\uFE0F HARDCORE MODE ENABLED: Potions are disabled and enemies deal extra damage!");
            WeatherEffect weather = WeatherEffect.getRandomWeather();
            ruleConfig.setWeatherEffect(weather);
            System.out.println("⛈️ Weather condition: " + weather + " — " + weather.getDescription());
        }

        if (ruleConfig.getGameMode() == RuleConfig.GameMode.RANDOM) {
            System.out.println("\uD83C\uDFB2 RANDOM MODE ACTIVATED!");

            RuleConfig.ArenaType randomArena = RuleConfig.ArenaType.getRandomArena();
            ruleConfig.setArenaType(randomArena);
            System.out.println("🌍 Arena selected: " + randomArena);

            WeatherEffect randomWeather = WeatherEffect.getRandomWeather();
            ruleConfig.setWeatherEffect(randomWeather);
            System.out.println("⛈️ Weather condition: " + randomWeather + " — " + randomWeather.getDescription());

            Weapon randomWeapon = WeaponFactory.getRandomWeapon();
            System.out.println("⚔\uFE0F Random weapon: " + randomWeapon.getName());

            playerTeam = buildRandomTeam(ruleConfig.getMaxTeamSize());
            assignWeaponToRandomMonster(playerTeam, randomWeapon);

            buildEnemyTeam();
            runGameVsAI();
            return;
        }

        Weapon chosenWeapon = weaponSelection();
        playerSelectTeam();

        System.out.println("\nChoose a monster to equip the weapon: ");
        playerTeam.showTeamStatus();
        int index = scanner.nextInt() - 1;
        if (index >= 0 && index < playerTeam.getMonsterCount()) {
            Monster selected = playerTeam.getMonster(index);
            selected.equipWeapon(chosenWeapon);
            System.out.println(" Weapon equipped to " + selected.getType());
        } else {
            System.out.println("Invalid index. No weapon equipped.");
        }

        if (ruleConfig.getGameMode() == RuleConfig.GameMode.PLAYER_VS_AI) {
            buildEnemyTeam();
            runGameVsAI();
        } else if (ruleConfig.getGameMode() == RuleConfig.GameMode.PLAYER_VS_PLAYER) {
            player2SelectTeam();

            // Player 1 selects weapon
            System.out.println("\n🎮 PLAYER 1: Choose a weapon:");
            Weapon p1Weapon = weaponSelection();

            System.out.println("Choose a monster to equip the weapon:");
            playerTeam.showTeamStatus();
            int p1Index = scanner.nextInt() - 1;
            if (p1Index >= 0 && p1Index < playerTeam.getMonsterCount()) {
                playerTeam.getMonster(p1Index).equipWeapon(p1Weapon);
                System.out.println("🛠 Weapon equipped to " + playerTeam.getMonster(p1Index).getType());
            } else {
                System.out.println("Invalid index. No weapon equipped.");
            }

            //  Player 2 selects weapon
            System.out.println("\n🎮 PLAYER 2: Choose a weapon:");
            Weapon p2Weapon = weaponSelection();

            System.out.println("Choose a monster to equip the weapon:");
            enemyTeam.showTeamStatus();
            int p2Index = scanner.nextInt() - 1;
            if (p2Index >= 0 && p2Index < enemyTeam.getMonsterCount()) {
                enemyTeam.getMonster(p2Index).equipWeapon(p2Weapon);
                System.out.println("🛠 Weapon equipped to " + enemyTeam.getMonster(p2Index).getType());
            } else {
                System.out.println("Invalid index. No weapon equipped.");
            }

            runGameVsPlayer();
        } else {
            System.out.println("This mode is not yet implemented.");
        }
    }


    private void runGameVsAI() {

        while (playerTeam.isTeamAlive() && enemyTeam.isTeamAlive()) {
            System.out.println("\n\uD83D\uDC41\uFE0F\u200D\uD83D\uDDE8\uFE0F Your Team:");
            playerTeam.showTeamStatus();

            Monster player = playerTeam.getActiveMonster();
            Monster enemy = enemyTeam.getActiveMonster();

            System.out.println("\nYour active monster: " + player.getType());
            System.out.println("Enemy's active monster: " + enemy.getType());

            boolean actionDone = false;
            while (!actionDone) {
                System.out.println("\n\uD83D\uDE08 Choose your move: ");
                System.out.println("[1] Basic Attack");
                System.out.println("[2] Special Attack");
                System.out.println("[3] Switch Monster");
                if (ruleConfig.isPotionsEnabled() && !ruleConfig.isHardcoreMode()) {
                    System.out.println("[4] Use Potion");
                }

                int choice = -1;
                int maxOption = (ruleConfig.isPotionsEnabled() && !ruleConfig.isHardcoreMode()) ? 4 : 3;

                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    if (choice < 1 || choice > maxOption) {
                        System.out.println("\uD83D\uDC7F Invalid number! Choose an option (1-" + maxOption + ").");
                        continue;
                    }
                } else {
                    System.out.println("\uD83D\uDC7F Invalid input! Please enter a number.");
                    scanner.next();
                    continue;
                }

                switch (choice) {
                    case 1 -> {
                        player.basicAttack(enemy);
                        actionDone = true;
                    }
                    case 2 -> {
                        try {
                            if (player.specialAttack(enemy)) {
                                actionDone = true;
                            }
                        } catch (NotEnoughEnergyException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    case 3 -> {
                        playerTeam.showTeamStatus();
                        System.out.println("Enter index of the monster to switch to: ");
                        int index = scanner.nextInt() - 1;
                        playerTeam.switchMonster(index);
                        player = playerTeam.getActiveMonster();
                    }
                    case 4 -> {
                        if (!ruleConfig.isPotionsEnabled() || ruleConfig.isHardcoreMode()) {
                            System.out.println("Potions are disabled in this mode!");
                            break;
                        }

                        System.out.println("\nChoose a potion to apply: ");
                        System.out.println("[1] Healing Potion");
                        System.out.println("[2] Energy Potion");
                        int potionChoice = scanner.nextInt();

                        System.out.println("Select the monster to apply it to: ");
                        playerTeam.showTeamStatus();
                        int selectedIndex = scanner.nextInt() - 1;
                        Monster selected = playerTeam.getMonster(selectedIndex);

                        if (potionChoice == 1) {
                            try {
                                playerTeam.useHealingPotion(selected);
                            } catch (InvalidTargetException e) {
                                System.out.println(e.getMessage());
                            }
                        } else if (potionChoice == 2) {
                            try {
                                playerTeam.useEnergyPotion(selected);
                            } catch (InvalidTargetException e) {
                                System.out.println(e.getMessage());
                            }
                        } else {
                            System.out.println("Invalid potion choice, try again");
                        }

                        actionDone = true;

                    }
                }
            }

            player.endTurn();

            if (!enemyTeam.isTeamAlive()) break;

            // AI for enemy
            System.out.println("\n\uD83D\uDC79 Enemy's turn...");
            if (!ruleConfig.isHardcoreMode()) {
                enemyUsePotions();
            }

            if (enemy.isSpecialReady()) {
                boolean usedSpecial = false;
                try {
                    usedSpecial = enemy.specialAttack(player);
                } catch (NotEnoughEnergyException e) {
                    System.out.println(e.getMessage());
                }
                applyHardcoreEnemyBonus(enemy, player, usedSpecial);
            } else {
                enemy.basicAttack(player);
                applyHardcoreEnemyBonus(enemy, player, false);
            }

            enemy.endTurn();

            if (!player.isAlive()) {
                System.out.println(player.getType() + " is dead! \uD83D\uDC80");
                playerTeam.getActiveMonster();
            }

            if (!enemy.isAlive()) {
                System.out.println(enemy.getType() + " is dead! \uD83D\uDC80");
                enemyTeam.getActiveMonster();
            }
        }

        if (playerTeam.isTeamAlive()) {
            System.out.println("\n\uD83C\uDFC6 YOU WIN! Champion \uD83C\uDF89");
        } else {
            System.out.println("\n☠\uFE0F You lost...");
        }
    }

    private void runGameVsPlayer() {
        while (playerTeam.isTeamAlive() && enemyTeam.isTeamAlive()) {

            // Player 1's turn
            System.out.println("\n\uD83C\uDFAE Player 1's Turn");
            System.out.println("\n\uD83D\uDC41\uFE0F\u200D\uD83D\uDDE8\uFE0F Your Team:");
            playerTeam.showTeamStatus();

            Monster player = playerTeam.getActiveMonster();
            Monster enemy = enemyTeam.getActiveMonster();

            System.out.println("\nYour active monster: " + player.getType());
            System.out.println("Enemy's active monster: " + enemy.getType());

            boolean actionDone = false;
            while (!actionDone) {
                System.out.println("\n😈 Choose your move: ");
                System.out.println("[1] Basic Attack");
                System.out.println("[2] Special Attack");
                System.out.println("[3] Switch Monster");
                System.out.println("[4] Use Potion");

                int choice = -1;

                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    if (choice < 1 || choice > 4) {
                        System.out.println("\uD83D\uDC7F Invalid number! Choose an option (1-4).");
                        continue;
                    }
                } else {
                    System.out.println("\uD83D\uDC7F Invalid input! Please enter a number (1-4).");
                    scanner.next();
                    continue;
                }

                switch (choice) {
                    case 1:
                        player.basicAttack(enemy);
                        actionDone = true;
                        break;
                    case 2:
                        try {
                            if (player.specialAttack(enemy)) {
                                actionDone = true;
                            }
                        } catch (NotEnoughEnergyException e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case 3:
                        playerTeam.showTeamStatus();
                        System.out.println("Enter index of the monster to switch to: ");
                        int index = scanner.nextInt() - 1;
                        playerTeam.switchMonster(index);
                        player = playerTeam.getActiveMonster();
                        break;
                    case 4:
                        System.out.println("\nChoose a potion to apply: ");
                        System.out.println("[1] Healing Potion");
                        System.out.println("[2] Energy Potion");
                        int potionChoice = scanner.nextInt();

                        System.out.println("Select the monster to apply it to: ");
                        playerTeam.showTeamStatus();
                        int selectedIndex = scanner.nextInt() - 1;
                        Monster selected = playerTeam.getMonster(selectedIndex);

                        if (potionChoice == 1) {
                            try {
                                playerTeam.useHealingPotion(selected);
                            } catch (InvalidTargetException e) {
                                System.out.println(e.getMessage());
                            }
                        } else if (potionChoice == 2) {
                            try {
                                playerTeam.useEnergyPotion(selected);
                            } catch (InvalidTargetException e) {
                                System.out.println(e.getMessage());
                            }
                        } else {
                            System.out.println("Invalid potion choice, try again");
                        }
                        break;
                }
            }

            player.endTurn();
            if (!enemyTeam.isTeamAlive()) break;

            // Player 2's turn
            System.out.println("\n\uD83C\uDFAE Player 2's Turn");
            System.out.println("\n\uD83D\uDC41\uFE0F\u200D\uD83D\uDDE8\uFE0F Your Team:");
            enemyTeam.showTeamStatus();

            player = playerTeam.getActiveMonster();
            enemy = enemyTeam.getActiveMonster();

            System.out.println("\nYour active monster: " + enemy.getType());
            System.out.println("Enemy's active monster: " + player.getType());

            actionDone = false;
            while (!actionDone) {
                System.out.println("\n😈 Choose your move: ");
                System.out.println("[1] Basic Attack");
                System.out.println("[2] Special Attack");
                System.out.println("[3] Switch Monster");
                System.out.println("[4] Use Potion");

                int choice = -1;

                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    if (choice < 1 || choice > 4) {
                        System.out.println("\uD83D\uDC7F Invalid number! Choose an option (1-4).");
                        continue;
                    }
                } else {
                    System.out.println("\uD83D\uDC7F Invalid input! Please enter a number (1-4).");
                    scanner.next();
                    continue;
                }

                switch (choice) {
                    case 1:
                        enemy.basicAttack(player);
                        actionDone = true;
                        break;
                    case 2:
                        try {
                            if (enemy.specialAttack(player)) {
                                actionDone = true;
                            }
                        } catch (NotEnoughEnergyException e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case 3:
                        enemyTeam.showTeamStatus();
                        System.out.println("Enter index of the monster to switch to: ");
                        int index = scanner.nextInt() - 1;
                        enemyTeam.switchMonster(index);
                        enemy = enemyTeam.getActiveMonster();
                        break;
                    case 4:
                        System.out.println("\nChoose a potion to apply: ");
                        System.out.println("[1] Healing Potion");
                        System.out.println("[2] Energy Potion");
                        int potionChoice = scanner.nextInt();

                        System.out.println("Select the monster to apply it to: ");
                        enemyTeam.showTeamStatus();
                        int selectedIndex = scanner.nextInt() - 1;
                        Monster selected = enemyTeam.getMonster(selectedIndex);

                        if (potionChoice == 1) {
                            try {
                                enemyTeam.useHealingPotion(selected);
                            } catch (InvalidTargetException e) {
                                System.out.println(e.getMessage());
                            }
                        } else if (potionChoice == 2) {
                            try {
                                enemyTeam.useEnergyPotion(selected);
                            } catch (InvalidTargetException e) {
                                System.out.println(e.getMessage());
                            }
                        } else {
                            System.out.println("Invalid potion choice, try again");
                        }
                        break;
                }
            }

            enemy.endTurn();

            // Monster death auto-switching
            if (!player.isAlive()) {
                System.out.println(player.getType() + " is dead! \uD83D\uDC80");
                playerTeam.getActiveMonster();
            }

            if (!enemy.isAlive()) {
                System.out.println(enemy.getType() + " is dead! \uD83D\uDC80");
                enemyTeam.getActiveMonster();
            }
        }

        if (playerTeam.isTeamAlive()) {
            System.out.println("\n🏆 Player 1 Wins!");
        } else {
            System.out.println("\n🏆 Player 2 Wins!");
        }
    }

    private void playerSelectTeam() {
        Scanner input = new Scanner(System.in);
        Set<Integer> selectedIndexes = new HashSet<>();

        int maxSize = ruleConfig.getMaxTeamSize();
        System.out.println("\uD83E\uDDE0 Choose " + maxSize + " different monsters for your team: ");

        while (selectedIndexes.size() < maxSize) {
            printAvailableMonsters();
            System.out.println("Pick monster #" + (selectedIndexes.size() + 1) + ": ");

            if (input.hasNextInt()) {
                int choice = input.nextInt();

                if (choice < 1 || choice > 5) {
                    System.out.println("\uD83D\uDC7F Invalid number. Please choose between 1 and 5.");
                } else if (selectedIndexes.contains(choice)){
                    System.out.println("⚠\uFE0F You already picked this monster. Choose a different one");
                } else {
                    selectedIndexes.add(choice);
                    Monster chosen = createMonsterByChoice(choice);
                    chosen.setRuleConfig(ruleConfig);
                    playerTeam.addMonster(chosen, maxSize);
                    System.out.println("✔\uFE0F" + chosen.getType() + " added to your team!");
                }
            } else {
                System.out.println("❌ Invalid input. Please enter a number.");
                input.next();
            }
        }
    }

    private void player2SelectTeam() {
        Scanner input = new Scanner(System.in);
        Set<Integer> selectedIndexes = new HashSet<>();

        int maxSize = ruleConfig.getMaxTeamSize();
        System.out.println("\uD83E\uDDE0 Player 2: Choose " + maxSize + " different monsters for your team: ");

        while (selectedIndexes.size() < maxSize) {
            printAvailableMonsters();
            System.out.println("Player 2: Pick monster #" + (selectedIndexes.size() + 1) + ": ");

            if (input.hasNextInt()) {
                int choice = input.nextInt();

                if (choice < 1 || choice > 5) {
                    System.out.println("\uD83D\uDC7F Invalid number. Please choose between 1 and 5.");
                } else if (selectedIndexes.contains(choice)){
                    System.out.println("⚠\uFE0F You already picked this monster. Choose a different one");
                } else {
                    selectedIndexes.add(choice);
                    Monster chosen = createMonsterByChoice(choice);
                    chosen.setRuleConfig(ruleConfig);
                    enemyTeam.addMonster(chosen, maxSize);
                    System.out.println("✔\uFE0F" + chosen.getType() + " added to your team!");
                }
            } else {
                System.out.println("❌ Invalid input. Please enter a number.");
                input.next();
            }
        }
    }


    private void printAvailableMonsters() {
        System.out.println("\n\uD83D\uDCDC Available Monsters: ");

        System.out.println("[1] Fire Monster \uD83D\uDD25");
        System.out.println("   - Role: Offensive, burst attacker");
        System.out.println("   - Special: Fireball - 30 DMG + fire damage, 2-turn cooldown\n");

        System.out.println("[2] Water Monster \uD83D\uDCA7");
        System.out.println("   - Role: Balanced and reliable fighter");
        System.out.println("   - Special: Water Blast - 25 DMG, fast cooldown\n");

        System.out.println("[3] Thunder Monster ⚡\uFE0F");
        System.out.println("   - Role: Tank, very durable");
        System.out.println("   - Special: Lightning Strike - 28 DMG, high energy cost\n");

        System.out.println("[4] Dark Monster \uD83E\uDD87");
        System.out.println("   - Role: Scary mage with a healing ability");
        System.out.println("   - Special: Dark Magic - 35 DMG + life steal (heals 15 HP)\n");

        System.out.println("[5] Acid Monster \uD83E\uDDEA");
        System.out.println("   - Role: Sneaky poison debuffer");
        System.out.println("   - Special: Acid Arrows - 20 DMG + poison effect\n");
    }

    private Monster createMonsterByChoice(int choice) {
        Monster monster = switch (choice) {
            case 1 -> new FireMonster();
            case 2 -> new WaterMonster();
            case 3 -> new ThunderMonster();
            case 4 -> new DarkMonster();
            case 5 -> new AcidMonster();
            default -> null;
        };
        applyArenaEffects(monster);
        return monster;
    }

    private void applyArenaEffects(Monster m) {
        RuleConfig.ArenaType arena = ruleConfig.getArenaType();

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

    private void buildEnemyTeam() {
        List<Integer> available = Arrays.asList(1, 2, 3, 4, 5);
        Collections.shuffle(available);

        int teamSize = ruleConfig.getMaxTeamSize();
        for (int i = 0; i < teamSize; i++) {
            int choice = available.get(i);
            Monster enemy = createMonsterByChoice(choice);
            enemy.setRuleConfig(ruleConfig);
            enemyTeam.addMonster(enemy, teamSize);
        }

        System.out.println("\n\uD83D\uDC79 Enemy's team has been assembled!");
    }

    public String enemyUsePotions() {
        Monster enemy = enemyTeam.getActiveMonster();

        if (enemy.getHealth() < 25 && enemyTeam.hasHealingPotions()) {
            try {
                enemyTeam.useHealingPotion(enemy);
                return "\uD83C\uDF7D️ Enemy used a healing potion on " + enemy.getType() + "!";
            } catch (InvalidTargetException e) {
                return e.getMessage();
            }
        }

        if (!enemy.isSpecialReady() && enemy.getEnergy() < enemy.getSpecialCost() && enemyTeam.hasEnergyPotions()) {
            try {
                enemyTeam.useEnergyPotion(enemy);
                return "\uD83D\uDD04 Enemy used an energy potion on " + enemy.getType() + "!";
            } catch (InvalidTargetException e) {
                return e.getMessage();
            }
        }

        return null;
    }


    private Weapon weaponSelection() {
        System.out.println("\n🗡️ Choose a weapon for your team:");
        System.out.println("[1] Trident - +5 damage");
        System.out.println("[2] Hammer - +8 damage, -2 energy/turn");
        System.out.println("[3] Shield - Block 5 damage on every hit");

        int choice = 0;
        while (choice < 1 || choice > 3) {
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice < 1 || choice > 3) {
                    System.out.println("Invalid choice. Choose between 1-3");
                }
            } else {
                System.out.println("Invalid input.");
                scanner.next();
            }
        }
        return switch (choice) {
            case 1 -> new Trident();
            case 2 -> new Hammer();
            case 3 -> new Shield();
            default -> null;
        };
    }

    public void playerSelectSurvivor() {
        System.out.println("\uD83E\uDDE0 Choose 1 monster to survive the endless waves: ");
        printAvailableMonsters();

        int choice = -1;
        while (choice < 1 || choice > 5) {
            System.out.println("Pick your monster: ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice < 1 || choice > 5) {
                    System.out.println("Invalid number. Choose between 1-5");
                }
            } else {
                System.out.println("Invalid input. Enter a number");
                scanner.next();
            }
        }
        Monster survivor = createMonsterByChoice(choice);
        survivor.setRuleConfig(ruleConfig);
        System.out.println(survivor.getType() + " selected!");

        // +10% HP and Energy
        int bonusHP = (int)(survivor.getHealth() * 0.1);
        int bonusEnergy = (int)(survivor.getEnergy() * 0.1);
        survivor.setHealth(survivor.getHealth() + bonusHP);
        survivor.setEnergy(survivor.getEnergy() + bonusEnergy);

        playerTeam.addMonster(survivor, 1);

        Weapon chosenWeapon = weaponSelection();
        survivor.equipWeapon(chosenWeapon);

        selectedSurvivalPerk = chooseSurvivalPerk();
        survivor.setSurvivalPerk(selectedSurvivalPerk);

        System.out.println("\uD83C\uDFAF Perk selected: " + selectedSurvivalPerk);
        System.out.println("\uD83D\uDEE0\uFE0F Weapon equipped: " + chosenWeapon.getName());
    }

    public SurvivalPerk getSurvivalPerk() {
        return selectedSurvivalPerk;
    }

    public SurvivalPerk chooseSurvivalPerk() {
        System.out.println("\n🧠 Choose a Survival Perk:");

        System.out.println("[1] Adrenaline Rush     → +5 Energy per turn");
        System.out.println("[2] Iron Will           → -5 damage from attacks");
        System.out.println("[3] Vampiric Aura       → Heal 5 HP on basic attack");
        System.out.println("[4] Phantom Reflex      → 20% dodge vs basic attacks");
        System.out.println("[5] Arcane Mastery      → Special attack costs 10 less energy");

        int choice = -1;
        while (choice < 1 || choice > 5) {
            System.out.println("Select one perk (1-5): ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            } else {
                scanner.next();
            }
        }

        return switch (choice) {
            case 1 -> SurvivalPerk.ADRENALINE_RUSH;
            case 2 -> SurvivalPerk.IRON_WILL;
            case 3 -> SurvivalPerk.VAMPIRIC_AURA;
            case 4 -> SurvivalPerk.PHANTOM_REFLEX;
            case 5 -> SurvivalPerk.ARCANE_MASTERY;
            default -> throw new IllegalStateException("Unexpected value: " + choice);
        };
    }

    public void runSurvivalMode() {
        System.out.println("\n\uD83C\uDF1F SURVIVAL MODE: How many waves can you survive?\n");

        playerSelectSurvivor();

        Monster player = playerTeam.getActiveMonster();
        int wave = 1;

        while (player.isAlive()) {
            System.out.println("\n\uD83C\uDF0B --- WAVE " + wave + " ---");
            generateEnemyTeamForWave(wave);

            Monster enemy = enemyTeam.getActiveMonster();

            while (player.isAlive() && enemy.isAlive()) {
                System.out.println("\n💥 BATTLE: " + player.getType() + " vs " + enemy.getType());

                System.out.println("\n😈 Your Move:");
                System.out.println("[1]  Basic Attack");
                System.out.println("[2] Special Attack");
                System.out.println("[3] Use Potion");
                System.out.println("[4] Wait / Regenerate (+10 Energy)");

                int choice = -1;
                while (choice < 1 || choice > 4) {
                    if (scanner.hasNextInt()) {
                        choice = scanner.nextInt();
                    } else {
                        scanner.next();
                    }
                }

                switch (choice) {
                    case 1 -> player.basicAttack(enemy);
                    case 2 -> {
                        try {
                            boolean success = player.specialAttack(enemy);
                            if (!success) continue;
                        } catch (NotEnoughEnergyException e) {
                            System.out.println(e.getMessage());
                            continue;
                        }
                    }
                    case 3 -> {
                        if (!ruleConfig.isPotionsEnabled()) {
                            System.out.println("Potions are disabled!");
                            continue;
                        }

                        System.out.println("[1] Healing Potion\n[2] Energy Potion");
                        int potionChoice = scanner.nextInt();

                        if (potionChoice == 1) {
                            try {
                                playerTeam.useHealingPotion(player);
                            } catch (InvalidTargetException e) {
                                System.out.println(e.getMessage());
                                continue;
                            }
                        } else if (potionChoice == 2) {
                            try {
                                playerTeam.useEnergyPotion(player);
                            } catch (InvalidTargetException e) {
                                System.out.println(e.getMessage());
                                continue;
                            }
                        } else {
                            System.out.println("Invalid potion option.");
                            continue;
                        }

                        continue;
                    }
                    case 4 -> {
                        if (waitCooldown > 0) {
                            System.out.println("⏳ You need to wait " + waitCooldown + " more turn(s) before resting again.");
                            continue;
                        }

                        if (player.getEnergy() >= player.getMaxEnergy()) {
                            System.out.println("⚠️ Your energy is already full! Rest not needed.");
                            continue;
                        }

                        int energyBefore = player.getEnergy();
                        player.setEnergy(player.getEnergy() + 10);
                        if (player.getEnergy() > player.getMaxEnergy()) {
                            player.setEnergy(player.getMaxEnergy());
                        }

                        System.out.println("🧘 You rested and gained " + (player.getEnergy() - energyBefore) + " energy.");
                        waitCooldown = 2;
                    }
                }

                if (!enemy.isAlive()) {
                    System.out.println("\n💀 Enemy defeated!");
                    break;
                }

                System.out.println("\n👹 Enemy's Turn...");
                if (enemyTeam.isPotionUsageEnabled()) {
                    enemyUsePotions();
                }
                if (enemy.isSpecialReady()) {
                    try {
                        enemy.specialAttack(player);
                    } catch (NotEnoughEnergyException e) {
                        enemy.basicAttack(player);
                    }
                } else {
                    enemy.basicAttack(player);
                }

                player.endTurn();
                if (waitCooldown > 0) waitCooldown--;
                enemy.endTurn();
                System.out.println("\n📊 Your Status → HP: " + player.getHealth() + " | Energy: " + player.getEnergy());

            }

            if (!player.isAlive()) break;

            System.out.println("\n🌊 You survived Wave " + wave + "!");
            player.setHealth(player.getHealth() + 30);
            player.setEnergy(player.getEnergy() + 20);
            System.out.println("❤️ +30 HP | ⚡ +20 Energy recovered");
            System.out.println("💪 Post-wave stats → HP: " + player.getHealth() + " | Energy: " + player.getEnergy());

            wave++;
        }

        System.out.println("\n☠️ Game Over! You survived " + (wave - 1) + " wave(s).");
    }

    public String handleBasicAttack() {
        Monster attacker = getActivePlayerMonster();
        Monster defender = getActiveEnemyMonster();

        if (attacker == null || defender == null) {
            return "❌ Cannot perform attack: One of the monsters is missing!";
        }

        int prevEnemyHealth = defender.getHealth();
        attacker.basicAttack(defender);

        int damageDealt = Math.max(0, prevEnemyHealth - defender.getHealth());
        return attacker.getType() + " dealt " + damageDealt + " damage to " + defender.getType() + "!";
    }

    public String handleSpecialAttack() {
        Monster attacker = getActivePlayerMonster();
        Monster defender = getActiveEnemyMonster();

        if (!attacker.isSpecialReady()) {
            return "⏳ " + attacker.getType() + "'s special attack is not ready yet!";
        }

        if (attacker.getCooldownRemaining() > 0) {
            return "⏳ " + attacker.getType() + "'s attack needs " + attacker.getCooldownRemaining() + " more turn(s) to recharge!";
        }

        if (attacker.getEnergy() < attacker.getSpecialCost()) {
            return "❌ Not enough energy to use special attack.";
        }

        int prevHP = defender.getHealth();
        try {
            boolean success = attacker.specialAttack(defender);
            if (success) {
                return attacker.getType() + " used special attack on " + defender.getType() + "!\n"
                        + "Dealt " + (prevHP - defender.getHealth()) + " damage. Energy used: " + attacker.getSpecialCost();
            } else {
                return "⚠️ Special attack failed for unknown reason.";
            }
        } catch (NotEnoughEnergyException e) {
            return "❌ Special attack failed: " + e.getMessage();
        }
    }


    public String handleUsePotion(String type) {
        Monster target = getActivePlayerMonster();

        try {
            if (type.equalsIgnoreCase("health")) {
                if (!playerTeam.hasHealingPotions()) {
                    return "⚠️ No Healing Potions left!";
                }
                playerTeam.useHealingPotion(target);
                return "💊 " + target.getType() + " used a Healing Potion and restored 50 HP!";
            } else if (type.equalsIgnoreCase("energy")) {
                if (!playerTeam.hasEnergyPotions()) {
                    return "⚠️ No Energy Potions left!";
                }
                playerTeam.useEnergyPotion(target);
                return "⚡ " + target.getType() + " used an Energy Potion and restored 50 energy!";
            } else {
                return "❌ Invalid potion type!";
            }
        } catch (InvalidTargetException e) {
            return e.getMessage();
        }
    }

    public String handleEnemyTurn() {
        Monster attacker = getActiveEnemyMonster();
        Monster defender = getActivePlayerMonster();

        if (attacker == null || defender == null) {
            return "Enemy turn skipped due to missing monster.";
        }

        StringBuilder log = new StringBuilder();

        if (enemyTeam.isPotionUsageEnabled()) {
            String potionLog = enemyUsePotions();
            if (potionLog != null) return potionLog;
        }

        boolean success = attemptSpecialAttack(attacker, defender, log);

        if (!success) {
            attacker.basicAttack(defender);
            log.append("🗡 Enemy ").append(attacker.getType()).append(" used a basic attack.\n");
            log.append(defender.getType()).append(" HP: ").append(defender.getHealth()).append("\n");
        }

        attacker.endTurn();
        endTurn();
        return log.toString();
    }


    public boolean attemptSpecialAttack(Monster attacker, Monster defender, StringBuilder log) {
        if (!attacker.isSpecialReady()) {
            log.append("⚠️ Special attack is not ready.\n");
            return false;
        }
        if (attacker.getCooldownRemaining() > 0) {
            log.append("⏳ " + attacker.getType() + "'s special is on cooldown (" + attacker.getCooldownRemaining() + " turns left).\n");
            return false;
        }
        if (attacker.getEnergy() < attacker.getSpecialCost()) {
            log.append("❌ Not enough energy to use special attack.\n");
            return false;
        }

        int prevHP = defender.getHealth();
        try {
            boolean success = attacker.specialAttack(defender);
            if (success) {
                log.append("⚡ " + attacker.getType() + " used special attack!\n");
                log.append("Dealt " + (prevHP - defender.getHealth()) + " damage. Energy used: " + attacker.getSpecialCost() + ".\n");
            } else {
                log.append("⚠️ Special attack failed for unknown reason.\n");
            }
            return success;
        } catch (Exception e) {
            log.append("❌ Special attack failed: ").append(e.getMessage()).append("\n");
            return false;
        }
    }

    public Monster getActivePlayerMonster() {
        return playerTeam.getActiveMonster();
    }

    public Monster getActiveEnemyMonster() {
        return enemyTeam.getActiveMonster();
    }

    public Team getPlayerTeam() {
        return playerTeam;
    }

    public Team getEnemyTeam() {
        return enemyTeam;
    }

    public void applyHardcoreEnemyBonus(Monster enemy, Monster player, boolean usedSpecial) {
        if (!ruleConfig.isHardcoreMode()) return;

        System.out.println("\uD83D\uDD25 Hardcore bonus applied!");

        if (usedSpecial) {
            System.out.println("💀 Enemy special attack hits harder in Hardcore!");
            player.takeDamage(10);
        } else {
            System.out.println("💀 Enemy basic attack is more aggressive in Hardcore!");
            player.takeDamage(5);
        }
    }

    public void endTurn() {
        if (playerTeam != null && playerTeam.getActiveMonster() != null) {
            playerTeam.getActiveMonster().reduceCooldown();
        }
        if (enemyTeam != null && enemyTeam.getActiveMonster() != null) {
            enemyTeam.getActiveMonster().reduceCooldown();
        }
    }

    public void setSurvivalPerk(SurvivalPerk perk) {
        this.selectedSurvivalPerk = perk;
        if (playerTeam.getMonsters().isEmpty()) return;
        Monster player = playerTeam.getActiveMonster();
        if (player != null) {
            player.setSurvivalPerk(perk);
        }
    }

    public void generateEnemyTeamForWave(int wave) {
        enemyTeam = new Team();
        enemyTeam.setEnablePotionUsage(false);
        List<Integer> pool = Arrays.asList(1, 2, 3, 4, 5);
        Collections.shuffle(pool);

        for (int i = 0; i < 3; i++) {
            Monster m = createMonsterByChoice(pool.get(i));
            m.setRuleConfig(ruleConfig);

            int baseHP = m.getMaxHealth();
            int baseEnergy = m.getMaxEnergy();

            // Wave-based scaling
            if (wave <= 2) {
                m.setHealth(Math.max((int)(baseHP * 0.8), 30));
                m.setEnergy(Math.max((int)(baseEnergy * 0.8), 20));
            } else {
                m.setHealth(baseHP);
                m.setEnergy(baseEnergy);
            }

            if (wave >= 5) {
                m.setEnergy(m.getEnergy() + 20);
                WeatherEffect randomWeather = WeatherEffect.getRandomWeather();
                ruleConfig.setWeatherEffect(randomWeather);
                System.out.println("⛈️ Weather has changed! New condition: " + randomWeather.getDescription());
            }

            System.out.println("⚔️ Enemy " + m.getType() + " | Wave " + wave +
                    " | HP = " + m.getHealth() + " | Energy = " + m.getEnergy());

            enemyTeam.addMonster(m, 3);
        }

        if (wave >= 7) {
            enemyTeam.setEnablePotionUsage(true);
        }

        System.out.println("👹 Enemy team ready for wave " + wave + "!");
    }

    private Team buildRandomTeam(int size) {
        return TeamBuilder.buildRandomTeam(size, this.ruleConfig);
    }

    private void assignWeaponToRandomMonster(Team team, Weapon weapon) {
        List<Monster> monsters = team.getMonsters();
        int index = new Random().nextInt(monsters.size());
        monsters.get(index).equipWeapon(weapon);
        System.out.println("🔧 " + weapon.getName() + " equipped to " + monsters.get(index).getType());
    }
}

