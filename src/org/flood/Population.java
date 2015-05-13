package org.flood;

import java.util.HashSet;
import java.util.Set;

/**
 * Population class that represents a Tile's population.
 * <p/>
 * Created by Bernardo on 13/05/2015.
 */
public class Population {

    private int total;
    private Set<Double[]> humans = new HashSet<Double[]>();

    public static Population makePopulation(TileType type) {
        Population population = new Population();
        population.setTotal(type.suggestedPopulation);
        for (int i = 0; i < type.suggestedPopulation; i++) {
            // The random distribution is pretty ugly. Consider a poisson disk algorithm.
            Double[] human = {GameData.random.nextDouble(), GameData.random.nextDouble()};
            population.humans.add(human);
        }
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

    /**
     * Returns a set of pairs of doubles representing how much (relatively) the representation of each human of this
     * population should be offset.
     *
     * @return a set of arrays of Double of length 2 that vary from 0.0d (inclusive) up to 1.0d (exclusive)
     */
    public Set<Double[]> getHumans() {
        return humans;
    }

}
