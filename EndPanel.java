package daptb;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.sound.sampled.*;
import java.io.InputStream;
import javax.imageio.ImageIO;

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

    public EndScreenPanel(JFrame parentFrame, boolean playEndMusic) {
        setLayout(new BorderLayout());
        loadBackgroundImage("game-end-screen.jpg");  

        if (playEndMusic) {
            stopMusic();  
            playMusic("game-end.wav");  
        }

        Font textFont = new Font("Times New Roman", Font.BOLD, 33);

        // ✅ Create centered paragraph labels using HTML
        JLabel text = createCenteredLabel("Congratulations! You have defeated the Final Boss and beat the game!", textFont, Color.WHITE);
        JLabel text2 = createCenteredLabel("Thank you for playing!", textFont, Color.WHITE);
        JLabel text3 = createCenteredLabel("Credits:", textFont, Color.GRAY);
        JLabel text4 = createCenteredLabel("Character: Dayspring, Title: Abdul, Level/Sprites: Phillip, Design: Tepiwa, Enemies: Benjamin D.A.P.T.B.", textFont, Color.GRAY);
        JLabel text5 = createCenteredLabel("Press 'Esc' to return to the Main Menu.", textFont, Color.GREEN);
        JLabel text6 = createCenteredLabel("Press Delete on Mac/Backspace on Windows to exit.", textFont, Color.RED);

        // ✅ Panel to hold text vertically
        JPanel textContainer = new JPanel();
        textContainer.setOpaque(false);  
        textContainer.setLayout(new BoxLayout(textContainer, BoxLayout.Y_AXIS));
        textContainer.add(Box.createVerticalGlue());
        for (JLabel label : new JLabel[]{text, text2, text3, text4, text5, text6}) {
            textContainer.add(label);
            textContainer.add(Box.createRigidArea(new Dimension(0, 15)));  
        }
        textContainer.add(Box.createVerticalGlue());

        // ✅ Center textContainer
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);  
        centerPanel.add(textContainer, new GridBagConstraints());  

        add(centerPanel, BorderLayout.CENTER);  

        // ✅ Key listener for navigation
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (clip != null && clip.isRunning()) clip.stop();  

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    parentFrame.dispose();  
                    new GameClass();  
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    parentFrame.dispose();  
                    System.exit(0);  
                }
            }
        });

        setFocusable(true);
        requestFocusInWindow();  
    }

    // ✅ Helper method to create centered JLabel with HTML for text alignment
    private JLabel createCenteredLabel(String text, Font font, Color color) {
        JLabel label = new JLabel("<html><div style='text-align: center;'>" + text + "</div></html>", SwingConstants.CENTER);
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
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(is);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);  
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
}
