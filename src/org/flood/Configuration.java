package org.flood;

import org.flood.algorithms.InitializationAlgorithm;
import org.flood.algorithms.InitializationAlgorithms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Configuration class that stores the game's configuration and provides methods that allow the player to modify it.
 * <p/>
 * Created by Bernardo on 15/05/2015.
 */
public class Configuration {

    private static final int GAP = 5;

    private final Game game;
    private GameSize gameSize = GameSize.MEDIUM;
    private Theme theme = GameData.THEMES[0];
    private InitializationAlgorithm initializationAlgorithm = InitializationAlgorithms.getDefaultAlgorithm();

    public Configuration(Game game) {
        this.game = game;
    }

    private static <E> JComboBox<E> makeComboBox(Collection<E> collection, E selected) {
        if (!collection.contains(selected)) {
            throw new AssertionError("selected should be in the provided Collection.");
        }
        JComboBox<E> comboBox = new JComboBox<E>();
        for (E initializationAlgorithm : collection) {
            comboBox.addItem(initializationAlgorithm);
        }
        comboBox.setSelectedItem(selected);
        return comboBox;
    }

    public GameSize getGameSize() {
        return gameSize;
    }

    public void setGameSize(GameSize gameSize) {
        if (!getGameSize().equals(gameSize)) {
            this.gameSize = gameSize;
            this.game.notifyGameSizeChange();
        }
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        if (!getTheme().equals(theme)) {
            this.theme = theme;
            this.game.notifyThemeChange();
        }
    }

    public InitializationAlgorithm getInitializationAlgorithm() {
        return initializationAlgorithm;
    }

    public void showConfigurationFrame(Frame owner) {
        final JDialog dialog = new JDialog(owner, "Configuration Panel", true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //frame.setLayout(new BorderLayout(5, 5));

        JPanel algorithmPanel = makeAlgorithmPanel();
        JPanel themePanel = makeThemePanel();
        JPanel sizePanel = makeSizePanel();

        final JButton doneButton = new JButton("Done");
        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Not sure if this is the best way to close the JFrame.
                // Seems better than .setVisible(false) and .dispose().
                dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
            }
        });

        GridLayout panelLayout = new GridLayout(0, 1, 5, 5); // One column and how many rows as necessary. 5 px gaps.
        JPanel panel = new JPanel(panelLayout);
        panel.setLayout(panelLayout);
        panel.setBorder(new EmptyBorder(GAP, GAP, GAP, GAP));
        panel.add(algorithmPanel);
        panel.add(themePanel);
        panel.add(sizePanel);
        panel.add(doneButton);

        dialog.add(panel);
        dialog.pack();
        dialog.setSize(new Dimension(480, dialog.getHeight()));
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private JPanel makeAlgorithmPanel() {
        List<InitializationAlgorithm> initializationAlgorithms = InitializationAlgorithms.getInitializationAlgorithms();
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Initialization Algorithm");
        JComboBox<InitializationAlgorithm> comboBox = makeComboBox(initializationAlgorithms, initializationAlgorithm);
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox) e.getSource();
                initializationAlgorithm = (InitializationAlgorithm) box.getSelectedItem();
            }
        });
        panel.add(label, BorderLayout.NORTH);
        panel.add(comboBox, BorderLayout.SOUTH);
        return panel;
    }

    public JPanel makeThemePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Theme");
        JComboBox<Theme> comboBox = makeComboBox(Arrays.asList(GameData.THEMES), theme);
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox) e.getSource();
                setTheme((Theme) box.getSelectedItem());
            }
        });
        panel.add(label, BorderLayout.NORTH);
        panel.add(comboBox, BorderLayout.SOUTH);
        return panel;
    }

    public JPanel makeSizePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Size");
        JComboBox<GameSize> comboBox = makeComboBox(Arrays.asList(GameSize.values()), gameSize);
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox) e.getSource();
                setGameSize((GameSize) box.getSelectedItem());
            }
        });
        panel.add(label, BorderLayout.NORTH);
        panel.add(comboBox, BorderLayout.SOUTH);
        return panel;
    }

}
