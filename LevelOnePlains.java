package daptb;

import javax.swing.*;

public class LevelOnePlains {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//This will be the level code
		JFrame plains = new JFrame();
		plains.setExtendedState(JFrame.MAXIMIZED_BOTH); //Make full screen
		plains.setTitle("Level One: Plains");
		plains.setVisible(true);
		
		//Create level 2 class, connect to this level
		plains.addKeyListener(new java.awt.event.KeyAdapter() {
		@Override
		

	
	public void keyPressed(java.awt.event.KeyEvent e) {
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) { // Detect "Return" key
            plains.dispose(); // Close Level One window
            SwingUtilities.invokeLater(LevelTwoDesert::new); // Open Level Two window
	        }
		 }
	
	  });
   }
}
