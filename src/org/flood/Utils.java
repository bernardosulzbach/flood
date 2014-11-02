package org.flood;

/**
 * Utility class for the Flood game.
 *
 * Created by Bernardo Sulzbach on 02/11/14.
 */

public class Utils {

    /**
     * Given a string, this method returns a string with the same contents, but with the first character converted to
     * uppercase and all the others converted to lowercase.
     *
     * @param s the string to be converted.
     * @return the converted string.
     */
    public static String toTitle(String s) {
        return s.substring(0, 1).toUpperCase().concat(s.substring(1).toLowerCase());
    }

}
