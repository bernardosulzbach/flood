package org.flood;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

/**
 * The panel added into the Game.
 * <p/>
 * Created by Bernardo on 23/10/2014.
 */
class TilesPanel extends JPanel {

    private static final Random random = new Random();

    private static final int MAGIC_FONT_BORDER = 4;

    private int tileWidth;
    private int tilesPerRow;
    private int totalTiles;

    private Theme theme;

    private Tile[][] tileMatrix;

    private final ArrayList<Tile> alreadyHitInThisChainReaction = new ArrayList<Tile>();

    private int mouseClicks = 0;

    /**
     * The default constructor.
     */
    public TilesPanel(GameSize gameSize) {
        super();
        setTheme(GameData.THEMES[0]);
        resize(gameSize);
        initializeTileMatrix(GameConstants.DEFAULT_INITIALIZATION_MODE);
        // Set the font used to write the status.
        setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mouseClicked(e);
                int i = e.getX() / tileWidth;
                int j = e.getY() / tileWidth;
                if (i >= 0 && i < tilesPerRow && j >= 0 && j < tilesPerRow) {
                    mouseClicks++;
                    startFlood(i, j);
                    repaint();
                    if (getWaterCount() == totalTiles) {
                        endGameOptionPane();
                    }
                }
            }
        });
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
        repaint();
    }

    /**
     * Updates all customizable variables.
     */
    void resize(GameSize gameSize) {
        tilesPerRow = gameSize.tilesPerRow;
        totalTiles = tilesPerRow * tilesPerRow;
        tileWidth = gameSize.tileWidth;
    }

    /**
     * Prompts the user (using a JOptionPane dialog) if he/she wants to play again or quit.
     */
    private void endGameOptionPane() {
        int choice = JOptionPane.showConfirmDialog(this, GameConstants.END_GAME_MESSAGE, GameConstants.END_GAME_TITLE, JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            initializeTileMatrix(GameConstants.DEFAULT_INITIALIZATION_MODE);
            repaint();
        } else {
            System.exit(0);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Tile currentTile;
        for (int y = 0; y < tilesPerRow; y++) {
            for (int x = 0; x < tilesPerRow; x++) {
                currentTile = tileMatrix[y][x];
                g.setColor(theme.colors.get(currentTile.getType()));
                g.fill3DRect(x * tileWidth, y * tileWidth, tileWidth, tileWidth, true);
            }
        }
        updateStatus(g);
    }

    /**
     * Starts a flood in the tile found in (i, j) after checking that it is floodable.
     *
     * @param i the i coordinate.
     * @param j the j coordinate.
     */
    private void startFlood(int i, int j) {
        flood(i, j);
        alreadyHitInThisChainReaction.clear();
        updateTiles();
    }

    /**
     * Floods the tile found in (i, j) and propagates the flood all floodable adjacent tiles.
     *
     * @param i the i coordinate.
     * @param j the j coordinate.
     */
    private void flood(int i, int j) {
        if (!alreadyHitInThisChainReaction.contains(tileMatrix[j][i]) && tileMatrix[j][i].isFloodable()) {
            alreadyHitInThisChainReaction.add(tileMatrix[j][i]);
            tileMatrix[j][i].setType(TileType.WATER);
            if (j != 0) {
                if (tileMatrix[j - 1][i].isFloodable()) {
                    flood(i, j - 1);
                }
            }
            if (j != tilesPerRow - 1) {
                if (tileMatrix[j + 1][i].isFloodable()) {
                    flood(i, j + 1);
                }
            }
            if (i != 0) {
                if (tileMatrix[j][i - 1].isFloodable()) {
                    flood(i - 1, j);
                }
            }
            if (i != tilesPerRow - 1) {
                if (tileMatrix[j][i + 1].isFloodable()) {
                    flood(i + 1, j);
                }
            }
        }
    }

    /**
     * Reinitializes the game panel, erasing all the current progress made by the player. Used to update the panel
     * after a new panel size is set.
     */
    public void reinitialize() {
        initializeTileMatrix(GameConstants.DEFAULT_INITIALIZATION_MODE);
        repaint();
    }

    /**
     * Initializes the Tile array with a distribution of tiles based on WATER_RATE and on the initialization mode.
     * This method also reset the mouse click counter and updates the tiles considered beaches.
     *
     * @param tileArrayInitializationMode the initialization mode used.
     */
    private void initializeTileMatrix(TileArrayInitializationMode tileArrayInitializationMode) {
        tileMatrix = new Tile[tilesPerRow][tilesPerRow];
        switch (tileArrayInitializationMode) {
            // Randomly chooses between water or grass to fill a tile.
            case SIMPLE: {
                for (int y = 0; y < tilesPerRow; y++) {
                    for (int x = 0; x < tilesPerRow; x++) {
                        if (random.nextDouble() < GameConstants.WATER_RATE) {
                            tileMatrix[y][x] = new Tile(TileType.WATER);
                        } else {
                            tileMatrix[y][x] = new Tile(TileType.WATER);
                        }
                    }
                }
                break;
            }
            // Randomly chooses between water or grass to fill a square of tiles of side two.
            case SQUARES: {
                for (int y = 0; y < tilesPerRow; y++) {
                    for (int x = 0; x < tilesPerRow; x++) {
                        if (y % 2 == 0) {
                            if (x % 2 == 0) {
                                if (random.nextDouble() < GameConstants.WATER_RATE) {
                                    tileMatrix[y][x] = new Tile(TileType.WATER);
                                } else {
                                    tileMatrix[y][x] = new Tile(TileType.GRASS);
                                }
                            } else {
                                tileMatrix[y][x] = new Tile(tileMatrix[y][x - 1].getType());
                            }
                        } else {
                            tileMatrix[y][x] = new Tile(tileMatrix[y - 1][x].getType());
                        }
                    }
                }
                break;
            }
            // Uses the same algorithm as SQUARES, but after filling a square of tiles of side two with water, there is
            // a 40 % chance of spreading water over one extra tile  that may overlap existing water and
            // a 20 % chance of spreading water over two extra tiles that may overlap existing water.
            // "Much complex, very genius." - B. Sulzbach.
            case COMPLEX: {
                // Declare everything here to get fantastical mega-godly ultra-fast ridiculously-fabulous performance.
                int x, y;
                int spreading = 0;
                int notDiagonalNeighbors;
                int remainingNeighbors;
                double randomDouble;
                for (int j = 0; j < tilesPerRow; j += 2) {
                    for (int i = 0; i < tilesPerRow; i += 2) {
                        //System.out.println(j + " " + i);

                        // Check if this is a water square or a grass square.
                        if (random.nextDouble() < GameConstants.WATER_RATE) {

                            // Get how many tiles will get the water spread effect.
                            randomDouble = random.nextDouble();
                            if (randomDouble < 0.2) {
                                spreading = 2;
                            } else if (randomDouble < 0.4) {
                                spreading = 1;
                            }

                            // Get how many neighbor tiles exist. Diagonals not accounted for.
                            if (j == 0 || j == tilesPerRow - 1) {
                                if (i == 0 || i == tilesPerRow - 1) {
                                    notDiagonalNeighbors = remainingNeighbors = 4;
                                } else {
                                    notDiagonalNeighbors = remainingNeighbors = 6;
                                }
                            } else {
                                if (i == 0 || i == tilesPerRow - 1) {
                                    notDiagonalNeighbors = remainingNeighbors = 6;
                                } else {
                                    notDiagonalNeighbors = remainingNeighbors = 8;
                                }
                            }

                            for (int b = -1; b < 3; b++) {
                                y = j + b;
                                if (y >= 0 && y < tilesPerRow) {
                                    for (int a = -1; a < 3; a++) {
                                        x = i + a;
                                        if (x >= 0 && x < tilesPerRow) {
                                            // Check if the algorithm is filling the square or hitting the margins.
                                            if ((a == 0 || a == 1) && (b == 0 || b == 1)) {
                                                tileMatrix[y][x] = new Tile(TileType.WATER);
                                            } else {
                                                // Is there water to spread?
                                                if (spreading != 0) {
                                                    // Not a diagonal, right?
                                                    if (!((a == -1 || a == 2) && (b == -1 || b == 2))) {
                                                        // Check if this is the marginal tile to start filling.
                                                        if (remainingNeighbors == 1 || random.nextInt(notDiagonalNeighbors) == 0) {
                                                            tileMatrix[y][x] = new Tile(TileType.WATER);
                                                            if (spreading == 2) {
                                                                // Not so much. Even more genius. Whoa!
                                                                // Spread, preferentially, to the left.
                                                                if (a == -1) {
                                                                    if (x > 0) {
                                                                        tileMatrix[y][x - 1] = new Tile(TileType.WATER);
                                                                    } else if (y > 0) {
                                                                        tileMatrix[y - 1][x] = new Tile(TileType.WATER);
                                                                    } else {
                                                                        tileMatrix[y + 1][x] = new Tile(TileType.WATER);
                                                                    }
                                                                }
                                                                // Spread, preferentially, to the right.
                                                                else if (a == 2) {
                                                                    if (x < tilesPerRow - 1) {
                                                                        tileMatrix[y][x + 1] = new Tile(TileType.WATER);
                                                                    } else if (y > 0) {
                                                                        tileMatrix[y - 1][x] = new Tile(TileType.WATER);
                                                                    } else {
                                                                        tileMatrix[y + 1][x] = new Tile(TileType.WATER);
                                                                    }
                                                                }
                                                                // Spread, preferentially, towards north.
                                                                else if (b == -1) {
                                                                    if (y > 0) {
                                                                        tileMatrix[y - 1][x] = new Tile(TileType.WATER);
                                                                    } else if (x > 0) {
                                                                        tileMatrix[y][x - 1] = new Tile(TileType.WATER);
                                                                    } else {
                                                                        tileMatrix[y][x + 1] = new Tile(TileType.WATER);
                                                                    }
                                                                }
                                                                // Spread, preferentially, towards south.
                                                                else {
                                                                    if (y < tilesPerRow - 1) {
                                                                        tileMatrix[y + 1][x] = new Tile(TileType.WATER);
                                                                    } else if (x > 0) {
                                                                        tileMatrix[y][x - 1] = new Tile(TileType.WATER);
                                                                    } else {
                                                                        tileMatrix[y][x + 1] = new Tile(TileType.WATER);
                                                                    }
                                                                }
                                                            }
                                                            // The water was spread. It shall now be zeroed.
                                                            // "Much complex, very genius." - Echoes from my past.
                                                            spreading = 0;
                                                        } else {
                                                            // After hitting a viable neighbor but not spreading water
                                                            // over it, decrement our remaining neighbors counter.
                                                            remainingNeighbors--;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            for (int b = 0; b < 2; b++) {
                                y = j + b;
                                if (y < tilesPerRow) {
                                    for (int a = 0; a < 2; a++) {
                                        x = i + a;
                                        if (x < tilesPerRow) {
                                            tileMatrix[y][x] = new Tile(TileType.GRASS);
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
                break;
            }
        }
        resetMouseClicks();
        updateTiles();
    }

    private void updateTiles() {
        boolean skipRemainingAdjacentTiles;
        int x, y;
        for (int j = 0; j < tilesPerRow; j++) {
            for (int i = 0; i < tilesPerRow; i++) {
                if (!tileMatrix[j][i].isWater()) {
                    skipRemainingAdjacentTiles = false;
                    for (int b = -1; b <= 1 && !skipRemainingAdjacentTiles; b++) {
                        y = j + b;
                        if (y >= 0 && y < tilesPerRow) {
                            for (int a = -1; a <= 1 && !skipRemainingAdjacentTiles; a++) {
                                x = i + a;
                                if (x >= 0 && x < tilesPerRow) {
                                    if (tileMatrix[y][x].isWater()) {
                                        tileMatrix[j][i].setType(TileType.SAND);
                                        skipRemainingAdjacentTiles = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateStatus(Graphics g) {
        g.setColor(Color.BLACK);
        // The black background.
        g.fillRect(0, tilesPerRow * tileWidth, getWidth(), getHeight() - tilesPerRow * tileWidth);
        g.setColor(Color.GREEN);
        int water = getWaterCount();
        int total = totalTiles;
        StringBuilder stringBuilder = new StringBuilder();
        // Water over total tiles fraction.
        stringBuilder.append(Integer.toString(water)).append('/').append(Integer.toString(total)).append(' ');
        // Water percentage.
        stringBuilder.append('(').append(Utils.getPercentageString(water, totalTiles)).append(')');
        // Mouse click count.
        if (mouseClicks > 0) {
            stringBuilder.append(" after ");
            if (mouseClicks == 1) {
                stringBuilder.append("1 click");
            } else if (mouseClicks > 1) {
                stringBuilder.append(Integer.toString(mouseClicks)).append(" clicks");
            }
        }
        g.drawString(stringBuilder.toString(), MAGIC_FONT_BORDER, getHeight() - MAGIC_FONT_BORDER);
    }

    int getWaterCount() {
        int count = 0;
        for (Tile[] row : tileMatrix) {
            for (Tile tile : row) {
                if (tile.isWater()) {
                    count++;
                }
            }
        }
        return count;
    }

    void resetMouseClicks() {
        mouseClicks = 0;
    }

}