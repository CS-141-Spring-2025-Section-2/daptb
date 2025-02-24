package daptb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

public class GameClass extends JFrame {
    private static final long serialVersionUID = 1L;
    private BufferedImage backgroundImage;

    // Corrected paths for background images
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

    private final int INITIAL_WIDTH = 800;
    private final int INITIAL_HEIGHT = 600;

    public GameClass() {
        setTitle("Travel and Conquer");
        setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        loadBackgroundImage();
        loadBackgroundMusic(basePath + "background_music.wav");
        loadClickSound(basePath + "click_sound.wav");

        titleLabel = createArtisticTitle("Travel and Conquer");
        titleLabel.setBounds(50, 20, 700, 100);
        add(titleLabel);

        // Create buttons with custom styles
        newGameButton = createButton("New Game", 300, 150, new Color(59, 89, 182)); // Blue color
        selectCharacterButton = createButton("Select Character", 300, 250, new Color(34, 139, 34)); // Green color
        continueButton = createButton("Continue", 300, 350, new Color(184, 134, 11)); // Gold color

        // Add action listeners
        newGameButton.addActionListener(e -> startNewGame());
        selectCharacterButton.addActionListener(e -> openCharacterSelection());
        continueButton.addActionListener(e -> continueGame());

        // Add buttons to the frame
        add(newGameButton);
        add(selectCharacterButton);
        add(continueButton);

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
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(184, 134, 11), 3)); // Gold border
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void changeBackground() {
        backgroundIndex = (backgroundIndex + 1) % backgroundImages.length;
        loadBackgroundImage();
        repaint();
    }

    private void startNewGame() {
        dispose();
        SwingUtilities.invokeLater(() -> new InstructionsPanelFrame());
    }

    private void openCharacterSelection() {
        dispose();
        SwingUtilities.invokeLater(() -> new CharacterSelect().setVisible(true));
    }

    private void continueGame() {
        JOptionPane.showMessageDialog(this, "Continuing from last save!");
    }

    private void resizeComponents() {
        int width = getWidth();
        int height = getHeight();

        double widthRatio = (double) width / INITIAL_WIDTH;
        double heightRatio = (double) height / INITIAL_HEIGHT;

        titleLabel.setFont(titleLabel.getFont().deriveFont((float) (48 * heightRatio)));
        titleLabel.setBounds((int) (50 * widthRatio), (int) (20 * heightRatio), (int) (700 * widthRatio), (int) (100 * heightRatio));

        resizeButton(newGameButton, 300, 150, widthRatio, heightRatio);
        resizeButton(selectCharacterButton, 300, 250, widthRatio, heightRatio);
        resizeButton(continueButton, 300, 350, widthRatio, heightRatio);
    }

    private void resizeButton(JButton button, int x, int y, double widthRatio, double heightRatio) {
        button.setFont(button.getFont().deriveFont((float) (20 * heightRatio)));
        button.setBounds((int) (x * widthRatio), (int) (y * heightRatio), (int) (200 * widthRatio), (int) (50 * heightRatio));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameClass menu = new GameClass();
            menu.setVisible(true);
        });
    }

    // Inner class for InstructionsPanelFrame
    class InstructionsPanelFrame extends JFrame {
        public InstructionsPanelFrame() {
            setTitle("Instructions");
            setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            add(new InstructionsPanel());
            setVisible(true);

            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    ((InstructionsPanel) getContentPane().getComponent(0)).resizeComponents();
                }
            });
        }
    }

    // Inner class for InstructionsPanel
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
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                if (parentFrame != null) {
                    parentFrame.dispose();
                    SwingUtilities.invokeLater(() -> new GameClass().setVisible(true));
                }
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
}