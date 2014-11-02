package org.flood;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.flood.GameConstants.MENU_BAR_HEIGHT;
import static org.flood.GameConstants.STATUS_BAR_HEIGHT;

/**
 * The main class of the game. A Game object represents a whole game.
 * <p/>
 * Created by Bernardo on 23/10/2014.
 */
public class Game {

    private JFrame frame = new JFrame("Flood!");
    private GameSize gameSize;

    private TilesPanel panel;

    protected Game(GameSize gameSize) {
        this.gameSize = gameSize;
        this.panel = new TilesPanel(gameSize);
        initComponents();
    }

    private void initComponents() {
        JMenuBar menuBar = new JMenuBar();
        JMenu optionsMenu = new JMenu("Options");
        JMenu sizeMenu = new JMenu("Size");
        JMenu themeMenu = new JMenu("Theme");

        // Make a menu item for each GameSize value.
        for (final GameSize possibleSize : GameSize.values()) {
            JMenuItem menuItem = new JMenuItem(Utils.toTitle(possibleSize.toString()));
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setGameSize(possibleSize);
                }
            });
            sizeMenu.add(menuItem);
        }

        // Make a menu items for each Theme.
        for (final Theme theme : GameData.THEMES) {
            JMenuItem menuItem = new JMenuItem(theme.name);
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setTheme(theme);
                }
            });
            themeMenu.add(menuItem);
        }

        optionsMenu.add(sizeMenu);
        optionsMenu.add(themeMenu);

        menuBar.add(optionsMenu);

        frame.setJMenuBar(menuBar);
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        updateFrameSize();
        centerFrame();
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public void setTheme(Theme theme) {
        this.panel.setTheme(theme);
    }

    public void setGameSize(GameSize gameSize) {
        this.gameSize = gameSize;
        panel.resize(gameSize);
        panel.reinitialize();
        updateFrameSize();
        centerFrame();
    }

    /**
     * Centers the game window on the screen.
     */
    private void centerFrame() {
        frame.setLocationRelativeTo(null);
    }

    private void updateFrameSize() {
        frame.setSize(gameSize.tileWidth * gameSize.tilesPerRow, gameSize.tileWidth * gameSize.tilesPerRow + MENU_BAR_HEIGHT + STATUS_BAR_HEIGHT);
    }

}
