package daptb;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{

	public boolean downPressed, leftPressed, rightPressed, wPressed, iPressed, oPressed;
	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		int code = e.getKeyCode();
		
		
		if (code == KeyEvent.VK_S) {
			downPressed = true;
		}
		if (code == KeyEvent.VK_A) {
			leftPressed = true;
		}
		if (code == KeyEvent.VK_D) {
			rightPressed = true;
		}
		if (code == KeyEvent.VK_W) {
			wPressed = true;
		}
		if (code == KeyEvent.VK_I) {
			iPressed = true; 
		}
		if (code == KeyEvent.VK_O) {
			oPressed = true; 
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		int code = e.getKeyCode();
		
		
		if (code == KeyEvent.VK_S) {
			downPressed = false;
		}
		if (code == KeyEvent.VK_A) {
			leftPressed = false;
		}
		if (code == KeyEvent.VK_D) {
			rightPressed = false;
		}
		if (code == KeyEvent.VK_W) {
			wPressed = false;
		}
		if (code == KeyEvent.VK_I) {
			iPressed = false; 
		}
		if (code == KeyEvent.VK_O) {
			oPressed = false; 
		}
	}

}
