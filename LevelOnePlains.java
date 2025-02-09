package daptb;

import javax.swing.*;

public class LevelOnePlains extends JFrame {  // Make the class extend JFrame
	
	private GamePanel gamePanel;  // Game Panel for rendering

    public LevelOnePlains() {  // Constructor to initialize the level
        setTitle("Level One: Plains");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Make full screen**
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close when window is closed
        setLocationRelativeTo(null); // Center on screen
        
        // Add any panels, components, or background images here
        
        gamePanel = new GamePanel();  //Initialize game panel
        add(gamePanel);  // Add panel to frame
        pack();
        
        setVisible(true); //Make the window visible
        
        gamePanel.startGameThread();
        
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
}
