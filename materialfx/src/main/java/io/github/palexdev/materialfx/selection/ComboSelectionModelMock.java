/*
 *     Copyright (C) 2021 Parisi Alessandro
 *     This file is part of MaterialFX (https://github.com/palexdev/MaterialFX).
 *
 *     MaterialFX is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MaterialFX is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MaterialFX.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.palexdev.materialfx.selection;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * Rather than recreating and adapting the entire JavaFX's selection model for usage
 * in {@link MFXComboBox} we "mock" it. The combo box listview selection model is bound to this one
 * and vice versa.
 * <p></p>
 * <b>Note: if the select methods do not work try adding a listener to the skin property
 * of the control and using the methods there.</b>
 */
public class ComboSelectionModelMock<T> {
    //================================================================================
    // Properties
    //================================================================================
    private final MFXComboBox<T> comboBox;
    private final ReadOnlyObjectWrapper<T> selectedItem = new ReadOnlyObjectWrapper<>(null);
    private final ReadOnlyIntegerWrapper selectedIndex = new ReadOnlyIntegerWrapper(-1);

    //================================================================================
    // Constructors
    //================================================================================
    public ComboSelectionModelMock(MFXComboBox<T> comboBox) {
        this.comboBox = comboBox;
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Clears the selection.
     */
    public void clearSelection() {
        selectedItem.set(null);
        selectedIndex.set(-1);
    }

    /**
     * Selects the given item if present in the combo items list.
     */
    public void selectItem(T item) {
        if (!comboBox.getItems().contains(item)) {
            return;
        }
        selectedIndex.set(comboBox.getItems().indexOf(item));
        selectedItem.set(item);
    }

    /**
     * Selects the first item in the combo items list.
     */
    public void selectFirst() {
        selectedIndex.set(0);
    }

    /**
     * Selects the next item in the combo items list.
     */
    public void selectNext() {
        if (getSelectedIndex() == (comboBox.getItems().size() - 1)) {
            return;
        }
        selectedIndex.add(1);
    }

    /**
     * Selects the last item in the combo items list.
     */
    public void selectLast() {
        selectedIndex.set(comboBox.getItems().size());
    }

    /**
     * Selects the previous item in the combo items list.
     */
    public void selectPrevious() {
        if (getSelectedIndex() == -1) {
            return;
        }
        selectedIndex.subtract(1);
    }

    /**
     * Returns the current selected item's index.
     */
    public int getSelectedIndex() {
        return selectedIndex.get();
    }

    /**
     * Returns the selected index property as a read only.
     */
    public ReadOnlyIntegerProperty selectedIndexProperty() {
        return selectedIndex.getReadOnlyProperty();
    }

    /**
     * Returns the current selected item.
     */
    public T getSelectedItem() {
        return selectedItem.get();
    }

    /**
     * Returns the selected item property as a read only.
     */
    public ReadOnlyObjectProperty<T> selectedItemProperty() {
        return selectedItem.getReadOnlyProperty();
    }
}