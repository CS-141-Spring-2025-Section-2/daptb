package daptb;

import javax.swing.*;

public class FinalLevelVolcanoCastlePanel extends JFrame {  
    private GamePanel gamePanel;  

    public FinalLevelVolcanoCastlePanel() {  
        setTitle("Final Level: Volcano Castle");
        setExtendedState(JFrame.MAXIMIZED_BOTH);  
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  

        this.gamePanel = new GamePanel(this, "final-level-volcano-castle.wav");  // ✅ Pass music file
        add(gamePanel);  
        pack();  
        gamePanel.requestFocusInWindow();  
        setVisible(true);  
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FinalLevelVolcanoCastlePanel::new);  
    }
}
