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
 * PrimaryController is the class that is responsible for centralizing control.
 * It is instantiated by the FXML loader creates the Model.
 * 
 * Each handler should always update the model with the changed data first, 
 * then act on the new state of the model.
 * 
 * The order of the code should match the order of the layout when possible.
 */
package phillockett65.Enigma;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class PrimaryController {

    private Model model;



    /************************************************************************
     * General support code.
     */

    private static final String ERROR = "error-text-field";

    /**
     * Use CSS to indicate if the contents of a TextField is valid.
     * @param field to adjust the appearance of.
     * @param valid status of TextField.
     */
    private void setValidTextField(TextField field, boolean valid) {

        if (valid) {
            field.getStyleClass().remove(ERROR);
        } else {
            if (!field.getStyleClass().contains(ERROR))
                field.getStyleClass().add(ERROR);
        }
        
    }



    /************************************************************************
     * Support code for the Initialization of the Controller.
     */

    /**
     * Responsible for constructing the Model and any local objects. Called by 
     * the FXMLLoader().
     */
    public PrimaryController() {
        // System.out.println("PrimaryController constructed.");
        model = new Model();
    }

    /**
     * Called by the FXML mechanism to initialize the controller. Called after 
     * the constructor to initialise all the controls.
     */
    @FXML public void initialize() {
        // System.out.println("PrimaryController initialized.");
        model.initialize();

        initializeReflector();
        initializeRotorSetup();
        initializePlugboardConnections();
        initializeEncipher();
    }

    /**
     * Called by Application after the stage has been set. Completes any 
     * initialization dependent on other components being initialized.
     */
    public void init() {
        // System.out.println("PrimaryController init.");
        model.init();
        syncUI();
    }

    /**
     * Called by Application on shutdown.
     */
    public void saveState() {
        DataStore.writeData(model);
    }

    /**
     * Synchronise all controls with the model. This should be the last step 
     * in the initialisation.
     */
    public void syncUI() {
        reflectorChoicebox.setValue(model.getReflectorChoice());
        reconfigurableCheckbox.setSelected(model.isReconfigurable());
        for (int i = 0; i < pairs.size(); ++i) {
            TextField pair = pairs.get(i);
            pair.setText(model.getPairText(i));
        }

        wheel0Choicebox.setValue(model.getWheelChoice(0));
        wheel1Choicebox.setValue(model.getWheelChoice(1));
        wheel2Choicebox.setValue(model.getWheelChoice(2));
        wheel3Choicebox.setValue(model.getWheelChoice(3));

        fourthWheelCheckbox.setSelected(model.isFourthWheel());
        useLettersCheckbox.setSelected(model.isUseLetters());
        showStepsCheckbox.setSelected(model.isShow());

        for (int i = 0; i < plugs.size(); ++i) {
            TextField plug = plugs.get(i);
            plug.setText(model.getPlugText(i));
        }
        encipherButton.setSelected(model.isEncipher());
    }



    /************************************************************************
     * Support code for "Reflector Set-Up" panel.
     */

    @FXML
    private TitledPane reflectorSetUpTitledPane;
  
    @FXML
    private ChoiceBox<String> reflectorChoicebox;

    @FXML
    private CheckBox reconfigurableCheckbox;

    @FXML
    void reconfigurableCheckboxActionPerformed(ActionEvent event) {
        model.setReconfigurable(reconfigurableCheckbox.isSelected());
        setReconfigurable();
        syncEncipherButton();
    }

    @FXML
    private TextField pair0;

    @FXML
    private TextField pair1;

    @FXML
    private TextField pair2;

    @FXML
    private TextField pair3;

    @FXML
    private TextField pair4;

    @FXML
    private TextField pair5;

    @FXML
    private TextField pair6;

    @FXML
    private TextField pair7;

    @FXML
    private TextField pair8;

    @FXML
    private TextField pair9;

    @FXML
    private TextField pair10;

    @FXML
    private TextField pair11;

    private ArrayList<TextField> pairs = new ArrayList<TextField>(Model.PAIR_COUNT);

    @FXML
    void reflectorKeyTyped(KeyEvent event) {
        TextField field = (TextField)event.getSource();
        // System.out.println("reflectorKeyTyped(" + field.getId() + ", " + field.getText() + ")");

        model.setPairText(field.getId(), field.getText());

        checkReflector();
        syncEncipherButton();
    }

    /**
     * Indicate error state of each reflector pair based on it's validity.
     */
    private void checkReflector() {
        // System.out.println("checkReflector()");

        for (int i = 0; i < pairs.size(); ++i) {
            final Boolean valid = model.isPairValid(i);
            setValidTextField(pairs.get(i), valid);
        }
    }

    /**
     * Control whether it is possible to change the reflector pairs.
     * @param editable indicates if the reflector pairs are editable.
     */
    private void editablePairs(boolean editable) {
        // System.out.println("editablePairs(" + editable + ")");

        for (TextField field : pairs)
            field.setEditable(editable);
    }

    /**
     * Control whether it is possible to change the reflector set-up.
     * @param editable indicates if the reflector set-up is editable.
     */
    private void editableReflector(boolean editable) {
        // System.out.println("editableReflector(" + editable + ")");

        if (editable) {
            setReconfigurable();
        } else {
            reflectorChoicebox.setDisable(true);
            editablePairs(false);
        }
        reconfigurableCheckbox.setDisable(!editable);
    }

    /**
     * Switch between being able to select a hard-wired reflector or set-up a 
     * reconfigurable reflector depending on isReconfigurable().
     */
    private void setReconfigurable() {
        final boolean reconfigurable = model.isReconfigurable();

        reflectorChoicebox.setDisable(reconfigurable);
        editablePairs(reconfigurable);
    }

    /**
     * Initialize "Reflector" panel.
     */
    private void initializeReflector() {
        reflectorSetUpTitledPane.setTooltip(new Tooltip("Select which Reflector (reversing drum) to use"));
        reconfigurableCheckbox.setSelected(model.isReconfigurable());
        reconfigurableCheckbox.setTooltip(new Tooltip("Select to set up and use a reconfigurable Reflector"));
        setReconfigurable();

        reflectorChoicebox.setItems(model.getReflectorList());
        reflectorChoicebox.setTooltip(new Tooltip("Select a Reflector"));

        reflectorChoicebox.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
            model.setReflectorChoice(newValue);
        });

        pairs.add(pair0);
        pairs.add(pair1);
        pairs.add(pair2);
        pairs.add(pair3);
        pairs.add(pair4);
        pairs.add(pair5);
        pairs.add(pair6);
        pairs.add(pair7);
        pairs.add(pair8);
        pairs.add(pair9);
        pairs.add(pair10);
        pairs.add(pair11);

        for (int i = 0; i < pairs.size(); ++i) {
            String id = String.valueOf(i);
            TextField pair = pairs.get(i);
            pair.setId(id);         // Use id as an index.
            pair.setTooltip(new Tooltip("Configure unique loop-back wiring pair"));

            setValidTextField(pair, model.isPairValid(i));

            pair.setTextFormatter(new TextFormatter<>(change -> {

                if (change.isAdded()) {
                    if (change.getText().matches("[a-z]*")) {
                        String text = change.getText().toUpperCase();
                        change.setText(text);
                    } else if (!change.getText().matches("[A-Z]*")) {
                        return null;
                    }
                }

                return change;
            }));
        }
    }



     /************************************************************************
     * Support code for "Rotor Selection" panel.
     */

    @FXML
    private TitledPane rotorSelectionTitledPane;

    @FXML
    private ChoiceBox<String> wheel0Choicebox;

    @FXML
    private ChoiceBox<String> wheel1Choicebox;

    @FXML
    private ChoiceBox<String> wheel2Choicebox;

    @FXML
    private ChoiceBox<String> wheel3Choicebox;

    /**
     * Control whether it is possible to change the rotor selection. Note 
     * wheel0Choicebox is controlled seperately in editableFourthWheel().
     * @param editable indicates if the rotor selections are editable.
     */
    private void editableWheelOrder(boolean editable) {
        // System.out.println("editableReflector(" + editable + ")");

        fourthWheelCheckbox.setDisable(!editable);

        wheel1Choicebox.setDisable(!editable);
        wheel2Choicebox.setDisable(!editable);
        wheel3Choicebox.setDisable(!editable);
    }

    /**
     * Initialize "Rotor Selection" panel.
     */
    private void initializeWheelOrder() {
        rotorSelectionTitledPane.setTooltip(new Tooltip("Select the Rotors to use"));

        wheel0Choicebox.setItems(model.getWheelList());
        wheel1Choicebox.setItems(model.getWheelList());
        wheel2Choicebox.setItems(model.getWheelList());
        wheel3Choicebox.setItems(model.getWheelList());

        wheel0Choicebox.setTooltip(new Tooltip("Select the fourth Rotor"));
        wheel1Choicebox.setTooltip(new Tooltip("Select the left Rotor"));
        wheel2Choicebox.setTooltip(new Tooltip("Select the middle Rotor"));
        wheel3Choicebox.setTooltip(new Tooltip("Select the right Rotor"));

        wheel0Choicebox.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
            model.setWheelChoice(0, newValue);
        });

        wheel1Choicebox.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
            model.setWheelChoice(1, newValue);
        });

        wheel2Choicebox.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
            model.setWheelChoice(2, newValue);
        });

        wheel3Choicebox.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
            model.setWheelChoice(3, newValue);
        });
    }



    /************************************************************************
     * Support code for "Ring Settings" panel.
     */

    @FXML
    private TitledPane ringSettingsTitledPane;

    @FXML
    private Spinner<String> ringSetting0Spinner;

    @FXML
    private Spinner<String> ringSetting1Spinner;

    @FXML
    private Spinner<String> ringSetting2Spinner;

    @FXML
    private Spinner<String> ringSetting3Spinner;


    /**
     * Control whether it is possible to change the ring settings. Note 
     * ringSetting0Spinner is controlled seperately in editableFourthWheel().
     * @param editable indicates if the ring settings are editable.
     */
    private void editableRingSettings(boolean editable) {
        // System.out.println("editableRingSettings(" + editable + ")");

        ringSetting1Spinner.setDisable(!editable);
        ringSetting2Spinner.setDisable(!editable);
        ringSetting3Spinner.setDisable(!editable);
    }

    /**
     * Initialize "Ring Settings" panel.
     */
    private void initializeRingSettings() {
        ringSettingsTitledPane.setTooltip(new Tooltip("Select Ring Setting (internal wiring offset) for the Rotors"));

        ringSetting0Spinner.setValueFactory(model.getRingSettingSVF(0));
        ringSetting1Spinner.setValueFactory(model.getRingSettingSVF(1));
        ringSetting2Spinner.setValueFactory(model.getRingSettingSVF(2));
        ringSetting3Spinner.setValueFactory(model.getRingSettingSVF(3));

        ringSetting0Spinner.getValueFactory().wrapAroundProperty().set(true);
        ringSetting1Spinner.getValueFactory().wrapAroundProperty().set(true);
        ringSetting2Spinner.getValueFactory().wrapAroundProperty().set(true);
        ringSetting3Spinner.getValueFactory().wrapAroundProperty().set(true);

        ringSetting0Spinner.setTooltip(new Tooltip("Select Ring Setting for the fourth Rotor"));
        ringSetting1Spinner.setTooltip(new Tooltip("Select Ring Setting for the left Rotor"));
        ringSetting2Spinner.setTooltip(new Tooltip("Select Ring Setting for the middle Rotor"));
        ringSetting3Spinner.setTooltip(new Tooltip("Select Ring Setting for the right Rotor"));

        ringSetting0Spinner.valueProperty().addListener( (v, oldValue, newValue) -> {
            // System.out.println("ringSetting0Spinner.valueProperty().Listener(" + newValue + "))");
            model.setRingSetting(0, newValue);
        });

        ringSetting1Spinner.valueProperty().addListener( (v, oldValue, newValue) -> {
            // System.out.println("ringSetting1Spinner.valueProperty().Listener(" + newValue + "))");
            model.setRingSetting(1, newValue);
        });

        ringSetting2Spinner.valueProperty().addListener( (v, oldValue, newValue) -> {
            // System.out.println("ringSetting2Spinner.valueProperty().Listener(" + newValue + "))");
            model.setRingSetting(2, newValue);
        });

        ringSetting3Spinner.valueProperty().addListener( (v, oldValue, newValue) -> {
            // System.out.println("ringSetting3Spinner.valueProperty().Listener(" + newValue + "))");
            model.setRingSetting(3, newValue);
        });
    }



    /************************************************************************
     * Support code for "Rotor Offsets" panel.
     */

    @FXML
    private TitledPane rotorOffsetsTitledPane;

    @FXML
    private Spinner<String> rotorOffset0Spinner;

    @FXML
    private Spinner<String> rotorOffset1Spinner;

    @FXML
    private Spinner<String> rotorOffset2Spinner;

    @FXML
    private Spinner<String> rotorOffset3Spinner;

    /**
     * Initialize "Rotor Offsets" panel.
     */
    private void initializeRotorOffsets() {
        rotorOffsetsTitledPane.setTooltip(new Tooltip("Select initial starting position for the selected Rotors"));

        rotorOffset0Spinner.setValueFactory(model.getRotorOffsetSVF(0));
        rotorOffset1Spinner.setValueFactory(model.getRotorOffsetSVF(1));
        rotorOffset2Spinner.setValueFactory(model.getRotorOffsetSVF(2));
        rotorOffset3Spinner.setValueFactory(model.getRotorOffsetSVF(3));

        rotorOffset0Spinner.getValueFactory().wrapAroundProperty().set(true);
        rotorOffset1Spinner.getValueFactory().wrapAroundProperty().set(true);
        rotorOffset2Spinner.getValueFactory().wrapAroundProperty().set(true);
        rotorOffset3Spinner.getValueFactory().wrapAroundProperty().set(true);

        rotorOffset0Spinner.setTooltip(new Tooltip("Select offset for the fourth Rotor"));
        rotorOffset1Spinner.setTooltip(new Tooltip("Select offset for the left Rotor"));
        rotorOffset2Spinner.setTooltip(new Tooltip("Select offset for the middle Rotor"));
        rotorOffset3Spinner.setTooltip(new Tooltip("Select offset for the right Rotor"));
        
        rotorOffset0Spinner.valueProperty().addListener( (v, oldValue, newValue) -> {
            // System.out.println("rotorOffset0Spinner.valueProperty().Listener(" + newValue + "))");
            model.setRotorOffset(0, newValue);
        });

        rotorOffset1Spinner.valueProperty().addListener( (v, oldValue, newValue) -> {
            // System.out.println("rotorOffset1Spinner.valueProperty().Listener(" + newValue + "))");
            model.setRotorOffset(1, newValue);
        });

        rotorOffset2Spinner.valueProperty().addListener( (v, oldValue, newValue) -> {
            // System.out.println("rotorOffset2Spinner.valueProperty().Listener(" + newValue + "))");
            model.setRotorOffset(2, newValue);
        });

        rotorOffset3Spinner.valueProperty().addListener( (v, oldValue, newValue) -> {
            // System.out.println("rotorOffset3Spinner.valueProperty().Listener(" + newValue + "))");
            model.setRotorOffset(3, newValue);
        });
    }



    /************************************************************************
     * Support code for "Rotor Set-Up" panel.
     */

    @FXML
    private TitledPane rotorSetUpTitledPane;
 
    @FXML
    private CheckBox fourthWheelCheckbox;

    @FXML
    private CheckBox useLettersCheckbox;

    @FXML
    private CheckBox showStepsCheckbox;

    @FXML
    void fourthWheelCheckboxActionPerformed(ActionEvent event) {
        model.setFourthWheel(fourthWheelCheckbox.isSelected());
        editableFourthWheel();
    }

    @FXML
    void useLettersCheckboxActionPerformed(ActionEvent event) {
        model.setUseLetters(useLettersCheckbox.isSelected());
    }
    @FXML
    void showStepsCheckboxActionPerformed(ActionEvent event) {
        model.setShow(showStepsCheckbox.isSelected());
    }

    /**
     * Control whether it is possible to select and change the fourth rotor 
     * depending on the states of fourthWheelCheckbox and encipherButton.
     * @param editable indicates if the ring settings are editable.
     */
    private void editableFourthWheel() {
        final boolean fourthWheel = model.isFourthWheel();
        final boolean disable = fourthWheel ? model.isEncipher() : true;

        wheel0Choicebox.setDisable(disable);
        ringSetting0Spinner.setDisable(disable);
        rotorOffset0Spinner.setDisable(!fourthWheel);
    }

    /**
     * Initialize "Rotor Set-Up".
     */
    private void initializeRotorSetup() {

        initializeWheelOrder();
        initializeRingSettings();
        initializeRotorOffsets();

        rotorSetUpTitledPane.setTooltip(new Tooltip("Select and set up the Rotors (wheels / drums)"));
        fourthWheelCheckbox.setTooltip(new Tooltip("Select to use a fourth Rotor"));
        useLettersCheckbox.setTooltip(new Tooltip("Select to use Letters instead of numbers on the Rotors"));
        showStepsCheckbox.setTooltip(new Tooltip("Select to show individual translation steps"));
    }



    /************************************************************************
     * Support code for "Plugboard Connections" panel.
     */

    @FXML
    private TitledPane plugboardConnectionsTitledPane;

    @FXML
    private TextField plug0;

    @FXML
    private TextField plug1;

    @FXML
    private TextField plug2;

    @FXML
    private TextField plug3;

    @FXML
    private TextField plug4;

    @FXML
    private TextField plug5;

    @FXML
    private TextField plug6;

    @FXML
    private TextField plug7;

    @FXML
    private TextField plug8;

    @FXML
    private TextField plug9;

    @FXML
    private TextField plug10;

    @FXML
    private TextField plug11;

    @FXML
    private TextField plug12;

    private ArrayList<TextField> plugs = new ArrayList<TextField>(Model.PLUG_COUNT);

    @FXML
    void plugBoardKeyTyped(KeyEvent event) {
        TextField field = (TextField)event.getSource();
        // System.out.println("plugBoardKeyTyped(" + field.getId() + ", " + field.getText() + ")");

        model.setPlugText(field.getId(), field.getText());
        
        checkPlugboard();
        syncEncipherButton();
    }

    /**
     * Indicate error state of each plugboard pair based on it's validity.
     */
    private void checkPlugboard() {
        // System.out.println("checkPlugboard()");

        for (int i = 0; i < plugs.size(); ++i) {
            final Boolean valid = model.isPlugValid(i);
            setValidTextField(plugs.get(i), valid);
        }
    }

    /**
     * Control whether it is possible to change the plugboard connections.
     * @param editable indicates if the plugboard connections are editable.
     */
    private void editablePlugboard(boolean editable) {
        // System.out.println("editablePlugboard(" + editable + ")");

        for (TextField field : plugs)
            field.setEditable(editable);
    }

    /**
     * Initialize "Plugboard Connections" panel.
     */
    private void initializePlugboardConnections() {

        plugboardConnectionsTitledPane.setTooltip(new Tooltip("Configure the Plugboard using unique wiring pairs"));

        plugs.add(plug0);
        plugs.add(plug1);
        plugs.add(plug2);
        plugs.add(plug3);
        plugs.add(plug4);
        plugs.add(plug5);
        plugs.add(plug6);
        plugs.add(plug7);
        plugs.add(plug8);
        plugs.add(plug9);
        plugs.add(plug10);
        plugs.add(plug11);
        plugs.add(plug12);

        for (int i = 0; i < plugs.size(); ++i) {
            String id = String.valueOf(i);
            TextField plug = plugs.get(i);
            plug.setId(id);         // Use id as an index.
            plug.setTooltip(new Tooltip("Configure plugboard wiring pair"));

            setValidTextField(plug, model.isPlugValid(i));

            plug.setTextFormatter(new TextFormatter<>(change -> {

                if (change.isAdded()) {
                    if (change.getText().matches("[a-z]*")) {
                        String text = change.getText().toUpperCase();
                        change.setText(text);
                    } else if (!change.getText().matches("[A-Z]*")) {
                        return null;
                    }
                }

                return change;
            }));
        }
    }



    /************************************************************************
     * Support code for "Translation" panel.
     */

    private int currentKey = -1;

    @FXML
    private ToggleButton encipherButton;

    @FXML
    private Button resetButton;

    @FXML
    private Label mainLabel;

    @FXML
    void encipherButtonActionPerformed(ActionEvent event) {
        final boolean encipher = encipherButton.isSelected();
        // System.out.println("encipherButtonActionPerformed(" + encipher + ")");

        model.setEncipher(encipher);
        updateGUIState();
        syncUI();
    }

    @FXML
    void resetButtonActionPerformed(ActionEvent event) {
        model.defaultSettings();
        syncUI();
    }

    /**
     * Control whether it is possible to reset the settings.
     * @param editable indicates if the reset button is available.
     */
    private void editableTranslation(boolean editable) {
        resetButton.setDisable(!editable);
    }

    /**
     * Update the config editability state of the GUI depending on whether we 
     * are currently translating letters or not.
     * @return true if we are currently translating letters, false otherwise.
     */
    private boolean updateGUIState() {
        final boolean encipher = model.isEncipher();

        // System.out.println("updateGUIState(" + encipher + ")");
        editableReflector(!encipher);
        editableWheelOrder(!encipher);
        editableRingSettings(!encipher);
        editablePlugboard(!encipher);
        editableFourthWheel();
        editableTranslation(!encipher);

        if (encipher) {
            encipherButton.setText("Press to Change Settings");
            encipherButton.setTooltip(new Tooltip("Press to resume changing settings"));

            mainLabel.setText("");
        } else {
            encipherButton.setText("Press to Start Translation");
            encipherButton.setTooltip(new Tooltip("Press to translate letters using the current settings"));

            mainLabel.setText("Configure Settings");
        }

        return encipher;
    }

    /**
     * Only allow the encipherButton to be selected if the config is valid.
     */
    private void syncEncipherButton() {
        encipherButton.setDisable(!model.isConfigValid());
    }

    /**
     * Initialize "Translation" panel.
     */
    private void initializeEncipher() {
        syncEncipherButton();
        encipherButton.setSelected(updateGUIState());
        resetButton.setTooltip(new Tooltip("Click to return all settings to the default values"));
    }


    /**
     * Called by the Application when a new key is pressed.
     * @param keyCode key to be processed
     */
    public void keyPress(KeyCode keyCode) {
        final boolean encipher = model.isEncipher();

        if (encipher) {
            if (currentKey == -1) {
                currentKey = Mapper.stringToIndex(keyCode.getChar());
                final int index = model.translate(currentKey);

                mainLabel.setText(keyCode.getChar() + "->" + Rotor.indexToString(index));
            }
        }
    }

    /**
     * Called by the Application when a current key is released.
     * @param keyCode key to be processed
     */
    public void keyRelease(KeyCode keyCode) {
        final boolean encipher = model.isEncipher();

        if (encipher) {
            final int index = Mapper.stringToIndex(keyCode.getChar());
            if (currentKey == index)
                currentKey = -1;
        }
    }

}
