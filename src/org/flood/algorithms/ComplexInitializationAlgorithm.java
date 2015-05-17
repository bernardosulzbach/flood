package org.flood.algorithms;

import org.flood.*;

/**
 * The most complex initialization algorithm so far.
 * <p/>
 * Created by Bernardo on 15/05/2015.
 */
class ComplexInitializationAlgorithm implements InitializationAlgorithm {

    // Uses the same algorithm as SQUARES, but after filling a square of tiles of side two with water, there is
    // a 40 % chance of spreading water over one extra tile  that may overlap existing water and
    // a 20 % chance of spreading water over two extra tiles that may overlap existing water.
    @Override
    public void initialize(TileMatrix tileMatrix) {
        double randomDouble;
        Dimension matrixDimensions = tileMatrix.getDimensions();
        for (int j = 0; j < matrixDimensions.height; j += 2) {
            for (int i = 0; i < matrixDimensions.width; i += 2) {
                // Check if this tile will be filled with water.
                if (GameData.random.nextDouble() < GameData.WATER_RATE) {
                    int spreading = 0;
                    // Get how many tiles will get the water spread effect.
                    randomDouble = GameData.random.nextDouble();
                    if (randomDouble < 0.2) {
                        spreading = 2;
                    } else if (randomDouble < 0.4) {
                        spreading = 1;
                    }
                    int notDiagonalNeighbors = calculateNotDiagonalNeighbors(i, j, matrixDimensions);
                    int remainingNeighbors = notDiagonalNeighbors;
                    for (int b = -1; b < 3; b++) {
                        int y = j + b;
                        if (y >= 0 && y < matrixDimensions.height) {
                            for (int a = -1; a < 3; a++) {
                                int x = i + a;
                                if (x >= 0 && x < matrixDimensions.width) {
                                    // Check if the algorithm is filling the square or hitting the margins.
                                    if ((a == 0 || a == 1) && (b == 0 || b == 1)) {
                                        tileMatrix.setTile(x, y, new Tile(TileType.WATER));
                                    } else {
                                        // Is there water to spread and we are not in a diagonal?
                                        if (spreading != 0 && !((a == -1 || a == 2) && (b == -1 || b == 2))) {
                                            // Check if this is the marginal tile to start filling.
                                            if (remainingNeighbors == 1 || GameData.random.nextInt(notDiagonalNeighbors) == 0) {
                                                // Fill the first tile.
                                                tileMatrix.setTile(x, y, new Tile(TileType.WATER));
                                                if (spreading == 2) {
                                                    // If two tiles should be filled, fill the second tile.
                                                    // Spread, preferentially, to the left.
                                                    if (a == -1) {
                                                        spreadWater(x, y, Direction.WEST, tileMatrix);
                                                    }
                                                    // Spread, preferentially, to the right.
                                                    else if (a == 2) {
                                                        spreadWater(x, y, Direction.EAST, tileMatrix);
                                                    }
                                                    // Spread, preferentially, towards north.
                                                    else if (b == -1) {
                                                        spreadWater(x, y, Direction.NORTH, tileMatrix);
                                                    }
                                                    // Spread, preferentially, towards south.
                                                    else {
                                                        spreadWater(x, y, Direction.SOUTH, tileMatrix);
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
                        int y = j + b;
                        if (y < matrixDimensions.height) {
                            for (int a = 0; a < 2; a++) {
                                int x = i + a;
                                if (x < matrixDimensions.width) {
                                    tileMatrix.setTile(x, y, new Tile(TileType.HILL));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Get how many neighbor tiles a square of side 2 has. Diagonals not accounted for.
     *
     * @param i                the horizontal coordinate
     * @param j                the vertical coordinate
     * @param matrixDimensions the Dimensions object that represents the dimensions of the TileMatrix
     * @return an integer representing how many neighbor tiles the square of side 2 starting at (i, j) has
     */
    private int calculateNotDiagonalNeighbors(int i, int j, Dimension matrixDimensions) {
        // TODO write tests for this.
        if (matrixDimensions.height < 2 || matrixDimensions.width < 2) { // Just in case.
            throw new AssertionError("Matrix is too small.");
        }
        int neighbors = 0;
        if (i > 0) {
            neighbors += 2;
        }
        if (i < matrixDimensions.width - 1) {
            neighbors += 2;
        }
        if (j > 0) {
            neighbors += 2;
        }
        if (j < matrixDimensions.height - 1) {
            neighbors += 2;
        }
        return neighbors;
    }

    /**
     * Attempts to spread water towards a given direction from (x, y). This method does not alter the type of a
     * Tile, instead it overwrites the old Tile (if there was one).
     *
     * @param x         the x coordinate
     * @param y         the y coordinate
     * @param direction the direction towards where water will be spread
     */
    private void spreadWater(int x, int y, Direction direction, TileMatrix matrix) {
        if (direction == Direction.WEST) {
            if (x > 0) {
                x--;
            } else if (y > 0) {
                y--;
            } else {
                y++;
            }
        } else if (direction == Direction.EAST) {
            if (x < matrix.getDimensions().width - 1) {
                x++;
            } else if (y > 0) {
                y--;
            } else {
                y++;
            }
        } else if (direction == Direction.NORTH) {
            if (y > 0) {
                y--;
            } else if (x > 0) {
                x--;
            } else {
                x++;
            }
        } else {
            if (y < matrix.getDimensions().height - 1) {
                y++;
            } else if (x > 0) {
                x--;
            } else {
                x++;
            }
        }
        matrix.setTile(x, y, new Tile(TileType.WATER));
    }

    @Override
    public String toString() {
        return "Complex";
    }

}