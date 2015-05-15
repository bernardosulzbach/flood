package org.flood;

/**
 * TileType enum that defines all the types of tiles there are.
 * <p/>
 * Created by Bernardo Sulzbach on 02/11/14.
 */
public enum TileType {

    HILL(4), BEACH(2), WATER(0);

    final int suggestedPopulation;

    TileType(int suggestedPopulation) {
        this.suggestedPopulation = suggestedPopulation;
    }

}
