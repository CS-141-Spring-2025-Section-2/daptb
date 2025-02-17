package daptb;

import java.awt.Graphics2D;
import java.io.IOException;
import javax.imageio.ImageIO;
import daptb.GamePanel;

public class TileManager {

    GamePanel gp;
    Tile[] tile;
    // Add a tile map for rendering multiple rows and columns
    int[][] mapTileNum;

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[10];
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        getTileImage();
    }

    public void getTileImage() { 
        try {
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/tiles/surface-grass.png"));
            // **CHANGE:** Mark the surface grass as collidable.
            tile[0].collision = true;

            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/tiles/dirt.png"));

            tile[2] = new Tile();
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("/tiles/bush.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        int groundLevelWorld = 650 + gp.tileSize;         // World coordinate for the top of the ground
        int groundLevelScreen = groundLevelWorld - gp.cameraY; // Screen coordinate for ground top

        // **CHANGE:** Calculate how many tile rows are needed to cover from the ground top to the bottom of the screen.
        int additionalRows = (int) Math.ceil((gp.screenHeight - groundLevelScreen) / (double) gp.tileSize);
        if (additionalRows < 1) {
            additionalRows = 1;
        }

        for (int col = 0; col < gp.maxWorldCol; col++) {
            int tileX = col * gp.tileSize;
            int screenX = tileX - gp.cameraX; // Convert world X to screen X

            // Only draw tiles if they are within the visible screen horizontally
            if (screenX + gp.tileSize > 0 && screenX < gp.screenWidth) {
                // Draw from the ground top to cover the bottom of the screen
                for (int row = 0; row < additionalRows; row++) {
                    int tileY = groundLevelScreen + (row * gp.tileSize);
                    if (row == 0) {
                        // Top row: grass tile
                        g2.drawImage(tile[0].image, screenX, tileY, gp.tileSize, gp.tileSize, null);
                    } else {
                        // Subsequent rows: dirt tile
                        g2.drawImage(tile[1].image, screenX, tileY, gp.tileSize, gp.tileSize, null);
                    }
                }
            }
        }
    }
}
