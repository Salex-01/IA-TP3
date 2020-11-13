import java.awt.*;
import java.io.IOException;
import java.util.concurrent.Semaphore;

public class Main {
    static Environment e = new Environment();

    public static void main(String[] args) throws IOException, InterruptedException {
        int size = 3;
        int initX = -1;
        int initY = -1;
        boolean showWindow = true;
        boolean death = false;
        for (int i = 0; i < args.length; i += 2) {
            String s = args[i].toLowerCase();
            switch (s) {
                case "size":
                    size = Integer.parseInt(args[i + 1]);
                    break;
                case "x":
                    initX = Integer.parseInt(args[i + 1]);
                    break;
                case "y":
                    initY = Integer.parseInt(args[i + 1]);
                    break;
                case "i":
                case "interface":
                    showWindow = args[i + 1].toLowerCase().startsWith("t");
                    break;
                case "death":
                    death = args[i + 1].toLowerCase().startsWith("t");
                    break;
                default:
                    System.out.println("Unknown argument +\"" + s + "\"");
                    System.exit(-1);
                    break;
            }
        }
        Constants.COMPLETION = 10 * size * size;
        e.build(size, initX, initY);
        Frame f = null;
        Button b = null;
        Semaphore s = null;
        if (showWindow) {
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            s = new Semaphore(0);
            f = new Frame();
            f.setBounds((int) (d.getWidth() * 0.1), (int) (d.getHeight() * 0.1), (int) (d.getWidth() * 0.8), (int) (d.getHeight() * 0.8));
            f.addWindowListener(new CloserListener(f, s));
            Container c = new Container();
            c.setBounds((int) (d.getWidth() * 0.1), (int) (d.getHeight() * 0.1), (int) (d.getWidth() * 0.8), (int) (d.getHeight() * 0.8));
            b = new Button();
            b.setBounds(50, 50, 150, 50);
            b.setBackground(Color.GREEN);
            b.setForeground(Color.BLACK);
            b.setLabel("MOVE");
            c.add(b);
            f.add(c);
            f.setVisible(true);
        }
        Dude d = new Dude(size, initX, initY, showWindow, f, death);
        if (showWindow) {
            b.addActionListener(event -> {
                if (d.doSomething()) {
                    e.build(d.mapSize, d.x, d.y);
                }
            });
            s.acquire(1);
        } else {
            int a;
            while (true) {
                if (System.in.available() > 0) {
                    byte[] array = new byte[2];
                    a = System.in.read(array);
                    if (a > 1) {
                        break;
                    }
                    if (d.doSomething()) {
                        e.build(d.mapSize, d.x, d.y);
                    }
                }
            }
        }
        System.out.println("Score : " + d.score);
    }
}