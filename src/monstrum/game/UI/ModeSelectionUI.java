package monstrum.game.UI;

import monstrum.game.GameFlowManager;
import monstrum.game.RuleConfig;
import monstrum.model.rules.WeatherEffect;

import javax.swing.*;
import java.awt.*;

public class ModeSelectionUI extends JFrame {

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

    public ModeSelectionUI() {
        setTitle("Choose Game Mode");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        BackgroundPanel backgroundPanel = new BackgroundPanel("/monstrum.assets/images/mode_background.png");
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("CHOOSE GAME MODE");
        titleLabel.setFont(loadCreepsterFont(56f));
        titleLabel.setForeground(new Color(159, 33, 159));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new GridLayout(1, 5, 30, 0));
        cardPanel.setOpaque(false);

        // PLAYER VS AI
        GameModeCard aiCard = new GameModeCard("AI_mode.png", "PLAYER VS AI", "<html><center>1v1 against an AI opponent</center></html> ", () -> {
            dispose();
            RuleConfig config = new RuleConfig();
            config.setGameMode(RuleConfig.GameMode.PLAYER_VS_AI);
            config.setPotionsEnabled(true);
            config.setWeaponsEnabled(true);
            GameFlowManager.startGameFlow(config);
        });

        // PLAYER VS PLAYER
        GameModeCard pvpCard = new GameModeCard("2player_mode.png", "PLAYER VS PLAYER", "<html><center>Challenge a friend in a strategic duel</center></html> ", () -> {
            dispose();
            RuleConfig config = new RuleConfig();
            config.setGameMode(RuleConfig.GameMode.PLAYER_VS_PLAYER);
            config.setPotionsEnabled(true);
            config.setWeaponsEnabled(true);
            GameFlowManager.startGameFlow(config);
        });

        // SURVIVAL MODE
        GameModeCard survivalCard = new GameModeCard("survival_mode.png", "SURVIVAL MODE", "Survive as long as possible facing endless waves of enemies ",
                () -> {
            dispose();
            RuleConfig config = new RuleConfig();
            config.setGameMode(RuleConfig.GameMode.SURVIVAL);
            config.setPotionsEnabled(true);
            config.setWeaponsEnabled(true);
            GameFlowManager.startGameFlow(config);
        });

        // RANDOM MODE
        GameModeCard randomCard = new GameModeCard("random_mode.png", "RANDOM MODE", "Every battle is different. Random teams, arenas and weather!", () -> {
            dispose();
            RuleConfig config = new RuleConfig();
            config.setGameMode(RuleConfig.GameMode.RANDOM);
            config.setPotionsEnabled(true);
            config.setWeaponsEnabled(true);
            config.setArenaType(RuleConfig.ArenaType.getRandomArena());
            GameFlowManager.startGameFlow(config);
        });

        // HARDCORE MODE
        GameModeCard hardcoreCard = new GameModeCard("hardcore_mode.png", "HARDCORE MODE", "Enemies hit harder. Potions are disabled. Only for the brave.",
                () -> {
            dispose();
            RuleConfig config = new RuleConfig();
            config.setGameMode(RuleConfig.GameMode.PLAYER_VS_AI);
            config.setHardcoreMode(true);
            config.setPotionsEnabled(false);
            config.setWeaponsEnabled(true);
            config.setWeatherEffect(WeatherEffect.getRandomWeather());
            GameFlowManager.startGameFlow(config);
        });

        cardPanel.add(aiCard);
        cardPanel.add(pvpCard);
        cardPanel.add(survivalCard);
        cardPanel.add(randomCard);
        cardPanel.add(hardcoreCard);

        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        backgroundPanel.add(titleLabel);
        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        backgroundPanel.add(cardPanel);

        setContentPane(backgroundPanel);
        setVisible(true);
    }

    private class BackgroundPanel extends JPanel {
        private final Image backgroundImage;

        public BackgroundPanel(String path) {
            backgroundImage = new ImageIcon(getClass().getResource(path)).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        new ModeSelectionUI();
    }
}
