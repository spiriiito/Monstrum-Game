package monstrum.game.UI;

import monstrum.game.Game;
import monstrum.game.RuleConfig;
import monstrum.game.Team;
import monstrum.game.TeamBuilder;
import monstrum.model.Monster;
import monstrum.model.Weapon;
import monstrum.model.equipment.WeaponFactory;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class WeaponEquipUI extends JFrame {

    private Monster selectedMonster;
    private Team team2;
    private Team team1;
    private final Border GLOW_BORDER = BorderFactory.createLineBorder(new Color(255, 105, 255), 4);
    private final Border DEFAULT_BORDER = BorderFactory.createLineBorder(new Color(120, 0, 160), 2);

    public WeaponEquipUI(List<Monster> team, String playerLabel, String selectedWeapon, RuleConfig ruleConfig, Team player1Team, Team player2Team, boolean isPlayerOne, Game game) {
        this.team2 = player2Team;
        setTitle("Equip Weapon to a Monster");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        BackgroundPanel background = new BackgroundPanel("/monstrum.assets/images/mode_background.png");
        background.setLayout(new BorderLayout());

        JLabel title = new JLabel("CHOOSE A MONSTER TO EQUIP THE WEAPON", SwingConstants.CENTER);
        title.setFont(loadCreepsterFont(50f));
        title.setForeground(new Color(200, 100, 200));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (playerLabel != null) {
            JLabel playerTag = new JLabel(playerLabel, SwingConstants.CENTER);
            playerTag.setFont(loadCreepsterFont(36f));
            playerTag.setForeground(new Color(255, 200, 255));
            playerTag.setAlignmentX(Component.CENTER_ALIGNMENT);
            topPanel.add(Box.createVerticalStrut(10));
            topPanel.add(playerTag);
            topPanel.add(Box.createVerticalStrut(5));
        } else {
            topPanel.add(Box.createVerticalStrut(15));
        }

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(title);

        JPanel topWrapper = new JPanel();
        topWrapper.setOpaque(false);
        topWrapper.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        topWrapper.add(topPanel);

        background.add(topWrapper, BorderLayout.NORTH);

        JPanel cardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        cardPanel.setOpaque(false);

        ButtonGroup group = new ButtonGroup();

        for (Monster m : team) {
            cardPanel.add(createMonsterCard(m, group));
        }

        background.add(cardPanel, BorderLayout.CENTER);

        JButton continueButton = new JButton("CONTINUE");
        continueButton.setFont(new Font("SansSerif", Font.BOLD, 26));
        continueButton.setForeground(Color.WHITE);
        continueButton.setBackground(new Color(20, 0, 30));
        continueButton.setBorder(BorderFactory.createLineBorder(new Color(185, 84, 185), 3));
        continueButton.setPreferredSize(new Dimension(260, 50));
        continueButton.setEnabled(false);

        continueButton.addActionListener(e -> {
            if (selectedMonster != null) {
                Weapon weapon = WeaponFactory.getWeaponByName(selectedWeapon);
                selectedMonster.equipWeapon(weapon);
                System.out.println("Equipped weapon (" + weapon.getName() + ") to: " + selectedMonster.getType());

                dispose();

                if (ruleConfig.getGameMode() == RuleConfig.GameMode.PLAYER_VS_AI) {
                    player1Team.addMonster(selectedMonster, ruleConfig.getMaxTeamSize());

                    Team enemyTeam = player2Team;
                    if (enemyTeam == null || enemyTeam.getMonsters().isEmpty()) {
                        enemyTeam = TeamBuilder.buildRandomTeam(ruleConfig.getMaxTeamSize(), ruleConfig);
                    }
                    for (Monster m : player1Team.getMonsters()) {
                        m.setRuleConfig(ruleConfig);
                    }
                    for (Monster m : enemyTeam.getMonsters()) {
                        m.setRuleConfig(ruleConfig);
                    }

                    Team finalEnemyTeam = enemyTeam;
                    Game battleGame = new Game(ruleConfig, player1Team, finalEnemyTeam);
                    SwingUtilities.invokeLater(() ->
                            new BattleUI(battleGame, ruleConfig, player1Team, finalEnemyTeam, false));
                }

                else if (!isPlayerOne && ruleConfig.getGameMode() == RuleConfig.GameMode.PLAYER_VS_PLAYER) {
                    player2Team.addMonster(selectedMonster, ruleConfig.getMaxTeamSize());

                    for (Monster m : player1Team.getMonsters()) {
                        m.setRuleConfig(ruleConfig);
                    }
                    for (Monster m : player2Team.getMonsters()) {
                        m.setRuleConfig(ruleConfig);
                    }

                    Game battleGame = new Game(ruleConfig, player1Team, player2Team);
                    SwingUtilities.invokeLater(() ->
                            new BattleUI(battleGame, ruleConfig, player1Team, player2Team, true));
                }
            }

        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(continueButton);
        background.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(background);
        setVisible(true);
    }

    private JPanel createMonsterCard(Monster monster, ButtonGroup group) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(275, 500));
        card.setMaximumSize(new Dimension(275, 500));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(DEFAULT_BORDER);

        JLabel imageLabel = new JLabel(loadImage(monster));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(monster.getType(), SwingConstants.CENTER);
        nameLabel.setFont(loadCreepsterFont(28f));
        switch (monster.getType()) {
            case "FireMonster" -> nameLabel.setForeground(new Color(255, 80, 0));
            case "WaterMonster" -> nameLabel.setForeground(new Color(100, 200, 255));
            case "ThunderMonster" -> nameLabel.setForeground(new Color(255, 230, 0));
            case "DarkMonster" -> nameLabel.setForeground(new Color(200, 100, 255));
            case "AcidMonster" -> nameLabel.setForeground(new Color(0, 255, 100));
            default -> nameLabel.setForeground(Color.WHITE);
        }
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JRadioButton selectRadio = new JRadioButton("EQUIP");
        selectRadio.setForeground(Color.WHITE);
        selectRadio.setBackground(new Color(20, 0, 30));
        selectRadio.setFont(new Font("SansSerif", Font.BOLD, 16));
        selectRadio.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectRadio.setPreferredSize(new Dimension(120, 40));
        group.add(selectRadio);

        selectRadio.addActionListener(e -> {
            selectedMonster = monster;

            Container parent = card.getParent();
            if (parent != null) {
                for (Component comp : parent.getComponents()) {
                    if (comp instanceof JPanel panel) {
                        panel.setBorder(DEFAULT_BORDER);
                    }
                }
            }
            card.setBorder(GLOW_BORDER);

            for (Component c : getContentPane().getComponents()) {
                if (c instanceof JPanel panel) {
                    for (Component b : panel.getComponents()) {
                        if (b instanceof JButton btn && btn.getText().equals("CONTINUE")) {
                            btn.setEnabled(true);
                        }
                    }
                }
            }
        });

        card.add(Box.createVerticalStrut(10));
        card.add(imageLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(nameLabel);
        card.add(Box.createVerticalGlue());
        card.add(selectRadio);
        card.add(Box.createVerticalStrut(15));

        return card;
    }

    private ImageIcon loadImage(Monster monster) {
        Map<String, String> monsterImageNames = Map.of(
                "FireMonster", "fire",
                "WaterMonster", "water",
                "ThunderMonster", "thunder",
                "DarkMonster", "dark",
                "AcidMonster", "acid"
        );

        String imageName = monsterImageNames.getOrDefault(monster.getType(), "default");
        String path = "/monstrum.assets/images/" + imageName + ".png";

        java.net.URL location = getClass().getResource(path);
        if (location == null) {
            System.err.println("❌ IMAGE NOT FOUND: " + path);
            return new ImageIcon();
        }

        ImageIcon icon = new ImageIcon(location);
        Image img = icon.getImage().getScaledInstance(250, 400, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private Font loadCreepsterFont(float size) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT,
                    getClass().getResourceAsStream("/monstrum.assets/fonts/Creepster-Regular.ttf"));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font.deriveFont(size);
        } catch (Exception e) {
            return new Font("SansSerif", Font.BOLD, (int) size);
        }
    }

    class BackgroundPanel extends JPanel {
        private final Image image;

        public BackgroundPanel(String path) {
            image = new ImageIcon(getClass().getResource(path)).getImage();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
