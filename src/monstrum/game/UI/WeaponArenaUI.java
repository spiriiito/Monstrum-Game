package monstrum.game.UI;

import monstrum.game.Game;
import monstrum.game.RuleConfig;
import monstrum.game.Team;
import monstrum.model.equipment.WeaponFactory;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

public class WeaponArenaUI extends JFrame {

    private JLabel weaponImageLabel, weaponTitleLabel, weaponDescLabel;
    private JLabel arenaImageLabel, arenaTitleLabel, arenaDescLabel;

    private final Map<String, String> weaponDescriptions = Map.of(
            "Trident", "A sharp aquatic spear that pierces with deadly precision",
            "Hammer", "A heavy thunderous weapon that crushes enemies with force",
            "Shield", "A protective artifact that absorbs incoming damage"
    );

    private final Map<String, String> arenaDescriptions = Map.of(
            "Default", "Neutral battlefield with no elemental advantage",
            "Volcano", "Boosts Fire Monsters with increased HP",
            "Swamp", "Boosts Acid Monsters with stronger poison",
            "Dark Forest", "Boosts Dark Monsters with stronger lifesteal",
            "Ocean", "Boosts Water Monsters with bonus special damage",
            "Zeus Mountain", "Boosts Thunder Monsters with cheaper special attacks"
    );

    private Game game;
    public WeaponArenaUI() {
        this(null, null, null);
    }

    public WeaponArenaUI(RuleConfig ruleConfig, Team team1, Team team2) {
        setTitle("Select Weapon and Arena");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        BackgroundPanel background = new BackgroundPanel("/monstrum.assets/images/mode_background.png");
        background.setLayout(new BorderLayout());

        JLabel title = new JLabel("CHOOSE YOUR WEAPON & ARENA", SwingConstants.CENTER);
        title.setFont(loadCreepsterFont(60f));
        title.setForeground(new Color(200, 100, 200));
        background.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 10, 100));

        centerPanel.add(Box.createHorizontalGlue());
        centerPanel.add(createWeaponCard());
        centerPanel.add(Box.createRigidArea(new Dimension(60, 0)));
        centerPanel.add(createArenaCard());
        centerPanel.add(Box.createHorizontalGlue());

        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
        centerWrapper.setOpaque(false);
        centerWrapper.add(Box.createVerticalStrut(20));
        centerWrapper.add(centerPanel);
        centerWrapper.add(Box.createVerticalGlue());

        background.add(centerWrapper, BorderLayout.CENTER);

        JButton continueButton = new JButton("CONTINUE");
        continueButton.setFont(new Font("SansSerif", Font.BOLD, 26));
        continueButton.setForeground(Color.WHITE);
        continueButton.setBackground(new Color(20, 0, 30));
        continueButton.setBorder(BorderFactory.createLineBorder(new Color(185, 84, 185), 3));
        continueButton.setPreferredSize(new Dimension(240, 50));
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(continueButton);

        continueButton.addActionListener(e -> {
            String selectedWeapon = weaponTitleLabel.getText();
            String selectedArena = arenaTitleLabel.getText();

            ruleConfig.setSelectedWeapon(selectedWeapon);
            ruleConfig.setArenaType(RuleConfig.ArenaType.valueOf(selectedArena.toUpperCase().replace(" ", "_")));

            game = new Game(ruleConfig, team1, team2 != null ? team2 : new Team());

            dispose();

            if (ruleConfig.getGameMode() == RuleConfig.GameMode.SURVIVAL) {
                new SurvivalPerkUI(ruleConfig, team1);
            } else if (ruleConfig.getGameMode() == RuleConfig.GameMode.PLAYER_VS_PLAYER) {
                if (team2 == null || team2.getMonsters().isEmpty()) {
                    new WeaponArenaUI(ruleConfig, team1, new Team());
                } else {
                    new WeaponEquipUI(team1.getMonsters(), "PLAYER 1", ruleConfig.getSelectedWeapon(), ruleConfig, team1, team2, true, game) {
                        @Override
                        public void dispose() {
                            super.dispose();
                            SwingUtilities.invokeLater(() ->
                                    new WeaponEquipUI(team2.getMonsters(), "PLAYER 2", ruleConfig.getSelectedWeapon(), ruleConfig, team1, team2, false, game)
                            );
                        }
                    };
                }
            } else if (ruleConfig.getGameMode() == RuleConfig.GameMode.PLAYER_VS_AI) {
                new WeaponEquipUI(team1.getMonsters(), null, ruleConfig.getSelectedWeapon(), ruleConfig, team1, null, true, game);
            }
        });

        background.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(background);
        setVisible(true);
    }

    private JPanel createWeaponCard() {
        JPanel card = createStyledCard();

        weaponTitleLabel = new JLabel("Trident", SwingConstants.CENTER);
        weaponTitleLabel.setFont(loadCreepsterFont(30f));
        weaponTitleLabel.setForeground(new Color(155, 200, 255));
        weaponTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        weaponImageLabel = new JLabel();
        weaponImageLabel.setIcon(loadImage("trident.png"));
        weaponImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        weaponImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        weaponDescLabel = new JLabel("" + weaponDescriptions.get("Trident"), SwingConstants.CENTER);
        weaponDescLabel.setForeground(Color.WHITE);
        weaponDescLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
        weaponDescLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JComboBox<String> weaponDropdown = createStyledDropdown(new String[]{"Trident", "Hammer", "Shield"});
        weaponDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel dropdownContainer = new JPanel();
        dropdownContainer.setOpaque(false);
        dropdownContainer.setLayout(new BoxLayout(dropdownContainer, BoxLayout.X_AXIS));
        dropdownContainer.add(Box.createHorizontalGlue());
        dropdownContainer.add(weaponDropdown);
        dropdownContainer.add(Box.createHorizontalGlue());

        weaponDropdown.addActionListener(e -> {
            String selected = (String) weaponDropdown.getSelectedItem();
            weaponTitleLabel.setText(selected);
            weaponImageLabel.setIcon(loadImage(selected.toLowerCase() + ".png"));
            weaponDescLabel.setText(weaponDescriptions.getOrDefault(selected, ""));

        });

        card.add(Box.createVerticalGlue());
        card.add(weaponTitleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(weaponImageLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(weaponDescLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(weaponDropdown);
        card.add(Box.createVerticalStrut(10));
        card.add(dropdownContainer);
        card.add(Box.createVerticalGlue());
        return card;
    }

    private JPanel createArenaCard() {
        JPanel card = createStyledCard();

        arenaTitleLabel = new JLabel("Default", SwingConstants.CENTER);
        arenaTitleLabel.setFont(loadCreepsterFont(30f));
        arenaTitleLabel.setForeground(new Color(255, 220, 180));
        arenaTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        arenaImageLabel = new JLabel();
        arenaImageLabel.setIcon(loadImage("arena_default.png"));
        arenaImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        arenaImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        arenaDescLabel = new JLabel("" + arenaDescriptions.get("Default"), SwingConstants.CENTER);
        arenaDescLabel.setForeground(Color.WHITE);
        arenaDescLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
        arenaDescLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JComboBox<String> arenaDropdown = createStyledDropdown(new String[]{
                "Default", "Volcano", "Swamp", "Dark Forest", "Ocean", "Zeus Mountain"
        });
        arenaDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel dropdownContainer = new JPanel();
        dropdownContainer.setOpaque(false);
        dropdownContainer.setLayout(new BoxLayout(dropdownContainer, BoxLayout.X_AXIS));
        dropdownContainer.add(Box.createHorizontalGlue());
        dropdownContainer.add(arenaDropdown);
        dropdownContainer.add(Box.createHorizontalGlue());

        arenaDropdown.addActionListener(e -> {
            String selected = (String) arenaDropdown.getSelectedItem();
            arenaTitleLabel.setText(selected);
            arenaImageLabel.setIcon(loadImage("arena_" + selected.toLowerCase().replace(" ", "_") + ".png"));
            arenaDescLabel.setText(arenaDescriptions.getOrDefault(selected, ""));
        });

        card.add(Box.createVerticalGlue());
        card.add(arenaTitleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(arenaImageLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(arenaDescLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(arenaDropdown);
        card.add(Box.createVerticalStrut(10));
        card.add(dropdownContainer);
        card.add(Box.createVerticalGlue());
        return card;
    }

    private JPanel createStyledCard() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        Dimension cardSize = new Dimension(400, 600);
        panel.setPreferredSize(cardSize);
        panel.setMinimumSize(cardSize);

        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(160, 60, 200), 3, 30),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        panel.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        return panel;
    }

    private JComboBox<String> createStyledDropdown(String[] items) {
        JComboBox<String> dropdown = new JComboBox<>(items);
        dropdown.setMaximumSize(new Dimension(220, 35));
        dropdown.setForeground(Color.WHITE);
        dropdown.setBackground(new Color(30, 0, 50));
        dropdown.setFont(new Font("SansSerif", Font.BOLD, 14));
        dropdown.setBorder(BorderFactory.createLineBorder(new Color(185, 84, 185), 2));
        return dropdown;
    }

    private ImageIcon loadImage(String filename) {
        String path = "/monstrum.assets/images/" + filename;
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image img = icon.getImage().getScaledInstance(340, 440, Image.SCALE_SMOOTH);
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

    public static void main(String[] args) {
        new WeaponArenaUI();
    }
}

class RoundedBorder implements Border {
    private final Color color;
    private final int thickness;
    private final int arc;

    public RoundedBorder(Color color, int thickness, int arc) {
        this.color = color;
        this.thickness = thickness;
        this.arc = arc;
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(this.thickness+1, this.thickness+1, this.thickness+1, this.thickness+1);
    }

    public boolean isBorderOpaque() {
        return false;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(thickness));
        g2d.drawRoundRect(x + thickness/2, y + thickness/2, width - thickness, height - thickness, arc, arc);
    }
}

