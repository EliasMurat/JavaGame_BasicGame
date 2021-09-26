import java.awt.*;

public class PowerUp {
    // FIELDS | CAMPOS - ATRIBUTOS
    private double x;
    private double y;
    private int r;

    /*
     * PowerUps types:
     * [1] -> +1 life  
     * [2] -> +1 power 
     * [3] -> +2 power
     */
    private int type;

    private Color color1;

    // CONSTRUCTOR | CONSTRUTOR
    public PowerUp(int type, double x, double y) {
        this.type = type;
        this.x = x;
        this.y = y;

        if (type == 1) {
            color1 = Color.decode("#E91E63");
            r = 5;
        }
        if (type == 2) {
            color1 = Color.decode("#FFEB3B");
            r = 5;
        }
        if (type == 3) {
            color1 = Color.decode("#FFEB3B");
            r = 7;
        }
    }

    // FUNCTIONS | FUNÇÔES
    public double getX() { return x; }
    public double getY() { return y; }
    public double getR() { return r; }

    public int getType() { return type; }

    public boolean update() {
        y += 2;

        if (y > GamePanel.HEIGHT + r) {
            return true;
        }

        return false;
    }

    public void draw(Graphics2D g) {
        g.setColor(color1);
        g.fillRect((int) (x - r), (int) (y - r), 2 * r, 2 * r);
        g.setStroke(new BasicStroke(3));
        g.setColor(color1.darker());
        g.drawRect((int) (x - r), (int) (y - r), 2 * r, 2 * r);
        g.setStroke(new BasicStroke(1));
    }

}