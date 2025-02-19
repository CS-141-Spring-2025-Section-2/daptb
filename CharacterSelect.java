package dapt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class CharacterSelect extends JFrame implements ActionListener {

    private String selectedCharacter = "";
    private JLabel titleLabel, selectedLabel, descriptionLabel, characterImageLabel;
    private JButton confirmButton, resetButton;
    private Map<JButton, CharacterData> characterMap;
    private JPanel characterPanel;

    public CharacterSelect() {
        setTitle("Select Your Character");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(30, 30, 30)); // Set background color

        initializeCharacters();

        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void initializeCharacters() {
        characterMap = new HashMap<>();

        String[] names = {"Warrior", "Mage", "Rogue", "Assassin", "Druid", "Paladin"};
        String[] imageFiles = {"warrior.jpeg", "mage.jpeg", "rogue.jpeg", "assassin.jpeg", "druid.jpeg", "paladin.jpeg"};
        String[] descriptions = {
            "A strong melee fighter with high defense.",
            "Master of the arcane arts, uses spells for damage.",
            "A stealthy and agile character known for speed and precision. Excels in ambush tactics and evasive maneuvers, striking swiftly from the shadows.",
            "A deadly and elusive fighter, skilled in swift, silent attacks and assassination techniques.",
            "A guardian of nature, wielding powerful magic to heal allies and control the elements.",
            "A holy warrior, devoted to justice and protection, armed with divine magic and strong melee capabilities."
        };

        for (int i = 0; i < names.length; i++) {
            JButton button = new JButton();
            ImageIcon icon = loadImage(imageFiles[i]);
            button.setIcon(scaleImageIcon(icon, 80, 80));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.addActionListener(this);
            characterMap.put(button, new CharacterData(names[i], icon, descriptions[i]));
        }
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        titleLabel = new JLabel("Select Your Character", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        selectedLabel = new JLabel("Selected: None", SwingConstants.CENTER);
        selectedLabel.setFont(new Font("Arial", Font.BOLD, 18));
        selectedLabel.setForeground(Color.WHITE);
        panel.add(selectedLabel);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        characterImageLabel = new JLabel();
        characterImageLabel.setPreferredSize(new Dimension(350, 400));
        characterImageLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(characterImageLabel, BorderLayout.WEST);

        descriptionLabel = new JLabel("Select a character to see details.", SwingConstants.LEFT);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        descriptionLabel.setForeground(Color.WHITE);
        panel.add(descriptionLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setOpaque(false);

        for (JButton button : characterMap.keySet()) {
            panel.add(button);
        }

        confirmButton = new JButton("Confirm");
        resetButton = new JButton("Reset");
        confirmButton.setEnabled(false);

        confirmButton.addActionListener(this);
        resetButton.addActionListener(this);

        panel.add(confirmButton);
        panel.add(resetButton);
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (characterMap.containsKey(e.getSource())) {
            CharacterData selected = characterMap.get(e.getSource());
            selectedCharacter = selected.name;
            selectedLabel.setText("Selected: " + selectedCharacter);
            descriptionLabel.setText("" + selected.description);
            characterImageLabel.setIcon(scaleImageIcon(selected.icon, 300, 400));
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
        java.net.URL imgUrl = getClass().getResource("/resources/" + fileName);
        if (imgUrl == null) {
            System.err.println("Error: Image not found - " + fileName);
            return new ImageIcon();
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
        SwingUtilities.invokeLater(CharacterSelect::new);
    }
}
