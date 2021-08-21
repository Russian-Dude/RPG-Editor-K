package com.rdude.rpgeditork.view.helper

import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.scene.control.TextField
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.text.Text
import tornadofx.*

class SummonSelectorElementProperties : GridPane() {

    init {
        paddingAll = 10.0
        hgap = 10.0
        vgap = 10.0
    }

    val chanceTextField = TextField("100 %").apply {
        textProperty().addListener{ observable, oldValue, newValue ->
            if (!newValue.matches(Regex("\\d* %")) || !newValue.replace(" %", "").isDouble()) {
                text = oldValue
            }
        }
        caretPositionProperty().addListener { _, _, newV ->
            val max = this.text.length - 2;
            if (newV as Int > max && this.selectedTextProperty().get().isEmpty()) {
                Platform.runLater { this.positionCaret(max) }
            }
        }
        add(Text("Chance"), 0, 0)
        add(this, 1, 0)
    }

    val turnsTextField = TextField("0").apply {
        filterInput { it.controlNewText.isInt() }
        add(Text("Turns duration"), 0, 1)
        add(this, 1, 1)
    }

    val minutesTextField = TextField("0").apply {
        filterInput { it.controlNewText.isInt() }
        add(Text("Minutes duration"), 0, 2)
        add(this, 1, 2)
    }

    var chance: Double
        get() {
            val str = chanceTextField.text.replace(" %", "")
            return if (str.isEmpty()) 100.0 else str.toDouble()
        }
        set(value) {
            chanceTextField.text = value.toString().replace(Regex("\\.0+\\b"), "") + " %"
        }

    var turns: Int
        get() {
            return if (turnsTextField.text.isEmpty()) 0 else turnsTextField.text.toInt()
        }
        set(value) {
            turnsTextField.text = value.toString()
        }

    var minutes: Int
        get() {
            return if (minutesTextField.text.isEmpty()) 0 else minutesTextField.text.toInt()
        }
        set(value) {
            minutesTextField.text = value.toString()
        }
}