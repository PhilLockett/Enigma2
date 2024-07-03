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
 * ListSpinner is a Spinner wrapper class for an ObservableList of Strings.
 * It maintains a reference to the observable list of strings that is used to 
 * populate the spinner value factory of strings. If any of all of the Strings 
 * in the observable list are changed externally, the spinner value factory 
 * automatically changes to reflect this. However if the number of items in 
 * the list is changed the behaviour is currently undefined.
 */
package phillockett65.Enigma;

import javafx.collections.ObservableList;
import javafx.scene.control.SpinnerValueFactory;

public class ListSpinner {

    private ObservableList<String> list;
    private SpinnerValueFactory<String> SVF;

    /**
     * Constructor.
     * @param list of Strings to be displayed on the spinner.
     */
    public ListSpinner(ObservableList<String> list) {
        this.list = list;
        SVF = new SpinnerValueFactory.ListSpinnerValueFactory<String>(list);
    }

    public ObservableList<String> getList() { return list; }
    public SpinnerValueFactory<String> getSVF() { return SVF; }

    public String getCurrent() { return SVF.getValue(); }
    public int getIndex() { return list.indexOf(getCurrent()); }

    public void setCurrent(String value) { SVF.setValue(value); }
    public void setIndex(int value) { SVF.setValue(list.get(value)); }

    public void increment(int steps) { SVF.increment(steps); }

}
