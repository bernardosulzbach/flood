package org.flood.algorithms;

import org.flood.TileMatrix;

/**
 * An InitializationAlgorithm.
 * <p/>
 * Created by Bernardo on 15/05/2015.
 */
public interface InitializationAlgorithm {

    String getName();

    void initialize(TileMatrix tileMatrix);

}
