/* UPDATED BattleUI.java: Battle log moved to right side */

package monstrum.game.UI;

import monstrum.model.*;
import monstrum.game.*;
import monstrum.model.rules.WeatherEffect;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Map;

public class BattleUI extends JFrame {

    private JTextArea battleLog;
    private Font creepsterFont;
    private final Game game;
    private final RuleConfig ruleConfig;
    private final Team playerTeam;
    private final Team enemyTeam;
    private JProgressBar playerHealthBar, enemyHealthBar;
    private JProgressBar playerEnergyBar, enemyEnergyBar;
    private JPanel playerWrapper;
    private JPanel enemyWrapper;


    public BattleUI(Game game, RuleConfig ruleConfig, Team playerTeam, Team enemyTeam) {
        this.game = game;
        this.ruleConfig = ruleConfig;
        this.playerTeam = playerTeam;
        this.enemyTeam = enemyTeam;

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

        if (ruleConfig.getWeatherEffect() != null) {
            JLabel weatherLabel = new JLabel(new ImageIcon(loadWeatherIcon(ruleConfig.getWeatherEffect())));
            weatherLabel.setToolTipText("Weather: " + ruleConfig.getWeatherEffect().name());
            JPanel weatherWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
            weatherWrapper.setOpaque(false);
            weatherWrapper.add(weatherLabel);
            topPanel.add(weatherWrapper, BorderLayout.WEST);
        }

        background.add(topPanel, BorderLayout.NORTH);

        Monster playerMonster = playerTeam.getActiveMonster();
        Monster enemyMonster = enemyTeam.getActiveMonster();

        JPanel playerPanel = createMonsterDisplayPanel(playerMonster, true, ruleConfig);
        JPanel enemyPanel = createMonsterDisplayPanel(enemyMonster, false, ruleConfig);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 200, 20));
        centerPanel.setOpaque(false);
        centerPanel.add(playerPanel);
        centerPanel.add(enemyPanel);
        background.add(centerPanel, BorderLayout.CENTER);

        playerWrapper = new JPanel();
        playerWrapper.setOpaque(false);
        playerWrapper.add(playerPanel);

        enemyWrapper = new JPanel();
        enemyWrapper.setOpaque(false);
        enemyWrapper.add(enemyPanel);

        centerPanel.add(playerWrapper);
        centerPanel.add(enemyWrapper);

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setPreferredSize(new Dimension(400, 0));
        logPanel.setOpaque(false);

        battleLog = new JTextArea();
        battleLog.setEditable(false);
        battleLog.setBackground(new Color(20, 0, 30));
        battleLog.setForeground(Color.WHITE);
        battleLog.setFont(new Font("Monospaced", Font.PLAIN, 14));
        battleLog.setLineWrap(true);
        battleLog.setWrapStyleWord(true);

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

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setOpaque(false);

        JButton basicAttackBtn = createStyledButton("ATTACK");
        JButton specialAttackBtn = createStyledButton("SPECIAL ATTACK");
        JButton switchBtn = createStyledButton("SWITCH MONSTER");
        JButton potionBtn = createStyledButton("USE POTION");

        basicAttackBtn.addActionListener(e -> {
            String log = game.handleBasicAttack();
            updateMonsterDisplay();
            battleLog.append(log + "\n");

            String enemyLog = game.handleEnemyTurn();
            updateMonsterDisplay();
            refreshEnemyPanel();
            battleLog.append(enemyLog + "\n");
        });

        specialAttackBtn.addActionListener(e -> {
            String log = game.handleSpecialAttack();
            updateMonsterDisplay();
            battleLog.append(log + "\n");

            String enemyLog = game.handleEnemyTurn();
            updateMonsterDisplay();
            refreshEnemyPanel();
            battleLog.append(enemyLog + "\n");
        });

        switchBtn.addActionListener(e -> handleMonsterSwitch());

        potionBtn.addActionListener(e -> handlePotionUse());
        String enemyLog = game.handleEnemyTurn();
        updateMonsterDisplay();
        refreshEnemyPanel();
        battleLog.append(enemyLog + "\n");

        buttonPanel.add(basicAttackBtn);
        buttonPanel.add(specialAttackBtn);
        buttonPanel.add(switchBtn);
        buttonPanel.add(potionBtn);

        background.add(buttonPanel, BorderLayout.PAGE_END);
        setContentPane(background);
        setVisible(true);
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
                BorderFactory.createEmptyBorder(10, 10, 10, 10)  // padding inside card
        ));

        // Monster name
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

        // Monster image
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
            if (healthUrl == null) {
                System.err.println("❌ Could not load health_potion.png");
            } else {
                ImageIcon icon = new ImageIcon(healthUrl);
                Image scaled = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                JLabel hpPotion = new JLabel(new ImageIcon(scaled));
                hpPotion.setToolTipText("Health Potion");
                potionPanel.add(hpPotion);
            }

            // Energy Potion
            URL energyUrl = getClass().getResource("/monstrum.assets/images/energy_potion.png");
            if (energyUrl == null) {
                System.err.println("❌ Could not load energy_potion.png");
            } else {
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
        playerHealthBar.setValue(player.getHealth());
        enemyHealthBar.setValue(enemy.getHealth());
        playerEnergyBar.setValue(player.getEnergy());
        enemyEnergyBar.setValue(enemy.getEnergy());
        playerHealthBar.setString(player.getHealth() + "/" + player.getMaxHealth());
        enemyHealthBar.setString(enemy.getHealth() + "/" + enemy.getMaxHealth());

        playerEnergyBar.setString(player.getEnergy() + "/" + player.getMaxEnergy());
        enemyEnergyBar.setString(enemy.getEnergy() + "/" + enemy.getMaxEnergy());

    }

    private void refreshEnemyPanel() {
        enemyWrapper.removeAll();
        JPanel newEnemyPanel = createMonsterDisplayPanel(enemyTeam.getActiveMonster(), false, ruleConfig);
        enemyWrapper.add(newEnemyPanel);
        enemyWrapper.revalidate();
        enemyWrapper.repaint();
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
            battleLog.append("⚠️ No other monsters available to switch!\n");
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
                    playerWrapper.removeAll();
                    JPanel newPlayerPanel = createMonsterDisplayPanel(playerTeam.getActiveMonster(), true, ruleConfig);
                    playerWrapper.add(newPlayerPanel);

                    playerWrapper.revalidate();
                    playerWrapper.repaint();

                    battleLog.append("🔄 Switched to " + selectedType + "\n");
                    updateMonsterDisplay();
                    break;
                }
            }
        }
    }

    private void handlePotionUse() {
        if (ruleConfig.isHardcoreMode()) {
            battleLog.append("❌ Potions are disabled in Hardcore Mode!\n");
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
            battleLog.append(log + "\n");
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
