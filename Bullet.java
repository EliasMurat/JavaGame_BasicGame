import java.awt.*;

public class Bullet {
  // FIELDS | CAMPOS - ATRIBUTOS
  private double x;
  private double y;
  private int r;

  private double dx;
  private double dy;
  private double rad;
  private double speed;

  private Color color1;

  // CONSTRUCTOR | CONSTRUTOR
  public Bullet(double angle, int x, int y){
    this.x = x;
    this.y = y;
    r = 3; 
    speed = 10;

    rad = Math.toRadians(angle);
    dx = Math.cos(rad) * speed;
    dy = Math.sin(rad) * speed;

    color1 = Color.decode("#FFEB3B");
  }

  // FUNCTIONS | FUNÇÔES
  public double getX() { return x; }
  public double getY() { return y; }
  public double getR() { return r; }
  
  public boolean update() {
    x += dx;
    y += dy;

    if(x < -r || x > GamePanel.WIDTH + r ||
       y < -r || y > GamePanel.HEIGHT + r) {
      return true;
    }

    return false;
  }

  public void draw(Graphics2D g) {
    g.setColor(color1);
    g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
  }

}
