package game2048;

import java.util.Formatter;
import java.util.Iterator;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author Suiren
 *  Ref: https://juejin.cn/post/7348842402826862633
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when the game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /**
     *  How to process the tilt?
     *  We can divide the move and merge into three steps:
     *  1. First move the non-empty tiles upwards to the top.
     *  2. Then merge the tiles from the top to the bottom if possible.
     *  3. After merging, move the non-empty tiles upwards again.
     *
     *  New empty slots result from the merging,
     *  so the last step can be integrated into the merging step as a consequence.
     *
     *  If the dst == src row, the move is not a real move, and the column remains unchanged.
     *  Notice that iff top is greater than the checking row, the move is a real move.
     */
    private boolean pushTilesUp(int col) {
        boolean changed = false;
        for(int row = board.size() - 1;row >= 0;--row) {
            Tile t = board.tile(col, row);
            if(t != null) {
                int top = board.size() - 1; // judge each row with the original top, prevent false move
                while(top > row) { // when the top is greater than row, we can move the tile upwards
                    if(board.tile(col, top) == null) { // ensure that no tile over head
                        board.move(col, top, t); // move to the top
                        changed = true; // a real move happened
                        break; // move once only, then check the next row
                    }
                    --top; // otherwise drop the top
                }
            }
        }
        return changed;
    }

    /**
     *  After pushing all tiles onwards, we must merge the tiles if possible.
     *  We check if the adjacent tiles have the same value, and merge into the upper slot.
     *  Thus, we can conclude that the merging tiles must not be null.
     *
     *  It may end up with some empty slots after merging,
     *  so we should call the pushTilesUp again but not another merge.
     */
    private boolean mergeTiles(int col) {
        boolean changed = false;
        for(int row = board.size() - 1;row >= 1;--row) { // merge dst will not be last row
            Tile upper = board.tile(col, row);
            Tile lower = board.tile(col, row - 1);
            if(upper != null && lower != null && upper.value() == lower.value()) {
                board.move(col, row, lower);
                score += 2 * lower.value();
                changed = true;
            }
        }
        if(changed) {
            pushTilesUp(col);
        }
        return changed;
    }


    /**
     *  Given that the default direction is North, this method will tilt the tiles onwards.
     *  For example, when the input direction is East, then we can rotate the board and
     *  set the original East to the NORTH Side.
     *  Then, the East tilt equals to the operation of tilting onwards after rotation.
     *  Consequently, we can ignore the input direction, and rotate the board and
     *  tilt the tiles onwards column wisely, and merge them if possible.
     *
     *  The main issue is to figure out which row each tile should be moved to.
     *  @param col column to process
     */
    private boolean colProcess(int col) {
        boolean first = pushTilesUp(col);
        boolean second = mergeTiles(col);
        return first || second;
    }


    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;
        board.setViewingPerspective(side);
        for(int col = 0; col < board.size(); ++col) {
            changed |= colProcess(col); // if one colProcess changes the board, the whole board is changed
        }

        board.setViewingPerspective(Side.NORTH); // change the viewing perspective back to north
        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        Iterator<Tile> it = b.iterator();
        while(it.hasNext()) {
            if(it.next() == null)
                return true;
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        Iterator<Tile> it = b.iterator();
        while(it.hasNext()) {
            Tile tile = it.next();
            if(tile != null && tile.value() == MAX_PIECE)
                return true;
        }
        return false;
    }

    /**
     * Check if a tile is in bounds
     * @param col column
     * @param row row
     * @param size board line length
     * @return true if the tile is in bounds
     */
    private static boolean inBounds(int col, int row, int size) {
        return col >= 0 && col < size && row >= 0 && row < size;
    }

    /**
     * Check if there exist two adjacent tiles with the same value.
     * Remember that the iteration is linear, but the board is square.
     *
     * @param b the board to check for mergeable tiles
     * @return true if a mergeable tile exists, false otherwise
     */
    private static boolean mergeableTileExists(Board b) {
        for(int col = 0;col < b.size();++col) {
            for(int row = 0;row < b.size();++row) {
                boolean north = inBounds(col, row + 1, b.size()); // if north tile is in bounds
                boolean east = inBounds(col + 1, row, b.size()); // if east tile is in bounds
                if((north && b.tile(col, row).value() == b.tile(col, row + 1).value()) ||
                        (east && b.tile(col, row).value() == b.tile(col + 1, row).value()))
                    return true;
            }
        }

        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        return emptySpaceExists(b) || mergeableTileExists(b);
    }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
