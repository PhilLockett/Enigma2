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
import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

public class Model {

    private final static String DATAFILE = "Settings.dat";

    public final static int FULL_COUNT = 13;
    public final static int PLUG_COUNT = 10;
    public final static int PAIR_COUNT = 12;

    private static final int SLOW = 0;
    private static final int LEFT = 1;
    private static final int MIDDLE = 2;
    private static final int RIGHT = 3;
    private static final int ROTOR_COUNT = 4;

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
        initDefaultSettings();

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

    public String getTitle() { return stage.getTitle(); }

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

    public void dailySettings(int date) {

        SettingsData settings = keyList649[date];

        ArrayList<String> list = Mapper.splitWords(settings.getReflector());
        initPairText(list);
        setReconfigurable(true);

        setFourthWheel(false);

        for (int i = 0; i < ROTOR_COUNT; ++i) {
            setRotorState(i, settings.getRotor(i), settings.getRingSetting(i), settings.getOffset(i, 0));
        }

        list = Mapper.splitWords(settings.getPlugboard());
        initPlugText(list);
        buildNewPlugboard();
    }



    /************************************************************************
     * Support code for Rotor definitions.
     */
    private HashMap<String, RotorData> rotors = new HashMap<>();
    private HashMap<String, RotorData> reflectors = new HashMap<>();

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
            String id = rotor.getId();

            if (rotor.isReflector()) {
                reflectors.put(id, rotor);
                reflectorList.add(id);
            } else {
                rotors.put(id, rotor);
                wheelList.add(id);
            }
        }

    }



    /************************************************************************
     * Support code for the monthly key list data number 649.
     *   https://en.wikipedia.org/wiki/Enigma_machine#Details
     */

    private HashMap<String, String> reflectors649 = new HashMap<>();
    private SettingsData[] keyList649 = new SettingsData[32];

    private void addToReflectors649(String name, String pairString) {
        reflectors649.put(name, pairString);
    }
    private void initRef649Reflector() {
        addToReflectors649("Ref649-1",  "IL AP EU HO QT WZ KV GM BF NR DX CS");
        addToReflectors649("Ref649-9",  "AI BT MV HU FW EL DG KN RZ OQ CP SX");
        addToReflectors649("Ref649-17", "IU AS DV GL FT OX EZ CH MR KN BQ PW");
        addToReflectors649("Ref649-25", "KM AX FZ GO DI CN BR PV LT EQ HS UW");
     }
 
    private void addToKeyList649(int day, String wheels, int r1, int r2, int r3, 
        String ref, String plugs, String indicator) {

        String reflector = reflectors649.get(ref);
        keyList649[day] = new SettingsData(wheels, r1, r2, r3, reflector, plugs, indicator);
    }
    private void initKeyList649() {
        addToKeyList649(31, "I V III",    14, 9, 24,  "Ref649-25",    "SZ GT DV KU FO MY EW JN IX LQ", "wny dgy ekb rzg");
        addToKeyList649(30, "IV III II",  5, 26, 2,   "Ref649-25",    "IS EV MX RW DT UZ JQ AO CH NY", "ktl acw zci wzo");
        addToKeyList649(29, "III II I",   2, 24, 3,   "Ref649-25",    "DJ AT CV IO ER QS LW PZ FN BH", "ioc acn ovw wvc");
        addToKeyList649(28, "II III V",   6, 8, 16,   "Ref649-25",    "CR FV AI DK OT MQ EU BX LP GJ", "lrb cld ude rzh");
        addToKeyList649(27, "III I IV",   11, 3, 7,   "Ref649-25",    "DY IN BV GR AM LO FP HT EX UW", "woj fbh vct uis");
        addToKeyList649(26, "I IV V",     17, 22, 19, "Ref649-25",    "VZ AL RT KO CG EI BJ DU FS HP", "xle gbo uev rxm");
        addToKeyList649(25, "IV III I",   8, 25, 12,  "Ref649-25",    "OR PV AD IT FK HJ LZ NS EQ CW", "ouc uhq uew uit");
        addToKeyList649(24, "V I IV",     5, 18, 14,  "Ref649-17",    "TY AS OW KV JM DR HX GL CZ NU", "kpl rwl vci tlq");
        addToKeyList649(23, "IV II I",    24, 12, 4,  "Ref649-17",    "QV FR AK EO DH CJ MZ SX GN LT", "ebn rwm udf tlo");
        addToKeyList649(22, "II IV V",    1, 9, 21,   "Ref649-17",    "FJ ES IM RX LV AY OU BG WZ CN", "jrc acx mwe wve");
        addToKeyList649(21, "I V II",     13, 5, 19,  "Ref649-17",    "RU HL FY OS GZ DM AW CE TV NX", "jpw del mwf wvf");
        addToKeyList649(20, "III IV V",   24, 1, 10,  "Ref649-17",    "DF MO QZ AU RY SV JL GX BE TW", "jqd cef nvo ysh");
        addToKeyList649(19, "V III I",    17, 25, 20, "Ref649-17",    "OX PR FH WY DL CM AE TZ JS GI", "idf fpx jwg tlg");
        addToKeyList649(18, "IV II V",    15, 23, 26, "Ref649-17",    "EJ OY IV AQ KW FX MT PS LU BD", "lsa zbw vcj rxn");
        addToKeyList649(17, "I IV II",    21, 10, 6,  "Ref649-17",    "IR KZ LS EM OV GY QX AF JP BU", "mae hzi sog ysi");
        addToKeyList649(16, "V II III",   8, 16, 13,  "Ref649-9",     "HM JO DI NR BY XZ GS PU FQ CT", "tdp dhb fkb uiv");
        addToKeyList649(15, "II IV I",    1, 3, 7,    "Ref649-9",     "DS HY MR GW LX AJ BQ CO IP NT", "ldw hzj soh wvg");
        addToKeyList649(14, "IV I V",     15, 11, 5,  "Ref649-9",     "GM JR KS IY HZ PL AX BT CQ NV", "imz noa tjv xtk");
        addToKeyList649(13, "I III II",   13, 20, 3,  "Ref649-9",     "LY AG KM BR IQ JU HV SW ET CX", "zgr dgz gjo ryq");
        addToKeyList649(12, "V II IV",    18, 10, 7,  "Ref649-9",     "MU BP CY RZ KX AN JT DG IL FW", "zdy rkf tjw xtl");
        addToKeyList649(11, "II IV III",  2, 26, 15,  "Ref649-9",     "KN UY HR PW FM BO EZ QT DX JV", "zea rjy soi wvh");
        addToKeyList649(10, "III V IV",   23, 21, 1,  "Ref649-9",     "LR IK MS QU HW PT GO VX FZ EN", "lrc zbx vbm rxo");
        addToKeyList649( 9, "V I III",    16, 4, 8,   "Ref649-9",     "QY BS LN KT AP IU DW HO RV JZ", "edj eyr vby tlh");
        addToKeyList649( 8, "IV II V",    13, 19, 25, "Ref649-1",     "FI NQ SY CU BZ AH EL TX DO KP", "yiz dha ekc tli");
        addToKeyList649( 7, "I IV II",    9, 3, 22,   "Ref649-1",     "UX IZ HN BK GQ CP FT JY MW AR", "lan dgb zsj wbi");
        addToKeyList649( 6, "III I V",    11, 18, 14, "Ref649-1",     "DQ GU BW NP HK AZ CI FO JX VY", "lao cft zsk wbj");
        addToKeyList649( 5, "V II IV",    23, 2, 25,  "Ref649-1",     "MV CL GK OQ BI FU HS PX NW EY", "lju cdr iye waj");
        addToKeyList649( 4, "II IV I",    4, 21, 9,   "Ref649-1",     "AC BL OZ EK QW GP SU DH JM TX", "lsb zby vcy ujb");
        addToKeyList649( 3, "V I II",     19, 11, 6,  "Ref649-1",     "KR MP CN BF EH DZ IW AV GJ LO", "lap owd iwu wak");
        addToKeyList649( 2, "IV V I",     16, 14, 2,  "Ref649-1",     "BN HU EG PY KQ CF OS JW AI VZ", "aqd bdy iyf xtd");
        addToKeyList649( 1, "II I III",   23, 12, 10, "Ref649-1",     "DP BM NZ CK GV HQ AF UY SW JO", "kgl cdf giq wuv");
    }

    /**
     * Construct all the monthly key list data.
     */
    private void initDefaultSettings() {
        initRef649Reflector();
        initKeyList649();
    }

     /************************************************************************
     * Support code for "Reflector Set-Up" panel.
     */

    private ObservableList<String> reflectorList = FXCollections.observableArrayList();
    private String reflectorChoice;
    private boolean reconfigurable = false;
    
    private Pairs pairs;
    private Mapper reflector;


    public ObservableList<String> getReflectorList()   { return reflectorList; }
    public String getReflectorChoice()   { return reflectorChoice; }
    public void setReflectorChoice(String choice)   { reflectorChoice = choice; }

    public void setReconfigurable(boolean state) { reconfigurable = state; }
    public boolean isReconfigurable() { return reconfigurable; }

    public void initPairText(ArrayList<String> links) {
        pairs.setLinks(links);
    }

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

    private void buildNewReflector() {
        int[] reflectorMap;

        if (reconfigurable) {
            reflectorMap = pairs.getMap();
        } else {
            RotorData rotor = getRotorData(reflectors, reflectorChoice);
            reflectorMap = rotor.getMap();
        }

        reflector = new Mapper("Reflector", reflectorMap);
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
    private ArrayList<Rotor> activeRotors = new ArrayList<Rotor>(ROTOR_COUNT);

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
    

    private RotorControl getState(int index) { return rotorControls.get(index); }

    public void setRotorState(int index, String wheelChoice, int ringIndex, int rotorIndex) { 
        getState(index).set(wheelChoice, ringIndex, rotorIndex); 
    }

    public void setTranslate(boolean selected) {
        for (RotorControl rotor : rotorControls)
            rotor.setLockDown(selected);
    }


    public String getWheelChoice(int index) { return getState(index).getWheelChoice(); }
    public int getRingIndex(int index) { return getState(index).getRingIndex(); }
    public int getRotorIndex(int index) { return getState(index).getRotorIndex(); }
    private void incrementRotorOffset(int index, int step) { getState(index).increment(step); }

    private Rotor buildNewRotor(int id) {
        return new Rotor(getRotorData(rotors, getWheelChoice(id)), getRingIndex(id));
    }

    private Rotor getActiveRotor(int id) { return activeRotors.get(id); }

    private void addActiveRotorEntry(int id) { activeRotors.add(buildNewRotor(id)); }
    private void updateActiveRotorEntry(int id) { activeRotors.set(id, buildNewRotor(id)); }
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
    private Mapper plugboard;


    public void setExtPlugboard(boolean state) { 
        extPlugboard = state;
        for (int i = PLUG_COUNT; i < FULL_COUNT; ++i)
            plugs.setEnabled(i, state);

        plugs.countLetterUsage();
    }
    public boolean isExtPlugboard() { return extPlugboard; }

    public void initPlugText(ArrayList<String> links) {
        plugs.setLinks(links);
    }

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

    private void buildNewPlugboard() {
        plugboard = new Mapper("Plugboard", plugs.getMap());
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
    private ObservableList<Integer> settingsList = FXCollections.observableArrayList();

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

    public ObservableList<Integer> getSettingsList()   { return settingsList; }

    /**
     * Find a RotorData with the given id in the given list,
     * @param list of Rotors to search.
     * @param target id of Rotor.
     * @return RotorData with matching id if found, entry 0 otherwise.
     */
    private RotorData getRotorData(HashMap<String, RotorData> list, String target) {
        return list.get(target);
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
     * Update the Rotor Offsets.
     */
    private void updateRotorOffsets() {
        for (int i = 0; i < ROTOR_COUNT; ++i) {
            setActiveRotorOffset(i);
        }
    }


    private int mapperTranslate(int index, Mapper mapper, int dir) {
        return mapper.swap(dir, index, isShow());
    }
    private int mapperTranslate(int index, int id, int dir) {
        return getActiveRotor(id).swap(dir, index, isShow());
    }

    /**
     * Translates an index (numerical equivalent of the letter) to another for 
     * every active Mapper.
     * @param index to translate.
     * @return the translated index.
     */
    private int translateIndex(int index) {
        if (isShow())
            System.out.print("Key: " + Mapper.indexToLetter(index) + "  ");

        index = mapperTranslate(index, plugboard, Mapper.RIGHT_TO_LEFT);

        index = mapperTranslate(index, RIGHT, Mapper.RIGHT_TO_LEFT);
        index = mapperTranslate(index, MIDDLE, Mapper.RIGHT_TO_LEFT);
        index = mapperTranslate(index, LEFT, Mapper.RIGHT_TO_LEFT);

        if (fourthWheel)
            index = mapperTranslate(index, SLOW, Mapper.RIGHT_TO_LEFT);

        index = mapperTranslate(index, reflector, Mapper.RIGHT_TO_LEFT);

        if (fourthWheel)
            index = mapperTranslate(index, SLOW, Mapper.LEFT_TO_RIGHT);

        index = mapperTranslate(index, LEFT, Mapper.LEFT_TO_RIGHT);
        index = mapperTranslate(index, MIDDLE, Mapper.LEFT_TO_RIGHT);
        index = mapperTranslate(index, RIGHT, Mapper.LEFT_TO_RIGHT);

        index = mapperTranslate(index, plugboard, Mapper.LEFT_TO_RIGHT);

        if (isShow())
            System.out.println("Lamp: " + Mapper.indexToLetter(index));

        return index;
    }


    /**
     * Advance the Rotors and translate an index (numerical equivalent of the 
     * letter) through the pipeline.
     * @param index to translate.
     * @return the translated index.
     */
    public int translate(int index) {
        advanceRotors();
        updateRotorOffsets();
        return translateIndex(index);
    }


    /**
     * Lockdown all the settings ready for translation. This involves building 
     * letter mappings as necessary, constructing needed Mappers, finalizing
     * ring settings and building the pipeline.
     */
    private void lockdownSettings() {
        buildNewPlugboard();
        buildNewReflector();

        for (int i = 0; i < ROTOR_COUNT; ++i) {
            updateActiveRotorEntry(i);
        }
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


    private void buildTheMappers() {
        for (int i = 0; i < ROTOR_COUNT; ++i) {
            addActiveRotorEntry(i);
            setActiveRotorOffset(i);
        }
    }

    private void initSettingsList() {
        final int max = keyList649.length;
        for (int i = 0; i < max; ++i) {
            if (keyList649[i] != null)
                settingsList.add(i);
        }
    }

    /**
     * Initialize "Translation" panel.
     */
    private void initializeEncipher() {
        buildTheMappers();
        initSettingsList();
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
        updateRotorOffsets();
        return translateIndex(Rotor.charToIndex(key));
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