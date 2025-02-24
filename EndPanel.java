package daptb;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.sound.sampled.*;

import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.awt.Font;

public class EndPanel extends JFrame {

    public EndPanel() {
        setTitle("Game Completed");
        setExtendedState(JFrame.MAXIMIZED_BOTH);  
        setUndecorated(true);  
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        setLocationRelativeTo(null);  

        add(new EndScreenPanel(this, true));  
        setVisible(true);  
    }

    public static void main(String[] args) {
        new EndPanel();  
    }
}

class EndScreenPanel extends JPanel {
    private static Clip clip;  
    private Image backgroundImage;  
    private int textYPosition;  // Current Y position of the text
    private Timer timer;  // Timer to update text position
    private JPanel textContainer;  // Class-level variable

    public EndScreenPanel(JFrame parentFrame, boolean playEndMusic) {
        setLayout(new BorderLayout());
        loadBackgroundImage("game-end-screen.jpg");  

        if (playEndMusic) {
            stopMusic();  
            playMusic("Laho.wav");  
        }

        Font textFont = new Font("Impact", Font.ITALIC, 33);

        // ✅ Create centered paragraph labels using HTML
        JLabel text = createCenteredLabel("Congratulations! You have defeated the Final Boss and beat the game!", textFont, Color.GRAY);
        JLabel text2 = createCenteredLabel("Thank you for playing!", textFont, Color.GRAY);
        JLabel text3 = createCenteredLabel("Credits:", textFont, Color.WHITE);
        JLabel text4 = createCenteredLabel("Characters: Dayspring, \nTitle/Instructions: Abdelrhman, \nLevel/Sprites Mechanics: Phillip, \nDesign/Presantation: Tapiwa, \nEnemies/Game Mechanics: Benjamin; D.A.P.T.B.", textFont, Color.WHITE);
        JLabel text5 = createCenteredLabel("Press 'Esc' to return to the Main Menu.", textFont, Color.GREEN);
        JLabel text6 = createCenteredLabel("Press Delete on Mac/Backspace on Windows to exit.", textFont, Color.RED);

        // ✅ Panel to hold text vertically
        textContainer = new JPanel() {
            @Override
            protected void paintChildren(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.translate(0, textYPosition);
                super.paintChildren(g2d);
                g2d.dispose();
            }
        };
        textContainer.setOpaque(false);  
        textContainer.setLayout(new BoxLayout(textContainer, BoxLayout.Y_AXIS));
        textContainer.add(Box.createVerticalGlue());
        for (JLabel label : new JLabel[]{text, text2, text3, text4, text5, text6}) {
            textContainer.add(label);
            textContainer.add(Box.createRigidArea(new Dimension(0, -7)));  
        }
        textContainer.add(Box.createVerticalGlue());

        // ✅ Center textContainer
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);  
        centerPanel.add(textContainer, new GridBagConstraints());  

        add(centerPanel, BorderLayout.CENTER);  

        // Add key listener for Esc and Backspace keys
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (clip != null && clip.isRunning()) clip.stop();

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.out.println("Esc key pressed. Transitioning to main menu...");

                    // Create the main menu
                    GameClass mainMenu = new GameClass();
                    mainMenu.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize the main menu
                    mainMenu.setUndecorated(true); // Remove decorations (if needed)
                    mainMenu.setVisible(true); // Make the main menu visible

                    // Use a Timer to handle the fade-out of EndPanel
                    Timer fadeTimer = new Timer(50, null);
                    fadeTimer.addActionListener(new ActionListener() {
                        float opacity = 1f; // Start with 100% opacity for EndPanel

                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            opacity -= 0.05f; // Reduce opacity of EndPanel
                            if (opacity <= 0f) {
                                fadeTimer.stop();
                                parentFrame.dispose(); // Dispose of EndPanel after fade-out
                                System.out.println("EndPanel disposed. Main menu is now active.");
                            } else {
                                parentFrame.setOpacity(opacity); // Fade out EndPanel
                            }
                        }
                    });
                    fadeTimer.start();
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    System.out.println("Backspace key pressed. Exiting application...");
                    parentFrame.dispose();
                    System.exit(0);
                }
            }
        });

        setFocusable(true);
        requestFocusInWindow();  

        // Initialize text position and start timer
        textYPosition = getHeight() + 220;
        startTextAnimation();
    }

    // ✅ Helper method to create centered JLabel with HTML for text alignment
    private JLabel createCenteredLabel(String text, Font font, Color color) {
        JLabel label = new JLabel("<html><div style='text-align: center; max-width: 1000px; '>" + text + "</div></html>", SwingConstants.CENTER);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);  
        return label;
    }

    private void loadBackgroundImage(String filepath) {
        try (InputStream is = getClass().getResourceAsStream(filepath)) {
            if (is == null) {
                System.out.println("Error: Background image not found at path: " + filepath);
                return;
            }
            backgroundImage = ImageIO.read(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);  
        }
    }

    private static void playMusic(String filepath) {
        try (InputStream is = EndScreenPanel.class.getResourceAsStream(filepath)) {
            if (is == null) {
                System.out.println("Error: Sound file not found at: " + filepath);
                return;
            }

            // Wrap the InputStream in a BufferedInputStream
            BufferedInputStream bufferedInputStream = new BufferedInputStream(is);

            // Use the BufferedInputStream to create the AudioInputStream
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedInputStream);

            // Get the Clip and open it
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            System.out.println("Clip opened successfully."); // Debug statement

            // Adjust volume (optional)
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(6.0f); // Increase volume by 6 decibels

            // Start the Clip
            clip.start();
            System.out.println("Clip started successfully."); // Debug statement
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the music
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }

    private void startTextAnimation() {
        timer = new Timer(80, new ActionListener() {  // Adjust the delay for speed control
            @Override
            public void actionPerformed(ActionEvent e) {
                textYPosition -= 1;  // Adjust the rise speed
                if (textYPosition + textContainer.getHeight() < 0) {
                    textYPosition = getHeight();  // Reset to bottom if off-screen
                }
                textContainer.repaint();
            }
        });
        timer.start();
    }
}