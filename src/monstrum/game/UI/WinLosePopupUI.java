package monstrum.game.UI;

import monstrum.game.UI.GameUI;

import javax.swing.*;
import java.awt.*;

public class WinLosePopupUI extends JFrame {

    public WinLosePopupUI(String title, String message, boolean isSurvivalMode) {
        setTitle(title);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);

        String bgPath;
        if (isSurvivalMode) {
            bgPath = "/monstrum.assets/images/popup_background_win.png";
        } else if (title.toLowerCase().contains("win")) {
            bgPath = "/monstrum.assets/images/popup_background_win.png";
        } else {
            bgPath = "/monstrum.assets/images/popup_background_lose.png";
        }

        BackgroundPanel background = new BackgroundPanel(bgPath);
        background.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(title.toUpperCase(), SwingConstants.CENTER);
        titleLabel.setFont(loadCreepsterFont(50f));
        titleLabel.setForeground(new Color(230, 50, 230));

        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        messageLabel.setForeground(Color.WHITE);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));
        textPanel.add(titleLabel);
        textPanel.add(messageLabel);
        background.add(textPanel, BorderLayout.CENTER);

        JButton quitButton = new JButton("QUIT GAME");
        quitButton.setBackground(new Color(100, 0, 0));
        quitButton.setForeground(Color.WHITE);
        quitButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        quitButton.addActionListener(e -> System.exit(0));

        JButton restartButton = new JButton("RETURN TO MENU");
        restartButton.setBackground(new Color(30, 0, 70));
        restartButton.setForeground(Color.WHITE);
        restartButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        restartButton.addActionListener(e -> {
            dispose();
            new GameUI();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.add(restartButton);
        buttonPanel.add(quitButton);
        background.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(background);
        setVisible(true);
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
        private final Image bgImage;

        public BackgroundPanel(String path) {
            bgImage = new ImageIcon(getClass().getResource(path)).getImage();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
