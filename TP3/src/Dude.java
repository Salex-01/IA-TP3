import java.awt.*;
import java.util.LinkedList;
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

    int[][] knowledge;
    boolean[][] explored;
    boolean[][][] possibilities;
    Pair<LinkedList<Character>, LinkedList<Character>> intentions;

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
        resetMental();
    }

    public boolean doSomething() throws DeadException {
        System.out.println("Stepping");
        if (intentions.key.size() == 0 && (intentions.value == null || intentions.value.size() == 0)) {
            s.all();
            if ((knowledge[x][y] & Constants.PORTAL) != 0) {
                System.out.println("Exit");
                return e.exit();
            }
            updatePossibilities();
            intentions = findPathToNewSafe();
            if (intentions.key == null) {
                intentions = determineActions();
            }
        }
        if (intentions.key.size() > 0) {
            return e.move(intentions.key.remove(0));
        } else {
            e.throwRock(intentions.value.remove(0));
            s.smell();
            if ((knowledge[x][y] & Constants.SMELLY) == 0) {
                intentions = new Pair<>(new LinkedList<>(), new LinkedList<>());
            }
            return false;
        }
    }

    private Pair<LinkedList<Character>, LinkedList<Character>> determineActions() {
        LinkedList<Character> path = null;
        int fx = 0;
        int fy = 0;
        LinkedList<Character> tmp;
        int[][][] chances = computeProbabilities();
        int i;
        int j;
        for (i = 0; i < mapSize; i++) {
            for (j = 0; j < mapSize; j++) {
                if ((knowledge[i][j] & Constants.SMELLY) != 0) {
                    tmp = floodPath(i, j);
                    if (path == null || (tmp != null && tmp.size() < path.size())) {
                        path = tmp;
                        fx = i;
                        fy = j;
                    }
                }
            }
        }
        if (path != null) {
            LinkedList<Pair<Character, Integer>> sorter = new LinkedList<>();
            if (fx > 0 && chances[fx - 1][fy][0] > 0) {
                sorter.add(new Pair<>('n', chances[fx - 1][fy][0]));
            }
            if (fx < mapSize - 1 && chances[fx + 1][fy][0] > 0) {
                sorter.add(new Pair<>('s', chances[fx + 1][fy][0]));
            }
            if (fy > 0 && chances[fx][fy - 1][0] > 0) {
                sorter.add(new Pair<>('w', chances[fx][fy - 1][0]));
            }
            if (fy < mapSize - 1 && chances[fx][fy + 1][0] > 0) {
                sorter.add(new Pair<>('e', chances[fx][fy + 1][0]));
            }
            sorter.sort((o1, o2) -> o2.value - o1.value);
            LinkedList<Character> rocks = new LinkedList<>();
            for (Pair<Character, Integer> p : sorter) {
                rocks.add(p.key);
            }
            return new Pair<>(path, rocks);
        } else {
            LinkedList<Character> testPath;
            int bv = Integer.MAX_VALUE;
            for (i = 0; i < mapSize; i++) {
                for (j = 0; j < mapSize; j++) {
                    if ((chances[i][j][1] > 0 && chances[i][j][1] <= bv)) {
                        testPath = floodPath(i, j);
                        if (path == null || chances[i][j][1] < bv || (testPath != null && chances[i][j][1] <= bv && testPath.size() < path.size())) {
                            path = testPath;
                        }
                        bv = chances[i][j][1];
                    }
                }
            }
            return new Pair<>(path, null);
        }
    }

    private int[][][] computeProbabilities() {
        int[][][] probabilities = new int[mapSize][mapSize][2];
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                for (int type = 0; type < 2; type++) {
                    if ((knowledge[i][j] & (type == 0 ? Constants.SMELLY : Constants.WINDY)) != 0) {
                        int pos = 0;
                        if (i > 0 && possibilities[i - 1][j][type]) {
                            pos++;
                        }
                        if (i < mapSize - 1 && possibilities[i + 1][j][type]) {
                            pos++;
                        }
                        if (j > 0 && possibilities[i][j - 1][type]) {
                            pos++;
                        }
                        if (j < mapSize - 1 && possibilities[i][j + 1][type]) {
                            pos++;
                        }
                        if (pos > 1) {
                            if (i > 0 && possibilities[i - 1][j][type]) {
                                probabilities[i - 1][j][type]++;
                            }
                            if (i < mapSize - 1 && possibilities[i + 1][j][type]) {
                                probabilities[i + 1][j][type]++;
                            }
                            if (j > 0 && possibilities[i][j - 1][type]) {
                                probabilities[i][j - 1][type]++;
                            }
                            if (j < mapSize - 1 && possibilities[i][j + 1][type]) {
                                probabilities[i][j + 1][type]++;
                            }
                        } else {
                            if (i > 0 && possibilities[i - 1][j][type]) {
                                probabilities[i - 1][j][type] += 5;
                            }
                            if (i < mapSize - 1 && possibilities[i + 1][j][type]) {
                                probabilities[i + 1][j][type] += 5;
                            }
                            if (j > 0 && possibilities[i][j - 1][type]) {
                                probabilities[i][j - 1][type] += 5;
                            }
                            if (j < mapSize - 1 && possibilities[i][j + 1][type]) {
                                probabilities[i][j + 1][type] += 5;
                            }
                        }
                    }
                }
            }
        }
        return probabilities;
    }

    private Pair<LinkedList<Character>, LinkedList<Character>> findPathToNewSafe() {
        LinkedList<Character> res = null;
        LinkedList<Character> tmp;
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                if (!explored[i][j] && !possibilities[i][j][0] && !possibilities[i][j][1]) {
                    tmp = floodPath(i, j);
                    if (res == null || (tmp != null && tmp.size() < res.size())) {
                        res = tmp;
                    }
                }
            }
        }
        return new Pair<>(res, null);
    }

    private LinkedList<Character> floodPath(int i, int j) {
        int[][] flood = new int[mapSize][mapSize];
        flood[i][j] = 1;
        boolean t;
        do {
            t = false;
            for (int k = 0; k < mapSize; k++) {
                for (int l = 0; l < mapSize; l++) {
                    if (flood[k][l] > 0) {
                        flood[k][l]++;
                    }
                    if (flood[k][l] == 0 && explored[k][l]) {
                        if ((k > 0 && flood[k - 1][l] == 2) || (k < mapSize - 1 && flood[k + 1][l] == 1) || (l > 0 && flood[k][l - 1] == 2) || (l < mapSize - 1 && flood[k][l + 1] == 1)) {
                            flood[k][l] = 1;
                            t = true;
                        }
                    }
                }
            }
        } while (t && flood[x][y] == 0);
        if (flood[x][y] != 0) {
            LinkedList<Character> res = new LinkedList<>();
            int tx = x;
            int ty = y;
            int maxV;
            char maxD = 0;
            while (tx != i || ty != j) {
                maxV = 0;
                if (tx > 0 && flood[tx - 1][ty] > maxV) {
                    maxV = flood[tx - 1][ty];
                    maxD = 'n';
                }
                if (tx < mapSize - 1 && flood[tx + 1][ty] > maxV) {
                    maxV = flood[tx + 1][ty];
                    maxD = 's';
                }
                if (ty > 0 && flood[tx][ty - 1] > maxV) {
                    maxV = flood[tx][ty - 1];
                    maxD = 'w';
                }
                if (ty < mapSize - 1 && flood[tx][ty + 1] > maxV) {
                    maxD = 'e';
                }
                res.add(maxD);
                switch (maxD) {
                    case 'n':
                        tx--;
                        break;
                    case 's':
                        tx++;
                        break;
                    case 'w':
                        ty--;
                        break;
                    case 'e':
                        ty++;
                        break;
                }
            }
            return res;
        }
        return null;
    }

    private void updatePossibilities() {
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                if ((knowledge[i][j] >= 0) && ((knowledge[i][j] & Constants.SMELLY) == 0)) {
                    safeAround(i, j, Constants.MONSTER);
                }
                if ((knowledge[i][j] >= 0) && ((knowledge[i][j] & Constants.WINDY) == 0)) {
                    safeAround(i, j, Constants.CREVASSE);
                }
            }
        }
    }

    private void safeAround(int i, int j, int type) {
        int index;
        switch (type) {
            case Constants.MONSTER:
                index = 0;
                break;
            case Constants.CREVASSE:
                index = 1;
                break;
            default:
                index = -1;
                break;
        }
        if (i > 0) {
            possibilities[i - 1][j][index] = false;
        }
        if (i < mapSize - 1) {
            possibilities[i + 1][j][index] = false;
        }
        if (j > 0) {
            possibilities[i][j - 1][index] = false;
        }
        if (j < mapSize - 1) {
            possibilities[i][j + 1][index] = false;
        }
    }

    public void resetMental() {
        knowledge = new int[mapSize][mapSize];
        explored = new boolean[mapSize][mapSize];
        possibilities = new boolean[mapSize][mapSize][2];
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                explored[i][j] = false;
                knowledge[i][j] = Integer.MIN_VALUE;
                possibilities[i][j][0] = true;
                possibilities[i][j][1] = true;
            }
        }
        explored[x][y] = true;
        possibilities[x][y][0] = false;
        possibilities[x][y][1] = false;
        intentions = new Pair<>(new LinkedList<>(), new LinkedList<>());
    }
}