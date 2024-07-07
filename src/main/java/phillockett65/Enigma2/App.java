/*  Enigma2 - a JavaFX based playing card image generator.
 *
 *  Copyright 2022 Philip Lockett.
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
package phillockett65.Enigma2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    PrimaryController controller;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("primary.fxml"));

        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);

        stage.setTitle("Enigma 2.0");
        stage.setOnCloseRequest(e -> Platform.exit());
        stage.resizableProperty().setValue(false);
        stage.setScene(scene);

        controller = fxmlLoader.getController();

        scene.setOnKeyPressed(event -> {
            final KeyCode keyCode = event.getCode();
            // System.out.println("scene.setOnKeyPressed(" + keyCode + ", " + event.getCharacter() + ").");
            if (keyCode.isLetterKey())
                controller.keyPress(keyCode);
        });

        scene.setOnKeyReleased(event -> {
            final KeyCode keyCode = event.getCode();
            // System.out.println("scene.setOnKeyReleased(" + keyCode + ", " + event.getCharacter() + ").");
            if (keyCode.isLetterKey())
                controller.keyRelease(keyCode);
        });

        stage.show();

        controller.init();
    }

    @Override
    public void stop() throws Exception {
        controller.saveState();
    }

    public static void main(String[] args) {
        launch();
    }

}