public class Effectors {
    Dude d;

    public Effectors(Dude d1) {
        d = d1;
    }

    // Pour bouger dans les directions Nord Sud Est Ouest
    public boolean move(char dir) throws DeadException {
        d.score--;
        d.d = dir;
        switch (dir) {
            case 'n':
                d.x--;
                break;
            case 's':
                d.x++;
                break;
            case 'e':
                d.y++;
                break;
            case 'w':
                d.y--;
                break;
        }
        // Si on a marché sur une case dangereuse
        if (((Main.e.map[d.x][d.y]) & (Constants.CREVASSE | Constants.MONSTER)) != 0) {
            d.score -= Constants.COMPLETION;
            // Si la mort définitive est activée (le programme s'arrête)
            if(d.death){
                throw new DeadException();
            }
            d.resetMental();
            return true;
        }
        // On marque la case comme explorée et sans danger
        d.explored[d.x][d.y] = true;
        d.possibilities[d.x][d.y][0] = false;
        d.possibilities[d.x][d.y][1] = false;
        return false;
    }

    // Pour jeter un caillou dans les directions Nord Sud Est Ouest
    public void throwRock(char dir) {
        d.score -= 10;
        int sx = -1;
        int sy = -1;
        switch (dir) {
            case 'n':
                sx = d.x-1;
                sy = d.y;
                break;
            case 's':
                sx = d.x+1;
                sy = d.y;
                break;
            case 'e':
                sx = d.x;
                sy = d.y+1;
                break;
            case 'w':
                sx = d.x;
                sy = d.y-1;
                break;
        }
        Main.e.map[sx][sy] &= ~Constants.MONSTER;
        // On marque que la case ne peut plus contenir de monstre
        d.possibilities[sx][sy][0] = false;
        // On met à jour l'odeur des cases voisines
        if (sx > 0) {
            updateSmelliness(sx - 1, sy);
        }
        if (sx < d.mapSize - 1) {
            updateSmelliness(sx + 1, sy);
        }
        if (sy > 0) {
            updateSmelliness(sx, sy - 1);
        }
        if (sy < d.mapSize - 1) {
            updateSmelliness(sx, sy + 1);
        }
    }

    // Met à jour l'odeur des cases après qu'on ait tué un monstre
    private void updateSmelliness(int x, int y) {
        if ((x > 0 && (Main.e.map[x - 1][y] & Constants.MONSTER) != 0) || (x < d.mapSize - 1 && (Main.e.map[x + 1][y] & Constants.MONSTER) != 0) || (y > 0 && (Main.e.map[x][y - 1] & Constants.MONSTER) != 0) || (y < d.mapSize - 1 && (Main.e.map[x][y + 1] & Constants.MONSTER) != 0)) {
            return;
        }
        Main.e.map[x][y] &= ~Constants.SMELLY;
    }

    // Pour sauter dans le portail
    public boolean exit() {
        if ((Main.e.map[d.x][d.y] & Constants.PORTAL) != 0) {
            d.score += Constants.COMPLETION;
            d.resetMental();
            return true;
        }
        return false;
    }
}