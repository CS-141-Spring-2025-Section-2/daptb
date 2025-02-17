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

public class LevelOnePlains extends JFrame {  // Make the class extend JFrame
	
	private GamePanel gamePanel;  // Game Panel for rendering

    public LevelOnePlains() {  // Constructor to initialize the level
        setTitle("Level One: Plains");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Make full screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close when window is closed
        setLocationRelativeTo(null); // Center on screen
        
        // Add any panels, components, or background images here
        
        gamePanel = new GamePanel();  //Initialize game panel
        add(gamePanel);  // Add panel to frame 
        pack();
        
        setVisible(true); //Make the window visible
        
        gamePanel.startGameThread();
        
        playMusic("sounds/LevelOnePlainsTheme.wav"); //Plays music
        
        addKeyListenerForNextLevel(); 
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LevelOnePlains::new); // Automatically launch the level
    }

    // KeyListener to proceed to next level or perform other actions (e.g., level progression)
    public void addKeyListenerForNextLevel() {
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) { // Detect "Enter" key press
                    dispose(); // Close Level One window
                    SwingUtilities.invokeLater(LevelTwoDesert::new); // Open next level (Level Two)
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