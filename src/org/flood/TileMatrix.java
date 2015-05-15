package org.flood;

import java.util.ArrayList;

/**
 * TileMatrix class that is a wrapper to a bidimensional array of Tile objects.
 * <p/>
 * Created by Bernardo Sulzbach on 02/11/14.
 */
class TileMatrix {

    private final Tile[][] tileArray;
    private final ArrayList<Tile> alreadyHitInThisChainReaction = new ArrayList<Tile>();
    private final Dimension dimensions;
    private int lastWaterCount;

    public TileMatrix(GameSize gameSize) {
        this.tileArray = new Tile[gameSize.tilesPerRow][gameSize.tilesPerRow];
        this.dimensions = new Dimension(gameSize.tilesPerRow, gameSize.tilesPerRow);
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
        InitializationAlgorithms.getBestAlgorithm().initialize(this);
        updateWaterCount();
        assertMinimumWaterLevel();
        updateTiles(true);
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

    /**
     * Sets a Tile to a given coordinate pair.
     *
     * @param x    the x coordinate
     * @param y    the y coordinate
     * @param tile the Tile object
     */
    public void setTile(int x, int y, Tile tile) {
        tileArray[y][x] = tile;
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

    public Dimension getDimensions() {
        return dimensions;
    }

}
