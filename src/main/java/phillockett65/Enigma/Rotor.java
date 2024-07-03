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
 * Rotor is a class that extends Mapper and captures the details of a rotor 
 * including the ring setting which is set post instantiation and the rotation
 * (offset) which is dynamically updated in normal use. Note, the turnover point
 * immediately follows the notch point.
 */
package phillockett65.Enigma;

import java.util.Arrays;

public class Rotor extends Mapper {
    private final String cipher;
    private final String date;
    private final String name;
    private final boolean[] turnover;
    private final boolean[] notches;
    
    private int ringSetting;

    private int offset;


    /************************************************************************
     * Initialization support code.
     */

    /**
     * Convert a String representation of the map to an integer array of 
     * indices (numerical equivalent of the letter).
     * @param cipher String representation of the mapping.
     * @return array of indices.
     */
    private static int[] buildIndices(String cipher) {
        int [] alan = new int[26];

        for (int i = 0; i < cipher.length(); ++i) {
            final int c = charToIndex(cipher.charAt(i));
            alan[i] = c;
        }

        return alan;
    }

    /**
     * Convert a String representing 1 or more turnover points into an array 
     * of flags.
     * @param turnovers String representation of the turnover points.
     * @return array of flags indicating turnover points.
     */
    private boolean[] buildTurnover(String turnovers) {
        boolean [] turing = new boolean[26];

        for (int i = 0; i < turing.length; ++i)
            turing[i] = false;

        for (int i = 0; i < turnovers.length(); ++i) {
            final int c = charToIndex(turnovers.charAt(i));
            turing[c] = true;
        }

        return turing;
    }

    /**
     * Translate the turnover points to notch points which occur 1 letter 
     * before the turnover point.
     * @return array of flags indicating notch points.
     */
    private boolean[] buildNotches() {
        boolean [] turing = new boolean[26];

        for (int i = 1; i < turnover.length; ++i)
            turing[i - 1] = turnover[i];

        turing[turnover.length - 1] = turnover[0];

        return turing;
    }

    /**
     * Constructor.
     * @param id of this mapping.
     * @param cipher String representation of the mapping.
     * @param date rotor was introduced (for reference purposes).
     * @param name of rotor group (for reference purposes).
     * @param turnover list of letters
     */
    public Rotor(String id, String cipher, String date, String name, String turnover) {
        super(id, buildIndices(cipher));
        this.cipher = cipher;
        this.date = date;
        this.name = name;
        this.turnover = buildTurnover(turnover);
        this.notches = buildNotches();

        offset = 0;
    }


    /************************************************************************
     * Getters support code.
     */

    public String getCipher()	{ return cipher; }
    public String getDate()		{ return date; }
    public String getName()		{ return name; }
    public boolean isTurnoverPoint(int index) { return turnover[index]; }
    public boolean isNotchPoint(int index) { return notches[index]; }

    public int getRingSetting()	{ return ringSetting; }

    public int getOffset() { return offset; }


    /************************************************************************
     * Setters support code.
     */

    public void setOffset(int value) { offset = value; }

    /**
     * Translates (swaps) an index (numerical equivalent of the letter) to 
     * another using the map.
     * @param direction of mapping. Eg A may map to J, but J may not map to A.
     * @param index to translate.
     * @param show the translation step on the command line.
     * @return the translated index.
     */
    public int swap(int direction, int index, boolean show) {
        int shift = (index + offset) % 26;

        int output = (direction == RIGHT_TO_LEFT) ? rightToLeft(shift) : leftToRight(shift);
        output = (output + 26 - offset) % 26;

        if (show)
            System.out.print(id + "[" + Rotor.indexToString(offset) + "](" + Rotor.indexToString(index) + "->" + Rotor.indexToString(output) + ")  ");

        return output;
    }

    /**
     * Set the ring setting and update the left and right mappings using the
     * map and ring setting.
     * @param index of the required ring setting.
     */
    public void setRingSetting(int index) {
        // System.out.println("setRingSetting(" + index + ")");

        ringSetting = index;

        for (int i = 0; i < map.length; ++i)
            rightMap[(i + index) % 26] = (map[i] + index) % 26;

        for (int i = 0; i < map.length; ++i)
            leftMap[rightMap[i]] = i;

        // System.out.print("rightMap = ");
        // dumpRightMap();
        // System.out.print("leftMap  = ");
        // dumpLeftMap();
    }


    /************************************************************************
     * Debug support code.
     */

    @Override
    public String toString() {
        return "Rotor [" + 
            "id=" + id + 
            ", map=" + Arrays.toString(map) + 
            // ", cipher=" + cipher + 
            ", name=" + name + 
            ", reflect=" + reflect + 
            ", offset=" + offset + 
            // ", date=" + date + 
            "]";
    }

    public void dumpFlags(boolean[] flags) {
        for (int i = 0; i < flags.length; ++i)
            System.out.print(flags[i] ? "1" : "0");

        System.out.println();
    }

    public void dumpCipher() { System.out.println(cipher); }
    public void dumpLeftMap() { dumpMapping(leftMap); }
    public void dumpRightMap() { dumpMapping(rightMap); }
    public void dumpTurnover() { dumpFlags(turnover); }
    public void dumpNotches() { dumpFlags(notches); }

}
