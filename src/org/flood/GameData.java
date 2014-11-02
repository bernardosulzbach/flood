package org.flood;

import java.awt.*;

/**
 * GameData class used to store constant data.
 * <p/>
 * Created by Bernardo Sulzbach on 02/11/14.
 */

public class GameData {

    protected static final Theme[] THEMES = new Theme[]{
            new Theme("Default", Color.ORANGE, Color.GREEN, Color.BLUE),
            new Theme("Lava", Color.GRAY, Color.DARK_GRAY, Color.RED)
    };

}
