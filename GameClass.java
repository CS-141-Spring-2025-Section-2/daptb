package daptb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GameClass extends JFrame {
    private static final long serialVersionUID = 1L;

    public GameClass() {
        setTitle("Guess and Conquer - Intro");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        InstructionsPanel instructionsPanel = new InstructionsPanel();
        add(instructionsPanel);
        setVisible(true);
        instructionsPanel.startAnimation();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameClass::new);
    }
}

class InstructionsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private BufferedImage backgroundImage;
    private int opacity = 0;
    private Timer animationTimer;
    private JLabel instructionLabel;
    private JButton nextButton;
    private int textIndex = 0;
    private boolean fadingOut = false;
    private final String[] instructionTexts = {
        "📜 Welcome, Traveler. You stand before an unknown land.\nYour mission is to uncover its secrets and claim it as your own.",
        "🔍 Explore your surroundings.\n🏺 Interact with objects.\n🗣️ Speak to characters.\n📖 Read scrolls carefully.",
        "❓ Use clues to guess the land's name and history.\n⌨️ Type your guess.\n📜 The closer you are, the stronger your army.\n⚠️ Wrong guesses weaken your forces.",
        "⚔️ Lead your army into battle!\n🎯 Deploy the best formations.\n🛡️ Adapt to unique conditions.\n🔥 Unlock special abilities.",
        "🏰 Secure victory and rebuild!\n🛠️ Upgrade settlements.\n🤝 Make choices that shape history.\n📖 Rewrite history in your favor."
    };

    public InstructionsPanel() {
        setLayout(null);
        setBackground(Color.BLACK);

        try {
            backgroundImage = ImageIO.read(new File("src/daptb/gamephoto.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        instructionLabel = new JLabel("", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Serif", Font.BOLD, 20));
        instructionLabel.setForeground(Color.WHITE);
        instructionLabel.setOpaque(true);
        instructionLabel.setBackground(new Color(0, 0, 0, 200));
        add(instructionLabel);
        
        nextButton = new JButton("Continue →");
        nextButton.setFont(new Font("Times New Roman", Font.BOLD, 18));
        nextButton.setFocusPainted(false);
        nextButton.setBorder(BorderFactory.createLineBorder(new Color(184, 134, 11), 3));
        nextButton.setBackground(new Color(139, 69, 19));
        nextButton.setForeground(Color.WHITE);
        nextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextButton.addActionListener(e -> startFadeOut());
        add(nextButton);
        
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    startFadeOut();
                }
            }
        });
    }

    public void startAnimation() {
        opacity = 0;
        fadingOut = false;
        animationTimer = new Timer(30, e -> {
            if (opacity < 200) {
                opacity += 10;
                repaint();
            } else {
                ((Timer) e.getSource()).stop();
                instructionLabel.setText("<html>" + instructionTexts[textIndex].replace("\n", "<br>") + "</html>");
            }
        });
        animationTimer.start();
    }

    private void startFadeOut() {
        if (!fadingOut) {
            fadingOut = true;
            animationTimer = new Timer(30, e -> {
                if (opacity > 0) {
                    opacity -= 10;
                    repaint();
                } else {
                    ((Timer) e.getSource()).stop();
                    showNextInstruction();
                }
            });
            animationTimer.start();
        }
    }

    private void showNextInstruction() {
        if (textIndex < instructionTexts.length - 1) {
            textIndex++;
            instructionLabel.setText("<html>" + instructionTexts[textIndex].replace("\n", "<br>") + "</html>");
            startAnimation();
        } else {
            nextButton.setText("Begin Your Quest →");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        g.setColor(new Color(0, 0, 0, opacity));
        int boxWidth = (int) (getWidth() * 0.6);
        int boxHeight = (int) (getHeight() * 0.3);
        int boxX = (getWidth() - boxWidth) / 2;
        int boxY = (getHeight() - boxHeight) / 2;
        instructionLabel.setBounds(boxX, boxY, boxWidth, boxHeight);
        nextButton.setBounds(boxX + (boxWidth / 4), boxY + boxHeight + 20, boxWidth / 2, 50);
        g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 30, 30);
    }
}
