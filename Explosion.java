import java.awt.*;

public class Explosion {
  // FIELDS | CAMPOS - ATRIBUTOS
  private double x;
  private double y;
  private double r;
  private double maxRadius;

  private int alpha;

  // CONSTRUCTOR | CONSTRUTOR
  public Explosion(double x, double y, int r, int maxRadius) {
      this.x = x;
      this.y = y;
      this.r = r;
      this.maxRadius = maxRadius;

      alpha = 255;
  }

  // FUNCTIONS | FUNÇÔES
  public boolean update() {
      r += 2.5;
      alpha -= 25;
      if (alpha < 10) {
        alpha = 255;
      }
      if (r > maxRadius) {
        return true;
      }
      return false;
  }
  public void draw(Graphics2D g) {
    g.setColor(new Color(158, 158, 158, alpha));
    g.setStroke(new BasicStroke(3));
    g.drawOval((int) (x - r), (int) (y - r), (int) (2 * r), (int) (2 * r));
    g.setStroke(new BasicStroke(1));
  }
}
