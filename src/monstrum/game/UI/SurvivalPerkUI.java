package monstrum.game.UI;

import monstrum.game.Game;
import monstrum.game.RuleConfig;
import monstrum.game.Team;
import monstrum.model.Monster;
import monstrum.model.SurvivalPerk;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SurvivalPerkUI extends JFrame {
    private SurvivalPerk selectedPerk;
    private RuleConfig ruleConfig;
    private Team playerTeam;

    private final Map<SurvivalPerk, String> perkDescriptions = Map.of(
            SurvivalPerk.ADRENALINE_RUSH, "+5 Energy per turn",
            SurvivalPerk.IRON_WILL, "-5 damage from attacks",
            SurvivalPerk.VAMPIRIC_AURA, "Heal 5 HP on basic attack",
            SurvivalPerk.PHANTOM_REFLEX, "20% chance to dodge basic attacks",
            SurvivalPerk.ARCANE_MASTERY, "Special attack costs 10 less energy"
    );

    public SurvivalPerkUI(RuleConfig ruleConfig, Team playerTeam) {
        this.ruleConfig = ruleConfig;
        this.playerTeam = playerTeam;
        initUI();
    }

    private void initUI(){
        setTitle("Select Your Survival Perk");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        BackgroundPanel background = new BackgroundPanel("/monstrum.assets/images/mode_background.png");
        background.setLayout(new BorderLayout());

        JLabel title = new JLabel("CHOOSE YOUR SURVIVAL PERK", SwingConstants.CENTER);
        title.setFont(loadCreepsterFont(60f));
        title.setForeground(new Color(200, 100, 200));
        background.add(title, BorderLayout.NORTH);

        JPanel cardPanel = new JPanel();
        cardPanel.setOpaque(false);
        cardPanel.setLayout(new GridLayout(1, 5, 20, 0));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        ButtonGroup group = new ButtonGroup();
        for (SurvivalPerk perk : SurvivalPerk.values()) {
            cardPanel.add(createPerkCard(perk, group));
        }

        background.add(cardPanel, BorderLayout.CENTER);

        JButton continueButton = new JButton("CONTINUE");
        continueButton.setFont(new Font("SansSerif", Font.BOLD, 26));
        continueButton.setForeground(Color.WHITE);
        continueButton.setBackground(new Color(20, 0, 30));
        continueButton.setBorder(BorderFactory.createLineBorder(new Color(185, 84, 185), 3));
        continueButton.setPreferredSize(new Dimension(240, 50));
        continueButton.setEnabled(false);
        continueButton.addActionListener(e -> {
            Game game = new Game(ruleConfig, playerTeam, new Team());
            game.setSurvivalPerk(selectedPerk);

            Monster survivor = playerTeam.getActiveMonster();
            survivor.setRuleConfig(ruleConfig);
            survivor.setSurvivalPerk(selectedPerk);

            int bonusHP = (int)(survivor.getHealth() * 0.1);
            int bonusEnergy = (int)(survivor.getEnergy() * 0.1);
            survivor.setHealth(survivor.getHealth() + bonusHP);
            survivor.setEnergy(survivor.getEnergy() + bonusEnergy);

            game.generateEnemyTeamForWave(1);

            System.out.println("Selected perk being passed to game: " + selectedPerk);
            System.out.println("Confirm getSurvivalPerk: " + game.getSurvivalPerk());

            new BattleUI(game, ruleConfig, game.getPlayerTeam(), game.getEnemyTeam(), false);
            dispose();
        });


        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(continueButton);
        background.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(background);
        setVisible(true);
    }

    private JPanel createPerkCard(SurvivalPerk perk, ButtonGroup group) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(260, 420));
        card.setMaximumSize(new Dimension(260, 420));
        card.setMinimumSize(new Dimension(260, 420));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createLineBorder(new Color(160, 60, 200), 2));

        JLabel imageLabel = new JLabel();
        imageLabel.setIcon(loadImage(perk));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(perk.toString().replace("_", " "), SwingConstants.CENTER);
        nameLabel.setFont(loadCreepsterFont(24f));
        switch (perk) {
            case ADRENALINE_RUSH -> nameLabel.setForeground(Color.YELLOW);
            case IRON_WILL       -> nameLabel.setForeground(Color.ORANGE);
            case VAMPIRIC_AURA   -> nameLabel.setForeground(Color.RED);
            case PHANTOM_REFLEX  -> nameLabel.setForeground(new Color(135, 206, 250));
            case ARCANE_MASTERY  -> nameLabel.setForeground(new Color(185, 84, 255));
        }
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel(perkDescriptions.get(perk), SwingConstants.CENTER);
        descLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
        descLabel.setForeground(Color.WHITE);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton selectButton = new JButton("SELECT");
        selectButton.setForeground(Color.WHITE);
        selectButton.setBackground(new Color(20, 0, 30));
        selectButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        selectButton.setBorder(BorderFactory.createLineBorder(new Color(185, 84, 185), 2));
        selectButton.setPreferredSize(new Dimension(130, 40));
        selectButton.setMaximumSize(new Dimension(140, 45));
        selectButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        selectButton.addActionListener(e -> {
            selectedPerk = perk;
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
        card.add(Box.createVerticalStrut(5));
        card.add(descLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(selectButton);
        card.add(Box.createVerticalStrut(5));

        return card;
    }


    private ImageIcon loadImage(SurvivalPerk perk) {
        String fileName = switch (perk) {
            case ADRENALINE_RUSH -> "perk_adrenaline_rush";
            case IRON_WILL -> "perk_iron_will";
            case VAMPIRIC_AURA -> "perk_vampiric_aura";
            case PHANTOM_REFLEX -> "perk_phantom_reflex";
            case ARCANE_MASTERY -> "perk_arcane_mastery";
        };

        String path = "/monstrum.assets/images/" + fileName + ".png";
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
            Font font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/monstrum.assets/fonts/Creepster-Regular.ttf"));
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
