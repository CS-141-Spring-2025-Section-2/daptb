/*
package daptb;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics2D; 
import java.awt.image.BufferedImage;

public class CSVMapLoader {
    private List<String[]> mapData = new ArrayList<>();
    private double scale;

    public CSVMapLoader(String levelFile) {
        this(levelFile, 1.0);
        System.out.println("Loading CSV from: " + getClass().getResource(levelFile));
    }

    public CSVMapLoader(String levelFile, double scale) {
        System.out.println("Initializing CSVMapLoader with file: " + levelFile);
        this.scale = scale;

        try {
            loadCSV(levelFile);
        } catch (IOException e) { // Catch IOException specifically
            System.err.println("❌ ERROR: Failed to load CSV file: " + levelFile);
            e.printStackTrace();
        }
    }

    public void loadCSV(String filePath) throws IOException { // Declare IOException
        java.net.URL fileURL = getClass().getResource(filePath);
        if (fileURL == null) {
            throw new FileNotFoundException("CSV file not found: " + filePath);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(fileURL.openStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("Line from CSV: " + line);
                mapData.add(line.split(","));
            }
        }
        System.out.println("Map Data: " + mapData);
    }



    public List<String[]> getMapData() {
        return mapData;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void draw(Graphics2D g2d, int xOffset, int yOffset, BufferedImage[] tiles) {
       if (mapData == null || tiles == null || g2d == null) {
           return;
       }

       for (int row = 0; row < mapData.size(); row++) {
           String[] line = mapData.get(row);
           for (int col = 0; col < line.length; col++) {
               try {
                   int tileIndex = Integer.parseInt(line[col]);
                   // Check if tileIndex is within bounds of the tiles array
                   if (tileIndex >= 1 && tileIndex <= tiles.length && tiles[tileIndex - 1] != null) { // Corrected bounds check
                       BufferedImage tile = tiles[tileIndex - 1]; // Corrected indexing
                       int x = col * tile.getWidth() * (int) scale + xOffset;
                       int y = row * tile.getHeight() * (int) scale + yOffset;
                       g2d.drawImage(tile, x, y, (int) (tile.getWidth() * scale), (int) (tile.getHeight() * scale), null);
                   } else {
                       System.err.println("Invalid tile index: " + tileIndex + " at row " + row + ", col " + col + ". tiles.length: " + tiles.length);
                   }
               } catch (NumberFormatException e) {
                   System.err.println("Invalid tile data (not an integer) at row " + row + ", col " + col + ": " + line[col]);
               }
           }
       }
   }
}
*/