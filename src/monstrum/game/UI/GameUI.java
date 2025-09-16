package monstrum.game.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import monstrum.utils.MusicPlayer;

public class GameUI extends JFrame {

    private Font loadCustomFont() {
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/monstrum.assets/fonts/Creepster-Regular.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
            return customFont.deriveFont(Font.PLAIN, 150f);
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("SansSerif", Font.BOLD, 75);
        }
    }

    public GameUI() {
        MusicPlayer.play("/monstrum.assets/audio/main_music.wav", true);
        setTitle("Monstrum - Main Menu");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        BackgroundPanel background = new BackgroundPanel("/monstrum.assets/images/background_image.png");
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("MONSTRUM", SwingConstants.CENTER);
        title.setFont(loadCustomFont());
        title.setForeground(new Color(88, 28, 140));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton playButton = createStyledButton("▶ Play Game");

        JButton exitButton = createStyledButton("❌ Quit");

        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new ModeSelectionUI();
            }
        });

        exitButton.addActionListener(e -> System.exit(0));

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
        button.setForeground(new Color(185, 84, 185));
        button.setBackground(Color.BLACK);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createLineBorder(new Color(185, 84, 185), 4));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(250, 60));
        button.setMaximumSize(new Dimension(250, 60));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(148, 0, 211));
                button.setBorder(BorderFactory.createLineBorder(new Color(255, 182, 193), 4));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(75, 0, 130));
                button.setBorder(BorderFactory.createLineBorder(new Color(255, 105, 180), 4));
            }
        });

        return button;
    }

    public static void main(String[] args) {
        new GameUI();
    }

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
