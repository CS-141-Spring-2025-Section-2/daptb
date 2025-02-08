package daptb;

import javax.swing.*;

public class FinalLevelVolcanoCastle {

		
		//This will be the level code
		public FinalLevelVolcanoCastle() { // Constructor to create and show the window
		JFrame volcanoCastle = new JFrame();
		volcanoCastle.setExtendedState(JFrame.MAXIMIZED_BOTH); //Make full screen
		volcanoCastle.setTitle("Final Level: Volcano Castle");
		volcanoCastle.setVisible(true);
		
		volcanoCastle.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
		public void keyPressed(java.awt.event.KeyEvent e) {
			if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
				volcanoCastle.dispose();
				SwingUtilities.invokeLater(endScreen::start);
			 }
		 }
	
	 });
	
 }
	
	public static void main(String[] args) {
		new FinalLevelVolcanoCastle();
	
	
	}
	

}
