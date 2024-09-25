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
 * Boilerplate code responsible for launching the JavaFX application. 
 */
package phillockett65.Enigma;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;


public class RotorControl extends VBox {

    private final static String[] names = { "fourth", "left", "middle", "right" };

    private ChoiceBox<String> wheelChoicebox;
    private Spinner<String> ringSettingSpinner;
    private Spinner<String> rotorOffsetSpinner;

    private ObservableList<String> wheelList = FXCollections.observableArrayList();
    private ObservableList<String> letters = FXCollections.observableArrayList();
    private ObservableList<String> numbers = FXCollections.observableArrayList();
    private ObservableList<String> ringList = FXCollections.observableArrayList();
    private boolean useNumbers = false;
    private int id = 0;

    private SpinnerValueFactory<String> ringSettingSVF;
    private SpinnerValueFactory<String> rotorOffsetSVF;

    public RotorControl() {
        super();
        // System.out.println("CustomRotorControl()");

        setSpacing(8);
        wheelChoicebox = new ChoiceBox<String>();
        ringSettingSpinner = new Spinner<String>();
        rotorOffsetSpinner = new Spinner<String>();

        getChildren().addAll(wheelChoicebox, ringSettingSpinner, rotorOffsetSpinner);

        // Initialize "letters", "numbers" and "ringList" ObservableList.
        for (int i = 0; i < 26; ++i) {
            final String letter = Rotor.indexToLetter(i);
            letters.add(letter);
            ringList.add(letter);

            numbers.add(String.valueOf(i + 1));
        }
    }

    public void init(int i, ObservableList<String> wheelList) {
        // System.out.println("init()");

        id = i;
        String name = names[id];
        this.wheelList.setAll(wheelList);

        // Initialize "Rotor Selection" choice box.
        wheelChoicebox.setItems(wheelList);
        wheelChoicebox.setValue(wheelList.get(0));
        wheelChoicebox.setTooltip(new Tooltip("Select the " + name + " Rotor"));

        // Initialize "Ring Settings" spinner.
        ringSettingSVF = new SpinnerValueFactory.ListSpinnerValueFactory<String>(ringList);
        ringSettingSpinner.setValueFactory(ringSettingSVF);
        ringSettingSpinner.getValueFactory().wrapAroundProperty().set(true);
        ringSettingSpinner.setTooltip(new Tooltip("Select Ring Setting for the " + name + " Rotor"));

        // Initialize "Rotor Offsets" spinner.
        rotorOffsetSVF = new SpinnerValueFactory.ListSpinnerValueFactory<String>(ringList);
        rotorOffsetSpinner.setValueFactory(rotorOffsetSVF);
        rotorOffsetSpinner.getValueFactory().wrapAroundProperty().set(true);
        rotorOffsetSpinner.setTooltip(new Tooltip("Select offset for the " + name + " Rotor"));
    }

    private int valueToIndex(String s) { return useNumbers ? Mapper.numberToIndex(s) : Mapper.letterToIndex(s); }

    public ChoiceBox<String> getWheelChoicebox() { return wheelChoicebox; }
    
    public Spinner<String> getRotorOffsetSpinner() { return rotorOffsetSpinner; };
    
    public Spinner<String> getRingSettingSpinner() { return ringSettingSpinner; }

    /**
     * Display Numbers or Letters on rotor (spinner).
     * @param state show numbers if true, letters if false.
     */
    public void setUseNumbers(boolean state) {
        if (useNumbers == state)
            return;

        useNumbers = state;
        if (useNumbers)
            ringList.setAll(numbers);
        else
            ringList.setAll(letters);
    }

    public boolean isUseNumbers() { return useNumbers; }

    public String getWheelChoice() { return wheelChoicebox.getValue(); }
    public void setWheelChoice(String value) { wheelChoicebox.setValue(value); }

    private String getRingValue() { return ringSettingSVF.getValue(); }
    public int getRingIndex() { return valueToIndex(getRingValue()); }
    private void setRingValue(String value) { ringSettingSVF.setValue(value); }
    public void setRingIndex(int index) { setRingValue(ringList.get(index % 26)); }

    private String getRotorValue() { return rotorOffsetSVF.getValue(); }
    public int getRotorIndex() { return valueToIndex(getRotorValue()); }
    private void setRotorValue(String value) { rotorOffsetSVF.setValue(value); }
    public void setRotorIndex(int index) { setRotorValue(ringList.get(index % 26)); }
    public void increment(int steps) { rotorOffsetSVF.increment(steps); }
    
    /**
     * Set up the custom controller
     * @param wheel selected.
     * @param ringIndex for the ring setting.
     * @param rotorIndex for the rotor start position.
     */
    public void set(String wheel, int ringIndex, int rotorIndex) {
        setWheelChoice(wheel);
        setRingIndex(ringIndex);
        setRotorIndex(rotorIndex);
    }

    /**
     * Lock down current settings.
     * @param state locked down if true, editable if false.
     */
    public void setLockDown(boolean state) {
        wheelChoicebox.setDisable(state);
        ringSettingSpinner.setDisable(state);
    }

}