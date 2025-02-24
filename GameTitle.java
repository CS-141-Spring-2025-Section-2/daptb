package daptb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GameTitle {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public class GameClass extends JFrame {
	    private static final long serialVersionUID = 1L;
	    private BufferedImage backgroundImage;
	    private String[] backgroundImages = {
	        "C:\\Users\\aabdelfatah\\Downloads\\q.jpg",
	        "C:\\Users\\aabdelfatah\\Downloads\\2.jpg",
	        "C:\\Users\\aabdelfatah\\Downloads\\3.jpg",
	        "C:\\Users\\aabdelfatah\\Downloads\\4.jpg",
	        "C:\\Users\\aabdelfatah\\Downloads\\5.jpg"
	    };
	    private int backgroundIndex = 0;

	    public GameClass() {
	        setTitle("Guess and Conquer");
	        setSize(800, 600);
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setLocationRelativeTo(null);
	        setLayout(null);

	        loadBackgroundImage();

	        JLabel titleLabel = new JLabel("Guess and Conquer", SwingConstants.CENTER);
	        titleLabel.setFont(new Font("Serif", Font.BOLD, 32));
	        titleLabel.setForeground(Color.WHITE);
	        titleLabel.setBounds(250, 50, 300, 50);
	        add(titleLabel);

	        JButton newGameButton = createButton("New Game", 300, 150);
	        JButton startButton = createButton("Start", 300, 250);
	        JButton continueButton = createButton("Continue", 300, 350);

	        newGameButton.addActionListener(e -> startNewGame());
	        startButton.addActionListener(e -> startGame());
	        continueButton.addActionListener(e -> continueGame());

	        add(newGameButton);
	        add(startButton);
	        add(continueButton);
	    }

	    private void loadBackgroundImage() {
	        try {
	            backgroundImage = ImageIO.read(new File(backgroundImages[backgroundIndex]));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    private JButton createButton(String text, int x, int y) {
	        JButton button = new JButton(text);
	        button.setFont(new Font("Times New Roman", Font.BOLD, 20));
	        button.setBounds(x, y, 200, 50);
	        button.setFocusPainted(false);
	        button.setBackground(new Color(139, 69, 19));
	        button.setForeground(Color.WHITE);
	        button.setBorder(BorderFactory.createLineBorder(new Color(184, 134, 11), 3));
	        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
	        button.addActionListener(e -> changeBackground());
	        return button;
	    }

	    private void changeBackground() {
	        backgroundIndex = (backgroundIndex + 1) % backgroundImages.length;
	        loadBackgroundImage();
	        repaint();
	    }

	    private void startNewGame() {
	        dispose();
	        SwingUtilities.invokeLater(() -> new InstructionsPanelFrame());
	    }

	    private void startGame() {
	        JOptionPane.showMessageDialog(this, "Game Started!");
	    }

	    private void continueGame() {
	        JOptionPane.showMessageDialog(this, "Continuing from last save!");
	    }
	    class InstructionsPanelFrame extends JFrame {
	        public InstructionsPanelFrame() {
	            setTitle("Instructions");
	            setSize(800, 600);
	            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	            setLocationRelativeTo(null);
	            add(new InstructionsPanel());
	            setVisible(true);
	        }
	    }

	    class InstructionsPanel extends JPanel {
	        private static final long serialVersionUID = 1L;
	        private JLabel instructionLabel;
	        private JButton nextButton;
	        private int textIndex = 0;
	        private String displayedText = "";
	        private Timer typingTimer;
	        private int charIndex = 0;
	        
	        private final String[] instructionTexts = {
	            "📜 Welcome, Traveler.\\n\\nYou stand before an unknown land. Your mission is to uncover its secrets and claim it as your own.",
	            "🔍 Exploration Phase\\n\\nSearch for clues to uncover history.\\n🏺 Interact with objects.\\n🗣️ Talk to characters.\\n📖 Read inscriptions carefully.",
	            "❓ Guessing Phase\\n\\nUse gathered clues to name the land.\\n⌨️ Type your guess.\\n📜 Correct guesses strengthen your army.\\n⚠️ Wrong guesses make battle harder.",
	            "⚔️ Battle Phase\\n\\nLead your army to victory!\\n🎯 Use strategy to deploy formations.\\n🛡️ Adapt to unique battle conditions.\\n🔥 Unlock special abilities.",
	            "🏰 Rebuilding Phase\\n\\nVictory is yours! Now, restore your empire.\\n🛠️ Upgrade settlements.\\n🤝 Shape history with your choices.\\n📖 Rewrite the fate of your land.",
	            "🚀 Your Journey Continues...\\n\\nNew lands await, but the challenges grow stronger.\\n\\n🏆 Will you rise as the ultimate conqueror?"
	        };

	        public InstructionsPanel() {
	            setLayout(null);
	            setBackground(Color.BLACK);
	            
	            instructionLabel = new JLabel("", SwingConstants.CENTER);
	            instructionLabel.setFont(new Font("Serif", Font.BOLD, 24));
	            instructionLabel.setForeground(Color.WHITE);
	            instructionLabel.setBounds(100, 100, 600, 150);
	            add(instructionLabel);

	            nextButton = new JButton("Continue →");
	            nextButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
	            nextButton.setBounds(300, 400, 200, 50);
	            nextButton.setEnabled(false);
	            nextButton.addActionListener(e -> showNextInstruction());
	            add(nextButton);
	            
	            startTypingAnimation();
	        }

	        public void startTypingAnimation() {
	            charIndex = 0;
	            displayedText = "";
	            nextButton.setEnabled(false);

	            typingTimer = new Timer(50, e -> {
	                if (charIndex < instructionTexts[textIndex].length()) {
	                    displayedText += instructionTexts[textIndex].charAt(charIndex);
	                    instructionLabel.setText("<html>" + displayedText.replace("\\n", "<br>") + "</html>");
	                    charIndex++;
	                } else {
	                    ((Timer) e.getSource()).stop();
	                    nextButton.setEnabled(true);
	                }
	            });
	            typingTimer.start();
	        }

	        private void showNextInstruction() {
	            if (textIndex < instructionTexts.length - 1) {
	                textIndex++;
	                startTypingAnimation();
	            } else {
	                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
	                if (parentFrame != null) {
	                    parentFrame.dispose();
	                }
	            }
	        }
	    }

	    @Override
	    public void paint(Graphics g) {
	        super.paint(g);
	        if (backgroundImage != null) {
	            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
	        }
	    }

	    public static void main(String[] args) {
	        SwingUtilities.invokeLater(() -> {
	            GameClass menu = new GameClass();
	            menu.setVisible(true);
	        });
	    }
	}


}
