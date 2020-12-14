import java.awt.*;
import java.util.concurrent.Semaphore;

public class GraphicThread extends Thread {
    Canvas c;
    Graphics g;
    Dude d;
    int size;
    boolean stopped = false;

    public GraphicThread(int si) {
        size = si;
    }

    public void setCanvas(Canvas c1) {
        c = c1;
        g = c.getGraphics();
    }

    public void setDude(Dude d1) {
        d = d1;
    }

    @Override
    public void run() {
        while (true) {
            g.clearRect(0,0,c.getWidth(),c.getHeight());
            if (stopped) {
                return;
            }
            for (int i = 0; i < size + 1; i++) {
                g.drawLine(0, Math.max((i * c.getHeight()) / size - 1, 0), c.getWidth(), Math.max((i * c.getHeight()) / size - 1, 0));
                g.drawLine(Math.max((i * c.getWidth()) / size - 1, 0), 0, Math.max((i * c.getWidth()) / size - 1, 0), c.getHeight());
            }
            for(int i = 0; i<size;i++){
                for(int j = 0; j<size;j++){
                    int tmp = Main.e.map[i][j];
                    if((tmp&Constants.PORTAL)!=0){
                        g.drawString("PORTAL", (int) ((i+0.1)*c.getWidth()/size), (int) ((j+0.1)*c.getHeight()/size));
                    }
                    if((tmp&Constants.MONSTER)!=0){
                        g.drawString("MONSTER", (int) ((i+0.1)*c.getWidth()/size), (int) ((j+0.1)*c.getHeight()/size));
                    }
                    if((tmp&Constants.CREVASSE)!=0){
                        g.drawString("CREVASSE", (int) ((i+0.1)*c.getWidth()/size), (int) ((j+0.1)*c.getHeight()/size));
                    }
                }
            }
            g.drawString("DUDE", (int) ((d.x+0.5)*c.getWidth()/size), (int) ((d.y+0.5)*c.getHeight()/size));
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void sstop() {
        stopped = true;
    }
}
