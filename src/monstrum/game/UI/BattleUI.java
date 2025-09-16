package monstrum.game.UI;

import monstrum.model.*;
import monstrum.game.*;
import monstrum.model.rules.WeatherEffect;
import monstrum.utils.MusicPlayer;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Map;

public class BattleUI extends JFrame {

    private JTextPane battleLog;
    private Font creepsterFont;
    private final Game game;
    private final RuleConfig ruleConfig;
    private final Team playerTeam;
    private Team enemyTeam;
    private JProgressBar playerHealthBar, enemyHealthBar;
    private JProgressBar playerEnergyBar, enemyEnergyBar;
    private JPanel playerWrapper;
    private JPanel enemyWrapper;
    private JLabel playerNameLabel;
    private JLabel playerImageLabel;
    private JLabel weatherIcon;
    private JLabel perkIcon;
    private JPanel centerPanel;
    private JButton basicAttackBtn;
    private JButton specialAttackBtn;
    private JButton switchBtn;
    private JButton potionBtn;
    private JButton p2AttackBtn;
    private JButton p2SpecialBtn;
    private JButton p2SwitchBtn;
    private JButton p2PotionBtn;
    private JButton regenerateBtn;
    private boolean isPlayerOneTurn = true;
    private boolean isPvPMode = false;
    private int waitCooldown = 0;
    private int currentWave = 1;

    public BattleUI(Game game, RuleConfig ruleConfig, Team playerTeam, Team enemyTeam, boolean isPvPMode) {
        this.game = game;
        this.ruleConfig = ruleConfig;
        this.playerTeam = playerTeam;
        this.enemyTeam = enemyTeam;
        this.isPvPMode = isPvPMode;

        setTitle("Battle Arena");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        creepsterFont = loadCreepsterFont(48f);

        String arenaBackgroundPath = getArenaBackground(ruleConfig.getArenaType());
        BackgroundPanel background = new BackgroundPanel(arenaBackgroundPath);
        background.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel title = new JLabel("BATTLE ARENA", SwingConstants.CENTER);
        title.setFont(creepsterFont.deriveFont(70f));
        title.setForeground(new Color(117, 50, 117));
        topPanel.add(title, BorderLayout.CENTER);

        JPanel weatherWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        weatherWrapper.setOpaque(false);
        weatherIcon = new JLabel();
        if (ruleConfig.getWeatherEffect() != null) {
            weatherIcon.setIcon(new ImageIcon(loadWeatherIcon(ruleConfig.getWeatherEffect())));
            weatherIcon.setToolTipText("Weather: " + ruleConfig.getWeatherEffect().name());
            weatherIcon.setVisible(true);
        }
        weatherWrapper.add(weatherIcon);
        topPanel.add(weatherWrapper, BorderLayout.WEST);

        JPanel perkWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        perkWrapper.setOpaque(false);
        perkIcon = new JLabel();
        if (ruleConfig.getGameMode() == RuleConfig.GameMode.SURVIVAL) {
            SurvivalPerk perk = game.getSurvivalPerk();
            System.out.println("🎯 Perk loaded in BattleUI: " + perk);

            if (perk != null) {
                perkIcon.setIcon(new ImageIcon(loadSurvivalPerkIcon(perk)));
                perkIcon.setToolTipText("Survival Perk: " + perk.name().replace("_", " "));
                perkIcon.setVisible(true);
            }
        }
        perkWrapper.add(perkIcon);
        topPanel.add(perkWrapper, BorderLayout.EAST);

        background.add(topPanel, BorderLayout.NORTH);


        playerWrapper = new JPanel();
        playerWrapper.setOpaque(false);
        playerWrapper.add(createMonsterDisplayPanel(playerTeam.getActiveMonster(), true, ruleConfig));

        enemyWrapper = new JPanel();
        enemyWrapper.setOpaque(false);
        enemyWrapper.add(createMonsterDisplayPanel(enemyTeam.getActiveMonster(), false, ruleConfig));

        centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 200, 20));
        centerPanel.setOpaque(false);
        centerPanel.add(playerWrapper);
        centerPanel.add(enemyWrapper);

        background.add(centerPanel, BorderLayout.CENTER);

        refreshPlayerPanel();
        refreshEnemyPanel();


        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setPreferredSize(new Dimension(400, 0));
        logPanel.setOpaque(false);

        battleLog = new JTextPane();
        battleLog.setEditable(false);
        battleLog.setBackground(new Color(20, 0, 30));
        battleLog.setForeground(Color.WHITE);
        battleLog.setFont(new Font("Monospaced", Font.PLAIN, 14));
        battleLog.setOpaque(true);

        JScrollPane scrollPane = new JScrollPane(battleLog);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(120, 40, 120), 2),
                "Battle Log",
                0, 0,
                new Font("SansSerif", Font.BOLD, 16),
                new Color (67, 23, 78)
        ));

        logPanel.add(scrollPane, BorderLayout.CENTER);
        background.add(logPanel, BorderLayout.EAST);

        if (ruleConfig.isHardcoreMode()) {
            appendToLog("☠️ Hardcore Mode Active: No potions, enemy monsters are stronger!\n", Color.MAGENTA);
            if (ruleConfig.getWeatherEffect() != null) {
                appendToLog("🧪 Weather Effect: " + ruleConfig.getWeatherEffect().name() + "\n", Color.CYAN);
            }
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setOpaque(false);

        basicAttackBtn = createStyledButton("ATTACK");
        specialAttackBtn = createStyledButton("SPECIAL ATTACK");
        switchBtn = createStyledButton("SWITCH MONSTER");
        regenerateBtn = createStyledButton("REGENERATE");
        potionBtn = createStyledButton("USE POTION");

        if (ruleConfig.isHardcoreMode()) {
            potionBtn.setEnabled(false);
        }

        if (ruleConfig.getGameMode() == RuleConfig.GameMode.SURVIVAL) {
            switchBtn.setVisible(false);
            regenerateBtn.setVisible(true);

            regenerateBtn.addActionListener(e -> {
                Monster player = playerTeam.getActiveMonster();

                if (player.getEnergy() >= player.getMaxEnergy()) {
                    appendToLog("⚠️ Your energy is already full! Rest not needed.\n", Color.WHITE);
                    return;
                }

                if (waitCooldown > 0) {
                    appendToLog("⏳ You need to wait " + waitCooldown + " more turn(s) before resting again.\n", Color.WHITE);
                    return;
                }

                int before = player.getEnergy();
                player.setEnergy(player.getEnergy() + 10);
                if (player.getEnergy() > player.getMaxEnergy()) {
                    player.setEnergy(player.getMaxEnergy());
                }

                waitCooldown = 2;
                int gained = player.getEnergy() - before;

                appendToLog("🧘 You rested and gained " + gained + " energy.\n", Color.WHITE);
                player.endTurn();

                String enemyLog = game.handleEnemyTurn();
                appendToLog("\n" + enemyLog + "\n", Color.YELLOW);

                if (!handleAutoSwitchIfDead()) return;

                updateMonsterDisplay();
                refreshEnemyPanel();
            });

        } else {
            regenerateBtn.setVisible(false);
            switchBtn.setVisible(true);
            switchBtn.addActionListener(e -> handleMonsterSwitch());
        }

        basicAttackBtn.addActionListener(e -> {
            if (isPvPMode) {
                handlePvPTurn(game.handleBasicAttack());
            } else {
                String log = game.handleBasicAttack();
                appendToLog(log + "\n", Color.WHITE);

                if (ruleConfig.getGameMode() == RuleConfig.GameMode.SURVIVAL && !enemyTeam.getActiveMonster().isAlive()) {
                    currentWave++;
                    startNextWave();
                    return;
                }

                Monster player = playerTeam.getActiveMonster();
                if (player == null) return;
                player.endTurn();

                if (!handleAutoSwitchIfDead()) return;

                String enemyLog = game.handleEnemyTurn();
                appendToLog(enemyLog + "\n", Color.YELLOW);

                if (!handleAutoSwitchIfDead()) return;

                if (!playerTeam.isTeamAlive()) {
                    String message = ruleConfig.isHardcoreMode() ? "You lost! The enemy was too strong..." : "You lost! All your monsters are defeated.";
                    new WinLosePopupUI("You Lose", message, false);
                    dispose();
                    return;
                }

                if (!enemyTeam.isTeamAlive()) {
                    new WinLosePopupUI("You Win!", "You have defeated all enemy monsters!", false);
                    dispose();
                    return;
                }

                updateMonsterDisplay();
                refreshEnemyPanel();
            }
        });

        specialAttackBtn.addActionListener(e -> {
            if (isPvPMode) {
                Monster current = getCurrentPlayerMonster();
                if (!current.isSpecialReady()) {
                    appendToLog("⚠️ " + current.getType() + "'s special attack is not ready! Cooldown: " + current.getCooldownRemaining() + " turn(s).\n", Color.WHITE);
                    return;
                }
                String log = game.handleSpecialAttack();
                playSpecialSoundIfNeeded(log, current);
                handlePvPTurn(log);
            } else {
                Monster player = playerTeam.getActiveMonster();
                if (player == null) return;
                if (!player.isSpecialReady()) {
                    appendToLog("⚠️ " + player.getType() + "'s special attack is not ready! Cooldown: " + player.getCooldownRemaining() + " turn(s).\n", Color.WHITE);
                    return;
                }

                String log = game.handleSpecialAttack();
                playSpecialSoundIfNeeded(log, player);

                appendToLog(log + "\n", Color.WHITE);

                if (ruleConfig.getGameMode() == RuleConfig.GameMode.SURVIVAL && !enemyTeam.getActiveMonster().isAlive()) {
                    currentWave++;
                    startNextWave();
                    return;
                }
                if (!handleAutoSwitchIfDead()) return;

                player.endTurn();

                String enemyLog = game.handleEnemyTurn();
                appendToLog("\n" + enemyLog + "\n", Color.YELLOW);

                if (ruleConfig.isHardcoreMode()) {
                    Monster attacker = game.getActiveEnemyMonster();
                    Monster target = game.getActivePlayerMonster();

                    boolean usedSpecial = enemyLog.toLowerCase().contains("special");

                    game.applyHardcoreEnemyBonus(attacker, target, usedSpecial);
                }

                if (!handleAutoSwitchIfDead()) return;

                if (!playerTeam.isTeamAlive()) {
                    String message = ruleConfig.isHardcoreMode() ? "You lost! The enemy was too strong..." : "You lost! All your monsters are defeated.";
                    new WinLosePopupUI("You Lose", message, false);
                    dispose();
                    return;
                }

                if (!enemyTeam.isTeamAlive()) {
                    new WinLosePopupUI("You Win!", "You have defeated all enemy monsters!", false);
                    dispose();
                    return;
                }

                updateMonsterDisplay();
                refreshEnemyPanel();
            }
        });

        potionBtn.addActionListener(e -> handlePotionUse());
        String enemyLog = game.handleEnemyTurn();
        updateMonsterDisplay();
        refreshEnemyPanel();
        appendToLog(enemyLog + "\n", Color.YELLOW);

        buttonPanel.add(basicAttackBtn);
        buttonPanel.add(specialAttackBtn);
        if (ruleConfig.getGameMode() == RuleConfig.GameMode.SURVIVAL) {
            buttonPanel.add(regenerateBtn);
        } else {
            buttonPanel.add(switchBtn);
        }
        buttonPanel.add(potionBtn);

        JPanel p2ButtonPanel = null;
        if (isPvPMode) {
            p2ButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
            p2ButtonPanel.setOpaque(false);

            p2AttackBtn = createStyledButton("ATTACK");
            p2SpecialBtn = createStyledButton("SPECIAL ATTACK");
            p2SwitchBtn = createStyledButton("SWITCH MONSTER");
            p2PotionBtn = createStyledButton("USE POTION");

            p2AttackBtn.addActionListener(e -> handlePvPTurn(game.handleBasicAttack()));
            p2SpecialBtn.addActionListener(e -> {
                Monster current = getCurrentPlayerMonster();
                if (!current.isSpecialReady()) {
                    appendToLog("⚠️ " + current.getType() + "'s special attack is not ready! Cooldown: " + current.getCooldownRemaining() + " turn(s).\n", Color.WHITE);
                    return;
                }

                String log = game.handleSpecialAttack();
                playSpecialSoundIfNeeded(log, current);
                handlePvPTurn(log);
            });
            p2SwitchBtn.addActionListener(e -> handleMonsterSwitch());
            p2PotionBtn.addActionListener(e -> handlePotionUse());

            p2ButtonPanel.add(p2AttackBtn);
            p2ButtonPanel.add(p2SpecialBtn);
            p2ButtonPanel.add(p2SwitchBtn);
            p2ButtonPanel.add(p2PotionBtn);
        }

        JPanel bottomPanel = new JPanel(new GridLayout(isPvPMode ? 2 : 1, 1));
        bottomPanel.setOpaque(false);
        bottomPanel.add(buttonPanel);
        if (isPvPMode) {
            bottomPanel.add(p2ButtonPanel);
        }
        background.add(bottomPanel, BorderLayout.PAGE_END);

        setContentPane(background);
        setVisible(true);

        if (ruleConfig.getGameMode() == RuleConfig.GameMode.SURVIVAL) {
            startNextWave();
            battleLog.setCaretPosition(battleLog.getDocument().getLength());
            battleLog.repaint();
        }

        updateButtonsForCurrentPlayer();
    }


    private JButton createStyledButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("SansSerif", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(20, 0, 30));
        button.setBorder(BorderFactory.createLineBorder(new Color(67, 23, 78), 3));
        button.setPreferredSize(new Dimension(220, 50));
        return button;
    }

    private JPanel createMonsterDisplayPanel(Monster monster, boolean isPlayer, RuleConfig ruleConfig) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(280, 600));
        card.setPreferredSize(new Dimension(260, 550));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(67, 23, 78), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel nameLabel = new JLabel(monster.getType(), SwingConstants.CENTER);
        nameLabel.setFont(loadCreepsterFont(28f));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setForeground(getColorByType(monster.getType()));

        // Health bar
        JProgressBar healthBar = new JProgressBar(0, monster.getMaxHealth());
        healthBar.setValue(monster.getHealth());
        healthBar.setString(monster.getHealth() + "/" + monster.getMaxHealth());
        healthBar.setStringPainted(true);
        healthBar.setForeground(new Color(200, 0, 0)); // red
        healthBar.setBackground(Color.DARK_GRAY);
        healthBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Energy bar
        JProgressBar energyBar = new JProgressBar(0, monster.getMaxEnergy());
        energyBar.setValue(monster.getEnergy());
        energyBar.setString(monster.getEnergy() + "/" + monster.getMaxEnergy());
        energyBar.setStringPainted(true);
        energyBar.setForeground(new Color(0, 150, 255)); // cyan
        energyBar.setBackground(Color.DARK_GRAY);
        energyBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (isPlayer) {
            playerHealthBar = healthBar;
            playerEnergyBar = energyBar;
        } else {
            enemyHealthBar = healthBar;
            enemyEnergyBar = energyBar;
        }

        JLabel imageLabel = new JLabel(loadMonsterImage(monster.getType()), SwingConstants.CENTER);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(nameLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(healthBar);
        card.add(Box.createVerticalStrut(3));
        card.add(energyBar);
        card.add(Box.createVerticalStrut(10));
        card.add(imageLabel);

        // Weapon icon
        if (monster.getWeapon() != null) {
            JLabel weaponIcon = new JLabel(new ImageIcon(loadWeaponIcon(monster.getWeapon().getName())));
            weaponIcon.setToolTipText("Weapon: " + monster.getWeapon().getName());
            weaponIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(Box.createVerticalStrut(10));
            card.add(weaponIcon);
        }

        // Potion icons
        if (isPlayer && !ruleConfig.isHardcoreMode()) {
            JPanel potionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            potionPanel.setOpaque(false);

            // Health Potion
            URL healthUrl = getClass().getResource("/monstrum.assets/images/health_potion.png");
            if (healthUrl != null) {
                ImageIcon icon = new ImageIcon(healthUrl);
                Image scaled = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                JLabel hpPotion = new JLabel(new ImageIcon(scaled));
                hpPotion.setToolTipText("Health Potion");
                potionPanel.add(hpPotion);
            }

            // Energy Potion
            URL energyUrl = getClass().getResource("/monstrum.assets/images/energy_potion.png");
            if (energyUrl != null) {
                ImageIcon icon = new ImageIcon(energyUrl);
                Image scaled = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                JLabel energyPotion = new JLabel(new ImageIcon(scaled));
                energyPotion.setToolTipText("Energy Potion");
                potionPanel.add(energyPotion);
            }

            card.add(Box.createVerticalStrut(10));
            card.add(potionPanel);
        }

        return card;
    }


    private ImageIcon loadMonsterImage(String type) {
        String fileName = switch (type) {
            case "FireMonster" -> "fire.png";
            case "WaterMonster" -> "water.png";
            case "ThunderMonster" -> "thunder.png";
            case "DarkMonster" -> "dark.png";
            case "AcidMonster" -> "acid.png";
            default -> "default.png";
        };

        String path = "/monstrum.assets/images/" + fileName;
        java.net.URL imageUrl = getClass().getResource(path);
        if (imageUrl == null) {
            System.err.println("❌ IMAGE NOT FOUND: " + path);
            return new ImageIcon();
        }
        Image image = new ImageIcon(imageUrl).getImage().getScaledInstance(300, 400, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    private Font loadCreepsterFont(float size) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/monstrum.assets/fonts/Creepster-Regular.ttf"));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font.deriveFont(size);
        } catch (Exception e) {
            return new Font("SansSerif", Font.BOLD, (int) size);
        }
    }

    private String getArenaBackground(RuleConfig.ArenaType arenaType) {
        return switch (arenaType) {
            case VOLCANO -> "/monstrum.assets/images/background_volcano.png";
            case SWAMP -> "/monstrum.assets/images/background_swamp.png";
            case DARK_FOREST -> "/monstrum.assets/images/background_dark_forest.png";
            case OCEAN -> "/monstrum.assets/images/background_ocean.png";
            case ZEUS_MOUNTAIN -> "/monstrum.assets/images/background_zeus_mountain.png";
            default -> "/monstrum.assets/images/battle_arena.png";
        };
    }

    private Image loadWeatherIcon(WeatherEffect effect) {
        String iconName = switch (effect) {
            case RAIN -> "rain.png";
            case STORM -> "storm.png";
            case FOG -> "fog.png";
            case HEATWAVE -> "heatwave.png";
            case MAGNETIC_FIELD -> "magnetic.png";
        };

        String path = "/monstrum.assets/icons/" + iconName;
        java.net.URL url = getClass().getResource(path);
        if (url == null) {
            System.err.println("❌ WEATHER ICON NOT FOUND: " + path);
            return new ImageIcon().getImage();
        }
        return new ImageIcon(url).getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
    }

    private Image loadWeaponIcon(String weaponName) {
        String name = weaponName.toLowerCase();
        String path = "/monstrum.assets/images/weapon_" + name + ".png";

        java.net.URL location = getClass().getResource(path);
        if (location == null) {
            System.err.println("❌ WEAPON ICON NOT FOUND: " + path);
            return new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
        }

        return new ImageIcon(location).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
    }

    private Image loadSurvivalPerkIcon(SurvivalPerk perk) {
        String iconName = switch (perk) {
            case ADRENALINE_RUSH -> "adrenaline.png";
            case IRON_WILL -> "iron_will.png";
            case VAMPIRIC_AURA -> "vampiric.png";
            case PHANTOM_REFLEX -> "phantom.png";
            case ARCANE_MASTERY -> "arcane.png";
        };

        String path = "/monstrum.assets/icons/icon_" + iconName;
        URL url = getClass().getResource(path);
        if (url == null) {
            System.err.println("❌ PERK ICON NOT FOUND: " + path);
            return new BufferedImage(70, 70, BufferedImage.TYPE_INT_ARGB);
        }

        return new ImageIcon(url).getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
    }

    private Color getColorByType(String type) {
        return switch (type) {
            case "FireMonster" -> new Color(255, 80, 0);
            case "WaterMonster" -> new Color(100, 200, 255);
            case "ThunderMonster" -> new Color(255, 230, 0);
            case "DarkMonster" -> new Color(200, 100, 255);
            case "AcidMonster" -> new Color(0, 255, 100);
            default -> Color.WHITE;
        };
    }
    private void updateMonsterDisplay() {
        Monster player = playerTeam.getActiveMonster();
        Monster enemy = enemyTeam.getActiveMonster();

        if (player == null || enemy == null) return;

        if (playerHealthBar != null) {
            playerHealthBar.setMaximum(player.getMaxHealth());
            playerHealthBar.setValue(player.getHealth());
            playerHealthBar.setString(player.getHealth() + "/" + player.getMaxHealth());
        }

        if (playerEnergyBar != null) {
            playerEnergyBar.setMaximum(player.getMaxEnergy());
            playerEnergyBar.setValue(player.getEnergy());
            playerEnergyBar.setString(player.getEnergy() + "/" + player.getMaxEnergy());
        }

        if (enemyHealthBar != null) {
            enemyHealthBar.setMaximum(enemy.getMaxHealth());
            enemyHealthBar.setValue(enemy.getHealth());
            enemyHealthBar.setString(enemy.getHealth() + "/" + enemy.getMaxHealth());
        }

        if (enemyEnergyBar != null) {
            enemyEnergyBar.setMaximum(enemy.getMaxEnergy());
            enemyEnergyBar.setValue(enemy.getEnergy());
            enemyEnergyBar.setString(enemy.getEnergy() + "/" + enemy.getMaxEnergy());
        }

        System.out.println("🔍 UI Updated: Player HP = " + player.getHealth() + ", Alive: " + player.isAlive());
        System.out.println("🔍 Current Active Player: " + player.getType());
    }

    private void refreshEnemyPanel() {
        enemyWrapper.removeAll();
        JPanel newEnemyPanel = createMonsterDisplayPanel(enemyTeam.getActiveMonster(), false, ruleConfig);
        enemyWrapper.add(newEnemyPanel);
        enemyWrapper.revalidate();
        enemyWrapper.repaint();
    }

    private void refreshPlayerPanel() {
        SwingUtilities.invokeLater(() -> {
            playerWrapper.removeAll();

            JPanel newPanel = createMonsterDisplayPanel(playerTeam.getActiveMonster(), true, ruleConfig);
            playerWrapper.add(newPanel);

            playerWrapper.revalidate();
            playerWrapper.repaint();

            Container parent = playerWrapper.getParent();
            if (parent != null) {
                parent.revalidate();
                parent.repaint();
            }

            playerHealthBar = (JProgressBar) newPanel.getComponent(2);
            playerEnergyBar = (JProgressBar) newPanel.getComponent(4);
        });
    }



    private void handleMonsterSwitch() {
        java.util.List<Monster> allMonsters = playerTeam.getMonsters();
        Monster current = playerTeam.getActiveMonster();

        java.util.List<Integer> switchableIndices = new java.util.ArrayList<>();
        java.util.List<String> optionsList = new java.util.ArrayList<>();

        for (int i = 0; i < allMonsters.size(); i++) {
            Monster m = allMonsters.get(i);
            if (m.isAlive() && !m.equals(current)) {
                switchableIndices.add(i);
                optionsList.add(m.getType());
            }
        }

        if (switchableIndices.isEmpty()) {
            appendToLog("⚠️ No other monsters available to switch!\n", Color.WHITE);
            return;
        }

        String[] options = optionsList.toArray(new String[0]);
        String selectedType = (String) JOptionPane.showInputDialog(
                this,
                "Choose a monster to switch to:",
                "Switch Monster",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (selectedType != null) {
            for (int i = 0; i < switchableIndices.size(); i++) {
                if (options[i].equals(selectedType)) {
                    playerTeam.switchMonster(switchableIndices.get(i));
                    forceReplacePlayerCard();

                    appendToLog("🔄 Switched to " + selectedType + "\n", Color.WHITE);
                    updateMonsterDisplay();
                    break;
                }
            }
        }
    }

    private void handlePotionUse() {
        if (ruleConfig.isHardcoreMode()) {
            appendToLog("❌ Potions are disabled in Hardcore Mode!\n", Color.MAGENTA);
            return;
        }

        String[] options = {"Health Potion", "Energy Potion"};
        String choice = (String) JOptionPane.showInputDialog(
                this,
                "Choose a potion to use:",
                "Use Potion",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice != null) {
            String type = choice.toLowerCase().contains("health") ? "health" : "energy";
            String log = game.handleUsePotion(type);
            updateMonsterDisplay();
            appendToLog(log + "\n", Color.WHITE);
        }
    }

    private boolean handleAutoSwitchIfDead() {
        Monster active = playerTeam.getActiveMonster();
        if (active == null || !active.isAlive()) {
            MusicPlayer.playSoundEffect("death_sound.wav");
            Monster next = playerTeam.switchToNextAliveMonster();
            if (next != null) {
                System.out.println("✅ Auto-switched to: " + next.getType());
                appendToLog("💀 Your monster fainted! Switching to " + next.getType() + "\n", Color.WHITE);

                // Replace player panel
                centerPanel.remove(playerWrapper);
                playerWrapper = new JPanel();
                playerWrapper.setOpaque(false);
                playerWrapper.add(createMonsterDisplayPanel(next, true, ruleConfig));
                centerPanel.add(playerWrapper, 0);
                centerPanel.revalidate();
                centerPanel.repaint();

                updateMonsterDisplay();
                return true;
            } else {
                if (ruleConfig.getGameMode() == RuleConfig.GameMode.SURVIVAL) {
                    new WinLosePopupUI("Game Over", "You survived " + (currentWave - 1) + " waves!", true);
                } else {
                    new WinLosePopupUI("Defeat", "All your monsters are defeated!", false);
                }

                basicAttackBtn.setEnabled(false);
                specialAttackBtn.setEnabled(false);
                regenerateBtn.setEnabled(false);
                switchBtn.setEnabled(false);
                potionBtn.setEnabled(false);

                if (isPvPMode) {
                    p2AttackBtn.setEnabled(false);
                    p2SpecialBtn.setEnabled(false);
                    p2SwitchBtn.setEnabled(false);
                    p2PotionBtn.setEnabled(false);
                }

                dispose();
                return false;
            }
        }
        return true;
    }



    private void forceReplacePlayerCard() {
        SwingUtilities.invokeLater(() -> {
            playerWrapper.removeAll();
            playerWrapper.revalidate();
            playerWrapper.repaint();

            Monster current = playerTeam.getActiveMonster();
            JPanel freshPanel = createMonsterDisplayPanel(current, true, ruleConfig);

            playerHealthBar = (JProgressBar) freshPanel.getComponent(2);
            playerEnergyBar = (JProgressBar) freshPanel.getComponent(4);

            playerWrapper.add(freshPanel);
            playerWrapper.revalidate();
            playerWrapper.repaint();

            System.out.println("✅ Player card rebuilt: " + current.getType());
        });
    }

    private Monster getCurrentPlayerMonster() {
        return isPlayerOneTurn ? playerTeam.getActiveMonster() : enemyTeam.getActiveMonster();
    }

    private void handlePvPTurn(String log) {
        appendToLog((isPlayerOneTurn ? "🎮 Player 1: " : "🎮 Player 2: ") + log + "\n", Color.WHITE);

        getCurrentPlayerMonster().endTurn();
        updateMonsterDisplay();
        refreshPlayerPanel();
        refreshEnemyPanel();

        // Switch turn
        isPlayerOneTurn = !isPlayerOneTurn;
        updateButtonsForCurrentPlayer();

        if (!playerTeam.isTeamAlive()) {
            new WinLosePopupUI("Player 2 Wins!", "All of Player 1's monsters are defeated!", false);
            dispose();
            return;
        } else if (!enemyTeam.isTeamAlive()) {
            new WinLosePopupUI("Player 1 Wins!", "All of Player 2's monsters are defeated!", false);
            dispose();
            return;
        }

    }

    private void updateButtonsForCurrentPlayer() {
        boolean isP1 = isPlayerOneTurn;

        basicAttackBtn.setEnabled(isP1);
        specialAttackBtn.setEnabled(isP1);
        switchBtn.setEnabled(isP1);
        potionBtn.setEnabled(isP1 && !ruleConfig.isHardcoreMode());

        p2AttackBtn.setEnabled(!isP1);
        p2SpecialBtn.setEnabled(!isP1);
        p2SwitchBtn.setEnabled(!isP1);
        p2PotionBtn.setEnabled(!isP1);
    }

    private void playSpecialSoundIfNeeded(String log, Monster monster) {
        String type = monster.getType().toLowerCase();
        String soundFile = switch (type) {
            case "firemonster" -> "fire_sound.wav";
            case "watermonster" -> "water_sound.wav";
            case "thundermonster" -> "thunder_sound.wav";
            case "darkmonster" -> "dark_sound.wav";
            case "acidmonster" -> "acid_sound.wav";
            default -> null;
        };
        if (soundFile != null && log.toLowerCase().contains("dealt")) {
            MusicPlayer.playSoundEffect(soundFile);
        }
    }

    private void startNextWave() {
        if (!playerTeam.getActiveMonster().isAlive()) {
            appendToLog("\n☠️ Game Over! You survived " + (currentWave - 1) + " wave(s).\n", Color.MAGENTA);
            new WinLosePopupUI("Game Over", "You survived " + (currentWave - 1) + " waves!", true);
            return;
        }

        game.generateEnemyTeamForWave(currentWave);
        enemyTeam = game.getEnemyTeam(); // update new team
        updateMonsterDisplay();
        refreshEnemyPanel();

        if (currentWave > 1) {
            Monster player = playerTeam.getActiveMonster();
            player.setHealth(player.getHealth() + 30);
            player.setEnergy(player.getEnergy() + 20);
            appendToLog("\n🌊 You survived Wave " + (currentWave - 1) + "!\n", Color.CYAN);
            appendToLog("❤️ +30 HP | ⚡ +20 Energy recovered\n", Color.WHITE);
        }

        if (currentWave >= 5 && ruleConfig.getWeatherEffect() != null) {
            weatherIcon.setIcon(new ImageIcon(loadWeatherIcon(ruleConfig.getWeatherEffect())));
            weatherIcon.setVisible(true);
            weatherIcon.setToolTipText("Weather: " + ruleConfig.getWeatherEffect());
            appendToLog("⛈️ Weather Effect Active: " + ruleConfig.getWeatherEffect() + "\n", Color.MAGENTA);
        }

        SwingUtilities.invokeLater(() -> appendToLog("🌪️ --- WAVE " + currentWave + " ---\n", Color.CYAN));
        battleLog.setCaretPosition(battleLog.getDocument().getLength());
        battleLog.repaint();
    }

    private void showEndPopup(String title, String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        });
    }

    private void appendToLog(String message, Color color) {
        StyledDocument doc = battleLog.getStyledDocument();
        Style style = battleLog.addStyle("Style", null);
        StyleConstants.setForeground(style, color);
        try {
            doc.insertString(doc.getLength(), message, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }


    class BackgroundPanel extends JPanel {
        private Image image;

        public BackgroundPanel(String path) {
            image = new ImageIcon(getClass().getResource(path)).getImage();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
