import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GamePlay extends JPanel implements ActionListener {

    private boolean play = true;
    private boolean moveLeft = false;
    private boolean moveRight = false;

    private int rows = 3;
    private int cols = 7;
    private int[][] bricks;

    private int totalBricks;
    private int score = 0;

    private Timer timer;
    private int delay = 10;

    private float playerX;
    private float ballX, ballY;
    private float ballDX = 2.5f;   // 🔥 slow start
    private float ballDY = -3f;

    private float maxSpeed = 9f;

    private String playerName;

    public GamePlay(String playerName) {
        this.playerName = playerName;
        initGame();
        setFocusable(true);
        setBackground(Color.BLACK);
        setupKeyBindings();

        timer = new Timer(delay, this);
        timer.start();
    }

    private void initGame() {
        bricks = new int[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                bricks[i][j] = 1;

        totalBricks = rows * cols;
        score = 0;

        ballDX = 2.5f;
        ballDY = -3f;
        play = true;
    }

    private void setupKeyBindings() {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        im.put(KeyStroke.getKeyStroke("pressed LEFT"), "leftPressed");
        im.put(KeyStroke.getKeyStroke("released LEFT"), "leftReleased");
        im.put(KeyStroke.getKeyStroke("pressed RIGHT"), "rightPressed");
        im.put(KeyStroke.getKeyStroke("released RIGHT"), "rightReleased");
        im.put(KeyStroke.getKeyStroke("pressed ENTER"), "restart");

        am.put("leftPressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { moveLeft = true; }
        });

        am.put("leftReleased", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { moveLeft = false; }
        });

        am.put("rightPressed", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { moveRight = true; }
        });

        am.put("rightReleased", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { moveRight = false; }
        });

        am.put("restart", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!play) initGame();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();

        Graphics2D g2d = (Graphics2D) g;

        GradientPaint gp = new GradientPaint(
                0, 0, new Color(25, 25, 112),
                0, height, Color.BLACK);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, width, height);

        int topMargin = height / 8;
        int brickAreaWidth = width - width / 4;
        int brickWidth = brickAreaWidth / cols;
        int brickHeight = height / 20;
        int startX = (width - brickAreaWidth) / 2;

        // Draw bricks
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (bricks[i][j] == 1) {
                    int x = startX + j * brickWidth;
                    int y = topMargin + i * brickHeight;
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(x, y, brickWidth - 2, brickHeight - 2);
                }
            }
        }

        int paddleWidth = width / 8;
        int paddleHeight = 12;
        int paddleY = height - height / 10;

        if (playerX == 0) {
            playerX = width / 2f - paddleWidth / 2f;
            ballX = width / 2f;
            ballY = height / 2f;
        }

        g.setColor(Color.CYAN);
        g.fillRect((int) playerX, paddleY, paddleWidth, paddleHeight);

        int ballSize = width / 60;
        g.setColor(Color.GREEN);
        g.fillOval((int) ballX, (int) ballY, ballSize, ballSize);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Player: " + playerName, 30, 40);
        g.drawString("Score: " + score, width - 150, 40);

        if (!play) {
            g.setFont(new Font("Arial", Font.BOLD, 50));
            String msg = (totalBricks == 0) ? "YOU WIN!" : "GAME OVER";
            int strWidth = g.getFontMetrics().stringWidth(msg);
            g.drawString(msg, (width - strWidth) / 2, height / 2);

            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Press ENTER to Restart",
                    width / 2 - 110, height / 2 + 40);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        int width = getWidth();
        int height = getHeight();

        int paddleWidth = width / 8;
        int paddleY = height - height / 10;
        int ballSize = width / 60;

        if (!play) {
            repaint();
            return;
        }

        // Paddle movement
        if (moveLeft && playerX > 0)
            playerX -= 6;
        if (moveRight && playerX < width - paddleWidth)
            playerX += 6;

        ballX += ballDX;
        ballY += ballDY;

        // Wall bounce
        if (ballX < 0 || ballX > width - ballSize)
            ballDX = -ballDX;

        if (ballY < 0)
            ballDY = -ballDY;

        // Paddle bounce with angle control
        if (ballY + ballSize >= paddleY &&
                ballX + ballSize >= playerX &&
                ballX <= playerX + paddleWidth) {

            float hitPos = (ballX - playerX) / paddleWidth - 0.5f;
            ballDX += hitPos * 2f;
            ballDY = -Math.abs(ballDY);
        }

        // Brick collision
        int topMargin = height / 8;
        int brickAreaWidth = width - width / 4;
        int brickWidth = brickAreaWidth / cols;
        int brickHeight = height / 20;
        int startX = (width - brickAreaWidth) / 2;

        Rectangle ballRect = new Rectangle(
                (int) ballX, (int) ballY, ballSize, ballSize);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                if (bricks[i][j] == 1) {

                    int brickX = startX + j * brickWidth;
                    int brickY = topMargin + i * brickHeight;

                    Rectangle brickRect = new Rectangle(
                            brickX, brickY,
                            brickWidth - 2, brickHeight - 2);

                    if (ballRect.intersects(brickRect)) {

                        bricks[i][j] = 0;
                        totalBricks--;
                        score += 10;

                        // Increase speed gradually
                        float speed = (float)Math.sqrt(ballDX * ballDX + ballDY * ballDY);
                        if (speed < maxSpeed) {
                            ballDX *= 1.05f;
                            ballDY *= 1.05f;
                        }

                        ballDY = -ballDY;
                        break;
                    }
                }
            }
        }

        if (ballY > height)
            play = false;

        if (totalBricks == 0)
            play = false;

        repaint();
    }
}