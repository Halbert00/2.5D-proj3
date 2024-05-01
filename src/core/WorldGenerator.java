package core;


import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.util.Random;

public class WorldGenerator {
    // General idea, we split the map into "blocks", which is 10 * 10.
    // Each block can have a room inside, or not.
    // The room in each block can be 2*2 to 8*8, if any.

    private class RoomInfo {
        // Each int bellow is from 1 to 8, which is the inner bias inside a room.
        int left;
        int down;
        int right;
        int up;
        boolean hasRoom;

        RoomInfo(int up, int down, int left, int right) {
            this.left = left;
            this.right = right;
            this.down = down;
            this.up = up;
            this.hasRoom = true;
        }

        RoomInfo() {
            this.hasRoom = false;
        }
    }

    RoomInfo[][] localRoomInfo; // We set each block to be 10 * 10,
    // this variable stores the info of the 9 * 4 blocks
    //which can have a room inside, or not.

    TETile[][] localMap;

    Random localRand; //A random number generator

    static final int WIDTH = 40; //The map has 90 squares horizontally
    static final int HEIGHT = 40; //The map has 40 squares vertically

    int ROOMWIDTH = 4;

    int ROOMHEIGHT = 4;

    long seed;

    WorldGenerator(long seed) {
        this.seed = seed;
        localRand = new Random(seed); //Setting the seed
        localMap = new TETile[WIDTH][HEIGHT];
        localRoomInfo = new RoomInfo[ROOMWIDTH][ROOMHEIGHT];
        initializeMap(); //We fill the Map with Nothing, and initialize the rooms to be empty.
        generateRooms(); //We generate the rooms here.
        connectRooms(); //If two rooms have the same i or j, we connect them.
        fillingWalls();
    }

    private void initializeMap() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                localMap[i][j] = Tileset.NOTHING;
            }
        }
        for (int i = 0; i < ROOMWIDTH; i++) {
            for (int j = 0; j < ROOMHEIGHT; j++) {
                localRoomInfo[i][j] = new RoomInfo();
            }
        }
    }

    public TETile[][] getWorld() {
        return localMap;
    }

    private void generateRooms() {
        for (int i = 0; i < ROOMWIDTH; i++) {
            for (int j = 0; j < ROOMHEIGHT; j++) {
                int n = RandomUtils.uniform(localRand, 3);
                if (j == 0 || n <= 1) {
                    assert (localRoomInfo[i][j] != null);
                    localRoomInfo[i][j].up = RandomUtils.uniform(localRand, 5, 9);
                    localRoomInfo[i][j].right = RandomUtils.uniform(localRand, 5, 9);
                    localRoomInfo[i][j].down = RandomUtils.uniform(localRand, 1, 5);
                    localRoomInfo[i][j].left = RandomUtils.uniform(localRand, 1, 5);
                    localRoomInfo[i][j].hasRoom = true;
                    createRoom(i, j, localRoomInfo[i][j]);
                }
            }
        }
    }

    private void createRoom(int roomI, int roomJ, RoomInfo r) {
        for (int i = roomI * 10 + r.left; i <= roomI * 10 + r.right; i++) {
            for (int j = roomJ * 10 + r.down; j <= roomJ * 10 + r.up; j++) {
                localMap[i][j] = Tileset.FLOOR;
            }
        }
    }

    private void connectRooms() {
        for (int i = 0; i < ROOMWIDTH; i++) {
            for (int j = 0; j < ROOMHEIGHT; j++) {
                if (localRoomInfo[i][j].hasRoom) {
                    for (int k = j + 1; k < ROOMHEIGHT; k++) {
                        if (localRoomInfo[i][k].hasRoom) {
                            connectVertical(i, j, k);
                            break;
                        }
                    }

                    for (int k = i + 1; k < ROOMWIDTH; k++) {
                        if (localRoomInfo[k][j].hasRoom) {
                            connectHorizontal(i, k, j);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void connectVertical(int i, int j1, int j2) {
        assert (j1 < j2);
        RoomInfo r1 = localRoomInfo[i][j1];
        RoomInfo r2 = localRoomInfo[i][j2];
        int startI1 = i * 10 + RandomUtils.uniform(localRand, r1.left, r1.right + 1);
        int startI2 = i * 10 + RandomUtils.uniform(localRand, r2.left, r2.right + 1);
        int startJ1 = j1 * 10 + r1.up + 1;
        int startJ2 = j2 * 10 + r2.down - 1;
        while (true) {
            localMap[startI1][startJ1] = Tileset.FLOOR;
            localMap[startI2][startJ2] = Tileset.FLOOR;
            if (startJ1 == startJ2) {
                break;
            }
            if (RandomUtils.uniform(localRand, 2) == 0) {
                startJ1++;
            } else {
                startJ2--;
            }
        }
        if (startI1 < startI2) {
            for (int k = startI1; k <= startI2; k++) {
                localMap[k][startJ2] = Tileset.FLOOR;
            }
        } else {
            for (int k = startI2; k <= startI1; k++) {
                localMap[k][startJ2] = Tileset.FLOOR;
            }
        }
    }

    private void connectHorizontal(int i1, int i2, int j) {
        assert (i1 < i2);
        RoomInfo r1 = localRoomInfo[i1][j];
        RoomInfo r2 = localRoomInfo[i2][j];
        int startJ1 = j * 10 + RandomUtils.uniform(localRand, r1.down, r1.up + 1);
        int startJ2 = j * 10 + RandomUtils.uniform(localRand, r2.down, r2.up + 1);
        int startI1 = i1 * 10 + r1.right + 1;
        int startI2 = i2 * 10 + r2.left - 1;
        while (true) {
            localMap[startI1][startJ1] = Tileset.FLOOR;
            localMap[startI2][startJ2] = Tileset.FLOOR;
            if (startI1 == startI2) {
                break;
            }
            if (RandomUtils.uniform(localRand, 2) == 0) {
                startI1++;
            } else {
                startI2--;
            }
        }
        if (startJ1 < startJ2) {
            for (int k = startJ1; k <= startJ2; k++) {
                localMap[startI2][k] = Tileset.FLOOR;
            }
        } else {
            for (int k = startJ2; k <= startJ1; k++) {
                localMap[startI2][k] = Tileset.FLOOR;
            }
        }
    }

    private void fillingWalls() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (localMap[i][j] == Tileset.FLOOR) {
                    for (int biasI = -1; biasI < 2; biasI++) {
                        for (int biasJ = -1; biasJ < 2; biasJ++) {
                            if (withinRange(i + biasI, j + biasJ)
                                    && localMap[i + biasI][j + biasJ] == Tileset.NOTHING) {
                                localMap[i + biasI][j + biasJ] = Tileset.WALL;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean withinRange(int i, int j) {
        if (i >= 0 && i < WIDTH && j >= 0 && j < HEIGHT) {
            return true;
        }
        return false;
    }

}
