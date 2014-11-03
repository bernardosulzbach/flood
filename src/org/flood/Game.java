package org.flood;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.flood.GameData.MENU_BAR_HEIGHT;
import static org.flood.GameData.STATUS_BAR_HEIGHT;

/**
 * The main class of the game. A Game object represents a whole game.
 * <p/>
 * Created by Bernardo on 23/10/2014.
 */
class Game {

    private final JFrame frame = new JFrame("Flood!");

    private final GamePanel panel;

    Game(GameSize gameSize) {
        this.panel = new GamePanel(gameSize);
        initComponents(gameSize);
    }

    private void initComponents(GameSize gameSize) {
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

        // An option to reset the game.
        JMenuItem resetOption = new JMenuItem("Reset");
        resetOption.setToolTipText("Recreates the tiles and resets the counter.");
        resetOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.reinitialize();
            }
        });

        // Another way to close the game.
        JMenuItem exitOption = new JMenuItem("Exit");
        exitOption.setToolTipText("Because simply closing the window is far too mainstream.");
        exitOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        optionsMenu.add(sizeMenu);
        optionsMenu.add(themeMenu);
        optionsMenu.add(resetOption);
        optionsMenu.add(exitOption);

        menuBar.add(optionsMenu);

        frame.setJMenuBar(menuBar);
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        // The frame must be set visible before the game size is set due to a Windows windowing issue.
        frame.setVisible(true);
        setGameSize(gameSize);
    }

    public void setTheme(Theme theme) {
        this.panel.setTheme(theme);
    }

    void setGameSize(GameSize gameSize) {
        panel.resize(gameSize);
        panel.reinitialize();
        updateFrameSize(gameSize);
        centerFrame();
    }

    private void updateFrameSize(GameSize gameSize) {
        int panelSide = gameSize.tileSide * gameSize.tilesPerRow;
        Insets insets = frame.getInsets();
        int width = panelSide + insets.right + insets.left;
        int height = panelSide + MENU_BAR_HEIGHT + STATUS_BAR_HEIGHT + insets.bottom + insets.top;
        frame.setSize(width, height);
        frame.repaint();
    }

    /**
     * Centers the game window on the screen.
     */
    private void centerFrame() {
        frame.setLocationRelativeTo(null);
    }

}
