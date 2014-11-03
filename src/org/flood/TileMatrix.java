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
        int count = 0;
        for (Tile[] row : tileArray) {
            for (Tile tile : row) {
                if (tile.isWater()) {
                    count++;
                }
            }
        }
        return count;
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
        updateTiles();
    }

    /**
     * Floods the tile found in (i, j) and propagates the flood all floodable adjacent tiles.
     *
     * @param i the i coordinate.
     * @param j the j coordinate.
     */
    private void flood(int i, int j) {
        if (!alreadyHitInThisChainReaction.contains(tileArray[j][i]) && tileArray[j][i].isFloodable()) {
            alreadyHitInThisChainReaction.add(tileArray[j][i]);
            tileArray[j][i].setType(TileType.WATER);
            if (j != 0) {
                if (tileArray[j - 1][i].isFloodable()) {
                    flood(i, j - 1);
                }
            }
            if (j != tileArray.length - 1) {
                if (tileArray[j + 1][i].isFloodable()) {
                    flood(i, j + 1);
                }
            }
            if (i != 0) {
                if (tileArray[j][i - 1].isFloodable()) {
                    flood(i - 1, j);
                }
            }
            if (i != tileArray.length - 1) {
                if (tileArray[j][i + 1].isFloodable()) {
                    flood(i + 1, j);
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
                                    tileArray[y][x] = new Tile(TileType.GRASS);
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
                        //System.out.println(j + " " + i);

                        // Check if this is a water square or a grass square.
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
                                                // Is there water to spread?
                                                if (spreading != 0) {
                                                    // Not a diagonal, right?
                                                    if (!((a == -1 || a == 2) && (b == -1 || b == 2))) {
                                                        // Check if this is the marginal tile to start filling.
                                                        if (remainingNeighbors == 1 || GameData.random.nextInt(notDiagonalNeighbors) == 0) {
                                                            tileArray[y][x] = new Tile(TileType.WATER);
                                                            if (spreading == 2) {
                                                                // Not so much. Even more genius. Whoa!
                                                                // Spread, preferentially, to the left.
                                                                if (a == -1) {
                                                                    if (x > 0) {
                                                                        tileArray[y][x - 1] = new Tile(TileType.WATER);
                                                                    } else if (y > 0) {
                                                                        tileArray[y - 1][x] = new Tile(TileType.WATER);
                                                                    } else {
                                                                        tileArray[y + 1][x] = new Tile(TileType.WATER);
                                                                    }
                                                                }
                                                                // Spread, preferentially, to the right.
                                                                else if (a == 2) {
                                                                    if (x < tileArray.length - 1) {
                                                                        tileArray[y][x + 1] = new Tile(TileType.WATER);
                                                                    } else if (y > 0) {
                                                                        tileArray[y - 1][x] = new Tile(TileType.WATER);
                                                                    } else {
                                                                        tileArray[y + 1][x] = new Tile(TileType.WATER);
                                                                    }
                                                                }
                                                                // Spread, preferentially, towards north.
                                                                else if (b == -1) {
                                                                    if (y > 0) {
                                                                        tileArray[y - 1][x] = new Tile(TileType.WATER);
                                                                    } else if (x > 0) {
                                                                        tileArray[y][x - 1] = new Tile(TileType.WATER);
                                                                    } else {
                                                                        tileArray[y][x + 1] = new Tile(TileType.WATER);
                                                                    }
                                                                }
                                                                // Spread, preferentially, towards south.
                                                                else {
                                                                    if (y < tileArray.length - 1) {
                                                                        tileArray[y + 1][x] = new Tile(TileType.WATER);
                                                                    } else if (x > 0) {
                                                                        tileArray[y][x - 1] = new Tile(TileType.WATER);
                                                                    } else {
                                                                        tileArray[y][x + 1] = new Tile(TileType.WATER);
                                                                    }
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
                            }
                        } else {
                            for (int b = 0; b < 2; b++) {
                                y = j + b;
                                if (y < tileArray.length) {
                                    for (int a = 0; a < 2; a++) {
                                        x = i + a;
                                        if (x < tileArray.length) {
                                            tileArray[y][x] = new Tile(TileType.GRASS);
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
        assertMinimumWaterLevel();
        updateTiles();
    }

    /**
     * Guarantees that there is at least one water tile on the array. If (and only if) water was added, it also updates
     * the tiles.
     */
    private void assertMinimumWaterLevel() {
        if (getWaterCount() == 0) {
            tileArray[GameData.random.nextInt(tileArray.length)][GameData.random.nextInt(tileArray.length)].setType(TileType.WATER);
            updateTiles();
        }
    }

    /**
     * Reinitializes the Tile array with a distribution of tiles based on WATER_RATE and on the generator mode. This
     * method also updates the tiles considered beaches.
     */
    void reinitialize() {
        initialize();
    }

    private void updateTiles() {
        boolean skipRemainingAdjacentTiles;
        int x, y;
        for (int j = 0; j < tileArray.length; j++) {
            for (int i = 0; i < tileArray.length; i++) {
                if (!tileArray[j][i].isWater()) {
                    skipRemainingAdjacentTiles = false;
                    for (int b = -1; b <= 1 && !skipRemainingAdjacentTiles; b++) {
                        y = j + b;
                        if (y >= 0 && y < tileArray.length) {
                            for (int a = -1; a <= 1 && !skipRemainingAdjacentTiles; a++) {
                                x = i + a;
                                if (x >= 0 && x < tileArray.length) {
                                    if (tileArray[y][x].isWater()) {
                                        tileArray[j][i].setType(TileType.SAND);
                                        skipRemainingAdjacentTiles = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
