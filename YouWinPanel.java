package daptb;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class YouWinPanel extends JPanel {
    private JFrame parentFrame;
    private Clip victoryClip;  // 🔊 Controls the victory sound


    public YouWinPanel(JFrame parentFrame) {
        
    	this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        
        AudioPlayer.stopMusic();  // Stop any existing music
		playVictorySound("level-victory-2.wav");  // ✅ Play and track the victory sound
        
        
        JLabel winLabel = new JLabel("LEVEL COMPLETE!", SwingConstants.CENTER);
        winLabel.setFont(new Font("Arial", Font.BOLD, 50));
        add(winLabel, BorderLayout.CENTER);

        JButton nextLevelButton = new JButton("Next Level");
        
        nextLevelButton.setFont(new Font("Arial", Font.BOLD, 30));
        
        nextLevelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (victoryClip != null && victoryClip.isRunning()) {
                    victoryClip.stop();  // 🚫 Stop victory sound immediately
                }

                AudioPlayer.stopMusic();  // 🚫 Stop any background music
                new FinalLevelVolcanoCastlePanel();  // ✅ Launch final level
                parentFrame.dispose();  // ✅ Close the You Win screen
                AudioPlayer.playMusic("final-level-volcano-castle.wav");  // Play final level music
            }
        });
        add(nextLevelButton, BorderLayout.SOUTH);
    }
        private void playVictorySound(String soundFile) {
            try {
                if (victoryClip != null && victoryClip.isRunning()) {
                    victoryClip.stop();  // 🚫 Stop if already playing
                }
                victoryClip = AudioSystem.getClip();
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource("/sounds/" + soundFile));
                victoryClip.open(audioStream);
                victoryClip.start();  // 🎵 Play the victory sound
            } catch (Exception e) {
                e.printStackTrace();
            }
       


        
    }
}