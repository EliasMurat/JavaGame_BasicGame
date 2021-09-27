import java.awt.*;

public class Enemy {
  // FIELDS | CAMPOS - ATRIBUTOS
  private double x;
  private double y;
  private int r;

  private double dx;
  private double dy;
  private double rad;
  private double speed;

  private int health;
  private int type;
  private int rank;

  private Color color1;
  private Color color2;

  private boolean recovering;
  private long recoveryTimer;

  private boolean ready;
  private boolean dead;

  private boolean slow;

  // CONSTRUCTOR | CONSTRUTOR
  public Enemy(int type, int rank) {
    this.type = type;
    this.rank = rank;

    if (type == 1) {
      if (rank == 1) {
        color1 = Color.decode("#4CAF50");
        color2 = Color.decode("#8BC34A");
        speed = 3;
        r = 7;
        health = 1;
      }
      if (rank == 2) {
        color1 = Color.decode("#FF5722");
        color2 = Color.decode("#FF7043");
        speed = 5;
        r = 10;
        health = 3;
      }
      if (rank == 3) {
        color1 = Color.decode("#9C27B0");
        color2 = Color.decode("#CE93D8");
        speed = 7;
        r = 15;
        health = 5;
      }
    }

    x = Math.random() * GamePanel.WIDTH / 2 + GamePanel.WIDTH / 4;
    y = -r;

    double angle = Math.random() * 140 + 20;
    rad = Math.toRadians(angle);

    dx = Math.cos(rad) * speed;
    dy = Math.sin(rad) * speed;

    recovering = false;
    recoveryTimer = 0;

    ready = false;
    dead = false;
  }

  // FUNCTIONS | FUNÇÔES
  public double getX() { return x; }
  public double getY() { return y; }
  public double getR() { return r; }

  public void setSlow(boolean b) { slow = b; }

  public int getType() { return type; }
  public int getRank() { return rank; }

  public boolean isRecovering() { return recovering; }

  public boolean isDead() { return dead; }

  public void hit() {
    health--;

    recovering = true;
    recoveryTimer = System.nanoTime();

    if (health <= 0) {
      dead = true;
    }
  }

  public void explode() {
    int amount = 0;
    if (rank == 2) {
      amount = 2;
    }
    if (rank == 3) {
      amount = 4;
    }
    for (int i = 0; i < amount; i++) {
      Enemy e = new Enemy(getType(), getRank() - 1);
      e.x = this.x;
      e.y = this.y;

      if (e.getRank() >= 1) {
        double angle = 0;
        if (!ready) {
          angle = Math.random() * 140 + 20;
        } else {
          angle = Math.random() * 360;
        }
        e.rad = Math.toRadians(angle);
        GamePanel.enemies.add(e);        
      }

    }
  }

  public void update() {
    if (slow) {
      x += dx * 0.3;
      y += dy * 0.3;
    } else {
      x += dx;
      y += dy;
    }
    
    if (!ready) {
      if (x > r && x < GamePanel.WIDTH - r && y > r && y < GamePanel.HEIGHT - r) {
        ready = true;
      }
    }

    if (x < r && dx < 0) dx = -dx;
    if (y < r && dy < 0) dy = -dy;
    if (x > GamePanel.WIDTH - r && dx > 0) dx = -dx;
    if (y > GamePanel.HEIGHT - r && dy > 0) dy = -dy;

    long elapsed = (System.nanoTime() - recoveryTimer) / 1000000;
      if (elapsed > 100) {
          recovering = false;
          recoveryTimer = 0;
      }
  }

  public void draw(Graphics2D g) {
    if (recovering) {
      g.setColor(color2);
      g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
      g.setStroke(new BasicStroke(3));
      g.setColor(color2.darker());
      g.drawOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);      
    } else {
      g.setColor(color1);
      g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
      g.setStroke(new BasicStroke(3));
      g.setColor(color1.darker());
      g.drawOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
    }
  }

}