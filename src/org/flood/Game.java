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
    private final Configuration configuration = new Configuration(this);
    private final GamePanel panel = new GamePanel(configuration);

    Game() {
        initComponents();
    }

    /**
     * Initializes all components.
     * <p/>
     * Should only be called after the {@code panel} class member is already set.
     */
    private void initComponents() {
        JMenuBar menuBar = new JMenuBar();
        JMenu optionsMenu = new JMenu("Options");
        JMenu sizeMenu = new JMenu("Size");
        JMenu themeMenu = new JMenu("Theme");
        JMenu highlightMenu = new JMenu("Highlight");

        // Make a menu item for each GameSize value.
        for (final GameSize possibleSize : GameSize.values()) {
            JMenuItem menuItem = new JMenuItem(Utils.toTitle(possibleSize.toString()));
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    configuration.setGameSize(possibleSize);
                    resetGameSize();
                }
            });
            sizeMenu.add(menuItem);
        }

        ButtonGroup themeButtonGroup = new ButtonGroup();
        for (final Theme theme : GameData.THEMES) {
            JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(theme.name);
            themeButtonGroup.add(menuItem);
            if (theme == panel.getTheme()) {
                themeButtonGroup.setSelected(menuItem.getModel(), true);
            }
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    configuration.setTheme(theme);
                }
            });
            themeMenu.add(menuItem);
        }

        ButtonGroup highlightButtonGroup = new ButtonGroup();
        for (final HighlightMode highlightMode : HighlightMode.values()) {
            JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(Utils.toTitle(highlightMode.toString()));
            highlightButtonGroup.add(menuItem);
            if (highlightMode == panel.getHighlightMode()) {
                highlightButtonGroup.setSelected(menuItem.getModel(), true);
            }
            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    panel.setHighlightMode(highlightMode);
                }
            });
            highlightMenu.add(menuItem);
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

        JMenuItem configurationsOption = new JMenuItem("Configurations");
        configurationsOption.setToolTipText("Opens the configuration panel.");
        configurationsOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                configuration.showConfigurationFrame(frame);
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
        optionsMenu.add(highlightMenu);
        optionsMenu.add(resetOption);
        optionsMenu.add(configurationsOption);
        optionsMenu.add(exitOption);

        menuBar.add(optionsMenu);

        frame.setJMenuBar(menuBar);
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        // The frame must be set visible before the game size is set due to a Windows windowing issue.
        frame.setVisible(true);
        resetGameSize();
    }

    public void notifyThemeChange() {
        panel.repaint();
    }

    void resetGameSize() {
        panel.resize(configuration);
        panel.reinitialize();
        updateFrameSize(configuration.getGameSize());
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
