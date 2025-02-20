package daptb;

import javax.swing.*;

public class FinalLevelVolcanoCastlePanel extends JFrame {  
    private GamePanel gamePanel;  

    public FinalLevelVolcanoCastlePanel() {  
        setTitle("Final Level: Volcano Castle");
        setExtendedState(JFrame.MAXIMIZED_BOTH);  // Fullscreen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Center window

        this.gamePanel = new GamePanel(this, "final-level-volcano-castle.wav");  // ✅ Pass music file
        add(gamePanel);  // Add the game panel to the frame
        pack();  // Fit window to preferred size
        gamePanel.requestFocusInWindow();  // Focus for key inputs
        setVisible(true);  // Show window

        AudioPlayer.playMusic("final-level-volcano-castle.wav");  // ✅ Play final level music
    }
    public void showEndScreen() {
        AudioPlayer.stopMusic();  // Stop final level music
        getContentPane().removeAll();  // Remove existing content
        add(new EndScreenPanel(this));  // Use 'this' as parent frame
        revalidate();  // Refresh layout
        repaint();  // Redraw components
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(FinalLevelVolcanoCastlePanel::new);  // ✅ Launch final level
    }
    
}
