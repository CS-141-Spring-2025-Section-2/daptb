package daptb;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import java.awt.*;
import javax.swing.*;


import javax.sound.sampled.*;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class EndPanel {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		EndScreenPanel.start(); //Invoke endScreen method in main method
	}

}


class EndScreenPanel extends JPanel {
	public static void start() {
		
	}
    private static Clip clip; // Store clip as a static variable

    public EndScreenPanel(JFrame parentFrame, boolean playEndMusic) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1100, 840)); // Set size for consistent display

        // ✅ Play end screen music if the flag is true
        if (playEndMusic) {
            stopMusic();  // Stop any existing music first
            playMusic("src/daptb/GameVictory!.wav");
        }

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(getPreferredSize());

        // ✅ Add background image
        backgroundPanel endscreen = new backgroundPanel("src/daptb/2D Game End Screen.jpg");
        endscreen.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(endscreen, JLayeredPane.DEFAULT_LAYER);

        // ✅ Text Panel
        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setOpaque(false);
        textPanel.setBounds(0, 0, getWidth(), getHeight());

        // ✅ Create End Screen text
        JLabel text = new JLabel("Congratulations! You have defeated the Final Boss and beat the game!!!", SwingConstants.CENTER);
        JLabel text2 = new JLabel("Thank you for playing!", SwingConstants.CENTER);
        JLabel text3 = new JLabel("Credits:", SwingConstants.CENTER);
        JLabel text4 = new JLabel("Dayspring, Abdul, Phillip, Tepiwa, Benjamin: D.A.P.T.B.", SwingConstants.CENTER);
        JLabel text5 = new JLabel("Press 'Esc' to return to the Main Menu.", SwingConstants.CENTER);
        JLabel text6 = new JLabel("Press Delete on Mac/Backspace on Windows to exit.", SwingConstants.CENTER);

        // ✅ Set text attributes
        Font textFont = new Font("Times New Roman", Font.BOLD, 33);
        for (JLabel label : new JLabel[]{text, text2, text3, text4, text5, text6}) {
            label.setFont(textFont);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        text.setForeground(Color.WHITE);
        text2.setForeground(Color.WHITE);
        text3.setForeground(Color.GRAY);
        text4.setForeground(Color.GRAY);
        text5.setForeground(Color.GREEN);
        text6.setForeground(Color.RED);

        // ✅ Arrange text vertically
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalGlue());
        panel.add(text);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(text2);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(text3);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(text4);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(text5);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(text6);
        panel.add(Box.createVerticalGlue());

        textPanel.add(panel, new GridBagConstraints());
        layeredPane.add(textPanel, JLayeredPane.PALETTE_LAYER);

        // ✅ Add layered pane to this panel
        add(layeredPane, BorderLayout.CENTER);

        // ✅ Add key listener to handle 'Esc' and 'Backspace'
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (clip != null && clip.isRunning()) {
                    clip.stop(); // Stop the end screen music
                }

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    parentFrame.getContentPane().removeAll();
                    EndScreenPanel.start();  // ✅ Uses existing static method, no constructor needed
                    parentFrame.revalidate();
                    parentFrame.repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    parentFrame.dispose();  // Exit the game
                    System.exit(0);
                }
            }
        });

        setFocusable(true);
        requestFocusInWindow();  // Ensure key listener works
    }
    public EndScreenPanel(JFrame parentFrame) {
        setLayout(new BorderLayout());

        JLabel congratulations = new JLabel("Congratulations! You have defeated the Final Boss!", SwingConstants.CENTER);
        congratulations.setFont(new Font("Arial", Font.BOLD, 36));
        add(congratulations, BorderLayout.CENTER);

        JButton mainMenuButton = new JButton("Return to Main Menu");
        mainMenuButton.addActionListener(e -> {
            AudioPlayer.stopMusic();  // Stop victory music
            parentFrame.getContentPane().removeAll();
            EndScreenPanel.start();            
            parentFrame.revalidate();
            parentFrame.repaint();
        });

        add(mainMenuButton, BorderLayout.SOUTH);

        AudioPlayer.stopMusic();  // ✅ Stop any previous music
        AudioPlayer.playSound("GameVictory!.wav");  // ✅ Plays only the End Screen music

    }

    // ✅ Music methods
    private static void playMusic(String filepath) {
        try {
            File musicFile = new File(filepath);
            if (musicFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
                clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                System.out.println("Music file not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}




class backgroundPanel extends JPanel {
	private Image backgroundImage;
	
	public backgroundPanel(String filepath) {
		try {
			backgroundImage = ImageIO.read(new File(filepath));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error: Background image not found!");
		}
	}
	
	@Override
	protected void paintComponent (Graphics g) {
		super.paintComponent(g);
		if (backgroundImage != null) {
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
			
			//Dims background so text is more visible
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}
}

	

		