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
 * DataStore is a class that serializes the settings data for saving and 
 * restoring to and from disc.
 */
package phillockett65.Enigma;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class DataStore implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reflectorChoice;
    private Boolean reconfigurable;
    private ArrayList<String> pairs = new ArrayList<String>();

    private Boolean fourthWheel;
    private Boolean useLetters;
    private Boolean show;

    private ArrayList<String> wheels = new ArrayList<String>();
    private ArrayList<Integer> ringSettings = new ArrayList<Integer>();
    private ArrayList<Integer> rotorOffsets = new ArrayList<Integer>();

    private ArrayList<String> plugs = new ArrayList<String>();
    private Boolean encipher;

    public DataStore() {
    }

    /**
     * Data exchange from the model to this DataStore.
     * @param model contains the data.
     * @return true if data successfully pulled from the model, false otherwise.
     */
    public boolean pull(Model model) {
        boolean success = true;

        reflectorChoice = model.getReflectorChoice();
        reconfigurable = model.isReconfigurable();

        final int pairCount = model.getPairCount();
        for (int i = 0; i < pairCount; ++i)
            pairs.add(model.getPairText(i));

        fourthWheel = model.isFourthWheel();
        useLetters = model.isUseLetters();
        show = model.isShow();

        final int rotorStateCount = model.getRotorStateCount();
        for (int i = 0; i < rotorStateCount; ++i) {
            wheels.add(model.getWheelChoice(i));
            ringSettings.add(model.getRingIndex(i));
            rotorOffsets.add(model.getRotorIndex(i));
        }

        final int plugCount = model.getPlugCount();
        for (int i = 0; i < plugCount; ++i)
            plugs.add(model.getPlugText(i));

        encipher = model.isEncipher();

        return success;
    }

    /**
     * Data exchange from this DataStore to the model.
     * @param model contains the data.
     * @return true if data successfully pushed to the model, false otherwise.
     */
    public boolean push(Model model) {
        boolean success = true;

        model.setReflectorChoice(reflectorChoice);
        model.setReconfigurable(reconfigurable);

        final int pairCount = pairs.size();
        for (int i = 0; i < pairCount; ++i)
            model.setPairText(i, pairs.get(i));

        model.setFourthWheel(fourthWheel);
        model.setUseLetters(useLetters);
        model.setShow(show);

        final int rotorStateCount = wheels.size();
        for (int i = 0; i < rotorStateCount; ++i) {
            model.setWheelChoice(i, wheels.get(i));
            model.setRingIndex(i, ringSettings.get(i));
            model.setRotorIndex(i, rotorOffsets.get(i));
        }

        final int plugCount = plugs.size();
        for (int i = 0; i < plugCount; ++i)
            model.setPlugText(i, plugs.get(i));

        model.setEncipher(encipher);

        return success;
    }



    /************************************************************************
     * Support code for static public interface.
     */

    /**
     * Static method that instantiates a DataStore, populates it from the 
     * model and writes it to disc.
     * @param model contains the data.
     * @return true if data successfully written to disc, false otherwise.
     */
    public static boolean writeData(Model model) {
        boolean success = false;

        DataStore dataStore = new DataStore();
        dataStore.pull(model);
        dataStore.dump();

        ObjectOutputStream objectOutputStream;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(model.getSettingsFile()));

            objectOutputStream.writeObject(dataStore);
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return success;
    }

    /**
     * Static method that instantiates a DataStore, populates it from disc 
     * and writes it to the model.
     * @param model contains the data.
     * @return true if data successfully read from disc, false otherwise.
     */
    public static boolean readData(Model model) {
        boolean success = false;

        ObjectInputStream objectInputStream;
        try {
            objectInputStream = new ObjectInputStream(new FileInputStream(model.getSettingsFile()));

            DataStore dataStore = (DataStore)objectInputStream.readObject();
            success = dataStore.push(model);
            dataStore.dump();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        return success;
    }



    /************************************************************************
     * Support code for debug stuff.
     */

    /**
      * Print data store on the command line.
      */
    private void dump() {
        // System.out.println("reflectorChoice = " + reflectorChoice);
        // System.out.println("reconfigurable = " + reconfigurable);
        // System.out.println("pairs = " + pairs);

        // System.out.println("fourthWheel = " + fourthWheel);
        // System.out.println("useLetters = " + useLetters);
        // System.out.println("show = " + show);

        // System.out.println("wheels = " + wheels);
        // System.out.println("ringSettings = " + ringSettings);
        // System.out.println("rotorOffsets = " + rotorOffsets);

        // System.out.println("plugs = " + plugs);
        // System.out.println("encipher = " + encipher);
    }

}

