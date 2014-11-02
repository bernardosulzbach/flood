package org.flood;

/**
* Created by Bernardo Sulzbach on 02/11/14.
*/
class Tile {

    private TileType type;

    public Tile(TileType type) {
        this.type = type;
    }

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public boolean isFloodable() {
        return type.equals(TileType.SAND);
    }

    public boolean isWater() {
        return type.equals(TileType.WATER);
    }
}
