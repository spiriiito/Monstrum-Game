package monstrum.game.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameUI extends JFrame {

    private Font loadCustomFont() {
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/monstrum.assets/fonts/Creepster-Regular.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            return customFont.deriveFont(Font.PLAIN, 150f); // 72 size for bigger arcade look
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("SansSerif", Font.BOLD, 75); // fallback
        }
    }

    public GameUI() {
        setTitle("Monstrum - Main Menu");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Background panel instead of dark color
        BackgroundPanel background = new BackgroundPanel("/monstrum.assets/images/background_image.png");
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));

        // Title
        JLabel title = new JLabel("MONSTRUM", SwingConstants.CENTER);
        title.setFont(loadCustomFont());
        title.setForeground(new Color(88, 28, 140));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Play button
        JButton playButton = createStyledButton("▶ Play Game");

        // Exit button
        JButton exitButton = createStyledButton("❌ Quit");

        // Add ActionListeners
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new ModeSelectionUI();
            }
        });

        exitButton.addActionListener(e -> System.exit(0));

        // Add components
        background.add(Box.createRigidArea(new Dimension(0, 100)));
        background.add(title);
        background.add(Box.createRigidArea(new Dimension(0, 80)));
        background.add(playButton);
        background.add(Box.createRigidArea(new Dimension(0, 20)));
        background.add(exitButton);

        setContentPane(background);
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 22));
        button.setForeground(new Color(185, 84, 185)); // Pink font
        button.setBackground(Color.BLACK);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createLineBorder(new Color(185, 84, 185), 4)); // Pink border
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(250, 60));
        button.setMaximumSize(new Dimension(250, 60));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(148, 0, 211)); // Brighter purple on hover
                button.setBorder(BorderFactory.createLineBorder(new Color(255, 182, 193), 4)); // Lighter pink border
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(75, 0, 130)); // Dark purple back
                button.setBorder(BorderFactory.createLineBorder(new Color(255, 105, 180), 4)); // Pink border back
            }
        });

        return button;
    }

    public static void main(String[] args) {
        new GameUI();
    }

    // Custom background panel class
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
