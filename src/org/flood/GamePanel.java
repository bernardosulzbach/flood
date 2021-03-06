package org.flood;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * The panel added into the Game.
 * <p/>
 * Created by Bernardo on 23/10/2014.
 */
class GamePanel extends JPanel {

    public static final int PEOPLE_SQUARE_SIDE = 2;
    static final String END_GAME_TITLE = "Flood complete!";
    static final String END_GAME_MESSAGE = "Everything was flooded.\nPlay again?";
    private static final int MAGIC_FONT_BORDER = 4;
    private int tileSide;
    private int tilesPerRow;
    private int totalTiles;
    private HighlightMode highlightMode;
    private TileMatrix tileMatrix;
    private Configuration configuration;
    private int mouseClicks = 0;

    /**
     * The default constructor.
     */
    public GamePanel(Configuration configuration) {
        super();
        this.configuration = configuration;
        tileMatrix = new TileMatrix(configuration);
        setBackground(Color.BLACK);
        highlightMode = HighlightMode.NONE;
        resize(configuration);
        // Set the font used to write the status.
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mouseClicked(e);
                int[] c = getMouseCoordinates(e.getPoint());
                if (c[0] >= 0 && c[0] < tilesPerRow && c[1] >= 0 && c[1] < tilesPerRow) {
                    mouseClicks++;
                    tileMatrix.startFlood(c[0], c[1]);
                    repaint();
                    if (tileMatrix.getWaterCount() == totalTiles) {
                        endGameOptionPane();
                    }
                }
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                if (highlightMode != HighlightMode.NONE) {
                    // TODO: check if the selected tile changed before invoking repaint(), to improve performance.
                    repaint();
                }
            }
        });
    }

    public Theme getTheme() {
        return configuration.getTheme();
    }

    /**
     * Updates all customizable variables.
     */
    void resize(Configuration configuration) {
        tileMatrix = new TileMatrix(configuration);
        tilesPerRow = configuration.getGameSize().tilesPerRow;
        totalTiles = tilesPerRow * tilesPerRow;
        tileSide = configuration.getGameSize().tileSide;
    }

    /**
     * Prompts the user (using a JOptionPane dialog) if he/she wants to play again or quit.
     */
    private void endGameOptionPane() {
        int choice = JOptionPane.showConfirmDialog(this, END_GAME_MESSAGE, END_GAME_TITLE, JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            reinitialize();
        } else {
            System.exit(0);
        }
    }

    /**
     * @param mousePosition the point the mouse is pointing to.
     * @return an array of two integers representing the mouse coordinates on the grid.
     */
    private int[] getMouseCoordinates(Point mousePosition) {
        int[] coordinates = new int[2];
        if (mousePosition != null) {
            coordinates[0] = (int) (mousePosition.getX() / tileSide);
            coordinates[1] = (int) (mousePosition.getY() / tileSide);
        } else {
            coordinates[0] = coordinates[1] = -1;
        }
        return coordinates;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int[] coordinates = getMouseCoordinates(getMousePosition());
        ArrayList<Tile> selection = new ArrayList<Tile>();
        if (isValidCoordinatePair(coordinates)) {
            if (highlightMode == HighlightMode.SELECTED_TILE) {
                selection.add(tileMatrix.getTile(coordinates[0], coordinates[1]));
            } else if (highlightMode == HighlightMode.FULL) {
                selection = tileMatrix.getSelection(coordinates[0], coordinates[1]);
            }
        }
        for (int j = 0; j < tilesPerRow; j++) {
            for (int i = 0; i < tilesPerRow; i++) {
                g.setColor(configuration.getTheme().colors.get(tileMatrix.getTileType(i, j)));
                // The selected tiles are 'down'. All the others are 'up'.
                Tile currentTile = tileMatrix.getTile(i, j);
                if (selection.contains(currentTile)) {
                    g.fill3DRect(i * tileSide, j * tileSide, tileSide, tileSide, false);
                } else {
                    g.fill3DRect(i * tileSide, j * tileSide, tileSide, tileSide, true);
                }
                if (!currentTile.isWater()) {
                    g.setColor(Color.RED);
                    for (Double[] human : currentTile.getPopulation().getHumans()) {
                        int factor = tileSide - PEOPLE_SQUARE_SIDE - 1;
                        if (factor < 1) {
                            throw new AssertionError("tileSide is too small.");
                        }
                        int x = 1 + (int) (factor * human[0]);
                        int y = 1 + (int) (factor * human[1]);
                        g.fillRect(i * tileSide + x, j * tileSide + y, PEOPLE_SQUARE_SIDE, PEOPLE_SQUARE_SIDE);
                    }
                }
            }
        }
        updateStatusBar(g);
    }

    private boolean isValidCoordinatePair(int[] pair) {
        return pair[0] != -1 && pair[0] < tilesPerRow && pair[1] != -1 && pair[1] < tilesPerRow;
    }

    /**
     * Reinitializes the game panel, erasing all the current progress made by the player. Used to update the panel after
     * a new panel size is set.
     */
    public void reinitialize() {
        tileMatrix.reinitialize();
        resetMouseClicks();
        repaint();
    }

    private void updateStatusBar(Graphics g) {
        updateWaterStatistics(g);
        updateHumanStatistics(g);
    }

    private void updateWaterStatistics(Graphics g) {
        g.setColor(Color.GREEN);
        int water = tileMatrix.getWaterCount();
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

    private void updateHumanStatistics(Graphics g) {
        g.setColor(Color.ORANGE);
        int human = tileMatrix.getTotalPopulation();
        String humansRemaining = String.format("%d humans remaining.", human);
        int stringWidth = (int) g.getFontMetrics().getStringBounds(humansRemaining, g).getBounds2D().getWidth();
        g.drawString(humansRemaining, getWidth() - stringWidth - MAGIC_FONT_BORDER, getHeight() - MAGIC_FONT_BORDER);
    }

    void resetMouseClicks() {
        mouseClicks = 0;
    }

    public HighlightMode getHighlightMode() {
        return highlightMode;
    }

    public void setHighlightMode(HighlightMode highlightMode) {
        this.highlightMode = highlightMode;
    }

}
