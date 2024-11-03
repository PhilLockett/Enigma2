/*  Enigma2 - a JavaFX based enigma machine simulator.
 *
 *  Copyright 2024 Philip Lockett.
 *
 *  This file is part of Enigma2.
 *
 *  Enigma2 is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Enigma2 is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Enigma2.  If not, see <https://www.gnu.org/licenses/>.
 */

/*
 * Mapper is a class that captures a directional mapping. For example, for 
 * Rotor I from Enigma I, the letter A maps to E, but E maps to L. The A to E
 * mapping is classed a RIGHT_TO_LEFT mapping. The E to L mapping is classed a 
 * LEFT_TO_RIGHT mapping. If these are the same then it is a Reflector mapping 
 * and isReflector() returns true.
 */
package phillockett65.Enigma;

import java.util.ArrayList;
import java.util.Arrays;

public class Mapper {

    public final static int RIGHT_TO_LEFT = 1;
    public final static int LEFT_TO_RIGHT = 2;

    private final String id;
    private final int[] map;
    private final boolean reflect;

    private final int[] leftMap;
    private final int[] rightMap;


    /************************************************************************
     * General support code.
     */

    public static int charToUpper(int v) { return Character.toUpperCase(v); }
    public static int charToIndex(int v) { return charToUpper(v) - 'A'; }
    public static int letterToIndex(String v) { return charToIndex(v.charAt(0)); }
    public static int indexToChar(int v) { return v + 'A'; }
    public static String indexToLetter(int v) { return "" + (char)indexToChar(v); }

    public static int charToInt(int v) { return charToIndex(v) + 1; }
    public static int intTochar(int v) { return indexToChar(v-1); }
    public static String intToString(int v) { return "" + (char)intTochar(v); }

    public static String indexToNumber(int v) { return Integer.toString(v+1); }
    public static int numberToIndex(String v) { return Integer.parseInt(v)-1; }

    public static int stringToIndex(String s) { return Character.isDigit(s.charAt(0)) ? numberToIndex(s) : letterToIndex(s); }


    /************************************************************************
     * Initialization support code.
     */

    /**
     * Calculate if this is a reflector mapping.
     * @return true if the mapping is bi-directional.
     */
    private boolean isReflect() {

        for (int i = 0; i < map.length; ++i) {
            final int c = map[i];
            if (c == i)
                return false;
            if (map[c] != i)
                return false;
        }

        return true;
    }

    private int[] initRightMap() {
        int[] output = new int[map.length];

        for (int i = 0; i < map.length; ++i) {
            output[i] = map[i];
        }

        return output;
    }

    private int[] initLeftMap() {
        int[] output = new int[map.length];

        for (int i = 0; i < map.length; ++i) {
            output[map[i]] = i;
        }

        return output;
    }

    /**
     * Constructor.
     * @param id of this mapping.
     * @param map of the required translation.
     */
    public Mapper(String id, int[] map) {
        this.id = id;
        this.map = map;
        reflect = isReflect();

        rightMap = initRightMap();
        leftMap = initLeftMap();
    }

    /**
     * Constructor.
     * @param id of this mapping.
     * @param cipher String representation of the mapping.
     */
    public Mapper(String id, String cipher) {
        this.id = id;
        this.map = deriveMap(cipher);
        reflect = isReflect();

        rightMap = initRightMap();
        leftMap = initLeftMap();
    }


    /************************************************************************
     * Getters support code.
     */

    public boolean is(String target) { return target.equals(id); }

    public String getId() { return id; }
    public int[] getMap() { return map; }
    public int getMapItem(int index) { return map[index]; }
    public int getMapLength() { return map.length; }
    public boolean isReflector() { return reflect; }

    public int[] getLeftMap()	{ return leftMap; }
    public int[] getRightMap()	{ return rightMap; }

    private int leftToRight(int index) { return leftMap[index]; }
    private int rightToLeft(int index) { return rightMap[index]; }


    /************************************************************************
     * Setters support code.
     */

    public void setOffset(int value) {}

    /**
     * Translates (swaps) an index (numerical equivalent of the letter) to 
     * another using the map.
     * @param direction of mapping.
     * @param index to translate.
     * @return the translated index.
     */
    private int swap(int direction, int index) {
        if (direction == RIGHT_TO_LEFT) 
            return rightToLeft(index);

        return leftToRight(index);
    }

    /**
     * Translates (swaps) an index (numerical equivalent of the letter) to 
     * another using the map.
     * @param direction of mapping.
     * @param index to translate.
     * @param show the translation step on the command line.
     * @return the translated index.
     */
    public int swap(int direction, int index, boolean show) {
        final int output = swap(direction, index);

        if (show)
            System.out.print(id + "(" + indexToLetter(index) + "->" + indexToLetter(output) + ")  ");

        return output;
    }


    /************************************************************************
     * Mapping support code.
     */

    /**
     * @brief Create and initialise a map with zeros, 
     * 
     * @param length of map.
     * @return initialised map.
     */
    public static int[] initZeroMap(int length)
    {
        int[] output = new int[length];

        for (int i = 0; i < length; ++i) {
            output[i] = i;
        }

        return output;
    }

    /**
     * @brief Create and initialise a map as a through map, 
     * i.e. A maps to A, B maps to B etc.
     * 
     * @param length of map.
     * @return initialised map.
     */
    public static int[] initThroughMap(int length)
    {
        int[] output = new int[length];

        for (int i = 0; i < length; ++i) {
            output[i] = i;
        }

        return output;
    }

    /**
     * @brief Create a map from a string representation
     * (e.g. cipher: "EKMFLGDQVZNTOWYHXUSPAIBRCJ").
     * 
     * @param cipher String representation of the mapping.
     * @return map derived from pairString.
     */
    public static int[] deriveMap(String cipher)
    {
        int[] output = initThroughMap(26);

        for (int i = 0; i < cipher.length(); ++i) {
            output[i] = charToIndex(cipher.charAt(i));
        }

        return output;
    }

    /**
     * @brief Convert a String of space seperated words into a list of Strings, 
     * each containing a word.
     * 
     * @param wordString required to be split.
     * @return list of words.
     */
    public static ArrayList<String> splitWords(String wordString)
    {
        ArrayList<String> output = new ArrayList<String>();

        String regex = "[\\s]";
        String[] words = wordString.split(regex);
        for (String word : words) {
            output.add(word);
        }

        return output;
    }

    /**
     * @brief Create a plugboard map from pairs captured as a string
     * (e.g. pairString: "SZ GT DV KU FO MY EW JN IX LQ").
     * 
     * @param pairString of the required translation.
     * @return plugboard map derived from pairString.
     */
    public static int[] derivePlugboardMap(String pairString)
    {
        int[] output = initThroughMap(26);
        ArrayList<String> pairs = splitWords(pairString);

        for (String pair: pairs) {
            final int p0 = charToIndex(pair.charAt(0));
            final int p1 = charToIndex(pair.charAt(1));
            output[p0] = p1;
            output[p1] = p0;
        }

        return output;
    }

    /**
     * @brief Create a reflector map from pairs captured as a string
     * (e.g. pairString: "IL AP EU HO QT WZ KV GM BF NR DX CS").
     * 
     * @param pairString of the required translation.
     * @return reflector map derived from pairString.
     */
    public static int[] deriveReflectorMap(String pairString)
    {
        int[] output = initThroughMap(26);
        int[] letterCounts = initZeroMap(26);
        ArrayList<String> pairs = splitWords(pairString);

        for (String pair: pairs) {
            final int p0 = charToIndex(pair.charAt(0));
            final int p1 = charToIndex(pair.charAt(1));
            output[p0] = p1;
            output[p1] = p0;
            letterCounts[p0]++;
            letterCounts[p1]++;
        }

        // Find and set up the unconfigured pair.
        int first = -1;
        for (int index = 0; index < letterCounts.length; ++index) {
            if (letterCounts[index] == 0) {
                if (first == -1) {
                    first = index;
                } else {
                    output[first] = index;
                    output[index] = first;
    
                    break;
                }
            }
        }

        return output;
    }


    /************************************************************************
     * Debug support code.
     */

    @Override
    public String toString() {
        return "Rotor [" + 
            "id=" + id + 
            ", map=" + Arrays.toString(map) + 
            ", reflect=" + reflect + 
            "]";
    }

    protected void dumpMapping(int[] map) {
        for (int i = 0; i < map.length; ++i)
            System.out.print(indexToLetter(map[i]));

        System.out.println();
    }

    public void dumpMap() { dumpMapping(map); }
    public void dumpLeftMap() { dumpMapping(leftMap); }
    public void dumpRightMap() { dumpMapping(rightMap); }

}
