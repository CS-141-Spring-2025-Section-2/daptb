package daptb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

public class GameClass extends JFrame {
    private static final long serialVersionUID = 1L;
    private BufferedImage backgroundImage;

    // Paths for background images
    private String basePath = "/Users/dayspringidahosa/eclipse-workspace/_pasted_code_/src/daptb/";
    private String[] backgroundImages = {
        basePath + "2.jpg",
        basePath + "3.jpg",
        basePath + "4.jpg",
        basePath + "5.jpg"
    };

    private int backgroundIndex = 0;
    private Clip backgroundMusicClip;
    private Clip clickSoundClip;

    private JLabel titleLabel;
    private JButton newGameButton;
    private JButton selectCharacterButton;
    private JButton continueButton;

    // The main menu panel which holds the title and buttons
    private JPanel mainMenuPanel;

    private final int INITIAL_WIDTH = 800;
    private final int INITIAL_HEIGHT = 600;

    public GameClass() {
        setTitle("Travel and Conquer");
        setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load resources
        loadBackgroundImage();
        loadBackgroundMusic(basePath + "background_music.wav");
        loadClickSound(basePath + "click_sound.wav");

        // Create the main menu panel (a custom background panel)
        mainMenuPanel = new BackgroundPanel();
        mainMenuPanel.setLayout(null);
        setContentPane(mainMenuPanel);

        // Create and add the title label
        titleLabel = createArtisticTitle("Travel and Conquer");
        titleLabel.setBounds(50, 20, 700, 100);
        mainMenuPanel.add(titleLabel);

        // Create buttons with updated colors for better contrast on an orange background
        newGameButton = createButton("New Game", 300, 150, new Color(70, 130, 180)); // Steel Blue
        selectCharacterButton = createButton("Select Character", 300, 250, new Color(138, 43, 226)); // Blue Violet
        continueButton = createButton("Continue", 300, 350, new Color(220, 20, 60)); // Crimson

        // Add action listeners
        newGameButton.addActionListener(e -> startNewGame());
        selectCharacterButton.addActionListener(e -> openCharacterSelection());
        continueButton.addActionListener(e -> continueGame());

        // Add buttons to the main menu panel
        mainMenuPanel.add(newGameButton);
        mainMenuPanel.add(selectCharacterButton);
        mainMenuPanel.add(continueButton);

        // Add a component listener to handle window resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });

        // Play background music
        playBackgroundMusic();

        // Add a mouse listener for the click sound
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playClickSound();
            }
        });
    }

    // Custom background panel that draws the background image
    class BackgroundPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    private JLabel createArtisticTitle(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 48));
        label.setForeground(new Color(255, 215, 0)); // Gold color
        label.setOpaque(false);
        label.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(184, 134, 11))); // Gold border
        return label;
    }

    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(new File(backgroundImages[backgroundIndex]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBackgroundMusic(String filePath) {
        try {
            File musicPath = new File(filePath);
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                backgroundMusicClip = AudioSystem.getClip();
                backgroundMusicClip.open(audioInput);
                backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                System.out.println("Background music file not found: " + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playBackgroundMusic() {
        if (backgroundMusicClip != null) {
            backgroundMusicClip.start();
        }
    }

    private void loadClickSound(String filePath) {
        try {
            File soundPath = new File(filePath);
            if (soundPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundPath);
                clickSoundClip = AudioSystem.getClip();
                clickSoundClip.open(audioInput);
            } else {
                System.out.println("Click sound file not found: " + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playClickSound() {
        if (clickSoundClip != null) {
            clickSoundClip.setFramePosition(0);
            clickSoundClip.start();
        }
    }

    private JButton createButton(String text, int x, int y, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Times New Roman", Font.BOLD, 20));
        button.setBounds(x, y, 200, 50);
        button.setFocusPainted(false);
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createLineBorder(new Color(184, 134, 11), 3));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Instead of disposing the current frame, replace the content pane with the instructions panel
    private void startNewGame() {
        setContentPane(new InstructionsPanel());
        revalidate();
        repaint();
    }

    // Replace the content pane with the character selection panel
    private void openCharacterSelection() {
        setContentPane(new CharacterSelect());
        revalidate();
        repaint();
    }

    private void continueGame() {
        JOptionPane.showMessageDialog(this, "Continuing from last save!");
    }

    private void resizeComponents() {
        int width = getWidth();
        int height = getHeight();

        // Calculate scaling ratios
        double widthRatio = (double) width / INITIAL_WIDTH;
        double heightRatio = (double) height / INITIAL_HEIGHT;

        // Ensure the ratios don't go below a minimum threshold
        widthRatio = Math.max(widthRatio, 0.5);
        heightRatio = Math.max(heightRatio, 0.5);

        // Resize and reposition the title label
        titleLabel.setFont(titleLabel.getFont().deriveFont((float) (48 * heightRatio)));
        titleLabel.setBounds(
            (int) (50 * widthRatio), 
            (int) (20 * heightRatio), 
            (int) (700 * widthRatio), 
            (int) (100 * heightRatio)
        );

        // Resize and reposition the buttons
        resizeButton(newGameButton, 300, 150, widthRatio, heightRatio, width, height);
        resizeButton(selectCharacterButton, 300, 250, widthRatio, heightRatio, width, height);
        resizeButton(continueButton, 300, 350, widthRatio, heightRatio, width, height);

        repaint();
    }

    private void resizeButton(JButton button, int initialX, int initialY, double widthRatio, double heightRatio, int windowWidth, int windowHeight) {
        int newX = (int) (initialX * widthRatio);
        int newY = (int) (initialY * heightRatio);
        int newWidth = (int) (200 * widthRatio);
        int newHeight = (int) (50 * heightRatio);

        if (newX + newWidth > windowWidth) {
            newX = windowWidth - newWidth;
        }
        if (newY + newHeight > windowHeight) {
            newY = windowHeight - newHeight;
        }

        button.setBounds(newX, newY, newWidth, newHeight);
        button.setFont(button.getFont().deriveFont((float) (20 * heightRatio)));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameClass menu = new GameClass();
            menu.setVisible(true);
        });
    }

    // Inner class for InstructionsPanel.
    // At the end of the instructions, the "Continue" button will restore the main menu.
    class InstructionsPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private JLabel instructionLabel;
        private JButton nextButton;
        private int textIndex = 0;
        private String displayedText = "";
        private Timer typingTimer;
        private int charIndex = 0;

        public InstructionsPanel() {
            setLayout(null);
            setBackground(Color.BLACK);

            instructionLabel = new JLabel("", SwingConstants.CENTER);
            instructionLabel.setFont(new Font("Serif", Font.BOLD, 24));
            instructionLabel.setForeground(Color.WHITE);
            instructionLabel.setBounds(100, 100, 600, 150);
            add(instructionLabel);

            nextButton = new JButton("Continue →");
            nextButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
            nextButton.setBounds(300, 400, 200, 50);
            nextButton.setEnabled(false);
            nextButton.addActionListener(e -> showNextInstruction());
            add(nextButton);

            startTypingAnimation();
        }

        private final String[] instructionTexts = {
            "🚀 Welcome to Travel and Conquer! \n\n Travel through different time periods, defeat enemies, and survive each battle to claim victory!",
            "🎮 Controls:\n - Move: Arrow keys (↑ ↓ ← →)\n - Shoot: Spacebar (aim with mouse)\n - Use Bombs: G (eliminates the three closest enemies, but you only have one bomb per level)",
            "⚠️ Game Elements:\n - Stay mobile to avoid attacks.\n - Use your **one bomb per level** wisely—it eliminates the three closest enemies but does not replenish.\n - **Don't get boxed in** by enemies! If trapped, your only escape may be using your bomb.\n - Aim accurately and keep an eye on your health bar.",
            "🏆 Levels:\n 1️⃣ **Ancient Egypt** – Fight Pharaohs in the desert.\n 2️⃣ **Medieval Europe** – Battle Knights in a castle setting.\n 3️⃣ **World War I** – Face off against Soldiers in trench warfare.\n 4️⃣ **World War II** – Engage Tanks on the battlefield.\n 5️⃣ **Future Warfare** – Battle high-tech Robots in a futuristic city.",
            "🔥 Your Journey Awaits! \n\n Complete all levels, conquer history, and prove yourself as the ultimate warrior! Adapt your strategy to each era and emerge victorious!"
        };

        public void startTypingAnimation() {
            charIndex = 0;
            displayedText = "";
            nextButton.setEnabled(false);

            typingTimer = new Timer(50, e -> {
                if (charIndex < instructionTexts[textIndex].length()) {
                    displayedText += instructionTexts[textIndex].charAt(charIndex);
                    instructionLabel.setText("<html>" + displayedText.replace("\n", "<br>") + "</html>");
                    charIndex++;
                } else {
                    ((Timer) e.getSource()).stop();
                    nextButton.setEnabled(true);
                }
            });
            typingTimer.start();
        }

        private void showNextInstruction() {
            if (textIndex < instructionTexts.length - 1) {
                textIndex++;
                startTypingAnimation();
            } else {
                // At the end of instructions, return to the main menu panel.
                GameClass.this.setContentPane(mainMenuPanel);
                GameClass.this.revalidate();
                GameClass.this.repaint();
            }
        }

        public void resizeComponents() {
            int width = getWidth();
            int height = getHeight();

            double widthRatio = (double) width / INITIAL_WIDTH;
            double heightRatio = (double) height / INITIAL_HEIGHT;

            instructionLabel.setFont(instructionLabel.getFont().deriveFont((float) (24 * heightRatio)));
            instructionLabel.setBounds((int) (100 * widthRatio), (int) (100 * heightRatio), (int) (600 * widthRatio), (int) (150 * heightRatio));

            nextButton.setFont(nextButton.getFont().deriveFont((float) (20 * heightRatio)));
            nextButton.setBounds((int) (300 * widthRatio), (int) (400 * heightRatio), (int) (200 * widthRatio), (int) (50 * heightRatio));
        }
    }

    // Inner class for CharacterSelect.
    // This panel now draws its background in paintComponent so that it always appears.
    class CharacterSelect extends JPanel implements ActionListener {
        private static final long serialVersionUID = 1L;
        private String selectedCharacter = "";
        private JLabel titleLabel, selectedLabel, descriptionLabel, characterImageLabel;
        private JButton confirmButton, resetButton;
        private Map<JButton, CharacterData> characterMap;
        private ImageIcon backgroundIcon;

        public CharacterSelect() {
            setLayout(new BorderLayout(10, 10));
            setPreferredSize(new Dimension(1000, 800));
            // Load the background image
            backgroundIcon = loadImage("deyplay.jpg");
            // Ensure transparency of sub-panels
            setOpaque(false);

            JPanel topPanel = createTopPanel();
            topPanel.setOpaque(false);
            add(topPanel, BorderLayout.NORTH);

            JPanel centerPanel = createCenterPanel();
            centerPanel.setOpaque(false);
            add(centerPanel, BorderLayout.CENTER);

            // Initialize characters before creating bottom panel
            initializeCharacters();
            JPanel bottomPanel = createBottomPanel();
            bottomPanel.setOpaque(false);
            add(bottomPanel, BorderLayout.SOUTH);
        }

        // Override paintComponent to draw the background
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundIcon != null && backgroundIcon.getImage() != null) {
                g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
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
                button.setBackground(new Color(50, 50, 50));
                button.setForeground(Color.WHITE);
            } else {
                button.setIcon(scaleImageIcon(icon, 100, 100));
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
            JPanel panel = new JPanel(new GridLayout(2, 3, 10, 10));
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
                characterImageLabel.setIcon(scaleImageIcon(selected.icon, 400, 500));
                confirmButton.setEnabled(true);
            } else if (e.getSource() == confirmButton) {
                launchEnvironment(selectedCharacter);
            } else if (e.getSource() == resetButton) {
                selectedCharacter = "";
                selectedLabel.setText("Selected: None");
                descriptionLabel.setText("Select a character to see details.");
                characterImageLabel.setIcon(null);
                confirmButton.setEnabled(false);
            }
        }

        private void launchEnvironment(String selectedCharacter) {
            // Dispose the current window and launch the environment
            GameClass.this.dispose();
            JFrame frame = new JFrame("Game Environment");
            environment game = new environment(selectedCharacter);
            frame.add(game);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            new Thread(game).start();
        }

        private ImageIcon scaleImageIcon(ImageIcon icon, int width, int height) {
            if (icon == null || icon.getImage() == null) return new ImageIcon();
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        }

        private ImageIcon loadImage(String fileName) {
            java.net.URL imgUrl = getClass().getResource("/daptb/" + fileName);
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
    }
}
