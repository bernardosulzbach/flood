package org.flood;

public enum GameSize {
    SMALL(10, 40), MEDIUM(20, 35), BIG(30, 30);

    final int tilesPerRow;
    final int tileWidth;

    GameSize(int tilesPerRow, int tileWidth) {
        this.tilesPerRow = tilesPerRow;
        this.tileWidth = tileWidth;
    }

}
