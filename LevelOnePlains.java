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

        this.gamePanel = new GamePanel(this);  // Pass JFrame to GamePanel
        add(gamePanel);  // Add game panel to window
        pack();  // Fit components
        gamePanel.requestFocusInWindow();  // Focus the panel for key inputs
        gamePanel.startGameThread();  // Start game loop
        setVisible(true);
        gamePanel.startGameThread();  

       
        
        }

        
       
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LevelOnePlains::new); 
    }
    


    // KeyListener to proceed to next level or perform other actions (e.g., level progression)
   
    
   
    public static void playMusic(String filepath) {
        try {
            URL soundURL = LevelOnePlains.class.getResource("LevelOnePlainsTheme.wav");  // ✅ Use leading slash
            System.out.println("Trying to load sound from: " + soundURL);  // Debug URL

            if (soundURL == null) {
                System.err.println("❌ Sound file not found in resources: " + filepath);
                return;  // Exit early if not found
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundURL);  
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);  // 🔁 Loop music

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}