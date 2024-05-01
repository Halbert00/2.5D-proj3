package core;


import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;

public class Main {
    public static void main(String[] args) {
        // build your own world!
        WorldRun core = new WorldRun();
        core.welcomeInterface();
        core.runGame();
//        core.renderWorld();

        StdDraw.show();
    }
}
