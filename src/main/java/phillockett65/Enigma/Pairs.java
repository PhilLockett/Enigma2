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
 * Pairs is a class that captures a collection letter pairs.
 */
package phillockett65.Enigma;

import java.util.ArrayList;

public class Pairs {

    private boolean[] letterUsed;
    private boolean[] multiUseErrors;
    private boolean multiUseError = false;
    private int letterCount = 0;
    private int[] letterCounts;

    private ArrayList<Pair> list;
    private boolean allowingEmpty = false;


    /**
     * Counts the letter frequency in the given list.
     */
    public void countLetterUsage() {

        multiUseError = false;
        letterCount = 0;
        for (int i = 0; i < letterCounts.length; ++i) {
            letterUsed[i] = false;
            multiUseErrors[i] = false;
            letterCounts[i] = 0;
        }

        for (Pair pair : list) {
            for (int i = 0; i < pair.count(); ++i) {
                if (pair.isCharAt(i)) {
                    final int index = pair.indexAt(i);
                    ++letterCounts[index];

                    if (letterUsed[index]) {
                        multiUseErrors[index] = true;
                        multiUseError = true;
                    } else {
                        letterUsed[index] = true;
                        ++letterCount;
                    }
                }
            }
        }
    }

    /**
     * Update the indexed plug with new text String then count the letter 
     * usage of all plugs.
     * @param index of targeted plug.
     * @param text to use.
     */
    public void setText(int index, String text) {
        list.get(index).set(text);
        countLetterUsage();
    }

    public void setEnabled(int index, boolean state) { list.get(index).setEnabled(state); }
    public int size() { return list.size(); }
    public String getText(int index)	{ return list.get(index).get(); }
    public int getCount(int index)		{ return list.get(index).count(); }

    public boolean isAllowingEmpty() { return allowingEmpty; }
    public boolean isMultiUseError() { return multiUseError; }

    /**
     * Determine if the indexed plug is valid.
     * @param index of targeted plug.
     * @return true if the plug is valid, false otherwise.
     */
    public boolean isValid(int index) {
        Pair pair = list.get(index);
        
        if ((isAllowingEmpty()) && (pair.isEmpty()))
            return true;

        if (!pair.isValid())
            return false;

        if (isAllowingEmpty()) {
            if (multiUseErrors[pair.first()])
                return false;
            if (multiUseErrors[pair.second()])
                return false;
        } else {
            if (letterCounts[pair.first()] != 1)
                return false;
            if (letterCounts[pair.second()] != 1)
                return false;
            }

        return true;
    }

    /**
     * Determine if the plugboard is valid.
     * @return true if the plugboard is valid, false otherwise.
     */
    public boolean isValid() {
        if (multiUseError)
            return false;

        if (isAllowingEmpty()) {
            for (Pair pair : list)
                if ((!pair.isEmpty()) && (!pair.isValid()))
                    return false;
        } else {
            // Check we have only 1 unconfigured pair.
            if (letterCount != 24)
                return false;

            for (Pair pair : list)
                if (!pair.isValid())
                    return false;
        }

        return true;
    }

    /**
     * Only called if isPlugboardValid() is true.
     */
    public int[] getMap() {
        int[] map = new int[26];

        for (int i = 0; i < map.length; ++i)
            map[i] = i;

        for (Pair pair : list) {
            if (pair.isEmpty())
                continue;

            final int a = pair.first();
            final int b = pair.second();
            map[a] = b;
            map[b] = a;
        }

        if (!isAllowingEmpty()) {
            // Set up unconfigured pair.
            int x = -1;
            for (int i = 0; i < letterUsed.length; ++i) {
                if (!letterUsed[i]) {
                    if (x == -1)
                        x = i;
                    else {
                        map[x] = i;
                        map[i] = x;

                        break;
                    }
                }
            }
        }

        return map;
    }


    public void clear() {
        for (Pair pair : list)
            pair.clear();
    }

    /**
     * Initialise pairList with the given ArrayList of Strings represntation.
     * @param links ArrayList of Strings.
     */
    public void setLinks(ArrayList<String> links) {
        // System.out.println("setLinks()");
        clear();
        if (links == null)
            return;

        int index = 0;
        for (String pairString : links) {
            if (pairString.length() != 2)
                continue;

            list.get(index++).set(pairString);
        }
        countLetterUsage();
    }

    /**
     * Construct a pair set.
     * @param allow blank pairs.
     */
    public Pairs(boolean allow) {
        allowingEmpty = allow;
        letterUsed = new boolean[26];
        multiUseErrors = new boolean[26];
        letterCounts = new int[26];

        final int size = allow ? Model.FULL_COUNT : Model.PAIR_COUNT;
        list = new ArrayList<Pair>(size);
        for (int i = 0; i < size; ++i)
            list.add(new Pair());
    }

}
