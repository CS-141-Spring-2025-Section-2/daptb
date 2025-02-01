package daptb;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import javax.swing.*;

import java.awt.event.*;
import javax.sound.sampled.*;

import java.io.File;
import java.io.IOException;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		endScreen.start(); //Invoke endScreen method in main method
	}

}


class endScreen {
	public static void start() {
		
		JFrame gameScreen = new JFrame("Game"); //Create window
		gameScreen.setSize(1100, 840); //Set Dimensions
		gameScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameScreen.setTitle("2D Adventure"); //Title of window
		
		playMusic("src/daptb/GameVictory!.wav"); //Plays music
		
		gameScreen.setLocationRelativeTo(null);
		gameScreen.setLayout(new BorderLayout());
		
		//Create Background
		JPanel panel = new JPanel();
		panel.setBackground(Color.BLACK);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//Create End Screen text
		JLabel text = new JLabel("Congratulations! You have defeated the Final Boss and beat the game!!!", SwingConstants.CENTER);
		JLabel text2 = new JLabel("Thank you for playing!", SwingConstants.CENTER);
		JLabel text3 = new JLabel("Credits:", SwingConstants.CENTER);
		JLabel text4 = new JLabel("Dayspring, Abdul, Phillip, Tepiwa, Benjamin: D.A.P.T.B.", SwingConstants.CENTER);
		JLabel text5 = new JLabel("Press 'Esc' to exit.", SwingConstants.CENTER);
		
		//Set text attributes
		Font textFont = new Font("Times New Roman", Font.BOLD, 24);
		text.setFont(textFont);
		text.setForeground(Color.WHITE);
		text2.setFont(textFont);
		text2.setForeground(Color.WHITE);
		text3.setFont(textFont);
		text3.setForeground(Color.WHITE);
		text4.setFont(textFont);
		text4.setForeground(Color.WHITE);
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

		gameScreen.add(panel);
			
		
		//Code for when pressing 'Esc' to exit
		gameScreen.addKeyListener(new KeyListener() {
		
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					gameScreen.dispose();
					System.exit(0);
				}
			
			}
			@Override
			public void keyReleased(KeyEvent e) {}
		
			@Override
			public void keyTyped(KeyEvent e) {}
		
		});
		
		gameScreen.setVisible(true); //Allows window to pop up
	}
	
	//Method to read music file
	public static void playMusic(String filepath) {
		try {
			File musicFile = new File(filepath);
			System.out.println("Looking for file at: " + musicFile.getAbsolutePath());
			if (musicFile.exists()) {
				AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
				Clip clip = AudioSystem.getClip();
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
	



	

		