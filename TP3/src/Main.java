import java.awt.*;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Main {
    static Environment e = new Environment();

    public static void main(String[] args) throws IOException, InterruptedException {
        int size = 3;
        int initX = -1;
        int initY = -1;
        boolean showWindow = true;
        boolean death = false;
        // On parse les paramètres
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
        if (initX < 0 || initX > size) {
            initX = new Random().nextInt(size);
        }
        if (initY < 0 || initY > size) {
            initY = new Random().nextInt(size);
        }
        // Création de la première carte
        e.build(size, initX, initY);
        Frame f = null;
        Button b = null;
        Semaphore s = new Semaphore(0);
        GraphicThread gt = new GraphicThread(size);
        //Préparation de l'affichage graphique
        if (showWindow) {
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
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
            Canvas c1 = new Canvas();
            c1.setBounds(250, 50, (int) (d.getWidth() * 0.8 - 300), (int) (d.getHeight() * 0.8 - 100));
            c1.setVisible(true);
            c.add(c1);
            gt.setCanvas(c1);
        }
        //Création du Dude
        Dude d = new Dude(size, initX, initY, showWindow, f, death);
        if (showWindow) {
            gt.setDude(d);
            gt.start();
            b.addActionListener(event -> {
                try {
                    if (d.doSomething()) {
                        System.out.println("Score " + d.score);
                        e.build(d.mapSize, d.x, d.y);
                    }
                } catch (DeadException deadException) {
                    s.release(1);
                }
            });
            s.acquire(1);
            gt.sstop();
        } else {
            consoleGraphics(size, d);
            int a;
            while (true) {
                if (System.in.available() > 0) {
                    byte[] array = new byte[2];
                    a = System.in.read(array);
                    if (a > 1) {
                        break;
                    }
                    try {
                        if (d.doSomething()) {
                            e.build(d.mapSize, d.x, d.y);
                        }
                    } catch (DeadException deadException) {
                        break;
                    }
                    consoleGraphics(size, d);
                }
            }
        }
        System.out.println("Final score : " + d.score);
    }

    private static void consoleGraphics(int size, Dude d) {
        System.out.println("####".repeat(size)+"#");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print("#"+((e.map[i][j]&Constants.MONSTER)!=0?"M":((e.map[i][j]&Constants.CREVASSE)!=0?"C":((e.map[i][j]&Constants.PORTAL)!=0?"P":" ")))+" "+(i==d.x&&j==d.y?"D":" "));
            }
            System.out.println("#");
            System.out.println("####".repeat(size)+"#");
        }
    }
}