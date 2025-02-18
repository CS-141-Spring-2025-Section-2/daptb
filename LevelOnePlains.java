package daptb;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class LevelOnePlains extends JFrame {  
    private GamePanel gamePanel;  

    public LevelOnePlains() {  
        setTitle("Level One: Plains");
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        System.out.println("Creating GamePanel...");
        this.gamePanel = new GamePanel();  // 🔹 Assign GamePanel correctly
        //System.out.println("GamePanel created: " + gamePanel);

        add(gamePanel);  // 🔹 Add it to the frame
        pack();

        setVisible(true);
        gamePanel.startGameThread();  

        playMusic("sounds/LevelOnePlainsTheme.wav"); 
        addKeyListenerForNextLevel(); 
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LevelOnePlains::new); 
    }



    // KeyListener to proceed to next level or perform other actions (e.g., level progression)
    public void addKeyListenerForNextLevel() {
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) { // Detect "Enter" key press
                    dispose(); // Close Level One window
                    SwingUtilities.invokeLater(FinalLevelVolcanoCastle::new); // Open next level (Level Two)
                }
            }
        });
    }
    
    public static void playMusic(String filepath) {
        try {
            URL soundURL = LevelOnePlains.class.getResource("/sounds/LevelOnePlainsTheme.wav"); // Load from resources
            System.out.println("Trying to load sound from: " + soundURL); // Print the URL for debugging

            if (soundURL == null) {
                System.err.println("Sound file not found in resources: " + filepath);
                return; // Exit early if the URL is null
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundURL); // Use the URL
            Clip clip = AudioSystem.getClip(); 
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}