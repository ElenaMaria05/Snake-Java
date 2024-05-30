import javax.swing.*;


public class Main {
    private static final int LATIME = 800;
    private static final int INALTIME = 600;

    public static void main(String[] args) {
        final JFrame frame = new JFrame("Snake");
        frame.setSize(LATIME,INALTIME);
        SnakeGame game= new SnakeGame(LATIME,INALTIME);
        frame.add(game);

        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

        frame.pack();

        game.startGame();
    }

}