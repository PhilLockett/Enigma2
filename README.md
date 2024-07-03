# Enigma

'Enigma' is a JavaFX application that simulates the Enigma machine.

## Overview
This project has been set up as a Maven project that uses JavaFX, FXML and 
CSS to render the GUI. Maven can be run from the command line as shown below.
Maven resolves dependencies and builds the application independently of an IDE.

This application simulates the [Enigma](https://en.wikipedia.org/wiki/Enigma_machine)
machine. There are no restrictions placed on the Rotor selection, as such some 
Rotor combinations can be made that may not be available on the real machine.

Note: for the Commercial, Rocket and SwissK Rotors, the current turnover points 
are guesses and may be incorrect.

## Dependencies
'Enigma' is dependent on the following:

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

	git clone https://github.com/PhilLockett/Enigma.git
	cd Enigma/

## Running
Once cloned the following command executes the code:

	mvn clean javafx:run

## User Guide
Selected settings and states will be maintained from one session to the next.
This means that whatever state Enigma is in (rotor settings, rotor offsets, 
plugboard connections etc.) are saved to "Settings.dat" when the application 
is shutdown. The next time Enigma is executed, these settings are loaded ready 
to continue from where it left off.

### Reflector Set-Up
The choice box allows standard pre-configured reflectors to be selected. 
Alternatively the check box allows for a reconfigurable reflector to be used.
Twelve loop-back wired pairs must be defined using the text boxes. When all 
Twelve are defined and each letter is used only once, the reflector is 
considered valid. The thirteenth pair is assumed from the two remaining 
unused letters.

### Rotor Set-Up
By default Enigma functions as a 3 Rotor machine, allowing the Left, Middle 
and Right Rotors to be defined. The 'Fourth Rotor' check box, when selected, 
brings in the Fourth Rotor.

Some machines use letters on the Rotors, whereas some use numbers. The 'Use 
Letters' check box switches between these characters on the 'Ring Settings'
and 'Rotor Offsets' spinners for convenience.

To see all the individual translation steps displayed on the command line
select the 'Show Steps' check box.

#### Rotor Selection
The choice boxes allow different Rotors to be selected for each of the 
positions. No restrictions are placed on the selection so combinations 
can be selected that may not be available on the real machine.

#### Ring Settings
The spinners allow the ring settings for each rotor to be set.

#### Rotor Offsets
The spinners allow the initial rotor offsets for each rotor to be set. The
Rotors advance in a predefined orderly manner with each key press before 
translation.

### Plugboard Connections
Zero or more swap-over pairs can be configured. The letters must be in pairs 
and each letter can be used once at most for the plugboard configuration to be 
considered valid.

### Translation
The toggle button is only available when the settings are valid. The toggle 
button switches between allowing settings to be changed and translating key 
presses.

While configuration settings are being selected the toggle button is red and 
shows 'Press to Start Translation' and the text message 'Configure Settings' 
is displayed.

 When the button is pressed it toggles to green showing 'Press to Change 
 Settings'. The majority of the settings become disabled so they cannot be 
 changed during the translation stage. The 'Configure Settings' text message 
 is replaced with the key press translation with each key press.

The 'Default Settings' button returns all settings to the original values 
including clearing the text boxes.

## Points of interest
This code has the following points of interest:

  * Enigma is a Maven project that uses Maven, JavaFX, FXML and CSS.
  * Enigma is derived from the [BaseFXML](https://github.com/PhilLockett/BaseFXML) 'framework'.
  * Enigma simulates the behaviour of the [Enigma](https://en.wikipedia.org/wiki/Enigma_machine) machine.
  * Rotor combinations can be selected that may not be available on the real machine.
  * Data persistence is maintained from one session to the next.
