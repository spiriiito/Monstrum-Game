package monstrum.game.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameModeCard extends JPanel {
    public GameModeCard(String imageFileName, String title, String description, Runnable onClick) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setPreferredSize(new Dimension(275, 480));

        String path = "/monstrum.assets/images/" + imageFileName;
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image scaledImage = icon.getImage().getScaledInstance(275, 400, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title + Description Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(50, 0, 80, 220));
        titlePanel.setBorder(BorderFactory.createLineBorder(new Color(150, 80, 200), 4));
        titlePanel.setMaximumSize(new Dimension(275, 80));
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Creepster", Font.PLAIN, 22));
        titleLabel.setForeground(new Color(150, 80, 200));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(titleLabel);

        if (description != null && !description.isEmpty()) {
            JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
            descLabel.setHorizontalAlignment(SwingConstants.CENTER);
            descLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
            descLabel.setForeground(Color.WHITE);
            descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titlePanel.add(Box.createVerticalStrut(2));
            titlePanel.add(descLabel);
        }

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
        });

        add(imageLabel);
        add(titlePanel);
    }
}
