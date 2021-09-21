import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

public class GamePanel extends JPanel implements Runnable {

    // FIELDS | CAMPOS
    public static int WIDTH = 480;
    public static int HEIGHT = 360;

    private Thread thread;
    private boolean running;

    private BufferedImage image;
    private Graphics2D g;

    // CONSTRUCTOR | CONSTRUTOR
    public GamePanel() {
        super();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
    }

    // FUNCTIONS | FUNÇÔES
    public void addNotify() {
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void run() {
        running = true;
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();

        // GAME LOOP
        while(running) {
            gameUpdate();
            gameRender();
            gameDraw();
        }
    }

    private void gameUpdate() {

    }

    private void gameRender() {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.BLACK);
        g.drawString("TESTE STRING", 100, 100);
    }

    private void gameDraw() {
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }

}
