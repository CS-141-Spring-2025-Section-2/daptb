package daptb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class YouWinPanel extends JPanel {
    private JFrame parentFrame;

    public YouWinPanel(JFrame parentFrame, boolean playVictoryMusic) {
  
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        
        if (playVictoryMusic) {
        	AudioPlayer.stopMusic();  // ✅ Ensure previous music stops
            AudioPlayer.playSound("level-victory-2.wav");  // ✅ Only play if flag is true
        }

        JLabel winLabel = new JLabel("LEVEL COMPLETE!", SwingConstants.CENTER);
        winLabel.setFont(new Font("Arial", Font.BOLD, 50));
        add(winLabel, BorderLayout.CENTER);

        JButton nextLevelButton = new JButton("Next Level");
        nextLevelButton.setFont(new Font("Arial", Font.BOLD, 30));
        nextLevelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.getContentPane().removeAll();
                parentFrame.revalidate();
                parentFrame.repaint();
                AudioPlayer.playMusic("final-level-volcano-castle.wav");  // Play final level music
            }
        });

        add(nextLevelButton, BorderLayout.SOUTH);
    }
}
