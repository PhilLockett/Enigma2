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
package phillockett65.Enigma;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

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

    private boolean defaulted = false;
    public boolean isDefaulted() { return defaulted; }
    
    private Stage stage;
    public void setMainPos(double x, double y) { stage.setX(x); stage.setY(y); }
    public double getMainXPos() { return stage.getX(); }
    public double getMainYPos() { return stage.getY(); }



    /************************************************************************
     * General support code.
     */

    private int idToIndex(String id) { return Integer.valueOf(id); }

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
     * Constructor.
     */
    public Model() {
        initRotorWiring();

        initializeReflector();
        initializeRotorSetup();
        initializePlugboardConnections();
        initializeEncipher();
    }


    /**
     * Called by the controller after the constructor to initialise any 
     * objects after the controls have been initialised.
     */
    public void initialize() {
        // System.out.println("Model initialized.");
    }

    /**
     * Called by the controller after the stage has been set. Completes any 
     * initialization dependent on other components being initialized.
     */
    public void init(Stage stage) {
        // System.out.println("Model init.");
        this.stage = stage;
        if (!DataStore.readData(this))
            defaultSettings();
    }

    /**
     * Set all attributes to the default values.
     */
    public void defaultSettings() {
        defaulted = true;

        setReflectorChoice("Reflector B");

        setReconfigurable(false);
        pairs.clear();

        setFourthWheel(false);
        setRotorState(SLOW, "IV", 0, 0);
        setRotorState(LEFT, "I", 1, 0);
        setRotorState(MIDDLE, "II", 10, 20);
        setRotorState(RIGHT, "III", 1, 25);

        setUseNumbers(false);
        setShow(false);

        setExtPlugboard(false);
        plugs.clear();

        setEncipher(false);
    }



    /************************************************************************
     * Support code for Rotor definitions.
     */

    private ObservableList<RotorData> rotors = FXCollections.observableArrayList();
    private ObservableList<RotorData> reflectors = FXCollections.observableArrayList();

    private static final RotorData[] rotorData = {

        new RotorData("IC",     "DMTWSILRUYQNKFEJCAZBPGXOHV",	"1924",	"Commercial Enigma A, B", "R"),
        new RotorData("IIC",    "HQZGPJTMOBLNCIFDYAWVEUSRKX",	"1924",	"Commercial Enigma A, B", "F"),
        new RotorData("IIIC",   "UQNTLSZFMREHDPXKIBVYGJCWOA",	"1924",	"Commercial Enigma A, B", "W"),

        new RotorData("I-R",    "JGDQOXUSCAMIFRVTPNEWKBLZYH",	"7 February 1941",	"German Railway (Rocket)", "R"),
        new RotorData("II-R",   "NTZPSFBOKMWRCJDIVLAEYUXHGQ",	"7 February 1941",	"German Railway (Rocket)", "F"),
        new RotorData("III-R",  "JVIUBHTCDYAKEQZPOSGXNRMWFL",	"7 February 1941",	"German Railway (Rocket)", "W"),
        new RotorData("UKW-R",  "QYHOGNECVPUZTFDJAXWMKISRBL",	"7 February 1941",	"German Railway (Rocket)", ""),
        new RotorData("ETW-R",  "QWERTZUIOASDFGHJKPYXCVBNML",	"7 February 1941",	"German Railway (Rocket)", ""),

        new RotorData("I-K",    "PEZUOHXSCVFMTBGLRINQJWAYDK",	"February 1939",	"Swiss K", "R"),
        new RotorData("II-K",   "ZOUESYDKFWPCIQXHMVBLGNJRAT",	"February 1939",	"Swiss K", "F"),
        new RotorData("III-K",  "EHRVXGAOBQUSIMZFLYNWKTPDJC",	"February 1939",	"Swiss K", "W"),
        new RotorData("UKW-K",  "IMETCGFRAYSQBZXWLHKDVUPOJN",	"February 1939",	"Swiss K", ""),
        new RotorData("ETW-K",  "QWERTZUIOASDFGHJKPYXCVBNML",	"February 1939",	"Swiss K", ""),

        new RotorData("I",      "EKMFLGDQVZNTOWYHXUSPAIBRCJ",	"1930",	"Enigma I", "R"),
        new RotorData("II",     "AJDKSIRUXBLHWTMCQGZNPYFVOE",	"1930",	"Enigma I", "F"),
        new RotorData("III",    "BDFHJLCPRTXVZNYEIWGAKMUSQO",	"1930",	"Enigma I", "W"),
        new RotorData("IV",     "ESOVPZJAYQUIRHXLNFTGKDCMWB",	"December 1938",	"M3 Army", "K"),
        new RotorData("V",      "VZBRGITYUPSDNHLXAWMJQOFECK",	"December 1938",	"M3 Army", "A"),
        new RotorData("VI",     "JPGVOUMFYQBENHZRDKASXLICTW",	"1939",	"M3 & M4 Naval (FEB 1942)", "AN"),
        new RotorData("VII",    "NZJHGRCXMYSWBOUFAIVLPEKQDT",	"1939",	"M3 & M4 Naval (FEB 1942)", "AN"),
        new RotorData("VIII",   "FKQHTLXOCBJSPDZRAMEWNIUYGV",	"1939",	"M3 & M4 Naval (FEB 1942)", "AN"),

        new RotorData("Beta",               "LEYJVCNIXWPBQMDRTAKZGFUHOS",	"Spring 1941",	"M4 R2", ""),
        new RotorData("Gamma",              "FSOKANUERHMBTIYCWLQPZXVGJD",	"Spring 1942",	"M4 R2", ""),
        new RotorData("Reflector A",        "EJMZALYXVBWFCRQUONTSPIKHGD",	"",	"", ""),
        new RotorData("Reflector B",        "YRUHQSLDPXNGOKMIEBFZCWVJAT",	"",	"", ""),
        new RotorData("Reflector C",        "FVPJIAOYEDRZXWGCTKUQSBNMHL",	"",	"", ""),
        new RotorData("Reflector B Thin",   "ENKQAUYWJICOPBLMDXZVFTHRGS",	"1940",	"M4 R1 (M3 + Thin)", ""),
        new RotorData("Reflector C Thin",   "RDOBJNTKVEHMLFCWZAXGYIPSUQ",	"1940",	"M4 R1 (M3 + Thin)", ""),
        new RotorData("ETW",                "ABCDEFGHIJKLMNOPQRSTUVWXYZ",	"",	"Enigma I", ""),

    };

    /**
     * Construct all the Rotor collections.
     * 
     * Note: for the commercial, rocket and swissK Rotors, the turnover points 
     * are guesses and may be incorrect.
     */
    private void initRotorWiring() {

        // Build list of rotors and list of reflectors that can be selected.
        for (RotorData rotor : rotorData) {

            if (rotor.isReflector()) {
                reflectors.add(rotor);
                reflectorList.add(rotor.getId());
            } else {
                rotors.add(rotor);
                wheelList.add(rotor.getId());
            }
        }

    }



    /************************************************************************
     * Support code for "Reflector Set-Up" panel.
     */

    private ObservableList<String> reflectorList = FXCollections.observableArrayList();
    private String reflectorChoice;
    private boolean reconfigurable = false;
    
    private Pairs pairs;


    public ObservableList<String> getReflectorList()   { return reflectorList; }
    public String getReflectorChoice()   { return reflectorChoice; }
    public void setReflectorChoice(String choice)   { reflectorChoice = choice; }

    public void setReconfigurable(boolean state) { reconfigurable = state; }
    public boolean isReconfigurable() { return reconfigurable; }

    public void setPairText(int index, String text) { pairs.setText(index, text); }

    public int getPairCount() { return pairs.size(); }
    public String getPairText(int index)	{ return pairs.getText(index); }
    public int getPairCount(int index)		{ return pairs.getCount(index); }

    /**
     * Determine if the indexed pair is valid.
     * @param index of targeted pair.
     * @return true if the pair is valid, false otherwise.
     */
    public boolean isPairValid(int index) {
        if (!reconfigurable)
            return true;

        return pairs.isValid(index);
    }

    public void setPairText(String id, String text) { setPairText(idToIndex(id), text); }
    public String getPairText(String id)	{ return getPairText(idToIndex(id)); }
    public int getPairCount(String id)		{ return getPairCount(idToIndex(id)); }
    public boolean isPairValid(String id)	{ return isPairValid(idToIndex(id)); }

    /**
     * Determine if the reflector is valid.
     * @return true if the reflector is valid, false otherwise.
     */
    public boolean isReflectorValid() {
        if (reconfigurable)
            return pairs.isValid();

        return true;
    }


    /**
     * @return a reference to the active reflector map.
     */
    private int[] getReflectorMap() {
        
        if (reconfigurable) {
            return pairs.getMap();
        }
        
        RotorData rotor = getRotorData(reflectors, reflectorChoice);
        return rotor.getMap();
    }

    /**
     * Initialize "Reflector Set-Up" panel.
     */
    private void initializeReflector() {
        pairs = new Pairs(false);
    }



    /************************************************************************
     * Support code for "Rotor Set-Up" panel.
     */

    private ObservableList<String> wheelList = FXCollections.observableArrayList();

    private ArrayList<RotorControl> rotorControls = new ArrayList<RotorControl>(ROTOR_COUNT);

    private boolean fourthWheel = false;
    private boolean useNumbers = false;


    public ObservableList<String> getWheelList() { return wheelList; }

    public ArrayList<RotorControl> getRotorControls() { return rotorControls; }
    public int getRotorStateCount() { return rotorControls.size(); }

    public void setFourthWheel(boolean state) {
        fourthWheel = state;
        getState(SLOW).setDisable(!fourthWheel);
    }
    public boolean isFourthWheel() { return fourthWheel; }

    /**
     * Update useNumbers and synchronise the ring setting and rotor offset 
     * Spinners.
     * @param state assigned to useNumbers;
     */
    public void setUseNumbers(boolean state) {
        if (useNumbers == state)
            return;

        useNumbers = state;
        for (RotorControl rotor : rotorControls)
            rotor.setUseNumbers(useNumbers);
    }

    public boolean isUseNumbers() { return useNumbers; }
    

    public void setRotorState(int index, String wheelChoice, int ringIndex, int rotorIndex) { 
        getState(index).set(wheelChoice, ringIndex, rotorIndex); 
    }

    public void setTranslate(boolean selected) {
        for (RotorControl rotor : rotorControls)
            rotor.setLockDown(selected);
    }


    private RotorControl getState(int index) { return rotorControls.get(index); }

    public String getWheelChoice(int index) { return getState(index).getWheelChoice(); }
    public int getRingIndex(int index) { return getState(index).getRingIndex(); }
    public int getRotorIndex(int index) { return getState(index).getRotorIndex(); }
    private void incrementRotorOffset(int index, int step) { getState(index).increment(step); }

    private Rotor buildNewRotor(int id) {
        return new Rotor(getRotorData(rotors, getWheelChoice(id)), getRingIndex(id));
    }
    
    private Rotor getActiveRotor(int id) { return activeRotors.get(id); }
    
    private void setActiveRotorOffset(int id) { getActiveRotor(id).setOffset(getRotorIndex(id)); }

    /**
     * Initialize "Rotor Set-Up".
     */
    private void initializeRotorSetup() {
        // Initialize "Rotor Control Set-Up".
        for (int i = 0; i < ROTOR_COUNT; ++i) {
            RotorControl rotorControl = new RotorControl();
            rotorControl.init(i, wheelList);
            rotorControl.setSpacing(8);

            rotorControls.add(rotorControl);
        }
    }



    /************************************************************************
     * Support code for "Plugboard Connections" panel.
     */
    
    private boolean extPlugboard = false;

    private Pairs plugs;


    public void setExtPlugboard(boolean state) { 
        extPlugboard = state;
        for (int i = PLUG_COUNT; i < FULL_COUNT; ++i)
            plugs.setEnabled(i, state);

        plugs.countLetterUsage();
    }
    public boolean isExtPlugboard() { return extPlugboard; }

    public void setPlugText(int index, String text) { plugs.setText(index, text); }

    public int getPlugCount() { return plugs.size(); }
    public int getActivePlugCount() { return extPlugboard ? plugs.size() : PLUG_COUNT; }
    public String getPlugText(int index)	{ return plugs.getText(index); }
    public int getPlugCount(int index)		{ return plugs.getCount(index); }

    public boolean isPlugValid(int index) { return plugs.isValid(index); }

    public void setPlugText(String id, String text) { setPlugText(idToIndex(id), text); }
    public String getPlugText(String id)	{ return getPlugText(idToIndex(id)); }
    public int getPlugCount(String id)		{ return getPlugCount(idToIndex(id)); }
    public boolean isPlugValid(String id)	{ return isPlugValid(idToIndex(id)); }

    public boolean isPlugboardValid() { return plugs.isValid(); }


    /**
     * Lockdown the plugboardMap.
     */
    private int[] getPlugboardMap() {
        return plugs.getMap();
    }

    /**
     * Initialize "Plugboard Connections" panel.
     */
    private void initializePlugboardConnections() {
        plugs = new Pairs(true);
    }



    /************************************************************************
     * Support code for "Translation" panel.
     */

    private boolean encipher = false;
    private boolean show = false;

    private ArrayList<Rotor> activeRotors = new ArrayList<Rotor>();
    private ArrayList<Translation> pipeline = new ArrayList<Translation>();

    public boolean isShow() { return show; }
    public void setShow(boolean state) { show = state; }


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
     * Find a RotorData with the given id in the given list,
     * @param list of Rotors to search.
     * @param target id of Rotor.
     * @return Rotor with matching id if found, null otherwise.
     */
    private RotorData getRotorData(ObservableList<RotorData> list, String target) {
        for (RotorData rotor : list)
             if (rotor.is(target))
                return rotor;

        return list.get(0);
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

        Rotor rotor = getActiveRotor(MIDDLE);
        if (rotor.isNotchPoint(getRotorIndex(MIDDLE))) {
            // Double step of the spinner of the middle rotor, normal step of 
            // the spinner of the left rotor.
            incrementRotorOffset(MIDDLE, 1);
            incrementRotorOffset(LEFT, 1);
        }

        rotor = getActiveRotor(RIGHT);
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
        private final Mapper mapper;
        private final int dir;

        public Translation(Mapper mapper, int dir) {
            this.mapper = mapper;
            this.dir = dir;
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
            setActiveRotorOffset(i);
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

        Mapper plugboard = new Mapper("Plugboard", getPlugboardMap());
        Mapper reflector = new Mapper("Reflector", getReflectorMap());

        Rotor slow = getActiveRotor(SLOW);

        Rotor left = getActiveRotor(LEFT);
        Rotor middle = getActiveRotor(MIDDLE);
        Rotor right = getActiveRotor(RIGHT);

        pipeline.add(new Translation(plugboard, Mapper.RIGHT_TO_LEFT));

        pipeline.add(new Translation(right, Mapper.RIGHT_TO_LEFT));
        pipeline.add(new Translation(middle, Mapper.RIGHT_TO_LEFT));
        pipeline.add(new Translation(left, Mapper.RIGHT_TO_LEFT));

        if (fourthWheel)
            pipeline.add(new Translation(slow, Mapper.RIGHT_TO_LEFT));

        pipeline.add(new Translation(reflector, Mapper.RIGHT_TO_LEFT));

        if (fourthWheel)
            pipeline.add(new Translation(slow, Mapper.LEFT_TO_RIGHT));

        pipeline.add(new Translation(left, Mapper.LEFT_TO_RIGHT));
        pipeline.add(new Translation(middle, Mapper.LEFT_TO_RIGHT));
        pipeline.add(new Translation(right, Mapper.LEFT_TO_RIGHT));

        pipeline.add(new Translation(plugboard, Mapper.LEFT_TO_RIGHT));
    }

    /**
     * Lockdown all the settings ready for translation. This involves building 
     * letter mappings as necessary, constructing needed Mappers, finalizing
     * ring settings and building the pipeline.
     */
    private void lockdownSettings() {

        activeRotors.clear();
        for (int i = 0; i < ROTOR_COUNT; ++i) {
            Rotor rotor = buildNewRotor(i);
            activeRotors.add(rotor);

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
        for (RotorData rotor : rotorData)
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