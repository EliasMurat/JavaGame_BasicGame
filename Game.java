import javax.swing.JFrame;

public class Game {
    public static void main(String[] args) {
        JFrame jFrame = new JFrame("First Game");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setContentPane(new GamePanel());
        jFrame.pack();
        jFrame.setVisible(true);
    }
}