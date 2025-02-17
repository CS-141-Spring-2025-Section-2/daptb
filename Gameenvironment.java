package sem1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class Gameenvironment extends JPanel implements KeyListener, Runnable, MouseListener, MouseMotionListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PLAYER_SPEED = 5;
    private static final int BULLET_SPEED = 10;
    private static final int PLAYER_HEALTH = 50;
    private static final int ENEMY_HEALTH = 5;
    private static final int ENEMIES_PER_WAVE = 5;
    private static final int TOTAL_ENEMIES = 20;
    private static final int ENEMY_SHOOT_DISTANCE = 200;
    private static final int ENEMY_SPEED = 2;
    private static final int ENEMY_STOP_DISTANCE = 100; // Distance at which enemies stop moving

    private int playerX, playerY;
    private int playerHealth;
    private boolean isGameRunning;
    private ArrayList<Enemy> enemies;
    private ArrayList<Bullet> playerBullets;
    private ArrayList<Bullet> enemyBullets;
    private ArrayList<Animation> animations; // For death animations
    private int currentLevel;
    private int enemiesRemaining;
    private Font customFont;
    private boolean[] keysPressed;
    private int mouseX, mouseY;
    private double playerAngle; // Angle for player rotation
    private String playerName; // Player's name
    private boolean showLevelCompleteBanner; // Flag to show level complete banner

    public Gameenvironment() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        playerX = WIDTH / 2;
        playerY = HEIGHT - 100;
        playerHealth = PLAYER_HEALTH;
        isGameRunning = true;
        enemies = new ArrayList<>();
        playerBullets = new ArrayList<>();
        enemyBullets = new ArrayList<>();
        animations = new ArrayList<>();
        currentLevel = 1;
        enemiesRemaining = TOTAL_ENEMIES;
        keysPressed = new boolean[4];
        mouseX = playerX;
        mouseY = playerY;
        playerAngle = 0; // Initial angle
        showLevelCompleteBanner = false;

        // Ask for player's name
        playerName = JOptionPane.showInputDialog(this, "Enter your name:", "Welcome to the Game!", JOptionPane.PLAIN_MESSAGE);
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Player"; // Default name
        }

        loadCustomFont();
        spawnEnemyWave();
    }

    private void loadCustomFont() {
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("custom_font.ttf")).deriveFont(24f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (Exception e) {
            System.out.println("Custom font not found! Using default font.");
            customFont = new Font("Arial", Font.BOLD, 24);
        }
    }

    private void spawnEnemyWave() {
        Random random = new Random();
        for (int i = 0; i < ENEMIES_PER_WAVE && enemiesRemaining > 0; i++) {
            int enemyX = random.nextInt(WIDTH - 50);
            int enemyY = random.nextInt(HEIGHT / 2);
            enemies.add(new Enemy(enemyX, enemyY, ENEMY_HEALTH, currentLevel));
            enemiesRemaining--;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw custom gradient background
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradient = new GradientPaint(0, 0, new Color(30, 30, 60), WIDTH, HEIGHT, new Color(10, 10, 30));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw player name banner
        drawPlayerNameBanner(g);

        // Draw player
        drawPlayer(g);

        // Draw enemies
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }

        // Draw bullets
        for (Bullet bullet : playerBullets) {
            bullet.draw(g);
        }
        for (Bullet bullet : enemyBullets) {
            bullet.draw(g);
        }

        // Draw animations
        for (int i = animations.size() - 1; i >= 0; i--) {
            Animation animation = animations.get(i);
            animation.draw(g);
            if (animation.isFinished()) {
                animations.remove(i);
            }
        }

        // Draw health bar
        drawHealthBar(g);

        // Draw level and enemies remaining
        g.setColor(Color.WHITE);
        g.setFont(customFont);
        g.drawString("Level: " + currentLevel, 10, 60);
        g.drawString("Enemies Remaining: " + (enemiesRemaining + enemies.size()), 10, 90);

        // Draw level complete banner if needed
        if (showLevelCompleteBanner) {
            drawLevelCompleteBanner(g);
        }
    }

    private void drawPlayerNameBanner(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(255, 255, 255, 150)); // Semi-transparent white
        g2d.fillRoundRect(10, 10, 200, 40, 20, 20); // Rounded rectangle
        g2d.setColor(Color.BLACK);
        g2d.setFont(customFont.deriveFont(20f));
        g2d.drawString("Player: " + playerName, 20, 35); // Player name
    }

    private void drawPlayer(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Draw player as a circle with a line indicating direction
        g2d.setColor(Color.BLUE);
        g2d.fillOval(playerX, playerY, 50, 50); // Player body

        // Draw a larger red line indicating the direction the player is facing
        int lineX = playerX + 25 + (int) (50 * Math.cos(playerAngle)); // Increased length
        int lineY = playerY + 25 + (int) (50 * Math.sin(playerAngle)); // Increased length
        g2d.setColor(Color.RED); // Changed to red
        g2d.setStroke(new BasicStroke(3)); // Thicker line
        g2d.drawLine(playerX + 25, playerY + 25, lineX, lineY);
    }

    private void drawHealthBar(Graphics g) {
        int barWidth = 200;
        int barHeight = 20;
        int barX = 10;
        int barY = 10;
        int healthWidth = (int) ((double) playerHealth / PLAYER_HEALTH * barWidth);

        g.setColor(Color.RED);
        g.fillRect(barX, barY, barWidth, barHeight);
        g.setColor(Color.GREEN);
        g.fillRect(barX, barY, healthWidth, barHeight);
        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);
    }

    private void drawLevelCompleteBanner(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 0, 200)); // Semi-transparent black
        g2d.fillRect(WIDTH / 2 - 150, HEIGHT / 2 - 100, 300, 200); // Banner background

        g2d.setColor(Color.WHITE);
        g2d.setFont(customFont.deriveFont(30f));
        g2d.drawString("Level " + currentLevel + " Complete!", WIDTH / 2 - 130, HEIGHT / 2 - 50);

        // Draw "Continue" button
        g2d.setColor(Color.GREEN);
        g2d.fillRect(WIDTH / 2 - 120, HEIGHT / 2 - 20, 100, 40);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Continue", WIDTH / 2 - 110, HEIGHT / 2 + 10);

        // Draw "Quit" button
        g2d.setColor(Color.RED);
        g2d.fillRect(WIDTH / 2 + 20, HEIGHT / 2 - 20, 100, 40);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Quit", WIDTH / 2 + 50, HEIGHT / 2 + 10);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) keysPressed[0] = true; // Left
        if (key == KeyEvent.VK_RIGHT) keysPressed[1] = true; // Right
        if (key == KeyEvent.VK_UP) keysPressed[2] = true; // Up
        if (key == KeyEvent.VK_DOWN) keysPressed[3] = true; // Down
        if (key == KeyEvent.VK_SPACE) {
            // Shoot in the direction the player is facing
            double bulletSpeedX = BULLET_SPEED * Math.cos(playerAngle);
            double bulletSpeedY = BULLET_SPEED * Math.sin(playerAngle);
            playerBullets.add(new Bullet(playerX + 25, playerY + 25, bulletSpeedX, bulletSpeedY, Color.RED)); // Red bullets
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) keysPressed[0] = false; // Left
        if (key == KeyEvent.VK_RIGHT) keysPressed[1] = false; // Right
        if (key == KeyEvent.VK_UP) keysPressed[2] = false; // Up
        if (key == KeyEvent.VK_DOWN) keysPressed[3] = false; // Down
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        // Update player angle based on mouse position
        mouseX = e.getX();
        mouseY = e.getY();
        playerAngle = Math.atan2(mouseY - (playerY + 25), mouseX - (playerX + 25));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // This method is required by MouseMotionListener but is not used in this game
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (showLevelCompleteBanner) {
            int x = e.getX();
            int y = e.getY();

            // Check if "Continue" button is clicked
            if (x >= WIDTH / 2 - 120 && x <= WIDTH / 2 - 20 && y >= HEIGHT / 2 - 20 && y <= HEIGHT / 2 + 20) {
                showLevelCompleteBanner = false;
                currentLevel++;
                enemiesRemaining = TOTAL_ENEMIES;
                spawnEnemyWave();
            }

            // Check if "Quit" button is clicked
            if (x >= WIDTH / 2 + 20 && x <= WIDTH / 2 + 120 && y >= HEIGHT / 2 - 20 && y <= HEIGHT / 2 + 20) {
                System.exit(0);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void run() {
        while (isGameRunning) {
            updateGame();
            repaint();
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        showGameOverPopup();
    }

    private void updateGame() {
        // Update player position based on keys pressed
        if (keysPressed[0] && playerX > 0) playerX -= PLAYER_SPEED; // Left
        if (keysPressed[1] && playerX < WIDTH - 50) playerX += PLAYER_SPEED; // Right
        if (keysPressed[2] && playerY > 0) playerY -= PLAYER_SPEED; // Up
        if (keysPressed[3] && playerY < HEIGHT - 50) playerY += PLAYER_SPEED; // Down

        // Move enemies and make them shoot
        for (Enemy enemy : enemies) {
            double distanceToPlayer = Math.hypot(playerX - enemy.x, playerY - enemy.y);
            if (distanceToPlayer > ENEMY_STOP_DISTANCE) {
                enemy.moveTowards(playerX, playerY, ENEMY_SPEED);
            } else {
                // Stop moving and shoot
                if (enemy.canShoot()) {
                    double angle = Math.atan2(playerY - enemy.y, playerX - enemy.x);
                    double bulletSpeedX = BULLET_SPEED * Math.cos(angle);
                    double bulletSpeedY = BULLET_SPEED * Math.sin(angle);
                    enemyBullets.add(new Bullet(enemy.x + 25, enemy.y + 25, bulletSpeedX, bulletSpeedY, Color.YELLOW));
                    enemy.resetShootCooldown();
                }
            }
        }

        // Move bullets
        for (int i = playerBullets.size() - 1; i >= 0; i--) {
            Bullet bullet = playerBullets.get(i);
            bullet.move();
            if (bullet.x < 0 || bullet.x > WIDTH || bullet.y < 0 || bullet.y > HEIGHT) {
                playerBullets.remove(i);
            }
        }
        for (int i = enemyBullets.size() - 1; i >= 0; i--) {
            Bullet bullet = enemyBullets.get(i);
            bullet.move();
            if (bullet.x < 0 || bullet.x > WIDTH || bullet.y < 0 || bullet.y > HEIGHT) {
                enemyBullets.remove(i);
            }
        }

        // Check collisions
        checkCollisions();

        // Check if current wave is cleared
        if (enemies.isEmpty() && enemiesRemaining > 0) {
            spawnEnemyWave();
        }

        // Check level completion
        if (enemies.isEmpty() && enemiesRemaining == 0) {
            showLevelCompleteBanner = true;
        }
    }

    private void checkCollisions() {
        // Player bullets hitting enemies
        for (int i = playerBullets.size() - 1; i >= 0; i--) {
            Bullet bullet = playerBullets.get(i);
            for (int j = enemies.size() - 1; j >= 0; j--) {
                Enemy enemy = enemies.get(j);
                if (bullet.x >= enemy.x && bullet.x <= enemy.x + 50 &&
                    bullet.y >= enemy.y && bullet.y <= enemy.y + 50) {
                    enemy.health--;
                    if (enemy.health <= 0) {
                        // Add death animation
                        animations.add(new Animation(enemy.x, enemy.y, 50, 50, 10));
                        enemies.remove(j);
                    }
                    playerBullets.remove(i);
                    break;
                }
            }
        }

        // Enemy bullets hitting player
        for (int i = enemyBullets.size() - 1; i >= 0; i--) {
            Bullet bullet = enemyBullets.get(i);
            if (bullet.x >= playerX && bullet.x <= playerX + 50 &&
                bullet.y >= playerY && bullet.y <= playerY + 50) {
                playerHealth--;
                enemyBullets.remove(i);
                if (playerHealth <= 0) {
                    isGameRunning = false;
                }
                break;
            }
        }
    }

    private void showGameOverPopup() {
        String[] options = {"Restart", "Quit"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Game Over!",
            "Game Over",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choice == 0) {
            restartGame();
        } else {
            System.exit(0);
        }
    }

    private void restartGame() {
        playerX = WIDTH / 2;
        playerY = HEIGHT - 100;
        playerHealth = PLAYER_HEALTH;
        isGameRunning = true;
        enemies.clear();
        playerBullets.clear();
        enemyBullets.clear();
        animations.clear();
        currentLevel = 1;
        enemiesRemaining = TOTAL_ENEMIES;
        showLevelCompleteBanner = false;

        spawnEnemyWave();
        new Thread(this).start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Game Environment");
        Gameenvironment game = new Gameenvironment();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Thread(game).start();
    }

    // Inner classes for Enemy, Bullet, and Animation
    class Enemy {
        int x, y, health;
        private long lastShotTime;
        private static final long SHOOT_COOLDOWN = 1000; // 1 second cooldown
        private int level;

        Enemy(int x, int y, int health, int level) {
            this.x = x;
            this.y = y;
            this.health = health;
            this.lastShotTime = System.currentTimeMillis();
            this.level = level;
        }

        void moveTowards(int targetX, int targetY, int speed) {
            double angle = Math.atan2(targetY - y, targetX - x);
            x += speed * Math.cos(angle);
            y += speed * Math.sin(angle);
        }

        boolean canShoot() {
            return System.currentTimeMillis() - lastShotTime >= SHOOT_COOLDOWN / level; // Faster shooting at higher levels
        }

        void resetShootCooldown() {
            lastShotTime = System.currentTimeMillis();
        }

        void draw(Graphics g) {
            // Draw a ragged enemy shape
            g.setColor(new Color(200, 50, 50)); // Dark red
            int[] xPoints = {x + 10, x + 40, x + 50, x + 30, x + 20, x + 10};
            int[] yPoints = {y + 10, y + 20, y + 50, y + 40, y + 30, y + 10};
            g.fillPolygon(xPoints, yPoints, 6);
        }
    }

    class Bullet {
        double x, y, speedX, speedY;
        Color color;

        Bullet(double x, double y, double speedX, double speedY, Color color) {
            this.x = x;
            this.y = y;
            this.speedX = speedX;
            this.speedY = speedY;
            this.color = color;
        }

        void move() {
            x += speedX;
            y += speedY;
        }

        void draw(Graphics g) {
            g.setColor(color);
            g.fillOval((int) x, (int) y, 10, 10);
        }
    }

    class Animation {
        int x, y, width, height, frames;
        int currentFrame;

        Animation(int x, int y, int width, int height, int frames) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.frames = frames;
            this.currentFrame = 0;
        }

        void draw(Graphics g) {
            if (currentFrame < frames) {
                g.setColor(new Color(255, 0, 0, 255 - (currentFrame * 25))); // Fading effect
                g.fillOval(x + currentFrame * 2, y + currentFrame * 2, width - currentFrame * 4, height - currentFrame * 4);
                currentFrame++;
            }
        }

        boolean isFinished() {
            return currentFrame >= frames;
        }
    }
}