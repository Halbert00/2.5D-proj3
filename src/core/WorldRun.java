package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.FileUtils;

import java.awt.*;

public class WorldRun {

    WorldGenerator worldGenerator;
    TETile[][] world;
    Avatar avatar;
    boolean saveWorld;
    private final TERenderer ter = new TERenderer();
    String filename = "savefile.txt";
    private boolean typeInColon;

    private boolean limitView;

    private int distanceOfView;

    private class Avatar {
        int x;
        int y;
        TETile avatarTile = Tileset.AVATAR;

        public Avatar(int x, int y) {
            this.x = x;
            this.y = y;
            world[x][y] = avatarTile;
        }

        public void move(int dx, int dy) {
            if (canMove(dx, dy)) {
                world[x][y] = Tileset.FLOOR;
                world[x + dx][y + dy] = avatarTile;
                x += dx;
                y += dy;
            }

        }

        // Basically this method is just used to judge whether the avatar
        // can move one step.
        private boolean canMove(int dx, int dy) {
            int width = WorldGenerator.WIDTH;
            int height = WorldGenerator.HEIGHT;
            return (x + dx >= 0)
                    && (x + dx <= width)
                    && (y + dy >= 0)
                    && (y + dy <= height)
                    && (world[x + dx][y + dy] == Tileset.FLOOR);
        }
    }

    public WorldRun() {
        saveWorld = false;
        ter.initialize(WorldGenerator.WIDTH, WorldGenerator.HEIGHT);
        typeInColon = false;
        limitView = false;
        distanceOfView = 5;
    }

    public WorldRun(boolean flag) {
        saveWorld = false;
        ter.initialize(WorldGenerator.WIDTH, WorldGenerator.HEIGHT);
        worldGenerator = new WorldGenerator(1);
        world = worldGenerator.getWorld();
        initializeAvatar();
        typeInColon = false;
        limitView = false;
        distanceOfView = 5;
    }


    public void initializeAvatar() {
        for (int j = 1; j <= WorldGenerator.HEIGHT - 1; j++) {
            for (int i = WorldGenerator.WIDTH - 1; i >= 1; i--) {
                if (world[i][j] == Tileset.FLOOR) {
                    avatar = new Avatar(i, j);
                    return;
                }
            }
        }
    }


    public void limitedDraw(int range) {
        StdDraw.clear(new Color(0, 0, 0));
        int numXTiles = world.length;
        int numYTiles = world[0].length;
        for (int x = 0; x < numXTiles; x++) {
            for (int y = 0; y < numYTiles; y++) {
                if (Math.abs(avatar.x - x) + Math.abs(avatar.y - y) <= range) {
                    world[x][y].draw(x, y);
                }
            }
        }
        StdDraw.show();
    }

    private void updateBoard() {
        if (typeInColon) {
            Font font = new Font("Arial", Font.BOLD, 60);
            StdDraw.clear(Color.black);
            StdDraw.setFont(font);
            StdDraw.setPenColor(255, 255, 255);
            StdDraw.text(20, 20, "Are you sure you want to quit and save?");
            StdDraw.setFont();
            StdDraw.text(20, 16, "(Q) Yes!");
            StdDraw.text(20, 12, "(Any other key) No!");
            StdDraw.show();
        }
        if (StdDraw.hasNextKeyTyped()) {
            char input = StdDraw.nextKeyTyped();
            if (typeInColon) {
                if (input == 'q' || input == 'Q') {
                    saveGame();
                } else {
                    typeInColon = false;
//                    if (!limitView) {
//                        ter.renderFrame(world);
//                    } else {
//                        limitedDraw(this.distanceOfView);
//                    }
                    renderWorld();
//                    addInfo();
                }
            } else {
                if (input == 'w' || input == 'W') {
                    avatar.move(0, 1);
                } else if (input == 'a' || input == 'A') {
                    avatar.move(-1, 0);
                } else if (input == 's' || input == 'S') {
                    avatar.move(0, -1);
                } else if (input == 'd' || input == 'D') {
                    avatar.move(1, 0);
                } else if (input == ':') {
                    typeInColon = true;
                } else if (input == 'T' || input == 't') {
                    limitView = !limitView;
                } else if (input == '+') {
                    distanceOfView++;
                } else if (input == '-') {
                    distanceOfView--;
                    distanceOfView = Math.max(distanceOfView, 1);
                }
//                if (!limitView) {
//                    ter.renderFrame(world);
//                } else {
//                    limitedDraw(this.distanceOfView);
//                }
                renderWorld();
//                addInfo();
            }
        }
    }
    private void addInfo() { // just adding info without clearing screen
        StdDraw.setFont();
        StdDraw.setPenColor(255, 255, 255);
        String string = "Tile: " + lastInfo;
        StdDraw.textLeft(1, WorldGenerator.HEIGHT - 1, string);
        StdDraw.show();
    }

    private String lastInfo = "";

    private void renderInfo() { // render the info and the world if info changes
        if (typeInColon) {
            return;
        }
        String tileInfo = getTileInfo(StdDraw.mouseX(), StdDraw.mouseY());
        if (tileInfo.equals(lastInfo)) {
            return;
        }
        lastInfo = tileInfo;
        if (!limitView) {
            ter.renderFrame(world);
        } else {
            limitedDraw(this.distanceOfView);
        }
        StdDraw.setFont();
        StdDraw.setPenColor(255, 255, 255);
        String string = "Tile: " + tileInfo;
        StdDraw.textLeft(1, WorldGenerator.HEIGHT - 1, string);
        StdDraw.show();
    }

    private String getTileInfo(double x, double y) {
        int x1 = (int) Math.floor(x);
        int y1 = (int) Math.floor(y);
        if (x1 < 0 || x1 > WorldGenerator.WIDTH - 1 || y1 < 0 || y1 > WorldGenerator.HEIGHT - 1) {
            return "";
        }
        TETile tile = world[x1][y1];
        if (tile == Tileset.AVATAR) {
            return "That's the most handsome/beautiful person in the world!";
        } else if (tile == Tileset.FLOOR) {
            return "It's walkable floor.";
        } else if (tile == Tileset.WALL) {
            return "That's hard wall.";
        } else if (tile == Tileset.NOTHING) {
            return "There's nothing.";
        }
        return "";
    }

    private boolean isGameOver() {
        return saveWorld;
    }

    private void saveGame() {
        saveWorld = true;
        StringBuilder contents = new StringBuilder();
        contents.append(worldGenerator.seed).append("\n");
        contents.append(avatar.x).append(" ").append(avatar.y).append("\n");
        if (limitView) {
            contents.append("T").append("\n");
        } else {
            contents.append("F").append("\n");
        }
        contents.append(distanceOfView).append("\n");
        FileUtils.writeFile(filename, contents.toString());
    }

    // This method get a TETile[][] from the file and remove everything in the file.
    public void loadGame() {
        String info = FileUtils.readFile(filename);
        if (info.isEmpty()) {
            System.exit(0);
        }
        String[] allLine = info.split("\n");
        this.worldGenerator = new WorldGenerator(Long.parseLong(allLine[0]));
        this.world = worldGenerator.getWorld();
        String[] coordinates = allLine[1].split(" ");
        avatar = new Avatar(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]));
        this.limitView = allLine[2].equals("T");
        this.distanceOfView = Integer.parseInt(allLine[3]);
        FileUtils.writeFile(filename, "");
    }

    public void runGame() {
        renderWorld();
        while (!isGameOver()) {
            updateBoard();
//            renderInfo();
        }
        System.exit(0);
    }

    private void renderSeed(long seed) {
        Font font = new Font("Arial", Font.BOLD, 60);
        StdDraw.clear(StdDraw.WHITE);
        StdDraw.setFont(font);
        StdDraw.setPenColor(0, 0, 0);
        StdDraw.text(20, 20, "Please enter a seed:");
        StdDraw.setFont();
        StdDraw.text(20, 10, "Your current seed: " + seed);
        StdDraw.text(20, 8, "(R) Reset Seed To Zero");
        StdDraw.text(20, 6, "(S) Let's Start!");
        StdDraw.show();
    }

    public void welcomeInterface() {
        Font font = new Font("Arial", Font.BOLD, 60);
        StdDraw.setFont(font);
        StdDraw.setPenColor(0, 0, 0);
        StdDraw.text(20, 20, "Welcome!");
        StdDraw.setFont();
        StdDraw.text(20, 10, "(N) A New World");
        String info = FileUtils.readFile(filename);
        if (info.isEmpty()) {
            StdDraw.setPenColor(255, 0, 0);
            StdDraw.text(20, 8, "(L) We Cannot Load, Press Will Quit");
        } else {
            StdDraw.setPenColor(51, 255, 255);
            StdDraw.text(20, 8, "(L) Load My Previous World");
        }
        StdDraw.setPenColor(0, 0, 0);
        StdDraw.text(20, 6, "(Q) Quit");
        StdDraw.show();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char input = StdDraw.nextKeyTyped();
                if (input == 'n' || input == 'N') {
                    long seed = 0;
                    System.out.println("rendering seed");
                    renderSeed(seed);
                    while (true) {
                        if (StdDraw.hasNextKeyTyped()) {
                            char c = StdDraw.nextKeyTyped();
                            if (c == 's' || c == 'S') {
                                break;
                            } else if (c >= '0' && c <= '9') {
                                seed = seed * 10 + c - '0';
                                renderSeed(seed);
                            } else if (c == 'r' || c == 'R') {
                                seed = 0;
                                renderSeed(seed);
                            }
                        }
                    }
                    this.worldGenerator = new WorldGenerator(seed);
                    this.world = worldGenerator.getWorld();
                    initializeAvatar();
                    break;
                } else if (input == 'l' || input == 'L') {
                    loadGame();
                    break;
                } else if (input == 'q' || input == 'Q') {
                    System.exit(0);
                }
            }
        }
    }

    private void drawDiamond(double x, double y, double v, double h) {
        double[] X = {x - h, x, x + h, x};
        double[] Y = {y, y + v, y, y - v};
        StdDraw.filledPolygon(X, Y);
        StdDraw.setPenColor(StdDraw.ORANGE);
        StdDraw.line(X[0], Y[0], X[1], Y[1]);
        StdDraw.line(X[1], Y[1], X[2], Y[2]);
        StdDraw.line(X[2], Y[2], X[3], Y[3]);
        StdDraw.line(X[0], Y[0], X[3], Y[3]);
    }

    private void drawDiamond(double x, double y) {
        drawDiamond(x, y, (double)1 / (2 * Math.sqrt(3)), (double) 1 / 2);
    }

    private void drawTile(int i, int j) {
        StdDraw.setPenColor(41, 255, 255);
        double startx = (double) 1 / 2;
        double starty = (double) 20;
        startx += (double)i / 2;
        starty -= i * (1 / (2 * Math.sqrt(3)));
        startx += (double)j / 2;
        starty += j * (1 / (2 * Math.sqrt(3)));
        drawDiamond(startx, starty);

    }

    private void drawCharacter(double x, double y, double dis) {
        double[] X = {x, x + Math.sqrt(3) * dis, x + Math.sqrt(3) * dis, x, x - Math.sqrt(3) * dis, x - Math.sqrt(3) * dis};
        double[] Y = {y - dis, y, y + 2 * dis, y + 3 * dis, y + 2 * dis, y};
        StdDraw.filledPolygon(X, Y);
        StdDraw.setPenColor(StdDraw.ORANGE);
        StdDraw.line(x, y + dis, x, y - dis);
        StdDraw.line(x, y + dis, x + Math.sqrt(3) * dis, y + dis * 2);
        StdDraw.line(x, y + dis, x - Math.sqrt(3) * dis, y + dis * 2);
        StdDraw.line(X[0], Y[0], X[1], Y[1]);
        StdDraw.line(X[1], Y[1], X[2], Y[2]);
        StdDraw.line(X[2], Y[2], X[3], Y[3]);
        StdDraw.line(X[3], Y[3], X[4], Y[4]);
        StdDraw.line(X[4], Y[4], X[5], Y[5]);
        StdDraw.line(X[5], Y[5], X[0], Y[0]);
    }

    private void drawCharacter(int i, int j) {
        StdDraw.setPenColor(255, 0, 0);
        double startx = (double) 1 / 2;
        double starty = (double) 20;
        startx += (double)i / 2;
        starty -= i * (1 / (2 * Math.sqrt(3)));
        startx += (double)j / 2;
        starty += j * (1 / (2 * Math.sqrt(3)));
        drawCharacter(startx, starty, (double) 1 / (2 * Math.sqrt(3)));
    }

    private void drawWall(int i, int j) {
        StdDraw.setPenColor(0, 0, 0);
        double startx = (double) 1 / 2;
        double starty = (double) 20;
        startx += (double)i / 2;
        starty -= i * (1 / (2 * Math.sqrt(3)));
        startx += (double)j / 2;
        starty += j * (1 / (2 * Math.sqrt(3)));
        drawCharacter(startx, starty, (double) 1 / (2 * Math.sqrt(3)));
    }


    public void renderWorld() {
        StdDraw.clear();
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 40; j++) {
                if (world[i][j] == Tileset.FLOOR) {
                    drawTile(i, j);
                }
            }
        }
        for (int i = 0; i < 40; i++) {
            for (int j = 39; j >= 0; j--) {
                if (world[i][j] == Tileset.WALL) {
                    if (i < 39 && (world[i + 1][j] == Tileset.FLOOR || world[i + 1][j] == Tileset.AVATAR)) {
                        drawWall(i, j);
                    }
                    if (j > 0 && (world[i][j - 1] == Tileset.FLOOR || world[i][j - 1] == Tileset.AVATAR)) {
                        drawWall(i, j);
                    }
                    if (j > 0 && i < 39 && (world[i + 1][j - 1] == Tileset.FLOOR || world[i + 1][j - 1] == Tileset.AVATAR)) {
                        drawWall(i, j);
                    }
                }
            }
        }
        for (int i = 0; i < 40; i++) {
            for (int j = 39; j >= 0; j--) {
                if (world[i][j] == Tileset.AVATAR) {
                    drawCharacter(i, j);
                    if (i < 39 && world[i + 1][j] == Tileset.WALL) {
                        drawWall(i + 1, j);
                    }
                    if (j > 0 && world[i][j - 1] == Tileset.WALL ) {
                        drawWall(i, j- 1);
                    }
                    if (j > 0 && i < 39 && world[i + 1][j - 1] == Tileset.WALL) {
                        drawWall(i + 1, j - 1);
                    }
                    for (int p = i; p < 40; p++) {
                        for (int q = j; q >= 0; q--) {
                            if (p == i && q == j) {
                                continue;
                            }
                            if (world[p][q] == Tileset.WALL) {
                                if (p < 39 && (world[p + 1][q] == Tileset.FLOOR || world[p + 1][q] == Tileset.AVATAR)) {
                                    drawWall(p, q);
                                }
                                if (q > 0 && (world[p][q - 1] == Tileset.FLOOR || world[p][q - 1] == Tileset.AVATAR)) {
                                    drawWall(p, q);
                                }
                                if (q > 0 && p < 39 && (world[p + 1][q - 1] == Tileset.FLOOR || world[p + 1][q - 1] == Tileset.AVATAR)) {
                                    drawWall(p, q);
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }

        StdDraw.show();
    }
}
