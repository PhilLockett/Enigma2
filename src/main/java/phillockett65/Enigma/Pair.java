/*  Enigma - a JavaFX based playing card image generator.
 *
 *  Copyright 2022 Philip Lockett.
 *
 *  This file is part of Enigma.
 *
 *  Enigma is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Enigma is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Enigma.  If not, see <https://www.gnu.org/licenses/>.
 */

/*
 * Pair is a class that captures a connection between two letters.
 */
package phillockett65.Enigma;

public class Pair {
    private String letters = "";

    public void set(String text) { letters = text; }
    public void clear() { letters = ""; }

    public String get() { return letters; }
    public int count() { return letters.length(); }
    public char charAt(int index) { return letters.charAt(index); }
    public int indexAt(int index) { return Rotor.charToIndex(charAt(index)); }
    
    public boolean isEmpty() { return count() == 0; }
    public boolean isCharAt(int index) { return Character.isAlphabetic(charAt(index)); }

    /**
     * Check the validity of the Pair.
     * @return true if the pair is valid, false otherwise.
     */
    public boolean isValid() {
        if (count() != 2)
            return false;

        if (!isCharAt(0))
            return false;

        if (!isCharAt(1))
            return false;

        if (charAt(0) == charAt(1))
            return false;

        return true;
    }

    /**
     * Perform clean up on the pair.
     */
    public void sanitize() {
        letters = letters.toUpperCase();
    }
}
