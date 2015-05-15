package org.flood.algorithms;

/**
 * Factory methods for InitializationAlgorithm.
 * <p/>
 * Created by Bernardo on 15/05/2015.
 */
public abstract class InitializationAlgorithms {

    private static final InitializationAlgorithm SIMPLE = new SimpleInitializationAlgorithm();
    private static final InitializationAlgorithm SQUARES = new SquaresInitializationAlgorithm();
    private static final InitializationAlgorithm COMPLEX = new ComplexInitializationAlgorithm();

    public static InitializationAlgorithm getBestAlgorithm() {
        return COMPLEX;
    }

}
