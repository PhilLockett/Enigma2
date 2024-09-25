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

    /**
     * Constructor.
     * @param id of this mapping.
     * @param map of the required translation.
     */
    public Mapper(String id, int[] map) {
        this.id = id;
        this.map = map;
        reflect = isReflect();

        rightMap = new int[26];
        leftMap = new int[26];

        for (int i = 0; i < map.length; ++i) {
            rightMap[i] = map[i];
            leftMap[map[i]] = i;
        }
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
    public void dumpLeftMap() { dumpMapping(map); }
    public void dumpRightMap() { dumpMapping(map); }

}
