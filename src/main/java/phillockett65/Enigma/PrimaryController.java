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
 * PrimaryController is the class that is responsible for centralizing control.
 * It is instantiated by the FXML loader creates the Model.
 * 
 * Each handler should always update the model with the changed data first, 
 * then act on the new state of the model.
 * 
 * The order of the code should match the order of the layout when possible.
 */
package phillockett65.Enigma;

import io.github.palexdev.materialfx.controls.MFXToggleButton;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;

public class PrimaryController {

    private Model model;



    /************************************************************************
     * General support code.
     */

    private static final String TOPBARICON = "top-bar-icon";
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

    private Stage stage;


    /**
     * Constructor.
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

        initializeTopBar();
        initializeReflector();
        initializeRotorSetup();
        initializePlugboardConnections();
        initializeEncipher();
    }

    /**
     * Called by Application after the stage has been set. Completes any 
     * initialization dependent on other components being initialized.
     */
    public void init(Stage stage) {
        // System.out.println("PrimaryController init.");
        this.stage = stage;
        model.init(stage);
        syncUI();
        if (!model.isDefaulted()) {
            stage.setX(model.getMainXPos());
            stage.setY(model.getMainYPos());
        }

        headingLabel.setText(model.getTitle());
    }

    /**
     * Called by Application on shutdown.
     */
    public void saveState() {
        // Only save window position just before saving state.
        model.setMainPos(stage.getX(), stage.getY());
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

        fourthWheelCheckbox.setSelected(model.isFourthWheel());
        plugboardCheckbox.setSelected(model.isExtPlugboard());
        useNumbersCheckbox.setSelected(model.isUseNumbers());
        showStepsCheckbox.setSelected(model.isShow());

        for (int i = 0; i < plugs.size(); ++i) {
            TextField plug = plugs.get(i);
            plug.setText(model.getPlugText(i));
        }

        encipherCheckbox.setSelected(updateGUIState());
    }



    /************************************************************************
     * Support code for "Top Bar" panel.
     */

    private double x = 0.0;
    private double y = 0.0;

    @FXML
    private HBox topBar;

    @FXML
    private Label headingLabel;

    @FXML
    void topBarOnMousePressed(MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
    }

    @FXML
    void topBarOnMouseDragged(MouseEvent event) {
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }
 
 
    /**
     * Builds the cancel button as a Pane and includes the mouse click handler.
     * @return the Pane that represents the cancel button.
     */
    private Pane buildCancel() {
        final double iconSize = 28.0;
        final double cancelStroke = 2.5;

        Pane cancel = new Pane();
        cancel.setPrefWidth(iconSize);
        cancel.setPrefHeight(iconSize);
        cancel.getStyleClass().add(TOPBARICON);

        double centre = iconSize / 2;
        double radius = centre * 0.7;

        Circle cancelCircle = new Circle(centre, centre, radius);
        cancelCircle.setFill(Color.TRANSPARENT);
        cancelCircle.getStyleClass().add(TOPBARICON);
        cancelCircle.setStrokeWidth(cancelStroke);

        double length = radius * 0.6;
        double a = centre + length;
        double b = centre - length;
        Line cancelLine = new Line(centre, a, centre, b);
        cancelLine.getStyleClass().add(TOPBARICON);
        cancelLine.setStrokeWidth(cancelStroke);
        cancelLine.setStrokeLineCap(StrokeLineCap.ROUND);

        cancel.getChildren().addAll(cancelCircle, cancelLine);

        cancel.setOnMouseClicked(event -> {
            stage.close();
        });

        return cancel;
    }


    /**
     * Initialize "Reflector" panel.
     */
    private void initializeTopBar() {
        topBar.getChildren().add(buildCancel());
    }
  

     /************************************************************************
     * Support code for "Reflector Set-Up" panel.
     */

    @FXML
    private TitledPane reflectorSetUpTitledPane;
  
    @FXML
    private ChoiceBox<String> reflectorChoicebox;

    @FXML
    private MFXToggleButton reconfigurableCheckbox;

    @FXML
    void reconfigurableCheckboxActionPerformed(ActionEvent event) {
        model.setReconfigurable(reconfigurableCheckbox.isSelected());
        setReconfigurable();
        syncEncipherButton();
        checkReflector();
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

        final boolean reconfigurable = model.isReconfigurable();
        for (TextField field : pairs) {
            field.setDisable(!reconfigurable);
            field.setEditable(editable);
        }
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

        setReconfigurable();
    }



    /************************************************************************
     * Support code for "Rotor Set-Up" panel.
     */

    @FXML
    private TitledPane rotorSetUpTitledPane;
 
    @FXML
    private HBox rotorSetUpHBox;

    @FXML
    private MFXToggleButton fourthWheelCheckbox;

    @FXML
    private MFXToggleButton useNumbersCheckbox;

    @FXML
    void fourthWheelCheckboxActionPerformed(ActionEvent event) {
        model.setFourthWheel(fourthWheelCheckbox.isSelected());
    }

    @FXML
    void useNumbersCheckboxActionPerformed(ActionEvent event) {
        model.setUseNumbers(useNumbersCheckbox.isSelected());
    }

    /**
     * Control whether it is possible to change the rotor selection. Note 
     * wheel0Choicebox is controlled seperately in editableFourthWheel().
     * @param editable indicates if the rotor selections are editable.
     */
    private void editableWheelOrder(boolean editable) {
        // System.out.println("editableReflector(" + editable + ")");

        fourthWheelCheckbox.setDisable(!editable);
        plugboardCheckbox.setDisable(!editable);
        model.setTranslate(!editable);
    }

    /**
     * Initialize "Rotor Set-Up".
     */
    private void initializeRotorSetup() {


        rotorSetUpHBox.getChildren().addAll(model.getRotorControls());

        rotorSetUpTitledPane.setTooltip(new Tooltip("Select and set up the Rotors (wheels / drums)"));
        fourthWheelCheckbox.setTooltip(new Tooltip("Select to use a fourth Rotor"));
        useNumbersCheckbox.setTooltip(new Tooltip("Select to use Numbers on the Rotors instead of Letters"));
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

    @FXML
    private MFXToggleButton plugboardCheckbox;

    private ArrayList<TextField> plugs = new ArrayList<TextField>(Model.FULL_COUNT);

    @FXML
    void plugBoardKeyTyped(KeyEvent event) {
        TextField field = (TextField)event.getSource();
        // System.out.println("plugBoardKeyTyped(" + field.getId() + ", " + field.getText() + ")");

        model.setPlugText(field.getId(), field.getText());
        
        checkPlugboard();
        syncEncipherButton();
    }

    @FXML
    void plugboardCheckboxActionPerformed(ActionEvent event) {
        model.setExtPlugboard(plugboardCheckbox.isSelected());
        checkPlugboard();
        editableExtPlugboard();
        syncEncipherButton();
    }

    /**
     * Control whether it is possible to change the extended plugboard 
     * depending on the states of plugboardCheckbox and encipherButton.
     */
    private void editableExtPlugboard() {
        final boolean extended = model.isExtPlugboard();
        final boolean encipher = model.isEncipher();
        final boolean disable = extended ? encipher : true;

        for (int i = Model.PLUG_COUNT; i < Model.FULL_COUNT; ++i) {
            plugs.get(i).setDisable(!extended);
            plugs.get(i).setEditable(!disable);
        }
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

        editableExtPlugboard();
    }

    /**
     * Initialize "Plugboard Connections" panel.
     */
    private void initializePlugboardConnections() {

        plugboardConnectionsTitledPane.setTooltip(new Tooltip("Configure the Plugboard using unique wiring pairs"));
        plugboardCheckbox.setTooltip(new Tooltip("Select to use all wiring pairs"));

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
    private MFXToggleButton encipherCheckbox;

    @FXML
    private ChoiceBox<Integer> settingsChoicebox;

    @FXML
    private MFXToggleButton showStepsCheckbox;

    @FXML
    private HBox mainIO;

    @FXML
    private Label mainLabel;

    @FXML
    private TextField keyIO;

    @FXML
    private Label labelIO;

    @FXML
    private TextField lampIO;

    @FXML
    void encipherCheckboxActionPerformed(ActionEvent event) {
        final boolean encipher = encipherCheckbox.isSelected();
        // System.out.println("encipherButtonActionPerformed(" + encipher + ")");

        model.setEncipher(encipher);
        updateGUIState();
        syncUI();
        keyIO.requestFocus();
    }

    @FXML
    void showStepsCheckboxActionPerformed(ActionEvent event) {
        model.setShow(showStepsCheckbox.isSelected());
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
        settingsChoicebox.setDisable(!editable);
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
        editablePlugboard(!encipher);
        editableTranslation(!encipher);

        if (encipher) {
            // encipherCheckbox.setText("Press to Change Settings");
            encipherCheckbox.setTooltip(new Tooltip("Un-select to resume changing settings"));

            mainLabel.setVisible(false);
            mainIO.setVisible(true);
        } else {
            // encipherCheckbox.setText("Press to Start Translation");
            encipherCheckbox.setTooltip(new Tooltip("Select to translate letters using the current settings"));

            mainLabel.setVisible(true);
            mainIO.setVisible(false);
            keyIO.setText("");
            lampIO.setText("");
        }

        return encipher;
    }

    /**
     * Only allow the encipherButton to be selected if the config is valid.
     */
    private void syncEncipherButton() {
        encipherCheckbox.setDisable(!model.isConfigValid());
    }

    /**
     * Initialize "Translation" panel.
     */
    private void initializeEncipher() {
        syncEncipherButton();
        encipherCheckbox.setSelected(updateGUIState());
        settingsChoicebox.setItems(model.getSettingsList());

        settingsChoicebox.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
            model.dailySettings(newValue);
            syncUI();
        });

        showStepsCheckbox.setTooltip(new Tooltip("Select to show each translation step on the command line"));
        settingsChoicebox.setTooltip(new Tooltip("Select a settings entry from the Luftwaffe Enigma key list number 649"));

        mainLabel.setText("Configure Settings");
        final char arrow = '\u2799';
        labelIO.setText("" + arrow);
    }


    /**
     * Called by the Application when a new key is pressed.
     * @param keyCode key to be processed
     */
    public void keyPress(KeyCode keyCode) {
        final boolean encipher = model.isEncipher();

        if (encipher) {
            if (currentKey == -1) {
                currentKey = Mapper.letterToIndex(keyCode.getChar());
                final int index = model.translate(currentKey);

                keyIO.setText(keyCode.getChar());
                lampIO.setText(Mapper.indexToLetter(index));
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
            final int index = Mapper.letterToIndex(keyCode.getChar());
            if (currentKey == index) {
                currentKey = -1;
                
            // lampIO.setText("");
            // keyIO.setText("");
            }
        }
    }

}
