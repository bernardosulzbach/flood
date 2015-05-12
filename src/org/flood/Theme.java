package org.flood;

import java.awt.*;
import java.util.HashMap;

/**
 * Theme class used to store themes.
 * <p/>
 * Created by Bernardo Sulzbach on 02/11/14.
 */

class Theme {

    final String name;

    final HashMap<TileType, Color> colors = new HashMap<TileType, Color>(3);

    public Theme(String name, Color sand, Color grass, Color water) {
        this.name = name;
        this.colors.put(TileType.BEACH, sand);
        this.colors.put(TileType.HILL, grass);
        this.colors.put(TileType.WATER, water);
    }

}
