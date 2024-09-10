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
 * Rotor is a class that extends Mapper and captures the details of a rotor 
 * including the ring setting which is set post instantiation and the rotation
 * (offset) which is dynamically updated in normal use. Note, the turnover point
 * immediately follows the notch point.
 */
package phillockett65.Enigma2;

import java.util.Arrays;

public class Rotor extends RotorData {

    private int[] leftMap;
    private int[] rightMap;
    
    private int ringSetting;
    private int offset;


    /************************************************************************
     * Initialization support code.
     */

    /**
     * Constructor.
     * @param id of this mapping.
     * @param cipher String representation of the mapping.
     * @param date rotor was introduced (for reference purposes).
     * @param name of rotor group (for reference purposes).
     * @param turnover list of letters
     */
    public Rotor(String id, String cipher, String date, String name, String turnover) {
        super(id, cipher, date, name, turnover);

        rightMap = new int[26];
        leftMap = new int[26];

        ringSetting = 0;
        offset = 0;
    }

    public Rotor(RotorData rhs) {
        super(rhs.getId(), rhs.getCipher(), rhs.getDate(), rhs.getName(), rhs.getTurnovers());

        rightMap = new int[26];
        leftMap = new int[26];

        ringSetting = 0;
        offset = 0;
    }


    /************************************************************************
     * Getters support code.
     */

    public int getRingSetting()	{ return ringSetting; }

    public int getOffset() { return offset; }

    private int leftToRight(int index) { return leftMap[index]; }
    private int rightToLeft(int index) { return rightMap[index]; }


    /************************************************************************
     * Setters support code.
     */

    public void setOffset(int value) { offset = value; }

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
     * @param direction of mapping. Eg A may map to J, but J may not map to A.
     * @param index to translate.
     * @param show the translation step on the command line.
     * @return the translated index.
     */
    public int swap(int direction, int index, boolean show) {
        final int shift = (index + offset) % 26;

        int output = swap(direction, shift);
        output = (output + 26 - offset) % 26;

        if (show)
            System.out.print(getId() + "[" + indexToLetter(offset) + "](" + indexToLetter(index) + "->" + indexToLetter(output) + ")  ");

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

        for (int i = 0; i < getMapLength(); ++i)
            rightMap[(i + index) % 26] = (getMapItem(i) + index) % 26;

        for (int i = 0; i < getMapLength(); ++i)
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
            "id=" + getId() + 
            ", map=" + Arrays.toString(getMap()) + 
            // ", cipher=" + cipher + 
            ", name=" + getName() + 
            ", reflect=" + isReflector() + 
            ", offset=" + offset + 
            // ", date=" + date + 
            "]";
    }

}
