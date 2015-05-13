package org.flood;

import java.util.ArrayList;

/**
 * TileMatrix class that is a wrapper to a bidimensional array of Tile objects.
 * <p/>
 * Created by Bernardo Sulzbach on 02/11/14.
 */

class TileMatrix {

    private final Tile[][] tileArray;

    private final GeneratorMode generatorMode;

    private final ArrayList<Tile> alreadyHitInThisChainReaction = new ArrayList<Tile>();

    private int lastWaterCount;

    public TileMatrix(GameSize gameSize, GeneratorMode generatorMode) {
        this.tileArray = new Tile[gameSize.tilesPerRow][gameSize.tilesPerRow];
        this.generatorMode = generatorMode;
        initialize();
    }

    /**
     * Returns the type of the Tile at (i, j).
     *
     * @param i the i coordinate.
     * @param j the j coordinate.
     * @return a TileType.
     */
    TileType getTileType(int i, int j) {
        return tileArray[j][i].getType();
    }

    /**
     * Returns how many tiles are water.
     *
     * @return an int.
     */
    int getWaterCount() {
        return lastWaterCount;
    }

    /**
     * Updates the water count variable. Should be called whenever the number of water tiles changes.
     */
    private void updateWaterCount() {
        lastWaterCount = 0;
        for (Tile[] row : tileArray) {
            for (Tile tile : row) {
                if (tile.isWater()) {
                    lastWaterCount++;
                }
            }
        }
    }

    /**
     * Starts a flood in the tile found in (i, j) after checking that it is floodable.
     *
     * @param i the i coordinate.
     * @param j the j coordinate.
     */
    void startFlood(int i, int j) {
        if (i < 0 || i >= tileArray.length || j < 0 || j >= tileArray.length) {
            throw new IllegalArgumentException("i and j must be nonnegative and smaller than the length of the array.");
        }
        flood(i, j);
        alreadyHitInThisChainReaction.clear();
        updateTiles(false);
        updateWaterCount();
    }

    /**
     * Floods the tile found in (i, j) and propagates the flood all floodable adjacent tiles.
     *
     * @param i the i coordinate.
     * @param j the j coordinate.
     */
    private void flood(int i, int j) {
        flood(i, j, false);
    }

    /**
     * Floods the tile found in (i, j) and propagates the flood all floodable adjacent tiles.
     *
     * @param i the i coordinate.
     * @param j the j coordinate.
     */
    private void flood(int i, int j, boolean simulated) {
        if (!alreadyHitInThisChainReaction.contains(tileArray[j][i]) && tileArray[j][i].isBeach()) {
            alreadyHitInThisChainReaction.add(tileArray[j][i]);
            if (!simulated) {
                tileArray[j][i].setType(TileType.WATER);
            }
            if (j != 0) {
                if (tileArray[j - 1][i].isBeach()) {
                    flood(i, j - 1, simulated);
                }
            }
            if (j != tileArray.length - 1) {
                if (tileArray[j + 1][i].isBeach()) {
                    flood(i, j + 1, simulated);
                }
            }
            if (i != 0) {
                if (tileArray[j][i - 1].isBeach()) {
                    flood(i - 1, j, simulated);
                }
            }
            if (i != tileArray.length - 1) {
                if (tileArray[j][i + 1].isBeach()) {
                    flood(i + 1, j, simulated);
                }
            }
        }
    }

    private void initialize() {
        switch (generatorMode) {
            // Randomly chooses between water or grass to fill a tile.
            case SIMPLE: {
                for (int y = 0; y < tileArray.length; y++) {
                    for (int x = 0; x < tileArray.length; x++) {
                        if (GameData.random.nextDouble() < GameData.WATER_RATE) {
                            tileArray[y][x] = new Tile(TileType.WATER);
                        } else {
                            tileArray[y][x] = new Tile(TileType.WATER);
                        }
                    }
                }
                break;
            }
            // Randomly chooses between water or grass to fill a square of tiles of side two.
            case SQUARES: {
                for (int y = 0; y < tileArray.length; y++) {
                    for (int x = 0; x < tileArray.length; x++) {
                        if (y % 2 == 0) {
                            if (x % 2 == 0) {
                                if (GameData.random.nextDouble() < GameData.WATER_RATE) {
                                    tileArray[y][x] = new Tile(TileType.WATER);
                                } else {
                                    tileArray[y][x] = new Tile(TileType.HILL);
                                }
                            } else {
                                tileArray[y][x] = new Tile(tileArray[y][x - 1].getType());
                            }
                        } else {
                            tileArray[y][x] = new Tile(tileArray[y - 1][x].getType());
                        }
                    }
                }
                break;
            }
            // Uses the same algorithm as SQUARES, but after filling a square of tiles of side two with water, there is
            // a 40 % chance of spreading water over one extra tile  that may overlap existing water and
            // a 20 % chance of spreading water over two extra tiles that may overlap existing water.
            // "Much complex, very genius." - B. Sulzbach.
            case COMPLEX: {
                // Declare everything here to get fantastical mega-godly ultra-fast ridiculously-fabulous performance.
                int x, y;
                int spreading = 0;
                int notDiagonalNeighbors;
                int remainingNeighbors;
                double randomDouble;
                for (int j = 0; j < tileArray.length; j += 2) {
                    for (int i = 0; i < tileArray.length; i += 2) {

                        // Check if this tile will be filled with water.
                        if (GameData.random.nextDouble() < GameData.WATER_RATE) {

                            // Get how many tiles will get the water spread effect.
                            randomDouble = GameData.random.nextDouble();
                            if (randomDouble < 0.2) {
                                spreading = 2;
                            } else if (randomDouble < 0.4) {
                                spreading = 1;
                            }

                            // Get how many neighbor tiles exist. Diagonals not accounted for.
                            if (j == 0 || j == tileArray.length - 1) {
                                if (i == 0 || i == tileArray.length - 1) {
                                    notDiagonalNeighbors = remainingNeighbors = 4;
                                } else {
                                    notDiagonalNeighbors = remainingNeighbors = 6;
                                }
                            } else {
                                if (i == 0 || i == tileArray.length - 1) {
                                    notDiagonalNeighbors = remainingNeighbors = 6;
                                } else {
                                    notDiagonalNeighbors = remainingNeighbors = 8;
                                }
                            }

                            for (int b = -1; b < 3; b++) {
                                y = j + b;
                                if (y >= 0 && y < tileArray.length) {
                                    for (int a = -1; a < 3; a++) {
                                        x = i + a;
                                        if (x >= 0 && x < tileArray.length) {
                                            // Check if the algorithm is filling the square or hitting the margins.
                                            if ((a == 0 || a == 1) && (b == 0 || b == 1)) {
                                                tileArray[y][x] = new Tile(TileType.WATER);
                                            } else {
                                                // Is there water to spread and we are not in a diagonal?
                                                if (spreading != 0 && !((a == -1 || a == 2) && (b == -1 || b == 2))) {
                                                    // Check if this is the marginal tile to start filling.
                                                    if (remainingNeighbors == 1 || GameData.random.nextInt(notDiagonalNeighbors) == 0) {
                                                        // Fill the first tile.
                                                        tileArray[y][x] = new Tile(TileType.WATER);
                                                        if (spreading == 2) {
                                                            // If two tiles should be filled, fill the second tile.
                                                            // Spread, preferentially, to the left.
                                                            if (a == -1) {
                                                                spreadWater(x, y, Direction.WEST);
                                                            }
                                                            // Spread, preferentially, to the right.
                                                            else if (a == 2) {
                                                                spreadWater(x, y, Direction.EAST);
                                                            }
                                                            // Spread, preferentially, towards north.
                                                            else if (b == -1) {
                                                                spreadWater(x, y, Direction.NORTH);
                                                            }
                                                            // Spread, preferentially, towards south.
                                                            else {
                                                                spreadWater(x, y, Direction.SOUTH);
                                                            }
                                                        }
                                                        // The water was spread. It shall now be zeroed.
                                                        // "Much complex, very genius." - Echoes from my past.
                                                        spreading = 0;
                                                    } else {
                                                        // After hitting a viable neighbor but not spreading water
                                                        // over it, decrement our remaining neighbors counter.
                                                        remainingNeighbors--;
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        } else { // The tile is not going to be filled with water, use hill instead.
                            for (int b = 0; b < 2; b++) {
                                y = j + b;
                                if (y < tileArray.length) {
                                    for (int a = 0; a < 2; a++) {
                                        x = i + a;
                                        if (x < tileArray.length) {
                                            tileArray[y][x] = new Tile(TileType.HILL);
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
                break;
            }
        }
        updateWaterCount();
        assertMinimumWaterLevel();
        updateTiles(true);
    }

    /**
     * Attempts to spread water towards a given direction from (x, y).
     *
     * @param x         the x coordinate.
     * @param y         the y coordinate.
     * @param direction the direction towards where water will be spread.
     */
    private void spreadWater(int x, int y, Direction direction) {
        switch (direction) {
            case WEST:
                if (x > 0) {
                    tileArray[y][x - 1] = new Tile(TileType.WATER);
                } else if (y > 0) {
                    tileArray[y - 1][x] = new Tile(TileType.WATER);
                } else {
                    tileArray[y + 1][x] = new Tile(TileType.WATER);
                }
            case EAST:
                if (x < tileArray.length - 1) {
                    tileArray[y][x + 1] = new Tile(TileType.WATER);
                } else if (y > 0) {
                    tileArray[y - 1][x] = new Tile(TileType.WATER);
                } else {
                    tileArray[y + 1][x] = new Tile(TileType.WATER);
                }
            case NORTH:
                if (y > 0) {
                    tileArray[y - 1][x] = new Tile(TileType.WATER);
                } else if (x > 0) {
                    tileArray[y][x - 1] = new Tile(TileType.WATER);
                } else {
                    tileArray[y][x + 1] = new Tile(TileType.WATER);
                }
            case SOUTH:
                if (y < tileArray.length - 1) {
                    tileArray[y + 1][x] = new Tile(TileType.WATER);
                } else if (x > 0) {
                    tileArray[y][x - 1] = new Tile(TileType.WATER);
                } else {
                    tileArray[y][x + 1] = new Tile(TileType.WATER);
                }
        }
    }

    ArrayList<Tile> getSelection(int i, int j) {
        flood(i, j, true);
        ArrayList<Tile> selection = new ArrayList<Tile>(alreadyHitInThisChainReaction.size());
        selection.addAll(alreadyHitInThisChainReaction);
        alreadyHitInThisChainReaction.clear();
        return selection;
    }

    /**
     * Guarantees that there is at least one water tile on the array. If (and only if) water was added, it also updates
     * the water count.
     */
    private void assertMinimumWaterLevel() {
        if (getWaterCount() == 0) {
            int x = GameData.random.nextInt(tileArray.length);
            int y = GameData.random.nextInt(tileArray.length);
            tileArray[y][x].setType(TileType.WATER);
            updateWaterCount();
        }
    }

    /**
     * Reinitializes the Tile array with a distribution of tiles based on WATER_RATE and on the generator mode. This
     * method also updates the tiles considered beaches.
     */
    void reinitialize() {
        initialize();
    }

    /**
     * Iterates over the TileMatrix setting all tiles that have at least one water neighbor to beach.
     */
    private void updateTiles(boolean overwrite) {
        for (int j = 0; j < tileArray.length; j++) { // Iterate over the matrix using i and j.
            for (int i = 0; i < tileArray.length; i++) {
                Tile tile = tileArray[j][i];
                if (!tile.isWater() && !tile.isBeach()) { // If the current tile is not water.
                    setToBeachIfThereIsWaterNeighbor(j, i, overwrite);
                }
            }
        }
    }

    /**
     * Sets the specified Tile to a beach if it has at least one water neighbor. If specified to overwrite, creates a
     * new Tile to replace the old one. This is used in the matrix initialization when a Tile does not become a beach
     * after a flood, but starts as one instead. Starting as a beach is different from becoming one as Tile's
     * constructor takes the TileType into account.
     *
     * @param j         the j coordinate
     * @param i         the i coordinate
     * @param overwrite if true, creates a new Tile object to replace the old one
     */
    private void setToBeachIfThereIsWaterNeighbor(int j, int i, boolean overwrite) {
        if (tileArray[j][i].isWater()) {
            throw new AssertionError("Called setToBeachIfThereIsWaterNeighbor for a water tile!");
        }
        for (int b = -1; b <= 1; b++) { // Iterate over all adjacent tiles.
            int y = j + b;
            if (y >= 0 && y < tileArray.length) {
                for (int a = -1; a <= 1; a++) {
                    int x = i + a;
                    if (x >= 0 && x < tileArray.length) {
                        if (tileArray[y][x].isWater()) { // Looking for water.
                            // If you find it, create a beach Tile or set the current Tile to beach.
                            if (overwrite) {
                                tileArray[j][i] = new Tile(TileType.BEACH);
                            } else {
                                tileArray[j][i].setType(TileType.BEACH);
                            }
                            return; // And stop looking.
                        }
                    }
                }
            }
        }
    }

    public Tile getTile(int i, int j) {
        return tileArray[j][i];
    }

    public int getTotalPopulation() {
        int total = 0;
        for (Tile[] row : tileArray) {
            for (Tile tile : row) {
                total += tile.getPopulation().getTotal();
            }
        }
        return total;
    }

}
