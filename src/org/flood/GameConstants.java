package org.flood;

import java.awt.*;

/**
 * A class containing all the shared game constants.
 * <p/>
 * Created by Bernardo Sulzbach on 31/10/14.
 */
public class GameConstants {

    protected static final int MENU_BAR_HEIGHT = 20;
    protected static final int STATUS_BAR_HEIGHT = 20;

    static final String END_GAME_TITLE = "Flood complete!"; // Seriously? Who would have thought that?
    static final String END_GAME_MESSAGE = "Everything was flooded.\nPlay again?"; // No. Do not play this again.

    static final TileArrayInitializationMode DEFAULT_INITIALIZATION_MODE = TileArrayInitializationMode.COMPLEX;

    // Should be nonnegative and smaller than or equal to one.
    static double WATER_RATE = 0.2;
}
