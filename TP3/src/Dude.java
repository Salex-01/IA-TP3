import java.awt.*;
import java.util.Random;

public class Dude {
    int mapSize;
    int x;
    int y;
    char d = 's';
    boolean sw;
    Frame f;
    boolean death;
    Sensors s = new Sensors(this);
    Effectors e = new Effectors(this);

    int score = 0;

    int[][] knowledges;

    public Dude(int size, int iX, int iY, boolean showWindow, Frame f1, boolean d) {
        Random r = new Random();
        mapSize = size;
        if (iX >= 0) {
            x = iX;
        } else {
            x = r.nextInt(mapSize);
        }
        if (iY >= 0) {
            y = iY;
        } else {
            y = r.nextInt(mapSize);
        }
        sw = showWindow;
        f = f1;
        death = d;
        knowledges = new int[mapSize][mapSize];
    }

    public boolean doSomething() {

        return true;
    }

}