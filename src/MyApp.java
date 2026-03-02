import javax.swing.*;

public class MyApp {

    public static String playerName;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            playerName = JOptionPane.showInputDialog("Enter your name:");
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "Guest";
            }

            JFrame frame = new JFrame("Brick Breaker Deluxe 🎮");
            GamePlay gameplay = new GamePlay(playerName);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.add(gameplay);

            // Normal window size (you can resize manually)
            frame.setSize(900, 700);

            // Allow resizing
            frame.setResizable(true);

            // Center on screen
            frame.setLocationRelativeTo(null);

            frame.setVisible(true);
        });
    }
}