package daptb;

import javax.swing.*;

public class LevelOnePlains extends JFrame {  // **Change 1**: Make the class extend JFrame

    public LevelOnePlains() {  // **Change 2**: Constructor to initialize the level
        setTitle("Level One: Plains");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // **Make full screen**
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close when window is closed
        setLocationRelativeTo(null); // Center on screen
        
        // Optionally, add any panels, components, or background images here
        
        setVisible(true); // Make the window visible
        
        addKeyListenerForNextLevel(); 
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LevelOnePlains::new); // **Change 3**: Automatically launch the level
    }

    // KeyListener to proceed to next level or perform other actions (e.g., level progression)
    public void addKeyListenerForNextLevel() {
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) { // **Detect "Enter" key press**
                    dispose(); // **Close Level One window**
                    SwingUtilities.invokeLater(LevelTwoDesert::new); // **Open next level (Level Two)**
                }
            }
        });
    }
}
