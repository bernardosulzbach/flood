package org.flood;

import java.awt.*;
import java.util.HashMap;

/**
 * Theme class used to store themes.
 *
 * Created by Bernardo Sulzbach on 02/11/14.
 */

public class Theme {

    protected final String name;

    protected final HashMap<TileType, Color> colors = new HashMap<TileType, Color>(3);

    public Theme(String name, Color sand, Color grass, Color water) {
        this.name = name;
        this.colors.put(TileType.SAND, sand);
        this.colors.put(TileType.GRASS, grass);
        this.colors.put(TileType.WATER, water);
    }

}