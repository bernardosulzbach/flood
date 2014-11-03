package org.flood;

public enum GameSize {
    SMALL(10, 40), MEDIUM(20, 35), BIG(30, 30), HUGE(50, 18), OVERKILL(100, 9);

    final int tilesPerRow;
    final int tileSide;

    GameSize(int tilesPerRow, int tileSide) {
        this.tilesPerRow = tilesPerRow;
        this.tileSide = tileSide;
    }

}
