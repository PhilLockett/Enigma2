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
package phillockett65.Enigma;

import java.util.Arrays;

public class RotorData extends Mapper {

    private final String cipher;
    private final String date;
    private final String name;
    private final String turnovers;
    private final boolean[] turnover;
    private final boolean[] notches;
    

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
        boolean [] mathison = new boolean[26];

        for (int i = 0; i < mathison.length; ++i)
            mathison[i] = false;

        for (int i = 0; i < turnovers.length(); ++i) {
            final int c = charToIndex(turnovers.charAt(i));
            mathison[c] = true;
        }

        return mathison;
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
    public RotorData(String id, String cipher, String date, String name, String turnover) {
        super(id, buildIndices(cipher));
        this.cipher = cipher;
        this.date = date;
        this.name = name;
        this.turnovers = turnover;
        this.turnover = buildTurnover(turnover);
        this.notches = buildNotches();
    }


    /************************************************************************
     * Getters support code.
     */

    public String getCipher()	{ return cipher; }
    public String getDate()		{ return date; }
    public String getName()		{ return name; }
    public String getTurnovers() { return turnovers; }
    public boolean isTurnoverPoint(int index) { return turnover[index]; }
    public boolean isNotchPoint(int index) { return notches[index]; }


    /************************************************************************
     * Debug support code.
     */

    @Override
    public String toString() {
        return "Rotor [" + 
            "id=" + getId() + 
            ", map=" + Arrays.toString(getMap()) + 
            // ", cipher=" + cipher + 
            ", name=" + name + 
            ", reflect=" + isReflector() + 
            // ", date=" + date + 
            "]";
    }

    public void dumpFlags(boolean[] flags) {
        for (int i = 0; i < flags.length; ++i)
            System.out.print(flags[i] ? "1" : "0");

        System.out.println();
    }

    public void dumpCipher() { System.out.println(cipher); }
    public void dumpTurnover() { dumpFlags(turnover); }
    public void dumpNotches() { dumpFlags(notches); }

}
