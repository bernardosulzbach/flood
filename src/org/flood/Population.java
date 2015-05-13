package org.flood;

/**
 * Population class that represents a Tile's population.
 * <p/>
 * Created by Bernardo on 13/05/2015.
 */
public class Population {

    private int total;

    public static Population makePopulation(TileType type) {
        Population population = new Population();
        population.setTotal(type.suggestedPopulation);
        return population;
    }

    public int getTotal() {
        return total;
    }

    /**
     * Sets the new total population value. If the specified value is negative, zero will be used.
     *
     * @param total the new total population count
     */
    public void setTotal(int total) {
        this.total = Math.max(total, 0);
    }

}
