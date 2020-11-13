import java.util.Random;

public class Environment {
    int[][] map;

    public void build(int size, int px, int py) {
        map = new int[size][size];
        Random r = new Random();
        int x;
        int y;
        do {
            x = r.nextInt(size);
            y = r.nextInt(size);
        } while (x == px && y == py);
        map[x][y] = Constants.PORTAL;
        for (int i = 0; i < (size - 1) / 2; i++) {
            do {
                x = r.nextInt(size);
                y = r.nextInt(size);
            } while (((map[x][y] & (Constants.CREVASSE | Constants.MONSTER)) != 0) || (x == px && y == py));
            map[x][y] = Constants.MONSTER;
            if (x > 0) {
                map[x - 1][y] |= Constants.SMELLY;
            }
            if (x < size - 1) {
                map[x + 1][y] |= Constants.SMELLY;
            }
            if (y > 0) {
                map[x][y - 1] |= Constants.SMELLY;
            }
            if (y < size - 1) {
                map[x][y + 1] |= Constants.SMELLY;
            }
            do {
                x = r.nextInt(size);
                y = r.nextInt(size);
            } while (((map[x][y] & (Constants.CREVASSE | Constants.MONSTER)) != 0) || (x == px && y == py));
            map[x][y] = Constants.CREVASSE;
            if (x > 0) {
                map[x - 1][y] |= Constants.WINDY;
            }
            if (x < size - 1) {
                map[x + 1][y] |= Constants.WINDY;
            }
            if (y > 0) {
                map[x][y - 1] |= Constants.WINDY;
            }
            if (y < size - 1) {
                map[x][y + 1] |= Constants.WINDY;
            }
        }
    }
}