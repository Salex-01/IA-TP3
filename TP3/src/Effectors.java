public class Effectors {
    Dude d;

    public Effectors(Dude d1) {
        d = d1;
    }

    private void move(char dir) {
        d.score--;
        switch (dir) {
            case 'n':
                d.y--;
                break;
            case 's':
                d.y++;
                break;
            case 'e':
                d.x++;
                break;
            case 'w':
                d.x--;
                break;
        }
    }

    private void throwRock(char dir) {
        d.score -= 10;
        int tx = -1;
        int ty = -1;
        switch (dir) {
            case 'n':
                tx = d.x;
                ty = d.y - 1;
                break;
            case 's':
                tx = d.x;
                ty = d.y + 1;
                break;
            case 'e':
                tx = d.x + 1;
                ty = d.y;
                break;
            case 'w':
                tx = d.x - 1;
                ty = d.y;
                break;
        }
        Main.e.map[tx][ty] &= ~Constants.MONSTER;
        if (tx > 0) {
            updateSmelliness(tx - 1, ty);
        }
        if (tx < d.mapSize - 1) {
            updateSmelliness(tx + 1, ty);
        }
        if (ty > 0) {
            updateSmelliness(tx, ty - 1);
        }
        if (ty < d.mapSize - 1) {
            updateSmelliness(tx, ty + 1);
        }
    }

    private void updateSmelliness(int x, int y) {
        if ((x > 0 && (Main.e.map[x - 1][y] & Constants.MONSTER) != 0) || (x < d.mapSize - 1 && (Main.e.map[x + 1][y] & Constants.MONSTER) != 0) || (y > 0 && (Main.e.map[x][y - 1] & Constants.MONSTER) != 0) || (y < d.mapSize - 1 && (Main.e.map[x][y + 1] & Constants.MONSTER) != 0)) {
            return;
        }
        Main.e.map[x][y] &= ~Constants.SMELLY;
    }

    private boolean exit() {
        if ((Main.e.map[d.x][d.y] & Constants.PORTAL) != 0) {
            d.score += Constants.COMPLETION;
            d.knowledges = new int[d.mapSize][d.mapSize];
            return true;
        }
        return false;
    }
}