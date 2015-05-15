package org.flood;

/**
 * Tile class that stores a TileType and provides convenience predicate methods about its type.
 * <p/>
 * Created by Bernardo Sulzbach on 02/11/14.
 */
public class Tile {

    private TileType type;
    private Population population;

    public Tile(TileType type) {
        this.type = type;
        this.population = Population.makePopulation(type);
    }

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        if (this.type == type) {
            throw new AssertionError("Redundant setType(TileType) call.");
        }
        if (type == TileType.WATER) {
            population.setTotal(0);
        }
        this.type = type;
    }

    public boolean isBeach() {
        return type.equals(TileType.BEACH);
    }

    public boolean isWater() {
        return type.equals(TileType.WATER);
    }

    public Population getPopulation() {
        return population;
    }

}
