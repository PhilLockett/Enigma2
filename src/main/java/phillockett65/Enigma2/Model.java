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
 * Model is the class that captures the dynamic shared data plus some 
 * supporting constants and provides access via getters and setters.
 */
package phillockett65.Enigma2;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Model {

    private final static String DATAFILE = "Settings.dat";

    public static final int ROTOR_COUNT = 4;
    public final static int FULL_COUNT = 13;
    public final static int PLUG_COUNT = 10;
    public final static int PAIR_COUNT = 12;

    private static final int OTHER = -1;
    private static final int SLOW = 0;
    private static final int LEFT = 1;
    private static final int MIDDLE = 2;
    private static final int RIGHT = 3;


    /************************************************************************
     * General support code.
     */

    private int idToIndex(String id) { return Integer.valueOf(id); }

    /**
     * Counts the letter frequency in the given list.
     * @param counts of letter frequency (count[0] -> 'A')
     * @param list of letter pairs for counting.
     */
    private void countLetterUsage(int[] counts, ArrayList<Pair> list) {

        for (int i = 0; i < counts.length; ++i) 
            counts[i] = 0;

        for (Pair pair : list) {
            for (int i = 0; i < pair.count(); ++i) {
                if (pair.isCharAt(i)) {
                    final int index = pair.indexAt(i);
                    counts[index]++;
                }
            }
        }
    }


    /**
     * @return the file path of the settings data file.
     */
    public String getSettingsFile() {
        return DATAFILE;
    }



    /************************************************************************
     * Support code for the Initialization of the Model.
     */

    /**
     * Responsible for constructing the Model and any local objects. Called by 
     * the controller.
     */
    public Model() {
        initRotorWiring();
    }


    /**
     * Called by the controller after the constructor to initialise any 
     * objects after the controls have been initialised.
     */
    public void initialize() {
        // System.out.println("Model initialized.");

        initializeReflector();
        initializeRotorSetup();
        initializePlugboardConnections();
        initializeEncipher();

        if (!DataStore.readData(this))
            defaultSettings();
    }

    /**
     * Called by the controller after the stage has been set. Completes any 
     * initialization dependent on other components being initialized.
     */
    public void init() {
        // System.out.println("Model init.");
    }

    /**
     * Set all attributes to the default values.
     */
    public void defaultSettings() {
        setReflectorChoice("Reflector B");

        setReconfigurable(false);
        for (Pair pair : pairs)
            pair.clear();

        setFourthWheel(false);
        setRotorState(SLOW, "IV", 0, 0);
        setRotorState(LEFT, "I", 1, 0);
        setRotorState(MIDDLE, "II", 10, 20);
        setRotorState(RIGHT, "III", 1, 25);

        setUseNumbers(false);
        setShow(false);

        setExtPlugboard(false);
        for (Pair pair : plugs)
            pair.clear();

        setEncipher(false);
    }



    /************************************************************************
     * Support code for Rotor definitions.
     */

    ObservableList<Rotor> commercial = FXCollections.observableArrayList();
    ObservableList<Rotor> rocket = FXCollections.observableArrayList();
    ObservableList<Rotor> swissK = FXCollections.observableArrayList();
    ObservableList<Rotor> m3 = FXCollections.observableArrayList();
    ObservableList<Rotor> m4 = FXCollections.observableArrayList();

    ObservableList<Rotor> rotors = FXCollections.observableArrayList();
    ObservableList<Rotor> reflectors = FXCollections.observableArrayList();

    /**
     * Construct all the Rotor collections.
     * 
     * Note: for the commercial, rocket and swissK Rotors, the turnover points 
     * are guesses and may be incorrect.
     */
    private void initRotorWiring() {

        commercial.add(new Rotor("IC",	"DMTWSILRUYQNKFEJCAZBPGXOHV",	"1924",	"Commercial Enigma A, B", "R"));
        commercial.add(new Rotor("IIC",	"HQZGPJTMOBLNCIFDYAWVEUSRKX",	"1924",	"Commercial Enigma A, B", "F"));
        commercial.add(new Rotor("IIIC","UQNTLSZFMREHDPXKIBVYGJCWOA",	"1924",	"Commercial Enigma A, B", "W"));

        rocket.add(new Rotor("I-R", 	"JGDQOXUSCAMIFRVTPNEWKBLZYH",	"7 February 1941",	"German Railway (Rocket)", "R"));
        rocket.add(new Rotor("II-R",	"NTZPSFBOKMWRCJDIVLAEYUXHGQ",	"7 February 1941",	"German Railway (Rocket)", "F"));
        rocket.add(new Rotor("III-R",	"JVIUBHTCDYAKEQZPOSGXNRMWFL",	"7 February 1941",	"German Railway (Rocket)", "W"));
        rocket.add(new Rotor("UKW-R",	"QYHOGNECVPUZTFDJAXWMKISRBL",	"7 February 1941",	"German Railway (Rocket)", ""));
        rocket.add(new Rotor("ETW-R",	"QWERTZUIOASDFGHJKPYXCVBNML",	"7 February 1941",	"German Railway (Rocket)", ""));

        swissK.add(new Rotor("I-K",		"PEZUOHXSCVFMTBGLRINQJWAYDK",	"February 1939",	"Swiss K", "R"));
        swissK.add(new Rotor("II-K",	"ZOUESYDKFWPCIQXHMVBLGNJRAT",	"February 1939",	"Swiss K", "F"));
        swissK.add(new Rotor("III-K",	"EHRVXGAOBQUSIMZFLYNWKTPDJC",	"February 1939",	"Swiss K", "W"));
        swissK.add(new Rotor("UKW-K",	"IMETCGFRAYSQBZXWLHKDVUPOJN",	"February 1939",	"Swiss K", ""));
        swissK.add(new Rotor("ETW-K",	"QWERTZUIOASDFGHJKPYXCVBNML",	"February 1939",	"Swiss K", ""));

        m3.add(new Rotor("I",		"EKMFLGDQVZNTOWYHXUSPAIBRCJ",	"1930",	"Enigma I", "R"));
        m3.add(new Rotor("II",		"AJDKSIRUXBLHWTMCQGZNPYFVOE",	"1930",	"Enigma I", "F"));
        m3.add(new Rotor("III",		"BDFHJLCPRTXVZNYEIWGAKMUSQO",	"1930",	"Enigma I", "W"));
        m3.add(new Rotor("IV",		"ESOVPZJAYQUIRHXLNFTGKDCMWB",	"December 1938",	"M3 Army", "K"));
        m3.add(new Rotor("V",		"VZBRGITYUPSDNHLXAWMJQOFECK",	"December 1938",	"M3 Army", "A"));
        m3.add(new Rotor("VI",		"JPGVOUMFYQBENHZRDKASXLICTW",	"1939",	"M3 & M4 Naval (FEB 1942)", "AN"));
        m3.add(new Rotor("VII",		"NZJHGRCXMYSWBOUFAIVLPEKQDT",	"1939",	"M3 & M4 Naval (FEB 1942)", "AN"));
        m3.add(new Rotor("VIII",	"FKQHTLXOCBJSPDZRAMEWNIUYGV",	"1939",	"M3 & M4 Naval (FEB 1942)", "AN"));

        m4.add(new Rotor("Beta",				"LEYJVCNIXWPBQMDRTAKZGFUHOS",	"Spring 1941",	"M4 R2", ""));
        m4.add(new Rotor("Gamma",				"FSOKANUERHMBTIYCWLQPZXVGJD",	"Spring 1942",	"M4 R2", ""));
        m4.add(new Rotor("Reflector A",			"EJMZALYXVBWFCRQUONTSPIKHGD",	"",	"", ""));
        m4.add(new Rotor("Reflector B",			"YRUHQSLDPXNGOKMIEBFZCWVJAT",	"",	"", ""));
        m4.add(new Rotor("Reflector C",			"FVPJIAOYEDRZXWGCTKUQSBNMHL",	"",	"", ""));
        m4.add(new Rotor("Reflector B Thin",	"ENKQAUYWJICOPBLMDXZVFTHRGS",	"1940",	"M4 R1 (M3 + Thin)", ""));
        m4.add(new Rotor("Reflector C Thin",	"RDOBJNTKVEHMLFCWZAXGYIPSUQ",	"1940",	"M4 R1 (M3 + Thin)", ""));
        m4.add(new Rotor("ETW",					"ABCDEFGHIJKLMNOPQRSTUVWXYZ",	"",	"Enigma I", ""));

        // Build list of rotors and list of reflectors that can be selected.
        for (Rotor rotor : m3)
            if (!rotor.isReflector())
                rotors.add(rotor);

        for (Rotor rotor : m4)
            if (!rotor.isReflector())
                rotors.add(rotor);
            else
                reflectors.add(rotor);

        for (Rotor rotor : rocket)
            if (!rotor.isReflector())
                rotors.add(rotor);
            else
                reflectors.add(rotor);

        for (Rotor rotor : swissK)
            if (!rotor.isReflector())
                rotors.add(rotor);
            else
                reflectors.add(rotor);

        for (Rotor rotor : commercial)
            if (!rotor.isReflector())
                rotors.add(rotor);
    }



    /************************************************************************
     * Support code for "Reflector Set-Up" panel.
     */

    ObservableList<String> reflectorList = FXCollections.observableArrayList();
    private String reflectorChoice;
    private boolean reconfigurable = false;

    private int[] reflectorLetterCounts;
    private int[] reconfigurableReflectorMap;
    private int[] reflectorMap;
    private Mapper reflector;

    private ArrayList<Pair> pairs = new ArrayList<Pair>(PAIR_COUNT);


    public ObservableList<String> getReflectorList()   { return reflectorList; }
    public String getReflectorChoice()   { return reflectorChoice; }
    public void setReflectorChoice(String choice)   { reflectorChoice = choice; }

    public void setReconfigurable(boolean state) { reconfigurable = state; }
    public boolean isReconfigurable() { return reconfigurable; }


    /**
     * Update the indexed pair with new text String then count the letter 
     * usage of all pairs.
     * @param index of targeted pair.
     * @param text to use.
     */
    public void setPairText(int index, String text) {
        pairs.get(index).set(text);
        countLetterUsage(reflectorLetterCounts, pairs);
    }

    public int getPairCount() { return pairs.size(); }
    public String getPairText(int index)	{ return pairs.get(index).get(); }
    public int getPairCount(int index)		{ return pairs.get(index).count(); }

    /**
     * Determine if the indexed pair is valid.
     * @param index of targeted pair.
     * @return true if the pair is valid, false otherwise.
     */
    public boolean isPairValid(int index) {
        if (!reconfigurable)
            return true;

        Pair pair = pairs.get(index);

        if (!pair.isValid())
            return false;

        for (int i = 0; i < 2; ++i)
            if (reflectorLetterCounts[pair.indexAt(i)] != 1)
                return false;

        return true;
    }

    public void setPairText(String id, String text) { setPairText(idToIndex(id), text); }
    public String getPairText(String id)	{ return getPairText(idToIndex(id)); }
    public int getPairCount(String id)		{ return getPairCount(idToIndex(id)); }
    public boolean isPairValid(String id)	{ return isPairValid(idToIndex(id)); }

    /**
     * Determine if the reconfigurable reflector is valid.
     * @return true if the reconfigurable reflector is valid, false otherwise.
     */
    private boolean isReconfigurableReflectorValid() {
        for (Pair pair : pairs)
            if (!pair.isValid())
                return false;

        int empty = 0;
        for (int i = 0; i < reflectorLetterCounts.length; ++i) {
            if (reflectorLetterCounts[i] == 0)
                empty++;

            if (reflectorLetterCounts[i] > 1)
                return false;
        }

        // Check we have only 1 unconfigured pair.
        if (empty != 2)
            return false;

        return true;
    }

    /**
     * Determine if the reflector is valid.
     * @return true if the reflector is valid, false otherwise.
     */
    public boolean isReflectorValid() {
        if (reconfigurable)
            return isReconfigurableReflectorValid();

        return true;
    }

    /**
     * Only called if isReconfigurableReflectorValid() is true.
     */
    private void setReconfigurableReflectorMap() {
        for (int i = 0; i < reconfigurableReflectorMap.length; ++i)
            reconfigurableReflectorMap[i] = 0;

        for (Pair pair : pairs) {
            final int a = pair.indexAt(0);
            final int b = pair.indexAt(1);
            reconfigurableReflectorMap[a] = b;
            reconfigurableReflectorMap[b] = a;
        }

        // Set up unconfigured pair.
        int x = -1;
        int y = -1;

        for (int i = 0; i < reflectorLetterCounts.length; ++i)
            if (reflectorLetterCounts[i] == 0) {
                if (x == -1)
                    x = i;
                else
                    y = i;
            }

        if ((x != -1) && (y != -1)) {
            reconfigurableReflectorMap[x] = y;
            reconfigurableReflectorMap[y] = x;
        }
    }

    /**
     * @return a reference to the active reflector map.
     */
    private int[] getReflectorMap() {
        
        if (reconfigurable) {
            setReconfigurableReflectorMap();

            return reconfigurableReflectorMap;
        } else {
            Rotor rotor = getRotor(reflectors, reflectorChoice);
            if (rotor != null)
                return rotor.getMap();
        }

        return null;
    }

    /**
     * Ascertain the active reflector map and assign to the global variable 
     * reflectorMap.
     */
    private void lockdownReflector() {
        reflectorMap = getReflectorMap();
    }


    /**
     * Construct the list of reflector names.
     */
    private void fillReflectorList() {
        reflectorList.clear();

        for (Rotor rotor : reflectors)
            reflectorList.add(rotor.getId());
    }

    /**
     * Initialize "Reflector Set-Up" panel.
     */
    private void initializeReflector() {
        reflectorLetterCounts = new int[26];
        reconfigurableReflectorMap = new int[26];
        fillReflectorList();

        for (int i = 0; i < PAIR_COUNT; ++i)
            pairs.add(new Pair());
    }



    /************************************************************************
     * Support code for "Rotor Set-Up" panel.
     */

    private ObservableList<String> wheelList = FXCollections.observableArrayList();

    private ArrayList<RotorControl> rotorControls = new ArrayList<RotorControl>(ROTOR_COUNT);

    private boolean fourthWheel = false;
    private boolean useNumbers = false;
    private boolean show = false;


    public ObservableList<String> getWheelList() { return wheelList; }

    public ArrayList<RotorControl> getRotorControls() { return rotorControls; }
    public int getRotorStateCount() { return rotorControls.size(); }

    public void setFourthWheel(boolean state) {
        fourthWheel = state;
        getState(SLOW).setDisable(!fourthWheel);
    }
    public boolean isFourthWheel() { return fourthWheel; }

    /**
     * Update useLetters and synchronise the ring setting and rotor offset 
     * Spinners.
     * @param state assigned to useLetters;
     */
    public void setUseNumbers(boolean state) {
        if (useNumbers == state)
            return;

        useNumbers = state;
        for (RotorControl rotor : rotorControls)
            rotor.setUseNumbers(useNumbers);
    }

    public boolean isUseNumbers() { return useNumbers; }
    
    public boolean isShow() { return show; }
    public void setShow(boolean state) { show = state; }


    public void setRotorState(int index, String wheelChoice, int ringIndex, int rotorIndex) { 
        getState(index).set(wheelChoice, ringIndex, rotorIndex); 
    }

    public void setTranslate(boolean selected) {
        for (RotorControl rotor : rotorControls)
            rotor.setLockDown(selected);
    }



    private RotorControl getState(int index) { return rotorControls.get(index); }

    public String getWheelChoice(int index) { return getState(index).getWheelChoice(); }
    public void setWheelChoice(int index, String choice) { getState(index).setWheelChoice(choice); }

    public int getRingIndex(int index) { return getState(index).getRingIndex(); }
    public void setRingSetting(int index, String value) { getState(index).setRingIndex(Mapper.stringToIndex(value)); }
    public void setRingIndex(int index, int value) { getState(index).setRingIndex(value); }

    public int getRotorIndex(int index) { return getState(index).getRotorIndex(); }
    public void setRotorOffset(int index, String value) { getState(index).setRotorIndex(Mapper.stringToIndex(value)); }
    public void setRotorIndex(int index, int value) { getState(index).setRotorIndex(value); }
    private void incrementRotorOffset(int index, int step) { getState(index).increment(step); }


    /**
     * Initialize "Rotor Set-Up".
     */
    private void initializeRotorSetup() {
        // Initialize wheelList.
        for (Rotor rotor : rotors)
            wheelList.add(rotor.getId());

        // Initialize "Rotor Control Set-Up".
        final String[] names = { "fourth", "left", "middle", "right" };
        for (int i = 0; i < ROTOR_COUNT; ++i) {
            rotorControls.add(new RotorControl());
            getState(i).init(names[i], wheelList);
        }
    }



    /************************************************************************
     * Support code for "Plugboard Connections" panel.
     */
    
    private int[] plugboardLetterCounts;
    private int[] plugboardMap;
    private Mapper plugboard;

    private ArrayList<Pair> plugs = new ArrayList<Pair>(FULL_COUNT);
    private boolean extPlugboard = false;

    public void setExtPlugboard(boolean state) { extPlugboard = state; }
    public boolean isExtPlugboard() { return extPlugboard; }

    /**
     * Update the indexed plug with new text String then count the letter 
     * usage of all plugs.
     * @param index of targeted plug.
     * @param text to use.
     */
    public void setPlugText(int index, String text) {
        plugs.get(index).set(text);
        countLetterUsage(plugboardLetterCounts, plugs);
    }

    public int getPlugCount() { return plugs.size(); }
    public int getActivePlugCount() { return extPlugboard ? plugs.size() : PLUG_COUNT; }
    public String getPlugText(int index)	{ return plugs.get(index).get(); }
    public int getPlugCount(int index)		{ return plugs.get(index).count(); }

    /**
     * Determine if the indexed plug is valid.
     * @param index of targeted plug.
     * @return true if the plug is valid, false otherwise.
     */
    public boolean isPlugValid(int index) {
        Pair plug = plugs.get(index);

        if (plug.isEmpty())
            return true;

        if (!plug.isValid())
            return false;

        for (int i = 0; i < 2; ++i)
            if (plugboardLetterCounts[plug.indexAt(i)] > 1)
                return false;

        return true;
    }

    public void setPlugText(String id, String text) { setPlugText(idToIndex(id), text); }
    public String getPlugText(String id)	{ return getPlugText(idToIndex(id)); }
    public int getPlugCount(String id)		{ return getPlugCount(idToIndex(id)); }
    public boolean isPlugValid(String id)	{ return isPlugValid(idToIndex(id)); }

    /**
     * Determine if the plugboard is valid.
     * @return true if the plugboard is valid, false otherwise.
     */
    public boolean isPlugboardValid() {
        for (Pair pair : plugs)
        if ((!pair.isEmpty()) && (!pair.isValid()))
                return false;

        for (int i = 0; i < plugboardLetterCounts.length; ++i)
            if (plugboardLetterCounts[i] > 1)
                return false;

        return true;
    }

    /**
     * Only called if isPlugboardValid() is true.
     */
    private void setPlugboardMap() {
        for (int i = 0; i < plugboardMap.length; ++i)
            plugboardMap[i] = i;

        int i = 0;
        for (Pair plug : plugs) {
            if ((!extPlugboard) && (i == PLUG_COUNT))
                break;

            if (plug.isEmpty())
                continue;

            int a = plug.indexAt(0);
            int b = plug.indexAt(1);
            plugboardMap[a] = b;
            plugboardMap[b] = a;
            ++i;
        }
    }

    /**
     * Lockdown the plugboardMap.
     */
    private void lockdownPlugboard() {
        setPlugboardMap();
    }

    /**
     * Initialize "Plugboard Connections" panel.
     */
    private void initializePlugboardConnections() {
        plugboardLetterCounts = new int[26];
        plugboardMap = new int[26];

        for (int i = 0; i < FULL_COUNT; ++i)
            plugs.add(new Pair());
    }



    /************************************************************************
     * Support code for "Translation" panel.
     */

    private boolean encipher = false;

    private ArrayList<Translation> pipeline = new ArrayList<Translation>(9);

    /**
     * Determine if all settings are valid which requires checking the 
     * reflector and the plugboard.
     * @return true if the settings are valid, false otherwise.
     */
    public boolean isConfigValid() {
        return isPlugboardValid() && isReflectorValid();
    }

    public boolean isEncipher() { return encipher; }

    /**
     * Find a Rotor with the given id in the given list,
     * @param list of Rotors to search.
     * @param target id of Rotor.
     * @return Rotor with matching id if found, null otherwise.
     */
    private Rotor getRotor(ObservableList<Rotor> list, String target) {
        for (Rotor rotor : list)
             if (rotor.is(target))
                return rotor;

        return null;
    }


    /**
     * Advances the spinner of the right rotor then checks the other rotors. 
     * The notch point of the middle rotor is used to check for a step of the 
     * left rotor and a double step of the middle rotor. The turnover point of 
     * the right rotor is used to check for a step of the middle rotor
     */
    private void advanceRotors() {
        // Normal step of the spinner of the right rotor.
        incrementRotorOffset(RIGHT, 1);

        Rotor rotor = getRotor(rotors, getWheelChoice(MIDDLE));
        if (rotor.isNotchPoint(getRotorIndex(MIDDLE))) {
            // Double step of the spinner of the middle rotor, normal step of 
            // the spinner of the left rotor.
            incrementRotorOffset(MIDDLE, 1);
            incrementRotorOffset(LEFT, 1);
        }

        rotor = getRotor(rotors, getWheelChoice(RIGHT));
        if (rotor.isTurnoverPoint(getRotorIndex(RIGHT))) {
            // The right rotor takes the spinner of the middle rotor one step 
            // further.
            incrementRotorOffset(MIDDLE, 1);
        }
    }


    /**
     * Translation is a class that is used in the construction of the pipeline 
     * to maintain direction and helps manage the offsets of the Rotors.
     */
    private class Translation {
        private final int pos;
        private final Mapper mapper;
        private final int dir;

        public Translation(int id, Mapper mapper, int dir) {
            this.pos = id;
            this.mapper = mapper;
            this.dir = dir;
        }

        /**
         * Update the offset of this mapper only if target matches pos.
         * @param target position to match with this pos.
         * @param offset to set this offset to.
         * @return true if the offset is updated, false otherwise.
         */
        public boolean conditionallyUpdate(int target, int offset) {
            if (target == pos) {
                mapper.setOffset(offset);

                return true;
            }

            return false;
        }
    
        /**
         * Translates an index (numerical equivalent of the letter) to another 
         * using this directional Mapper (Rotor).
         * @param index to translate.
         * @return the translated index.
         */
        public int translate(int index) {
            return mapper.swap(dir, index, isShow());
        }	

    }

    /**
     * Translates an index (numerical equivalent of the letter) to another for 
     * every Mapper in the pipeline.
     * @param index to translate.
     * @return the translated index.
     */
    private int translatePipeline(int index) {
        if (isShow())
            System.out.print("Key: " + Mapper.indexToLetter(index) + "  ");

        for (Translation translator : pipeline)
            index = translator.translate(index);

        if (isShow())
            System.out.println("Lamp: " + Mapper.indexToLetter(index));

        return index;
    }

    /**
     * Advance the Rotor Spinners then update the Rotors to match.
     */
    private void updatePipeline() {
        advanceRotors();

        for (int i = 0; i < ROTOR_COUNT; ++i) {
            int offset = getRotorIndex(i);

            for (Translation translator : pipeline)
                translator.conditionallyUpdate(i, offset);
        }
    }

    /**
     * Advance the Rotors and translate an index (numerical equivalent of the 
     * letter) through the pipeline.
     * @param index to translate.
     * @return the translated index.
     */
    public int translate(int index) {
        updatePipeline();
        return translatePipeline(index);
    }

    /**
     * Build the pipeline of Mappers (Rotors) including the identifiers for 
     * offset updates and the direction of translation.
     */
    private void buildPipeline() {

        pipeline.clear();

        Rotor slow = getRotor(rotors, getWheelChoice(SLOW));

        Rotor left = getRotor(rotors, getWheelChoice(LEFT));
        Rotor middle = getRotor(rotors, getWheelChoice(MIDDLE));
        Rotor right = getRotor(rotors, getWheelChoice(RIGHT));

        pipeline.add(new Translation(OTHER, plugboard, Mapper.RIGHT_TO_LEFT));

        pipeline.add(new Translation(RIGHT, right, Mapper.RIGHT_TO_LEFT));
        pipeline.add(new Translation(MIDDLE, middle, Mapper.RIGHT_TO_LEFT));
        pipeline.add(new Translation(LEFT, left, Mapper.RIGHT_TO_LEFT));

        if (fourthWheel)
            pipeline.add(new Translation(SLOW, slow, Mapper.RIGHT_TO_LEFT));

        pipeline.add(new Translation(OTHER, reflector, Mapper.RIGHT_TO_LEFT));

        if (fourthWheel)
            pipeline.add(new Translation(SLOW, slow, Mapper.LEFT_TO_RIGHT));

        pipeline.add(new Translation(LEFT, left, Mapper.LEFT_TO_RIGHT));
        pipeline.add(new Translation(MIDDLE, middle, Mapper.LEFT_TO_RIGHT));
        pipeline.add(new Translation(RIGHT, right, Mapper.LEFT_TO_RIGHT));

        pipeline.add(new Translation(OTHER, plugboard, Mapper.LEFT_TO_RIGHT));
    }

    /**
     * Lockdown all the settings ready for translation. This involves building 
     * letter mappings as necessary, constructing needed Mappers, finalizing
     * ring settings and building the pipeline.
     */
    private void lockdownSettings() {
        lockdownPlugboard();
        lockdownReflector();

        plugboard = new Mapper("Plugboard", plugboardMap);
        reflector = new Mapper("Reflector", reflectorMap);

        Rotor rotor;
        for (int i = 0; i < ROTOR_COUNT; ++i) {
            rotor = getRotor(rotors, getWheelChoice(i));
            rotor.setRingSetting(getRingIndex(i));

            // rotor.dumpRightMap();
            // rotor.dumpLeftMap();
        }

        buildPipeline();
    }

    /**
     * Set the encipher state and lockdown all the data if we are about to 
     * translate keys.
     * @param state
     */
    public void setEncipher(boolean state) {
        // System.out.println("setEncipher(" + state + ").");
        encipher = state;
        if (encipher)
            lockdownSettings();
    }

    /**
     * Initialize "Translation" panel.
     */
    private void initializeEncipher() {
    }



    /************************************************************************
     * Support code for debug stuff.
     */

    public void dumpRotorWiring() {
        for (Rotor rotor : commercial)
            System.out.println(rotor.toString());
        System.out.println();
        for (Rotor rotor : rocket)
            System.out.println(rotor.toString());
        System.out.println();
        for (Rotor rotor : swissK)
            System.out.println(rotor.toString());
        System.out.println();
        for (Rotor rotor : m3)
            System.out.println(rotor.toString());
        System.out.println();
        for (Rotor rotor : m4)
            System.out.println(rotor.toString());
        System.out.println();
    }

    public int test1(char key) {
        updatePipeline();
        return translatePipeline(Rotor.charToIndex(key));
        // return translate(Rotor.charToIndex(key));
    }

    public int test5() {
        int output = 0;
        
        final String input = "AAAAA";
        for (int i = 0; i < input.length(); ++i)
            output = test1(input.charAt(i));

        return output;
    }

    public int test() {
        // dumpRotorWiring();
        // lockdownSettings();

        return test1('A');
        // return test5();
    }



}