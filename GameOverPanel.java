package daptb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameOverPanel extends JPanel {
    private GamePanel gamePanel;
    private JFrame parentFrame;

    public GameOverPanel(JFrame parentFrame) {
    	AudioPlayer.playGameOverMusic("game-over.wav");  // 🎵 Play Game Over music

    	this.parentFrame = parentFrame;  // Store frame for restarting
        setLayout(new BorderLayout());  // Layout manager for positioning components

        // GAME OVER label
        JLabel gameOverLabel = new JLabel("GAME OVER", SwingConstants.CENTER);  // Centered text
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 50));  // Large, bold font
        add(gameOverLabel, BorderLayout.CENTER);  // Add label to center of panel

        // Restart button
        JButton restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Arial", Font.BOLD, 30));  // Button styling
        restartButton.addActionListener(new ActionListener() {  // Handle button clicks
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();  // Call method to restart the game
            }
        });
        add(restartButton, BorderLayout.SOUTH);  // Add button at the bottom
    }

    private void restartGame() {
    	AudioPlayer.stopMusic();  // 🔇 Stop Game Over music before restarting

        parentFrame.getContentPane().removeAll();  // Remove Game Over screen
        GamePanel newGamePanel = new GamePanel(parentFrame);  // Create fresh game
        parentFrame.add(newGamePanel);  // Add new game panel
        parentFrame.revalidate();  // Refresh layout
        parentFrame.repaint();  // Redraw window
        AudioPlayer.playMusic("LevelOnePlainsTheme.wav");  // 🎵 Restart level music
        newGamePanel.resumeGame();  // 🟢 Resume game updates
        newGamePanel.requestFocusInWindow();  // Ensure new panel captures key inputs
        newGamePanel.startGameThread();  // Start the new game loop
    }
}
/*
 * i need the game to have a win condition that closes out the level one window and opens the FinalLevelVolcanoCastle level, 
 * Here’s a step-by-step guide to implement a Game Over screen that displays "GAME OVER" when the player dies, along with a restart option. 
 * I’ll explain each line of code and syntax for clarity. Would you like help adding a fade effect or background music for the Game Over screen? 😊 with no errors
 */