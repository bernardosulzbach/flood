package org.flood.algorithms;

import org.flood.*;

/**
 * Another simple initialization algorithm. Makes lake-like structures.
 * <p/>
 * Created by Bernardo on 15/05/2015.
 */
class SquaresInitializationAlgorithm implements InitializationAlgorithm {

    @Override
    public void initialize(TileMatrix tileMatrix) {
        Dimension matrixDimensions = tileMatrix.getDimensions();
        for (int y = 0; y < matrixDimensions.height; y++) {
            for (int x = 0; x < matrixDimensions.width; x++) {
                if (y % 2 == 0) {
                    if (x % 2 == 0) {
                        Tile tile;
                        if (GameData.random.nextDouble() < GameData.WATER_RATE) {
                            tile = new Tile(TileType.WATER);
                        } else {
                            tile = new Tile(TileType.HILL);
                        }
                        tileMatrix.setTile(x, y, tile);
                    } else {
                        tileMatrix.setTile(x, y, new Tile(tileMatrix.getTile(x - 1, y).getType()));
                    }
                } else {
                    tileMatrix.setTile(x, y, new Tile(tileMatrix.getTile(x, y - 1).getType()));
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Squares";
    }

}
