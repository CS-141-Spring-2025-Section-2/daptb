package daptb;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    GamePanel gp;  // Reference to the GamePanel

    public boolean downPressed, leftPressed, rightPressed, wPressed, iPressed, oPressed;

    // **Constructor to Assign GamePanel**
    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_S) downPressed = true;
        if (code == KeyEvent.VK_A) leftPressed = true;
        if (code == KeyEvent.VK_D) rightPressed = true;
        if (code == KeyEvent.VK_W) wPressed = true;
        if (code == KeyEvent.VK_I) iPressed = true;
        if (code == KeyEvent.VK_O) oPressed = true;

        if (gp != null && gp.player != null) {  // Ensure gp is assigned before using it
            if (code == KeyEvent.VK_I) {
                gp.player.attack("Punch");
            } else if (code == KeyEvent.VK_O) {
                gp.player.attack("Kick");
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_S) downPressed = false;
        if (code == KeyEvent.VK_A) leftPressed = false;
        if (code == KeyEvent.VK_D) rightPressed = false;
        if (code == KeyEvent.VK_W) wPressed = false;
        if (code == KeyEvent.VK_I) iPressed = false;
        if (code == KeyEvent.VK_O) oPressed = false;
    }
    
    
    public void keyPressing(KeyEvent e) {
        int code = e.getKeyCode();
 
        if (gp.player != null && !gp.player.isAttackOnCooldown()) {  
            if (code == KeyEvent.VK_I && !gp.player.isAttacking()) {  // Punch
                gp.player.attack("Punch");
            } else if (code == KeyEvent.VK_O && !gp.player.isAttacking()) {  // Kick
                gp.player.attack("Kick");
            }
        }
    }

}
