package com.subakstudio.subak.ui

import groovy.beans.Bindable

import javax.swing.ComboBoxModel
import javax.swing.DefaultComboBoxModel

/**
 * Created by yeoupooh on 12/15/15.
 */
class SelectableModel {
    @Bindable
    ComboBoxModel engines = new DefaultComboBoxModel()
    @Bindable
    def selectedItem
    @Bindable
    int selectedIndex
}
