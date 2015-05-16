package org.flood.algorithms;

import org.flood.*;

/**
 * The simplest initialization algorithm. Randomly assigns TileTypes to Tiles.
 * <p/>
 * Created by Bernardo on 15/05/2015.
 */
class SimpleInitializationAlgorithm implements InitializationAlgorithm {

    @Override
    public String getName() {
        return "Simple";
    }

    @Override
    public void initialize(TileMatrix tileMatrix) {
        Dimension matrixDimensions = tileMatrix.getDimensions();
        for (int y = 0; y < matrixDimensions.height; y++) {
            for (int x = 0; x < matrixDimensions.width; x++) {
                Tile tile;
                if (GameData.random.nextDouble() < GameData.WATER_RATE) {
                    tile = new Tile(TileType.WATER);
                } else {
                    tile = new Tile(TileType.HILL);
                }
                tileMatrix.setTile(x, y, tile);
            }
        }
    }

}
