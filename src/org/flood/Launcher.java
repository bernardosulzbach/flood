package org.flood;

/**
 * Launcher class that starts the game.
 * <p/>
 * Created by bernardo on 01/11/14.
 */
public class Launcher {

    public static void main(String[] args) {
        Game game = new Game(GameSize.MEDIUM);
        game.setTheme(GameData.THEMES[0]);
    }

}
