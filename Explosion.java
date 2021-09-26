import java.awt.*;

public class Explosion {
  // FIELDS | CAMPOS - ATRIBUTOS
  private double x;
  private double y;
  private int r;
  private int maxRadius;

  // CONSTRUCTOR | CONSTRUTOR
  public Explosion(double x, double y, int r, int maxRadius) {
      this.x = x;
      this.y = y;
      this.r = r;
      this.maxRadius = maxRadius;
  }

  // FUNCTIONS | FUNÇÔES
  public boolean update() {
      r += 2;
      if (r > maxRadius) {
        return true;
      }
      return false;
  }
  public void draw(Graphics2D g) {
    g.setColor(Color.decode("#fafafa"));
    g.setStroke(new BasicStroke(3));
    g.drawOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
    g.setStroke(new BasicStroke(1));
  }
}
