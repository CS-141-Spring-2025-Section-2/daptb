package daptb;

import java.io.File;
import java.io.IOException;

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
        
        playMusic("src/daptb/LevelOnePlainsTheme.wav"); //Plays music
        
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
    
    private static Clip clip; //Store clip as a static variable
	//Method to read music file
	public static void playMusic(String filepath) {
		try {
			File musicFile = new File(filepath);
			System.out.println("Looking for file at: " + musicFile.getAbsolutePath());
			if (musicFile.exists()) {
				AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
				clip = AudioSystem.getClip();
				clip.open(audioStream);
				clip.start();
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			} else {
				System.out.println("Music file not found!");
			}
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}		
}
