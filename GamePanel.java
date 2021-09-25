import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import java.util.ArrayList;
import java.awt.event.*;

public class GamePanel extends JPanel implements Runnable, KeyListener {

    // FIELDS | CAMPOS - ATRIBUTOS
    public static int WIDTH = 480;
    public static int HEIGHT = 360;

    private Thread thread;
    private boolean running;

    private BufferedImage image;
    private Graphics2D g;

    private int FPS = 30;
    private double averageFPS;

    public static Player player;
    public static ArrayList<Bullet> bullets;
    public static ArrayList<Enemy> enemies;

    private long waveStartTime;
    private long waveStartTimeDiff;
    private int waveNumber;
    private boolean waveStart;
    private int waveDelay = 2000;

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
        addKeyListener(this);
    }

    public void run() {
        running = true;
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        player = new Player();
        bullets = new ArrayList<Bullet>();
        enemies = new ArrayList<Enemy>();

        waveStartTime = 0;
        waveStartTimeDiff = 0;
        waveStart = true;
        waveNumber = 0;

        long startTime;
        long URDTimeMillis;
        long waitTime;

        long totalTime = 0;

        int frameCount = 0;
        int maxFrameCount = 30;

        long targetTime = 1000 / FPS;

        // GAME LOOP
        while(running) {
            startTime = System.nanoTime();

            gameUpdate();
            gameRender();
            gameDraw();

            URDTimeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - URDTimeMillis;

            try{
                Thread.sleep(waitTime);
            } catch(Exception ex) {
                System.out.println(ex);
            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;

            if (frameCount == maxFrameCount) {
                averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
            }
        }
    }

    private void gameUpdate() {
        // NEW WAVE
        if (waveStartTime == 0 && enemies.size() == 0) {
            waveNumber++;
            waveStart = false;
            waveStartTime = System.nanoTime();
        } else {
            waveStartTimeDiff = (System.nanoTime() - waveStartTime) / 1000000;
            if (waveStartTimeDiff > waveDelay) {
                waveStart = true;
                waveStartTime = 0;
                waveStartTimeDiff = 0;
            }
        }

        // CREATE ENEMIES
        if (waveStart && enemies.size() == 0) {
            createNewEnemies();
        }

        // PLAYER UPDATE
        player.update();
        
        // BULLETS UPDATE
        for (int i = 0; i < bullets.size(); i++) {
            boolean remove = bullets.get(i).update();
            if (remove) {
                bullets.remove(i);
                i--;
            }
        }

        // ENEMY UPDATE
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).update();
        }

        // COLLISION
        // Bullet -> Enemy
        for (int b = 0; b < bullets.size(); b++) {

            // get bullet position
            Bullet bullet = bullets.get(b);
            double bx = bullet.getX();
            double by = bullet.getY();
            double br = bullet.getR();

            for (int e = 0; e < enemies.size(); e++) {

                // get enemy position
                Enemy enemy = enemies.get(e);
                double ex = enemy.getX();
                double ey = enemy.getY();
                double er = enemy.getR();
    
                // get delta between two points | calc dist between two points
                double dx = bx - ex;
                double dy = by - ey;
                double dist = Math.sqrt(dx * dx + dy * dy);

                // check if dist is equals a collision
                if (dist < br + er) {
                    enemy.hit();
                    bullets.remove(b);
                    b--;
                    break;
                }
            }
        }

        // CHECK DEAD ENEMIES
        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).isDead()) {
                enemies.remove(i);
                i--;
            }
        }

    }

    private void gameRender() {
        // DRAW BACKGROUND
        g.setColor(Color.decode("#212121"));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // DRAW UI (User Interface)
        g.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        g.setColor(Color.decode("#fafafa"));
        int formatedFPS = (int) averageFPS;
        g.drawString("FPS: " + formatedFPS, 8, 16); 

        // DRAW PLAYER LIVES
        for (int i = 0; i < player.getLives(); i++) {
            g.setColor(Color.decode("#F44336"));
            g.fillOval((WIDTH - 20 * i) - 20, 8, 10, 10);
            g.setStroke(new BasicStroke(3));
            g.setColor(Color.decode("#F44336").darker());
            g.drawOval((WIDTH - 20 * i) - 20, 8, 10, 10);
            g.setStroke(new BasicStroke(1));
        }
        
        // DRAW PLAYER
        player.draw(g);

        // DRAW BULLETS
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).draw(g);
        }

        // DRAW ENEMY
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).draw(g);
        }

        // DRAW WAVE NUMBER
        if (waveStartTime != 0) {
            g.setFont(new Font("Century Gothic", Font.PLAIN, 18));
            String s = "- W A V E   " + waveNumber + "  -";
            int lenght = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
            int alpha = (int) (255 * Math.sin(3.14 * waveStartTimeDiff / waveDelay));
            if(alpha > 255) alpha = 255;
            g.setColor(new Color(255, 255, 255, alpha));
            g.drawString(s, WIDTH / 2 - lenght / 2, HEIGHT / 2); 
        }

    }

    private void gameDraw() {
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }

    private void createNewEnemies() {
        enemies.clear();
        Enemy e;

        if (waveNumber == 1) {
            for (int i = 0; i < 10; i++) {
                enemies.add(new Enemy(1, 1));
            }
        }
        if (waveNumber == 2) {
            for (int i = 0; i < 7; i++) {
                enemies.add(new Enemy(1, 2));
            }
        }
        if (waveNumber == 3) {
            for (int i = 0; i < 5; i++) {
                enemies.add(new Enemy(1, 3));
            }
        }
    }

    public void keyTyped(KeyEvent key) {}
    public void keyPressed(KeyEvent key) {
        int keyCode = key.getKeyCode();
        if(keyCode == KeyEvent.VK_LEFT){
            player.setLeft(true);
        }
        if(keyCode == KeyEvent.VK_RIGHT){
            player.setRight(true);
        }
        if(keyCode == KeyEvent.VK_UP){
            player.setUp(true);
        }
        if(keyCode == KeyEvent.VK_DOWN){
            player.setDown(true);
        }
        if(keyCode == KeyEvent.VK_SPACE){
            player.setFiring(true);
        }
    }
    public void keyReleased(KeyEvent key) {
        int keyCode = key.getKeyCode();
        if(keyCode == KeyEvent.VK_LEFT){
            player.setLeft(false);
        }
        if(keyCode == KeyEvent.VK_RIGHT){
            player.setRight(false);
        }
        if(keyCode == KeyEvent.VK_UP){
            player.setUp(false);
        }
        if(keyCode == KeyEvent.VK_DOWN){
            player.setDown(false);
        }
        if(keyCode == KeyEvent.VK_SPACE){
            player.setFiring(false);
        }
    }
}
