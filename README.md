# Enigma2

'Enigma2' is a JavaFX application that simulates the Enigma machine.

**USE AT OWN RISK.**

## Overview
This project is set up as a Maven project that uses JavaFX, MaterialFX, FXML and 
CSS to render the GUI. 
Maven can be run from the command line as shown below.
Maven resolves dependencies and builds the application independently of an IDE.

This application simulates the [Enigma Machine](https://en.wikipedia.org/wiki/Enigma_machine). 
There are no restrictions placed on the Rotor selection, as such some 
Rotor combinations can be made that may not be available on the real machine.

Note: for the Commercial, Rocket and SwissK Rotors, the current turnover points 
are guesses and may be incorrect.

## Dependencies
'Enigma2' is dependent on the following:

  * Java 15.0.1
  * Apache Maven 3.6.3

The source code is structured as a standard Maven project which requires Maven 
and a JDK to be installed. A quick web search will help, alternatively
[Oracle](https://www.java.com/en/download/) and 
[Apache](https://maven.apache.org/install.html) should guide you through the
install.

Also [OpenJFX](https://openjfx.io/openjfx-docs/) can help set up your 
favourite IDE to be JavaFX compatible, however, Maven does not require this.

## Cloning
The following commands clone the code:

	git clone https://github.com/PhilLockett/Enigma2.git
	cd Enigma2/

## Running
Once cloned the following command executes the code:

	mvn clean javafx:run

## User Guide
Selected settings and states will be persisted from one session to the next.
This means that all settings are saved to "Settings.dat" when the application 
is shutdown. The next time Enigma2 is executed, these settings are loaded ready 
to continue from where it left off.

### Reflector Set-Up
The choice box allows standard pre-configured reflectors to be selected. 

Alternatively, the toggle allows for a reconfigurable reflector to be used.
Twelve loop-back wired pairs must be defined using the text boxes. When all 
Twelve pairs are defined using each letter only once, the reflector is 
considered valid. The thirteenth pair is assumed from the two remaining 
unused letters.

### Rotor Set-Up
By default, Enigma2 functions as a 3 Rotor machine, allowing the Left, Middle 
and Right Rotors to be defined. The 'Fourth Rotor' toggle, when selected, 
brings in the Fourth Rotor.

Some machines use letters on the Rotors, whereas some use numbers. The 'Use 
Numbers' toggle switches between these characters on the 'Ring Settings'
and Rotor 'Offsets' spinners for convenience.

To see all the individual translation steps displayed on the command line
select the 'Show Steps' toggle.

#### Rotor Selection
These choice boxes allow different Rotors to be selected for each of the 
positions. No restrictions are placed on the selection so combinations 
can be selected that may not be available on the real machine.

#### Ring Settings
These spinners allow the ring settings for each rotor to be set.

#### Rotor Offsets
These spinners set up the initial rotor offsets. 

The Rotors advance in a predefined orderly manner with each key press before 
translation.

### Plugboard Connections
Zero or more swap-over pairs can be configured (typically 10 are set). 
The letters must be in pairs and each letter can be used only once for a valid 
plugboard configuration.

The toggle switch allows for up to 13 plugboard pairs to be configured.

### Translation
This area shows the key presses and translated values.

#### Encipher / Decipher
The 'Encipher' toggle is only available when all settings are valid. 

Un-select the toggle to make configuration changes.

Select the toggle to translate key presses using the current configuration 
settings.
The majority of the settings become disabled while translating.

#### Default Settings
The 'Default Settings' button returns all settings to the original values 
including clearing all the text boxes.

## Points of interest
This code has the following points of interest:

  * Enigma2 is an updated version of [Enigma](https://github.com/PhilLockett/Enigma).
  * Enigma2 is a Maven project that uses JavaFX, MaterialFX, FXML and CSS.
  * Enigma2 uses a custom controller, RotorController, to represent a rotor.
  * Enigma2 simulates the behaviour of the [Enigma](https://en.wikipedia.org/wiki/Enigma_machine) machine.
  * Rotor combinations can be selected that may not be available on the real machine.
  * Data is persisted from one session to the next.
