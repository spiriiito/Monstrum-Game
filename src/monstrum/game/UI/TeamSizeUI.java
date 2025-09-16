package monstrum.game.UI;

import monstrum.game.GameFlowManager;
import monstrum.game.RuleConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TeamSizeUI extends JFrame {

    private Font creepsterFont;
    private int selectedSize = -1;
    private JButton continueButton;

    public TeamSizeUI(RuleConfig ruleConfig) {
        setTitle("Select Team Size");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        loadCreepsterFont();

        BackgroundPanel background = new BackgroundPanel("/monstrum.assets/images/team_size_background.png");
        background.setLayout(new BorderLayout());

        JLabel title = new JLabel("CHOOSE TEAM SIZE", SwingConstants.CENTER);
        title.setFont(creepsterFont.deriveFont(70f));
        title.setForeground(new Color(180, 50, 220));
        background.add(title, BorderLayout.NORTH);

        JPanel sizePanelContainer = new JPanel();
        sizePanelContainer.setOpaque(false);
        sizePanelContainer.setLayout(new BoxLayout(sizePanelContainer, BoxLayout.Y_AXIS));

        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 40));
        sizePanel.setOpaque(false);

        JButton[] sizeButtons = new JButton[5];

        for (int i = 1; i <= 5; i++) {
            int size = i;
            JButton sizeBtn = new JButton(String.valueOf(size));
            sizeBtn.setFont(new Font("SansSerif", Font.BOLD, 36));
            sizeBtn.setBackground(new Color(60, 0, 80));
            sizeBtn.setForeground(Color.WHITE);
            sizeBtn.setFocusPainted(false);
            sizeBtn.setPreferredSize(new Dimension(120, 120));

            sizeBtn.addActionListener((ActionEvent e) -> {
                selectedSize = size;
                continueButton.setEnabled(true);
                for (JButton btn : sizeButtons) {
                    btn.setBackground(new Color(60, 0, 80));
                }
                sizeBtn.setBackground(new Color(120, 0, 180));
            });

            sizeButtons[i - 1] = sizeBtn;
            sizePanel.add(sizeBtn);
        }

        sizePanelContainer.add(Box.createVerticalGlue());
        sizePanelContainer.add(sizePanel);
        sizePanelContainer.add(Box.createVerticalGlue());
        background.add(sizePanelContainer, BorderLayout.CENTER);

        continueButton = new JButton("CONTINUE");
        continueButton.setFont(new Font("SansSerif", Font.BOLD, 24));
        continueButton.setBackground(new Color(150, 0, 180));
        continueButton.setForeground(Color.WHITE);
        continueButton.setFocusPainted(false);
        continueButton.setEnabled(false);
        continueButton.setPreferredSize(new Dimension(220, 60));

        continueButton.addActionListener((ActionEvent e) -> {
            ruleConfig.setMaxTeamSize(selectedSize);
            dispose();
            GameFlowManager.handleTeamSizeSelected(ruleConfig);
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 80, 0));
        bottomPanel.add(continueButton);
        background.add(bottomPanel, BorderLayout.PAGE_END);

        setContentPane(background);
        setVisible(true);
    }

    private void loadCreepsterFont() {
        try {
            creepsterFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/monstrum.assets/fonts/Creepster-Regular.ttf"));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(creepsterFont);
        } catch (Exception e) {
            creepsterFont = new Font("SansSerif", Font.BOLD, 48);
        }
    }

    static class BackgroundPanel extends JPanel {
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