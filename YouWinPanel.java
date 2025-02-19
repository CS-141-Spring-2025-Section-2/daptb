package daptb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class YouWinPanel extends JPanel {
    private JFrame parentFrame;

    public YouWinPanel(JFrame parentFrame) {
        AudioPlayer.stopMusic();  // Stop any existing music
        AudioPlayer.playSound("level-victory-2.wav");  // Play win sound (place `you-win.wav` in /sounds/)

        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());

        JLabel winLabel = new JLabel("LEVEL COMPLETE!", SwingConstants.CENTER);
        winLabel.setFont(new Font("Arial", Font.BOLD, 50));
        add(winLabel, BorderLayout.CENTER);

        JButton nextLevelButton = new JButton("Next Level");
        nextLevelButton.setFont(new Font("Arial", Font.BOLD, 30));
        nextLevelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.getContentPane().removeAll();
                parentFrame.add(new FinalLevelVolcanoCastlePanel(parentFrame));  // Create next level panel
                parentFrame.revalidate();
                parentFrame.repaint();
                AudioPlayer.playMusic("FinalLevelVolcanoCastleTheme.wav");  // Play final level music
            }
        });

        add(nextLevelButton, BorderLayout.SOUTH);
    }
}
