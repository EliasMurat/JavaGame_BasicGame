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
    public static ArrayList<PowerUp> powerUps;
    public static ArrayList<Explosion> explosions;
    public static ArrayList<Text> texts;
    
    private long waveStartTime;
    private long waveStartTimeDiff;
    private int waveNumber;
    private boolean waveStart;
    private int waveDelay = 2000;

    private long slowDownTime;
    private long slowDownTimeDiff;
    private int slowDownLength = 6000;

    private String STATUS = "PLAY";

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
        powerUps = new ArrayList<PowerUp>();
        explosions = new ArrayList<Explosion>();
        texts = new ArrayList<Text>();

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

            if (STATUS.equals("WIN")) {
                g.setColor(Color.decode("#212121"));
                g.fillRect(0, 0, WIDTH, HEIGHT);
                
                g.setColor(Color.decode("#fafafa"));
                g.setFont(new Font("Century Gothic", Font.BOLD, 18));
                String s1 = "- Y O U   W I N -";
                int lenghtS1 = (int) g.getFontMetrics().getStringBounds(s1, g).getWidth();
                g.drawString(s1, (WIDTH - lenghtS1) / 2, (HEIGHT / 2) - 24);
                
                g.setColor(Color.decode("#fafafa"));
                g.setFont(new Font("Century Gothic", Font.BOLD, 14));
                String s2 = "SCORE: " + player.getScore();
                int lenghtS2 = (int) g.getFontMetrics().getStringBounds(s2, g).getWidth();
                g.drawString(s2, (WIDTH - lenghtS2) / 2, (HEIGHT / 2) + 12);

                g.setColor(Color.decode("#fafafa"));
                g.setFont(new Font("Century Gothic", Font.BOLD, 14));
                String s3 = ">>> PRESS ENTER TO PLAY AGAIN <<<";
                int lenghtS3 = (int) g.getFontMetrics().getStringBounds(s3, g).getWidth();
                g.drawString(s3, (WIDTH - lenghtS3) / 2, (HEIGHT / 2) + 48);

                gameDraw();
            } 
            
            if (STATUS.equals("LOSE")) {
                g.setColor(Color.decode("#212121"));
                g.fillRect(0, 0, WIDTH, HEIGHT);
                
                g.setColor(Color.decode("#fafafa"));
                g.setFont(new Font("Century Gothic", Font.BOLD, 18));
                String s1 = "- G A M E   O V E R -";
                int lenghtS1 = (int) g.getFontMetrics().getStringBounds(s1, g).getWidth();
                g.drawString(s1, (WIDTH - lenghtS1) / 2, (HEIGHT / 2) - 24);
                
                g.setColor(Color.decode("#fafafa"));
                g.setFont(new Font("Century Gothic", Font.BOLD, 14));
                String s2 = "SCORE: " + player.getScore();
                int lenghtS2 = (int) g.getFontMetrics().getStringBounds(s2, g).getWidth();
                g.drawString(s2, (WIDTH - lenghtS2) / 2, (HEIGHT / 2) + 12);

                g.setColor(Color.decode("#fafafa"));
                g.setFont(new Font("Century Gothic", Font.BOLD, 14));
                String s3 = ">>> PRESS ENTER TO PLAY AGAIN <<<";
                int lenghtS3 = (int) g.getFontMetrics().getStringBounds(s3, g).getWidth();
                g.drawString(s3, (WIDTH - lenghtS3) / 2, (HEIGHT / 2) + 48);

                gameDraw();
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

        // POWER-UP UPDATE
        for (int i = 0; i < powerUps.size(); i++) {
            boolean remove = powerUps.get(i).update();
            if (remove) {
                powerUps.remove(i);
                i--;
            }
        }

        // EXPLOSION UPDATE
        for (int i = 0; i < explosions.size(); i++) {
            boolean remove = explosions.get(i).update();
            if (remove) {
                explosions.remove(i);
                i--;
            }
        }

        // TEXTS UPDATE
        for (int i = 0; i < texts.size(); i++) {
            boolean remove = texts.get(i).update();
            if (remove) {
                texts.remove(i);
                i--;
            }
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
                    if (!enemy.isRecovering()) {
                        enemy.hit();
                    }
                    bullets.remove(b);
                    b--;
                    break;
                }
            }
        }

        // Enemy -> Player
        if (!player.isRecovering()) {
            // get player position
            int px = (int) player.getX();
            int py = (int) player.getY();
            int pr = (int) player.getR();
            
            for (int e = 0; e < enemies.size(); e++) {
                // get enemy position
                Enemy enemy = enemies.get(e);
                double ex = enemy.getX();
                double ey = enemy.getY();
                double er = enemy.getR();
                
                // get delta between two points | calc dist between two points
                double dx = px - ex;
                double dy = py - ey;
                double dist = Math.sqrt(dx * dx + dy * dy);

                // check if dist is equals a collision
                if (dist < pr + er) {
                    player.loseLife();
                }
            }
        }

        // Player -> Power Up
        // get player position
        int px = (int) player.getX();
        int py = (int) player.getY();
        int pr = (int) player.getR();
        for (int pu = 0; pu < powerUps.size(); pu++) {
            // get power up position
            PowerUp powerUp = powerUps.get(pu);
            double pux = powerUp.getX();
            double puy = powerUp.getY();
            double pur = powerUp.getR();

            // get delta between two points | calc dist between two points
            double dx = px - pux;
            double dy = py - puy;
            double dist = Math.sqrt(dx * dx + dy * dy);

            // check if dist is equals a collision 
            if (dist < pr + pur) {

                // get power up type
                int type = powerUp.getType();

                // check type
                if (type == 1) {
                    player.gainLife();
                    texts.add(new Text(player.getX(), player.getY(), 1000, "+1 LIFE"));
                }
                if (type == 2) {
                    player.increasePower(1);
                    texts.add(new Text(player.getX(), player.getY(), 1000, "+1 POWER"));
                }
                if (type == 3) {
                    player.increasePower(3);
                    texts.add(new Text(player.getX(), player.getY(), 1000, "+2 POWER"));
                }
                if (type == 4) {
                    player.increaseSpeed(1);
                    texts.add(new Text(player.getX(), player.getY(), 1000, "+1 SPEED"));
                }
                if (type == 5) {
                    slowDownTime = System.nanoTime();
                    for (int i = 0; i < enemies.size(); i++) {
                        enemies.get(i).setSlow(true);
                    }
                    texts.add(new Text(player.getX(), player.getY(), 1000, "SLOW DOWN"));
                }

                // remove power up | collected power up
                powerUps.remove(pu);
                pu--;

            }
        }

        // SLOWDOWN UPDATE
        if (slowDownTime != 0) {
            slowDownTimeDiff = (System.nanoTime() - slowDownTime) / 1000000;
            if (slowDownTimeDiff > slowDownLength) {
                slowDownTime = 0;
                for (int i = 0; i < enemies.size(); i++) {
                    enemies.get(i).setSlow(false);
                }
            }
        }

        // CHECK PLAYER IS DEAD
        if(player.isDead()) {
            STATUS = "LOSE";
        }

        // CHECK DEAD ENEMIES
        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).isDead()) {
                // Get enemy
                Enemy e = enemies.get(i);
                
                // Change for power up
                double rand = Math.random();
                if (rand < 0.025 && player.getSpeed() < 10) powerUps.add(new PowerUp(4, e.getX(), e.getY()));
                else if (rand < 0.050) powerUps.add(new PowerUp(5, e.getX(), e.getY()));
                else if (rand < 0.075 && player.getPower() < 10) powerUps.add(new PowerUp(3, e.getX(), e.getY()));
                else if (rand < 0.100 && player.getPower() < 10) powerUps.add(new PowerUp(2, e.getX(), e.getY()));
                else if (rand < 0.125 && player.getLives() < 10) powerUps.add(new PowerUp(1, e.getX(), e.getY()));
                
                // Add score
                player.addScore(e.getType() + e.getRank());
                
                // Remove enemy
                enemies.remove(i);
                i--;

                // Enemy explode
                e.explode();
                explosions.add(new Explosion(e.getX(), e.getY(), (int) e.getR(), (int) e.getR() + 20));
                
                // Add slow down in new enemies
                if (slowDownTime != 0) {
                    for (int j = 0; j < enemies.size(); j++) {
                        enemies.get(j).setSlow(true);
                    }
                }
            }
        }

    }

    private void gameRender() {
        if (STATUS.equals("PLAY")) {
            // DRAW BACKGROUND
            g.setColor(Color.decode("#212121"));
            g.fillRect(0, 0, WIDTH, HEIGHT);

            // DRAW SLOWDOWN SCREEN
            if (slowDownTime != 0) {
                g.setColor(new Color(0, 188, 212, 64));
                g.fillRect(0, 0, WIDTH, HEIGHT);
            }
        
            // DRAW ENTITIES
            // DRAW BULLETS
            for (int i = 0; i < bullets.size(); i++) {
                bullets.get(i).draw(g);
            }

            // DRAW EXPLOSIONS
            for (int i = 0; i < explosions.size(); i++) {
                explosions.get(i).draw(g);
            }

            // DRAW ENEMY
            for (int i = 0; i < enemies.size(); i++) {
                enemies.get(i).draw(g);
            }

            // DRAW POWER-UP
            for (int i = 0; i < powerUps.size(); i++) {
                powerUps.get(i).draw(g);
            }

            // DRAW PLAYER
            player.draw(g);

            // DRAW TEXTS
            for (int i = 0; i < texts.size(); i++) {
                texts.get(i).draw(g);
            }

            // DRAW UI (User Interface)
            // FPS
            g.setFont(new Font("Century Gothic", Font.PLAIN, 12));
            g.setColor(Color.decode("#fafafa"));
            int formatedFPS = (int) averageFPS;
            g.drawString("FPS: " + formatedFPS, 8, 16); 

            // DRAW SCORE
            g.setFont(new Font("Century Gothic", Font.PLAIN, 12));
            g.setColor(Color.decode("#fafafa"));
            g.drawString("SCORE: " + player.getScore(), 6, 38); 

            // DRAW PLAYER LIVES
            for (int i = 0; i < player.getLives(); i++) {
                g.setColor(Color.decode("#F44336"));
                g.fillOval((WIDTH - 20 * i) - 20, 8, 10, 10);
                g.setStroke(new BasicStroke(3));
                g.setColor(Color.decode("#F44336").darker());
                g.drawOval((WIDTH - 20 * i) - 20, 8, 10, 10);
                g.setStroke(new BasicStroke(1));
            }

            // DRAW PLAYER POWER
            for (int i = 0; i < player.getPower(); i++) {
                g.setColor(Color.decode("#9E9E9E"));
                g.fillRect((WIDTH - 20 * i) - 20, 32, 10, 10);
                g.setStroke(new BasicStroke(3));
                g.setColor(Color.decode("#9E9E9E").darker());
                g.drawRect((WIDTH - 20 * i) - 20, 32, 10, 10);
                g.setStroke(new BasicStroke(1));
            }

            // DRAW SLOWDOWN
            if (slowDownTime != 0) {
                g.setColor(Color.decode("#00BCD4"));
                g.drawRect(WIDTH - 109, 55, 100, 10);
                g.fillRect(WIDTH - 109, 55, (int) (100 - 100.0 * slowDownTimeDiff / slowDownLength), 10);
            }

            // DRAW WAVE NUMBER
            if (waveStartTime != 0 && waveNumber != 7 && STATUS.equals("PLAY")) {
                g.setFont(new Font("Century Gothic", Font.BOLD, 18));
                String s = "- W A V E   " + waveNumber + " -";
                int lenght = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
                int alpha = (int) (255 * Math.sin(3.14 * waveStartTimeDiff / waveDelay));
                if(alpha > 255) alpha = 255;
                g.setColor(new Color(255, 255, 255, alpha));
                g.drawString(s, WIDTH / 2 - lenght / 2, HEIGHT / 2); 
            }
        }
    }

    private void gameDraw() {
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }

    private void createNewEnemies() {
        enemies.clear();
        slowDownTime = 0;

        if (waveNumber == 1) {
            for (int i = 0; i < 10; i++) {
                enemies.add(new Enemy(1, 1));
            }
        }

        if (waveNumber == 2) {
            for (int i = 0; i < 5; i++) {
                enemies.add(new Enemy(1, 2));
            }
        }
        if (waveNumber == 3) {
            for (int i = 0; i < 5; i++) {
                enemies.add(new Enemy(1, 3));
            }
        }
        if (waveNumber == 4) {
            for (int i = 0; i < 5; i++) {
                enemies.add(new Enemy(1, 1));
            }
            for (int i = 0; i < 5; i++) {
                enemies.add(new Enemy(1, 2));
            }
        }
        if (waveNumber == 5) {
            for (int i = 0; i < 10; i++) {
                enemies.add(new Enemy(1, 1));
            }
            for (int i = 0; i < 5; i++) {
                enemies.add(new Enemy(1, 3));
            }
        }
        if (waveNumber == 6) {
            for (int i = 0; i < 10; i++) {
                enemies.add(new Enemy(1, 1));
            }
            for (int i = 0; i < 5; i++) {
                enemies.add(new Enemy(1, 2));
            }
            for (int i = 0; i < 5; i++) {
                enemies.add(new Enemy(1, 3));
            }
        }
        if (waveNumber == 7) {
            STATUS = "WIN";
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
        if(keyCode == KeyEvent.VK_ENTER){
            if (!STATUS.equals("PLAY")) {
                waveNumber = 0;
                enemies.clear();
                powerUps.clear();
                texts.clear();
                explosions.clear();
                player = new Player();
                STATUS = "PLAY";
            }
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
