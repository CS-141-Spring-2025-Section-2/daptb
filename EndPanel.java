package daptb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class EndPanel extends JFrame implements ActionListener {

    private String selectedCharacter = "";
    private JLabel titleLabel, selectedLabel, descriptionLabel, characterImageLabel;
    private JButton confirmButton, resetButton;
    private Map<JButton, CharacterData> characterMap;
    private JLabel backgroundLabel;

    public EndPanel() {
        setTitle("Select Your Character");
        setSize(1000, 800); // Larger initial size for better visibility
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        setBackground();
        initializeCharacters();

        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        // Make the window resizable and ensure components scale properly
        setResizable(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                scaleBackground();
                scaleCharacterButtons();
                scaleCharacterImage();
            }
        });

        setVisible(true);
    }

    private void setBackground() {
        // Load the background image from the daptb package
        ImageIcon backgroundIcon = loadImage("deyplay.jpg");
        backgroundLabel = new JLabel();
        if (backgroundIcon.getImage() == null) {
            System.err.println("Error: Background image not found. Using default background.");
            backgroundLabel.setBackground(new Color(30, 30, 30)); // Dark gray fallback
            backgroundLabel.setOpaque(true);
        } else {
            backgroundLabel.setIcon(backgroundIcon);
        }
        backgroundLabel.setLayout(new BorderLayout());
        setContentPane(backgroundLabel);
    }

    private void scaleBackground() {
        if (backgroundLabel.getIcon() != null) {
            ImageIcon backgroundIcon = (ImageIcon) backgroundLabel.getIcon();
            Image scaledImage = backgroundIcon.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
            backgroundLabel.setIcon(new ImageIcon(scaledImage));
        }
    }

    private void scaleCharacterButtons() {
        for (JButton button : characterMap.keySet()) {
            CharacterData data = characterMap.get(button);
            int buttonSize = Math.min(getWidth() / 6, getHeight() / 4); // Dynamic button size
            button.setIcon(scaleImageIcon(data.icon, buttonSize, buttonSize));
        }
    }

    private void scaleCharacterImage() {
        if (characterImageLabel.getIcon() != null) {
            int imageWidth = getWidth() / 3; // Dynamic image width
            int imageHeight = getHeight() / 2; // Dynamic image height
            characterImageLabel.setIcon(scaleImageIcon((ImageIcon) characterImageLabel.getIcon(), imageWidth, imageHeight));
        }
    }

    private void initializeCharacters() {
        characterMap = new HashMap<>();

        String[] names = {"Warrior", "Mage", "Rogue", "Assassin", "Druid", "Paladin"};
        String[] imageFiles = {"warrior.jpeg", "mage.jpeg", "rogue.jpeg", "assassin.jpeg", "druid.jpeg", "paladin.jpeg"};
        String[] descriptions = {
            "A strong melee fighter with high defense.",
            "Master of the arcane arts, uses spells for damage.",
            "A stealthy and agile character known for speed and precision.",
            "A deadly and elusive fighter, skilled in swift, silent attacks.",
            "A guardian of nature, wielding powerful magic to heal allies.",
            "A holy warrior, devoted to justice and protection."
        };

        for (int i = 0; i < names.length; i++) {
            JButton button = createCharacterButton(names[i], imageFiles[i]);
            characterMap.put(button, new CharacterData(names[i], loadImage(imageFiles[i]), descriptions[i]));
        }
    }

    private JButton createCharacterButton(String name, String imageFile) {
        JButton button = new JButton(name);
        ImageIcon icon = loadImage(imageFile);
        if (icon.getImage() == null) {
            System.err.println("Error: Image not found for " + name + ". Using placeholder.");
            button.setBackground(new Color(50, 50, 50)); // Dark background for placeholder
            button.setForeground(Color.WHITE);
        } else {
            button.setIcon(scaleImageIcon(icon, 100, 100)); // Initial scaled icon
        }
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.addActionListener(this);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(Color.YELLOW);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(Color.WHITE);
            }
        });
        return button;
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        titleLabel = new JLabel("Select Your Character", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        selectedLabel = new JLabel("Selected: None", SwingConstants.CENTER);
        selectedLabel.setFont(new Font("Arial", Font.BOLD, 20));
        selectedLabel.setForeground(Color.WHITE);
        panel.add(selectedLabel);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        characterImageLabel = new JLabel();
        characterImageLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(characterImageLabel, BorderLayout.CENTER);

        descriptionLabel = new JLabel("Select a character to see details.", SwingConstants.LEFT);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        descriptionLabel.setForeground(Color.WHITE);
        panel.add(descriptionLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 10)); // Grid layout for character buttons
        panel.setOpaque(false);

        for (JButton button : characterMap.keySet()) {
            panel.add(button);
        }

        confirmButton = new JButton("Confirm");
        resetButton = new JButton("Reset");
        confirmButton.setEnabled(false);

        confirmButton.addActionListener(this);
        resetButton.addActionListener(this);

        styleButton(confirmButton);
        styleButton(resetButton);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(confirmButton);
        buttonPanel.add(resetButton);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(panel, BorderLayout.CENTER);
        wrapperPanel.add(buttonPanel, BorderLayout.SOUTH);

        return wrapperPanel;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(59, 89, 182));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(89, 119, 212));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(59, 89, 182));
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (characterMap.containsKey(e.getSource())) {
            CharacterData selected = characterMap.get(e.getSource());
            selectedCharacter = selected.name;
            selectedLabel.setText("Selected: " + selectedCharacter);
            descriptionLabel.setText("<html><div style='width: 350px;'>" + selected.description + "</div></html>");
            characterImageLabel.setIcon(scaleImageIcon(selected.icon, getWidth() / 3, getHeight() / 2)); // Dynamic image size
            confirmButton.setEnabled(true);
        } else if (e.getSource() == confirmButton) {
            JOptionPane.showMessageDialog(this, "You have confirmed: " + selectedCharacter,
                    "Character Confirmed", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == resetButton) {
            selectedCharacter = "";
            selectedLabel.setText("Selected: None");
            descriptionLabel.setText("Select a character to see details.");
            characterImageLabel.setIcon(null);
            confirmButton.setEnabled(false);
        }
    }

    private ImageIcon scaleImageIcon(ImageIcon icon, int width, int height) {
        if (icon == null || icon.getImage() == null) return new ImageIcon();
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    private ImageIcon loadImage(String fileName) {
        // Load images from the daptb package
        java.net.URL imgUrl = getClass().getResource("/daptb/" + fileName);
        if (imgUrl == null) {
            System.err.println("Error: Image not found - " + fileName);
            return new ImageIcon(); // Return an empty ImageIcon if the image is not found
        }
        return new ImageIcon(imgUrl);
    }

    class CharacterData {
        String name;
        ImageIcon icon;
        String description;

        CharacterData(String name, ImageIcon icon, String description) {
            this.name = name;
            this.icon = icon;
            this.description = description;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EndPanel::new);
    }
}
