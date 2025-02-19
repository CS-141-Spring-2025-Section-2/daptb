package daptb;

import javax.swing.*;
import java.awt.*;

public class FinalLevelVolcanoCastlePanel extends JPanel {
    private JFrame parentFrame;
    
    public FinalLevelVolcanoCastlePanel() {
    	this.parentFrame = parentFrame;
        initialize();  // Ensure you have an initialize() method or setup code
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);  // Example background
        setPreferredSize(new Dimension(800, 600));  // Set consistent level size
        parentFrame.pack();
        parentFrame.setLocationRelativeTo(null);  // Center window

        // Add level setup code (e.g., add enemies, player, map)
    }

    
    public FinalLevelVolcanoCastlePanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Welcome to the Final Level: Volcano Castle!", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 40));
        add(label, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Main Menu");
        backButton.setFont(new Font("Arial", Font.BOLD, 30));
        backButton.addActionListener(e -> {
            parentFrame.getContentPane().removeAll();
            parentFrame.add(new FinalLevelVolcanoCastlePanel(parentFrame));  // ✅ Correct Return to Level One
            parentFrame.revalidate();
            parentFrame.repaint();
        });

        add(backButton, BorderLayout.SOUTH);
    }
}
