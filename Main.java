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


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		endScreen.start(); //Invoke endScreen method in main method
	}

}


class endScreen {
	public static void start() {
		
		JFrame gameScreen = new JFrame("Game"); //Create window
		gameScreen.setUndecorated(false);
		gameScreen.setSize(1100, 840); //Set Dimensions
		gameScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameScreen.setTitle("2D Adventure"); //Title of window
		gameScreen.setLocationRelativeTo(null);
		gameScreen.setResizable(true);
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(gameScreen.getSize());
		
		BackgroundPanel endscreen = new BackgroundPanel("src/daptb/2D Game End Screen.jpg"); //Adds background image **IF IMAGE ISN'T SHOWING ON YOUR COMPUTER, THIS IS THE PROBLEM LINE**
		endscreen.setBounds(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);
		layeredPane.add(endscreen, JLayeredPane.DEFAULT_LAYER);
		
		
		JPanel textpanel = new JPanel(new GridBagLayout());
		textpanel.setOpaque(false);
		textpanel.setBounds(0, 0, gameScreen.getWidth(), gameScreen.getHeight());
		textpanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		playMusic("src/daptb/GameVictory!.wav"); //Plays music
		
		gameScreen.setLocationRelativeTo(null);
		gameScreen.setLayout(new BorderLayout());
		
		//Create Background
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//Create End Screen text
		JLabel text = new JLabel("Congratulations! You have defeated the Final Boss and beat the game!!!", SwingConstants.CENTER);
		JLabel text2 = new JLabel("Thank you for playing!", SwingConstants.CENTER);
		JLabel text3 = new JLabel("Credits:", SwingConstants.CENTER);
		JLabel text4 = new JLabel("Dayspring, Abdul, Phillip, Tepiwa, Benjamin: D.A.P.T.B.", SwingConstants.CENTER);
		JLabel text5 = new JLabel("Press 'Esc' to return to the Main Menu.", SwingConstants.CENTER);
		
		//Set text attributes
		Font textFont = new Font("Times New Roman", Font.BOLD, 33);
		text.setFont(textFont);
		text.setForeground(Color.WHITE);
		text2.setFont(textFont);
		text2.setForeground(Color.WHITE);
		text3.setFont(textFont);
		text3.setForeground(Color.GRAY);
		text4.setFont(textFont);
		text4.setForeground(Color.GRAY);
		text5.setFont(textFont);
		text5.setForeground(Color.RED);
		
		text.setAlignmentX(Component.CENTER_ALIGNMENT); //Center text
        text2.setAlignmentX(Component.CENTER_ALIGNMENT); 
        text3.setAlignmentX(Component.CENTER_ALIGNMENT);
        text4.setAlignmentX(Component.CENTER_ALIGNMENT);
        text5.setAlignmentX(Component.CENTER_ALIGNMENT);
		
        //Add text to window
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
		panel.add(Box.createVerticalGlue());
		
		GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER; // <-- CHANGED: Ensures text remains centered
        gbc.weighty = 1.0;
        textpanel.add(panel, gbc); // <-- CHANGED: Add panel to GridBagLayout container

       

        
		gameScreen.addComponentListener(new ComponentAdapter() { //Detect window resizing
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension newSize = gameScreen.getSize();
                textpanel.setBounds(0, 0, newSize.width, newSize.height);
                textpanel.revalidate();  // ✅ Forces text layout update
                textpanel.repaint();// Update size
            }
        });
		

		//Code for when pressing 'Esc' to exit
		gameScreen.addKeyListener(new KeyListener() {
		
			@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (clip != null && clip.isRunning()) {
				clip.stop(); //Pauses GameVictory music
			}
			gameScreen.dispose();
			SwingUtilities.invokeLater(GameClass::new);;
		}
			
	}
			@Override
	public void keyReleased(KeyEvent e) {}
		
			@Override
	public void keyTyped(KeyEvent e) {}
		
		});
		
		
		layeredPane.add(textpanel, JLayeredPane.PALETTE_LAYER);
		gameScreen.setContentPane(layeredPane);
		
		gameScreen.setContentPane(layeredPane);
		gameScreen.setVisible(true); //Allows window to pop up
	}
	
	private static Clip clip; //Store clip as a static variable
	//Method to read music file
	public static void playMusic(String filepath) {
		try {
			File musicFile = new File(filepath);
			System.out.println("Looking for file at: " + musicFile.getAbsolutePath());
			if (musicFile.exists()) {
				AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
				clip = AudioSystem.getClip();
				clip.open(audioStream);
				clip.start();
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			} else {
				System.out.println("Music file not found!");
			}
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}		
} 



class BackgroundPanel extends JPanel {
	private Image backgroundImage;
	
	public BackgroundPanel(String filepath) {
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

	

		