package daptb;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class GamePanel extends JPanel implements Runnable {
	KeyHandler keyH;  
    Player player;  
    
    public GamePanel() {
        System.out.println("Initializing GamePanel...");

        this.keyH = new KeyHandler(this);  // 🔹 Initialize KeyHandler FIRST
        //System.out.println("KeyHandler created: " + keyH);

       // System.out.println("KeyHandler instance in GamePanel: " + keyH);
        this.player = new Player(this, keyH);  // 🔹 Pass KeyHandler to Player
        // System.out.println("Player created with KeyHandler: " + player.keyH);

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.CYAN);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);  // 🔹 Attach KeyHandler to listen for inputs
        this.setFocusable(true);

        enemy = new Enemy(this, 500, player.worldY);
        
        startGameThread(); 
    }
    private BufferedImage img;

    // SCREEN SETTINGS
    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; // 48x48 tile
    public final int maxScreenCol = 32;
    public final int maxScreenRow = 24;
    public final int screenWidth = tileSize * maxScreenCol; // 1536 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 1152 pixels

    // WORLD SETTINGS
    public final int maxWorldCol = 100;  // Adjust for level length
    public final int maxWorldRow = 24;   // Keep vertical the same
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    // CAMERA VARIABLES
    public int cameraX = 0;  // Camera starting position
    public int cameraY = 0;
    public final int cameraOffsetX = screenWidth / 2 - tileSize / 2; // Center knight horizontally
    public final int cameraOffsetY = screenHeight / 2 - tileSize / 2; // Center knight vertically
    
    

    // FPS
    int FPS = 60;

    TileManager tileM = new TileManager(this);

    

    Thread gameThread;

    

    Enemy enemy; // Declare Enemy object
	public Enemy[] enemies;

    



    public int getGroundLevel() {
        // Returns the ground level in the world coordinates
        return worldHeight - tileSize;
    }
    
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1000000000) {
                drawCount = 0;
                timer = 0;
            }
        }
    }

    // Player and enemy controls
    public void update() {
        player.update();
        if (enemy != null) {
        	enemy.update();
        }
        //System.out.println("CameraX: " + cameraX + " | CameraY: " + cameraY); // For debugging

           
    }

    public void draw(Graphics2D g2) {
        // **Draw tiles first (background)**
        tileM.draw(g2);

        // **Draw player and enemy next**
        player.draw(g2);
        if (enemy != null) {
        enemy.draw(g2);
        }
        
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // **Use Buffered Image for Smooth Drawing**
        BufferedImage buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bufferGraphics = buffer.createGraphics();
       
        
        draw(bufferGraphics);  // **Draw everything onto the buffer first**
        g2.drawImage(buffer, 0, 0, null); // **Then draw the buffer onto the screen**

        bufferGraphics.dispose();
        g2.dispose();
    }

	public void restartGame() {
		// TODO Auto-generated method stub
        startGameThread();
		
	}
}