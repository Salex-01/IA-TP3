public class Sensors {
    Dude d;

    public Sensors(Dude d1) {
        d = d1;
    }

    public void smell() {
        d.knowledge[d.x][d.y] = (d.knowledge[d.x][d.y] & ~Constants.SMELLY) | (Main.e.map[d.x][d.y] & Constants.SMELLY);
    }

    public void wind() {
        d.knowledge[d.x][d.y] = (d.knowledge[d.x][d.y] & ~Constants.WINDY) | (Main.e.map[d.x][d.y] & Constants.WINDY);
    }

    public void light() {
        d.knowledge[d.x][d.y] = (d.knowledge[d.x][d.y] & ~Constants.PORTAL) | (Main.e.map[d.x][d.y] & Constants.PORTAL);
    }

    public void all() {
        d.knowledge[d.x][d.y] = 0;
        smell();
        wind();
        light();
    }
}