package org.flood;

/**
 * Tile class that stores a TileType and provides convenience predicate methods about its type.
 * <p/>
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

    public boolean isBeach() {
        return type.equals(TileType.BEACH);
    }

    public boolean isWater() {
        return type.equals(TileType.WATER);
    }

    public int getPeopleCount() {
        return (int) Math.sqrt(1 + GameData.random.nextInt(9)); // Random integer from the range [1, 3].
    }

}
