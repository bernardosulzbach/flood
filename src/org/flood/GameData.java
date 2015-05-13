package org.flood;

import java.awt.*;
import java.util.Random;

/**
 * GameData class used to store constant data.
 * <p/>
 * Created by Bernardo Sulzbach on 02/11/14.
 */

class GameData {

    static final int MENU_BAR_HEIGHT = 20;
    static final int STATUS_BAR_HEIGHT = 20;

    static final GeneratorMode DEFAULT_GENERATOR_MODE = GeneratorMode.COMPLEX;
    static final Random random = new Random();
    // Should be nonnegative and smaller than or equal to one.
    static final double WATER_RATE = 0.2;
    // Colors from http://www.tayloredmktg.com/rgb/
    private static final Color STEEL_BLUE = new Color(70, 130, 180);
    private static final Color DARK_GREEN = new Color(0, 100, 0);
    private static final Color DARK_OLIVE_GREEN = new Color(85, 107, 47);
    static final Theme[] THEMES = new Theme[]{
            new Theme("Default", Color.ORANGE, Color.GREEN, Color.BLUE),
            new Theme("Swamp", DARK_OLIVE_GREEN, DARK_GREEN, STEEL_BLUE),
            new Theme("Lava", Color.GRAY, Color.DARK_GRAY, Color.RED)
    };

}
