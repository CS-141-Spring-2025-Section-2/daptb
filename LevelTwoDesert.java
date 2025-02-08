package daptb;

import javax.swing.*;

public class LevelTwoDesert {
		private JFrame desert;

		public LevelTwoDesert() { // Constructor to create and show the window
		JFrame desert = new JFrame();
		desert.setExtendedState(JFrame.MAXIMIZED_BOTH); //Make full screen
		desert.setTitle("Level Two: Desert");
		desert.setVisible(true);
		desert.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensure it closes properly
		
		
		desert.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			
		
		public void keyPressed(java.awt.event.KeyEvent e) {
	        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) { // Detect "Return" key
	            desert.dispose(); // Close Level Two window
	            SwingUtilities.invokeLater(FinalLevelVolcanoCastle::new); // Open Level Two window
		        }
			 }
		
		 });
			
	}
		
		
	public static void main(String[] args) {
		new LevelTwoDesert(); // Directly launch Level Two
	}
}




