import java.awt.*;

public class Text {
    // FIELDS | CAMPOS - ATRIBUTOS
    private double x;
    private double y;
    private long time;
    private String s;

    private long start;

    // CONSTRUCTOR | CONSTRUTOR
    public Text(double x, double y, long time, String s){
        this.x = x;
        this.y = y;
        this.time = time;
        this.s = s;
        start = System.nanoTime();
    }

    // FUNCTIONS | FUNÇÔES
    public boolean update() {
        long elapsed = (System.nanoTime() - start) / 1000000;
        if (elapsed > time) {
            return true;
        }
        return false;
    }

    public void draw(Graphics2D g) {
        g.setFont(new Font("Century Gothic", Font.PLAIN, 14));
        g.setColor(Color.decode("#fafafa"));
        g.drawString(s, (int) x, (int) y); 
    }
}
