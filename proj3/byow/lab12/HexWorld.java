package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 60;
    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    private static void fillWithNothing(TETile[][] world) {
        for (int x = 0; x < WIDTH; ++x) {
            for (int y = 0; y < HEIGHT; ++y) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    private static void drawRow(TETile[][] world, TETile tile, Position start, int length) {
        for (int i = 0; i < length; ++i) {
            world[start.x + i][start.y] = tile;
        }
    }

    private static class Position {
        int x;
        int y;
        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public Position shift(int dx, int dy) {
            return new Position(x + dx, y + dy);
        }
    }

    private static void addHexagonHelper(TETile[][] world, TETile tile, Position start, int blank, int t) {
        Position startRow = start.shift(blank, 0);
        drawRow(world, tile, startRow, t);

        if (blank > 0) {
            addHexagonHelper(world, tile, start.shift(0, -1), blank - 1, t + 2);
        }

        Position startCorRow = startRow.shift(0, -(2 * blank + 1));
        drawRow(world, tile, startCorRow, t);
    }

    private static void addHexagon(TETile[][] world, TETile tile, Position start, int size) {
        if (size < 2) {
            return;
        }
        addHexagonHelper(world, tile, start, size - 1, size);
    }

    private static void drawWorld(TETile[][] world, Position p, int hexSize, int tessSize) {
        fillWithNothing(world);
        addHexColumn(world, p, hexSize, tessSize);
        for (int i = 1; i < tessSize; ++i) {
            p = getTopRightNeighbor(p, hexSize);
            addHexColumn(world, p, hexSize, tessSize + i);
        }

        for (int i = tessSize - 2; i >= 0; --i) {
            p = getBottomRightNeighbor(p, hexSize);
            addHexColumn(world, p, hexSize, tessSize + i);
        }
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(4);
        return switch (tileNum) {
            case 0 -> Tileset.FLOWER;
            case 1 -> Tileset.GRASS;
            case 2 -> Tileset.WATER;
            case 3 -> Tileset.TREE;
            default -> Tileset.NOTHING;
        };
    }

    private static void addHexColumn(TETile[][] world, Position start, int size, int num) {
        if (num < 1) return;
        addHexagon(world, randomTile(), start, size);
        if (size > 1) {
            addHexColumn(world, getBottomNeighbor(start, size), size, num - 1);
        }
    }

    private static Position getBottomNeighbor(Position p, int size) {
        return p.shift(0, -2 * size);
    }

    private static Position getTopRightNeighbor(Position p, int size) {
        return p.shift(2 * size - 1, size);
    }

    private static Position getBottomRightNeighbor(Position p, int size) {
        return p.shift(2 * size - 1, -size);
    }

    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        drawWorld(world, new Position(10, 30), 3, 3);

        // draws the world to the screen
        ter.renderFrame(world);
    }
}
