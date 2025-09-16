package monstrum.game.UI;

import monstrum.model.*;
import monstrum.game.*;
import monstrum.model.monsters.*;
import monstrum.game.RuleConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class TeamSelectionUI extends JFrame {

    private JLabel counterLabel;
    private JButton continueButton;
    private int selectedCount = 0;
    private int maxTeamSize;
    private ArrayList<JToggleButton> selectedButtons = new ArrayList<>();
    private ArrayList<Monster> selectedMonsters = new ArrayList<>();
    private boolean isVsAI;
    private boolean isPlayerOneTurn;
    private Team player1Team;
    private Team player2Team;
    private RuleConfig ruleConfig;

    private Font loadCreepsterFont(float size) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/monstrum.assets/fonts/Creepster-Regular.ttf"));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font.deriveFont(size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("SansSerif", Font.BOLD, (int) size);
        }
    }

    public TeamSelectionUI(RuleConfig ruleConfig, boolean isPlayerOneTurn, Team team1, Team team2, int maxTeamSize) {
        this.ruleConfig = ruleConfig;
        this.isPlayerOneTurn = isPlayerOneTurn;
        this.player1Team = (team1 != null) ? team1 : new Team();
        this.player2Team = (team2 != null) ? team2 : new Team();
        this.maxTeamSize = maxTeamSize;

        setTitle("Team Selection");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        BackgroundPanel background = new BackgroundPanel("/monstrum.assets/images/mode_background.png");
        background.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("SELECT YOUR TEAM", SwingConstants.CENTER);
        titleLabel.setFont(loadCreepsterFont(60f));
        titleLabel.setForeground(new Color(200, 100, 200));
        background.add(titleLabel, BorderLayout.NORTH);

        JPanel cardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        cardPanel.setOpaque(false);

        cardPanel.add(createMonsterCard("Fire Monster", "Spirit born of flame, wielding the wrath of fire", "fire.png", new Color(255, 102, 0)));
        cardPanel.add(createMonsterCard("Water Monster", "Elemental of the deep, master of torrents", "water.png", new Color(0, 204, 255)));
        cardPanel.add(createMonsterCard("Thunder Monster", "Thunder-born guardian, unleashes lightning strikes", "thunder.png", new Color(244, 224, 77)));
        cardPanel.add(createMonsterCard("Dark Monster", "Shadow mage with the power to drain life", "dark.png", new Color(184, 78, 255)));
        cardPanel.add(createMonsterCard("Acid Monster", "Sneaky and venomous creature made of acid", "acid.png", new Color(127, 255, 0)));

        background.add(cardPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);

        counterLabel = new JLabel("Selected: 0 / " + maxTeamSize);
        counterLabel.setFont(loadCreepsterFont(28f));
        counterLabel.setForeground(new Color(200, 100, 200));
        counterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        continueButton = new JButton("CONTINUE");
        continueButton.setFont(new Font("SansSerif", Font.BOLD, 26));
        continueButton.setForeground(Color.WHITE);
        continueButton.setBackground(new Color(20, 0, 30));
        continueButton.setBorder(BorderFactory.createLineBorder(new Color(185, 84, 185), 3));
        continueButton.setFocusPainted(false);
        continueButton.setPreferredSize(new Dimension(260, 50));
        continueButton.setMaximumSize(new Dimension(260, 50));
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueButton.setEnabled(false);
        continueButton.addActionListener(e -> {
            for (JToggleButton button : selectedButtons) {
                String monsterName = button.getActionCommand();
                Monster m = switch (monsterName) {
                    case "Fire Monster" -> new FireMonster();
                    case "Water Monster" -> new WaterMonster();
                    case "Thunder Monster" -> new ThunderMonster();
                    case "Dark Monster" -> new DarkMonster();
                    case "Acid Monster" -> new AcidMonster();
                    default -> null;
                };
                if (m != null) selectedMonsters.add(m);
            }

            if (ruleConfig.getGameMode() == RuleConfig.GameMode.PLAYER_VS_PLAYER) {
                if (isPlayerOneTurn) {
                    for (Monster m : selectedMonsters) player1Team.addMonster(m, maxTeamSize);
                    dispose();
                    new TeamSelectionUI(ruleConfig, false, player1Team, player2Team, maxTeamSize);
                } else {
                    for (Monster m : selectedMonsters) player2Team.addMonster(m, maxTeamSize);
                    dispose();
                    new WeaponArenaUI(ruleConfig, player1Team, player2Team);
                }
            } else if (ruleConfig.getGameMode() == RuleConfig.GameMode.SURVIVAL) {
                for (Monster m : selectedMonsters) player1Team.addMonster(m, maxTeamSize);
                dispose();
                new WeaponArenaUI(ruleConfig, player1Team, null);
            } else {
                for (Monster m : selectedMonsters) player1Team.addMonster(m, maxTeamSize);
                dispose();
                new WeaponArenaUI(ruleConfig, player1Team, null);
            }
        });


        bottomPanel.add(counterLabel);
        bottomPanel.add(continueButton);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        background.add(bottomPanel, BorderLayout.SOUTH);
        add(background);
        setVisible(true);
    }

    private JPanel createMonsterCard(String name, String desc, String imgPath, Color nameColor) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(275, 480));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createLineBorder(new Color(120, 0, 160), 2));

        ImageIcon icon = new ImageIcon(getClass().getResource("/monstrum.assets/images/" + imgPath));
        Image scaled = icon.getImage().getScaledInstance(278, 380, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaled));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(loadCreepsterFont(30f));
        nameLabel.setForeground(nameColor);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea descArea = new JTextArea(desc);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setOpaque(false);
        descArea.setEditable(false);
        descArea.setFocusable(false);
        descArea.setForeground(Color.WHITE);
        descArea.setFont(new Font("SansSerif", Font.ITALIC, 10));
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        descArea.setMaximumSize(new Dimension(278, 50));

        JToggleButton selectButton = new JToggleButton("SELECT");
        selectButton.setForeground(Color.WHITE);
        selectButton.setBackground(new Color(20, 0, 30));
        selectButton.setBorder(BorderFactory.createLineBorder(new Color(185, 84, 185), 2));
        selectButton.setFocusPainted(false);
        selectButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        selectButton.setPreferredSize(new Dimension(260, 40));
        selectButton.setMaximumSize(new Dimension(260, 40));
        selectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectButton.setActionCommand(name);
        selectButton.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (selectedCount < maxTeamSize) {
                    selectedCount++;
                    selectedButtons.add(selectButton);
                } else {
                    selectButton.setSelected(false);
                    return;
                }
            } else {
                selectedCount--;
                selectedButtons.remove(selectButton);
            }
            counterLabel.setText("Selected: " + selectedCount + " / " + maxTeamSize);
            continueButton.setEnabled(selectedCount == maxTeamSize);
        });

        card.add(imageLabel);
        card.add(Box.createVerticalGlue());
        card.add(nameLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(descArea);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(selectButton);

        return card;
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

    public static void main(String[] args) {
        int maxTeamSize = 5;
        RuleConfig ruleConfig = new RuleConfig();
        new TeamSelectionUI(ruleConfig, true, new Team(), null, maxTeamSize);
    }
}
