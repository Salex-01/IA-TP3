public class Sensors {
    Dude d;

    public Sensors(Dude d1) {
        d = d1;
    }

    public void smell() {
        d.knowledges[d.x][d.y] |= (Main.e.map[d.x][d.y] & Constants.SMELLY);
    }

    public void wind() {
        d.knowledges[d.x][d.y] |= (Main.e.map[d.x][d.y] & Constants.WINDY);
    }

    public void light() {
        d.knowledges[d.x][d.y] |= (Main.e.map[d.x][d.y] & Constants.PORTAL);
    }

    public void all() {
        smell();
        wind();
        light();
    }
}