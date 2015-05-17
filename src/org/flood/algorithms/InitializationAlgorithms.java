package org.flood.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Factory methods for InitializationAlgorithm.
 * <p/>
 * Created by Bernardo on 15/05/2015.
 */
public abstract class InitializationAlgorithms {

    private static List<InitializationAlgorithm> ALGORITHMS = new ArrayList<InitializationAlgorithm>();

    static {
        ALGORITHMS.add(new SimpleInitializationAlgorithm());
        ALGORITHMS.add(new SquaresInitializationAlgorithm());
        ALGORITHMS.add(new ComplexInitializationAlgorithm());
        ALGORITHMS = Collections.unmodifiableList(ALGORITHMS);
    }

    public static InitializationAlgorithm getDefaultAlgorithm() {
        return ALGORITHMS.get(ALGORITHMS.size() - 1);
    }

    /**
     * Returns a List of all the available InitializationAlgorithms.
     *
     * @return a List of all the available InitializationAlgorithms
     */
    public static List<InitializationAlgorithm> getInitializationAlgorithms() {
        return ALGORITHMS;
    }

}
