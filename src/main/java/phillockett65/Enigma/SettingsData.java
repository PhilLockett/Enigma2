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
 * Settings is a class that captures the details of a Luftwaffe Enigma key list 
 * entry as shown at https://en.wikipedia.org/wiki/Enigma_machine#Details.
 */
package phillockett65.Enigma;

import java.util.ArrayList;

public class SettingsData {

    private final String reflector;

    private final String[] rotors;
    private final int[] ringSettings;
    private final int[][] offsets;

    private final String plugboard;


    /************************************************************************
     * Initialization support code.
     */
    private String[] initRotors(String wheels) {
        String[] output = new String[4];
        ArrayList<String> list = Mapper.splitWords(wheels);
        final int max = list.size();

        output[0] = "I";
        for (int rotor = 0; rotor < max; ++rotor) {
            output[rotor+1] = list.get(rotor);
        }

        return output;
    }

    private int[] initRingSettings(int r1, int r2, int r3) {
        int[] output = new int[4];

        output[0] = 0;
        output[1] = r1-1;
        output[2] = r2-1;
        output[3] = r3-1;

        return output;
    }

    private int[][] initOffsets(String indicator) {
        ArrayList<String> list = Mapper.splitWords(indicator);
        final int max = list.size();
        int[][] output = new int[max][max];

        for (int i = 0; i < max; ++i) {
            final String str = list.get(i);
            final int len = str.length();

            output[0][i] = 0;
            for (int rotor = 0; rotor < len; ++rotor) {
                output[rotor+1][i] = Mapper.charToIndex(str.charAt(rotor));
            }
        }

        return output;
    }

    /**
     * Constructor.
     * @param wheels list of 3 rotors to use.
     * @param r1 ring offset of rotor 1.
     * @param r2 ring offset of rotor 2.
     * @param r3 ring offset of rotor 3.
     * @param ref String representation of the reflector mapping.
     * @param plugs String representation of the plugboard mapping.
     * @param indicator list of multiple start positions for the 3 rotors.
     */
    public SettingsData(String wheels, int r1, int r2, int r3, String ref, 
        String plugs, String indicator) {

        reflector = ref;

        rotors = initRotors(wheels);
        ringSettings = initRingSettings(r1, r2, r3);
        offsets = initOffsets(indicator);

        plugboard = plugs;
    }


    /************************************************************************
     * Getters support code.
     */

    public String getReflector() { return reflector; }

    public String getRotor(int index) { return rotors[index]; }
    public int getRingSetting(int index) { return ringSettings[index]; }
    public int getOffset(int index, int quarter) { return offsets[index][quarter]; }
    public int[] getOffsets(int index) { return offsets[index]; }

    public String getPlugboard() { return plugboard; }
  
 
    /************************************************************************
     * Debug support code.
     */

    @Override
    public String toString() {
        return "Settings [" + 
            "reflector=" + reflector + 
            ", plugboard=" + plugboard + 
            "]";
    }

}
